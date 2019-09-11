package com.unifiprojects.app.appichetto.models;

import java.util.List;

public interface ReceiptRepository {
	public void saveReceipt(Receipt receipt);

	public List<Receipt> getAllUnpaidReceiptOf(User user);

	public List<Receipt> getAllUnpaidReceiptBoughtBy(User user);

	public Receipt getById(long id);

}
