package com.ailk.wxserver.service.base.interfaces;


public interface MemCachedBO {

	/**
	 * 内存库set
	 * @param logid
	 * @param key
	 * @param value
	 * @param expired_msec
	 * @return
	 */
	public boolean set(String logid, String key, String value,long expired_msec);
	
	/**
	 * 内存库get
	 * @param paramMap
	 * @param key
	 * @param value
	 * @return
	 */
	public String get(String logid,String key);
	
	/**
	 * 删除内存
	 * @param logid
	 * @param key
	 */
	public void delete(String logid,String key);
}
