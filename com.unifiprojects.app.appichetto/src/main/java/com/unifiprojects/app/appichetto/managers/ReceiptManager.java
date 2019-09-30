package com.unifiprojects.app.appichetto.managers;

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

	public ReceiptManager(Receipt receipt, ReceiptRepository receiptRepository, Map<User, Accounting> accountings) {
		this.receipt = receipt;
		this.receiptRepository = receiptRepository;
		this.accountings = accountings;
	}

	public void addItem(Item item) {
		receipt.addItem(item);
		double pricePerOwner = item.getPrice() * item.getQuantity() / item.getOwners().size();

		item.getOwners().stream().filter(user -> !user.equals(receipt.getBuyer())).forEach(user -> {
			if (accountings.containsKey(user))
				accountings.get(user).addAmount(pricePerOwner);
			else
				accountings.put(user, new Accounting(user, pricePerOwner));
		});
	}

	public void updateItem(int index, Item item) {
		receipt.updateItem(index, item);
		double oldPrice = receipt.getItem(index).getPricePerOwner();
		double priceGap = item.getPricePerOwner() - oldPrice;

		item.getOwners().stream().forEach(user -> accountings.get(user).addAmount(priceGap));

	}

	public void deleteItem(Item itemToDelete) {
		receipt.deleteItem(itemToDelete);
		double price = -itemToDelete.getPricePerOwner();

		itemToDelete.getOwners().stream().forEach(user -> accountings.get(user).addAmount(price));
	}

	public void saveReceipt(){
		accountings.values().forEach(accounting -> receipt.addAccounting(accounting));
		receiptRepository.saveReceipt(receipt);
	}

	public int getItemsListSize() {
		return receipt.getItemsListSize();
	}

}
