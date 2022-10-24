package com.ailk.wxserver.local_ctsh.service.base.impl;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.ci.client.sendsms.CommonSendSMSProxy;
import com.ailk.wxserver.ci.client.sendsms.CommonSendSMSRequest;
import com.ailk.wxserver.ci.client.sendsms.CommonSendSmsResponse;
import com.ailk.wxserver.service.base.interfaces.SMSBO;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.SystemUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class SMSBOImpl implements SMSBO {

	public static final Logger log = LogFactory.getLogger("sms");
	public static final Logger error = LogFactory.getLogger("error");
	@Override
	public int sendSMS(Map<String, String> paramMap) {
		String phonenumber = paramMap.get("phonenumber");
		String smscontent = paramMap.get("smscontent");
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get("sessionid"))
				.putSysKey(LogObj.MODULE, "smgpsms")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("phonenumber", phonenumber).putData("smscontent",
				smscontent);
		log.info(logObj);
		String sendsmsurl = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] { "sendsms",
						"url" }, String.class);
		log.info(sendsmsurl);
		CommonSendSMSProxy proxy = new CommonSendSMSProxy(sendsmsurl);
		CommonSendSMSRequest request = new CommonSendSMSRequest();
		request.setPhoneNumber(phonenumber);
		request.setContent(smscontent);
		String timestamp = SystemUtil.getDate(new Date(), "yyyyMMddHHmmss");
		request.setTimeStamp(timestamp);
		CommonSendSmsResponse response= null;
		try {
			response = proxy.sendsms(request);
		} catch (RemoteException e) {
			LogUtil.printErrorStackTrace(error, e);
			response = new CommonSendSmsResponse();
			response.setResult("-1");
			response.setDescription("sendsms exception");
		}
		if(response == null){
			response = new CommonSendSmsResponse();
			response.setResult("-1");
			response.setDescription("sendsms response is null");
		}
		logObj.putSysKey(LogObj.STEP, "response");
		logObj.putData("result",response.getResult());
		logObj.putData("description",response.getDescription());
		if("0".equals(response.getResult())){
			log.info(logObj);
		}else{
			log.error(logObj);
		}
		return Integer.parseInt(response.getResult());
	}

}
