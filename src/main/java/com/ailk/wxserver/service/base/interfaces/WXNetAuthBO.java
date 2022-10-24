package com.ailk.wxserver.service.base.interfaces;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.service.constant.CommonConstant;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.util.FormatUtil;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.log.LogObj;

public interface WXNetAuthBO {

	/**
	 * 校验微信上网认证请求
	 * @param paramMap
	 * @return
	 */
	public boolean validNetAuthReq(Map<String, String> paramMap) ;
	
	
	/**
	 * 校验上网链接有效期
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public boolean checkNetAuthTimestamp(String timestamp) throws Exception;
	
	
	/**
	 * 构造跳转url
	 * @param paramMap
	 * @param redirectUrl
	 * @param finalParamMap
	 * @return
	 */
	public String formRedirectUrl(Map<String, String> paramMap, String redirectUrl,Map<String, String> finalParamMap) ;
	
	
	/**
	 * 构造微信上网url
	 * @param finalParamMap
	 * @return
	 */
	public String formNetAuthUrl(Map<String, String> finalParamMap) ;
}
