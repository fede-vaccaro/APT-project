package com.unifiprojects.app.appichetto.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.swingViews.PayViewReceiptsView;
import com.unifiprojects.app.appichetto.transactionHandlers.TransactionHandler;

public class PayViewReceiptsController {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(PayViewReceiptsController.class);

	private TransactionHandler transaction;

	public void setTransactionHandler(TransactionHandler transaction) {
		this.transaction = transaction;
	}

	private ReceiptRepository receiptRepository;
	private AccountingRepository accountingRepository;
	private PayViewReceiptsView payViewReceiptsView;
	private ArrayList<Receipt> unpaidReceipts;

	public PayViewReceiptsController(ReceiptRepository receiptRepository, AccountingRepository accountingRepository,
			PayViewReceiptsView payViewReceiptsView) {
		this.receiptRepository = receiptRepository;
		this.accountingRepository = accountingRepository;
		this.payViewReceiptsView = payViewReceiptsView;
	}

	public void showUnpaidReceiptsOfLoggedUser(User loggedUser) {
		unpaidReceipts = new ArrayList<Receipt>(receiptRepository.getAllUnpaidReceiptsOf(loggedUser));
		Comparator<Receipt> dateComparator = (Receipt r1, Receipt r2) -> r1.getTimestamp().compareTo(r2.getTimestamp());
		unpaidReceipts.sort(dateComparator.reversed());
		payViewReceiptsView.showReceipts(unpaidReceipts);
		Optional<Receipt> firstReceipt = unpaidReceipts.stream().findFirst();
		if (firstReceipt.isPresent())
			payViewReceiptsView.showItems(firstReceipt.get().getItems());
	}

	public void payAmount(double enteredAmount, User loggedUser, User buyerUser) {
		if (enteredAmount > 0.0) {
			List<Accounting> accountingsBetweenLoggedAndBuyer = getAccountingsBetweenLoggedAndBuyer(loggedUser,
					buyerUser);

			Comparator<Accounting> dateReceiptComparator = (Accounting a1, Accounting a2) -> a1.getReceipt()
					.getTimestamp().compareTo(a2.getReceipt().getTimestamp());

			accountingsBetweenLoggedAndBuyer.sort(dateReceiptComparator);

			Double totalAmountToPay = accountingsBetweenLoggedAndBuyer.stream().mapToDouble(a -> a.getAmount()).sum();

			try {
				transaction.doInTransaction(() -> {
					Double remainingAmount = new Double(enteredAmount);

					if (totalAmountToPay < remainingAmount) {
						payViewReceiptsView.showErrorMsg("Entered amount more than should be payed.");
						return;
					}

					for (Accounting accounting : accountingsBetweenLoggedAndBuyer) {
						Double accountingAmount = new Double(accounting.getAmount());
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
		} else {
			payViewReceiptsView.showErrorMsg("Amount payed should be more than zero.");
		}
	}

	private List<Accounting> getAccountingsBetweenLoggedAndBuyer(User loggedUser, User buyerUser) {
		List<Accounting> accountings = accountingRepository.getAccountingsOf(loggedUser);

		List<Accounting> accountingsBetweenLoggedAndBuyer = accountings.stream()
				.filter(a -> a.getReceipt().getBuyer().equals(buyerUser)).collect(Collectors.toList());
		return accountingsBetweenLoggedAndBuyer;
	}
}
