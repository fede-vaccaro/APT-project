package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import javax.persistence.EntityManager;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.User;

public class AccountingRepositoryHibernate implements AccountingRepository {

	private EntityManager entityManager;
	
	public AccountingRepositoryHibernate(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void saveAccounting(Accounting accounting) {
		entityManager.merge(accounting);
	}

	@Override
	public List<Accounting> getAccountingsOf(User user) {
		return entityManager.createQuery("from Accounting where user = :user", Accounting.class)
				.setParameter("user", user).getResultList();
	}

	@Override
	public Accounting getById(long id) {
		return entityManager.find(Accounting.class, id);
	}

}
