package com.unifiprojects.app.appichetto.modelsTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalPrice;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;

public class ItemTest {
	
	@Test
	public void testNewItemWithNullName() {
		String name = null;
		String price = null;
		String description = null;
		List<User> users = null;
		
		try {
			new Item( name, price, description, users);
			fail("Name is null");
		}catch(IllegalName e) {
			assertEquals(e.getMessage(), "Name is empty");
		}

	}

	@Test
	public void testNewItemWithEmptyName() {
		String name = "";
		String price = null;
		String description = null;
		List<User> users = null;
		
		try {
			new Item( name, price, description, users);
			fail("Name is empty");
		}catch(IllegalName e) {
			assertEquals(e.getMessage(), "Name is empty");
		}
	}

	@Test
	public void testNewItemWithNullPrice() {
		String name = "Item";
		String price = null;
		String description = null;
		List<User> users = null;
		
		try {
			new Item( name, price, description, users);
			fail("Price is null");
		}catch(IllegalPrice e) {
			assertEquals(e.getMessage(), "Price is empty");
		}
	}

	@Test
	public void testNewItemWithStringPrice() {
		String name = "Item";
		String price = "aaa";
		String description = null;
		List<User> users = null;
		
		try {
			new Item( name, price, description, users);
			fail("Price is null");
		}catch(IllegalPrice e) {
			assertEquals(e.getMessage(), "Price is not double");
		}
	}
	
	@Test
	public void testNewItemWithZeroPrice() {
		String name = "Item";
		String price = "0";
		String description = null;
		List<User> users = null;
		
		try {
			new Item( name, price, description, users);
			fail("Price is null");
		}catch(IllegalPrice e) {
			assertEquals(e.getMessage(), "Price is zero");
		}
	}

	@Test
	public void testNewItemWithNullUser() {
		String name = "Item";
		String price = "2";
		String description = null;
		List<User> users = null;
		
		try {
			new Item( name, price, description, users);
			fail("Users is null");
		}catch(IllegalUsers e) {
			assertEquals(e.getMessage(), "Users list is empty");
		}
	}

	@Test
	public void testNewItemWithEmptyUser() {
		String name = "Item";
		String price = "2";
		String description = null;
		List<User> users = new ArrayList<User>();
		
		try {
			new Item( name, price, description, users);
			fail("Users is null");
		}catch(IllegalUsers e) {
			assertEquals(e.getMessage(), "Users list is empty");
		}
	}

	@Test
	public void testNewItem() {
		String name = "Item";
		String price = "2";
		String description = null;
		List<User> users = new ArrayList<User>(Arrays.asList(new User()));
		
		Item item =	new Item( name, price, description, users);

	}

}
