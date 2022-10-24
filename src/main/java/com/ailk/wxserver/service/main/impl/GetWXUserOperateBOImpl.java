package com.ailk.wxserver.service.main.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.wxserver.ci.server.getwxuser.GetPhoneRequest;
import com.ailk.wxserver.ci.server.getwxuser.GetPhoneResponse;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.service.handler.interfaces.GetWXUserHandlerBO;
import com.ailk.wxserver.service.main.interfaces.GetWXUserOperateBO;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class GetWXUserOperateBOImpl implements GetWXUserOperateBO {

	private static Logger log = LogFactory.getLogger("getwxuseroperate");

	private GetWXUserHandlerBO getWXUserHandlerBO;
	
	@Override
	public GetPhoneResponse getPhone(GetPhoneRequest request) {
		int logid = (int) (Math.random() * Integer.MAX_VALUE);
		String sessionid = String.valueOf(logid);

		String requestIP = RequestParamUtil.getRequestIp();

		LogObj logObj = new LogObj();

		logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "getwxuseroperate")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("username", request.getUsername())
				.putData("eccode", request.getEccode())
				.putData("timeStamp", request.getTimeStamp())
				.putData("requestIP", requestIP);
		log.info(logObj);
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.KEY_SESSIONID, sessionid);
		paramMap.put(ParamConstant.KEY_USERNAME, request.getUsername());
		paramMap.put(ParamConstant.KEY_ECCODE, request.getEccode());
		paramMap.put(ParamConstant.KEY_TIMESTAMP, request.getTimeStamp());
		paramMap.put(ParamConstant.KEY_REQUESTIP, requestIP);
		
		ResponseResult result = getWXUserHandlerBO.getPhoneByUserName(paramMap);
		
		GetPhoneResponse response = new GetPhoneResponse();
		response.setDescription(result.getDescription());
		response.setResultCode(Integer.parseInt(result.getResult()));
		if(WXResultConstant.RESULT_SUCCESS == Integer.parseInt(result.getResult())){
			String phone = (String) result.getObject();
			response.setPhone(phone);
		}
		
		logObj.putSysKey(LogObj.STEP,"response");
		logObj.putData("resultCode",response.getResultCode()).putData("description",response.getDescription())
		.putData("phone",response.getPhone());
		if(response.getResultCode() == WXResultConstant.RESULT_SUCCESS){
			log.info(logObj);
		}else{
			log.error(logObj);
		}
		return response;
	}

	public GetWXUserHandlerBO getGetWXUserHandlerBO() {
		return getWXUserHandlerBO;
	}

	public void setGetWXUserHandlerBO(GetWXUserHandlerBO getWXUserHandlerBO) {
		this.getWXUserHandlerBO = getWXUserHandlerBO;
	}

}
