package com.unifiprojects.app.appichetto.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Receipt {

	@Id
	@GeneratedValue
	private Long id;
	
	private String description;
	private GregorianCalendar timestamp;
	
	@OneToOne
	private User buyer;
	private double totalPrice;
	
	@OneToMany
	private List<Item> items;
	
	@OneToMany
	private List<Accounting> accountingList;

	public Receipt() {
		items = new ArrayList<>();
	}

	public Long getId() {
		return id;

  }
	public List<Item> getItems() {
		return items;
	}

	public void addItem(Item item) {
		items.add(item);
}
	public GregorianCalendar getTimestamp() {
		return timestamp;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountingList == null) ? 0 : accountingList.hashCode());
		result = prime * result + ((buyer == null) ? 0 : buyer.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		long temp;
		temp = Double.doubleToLongBits(totalPrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Receipt other = (Receipt) obj;
		if (accountingList == null) {
			if (other.accountingList != null)
				return false;
		} else if (!accountingList.equals(other.accountingList))
			return false;
		if (buyer == null) {
			if (other.buyer != null)
				return false;
		} else if (!buyer.equals(other.buyer))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (Double.doubleToLongBits(totalPrice) != Double.doubleToLongBits(other.totalPrice))
			return false;
		return true;
	}

	public User getBuyer() {
		return buyer;
	}

	public double getTotalPrice() {
		return totalPrice;
	}


}
