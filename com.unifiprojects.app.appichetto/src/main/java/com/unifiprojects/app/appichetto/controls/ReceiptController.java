package com.unifiprojects.app.appichetto.controls;

import java.util.List;
import java.util.Objects;

import com.unifiprojects.app.appichetto.exceptions.IllegalName;
import com.unifiprojects.app.appichetto.exceptions.IllegalPrice;
import com.unifiprojects.app.appichetto.exceptions.IllegalUsers;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class ReceiptController {

	private Receipt receipt;
	private ReceiptView receiptView;

	public ReceiptController(Receipt receipt, ReceiptView receiptView) {
		this.receipt = receipt;
		this.receiptView = receiptView;
	}

	public Item addItem(String name, String stringPrice, String description, List<User> users) {

		Item item = null;
		try {
			item = new Item(name, stringPrice, description, users);
			receipt.addItem(item);
			receiptView.showDoneMsg("Item added");
			receiptView.showCurrentItemList(receipt.getItems());
		} catch (IllegalName e) {
			receiptView.showError("Empty name");

		} catch (IllegalPrice e) {
			receiptView.showError("Empty price");

		} catch (IllegalUsers e) {
			receiptView.showError("Empty users list");
		}
		return item;
	}

}
