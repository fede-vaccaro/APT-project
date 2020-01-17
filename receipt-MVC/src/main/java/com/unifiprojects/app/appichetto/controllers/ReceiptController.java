package com.unifiprojects.app.appichetto.controllers;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.unifiprojects.app.appichetto.exceptions.IllegalIndex;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptController extends UserController {

	private ReceiptManager receiptManager;
	private ReceiptView receiptView;
	private UserRepository userRepository;
	private TransactionHandler transactionHandler;
	

	@Inject
	public ReceiptController(ReceiptManager receiptManager, @Assisted ReceiptView receiptView,
			UserRepository userRepository, TransactionHandler transactionHandler) {
		this.receiptManager = receiptManager;
		this.receiptView = receiptView;
		this.userRepository = userRepository;
		this.transactionHandler = transactionHandler;
	}

	public void setTransactionHandler(TransactionHandler transactionHandler) {
		this.transactionHandler = transactionHandler;
	}

	public void addItem(Item item) {
		receiptManager.addItem(item);
		receiptView.itemAdded(item);
	}

	public void updateItem(int index, Item item) {
		try {
			receiptManager.updateItem(index, item);
			receiptView.itemUpdated(index, item);
		} catch (IllegalIndex e) {
			receiptView.showError("Item index not in list");
		}
	}

	public void deleteItem(Item item) {
		receiptManager.deleteItem(item);
		receiptView.itemDeleted(item);
	}

	public void saveReceipt() {
		try {
			transactionHandler.doInTransaction(() -> receiptManager.saveReceipt());
			receiptView.goBack();
		} catch (UncommittableTransactionException e) {
			receiptView.showError("Something went wrong while saving receipt.");
		}
		clear();
	}
	
	public void uploadReceipt(Receipt receipt) {
		receiptManager.uploadReceipt(receipt);
		receiptView.setDescriptionUploadedReceipt(receipt.getDescription());
		receiptView.showCurrentItemsList(receipt.getItems());
		receiptView.setTimestampUploadedReceipt(receipt.getTimestamp());
	}
	
	protected void clear() {
		receiptView.showCurrentItemsList(null);
	}

	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public void update() {
		receiptManager.setBuyer(loggedUser);
	}
}
