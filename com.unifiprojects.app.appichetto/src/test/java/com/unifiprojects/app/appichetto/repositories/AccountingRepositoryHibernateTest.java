package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.User;

public class AccountingRepositoryHibernateTest {

	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager entityManager;

	private AccountingRepository accountingRepository;

	@BeforeClass
	public static void setUpBeforeClass() {
		entityManagerFactory = Persistence.createEntityManagerFactory("test-persistence-unit");
		entityManager = entityManagerFactory.createEntityManager();
	}

	@Before
	public void setUp() {
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("TRUNCATE SCHEMA public AND COMMIT").executeUpdate();
		entityManager.getTransaction().commit();

		accountingRepository = new AccountingRepositoryHibernate(entityManager);
	}

	@AfterClass
	public static void tearDown() {
		entityManager.close();
		entityManagerFactory.close();
	}
	
	private User createAndPersistUser(String name) {
		User user = new User(name,"psw");
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
		
		accountingRepository.saveAccounting(accounting);
		entityManager.clear();
		
		Accounting retrivedAccounting = entityManager.find(Accounting.class, accounting.getId());
		assertThat(accounting).isEqualTo(retrivedAccounting);
	}
	
	@Test
	public void testGetAccountingsOfOneUserWhenNoAccountingsArePresent() {
		User pippo = createAndPersistUser("Pippo");
		
		List<Accounting> retrivedPippoAccounting = accountingRepository.getAccountingsOf(pippo);
		assertThat(retrivedPippoAccounting).isEmpty();
	}

	@Test
	public void testGetAccountingsOfOneUserWhenTwoOfTheseOfDifferentUsersArePresent() {
		User pippo = createAndPersistUser("Pippo");
		User pluto = createAndPersistUser("Pluto");
		
		Accounting accountingOfPippo = new Accounting(pippo);
		Accounting accountingOfPluto = new Accounting(pluto);
		
		entityManager.getTransaction().begin();
		entityManager.persist(accountingOfPippo);
		entityManager.persist(accountingOfPluto);
		entityManager.getTransaction().commit();
		entityManager.clear();
		
		
		List<Accounting> retrivedPippoAccounting = accountingRepository.getAccountingsOf(pippo);
		assertThat(retrivedPippoAccounting).containsExactlyInAnyOrder(accountingOfPippo);	
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
