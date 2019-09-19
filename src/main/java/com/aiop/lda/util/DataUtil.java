package com.aiop.lda.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataUtil {
	
	

	private static final String _0 = "0";
	
	public static final String YYYYMMDD = "yyyyMMdd";
	
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	
	public static final String YYYY_MM_DD_HHMMSS_SSS = "yyyy-MM-dd HH:mm:ss SSS";
	
	private static final String SESSION_ID_SPILIT = "-";
	
	
	public static String getYYYYMMDD4Cal(Calendar cal) {
		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DATE);
		String dateStr = cal.get(Calendar.YEAR) + "-" + (month < 10 ? _0 + month : month) + "-" 
		        		+  (date < 10 ? _0 + date : date);
		return dateStr;
	}
	
	public static String getCurrentTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}
	

	
	public static String generateSessionID(String sessionId) {
		return  DataUtil.getCurrentDateStr(DataUtil.YYYYMMDD)+SESSION_ID_SPILIT+sessionId;
	}
	
	public static String getCurrentDateStr(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}
	
	

}
