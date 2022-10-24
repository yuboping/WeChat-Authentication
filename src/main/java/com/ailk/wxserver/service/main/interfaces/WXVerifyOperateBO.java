package com.ailk.wxserver.service.main.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXVerifyOperateBO {

	/**
	 * 微信手机号验证
	 * 
	 * @param request
	 * @param response
	 */
	public void doWXVerify(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
