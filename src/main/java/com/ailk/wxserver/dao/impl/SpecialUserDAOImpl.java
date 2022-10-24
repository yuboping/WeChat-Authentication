package com.ailk.wxserver.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.ailk.wxserver.dao.interfaces.SpecialUserDAO;
import com.ailk.wxserver.po.BSpecialUser;
import com.ailk.wxserver.util.Hash163;
import com.ailk.wxserver.util.LogUtil;
import com.ailk.wxserver.util.exception.DAOException;
import com.ailk.wxserver.util.log.LogFactory;

public class SpecialUserDAOImpl extends AbstractDaoImpl implements
		SpecialUserDAO {

	private static Logger log = LogFactory.getLogger("sql");
	private static Logger error = LogFactory.getLogger("error");


	public boolean delete(BSpecialUser user) throws DAOException {
		boolean ret = false;
		Connection conn = null;
		Statement stmt = null;
		String deleteSql = "";
		try {
			deleteSql = deleteSpecialUserSql(user.getEccode(),
					user.getOpenid(), user.getApptype(),user.getUsername());
			log.info("deleteSql:" + deleteSql);
			conn = createConn();
			setAutoCommit(conn, false);
			stmt = conn.createStatement();
			int count = stmt.executeUpdate(deleteSql);
			if (count > 0) {
				commit(conn);
				ret = true;
			} else {
				rollback(conn);
			}
		} catch (Exception e) {
			log.error("exec delete exception , sql:" + deleteSql.toString());
			LogUtil.printErrorStackTrace(error, e);
			rollback(conn);
			throw new DAOException("exec delete exception", e);
		} finally {
			close(conn, stmt, null);
		}

		return ret;
	}

	@Override
	public boolean update(BSpecialUser user) throws DAOException {
		boolean ret = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String updateSql = "";
		try {
			updateSql = updateSpecialUserSql(user.getUsername());
			log.info("updateSql:" + updateSql + "," + user);
			conn = createConn();
			setAutoCommit(conn, false);
			pstmt = conn.prepareStatement(updateSql);
			pstmt.setString(1, user.getPhone());
			pstmt.setInt(2, user.getVerifyStatus());
			pstmt.setInt(3, user.getOpenuserStatus());
			pstmt.setTimestamp(4, date2Timestamp(user.getVerifyexpireDate()));
			pstmt.setTimestamp(5, date2Timestamp(user.getOpenDate()));
			pstmt.setTimestamp(6, date2Timestamp(user.getModDate()));
			pstmt.setString(7, user.getUsername());
			pstmt.setInt(8, user.getQrgroupid());
			pstmt.setString(9, user.getOpenid());
			pstmt.setString(10, user.getEccode());
			pstmt.setInt(11, user.getApptype());
			int count = pstmt.executeUpdate();
			if (count > 0) {
				commit(conn);
				ret = true;
			} else {
				rollback(conn);
			}
		} catch (Exception e) {
			log.error("exec update exception , sql:" + updateSql.toString());
			LogUtil.printErrorStackTrace(error, e);
			rollback(conn);
			throw new DAOException("exec update exception", e);
		} finally {
			close(conn, pstmt, null);
		}

		return ret;
	}

	@Override
	public boolean add(BSpecialUser user) throws DAOException {
		boolean ret = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String addSql = "";
		try {
			addSql = addSpecialUserSql(user.getUsername());
			log.info("addSql:" + addSql + "," + user);
			conn = createConn();
			setAutoCommit(conn, false);
			pstmt = conn.prepareStatement(addSql);
			pstmt.setString(1, user.getOpenid());
			pstmt.setString(2, user.getEccode());
			pstmt.setInt(3, user.getApptype());
			pstmt.setString(4, user.getPhone());
			pstmt.setInt(5, user.getVerifyStatus());
			pstmt.setInt(6, user.getOpenuserStatus());
			pstmt.setTimestamp(7, date2Timestamp(user.getVerifyexpireDate()));
			pstmt.setTimestamp(8, date2Timestamp(user.getOpenDate()));
			pstmt.setTimestamp(9, date2Timestamp(user.getModDate()));
			pstmt.setString(10, user.getUsername());
			pstmt.setInt(11, user.getQrgroupid());
			int count = pstmt.executeUpdate();
			if (count > 0) {
				commit(conn);
				ret = true;
			} else {
				rollback(conn);
			}
		} catch (Exception e) {
			log.error("exec add exception , sql:" + addSql.toString());
			LogUtil.printErrorStackTrace(error, e);
			rollback(conn);
			throw new DAOException("exec add exception", e);
		} finally {
			close(conn, pstmt, null);
		}

		return ret;
	}

	@Override
	public BSpecialUser query(String eccode, String openid, int apptype,String username)
			throws DAOException {
		BSpecialUser user = null;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String querysql = "";
		try {
			querysql = querySpecialUserSql(eccode, openid, apptype,username);
			log.debug("querySpecialUser:" + querysql);
			conn = createConn();
			st = conn.createStatement();

			rs = st.executeQuery(querysql);
			if (rs.next()) {
				user = new BSpecialUser();
				user.setOpenid(openid);
				user.setEccode(eccode);
				user.setApptype(apptype);
				user.setPhone(rs.getString("phone"));
				user.setVerifyStatus(rs.getInt("verifystatus"));
				user.setOpenuserStatus(rs.getInt("openuserstatus"));
				user.setVerifyexpireDate(timestamp2Date(rs
						.getTimestamp("verifyexpiredate")));
				user.setOpenDate(timestamp2Date(rs.getTimestamp("opendate")));
				user.setModDate(timestamp2Date(rs.getTimestamp("moddate")));
				user.setUsername(rs.getString("username"));
				user.setQrgroupid(rs.getInt("qrgroupid"));
			}
		} catch (Exception e) {
			log.error("exec query exception , sql:" + querysql.toString());
			LogUtil.printErrorStackTrace(error, e);
			throw new DAOException("exec query exception", e);
		} finally {
			close(conn, st, rs);
		}
		return user;
	}

	@Override
	public BSpecialUser query(String eccode, String username)
			throws DAOException {
		BSpecialUser user = null;
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String querysql = "";
		try {
			querysql = querySpecialUserSql(eccode, username);
			log.debug("querySpecialUser:" + querysql);
			conn = createConn();
			st = conn.createStatement();

			rs = st.executeQuery(querysql);
			if (rs.next()) {
				user = new BSpecialUser();
				user.setOpenid(rs.getString("openid"));
				user.setEccode(eccode);
				user.setApptype(rs.getInt("apptype"));
				user.setPhone(rs.getString("phone"));
				user.setVerifyStatus(rs.getInt("verifystatus"));
				user.setOpenuserStatus(rs.getInt("openuserstatus"));
				user.setVerifyexpireDate(timestamp2Date(rs
						.getTimestamp("verifyexpiredate")));
				user.setOpenDate(timestamp2Date(rs.getTimestamp("opendate")));
				user.setModDate(timestamp2Date(rs.getTimestamp("moddate")));
				user.setUsername(rs.getString("username"));
				user.setQrgroupid(rs.getInt("qrgroupid"));
			}
		} catch (Exception e) {
			log.error("exec query exception , sql:" + querysql.toString());
			LogUtil.printErrorStackTrace(error, e);
			throw new DAOException("exec query exception", e);
		} finally {
			close(conn, st, rs);
		}
		return user;
	}
	
	/**
	 * 构造delete sql
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String deleteSpecialUserSql(String eccode, String openid, int apptype,String username) throws Exception {
		StringBuffer sql = new StringBuffer();
		String tablename = BSpecialUser.TABLE_NAME+Hash163.sHash(username);
		sql.append("delete from ").append(tablename)
				.append(" where openid ='").append(openid)
				.append("' and eccode='").append(eccode).append("' and apptype=")
				.append(apptype);
		return sql.toString();
	}

	/**
	 * 构造update sql
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String updateSpecialUserSql(String username) throws Exception {
		StringBuffer sql = new StringBuffer();
		String tablename = BSpecialUser.TABLE_NAME+Hash163.sHash(username);
		sql.append("update ")
				.append(tablename)
				.append(" set phone=?,verifystatus=?,openuserstatus=?,verifyexpiredate=?,opendate=?,moddate=?, username=?,qrgroupid=? where openid =? and eccode=? and apptype=?");
		return sql.toString();
	}

	/**
	 * 构造insert sql
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String addSpecialUserSql(String username) throws Exception {
		StringBuffer sql = new StringBuffer();
		String tablename = BSpecialUser.TABLE_NAME+Hash163.sHash(username);
		sql.append("insert into ")
				.append(tablename)
				.append("(openid,eccode,apptype,phone,verifystatus,openuserstatus,verifyexpiredate,opendate,moddate,username,qrgroupid) values(?,?,?,?,?,?,?,?,?,?,?)");
		return sql.toString();
	}

	/**
	 * 构造select sql
	 * 
	 * @param eccode
	 * @param openid
	 * @param apptype
	 * @return
	 * @throws Exception 
	 */
	private String querySpecialUserSql(String eccode, String openid, int apptype,String username) throws Exception {
		StringBuffer sql = new StringBuffer();
		String tablename = BSpecialUser.TABLE_NAME+Hash163.sHash(username);
		sql.append("select * from ").append(tablename)
				.append(" where openid='").append(openid)
				.append("' and eccode='").append(eccode).append("' and apptype=")
				.append(apptype);
		return sql.toString();
	}
	
	/**
	 * 构造select sql
	 * 
	 * @param eccode
	 * @param openid
	 * @param apptype
	 * @return
	 * @throws Exception 
	 */
	private String querySpecialUserSql(String eccode,String username) throws Exception {
		StringBuffer sql = new StringBuffer();
		String tablename = BSpecialUser.TABLE_NAME+Hash163.sHash(username);
		sql.append("select * from ").append(tablename)
				.append(" where  eccode='").append(eccode).append("' and username='")
				.append(username).append("'");
		return sql.toString();
	}

}
