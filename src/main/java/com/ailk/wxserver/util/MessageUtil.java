package com.ailk.wxserver.util;

import java.util.Map;

import com.ailk.lcims.support.mp.client.MPClientUtils;

public class MessageUtil {
	
	public static String getMessage(String funcName,String result){
		return MPClientUtils.callService(
				"/mp/portal/config/messageshow/getMessage",
				new Object[] { funcName,result}, String.class);
		
	}
	
	/**
	 * 根据功能名和结果集获取提示信息
	 * 
	 * @param funcName
	 * @param map
	 * @return
	 */
	public static String getMessage(String funcName,Map<String,String> map){
		String result=map.get("result");
		
		String message=getMessage(funcName, result);
		
		try {
			message=StringUtils.placeHolderReform(1, message,map);
		} catch (Exception e) {
			return message;
		}
		return message;
	}
	
	
	public static String getMessage(String funcName,String defaultFuncName,String result){
		return MPClientUtils.callService(
				"/mp/portal/config/messageshow/getSpecialMessage",
				new Object[] { funcName,defaultFuncName,result}, String.class);
		
	}
	
	/**
	 * 先根据功能名和结果集获取提示信息,查不到再根据默认功能名查找
	 * 
	 * @param funcName
	 * @param map
	 * @return
	 */
	public static String getMessage(String funcName,String defaultFuncName,Map<String,String> map){
		String result=map.get("result");
		
		String message=getMessage(funcName, defaultFuncName, result);
		
		try {
			message=StringUtils.placeHolderReform(1, message,map);
		} catch (Exception e) {
			return message;
		}
		return message;
	}
}
