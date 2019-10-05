package com.unifiprojects.app.appichetto.transactionhandlers;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;

public class HibernateTransaction implements TransactionHandler {

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	private static final Logger LOGGER = LogManager.getLogger(HibernateTransaction.class);

	
	EntityManager entityManager;

	public HibernateTransaction(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void doInTransaction(TransactionCommands commands) {
		try {
			entityManager.getTransaction().begin();
			commands.execute();
			entityManager.getTransaction().commit();
		} catch (RollbackException re) {
			entityManager.getTransaction().rollback();
			LOGGER.log(Level.ERROR, String.format("Unable to commit transaction: %s", re.getMessage()));
			throw new UncommittableTransactionException("Unable to commit the transaction");
		}

	}

}
