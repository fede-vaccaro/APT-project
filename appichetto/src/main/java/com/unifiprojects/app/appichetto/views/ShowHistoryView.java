package com.unifiprojects.app.appichetto.views;

import java.util.List;

import com.unifiprojects.app.appichetto.models.Receipt;

public interface ShowHistoryView {
	public void showShoppingHistory(List<Receipt> receipts);
	
	public void showErrorMsg(String msg);

	public void setReceiptView(ReceiptView receiptView);
}
