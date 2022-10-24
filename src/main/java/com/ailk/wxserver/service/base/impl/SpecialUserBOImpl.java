package com.ailk.wxserver.service.base.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.dao.interfaces.SpecialUserDAO;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.po.DQrcodeGroup;
import com.ailk.wxserver.service.base.interfaces.BroadUserBO;
import com.ailk.wxserver.service.base.interfaces.MacOperateBO;
import com.ailk.wxserver.service.base.interfaces.SpecialUserBO;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.SpecialAuthConstant;
import com.ailk.wxserver.service.constant.UserConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.TimeUtil;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogFactory;
import com.ailk.wxserver.util.log.LogObj;

public class SpecialUserBOImpl implements SpecialUserBO {

	private SpecialUserDAO specialUserDAO = null;
	private BroadUserBO broadUserBO = null;
	private MacOperateBO macOperateBO = null;
	
	private Logger log = LogFactory.getLogger("specialuser");
	
	@Override
	public BSpecialUser querySpecialUser(String openid, String eccode) throws DAOException {
		String username = broadUserBO.generateUserName(
				SpecialAuthConstant.APPTYPE_WX_VALUE, eccode, openid);
		BSpecialUser specialUser = specialUserDAO.query(eccode, openid,
				SpecialAuthConstant.APPTYPE_WX_VALUE,username);
		return specialUser;
	}

	@Override
	public int isNeedVerifyPhone(BSpecialUser specialUser){
		int result = 0;
		if(specialUser.getOpenuserStatus() != WXAuthContant.OPENUSERSTATUS_OK){
			result = 1;
		}else if(specialUser.getVerifyStatus() == WXAuthContant.VERIFYSTATUS_NO){
			result = 1;
		}else if (specialUser.getVerifyStatus() == WXAuthContant.VERIFYSTATUS_VERIFYING){
			result = 1;
		}else if (specialUser.getVerifyStatus() == WXAuthContant.VERIFYSTATUS_VERIFYFAIL){
			result = 1;
		}else{
			if(TimeUtil.isExpired(specialUser.getVerifyexpireDate())){
				result = 2;
			}
		}
		return result ;
	}

	@Override
	public int validSpeicalUserStatus(BSpecialUser specialUser){
		if(specialUser == null){
			return UserConstant.WXUSER_SPECIALUSER_NOTEXIST;
		}
		if(specialUser.getOpenuserStatus() != WXAuthContant.OPENUSERSTATUS_OK){
			return UserConstant.WXUSER_BROADUSER_NOORFAIL;
		}
		if(specialUser.getVerifyStatus() != WXAuthContant.VERIFYSTATUS_VERIFYOK){
			return UserConstant.WXUSER_VERIFY_NOORFAIL;
		}
		if(TimeUtil.isExpired(specialUser.getVerifyexpireDate())){
			return UserConstant.WXUSER_VERIFY_EXPIRED;
		}
		return UserConstant.WXUSER_NORMAIL;
	}

	@Override
	public ResponseResult openBroadUserAndUpdateSpecialUser(
			BSpecialUser specialUser, Map<String, String> userMap)
			throws DAOException {
		String registerid = userMap.get(ParamConstant.KEY_REGISTERID);
		String sessionid = userMap.get(ParamConstant.KEY_SESSIONID);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "openBroadUserAndUpdateSpecialUser")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(ParamConstant.KEY_REGISTERID, registerid)
				.putData(ParamConstant.KEY_ECCODE, specialUser.getEccode())
				.putData(ParamConstant.KEY_WX_OPENID, specialUser.getOpenid());
		log.info(logObj);
		
