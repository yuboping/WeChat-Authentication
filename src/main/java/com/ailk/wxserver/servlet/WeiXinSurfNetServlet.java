package com.ailk.wxserver.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.wxserver.service.main.interfaces.WXSurfNetOperateBO;
import com.ailk.wxserver.util.spring.BeanUtil;

/**
 * 
 * @ClassName: WeiXinSurfNetServlet
 * @Description: TODO(第三方一键上网菜单入口)
 * @author yubp@asiainfo-sec.com
 * @date 2017年8月18日 下午3:20:54
 *
 */
@SuppressWarnings("serial")
public class WeiXinSurfNetServlet extends HttpServlet {
    private WXSurfNetOperateBO wxSurfNetOperateBO;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        wxSurfNetOperateBO.doSurfNetOperation(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.doGet(request, response);
    }

    @Override
    public void init() {
        this.wxSurfNetOperateBO = (WXSurfNetOperateBO) BeanUtil.getBean("wxSurfNetOperateBO");
    }
}
