package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.unifiprojects.app.appichetto.exceptions.AlreadyExistentException;
import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.Item;
import com.unifiprojects.app.appichetto.models.Receipt;
import com.unifiprojects.app.appichetto.models.User;

public class UserRepositoryHibernate implements UserRepository {

	private EntityManager entityManager;
	private static final Logger LOGGER = LogManager.getLogger(UserRepositoryHibernate.class);

	@Inject
	public UserRepositoryHibernate(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void save(User user) {
		if ((user.getUsername().trim()).equals(""))
			throw new IllegalArgumentException("You can't use empty string username.");

		User userWithSameName = this.findByUsername(user.getUsername());

		if (userWithSameName != null && !userWithSameName.getId().equals(user.getId())) {
			throw new AlreadyExistentException(
					String.format("Username %s has been already picked.", user.getUsername()));

		}
		if (user.getId() != null) {
			entityManager.merge(user);
		} else {
			entityManager.persist(user);
		}
	}

	@Override
	public User findById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public List<User> findAll() {
		return entityManager.createQuery("from users", User.class).getResultList();
	}

	@Override
	public User findByUsername(String username) {
		try {
			return entityManager.createQuery("from users where username = :username", User.class)
					.setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			LOGGER.debug(String.format("User with username %s not found.", username), e);
			return null;
		}
	}

	@Override
	public void removeUser(User user) {
		User toBeRemoved = user;
		if (!entityManager.contains(toBeRemoved)) {
			toBeRemoved = entityManager.merge(toBeRemoved);
		}

		deleteBoughtReceipts(toBeRemoved);

		deleteAccountings(toBeRemoved);

		deleteItems(toBeRemoved);

		entityManager.remove(toBeRemoved);
	}

	void deleteAccountings(User toBeRemoved) {
		List<Accounting> userAccountings = entityManager
				.createQuery("from Accounting where user=:user", Accounting.class).setParameter("user", toBeRemoved)
				.getResultList();

		userAccountings.stream().forEach(a -> {
			Receipt receipt = a.getReceipt();
			entityManager.detach(receipt);

			receipt.removeAccounting(a);

			entityManager.merge(receipt);

			entityManager.remove(a);
		});
	}

	void deleteBoughtReceipts(User toBeRemoved) {
		List<Receipt> receiptsBoughtByUser = entityManager.createQuery("from Receipt where buyer=:buyer", Receipt.class)
				.setParameter("buyer", toBeRemoved).getResultList();

		receiptsBoughtByUser.stream().forEach(entityManager::remove);
	}

	void deleteItems(User deletingUser) {
		List<Item> ownedItems = entityManager
				.createQuery("select i from Item i join i.owners u where u=:user", Item.class)
				.setParameter("user", deletingUser).getResultList();

		ownedItems.forEach(item -> {
			item.removeOwner(deletingUser);
			entityManager.merge(item);
		});
	}
}
