package com.unifiprojects.app.appichetto.managers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.controllers.ReceiptGenerator;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;

public class PaymentManagerTest {

	@Mock
	private AccountingRepository accountingRepository;

	@InjectMocks
	private PaymentManager paymentManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testMakePaymentThatAccountingIsPaidWhenIfThereIsOnlyOneReceiptAndEnteredAmountIsEqualToDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Accounting accounting = receipt1.getAccountings().get(0);
		double exactAmountToPay = accounting.getAmount();

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));
		paymentManager.makePayment(exactAmountToPay, loggedUser, payerUser);

		assertThat(accounting.isPaid()).isTrue();
		verify(accountingRepository).saveAccounting(accounting);
	}

	@Test
	public void testAccountingIsNotPaidButScaledWhenCalledMakePaymentAndEnteredAmountIsLessThanDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));

		Accounting accounting = receipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));

		double difference = 2.0;
		double amountToPay = accounting.getAmount() - difference;

		paymentManager.makePayment(amountToPay, loggedUser, payerUser);

		assertThat(accounting.isPaid()).isFalse();
		assertThat(accounting.getAmount()).isEqualTo(difference);
		verify(accountingRepository).saveAccounting(accounting);
	}

	@Test
	public void testOlderAccountingIsPaidFirstAndNewerLaterWhenCalledMakePaymentAndEnteredAmountIsEqualToDebt() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 9, 14));
		Receipt olderReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 9, 4));

		InOrder inOrder = inOrder(accountingRepository);

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting = olderReceipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting));

		double amountToPay = newerAccounting.getAmount() + olderAccounting.getAmount();

		paymentManager.makePayment(amountToPay, loggedUser, payerUser);

		assertThat(olderAccounting.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isTrue();
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting);
		inOrder.verify(accountingRepository).saveAccounting(newerAccounting);
		verifyNoMoreInteractions(accountingRepository);
	}

	@Test
	public void testMakePaymentOnlyOlderAccountingIsPaidAndNewerScaledWhenPayAmountIsLessThanDebtButEnoughToPayTheFirstAccounting() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 4));

		InOrder inOrder = inOrder(accountingRepository);

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting = olderReceipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting));

		double difference = 2.0;
		double amountToPay = newerAccounting.getAmount() + olderAccounting.getAmount() - difference;

		paymentManager.makePayment(amountToPay, loggedUser, payerUser);

		assertThat(olderAccounting.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isFalse();
		assertThat(newerAccounting.getAmount()).isEqualTo(difference);
		inOrder.verify(accountingRepository).getAccountingsOf(loggedUser);
		inOrder.verify(accountingRepository).saveAccounting(olderAccounting);
		inOrder.verify(accountingRepository).saveAccounting(newerAccounting);
		verifyNoMoreInteractions(accountingRepository);
	}

	@Test
	public void testOnlyFirstTwoOlderAccountigArePaidAndNewerScaledWhenCalledMakePaymentAndEnteredAmountIsLessThanDebtButEnoughToPayTheFirstTwoAccountings() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		double difference = 2.0;
		double amountToPay = newerAccounting.getAmount() + olderAccounting1.getAmount() + olderAccounting2.getAmount()
				- difference;

		paymentManager.makePayment(amountToPay, loggedUser, payerUser);

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
	public void testOnlyOlderAccountingIsPaidNewerScaledAndLastUnpaidWhenCalledMakePaymentButEnteredAmountIsLessThanDebtButEnoughToPayJustTheFirstAccounting() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		double surplus = 2.0;
		double amountToPay = newerAccounting.getAmount() + surplus;

		double oldAccounting2Amount = olderAccounting2.getAmount();

		paymentManager.makePayment(amountToPay, loggedUser, payerUser);

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

		Receipt receiptByPayer1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payer1, new GregorianCalendar(2019, 8, 14));

		Receipt receiptByPayer2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payer2, new GregorianCalendar(2019, 8, 3));

		Accounting accountingByPayer1 = receiptByPayer1.getAccountings().get(0);
		Accounting accountingByPayer2 = receiptByPayer2.getAccountings().get(0);

		double amountToPay = accountingByPayer1.getAmount();

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(accountingByPayer1, accountingByPayer2));

		paymentManager.makePayment(amountToPay, loggedUser, payer1);

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

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> paymentManager.makePayment(amountToPay, loggedUser, payerUser));
	}

	@Test
	public void testNothingIsPayedIfAmountIsEqualToZeroAndShowErrorMsg() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw2");

		double amountToPay = 0.0;

		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> paymentManager.makePayment(amountToPay, loggedUser, payerUser));
	}

	@Test
	public void testNothingIsPayedIfEnteredAmountIsMoreThanAmountToPayAndShowErrorMsg() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));
		Accounting accounting = receipt1.getAccountings().get(0);
		double amountToPayHigher = accounting.getAmount() * 2.0;

		when(accountingRepository.getAccountingsOf(loggedUser)).thenReturn(Arrays.asList(accounting));
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> paymentManager.makePayment(amountToPayHigher, loggedUser, payerUser));

		verify(accountingRepository).getAccountingsOf(loggedUser);
		verifyNoMoreInteractions(accountingRepository);
		assertThat(accounting.isPaid()).isFalse();
	}

	@Test
	public void testMakePaymentWithReceiptWhenNewAccountingIsEqualToTheDebtTowardAnotherUser() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		// debt from loggedUser to payerUser
		double amountToPay = newerAccounting.getAmount() + olderAccounting2.getAmount() + olderAccounting1.getAmount();

		// loggedUser want to "pay" with a receipt, which some items are shared with
		// payerUser
		Item item1 = new Item("item1", amountToPay, Arrays.asList(loggedUser, payerUser));
		Item item2 = new Item("item2", amountToPay, Arrays.asList(loggedUser, payerUser));
		Receipt loggedUserReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payerUser,
				loggedUser, new GregorianCalendar(2018, 8, 20), Arrays.asList(item1, item2));
		Accounting loggedUserAccounting = loggedUserReceipt.getAccountings().get(0);
		// this means that the new generated debt is from payerUser to loggedUser, and
		// it is equal to amountToPay/2.0 + amountToPay/2.0, because they
		// shares the two items

		paymentManager.makePaymentWithReceipt(loggedUserReceipt, loggedUser);

		// everything is paid
		assertThat(olderAccounting1.isPaid()).isTrue();
		assertThat(olderAccounting2.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isTrue();

		assertThat(loggedUserAccounting.isPaid()).isTrue();

	}

	@Test
	public void testMakePaymentWithReceiptWhenNewAccountingIsMoreThanTheDebtTowardAnotherUser() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		// debt from loggedUser to payerUser
		double amountToPay = newerAccounting.getAmount() + olderAccounting2.getAmount() + olderAccounting1.getAmount();

		// loggedUser want to "pay" with a receipt, which some items are shared with
		// payerUser
		Item item1 = new Item("item1", amountToPay, Arrays.asList(loggedUser, payerUser));
		Item item2 = new Item("item2", amountToPay, Arrays.asList(loggedUser, payerUser));
		Item item3 = new Item("item3", amountToPay, Arrays.asList(loggedUser, payerUser));
		Receipt loggedUserReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payerUser,
				loggedUser, new GregorianCalendar(2018, 8, 20), Arrays.asList(item1, item2, item3));
		Accounting loggedUserAccounting = loggedUserReceipt.getAccountings().get(0);
		// this means that the new generated debt is from payerUser to loggedUser, and
		// it is equal to 3.0*(amountToPay/2.0), because they
		// shares the three items
		// we expect that just 2/3 of the loggedUserAccounting is paid, so the remaining
		// should be amountToPay/2.0

		paymentManager.makePaymentWithReceipt(loggedUserReceipt, loggedUser);

		// everything is paid
		assertThat(olderAccounting1.isPaid()).isTrue();
		assertThat(olderAccounting2.isPaid()).isTrue();
		assertThat(newerAccounting.isPaid()).isTrue();

		// but the payerUser now has debt to loggedUser
		assertThat(loggedUserAccounting.isPaid()).isFalse();
		assertThat(loggedUserAccounting.getAmount()).isEqualTo(amountToPay / 2.0);
	}

	@Test
	public void testMakePaymentWithReceiptWhenNewAccountingIsLessThanTheDebtTowardAnotherUser() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 14));
		Receipt olderReceipt2 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 4));
		Receipt olderReceipt1 = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 1));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);
		Accounting olderAccounting2 = olderReceipt2.getAccountings().get(0);
		Accounting olderAccounting1 = olderReceipt1.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(newerAccounting, olderAccounting2, olderAccounting1));

		// debt from loggedUser to payerUser
		double amountToPay = newerAccounting.getAmount() + olderAccounting2.getAmount() + olderAccounting1.getAmount();

		// loggedUser want to "pay" with a receipt, which some items are shared with
		// payerUser
		Item item1 = new Item("item1", amountToPay, Arrays.asList(loggedUser, payerUser));
		Receipt loggedUserReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(payerUser,
				loggedUser, new GregorianCalendar(2018, 8, 20), Arrays.asList(item1));
		Accounting loggedUserAccounting = loggedUserReceipt.getAccountings().get(0);
		// this means that the new generated debt is from payerUser to loggedUser, and
		// it is equal to amountToPay/2.0
		// we expect that the entire loggedUserAccounting is paid, and:
		// - the olderReceipt1 should be paid
		// - the olderReceipt2 should not be paid
		// - the newerReceipt should not be paid at all

		paymentManager.makePaymentWithReceipt(loggedUserReceipt, loggedUser);

		assertThat(olderAccounting1.isPaid()).isTrue();
		assertThat(olderAccounting2.isPaid()).isFalse();
		assertThat(newerAccounting.isPaid()).isFalse();

		assertThat(loggedUserAccounting.isPaid()).isTrue();
	}

	@Test
	public void testMakePaymentWithReceiptPayToEachUserTowardTheLoggedHasDebt() {
		User loggedUser = new User("logged", "pw");
		User payer1 = new User("payer1", "pw");
		User payer2 = new User("payer2", "pw");

		Receipt payer1Receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payer1, new GregorianCalendar(2018, 9, 10));
		Receipt payer2Receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payer2, new GregorianCalendar(2018, 9, 9));

		Accounting payer1Accounting = payer1Receipt.getAccountings().get(0);
		Accounting payer2Accounting = payer2Receipt.getAccountings().get(0);

		when(accountingRepository.getAccountingsOf(loggedUser))
				.thenReturn(Arrays.asList(payer1Accounting, payer2Accounting));
		
		double amountToPayToPayer1 = payer1Accounting.getAmount();
		double amountToPayToPayer2 = payer2Accounting.getAmount();
		
		Receipt loggedUserReceipt = new Receipt();
		loggedUserReceipt.setBuyer(loggedUser);
		Item itemBoughtForPayer1 = new Item("shareWithPayer1", amountToPayToPayer1, Arrays.asList(payer1));
		Item itemBoughtForPayer2 = new Item("shareWithPayer2", amountToPayToPayer2, Arrays.asList(payer2));
		Accounting accountingToPayer1 = new Accounting(payer1, amountToPayToPayer1);
		Accounting accountingToPayer2 = new Accounting(payer2, amountToPayToPayer2);
		loggedUserReceipt.setItems(Arrays.asList(itemBoughtForPayer1, itemBoughtForPayer2));
		loggedUserReceipt.setAccountingList(Arrays.asList(accountingToPayer1, accountingToPayer2));
		
		paymentManager.makePaymentWithReceipt(loggedUserReceipt, loggedUser);

		assertThat(payer1Accounting.isPaid()).isTrue();
		assertThat(payer2Accounting.isPaid()).isTrue();
		
		assertThat(accountingToPayer1.isPaid()).isTrue();
		assertThat(accountingToPayer2.isPaid()).isTrue();
	}

}
