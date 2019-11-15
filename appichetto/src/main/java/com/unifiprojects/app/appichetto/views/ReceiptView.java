package com.unifiprojects.app.appichetto.views;

import java.util.GregorianCalendar;
import java.util.List;

import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;

public interface ReceiptView {
	void showCurrentItemsList(List<Item> items);

	void showCurrentUsers(List<User> users);

	void showError(String string);

	void itemAdded(Item item);
	
	void itemDeleted(Item item);

	void itemUpdated(int index, Item item);
	
	void goBack();

	void descriptionUploaded(String description);

	void dateUploaded(GregorianCalendar timestamp);
}
