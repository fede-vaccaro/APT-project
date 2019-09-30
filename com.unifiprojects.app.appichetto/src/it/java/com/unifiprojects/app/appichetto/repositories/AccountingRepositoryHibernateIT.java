package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.User;

public class AccountingRepositoryHibernateIT {

	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;

	private AccountingRepository accountingRepository;

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
		accountingRepository = new AccountingRepositoryHibernate(entityManager);
	}


	private User createAndPersistUser(String name) {
		User user = new User(name, "psw");
		entityManager.getTransaction().begin();
		entityManager.persist(user);
		entityManager.getTransaction().commit();
		entityManager.clear();

		return user;
	}

	@Test
	public void testSaveAccounting() {
		User user = createAndPersistUser("Pippo");

		Accounting accounting = new Accounting(user);

		entityManager.getTransaction().begin();
		accountingRepository.saveAccounting(accounting);
		entityManager.getTransaction().commit();
		entityManager.clear();

		Accounting retrivedAccounting = entityManager.find(Accounting.class, accounting.getId());
		assertThat(accounting).isEqualTo(retrivedAccounting);
	}

	@Test
	public void testUpdateAccounting() {
		User user = createAndPersistUser("Pippo");

		Accounting accounting = new Accounting(user);

		entityManager.getTransaction().begin();
		entityManager.persist(accounting);
		entityManager.getTransaction().commit();
		entityManager.clear();

		accounting.addAmount(2.2);

		entityManager.getTransaction().begin();
		accountingRepository.saveAccounting(accounting);
		entityManager.getTransaction().commit();
		entityManager.clear();

		Accounting retrivedAccounting = entityManager.find(Accounting.class, accounting.getId());
		assertThat(accounting).isEqualTo(retrivedAccounting);
	}

	@Test
	public void testGetById() {
		User user = createAndPersistUser("Pippo");
		Accounting accounting = new Accounting(user);

		entityManager.getTransaction().begin();
		entityManager.persist(accounting);
		entityManager.getTransaction().commit();
		entityManager.clear();

		Accounting retrivedAccounting = accountingRepository.getById(accounting.getId());

		assertThat(accounting).isEqualTo(retrivedAccounting);
	}
}
