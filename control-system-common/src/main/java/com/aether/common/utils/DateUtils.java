package com.aether.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @Title: 时间工具类
 * @Description:
 * @Author:Administrator
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	// 格式：年－月－日
    public static final String LONG_DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * 给知道日期加上指定天数
     * @param date
     * @param day
     * @return
     * @version:v1.0
     * @author:Administrator
     */
    public static Date addDate(Date date, int day){
    	return addDays(date, day);
    }
    
	/**
	 * @return 获取最近3天时间
	 * @Description:获取最近3天时间
	 */
	public static Date getLastThreeDay() {
		return addDays(new Date(), -3);
	}

	/**
	 * @return 获取上周（7天）时间
	 * @Description:获取上周（7天）时间
	 */
	public static Date getLastWeek() {
		return addDays(new Date(), -7);
	}

	/*public static Date getLastCommissionDate() {
		Integer lastDay = NumberUtils.createInteger(SettingUtils.get().getLastCommissionDate());
		return addDays(new Date(), -lastDay);
	}*/

	
	public static Date getLastMonth() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	public static Date getSendDate(Date date) {
		if (date == null) {
			return null;
		}
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date sendDate = sf.parse(DateFormatUtils.format(date, "yyyy-MM-dd"));
			sendDate = addHours(sendDate, 17);
			if (sendDate.before(date)) {
				return addDays(sendDate, 1);
			}
			return sendDate;
		} catch (ParseException e) {
			//logger.warn("时间转换错误", e);
		}
		return null;
	}

	/**
	 * @param dateStr
	 * @return
	 * @Description: 字符串转日期，格式(yyyy-MM-dd)
	 */
	public static Date convertDate(String dateStr) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		if (StringUtils.isNotEmpty(dateStr)) {
			try {
				date = sf.parse(dateStr);
			} catch (ParseException e) {
				//logger.warn("时间转换错误", e);
			}
		}
		return date;
	}
	
	/**
	 * @param dateStr
	 * @return
	 * @Description: 字符串转日期
	 */
	public static Date convertDateFormat(String dateStr, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		Date date = null;
		if (StringUtils.isNotEmpty(dateStr)) {
			try {
				date = sf.parse(dateStr);
			} catch (ParseException e) {
				//logger.warn("时间转换错误", e);
			}
		}
		return date;
	}

	public static Date convertBeginDate(String dateStr) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		if (StringUtils.isNotEmpty(dateStr)) {
			try {
				date = sf.parse(dateStr);
				// date = DateUtils.addHours(date, 8);
			} catch (ParseException e) {
				//logger.warn("时间转换错误", e);
			}
		}
		return date;
	}

	public static Date convertEndDate(String dateStr) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		if (StringUtils.isNotEmpty(dateStr)) {
			try {
				date = sf.parse(dateStr);
				date = DateUtils.addSeconds(DateUtils.addDays(date, 1), -1);
				// date = DateUtils.addHours(date, 8);
			} catch (ParseException e) {
				//logger.warn("时间转换错误", e);
			}
		}
		return date;
	}

	public static Date toGMTDate(Date date) {
		/*
		 * if (date == null) { return null; } SimpleDateFormat sdf = new
		 * SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); DateFormat gmt = new
		 * SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		 * gmt.setTimeZone(TimeZone.getTimeZone("GMT")); String gmtStr =
		 * gmt.format(date); Date gmt_date = null; try { gmt_date =
		 * sdf.parse(gmtStr); } catch (ParseException e) { } return gmt_date;
		 */
		return date;
	}

	public static String formatDate(Date date, String pattern) {
		if (date == null) {
			throw new IllegalArgumentException("date is null");
		}
		if (pattern == null) {
			throw new IllegalArgumentException("pattern is null");
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			return formatter.format(date);
		}
	}
	/**
	 * @Function: com.pub.util.DateUtils.formatDate
	 * @Description: 将时间转成yyyy-MM-dd hh24:mm:ss字符串
	 *
	 * @param date
	 * @return
	 * @author:Administrator
	 */
	public static String formatDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("date is null");
		}
		return formatDate(date,"yyyy-MM-dd HH:mm:ss");
		
	}
	
	/**
	 * 
	* @Title: DateUtils.java 
	* @Description: TODO
	* @author Administrator 
	 */
	public static Date getDateLong(String date)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date strtodate = null;
		try {
			if(date == null || ("").equals(date)){
				return null;
			}
			if(date != null && !("").equals(date) && date.length()<11){
				date=date+" 00:00:00";
			}
			
			strtodate = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strtodate;
	}
	/**
	 * 计算日期的时间差，返回秒数
	 */
	public static long calcDifference(Date d1, Date d2){
		return (d1.getTime()-d2.getTime())/1000;
	} 

	
	/**
	 * 
	 * @Description:比较当前日期与指定日期的大小, 小于当前日期返回true
	 * @return
	 * @author:Administrator
	 */
	 public static boolean lessThanToday(String appointDay) {
		boolean flag=false;
		try {
		    Date today = new Date();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			today = sf.parse(DateFormatUtils.format(today, "yyyy-MM-dd"));
		    Date appointDayDate = convertDateFormat(appointDay, LONG_DATE_FORMAT);
		    if (appointDayDate.before(today) || appointDayDate.compareTo(today) <=0) {
		    	flag = true; 
			}
		    
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	      return flag;
	 } 
	 
		/**
		 * 
		 * @Description:比较当前日期与指定日期的大小，包含当天
		 *
		 * @param after
		 * @return
		 * @author:Administrator
		 */
		 public static boolean yearDiffCurrBefore(String after) {
			boolean flag=false;
			try {
			    Date currentDay = new Date();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				currentDay = sf.parse(DateFormatUtils.format(currentDay, "yyyy-MM-dd"));
			    Date afterDay = convertDateFormat(after, LONG_DATE_FORMAT);
			    if (afterDay.before(currentDay) || afterDay.compareTo(currentDay) < 0) {
			    	flag = true; 
				}
			    
			} catch (ParseException e) {
				e.printStackTrace();
			} 
		      return flag;
		 } 
	 
	 /**
	  * 给当前时间加上指定毫秒数
	  * @param ms
	  * @return
	  * @author:Administrator
	  */
	 public static Date addms(Long ms){
		 try {
			Date d = new Date();
			Date d2 = new Date(d.getTime()+ms);
			 return d2;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	 }

	/**
	 * 获取指定月第一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getAppointMonthFirstDate(int month) {
		// 获取当前日期
		Calendar cal_1 = Calendar.getInstance();
		cal_1.set(Calendar.MONTH, month - 1);
		// 设置为1号,当前日期既为本月第一天
		cal_1.set(Calendar.DAY_OF_MONTH, 1);
		return DateUtils.formatDate(cal_1.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 获取指定月最后一天
	 * 
	 * @param month
	 * @return
	 */
	public static String getAppointMonthLastDate(int month) {
		Calendar cal_1 = Calendar.getInstance();// 获取当前日期
		cal_1.set(Calendar.MONTH, month);
		cal_1.set(Calendar.DAY_OF_MONTH, 0);// 设置为1号,当前日期既为本月第一天
		return DateUtils.formatDate(cal_1.getTime(), "yyyy-MM-dd");
	}
	/**
	 * 获取当前月第一天
	 * @return
	 */
	public static String getCurrentMonthFirstDate() {
		// 获取当前月第一天：
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		return DateUtils.formatDate(c.getTime(), "yyyy-MM-dd");
	}
	/**
	 * 获取当前月最后一天
	 * @return
	 */
	public static String getCurrentMonthLastDate() {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH,
				ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		return DateUtils.formatDate(ca.getTime(), "yyyy-MM-dd");
	}
	
	public static String getDayByYearAndMonth(int year, int month){
		Calendar c = Calendar.getInstance();
	    c.set(Calendar.YEAR, year);
	    c.set(Calendar.MONTH, month-1);
	    int  day = c.getActualMaximum(Calendar.DATE);
	        
	    c.set(Calendar.DAY_OF_MONTH, day);
	    String lastDayOfMonth = DateUtils.formatDate(c.getTime(), "yyyy-MM-dd");
	    return lastDayOfMonth;
	}
	
	public static String getFirstDayByYearAndMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
	    c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH,c.getMinimum(Calendar.DATE));
		return DateUtils.formatDate(c.getTime(), "yyyy-MM-dd");
	}
	
	public static String getLastDayByYearAndMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
	    c.set(Calendar.MONTH, month-1);
		c.set(Calendar.DAY_OF_MONTH,c.getMaximum(Calendar.DATE));
		return DateUtils.formatDate(c.getTime(), "yyyy-MM-dd");
	}
	
	public static Date formateDateTime(String strDate){
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	
	public static void genDayPram(Map<String, Object> param, String startDay, String endDay){
		if(StringUtils.isEmpty(startDay)){
			param.put("startDay", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		}else{
			param.put("startDay",startDay);
		}
		if(StringUtils.isEmpty(endDay)){
			param.put("endDay", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		}else{
			param.put("endDay",endDay);
		}
	}
	
    public static String getNowYMDH() {
    	Calendar now = Calendar.getInstance();
    	String year = String.valueOf(now.get(Calendar.YEAR));
    	String fymdh = year.substring(2,4)+"年"
    				+(now.get(Calendar.MONTH) + 1)+"月"
    				+now.get(Calendar.DAY_OF_MONTH)+"日"
    				+now.get(Calendar.HOUR_OF_DAY)+"时";
    	return fymdh;
    }
    
    public static String getNowYMD() {
    	return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    
    public static String formatDateYMD(Date d) {
    	return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }
    public static String formatDateYMD(String dstr){
    	try{
	    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	    	Date date=sdf.parse(dstr);
	    	return sdf.format(date);
    	}catch(Exception e){
    		return null;
    	}
    }
    public static String formateDateToStr(Date d){
    	
    	return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }
    
    public static String formateDateAfterMinutes(Date d, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }
    
    public static Date getDateAfterMinutes(Date d, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }
    
    /**
     * 获取前一个月第一天
     * 
     * @return
     */
    public static String getFirstDayOfLashMonth(){
    	Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DAY_OF_MONTH,1);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar1.getTime());
    }
    
    /**
     * 获取前一个月最后一天
     * 
     * @return
     */
    public static String getLastDayOfLashMonth(){
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH, 0);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar2.getTime());
    }
    
    /**
     * 获取昨天日期
     * 
     * @return
     */
    public static String getYestodayYMD(){
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE, -1);
    	return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }
    
    /**
     * 获取今天日期
     * 
     * @return
     */
    public static String getTodayYMD(){
    	return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    
    /**
     * 获取当天的前几天后后几天
     * 
     * @param day
     * @return
     */
    public static String getSomeDayOfTodayYMD(int day){
        Date date=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, +day);
        date = calendar.getTime();  
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
    /** 
     * 获取任意时间的月的最后一天
     * @param repeatDate
     * @return
     * @throws ParseException
     */
    public static String getMaxMonthDate(String repeatDate) throws ParseException {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dft.parse(repeatDate));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }
      
    /**
     * 获取任意时间的月第一天 
     * @param repeatDate
     * @return
     * @throws ParseException
     */
    public static String getMinMonthDate(String repeatDate) throws ParseException {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dft.parse(repeatDate));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
       return dft.format(calendar.getTime());
    }
    
    /** 
     * 获取某年第一天日期 
     * @param year 年份 
     * @return Date 
     */  
    public static String getYearFirst(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        return dft.format(currYearFirst);
    }
      
    /** 
     * 获取某年最后一天日期 
     * @param year 年份 
     * @return Date 
     */  
    public static String getYearLast(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        return dft.format(currYearLast);
    }
    
    public static Date formatYmdToDate(String dateStr){
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = null;
    	try {
    		date = format.parse(dateStr);
    	}catch (ParseException e) {
        }
    	return date;
    }

    public static String getMinDayOfMonth(String ds) {
    	try{
	        Calendar cal = Calendar.getInstance();
	        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	        cal.setTime(dft.parse(ds));
	        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
	        return dft.format(cal.getTime());
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public static String getMaxDayOfMonth(String ds) {
    	try{
	        Calendar cal = Calendar.getInstance();
	        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	        cal.setTime(dft.parse(ds));
	        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
	        return dft.format(cal.getTime());
    	}catch(Exception e){
    		return null;
    	}
    }
}
