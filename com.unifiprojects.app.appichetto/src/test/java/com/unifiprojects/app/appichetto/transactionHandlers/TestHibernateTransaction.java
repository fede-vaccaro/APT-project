package com.unifiprojects.app.appichetto.transactionHandlers;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransaction;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.transactionHandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.transactionHandlers.TransactionHandler;

public class TestHibernateTransaction {

	private static EntityManager entityManager;
	private static EntityManagerFactory entityManagerFactory;

	private TransactionHandler transaction;

	@BeforeClass
	public static void setUpBeforeClass() {
		entityManagerFactory = Persistence.createEntityManagerFactory("test-persistence-unit");
	}

	@AfterClass
	public static void tearDownClass() {
		if (entityManager.isOpen())
			entityManager.close();
		if (entityManagerFactory.isOpen())
			entityManagerFactory.close();
	}

	@Before
	public void setUp() {

		if (entityManagerFactory == null || !entityManagerFactory.isOpen())
			Persistence.createEntityManagerFactory("test-persistence-unit");

		entityManager = entityManagerFactory.createEntityManager();

		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("TRUNCATE SCHEMA public AND COMMIT").executeUpdate();
		entityManager.getTransaction().commit();

		transaction = new HibernateTransaction(entityManager);
	}

	@Test
	public void testDoInTransactionExecutesTheCommandsAndReturnsTrue() {
		User user1 = new User("user", "pw");

		transaction.doInTransaction(() -> {
			entityManager.persist(user1);
		});
		entityManager.clear();

		assertThat(entityManager.find(User.class, user1.getId())).isEqualTo(user1);

	}

	@Test
	public void testDoInTransactionDoesRollbackIfRollbackExceptionIsThrownAndReturnFalse() {
		User user1 = new User("user", "pw");

		assertThatExceptionOfType(UncommittableTransaction.class).isThrownBy(() -> {
			transaction.doInTransaction(() -> {
				entityManager.persist(user1);
				throw new RollbackException();
			});
		});

		entityManager.clear();
		
		assertThat(entityManager.find(User.class, user1.getId())).isNull();
	}

}
