package com.ailk.wxserver.util;

import java.util.Random;

import org.apache.log4j.Logger;

public class LogUtil {
	
    public static void printErrorStackTrace(Logger log,Throwable e){
		log.error(e.toString());
		StackTraceElement[] stackTrace = e.getStackTrace();
		for(int i = 0;i < stackTrace.length;i++){
			log.error(stackTrace[i].toString());
		}
	}
    
    public static Long getSerialId(){
		Random r = new Random();
		return new Long(Math.abs(new Integer(r.nextInt()).longValue()));
    }
    
}
