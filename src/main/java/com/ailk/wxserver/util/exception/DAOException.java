package com.ailk.wxserver.util.exception;

public class DAOException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6956390727854421070L;

	
	public DAOException(String msg,Throwable throwable){
		super(msg, throwable);
	}
}
