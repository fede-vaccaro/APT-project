package com.unifiprojects.app.appichetto.controllers;

import java.util.Comparator;
import java.util.List;

import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class ShowHistoryController {

	private ReceiptRepository receiptRepository;
	private ShowHistoryView showHistoryView;

	private User loggedUser;

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public ShowHistoryController(ReceiptRepository receiptRepository, ShowHistoryView showHistoryView) {
		this.receiptRepository = receiptRepository;
		this.showHistoryView = showHistoryView;
	}

	public void showHistory() {
		List<Receipt> boughtReceipts = receiptRepository.getAllReceiptsBoughtBy(loggedUser);

		Comparator<Receipt> dateComparator = (Receipt r1, Receipt r2) -> r1.getTimestamp().compareTo(r2.getTimestamp());
		boughtReceipts.sort(dateComparator.reversed());

		showHistoryView.showShoppingHistory(boughtReceipts);

		if (boughtReceipts == null) {
			showHistoryView.showErrorMsg("You have no receipts in the history.");
		}

	}

}
