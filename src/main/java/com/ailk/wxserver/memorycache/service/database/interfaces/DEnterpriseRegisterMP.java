package com.ailk.wxserver.memorycache.service.database.interfaces;

import com.ailk.wxserver.po.DEnterpriseRegister;
public interface DEnterpriseRegisterMP {

	/**
	 * 根据商户注册号REGISTERID获取集团客户信息
	 * 
	 * @param registerid
	 * @return
	 */
	public DEnterpriseRegister getByRegisterid(String registerid);
	
	
	/**
	 * 根据唯一码
	 * @param uncode
	 * @return
	 */
	public DEnterpriseRegister getByUncode(String uncode);
	
	
	/**
	 * 根据商户集客编码获取集团客户信息
	 * 
	 * @param registerid
	 * @return
	 */
	public DEnterpriseRegister getByEccode(String eccode);
	
}
