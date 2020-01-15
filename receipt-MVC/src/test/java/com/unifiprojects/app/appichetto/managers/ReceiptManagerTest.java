package com.unifiprojects.app.appichetto.managers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.exceptions.IllegalIndex;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.services.CreateDebtsService;

public class ReceiptManagerTest {

	private Receipt receipt;

	private Map<User, Accounting> accountingsMap;

	private User buyer;

	@Mock
	private CreateDebtsService createDebtsService;

	@Mock
	private ReceiptRepository receiptRepository;

	@InjectMocks
	private ReceiptManager receiptManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		receipt = spy(new Receipt(buyer));
		accountingsMap = spy(new HashMap<>());
		receiptManager.setReceipt(receipt);
		receiptManager.setAccountings(accountingsMap);
		receiptManager.setCreateDebtsService(createDebtsService);
	}

	@Test
	public void testWhenAnItemIsAddedThenItIsAddedInReceipt() {
		User pippo = new User("Pippo", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo));

		receiptManager.addItem(item);

		verify(receipt).addItem(item);
	}

	@Test
	public void testUpdateItemWithWrongIndexThrownIllegalIndexException() {
		User pippo = new User("Pippo", "psw");
		Item item = new Item("Sugo", 2.2, 2, Arrays.asList(pippo));

		try {
			receiptManager.updateItem(0, item);
			fail("Illegal index");
		} catch (IllegalIndex e) {
			assertThat(e.getMessage()).isEqualTo("Index not in list");
		}
	}

	@Test
	public void testWhenAnItemIsUpdatedThenIsUpdateInReceipt() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item oldItem = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));
		Item updatedItem = new Item("Sugo", 2.2, 3, Arrays.asList(pippo, pluto));

		receipt.addItem(oldItem);

		receiptManager.updateItem(0, updatedItem);

		verify(receipt).updateItem(0, updatedItem);
	}

	@Test
	public void testWhenItemIsDeletedThenItIsDeletedInByReceipt() {
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Item itemToDelete = new Item("Sugo", 2.2, 2, Arrays.asList(pippo, pluto));

		receiptManager.deleteItem(itemToDelete);

		verify(receipt).deleteItem(itemToDelete);
	}

	@Test
	public void testUploadReceiptInitializeAccounting() {
		User mario = new User("Mario", "psw");
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Accounting accountingPippo = new Accounting(pippo, 3.3);
		Accounting accountingPluto = new Accounting(pluto, 1.1);

		Receipt receiptToUpload = new Receipt(mario);
		receiptToUpload.addItem(new Item("Sugo", 1.1, 2, Arrays.asList(pippo, mario)));
		receiptToUpload.addItem(new Item("Pesto", 1.1, 2, Arrays.asList(pippo)));
		receiptToUpload.addItem(new Item("Pasta", 1.1, 1, Arrays.asList(pluto)));

		receiptManager.uploadReceipt(receiptToUpload);

		assertThat(accountingsMap.size()).isEqualTo(2);
		assertThat(accountingsMap.get(pluto)).isEqualTo(accountingPluto);
		assertThat(accountingsMap.get(pippo)).isEqualTo(accountingPippo);
	}

	@Test
	public void testUploadReceiptMantainDataconsistency() {
		User mario = new User("Mario", "psw");
		User pippo = new User("Pippo", "psw");
		User pluto = new User("Pluto", "psw");
		Accounting accountingPippo = new Accounting(pippo, 3.3);
		Accounting accountingPluto = new Accounting(pluto, 1.1);

		receipt.addItem(new Item("Sugo", 10., 1, Arrays.asList(pluto)));
		accountingsMap.put(pluto, new Accounting(pluto, 10.));

		Receipt receiptToUpload = new Receipt(mario);
		receiptToUpload.addItem(new Item("Sugo", 1.1, 2, Arrays.asList(pippo, mario)));
		receiptToUpload.addItem(new Item("Pesto", 1.1, 2, Arrays.asList(pippo)));
		receiptToUpload.addItem(new Item("Pasta", 1.1, 1, Arrays.asList(pluto)));

		receiptManager.uploadReceipt(receiptToUpload);

		assertThat(accountingsMap.size()).isEqualTo(2);
		assertThat(accountingsMap.get(pluto)).isEqualTo(accountingPluto);
		assertThat(accountingsMap.get(pippo)).isEqualTo(accountingPippo);
	}

	@Test
	public void testSaveReceiptSetAccountingInReceipt() {
		when(createDebtsService.computeAccountingDebtsAndRefoundReceipts(receipt, accountingsMap))
				.thenReturn(new Pair<>(new ArrayList<Accounting>(), new ArrayList<Receipt>()));

		receiptManager.saveReceipt();

		verify(receipt).setAccountingList(new ArrayList<>());
	}

	@Test
	public void testSaveReceiptCallReceiptRepositorySaveReceipt() {
		when(createDebtsService.computeAccountingDebtsAndRefoundReceipts(receipt, accountingsMap))
				.thenReturn(new Pair<>(new ArrayList<Accounting>(), new ArrayList<Receipt>()));

		Receipt savedReceipt = receiptManager.saveReceipt();

		assertThat(savedReceipt).isEqualTo(receipt);
		verify(receiptRepository).saveReceipt(receipt);
	}

	@Test
	public void testSaveReceiptCallCreateDebtsServiceWithItsGet() {
		when(createDebtsService.computeAccountingDebtsAndRefoundReceipts(receipt, accountingsMap))
				.thenReturn(new Pair<>(new ArrayList<>(), new ArrayList<>()));

		receiptManager.saveReceipt();

		verify(createDebtsService).computeAccountingDebtsAndRefoundReceipts(receipt, accountingsMap);
	}

	@Test
	public void testSaveReceiptCallReceiptRepositorySaveRefoundReceipt() {
		User mario = new User("Mario", "psw");
		User pluto = new User("Pluto", "psw");

		Receipt refoundReceipt = new Receipt(mario);
		refoundReceipt.addItem(new Item("", 1.1, 1, Arrays.asList(pluto)));

		when(createDebtsService.computeAccountingDebtsAndRefoundReceipts(receipt, accountingsMap))
				.thenReturn(new Pair<>(new ArrayList<>(), new ArrayList<>(Arrays.asList(refoundReceipt))));

		receiptManager.saveReceipt();

		verify(receiptRepository).saveReceipt(refoundReceipt);
	}

	@Test
	public void testSetBuyer() {
		User newBuyer = new User("buyer", "");

		receiptManager.setBuyer(newBuyer);

		assertThat(receipt.getBuyer()).isEqualTo(newBuyer);
	}
}