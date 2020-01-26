package com.unifiprojects.app.appichetto.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class ReceiptGenerator {
	
	private static final String ITEM_1_NAME = "Item1";
	private static final String ITEM_2_NAME = "Item2";

	private ReceiptGenerator() {
		
	}
	
	public static Receipt generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(User participant, User buyer,
			GregorianCalendar timestamp) {

		Receipt receipt = new Receipt();

		receipt.setTimestamp(timestamp);
		receipt.setBuyer(buyer);

		// receipt setup: payerUser bought item1 and item2 but he shares them with
		// logged user...
		Item item1 = new Item(ITEM_1_NAME, 10., new ArrayList<User>( Arrays.asList(participant, buyer)));
		Item item2 = new Item(ITEM_2_NAME, 5., new ArrayList<User>( Arrays.asList(participant, buyer)));

		receipt.setItems(new ArrayList<Item>( Arrays.asList(item1, item2)));
		receipt.setTotalPrice(item1.getPrice() + item2.getPrice());

		// so now, logged user owes 7.5 credits to payer user
		Accounting debtFromLoggedToPayer = new Accounting(participant, item1.getPrice() / 2 + item2.getPrice() / 2);
		debtFromLoggedToPayer.setReceipt(receipt);
		receipt.setAccountingList(new ArrayList<Accounting>( Arrays.asList(debtFromLoggedToPayer)));
		return receipt;
	}

	public static Receipt generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(User participant, User buyer) {
		
		Receipt receipt = new Receipt();
		
		receipt.setBuyer(buyer);
		
		// receipt setup: payerUser bought item1 and item2 but he shares them with
		// logged user...
		Item item1 = new Item(ITEM_1_NAME, 10., new ArrayList<User>( Arrays.asList(participant, buyer)));
		Item item2 = new Item(ITEM_2_NAME, 5., new ArrayList<User>( Arrays.asList(participant, buyer)));
		
		receipt.setItems(new ArrayList<Item>( Arrays.asList(item1, item2)));
		receipt.setTotalPrice(item1.getPrice() + item2.getPrice());
		
		// so now, logged user owes 7.5 credits to payer user
		Accounting debtFromLoggedToPayer = new Accounting(participant, item1.getPrice() / 2 + item2.getPrice() / 2);
		debtFromLoggedToPayer.setReceipt(receipt);
		receipt.setAccountingList(new ArrayList<Accounting>( Arrays.asList(debtFromLoggedToPayer)));
		return receipt;
	}
	
	public static Receipt generateReceiptWithTwoItemsSharedByLoggedUserAndPayer(User participant, User buyer,
			GregorianCalendar timestamp, List<Item> itemList) {

		Receipt receipt = new Receipt();

		receipt.setTimestamp(timestamp);
		receipt.setBuyer(buyer);

		// receipt setup: payerUser bought item1 and item2 but he shares them with
		// logged user...
		if (itemList == null) {
			Item item1 = new Item(ITEM_1_NAME, 10., new ArrayList<User>( Arrays.asList(participant, buyer)));
			Item item2 = new Item(ITEM_2_NAME, 5., new ArrayList<User>( Arrays.asList(participant, buyer)));
			itemList = new ArrayList<>( Arrays.asList(item1, item2));
		}
		receipt.setItems(itemList);
		// assuming each item is owned by logged user and a buyer
		Double totalAmount = itemList.stream().mapToDouble(Item::getPrice).sum();
		receipt.setTotalPrice(totalAmount);
		Accounting debtFromLoggedToPayer = new Accounting(participant, totalAmount / 2.0);
		debtFromLoggedToPayer.setReceipt(receipt);
		receipt.setAccountingList(new ArrayList<Accounting>( Arrays.asList(debtFromLoggedToPayer)));
		return receipt;
	}


}
