package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import java.util.stream.Collectors;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class ReceiptRepositoryHibernate implements ReceiptRepository {

	private EntityManager entityManager;

	public ReceiptRepositoryHibernate(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void saveReceipt(Receipt receipt) {
		entityManager.getTransaction().begin();
		if (receipt.getId() != null) {
			entityManager.merge(receipt);
		} else {
			entityManager.persist(receipt);
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public List<Receipt> getAllUnpaidReceiptsOf(User user) {
		List<Receipt> unpaids = (List<Receipt>) entityManager
				.createQuery("from Accounting where paid=:paid and user=:user", Accounting.class)
				.setParameter("paid", false).setParameter("user", user).getResultList().stream()
				.map(a -> a.getReceipt()).collect(Collectors.toList());
		if (unpaids.isEmpty())
			return null;

		return unpaids;
	}

	@Override
	public List<Receipt> getAllReceiptsBoughtBy(User user) {
		List<Receipt> receiptsBoughtBy = (List<Receipt>) entityManager
				.createQuery("from Receipt where buyer=:buyer", Receipt.class).setParameter("buyer", user)
				.getResultList();
		if (receiptsBoughtBy.isEmpty())
			return null;

		return receiptsBoughtBy;
	}

	@Override
	public Receipt getById(Long id) {
		return entityManager.find(Receipt.class, id);
	}

}