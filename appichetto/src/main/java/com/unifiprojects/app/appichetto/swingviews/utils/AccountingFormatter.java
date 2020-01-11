package com.unifiprojects.app.appichetto.swingviews.utils;

import com.unifiprojects.app.appichetto.models.Accounting;

public class AccountingFormatter {
	private AccountingFormatter() {
		
	};
	
	public static String format(Accounting a) {
		return a.getUser().getUsername() + ": " + String.format("%.2f", a.getAmount()) + "; paid: " + a.isPaid();
	}
}
