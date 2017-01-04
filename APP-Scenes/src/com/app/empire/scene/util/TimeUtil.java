package com.app.empire.scene.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.app.empire.scene.service.role.objects.Living;

/**
 * <pre>
 * 时间辅助类
 * </pre>
 */
public class TimeUtil {
	protected static Logger log = Logger.getLogger(TimeUtil.class);

	/**
	 * 获取系统距1970年1月1日总毫秒
	 * 
	 * @return
	 */
	public static long getSysCurTimeMillis() {
		return getCalendar().getTimeInMillis();
	}

	/**
	 * 获取系统距1970年1月1日总秒
	 * 
	 * @return
	 */
	public static long getSysCurSeconds() {
		return getCalendar().getTimeInMillis() / 1000;
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static Timestamp getSysteCurTime() {
		Timestamp ts = new Timestamp(getCalendar().getTimeInMillis());
		return ts;
	}

	public static Timestamp getSysMonth() {
		java.util.Calendar now = getCalendar();
		now.set(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		formatter.format(now.getTime());
		return new Timestamp(now.getTimeInMillis());
	}

	/**
	 * 获取指定日期距1970年1月1日总秒
	 * 
	 * @param date
	 * @return
	 */
	public static long getDateToSeconds(Date date) {
		return getCalendar(date).getTimeInMillis() / 1000;
	}

	/**
	 * 获取时间的秒
	 * 
	 * @param time
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getTimeToSeconds(Time time) {
		if (time != null)
			return time.getHours() * 3600 + time.getMinutes() * 60 + time.getSeconds();
		return 0;
	}

	@SuppressWarnings("deprecation")
	public static Time getSecondsToTime(int seconds) {
		Time time = new Time(seconds / 3600, seconds % 3600 / 60, seconds % 3600 % 60);
		return time;
	}

	/**
	 * 获取当前时间的秒
	 * 
	 * @return
	 */
	public static int getSysTimeSeconds() {
		Calendar cal = getCalendar();
		return cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
	}

	/**
	 * 获取指定日期距1970年1月1日总毫秒
	 * 
	 * @param date
	 * @return
	 */
	public static long getDateToMillis(Date date) {
		return getCalendar(date).getTimeInMillis();
	}

	/**
	 * 获取当前小时
	 * 
	 * @return
	 */
	public static int getCurrentHour() {
		return getCalendar().get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获 取当前分钟
	 * 
	 * @return
	 */
	public static int getCurrentMinute() {
		return getCalendar().get(Calendar.MINUTE);
	}

	/**
	 * 获取当前秒数
	 * 
	 * @return
	 */
	public static int getCurrentSecond() {
		return getCalendar().get(Calendar.SECOND);
	}

	/**
	 * 获取当前天
	 */
	public static int getCurrentDay() {
		return getCalendar().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 指定的毫秒long值转成Timestamp类型
	 * 
	 * @param value
	 * @return
	 */
	public static java.sql.Timestamp getMillisToDate(long value) {
		return new java.sql.Timestamp(value);
	}

	/**
	 * 当前系统时间增加值
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static java.util.Date addSystemCurTime(int type, int value) {
		Calendar cal = getCalendar();
		switch (type) {
		case Calendar.DATE:// 增加天数
			cal.add(Calendar.DATE, value);
			break;
		case Calendar.HOUR:// 增加小时
			cal.add(Calendar.HOUR, value);
			break;
		case Calendar.MINUTE:// 增加分钟
			cal.add(Calendar.MINUTE, value);
			break;
		case Calendar.SECOND:// 增加秒
			cal.add(Calendar.SECOND, value);
			break;
		case Calendar.MILLISECOND:// 增加毫秒
			cal.add(Calendar.MILLISECOND, value);
			break;
		default:
			break;
		}
		return new java.util.Date(cal.getTimeInMillis());
	}

	public static Date getNextDate() {
		Calendar cal = getCalendar();
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateFormat(java.util.Date date) {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String ctime = formatter.format(date);
		return ctime;
	}

	/**
	 * 判断是否在时间段
	 * 
	 * @param startTime 格式 HH:mm
	 * @param endTime 格式 HH:mm
	 * @return
	 */
	public static boolean checkPeriod(String startTime, String endTime) {
		if (startTime.isEmpty() || endTime.isEmpty())
			return false;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String ctime = formatter.format(new Date());
		startTime = ctime + " " + startTime;
		endTime = ctime + " " + endTime;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date nowDate = new Date();
			Date startDateTime = df.parse(startTime);
			Date endDateTime = df.parse(endTime);
			if (nowDate.getTime() > startDateTime.getTime() && nowDate.getTime() < endDateTime.getTime()) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	// public static void main(String[] args) {
	// boolean r = checkPeriod("8:15", "20:11");
	// System.out.println(r);
	// }

	/**
	 * 获取默认日期2000-01-01
	 * 
	 * @return 返回默认起始时间
	 */
	public static java.sql.Timestamp getDefaultDate() {
		java.util.Date defaultDate = null;
		try {
			defaultDate = (java.util.Date) new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parseObject("2000-01-01 00:00:00");

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new java.sql.Timestamp(defaultDate.getTime());
	}

	/**
	 * 获取默认目上限日期2999-01-01
	 * 
	 * @return 返回默认上限时间
	 */
	public static java.sql.Timestamp getDefaultMaxDate() {
		java.util.Date defaultDate = null;
		try {
			defaultDate = (java.util.Date) new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parseObject("2999-01-01 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new java.sql.Timestamp(defaultDate.getTime());
	}

	/**
	 * <pre>
	 * 比较日期是否同一天(注意：分界线为晚上 12 点)
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static boolean dateCompare(Date date) {
		if (date == null)
			return false;
		java.util.Calendar now = getCalendar();
		java.util.Calendar other = getCalendar(date);
		return dateCompare(now, other) == 0 ? true : false;
	}

	/**
	 * <pre>
	 * 比较日期是否同一天(注意：分界线为晚上 12 点)
	 * </pre>
	 * 
	 * @param long
	 * @return
	 */
	public static boolean dateCompare(long date) {
		java.util.Calendar now = getCalendar();
		java.util.Calendar other = getCalendar(getMillisToDate(date));
		return dateCompare(now, other) == 0 ? true : false;
	}

	/**
	 * <pre>
	 * 比较是否为同一天(注意：分界线为凌晨 5 点)
	 * </pre>
	 * 
	 * @param date
	 * @return false:不是同一天
	 */
	public static boolean dataCompare5(Date date) {
		if (date == null)
			return false;
		java.util.Calendar now = getCalendar();
		now.add(Calendar.HOUR_OF_DAY, -5);
		java.util.Calendar other = getCalendar(date);
		other.add(Calendar.HOUR_OF_DAY, -5);
		if (dateCompare(now, other) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * <pre>
	 * 比较是否为同一天(注意：分界线为凌晨 6 点)
	 * </pre>
	 * 
	 * @param date
	 * @return true：同一天
	 */
	public static boolean dataCompare6(Date date) {
		if (date == null)
			return false;
		java.util.Calendar now = getCalendar();
		now.add(Calendar.HOUR_OF_DAY, -6);
		java.util.Calendar other = getCalendar(date);
		other.add(Calendar.HOUR_OF_DAY, -6);
		if (dateCompare(now, other) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * <pre>
	 * 比较日期是否同一天(注意：分界线为晚上 12 点)
	 * </pre>
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean dataCompare(Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return false;
		java.util.Calendar c1 = getCalendar(date1);
		java.util.Calendar c2 = getCalendar(date2);
		return dateCompare(c1, c2) == 0 ? true : false;
	}

	/**
	 * 返回两个日期相差天数
	 * 
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @return
	 */
	public static int dateCompare(java.util.Calendar startDate, java.util.Calendar endDate) {
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);

		int day = (int) (endDate.getTimeInMillis() / 1000 / 60 / 60 / 24 - startDate.getTimeInMillis() / 1000 / 60 / 60 / 24);
		return day;
	}

	/**
	 * <pre>
	 * 比较日期是否同一天(注意：分界线为晚上 12 点)
	 * </pre>
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static int dateCompare(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			log.error("日期比较出现异常 数值为 null");
			return 0;
		}
		java.util.Calendar c1 = getCalendar(startDate);
		java.util.Calendar c2 = getCalendar(endDate);
		return dateCompare(c1, c2);
	}

	/**
	 * <pre>
	 * 返回两个日期相差天数(注意：分界线为凌晨 5 点)
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static int dateCompare5(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return 0;
		}
		java.util.Calendar c1 = getCalendar(startDate);
		c1.add(Calendar.HOUR_OF_DAY, -5);
		java.util.Calendar c2 = getCalendar(endDate);
		c2.add(Calendar.HOUR_OF_DAY, -5);
		return dateCompare(c1, c2);
	}

	/**
	 * <pre>
	 * 返回两个日期相差天数(注意：分界线为凌晨 6 点)
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static int dateCompare6(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			return 0;
		}
		java.util.Calendar c1 = getCalendar(startDate);
		c1.add(Calendar.HOUR_OF_DAY, -6);
		java.util.Calendar c2 = getCalendar(endDate);
		c2.add(Calendar.HOUR_OF_DAY, -6);
		return dateCompare(c1, c2);
	}

	/**
	 * 比较日期是否是同一个月份
	 * 
	 * @param date 被比较的日期
	 * @return
	 */
	public static boolean monthCompare(Date date) {// 一年之内是否是同一个月
		if (date == null)
			return false;
		java.util.Calendar now = getCalendar();
		java.util.Calendar other = getCalendar(date);
		int nowMonth = now.get(Calendar.MONTH) + 1;
		int otherMonth = other.get(Calendar.MONTH) + 1;
		return (otherMonth - nowMonth) == 0 ? true : false;
	}

	/**
	 * 获取该月的天数
	 * 
	 * @return
	 */
	public static int monthDays() {// 返回当前月份的天数
		java.util.Calendar now = getCalendar();
		return now.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取当前是该月的第几天
	 * 
	 * @return
	 */
	public static int monthDay() {
		java.util.Calendar now = getCalendar();
		return now.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 重置防沉迷刷新时间
	 * 
	 * @param hour 刷新时间点
	 * @param refreshTime 刷新时间引用
	 */
	public static void setAASRefreshTime(int hour, Calendar refreshTime) {
		refreshTime.setTime(getSysteCurTime());
		refreshTime.set(Calendar.HOUR_OF_DAY, hour);
		refreshTime.set(Calendar.MINUTE, 0);
		refreshTime.set(Calendar.SECOND, 0);
	}

	public static long calcDistanceMillis(Date startTime, Date endTime) {
		long startSecond = getDateToSeconds(startTime);
		long endSecond = getDateToSeconds(endTime);
		return (endSecond - startSecond) * 1000;
	}

	/**
	 * 间隔时间以小时为单位
	 * 
	 * @param startDate
	 * @param interval
	 * @return
	 */
	public static boolean isInterval(Date startDate, int interval) {
		return dataCompare5(startDate);
	}

	public static int timeToFrame(int secondTime) {
		return (secondTime * 25) / 1000;
	}

	public static String getSignStr() {
		String[] strs = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1",
				"2", "3", "4", "5", "6", "7", "8", "9" };

		ThreadSafeRandom random = new ThreadSafeRandom();
		String signStr = "";
		for (int i = 0; i < 6; i++) {
			int j = random.next(strs.length);
			signStr += strs[j];

		}
		return signStr;
	}

	/**
	 * 获取系统时间
	 * 
	 * @return
	 */
	public synchronized static Calendar getCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * 获取指定的时间
	 * 
	 * @param date
	 * @return
	 */
	public static java.util.Calendar getCalendar(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 获取指定的时间
	 * 
	 * @param date
	 * @return
	 */
	public static java.util.Calendar getCalendar(long time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		return calendar;
	}

	// 这个方法好像有问题，先注释掉
	// public static Timestamp getCalendarToDate(java.util.Calendar calendar) {
	// if (calendar != null)
	// return new Timestamp(getCalendar().getTimeInMillis());
	// return null;
	// }

	public static Date addDate(Date date, long value) {
		long time = date.getTime() + value;
		return new Date(time);
	}

	/**
	 * 把日期类型转换为字节数组
	 * 
	 * @param date
	 * @return
	 */
	public static byte[] dateToBytes(Date date) {
		Calendar calendar = Calendar.getInstance();
		byte[] byteArray = new byte[7];
		calendar.setTime(date);
		short year = (short) calendar.get(Calendar.YEAR);
		byteArray[0] = (byte) ((year >>> 8) & 0xFF);
		byteArray[1] = (byte) (year & 0xFF);
		byteArray[2] = (byte) (calendar.get(Calendar.MONTH) + 1);
		byteArray[3] = (byte) calendar.get(Calendar.DATE);
		byteArray[4] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
		byteArray[5] = (byte) calendar.get(Calendar.MINUTE);
		byteArray[6] = (byte) calendar.get(Calendar.SECOND);
		return byteArray;
	}

	public static Date getSunday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		Date monday = currentDate.getTime();
		return monday;
	}

	public static Date getNextMonday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		Date monday = currentDate.getTime();
		return monday;
	}

	private static int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		// 获得今天是一周的第几天，星期日是第一天，星期一是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
		if (dayOfWeek == 1) {
			return 0;
		} else {
			return 1 - dayOfWeek;
		}
	}

	public static int getDayOfWeekIndex() {
		Calendar calendar = Calendar.getInstance();
		int index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (index == 0) {
			index = 7;
		}
		return index;
	}

	public static boolean isTimeOut(Date expDate) {
		Calendar curentDate = Calendar.getInstance();
		Calendar expirtDate = Calendar.getInstance();
		expirtDate.setTime(expDate);

		long intervalMillis = expirtDate.getTimeInMillis() - curentDate.getTimeInMillis();
		return intervalMillis <= 0;
	}

	public static Date getSaturday(int nextWeek) {
		int mondayPlus = getMondayPlus();
		if (nextWeek > 0) {
			mondayPlus = mondayPlus + (nextWeek * 7);
		}
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 5);
		currentDate.set(Calendar.HOUR_OF_DAY, 5);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		Date saturday = currentDate.getTime();
		return saturday;
	}

	public static boolean isSaturday() {
		int dayIndex = getDayOfWeekIndex();
		if (6 == dayIndex) {
			return true;
		}
		return false;
	}

	public static Date parseDate(String dateStr) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = df.parse(dateStr);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isInDate(long curTimeMillis, Date openDate, Date stopDate) {
		if (openDate == null || stopDate == null) {
			return false;
		}
		long openMillis = TimeUtil.getDateToMillis(openDate);
		long stopMillis = TimeUtil.getDateToMillis(stopDate);
		return isInDate(curTimeMillis, openMillis, stopMillis);
	}

	public static boolean isInDate(long curTimeMillis, long openMillis, long stopMillis) {
		return curTimeMillis >= openMillis && curTimeMillis <= stopMillis;
	}

	public static boolean isAfter(long currentMillis, Date date) {
		if (date == null || date == null) {
			return false;
		}
		long openMillis = TimeUtil.getDateToMillis(date);
		return currentMillis >= openMillis;
	}

	public static Date addTime(Date current, int type, int value) {
		Calendar cal = getCalendar(current);
		switch (type) {
		case Calendar.DATE:// 增加天数
			cal.add(Calendar.DATE, value);
			break;
		case Calendar.HOUR:// 增加小时
			cal.add(Calendar.HOUR, value);
			break;
		case Calendar.MINUTE:// 增加分钟
			cal.add(Calendar.MINUTE, value);
			break;
		case Calendar.SECOND:// 增加秒
			cal.add(Calendar.SECOND, value);
			break;
		case Calendar.MILLISECOND:// 增加毫秒
			cal.add(Calendar.MILLISECOND, value);
			break;
		default:
			break;
		}
		return new java.util.Date(cal.getTimeInMillis());
	}

	public static boolean isSaturday(Date date) {
		int dayIndex = getDayOfWeekIndex(date);
		if (6 == dayIndex) {
			return true;
		}
		return false;
	}

	public static int getDayOfWeekIndex(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (index == 0) {
			index = 7;
		}
		return index;
	}

	/** 上周一 00:00:01，譬如：当前时间 2013-05-03 返回：2013-04-29 00:00:30 */
	public static Date getSunday(int nextWeek) {
		int mondayPlus = getMondayPlus();
		if (nextWeek > 0) {
			mondayPlus = mondayPlus + (nextWeek * 7);
		}
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 30);
		Date saturday = currentDate.getTime();
		return saturday;
	}

	/**
	 * <pre>
	 * 获取 date 所在周的周日日期
	 * </pre>
	 * 
	 * @param date
	 * @return
	 */
	public static Date getCurrentWeekEndDate(Date date) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(date);

		// 取当前日期是星期几(week:星期几)
		int week = aCalendar.get(Calendar.DAY_OF_WEEK) - 1;
		int count = 0;
		if (week == 1) {
			count = 6;
		} else if (week == 2) {
			count = 5;
		} else if (week == 3) {
			count = 4;
		} else if (week == 4) {
			count = 3;
		} else if (week == 5) {
			count = 2;
		} else if (week == 6) {
			count = 1;
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_WEEK, count);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 是否是同一周。以每同一的5点为分隔点
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean isSameWeek5(Date d1, Date d2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(d1);
		cal1.add(Calendar.HOUR, -5); // 向前推5个小时

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(d2);
		cal2.add(Calendar.HOUR, -5);

		if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
			return true;
		} else {
			return false;
		}
	}

	public static Calendar getNextDateByIndex(int indexOfWeek) {
		int addDay = 0;
		int mondayPlus = getDayOfWeekIndex();
		if (mondayPlus == 7) {
			addDay = 0;
		} else if (mondayPlus == 6) {
			addDay = 1;
		} else if (mondayPlus == 5) {
			addDay = 2;
		} else if (mondayPlus == 4) {
			addDay = 3;
		} else if (mondayPlus == 3) {
			addDay = 4;
		} else if (mondayPlus == 2) {
			addDay = 5;
		} else if (mondayPlus == 1) {
			addDay = 6;
		}
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, addDay + indexOfWeek);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		return currentDate;
	}

	/**
	 * 判断指定时间是否在时间范围内
	 * 
	 * @param curTime 指定时间
	 * @param timeType 时间限制类型 0无限制 1 每日 2固定时间
	 * @param startTime 开始时间，格式如下： 时间限制timeType为1时格式：HHmmss; 时间限制timeType为2时格式：yyyyMMddHHmmss
	 * @param endTime 结束时间，格式同开始时间
	 * @return
	 */
	public static boolean isInTime(long curTime, int timeType, String startTime, String endTime) {
		return isInTime(getCalendar(curTime), timeType, startTime, endTime);
	}

	/**
	 * 判断指定时间是否在时间范围内
	 * 
	 * @param curTime 指定时间
	 * @param timeType 时间限制类型 0无限制 1 每日 2固定时间
	 * @param startTime 开始时间，格式如下： 时间限制timeType为1时格式：HHmmss; 时间限制timeType为2时格式：yyyyMMddHHmmss
	 * @param endTime 结束时间，格式同开始时间
	 * @return
	 */
	public static boolean isInTime(Calendar curTime, int timeType, String startTime, String endTime) {
		if (timeType == 0) {
			return true;
		}
		DateFormat dfDate = new SimpleDateFormat("yyyyMMddHHmmss");

		String startStr;
		String endStr;

		switch (timeType) {
		case 1:
			while (startTime.length() < 6) {
				startTime = "0" + startTime;
			}
			while (endTime.length() < 6) {
				endTime = "0" + endTime;
			}

			DateFormat yyyyMMddDf = new SimpleDateFormat("yyyyMMdd");
			String yyyyMMddStr = yyyyMMddDf.format(curTime.getTime());

			startStr = yyyyMMddStr + startTime;
			endStr = yyyyMMddStr + endTime;
			break;
		case 2:
			while (startTime.length() < 14) {
				startTime = "0" + startTime;
			}
			while (endTime.length() < 14) {
				endTime = "0" + endTime;
			}
			startStr = startTime;
			endStr = endTime;
			break;
		default:
			return false;
		}
		Date startDate;
		Date endDate;
		try {
			startDate = dfDate.parse(startStr);
			endDate = dfDate.parse(endStr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		if (curTime.getTimeInMillis() >= startDate.getTime() && curTime.getTimeInMillis() <= endDate.getTime()) {
			return true;
		}
		return false;
	}

	/**
	 * 字符串转DATE 1:hhmmss 3:yyyyMMddHHmmss
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date getDateByString(String dateStr, int timeType) {
		Date date = null;
		if (StringUtils.isNullOrEmpty(dateStr)) {
			return null;
		}
		String format = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (timeType == 1) {
			dateStr = getNowYYYYMMDD() + dateStr;
		} else if (timeType == 3) {
			format = "yyyyMMddHHmmss";
			sdf = new SimpleDateFormat(format);
		}
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}

	/**
	 * 根据当天的日期将字符串转日期对象
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date getDateFromNowByString(String dateStr) {
		Date date = null;
		if (StringUtils.isNullOrEmpty(dateStr)) {
			return null;
		}

		String format = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		try {
			date = sdf.parse(getNowYYYYMMDD() + dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取当前的YYMMDD
	 * 
	 * @return
	 */
	public static final String getNowYYYYMMDD() {
		DateFormat ymdFormat = new SimpleDateFormat("yyyyMMdd");
		return ymdFormat.format(getCalendar().getTime());
	}

	/**
	 * 获取时间
	 * 
	 * @param timeStr 配置格式：HHmmss
	 * @return
	 * @throws ParseException
	 */
	public static Date getDate(String timeStr) throws ParseException {
		Date now = new Date();
		SimpleDateFormat cf = new SimpleDateFormat("yyyy-MM-dd");
		String str = cf.format(now);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddHHmmss");
		Date startDate = df.parse(str + timeStr);
		return startDate;
	}
}
