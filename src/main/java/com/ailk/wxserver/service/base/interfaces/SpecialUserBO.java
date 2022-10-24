package com.ailk.wxserver.service.base.interfaces;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.po.DQrcodeGroup;
import com.ailk.wxserver.service.constant.ParamConstant;
import com.ailk.wxserver.service.constant.SpecialAuthConstant;
import com.ailk.wxserver.service.constant.WXAuthContant;
import com.ailk.wxserver.service.constant.WXResultConstant;
import com.ailk.wxserver.util.ResponseResult;
import com.ailk.wxserver.util.StringUtils;
import com.ailk.wxserver.util.exception.DAOException;

public interface SpecialUserBO {

	/**
	 * 查询微信用户
	 * @param openid
	 * @param eccode
	 * @return
	 */
	public BSpecialUser querySpecialUser(String openid, String eccode) throws DAOException;
	
	
	/**
	 * 校验是否需要验证手机号
	 * @param specialUser
	 * @return 0-已验证过，不需要验证   1-需要验证：宽带开户不成功、未验证或验证失败 2-需要验证：验证已经过期
	 */
	public int isNeedVerifyPhone(BSpecialUser specialUser);
	
	
	/**
	 * 校验微信用户状态
	 * @param specialUser
	 * @return
	 */
	public int validSpeicalUserStatus(BSpecialUser specialUser);
	
	/**
	 * 修改specialuser
	 * @param specialUser
	 * @return
	 */
	public boolean updateSpecialUser(BSpecialUser specialUser)throws DAOException;
	
	/**
	 * specialuser存在，新开宽带用户，并根据开户结果设置开户状态
	 * @param specialUser
	 * @return
	 * @throws DAOException
	 */
	public ResponseResult openBroadUserAndUpdateSpecialUser(
			BSpecialUser specialUser, Map<String, String> userMap) throws DAOException ;
	
	/**
	 * specialuser不存在，新增specialuser并开宽带用户
	 * 
	 * @param registerid
	 * @param eccode
	 * @param openid
	 * @return
	 * @throws DAOException
	 * @throws Exception
	 */
	public ResponseResult addSpecialUserAndOpenBroadUser(Map<String, String> paramMap) throws DAOException ;
	
	/**
	 * 删除微信用户及微信宽带用户、取消mac绑定
	 * @return
	 * @throws DAOException
	 */
	public ResponseResult closeSpecialUser(
			BSpecialUser specialUser, Map<String, String> userMap) throws DAOException;
	
	/**
	 * 获取关注组别
	 * @param eccode
	 * @param evenKey
	 * @return
	 */
	public int getQrGroupID(String eccode, String evenKey);
	
	/**
	 * 根据宽带用户名查询微信用户
	 * @param eccode
	 * @param username
	 * @return
	 */
	public BSpecialUser querySpecialUserByUsername(String eccode,String username) throws DAOException;
}
