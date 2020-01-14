package com.unifiprojects.app.appichetto.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.managers.PaymentManager;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
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
	private PaymentManager paymentManager;

	@Mock
	private PayReceiptsView payReceiptsView;

	@Mock
	private TransactionHandler transaction;

	@Captor
	private ArgumentCaptor<List<Receipt>> listReceiptCaptor;

	@Captor
	private ArgumentCaptor<TransactionCommands> commandCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testRepositoryAndViewAreDelegatedWhenShowUnpaidReceiptsThenShowItems() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));

		List<Receipt> unpaids = Arrays.asList(receipt);
		when(receiptRepository.getAllUnpaidReceiptsOf(loggedUser)).thenReturn(unpaids);

		payReceiptsController.showUnpaidReceipts(loggedUser);
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

		payReceiptsController.showUnpaidReceipts(loggedUser);

		List<Receipt> unpaidsOrdered = Arrays.asList(receipt3, receipt2, receipt1);

		verify(payReceiptsView).showReceipts(unpaidsOrdered);

	}

	@Test
	public void testNoItemIsShownWhenUnpaidReceiptsIsVoid() {
		User loggedUser = new User("logged", "pw");

		List<Receipt> unpaids = Arrays.asList();
		when(receiptRepository.getAllUnpaidReceiptsOf(loggedUser)).thenReturn(unpaids);

		payReceiptsController.showUnpaidReceipts(loggedUser);
		verify(payReceiptsView).showReceipts(unpaids);
		verifyNoMoreInteractions(payReceiptsView);

	}

	@Test
	public void testShowErrorMessageIfUncommittableExceptionIsLaunchedDuringTransaction() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt newerReceipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser,
				payerUser, new GregorianCalendar(2019, 8, 14));

		Accounting newerAccounting = newerReceipt.getAccountings().get(0);

		double surplus = 2.0;
		double amountToPay = newerAccounting.getAmount() - surplus;

		doThrow(UncommittableTransactionException.class).when(transaction)
				.doInTransaction(any(TransactionCommands.class));

		payReceiptsController.payAmount(amountToPay, loggedUser, payerUser);

		verify(payReceiptsView).showErrorMsg("Something went wrong while committing the payment.");
	}

	@Test
	public void testShowErrorMsgWhenIllegalArgumentExceptionIsThrown() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw2");
		double invalidAmount = -1.0;

		doThrow(IllegalArgumentException.class).when(paymentManager).makePayment(invalidAmount, loggedUser, payerUser);

		payReceiptsController.setTransactionHandler(new FakeTransaction());
		payReceiptsController.payAmount(invalidAmount, loggedUser, payerUser);

		verify(payReceiptsView).showErrorMsg(anyString());
	}

	@Test
	public void testPayAmountCallPaymentManagerInTransaction() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw2");
		double amount = 10.0;

		payReceiptsController.payAmount(amount, loggedUser, payerUser);
		verify(transaction).doInTransaction(commandCaptor.capture());

		commandCaptor.getValue().execute();

		verify(paymentManager).makePayment(amount, loggedUser, payerUser);
	}

	@Test
	public void testViewIsRefreshedAfterCallingPaymentManager() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw2");
		double amount = 10.0;
		
		when(receiptRepository.getAllUnpaidReceiptsOf(loggedUser)).thenReturn(Arrays.asList(new Receipt(payerUser)));
		
		payReceiptsController.payAmount(amount, loggedUser, payerUser);

		verify(payReceiptsView).showReceipts(Arrays.asList(new Receipt(payerUser)));
	}
	
	@Test
	public void testUpdate() {
		User loggedUser = new User("logged", "pw");
		User payerUser = new User("payer", "pw");

		Receipt receipt = ReceiptGenerator.generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(loggedUser, payerUser,
				new GregorianCalendar(2019, 9, 14));

		List<Receipt> unpaids = Arrays.asList(receipt);
		when(receiptRepository.getAllUnpaidReceiptsOf(loggedUser)).thenReturn(unpaids);
		payReceiptsController.setLoggedUser(loggedUser);
		
		payReceiptsController.update();
		
		verify(payReceiptsView).showReceipts(unpaids);
	}

}
