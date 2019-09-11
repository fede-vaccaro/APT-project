package com.unifiprojects.app.appichetto.views;

import java.util.List;

import com.unifiprojects.app.appichetto.models.Item;

public interface ReceiptView {
	public void showCurrentItemList(List<Item> items);

	public void showError();

	public void showDoneMsg();

	public void showSelectedItem(Item item);

}
