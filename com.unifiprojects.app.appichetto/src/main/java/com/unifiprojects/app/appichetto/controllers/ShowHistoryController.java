package com.unifiprojects.app.appichetto.controllers;

import java.util.Comparator;
import java.util.List;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.services.AppIchettoService;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class ShowHistoryController {

	private ReceiptRepository receiptRepository;
	private ShowHistoryView showHistoryView;
	private TransactionHandler transaction;
	private AppIchettoService updateReceiptService;

	public void setTransaction(TransactionHandler transaction) {
		this.transaction = transaction;
	}

	private User loggedUser;

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public ShowHistoryController(ReceiptRepository receiptRepository, ShowHistoryView showHistoryView) {
		this.receiptRepository = receiptRepository;
		this.showHistoryView = showHistoryView;
	}

	public void showHistory() {
		List<Receipt> boughtReceipts = receiptRepository.getAllReceiptsBoughtBy(loggedUser);

		if (boughtReceipts.isEmpty()) {
			showHistoryView.showErrorMsg("You have no receipts in the history.");
		} else {
			Comparator<Receipt> dateComparator = (Receipt r1, Receipt r2) -> r1.getTimestamp()
					.compareTo(r2.getTimestamp());
			boughtReceipts.sort(dateComparator.reversed());
		}
		showHistoryView.showShoppingHistory(boughtReceipts);
	}

	public void removeReceipt(Receipt r) {
		try {
			transaction.doInTransaction(() -> receiptRepository.removeReceipt(r));
			showHistoryView.showErrorMsg(String.format("Receipt (from %s) deleted.", r.getTimestamp().getTime()));
		} catch (UncommittableTransactionException ex) {
			showHistoryView.showErrorMsg("Something went wrong with the database.");
		}
		this.showHistory();
	}
	
	public void setUpdateReceiptService(AppIchettoService updateReceiptService) {
		this.updateReceiptService = updateReceiptService;
	}

	public void startUpdateReceiptService(Receipt selectedReceipt) {
		updateReceiptService.execute(selectedReceipt);		
	}

}