		ResponseResult result = new ResponseResult();
		Map<String, String> openUserMap = new HashMap<String, String>();
		openUserMap.put(ParamConstant.KEY_REGISTERID, registerid);
		openUserMap.put(ParamConstant.KEY_ECCODE, specialUser.getEccode());
		openUserMap.put(ParamConstant.KEY_WX_OPENID, specialUser.getOpenid());
		String username = broadUserBO.generateUserName(
				SpecialAuthConstant.APPTYPE_WX_VALUE, specialUser.getEccode(),
				specialUser.getOpenid());
		openUserMap.put(ParamConstant.KEY_USERNAME, username);
		String password = broadUserBO.generatePwd(username);
		openUserMap.put(ParamConstant.KEY_PASSWORD, password);
		openUserMap.put(ParamConstant.KEY_SESSIONID, sessionid);
		ResponseResult openUserResult = broadUserBO.openUser(openUserMap);
		if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				openUserResult.getResult())) {
			result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			result.setDescription("aaa openuser success");
			specialUser.setOpenuserStatus(WXAuthContant.OPENUSERSTATUS_OK);
		} else {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
			result.setDescription("aaa openuser fail");
			specialUser.setOpenuserStatus(WXAuthContant.OPENUSERSTATUS_FAIL);
		}
		specialUser.setPhone("");
		specialUser.setVerifyexpireDate(null);
		specialUser.setVerifyStatus(WXAuthContant.VERIFYSTATUS_NO);
		specialUser.setModDate(new Date());
		boolean updateFlag = updateSpecialUser(specialUser);
		if (!updateFlag) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_FAIL));
			result.setDescription("specialuser update fail");
		}

		logObj.putData("result", result.getResult()).putData("desc",
				result.getDescription());
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
	public boolean updateSpecialUser(BSpecialUser specialUser) throws DAOException {
		boolean updateFlag = specialUserDAO.update(specialUser);
		return updateFlag;
	}

	@Override
	/**
	 * 获取关注组别
	 * @param eccode
	 * @param evenKey
	 * @return
	 */
	public int getQrGroupID(String eccode, String evenKey) {
		if(!StringUtils.isEmpty(evenKey)&& evenKey.startsWith(WXAuthContant.WX_EVENTKEY_PREFIX)){
			evenKey = evenKey.substring(WXAuthContant.WX_EVENTKEY_PREFIX.length());
		}
		DQrcodeGroup qrcodeGroup = MPClientUtils.callService(
				"/mp/portal/dQrcodeGroup/getQrcodeGroup",
				new Object[] {evenKey,eccode },
				DQrcodeGroup.class);
		int qrgroupid = -1;
		if(qrcodeGroup != null){
			qrgroupid = qrcodeGroup.getGroupid();
		}
		return qrgroupid;
	}

	@Override
	public ResponseResult addSpecialUserAndOpenBroadUser(Map<String, String> paramMap) throws DAOException {
		String registerid = paramMap.get(ParamConstant.KEY_REGISTERID);
		String eccode = paramMap.get(ParamConstant.KEY_ECCODE);
		String openid = paramMap.get(ParamConstant.KEY_WX_OPENID);
		String phone = paramMap.get(ParamConstant.KEY_PHONE);
		int qrgroupid = Integer.parseInt(paramMap.get(ParamConstant.KEY_QRGROUPID));
		String sessionid = paramMap.get(ParamConstant.KEY_SESSIONID);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "addSpecialUserAndOpenBroadUser")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(ParamConstant.KEY_REGISTERID, registerid)
				.putData(ParamConstant.KEY_ECCODE,eccode)
				.putData(ParamConstant.KEY_WX_OPENID, openid)
				.putData(ParamConstant.KEY_PHONE,phone)
				.putData(ParamConstant.KEY_QRGROUPID,qrgroupid);
		log.info(logObj);
		
		ResponseResult result = new ResponseResult();
		BSpecialUser specialUser = new BSpecialUser();
		specialUser.setEccode(eccode);
		specialUser.setOpenid(openid);
		specialUser.setApptype(SpecialAuthConstant.APPTYPE_WX_VALUE);
		specialUser.setPhone("");
		specialUser.setVerifyexpireDate(null);
		specialUser.setVerifyStatus(WXAuthContant.VERIFYSTATUS_NO);
		specialUser.setOpenuserStatus(WXAuthContant.OPENUSERSTATUS_NO);
		specialUser.setOpenDate(new Date());
		specialUser.setModDate(null);
		specialUser.setQrgroupid(qrgroupid);
		String username = broadUserBO.generateUserName(
				SpecialAuthConstant.APPTYPE_WX_VALUE, eccode, openid);
		specialUser.setUsername(username);
		boolean addFlag = specialUserDAO.add(specialUser);
		if (addFlag) {
			Map<String, String> userMap = new HashMap<String, String>();
			userMap.put(ParamConstant.KEY_REGISTERID, registerid);
			userMap.put(ParamConstant.KEY_ECCODE, eccode);
			userMap.put(ParamConstant.KEY_WX_OPENID, openid);
			userMap.put(ParamConstant.KEY_USERNAME, username);
			String password = broadUserBO.generatePwd(username);
			userMap.put(ParamConstant.KEY_PASSWORD, password);
			userMap.put(ParamConstant.KEY_PHONE, phone);
			userMap.put("sessionid",sessionid );
			ResponseResult openUserResult = broadUserBO.openUser(userMap);
			if (String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
					openUserResult.getResult())) {
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_SUCCESS));
				result.setDescription("aaa openuser success");
				specialUser.setOpenuserStatus(WXAuthContant.OPENUSERSTATUS_OK);
				specialUser.setModDate(new Date());
			} else {
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
				result.setDescription("aaa openuser fail");
				specialUser
						.setOpenuserStatus(WXAuthContant.OPENUSERSTATUS_FAIL);
				specialUser.setModDate(new Date());
			}
			boolean updateFlag = specialUserDAO.update(specialUser);
			if (!updateFlag) {
				result.setResult(String
						.valueOf(WXResultConstant.RESULT_OPERATEDB_FAIL));
				result.setDescription("specialuser update fail");
			}
		} else {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_FAIL));
			result.setDescription("specialuser add fail");
		}
		
		logObj.putData("result", result.getResult()).putData("desc",
				result.getDescription());
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
	public ResponseResult closeSpecialUser(
			BSpecialUser specialUser, Map<String, String> userMap) throws DAOException{
		String registerid = userMap.get(ParamConstant.KEY_REGISTERID);
		String sessionid = userMap.get(ParamConstant.KEY_SESSIONID);
		
		LogObj logObj = new LogObj();
		logObj.putSysKey(LogObj.COLLECT, "y").putSysKey(LogObj.SID, sessionid)
				.putSysKey(LogObj.MODULE, "closeSpecialUser")
				.putSysKey(LogObj.STEP, "request");
		logObj.putData(ParamConstant.KEY_REGISTERID, registerid)
			.putData(ParamConstant.KEY_ECCODE, specialUser.getEccode())
			.putData(ParamConstant.KEY_WX_OPENID, specialUser.getOpenid());
		log.info(logObj);
		
		ResponseResult result = new ResponseResult();
		boolean deleteDBFlag = specialUserDAO.delete(specialUser);
		ResponseResult closeUserResult = null;
		ResponseResult cancelMacBindResult = null;
		if (specialUser.getOpenuserStatus() == WXAuthContant.OPENUSERSTATUS_OK) {
			String username = broadUserBO.generateUserName(
					SpecialAuthConstant.APPTYPE_WX_VALUE, specialUser.getEccode(),
					specialUser.getOpenid());
			closeUserResult = closeBroadUser(specialUser, registerid,username,sessionid);
			cancelMacBindResult = cancelMacBind(specialUser, username,sessionid);
		}else{
			closeUserResult = new ResponseResult();
			closeUserResult.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			closeUserResult.setDescription("openuserstatus is not ok,not need close broaduser");
			cancelMacBindResult = new ResponseResult();
			cancelMacBindResult.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			cancelMacBindResult.setDescription("openuserstatus is not ok,not need cancel macbind");
		}
		if (deleteDBFlag
				&& String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
						closeUserResult.getResult()) && String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
								cancelMacBindResult.getResult())) {
			result.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			result.setDescription("delete specialuser、 close broad user and cancel macbind success");
		} else if (!deleteDBFlag) {
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_OPERATEDB_FAIL));
			result.setDescription("delete specialuser fail");
		} else if(!String.valueOf(WXResultConstant.RESULT_SUCCESS).equals(
				closeUserResult.getResult())){
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_AAAREQ_FAIL));
			result.setDescription("aaa close broad user fail");
		} else{
			result.setResult(String
					.valueOf(WXResultConstant.RESULT_MACREQ_FAIL));
			result.setDescription("cancel macbind fail");
		}
		
		logObj.putData("result", result.getResult()).putData("desc",
				result.getDescription());
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
	 * mac取消绑定
	 * @param specialUser
	 * @param username
	 * @return
	 */
	private ResponseResult cancelMacBind(BSpecialUser specialUser,String username,String sessionid){
		ResponseResult cancelMacBindResult;
		String macsupportflag = MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"mac", "macsupportflag" }, String.class);
		if("true".equals(macsupportflag)){
			Map<String, String> macMap = new HashMap<String, String>();
			macMap.put(ParamConstant.KEY_ECCODE, specialUser.getEccode());
			macMap.put(ParamConstant.KEY_USERNAME, username);
			macMap.put(ParamConstant.KEY_SOURCETYPE, String.valueOf(SpecialAuthConstant.SOURCETYPE_WX_VALUE));
			
			macMap.put("sessionid",sessionid );
			
			cancelMacBindResult = macOperateBO.directCancelMacBind(macMap);
		}else{
			cancelMacBindResult = new ResponseResult();
			cancelMacBindResult.setResult(String.valueOf(WXResultConstant.RESULT_SUCCESS));
			cancelMacBindResult.setDescription("config is false,don't need mac cancel");
		}
		
		return cancelMacBindResult;
		
	}
	/**
	 * 宽带销户
	 */
	private ResponseResult closeBroadUser(BSpecialUser specialUser,
			String registerid,String username,String sessionid) {
		ResponseResult closeUserResult;
		Map<String, String> userMap = new HashMap<String, String>();
		userMap.put(ParamConstant.KEY_REGISTERID, registerid);
		userMap.put(ParamConstant.KEY_ECCODE, specialUser.getEccode());
		userMap.put(ParamConstant.KEY_WX_OPENID, specialUser.getOpenid());
		userMap.put(ParamConstant.KEY_USERNAME, username);
		userMap.put("sessionid",sessionid );
		closeUserResult = broadUserBO.closeUser(userMap);
		return closeUserResult;
	}

	@Override
	public BSpecialUser querySpecialUserByUsername(String eccode,
			String username) throws DAOException {
		BSpecialUser specialUser = specialUserDAO.query(eccode, username);
		return specialUser;
	}
	
	public SpecialUserDAO getSpecialUserDAO() {
		return specialUserDAO;
	}

	public BroadUserBO getBroadUserBO() {
		return broadUserBO;
	}

	public MacOperateBO getMacOperateBO() {
		return macOperateBO;
	}

	public void setSpecialUserDAO(SpecialUserDAO specialUserDAO) {
		this.specialUserDAO = specialUserDAO;
	}

	public void setBroadUserBO(BroadUserBO broadUserBO) {
		this.broadUserBO = broadUserBO;
	}

	public void setMacOperateBO(MacOperateBO macOperateBO) {
		this.macOperateBO = macOperateBO;
	}

}
