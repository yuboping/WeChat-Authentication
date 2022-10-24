package com.ailk.wxserver.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ailk.lcims.support.mp.client.MPClientUtils;
import com.ailk.lcims.support.util.CollectionUtils;

public class FormatUtil {
	// 格式化ip,比如把127.0.0.1变成127.000.000.001
	public static String formatIP(String ip) {
		String strIP = "";
		String strTemp = "";
		StringTokenizer st = new StringTokenizer(ip, ".");
		while (st.hasMoreTokens()) {
			strTemp = st.nextToken();
			strTemp = ("00" + strTemp.trim());
			strTemp = strTemp.substring(strTemp.length() - 3);
			strIP += "." + strTemp;
		}
		strIP = strIP.substring(1);
		return strIP;
	}

	// redback设备必须要没有规整过的ip 2008/03/26
	public static String unformatIP(String ip) {
		String strIP = "";
		String strTemp = "";
		StringTokenizer st = new StringTokenizer(ip, ".");
		while (st.hasMoreTokens()) {
			strTemp = st.nextToken();
			strTemp = "" + Integer.parseInt(strTemp.trim());
			strIP += "." + strTemp;
		}
		strIP = strIP.substring(1);
		return strIP;
	}

	// 格式化时间，获得当前时间
	public static String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = "0" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String day = "0" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		String hour = "0" + String.valueOf(calendar.get(Calendar.HOUR));
		String minute = "0" + String.valueOf(calendar.get(Calendar.MINUTE));
		String second = "0" + String.valueOf(calendar.get(Calendar.SECOND));
		return year + "/" + month.substring(month.length() - 2) + "/"
				+ day.substring(day.length() - 2) + " "
				+ hour.substring(hour.length() - 2) + ":"
				+ minute.substring(minute.length() - 2) + ":"
				+ second.substring(second.length() - 2);
	}

	public static String formatDate(java.util.Date date, String strFormat) {
		SimpleDateFormat formater = new SimpleDateFormat(strFormat);
		return formater.format(date).toString();
	}

	// 格式化日期
	public static String formatDate(String formatstr) {
		SimpleDateFormat formater = new SimpleDateFormat(formatstr);
		return formater.format(new java.util.Date()).toString();
	}

	// 取得数据库date字符串
	public static String getDateSql(java.util.Date datefield) {
		String sql = "";
		String t = "";
		try {
			t = MPClientUtils.callService("/mp/portal/config/business/getUserPasswordType", null,
	                String.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		int type = Integer.parseInt(t);
		// oracle
		if (type == 1) {
			sql = " to_date('" + formatDate(datefield, "yyyy-MM-dd HH:mm:ss")
					+ "','YYYY-MM-DD HH24:MI:SS')";
		}
		// sybase
		else if (type == 2) {
			sql = "'" + formatDate(datefield, "yyyy-MM-dd HH:mm:ss") + "'";
		}
		return sql;
	}

	/**
	 * @author jalin
	 * @date 2008-12-18 将日期转化为时间戳,格式yyyyMMddHHmmss
	 * @param timestamp
	 * @return Date
	 * @throws ParseException
	 */
	public static String dateToTimestamp(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}

	/**
	 * @author jalin
	 * @date 2008-12-18 将时间戳转化为日期型，格式yyyyMMddHHmmss
	 * @param timestamp
	 * @return Date
	 * @throws ParseException
	 */
	public static Date timestampToDate(String timestamp) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.parse(timestamp);
	}

	/**
	 * 将Map转xml Map中均为String <info> <key1>value1</key1> <key2>value2</key2> ...
	 * </info>
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToXml(Map<String,String> map) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("UTF-8");
		Element root = doc.addElement("info");
		if(!CollectionUtils.isEmpty(map)){
			for(Entry<String,String> entry:map.entrySet()){
				String key = entry.getKey();
				String value = entry.getValue();
				if (isCDATAFormat(value)) {
					root.addElement(key).addCDATA(value);
				} else {
					root.addElement(key).addText(value);
				}
			}
		}

		String str = "";
		ByteArrayOutputStream sout = null;
		XMLWriter out = null;
		try {
			sout = new ByteArrayOutputStream();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			out = new XMLWriter(sout, format);
			out.write(doc);
			str = new String(sout.toByteArray(), "UTF-8");

		} catch (Exception e) {
			// TODO Auto-generated catch block
		} finally {
			try {
				if (out != null)
					out.close();
				if (sout != null) {
					sout.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		return str;
	}	
	    
    /**
     * 将Map转xml
     * <info>
     * 	<key1>value1</key1>
     * 	<key2>value2</key2>
     * 	...
     * </info>
     * @param map
     * @return
     */
    public static String mapToXml1(Map<String,String> map) {

		String str = "<info>";
		if(!CollectionUtils.isEmpty(map)){
			for(Entry<String,String> entry:map.entrySet()){
				String key = entry.getKey();
				String value = entry.getValue();
				str = str + "<" + key + ">" + value + "</" + key + ">";
			}
		}
		str = str + "</info>";
		return str;
	}

	private static boolean isCDATAFormat(String str) {
		return str.matches("^.*[<>&'\"].*");
	}

	/**
	 * 将xml转Map，将根节点下的元素列表的元素名设为key，元素内容设为value
	 * 
	 * @param map
	 * @return xml格式为 <info> <key1>value1</key1> <key2>value2</key2> ... </info>
	 */
	public static Map<String,String> xmlToMap(String info) {
		Map<String,String> map = new HashMap<String,String>();
		ByteArrayInputStream in = null;
		try {
			if (info != null && !"".equals(info)) {
				in = new ByteArrayInputStream(info.getBytes("UTF-8"));
				SAXReader reader = new SAXReader();
				Document doc = reader.read(in);

				Element ele = doc.getRootElement();
				Iterator iter = ele.elements().iterator();
				while (iter.hasNext()) {
					Element e = (Element) iter.next();
					String key = e.getName();
					String value = e.getTextTrim();
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
		}
		return map;
	}

	public static void main(String args[]) {
		Map map = new HashMap();
		map.put("key1", "123");
		map.put("key2", "既爱又恨你");
		String info = FormatUtil.mapToXml(map);
		System.out.println(info);
		Map testMap = FormatUtil.xmlToMap(info);
		System.out.println(testMap.get("key1"));
		System.out.println(testMap.get("key2"));
	}
}