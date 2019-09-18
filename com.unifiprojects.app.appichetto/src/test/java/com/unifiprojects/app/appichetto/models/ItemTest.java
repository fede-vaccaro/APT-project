package com.unifiprojects.app.appichetto.modelsTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalPrice;
import com.unifiprojects.app.appichetto.exceptions.IllegalQuantity;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;

public class ItemTest {
	
	@Test
	public void testNewItemWithNullName() {
		String name = null;
		String price = null;
		String quantity = null;
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Name is null");
		}catch(IllegalName e) {
			assertEquals(e.getMessage(), "Name is empty");
		}

	}

	@Test
	public void testNewItemWithEmptyName() {
		String name = "";
		String price = null;
		String quantity = null;
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Name is empty");
		}catch(IllegalName e) {
			assertEquals(e.getMessage(), "Name is empty");
		}
	}

	@Test
	public void testNewItemWithNullPrice() {
		String name = "Item";
		String price = null;
		String quantity = null;
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Price is null");
		}catch(IllegalPrice e) {
			assertEquals(e.getMessage(), "Price is empty");
		}
	}

	@Test
	public void testNewItemWithNotDoublePrice() {
		String name = "Item";
		String price = "aaa";
		String quantity = null;
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Price is not double");
		}catch(IllegalPrice e) {
			assertEquals(e.getMessage(), "Price is not double");
		}
	}
	
	@Test
	public void testNewItemWithZeroPrice() {
		String name = "Item";
		String price = "0";
		String quantity = null;
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Price is zero");
		}catch(IllegalPrice e) {
			assertEquals(e.getMessage(), "Price is zero");
		}
	}

	@Test
	public void testNewItemWithNullQuantity() {
		String name = "Item";
		String price = "1";
		String quantity = null;
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Quantity is null");
		}catch(IllegalQuantity e) {
			assertEquals(e.getMessage(), "Quantity is not int");
		}
	}
	
	@Test
	public void testNewItemWithNotIntegerQuantity() {
		String name = "Item";
		String price = "1";
		String quantity = "aaa"; 
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Quantity is not int");
		}catch(IllegalQuantity e) {
			assertEquals(e.getMessage(), "Quantity is not int");
		}
	}

	@Test
	public void testNewItemWithZeroQuantity() {
		String name = "Item";
		String price = "1";
		String quantity = "0"; 
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Quantity is zero");
		}catch(IllegalQuantity e) {
			assertEquals(e.getMessage(), "Quantity is zero");
		}
	}

	@Test
	public void testNewItemWithNullUser() {
		String name = "Item";
		String price = "2";
		String quantity = "1";
		List<User> users = null;
		
		try {
			new Item( name, price, quantity, users);
			fail("Users is null");
		}catch(IllegalUsers e) {
			assertEquals(e.getMessage(), "Users list is empty");
		}
	}

	@Test
	public void testNewItemWithEmptyUser() {
		String name = "Item";
		String price = "2";
		String quantity = "1";
		List<User> users = new ArrayList<User>();
		
		try {
			new Item( name, price, quantity, users);
			fail("Users is null");
		}catch(IllegalUsers e) {
			assertEquals(e.getMessage(), "Users list is empty");
		}
	}

	@Test
	public void testNewItem() {
		String name = "Item";
		String price = "2";
		String quantity = "1";
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		
		Item item =	new Item( name, price, quantity, users);

	}

}
