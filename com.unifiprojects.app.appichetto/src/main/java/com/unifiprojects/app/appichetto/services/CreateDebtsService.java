package com.unifiprojects.app.appichetto.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class CreateDebtsService {

	private Map<User, Accounting> accountingsMap;
	private List<Receipt> refundReceipts;

	public CreateDebtsService() {
		this.accountingsMap = new HashMap<>();
		this.refundReceipts = new ArrayList<>();
	}

	public void computeDebts(Receipt receipt, Map<User, Accounting> oldAccountingsByItemPriceMap) {
		Map<User, Accounting> accountingsByItemPriceMap = calculateUserAccountingMapByItemPrice(receipt);
		Map<User, Accounting> oldAccountingsMap = createUserAccountingMap(receipt);
		calculateUserAccountingMap(oldAccountingsByItemPriceMap, oldAccountingsMap, accountingsByItemPriceMap);
		createRefundReceipts(accountingsMap, receipt.getBuyer());
	}

	Map<User, Accounting> createUserAccountingMap(Receipt receipt) {
		Map<User, Accounting> accountings = new HashMap<>();
		receipt.getAccountings().stream().forEach(accounting -> accountings.put(accounting.getUser(), accounting));
		return accountings;
	}

	Map<User, Accounting> calculateUserAccountingMap(Map<User, Accounting> oldAIPM, Map<User, Accounting> oldAM,
			Map<User, Accounting> aIPM) {
		Accounting defaultAccountig = new Accounting(new User(), 0.0);
		Set<User> users = new HashSet<>();
		users.addAll(oldAIPM.keySet());
		users.addAll(aIPM.keySet());

		users.stream().forEach(user -> {
			Double amount = Math.round(100 * (oldAM.getOrDefault(user, defaultAccountig).getAmount()
					- oldAIPM.getOrDefault(user, defaultAccountig).getAmount()
					+ aIPM.getOrDefault(user, defaultAccountig).getAmount())) / 100.0;
			if (amount != 0.0)// TODO da decidere
				accountingsMap.put(user, new Accounting(user, amount));
		});

		return accountingsMap;
	}

	Map<User, Accounting> calculateUserAccountingMapByItemPrice(Receipt receipt) {
		Map<User, Accounting> accountingByPrice = new HashMap<>();
		for (Item i : receipt.getItems()) {
			i.getOwners().stream().filter(owner -> !owner.equals(receipt.getBuyer())).forEach(owner -> {
				if (accountingByPrice.containsKey(owner))
					accountingByPrice.get(owner).addAmount(i.getPricePerOwner());
				else
					accountingByPrice.put(owner, new Accounting(owner, i.getPricePerOwner()));
			});
		}
		return accountingByPrice;
	}

	List<Receipt> createRefundReceipts(Map<User, Accounting> accountings, User debtor) {
		accountings.values().stream().filter(accountig -> accountig.getAmount() < 0.0)
				.map(accounting -> createRefundReceipt(accounting, debtor))
				.forEach(refundReceipt -> refundReceipts.add(refundReceipt));
		return refundReceipts;
	}

	Receipt createRefundReceipt(Accounting accounting, User debtor) {
		Receipt receipt = new Receipt(accounting.getUser());
		receipt.addAccounting(new Accounting(debtor, Math.abs(accounting.getAmount())));
		receipt.setDescription("Refund receipt");
		accounting.setAmount(0.0);
		return receipt;
	}

	void setRefundReceipts(List<Receipt> refundReceipts) {
		this.refundReceipts = refundReceipts;
	}

	public List<Receipt> getRefundReceipts() {
		return refundReceipts;
	}

	public List<Accounting> getAccountings() {
		return new ArrayList<>(accountingsMap.values());
	}

}
