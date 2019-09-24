package com.unifiprojects.app.appichetto.transactionHandlers;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;

public interface TransactionHandler {

	public void doInTransaction(TransactionCommands command) throws UncommittableTransactionException;
	
}

