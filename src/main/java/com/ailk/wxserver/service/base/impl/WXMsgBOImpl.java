package com.ailk.wxserver.service.base.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.model.WXMsgModel;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.base.interfaces.WXMsgBO;
import com.ailk.wxserver.service.base.interfaces.WXNetAuthBO;
import com.ailk.wxserver.service.base.interfaces.WXVerifyBO;
import com.ailk.wxserver.service.constant.CommonConstant;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.UserConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXMsgCodeConstant;
import com.ailk.wxserver.service.constant.WXReqTypeConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.util.FormatUtil;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.MessageUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.SystemUtil;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.ailk.wxserver.util.qq.wx.AesException;
import com.ailk.wxserver.util.qq.wx.WXBizMsgCrypt;
import com.ailk.wxserver.util.qq.wx.WXUtil;
import com.asiainfo.lcims.lcbmi.weixin.server.BindWXUserReq;
import com.asiainfo.lcims.lcbmi.weixin.server.BindWXUserResp;
import com.asiainfo.lcims.lcbmi.weixin.server.WXServiceProxy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class WXMsgBOImpl implements WXMsgBO {

	private Logger log = LogFactory.getLogger("wxmsg");
	private Logger error = LogFactory.getLogger("error");
	
	private SpecialUserBO specialUserBO;
	private WXVerifyBO wxVerifyBO;
	private WXNetAuthBO wxNetAuthBO;
	
	@Override
	public String getRequestPostMsg(HttpServletRequest request) throws IOException {
		// 处理接收消息
		InputStream in = request.getInputStream();
		// 将流转换为字符串
		StringBuilder xmlMsg = new StringBuilder();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			xmlMsg.append(new String(b, 0, n, "UTF-8"));
		}
		return xmlMsg.toString();
	}

	@Override
	public ResponseResult decryptMsg(Map<String, String> paramMap) {
		ResponseResult result = new ResponseResult();

		String encrypt_type = paramMap.get(WXAuthContant.KEY_URL_ENCRYPT_TYPE);
		String message = paramMap.get(WXAuthContant.KEY_MESSAGE);
		String timestamp = paramMap.get(WXAuthContant.KEY_URL_TIMESTAMP);
		String nonce = paramMap.get(WXAuthContant.KEY_URL_NONCE);
		String msg_signature = paramMap
				.get(WXAuthContant.KEY_URL_MSG_SIGNATURE);
		String uncode = paramMap.get(WXAuthContant.KEY_URL_UNCODE);
		if (WXAuthContant.WX_ENCRYPT_TYPE_AES.equals(encrypt_type)) {
			DEnterpriseRegister der = MPClientUtils.callService(
					"/mp/portal/dEnterpriseRegister/getByUncode",
					new Object[] { uncode }, DEnterpriseRegister.class);
			if (der == null) {
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
				result.setDescription("no exist DEenterpriseRegister by uncode:"
						+ uncode);
				result.setInfo(WXResultConstant
						.getMsgByResultCode(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
				return result;
			}
            int status = der.getStatus();
            if(status == ParamConstant.KEY_STATUS_PAUSE){
                result.setResult(String
                        .valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
                result.setDescription("DEenterpriseRegister status is pause:" + status);
                result.setInfo(WXResultConstant
                        .getMsgByResultCode(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
                return result;
            }
			WXBizMsgCrypt wbmc;
			try {
				wbmc = new WXBizMsgCrypt(der.getToken(), der.getAeskey(),
						der.getAppid());
				String plainMsgXML = wbmc.decryptMsg(msg_signature, timestamp,
						nonce, message);
				paramMap.put(WXAuthContant.KEY_MESSAGE, plainMsgXML);
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_SUCCESS));
				result.setDescription("decript message success");
			} catch (AesException e) {
				result.setResult(String.valueOf(e.getCode()));
				result.setDescription(e.getMessage());
				result.setInfo(e.getMessage());
			}
		} else {
			result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			result.setDescription("message don't need prepare");
			result.setInfo(message);
		}
		return result;
	}

	@Override
	public WXMsgModel parseMessage(String message) throws IOException {
		// 将POST流转换为XStream对象
		XStream xs = new XStream(new DomDriver());
		// 将指定节点下的xml节点数据映射为对象
		xs.alias("xml", WXMsgModel.class);

		return (WXMsgModel) xs.fromXML(message);
	}

	@Override
	public String getPhone(String content,String regex){
		String phone = "";
		Pattern pattern= Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()){
			phone = matcher.group(1);
		}
		return phone;
	}
	
	@Override
	public String formReplyPlainMsg(WXMsgModel wxMsgModel, String content) {
		StringBuffer msg = new StringBuffer();
		msg.append("<xml>");
		msg.append("<ToUserName><![CDATA[");
		msg.append(wxMsgModel.getFromUserName());
		msg.append("]]></ToUserName>");
		msg.append("<FromUserName><![CDATA[");
		msg.append(wxMsgModel.getToUserName());
		msg.append("]]></FromUserName>");
		msg.append("<CreateTime>");
		msg.append(wxMsgModel.getCreateTime());
		msg.append("</CreateTime>");
		msg.append("<MsgType><![CDATA[");
		msg.append(WXAuthContant.MSGTYPE_TEXT);
		msg.append("]]></MsgType>");
		msg.append("<Content><![CDATA[");
		msg.append(content);
		msg.append("]]></Content>");
		msg.append("</xml>");

		return msg.toString();
	}

	@Override
	public ResponseResult encryptReplyMsg(Map<String, String> paramMap,
			WXMsgModel wxMsgModel, String replymsg) {
		ResponseResult result = new ResponseResult();
		String timestamp = paramMap.get(WXAuthContant.KEY_URL_TIMESTAMP);
		String nonce = paramMap.get(WXAuthContant.KEY_URL_NONCE);
		String encrypt_type = paramMap
				.get(WXAuthContant.KEY_URL_ENCRYPT_TYPE);
		if(WXAuthContant.WX_ENCRYPT_TYPE_AES.equals(encrypt_type)){
			DEnterpriseRegister der = MPClientUtils.callService(
					"/mp/portal/dEnterpriseRegister/getByRegisterid",
					new Object[] { wxMsgModel.getToUserName() },
					DEnterpriseRegister.class);
			try {
				WXBizMsgCrypt wbmc = new WXBizMsgCrypt(der.getToken(), der.getAeskey(), der.getAppid());
				String encryReplyMsg = wbmc.encryptMsg(replymsg, timestamp, nonce);
				result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
				result.setDescription("encrypt reply message success");
				result.setInfo(encryReplyMsg);
			} catch (AesException e) {
				result.setResult(String.valueOf(e.getCode()));
				result.setDescription(e.getMessage());
				result.setInfo(e.getMessage());
			}
		}else{
			result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			result.setDescription("not need encry,reply plain txt");
			result.setInfo(replymsg);
		}
		return result;
	}
	
	/**
	 * 初始替换map
	 * @return
	 */
	private Map<String, String> initPlaceMap(){
		Map<String,String> placeMap = new HashMap<String, String>();
		placeMap.put(CommonConstant.PLACE_NEWLINE, "\n");
		String timeStr = SystemUtil.getDate(new Date(), "HH:mm");
		placeMap.put(CommonConstant.PLACE_TIME, timeStr);
		return placeMap;
	}
	
	/**
	 * 从配置中根据回应消息码构造回应消息
	 * @param result
	 * @param replyMsg_code
	 * @param placeMap
	 * @return
	 */
	private Map<String, String> getReplyMsg(ResponseResult result,int replyMsg_code,String eccode,Map<String, String> placeMap){
		Map<String, String> resultMap = new HashMap<String, String>();
		String replyMsg = "";
		try {
			replyMsg = MessageUtil.getMessage(eccode+"_"+WXAuthContant.MSG_FUNCNAME_WX,WXAuthContant.MSG_FUNCNAME_WX,
					String.valueOf(replyMsg_code));
			if(placeMap.size() !=0){
				replyMsg = StringUtils.placeHolderReform(1, replyMsg, placeMap);
			}
			if("NULL".equals(replyMsg))
				replyMsg = "";
			
			//是欢迎消息，即首次的欢迎事件，是否是从菜单提示认证？
			//如果是isongjiang等公众号的欢迎消息，则不需要回复内容,
//			if(replyMsg_code == WXMsgCodeConstant.WX_MSG_CODE_WELCOME){
//				String eccodes = MPClientUtils.callService(
//						"/mp/portal/config/business/getConfig", new Object[] {
//								"auth_from_menu", "eccodes" }, String.class);
//				if (!StringUtils.isEmpty(eccodes)) {
//					String[] menuEccodes = eccodes.split(";");
//					for (String _eccode : menuEccodes) {
//						if (eccode.equals(_eccode)) {
//							replyMsg="";
//							break;
//						}
//					}
//				}
//			}
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_SYSTEM_EXCEPTION));
			result.setDescription("msg getlink to form replymsg exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
			replyMsg = MessageUtil.getMessage(eccode+"_"+WXAuthContant.MSG_FUNCNAME_WX,WXAuthContant.MSG_FUNCNAME_WX,
					String.valueOf(replyMsg_code));
		}
		resultMap.put(ParamConstant.KEY_REPLYMSG_CODE, String.valueOf(replyMsg_code));
		resultMap.put(ParamConstant.KEY_REPLYMSG, replyMsg);
		return resultMap;
	}
	
	@Override
	public ResponseResult event_subscribe(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) {
		/**
		 * 1.不存在微信用户，则需要新增微信用户（手机号为空，验证状态为未校验），开宽带用户，提醒用户回复手机号码
		 * 2.存在微信用户，则先删除微信用户（包括微信用户、宽带用户、mac绑定），再重新新开，并提醒用户回复手机号码 
		 * 3.订阅事件回应用户都是“提醒用户回复手机号码”
		 */
		ResponseResult result = new ResponseResult();
		String fromUserName = wxMsgModel.getFromUserName();
		String toUserName = wxMsgModel.getToUserName();
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "event_subscribe")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", wxMsgModel.getToUserName());
		logObj.putData("fromUserName", wxMsgModel.getFromUserName());
		log.info(logObj);
		String openid = fromUserName;
		String registerid = toUserName;
		int replyMsg_code = 0;
		Map<String, String> placeMap = initPlaceMap();
		String eccode ="";
		try {
			DEnterpriseRegister der = MPClientUtils.callService(
					"/mp/portal/dEnterpriseRegister/getByRegisterid",
					new Object[] { wxMsgModel.getToUserName() },
					DEnterpriseRegister.class);
			if(der == null){
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
				result.setDescription("register is not configure");
				replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
			}else{
                int status = der.getStatus();
                if (status == ParamConstant.KEY_STATUS_PAUSE) {
                    result.setResult(String
                            .valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
                    result.setDescription("DEenterpriseRegister status is pause");
                    replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
                } else {
                    eccode = der.getEccode();
                    BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                    result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                    if (specialUser != null) {
                        Map<String, String> userMap = new HashMap<String, String>();
                        userMap.put(ParamConstant.KEY_REGISTERID, registerid);
                        userMap.put(ParamConstant.KEY_SESSIONID, sessionid);
                        result = specialUserBO.closeSpecialUser(specialUser, userMap);
                    }
                    if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(result.getResult())) {
                        int qrgroupid = specialUserBO
                                .getQrGroupID(eccode, wxMsgModel.getEventKey());
                        Map<String, String> addParamMap = new HashMap<String, String>();
                        addParamMap.put(ParamConstant.KEY_REGISTERID, registerid);
                        addParamMap.put(ParamConstant.KEY_ECCODE, eccode);
                        addParamMap.put(ParamConstant.KEY_WX_OPENID, openid);
                        addParamMap.put(ParamConstant.KEY_PHONE, "");
                        addParamMap.put(ParamConstant.KEY_QRGROUPID, String.valueOf(qrgroupid));
                        addParamMap.put(ParamConstant.KEY_SESSIONID, sessionid);
                        result = specialUserBO.addSpecialUserAndOpenBroadUser(addParamMap);
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_WELCOME;
                    }
                }
			}
		} catch (DAOException e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_EXCEPTION));
			result.setDescription("operatedb exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_WELCOME;
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_SYSTEM_EXCEPTION));
			result.setDescription("event subscribe exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_WELCOME;
		}
		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		
		result.setInfo(replyMsg);
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription())
				.putData("replyMsg_code", replyMsg_code)
				.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}

	@Override
	public ResponseResult event_unsubscribe(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) {
		/**
		 *  1.不存在微信绑定用户，则回应成功 
		 *  2.存在微信绑定用户，则删除，并发送aaa销户
		 */
		ResponseResult result = new ResponseResult();
		String fromUserName = wxMsgModel.getFromUserName();
		String toUserName = wxMsgModel.getToUserName();
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "event_unsubscribe")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", wxMsgModel.getToUserName()).putData(
				"fromUserName", wxMsgModel.getFromUserName());
		log.info(logObj);
		String openid = fromUserName;
		String registerid = toUserName;
		int replyMsg_code = 0;
		Map<String, String> placeMap = initPlaceMap();
		String eccode ="";
		try {
			DEnterpriseRegister der = MPClientUtils.callService(
					"/mp/portal/dEnterpriseRegister/getByRegisterid",
					new Object[] { wxMsgModel.getToUserName() },
					DEnterpriseRegister.class);
			if(der == null){
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
				result.setDescription("register is not configure");
				replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
			}else{
                int status = der.getStatus();
                if (status == ParamConstant.KEY_STATUS_PAUSE) {
                    result.setResult(String
                            .valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
                    result.setDescription("DEenterpriseRegister status is pause");
                    replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
                } else {
                    eccode = der.getEccode();
                    BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                    if (specialUser == null) {
                        result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                        result.setDescription("specialuser is notexist ,not need cancel");
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_UNSUBCRIBE_SUCCESS;
                    } else {
                        Map<String, String> userMap = new HashMap<String, String>();
                        userMap.put(ParamConstant.KEY_REGISTERID, registerid);
                        userMap.put(ParamConstant.KEY_SESSIONID, sessionid);

                        result = specialUserBO.closeSpecialUser(specialUser, userMap);
                        if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                                result.getResult())) {
                            replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_UNSUBCRIBE_SUCCESS;
                        } else {
                            replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_UNSUBCRIBE_FAILURE;
                        }
                    }
                }
			}
		} catch (DAOException e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_EXCEPTION));
			result.setDescription("operatedb exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_UNSUBCRIBE_FAILURE;
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_SYSTEM_EXCEPTION));
			result.setDescription("event subscribe exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_UNSUBCRIBE_FAILURE;
		}
		
		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		result.setInfo(replyMsg);
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription())
				.putData("replyMsg_code", replyMsg_code)
				.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}

	@Override
	public ResponseResult event_scan(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) {
		ResponseResult result = new ResponseResult();
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "event_scan")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", wxMsgModel.getToUserName()).putData(
				"fromUserName", wxMsgModel.getFromUserName());
		log.info(logObj);
		
		int replyMsg_code = 0;
		
		Map<String, String> placeMap = initPlaceMap();
		String eccode ="";
		
		DEnterpriseRegister der = MPClientUtils.callService(
				"/mp/portal/dEnterpriseRegister/getByRegisterid",
				new Object[] { wxMsgModel.getToUserName() },
				DEnterpriseRegister.class);
		if(der != null){
            int status = der.getStatus();
            if (status != ParamConstant.KEY_STATUS_PAUSE) {
                eccode = der.getEccode();
            }
		}

		replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_NULL;
		result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
		result.setDescription("event scan ,don't need process");
		
		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		result.setInfo(replyMsg);
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription())
				.putData("replyMsg_code", replyMsg_code);
		logObj.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}

	@Override
	public ResponseResult event_other(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) {
		ResponseResult result = new ResponseResult();
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "event_other")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", wxMsgModel.getToUserName()).putData(
				"fromUserName", wxMsgModel.getFromUserName());
		log.info(logObj);
		int replyMsg_code = 0;
		Map<String, String> placeMap = initPlaceMap();
		String eccode ="";
		DEnterpriseRegister der = MPClientUtils.callService(
				"/mp/portal/dEnterpriseRegister/getByRegisterid",
				new Object[] { wxMsgModel.getToUserName() },
				DEnterpriseRegister.class);
		if(der != null){
            int status = der.getStatus();
            if (status != ParamConstant.KEY_STATUS_PAUSE) {
                eccode = der.getEccode();
            }
		}
		
		replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_NULL;
		result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
		result.setDescription("event:"+wxMsgModel.getEvent()+" ,don't need process");
		
		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		result.setInfo(replyMsg);
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription())
				.putData("replyMsg_code", replyMsg_code);
		logObj.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}

	@Override
	public ResponseResult event_wifiConnected(Map<String, String> paramMap,WXMsgModel wxMsgModel){
		ResponseResult result = new ResponseResult();
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "event_wifiConnected")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", wxMsgModel.getToUserName()).putData(
				"fromUserName", wxMsgModel.getFromUserName());
		log.info(logObj);
		int replyMsg_code = 0;
		Map<String, String> placeMap = initPlaceMap();
		String eccode ="";
		DEnterpriseRegister der = MPClientUtils.callService(
				"/mp/portal/dEnterpriseRegister/getByRegisterid",
				new Object[] { wxMsgModel.getToUserName() },
				DEnterpriseRegister.class);
		if(der != null){
            int status = der.getStatus();
            if (status != ParamConstant.KEY_STATUS_PAUSE) {
                eccode = der.getEccode();
            }
		}
		
		replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_NULL;
		result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
		result.setDescription("event:wificonnected,don't need process");
		
		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		
		result.setInfo(replyMsg);
		
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription());
		logObj.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}

	@Override
	public ResponseResult msg_bindphone(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) {
		ResponseResult result = new ResponseResult();
		String fromUserName = wxMsgModel.getFromUserName();
		String toUserName = wxMsgModel.getToUserName();
		String phone = paramMap.get(ParamConstant.KEY_PHONE);
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "msg_bindphone")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", wxMsgModel.getToUserName())
				.putData("fromUserName", wxMsgModel.getFromUserName())
				.putData("content", wxMsgModel.getContent())
				.putData(ParamConstant.KEY_PHONE,phone);
		log.info(logObj);
		String openid = fromUserName;
		String registerid = toUserName;
		int replyMsg_code = 0;
		Map<String, String> placeMap = initPlaceMap();
		String eccode = "";
		try {
			if(!WXUtil.isValidPhone(phone)){
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_INVALID_MOBILE));
				result.setDescription("invalid mobile:"+phone);
				replyMsg_code = WXMsgCodeConstant.WX_MSG_INVALID_MOBILE;
			}else{
				boolean needBindUserPhone = false;
				DEnterpriseRegister der = MPClientUtils.callService(
						"/mp/portal/dEnterpriseRegister/getByRegisterid",
						new Object[] { wxMsgModel.getToUserName() },
						DEnterpriseRegister.class);
				if(der == null){
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
					result.setDescription("register is not configure");
					replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
				}else{
                    int status = der.getStatus();
                    if (status == ParamConstant.KEY_STATUS_PAUSE) {
                        result.setResult(String
                                .valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
                        result.setDescription("DEenterpriseRegister status is pause");
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
                    } else {
                        eccode = der.getEccode();
                        BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                        int wxuser_status = specialUserBO.validSpeicalUserStatus(specialUser);
                        if (wxuser_status == UserConstant.WXUSER_SPECIALUSER_NOTEXIST) {
                            // 不存在
                            int qrgroupid = -1;
                            Map<String, String> addParamMap = new HashMap<String, String>();
                            addParamMap.put(ParamConstant.KEY_REGISTERID, registerid);
                            addParamMap.put(ParamConstant.KEY_ECCODE, eccode);
                            addParamMap.put(ParamConstant.KEY_WX_OPENID, openid);
                            addParamMap.put(ParamConstant.KEY_PHONE, phone);
                            addParamMap.put(ParamConstant.KEY_QRGROUPID, String.valueOf(qrgroupid));
                            addParamMap.put(ParamConstant.KEY_SESSIONID, paramMap.get("sessionid"));
                            result = specialUserBO.addSpecialUserAndOpenBroadUser(addParamMap);
                            if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                                    result.getResult())) {
                                needBindUserPhone = true;
                            } else {
                                replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
                            }
                        } else if (wxuser_status == UserConstant.WXUSER_BROADUSER_NOORFAIL) {
                            Map<String, String> userMap = new HashMap<String, String>();
                            userMap.put(ParamConstant.KEY_REGISTERID, registerid);
                            userMap.put(ParamConstant.KEY_SESSIONID, sessionid);
                            result = specialUserBO.openBroadUserAndUpdateSpecialUser(specialUser,
                                    userMap);
                            if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
                                    result.getResult())) {
                                needBindUserPhone = true;
                            } else {
                                replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
                            }
                        } else if (wxuser_status == UserConstant.WXUSER_VERIFY_EXPIRED) {
                            // 验证过期
                            result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                            result.setDescription("expired,need verify");
                            needBindUserPhone = true;
                        } else if (wxuser_status == UserConstant.WXUSER_VERIFY_NOORFAIL) {
                            // 微信没有验证或者验证不成功
                            result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                            result.setDescription("need verify");
                            needBindUserPhone = true;
                        } else {
                            // 已验证成功且为过期，不用重复验证
                            result.setResult(String.valueOf(WXResultConstant.RESULT_HAS_VERIFIED));
                            result.setDescription("verify has done and not expired");
                            replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_REBINDPHONE;
                        }
                    }
				}
				boolean needSendVerifySms = false;
				//先绑定手机号到aaa端，调用绑定手机号接口
				if (needBindUserPhone) {
					String endpoint = MPClientUtils.callService(
							"/mp/portal/config/business/getConfig", new Object[] {
									"wxauth", "aaa_url" }, String.class);
					WXServiceProxy proxy = new WXServiceProxy(endpoint);
					String timestamp = SystemUtil.getDate(new Date(), "yyyyMMddHHmmss");
					BindWXUserReq req = new BindWXUserReq();
					req.setOpenid(openid);
					req.setPhone(phone);
					req.setRegisterid(registerid);
					req.setReqNo(timestamp);
					req.setTimeStamp(timestamp);
					BindWXUserResp resp = proxy.bindWXUser(req);
					if (resp == null) {
						result.setResult(String
								.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
						result.setDescription("aaa bindWXUser resp is null");
						logObj.putData("result", WXResultConstant.RESULT_AAAREQ_FAIL);
						logObj.putData("description", "aaa bindWXUser resp is null");
					} else {
						logObj.putData("result", resp.getResultCode());
						logObj.putData("description", resp.getDescription());
						if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
								resp.getResultCode())) {
							needSendVerifySms = true;
							result.setResult(String
									.valueOf(WXResultConstant.RESULT_SUCCESS));
							result.setDescription("aaa bindWXUser resp success");
						} else {
							result.setResult(String
									.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
							result.setDescription("aaa bindWXUser resp fail");
						}
					}
				}
				
				//下发短信
				if (needSendVerifySms) {
					paramMap.put(ParamConstant.KEY_ECCODE, eccode);
					paramMap.put(ParamConstant.KEY_PHONE, phone);
					paramMap.put(ParamConstant.KEY_WX_OPENID, openid);
					paramMap.put(ParamConstant.KEY_REGISTERID, registerid);
					paramMap.put(ParamConstant.KEY_VERIFY_TYPE, WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_LINK);
					result = wxVerifyBO.sendVerifySms(paramMap);
					if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
							result.getResult())) {
						replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SENDSMS_SUCCESS;
					} else if (String.valueOf(
							WXResultConstant.RESULT_SENDSMS_FAIL).equals(
									result.getResult())) {
						replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SENDSMS_FAIL;
					}else {
						replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
					}
				}
			}
		} catch (DAOException e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_EXCEPTION));
			result.setDescription("operatedb exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_SYSTEM_EXCEPTION));
			result.setDescription("msg bindphone exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
		}

		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		
		result.setInfo(replyMsg);
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription())
				.putData("replyMsg_code", replyMsg_code)
				.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}

	@Override
	public ResponseResult msg_getlink(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) {
		ResponseResult result = new ResponseResult();
		String fromUserName = wxMsgModel.getFromUserName();
		String toUserName = wxMsgModel.getToUserName();
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "msg_getlink")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", toUserName)
				.putData("fromUserName", fromUserName)
				.putData("content", wxMsgModel.getContent());
		log.info(logObj);
		String openid = fromUserName;
		int replyMsg_code = 0;
		Map<String, String> placeMap = initPlaceMap();
		String eccode ="";
		try {
			DEnterpriseRegister der = MPClientUtils.callService(
					"/mp/portal/dEnterpriseRegister/getByRegisterid",
					new Object[] { wxMsgModel.getToUserName() },
					DEnterpriseRegister.class);
			if(der == null){
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
				result.setDescription("register is not configure");
				replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
			}else{
                int status = der.getStatus();
                if (status == ParamConstant.KEY_STATUS_PAUSE) {
                    result.setResult(String
                            .valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
                    result.setDescription("DEenterpriseRegister status is pause");
                    replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_ERROR;
                } else {
                    eccode = der.getEccode();
                    BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
                    int wxuser_status = specialUserBO.validSpeicalUserStatus(specialUser);
                    if (wxuser_status == UserConstant.WXUSER_SPECIALUSER_NOTEXIST) {
                        // 微信用户不存在
                        result.setResult(String
                                .valueOf(WXResultConstant.RESULT_SPECIALUSER_NOTEXIST));
                        result.setDescription("special user is not exist");
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_GETPHONE;
                    } else if (wxuser_status == UserConstant.WXUSER_BROADUSER_NOORFAIL) {
                        // 宽带用户不存在
                        result.setResult(String.valueOf(WXResultConstant.RESULT_BROADUSER_NOTEXIST));
                        result.setDescription("broad user is not exist");
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_GETPHONE;
                    } else if (wxuser_status == UserConstant.WXUSER_VERIFY_EXPIRED) {
                        result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_EXPIRED));
                        result.setDescription("expired,need verify");
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_EXPIRED;
                    } else if (wxuser_status == UserConstant.WXUSER_VERIFY_NOORFAIL) {
                        // 微信没有验证
                        result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_NO));
                        result.setDescription("no verify or verify fail");
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_GETPHONE;
                    } else {
                        // 正常，构造上网url
                        Map<String, String> finalParamMap = new HashMap<String, String>();
                        finalParamMap.put(ParamConstant.KEY_ECCODE, eccode);
                        finalParamMap.put(ParamConstant.KEY_WX_OPENID, openid);
                        finalParamMap.put(ParamConstant.KEY_SESSIONID, sessionid);
                        finalParamMap.put(ParamConstant.KEY_TIMESTAMP,
                                FormatUtil.dateToTimestamp(new Date()));
                        finalParamMap.put(ParamConstant.KEY_REGISTERID, toUserName);

                        String netauthUrl = wxNetAuthBO.formNetAuthUrl(finalParamMap);
                        logObj.putSysKey(LogObj.STEP, "formnetauthurl");
                        logObj.putData(ParamConstant.KEY_REDIRCTURL, netauthUrl).putData(
                                ParamConstant.KEY_FINALPARAMMAP, finalParamMap);
                        log.info(logObj);
                        result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                        result.setDescription("get link success");
                        replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_LINK;
                        placeMap.put(CommonConstant.PLACE_URL, netauthUrl);
                    }
                }
			}
		} catch (DAOException e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_EXCEPTION));
			result.setDescription("operate db exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_SYSTEM_EXCEPTION));
			result.setDescription("msg getlink exception:" + e.getMessage());
			replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_SYSTEM_BUSY;
		}

		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		
		result.setInfo(replyMsg);
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription())
				.putData("replyMsg_code", replyMsg_code)
				.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}
	@Override
	public ResponseResult msg_other(Map<String, String> paramMap,
			WXMsgModel wxMsgModel) {
		ResponseResult result = new ResponseResult();
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get(ParamConstant.KEY_SESSIONID))
				.putSysKey(LogObj.MODULE, "msg_other")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("toUserName", wxMsgModel.getToUserName())
				.putData("fromUserName", wxMsgModel.getFromUserName())
				.putData("content", wxMsgModel.getContent());
		log.info(logObj);
		int replyMsg_code = 0;
		Map<String, String> placeMap = initPlaceMap();
		String eccode = "";
		DEnterpriseRegister der = MPClientUtils.callService(
				"/mp/portal/dEnterpriseRegister/getByRegisterid",
				new Object[] { wxMsgModel.getToUserName() },
				DEnterpriseRegister.class);
		if(der != null){
            int status = der.getStatus();
            if (status != ParamConstant.KEY_STATUS_PAUSE) {
                eccode = der.getEccode();
            }
		}
		replyMsg_code = WXMsgCodeConstant.WX_MSG_CODE_NULL;
		result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
		result.setDescription("msg other ,don't need process");
		
		Map<String, String> replyMsgResult = getReplyMsg(result, replyMsg_code,eccode, placeMap);
		String replyMsg = replyMsgResult.get(ParamConstant.KEY_REPLYMSG);
		replyMsg_code = Integer.parseInt(replyMsgResult.get(ParamConstant.KEY_REPLYMSG_CODE));
		
		result.setInfo(replyMsg);
		logObj.putData("result", result.getResult())
				.putData("desc", result.getDescription())
				.putData("replyMsg_code", replyMsg_code)
				.putData("replyMsg", replyMsg);
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			// 校验成功
			log.info(logObj);
		} else {
			// 校验失败
			log.error(logObj);
		}
		return result;
	}

	public SpecialUserBO getSpecialUserBO() {
		return specialUserBO;
	}

	public WXVerifyBO getWxVerifyBO() {
		return wxVerifyBO;
	}

	public WXNetAuthBO getWxNetAuthBO() {
		return wxNetAuthBO;
	}

	public void setSpecialUserBO(SpecialUserBO specialUserBO) {
		this.specialUserBO = specialUserBO;
	}

	public void setWxVerifyBO(WXVerifyBO wxVerifyBO) {
		this.wxVerifyBO = wxVerifyBO;
	}

	public void setWxNetAuthBO(WXNetAuthBO wxNetAuthBO) {
		this.wxNetAuthBO = wxNetAuthBO;
	}
	
}
