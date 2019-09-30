package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.User;

public class UserRepositoryHibernateIT {
	
	private static MVCBaseTest baseTest = new MVCBaseTest();
	private static EntityManager entityManager;
	
	
	private UserRepositoryHibernate userRepository;

	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}

	@Before
	public void setUp() {
		baseTest.wipeTablesBeforeTest();
		entityManager = baseTest.getEntityManager();
		userRepository = new UserRepositoryHibernate(entityManager);
	}

	@Test
	public void testSaveUser() {
		entityManager.getTransaction().begin();
		User user = new User("username", "password");
		userRepository.save(user);
		entityManager.getTransaction().commit();

		entityManager.clear();

		User saved = entityManager.find(User.class, user.getId());
		assertThat(saved.getId()).isEqualTo(user.getId());
		assertThat(saved).isEqualTo(user);
	}

	@Test
	public void testUpdateUser() {
		entityManager.getTransaction().begin();
		User user = new User("username", "password");
		entityManager.getTransaction().commit();

		entityManager.clear();

		user.setPassword("newPassword");
		entityManager.getTransaction().begin();
		userRepository.save(user);
		entityManager.getTransaction().commit();
		User saved = entityManager.find(User.class, user.getId());
		assertThat(saved.getId()).isEqualTo(user.getId());
		assertThat(saved).isEqualTo(user);
	}

	@Test
	public void testFindAll() {
		entityManager.getTransaction().begin();
		User user1 = new User("username1", "password1");
		User user2 = new User("username2", "password2");
		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.getTransaction().commit();

		entityManager.clear();

		List<User> allUsers = userRepository.findAll();

		assertThat(allUsers).containsExactlyInAnyOrder(user1, user2);
	}
	
	@Test
	public void testByUsername() {
		entityManager.getTransaction().begin();
		User user = new User("Heisenberg59", "password");
		entityManager.persist(user);
		entityManager.getTransaction().commit();
		
		entityManager.clear();
		
		User saved = userRepository.findByUsername(user.getUsername());
		assertThat(saved).isEqualTo(user);
		assertThat(saved.getId()).isEqualTo(user.getId());
	}
	

}
