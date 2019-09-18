package com.unifiprojects.app.appichetto.models;

public class Accounting {
	private Long id;

	private User user;
	private double amount;
	boolean paid;
	
	public Accounting(){
		this.paid = false;
		this.amount = 0.0;
		this.receipt = null;
	}
	
	public Accounting(User user, double amount) {
		this.user = user;
		this.amount = amount;
		this.paid = false;
		this.receipt = null;
	}
	
	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public double getAmount() {
		return amount;
	}

	public boolean isPaid() {
		return paid;
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

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	private Receipt receipt;
	
	
}
