package com.qe.exception;

public class ReportingUtilException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ReportingUtilException(Exception e) {
		super(e);
	}
	
	public ReportingUtilException(String message) {
		super(message);
	}

}
