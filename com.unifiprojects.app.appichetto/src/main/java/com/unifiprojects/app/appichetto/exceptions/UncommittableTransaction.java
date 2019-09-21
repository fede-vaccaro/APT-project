package com.unifiprojects.app.appichetto.exceptions;

public class UncommittableTransaction extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UncommittableTransaction(String msg) {
		super(msg);
	}

	public UncommittableTransaction() {
		super("Unable to commit this transaction.");
	}
}
