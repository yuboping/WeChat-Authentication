package com.ailk.wxserver.service.base.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.lcims.support.util.password.Password;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.service.base.interfaces.MemCachedBO;
import com.ailk.wxserver.service.base.interfaces.SMSBO;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.base.interfaces.WXVerifyBO;
import com.ailk.wxserver.service.constant.CommonConstant;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.SpecialAuthConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXMsgCodeConstant;
import com.ailk.wxserver.service.constant.WXReqTypeConstant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.util.FormatUtil;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.MessageUtil;
import com.ailk.wxserver.util.RequestParamUtil;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.SystemUtil;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;
import com.ailk.wxserver.util.qq.wx.WXUtil;

public class WXVerifyBOImpl implements WXVerifyBO {

	private Logger log = LogFactory.getLogger("wxverify");
	private Logger error = LogFactory.getLogger("error");
	
	private SpecialUserBO specialUserBO;
	private MemCachedBO memCachedBO = null;
	private SMSBO smsBO = null;
	
	@Override
	public boolean validGetVerifyCodeReq(Map<String, String> paramMap) {
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String phone = paramMap.get(ParamConstant.KEY_PHONE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		if (StringUtils.isEmpty(eccode)) {
			return false;
		}
		if (StringUtils.isEmpty(openid)) {
			return false;
		}
		if (StringUtils.isEmpty(phone)) {
			return false;
		}
		return true;

	}

	@Override
	public ResponseResult sendVerifySms(Map<String, String> paramMap) throws DAOException,
			Exception {
		ResponseResult result = new ResponseResult();
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String phone = paramMap.get(ParamConstant.KEY_PHONE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String registerid = paramMap.get(ParamConstant.KEY_REGISTERID);
		String verifyType = paramMap.get(ParamConstant.KEY_VERIFY_TYPE);
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y")
				.putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "sendVerifySms")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData("phone", phone).putData("openid", openid).putData("registerid", registerid).putData("eccode", eccode);
		log.info(logObj);
		BSpecialUser specialUser = specialUserBO.querySpecialUser(openid, eccode);
		specialUser.setVerifyexpireDate(null);
		specialUser.setVerifyStatus(WXAuthContant.VERIFYSTATUS_VERIFYING);
		specialUser.setModDate(new Date());
		if (specialUserBO.updateSpecialUser(specialUser)) {
			String verifycode = formVerifyCode(verifyType,eccode,openid);
			paramMap.put(ParamConstant.KEY_VERIFY_VERIFYCODE, verifycode);
			boolean memcached_result = saveVerifyToMemcache(sessionid,verifyType, eccode, phone,
					openid,registerid, verifycode,logObj);
			if(memcached_result){
				String smscontent = formVerifySmscontent(verifyType,eccode,verifycode,logObj);
				Map<String, String> smsMsp = new HashMap<String, String>();
				smsMsp.put(ParamConstant.KEY_SESSIONID, paramMap.get(ParamConstant.KEY_SESSIONID));
				smsMsp.put(ParamConstant.KEY_PHONENUMBER, phone);
				smsMsp.put(ParamConstant.KEY_SMSCONTENT, smscontent);
				int ret = smsBO.sendSMS(smsMsp);
				logObj.putData("sendresult", ret);
				if (ret == 0) {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_SUCCESS));
					result.setDescription("send smscontent success");
					log.info(logObj);
				} else {
					result.setResult(String
							.valueOf(WXResultConstant.RESULT_SENDSMS_FAIL));
					result.setDescription("send smscontent fail");
					log.error(logObj);
				}
			}else{
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_MEMCACHED_OPERATE_FAIL));
				result.setDescription("memcached operate fail");
			}
		} else {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_FAIL));
			result.setDescription("specialuser update fail");
		}
		logObj.putData("result", result.getResult());
		logObj.putData("desc", result.getDescription());
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

	/**
	 * 构造验证短信
	 */
	private String formVerifySmscontent(String verifytype,String eccode, String verifycode,
			LogObj logObj) throws Exception {
		String smscontent = "";
		if(verifytype.equals(WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_LINK)){
			smscontent = formVerifyLinkSmscontent(eccode,verifycode);
		}else if(verifytype.equals(WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE)){
			smscontent = formVerifyCodeSmscontent(eccode,verifycode);
		}
		logObj.putSysKey(LogObj.STEP, "formsmscontent");
		logObj.putData("smscontent", smscontent);
		log.info(logObj);
		return smscontent;
	}
	
	/**
	 * 构造验证链接短信
	 */
	protected String formVerifyLinkSmscontent(String eccode, String verifycode)
			throws Exception {
		String wxverify_verify_link = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxverify", "verify_link" }, String.class);
		Map<String, String> placeMap = new HashMap<String, String>();
		placeMap.put(CommonConstant.PLACE_VERIFYCODE, verifycode);
		placeMap.put(CommonConstant.PLACE_VERIFYTYPE, WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_LINK);
		wxverify_verify_link = StringUtils.placeHolderReform(1, wxverify_verify_link, placeMap);

		String smscontent = MessageUtil.getMessage(eccode+"_"+WXAuthContant.MSG_FUNCNAME_WX_SMS,
				WXAuthContant.MSG_FUNCNAME_WX_SMS,
				String.valueOf(WXMsgCodeConstant.WX_SMS_CODE_VERIFYLINK));
		placeMap.put(CommonConstant.PLACE_URL, wxverify_verify_link);
		smscontent = StringUtils.placeHolderReform(1, smscontent, placeMap);
		
		return smscontent;
	}
	
	/**
	 * 构造验证码短信
	 */
	private String formVerifyCodeSmscontent(String eccode, String verifycode) throws Exception {
		String smscontent = MessageUtil.getMessage(eccode + "_"
				+ WXAuthContant.MSG_FUNCNAME_WX_SMS,
				WXAuthContant.MSG_FUNCNAME_WX_SMS,
				String.valueOf(WXMsgCodeConstant.WX_SMS_CODE_VERIFYCODE));
		Map<String, String> placeMap = new HashMap<String, String>();
		placeMap.put(CommonConstant.PLACE_VERIFYCODE, verifycode);
		smscontent = StringUtils.placeHolderReform(1, smscontent, placeMap);

		return smscontent;
	}
	
	/**
	 * 保存内存库
	 * @param paramMap
	 * @param eccode
	 * @param phone
	 * @param openid
	 * @param logObj
	 * @param verifycode
	 * @return
	 */
	private boolean saveVerifyToMemcache(String sessionid,String verifytype,String eccode,
			String phone, String openid,String registerid, String verifycode, LogObj logObj) {
		String mem_key = formMemKey(verifytype, eccode,openid,verifycode);
		String mem_value = WXUtil.formVerifyData(eccode, openid,registerid, phone);
		String mem_encry_value = WXUtil.encry(mem_value);
		String timestamp_valid = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"wxauth", "timestamp_valid" }, String.class);
		long expired_msec = Long.parseLong(timestamp_valid);
		logObj.putSysKey(LogObj.STEP, "memcachedset");
		logObj.putData("mem_value",mem_value)
			.putData("mem_encry_value", mem_encry_value)
			.putData("mem_key",mem_key)
			.putData("expired_msec",expired_msec);
		boolean memcached_result = memCachedBO.set(sessionid, mem_key, mem_encry_value,expired_msec);
		if(memcached_result){
			log.info(logObj);
		}else{
			log.error(logObj);
		}
		return memcached_result;
	}
	
	@Override
	public String formMemKey(String verifytype,String eccode,String openid,String verifycode) {
		String mem_key = "";
		if(verifytype.equals(WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_LINK)){
			mem_key = CommonConstant.MEM_KEY_PREFIX_VERIFY + "." + verifytype+"." +SpecialAuthConstant.APPTYPE_WX+"."+verifycode;
		}else if(verifytype.equals(WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE)){
			mem_key = CommonConstant.MEM_KEY_PREFIX_VERIFY + "." + verifytype +"."+eccode+"."+openid+ "." +SpecialAuthConstant.APPTYPE_WX+"."+verifycode;
		}
		return mem_key;
	}
	
	/**
	 * 构造验证码
	 * 页面验证码是6位随机数
	 * 验证链接的验证码是一串加密字符串
	 * @param verifytype
	 * @param eccode
	 * @param openid
	 * @return
	 */
	private String formVerifyCode(String verifytype,String eccode,String openid){
		String verifycode = "";
		if(verifytype.equals(WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_LINK)){
			StringBuffer sb = new StringBuffer();
			sb.append(SpecialAuthConstant.APPTYPE_WX);
			sb.append(".");
			sb.append(eccode);
			sb.append(".");
			sb.append(openid);
			sb.append(".");
			sb.append(SystemUtil.getDate(new Date(), "yyyyMMddHHmmss"));
			sb.append(".");
			sb.append(StringUtils.getRandomString(5));
			verifycode = Password.encryptPassword(sb.toString(), 5);
			verifycode = verifycode.substring(8, 24);
		}else if(verifytype.equals(WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE)){
			int wx_verifycode_length = Integer.parseInt(MPClientUtils.callService(
					"/mp/portal/config/business/getConfig", new Object[] {
							"wxverify", "verifycode_length" }, String.class));
			verifycode = StringUtils.getRandomString(wx_verifycode_length);
		}
		return verifycode;
	}

	@Override
	public boolean validVerifyReq(Map<String, String> paramMap) {
		String verifytype = paramMap.get(ParamConstant.KEY_VERIFY_TYPE);
		String idfcode = paramMap.get(ParamConstant.KEY_VERIFY_VERIFYCODE);
		String phone = paramMap.get(ParamConstant.KEY_PHONE);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);

		if (StringUtils.isEmpty(verifytype)) {
			return false;
		}
		if (StringUtils.isEmpty(idfcode)) {
			return false;
		}
		if (WXReqTypeConstant.WX_REQTYPE_VERIFY_TYPE_VERIFYCODE.equals(verifytype)) {
			if (StringUtils.isEmpty(phone)) {
				return false;
			}
			if (StringUtils.isEmpty(eccode)) {
				return false;
			}
			if (StringUtils.isEmpty(openid)) {
				return false;
			}
		}
		return true;

	}

	@Override
	public Map<String, String> getMemValue(Map<String, String> paramMap) {
		String verifytype = paramMap.get(ParamConstant.KEY_VERIFY_TYPE);
		String verifycode = paramMap.get(ParamConstant.KEY_VERIFY_VERIFYCODE);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		
		String mem_key = formMemKey(verifytype, eccode, openid,
				verifycode);
		String mem_value = memCachedBO.get(paramMap.get(sessionid),
				mem_key);
        // 解决手机安全认证，去除获取之后删除问题。
        // if(!StringUtils.isEmpty(mem_value)){
        // memCachedBO.delete(sessionid, mem_key);
        // }
		Map<String, String> map = new HashMap<String, String>();
		map.put(ParamConstant.KEY_MEM_KEY, mem_key);
		map.put(ParamConstant.KEY_MEM_VALUE, mem_value);
		return map;
	}

	@Override
	public String formRedirectUrl(Map<String, String> paramMap,
			String redirectUrl, Map<String, String> finalParamMap) {
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String registerid = paramMap.get(ParamConstant.KEY_REGISTERID);
		String replyMsg_code = paramMap.get(ParamConstant.KEY_REPLYMSG_CODE);
		
		finalParamMap.put(ParamConstant.KEY_REPLYMSG_CODE,
				String.valueOf(replyMsg_code));
		finalParamMap.put(ParamConstant.KEY_ECCODE, eccode);
		finalParamMap.put(ParamConstant.KEY_WX_OPENID, openid);
		finalParamMap.put(ParamConstant.KEY_SESSIONID,
				paramMap.get(ParamConstant.KEY_SESSIONID));
		finalParamMap.put(ParamConstant.KEY_TIMESTAMP, FormatUtil.dateToTimestamp(new Date()));
		finalParamMap.put(WXAuthContant.WX_VERIFY_REQNAME, paramMap.get(WXAuthContant.WX_VERIFY_REQNAME));
		finalParamMap.put(ParamConstant.KEY_REGISTERID, registerid);
		
		String paramStr = RequestParamUtil.MapValueToString(finalParamMap);
		Map<String, String> placeMap = new HashMap<String, String>();
		placeMap.put(CommonConstant.PLACE_PARAMSTR, paramStr);
		if(StringUtils.isEmpty(eccode)){
			placeMap.put(CommonConstant.PLACE_ECCODE, "default");
		}else{
			placeMap.put(CommonConstant.PLACE_ECCODE, eccode);
		}
		try {
			redirectUrl = StringUtils.placeHolderReform(1, redirectUrl, placeMap);
		} catch (Exception e) {
			LogUtil.printErrorStackTrace(error, e);
		}
		return redirectUrl;
	}

	public SpecialUserBO getSpecialUserBO() {
		return specialUserBO;
	}

	public MemCachedBO getMemCachedBO() {
		return memCachedBO;
	}

	public SMSBO getSmsBO() {
		return smsBO;
	}

	public void setSpecialUserBO(SpecialUserBO specialUserBO) {
		this.specialUserBO = specialUserBO;
	}

	public void setMemCachedBO(MemCachedBO memCachedBO) {
		this.memCachedBO = memCachedBO;
	}

	public void setSmsBO(SMSBO smsBO) {
		this.smsBO = smsBO;
	}
}
