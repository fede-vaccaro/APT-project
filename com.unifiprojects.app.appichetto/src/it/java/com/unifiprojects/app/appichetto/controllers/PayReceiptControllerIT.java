package com.unifiprojects.app.appichetto.controllers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.managers.PaymentManager;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.AccountingRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

public class PayReceiptControllerIT {

	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private AccountingRepository accountingRepository;
	private ReceiptRepository receiptRepository;
	private PayReceiptsController payReceiptsController;
	private PaymentManager paymentManager;

	@Mock
	private PayReceiptsView payReceiptsView;
	private User loggedUser;
	private User payerUser;
	private Receipt firstReceipt;
	private Receipt secondReceipt;
	private Receipt thirdReceipt;

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

		accountingRepository = new AccountingRepositoryHibernate(entityManager);
		receiptRepository = new ReceiptRepositoryHibernate(entityManager);
		paymentManager = new PaymentManager();
		paymentManager.setAccountingRepository(accountingRepository);
		payReceiptsController = new PayReceiptsController(paymentManager, receiptRepository,
				payReceiptsView, new HibernateTransaction(entityManager));

		
		loggedUser = new User("logged", "pw");
		payerUser = new User("payer", "pw");

		firstReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 1));
		secondReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 2));
		thirdReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 3));
		
		entityManager.getTransaction().begin();
		entityManager.persist(loggedUser);
		entityManager.persist(payerUser);
		
		entityManager.persist(firstReceipt);
		entityManager.persist(thirdReceipt);
		entityManager.persist(secondReceipt);
		entityManager.getTransaction().commit();
		
		entityManager.clear();
		
	}
	
	@Test
	public void testShowUnpaidReceipts() {
		payReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser);
		
		verify(payReceiptsView).showReceipts(Arrays.asList(thirdReceipt, secondReceipt, firstReceipt));
	}
	
	@Test
	public void testPayAmountShowErrorMsgWhenEnteredAmountIsIllegal() {
		Double totalDebt = Arrays.asList(firstReceipt, secondReceipt, thirdReceipt)
				.stream()
				.map(r -> r.getAccountings().get(0))
				.mapToDouble(Accounting::getAmount)
				.sum();
		
		payReceiptsController.payAmount(totalDebt*2.0, loggedUser, payerUser);
		
		verify(payReceiptsView).showErrorMsg(anyString());
	}
	
	@Test
	public void testPayAmountShowErrorMsgWhenAmountIsDoubleOfTheTotalDebt() {
		Double totalDebt = Arrays.asList(firstReceipt, secondReceipt, thirdReceipt)
				.stream()
				.map(r -> r.getAccountings().get(0))
				.mapToDouble(Accounting::getAmount)
				.sum();
		
		payReceiptsController.payAmount(totalDebt*2.0, loggedUser, payerUser);
		
		verify(payReceiptsView).showErrorMsg(anyString());
	}
	
}
