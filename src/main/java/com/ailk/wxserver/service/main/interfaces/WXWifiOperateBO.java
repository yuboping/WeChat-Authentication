package com.ailk.wxserver.service.main.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXWifiOperateBO {

	/**
	 * 微信Wifi
	 * 
	 * @param request
	 * @param response
	 */
	public void doWXWifi(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
