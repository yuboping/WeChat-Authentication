package com.ailk.wxserver.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.service.main.interfaces.WXServerOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;

/**
 * 
 * @ClassName: WeiXinServerServlet
 * @Description: TODO(微信认证上网第三方接入接口)
 * @author yubp@asiainfo-sec.com
 * @date 2017年4月13日 上午11:22:09
 *
 */
@SuppressWarnings("serial")
public class WeiXinServerServlet extends HttpServlet {
	private WXServerOperateBO wxServerOperateBO;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		wxServerOperateBO.doWXOperation(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	@Override
	public void init() {
		this.wxServerOperateBO = (WXServerOperateBO) BeanUtil
				.getBean("wxServerOperateBO");
	}
}
