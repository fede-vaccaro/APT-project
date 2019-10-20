package com.unifiprojects.app.appichetto.services;

import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.swingviews.ReceiptSwingView;

public class UpdateReceiptService implements AppIchettoService {

	private ReceiptController receiptController;
	private ReceiptManager receiptManager;
	private ReceiptSwingView receiptView;

	public UpdateReceiptService(ReceiptRepository receiptRepository, UserRepository userRepository) {
		receiptManager = new ReceiptManager(receiptRepository);
		receiptView = new ReceiptSwingView();
		receiptController = new ReceiptController(receiptView, userRepository);
		receiptView.setReceiptController(receiptController);
	}

	@Override
	public void execute(Object object) {
		if (!(object instanceof Receipt))
			throw new IllegalArgumentException("Argument must be Receipt");
		Receipt receipt = (Receipt) object;
		receiptManager.uploadReceipt(receipt);
		receiptController.uploadReceiptManager(receiptManager);
		receiptView.show();
		receiptView.setUsers();
	}

	void setReceiptManager(ReceiptManager receiptManager) {
		this.receiptManager = receiptManager;
	}

	void setReceiptView(ReceiptSwingView receiptView) {
		this.receiptView = receiptView;
	}

	void setReceiptController(ReceiptController receiptController) {
		this.receiptController = receiptController;
	}
	
}
