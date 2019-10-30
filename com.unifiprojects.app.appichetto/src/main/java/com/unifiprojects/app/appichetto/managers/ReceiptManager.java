package com.unifiprojects.app.appichetto.managers;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private static final Logger LOGGER = LogManager.getLogger(ReceiptManager.class);
	
	public ReceiptManager(User buyer, ReceiptRepository receiptRepository) {
		this.receiptRepository = receiptRepository;
		this.receipt = new Receipt(buyer);
		this.accountingsMap = new HashMap<>();
		this.createDebtsService = new CreateDebtsService();
	}
	
	
	public ReceiptManager(ReceiptRepository receiptRepository) {														
		this.receiptRepository = receiptRepository;
		this.accountingsMap = new HashMap<>();
	}

	@Inject
	public ReceiptManager(Receipt receipt, ReceiptRepository receiptRepository, CreateDebtsService createDebtsService) {
		this.receipt = receipt;
		this.receiptRepository = receiptRepository;
		this.createDebtsService = createDebtsService;
		this.accountingsMap = new HashMap<>();
	}

	public void addItem(Item item) {
		receipt.addItem(item);
		LOGGER.debug("{} ADDED BY RECEIPT MANAGER", item);
	}

	public void updateItem(int index, Item item) {
		if (index < receipt.getItems().size()) {
			receipt.updateItem(index, item);
			LOGGER.debug("{} UPDATED BY RECEIPT MANAGER", item);
		} else
			throw new IllegalIndex("Index not in list");
	}

	public void deleteItem(Item itemToDelete) {
		receipt.deleteItem(itemToDelete);
		LOGGER.debug("{} DELETED BY RECEIPT MANAGER", itemToDelete);
	}

	public Long saveReceipt() {
		createDebtsService.computeDebts(receipt, accountingsMap);
		
		List<Receipt> refoundReceipts = createDebtsService.getRefundReceipts();
		List<Accounting> accountings = createDebtsService.getAccountings();
		
		receipt.setAccountingList(accountings);
		refoundReceipts.stream().forEach(refoundReceipt -> receiptRepository.saveReceipt(refoundReceipt));
		receiptRepository.saveReceipt(receipt);
		
		LOGGER.debug("{} SAVED BY RECEIPT MANAGER", receipt);
		return receipt.getId();
	}
	
	public void uploadReceipt(Receipt receipt) {
		this.receipt = receipt;
		for (Item i : receipt.getItems()) {
			i.getOwners().stream().filter(owner -> !owner.equals(receipt.getBuyer()))
					.forEach(owner -> {
						if (accountingsMap.containsKey(owner))
							accountingsMap.get(owner).addAmount(i.getPricePerOwner());
						else
							accountingsMap.put(owner, new Accounting(owner, i.getPricePerOwner()));
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

	public int getItemsListSize() {
		return receipt.getItemsListSize();
	}
	
	public Receipt getReceipt() {
		return receipt;
	}

	public String getDescription() {
		return receipt.getDescription();
	}

	public List<Item> getItems() {
		return receipt.getItems();
	}

	public GregorianCalendar getTimestamp() {
		return receipt.getTimestamp();
	}

}
