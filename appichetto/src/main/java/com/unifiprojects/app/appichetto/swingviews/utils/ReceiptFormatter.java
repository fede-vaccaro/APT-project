package com.unifiprojects.app.appichetto.swingviews.utils;

import com.unifiprojects.app.appichetto.models.Receipt;

public class ReceiptFormatter {

	public static String format(Receipt r) {
		String description = "";
		if (r.getDescription() != null) {
			description = " - " + r.getDescription();
		}
		return r.getTimestamp().getTime().toString() + description;

	}

}
