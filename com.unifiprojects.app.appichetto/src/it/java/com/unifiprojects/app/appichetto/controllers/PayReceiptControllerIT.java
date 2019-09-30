package com.unifiprojects.app.appichetto.controllers;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.AccountingRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.swingviews.PayViewReceiptsView;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;

public class PayReceiptControllerIT {

	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private AccountingRepository accountingRepository;
	private ReceiptRepository receiptRepository;
	private PayViewReceiptsController payReceiptsController;

	@Mock
	private PayViewReceiptsView payViewReceiptsView;
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
		payReceiptsController = new PayViewReceiptsController(receiptRepository, accountingRepository,
				payViewReceiptsView);
		payReceiptsController.setTransactionHandler(new HibernateTransaction(entityManager));

		
		loggedUser = new User("logged", "pw");
		payerUser = new User("payer", "pw");

		firstReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 1));
		secondReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 2));
		thirdReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
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
	
	private Receipt generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(User loggedUser, User payerUser,
			GregorianCalendar timestamp) {

		Receipt receipt = new Receipt();

		receipt.setTimestamp(timestamp);
		receipt.setBuyer(payerUser);

		// receipt setup: payerUser bought item1 and item2 but he shares them with
		// logged user...
		Item item1 = new Item("Item1", 10., Arrays.asList(loggedUser, payerUser));
		Item item2 = new Item("Item2", 5., Arrays.asList(loggedUser, payerUser));

		receipt.setItems(Arrays.asList(item1, item2));
		receipt.setTotalPrice(item1.getPrice() + item2.getPrice());

		// so now, logged user owes 7.5 credits to payer user
		Accounting debtFromLoggedToPayer = new Accounting(loggedUser, item1.getPrice() / 2 + item2.getPrice() / 2);
		debtFromLoggedToPayer.setReceipt(receipt);
		receipt.setAccountingList(Arrays.asList(debtFromLoggedToPayer));
		return receipt;
	}
	
	@Test
	public void testPayAmount() {
		Double totalDebt = Arrays.asList(firstReceipt, secondReceipt, thirdReceipt)
				.stream()
				.map(r -> r.getAccountings().get(0))
				.mapToDouble(Accounting::getAmount)
				.sum();
		
		payReceiptsController.payAmount(totalDebt, loggedUser, payerUser);
		
		List<Accounting> unpaidAccountings = entityManager.createQuery("from Accounting where paid=:paid and user=:user", Accounting.class)
				.setParameter("paid", false).setParameter("user", loggedUser).getResultList();
		
		assertThat(unpaidAccountings).isEmpty();
		verify(payViewReceiptsView).showReceipts(Arrays.asList());
	}
	
	@Test
	public void testShowUnpaidReceipts() {
		payReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser);
		
		verify(payViewReceiptsView).showReceipts(Arrays.asList(thirdReceipt, secondReceipt, firstReceipt));
	}
	
	@Test
	public void testPayAmountWhenAmountIsHalfOfTheTotalDebt() {
		Double totalDebt = Arrays.asList(firstReceipt, secondReceipt, thirdReceipt)
				.stream()
				.map(r -> r.getAccountings().get(0))
				.mapToDouble(Accounting::getAmount)
				.sum();
		
		payReceiptsController.payAmount(totalDebt/2.0, loggedUser, payerUser);
		
		List<Accounting> unpaidAccountings = entityManager.createQuery("from Accounting where paid=:paid and user=:user", Accounting.class)
				.setParameter("paid", false).setParameter("user", loggedUser).getResultList();
		
		
		Accounting secondAccounting = new Accounting(loggedUser, secondReceipt.getTotalPrice()/4.0);
		secondAccounting.setReceipt(secondReceipt);
		
		Accounting thirdAccounting = new Accounting(loggedUser, secondReceipt.getTotalPrice()/2.0);
		thirdAccounting.setReceipt(thirdReceipt);
		
		assertThat(unpaidAccountings).containsExactlyInAnyOrder(secondAccounting, thirdAccounting);
				
		verify(payViewReceiptsView).showReceipts(Arrays.asList(thirdReceipt, secondReceipt));

	}

}
