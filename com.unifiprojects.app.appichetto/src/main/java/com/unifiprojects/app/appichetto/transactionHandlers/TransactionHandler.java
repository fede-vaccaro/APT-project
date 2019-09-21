package com.unifiprojects.app.appichetto.transactionHandlers;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransaction;

public interface TransactionHandler {

	public void doInTransaction(ExecuteInTransaction command) throws UncommittableTransaction;
	
}

