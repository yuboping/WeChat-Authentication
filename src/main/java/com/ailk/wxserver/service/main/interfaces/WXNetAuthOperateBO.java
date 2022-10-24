package com.ailk.wxserver.service.main.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXNetAuthOperateBO {

	/**
	 * 微信上网认证
	 * 
	 * @param request
	 * @param response
	 */
	public void doWXNetAuth(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
