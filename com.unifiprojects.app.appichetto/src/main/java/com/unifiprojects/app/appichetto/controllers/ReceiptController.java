package com.unifiprojects.app.appichetto.controllers;

import java.util.Arrays;
import java.util.List;

import com.unifiprojects.app.appichetto.exceptions.IllegalIndex;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptController {

	private Receipt receipt;
	private ReceiptView receiptView;

	public ReceiptController(Receipt receipt, ReceiptView receiptView) {
		this.setReceipt(receipt);
		this.receiptView = receiptView;
	}

	void addItemToReceipt(Item item) {
		receipt.addItem(item);
	}

	public void addItem(Item item) {
		receipt.addItem(item);
		receiptView.itemAdded(item); 
	}

	public void updateItem(Item item, int index) {
		if (index >= receipt.getItemsListSize())
			throw new IllegalIndex("Index not in list");
		else {
			receipt.updateItem(index, item);
			receiptView.itemUpdated(index, item);
		}
	}

	public List<User> getUsers() {
		// TODO call user repository
		return Arrays.asList(new User());
	}

	public void deleteItem(Item item) {
		receipt.deteleItem(item);
		receiptView.itemDeleted(item);
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

}
