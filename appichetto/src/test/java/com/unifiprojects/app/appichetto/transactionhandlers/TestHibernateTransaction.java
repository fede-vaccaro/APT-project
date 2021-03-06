package com.unifiprojects.app.appichetto.transactionhandlers;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.doThrow;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.transactionhandlers.TransactionHandler;

public class TestHibernateTransaction {

	private static EntityManager entityManager;
	private static EntityManagerFactory entityManagerFactory;

	private TransactionHandler transaction;
	
	@Mock
	TransactionCommands commands;

	@BeforeClass
	public static void setUpBeforeClass() {
		entityManagerFactory = Persistence.createEntityManagerFactory("test-persistence-unit");
		entityManager = entityManagerFactory.createEntityManager();
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

		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("TRUNCATE SCHEMA public AND COMMIT").executeUpdate();
		entityManager.getTransaction().commit();

		MockitoAnnotations.initMocks(this);
		transaction = new HibernateTransaction(entityManager);
	}

	@Test
	public void testDoInTransactionExecutesTheCommandsInside() {
		User user1 = new User("user", "pw");

		transaction.doInTransaction(() -> {
			entityManager.persist(user1);
		});
		entityManager.clear();

		assertThat(entityManager.find(User.class, user1.getId())).isEqualTo(user1);

	}

	@Test
	public void testDoInTransactionDoesRollbackIfRollbackExceptionIsThrown() {
		User user1 = new User("user", "pw");

		assertThatExceptionOfType(UncommittableTransactionException.class).isThrownBy(() -> {
			transaction.doInTransaction(() -> {
				entityManager.persist(user1);
				throw new RollbackException();
			});
		});

		entityManager.clear();
		
		assertThat(entityManager.find(User.class, user1.getId())).isNull();
	}
	
	@Test
	public void testTransactionIsClosedWhenOccursARollbackException() {
		
		doThrow(new RollbackException()).when(commands).execute();
		
		assertThatExceptionOfType(UncommittableTransactionException.class).isThrownBy(() -> transaction.doInTransaction(commands));
		
		assertThat(entityManager.getTransaction().isActive()).isFalse();
	}
	
	@Test
	public void testTransactionIsClosedWhenAnExceptionDifferentFromUncommittableIsThrownToTheOutsideOfTheTransaction() {
		doThrow(new RuntimeException()).when(commands).execute();
		
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
			transaction.doInTransaction(commands);
		});
		
		assertThat(entityManager.getTransaction().isActive()).isFalse();
	}

}
