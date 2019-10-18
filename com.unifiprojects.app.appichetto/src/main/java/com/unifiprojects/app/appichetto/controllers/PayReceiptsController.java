package com.unifiprojects.app.appichetto.controllers;

import java.util.ArrayList;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.managers.PaymentManager;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.PayReceiptsView;

public class PayReceiptsController {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(PayReceiptsController.class);

	private TransactionHandler transaction;

	public void setTransactionHandler(TransactionHandler transaction) {
		this.transaction = transaction;
	}

	private PaymentManager paymentManager;
	private ReceiptRepository receiptRepository;		
	private PayReceiptsView payViewReceiptsView;

	public PayReceiptsController(PaymentManager paymentManager, ReceiptRepository receiptRepository,
			PayReceiptsView payViewReceiptsView, TransactionHandler transaction) {
		this.paymentManager = paymentManager;
		this.receiptRepository = receiptRepository;
		this.payViewReceiptsView = payViewReceiptsView;
		this.transaction = transaction;
	}

	public void showUnpaidReceipts(User loggedUser) {
		ArrayList<Receipt> unpaidReceipts = new ArrayList<>(receiptRepository.getAllUnpaidReceiptsOf(loggedUser));
		Comparator<Receipt> dateComparator = (Receipt r1, Receipt r2) -> r1.getTimestamp().compareTo(r2.getTimestamp());
		unpaidReceipts.sort(dateComparator.reversed());
		payViewReceiptsView.showReceipts(unpaidReceipts);
	}

	public void payAmount(double enteredAmount, User loggedUser, User buyerUser) {
		try {
			transaction.doInTransaction(() -> {
				paymentManager.makePayment(enteredAmount, loggedUser, buyerUser);
			});
		} catch (UncommittableTransactionException e) {
			payViewReceiptsView.showErrorMsg("Something went wrong while committing the payment.");
		} catch (IllegalArgumentException e) {
			payViewReceiptsView.showErrorMsg(e.getMessage());
		}
		showUnpaidReceipts(loggedUser);

	}

}
