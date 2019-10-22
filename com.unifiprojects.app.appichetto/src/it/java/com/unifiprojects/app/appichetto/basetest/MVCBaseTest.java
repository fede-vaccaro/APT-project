package com.unifiprojects.app.appichetto.basetest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.Inject;

public class MVCBaseTest {

	private EntityManager entityManager;

	public MVCBaseTest() {
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public void setupEntityManager() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("it-persistence-unit");
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	@Inject
	public void setupEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void closeEntityManager() {
		entityManager.close();
		entityManager.getEntityManagerFactory().close();
	}

	public void wipeTablesBeforeTest() {
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("do\n" + "$$\n" + "declare\n" + "  l_stmt text;\n" + "begin\n"
				+ "  select 'truncate ' || string_agg(format('%I.%I', schemaname, tablename), ',')\n"
				+ "    into l_stmt\n" + "  from pg_tables\n" + "  where schemaname in ('public');\n" + "\n"
				+ "  execute l_stmt;\n" + "end;\n" + "$$").executeUpdate();
		entityManager.getTransaction().commit();
		entityManager.clear();
	}

}
