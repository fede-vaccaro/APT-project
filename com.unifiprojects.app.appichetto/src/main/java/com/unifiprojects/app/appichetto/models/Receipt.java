package com.unifiprojects.app.appichetto.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Receipt {
	private Long id;
	
	private String description;
	private Date timestamp;
	private User buyer;
	private double totalPrice;
	private List<Item> items;
	private List<Accounting> accountingList;

	public Receipt() {
		items = new ArrayList<Item>();

	public Long getId() {
		return id;

  }
	public List<Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public void updateItem(int index, Item item) {
		items.set(index, item);
	}

	public void deteleItem(Item itemToDelete) {
		items.remove(itemToDelete);
	}

	public int getItemsListSize() {
		return items.size();
	}

	public List<Accounting> getAccountings() {
		return accountingList;
	}

	public void addAccounting(Accounting accounting) {
		accountingList.add(accounting);
		accounting.setReceipt(this);
	}

}
