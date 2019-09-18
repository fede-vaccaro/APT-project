package com.unifiprojects.app.appichetto.controls;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalPrice;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptController {

	private Receipt receipt;
	private ReceiptView receiptView;

	public ReceiptController(Receipt receipt, ReceiptView receiptView) {
		this.receipt = receipt;
		this.receiptView = receiptView;
	}

	public Item addItem(String name, String stringPrice, String quantity, List<User> users) {

		Item item = null;
		try {
			item = new Item(name, stringPrice, quantity, users);
			receipt.addItem(item);
			receiptView.showDoneMsg("Item added");
			receiptView.showCurrentItemsList(receipt.getItems());
		} catch (IllegalName e) {
			receiptView.showError("Empty name");

		} catch (IllegalPrice e) {
			receiptView.showError("Empty price");

		} catch (IllegalUsers e) {
			receiptView.showError("Empty users list");
		}
		return item;
	}

	public List<User> getUsers() {
		//TODO call user repository
		return Arrays.asList(new User());
	}

	public void deleteItem(Item item) {
		// TODO Auto-generated method stub
		
	}

	public void updateItem(String name, String stringPrice, String quantity, List<User> users, int index ) {
		// TODO Auto-generated method stub
		
	}

}
