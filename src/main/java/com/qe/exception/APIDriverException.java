package com.qe.exception;

public class APIDriverException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public APIDriverException(Exception e) {
		super(e);
	}
	
	public APIDriverException(String message) {
		super(message);
	}

}
