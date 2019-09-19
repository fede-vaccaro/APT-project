package com.unifiprojects.app.appichetto.views;
import com.unifiprojects.app.appichetto.models.Receipt;

public class CustomToStringReceipt {
	Receipt receipt;

	public CustomToStringReceipt(Receipt r) {
		receipt = r;
	}
	
	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	@Override
	public String toString() {
		String description = "";
		if (receipt.getDescription() != null) {
			description = " - " + receipt.getDescription();
		}
		return receipt.getTimestamp().getTime().toString() + description;
	}
}
