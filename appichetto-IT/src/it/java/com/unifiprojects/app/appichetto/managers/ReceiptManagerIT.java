package com.unifiprojects.app.appichetto.managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;

public class ReceiptManagerIT {
	private static MVCBaseTest baseTest;// = new MVCBaseTest();
	private static EntityManager entityManager;
	private User buyer;
	private static ReceiptManager receiptManager;
	private static Injector baseTestInjector;
	
	@BeforeClass
	public static void setupEntityManager() {
		//baseTest.setupEntityManager();
		baseTestInjector = Guice.createInjector(new EntityManagerModule());
		baseTest = baseTestInjector.getInstance(MVCBaseTest.class);
	}

	@AfterClass
	public static void close() {
		baseTest.closeEntityManager();
	}

	@Before
	public void setup() {

		baseTest.wipeTablesBeforeTest();
		entityManager = baseTest.getEntityManager();
		buyer = new User("Pippo", "psw");
		Injector repositoryInjector = baseTestInjector.createChildInjector(new RepositoriesModule());
		repositoryInjector.getInstance(ReceiptRepository.class);
		receiptManager = repositoryInjector.createChildInjector(new AbstractModule() {}).getInstance(ReceiptManager.class);
		receiptManager.setBuyer(buyer);
		
		entityManager.getTransaction().begin();
		entityManager.persist(buyer);
		entityManager.getTransaction().commit();
	}
	
	@Test
	public void testSaveReceipt() {
		User pluto = new User("Pluto","psw");
		User mario = new User("Mario","psw");
		Map<User, Accounting> accountings = new HashMap<>();
		accountings.put(pluto, new Accounting(pluto, 2.2));
		accountings.put(mario, new Accounting(mario, 2.2));
		receiptManager.setAccountings(accountings);
		entityManager.getTransaction().begin();
		entityManager.persist(pluto);
		entityManager.persist(mario);
		entityManager.getTransaction().commit();
		
		entityManager.getTransaction().begin();
		Long receiptId = receiptManager.saveReceipt();
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		System.out.println(receiptId);
		assertThat(entityManager.find(Receipt.class, receiptId)).isEqualTo(receiptManager.getReceipt());
	}
}
