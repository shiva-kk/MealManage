package com.mealManage.exception;

public class DatabaseErrorException extends RuntimeException {

    public DatabaseErrorException(String message) {
        super(message);
    }
}
