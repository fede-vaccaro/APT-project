package com.unifiprojects.app.appichetto.transactionHandlers;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;

public class FakeTransaction implements TransactionHandler {

		@Override
		public void doInTransaction(ExecuteInTransaction command) throws UncommittableTransactionException {
			command.execute();
		}
		
	}