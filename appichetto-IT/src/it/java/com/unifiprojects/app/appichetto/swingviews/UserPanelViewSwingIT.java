package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.PayReceiptsController;
import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.controllers.UserController;
import com.unifiprojects.app.appichetto.controllers.UserPanelController;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.LoginModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.modules.UserPanelModule;

@RunWith(GUITestRunner.class)
public class UserPanelViewSwingIT extends AssertJSwingJUnitTestCase {

	private FrameFixture window;
	private static MVCBaseTest baseTest = new MVCBaseTest();

	private UserPanelViewSwing userPanelViewSwing;
	private UserPanelController userPanelController;

	private User loggedUser;
	private User payer1;
	private Receipt firstReceiptPayer1;
	private Receipt secondReceiptPayer1;
	private Receipt thirdReceiptPayer1;
	private Receipt firstReceiptPayer2;
	private User payer2;
	private HomepageSwingView homepage;
	private Receipt firstReceiptLoggedUser;
	private LoginViewSwing loginView;

	private static EntityManager entityManager;
	private static Injector injector;

	@BeforeClass
	public static void setupEntityManager() {

		Module entityManagerModule = new EntityManagerModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);

		baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		entityManager = persistenceInjector.getInstance(EntityManager.class);

		injector = persistenceInjector.createChildInjector(new RepositoriesModule(), new UserPanelModule(),
				new LoginModule());
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			baseTest.wipeTablesBeforeTest();

			loggedUser = new User("logged", "pw");
			payer1 = new User("payer", "pw");
			payer2 = new User("payer2", "pw");

			firstReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 10));
			secondReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 11));
			firstReceiptPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer2, new GregorianCalendar(2019, 8, 10));
			firstReceiptLoggedUser = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payer1,
					loggedUser);

			Item tomato = new Item("tomato", 1.35, Arrays.asList(loggedUser, payer1));
			Item hamburger = new Item("hamburger", 4.45, Arrays.asList(loggedUser, payer1));
			Item bread = new Item("bread", 3.89, Arrays.asList(loggedUser, payer1));
			thirdReceiptPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
					payer1, new GregorianCalendar(2019, 8, 12), Arrays.asList(tomato, bread, hamburger));

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

			loginView = injector.getInstance(LoginViewSwing.class);
			homepage = (HomepageSwingView) loginView.getHomepage();

			mock(UserController.class);

			((PayReceiptsViewSwing) homepage.payReceiptsView)
					.setPayReceiptsController((PayReceiptsController) mock(PayReceiptsController.class));
			((ReceiptSwingView) homepage.receiptView)
					.setReceiptController((ReceiptController) mock(ReceiptController.class));
			((ShowHistoryViewSwing) homepage.showHistoryView)
					.setShowHistoryController((ShowHistoryController) mock(ShowHistoryController.class));

//			homepage.loginView = loginView;

			userPanelViewSwing = (UserPanelViewSwing) homepage.userPanelView;
			userPanelViewSwing.setLoginViewSwing(homepage.loginView);
			userPanelViewSwing.setLinkedSwingView(homepage);

			userPanelController = (UserPanelController) userPanelViewSwing.getController();
			userPanelController.setHomepageView(homepage);
			userPanelController.setLoggedUser(loggedUser);

			userPanelController.update();
			LinkedSwingView.initializeMainFrame();

			return userPanelViewSwing;
		});

		window = new FrameFixture(robot(), userPanelViewSwing.getFrame());
		window.show(); // shows the frame to test
	}

	@GUITest
	@Test
	public void testInitialState() {
		window.label("userLabel").requireText("Hello logged!");
	}

	@GUITest
	@Test
	public void testGoBackHome() {
		window.button(JButtonMatcher.withText("Back")).click();
		assertThat(LinkedSwingView.mainFrame.getContentPane().getComponents()[0])
				.isEqualTo(homepage.getFrame().getContentPane());
	}

	@GUITest
	@Test
	public void testRemoveUserGoToLoginView() {
		window.button(JButtonMatcher.withText("Remove user")).click();
		window.button(JButtonMatcher.withText("Yes")).click();

		assertThat(LinkedSwingView.mainFrame.getContentPane().getComponents()[0])
				.isEqualTo(homepage.loginView.getFrame().getContentPane());

		return;
	}

	@GUITest
	@Test
	public void testChangeCredential() {
		String testNewName = "newName";
		String newPassword = "newPw";

		window.textBox("newName").enterText(testNewName);
		window.textBox("newPW").enterText(newPassword);

		window.button(JButtonMatcher.withText("Update credential")).click();

		window.label("userLabel").requireText("Hello newName!");

		User loggedUserReloaded = entityManager.find(User.class, loggedUser.getId());

		assertThat(loggedUserReloaded).isEqualTo(loggedUser);

	}

	@GUITest
	@Test
	public void testShowErrorMessageIfChangeCredentialFails() {
		String testNewName = "payer";

		window.textBox("newName").enterText(testNewName);

		window.button(JButtonMatcher.withText("Update credential")).click();

		User loggedUserReloaded = entityManager.find(User.class, loggedUser.getId());

		window.label("userLabel").requireText(String.format("Hello %s!", loggedUserReloaded.getUsername()));

		window.label("errorMsg").requireText(String.format("Username %s has been already picked.", testNewName));
	}

}
