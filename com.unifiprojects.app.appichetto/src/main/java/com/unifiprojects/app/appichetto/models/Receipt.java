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
	
	
	public List<Item> getItems() {
		return items;
	}


	public void addItem(Item item) {
		items.add(item);
	}

	

}
