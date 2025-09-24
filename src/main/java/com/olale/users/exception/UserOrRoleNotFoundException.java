package com.olale.users.exception;

public class UserOrRoleNotFoundException extends RuntimeException {

    public UserOrRoleNotFoundException(String message) {
        super(message);
    }
}
