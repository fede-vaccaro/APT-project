package com.unifiprojects.app.appichetto.repositories;

public class AlreadyExistentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4244894847707876016L;

	public AlreadyExistentException(String msg) {
		super(msg);
	}
}
