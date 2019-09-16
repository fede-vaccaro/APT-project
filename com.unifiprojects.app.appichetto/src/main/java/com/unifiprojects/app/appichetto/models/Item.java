package com.unifiprojects.app.appichetto.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalPrice;
import com.unifiprojects.app.appichetto.exceptions.IllegalQuantity;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;

public class Item {
	private Long id;
	private String name;
	private Integer quantity;
	private Double price;
	private List<User> users;

	public Item(String name, String stringPrice, String stringQuantity, List<User> users) {

		Double price = 0.0;
		Integer quantity = 0;

		if (Objects.isNull(name) || name.isEmpty())
			throw new IllegalName("Name is empty");

		try {
			price = Double.parseDouble(stringPrice);
		} catch (NumberFormatException e) {
			throw new IllegalPrice("Price is not double");
		} catch (NullPointerException e) {
			throw new IllegalPrice("Price is empty");
		}

		if (price == 0.0)
			throw new IllegalPrice("Price is zero");

		try {
			quantity = Integer.parseInt(stringQuantity);
		} catch (NullPointerException e) {
			throw new IllegalQuantity("Quantity is empty");

		} catch (NumberFormatException e) {
			throw new IllegalQuantity("Quantity is not int");
		}
		
		if (quantity == 0.0)
			throw new IllegalQuantity("Quantity is zero");

		if (Objects.isNull(users) || users.isEmpty())
			throw new IllegalUsers("Users list is empty");

		this.name = name;
		this.price = price;
		this.users = users;
		this.quantity = quantity;

	}
	

	public String toString() {
		return this.name+" x"+this.quantity;
	}

	public String getName() {
		return name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Double getPrice() {
		return price;
	}

	public List<User> getUsers() {
		return users;
	}

	
}
