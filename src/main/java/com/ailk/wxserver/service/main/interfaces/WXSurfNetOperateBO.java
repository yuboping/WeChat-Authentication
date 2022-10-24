package com.ailk.wxserver.service.main.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WXSurfNetOperateBO {
    public void doSurfNetOperation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
}
