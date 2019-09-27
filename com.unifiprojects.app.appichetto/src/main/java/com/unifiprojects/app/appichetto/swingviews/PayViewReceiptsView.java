package com.unifiprojects.app.appichetto.swingviews;

import java.util.List;

import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;

public interface PayViewReceiptsView {

	void showReceipts(List<Receipt> unpaids);

	void showItems(List<Item> items);
	
	void showErrorMsg(String msg);

}