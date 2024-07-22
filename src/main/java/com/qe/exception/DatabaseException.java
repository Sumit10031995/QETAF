package com.qe.exception;

@SuppressWarnings("serial")
public class DatabaseException extends Exception{

	public DatabaseException (String str)  
    {  
        // calling the constructor of parent Exception  
        super(str);  
    }  
}
