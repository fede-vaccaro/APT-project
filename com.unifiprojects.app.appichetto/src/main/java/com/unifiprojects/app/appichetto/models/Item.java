package com.unifiprojects.app.appichetto.models;

import java.util.List;

public class Item {
	private Long id;
	private String name;
	private Integer quantity;
	private Double price;
	private List<User> users;

	public Item(String name, Double price, Integer quantity, List<User> users) {

		this.price = price;
		this.quantity = quantity;
		this.name = name;
		this.users = users;

	}

	public Item(String name, String price, String quantity, List<User> users) {

		this.price = Double.valueOf(price);
		this.quantity = Integer.valueOf(quantity);
		this.name = name;
		this.users = users;

	}

	public Item() {
	}

	public String toString() {
		return this.name + " x" + this.quantity;
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

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		if (users == null) {
			if (other.users != null)
				return false;
		} else if (!users.equals(other.users))
			return false;
		return true;
	}


}
