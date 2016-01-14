package com.example.administrator.myproject.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Utils {
	/**
	 * 字符串变大写
	 * @param s
	 * @return
	 */
	public static String stringChangeCapital(String s) {
		if (TextUtils.isEmpty(s)) {
			return "";
		}
		char[] c = s.toCharArray();
		for (int i = 0; i < s.length(); i++) {
			if (c[i] >= 'a' && c[i] <= 'z') {
				c[i] = Character.toUpperCase(c[i]);
			} else if (c[i] >= 'A' && c[i] <= 'Z') {
				c[i] = Character.toLowerCase(c[i]);
			}
		}
		return String.valueOf(c);
	}

	public static String toZero(String name) {
		if (name == null) {
			return "0";
		}
		return name;
	}

	public static boolean isWord(String str) {
		if (str == null)
			return false;
		Pattern pattern = Pattern
				.compile("^(\\s|[a-zA-Z_0-9\\u4E00-\\u9FA5])*$");
		return pattern.matcher(str).matches();
	}

	public static boolean isValidUserName(String str) {
		if (str == null)
			return false;
		Pattern pattern = Pattern.compile("^[a-z]{1}[a-z0-9_]{5,19}$");
		return pattern.matcher(str).matches();
	}
	public static boolean isValidUserPass(String str) {
		if (str == null)
			return false;
		Pattern pattern = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z0-9_]{5,19}$");
		return pattern.matcher(str).matches();
	}
	/**
	 * 验证身份证号是否符合规则
	 * 
	 * @param text身份证号
	 * @return
	 */
	public static boolean isPersonId(String text) {
		String regx = "[0-9]{15}|[0-9]{18}|[0-9]{17}x";
		
		return text.matches(regx);
		
		
		
	}
	/**
	 * 数字检测
	 * @param no
	 * @return
	 */
	public static boolean isLetters(String source) {
		boolean result = false;
		if (source != null) {
			String regularExp = "^[a-zA-Z]*$";
			result = source.matches(regularExp);
		}
		return result;
	}
	/**
	 * 数字检测
	 * @param no
	 * @return
	 */
	public static boolean isValidNo(String no) {
		boolean result = false;
		if (no != null) {
			String regularExp = "^[0-9]*$";
			result = no.matches(regularExp);
		}
		return result;
	}
	// 2-10个中文或 2-30个英文字母
	public static boolean isValidUserRealName(String name) {
		boolean result = false;
		if (name != null) {
			String regularExp = "(^[\u4e00-\u9fa5]{2,10}$)|(^[a-zA-z][a-zA-z\\s\\/]{0,28}[a-zA-z]$)";
			result = name.matches(regularExp);
		}
		return result;
	}
	/**
	 * 判断身份证号码是否有效
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		if (checkCertiCode(idCard) == 0)
			return true;
		else
			return false;
	}

	/**
	 * 校验身份证
	 * 
	 * @param certiCode
	 *            待校验身份证
	 * @return 0--校验成功; 1--位数不对; 2--生日格式不对 ; 3--校验位不对 ; 4--其他异常;5--字符异常;
	 * @param certiCode
	 * @return
	 */
	public static int checkCertiCode(String certiCode) {
		try {
			if (certiCode == null || certiCode.length() != 15
					&& certiCode.length() != 18)
				return 1;
			String s1;
			String s2;
			String s3;
			if (certiCode.length() == 15) {
				if (!checkFigure(certiCode)) {
					return 5;
				}
				s1 = "19" + certiCode.substring(6, 8);
				s2 = certiCode.substring(8, 10);
				s3 = certiCode.substring(10, 12);
				if (!checkDate(s1, s2, s3))
					return 2;
			}
			if (certiCode.length() == 18) {
				if (!checkFigure(certiCode.substring(0, 17))) {
					return 5;
				}
				s1 = certiCode.substring(6, 10);
				s2 = certiCode.substring(10, 12);
				s3 = certiCode.substring(12, 14);
				if (!checkDate(s1, s2, s3))
					return 2;
				if (!checkIDParityBit(certiCode))
					return 3;
			}
		} catch (Exception exception) {
			return 4;
		}
		return 0;
	}

	/**
	 * 检查校验位
	 * 
	 * @param certiCode
	 * @return
	 */
	private static boolean checkIDParityBit(String certiCode) {
		boolean flag = false;
		if (certiCode == null || "".equals(certiCode))
			return false;
		int ai[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
		int last[] = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
		if (certiCode.length() == 18) {
			int i = 0;
			for (int k = 0; k < 17; k++) {
				char c = certiCode.charAt(k);
				int j;
				if (c <= '9' || c >= '0')
					j = c - 48;
				else
					return flag;
				i += j * ai[k];
			}
			int index = i % 11;
			certiCode.replace('x', 'X');
			
			if(last[index] == certiCode.charAt(17)){
				flag = true;
			}
				
		}
		return flag;
	}
	
	/**
	 * 检查日期格式
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	private static boolean checkDate(String year, String month, String day) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		try {
			String s3 = year + month + day;
			simpledateformat.setLenient(false);
			simpledateformat.parse(s3);
		} catch (java.text.ParseException parseexception) {
			return false;
		}
		return true;
	}

	/**
	 * 检查字符串是否全为数字
	 * 
	 * @param certiCode
	 * @return
	 */
	private static boolean checkFigure(String certiCode) {
		try {
			Long.parseLong(certiCode);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/***
	 * 获取渠道id
	 * @param context
	 * @param fileName
	 * @param channel
	 * @return
	 */
	public static String getSourceIdCode(Context context,String fileName,String channel) {
		Map<String, String> map = new HashMap<String, String>();
		
		String assetsFile = getAssetsFile(context, fileName);
		String[] sourceIcodes = assetsFile.split("\\r");
		
		for (int i = 0; i < sourceIcodes.length; i++) {
			map.put(sourceIcodes[i].split("=")[0], sourceIcodes[i].split("=")[1]);
		}
		return map.get(channel);
	}
	/**
	 * 获取asserts文件
	 */
	public static String getAssetsFile(Context context,String fileName) {
		
		try {
			
			AssetManager manager = context.getAssets();
			InputStream stream = manager.open(fileName);
			int len = stream.available();
			byte[] buffer = new byte[len];
			stream.read(buffer);
			return new String(buffer, "UTF-8");
		} catch (Exception e) {
			
		}
		return null;
		
		

	}
	/**
	 * 获取Raw文件
	 */
	public static String getRawFile(Context context,int resid) {
		
		try {
			
			InputStream stream = context.getResources().openRawResource(resid);
			
			int len = stream.available();
			byte[] buffer = new byte[len];
			stream.read(buffer);
			return new String(buffer, "UTF-8");
		} catch (Exception e) {
			
		}
		return null;
		
		

	}
	/**
	 * 输入流转字符流
	 * @param stream
	 * @return
	 */
	private String InputStreamToString(InputStream stream) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len= stream.read(buffer)) !=-1) {
				outputStream.write(buffer, 0, len);
				
			}
			
			return new String(outputStream.toByteArray(), "UTF-8");
		} catch (Exception e) {
			
		}
		return null;

	}
	/**
	 * 计算带有两位小数的float
	 * @param floatStr1
	 * @param floatStr2
	 * @return
	 */
	public static String calculateFloat(String floatStr1,String floatStr2) {
		BigDecimal decimal = null;
		try {
			decimal = new BigDecimal(floatStr1);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			decimal = decimal.add(new BigDecimal(floatStr2));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return decimal.toString();

	}
	/**
	 * 计算带有两位小数的float
	 * @param floatStr1
	 * @param floatStr2
	 * @param floatStr3
	 * @return
	 */
	public static String calculateFloat(String floatStr1,String floatStr2,String floatStr3) {
		BigDecimal decimal = null;
		try {
			decimal = new BigDecimal(floatStr1);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			decimal = decimal.add(new BigDecimal(floatStr2));
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			decimal = decimal.add(new BigDecimal(floatStr3));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return decimal.toString();

	}
	
	public static String getSystemName(){
		return "android";
	}
	/**
	 * 版本号
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context){
		PackageInfo pkgInfo;
		try {
			pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			
			if (pkgInfo != null) {
				return pkgInfo.versionName + "";
			}
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 得到当前的手机网络类型
	 * 
	 * @param context
	 * @return
	 */ 
	public static String getCurrentNetType(Context context) { 
	    String type = ""; 
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	    NetworkInfo info = cm.getActiveNetworkInfo(); 
	    if (info == null) { 
	        type = "null"; 
	    } else if (info.getType() == ConnectivityManager.TYPE_WIFI) { 
	        type = "wifi"; 
	    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) { 
	        int subType = info.getSubtype(); 
	        if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS 
	                || subType == TelephonyManager.NETWORK_TYPE_EDGE) { 
	            type = "2g"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA 
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0 
	                || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) { 
	            type = "3g"; 
	        } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准 
	            type = "4g"; 
	        } 
	    } 
	    return type; 
	}
	
}