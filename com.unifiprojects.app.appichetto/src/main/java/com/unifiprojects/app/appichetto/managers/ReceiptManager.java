package com.unifiprojects.app.appichetto.managers;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;

public class ReceiptManager {
	private Receipt receipt;
	private ReceiptRepository receiptRepository;
	private Map<User, Accounting> accountings;
	private static final Logger LOGGER = LogManager.getLogger(ReceiptManager.class);

	public ReceiptManager(User buyer, ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
		this.receipt = new Receipt(buyer);
		this.accountings = new HashMap<>();
	}

	public ReceiptManager(ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
		this.accountings = new HashMap<>();
	}

	public void addItem(Item item) {
		receipt.addItem(item);
		double pricePerOwner = Math.round(100 * item.getPrice() * item.getQuantity() / item.getOwners().size()) / 100.0;

		item.getOwners().stream().filter(user -> !user.equals(receipt.getBuyer())).forEach(user -> {
			if (accountings.containsKey(user))
				accountings.get(user).addAmount(pricePerOwner);
			else
				accountings.put(user, new Accounting(user, pricePerOwner));
		});
		LOGGER.debug("{} ADDED BY RECEIPT MANAGER", item);
	}

	public void updateItem(int index, Item item) {
		double oldPrice = receipt.getItem(index).getPricePerOwner();
		double priceGap = item.getPricePerOwner() - oldPrice;
		receipt.updateItem(index, item);
		item.getOwners().stream().filter(user -> !user.equals(receipt.getBuyer()))
				.forEach(user -> accountings.get(user).addAmount(priceGap));
		LOGGER.debug("{} UPDATED BY RECEIPT MANAGER", item);
	}

	public void deleteItem(Item itemToDelete) {
		receipt.deleteItem(itemToDelete);
		Double price = -itemToDelete.getPricePerOwner();
		itemToDelete.getOwners().stream().filter(user -> !user.equals(receipt.getBuyer()))
				.forEach(user -> accountings.get(user).addAmount(price));
		LOGGER.debug("{} DELETED BY RECEIPT MANAGER", itemToDelete);
	}

	public Long saveReceipt() {
		accountings.values().forEach(accounting -> receipt.addAccounting(accounting));
		receiptRepository.saveReceipt(receipt);
		LOGGER.debug("{} SAVED BY RECEIPT MANAGER", receipt);
		return receipt.getId();
	}

	public int getItemsListSize() {
		return receipt.getItemsListSize();
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void uploadReceipt(Receipt receipt) {
		this.receipt = receipt;
		receipt.getAccountings().stream().forEach(accounting -> accountings.put(accounting.getUser(), accounting));
	}

	void setAccountings(Map<User, Accounting> accountings) {
		this.accountings = accountings;
	}

	void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public String getDescription() {
		return receipt.getDescription();
	}

	public List<Item> getItems() {
		return receipt.getItems();
	}

	public GregorianCalendar getTimestamp() {
		return receipt.getTimestamp();
	}

}
