package com.unifiprojects.app.appichetto.models;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Receipt {
	private Long id;
	
	private String description;
	private GregorianCalendar timestamp;
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

	public GregorianCalendar getTimestamp() {
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

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<Accounting> getAccountings() {
		return accountingList;
	}

	public void setTimestamp(GregorianCalendar timestamp) {
		this.timestamp = timestamp;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setAccountingList(List<Accounting> accountingList) {
		this.accountingList = accountingList;
	}

	public void addAccounting(Accounting accounting) {
		accountingList.add(accounting);
		accounting.setReceipt(this);
	}

}
