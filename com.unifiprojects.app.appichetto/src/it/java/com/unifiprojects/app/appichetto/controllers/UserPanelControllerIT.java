package com.unifiprojects.app.appichetto.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.swingviews.UserPanelViewSwing;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.HomepageView;

@RunWith(GUITestRunner.class)
public class UserPanelControllerIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	@Mock
	private UserPanelViewSwing userPanelViewSwing;

	@Mock
	private HomepageView homepageView;

	private UserPanelController userPanelController;

	private User loggedUser;
	private User payer1;
	private Receipt firstReceiptPayer1;
	private Receipt secondReceiptPayer1;
	private Receipt thirdReceiptPayer1;
	private Receipt firstReceiptPayer2;
	private User payer2;
	private Receipt firstReceiptLoggedUser;
	private UserRepository userRepository;

	private static EntityManager entityManager;
	private static Injector injector;

	@BeforeClass
	public static void setupEntityManager() {

		Module entityManagerModule = new EntityManagerModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(new RepositoriesModule());
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		baseTest.wipeTablesBeforeTest();

		MockitoAnnotations.initMocks(this);

		loggedUser = new User("logged", "pw");
		payer1 = new User("payer", "pw");
		payer2 = new User("payer2", "pw");

		firstReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer1,
				new GregorianCalendar(2019, 8, 10));
		secondReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer1,
				new GregorianCalendar(2019, 8, 11));
		firstReceiptPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer2,
				new GregorianCalendar(2019, 8, 10));
		firstReceiptLoggedUser = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payer1,
				loggedUser);

		Item tomato = new Item("tomato", 1.35, Arrays.asList(loggedUser, payer1));
		Item hamburger = new Item("hamburger", 4.45, Arrays.asList(loggedUser, payer1));
		Item bread = new Item("bread", 3.89, Arrays.asList(loggedUser, payer1));
		thirdReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer1,
				new GregorianCalendar(2019, 8, 12), Arrays.asList(tomato, bread, hamburger));

		entityManager.getTransaction().begin();
		entityManager.persist(loggedUser);
		entityManager.persist(payer1);
		entityManager.persist(payer2);
		entityManager.persist(firstReceiptPayer1);
		entityManager.persist(secondReceiptPayer1);
		entityManager.persist(thirdReceiptPayer1);
		entityManager.persist(firstReceiptPayer2);
		entityManager.persist(firstReceiptLoggedUser);
		entityManager.getTransaction().commit();

		entityManager.clear();

		userRepository = injector.getInstance(UserRepository.class);
		TransactionHandler transaction = injector.getInstance(HibernateTransaction.class);

		userPanelController = new UserPanelController(userPanelViewSwing, userRepository, transaction, homepageView);
		userPanelController.setLoggedUser(loggedUser);
	}

	private Receipt reloadReceipt(Receipt r) {
		return entityManager.find(Receipt.class, r.getId());
	}

	@Test
	public void testChangeCredential() {
		String newName = "newName";
		String newPassword = "newPassword";

		userPanelController.changeCredential(newName, newPassword);

		entityManager.clear();
		
		User loggedUserReloaded = entityManager.find(User.class, loggedUser.getId());
		
		assertThat(loggedUserReloaded).isEqualTo(loggedUser);

		verify(homepageView).setLoggedUser(loggedUser);
		verify(userPanelViewSwing).showUser(loggedUser.getUsername());

	}

	@Test
	public void testRemoveUserDeleteTheUserFromDB() {
		userPanelController.deleteUser();

		assertThat(entityManager.find(User.class, loggedUser.getId())).isNull();

		assertThat(entityManager.find(Receipt.class, firstReceiptLoggedUser.getId())).isNull();

		firstReceiptPayer1 = reloadReceipt(firstReceiptPayer1);
		firstReceiptPayer2 = reloadReceipt(firstReceiptPayer2);
		secondReceiptPayer1 = reloadReceipt(secondReceiptPayer1);

		firstReceiptPayer1.getAccountings().forEach(a -> assertThat(a.getUser()).isNotEqualTo(loggedUser));
		firstReceiptPayer2.getAccountings().forEach(a -> assertThat(a.getUser()).isNotEqualTo(loggedUser));
		secondReceiptPayer1.getAccountings().forEach(a -> assertThat(a.getUser()).isNotEqualTo(loggedUser));

		firstReceiptPayer1.getItems().forEach(a -> assertThat(a.getOwners()).doesNotContain(loggedUser));
		firstReceiptPayer2.getItems().forEach(a -> assertThat(a.getOwners()).doesNotContain(loggedUser));
		secondReceiptPayer1.getItems().forEach(a -> assertThat(a.getOwners()).doesNotContain(loggedUser));
	}

}
