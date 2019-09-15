package com.unifiprojects.app.appichetto.models;

import java.util.List;
import java.util.Objects;

import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalPrice;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;

public class Item {
	private Long id;
	private String name;
	private int quantity;
	private Double price;
	private List<User> users;


	public Item(String name, double price, int quantity, List<User> users) {
		
		
		if (Objects.isNull(name) || name.isEmpty())
			throw new IllegalName("Name is empty");

			
		
		if ( price == 0.0 )
			throw new IllegalPrice("Price is zero");
		
		this.price = price;

		if ( Objects.isNull(users) || users.isEmpty())
			throw new IllegalUsers("Users list is empty");
		
		this.name = name;
		this.price = price;
		this.users = users;
		this.quantity = quantity;	
		
	}
	
	public Item(String name, double price, List<User> users) {
		
		
		if (Objects.isNull(name) || name.isEmpty())
			throw new IllegalName("Name is empty");

			
		
		if ( price == 0.0 )
			throw new IllegalPrice("Price is zero");
		
		this.price = price;

		if ( Objects.isNull(users) || users.isEmpty())
			throw new IllegalUsers("Users list is empty");
		
		this.name = name;
		this.price = price;
		this.users = users;
		this.quantity = 1;	
		
	}
	
	@Override
	public String toString() {
		return "Item [name=" + name + ", quantity=" + quantity + ", price=" + price + ", users=" + users + "]";
	}

	public double getPrice() {
		return price;
	}

}
