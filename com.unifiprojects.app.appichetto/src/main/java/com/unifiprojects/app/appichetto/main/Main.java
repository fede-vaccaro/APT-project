package com.unifiprojects.app.appichetto.main;

import java.awt.EventQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.unifiprojects.app.appichetto.controllers.LoginController;
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
import com.unifiprojects.app.appichetto.swingviews.HomepageSwingView;
import com.unifiprojects.app.appichetto.swingviews.LoginViewSwing;
import com.unifiprojects.app.appichetto.swingviews.PayViewReceiptsViewSwing;
import com.unifiprojects.app.appichetto.swingviews.ReceiptSwingView;
import com.unifiprojects.app.appichetto.swingviews.ShowHistoryViewSwing;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;

public class Main {

	public static void main(String[] args) {
		// create login view
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("it-persistence-unit");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("do\n" + "$$\n" + "declare\n" + "  l_stmt text;\n" + "begin\n"
				+ "  select 'truncate ' || string_agg(format('%I.%I', schemaname, tablename), ',')\n"
				+ "    into l_stmt\n" + "  from pg_tables\n" + "  where schemaname in ('public');\n" + "\n"
				+ "  execute l_stmt;\n" + "end;\n" + "$$").executeUpdate();
		entityManager.getTransaction().commit();

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

		//set LogIn 
		LoginViewSwing loginViewSwing = new LoginViewSwing();
		LoginController loginController = new LoginController(transaction, userRepository, loginViewSwing);
		loginViewSwing.setLoginController(loginController);
		// set PayViewReceipts block
		PayViewReceiptsViewSwing payReceiptsView = new PayViewReceiptsViewSwing();
		PayViewReceiptsController payReceiptsController = new PayViewReceiptsController(receiptRepository,
				accountingRepository, payReceiptsView);
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
		ReceiptSwingView receiptSwingView = new ReceiptSwingView();
		ReceiptController receiptController = new ReceiptController(receiptManager, receiptSwingView, userRepository);
		receiptController.setTeansactionHandler(transaction);
		receiptSwingView.setReceiptController(receiptController);
		receiptSwingView.setUsers();

		// set HomePage
		HomepageSwingView homepageSwingView = new HomepageSwingView();
		homepageSwingView.setHistoryViewSwing(showHistoryView);
		homepageSwingView.setPayViewReceiptsViewSwing(payReceiptsView);
		homepageSwingView.setReceiptSwingView(receiptSwingView);
		homepageSwingView.setLoginView(loginViewSwing);
		receiptSwingView.addObserver(homepageSwingView);
		payReceiptsView.addObserver(homepageSwingView);
		showHistoryView.addObserver(homepageSwingView);
		loginViewSwing.addObserver(homepageSwingView);

		EventQueue.invokeLater(() -> {
			try {
				loginViewSwing.getFrame().setVisible(true);
			} catch (Exception e) {

			}
		});
	}

}
