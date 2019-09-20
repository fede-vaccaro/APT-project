package com.unifiprojects.app.appichetto.managers;

import java.util.List;
import java.util.Map;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class ReceiptManager {
	private User receiptOwner;
	private Receipt receipt;
	private List<User> users;
	private List<Item> items;
	private Map<User, Accounting> accountings;
	private RepositoryManager repositoryManager;

	public ReceiptManager(User receiptOwner, List<User> users, Receipt receipt, RepositoryManager repositoryManager) {
		this.receiptOwner = receiptOwner;
		this.users = users;
		this.receipt = receipt;
		this.items = receipt.getItems();
		this.repositoryManager = repositoryManager;
		//this.accountings = new HashMap<>();
		
		
		//users.stream().forEach(user -> accountings.put(user, new Accounting(user, 0.0)));
	}

	public void addItem(Item item) {
		List<User> itemOwner = item.getUsers();
		double pricePerOwner = item.getPrice()/itemOwner.size();
		items.add(item);
		itemOwner.stream().forEach(owner -> accountings.get(owner).addAmount(pricePerOwner));
	}
	
}
