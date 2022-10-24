package com.ailk.wxserver.service.main.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXNetAuthHandlerBO;
import com.ailk.wxserver.service.main.interfaces.WXNetAuthOperateBO;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class WXNetAuthOperateBOImpl implements WXNetAuthOperateBO {

	private Logger log = LogFactory.getLogger("wxnetauthoperate");
	
	private WXNetAuthHandlerBO wxNetAuthHandlerBO;
	
	@Override
	public void doWXNetAuth(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 浏览器cache处理
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", -10);

		String reqIp = request.getRemoteAddr();
		Map<String, String> paramMap = wxNetAuthHandlerBO.getRequestParam(request);
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		if (StringUtils.isEmpty(sessionid)) {
			int logid = (int) (Math.random() * Integer.MAX_VALUE);
			sessionid = String.valueOf(logid);
			paramMap.put(ParamConstant.KEY_SESSIONID, sessionid);
		}

		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "wxnetauth")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("reqIp", reqIp).putData("paramMap", paramMap);
		log.info(logObj);

		Map<String, String> result = wxNetAuthHandlerBO.doNetAuthReq(paramMap);
		String resultcode = result.get(ParamConstant.KEY_RESULTCODE);
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("result", result);

		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(resultcode)) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		String info = result.get(ParamConstant.KEY_INFO);
		String redirectUrl = result.get(ParamConstant.KEY_REDIRCTURL);
		wxNetAuthHandlerBO.response(response, info, redirectUrl);
	}

	public WXNetAuthHandlerBO getWxNetAuthHandlerBO() {
		return wxNetAuthHandlerBO;
	}

	public void setWxNetAuthHandlerBO(WXNetAuthHandlerBO wxNetAuthHandlerBO) {
		this.wxNetAuthHandlerBO = wxNetAuthHandlerBO;
	}
}
