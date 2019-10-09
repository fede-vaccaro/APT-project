package com.unifiprojects.app.appichetto.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
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

	private ReceiptRepository receiptRepository;
	private AccountingRepository accountingRepository;
	private PayReceiptsView payViewReceiptsView;

	public PayReceiptsController(ReceiptRepository receiptRepository, AccountingRepository accountingRepository,
			PayReceiptsView payViewReceiptsView) {
		this.receiptRepository = receiptRepository;
		this.accountingRepository = accountingRepository;
		this.payViewReceiptsView = payViewReceiptsView;
	}

	public void showUnpaidReceiptsOfLoggedUser(User loggedUser) {
		ArrayList<Receipt> unpaidReceipts = new ArrayList<>(receiptRepository.getAllUnpaidReceiptsOf(loggedUser));
		Comparator<Receipt> dateComparator = (Receipt r1, Receipt r2) -> r1.getTimestamp().compareTo(r2.getTimestamp());
		unpaidReceipts.sort(dateComparator.reversed());
		payViewReceiptsView.showReceipts(unpaidReceipts);
	}

	public void payAmount(double enteredAmount, User loggedUser, User buyerUser) {
		if (enteredAmount <= 0.0) {
			payViewReceiptsView.showErrorMsg("Amount payed should be more than zero.");
			return;
		}

		List<Accounting> accountingsBetweenLoggedAndBuyer = getAccountingsBetweenLoggedAndBuyer(loggedUser, buyerUser);

		Comparator<Accounting> dateReceiptComparator = (Accounting a1, Accounting a2) -> a1.getReceipt().getTimestamp()
				.compareTo(a2.getReceipt().getTimestamp());

		accountingsBetweenLoggedAndBuyer.sort(dateReceiptComparator);

		Double totalAmountToPay = accountingsBetweenLoggedAndBuyer.stream().mapToDouble(Accounting::getAmount).sum();

		try {
			transaction.doInTransaction(() -> {
				Double remainingAmount = enteredAmount;

				if (Precision.round(totalAmountToPay,2) < Precision.round(remainingAmount,2)) {
					payViewReceiptsView.showErrorMsg("Entered amount more than should be payed.");
					return;
				}

				for (Accounting accounting : accountingsBetweenLoggedAndBuyer) {
					Double accountingAmount = accounting.getAmount();
					if (remainingAmount > 0.0) {
						if (remainingAmount >= accountingAmount) {
							accounting.setPaid(true);
							remainingAmount -= accountingAmount;
						} else {
							accountingAmount -= remainingAmount;
							accounting.setAmount(accountingAmount);
							remainingAmount = 0.0;
						}
						accountingRepository.saveAccounting(accounting);
					}
				}
			});
		} catch (UncommittableTransactionException e) {
			payViewReceiptsView.showErrorMsg("Something went wrong while committing the payment.");
		}
		showUnpaidReceiptsOfLoggedUser(loggedUser);

	}

	private List<Accounting> getAccountingsBetweenLoggedAndBuyer(User loggedUser, User buyerUser) {
		List<Accounting> accountings = accountingRepository.getAccountingsOf(loggedUser);

		return accountings.stream().filter(a -> a.getReceipt().getBuyer().equals(buyerUser))
				.collect(Collectors.toList());
	}
}
