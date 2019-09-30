package com.unifiprojects.app.appichetto.controllers;


import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.unifiprojects.app.appichetto.basetest.MVCBaseTest;
import com.unifiprojects.app.appichetto.models.User;
import com.unifiprojects.app.appichetto.repositories.UserRepository;
import com.unifiprojects.app.appichetto.repositories.UserRepositoryHibernate;
import com.unifiprojects.app.appichetto.transactionhandlers.HibernateTransaction;
import com.unifiprojects.app.appichetto.views.LoginView;

public class LoginControllerIT {
	
	static MVCBaseTest baseTest = new MVCBaseTest();
	EntityManager entityManager;
	LoginController loginController;
	UserRepository userRepository;
	
	@Mock
	LoginView loginView;
	
	@BeforeClass
	public static void setupEntityManager() {
		baseTest.setupEntityManager();
	}

	@AfterClass
	public static void closeEntityManager() {
		baseTest.closeEntityManager();
	}


	@Before
	public void wipeTablesBeforeTest() {
		
		MockitoAnnotations.initMocks(this);
		entityManager = baseTest.getEntityManager();
		userRepository = new UserRepositoryHibernate(entityManager);
		loginController = new LoginController(new HibernateTransaction(entityManager), userRepository, loginView);
	}
	
	@Test
	public void testLoginWhenUserIsExistentAndPasswordIsCorrect() {
		User user = new User("username", "pw");
		
		entityManager.getTransaction().begin();
		entityManager.persist(user);
		entityManager.getTransaction().commit();
		
		loginController.login("username", "pw");
		
		verify(loginView).goToHomePage();
	}
	
	@Test
	public void testSigninWhenUsernameIsNotPicked() {
		String newUsername = "user";
		String newPassword = "pw";
		
		loginController.signIn(newUsername, newPassword);
		
		verify(loginView).goToHomePage();
	}
	
}
