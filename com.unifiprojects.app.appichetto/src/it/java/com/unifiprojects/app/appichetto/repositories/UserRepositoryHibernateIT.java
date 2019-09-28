package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

public class UserRepositoryHibernateIT {

	@Test
	public void test() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("it-persistence-unit");
		assertThat(emf).isNotNull();
	}

}
