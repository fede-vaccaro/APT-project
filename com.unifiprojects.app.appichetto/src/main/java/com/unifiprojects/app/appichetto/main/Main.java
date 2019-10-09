package com.unifiprojects.app.appichetto.main;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.unifiprojects.app.appichetto.controllers.PayViewReceiptsController;
import com.unifiprojects.app.appichetto.controllers.ReceiptController;
import com.unifiprojects.app.appichetto.controllers.ShowHistoryController;
import com.unifiprojects.app.appichetto.managers.ReceiptManager;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.AccountingRepository;
import com.unifiprojects.app.appichetto.repositories.AccountingRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.swingviews.PayViewReceiptsViewSwing;
import com.unifiprojects.app.appichetto.swingviews.ShowHistoryViewSwing;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;
import com.unifiprojects.app.appichetto.views.ShowHistoryView;

public class Main {

	public static void main(String[] args) {
		// create login view
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("it-persistence-unit");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		// logged user
		User logged = new User("Federico", "pw");
		User user2 = new User("Giuseppe", "pw");
		
		entityManager.getTransaction().begin();
		entityManager.persist(logged);
		entityManager.persist(user2);
		entityManager.getTransaction().commit();
		
		// repositories
		UserRepository userRepository = new UserRepositoryHibernate(entityManager);
		ReceiptRepository receiptRepository = new ReceiptRepositoryHibernate(entityManager);
		AccountingRepository accountingRepository = new AccountingRepositoryHibernate(entityManager);
		
		// transaction handler
		TransactionHandler transaction = new HibernateTransaction(entityManager);
		
		// set PayViewReceipts block
		PayViewReceiptsViewSwing payReceiptsView = new PayViewReceiptsViewSwing();
		PayViewReceiptsController payReceiptsController = new PayViewReceiptsController(receiptRepository, accountingRepository, payReceiptsView);
		payReceiptsController.setTransactionHandler(transaction);
		payReceiptsView.setController(payReceiptsController);
		payReceiptsView.setLoggedUser(logged);
		
		// set ShowHistory block
		ShowHistoryViewSwing showHistoryView = new ShowHistoryViewSwing();
		ShowHistoryController showHistoryController = new ShowHistoryController(receiptRepository, showHistoryView);
		showHistoryView.setController(showHistoryController);
		showHistoryController.setTransaction(transaction);
		showHistoryController.setLoggedUser(logged);
		
		// set CreateReceipt block
		ReceiptManager receiptManager = new ReceiptManager(logged, receiptRepository);
		ReceiptController receiptController = new ReceiptController(receiptManager, receiptView, userRepository);
		
		
				
	}

}
