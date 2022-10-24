package com.ailk.wxserver.util;

import com.ailk.lcims.support.mp.client.MPClientUtils;

//此类处理按hash值分表
public class Hash163 {
	private static int TABLE_COUNT = 6;//分表数
	private final static String CUSTOMER_TABLE_HEAD = "M_CUSTOMER";//客户表头
 	private final static String DIALUP_TABLE_HEAD = "B_DIALUP_USER";//拨号用户表头
// 	private final static String DSL_TABLE_HEAD = "B_DSL_USER";//DSL用户表头
// 	private final static String LAN_TABLE_HEAD = "B_LAN_USER";//LAN用户表头
	private final static String CARD_TABLE_HEAD = "B_CARD_USER";//卡用户表头
	private final static String BROAD_TABLE_HEAD = "B_BROADBAND_USER";//宽带用户表头
	static{
		String user_hash =  MPClientUtils.callService(
				"/mp/portal/config/business/getConfig", new Object[] {
						"main", "user_hash" }, String.class);
		TABLE_COUNT = Integer.parseInt(user_hash);
	}
/**
 * 此处插入方法描述.
 * 创建 日期: (2002-8-27 9:24:58)
 * @return java.lang.String
 * @param userName java.lang.String
 */
public static String BRDHash(String userName) throws Exception {
	return getTableName(userName, BROAD_TABLE_HEAD);
}
/**
 * 此处插入方法描述.
 * 创建 日期: (2002-6-20 15:31:55)
 * @return java.lang.String
 * @param cardNo java.lang.String
 */
public static String CRDHash(String cardNo)throws Exception {
	return getTableName(cardNo,CARD_TABLE_HEAD);
}
//根据用户名获得客户表表名
static public String CUSHash(String str) throws Exception {
	return getTableName(str,CUSTOMER_TABLE_HEAD);
}
/**
 * 根据用户名获得拨号用户表名.
 * 创建 日期: (2002-6-6 11:24:32)
 * @return java.lang.String 拨号用户表名
 * @param str java.lang.String 用户名
 * @exception java.lang.Exception The exception description.
 */
public static String DIPHash(String str) throws java.lang.Exception {
	return getTableName(str,DIALUP_TABLE_HEAD);
}
//根据用户名获得DSL用户表名
static public String DSLHash(String str) throws Exception{
	return BRDHash(str);
}
/**
 * 根据用户名获得相关表名.
 * 创建 日期: (2002-6-6 11:18:12)
 * @return java.lang.String
 * @param str java.lang.String
 * @param tableHead java.lang.String
 */
public static String getTableName(String str, String tableHead) throws Exception {
	if (tableHead == null || tableHead.equals(""))
	throw new Exception("Exception in method getTableName of class Hash163:param tableHead is null");
	return tableHead + sHash(str);
}
//根据用户名获得HASH值
static public int hash(String str) {
	int l_power[] = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
		73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173};
	int s_sum, i;
	s_sum = 0;
	for (i = 0; i < str.length(); i++) {
		s_sum += str.charAt(i) * l_power[i];
	}
	return (s_sum % TABLE_COUNT);
}
/**
 * 根据用户名获得LAN用户表名.
 * 创建 日期: (2002-6-6 11:24:32)
 * @return java.lang.String LAN用户表名
 * @param str java.lang.String LAN用户名
 * @exception java.lang.Exception The exception description.
 */
public static String LANHash(String str) throws java.lang.Exception {
	return BRDHash(str);
}
/**
 * 根据用户名获得LAN用户表名.
 * 创建 日期: (2003-1-14 15:44:32)
 * @return java.lang.String WLAN用户表名
 * @param str java.lang.String WLAN用户名
 * @exception java.lang.Exception The exception description.
 */
public static String WLANHash(String str) throws java.lang.Exception {
	return BRDHash(str);
}
/**
 * 根据客户名得到EMAIL表名.
 * 创建 日期: (2002-7-4 11:22:45)
 * @return java.lang.String
 * @param userName java.lang.String
 */
public static String MALHash(String userName) throws Exception {
	String ret = "B_EMAIL_USER";
	/*
	int hashValue = hash(userName);
	if (hashValue % 2 == 1)
		ret = "B_EMAIL_USER1";
	else
		ret = "B_EMAIL_USER2";
	*/
	return ret;
}
/**
 * 此处插入方法描述.
 * 创建 日期: (2002-6-7 10:49:11)
 * @return java.lang.String
 * @param str java.lang.String
 */
public static String sHash(String str) throws Exception{
	int sum = -1;
	String ret = "";
	if (str == null || str.length()>40)
	throw new Exception("Exception in method sHash of class Hash163:str is null or lenth > 40");
	sum = hash(str);
	if ((sum < 0) || (sum > TABLE_COUNT - 1))
		throw new Exception("Exception in method sHash of class Hash163:error");
	if (sum < 10)
		ret = "0" + sum;
	else
		if (sum >= 10)
			ret = String.valueOf(sum);
	return (ret);
}
/**
 * 根据客户名得到UM表名.
 * 创建 日期: (2002-7-4 11:27:16)
 * @return java.lang.String
 * @param userName java.lang.String
 */
public static String UMHash(String userName) {
	String ret = "B_UM_USER";
	/*
	int hashValue = hash(userName);
	if (hashValue % 2 == 1)
		ret = "B_UM_USER1";
	else
		ret = "B_UM_USER2";
	*/
	return ret;
}
/**
 * 根据客户名得到UM表名.
 * 创建 日期: (2002-7-4 11:27:16)
 * @return java.lang.String
 * @param userName java.lang.String
 */
public static String REGHash(String userName) {
	String ret = "B_REGISTER_USER";
	return ret;
}
/**
 * 根据客户名得到VIEW(新视通)表名.
 * 创建 日期: (2003-1-9 16:33)
 * @return java.lang.String
 * @param userName java.lang.String
 */
public static String VIEWHash(String userName) {
	String ret = "";
	int hashValue = hash(userName);
	if (hashValue % 2 == 1)
		ret = "B_VIEW_USER1";
	else
		ret = "B_VIEW_USER2";
	return ret;
}
/**
 * 根据客户名得到LINE(专线)表名.
 * 创建 日期: (2003-4-26 16:33)
 * @return java.lang.String
 * @param userName java.lang.String
 */
public static String LINEHash(String userName) {
	String ret = "B_LINE_USER";
	return ret;
}
static public void main(String[] args) {
	try {
//		System.out.println(sHash("chengt"));
//		System.out.println(CUSHash("111"));
//		System.out.println(sHash("ladfja"));
//		System.out.println(DSLHash("ladfja"));
//		System.out.println(LANHash("ladfja1"));
//		System.out.println(DIPHash("ladfja1ASF7777w34534534565657"));
//		for (int i=1000;i<2000;i++){
//			String num="1531000"+i;
		Hash163.TABLE_COUNT=12;
			System.out.println(Hash163.BRDHash("13851995829"));
//		}
	} catch (Exception e) {
		System.out.println(e.toString());
	}
}
}
