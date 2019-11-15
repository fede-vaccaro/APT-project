package com.unifiprojects.app.appichetto.modules;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.google.inject.AbstractModule;

public class EntityManagerModule extends AbstractModule {
	@Override
	protected void configure() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("it-persistence-unit");
		EntityManager em = emf.createEntityManager();
		bind(EntityManager.class).toProvider(() -> em);
	}
}
