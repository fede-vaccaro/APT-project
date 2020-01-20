package com.unifiprojects.app.appichetto.factories;

import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.views.ReceiptView;

public interface ReceiptControllerFactory {
	ReceiptController create(ReceiptView receiptView);
}
