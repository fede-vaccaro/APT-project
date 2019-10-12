package com.unifiprojects.app.appichetto.managers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;

public class PaymentManager {
	
	private AccountingRepository accountingRepository;

	public AccountingRepository getAccountingRepository() {
		return accountingRepository;
	}

	public void setAccountingRepository(AccountingRepository accountingRepository) {
		this.accountingRepository = accountingRepository;
	}

	public void makePayment(double enteredAmount, User loggedUser, User buyerUser) {
		if (enteredAmount <= 0.0) {
			throw new IllegalArgumentException("enteredAmount should be greater than zero.");
		}

		List<Accounting> accountingsBetweenLoggedAndBuyer = getAccountingsBetweenLoggedAndBuyer(loggedUser,
				buyerUser);

		Comparator<Accounting> dateReceiptComparator = (Accounting a1, Accounting a2) -> a1.getReceipt()
				.getTimestamp().compareTo(a2.getReceipt().getTimestamp());

		accountingsBetweenLoggedAndBuyer.sort(dateReceiptComparator);

		Double totalAmountToPay = accountingsBetweenLoggedAndBuyer.stream().mapToDouble(Accounting::getAmount)
				.sum();

		Double remainingAmount = enteredAmount;

		if (Precision.round(totalAmountToPay, 2) < Precision.round(remainingAmount, 2)) {
			throw new IllegalArgumentException("enteredAmount should be > 0");
		}

		for (Accounting accounting : accountingsBetweenLoggedAndBuyer) {
			Double accountingAmount = accounting.getAmount();
			if (remainingAmount > 0.0) {
				if (remainingAmount >= accountingAmount) {
					// accounting.setPaid(true);
					accounting.setAmount(0.0);
					remainingAmount -= accountingAmount;
				} else {
					accountingAmount -= remainingAmount;
					accounting.setAmount(accountingAmount);
					remainingAmount = 0.0;
				}
				accountingRepository.saveAccounting(accounting);
			}
		}
	}

	List<Accounting> getAccountingsBetweenLoggedAndBuyer(User loggedUser, User buyerUser) {
		List<Accounting> accountings = accountingRepository.getAccountingsOf(loggedUser);

		return accountings.stream().filter(a -> a.getReceipt().getBuyer().equals(buyerUser))
				.collect(Collectors.toList());
	}


}
