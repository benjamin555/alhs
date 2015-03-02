package com.homtrip.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
public class DateUtils {
	
	public static String format(Date d) {
		if (d==null) {
			return "";
		}
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(d);
	}

}
