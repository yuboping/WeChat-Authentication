package com.ailk.wxserver.dao.mp.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ailk.lcims.support.mp.MPContext;
import com.ailk.lcims.support.util.GenericTypeReference;
import com.ailk.wxserver.dao.mp.interfaces.MyBaseMPDAO;
import com.ailk.wxserver.util.SystemUtil;
import com.ailk.wxserver.util.config.ConfigUtil;
import com.ailk.wxserver.util.config.model.Config;
import com.ailk.wxserver.util.log.LogFactory;

public abstract class MyBaseMPDAOImpl implements  MyBaseMPDAO{
	/**
	 * 考虑做成配置
	 */
	public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";


	private static Logger log = LogFactory.getLogger("sql");

	private DataSource dataSource;
	private String poClassname;
	private String tableName;

	@SuppressWarnings({ "rawtypes" })
	public List getAllOrderby(String orderby) throws Exception {
		List list=null;
		if(!isStartupWithoutDB()){
			try {
				list= getDBAllOrderby(orderby);
				this.persist(list);
			}catch(Exception e){
				if(startupAdaptivity()){
					list=this.depersist();
				}else{
					throw e;
				}
			}
		}else{
			try {
				list=this.depersist();
			}catch(Exception e){
				if(startupAdaptivity()){
					list= getDBAllOrderby(orderby);
					this.persist(list);
				}else{
					throw e;
				}
			}
		}
		
		
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getDBAllOrderby(String orderby) throws Exception {
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		ResultSetMetaData rsmd=null;
		ArrayList list = new ArrayList();
		Object PO_OBJ;
		
		try {
			Class PO_CLASS=Class.forName(poClassname);
			conn=dataSource.getConnection();
			String sql = "select * from " + this.tableName;
			if (orderby != null && !orderby.equals("")) {
				sql = sql + " order by " + orderby;
			}
			log.debug("getAllOrderby sql:" + sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rsmd = rs.getMetaData();
			
			List relations=getRelations(rsmd,PO_CLASS);
			
			while (rs.next()) {
				PO_OBJ=PO_CLASS.newInstance();
				for (int i = 0; i < relations.size(); i++) {
					
					Relation relation=(Relation)relations.get(i);
					
					String colName =relation.getColName();
					String setMethodName = relation.getSetMethodName();
					Class fieldType=relation.getFieldType();
					
					setField(PO_OBJ, setMethodName, fieldType, rs.getString(colName));
					
				}
				list.add(PO_OBJ);
			}
			
		} catch (Exception e) {
			throw e;
		}finally{
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
		return list;
	}

	public abstract List<?> getAll() throws Exception ;


	/**
	 * 获取表和PO的对应关系（目前使用时必须先获取结果集的列属性rsmd）
	 * 
	 * @return List 顺序与表字段一致；List中每个对象为一Map 包括String colName、String
	 *         setName、String getName、Class fieldType
	 * @throws Exception
	 */
	private List<Relation> getRelations(ResultSetMetaData rsmd,Class<?> PO_CLASS) throws Exception {
		List<Relation> relations = new ArrayList<Relation>();

		int colCount = rsmd.getColumnCount();
		Method[] methods = PO_CLASS.getMethods();
		
		for (int i = 0; i < colCount; i++) {
			String colName = rsmd.getColumnName(i + 1);
			for (int j = 0; j < methods.length; j++) {
				String mName = methods[j].getName();
				//方法名不是以get开头或者是getClass，则继续匹配下一个方法
				if (!mName.startsWith("get") || mName.equals("getClass"))
					continue;
				//是除getClass以外get方法，截取第4位以后字符串与表字段做忽略大小写的匹配
				String poFieldName = mName.substring(3);
				if (!poFieldName.equalsIgnoreCase(colName))
					continue;
				
				
				Class<?> fieldType = methods[j].getReturnType();

				Relation relation=new Relation();
				relation.setColName(colName);
				relation.setSetMethodName("set" + poFieldName);
				relation.setGetMethodName(mName);
				relation.setFieldType(fieldType);
				relations.add(relation);
			}
		}
		return relations;
	}
	
	/**
	 * 为对象填充值
	 * @param obj
	 * @param setMethodName
	 * @param fieldType
	 * @param value
	 * @throws Exception
	 */
	private void setField(Object obj,String setMethodName,Class<?> fieldType,String value)throws Exception {
		Method m = obj.getClass().getMethod(setMethodName,
				new Class[] { fieldType });
		if (fieldType == int.class) {
			if(value==null) return;
			m.invoke(obj, new Object[] { Integer.parseInt(value)});
		} else if (fieldType == String.class) {
			m.invoke(obj, new Object[] { value});
		} else if (fieldType == Date.class) {
			SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT);
			Date date = formater.parse(value);
			m.invoke(obj, new Object[] {date});
		} else {
			throw new Exception("非支持的字段类型[" + fieldType.getName() + "]at"+ setMethodName);
		}
	}

	
	
	/**
	 * MP持久化方法
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public void persist(List<?> all) throws Exception {

		FileOutputStream fos =null;
		try {
			
			File file = new File(getFullFilename());
			file.createNewFile();
			fos= new FileOutputStream(file);
			if (all != null && all.size() > 0) {
				for(Object obj :all){
					Class objClass = obj.getClass();
					Method[] methods = objClass.getMethods();
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < methods.length; i++) {
						Method method = methods[i];
						String methodName = method.getName();
						if (methodName.startsWith("get") && !(methodName.equals("getClass"))) {
							sb.append(methodName.substring(3) + "===" +method.invoke(obj)+":::");
						}
					}
					sb.append('\n');
					fos.write(sb.toString().getBytes());
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(fos!=null)fos.close();
		}
	}
	
	/**
	 * MP反持久化方法
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List depersist() throws Exception {
		BufferedReader reader = null;
		List all=new ArrayList();
		try {
			Class<?> poClass=Class.forName(poClassname);
			String fullFileName=getFullFilename();
			File file = new File(fullFileName);
			if (file.exists() == false) {
				throw new Exception("持久化文件不存在:"+fullFileName);
			}
			
			reader=new BufferedReader(new  InputStreamReader(new FileInputStream(file)));
			
			String line=null;
			while ( (line = reader.readLine()) != null) {
				Object obj = poClass.newInstance();
				Method[] methods = poClass.getMethods();
				
				//such as columnname1===columnvalue1:::columnname2===columnvalue2
				String[] columns = line.split(":::");
				for (int i = 0; i < columns.length ; i++) {
					//such as columnname1===columnvalue1
					String[] columnInfo = columns[i].split("===");
					String columnName = columnInfo[0];
					String columnValue = columnInfo[1];
					Method m = null;
					for (int j = 0; j < methods.length; j++) {
						if (methods[j].getName().equals("set"+columnName)) {
							m = methods[j];
							break;
						}
					}
					if(m==null){continue;}
					Class paramClass = (m.getParameterTypes())[0];
					if (paramClass == int.class ) {
						m.invoke(obj,new Object[]{Integer.parseInt(columnValue)});
					}else if (paramClass == String.class) {
						m.invoke(obj,new Object[]{columnValue});
					}else if (paramClass == Date.class) {
						m.invoke(obj,new Object[]{SystemUtil.getDateFromString(columnValue)});
					}else {
						throw new Exception("非支持的字段类型"+paramClass+";"+columnName);
					}
				}
				all.add(obj);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(reader!=null)reader.close();
		}
		return all;
	}
	
	
	/**
	 * 获取持久化文件的目录路径
	 * @return
	 */
	private String getfullPersistPath(){
		List<Config> configs=MPContext.getContext().getDataContainer().getInstance("mpConfig", new GenericTypeReference<List<Config>>(){});
		return ConfigUtil.getConfigPropertyValue(configs, "main","persist_path")+File.separator+ConfigUtil.getOperator()+File.separator+ConfigUtil.getProvince();
		
	}
	
	/**
	 * 根据表名获取mp文件全路径
	 * @return
	 */
	private String getFullFilename(){
		String fullPersistPath=getfullPersistPath();
		String fileName = this.tableName+".inf";
		String fullFileName =fullPersistPath + File.separator + fileName;
		return fullFileName;
		
	}
	
	/**
	 * 获取是否无数据库启动
	 * @return
	 */
	private boolean isStartupWithoutDB(){
		List<Config> configs=MPContext.getContext().getDataContainer().getInstance("businessConfig", new GenericTypeReference<List<Config>>(){});
		String str =ConfigUtil.getConfigPropertyValue(configs, "emergency","isstartupwithoutdb");
		if("true".equals(str)){
			return true;
		}
		return false;		
	}
	/**
	 * 是否自适应启动
	 * @return
	 */
	private boolean startupAdaptivity(){
		List<Config> configs=MPContext.getContext().getDataContainer().getInstance("businessConfig", new GenericTypeReference<List<Config>>(){});
		String str =ConfigUtil.getConfigPropertyValue(configs, "emergency","startupadaptivity");
		if("true".equals(str)){
			return true;
		}
		return false;
		
	}
	
	
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setDataSource(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
	}

	public void setPoClassname(String poClassname) {
		this.poClassname = poClassname;
	}


}

class Relation{
	private String colName;
	private String setMethodName;
	private String getMethodName;
	private Class<?> fieldType;
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getSetMethodName() {
		return setMethodName;
	}
	public void setSetMethodName(String setMethodName) {
		this.setMethodName = setMethodName;
	}
	public String getGetMethodName() {
		return getMethodName;
	}
	public void setGetMethodName(String getMethodName) {
		this.getMethodName = getMethodName;
	}
	public Class<?> getFieldType() {
		return fieldType;
	}
	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}
}
