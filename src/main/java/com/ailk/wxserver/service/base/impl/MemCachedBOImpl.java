package com.ailk.wxserver.service.base.impl;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.wxserver.service.base.interfaces.MemCachedBO;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.danga.MemCached.MemCachedClient;

public class MemCachedBOImpl implements MemCachedBO {

	public static final Logger log = LogFactory.getLogger("memcached");

	private MemCachedClient memCachedClient;

	@Override
	public boolean set(String logid, String key, String value,long expired_msec) {
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, logid)
				.putSysKey(LogObj.MODULE, "memcachedset")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("key", key).putData("value", value)
				.putData("expired_msec", expired_msec);
		log.info(logObj);

		boolean result = memCachedClient
				.set(key, value, new Date(expired_msec));
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("result", result);
		log.info(logObj);
		
		return result;
	}

	@Override
	public String get(String logid,String key) {
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, logid)
				.putSysKey(LogObj.MODULE, "memcachedget")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("key", key);
		log.info(logObj);

		String value = (String) memCachedClient.get(key);
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("value", value);
		log.info(logObj);
		return value;
	}
	
	@Override
	public void delete(String logid, String key) {
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, logid)
				.putSysKey(LogObj.MODULE, "memcacheddelete")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("key", key);
		log.info(logObj);

		boolean result = memCachedClient.delete(key);
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("result", result);
		log.info(logObj);
	}
	
	public MemCachedClient getMemCachedClient() {
		return memCachedClient;
	}

	public void setMemCachedClient(MemCachedClient memCachedClient) {
		this.memCachedClient = memCachedClient;
	}

}
