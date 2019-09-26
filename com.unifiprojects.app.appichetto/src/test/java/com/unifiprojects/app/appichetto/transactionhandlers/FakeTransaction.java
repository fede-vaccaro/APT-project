package com.unifiprojects.app.appichetto.transactionhandlers;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionCommands;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;

public class FakeTransaction implements TransactionHandler {

		@Override
		public void doInTransaction(TransactionCommands command) throws UncommittableTransactionException {
			command.execute();
		}
		
	}