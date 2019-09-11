package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public interface ReceiptRepository {
	public void saveReceipt(Receipt receipt);

	public List<Receipt> getAllUnpaidReceiptOf(User user);

	public List<Receipt> getAllUnpaidReceiptBoughtBy(User user);

	public Receipt getById(long id);

}
