package com.unifiprojects.app.appichetto.transactionhandlers;

public class FakeTransaction implements TransactionHandler {

		@Override
		public void doInTransaction(TransactionCommands command) {
			command.execute();
		}
		
	}