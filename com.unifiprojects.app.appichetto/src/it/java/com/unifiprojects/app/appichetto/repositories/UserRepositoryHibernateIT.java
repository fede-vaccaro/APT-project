package com.unifiprojects.app.appichetto.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class UserRepositoryHibernateIT {

	@Test
	public void test() {
		String testMessage = "success";
		System.out.println("TEST MESSAGE IS: " + testMessage);
		assertThat(testMessage).isEqualTo("success");
	}

}
