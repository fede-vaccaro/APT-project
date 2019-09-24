package com.unifiprojects.app.appichetto.managers;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.Mockito.inOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.assertj.core.internal.Lists;
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
	
	@Mock
	private Receipt receipt;

	@Mock
	private ReceiptRepository receiptRepository;
	
	@Mock
	private Map<User, Accounting> accountings;
	
	@InjectMocks
	private ReceiptManager receiptManager;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testWhenAnItemIsAddedAndAccountingNotExistThenWillBeCreated() {
		User pippo = new User("Pippo", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo));
		
		when(accountings.get(pippo)).thenReturn(null);

		receiptManager.addItem(item);
		
		verify(accountings).put(pippo, new Accounting(pippo, 4.4));		

	}
	
	@Test
	public void testAddItemAddItemToReceiptAndUpdateAccountingsCorrectly() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = spy(new Accounting(pippo));
		Accounting accountingPluto = spy(new Accounting(pluto));
		ArgumentCaptor<Double>  priceOfPippoCaptor = ArgumentCaptor.forClass(Double.class);
		ArgumentCaptor<Double>  priceOfPlutoCaptor = ArgumentCaptor.forClass(Double.class);
		
		when(accountings.get(pippo)).thenReturn(accountingPippo);
		when(accountings.containsKey(pippo)).thenReturn(true);
		when(accountings.get(pluto)).thenReturn(accountingPluto);
		when(accountings.containsKey(pluto)).thenReturn(true);

		receiptManager.addItem(item);
		
		verify(receipt).addItem(item);		
		verify(accountings.get(pippo)).addAmount(priceOfPippoCaptor.capture());
		verify(accountings.get(pluto)).addAmount(priceOfPlutoCaptor.capture());
		assert(priceOfPippoCaptor.getValue()).equals(2.2);
		assert(priceOfPlutoCaptor.getValue()).equals(2.2);
	}
	
	@Test
	public void testUpdateItemUpdateItemAndUpdateAccountingsCorrectly() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item oldItem = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Item updatedItem = new Item("Sugo", 2.2, 3, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = spy(new Accounting(pippo, 2.2));
		Accounting accountingPluto = spy(new Accounting(pluto, 2.2));
		ArgumentCaptor<Double>  priceOfPippoCaptor = ArgumentCaptor.forClass(Double.class);
		ArgumentCaptor<Double>  priceOfPlutoCaptor = ArgumentCaptor.forClass(Double.class);
		
		when(accountings.get(pippo)).thenReturn(accountingPippo);
		when(accountings.containsKey(pippo)).thenReturn(true);
		when(accountings.get(pluto)).thenReturn(accountingPluto);
		when(accountings.containsKey(pluto)).thenReturn(true);
		when(receipt.getItem(0)).thenReturn(oldItem);
		
		receiptManager.updateItem(0, updatedItem);
		
		verify(receipt).updateItem(0, updatedItem);		
		verify(accountings.get(pippo)).addAmount(priceOfPippoCaptor.capture());
		verify(accountings.get(pluto)).addAmount(priceOfPlutoCaptor.capture());
		assert(priceOfPippoCaptor.getValue()).equals(1.1);
		assert(priceOfPlutoCaptor.getValue()).equals(1.1);
	}
	
	@Test
	public void testDeleteItemDeleteItemAndUpdateAccountingsCorrectly() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item itemToDelete = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = spy(new Accounting(pippo, 2.2));
		Accounting accountingPluto = spy(new Accounting(pluto, 2.2));
		ArgumentCaptor<Double>  priceOfPippoCaptor = ArgumentCaptor.forClass(Double.class);
		ArgumentCaptor<Double>  priceOfPlutoCaptor = ArgumentCaptor.forClass(Double.class);
		
		when(accountings.get(pippo)).thenReturn(accountingPippo);
		when(accountings.containsKey(pippo)).thenReturn(true);
		when(accountings.get(pluto)).thenReturn(accountingPluto);
		when(accountings.containsKey(pluto)).thenReturn(true);
		
		receiptManager.deleteItem(itemToDelete);
		
		verify(receipt).deleteItem(itemToDelete);		
		verify(accountings.get(pippo)).addAmount(priceOfPippoCaptor.capture());
		verify(accountings.get(pluto)).addAmount(priceOfPlutoCaptor.capture());
		assert(priceOfPippoCaptor.getValue()).equals(-2.2);
		assert(priceOfPlutoCaptor.getValue()).equals(-2.2);
	}
	
	@Test
	public void testSaveReceiptAddAccountingToReceiptAndCallTheRepository() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item itemToDelete = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Accounting accountingPippo = new Accounting(pippo, 1.1);
		Accounting accountingPluto = new Accounting(pluto, 1.1);
		
		InOrder inOrder = inOrder(receiptRepository, receipt);
		
		when(accountings.values()).thenReturn(Arrays.asList(accountingPippo, accountingPluto));
		
		receiptManager.saveReceipt();
		
		inOrder.verify(receipt).addAccounting(accountingPippo);
		inOrder.verify(receipt).addAccounting(accountingPluto);
		inOrder.verify(receiptRepository).saveReceipt(receipt);
	
	}
	

}
