package com.homtrip.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
* @author 陈嘉镇
* @version 创建时间：2015-2-12 下午3:10:52
* @email benjaminchen555@gmail.com
*/
public class NumberUtils {
	private static NumberFormat percentInstance = NumberFormat.getPercentInstance();  
	private static NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(Locale.CHINA);  
	static{
		currencyInstance.setMaximumFractionDigits(0);
	}

	public static String formatCurrency(double d ) {
		return currencyInstance.format(d);
	}
	
	
	public static String formatPercent(double d ) {
		return percentInstance.format(d);
	}

}
