package com.ailk.wxserver.service.handler.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.model.WXMsgModel;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.base.interfaces.WXMsgBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXMsgHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class WXMsgHandlerBOImpl implements WXMsgHandlerBO {

	private Logger log = LogFactory.getLogger("wxmsghandler");
	private Logger error = LogFactory.getLogger("error");
	
	private WXMsgBO wxMsgBO;

	@Override
	public Map<String, String> getRequestParam(HttpServletRequest request)
			throws IOException {
		// 获取url中参数
		String signature = request
				.getParameter(WXAuthContant.KEY_URL_SIGNATURE);
		String echostr = request.getParameter(WXAuthContant.KEY_URL_ECHOSTR);
		String timestamp = request
				.getParameter(WXAuthContant.KEY_URL_TIMESTAMP);
		String nonce = request.getParameter(WXAuthContant.KEY_URL_NONCE);
		String encrypt_type = request
				.getParameter(WXAuthContant.KEY_URL_ENCRYPT_TYPE);
		String msg_signature = request
				.getParameter(WXAuthContant.KEY_URL_MSG_SIGNATURE);
		String uncode = request.getParameter(WXAuthContant.KEY_URL_UNCODE);

		// 获取post消息体
		String message = wxMsgBO.getRequestPostMsg(request);

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(WXAuthContant.KEY_URL_SIGNATURE, signature);
		paramMap.put(WXAuthContant.KEY_URL_ECHOSTR, echostr);
		paramMap.put(WXAuthContant.KEY_URL_TIMESTAMP, timestamp);
		paramMap.put(WXAuthContant.KEY_URL_NONCE, nonce);
		paramMap.put(WXAuthContant.KEY_URL_ENCRYPT_TYPE, encrypt_type);
		paramMap.put(WXAuthContant.KEY_URL_MSG_SIGNATURE, msg_signature);
		paramMap.put(WXAuthContant.KEY_URL_UNCODE, uncode);
		paramMap.put(WXAuthContant.KEY_MESSAGE, message);
		return paramMap;
	}

	@Override
	public void responseMsg(HttpServletResponse response, String reply_msg)
			throws IOException {
		response.getWriter().print(reply_msg);
	}

	@Override
	public ResponseResult processMsg(Map<String, String> paramMap) {
		ResponseResult result = new ResponseResult();
		result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get("sessionid"))
				.putSysKey(LogObj.MODULE, "msgoperate")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("paramMap", paramMap);
		log.info(logObj);
		
		result = prepareMsg(paramMap, logObj);
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			WXMsgModel wxMsgModel = (WXMsgModel) result.getObject();
			logObj.putSysKey(LogObj.STEP, "parparemsg");
			logObj.putData("toUserName", wxMsgModel.getToUserName())
					.putData("fromUserName", wxMsgModel.getFromUserName())
					.putData("createTime", wxMsgModel.getCreateTime())
					.putData("content", wxMsgModel.getContent())
					.putData("msgType", wxMsgModel.getMsgType())
					.putData("event", wxMsgModel.getEvent());
			log.info(logObj);
			
			result = handleMsg(paramMap, wxMsgModel,logObj);
			String replymsg = result.getInfo();
			result = formReplyMsg(paramMap, replymsg, wxMsgModel, logObj);
		}
		logObj.putSysKey(LogObj.STEP,"response");
		logObj.putData("result", result.getResult());
		logObj.putData("desc", result.getDescription());
		logObj.putData("info",result.getInfo());
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			log.info(logObj);
		}else{
			log.error(logObj);
		}
		return result;
	}

	/**
	 * 构造回应消息体
	 * 先构造明文消息，再加密
	 * @param paramMap
	 * @param replymsg
	 * @param logObj
	 * @param wxMsgModel
	 * @return
	 */
	public ResponseResult formReplyMsg(Map<String, String> paramMap,String replymsg, WXMsgModel wxMsgModel, LogObj logObj) {
		String reply_plaintxt = "";
		if(StringUtils.isEmpty(replymsg)){
			reply_plaintxt = "success";
		}else{
			reply_plaintxt = wxMsgBO.formReplyPlainMsg(wxMsgModel,replymsg);
		}
		logObj.putSysKey(LogObj.STEP,"replymsg");
		logObj.putData("reply_plaintxt",reply_plaintxt);
		log.info(logObj);
		ResponseResult result = null;
		if(StringUtils.isEmpty(reply_plaintxt) || "success".equalsIgnoreCase(reply_plaintxt)){
			result = new ResponseResult();
			result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			result.setDescription("reply msg is empty,direct reply");
			result.setInfo(reply_plaintxt);
			
		}else{
			result = wxMsgBO.encryptReplyMsg(paramMap,wxMsgModel, reply_plaintxt);
		}
		return result;
	}
	
	/**
	 * 具体处理消息
	 * @param paramMap
	 * @param wxMsgModel
	 * @param logObj
	 * @return
	 */
	private ResponseResult handleMsg(Map<String, String> paramMap,WXMsgModel wxMsgModel,LogObj logObj){
		ResponseResult result = new ResponseResult();
		String verify_cmd =  MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "verify_cmd" }, String.class);
		String link_cmd =  MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "link_cmd" }, String.class);
		if (WXAuthContant.MSGTYPE_TEXT.equals(wxMsgModel.getMsgType())) {
			//文本消息
			String content = wxMsgModel.getContent();
			if(content.matches(verify_cmd)){
				String phone = wxMsgBO.getPhone(content, verify_cmd);
				paramMap.put(ParamConstant.KEY_PHONE, phone);
				//手机号验证消息
				result = wxMsgBO.msg_bindphone(paramMap, wxMsgModel);
			}else if(content.matches(link_cmd)){
			//获取上网链接消息
				result = wxMsgBO.msg_getlink(paramMap, wxMsgModel);
			}else{
			//其他文本消息
				result = wxMsgBO.msg_other(paramMap, wxMsgModel);
			}
		}else if(WXAuthContant.MSGTYPE_EVENT.equals(wxMsgModel
				.getMsgType())){
			// 事件推送
			String event = wxMsgModel.getEvent();
			if (WXAuthContant.EVENT_SUBSCRIBE.equals(event)) {
				// 订阅,包括微信扫描
				result = wxMsgBO.event_subscribe(paramMap, wxMsgModel);
			} else if (WXAuthContant.EVENT_UNSUBSCRIBE.equals(event)) {
				//取消订阅
				result = wxMsgBO.event_unsubscribe(paramMap, wxMsgModel);
			}else if(WXAuthContant.EVENT_CLICK.equals(event)){
				//按钮菜单点击事件
				String eventkey = wxMsgModel.getEventKey();
				DEnterpriseRegister der = MPClientUtils.callService(
						"/mp/portal/dEnterpriseRegister/getByRegisterid",
						new Object[] { wxMsgModel.getToUserName() },
						DEnterpriseRegister.class);
				boolean isMenuAuth = false;
				if(der != null){
                    int status = der.getStatus();
                    if (status != ParamConstant.KEY_STATUS_PAUSE) {
                        String eccode = der.getEccode();
                        String menuKey = MPClientUtils.callService(
                                "/mp/portal/config/business/getConfig", new Object[] {
                                        "auth_from_menu", eccode }, String.class);
                        if (menuKey != null && menuKey.equals(eventkey)) {
                            isMenuAuth = true;
                        }
                    }
				}
				//如果匹配到从菜单点击来认证上网的事件,或者key值就是8，那么做认证操作
				if(eventkey.matches(link_cmd) || isMenuAuth){
					result = wxMsgBO.msg_getlink(paramMap, wxMsgModel);
				}
				else{
					result = wxMsgBO.event_other(paramMap, wxMsgModel);
				}
			}else if(WXAuthContant.EVENT_WIFICONNECTED.equals(event)){
				result = wxMsgBO.event_wifiConnected(paramMap, wxMsgModel);
			}else{
				//其他事件
				result = wxMsgBO.event_other(paramMap, wxMsgModel);
			}
		}
		logObj.putSysKey(LogObj.STEP,"msghandle");
		logObj.putData("result", result.getResult());
		logObj.putData("desc", result.getDescription());
		logObj.putData("info",result.getInfo());
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			log.info(logObj);
		}else{
			log.error(logObj);
		}
		return result;
		
	}
	
	/**
	 * 预处理消息，先解密，然后再解析成WXMsgModel对象
	 * @param paramMap
	 * @param logObj
	 * @return
	 */
	public ResponseResult prepareMsg(Map<String, String> paramMap, LogObj logObj) {
		ResponseResult result;
		result = wxMsgBO.decryptMsg(paramMap);
		if(String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())){
			logObj.putSysKey(LogObj.STEP,"decryptmsg");
			String message = paramMap.get(WXAuthContant.KEY_MESSAGE);
			logObj.putData("message",message);
			log.info(logObj);
			try {
				WXMsgModel wxMsgModel = wxMsgBO.parseMessage(message);
				result.setObject(wxMsgModel);
			} catch (IOException e) {
				LogUtil.printErrorStackTrace(error, e);
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_INVALID_MSG));
				result.setDescription("parse message to model exception");
				result.setInfo(WXResultConstant
						.getMsgByResultCode(WXResultConstant.RESULT_INVALID_MSG));
			}
		}
		return result;
	}

	public WXMsgBO getWxMsgBO() {
		return wxMsgBO;
	}

	public void setWxMsgBO(WXMsgBO wxMsgBO) {
		this.wxMsgBO = wxMsgBO;
	}

}
