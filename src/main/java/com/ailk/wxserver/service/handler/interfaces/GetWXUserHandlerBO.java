package com.ailk.wxserver.service.handler.interfaces;

import java.util.Map;

import com.ailk.wxserver.util.ResponseResult;

/**
 * 查询微信用户信息处理
 * @author zhoutj
 *
 */
public interface GetWXUserHandlerBO {

	/**
	 * 根据微信宽带账号提取用户手机号码
	 * @param paramMap
	 * @return
	 */
	public ResponseResult getPhoneByUserName(Map<String, String> paramMap);
}
