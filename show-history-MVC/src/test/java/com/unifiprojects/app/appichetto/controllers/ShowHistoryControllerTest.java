package com.unifiprojects.app.appichetto.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

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
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.FakeTransaction;
import com.unifiprojects.app.appichetto.views.ReceiptView;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class ShowHistoryControllerTest {

	@InjectMocks
	private ShowHistoryController showHistoryController;
	
	@Mock
	private ReceiptRepository receiptRepository;
	
	@Mock
	private ShowHistoryView showHistoryView;
	
	@Mock
	private ReceiptView receiptView;
	
	@Captor
	ArgumentCaptor<String> stringCaptor;
	
	@Captor
	ArgumentCaptor<List<Receipt>> receiptListCaptor;
	
	private User loggedUser;

	private Receipt receipt1;
	private Receipt receipt0;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		reset(showHistoryView);
		loggedUser = new User("logged", "pw");
		showHistoryController.setLoggedUser(loggedUser);
		
		User otherOwner1 = new User("other1", "pw");
		User otherOwner2 = new User("other2", "pw");

		// receipt1
		Item item0 = new Item("tomato", 12.0, Arrays.asList(loggedUser, otherOwner1));
		Item item1 = new Item("potato", 4.0, Arrays.asList(loggedUser, otherOwner2));

		
		receipt0 = new Receipt();
				
		receipt0.setBuyer(loggedUser);
		receipt0.setTimestamp(new GregorianCalendar(2019, 8, 20));
		receipt0.setItems(Arrays.asList(item0, item1));
		
		Accounting accounting1ToOtherOwner1 = new Accounting(otherOwner1, item0.getPricePerOwner());
		Accounting accounting1ToOtherOwner2 = new Accounting(otherOwner2, item1.getPricePerOwner());

		receipt0.setAccountingList(Arrays.asList(accounting1ToOtherOwner1, accounting1ToOtherOwner2));
		
		// receipt1
		Item item2 = new Item("hamburger", 15.0, Arrays.asList(otherOwner1, otherOwner2));
		Item item3 = new Item("bread", 5.0, Arrays.asList(loggedUser, otherOwner1, otherOwner2));
		
		receipt1 = new Receipt();
		
		receipt1.setBuyer(loggedUser);
		receipt1.setTimestamp(new GregorianCalendar(2019, 8, 15));
		receipt1.setItems(Arrays.asList(item2, item3));
		
		Accounting accounting2ToOtherOwner1 = new Accounting(otherOwner1, item2.getPricePerOwner() + item3.getPricePerOwner()); 
		Accounting accounting2ToOtherOwner2 = new Accounting(otherOwner2, item2.getPricePerOwner() + item3.getPricePerOwner());
		
		receipt1.setAccountingList(Arrays.asList(accounting2ToOtherOwner1, accounting2ToOtherOwner2));
		
	}
	
	@Test
	public void testShowHistoryUpdateTheView() {
		
		List<Receipt> history = Arrays.asList(receipt1, receipt0);
		when(receiptRepository.getAllReceiptsBoughtBy(loggedUser)).thenReturn(history);
		
		showHistoryController.showHistory();
		
		verify(showHistoryView).showShoppingHistory(history);
		verifyNoMoreInteractions(showHistoryView);
	}
	
	@Test
	public void testShowHistoryShowAnErrorMsgIfTheResultIsEmpty() {
		List<Receipt> history = Arrays.asList();
		when(receiptRepository.getAllReceiptsBoughtBy(loggedUser)).thenReturn(history);
		showHistoryController.showHistory();
		verify(showHistoryView).showShoppingHistory(history);
		verify(showHistoryView).showErrorMsg(stringCaptor.capture());
		verifyNoMoreInteractions(showHistoryView);
	}
	
	@Test
	public void testShowHistoryUpdateOrdersTheReceiptsByTheLatestBeforeCallingTheView() {
		Receipt receipt2 = new Receipt();
		receipt2.setBuyer(loggedUser);
		
		receipt2.setTimestamp(new GregorianCalendar(2019, 7, 18));
		receipt0.setTimestamp(new GregorianCalendar(2019, 8, 4));
		receipt1.setTimestamp(new GregorianCalendar(2019, 8, 18));
		
		
		List<Receipt> history = Arrays.asList(receipt2, receipt0, receipt1);
		
		when(receiptRepository.getAllReceiptsBoughtBy(loggedUser)).thenReturn(history);
		
		showHistoryController.showHistory();
		
		verify(showHistoryView).showShoppingHistory(receiptListCaptor.capture());
		verifyNoMoreInteractions(showHistoryView);

		assertThat(receiptListCaptor.getValue()).isEqualTo(Arrays.asList(receipt1, receipt0, receipt2));

	}

	@Test
	public void testRemoveReceipt() {
		showHistoryController.setTransaction(new FakeTransaction());
		
		
		showHistoryController.removeReceipt(receipt0);
		
		verify(receiptRepository).removeReceipt(receipt0);
		verify(showHistoryView).showErrorMsg(String.format("Receipt (from %s) deleted.", receipt0.getTimestamp().getTime()));
		verify(showHistoryView).showShoppingHistory(receiptListCaptor.capture());
	}
	
	@Test
	public void testRemoveReceiptWhenUncommittableExceptionIsLaunched() {
		showHistoryController.setTransaction(new FakeTransaction());
		doThrow(new UncommittableTransactionException()).when(receiptRepository).removeReceipt(receipt0);
				
		showHistoryController.removeReceipt(receipt0);
			
		verify(showHistoryView).showErrorMsg("Something went wrong with the database.");
		verify(showHistoryView).showShoppingHistory(receiptListCaptor.capture());
	}
	
	@Test
	public void testUpdate() {
		List<Receipt> history = Arrays.asList(receipt1, receipt0);
		when(receiptRepository.getAllReceiptsBoughtBy(loggedUser)).thenReturn(history);
		
		showHistoryController.update();
		
		verify(showHistoryView).showShoppingHistory(history);
		verifyNoMoreInteractions(showHistoryView);
	}
}
