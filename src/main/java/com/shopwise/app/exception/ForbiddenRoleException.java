package com.shopwise.app.exception;

public class ForbiddenRoleException extends RuntimeException {

	public ForbiddenRoleException() {
		super("Seul un administrateur peut créer un compte ADMIN");
	}

}
