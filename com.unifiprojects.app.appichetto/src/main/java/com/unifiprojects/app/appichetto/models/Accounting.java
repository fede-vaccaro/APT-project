package com.unifiprojects.app.appichetto.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Accounting {

	@Override
	public String toString() {
		return "Accounting [user=" + user.getUsername() + ", amount=" + amount + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((receipt == null) ? 0 : receipt.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		Accounting other = (Accounting) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (receipt == null) {
			if (other.receipt != null)
				return false;
		} else if (!receipt.equals(other.receipt))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private User user;

	private double amount;

	@ManyToOne(fetch = FetchType.EAGER)
	private Receipt receipt;

	public Accounting() {
		this.amount = 0.0;
		this.receipt = null;
		this.user = null;
	}

	public Accounting(User user) {
		this.amount = 0.0;
		this.receipt = null;
		this.user = user;
	}

	public Accounting(User user, double amount) {
		this.user = user;
		this.amount = amount;
		this.receipt = null;
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public double getAmount() {
		return amount;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public void addAmount(double amount) {
		this.amount = Math.round(100 * (this.amount + amount))/100.0;
	}

	public boolean isPaid() {
		if (this.amount == 0.0)
			return true;
		return false;
	}

}