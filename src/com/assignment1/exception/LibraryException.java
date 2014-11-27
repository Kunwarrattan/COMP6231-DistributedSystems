package com.assignment1.exception;

import com.assignment1.config.Configuration;

public class LibraryException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int code = Configuration.DEFAULT_EXCEPTION_CODE;
	
    public LibraryException(int code, String message)
    {
        super(message);
        this.code = code;
    }
    
    public LibraryException(String message)
    {
        super(message);
    }
}
