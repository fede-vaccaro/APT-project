package com.unifiprojects.app.appichetto.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.exceptions.IllegalIndex;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.services.CreateDebtsService;

public class ReceiptManager {

	private Receipt receipt;
	private ReceiptRepository receiptRepository;
	private Map<User, Accounting> accountingsMap;
	private CreateDebtsService createDebtsService;

	@Inject
	public ReceiptManager(Receipt receipt, ReceiptRepository receiptRepository, CreateDebtsService createDebtsService) {
		this.receipt = receipt;
		this.receiptRepository = receiptRepository;
		this.createDebtsService = createDebtsService;
		this.accountingsMap = new HashMap<>();
	}

	public void addItem(Item item) {
		receipt.addItem(item);
	}

	public void updateItem(int index, Item item) {
		if (index < receipt.getItems().size()) {
			receipt.updateItem(index, item);
		} 
		else
			throw new IllegalIndex("Index not in list");
	}

	public void deleteItem(Item itemToDelete) {
		receipt.deleteItem(itemToDelete);
	}

	public Receipt saveReceipt() {
		Pair<List<Accounting>, List<Receipt>> accountingListRefundReceiptsListPair = createDebtsService.computeAccountingDebtsAndRefoundReceipts(receipt, accountingsMap);

		List<Accounting> accountings = accountingListRefundReceiptsListPair.getFirst();
		List<Receipt> refoundReceipts = accountingListRefundReceiptsListPair.getSecond();

		receipt.setAccountingList(accountings);
		receiptRepository.saveReceipt(receipt);
		refoundReceipts.stream().forEach(refoundReceipt -> receiptRepository.saveReceipt(refoundReceipt));

		return receipt;
	}

	public void uploadReceipt(Receipt receipt) {
		this.receipt = receipt;
		accountingsMap.clear();
		for (Item item : receipt.getItems()) {
			item.getOwners().stream().filter(owner -> !owner.equals(receipt.getBuyer())).forEach(owner -> {
				if (accountingsMap.containsKey(owner))
					accountingsMap.get(owner).addAmount(item.getPricePerOwner());
				else
					accountingsMap.put(owner, new Accounting(owner, item.getPricePerOwner()));
			});
		}
	}

	void setAccountings(Map<User, Accounting> accountings) {
		this.accountingsMap = accountings;
	}

	void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public void setCreateDebtsService(CreateDebtsService createDebtsService) {
		this.createDebtsService = createDebtsService;
	}

	public void setBuyer(User buyer) {
		receipt.setBuyer(buyer);
	}

}
