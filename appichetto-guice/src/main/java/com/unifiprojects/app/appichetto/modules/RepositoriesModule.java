package com.unifiprojects.app.appichetto.modules;

import com.google.inject.AbstractModule;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.AccountingRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;

public class RepositoriesModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AccountingRepository.class).to(AccountingRepositoryHibernate.class);
		bind(ReceiptRepository.class).to(ReceiptRepositoryHibernate.class);
		bind(TransactionHandler.class).to(HibernateTransaction.class);
		bind(UserRepository.class).to(UserRepositoryHibernate.class);
	}
}
