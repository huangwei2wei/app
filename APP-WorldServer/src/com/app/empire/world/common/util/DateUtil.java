package com.app.empire.world.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期基类 一些日期相关操作
 */
public class DateUtil {
	// 定义默认时区
	public final static String DEFAULT_ZONE_ID = "Asia/Hong_Kong";
	// 设置默认时区
	static {
		// TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_ZONE_ID));
	}
	/**
	 * 格式化时间
	 * 
	 * @param date 日期
	 * @param formatStr 格式： "yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String format(Date date, String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		return format.format(new Date());
	}
	/**
	 * 时间字符串转data
	 * 
	 * @param data
	 * @param formatStr
	 * @return
	 */
	public static Date parse(String data, String formatStr) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatStr);
			return format.parse(data);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取当前日期-年份
	 * 
	 * @return 当前日期-年份
	 */
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * 获取当前日期-月份
	 * 
	 * @return 当前日期-月份
	 */
	public static int getCurrentMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当前日期-在一年中第几周
	 * 
	 * @return 当前在一年中第几周
	 */
	public static int getCurrentWeek() {
		return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 获取当前日期-日数
	 * 
	 * @return 当前日期-日数
	 */
	public static int getCurrentDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取当前日期-小时
	 * 
	 * @return 当前日期-小时
	 */
	public static int getCurrentHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取当前日期-分钟
	 * 
	 * @return 当前日期-分钟
	 */
	public static int getCurrentMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	/**
	 * 获取当前日期-秒钟
	 * 
	 * @return 当前日期-秒钟
	 */
	public static int getCurrentSecond() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}

	public static void main(String[] s) {
		System.out.println(format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		System.out.println(getCurrentHour());
		System.out.println(getCurrentMonth());

		System.out.println(format(new Date(), "yyyyMMdd"));
	}
}
