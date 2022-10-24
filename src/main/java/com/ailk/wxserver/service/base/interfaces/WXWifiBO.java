package com.ailk.wxserver.service.base.interfaces;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.ailk.wxserver.po.DEnterpriseRegister;

public interface WXWifiBO {

	/**
	 * 构造extend参数
	 * 格式:eccode,uniqueid
	 * @param paramMap
	 * @param logObj
	 * @return
	 */
	public String formExtend(Map<String, String> paramMap);
	
	/**
	 * 解析extend数据
	 * @param paramMap
	 * @param extend
	 * @param logObj
	 */
	public Map<String, String> parseExtend(String extend);
	
	/**
	 * 是否读取cookie
	 * @param paramMap
	 * @return
	 */
	public boolean isReadCookie(Map<String, String> paramMap);
	
	
	/**
	 * 是否写cookie
	 * @param paramMap
	 * @return
	 */
	public boolean isWriteCookie(Map<String, String> paramMap);
	
	
	/**
	 * wifi签名
	 * @param paramMap
	 * @param der
	 * @return
	 */
	public Map<String, String> wifisign(Map<String, String> paramMap,DEnterpriseRegister der) throws NoSuchAlgorithmException;
	
	
	/**
	 * 构造wifi的内存库 key
	 * @param uniqueid--唯一码
	 * @return
	 */
	public String formWifiMemKey(String uniqueid);
	
	/**
	 * 构造wifi的数据
	 * @param paramMap
	 * @return
	 */
	public String formWifiMemData(Map<String, String> paramMap);
	
	/**
	 * 解析wifidata
	 * @param wifidata
	 * @return
	 */
	public Map<String, String> parseMemWifiData(String wifidata);
	
	/**
	 * 校验wifcheck请求
	 * @param paramMap
	 * @return
	 */
	public boolean validWificheckreq(Map<String, String> paramMap);
	
	/**
	 * 构造跳转url
	 * @param paramMap
	 * @param redirectUrl
	 * @param finalParamMap
	 * @return
	 */
	public String formRedirectUrl(Map<String, String> paramMap, String redirectUrl,Map<String, String> finalParamMap) ;
	
	/**
	 * wifi连接pc需要的参数
	 * @param paramMap
	 * @param der
	 * @return
	 */
	public Map<String, String> wifiPc(Map<String, String> paramMap,DEnterpriseRegister der) throws NoSuchAlgorithmException;


}
