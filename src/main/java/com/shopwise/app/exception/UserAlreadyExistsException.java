package com.shopwise.app.exception;

public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException(String username) {
		super("Le nom d'utilisateur existe déjà : " + username);
	}

}
