package com.unifiprojects.app.appichetto.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.views.ReceiptView;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class ShowHistoryControllerIT {

	static MVCBaseTest baseTest = new MVCBaseTest();
	EntityManager entityManager;

	ShowHistoryController showHistoryController;
	ReceiptRepository receiptRepository;

	@Mock
	ShowHistoryView showHistoryView;
	@Mock
	ReceiptView receiptView;
	
	private User loggedUser;
	private User payerUser;
	private Receipt firstReceipt;
	private Receipt secondReceipt;
	private Receipt thirdReceipt;

	@Captor
	private ArgumentCaptor<List<Receipt>> receiptListCaptor;
	@Captor
	private ArgumentCaptor<String> stringCaptor;

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Before
	public void setUp() {
		baseTest.wipeTablesBeforeTest();
		MockitoAnnotations.initMocks(this);
		
		entityManager = baseTest.getEntityManager();
		receiptRepository = new ReceiptRepositoryHibernate(entityManager);
		showHistoryController = new ShowHistoryController(receiptRepository, showHistoryView, new HibernateTransaction(entityManager), null);
		reset(showHistoryView);

		loggedUser = new User("logged", "pw");
		payerUser = new User("payer", "pw");

		firstReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payerUser, loggedUser,
				new GregorianCalendar(2019, 9, 1));
		secondReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payerUser, loggedUser,
				new GregorianCalendar(2019, 9, 2));
		thirdReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payerUser, loggedUser,
				new GregorianCalendar(2019, 9, 3));

		entityManager.getTransaction().begin();
		entityManager.persist(loggedUser);
		entityManager.persist(payerUser);

		entityManager.persist(firstReceipt);
		entityManager.persist(thirdReceipt);
		entityManager.persist(secondReceipt);
		entityManager.getTransaction().commit();

		showHistoryController.setLoggedUser(loggedUser);
	}

	@Test
	public void testShowHistoryUpdateTheView() {
		showHistoryController.showHistory();

		verify(showHistoryView).showShoppingHistory(receiptListCaptor.capture());
		verifyNoMoreInteractions(showHistoryView);
		assertThat(receiptListCaptor.getValue()).containsExactlyInAnyOrder(firstReceipt, secondReceipt, thirdReceipt);
	}

	@Test
	public void testRemove() {

		showHistoryController.removeReceipt(thirdReceipt);

		verify(showHistoryView).showShoppingHistory(receiptListCaptor.capture());
		verify(showHistoryView).showErrorMsg(stringCaptor.capture());
		verifyNoMoreInteractions(showHistoryView);
		
		assertThat(receiptListCaptor.getValue()).containsExactlyInAnyOrder(firstReceipt, secondReceipt);
	}

}
