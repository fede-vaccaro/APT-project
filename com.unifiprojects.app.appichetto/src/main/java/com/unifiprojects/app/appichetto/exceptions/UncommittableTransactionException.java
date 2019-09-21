package com.unifiprojects.app.appichetto.exceptions;

public class UncommittableTransactionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UncommittableTransactionException(String msg) {
		super(msg);
	}

	public UncommittableTransactionException() {
		super("Unable to commit this transaction.");
	}
}
