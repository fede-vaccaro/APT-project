package com.unifiprojects.app.appichetto.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.models.*;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.views.PayViewReceiptsView;

public class PayViewReceiptsController {
	
	private static final Logger LOGGER = LogManager.getLogger(PayViewReceiptsController.class);
	
	private ReceiptRepository receiptRepository;
	private AccountingRepository accountingRepository;
	private PayViewReceiptsView payViewReceiptsView;
	private User loggedUser;
	private ArrayList<Receipt> unpaidReceipts;

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public PayViewReceiptsController(ReceiptRepository receiptRepository, AccountingRepository accountingRepository,
			PayViewReceiptsView payViewReceiptsView) {
		this.receiptRepository = receiptRepository;
		this.accountingRepository = accountingRepository;
		this.payViewReceiptsView = payViewReceiptsView;
	}

	public void showUnpaidReceiptsOfLoggedUser(User loggedUser) {
		unpaidReceipts = new ArrayList<Receipt>(receiptRepository.getAllUnpaidReceiptOf(loggedUser));
		Comparator<Receipt> dateComparator = (Receipt r1, Receipt r2) -> r1.getTimestamp().compareTo(r2.getTimestamp());
		unpaidReceipts.sort(dateComparator.reversed());
		payViewReceiptsView.showReceipts(unpaidReceipts);
		Optional<Receipt> firstReceipt = unpaidReceipts.stream().findFirst();
		if (firstReceipt.isPresent())
			payViewReceiptsView.showItems(firstReceipt.get().getItems());
	}

	public void payAmount(double amountToPay, User loggedUser, User buyerUser) {
		List<Accounting> accountings = accountingRepository.getAccountingsOf(loggedUser);

		List<Accounting> accountingsBetweenLoggedAndBuyer = accountings.stream()
				.filter(a -> a.getReceipt().getBuyer().equals(buyerUser))
				.collect(Collectors.toList());
		
		
		Comparator<Accounting> dateReceiptComparator = (Accounting a1, Accounting a2) -> a1.getReceipt().getTimestamp()
				.compareTo(a2.getReceipt().getTimestamp());

		accountingsBetweenLoggedAndBuyer.sort(dateReceiptComparator);

		Double remainingAmountToPay = new Double(amountToPay);

		for (Accounting accounting : accountingsBetweenLoggedAndBuyer) {
			Double accountingAmount = new Double(accounting.getAmount());
			if (remainingAmountToPay > 0.0) {
				if (remainingAmountToPay >= accountingAmount) {
					accounting.setPaid(true);
					remainingAmountToPay -= accountingAmount;
				}else {
					accountingAmount -= remainingAmountToPay;
					accounting.setAmount(accountingAmount);
					remainingAmountToPay = 0.0;
				}
				accountingRepository.saveAccounting(accounting);
			}
		}

	}
}
