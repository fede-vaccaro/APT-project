package com.unifiprojects.app.appichetto.controllerTests;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import com.unifiprojects.app.appichetto.controllers.PayViewReceiptsController;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.views.PayViewReceiptsView;

public class PayViewReceiptsControllerTest {

	@InjectMocks
	private PayViewReceiptsController payViewReceiptsController;

	@Mock
	private ReceiptRepository receiptRepository;

	@Mock
	private AccountingRepository accountingRepository;

	@Mock
	private PayViewReceiptsView payViewReceiptsView;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testRepositoryAndViewAreDelegatedWhenShowUnpaidReceiptsThenShowItems() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));

		List<Receipt> unpaids = Arrays.asList(receipt);
		when(receiptRepository.getAllUnpaidReceiptOf(loggedUser)).thenReturn(unpaids);

		payViewReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser);
		verify(payViewReceiptsView).showReceipts(unpaids);
		verify(payViewReceiptsView).showItems(unpaids.get(0).getItems());
	}

	@Test
	public void testNoItemIsShownWhenUnpaidReceiptsIsVoid() {
		User loggedUser = new User("logged", "pw");

		List<Receipt> unpaids = Arrays.asList();
		when(receiptRepository.getAllUnpaidReceiptOf(loggedUser)).thenReturn(unpaids);

		payViewReceiptsController.showUnpaidReceiptsOfLoggedUser(loggedUser);
		verify(payViewReceiptsView).showReceipts(unpaids);
		verifyNoMoreInteractions(payViewReceiptsView);

	}

	public Receipt generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(User loggedUser, User payerUser,
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
	public void testAccountingIsPaidWhenPayAmountIfThereIsOnlyOneReceiptAndPaymentIsEqualToDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Accounting accounting = receipt1.getAccountings().get(0);
		double exactAmountToPay = accounting.getAmount();

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));
		payViewReceiptsController.payAmount(exactAmountToPay, loggedUser, payerUser);

		assertThat(accounting.isPaid()).isTrue();
		verify(accountingRepository).saveAccounting(accounting);
	}

	@Test
	public void testAccountingIsNotPaidButScaledWhenPayAmountIsLessThanDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));

		Accounting accounting = receipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));

		double difference = 2.0;
		double amountToPay = accounting.getAmount() - difference;

		payViewReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		assertThat(accounting.isPaid()).isFalse();
		assertThat(accounting.getAmount()).isEqualTo(difference);
		verify(accountingRepository).saveAccounting(accounting);
	}

	@Test
	public void testOlderAccountingIsPaidFirstAndNewerLaterWhenPayAmountIsEqualToDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Receipt olderReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 4));

		InOrder inOrder = inOrder(accountingRepository);

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting = olderReceipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting));

		double amountToPay = newerAccounting.getAmount() + olderAccounting.getAmount();

		payViewReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		assertThat(olderAccounting.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isTrue();
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting);
		inOrder.verify(accountingRepository).saveAccounting(newerAccounting);
		verifyNoMoreInteractions(accountingRepository);
	}

	@Test
	public void testOnlyOlderAccountingIsPaidAndNewerScaledWhenPayAmountIsLessThanDebtButEnoughToPayTheFirstAccounting() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 4));

		InOrder inOrder = inOrder(accountingRepository);

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting = olderReceipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting));

		double difference = 2.0;
		double amountToPay = newerAccounting.getAmount() + olderAccounting.getAmount() - difference;

		payViewReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		assertThat(olderAccounting.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isFalse();
		assertThat(newerAccounting.getAmount()).isEqualTo(difference);
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting);
		inOrder.verify(accountingRepository).saveAccounting(newerAccounting);
		verifyNoMoreInteractions(accountingRepository);
	}

	@Test
	public void testOnlyFirstTwoOlderAccountigArePaidAndNewerScaledWhenPayAmountIsLessThanDebtButEnoughToPayTheFirstTwoAccounting() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		double difference = 2.0;
		double amountToPay = newerAccounting.getAmount() + olderAccounting1.getAmount() + olderAccounting2.getAmount()
				- difference;

		payViewReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

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
	}

	@Test
	public void testOnlyOlderAccountingIsPaidNewerScaledAndLastUnpaidWhenPayAmountIsLessThanDebtButEnoughToPayJustTheFirstAccounting() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		double surplus = 2.0;
		double amountToPay = newerAccounting.getAmount() + surplus;

		double oldAccounting2Amount = olderAccounting2.getAmount();

		payViewReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		InOrder inOrder = inOrder(accountingRepository);

		assertThat(olderAccounting1.isPaid()).isTrue();
		assertThat(olderAccounting2.isPaid()).isFalse();
		assertThat(newerAccounting.isPaid()).isFalse();
		assertThat(olderAccounting2.getAmount()).isEqualTo(oldAccounting2Amount - surplus);
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting1);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting2);
		verifyNoMoreInteractions(accountingRepository);
	}

	@Test
	public void testOnlyPayer1DebtArePayedByLoggedUserAndNotPayer2WhenPayingPayer1() {
		User loggedUser = new User("logged", "pw");
		User payer1 = new User("payer1", "pw");
		User payer2 = new User("payer2", "pw");

		Receipt receiptByPayer1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer1,
				new GregorianCalendar(2019, 8, 14));

		Receipt receiptByPayer2 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payer2,
				new GregorianCalendar(2019, 8, 3));

		Accounting accountingByPayer1 = receiptByPayer1.getAccountings().get(0);
		Accounting accountingByPayer2 = receiptByPayer2.getAccountings().get(0);

		double amountToPay = accountingByPayer1.getAmount();

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(accountingByPayer1, accountingByPayer2));

		payViewReceiptsController.payAmount(amountToPay, loggedUser, payer1);

		InOrder inOrder = inOrder(accountingRepository);

		assertThat(accountingByPayer1.isPaid()).isTrue();
		assertThat(accountingByPayer2.isPaid()).isFalse();
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(accountingByPayer1);
		verifyNoMoreInteractions(accountingRepository);

	}

	@Test
	public void testNothingIsPayedIfAmountIsLessEqualThanZeroAndShowErrorMsg() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw2");

		double amountToPay = -3.0;

		payViewReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		verify(payViewReceiptsView).showErrorMsg("Amount payed should be more than zero.");
		verifyNoMoreInteractions(accountingRepository);
	}
	
	@Test
	public void testNothingIsPayedIfEnteredAmountIsMoreThanAmountToPayAndShowErrorMsg() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Accounting accounting = receipt1.getAccountings().get(0);
		double superiorAmountToPay = accounting.getAmount()*2.0;

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));
		payViewReceiptsController.payAmount(superiorAmountToPay, loggedUser, payerUser);
		
		verify(accountingRepository).getAccountingsOf(loggedUser);
		verify(payViewReceiptsView).showErrorMsg("Entered amount more than should be payed.");
		verifyNoMoreInteractions(accountingRepository);
		assertThat(accounting.isPaid()).isFalse();
	}


}
