package com.unifiprojects.app.appichetto.controllers;

import java.util.Arrays;
import java.util.List;

import com.unifiprojects.app.appichetto.exceptions.IllegalIndex;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptController {

	private ReceiptManager receiptManager;
	private ReceiptView receiptView;
	private TransactionHandler transactionHandler;

	public ReceiptController(ReceiptManager receiptManager, ReceiptView receiptView) {
		this.receiptManager = receiptManager;
		this.receiptView = receiptView;
	}

	public void addItem(Item item) {
		receiptManager.addItem(item);
		receiptView.itemAdded(item);
	}

	public void updateItem(Item item, int index) {
		if (index >= receiptManager.getItemsListSize())
			throw new IllegalIndex("Index not in list");
		else {
			receiptManager.updateItem(index, item);
			receiptView.itemUpdated(index, item);
		}
	}

	public List<User> getUsers() {
		// TODO call user repository
		return Arrays.asList(new User());
	}

	public void deleteItem(Item item) {
		receiptManager.deleteItem(item);
		receiptView.itemDeleted(item);
	}

	public void saveReceipt() {
		try {
			transactionHandler.doInTransaction(() -> receiptManager.saveReceipt());
			receiptView.goToHome();
		} catch (UncommittableTransactionException e) {
			receiptView.showError("Something went wrong while saving receipt.");
		}
	}

}
