package com.unifiprojects.app.appichetto.services;

import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public class UpdateReceiptService implements AppIchettoService{

	private ReceiptController receiptController;
	private ReceiptManager receiptManager;
	private ReceiptView receiptView;
	
	@Override
	public void start(Object object) {
		
	}

}
