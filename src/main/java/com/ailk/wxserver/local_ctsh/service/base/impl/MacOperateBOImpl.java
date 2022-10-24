package com.ailk.wxserver.local_ctsh.service.base.impl;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.service.base.interfaces.MacOperateBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.SystemUtil;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.linkage.lcunp.interfaces.server.mac.cancel.CancelMacBindProxy;
import com.linkage.lcunp.interfaces.server.mac.cancel.OperateMacRequest;
import com.linkage.lcunp.interfaces.server.mac.cancel.OperateMacResponse;

public class MacOperateBOImpl implements MacOperateBO {

	public static final Logger log = LogFactory.getLogger("macoperate");
	public static final Logger error = LogFactory.getLogger("error");
	
	@Override
	public ResponseResult directCancelMacBind(Map<String, String> paramMap) {
		
		ResponseResult result = new ResponseResult();
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, paramMap.get("sessionid"))
				.putSysKey(LogObj.MODULE, "directmaccancel")
				.putSysKey(LogObj.STEP, "request");
		String endpoint = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"mac", "directcancelmac_url" }, String.class);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String userName = paramMap.get(ParamConstant.KEY_USERNAME);
		String sourcetype = paramMap.get(ParamConstant.KEY_SOURCETYPE);
		String timestamp = SystemUtil.getDate(new Date(), "yyyyMMddHHmmss");
		logObj.putData("macurl", endpoint)
				.putData(paramMap.get(ParamConstant.KEY_ECCODE), eccode)
				.putData(ParamConstant.KEY_USERNAME, userName)
				.putData(ParamConstant.KEY_SOURCETYPE, sourcetype)
				.putData(ParamConstant.KEY_TIMESTAMP, timestamp);
		log.info(logObj);
		try {
			
			
			String cancelbindpassword = MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"mac", "cancelbindpassword" }, String.class);
			CancelMacBindProxy proxy = new CancelMacBindProxy(endpoint);
			//构造请求参数，调用soap接口，完成mac绑定的取消
			OperateMacRequest request = new OperateMacRequest();
			request.setSourcetype(Integer.parseInt(sourcetype));
			request.setEcCode(eccode);
			request.setTimeStamp(timestamp);
			request.setUserName(userName);
			request.setUserIp("");
			request.setPassword(cancelbindpassword);
			
			OperateMacResponse response = proxy.cancelMacBind(request);
			logObj.putSysKey(LogObj.STEP, "macresponse");
			if (response == null) {
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
				result.setDescription("mac directmaccancel resp is null");
				logObj.putData("result", WXResultConstant.RESULT_AAAREQ_FAIL);
				logObj.putData("description", "mac directmaccancel resp is null");
				log.error(logObj);
			} else {
				logObj.putData("result", response.getResultCode());
				logObj.putData("description", response.getDescription());
				if (WXResultConstant.RESULT_SUCCESS == response.getResultCode()) {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_SUCCESS));
					result.setDescription("mac directmaccancel resp success");
					log.info(logObj);
				} else {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_MACREQ_FAIL));
					result.setDescription("mac directmaccancel resp fail");
					log.error(logObj);
				}
			}
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_MACREQ_FAIL));
			result.setDescription("mac directmaccancel exception");
			logObj.putData("result", WXResultConstant.RESULT_AAAREQ_FAIL);
			logObj.putData("description", "mac directmaccancel exception");
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
