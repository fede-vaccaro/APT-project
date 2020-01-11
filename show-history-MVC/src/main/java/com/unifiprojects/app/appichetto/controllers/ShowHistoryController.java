package com.unifiprojects.app.appichetto.controllers;

import java.util.Comparator;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.swingviews.ReceiptSwingView;
import com.unifiprojects.app.appichetto.swingviews.utils.LinkedSwingView;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class ShowHistoryController extends UserController {

	private ReceiptRepository receiptRepository;
	private ShowHistoryView showHistoryView;
	private TransactionHandler transaction;
	private ReceiptSwingView receiptView;

	@Inject
	public ShowHistoryController(ReceiptRepository receiptRepository, @Assisted ShowHistoryView showHistoryView,
			TransactionHandler transaction, ReceiptSwingView receiptView) {
		this.receiptRepository = receiptRepository;
		this.showHistoryView = showHistoryView;
		this.transaction = transaction;
		this.receiptView = receiptView;
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

	public void setTransaction(TransactionHandler transaction) {
		this.transaction = transaction;
	}

	//TODO
	public void updateReceipt(Receipt selected) {
		receiptView.getController().setLoggedUser(loggedUser);
		((ReceiptController)receiptView.getController()).uploadReceipt(selected);
		receiptView.setLinkedSwingView((LinkedSwingView) showHistoryView);
		receiptView.show();
	}

	@Override
	public void update() {
		showHistory();
	}
}
