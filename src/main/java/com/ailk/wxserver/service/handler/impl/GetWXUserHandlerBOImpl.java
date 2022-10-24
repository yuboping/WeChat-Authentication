package com.ailk.wxserver.service.handler.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.UserConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.GetWXUserHandlerBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.Validate;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class GetWXUserHandlerBOImpl implements GetWXUserHandlerBO {

	private static Logger log = LogFactory.getLogger("getwxuserhandler");
	private static Logger error = LogFactory.getLogger("error");

	private SpecialUserBO specialUserBO;

	@Override
	public ResponseResult getPhoneByUserName(Map<String, String> paramMap) {

		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);

		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "getphonebyusername")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("paramMap", paramMap);
		log.info(logObj);

		ResponseResult result;
		try {
			result = checkGetPhoneReq(paramMap);
			if (WXResultConstant.RESULT_SUCCESS == Integer.parseInt(result
					.getResult())) {
				result = handler(paramMap);
			}
		} catch (DAOException e) {
			error.error("operate db exception:" + e.getMessage());
			LogUtil.printErrorStackTrace(error, e);
			result = new ResponseResult();
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_EXCEPTION));
			result.setDescription("operate db exception:" + e.getMessage());
		} catch (Exception e) {
			error.error("getphone error:" + e.getMessage());
			LogUtil.printErrorStackTrace(error, e);
			result = new ResponseResult();
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_SYSTEM_EXCEPTION));
			result.setDescription("getphone error:" + e.getMessage());
		}
		logObj.putData("result", result.getResult()).putData("description",
				result.getDescription());
		if (WXResultConstant.RESULT_SUCCESS == Integer.parseInt(result
				.getResult())) {
			String phone = paramMap.get(ParamConstant.KEY_PHONE);
			logObj.putData(ParamConstant.KEY_PHONE, phone);
			log.info(logObj);
		} else {
			log.error(logObj);
		}
		return result;
	}

	private ResponseResult handler(Map<String, String> paramMap)
			throws DAOException {
		ResponseResult result = new ResponseResult();
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String username = paramMap.get(ParamConstant.KEY_USERNAME);
		BSpecialUser specialUser = specialUserBO.querySpecialUserByUsername(
				eccode, username);

		int wxuser_status = specialUserBO.validSpeicalUserStatus(specialUser);
		if (wxuser_status == UserConstant.WXUSER_SPECIALUSER_NOTEXIST) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_SPECIALUSER_NOTEXIST));
			result.setDescription("special user is not exist");
		} else if (wxuser_status == UserConstant.WXUSER_BROADUSER_NOORFAIL) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_BROADUSER_NOTEXIST));
			result.setDescription("broad user is not exist");
		} else if (wxuser_status == UserConstant.WXUSER_VERIFY_EXPIRED) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_VERIFY_EXPIRED));
			result.setDescription("expired,need verify");
		} else if (wxuser_status == UserConstant.WXUSER_VERIFY_NOORFAIL) {
			// 微信没有验证
			result.setResult(String.valueOf(WXResultConstant.RESULT_VERIFY_NO));
			result.setDescription("no verify or verify fail");
		} else {
			String phone = specialUser.getPhone();
			result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			result.setDescription("get phone success");
			result.setObject(phone);
		}
		return result;
	}

	private ResponseResult checkGetPhoneReq(Map<String, String> paramMap)
			throws Exception {
		ResponseResult result = new ResponseResult();

		String requestIP = paramMap.get(ParamConstant.KEY_REQUESTIP);
		if (!checkPermitIP(requestIP)) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_NOT_PERMIT_IP));
			result.setDescription("requestIP is not permit");
			return result;
		}

		String username = paramMap.get(ParamConstant.KEY_USERNAME);
		if (StringUtils.isEmpty(username)) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_REQPARAM_ERROR));
			result.setDescription("username is null");
			return result;
		}

		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		if (StringUtils.isEmpty(eccode)) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_REQPARAM_ERROR));
			result.setDescription("eccode is null");
			return result;
		}

		String timeStamp = paramMap.get(ParamConstant.KEY_TIMESTAMP);
		if (StringUtils.isEmpty(timeStamp)) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_REQPARAM_ERROR));
			result.setDescription("timeStamp is null");
			return result;
		}

		String timeout = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"getwxuser", "timeout" }, String.class);
		if (!StringUtils.isEmpty(timeout)) {
			long opotTimeout = Long.parseLong(timeout);
			if (!Validate.validateTimestamp(opotTimeout, timeStamp)) {
				// 时间戳限制
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_REQPARAM_ERROR));
				result.setDescription("is expired timeStamp");
				return result;
			}
		}

		result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
		result.setDescription("valid req success");
		return result;

	}

	private boolean checkPermitIP(String requestIP) {
		String permitip = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"getwxuser", "permitip" }, String.class);
		if (!StringUtils.isEmpty(permitip)) {
			String[] allowIps = permitip.split(";");
			for (String ip : allowIps) {
				if (requestIP.equals(ip)) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}

	public SpecialUserBO getSpecialUserBO() {
		return specialUserBO;
	}

	public void setSpecialUserBO(SpecialUserBO specialUserBO) {
		this.specialUserBO = specialUserBO;
	}
}
