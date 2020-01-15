package com.unifiprojects.app.appichetto.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Version;

import com.google.inject.Inject;

@Entity
public class Receipt {

	@Id
	@GeneratedValue
	private Long id;
	private String description;
	private GregorianCalendar timestamp;

	@Version
	private int version;

	@ManyToOne
	private User buyer;

	private double totalPrice;

	@OrderColumn
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Item> items;

	@OrderColumn
	@OneToMany(mappedBy = "receipt", cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Accounting> accountingList;

	@Inject
	public Receipt() {
		items = new ArrayList<>();
		accountingList = new ArrayList<>();
		timestamp = new GregorianCalendar(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
				LocalDate.now().getDayOfMonth());
	}

	public Receipt(User buyer) {
		this.buyer = buyer;
		items = new ArrayList<>();
		accountingList = new ArrayList<>();
		timestamp = new GregorianCalendar(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
				LocalDate.now().getDayOfMonth());
	}

	public void removeAccounting(Accounting a) {
		if (!(accountingList instanceof ArrayList<?>))
			accountingList = new ArrayList<>(accountingList);
		this.accountingList.remove(a);
		a.setReceipt(null);
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

	public void deleteItem(Item itemToDelete) {
		items.remove(itemToDelete);
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
		this.accountingList.clear();
		this.accountingList.addAll(accountingList);
		accountingList.forEach(a -> a.setReceipt(this));
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
		if (accountingList == null && other.accountingList != null) {
			return false;
		}
		if(!accountingList.containsAll(other.accountingList))
			return false;

		if(!other.accountingList.containsAll(accountingList))
			return false;
					
		if (buyer == null) {
			if (other.buyer != null)
				return false;
		} else {
			if (!buyer.equals(other.buyer))
				return false;
		}
		if (description == null) {
			if (other.description != null)
				return false;
		} else {
			if (!description.equals(other.description))
				return false;
		}
		if (items == null) {
			if (other.items != null)
				return false;
		} else {
			if (!(items.containsAll(other.items) && other.items.containsAll(this.items)))
				return false;
		}
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else {
			if (!timestamp.getTime().equals(other.timestamp.getTime()))
				return false;
		}
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

	public Item getItem(int index) {
		return items.get(index);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Receipt [description= " + description + " items " + items + "]" + accountingList;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
