package com.unifiprojects.app.appichetto.managers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepository;
import com.unifiprojects.app.appichetto.repositories.ReceiptRepositoryHibernate;

public class ReceiptManagerIT {
	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private ReceiptRepository receiptRepository;

	private User buyer;

	@InjectMocks
	private ReceiptManager receiptManager;

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
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
		receiptRepository = new ReceiptRepositoryHibernate(entityManager);
		receiptManager = new ReceiptManager(buyer, receiptRepository);

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
		
		Long receiptId = receiptManager.saveReceipt();
		
		assertThat(receiptRepository.getById(receiptId)).isEqualTo(receiptManager.getReceipt());
	}

}
