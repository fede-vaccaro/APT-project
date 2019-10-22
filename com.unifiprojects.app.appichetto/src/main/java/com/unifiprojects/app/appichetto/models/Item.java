package com.unifiprojects.app.appichetto.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

@Entity
public class Item {

	public Item() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Item [name=" + name + ", quantity=" + quantity + String.format(", price=%.2f", price) + ", owners="
				+ owners + "]";
	}

	public Long getId() {
		return id;
	}

	@Id
	@GeneratedValue
	private Long id;

	private String name;
	private Integer quantity;
	private Double price;

	@OrderColumn
	@ManyToMany
	private List<User> owners;

	public Item(String name, Double price, Integer quantity, List<User> users) {
		this.name = name;
		this.price = price;
		this.owners = users;
		this.quantity = quantity;
	}

	public Item(String name, double price, List<User> users) {
		this.price = price;
		this.name = name;
		this.price = price;
		this.owners = users;
		this.quantity = 1;
	}

	/*
	 * @Override public String toString() { return this.name + " x" + this.quantity;
	 * }
	 */
	
	public void removeOwner(User owner) {
		if(!(owners instanceof ArrayList<?>))
			this.owners = new ArrayList<User>(owners);
		this.owners.remove(owner);

	}

	public Integer getQuantity() {
		return quantity;
	}

	public Double getPrice() {
		return price;
	}

	public List<User> getOwners() {
		return owners;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owners == null) ? 0 : owners.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + quantity;
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
		if (owners == null) {
			if (other.owners != null)
				return false;
		} else if (!owners.containsAll(other.owners) && other.owners.containsAll(owners))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}

	public double getPricePerOwner() {
		return price * quantity / owners.size();
	}

}