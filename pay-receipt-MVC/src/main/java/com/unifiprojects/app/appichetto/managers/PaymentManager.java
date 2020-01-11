package com.unifiprojects.app.appichetto.managers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Precision;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;

public class PaymentManager {

	@Inject
	private AccountingRepository accountingRepository;

	public void setAccountingRepository(AccountingRepository accountingRepository) {
		this.accountingRepository = accountingRepository;
	}

	public void makePayment(double enteredAmount, User loggedUser, User buyerUser) {
		if (enteredAmount <= 0.0) {
			throw new IllegalArgumentException("enteredAmount should be greater than zero.");
		}

		List<Accounting> accountingsBetweenLoggedAndBuyer = getAndSortAccountingsBetweenLoggedAndBuyer(loggedUser,
				buyerUser);

		Double totalAmountToPay = getTotalAmountToPay(accountingsBetweenLoggedAndBuyer);

		double remainingAmount = enteredAmount;

		if (Precision.round(totalAmountToPay, 2) < Precision.round(remainingAmount, 2)) {
			throw new IllegalArgumentException(
					String.format("enteredAmount (%.2f) should be lower than the amount to pay (%.2f).", enteredAmount,
							totalAmountToPay));
		}

		executePayment(accountingsBetweenLoggedAndBuyer, remainingAmount);
	}

	private void executePayment(List<Accounting> accountingsBetweenLoggedAndBuyer, double remainingAmount) {
		for (Accounting accounting : accountingsBetweenLoggedAndBuyer) {
			double accountingAmount = accounting.getAmount();
			if (remainingAmount > 0.0) {
				if (remainingAmount >= accountingAmount) {
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

	private double getTotalAmountToPay(List<Accounting> accountingsBetweenLoggedAndBuyer) {
		return accountingsBetweenLoggedAndBuyer.stream().mapToDouble(Accounting::getAmount).sum();
	}

	List<Accounting> getAndSortAccountingsBetweenLoggedAndBuyer(User loggedUser, User buyerUser) {
		List<Accounting> accountings = accountingRepository.getAccountingsOf(loggedUser);

		accountings = accountings.stream().filter(a -> a.getReceipt().getBuyer().equals(buyerUser))
				.collect(Collectors.toList());

		Comparator<Accounting> dateReceiptComparator = (Accounting a1, Accounting a2) -> a1.getReceipt().getTimestamp()
				.compareTo(a2.getReceipt().getTimestamp());

		accountings.sort(dateReceiptComparator);

		return accountings;
	}

	public void makePaymentWithReceipt(Receipt loggedUserReceipt, User loggedUser) {
		loggedUserReceipt.getAccountings().forEach(a -> {
			List<Accounting> accountingsBetweenLoggedAndBuyer = getAndSortAccountingsBetweenLoggedAndBuyer(loggedUser,
					a.getUser());

			Double totalAmountToPay = getTotalAmountToPay(accountingsBetweenLoggedAndBuyer);

			double remainingAmount = a.getAmount();

			a.setAmount(Math.max(remainingAmount - totalAmountToPay, 0.0));

			executePayment(accountingsBetweenLoggedAndBuyer, remainingAmount);

		});
	}

}
