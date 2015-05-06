package com.remedeo.remedeoapp.exceptions;

@SuppressWarnings("serial")
public class DuplicateRecordException extends Exception {
	
	public DuplicateRecordException() {
		super();
	}
	
	public DuplicateRecordException(String message) {
		super(message);
	}
	
}
