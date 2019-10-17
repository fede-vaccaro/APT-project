package com.unifiprojects.app.appichetto.repositories;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class ReceiptRepositoryHibernate implements ReceiptRepository {

	public EntityManager entityManager;

	@Inject
	public ReceiptRepositoryHibernate(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void saveReceipt(Receipt receipt) {
		if (receipt.getId() != null) {
			entityManager.merge(receipt);
		} else {
			entityManager.persist(receipt);
		}
		
		receipt.getAccountings().forEach(entityManager::merge);
	}

	@Override
	public List<Receipt> getAllUnpaidReceiptsOf(User user) {
		return entityManager.createQuery("from Accounting where amount!=:amount and user=:user", Accounting.class)
				.setParameter("amount", 0.0).setParameter("user", user).getResultList().stream()
				.map(Accounting::getReceipt).collect(Collectors.toList());

	}

	@Override
	public List<Receipt> getAllReceiptsBoughtBy(User user) {
		return entityManager.createQuery("from Receipt where buyer=:buyer", Receipt.class).setParameter("buyer", user)
				.getResultList();

	}

	@Override
	public Receipt getById(Long id) {
		return entityManager.find(Receipt.class, id);
	}

	@Override
	public void removeReceipt(Receipt receipt) {
		Receipt toBeRemoved = receipt;
		if (!entityManager.contains(receipt)) {
			toBeRemoved = entityManager.merge(receipt);
		}


		entityManager.remove(toBeRemoved);

	}

}
