package com.unifiprojects.app.appichetto.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.services.CreateDebtsService;
import com.unifiprojects.app.appichetto.swingviews.HomepageSwingView;
import com.unifiprojects.app.appichetto.swingviews.ReceiptSwingView;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;

public class ReceiptControllerIT {

	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private ReceiptController receiptController;
	private UserRepository userRepository;
	private ReceiptRepository receiptRepository;
	private ReceiptManager receiptManager;
	private ReceiptSwingView receiptView;
	User buyer;
	User participant;
	Receipt receipt;

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
		entityManager = baseTest.getEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}
	
	@Before
	public void setUp() {
		baseTest.wipeTablesBeforeTest();
		MockitoAnnotations.initMocks(this);
		
		buyer = new User("Pippo","");
		participant = new User("Pluto","");
		receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(participant, buyer);
		userRepository = new UserRepositoryHibernate(entityManager);
		receiptRepository = new ReceiptRepositoryHibernate(entityManager);
		receiptManager = new ReceiptManager(receipt, new ReceiptRepositoryHibernate(entityManager), new CreateDebtsService());
		receiptView = new ReceiptSwingView();
		receiptView.setLinkedSwingView(mock(HomepageSwingView.class));
		
		receiptController = new ReceiptController(receiptManager, receiptView, userRepository, new HibernateTransaction(entityManager));
		
		
		entityManager.getTransaction().begin();
		entityManager.persist(buyer);
		entityManager.persist(participant);
		entityManager.getTransaction().commit();
		
		entityManager.clear();
	}
	
	@Test
	public void testGetUser() {
		assertThat(receiptController.getUsers()).containsExactlyInAnyOrder(participant, buyer);
	}
	
	@Test
	public void testAddItem() {
		Item item = new Item("pizza", 6.5, new ArrayList<User>(Arrays.asList(participant, buyer)));
		receiptController.addItem(item);
		
		assertThat(receipt.getItems()).contains(item);
		assertThat(receiptView.getListItemModel().contains(item)).isTrue();
	}
	
	@Test
	public void testSaveReceipt() {
		
		receiptController.saveReceipt();
		
		assertThat(receiptRepository.getAllReceiptsBoughtBy(buyer)).contains(receipt);
		assertThat(receiptView.getFrame().isVisible()).isFalse();
	}
}
