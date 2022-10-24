package com.ailk.wxserver.service.main.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXMsgHandlerBO;
import com.ailk.wxserver.service.handler.interfaces.WXSignatureCheckHandlerBO;
import com.ailk.wxserver.service.main.interfaces.WXOperateBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

/**
 * 微信处理
 * 
 * @author zhoutj
 *
 */
public class WXOperateBOImpl implements WXOperateBO {

	private static Logger log = LogFactory.getLogger("wxoperate");
	private static Logger error = LogFactory.getLogger("error");

	private WXSignatureCheckHandlerBO wxSignatureCheckHandlerBO;
	
	private WXMsgHandlerBO wxMsgHandlerBO = null;

	@Override
	public void doWeixinOperation(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		LogObj logObj = new LogObj();
		try {
			Map<String, String> paramMap = wxMsgHandlerBO
					.getRequestParam(request);
			String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
			if(StringUtils.isEmpty(sessionid)){
				int logid = (int) (Math.random() * Integer.MAX_VALUE);
				sessionid = String.valueOf(logid);
				paramMap.put(ParamConstant.KEY_SESSIONID, sessionid);
			}
			// 读取参数
			logObj.putSysKey(LogObj.COLLECT, "y")
					.putSysKey(LogObj.SID, sessionid)
					.putSysKey(LogObj.MODULE, "wxmsgoperation")
					.putSysKey(LogObj.STEP, "request");
			logObj.putData("submitType", "post")
					.putData(WXAuthContant.KEY_URL_SIGNATURE,
							paramMap.get(WXAuthContant.KEY_URL_SIGNATURE))
					.putData(WXAuthContant.KEY_URL_ECHOSTR,
							paramMap.get(WXAuthContant.KEY_URL_ECHOSTR))
					.putData(WXAuthContant.KEY_URL_TIMESTAMP,
							paramMap.get(WXAuthContant.KEY_URL_TIMESTAMP))
					.putData(WXAuthContant.KEY_URL_NONCE,
							paramMap.get(WXAuthContant.KEY_URL_NONCE))
					.putData(WXAuthContant.KEY_URL_ENCRYPT_TYPE,
							paramMap.get(WXAuthContant.KEY_URL_ENCRYPT_TYPE))
					.putData(WXAuthContant.KEY_URL_MSG_SIGNATURE,
							paramMap.get(WXAuthContant.KEY_URL_MSG_SIGNATURE))
					.putData(WXAuthContant.KEY_URL_UNCODE,
							paramMap.get(WXAuthContant.KEY_URL_UNCODE))
					.putData(WXAuthContant.KEY_MESSAGE,
							paramMap.get(WXAuthContant.KEY_MESSAGE));
			log.info(logObj);
			String reply_msg = "";
			ResponseResult result = wxMsgHandlerBO.processMsg(paramMap);
			reply_msg = result.getInfo();
			logObj.putSysKey(LogObj.STEP, "response");
			logObj.putData("result", result.getResult())
					.putData("desc", result.getDescription())
					.putData("replymsg", result.getInfo());
			if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
					result.getResult())) {
				log.info(logObj);
			} else {
				log.error(logObj);
			}
			wxMsgHandlerBO.responseMsg(response, reply_msg);
		} catch (IOException e) {
			LogUtil.printErrorStackTrace(error, e);
		}

	}

	@Override
	public void doSignatureCheck(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 读取参数
		Map<String, String> paramMap = wxSignatureCheckHandlerBO
				.getRequestParam(request);
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		if(StringUtils.isEmpty(sessionid)){
			int logid = (int) (Math.random() * Integer.MAX_VALUE);
			sessionid = String.valueOf(logid);
			paramMap.put(ParamConstant.KEY_SESSIONID, sessionid);
		}

		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "wxsignaturecheck")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("submitType", "get")
				.putData(WXAuthContant.KEY_URL_SIGNATURE,
						paramMap.get(WXAuthContant.KEY_URL_SIGNATURE))
				.putData(WXAuthContant.KEY_URL_ECHOSTR,
						paramMap.get(WXAuthContant.KEY_URL_ECHOSTR))
				.putData(WXAuthContant.KEY_URL_TIMESTAMP,
						paramMap.get(WXAuthContant.KEY_URL_TIMESTAMP))
				.putData(WXAuthContant.KEY_URL_NONCE,
						paramMap.get(WXAuthContant.KEY_URL_NONCE))
				.putData(WXAuthContant.KEY_URL_ENCRYPT_TYPE,
						paramMap.get(WXAuthContant.KEY_URL_ENCRYPT_TYPE))
				.putData(WXAuthContant.KEY_URL_MSG_SIGNATURE,
						paramMap.get(WXAuthContant.KEY_URL_MSG_SIGNATURE))
				.putData(WXAuthContant.KEY_URL_UNCODE,
						paramMap.get(WXAuthContant.KEY_URL_UNCODE));
		log.info(logObj);

		ResponseResult result = wxSignatureCheckHandlerBO.signaturecheck(paramMap);
		String reply_msg = result.getInfo();
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("result", result.getResult());
		logObj.putData("desc", result.getDescription());
		logObj.putData("reply_msg", reply_msg);
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		wxSignatureCheckHandlerBO.responseMsg(response, reply_msg);
	}

	public WXSignatureCheckHandlerBO getWxSignatureCheckHandlerBO() {
		return wxSignatureCheckHandlerBO;
	}

	public WXMsgHandlerBO getWxMsgHandlerBO() {
		return wxMsgHandlerBO;
	}

	public void setWxSignatureCheckHandlerBO(
			WXSignatureCheckHandlerBO wxSignatureCheckHandlerBO) {
		this.wxSignatureCheckHandlerBO = wxSignatureCheckHandlerBO;
	}

	public void setWxMsgHandlerBO(WXMsgHandlerBO wxMsgHandlerBO) {
		this.wxMsgHandlerBO = wxMsgHandlerBO;
	}



}
