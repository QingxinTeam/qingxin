package com.aiop.lda.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	public static final String YYYY_MM_DD_HHMMSS_SSS = "yyyy-MM-dd HH:mm:ss";
//	2019-09-16T06:47:17.791Z
	public static final String YYYY_MM_DD_HHMMSS_SSST = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	public static String formatDate(Date date,String pattern) {
		if(null==date) {
			return "";
		}
		if(StringsUtil.isBlank(pattern)) {
			return "";
		}
		DateFormat dateFormat=new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}
	
	public static Date StrToDateForPatternAddHour(String str) {
		Instant instant = Instant.parse(str);
		Date date=Date.from( instant );
		return date;
		
	}
	
	public static Date StrToDateForPattern(String str,String pattern) {
		  
		   SimpleDateFormat format = new SimpleDateFormat(pattern);
		   Date date = null;
		   try {
		    date = format.parse(str);
		   } catch (ParseException e) {
		    e.printStackTrace();
		   }
		   return date;
		}
		
	
	public static String DateToUTCstr(Date time){
		SimpleDateFormat dateFormat=new SimpleDateFormat(YYYY_MM_DD_HHMMSS_SSST);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(time);
		
	}
	
	public static void main(String[] args) {
		String a="2019-09-16T06:47:17.791Z";
		System.out.println(StrToDateForPatternAddHour(a));
		
		System.out.println(DateToUTCstr(new Date()));
	}
	

}
