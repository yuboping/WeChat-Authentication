package com.ailk.wxserver.service.handler.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.po.DEnterpriseRegister;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.WXSignatureCheckHandlerBO;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.ailk.wxserver.util.qq.wx.AesException;
import com.ailk.wxserver.util.qq.wx.WXBizMsgCrypt;
import com.ailk.wxserver.util.qq.wx.WXBizMsgPlain;

public class WXSignatureCheckHandlerBOImpl implements WXSignatureCheckHandlerBO {

	private Logger log = LogFactory.getLogger("wxsignaturecheckhandler");
	
	@Override
	public Map<String, String> getRequestParam(HttpServletRequest request) {
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
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(WXAuthContant.KEY_URL_SIGNATURE, signature);
		paramMap.put(WXAuthContant.KEY_URL_ECHOSTR, echostr);
		paramMap.put(WXAuthContant.KEY_URL_TIMESTAMP, timestamp);
		paramMap.put(WXAuthContant.KEY_URL_NONCE, nonce);
		paramMap.put(WXAuthContant.KEY_URL_ENCRYPT_TYPE, encrypt_type);
		paramMap.put(WXAuthContant.KEY_URL_MSG_SIGNATURE, msg_signature);
		paramMap.put(WXAuthContant.KEY_URL_UNCODE, uncode);
		return paramMap;
	}

	@Override
	public void responseMsg(HttpServletResponse response, String reply_msg)
			throws IOException {
		response.getWriter().print(reply_msg);
	}
	
	@Override
	public ResponseResult signaturecheck(Map<String, String> paramMap) {
		ResponseResult result = new ResponseResult();
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		String signature = paramMap.get(WXAuthContant.KEY_URL_SIGNATURE);
		String echostr = paramMap.get(WXAuthContant.KEY_URL_ECHOSTR);
		String timestamp = paramMap.get(WXAuthContant.KEY_URL_TIMESTAMP);
		String nonce = paramMap.get(WXAuthContant.KEY_URL_NONCE);
		String encrypt_type = paramMap.get(WXAuthContant.KEY_URL_ENCRYPT_TYPE);
		String msg_signature = paramMap
				.get(WXAuthContant.KEY_URL_MSG_SIGNATURE);
		String uncode = paramMap.get(WXAuthContant.KEY_URL_UNCODE);

		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "wxsignaturecheck")
				.putSysKey(LogObj.STEP, "request");
		logObj
				.putData(WXAuthContant.KEY_URL_SIGNATURE,signature)
				.putData(WXAuthContant.KEY_URL_ECHOSTR,echostr)
				.putData(WXAuthContant.KEY_URL_TIMESTAMP,timestamp)
				.putData(WXAuthContant.KEY_URL_NONCE,nonce)
				.putData(WXAuthContant.KEY_URL_ENCRYPT_TYPE,encrypt_type)
				.putData(WXAuthContant.KEY_URL_MSG_SIGNATURE,msg_signature)
				.putData(WXAuthContant.KEY_URL_UNCODE,uncode);
		log.info(logObj);
		
		DEnterpriseRegister der = MPClientUtils.callService(
				"/mp/portal/dEnterpriseRegister/getByUncode",
				new Object[] { uncode }, DEnterpriseRegister.class);
		if (der == null) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_NOEXIST));
			result.setDescription("no exist DEenterpriseRegister by uncode:"
					+ uncode);
			result.setInfo("");
		}else{
		    int status = der.getStatus();
            if (status == ParamConstant.KEY_STATUS_PAUSE) {
                result.setResult(String
                        .valueOf(WXResultConstant.RESULT_ENTERPRISE_REGISTER_STATUS_PAUSE));
                result.setDescription("DEenterpriseRegister status is pause:" + status);
                result.setInfo("");
            }else{
                try {
                    String rechostr = "";
                    /**
                     * 根据是否加密，进行签名校验
                     */
                    if (WXAuthContant.WX_ENCRYPT_TYPE_AES.equals(encrypt_type)) {
                        WXBizMsgCrypt wbmc = new WXBizMsgCrypt(der.getToken(),
                                der.getAeskey(), der.getAppid());
                        rechostr = wbmc.verifyUrl(msg_signature, timestamp, nonce,
                                echostr);
                    } else {
                        WXBizMsgPlain wbmp = new WXBizMsgPlain(der.getToken());
                        rechostr = wbmp.verifyUrlNoEncry(signature, timestamp, nonce,
                                echostr);
                    }
                    result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
                    result.setDescription("check signature url success ");
                    result.setInfo(rechostr);
                } catch (AesException e) {
                    result.setResult(String.valueOf(e.getCode()));
                    result.setDescription(e.getMessage());
                    result.setInfo("");
                }
            }
		}
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("result", result.getResult());
		logObj.putData("desc", result.getDescription());
		logObj.putData("reply_msg", result.getInfo());
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

}
