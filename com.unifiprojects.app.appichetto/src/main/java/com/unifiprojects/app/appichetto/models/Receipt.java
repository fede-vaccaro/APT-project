package com.unifiprojects.app.appichetto.models;

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

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public User getBuyer() {
		return buyer;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public List<Item> getItems() {
		return items;
	}

	public List<Accounting> getAccountings() {
		return accountingList;
	}

	public void addAccounting(Accounting accounting) {
		accountingList.add(accounting);
		accounting.setReceipt(this);
	}

}
