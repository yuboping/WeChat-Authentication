package com.ailk.wxserver.util.log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

	public class LogObj{

	public static final String SYSKEY = "sysKey";
	public static final String DATAKEY = "dataKey";
	public static final String DATA = "data";
	
	public static final String COLLECT = "collect";
	public static final String SID = "sid";
	public static final String MODULE ="module";
	public static final String STEP ="step";
	public static final String BID = "bid";
	
	private Map sysKeyMap = null;
	private Map dataKeyMap = null;
	private Map dataMap = null;
	
	public LogObj() {
		sysKeyMap = new LinkedHashMap();
		dataKeyMap = new LinkedHashMap();
		dataMap = new LinkedHashMap();
	}
	
	public Map getDataKeyMap() {
		return dataKeyMap;
	}
	public void setDataKeyMap(Map dataKeyMap) {
		this.dataKeyMap = dataKeyMap;
	}
	public Map getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map dataMap) {
		this.dataMap = dataMap;
	}
	public Map getSysKeyMap() {
		return sysKeyMap;
	}
	public void setSysKeyMap(Map sysKeyMap) {
		this.sysKeyMap = sysKeyMap;
	}
	/**
	 * 放置系统信息
	 * @param key
	 * @param value
	 */
	public LogObj putSysKey(Object key, Object value) {
		if (null == sysKeyMap) {
			sysKeyMap = new LinkedHashMap();
		}
		sysKeyMap.put(key, value);
		return this;
	}
	public LogObj putSysKey(Object... value) {
		if (null == sysKeyMap) {
			sysKeyMap = new LinkedHashMap();
		}
		put(value,sysKeyMap);
		return this;
	}
	/**
	 * 放置关键数据
	 * @param key
	 * @param value
	 */
	public LogObj putDataKey(Object key, Object value) {
		if (null == dataKeyMap) {
			dataKeyMap = new LinkedHashMap();
		}
		dataKeyMap.put(key, value);
		return this;
	}
	public LogObj putDataKey(Object... value) {
		if (null == dataKeyMap) {
			dataKeyMap = new LinkedHashMap();
		}
		put(value,dataKeyMap);
		return this;
	}
	/**
	 * 放置临时数据
	 * @param key
	 * @param value
	 */
	public LogObj putData(Object key, Object value) {
		if (null == dataMap) {
			dataMap = new LinkedHashMap();
		}
		dataMap.put(key, value);
		return this;
	}
	public LogObj putData(Object... value) {
		if (null == dataMap) {
			dataMap = new LinkedHashMap();
		}
		put(value,dataMap);
		return this;
	}
	
	private void put(Object[] strArray,Map map){
		
		for(int i=0;i<strArray.length;i++){
			if(i%2==1){
				map.put(strArray[i-1], strArray[i]);
			}
		}
		
	}
	
	public void clearDataMap() {
		if (null != dataMap) {
			dataMap.clear();
		}
	}
	
	public void putFromMap(String targetMapName, String[] keys, Map map) {
		Map tempMap = null;
		if (SYSKEY.equals(targetMapName)) {
			tempMap = sysKeyMap;
		}else if (DATAKEY.equals(targetMapName)) {
			tempMap = dataKeyMap;
		}else if (DATA.equals(targetMapName)) {
			tempMap = dataMap;
		}else{
			return;
		}
		for (int i = 0; i < keys.length; i++) {
			tempMap.put(keys[i], map.get(keys[i]));
		}
	}
	public String logFormat() {
		String str = "";
		Map sysKeyMap = this.getSysKeyMap();
		Map dataKeyMap = this.getDataKeyMap();
		Map dataMap = this.getDataMap();
		str += mapToString(sysKeyMap);
		str += "- " + mapToString(dataKeyMap);
		str += mapToString(dataMap);	
		return str;
	}
	
	public String toString(){
		String str = logFormat();
		this.clearDataMap();
		return str;
	}
	
	public String mapToString(Map map) {
		String str = "";
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			str +=entry.getKey()+"["+entry.getValue()+"] ";
		}
		return str;
	}
	
	public static void main(String[] args) {
		LogObj logObj = new LogObj();
		
//		logObj.putSysKey(COLLECT, "y",SID, "1575212356",MODULE, "Auth",STEP, "request").putDataKey("accessIp", "192.168.97.61","username", "test")
//		.putData("wlanuserip", "1.1.1.1","wlanacname", "0000.000.001.12");
//		
//		System.out.println(logObj);
//		
//		logObj.putSysKey(COLLECT, "n");
//		logObj.putData("wlanuserip", "1.1.2.2");
//		logObj.putData("terminalType", "pc");
//		System.out.println(logObj);
		
		LinkedHashMap data = new LinkedHashMap();
		data.put(COLLECT, "y");
		data.put(SID, "1234567890");
		data.put(MODULE, "PreAuth");
		data.put(STEP, "response");
		
		data.put("accessIp", "192.168.97.61");
		data.put("username", "test1");         
		
		data.put("wlanuserip", "1.1.1.1");
		data.put("wlanacname", "0000.000.001.12");
		
		logObj.putFromMap(SYSKEY, new String[]{COLLECT,SID,MODULE,STEP}, data);
		logObj.putFromMap(DATAKEY, new String[]{"accessIp","username"}, data);
		logObj.putFromMap(DATA, new String[]{"wlanuserip","wlanacname"}, data);
		logObj.putData("terminalType", "pc");
		System.out.println(logObj);
		
	}
}
