package com.unifiprojects.app.appichetto.managers;

import java.util.HashMap;
import java.util.Map;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;

public class ReceiptManager {
	private Receipt receipt;
	private ReceiptRepository receiptRepository;
	private Map<User, Accounting> accountings;

	public ReceiptManager(User buyer, ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
		this.receipt = new Receipt(buyer);
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
	}

	public void updateItem(int index, Item item) {
		double oldPrice = receipt.getItem(index).getPricePerOwner();
		double priceGap = item.getPricePerOwner() - oldPrice;
		receipt.updateItem(index, item);
		item.getOwners().stream().filter(user -> !user.equals(receipt.getBuyer()))
				.forEach(user -> accountings.get(user).addAmount(priceGap));
	}

	public void deleteItem(Item itemToDelete) {
		receipt.deleteItem(itemToDelete);
		Double price = -itemToDelete.getPricePerOwner();

		itemToDelete.getOwners().stream().forEach(user -> accountings.get(user).addAmount(price));
	}

	public Long saveReceipt() {
		accountings.values().forEach(accounting -> receipt.addAccounting(accounting));
		receiptRepository.saveReceipt(receipt);
		return receipt.getId();
	}
	

	public int getItemsListSize() {
		return receipt.getItemsListSize();
	}
	
	public Receipt getReceipt() {
		return receipt;
	}

	void setAccountings(Map<User, Accounting> accountings) {
		this.accountings = accountings;
	}
	
	void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}
}
