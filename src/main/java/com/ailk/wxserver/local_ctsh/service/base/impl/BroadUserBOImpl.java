package com.ailk.wxserver.local_ctsh.service.base.impl;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;

import com.ailk.wxserver.service.base.interfaces.BroadUserBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.SpecialAuthUtil;
import com.ailk.wxserver.util.SystemUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.asiainfo.lcims.lcbmi.weixin.server.CloseWXUserReq;
import com.asiainfo.lcims.lcbmi.weixin.server.CloseWXUserResp;
import com.asiainfo.lcims.lcbmi.weixin.server.OpenWXUserReq;
import com.asiainfo.lcims.lcbmi.weixin.server.OpenWXUserResp;
import com.asiainfo.lcims.lcbmi.weixin.server.WXServiceProxy;

public class BroadUserBOImpl implements BroadUserBO {

	public static final Logger log = LogFactory.getLogger("broaduser");

	public static final Logger error = LogFactory.getLogger("error");

	@Override
	public ResponseResult openUser(Map<String, String> paramMap) {
		ResponseResult result = new ResponseResult();

		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get("sessionid"))
				.putSysKey(LogObj.MODULE, "openuser")
				.putSysKey(LogObj.STEP, "request");
		String endpoint = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "aaa_url" }, String.class);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String registerid = paramMap.get(ParamConstant.KEY_REGISTERID);
		String phone = paramMap.get(ParamConstant.KEY_PHONE);
		String userName = paramMap.get(ParamConstant.KEY_USERNAME);
		String password = paramMap.get(ParamConstant.KEY_PASSWORD);
		String timestamp = SystemUtil.getDate(new Date(), "yyyyMMddHHmmss");
		logObj.putData("aaaurl", endpoint)
				.putData(ParamConstant.KEY_ECCODE, eccode)
				.putData(ParamConstant.KEY_WX_OPENID, openid)
				.putData(ParamConstant.KEY_REGISTERID, registerid)
				.putData(ParamConstant.KEY_PHONE, phone)
				.putData(ParamConstant.KEY_USERNAME, userName)
				.putData(ParamConstant.KEY_PASSWORD, password)
				.putData(ParamConstant.KEY_TIMESTAMP, timestamp);
		log.info(logObj);
		WXServiceProxy proxy = new WXServiceProxy(endpoint);
		OpenWXUserReq req = new OpenWXUserReq();
//		req.setEccode(eccode);
		req.setOpenid(openid);
		req.setRegisterid(registerid);
		req.setPhone(phone);
//		req.setUserName(userName);
//		req.setPassword(password);
		req.setTimeStamp(timestamp);
		req.setReqNo(timestamp);
		try {
			OpenWXUserResp resp = proxy.openWXUser(req);
			if (resp == null) {
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
				result.setDescription("aaa openuser resp is null");
				logObj.putData("result", WXResultConstant.RESULT_AAAREQ_FAIL);
				logObj.putData("description", "aaa openuser resp is null");
			} else {
				logObj.putData("result", resp.getResultCode());
				logObj.putData("description", resp.getDescription());
				if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
						resp.getResultCode())) {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_SUCCESS));
					result.setDescription("aaa openuser resp success");
				} else {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
					result.setDescription("aaa openuser resp fail");
				}
			}
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
			result.setDescription("aaa openuser exception");
			logObj.putData("result", WXResultConstant.RESULT_AAAREQ_FAIL);
			logObj.putData("description", "aaa openuser exception");
		}
		logObj.putSysKey(LogObj.STEP, "aaaresp");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			log.info(logObj);
		} else {
			log.error(logObj);
		}

		logObj.putData("result", result.getResult()).putData("desc",
				result.getDescription());
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			log.info(logObj);
		} else {
			log.error(logObj);
		}
		return result;
	}

	@Override
	public String generateUserName(int appType, String ecCode, String openID) {
		String username = SpecialAuthUtil.generateUserName(appType, ecCode,
				openID);
		return username;
	}

	@Override
	public String generatePwd(String username) {
		String password_length = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "password_length" }, String.class);
		String password = SpecialAuthUtil.generatePwd(username,
				Integer.parseInt(password_length));
		return password;
	}

	@Override
	public ResponseResult closeUser(Map<String, String> paramMap) {
		ResponseResult result = new ResponseResult();

		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get("sessionid"))
				.putSysKey(LogObj.MODULE, "closeuser")
				.putSysKey(LogObj.STEP, "request");
		String endpoint = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "aaa_url" }, String.class);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String registerid = paramMap.get(ParamConstant.KEY_REGISTERID);
		String userName = paramMap.get(ParamConstant.KEY_USERNAME);
		String timestamp = SystemUtil.getDate(new Date(), "yyyyMMddHHmmss");
		logObj.putData("aaaurl", endpoint)
				.putData(paramMap.get(ParamConstant.KEY_ECCODE), eccode)
				.putData(paramMap.get(ParamConstant.KEY_WX_OPENID), openid)
				.putData(paramMap.get(ParamConstant.KEY_REGISTERID), registerid)
				.putData(ParamConstant.KEY_USERNAME, userName)
				.putData(ParamConstant.KEY_TIMESTAMP, timestamp);
		log.info(logObj);
		WXServiceProxy proxy = new WXServiceProxy(endpoint);
		CloseWXUserReq req = new CloseWXUserReq();
//		req.setEccode(eccode);
		req.setOpenid(openid);
		req.setRegisterid(registerid);
		req.setReqNo(timestamp);
//		req.setUserName(userName);
		req.setTimeStamp(timestamp);
		try {
			CloseWXUserResp resp = proxy.closeWXUser(req);
			if (resp == null) {
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
				result.setDescription("aaa closeuser resp is null");
				logObj.putData("result", WXResultConstant.RESULT_AAAREQ_FAIL);
				logObj.putData("description", "aaa closeuser resp is null");
			} else {
				logObj.putData("result", resp.getResultCode());
				logObj.putData("description", resp.getDescription());
				if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
						resp.getResultCode())) {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_SUCCESS));
					result.setDescription("aaa closeuser resp success");
				} else {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
					result.setDescription("aaa closeuser resp fail");
				}
			}
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
			result.setDescription("aaa closeuser exception");
			logObj.putData("result", WXResultConstant.RESULT_AAAREQ_FAIL);
			logObj.putData("description", "aaa closeuser exception");
		}
		logObj.putSysKey(LogObj.STEP, "aaaresp");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			log.info(logObj);
		} else {
			log.error(logObj);
		}

		logObj.putData("result", result.getResult()).putData("desc",
				result.getDescription());
		logObj.putSysKey(LogObj.STEP, "response");
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				result.getResult())) {
			log.info(logObj);
		} else {
			log.error(logObj);
		}
		return result;
	}

}
