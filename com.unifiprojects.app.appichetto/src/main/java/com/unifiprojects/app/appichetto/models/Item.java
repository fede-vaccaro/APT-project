package com.unifiprojects.app.appichetto.models;

import java.util.List;
import java.util.Objects;

import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalPrice;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;

public class Item {
	private Long id;
	private String name;
	private String description;
	private Double price;
	private List<User> users;

	public Item(String name, String stringPrice, String description, List<User> users) {
		
		Double price = 0.0;
		
		if (Objects.isNull(name) || name.isEmpty())
			throw new IllegalName("Name is empty");

		try {
			price = Double.parseDouble(stringPrice);
		}catch(NumberFormatException e) {
			throw new IllegalPrice("Price is not double");
		} catch (NullPointerException e) {
			throw new IllegalPrice("Price is empty");
		}		
		
		if ( price == 0.0 )
			throw new IllegalPrice("Price is zero");
		
		if ( Objects.isNull(users) || users.isEmpty())
			throw new IllegalUsers("Users list is empty");
		
		this.name = name;
		this.price = price;
		this.users = users;
		this.description = description;	
		
	}
	
	public Item(String name, double price, String description, List<User> users) {
		
		
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
		this.description = description;	
		
	}
	
	public double getPrice() {
		return price;
	}

}
