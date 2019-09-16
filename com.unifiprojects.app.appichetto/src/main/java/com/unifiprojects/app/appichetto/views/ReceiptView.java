package com.unifiprojects.app.appichetto.views;

import java.util.List;

import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.User;

public interface ReceiptView {
	public void showCurrentItemsList(List<Item> items);

	public void showCurrentUsers(List<User> users);

	public void showError(String string);

	public void showDoneMsg(String string);

	public void showSelectedItem(Item item);

	public void itemAdded(Item item);
	
	public void itemDeleted(Item item);

	public void itemUpdated(int index, Item item);

}
