package com.ailk.wxserver.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.service.main.interfaces.WXWifiOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;

/**
 * 微信wifi处理
 * @author zhoutj
 *
 */
@SuppressWarnings("serial")
public class WeiXinWifiServlet extends HttpServlet {
	private WXWifiOperateBO wxWifiOperateBO;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		wxWifiOperateBO.doWXWifi(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	@Override
	public void init() {
		this.wxWifiOperateBO = (WXWifiOperateBO) BeanUtil.getBean("wxWifiOperateBO");
	}
}
