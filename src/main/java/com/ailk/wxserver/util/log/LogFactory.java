package com.ailk.wxserver.util.log;

import org.apache.log4j.Logger;

public class LogFactory
{
    public LogFactory()
    {
    }

    private static String logpath;

    static
    {
    	try {
//			logpath = MPClientUtils.callService("systemConfigMP#getConfigPath",
//					new Object[]{"log_config"}, String.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
        //System.out.println("log4j xml path="+logpath);
        //获取日志记录器配置文件属性.
//        DOMConfigurator.configure(logpath);
    }
    
    public static Logger getLogger(Class c){
    	return Logger.getLogger(c);
    }
    
    public static Logger getLogger(String logName){
    	return Logger.getLogger(logName);
    }


}
