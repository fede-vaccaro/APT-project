package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public interface ReceiptRepository {
	public void saveReceipt(Receipt receipt);

	public void removeReceipt(Receipt receipt);
	
	public List<Receipt> getAllUnpaidReceiptsOf(User user);

	public List<Receipt> getAllReceiptsBoughtBy(User user);

	public Receipt getById(Long id);
}
