package com.example.administrator.myproject.utils;

import java.text.DecimalFormat;

public class FormatUtils {
	/**
	 * 
	 * @param value 需要格式化的数字以字符串传入
	 * @return
	 */
	public static String format(String value){
		DecimalFormat df = new DecimalFormat("###,###,###,###") ;   // 实例化对象，传入模板  
        String str = df.format(Double.parseDouble(value)) ;     // 格式化数字  
        return str;
	}
	/**
	 * 
	 * @param pattern 格式
	 * @param value 需要格式化的数字
	 * @return
	 */
	public static String formatToString(String pattern,double value){   // 此方法专门用于完成数字的格式化显示  
        DecimalFormat df = new DecimalFormat(pattern) ;   // 实例化对象，传入模板  
        String str = df.format(value) ;     // 格式化数字  
        return str;
    }  
	/**
	 * 
	 * @param pattern 格式
	 * @param value 需要格式化的数字
	 * @return
	 */
	public static double formatToLong(String pattern,double value){
		DecimalFormat df = new DecimalFormat(pattern) ;   // 实例化对象，传入模板  
        String str = df.format(value) ;     // 格式化数字  
        return Double.parseDouble(str);
	}
}

