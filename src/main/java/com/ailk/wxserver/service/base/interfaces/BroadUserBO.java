package com.ailk.wxserver.service.base.interfaces;

import java.util.Map;

import com.ailk.wxserver.util.ResponseResult;

public interface BroadUserBO {

	/**
	 * 开户
	 * @param paramMap
	 * @return
	 */
	public ResponseResult openUser(Map<String, String> paramMap);
	
	/**
	 * 销户
	 * @param paramMap
	 * @return
	 */
	public ResponseResult closeUser(Map<String, String> paramMap);
	
	
	
	/**
	 * 构造用户名
	 * @param appType
	 * @param ecCode
	 * @param openID
	 * @return
	 */
	public String generateUserName(int appType, String ecCode,
			String openID);
	
	/**
	 * 构造密码
	 * @param username
	 * @return
	 */
	public String generatePwd(String username);
}
