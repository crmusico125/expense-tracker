package com.github.crmusico125.expensetracker.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
}
