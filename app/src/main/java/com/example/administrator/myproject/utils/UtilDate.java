package com.example.administrator.myproject.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.text.TextUtils;

public class UtilDate
{
   private static String defaultDatePattern = null;
   private static String timePattern = "HH:mm";
   public static final String TS_FORMAT = UtilDate.getDatePattern() + " HH:mm:ss.S";
   private static Calendar cale = Calendar.getInstance();
   /**
    * yyyy-MM-dd
    */
   public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   /**
    * HH:mm:ss
    */
   public static SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
   /**
    * yyyy-MM-dd HH:mm:ss
    */
   public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   /**
    * yyyyMMdd
    */
   public static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
   /**
    * HH:mm
    */
   public static SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm");
   /**
    * yyyyMMddHHmmss
    */
   public static SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmss");
   /**
    * yyyy年MM月dd日
    */
   public static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy年MM月dd日");
   // ~ Methods
   // ================================================================

   public UtilDate()
   {
   }

   public static String pad(int c)
   {
	  if (c >= 10)
		 return String.valueOf(c);
	  else
		 return "0" + String.valueOf(c);
   }
   /**
    * 返回指定格式的日期
    * @param sdf62
    * @return
    */
   public static String getDateTime(SimpleDateFormat sdf62)
   {
	  try
	  {
		  
		 return sdf62.format(cale.getTime());
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   
   /**
    * 获得服务器当前日期及时间，以格式为：yyyy-MM-dd HH:mm:ss的日期字符串形式返回
    */
   public static String getDateTime()
   {
	  try
	  {
		 return sdf2.format(cale.getTime());
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 格式化时间，以格式为：yyyyMMddHHmmss的日期字符串形式返回
    */
   public static String getPayDateTime(Date date)
   {
	  try
	  {
		 return sdf5.format(date);
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 格式化时间，以格式为：yyyy-MM-dd HH:mm:ss的日期字符串形式返回
    */
   public static String getDateTime(Date date)
   {
	  try
	  {
		 return sdf2.format(date);
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 获得服务器当前日期，以格式为：yyyy-MM-dd的日期字符串形式返回
    */
   public static String getDate()
   {
	  try
	  {
		 return sdf.format(cale.getTime());
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 获得服务器当前时间，以格式为：HH:mm:ss的日期字符串形式返回
    */
   public static String getTime()
   {
	  String temp = " ";
	  try
	  {
		 temp += sdf1.format(cale.getTime());
		 return temp;
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 统计时开始日期的默认值
    */
   public static String getStartDate()
   {
	  try
	  {
		 return getYear() + "-01-01";
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 统计时结束日期的默认值
    */
   public static String getEndDate()
   {
	  try
	  {
		 return getDate();
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 获得服务器当前日期的年份
    */
   public static String getYear()
   {
	  try
	  {
		 return String.valueOf(cale.get(Calendar.YEAR));
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 获得服务器当前日期的月份
    */
   public static String getMonth()
   {
	  try
	  {
		 java.text.DecimalFormat df = new java.text.DecimalFormat();
		 df.applyPattern("00;00");
		 return df.format((cale.get(Calendar.MONTH) + 1));
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 获得服务器在当前月中天数
    */
   public static String getDay()
   {
	  try
	  {
		 return String.valueOf(cale.get(Calendar.DAY_OF_MONTH));
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 比较两个日期相差的天数
    * 
    */
   public static int getMargin(String date1, String date2)
   {
	  int margin;
	  try
	  {
		 ParsePosition pos = new ParsePosition(0);
		 ParsePosition pos1 = new ParsePosition(0);
		 Date dt1 = sdf.parse(date1, pos);
		 Date dt2 = sdf.parse(date2, pos1);
		 long l = dt1.getTime() - dt2.getTime();
		 margin = (int) (l / (24 * 60 * 60 * 1000));
		 return margin;
	  }
	  catch (Exception e)
	  {
		 return 0;
	  }
   }

   public static String getCurrentDate()
   {
	  final Calendar c = Calendar.getInstance();
	  int mYear = c.get(Calendar.YEAR);
	  int mMonth = c.get(Calendar.MONTH);
	  int mDay = c.get(Calendar.DAY_OF_MONTH);
	 // return String.format("%d-%02d-%", );
	  return new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).toString();
   }
   public static String getCurrentDateEx()
   {
	  final Calendar c = Calendar.getInstance();
	  int mYear = c.get(Calendar.YEAR);
	  int mMonth = c.get(Calendar.MONTH);
	  int mDay = c.get(Calendar.DAY_OF_MONTH);
	  return String.format("%04d-%02d-%02d", mYear, mMonth + 1, mDay);
	 
   }
   
   public static Date getCurrentDateD()
   {
	  final Calendar c = Calendar.getInstance();
	  int mYear = c.get(Calendar.YEAR);
	  int mMonth = c.get(Calendar.MONTH);
	  int mDay = c.get(Calendar.DAY_OF_MONTH);
	  GregorianCalendar gCal = new GregorianCalendar(mYear, mMonth, mDay);
	  return new Date(gCal.get(Calendar.YEAR) - 1900, gCal.get(Calendar.MONTH), gCal.get(Calendar.DAY_OF_MONTH));
   }

   public static void main(String[] args)
   {
	  System.out.println(UtilDate.getCurrentDate());
   }

   /**
    * 比较两个日期相差的天数
    */
   public static double getDoubleMargin(String date1, String date2)
   {
	  double margin;
	  try
	  {
		 ParsePosition pos = new ParsePosition(0);
		 ParsePosition pos1 = new ParsePosition(0);
		 Date dt1 = sdf2.parse(date1, pos);
		 Date dt2 = sdf2.parse(date2, pos1);
		 long l = dt1.getTime() - dt2.getTime();
		 margin = (l / (24 * 60 * 60 * 1000.00));
		 return margin;
	  }
	  catch (Exception e)
	  {
		 return 0;
	  }
   }

   /**
    * 比较两个日期相差的月数
    */
   public static int getMonthMargin(String date1, String date2)
   {
	  int margin;
	  try
	  {
		 margin = (Integer.parseInt(date2.substring(0, 4)) - Integer.parseInt(date1.substring(0, 4))) * 12;
		 margin += (Integer.parseInt(date2.substring(4, 7).replaceAll("-0", "-")) - Integer.parseInt(date1.substring(4, 7).replaceAll("-0", "-")));
		 return margin;
	  }
	  catch (Exception e)
	  {
		 return 0;
	  }
   }

   /**
    * 返回日期加X天后的日期
    */
   public static String addDay(String date, int i)
   {
	  try
	  {
		 GregorianCalendar gCal = new GregorianCalendar(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
		 gCal.add(GregorianCalendar.DATE, i);
		 return sdf.format(gCal.getTime());
	  }
	  catch (Exception e)
	  {
		 return getDate();
	  }
   }

   /**
    * 返回日期加X月后的日期
    */
   public static String addMonth(String date, int i)
   {
	  try
	  {
		 GregorianCalendar gCal = new GregorianCalendar(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
		 gCal.add(GregorianCalendar.MONTH, i);
		 return sdf.format(gCal.getTime());
	  }
	  catch (Exception e)
	  {
		 return getDate();
	  }
   }

   /**
    * 返回日期加X年后的日期
    */
   public static String addYear(String date, int i)
   {
	  try
	  {
		 GregorianCalendar gCal = new GregorianCalendar(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
		 gCal.add(GregorianCalendar.YEAR, i);
		 return sdf.format(gCal.getTime());
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 返回某年某月中的最大天
    */
   public static int getMaxDay(String year, String month)
   {
	  int day = 0;
	  try
	  {
		 int iyear = Integer.parseInt(year);
		 int imonth = Integer.parseInt(month);
		 if (imonth == 1 || imonth == 3 || imonth == 5 || imonth == 7 || imonth == 8 || imonth == 10 || imonth == 12)
		 {
			day = 31;
		 }
		 else if (imonth == 4 || imonth == 6 || imonth == 9 || imonth == 11)
		 {
			day = 30;
		 }
		 else if ((0 == (iyear % 4)) && (0 != (iyear % 100)) || (0 == (iyear % 400)))
		 {
			day = 29;
		 }
		 else
		 {
			day = 28;
		 }
		 return day;
	  }
	  catch (Exception e)
	  {
		 return 1;
	  }
   }

   /**
    * 格式化日期
    */
   @SuppressWarnings("static-access")
   public String rollDate(String orgDate, int Type, int Span)
   {
	  try
	  {
		 String temp = "";
		 int iyear, imonth, iday;
		 int iPos = 0;
		 char seperater = '-';
		 if (orgDate == null || orgDate.length() < 6)
		 {
			return "";
		 }
		 iPos = orgDate.indexOf(seperater);
		 if (iPos > 0)
		 {
			iyear = Integer.parseInt(orgDate.substring(0, iPos));
			temp = orgDate.substring(iPos + 1);
		 }
		 else
		 {
			iyear = Integer.parseInt(orgDate.substring(0, 4));
			temp = orgDate.substring(4);
		 }
		 iPos = temp.indexOf(seperater);
		 if (iPos > 0)
		 {
			imonth = Integer.parseInt(temp.substring(0, iPos));
			temp = temp.substring(iPos + 1);
		 }
		 else
		 {
			imonth = Integer.parseInt(temp.substring(0, 2));
			temp = temp.substring(2);
		 }
		 imonth--;
		 if (imonth < 0 || imonth > 11)
		 {
			imonth = 0;
		 }
		 iday = Integer.parseInt(temp);
		 if (iday < 1 || iday > 31)
			iday = 1;

		 Calendar orgcale = Calendar.getInstance();
		 orgcale.set(iyear, imonth, iday);
		 temp = this.rollDate(orgcale, Type, Span);
		 return temp;
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   public static String rollDate(Calendar cal, int Type, int Span)
   {
	  try
	  {
		 String temp = "";
		 Calendar rolcale;
		 rolcale = cal;
		 rolcale.add(Type, Span);
		 temp = sdf.format(rolcale.getTime());
		 return temp;
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 
    * 返回默认的日期格式
    * 
    */
   public static synchronized String getDatePattern()
   {
	  defaultDatePattern = "yyyy-MM-dd";
	  return defaultDatePattern;
   }

   /**
    * 将指定日期按默认格式进行格式代化成字符串后输出如：yyyy-MM-dd
    */
   public static final String getDate(Date aDate)
   {
	  SimpleDateFormat df = null;
	  String returnValue = "";

	  if (aDate != null)
	  {
		 df = new SimpleDateFormat(getDatePattern());
		 returnValue = df.format(aDate);
	  }
	  return (returnValue);
   }

   /**
    * 取得给定日期的时间字符串，格式为当前默认时间格式
    */
   public static String getTimeNow(Date theTime)
   {
	  return getDateTime(timePattern, theTime);
   }

   /**
    * 取得当前时间的Calendar日历对象
    */
   public Calendar getToday() throws ParseException
   {
	  Date today = new Date();
	  SimpleDateFormat df = new SimpleDateFormat(getDatePattern());
	  String todayAsString = df.format(today);
	  Calendar cal = new GregorianCalendar();
	  cal.setTime(convertStringToDate(todayAsString));
	  return cal;
   }

   /**
    * 取得当前时间的string对象
    */
   public static String getCurrentDay()
   {
	  final Calendar c = Calendar.getInstance();
	  int year = c.get(Calendar.YEAR);
	  int month = c.get(Calendar.MONTH);
	  int day = c.get(Calendar.DAY_OF_MONTH);

	  SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	  Date date = new Date(year - 1900, month, day);
	  return sdfDate.format(date);

   }

   /**
    * 将日期类转换成指定格式的字符串形式
    */
   public static final String getDateTime(String aMask, Date aDate)
   {
	  SimpleDateFormat df = null;
	  String returnValue = "";

	  if (aDate != null)
	  {
		 df = new SimpleDateFormat(aMask);
		 returnValue = df.format(aDate);
	  }
	  return (returnValue);
   }

   /**
    * 将指定的日期转换成默认格式的字符串形式
    */
   public static final String convertDateToString(Date aDate)
   {
	  return getDateTime(getDatePattern(), aDate);
   }

   /**
    * 将指定的日期转换成指定格式的字符串形式
    */
   public static final String convertDateToString(String aMask, Date aDate)
   {
	  return getDateTime(aMask, aDate);
   }
   /**
    * 将日期字符串按指定格式转换成日期类型
    * 
    * @param aMask
    *           指定的日期格式，如:yyyy-MM-dd
    * @param strDate
    *           待转换的日期字符串
    */
   public static final Date convertStringToDate(String aMask, String strDate) throws ParseException
   {
	  SimpleDateFormat df = null;
	  Date date = null;
	  df = new SimpleDateFormat(aMask);

	  try
	  {
		 date = df.parse(strDate);
	  }
	  catch (ParseException pe)
	  {
		 throw pe;
	  }
	  return (date);
   }

   /**
    * 将日期字符串按默认格式转换成日期类型
    */
   public static Date convertStringToDate(String strDate) throws ParseException
   {
	  Date aDate = null;
	  try
	  {
		 aDate = convertStringToDate(getDatePattern(), strDate);
	  }
	  catch (ParseException pe)
	  {
		 throw new ParseException(pe.getMessage(), pe.getErrorOffset());
	  }
	  return aDate;
   }

   /**
    * 将日期字符串按yyyy-MM-dd HH:mm:ss转换成日期类型
    */
   public static Date convertStringToDate2(String strDate) throws ParseException
   {
	  Date aDate = null;
	  try
	  {
		 aDate = convertStringToDate("yyyy-MM-dd HH:mm:ss", strDate);
	  }
	  catch (ParseException pe)
	  {
		 throw new ParseException(pe.getMessage(), pe.getErrorOffset());
	  }
	  return aDate;
   }

   /**
    * 返回一个JAVA简单类型的日期字符串
    */
   public static String getSimpleDateFormat()
   {
	  SimpleDateFormat formatter = new SimpleDateFormat();
	  String NDateTime = formatter.format(new Date());
	  return NDateTime;
   }

   /**
    * 将两个字符串格式的日期进行比较,得到间隔天数
    * 
    * @param sj1
    *           要比较的第一个日期字符串
    * @param sj2
    *           要比较的第二个日期格式字符串
    */
   public static int getDaysBetween(String beginDate, String endDate)
   {
	  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	  Date bDate;
	  try
	  {
		 bDate = format.parse(beginDate);
		 Date eDate = format.parse(endDate);
		 Calendar d1 = new GregorianCalendar();
		 d1.setTime(bDate);
		 Calendar d2 = new GregorianCalendar();
		 d2.setTime(eDate);
		 int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
		 int y2 = d2.get(Calendar.YEAR);
		 if (d1.get(Calendar.YEAR) != y2)
		 {
			d1 = (Calendar) d1.clone();
			do
			{
			   days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);// 得到当年的实际天数
			   d1.add(Calendar.YEAR, 1);
			} while (d1.get(Calendar.YEAR) != y2);
		 }
		 return days;
	  }
	  catch (ParseException e)
	  {
		 e.printStackTrace();
	  }
	  return -1;
   }

   /**
    * 将两个字符串格式的日期进行比较
    * 
    * @param last
    *           要比较的第一个日期字符串
    * @param now
    *           要比较的第二个日期格式字符串
    * @return true(last 在now 日期之前),false(last 在now 日期之后)
    */
   public static boolean compareTo(String last, String now)
   {
	  try
	  {
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Date temp1 = formatter.parse(last);
		 Date temp2 = formatter.parse(now);
		 if (temp1.after(temp2))
			return false;
		 else if (temp1.before(temp2))
			return true;
	  }
	  catch (ParseException e)
	  {
	  }
	  return false;
   }

   public static boolean compareToDay(String last, String now)
   {
	  try
	  {
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 Date temp1 = formatter.parse(last);
		 Date temp2 = formatter.parse(now);
		 if (temp1.after(temp2))
		 {
			return false;
		 }
		 else if (temp1.before(temp2))
		 {
			return true;
		 }
		 else if (temp1.equals(temp2))
		 {
			return true;
		 }
	  }
	  catch (ParseException e)
	  {
	  }
	  return false;
   }

   /**
    * 返回指定年份中指定月份的天数
    * 
    * @param 年份year
    * @param 月份month
    * @return 指定月的总天数
    */
   public static String getMonthLastDay(int year, int month)
   {
	  int[][] day = { { 0, 30, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }, { 0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 } };
	  if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
	  {
		 return day[1][month] + "";
	  }
	  else
	  {
		 return day[0][month] + "";
	  }
   }

   /**
    * 取得指定时间的天
    * 
    * @return
    */
   public static int getDaOfMonth(Date date)
   {
	  cale.setTime(date);
	  return cale.get(Calendar.DATE);
   }

   public final static String[] monthArr = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };// 月份

   /**
    * 获得服务器当前时间，以格式为：HH:mm的日期字符串形式返回
    */
   public static String getCurrentTime()
   {
	  String temp = "";
	  try
	  {
		 temp += sdf4.format(cale.getTime());
		 return temp;
	  }
	  catch (Exception e)
	  {
		 return "";
	  }
   }

   /**
    * 格式化日期 如20111031 -》 2011-10-31
    */
   public static String formatDate(String date)
   {
	  try
	  {
		 Date d = sdf3.parse(date);
		 String temp = sdf.format(d);
		 return temp;
	  }
	  catch (ParseException e1)
	  {
		 e1.printStackTrace();
		 return "";
	  }
   }

   public static String GetWeek(int index)
   {
	  switch (index)
	  {
		 case 1:
			return "星期一";
		 case 2:
			return "星期二";
		 case 3:
			return "星期三";
		 case 4:
			return "星期四";
		 case 5:
			return "星期五";
		 case 6:
			return "星期六";
		 case 0:
			return "星期日";
		 default:
			return "";
	  }
   }

   public static Date DateAddDay(Date in, int iAdd)
   {
	  GregorianCalendar gCal = new GregorianCalendar(in.getYear() + 1900, in.getMonth(), in.getDate());
	  gCal.add(GregorianCalendar.DATE, iAdd);
	  return new Date(gCal.get(Calendar.YEAR) - 1900, gCal.get(Calendar.MONTH), gCal.get(Calendar.DAY_OF_MONTH));
   }

   public static String DayAddDay(String day, int iAdd)
   {
	  Date in = null;
	  try
	  {
		 in = convertStringToDate(day);
		 return convertDateToString(DateAddDay(in, iAdd));
	  }
	  catch (ParseException e)
	  {
		 e.printStackTrace();
	  }
	  return "";
   }

   public static long getTimeDif(Date time1, Date time2)
   {
	  if (time1 != null && time2 != null)
	  {
		 return (time2.getTime() - time1.getTime());
	  }
	  return 0;
   }

   public static long getTimeDif(String time1, String time2)
   {
	  if (!TextUtils.isEmpty(time1) && !TextUtils.isEmpty(time2))
	  {
		 try
		 {
			Date t1 = convertStringToDate(time1);
			Date t2 = convertStringToDate(time2);
			return getTimeDif(t1, t2);
		 }
		 catch (ParseException e)
		 {
			e.printStackTrace();
		 }

		 // return (time2.getTime() - time1.getTime());
	  }
	  return 0;
   }
   /**
	 * 将字符串转换为int
	 * 
	 * @param str
	 * @return
	 */
	public static int parseInt(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}
		if (str.indexOf(".") >= 0){
			float fTemp = Float.parseFloat(str);
			if((fTemp % 1) >= 0.5){
				return (int)fTemp + 1;
			}else{
				return (int)fTemp;
			}
			//return (int)Float.parseFloat(str);
		}else
			return Integer.parseInt(str);
	}

	public static float parseFloat(String str) {
		if (TextUtils.isEmpty(str)) {
			return 0;
		}
		return Float.parseFloat(str);
		
	}
   // hh:mm
   public static int getShortTimeDif(String time1, String time2)
   {
	  int t1 = HHMMToInt(time1);
	  int t2 = HHMMToInt(time2);
	  if (t1 > t2)
	  {
		 return t1 - t2;
	  }
	  else
	  {
		 return 0;// t1 - t2 + 24 * 60 * 60;
	  }
   }

   public static int getShortTimeNow(String t)
   {
	  Date dNow = cale.getTime();
	  String sNow = String.format("%02d:%02d", dNow.getHours(), dNow.getMinutes());

	  // String t1 = UtiString.getTime(getCurrentDate());

	  return getShortTimeDif(sNow, t);
   }

   public static int HHMMToInt(String hhmm)
   {
	  if (TextUtils.isEmpty(hhmm) || hhmm.length() < 5)
	  {
		 return 0;
	  }

	  String hh = hhmm.substring(0, 2);
	  String mm = hhmm.substring(3, 5);

	  int iReturn = parseInt(hh) * 3600 + parseInt(mm) * 60;

	  return iReturn;
   }

   public static String TimeToString(long t)
   {
	  if (t <= 0)
	  {
		 return "00时00分00秒";
	  }
	  long d = t / (24 * 60 * 60 * 1000);
	  t = t % (24 * 60 * 60 * 1000);
	  long hh = t / (60 * 60 * 1000);
	  t = t % (60 * 60 * 1000);
	  long mm = t / (60 * 1000);
	  t = t % (60 * 1000);
	  long ss = t / 1000;
	  if (d <= 0)
	  {
		 if (hh <= 0)
		 {
			if (mm <= 0)
			{
			   return String.format("%02d秒", ss);
			}
			return String.format("%02d分%02d秒", mm, ss);
		 }
		 return String.format("%02d时%02d分%02d秒", hh, mm, ss);
	  }
	  else
	  {
		 return String.format("%d天%02d时%02d分%02d秒", d, hh, mm, ss);
	  }
   }
}
