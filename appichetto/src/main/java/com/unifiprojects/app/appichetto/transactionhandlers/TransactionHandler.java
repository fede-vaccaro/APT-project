package com.unifiprojects.app.appichetto.transactionhandlers;

public interface TransactionHandler {

	public void doInTransaction(TransactionCommands command);
	
}

