package com.qe.exception;

public class TestDataExceptions extends Exception {

    public TestDataExceptions() {
        super();
    }

    public TestDataExceptions(String errorMessage) {
        super(errorMessage);
    }

    public TestDataExceptions(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }
}
