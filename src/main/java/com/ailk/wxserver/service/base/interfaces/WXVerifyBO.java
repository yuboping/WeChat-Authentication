package com.ailk.wxserver.service.base.interfaces;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXReqTypeConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogObj;

public interface WXVerifyBO {

	/**
	 * 校验获取验证码请求
	 * 
	 * @param paramMap
	 * @return
	 */
	public boolean validGetVerifyCodeReq(Map<String, String> paramMap);
	
	
	/**
	 * 下发验证短信
	 * 
	 * @param paramMap
	 * @return
	 * @throws DAOException
	 * @throws Exception
	 */
	public ResponseResult sendVerifySms(Map<String, String> paramMap) throws DAOException,
			Exception ;
	
	
	/**
	 * 校验验证请求
	 * 
	 * @param paramMap
	 * @return
	 */
	public boolean validVerifyReq(Map<String, String> paramMap);
	
	
	/**
	 * 构造内存库key
	 * @return
	 */
	public String formMemKey(String verifytype,String eccode,String openid,String verifycode) ;
	
	/**
	 * 获取内存库数据
	 * @param memkey
	 * @return
	 */
	public Map<String, String> getMemValue(Map<String, String> paramMap) ;
	
	/**
	 * 构造跳转url
	 * @param paramMap
	 * @param redirectUrl
	 * @param finalParamMap
	 * @return
	 */
	public String formRedirectUrl(Map<String, String> paramMap, String redirectUrl,Map<String, String> finalParamMap) ;
	
}
