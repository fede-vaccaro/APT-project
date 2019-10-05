package com.unifiprojects.app.appichetto.managers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;

public class ReceiptManagerTest {

	private Receipt receipt;

	private Map<User, Accounting> accountings;

	private User buyer;

	@Mock
	private ReceiptRepository receiptRepository;

	@InjectMocks
	private ReceiptManager receiptManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private void spyAndSetReceipt() {
		receipt = spy(new Receipt(buyer));
		receiptManager.setReceipt(receipt);
	}

	private void spyAndSetAccountings() {
		accountings = spy(new HashMap<>());
		receiptManager.setAccountings(accountings);
	}

	@Test
	public void testWhenAnItemIsAddedAndAccountingNotExistThenWillBeCreated() {
		User pippo = new User("Pippo", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo));
		spyAndSetAccountings();

		receiptManager.addItem(item);

		verify(accountings).put(pippo, new Accounting(pippo, 4.4));
	}

	@Test
	public void testWhenAnItemIsAddedAccountingOfReceiptBuyerWillNotBeCreated() {
		User pippo = new User("Pippo", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo));
		spyAndSetReceipt();
		spyAndSetAccountings();
		receipt.setBuyer(pippo);

		receiptManager.addItem(item);

		verifyZeroInteractions(accountings);
	}

	@Test
	public void testAddItemWhenAccountingExistsThenItemIsAddedToReceiptAndAccountingsAreUpdatedCorrectly() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = spy(new Accounting(pippo));
		Accounting accountingPluto = spy(new Accounting(pluto));
		spyAndSetReceipt();
		spyAndSetAccountings();
		accountings.put(pippo, accountingPippo);
		accountings.put(pluto, accountingPluto);

		receiptManager.addItem(item);

		verify(receipt).addItem(item);
		verify(accountings.get(pippo)).addAmount(2.2);
		verify(accountings.get(pluto)).addAmount(2.2);
	}

	@Test
	public void testUpdateItemUpdateItemAndUpdateAccountingsCorrectly() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item oldItem = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Item updatedItem = new Item("Sugo", 2.2, 3, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = spy(new Accounting(pippo, 2.2));
		Accounting accountingPluto = spy(new Accounting(pluto, 2.2));
		spyAndSetReceipt();
		spyAndSetAccountings();
		receipt.addItem(oldItem);
		accountings.put(pippo, accountingPippo);
		accountings.put(pluto, accountingPluto);

		receiptManager.updateItem(0, updatedItem);

		verify(receipt).updateItem(0, updatedItem);
		verify(accountings.get(pippo)).addAmount(1.1);
		verify(accountings.get(pluto)).addAmount(1.1);
	}

	@Test
	public void testWhenAnItemIsUpdatedAccountingOfReceiptBuyerWillNotBeCalled() {
		User pippo = new User("Pippo", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo));
		spyAndSetReceipt();
		spyAndSetAccountings();
		receipt.setBuyer(pippo);
		receipt.addItem(item);

		receiptManager.updateItem(0, item);

		verifyZeroInteractions(accountings);
	}

	@Test
	public void testDeleteItemDeleteItemAndUpdateAccountingsCorrectly() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item itemToDelete = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = spy(new Accounting(pippo, 2.2));
		Accounting accountingPluto = spy(new Accounting(pluto, 2.2));
		spyAndSetReceipt();
		spyAndSetAccountings();
		accountings.put(pippo, accountingPippo);
		accountings.put(pluto, accountingPluto);

		receiptManager.deleteItem(itemToDelete);

		verify(receipt).deleteItem(itemToDelete);
		verify(accountings.get(pippo)).addAmount(-2.2);
		verify(accountings.get(pluto)).addAmount(-2.2);
	}

	@Test
	public void testDeleteItemDeleteItemDoesntCallAccountingsOfBuyer() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item itemToDelete = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = spy(new Accounting(pippo, 2.2));
		Accounting accountingPluto = spy(new Accounting(pluto, 2.2));
		spyAndSetReceipt();
		spyAndSetAccountings();
		receipt.setBuyer(pippo);
		accountings.put(pippo, accountingPippo);
		accountings.put(pluto, accountingPluto);

		receiptManager.deleteItem(itemToDelete);

		verify(receipt).deleteItem(itemToDelete);
		verifyZeroInteractions(accountings.get(pippo));
		verify(accountings.get(pluto)).addAmount(-2.2);
	}

	@Test
	public void testSaveReceiptAddAccountingToReceiptAndCallTheRepository() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Accounting accountingPippo = new Accounting(pippo, 1.1);
		Accounting accountingPluto = new Accounting(pluto, 1.1);
		ArgumentCaptor<Accounting> accountingCaptor = ArgumentCaptor.forClass(Accounting.class);
		spyAndSetReceipt();
		spyAndSetAccountings();
		accountings.put(pippo, accountingPippo);
		accountings.put(pluto, accountingPluto);
		InOrder inOrder = inOrder(receiptRepository, receipt);

		receiptManager.saveReceipt();

		inOrder.verify(receipt, times(2)).addAccounting(accountingCaptor.capture());
		inOrder.verify(receiptRepository).saveReceipt(receipt);
		assertThat(accountingCaptor.getAllValues()).containsExactlyInAnyOrder(accountingPippo, accountingPluto);
	}
}