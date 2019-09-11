package com.unifiprojects.app.appichetto.models;

import java.util.Date;
import java.util.List;

public class Receipt {
	private String id;
	private String description;
	private Date timestamp;
	private User buyer;
	private double totalPrice;
	private List<Item> items;

	public String getId() {
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

}
