package com.ailk.wxserver.service.main.interfaces;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RefreshProcessBO {
	public void doRefresh(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;
	
	public String list(String operateName);
	
	public boolean reload(String operateName,String paramterName, boolean bRefresh) ;
}
