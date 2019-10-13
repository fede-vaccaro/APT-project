package com.unifiprojects.app.appichetto.managers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.AccountingRepositoryHibernate;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

public class PaymentManagerIT {

	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private AccountingRepository accountingRepository;
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
		paymentManager = new PaymentManager();
		paymentManager.setAccountingRepository(accountingRepository);

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
	public void testPayAmount() {
		Double totalDebt = Arrays.asList(firstReceipt, secondReceipt, thirdReceipt).stream()
				.map(r -> r.getAccountings().get(0)).mapToDouble(Accounting::getAmount).sum();

		entityManager.getTransaction().begin();
		paymentManager.makePayment(totalDebt, loggedUser, payerUser);
		entityManager.getTransaction().commit();

		List<Accounting> unpaidAccountings = entityManager
				.createQuery("from Accounting where amount!=:amount and user=:user", Accounting.class)
				.setParameter("amount", 0.0).setParameter("user", loggedUser).getResultList();

		assertThat(unpaidAccountings).isEmpty();
	}

	@Test
	public void testPayAmountWhenAmountIsHalfOfTheTotalDebt() {
		Double totalDebt = Arrays.asList(firstReceipt, secondReceipt, thirdReceipt).stream()
				.map(r -> r.getAccountings().get(0)).mapToDouble(Accounting::getAmount).sum();

		entityManager.getTransaction().begin();
		paymentManager.makePayment(totalDebt / 2.0, loggedUser, payerUser);
		entityManager.getTransaction().commit();

		List<Accounting> unpaidAccountings = entityManager
				.createQuery("from Accounting where amount!=:amount and user=:user", Accounting.class)
				.setParameter("amount", 0.0).setParameter("user", loggedUser).getResultList();

		Accounting secondAccounting = new Accounting(loggedUser, secondReceipt.getTotalPrice() / 4.0);
		secondAccounting.setReceipt(secondReceipt);

		Accounting thirdAccounting = new Accounting(loggedUser, secondReceipt.getTotalPrice() / 2.0);
		thirdAccounting.setReceipt(thirdReceipt);

		assertThat(unpaidAccountings).containsExactlyInAnyOrder(secondAccounting, thirdAccounting);
	}

	@Test
	public void testMakePaymentWithReceiptWhenNewAccountingIsEqualToTheDebtTowardAnotherUser() {
		Double totalDebt = Arrays.asList(firstReceipt, secondReceipt, thirdReceipt).stream()
				.map(r -> r.getAccountings().get(0)).mapToDouble(Accounting::getAmount).sum();

		Item item1 = new Item("item1", totalDebt, Arrays.asList(loggedUser, payerUser));
		Item item2 = new Item("item2", totalDebt, Arrays.asList(loggedUser, payerUser));
		Receipt loggedUserReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payerUser, loggedUser,
				new GregorianCalendar(2018, 8, 20), Arrays.asList(item1, item2));

		
		entityManager.getTransaction().begin();
		entityManager.persist(loggedUserReceipt);
		paymentManager.makePaymentWithReceipt(loggedUserReceipt, loggedUser);
		entityManager.getTransaction().commit();

		List<Accounting> unpaidAccountingsOfLoggedUser = entityManager
				.createQuery("from Accounting where amount!=:amount and user=:user", Accounting.class)
				.setParameter("amount", 0.0).setParameter("user", loggedUser).getResultList();

		List<Accounting> unpaidAccountingsOfPayerUser = entityManager
				.createQuery("from Accounting where amount!=:amount and user=:user", Accounting.class)
				.setParameter("amount", 0.0).setParameter("user", payerUser).getResultList();

		
		assertThat(unpaidAccountingsOfLoggedUser).isEmpty();
		assertThat(unpaidAccountingsOfPayerUser).isEmpty();
	}

}
