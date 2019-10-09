package com.unifiprojects.app.appichetto.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.FakeTransaction;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionCommands;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

public class PayReceiptsControllerTest {

	@InjectMocks
	private PayReceiptsController payReceiptsController;

	@Mock
	private ReceiptRepository receiptRepository;

	@Mock
	private AccountingRepository accountingRepository;

	@Mock
	private PayReceiptsView payReceiptsView;

	@Captor
	private ArgumentCaptor<List<Receipt>> listReceiptCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		payReceiptsController.setTransactionHandler(new FakeTransaction());
	}

	@Test
	public void testRepositoryAndViewAreDelegatedWhenShowUnpaidReceiptsThenShowItems() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));

		List<Receipt> unpaids = Arrays.asList(receipt);
		when(receiptRepository.getAllUnpaidReceiptsOf(loggedUser)).thenReturn(unpaids);

		payReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser);
		verify(payReceiptsView).showReceipts(unpaids);
	}

	@Test
	public void testReceiptsAreOrderedFromLatestToOlderWhenCallingShowUnpaidReceipts() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 10));
		Receipt receipt3 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 17));

		List<Receipt> unpaids = Arrays.asList(receipt2, receipt1, receipt3);
		when(receiptRepository.getAllUnpaidReceiptsOf(loggedUser)).thenReturn(unpaids);

		payReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser);

		List<Receipt> unpaidsOrdered = Arrays.asList(receipt3, receipt2, receipt1);

		verify(payReceiptsView).showReceipts(unpaidsOrdered);

	}

	@Test
	public void testNoItemIsShownWhenUnpaidReceiptsIsVoid() {
		User loggedUser = new User("logged", "pw");

		List<Receipt> unpaids = Arrays.asList();
		when(receiptRepository.getAllUnpaidReceiptsOf(loggedUser)).thenReturn(unpaids);

		payReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser);
		verify(payReceiptsView).showReceipts(unpaids);
		verifyNoMoreInteractions(payReceiptsView);

	}


	@Test
	public void testAccountingIsPaidWhenCalledPayAmountIfThereIsOnlyOneReceiptAndEnteredAmountIsEqualToDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Accounting accounting = receipt1.getAccountings().get(0);
		double exactAmountToPay = accounting.getAmount();

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));
		payReceiptsController.payAmount(exactAmountToPay, loggedUser, payerUser);

		assertThat(accounting.isPaid()).isTrue();
		verify(accountingRepository).saveAccounting(accounting);
		verify(payReceiptsView).showReceipts(listReceiptCaptor.capture());
	}

	@Test
	public void testAccountingIsNotPaidButScaledWhenCalledPayAmountAndEnteredAmountIsLessThanDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));

		Accounting accounting = receipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));

		double difference = 2.0;
		double amountToPay = accounting.getAmount() - difference;

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		assertThat(accounting.isPaid()).isFalse();
		assertThat(accounting.getAmount()).isEqualTo(difference);
		verify(accountingRepository).saveAccounting(accounting);
		verify(payReceiptsView).showReceipts(listReceiptCaptor.capture());
	}

	@Test
	public void testOlderAccountingIsPaidFirstAndNewerLaterWhenCalledPayAmountAndEnteredAmountIsEqualToDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Receipt olderReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 4));

		InOrder inOrder = inOrder(accountingRepository);

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting = olderReceipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting));

		double amountToPay = newerAccounting.getAmount() + olderAccounting.getAmount();

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		assertThat(olderAccounting.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isTrue();
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting);
		inOrder.verify(accountingRepository).saveAccounting(newerAccounting);
		verifyNoMoreInteractions(accountingRepository);
		verify(payReceiptsView).showReceipts(listReceiptCaptor.capture());
	}

	@Test
	public void testOnlyOlderAccountingIsPaidAndNewerScaledWhenPayAmountIsLessThanDebtButEnoughToPayTheFirstAccounting() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 4));

		InOrder inOrder = inOrder(accountingRepository);

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting = olderReceipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting));

		double difference = 2.0;
		double amountToPay = newerAccounting.getAmount() + olderAccounting.getAmount() - difference;

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		assertThat(olderAccounting.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isFalse();
		assertThat(newerAccounting.getAmount()).isEqualTo(difference);
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting);
		inOrder.verify(accountingRepository).saveAccounting(newerAccounting);
		verifyNoMoreInteractions(accountingRepository);
		verify(payReceiptsView).showReceipts(listReceiptCaptor.capture());
	}

	@Test
	public void testOnlyFirstTwoOlderAccountigArePaidAndNewerScaledWhenCalledPayAmountAndEnteredAmountIsLessThanDebtButEnoughToPayTheFirstTwoAccountings() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		double difference = 2.0;
		double amountToPay = newerAccounting.getAmount() + olderAccounting1.getAmount() + olderAccounting2.getAmount()
				- difference;

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		InOrder inOrder = inOrder(accountingRepository);

		assertThat(olderAccounting1.isPaid()).isTrue();
		assertThat(olderAccounting2.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isFalse();
		assertThat(newerAccounting.getAmount()).isEqualTo(difference);
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting1);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting2);
		inOrder.verify(accountingRepository).saveAccounting(newerAccounting);
		verifyNoMoreInteractions(accountingRepository);
		verify(payReceiptsView).showReceipts(listReceiptCaptor.capture());
	}

	@Test
	public void testOnlyOlderAccountingIsPaidNewerScaledAndLastUnpaidWhenCalledPayAmountButEnteredAmountIsLessThanDebtButEnoughToPayJustTheFirstAccounting() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		double surplus = 2.0;
		double amountToPay = newerAccounting.getAmount() + surplus;

		double oldAccounting2Amount = olderAccounting2.getAmount();

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		InOrder inOrder = inOrder(accountingRepository);

		assertThat(olderAccounting1.isPaid()).isTrue();
		assertThat(olderAccounting2.isPaid()).isFalse();
		assertThat(newerAccounting.isPaid()).isFalse();
		assertThat(olderAccounting2.getAmount()).isEqualTo(oldAccounting2Amount - surplus);
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting1);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting2);
		verifyNoMoreInteractions(accountingRepository);
		verify(payReceiptsView).showReceipts(listReceiptCaptor.capture());
	}

	@Test
	public void testShowErrorMessageIfUncommittableExceptionIsLaunchedDuringTransaction() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 14));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(newerAccounting));

		double surplus = 2.0;
		double amountToPay = newerAccounting.getAmount() - surplus;

		TransactionHandler throwingExceptionTransaction = new TransactionHandler() {
			@Override
			public void doInTransaction(TransactionCommands command) throws UncommittableTransactionException {
				try {
					command.execute();
					throw new UncommittableTransactionException("Can't connect to the DB");
				} catch (IllegalArgumentException e) {

				}
			}
		};

		payReceiptsController.setTransactionHandler(throwingExceptionTransaction);

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		verify(payReceiptsView).showErrorMsg("Something went wrong while committing the payment.");
	}

	@Test
	public void testOnlyPayer1DebtArePayedByLoggedUserAndNotPayer2WhenPayingPayer1() {
		User loggedUser = new User("logged", "pw");
		User payer1 = new User("payer1", "pw");
		User payer2 = new User("payer2", "pw");

		Receipt receiptByPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer1,
				new GregorianCalendar(2019, 8, 14));

		Receipt receiptByPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer2,
				new GregorianCalendar(2019, 8, 3));

		Accounting accountingByPayer1 = receiptByPayer1.getAccountings().get(0);
		Accounting accountingByPayer2 = receiptByPayer2.getAccountings().get(0);

		double amountToPay = accountingByPayer1.getAmount();

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(accountingByPayer1, accountingByPayer2));

		payReceiptsController.payAmount(amountToPay, loggedUser, payer1);

		InOrder inOrder = inOrder(accountingRepository);

		assertThat(accountingByPayer1.isPaid()).isTrue();
		assertThat(accountingByPayer2.isPaid()).isFalse();
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(accountingByPayer1);
		verifyNoMoreInteractions(accountingRepository);

	}

	@Test
	public void testNothingIsPayedIfAmountIsLessZeroAndShowErrorMsg() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw2");

		double amountToPay = -3.0;

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		verify(payReceiptsView).showErrorMsg("Amount payed should be more than zero.");
		verifyNoMoreInteractions(accountingRepository);
	}

	@Test
	public void testNothingIsPayedIfAmountIsEqualToZeroAndShowErrorMsg() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw2");

		double amountToPay = 0.0;

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		verify(payReceiptsView).showErrorMsg("Amount payed should be more than zero.");
		verifyNoMoreInteractions(accountingRepository);
	}

	@Test
	public void testNothingIsPayedIfEnteredAmountIsMoreThanAmountToPayAndShowErrorMsg() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Accounting accounting = receipt1.getAccountings().get(0);
		double superiorAmountToPay = accounting.getAmount() * 2.0;

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));
		payReceiptsController.payAmount(superiorAmountToPay, loggedUser, payerUser);

		verify(accountingRepository).getAccountingsOf(loggedUser);
		verify(payReceiptsView).showErrorMsg("Entered amount more than should be payed.");
		verifyNoMoreInteractions(accountingRepository);
		assertThat(accounting.isPaid()).isFalse();
	}

}
