package com.unifiprojects.app.appichetto.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;


public class CreateDebtsService {

	private Map<User, Accounting> accountingsMap;
	private List<Receipt> refundReceipts;

	@Inject
	public CreateDebtsService() {
		this.accountingsMap = new HashMap<>();
		this.refundReceipts = new ArrayList<>();
	}

	public Pair<List<Accounting>, List<Receipt>> computeAccountingDebtsAndRefoundReceipts(Receipt receipt,
			Map<User, Accounting> accountingsFromOldItemPrice) {
		
		Map<User, Accounting> accountingsFromItemPrice = calculateAccountingMapFromItemPrice(receipt);
		Map<User, Accounting> oldAccountingsMap = createAccountingMapFromAccountingList(receipt.getAccountings());
		
		calculateAccountingMap(accountingsFromOldItemPrice, oldAccountingsMap, accountingsFromItemPrice);
		createRefundReceipts(accountingsMap, receipt.getBuyer());

		accountingsMap.values().removeIf(accounting -> accounting.getAmount() == 0.0);
		
		return new Pair<>(new ArrayList<>(accountingsMap.values()), refundReceipts);
	}

	Map<User, Accounting> createAccountingMapFromAccountingList(List<Accounting> receiptAccountings) {
		Map<User, Accounting> accountings = new HashMap<>();
		receiptAccountings.stream().forEach(accounting -> accountings.put(accounting.getUser(), accounting));
		return accountings;
	}

	Map<User, Accounting> calculateAccountingMap(Map<User, Accounting> accountingsFromOldItemPrice, Map<User, Accounting> oldAccountingsMap,
			Map<User, Accounting> accountingsFromItemPrice) {
		Accounting defaultAccountig = new Accounting(new User(), 0.0);
		Set<User> users = new HashSet<>();
		
		users.addAll(accountingsFromOldItemPrice.keySet());
		users.addAll(accountingsFromItemPrice.keySet());

		users.stream().forEach(user -> {
			Double amount = Math.round(100 * (oldAccountingsMap.getOrDefault(user, defaultAccountig).getAmount()
					- accountingsFromOldItemPrice.getOrDefault(user, defaultAccountig).getAmount()
					+ accountingsFromItemPrice.getOrDefault(user, defaultAccountig).getAmount())) / 100.0;
			if (amount != 0.0)
				accountingsMap.put(user, new Accounting(user, amount));
		});

		return accountingsMap;
	}

	Map<User, Accounting> calculateAccountingMapFromItemPrice(Receipt receipt) {
		Map<User, Accounting> accountingFromItemPrice = new HashMap<>();
		for (Item i : receipt.getItems()) {
			i.getOwners().stream().filter(owner -> !owner.equals(receipt.getBuyer())).forEach(owner -> {
				if (accountingFromItemPrice.containsKey(owner))
					accountingFromItemPrice.get(owner).addAmount(i.getPricePerOwner());
				else
					accountingFromItemPrice.put(owner, new Accounting(owner, i.getPricePerOwner()));
			});
		}
		return accountingFromItemPrice;
	}

	List<Receipt> createRefundReceipts(Map<User, Accounting> accountingsMap, User debtor) {
		accountingsMap.values().stream().filter(accounting -> Math.signum(accounting.getAmount()) == -1.0)
				.map(accounting -> createRefundReceipt(accounting, debtor))
				.forEach(refundReceipt -> refundReceipts.add(refundReceipt));
		return refundReceipts;
	}

	Receipt createRefundReceipt(Accounting accounting, User debtor) {
		Receipt receipt = new Receipt(accounting.getUser());
		receipt.addAccounting(new Accounting(debtor, Math.abs(accounting.getAmount())));
		receipt.setDescription("Refund receipt");
		accounting.setAmount(0.);
		return receipt;
	}

}
