package com.unifiprojects.app.appichetto.transactionHandlers;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unifiprojects.app.appichetto.controllers.PayViewReceiptsController;
import com.unifiprojects.app.appichetto.exceptions.UncommittableTransactionException;

public class HibernateTransaction implements TransactionHandler {

	private static final Logger LOGGER = LogManager.getLogger(PayViewReceiptsController.class);

	
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
			LOGGER.log(Level.ERROR, "Unable to commit transaction: " + re.getMessage());
			throw new UncommittableTransactionException("Unable to commit the transaction");
		}

	}

}
