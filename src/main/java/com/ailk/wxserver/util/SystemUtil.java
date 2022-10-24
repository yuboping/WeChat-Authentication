package com.ailk.wxserver.util;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class SystemUtil {

	private SystemUtil() {
	}

	public static final String DEFAULT_OPERATETYPE_SPLIT = "&&";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-mm-dd";

	// yyyy-MM-dd HH:mm:ss
	public static String getDate(String strFormat) {
		Date myDate = new Date();
		return getDate(myDate, strFormat);
	}

	public static String getDate(Date date, String strFormat) {
		if (date == null)
			date = new Date();
		java.text.SimpleDateFormat formater = new SimpleDateFormat(strFormat);
		String strDate = formater.format(date);
		return strDate;
	}

	public static Date dateAddDay(int interval) {
		Date date = new Date();
		return dateAddDay(date, interval);
	}

	public static Date dateAddDay(Date date, int interval) {
		if (date == null)
			date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, interval);
		return calendar.getTime();
	}

	public static Date dateAddMonth(int interval) {
		Date date = new Date();
		return dateAddMonth(date, interval);
	}

	public static Date dateAddMonth(Date date, int interval) {
		if (date == null)
			date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, interval);
		return calendar.getTime();
	}
	
	/**
     * 对指定的日期增加指定的月
     * @param in_date String 原日期 YYYYMMDD/YYYYMM
     * @param mon int 需要增加的月份
     * @return String 增加后的日期 YYYYMMDD/YYYYMM
     */
    public static String dateAddMonth(String in_date,int mon)
    {
        String tmp = in_date;
        if (in_date.length() == 6)
            tmp = tmp + "01";
        try
        {
            int nYear=Integer.parseInt(tmp.substring(0,4));
            int nMon=Integer.parseInt(tmp.substring(4,6));
            int nDate=Integer.parseInt(tmp.substring(6,8));
            Calendar cal=Calendar.getInstance();
            cal.set(nYear,nMon-1,nDate);

            cal.add(Calendar.MONTH,mon);

            nYear=cal.get(Calendar.YEAR);
            nMon=cal.get(Calendar.MONTH)+1;
            nDate=cal.get(Calendar.DATE);
            String rtnDate=formDateString(nYear,nMon,nDate);
            if (in_date.length() == 6)
                rtnDate = rtnDate.substring(0,6);
            return rtnDate;
        }
        catch(Exception e)
        {
            return "";
        }
    }

	public static Date dateAddYear(int interval) {
		Date date = new Date();
		return dateAddYear(date, interval);
	}

	public static Date dateAddYear(Date date, int interval) {
		if (date == null)
			date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, interval);
		return calendar.getTime();
	}

	public static Date dateAddDate(int year, int month, int day) {
		Date date = new Date();
		return dateAddDate(date, year, month, day);
	}

	public static Date dateAddDate(Date date, int year, int month, int day) {
		if (date == null)
			date = new Date();
		return dateAddYear(dateAddMonth(dateAddDay(date, day), month), year);
	}

	/**
	 * 对给定的日期加上指定的天
	 * @param in_date String 原日期 YYYYMMDD
	 * @param days int 需要增加的天数
	 * @return String 增加以后的日期 YYYYMMDD
	 */
	public static String dateAddDay(String in_date, int days) {
		String rtnDate = "";
		if (in_date.length() != 8)
			return rtnDate;
		try {
			int nYear = Integer.parseInt(in_date.substring(0, 4));
			int nMon = Integer.parseInt(in_date.substring(4, 6));
			int nDate = Integer.parseInt(in_date.substring(6, 8));
			Calendar cal = Calendar.getInstance();
			cal.set(nYear, nMon - 1, nDate);
			cal.add(Calendar.DATE, days);
			nYear = cal.get(Calendar.YEAR);
			nMon = cal.get(Calendar.MONTH) + 1;
			nDate = cal.get(Calendar.DATE);
			rtnDate = formDateString(nYear, nMon, nDate);
			return rtnDate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * To make sure that str is an int.
	 * 
	 * @param str
	 * @return int -1: not an int 0 : an int
	 */
	public static int isInteger(String str) {
		int intIsInt = 0;
		int intLen = str.length();
		String strAtIndex = null;

		for (int i = 0; i < intLen; i++) {
			strAtIndex = str.substring(i, i + 1);
			if (strAtIndex.equals("0")) {
			} else if (strAtIndex.equals("1")) {
			} else if (strAtIndex.equals("2")) {
			} else if (strAtIndex.equals("3")) {
			} else if (strAtIndex.equals("4")) {
			} else if (strAtIndex.equals("5")) {
			} else if (strAtIndex.equals("6")) {
			} else if (strAtIndex.equals("7")) {
			} else if (strAtIndex.equals("8")) {
			} else if (strAtIndex.equals("9")) {
			} else if (strAtIndex.equals(".")) {
			} else {
				intIsInt = -1;
			}

		}
		return intIsInt;

	}

	/**
	 * Turn String to int
	 * 
	 * @param str
	 * @return int -1: error
	 */
	public static int strToint(String str) {
		try {
			int i = Integer.parseInt(str);
			return i;
		} catch (java.lang.NumberFormatException nfe) {
//			System.err.println(str + " is not a number.");
			return -1;
		}
	}
	
	public static String int2str(int i,int length) {
		String ret = "";
		for (int j = 0; j < length; j++) {
			ret += "0";
		}
		ret += i;
		if (i > Math.pow(10,length)) 
			ret = ""+i;
		else
			ret = ret.substring(ret.length()-length);
		return ret;
	}
	public static String formateDateString(String inDate) {
		if (inDate.length() > 10) {
			inDate = inDate.substring(0, 10);
		}
		if (inDate.indexOf("/") >= 0) {
			inDate = StringUtils.replace(inDate, "/", "-");
		} else if (inDate.length() == 6) {
			inDate = inDate.substring(0, 4) + "-" + inDate.substring(4, 6);
		} else if (inDate.length() == 8) {
			inDate = inDate.substring(0, 4) + "-" + inDate.substring(4, 6)
					+ "-" + inDate.substring(6, 8);
		}
		return inDate;
	}
	
	public static String formateDateStringForConvert(String inDate) {
		if (inDate.length() > 10) {
			if(inDate.length() == 19){
				String[] tmp = inDate.split(" ");
				if(tmp.length != 2)
					return inDate;
				inDate = tmp[0] + "-" + StringUtils.replace(tmp[1], ":", "-");
			}else{
				inDate = inDate.substring(0, 10);
			}
		}
		if (inDate.indexOf("/") >= 0) {
			inDate = StringUtils.replace(inDate, "/", "-");
		} else if (inDate.length() == 6) {
			inDate = inDate.substring(0, 4) + "-" + inDate.substring(4, 6);
		} else if (inDate.length() == 8) {
			inDate = inDate.substring(0, 4) + "-" + inDate.substring(4, 6)
					+ "-" + inDate.substring(6, 8);
		}
		return inDate;
	}

	/**
	 * 根据传入的参数生成YYYY-MM-DD格式的字符串
	 * @param nYear int 年
	 * @param nMonth int 月
	 * @param nDate int 日
	 * @return String YYYY-MM-DD格式的字符串
	 */
	public static String formDateString(int nYear, int nMonth, int nDate) {
		try {
			if (nMonth <= 0 || nMonth > 12 || nYear <= 0 || nDate <= 0
					|| nDate > 31)
				return "";
			String sYear = String.valueOf(nYear);
			String sMonth = String.valueOf(nMonth);
			String sDate = String.valueOf(nDate);
			if (sMonth.length() == 1)
				sMonth = "0" + sMonth;
			if (sDate.length() == 1)
				sDate = "0" + sDate;
			String rtnDate = sYear + sMonth + sDate;
			if (rtnDate.length() < 8) {
				for (int i = 0; i < (8 - rtnDate.length()); i++) {
					rtnDate = "0" + rtnDate;
				}
			}
			return rtnDate;
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isValidDate(String inDate) {
		try {
			inDate = inDate.trim();
			if (inDate.length() > 10)
				inDate = inDate.substring(0, 10);
			if (inDate.indexOf("/") > 0)
				inDate = StringUtils.replace(inDate, "/", "");
			if (inDate.indexOf("-") > 0)
				inDate = StringUtils.replace(inDate, "-", "");
			int nYear, nMonth, nDate;

			boolean leap;

			if (!(inDate.length() == 6 || inDate.length() == 8))
				return false;

			nYear = Integer.parseInt(inDate.substring(0, 4));

			leap = nYear % 4 == 0 && nYear % 100 != 0 || nYear % 400 == 0;

			nMonth = nDate = 0;

			if (inDate.length() >= 6) {
				nMonth = Integer.parseInt(inDate.substring(4, 6));
			}

			if (inDate.length() > 6) {
				nDate = Integer.parseInt(inDate.substring(6, 8));
				if ((nMonth == 1 || nMonth == 3 || nMonth == 5 || nMonth == 7
						|| nMonth == 8 || nMonth == 10 || nMonth == 12)
						&& (nDate > 31 || nDate < 1)) {
					return false;
				} else if ((nMonth == 4 || nMonth == 6 || nMonth == 9 || nMonth == 11)
						&& (nDate > 30 || nDate < 1)) {
					return false;
				} else if ((nMonth == 2)
						&& ((leap == true && nDate > 29)
								|| (leap == false && nDate > 28) || nDate < 1)) {
					return false;
				} else if (nMonth > 12 || nMonth <= 0) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
//			System.out.println(e);
			return false;
		}
	}

	public static Date getDateFromString(String dateString) {
		Calendar c = Calendar.getInstance();
		String[] tmp = dateString.split("-");
		if(tmp.length == 3){
			c.set(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]) - 1,
					Integer.parseInt(tmp[2]), 0, 0, 0);
			return c.getTime();
		}else if(tmp.length == 6){
			c.set(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]) - 1,
					Integer.parseInt(tmp[2]), Integer.parseInt(tmp[3]), 
					Integer.parseInt(tmp[4]), Integer.parseInt(tmp[5]));
			return c.getTime();
		}else{
			return null;
		}
	}

	public static int compareDate(Date d1, Date d2) throws Exception {
		String dateString1 = getDate(d1, DEFAULT_DATE_FORMAT);
		String dateString2 = getDate(d2, DEFAULT_DATE_FORMAT);
		d1 = getDateFromString(dateString1);
		d2 = getDateFromString(dateString2);
		return d1.compareTo(d2);
	}

	public static long dateDiff(Date date1, Date date2, String unit) {
		long ret = 0;
		long timeunit = 1;
		if (unit.equals("ss")) {
			timeunit = 1000;
		}
		if (unit.equals("mm")) {
			timeunit = 1000 * 60;
		}
		if (unit.equals("HH")) {
			timeunit = 1000 * 60 * 60;
		}
		if (unit.equals("dd")) {
			timeunit = 1000 * 60 * 60 * 24;
		}
		long mTimeInterval = date1.getTime() - date2.getTime();
		ret = mTimeInterval / timeunit;
		return ret;
	}

	/*
	 * 根据指定类型 比较num1和num2
	 * 		
	 */
	public static boolean compareInt(String type, int num1, int num2) {
		if (type.trim().toUpperCase().equals("EQ")) {
			if (num1 == num2) return true;
			else return false;
		}else if (type.trim().toUpperCase().equals("NE")) {
			if (num1 != num2) return true;
			else return false;
		}else if (type.trim().toUpperCase().equals("GT")) {
			if (num1 > num2) return true;
			else return false;
		}else if (type.trim().toUpperCase().equals("GE")) {
			if (num1 >= num2) return true;
			else return false;
		}else if (type.trim().toUpperCase().equals("LT")) {
			if (num1 < num2) return true;
			else return false;
		}else if (type.trim().toUpperCase().equals("LE")) {
			if (num1 <= num2) return true;
			else return false;
		}
		return false;
	}
	
	public static String compareType(String type) {
		if (type.trim().toUpperCase().equals("EQ")) {
			return "为";
		}else if (type.trim().toUpperCase().equals("NE")) {
			return "非";
		}else if (type.trim().toUpperCase().equals("GT")) {
			return "大于";
		}else if (type.trim().toUpperCase().equals("GE")) {
			return "大于等于";
		}else if (type.trim().toUpperCase().equals("LT")) {
			return "小于";
		}else if (type.trim().toUpperCase().equals("LE")) {
			return "小于等于";
		}
		return "";
	}
	
	public static String nonCompareType(String type) {
		if (type.trim().toUpperCase().equals("EQ")) {
			return "非";
		}else if (type.trim().toUpperCase().equals("NE")) {
			return "为";
		}else if (type.trim().toUpperCase().equals("GT")) {
			return "小于等于";
		}else if (type.trim().toUpperCase().equals("GE")) {
			return "小于";
		}else if (type.trim().toUpperCase().equals("LT")) {
			return "大于等于";
		}else if (type.trim().toUpperCase().equals("LE")) {
			return "大于";
		}
		return "";
	}
	
	//2006/04/21 增加两个方法判断日期是否为月第一天,日期是否为月最后一天
	public static boolean isFirstDayOfMonth(Calendar c) {
		boolean ret = false;
		int day = c.get(Calendar.DATE);
		if (day == 1) {
			ret = true;
		}
		return ret;
	}
	public static boolean isLastDayOfMonth(Calendar c) {
		boolean ret = false;
		c.add(Calendar.DATE,1);
		ret = isFirstDayOfMonth(c);
		return ret;
	}
	//获取当月天数
	public static int getCurrentMonthDays(int year, int month) {
		int ret = 0;
		int[] days = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
		ret = days[month-1];
		if (month == 2 && ((year%4 == 0 && year%100 != 0) || year%400 == 0)) { //闰年2月为29天
			ret = 29;
		}
		return ret;
	}

	public static String getExceptionStackTrace(Exception e) {
		String ret = "";
		StackTraceElement[] steArray = e.getStackTrace();
		for (int i = 0; i < steArray.length; i++) {
			ret += steArray[i].toString();
		}
		return ret;
	}

	public static String getFullNumberAccountPrefix(String prefix) {
		String ret = "";
		if (prefix.trim().equals("ZZ") || prefix.trim().toLowerCase().equals("zz")) ret = "11";
		else if (prefix.trim().equals("KF") || prefix.trim().toLowerCase().equals("kf")) ret = "12";
		else if (prefix.trim().equals("LY") || prefix.trim().toLowerCase().equals("ly")) ret = "13";
		else if (prefix.trim().equals("PD") || prefix.trim().toLowerCase().equals("pd")) ret = "14";
		else if (prefix.trim().equals("AY") || prefix.trim().toLowerCase().equals("ay")) ret = "15";
		else if (prefix.trim().equals("HB") || prefix.trim().toLowerCase().equals("hb")) ret = "16";
		else if (prefix.trim().equals("XX") || prefix.trim().toLowerCase().equals("xx")) ret = "17";
		else if (prefix.trim().equals("JZ") || prefix.trim().toLowerCase().equals("jz")) ret = "18";
		else if (prefix.trim().equals("PY") || prefix.trim().toLowerCase().equals("py")) ret = "19";
		else if (prefix.trim().equals("XC") || prefix.trim().toLowerCase().equals("xc")) ret = "20";
		else if (prefix.trim().equals("LH") || prefix.trim().toLowerCase().equals("lh")) ret = "21";
		else if (prefix.trim().equals("SM") || prefix.trim().toLowerCase().equals("sm")) ret = "22";
		else if (prefix.trim().equals("SQ") || prefix.trim().toLowerCase().equals("sq")) ret = "23";
		else if (prefix.trim().equals("ZK") || prefix.trim().toLowerCase().equals("zk")) ret = "24";
		else if (prefix.trim().equals("ZM") || prefix.trim().toLowerCase().equals("zm")) ret = "25";
		else if (prefix.trim().equals("NY") || prefix.trim().toLowerCase().equals("ny")) ret = "26";
		else if (prefix.trim().equals("XY") || prefix.trim().toLowerCase().equals("xy")) ret = "27";
		else if (prefix.trim().equals("JY") || prefix.trim().toLowerCase().equals("jy")) ret = "28";
		return ret;
	}
	
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
 
	public static boolean isValidIP(String ip){
		if(ip==null||ip.length()==0)
			return false;
	 
		Pattern p = Pattern
	 			.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");
	 return p.matcher(ip).matches();
	}
	
	
	public static void map_put(Map<String, String> paramMap,Map<String, String> addMap){
		if(addMap != null && addMap.size() > 0 && paramMap != null){
			Iterator<String> iterator = addMap.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = addMap.get(key);
				String p_value = paramMap.get(key);
				if(StringUtils.isEmpty(p_value)){
					paramMap.put(key, value);
				}
			}
		}
	}
	
	public static void main(String[] args) {
//		Calendar c = Calendar.getInstance();
//		c.set(2005, 8, 31);
//		System.out.println(c.getTime());
//		System.out.println(SystemUtil.formateDateStringForConvert("2005-07-01 01:01:01"));
//		System.out.println(int2str(123,2));
	}
}
