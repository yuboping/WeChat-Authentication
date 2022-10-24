package com.ailk.wxserver.dao.interfaces;

import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.util.exception.DAOException;

public interface SpecialUserDAO {

	
	/**
	 * 删除用户
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean delete(BSpecialUser user) throws DAOException;
	
	/**
	 * 修改用户
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean update(BSpecialUser user) throws DAOException;
	
	/**
	 * 新增用户
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean add(BSpecialUser user) throws DAOException;
	
	/**
	 * 查询认证用户
	 * @param eccode
	 * @param openid
	 * @param type
	 * @param username -- 用来计算分表hash
	 * @return
	 */
	public BSpecialUser query(String eccode,String openid,int type,String username) throws DAOException; 
	
	/**
	 * 根据宽带用户名查询微信用户
	 * @param eccode
	 * @param username
	 * @return
	 * @throws DAOException
	 */
	public BSpecialUser query(String eccode,String username) throws DAOException;
}
