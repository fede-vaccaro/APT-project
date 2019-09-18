package com.unifiprojects.app.appichetto.controllerTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import com.unifiprojects.app.appichetto.controls.ReceiptController;
import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptControllerTest {

	@Mock
	private Receipt receipt;

	@Mock
	private ReceiptView receiptView;

	@InjectMocks
	private ReceiptController receiptController;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAddItemWithNullName() {
		String name = null;
		String price = null;
		String quantity = null;
		List<User> users = null;

		receiptController.addItem(name, price, quantity, users);
		verify(receiptView).showError("Empty name");
	}

	@Test
	public void testAddItemWithEmptyName() {
		String name = "";
		String price = null;
		String quantity = null;
		List<User> users = null;

		receiptController.addItem(name, price, quantity, users);

		verify(receiptView).showError("Empty name");
	}

	@Test
	public void testAddItemWithNullPrice() {
		String name = "Item";
		String price = null;
		String quantity = null;
		List<User> users = null;

		receiptController.addItem(name, price, quantity, users);

		verify(receiptView).showError("Empty price");
	}

	@Test
	public void testAddItemWithZeroPrice() {
		String name = "Item";
		String price = "0";
		String quantity = null;
		List<User> users = null;

		receiptController.addItem(name, price, quantity, users);

		verify(receiptView).showError("Empty price");
	}

	@Test
	public void testNewItemWithNullUser() {
		String name = "Item";
		String price = "2";
		String quantity = "2";
		List<User> users = null;

		receiptController.addItem(name, price, quantity, users);
		verify(receiptView).showError("Empty users list");
	}

	@Test
	public void testNewItemWithEmptyUser() {
		String name = "Item";
		String price = "2";
		String quantity = "2";
		List<User> users = new ArrayList<User>();

		receiptController.addItem(name, price, quantity, users);
		verify(receiptView).showError("Empty users list");
	}
	

	@Test
	public void testNewItem() {
		String name = "Item";
		String price = "2";
		String quantity = "2";
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		
		Item item = receiptController.addItem( name, price, quantity, users);
		
		verify(receipt).addItem(item);
		verify(receiptView).showDoneMsg("Item added");
		verify(receiptView).showCurrentItemsList(receipt.getItems());;
	}

}
