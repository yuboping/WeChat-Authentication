package com.ailk.wxserver.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ailk.wxserver.util.log.LogFactory;

public class AbstractDaoImpl {

	private static Logger error = LogFactory.getLogger("error");

	
	protected DataSource dataSource;
	
	
	/**
	 * 从数据库连接池中获取连接
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Connection createConn() throws Exception
	{
		return  dataSource.getConnection();
	}
	
	/**
	 * 设置自动提交属性
	 * 
	 * @param conn
	 * @param type
	 * @throws Exception
	 */
	public void setAutoCommit(Connection conn, boolean type) throws Exception {
		if (conn.getAutoCommit() != type) {
			conn.setAutoCommit(type);
		}
	}

	/**
	 * 回滚
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			error.error("Fail to rollback", e);
		}
	}

	/**
	 * 提交
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public void commit(Connection conn) throws Exception {
		conn.commit();
	}

	/**
	 * java.util.Date ->java.sql.Timestamp
	 * 
	 * @param date
	 * @return
	 */
	public Timestamp date2Timestamp(Date date) {
		if (date != null) {
			return new java.sql.Timestamp(date.getTime());
		} else {
			return null;
		}

	}

	/**
	 * java.sql.Timestamp -> java.util.Date
	 * 
	 * @param timestamp
	 * @return
	 */
	public Date timestamp2Date(Timestamp timestamp) {
		if (timestamp != null) {
			return new Date(timestamp.getTime());
		} else {
			return null;
		}

	}

	/**
	 * 关闭数据库连接，结果集等
	 * 
	 * @param conn
	 * @param pstmt
	 * @param set
	 */
	public void close(Connection conn, Statement stat, ResultSet set) {
		if (set != null) {
			try {
				set.close();
			} catch (SQLException e) {
				error.error("Exception to close ResultSet", e);
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				error.error("Exception to close Statement", e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				error.error("Exception to close Connection", e);
			}
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
