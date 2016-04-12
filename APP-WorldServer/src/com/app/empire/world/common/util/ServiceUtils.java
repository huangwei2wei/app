package com.app.empire.world.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

public class ServiceUtils {
	static Logger log = Logger.getLogger(ServiceUtils.class);
	public static Random randGen;
	private static final byte[] highDigits;
	private static final byte[] lowDigits;
	static {
		byte[] digits = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
		byte[] high = new byte[256];
		byte[] low = new byte[256];
		for (int i = 0; i < 256; ++i) {
			high[i] = digits[(i >>> 4)];
			low[i] = digits[(i & 0xF)];
		}
		highDigits = high;
		lowDigits = low;
		randGen = new Random();
	}

	/**
	 * 记录日志
	 * 
	 * @param log
	 * @param id
	 * @param typeStr
	 * @param msg
	 */
	public static void log(Logger log, int id, String typeStr, String msg) {
		StringBuffer buff = new StringBuffer();
		buff.append("ID[");
		buff.append(id);
		buff.append("],");
		buff.append("TYPE[");
		buff.append(typeStr);
		buff.append("],");
		if (msg != null) {
			buff.append(msg);
		}
		log.info(buff.toString());
	}

	/**
	 * 记录日志
	 * 
	 * @param log
	 * @param id
	 * @param type
	 * @param subType
	 * @param msg
	 */
	public static void log(Logger log, int id, byte type, byte subType, String msg) {
		StringBuffer buff = new StringBuffer();
		buff.append("ID[");
		buff.append(id);
		buff.append("],");
		buff.append("TYPE[");
		buff.append(new StringBuilder().append(type).append(".").append(subType).toString());
		buff.append("],");
		if (msg != null) {
			buff.append(msg);
		}
		log.info(buff.toString());
	}

	/**
	 * 记录日志
	 * 
	 * @param log
	 * @param id
	 * @param type
	 * @param msg
	 */
	public static void log(Logger log, int id, int type, String msg) {
		StringBuffer buff = new StringBuffer();
		buff.append("ID[");
		buff.append(id);
		buff.append("],");
		buff.append("TYPE[");
		buff.append(type);
		buff.append("],");
		if (msg != null) {
			buff.append(msg);
		}
		log.info(buff.toString());
	}

	/**
	 * 检查非法字符
	 * 
	 * @param s
	 * @param allowColon
	 * @return
	 */
	public static boolean checkString(String s, boolean allowColon) {
		if (s == null) {
			return false;
		}
		for (int i = 0; i < s.length(); ++i) {
			char ch = s.charAt(i);
			boolean isValid = false;
			if ((ch >= 'a') && (ch <= 'z'))
				isValid = true;
			else if ((ch >= 'A') && (ch <= 'Z'))
				isValid = true;
			else if ((ch >= '0') && (ch <= '9'))
				isValid = true;
			else if (ch == '_')
				isValid = true;
			else if ((ch >= 19968) && (ch <= 40869))
				isValid = true;
			else if ((ch >= 44032) && (ch <= 55215))
				isValid = true;
			else if ((ch >= 4352) && (ch <= 4607))
				isValid = true;
			else if ((ch >= 12592) && (ch <= 12687))
				isValid = true;
			else if ((ch >= 'ぁ') && (ch <= 'ん'))// 日文
				isValid = true;
			else if ((ch >= 'ァ') && (ch <= 'ヶ'))// 日文
				isValid = true;
			else if (ch == 'ー')// 日文
				isValid = true;
			else if (ch == '々')// 日文
				isValid = true;
			else if ((allowColon) && (ch == ':')) {
				isValid = true;
			}
			if (!(isValid)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 解析十六进制输出
	 * 
	 * @param in
	 * @return
	 */
	public static String getHexdump(byte[] in) {
		if (in == null)
			return "null";
		if (in.length == 0) {
			return "empty";
		}
		StringBuffer out = new StringBuffer(in.length * 3);
		for (int i = 0; i < in.length; ++i) {
			int byteValue = in[i] & 0xFF;
			out.append((char) highDigits[byteValue]);
			out.append((char) lowDigits[byteValue]);
			out.append(' ');
		}
		return out.toString();
	}

	/**
	 * 四舍五入取整数
	 * 
	 * @param old
	 * @return
	 */
	public static int getInt(double old) {
		BigDecimal test = new BigDecimal(old).setScale(0, BigDecimal.ROUND_HALF_UP);
		return test.intValue();
	}

	/**
	 * 转换byte数组
	 * 
	 * @param objs
	 * @return
	 */
	public static byte[] getBytes(Object[] objs) {
		byte[] ret = new byte[objs.length];
		int i = 0;
		for (Object obj : objs) {
			ret[i] = null == obj ? 0 : Byte.parseByte(obj.toString());
			i++;
		}
		return ret;
	}

	/**
	 * 转换int数组
	 * 
	 * @param objs
	 * @return
	 */
	public static int[] getInts(Object[] objs) {
		int[] ret = new int[objs.length];
		int i = 0;
		for (Object obj : objs) {
			ret[i] = null == obj ? 0 : Integer.parseInt(obj.toString());
			i++;
		}
		return ret;
	}

	/**
	 * 转换String数组
	 * 
	 * @param objs
	 * @return
	 */
	public static String[] getStrings(Object[] objs) {
		String[] ret = new String[objs.length];
		int i = 0;
		for (Object obj : objs) {
			ret[i] = obj.toString();
			i++;
		}
		return ret;
	}

	/**
	 * 转换boolean数组
	 * 
	 * @param objs
	 * @return
	 */
	public static boolean[] getBooleans(Object[] objs) {
		boolean[] ret = new boolean[objs.length];
		int i = 0;
		for (Object obj : objs) {
			ret[i] = null == obj ? false : Boolean.parseBoolean(obj.toString());
			i++;
		}
		return ret;
	}

	/**
	 * 用于控制台输出
	 * 
	 * @param text
	 */
	public static void out(String text) {
		System.out.println(text);
	}

	/**
	 * 获取start到end的随机数(不包括end，不包括负数)
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getRandomNum(int start, int end) {
		int ret = (int) (Math.random() * (end - start) + start);
		return ret;
	}

	/**
	 * 随机获取传入的两个参数中的一个
	 * 
	 * @param iPlayer
	 * @param b
	 * @return
	 */
	public static int getRandomNumIntTwo(int a, int b) {
		int ret = (int) (Math.random() % 2);
		if (ret == 0) {
			return a;
		} else {
			return b;
		}
	}

	/**
	 * 校验String是否有纯数字组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取指定时间当天的凌晨
	 * 
	 * @param date
	 *            给定时间当天的凌晨
	 * @return
	 */
	public static Date getMorning(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/**
	 * 利用MD5进行加密
	 * 
	 * @param str
	 *            待加密的字符串
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 *             没有这种产生消息摘要的算法
	 * @throws UnsupportedEncodingException
	 */
	public static String getMD5(String str) {
		String reStr = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");// 创建具有指定算法名称的信息摘要
			md.update(str.getBytes());// 使用指定的字节更新摘要。
			byte ss[] = md.digest();// 通过执行诸如填充之类的最终操作完成哈希计算
			reStr = bytes2String(ss);
		} catch (NoSuchAlgorithmException e) {
		}
		return reStr;
	}

	private static String bytes2String(byte[] aa) {// 将字节数组转换为字符串
		String hash = "";
		for (int i = 0; i < aa.length; i++) {// 循环数组
			int temp;
			if (aa[i] < 0) // 如果小于零，将其变为正数
				temp = 256 + aa[i];
			else
				temp = aa[i];
			if (temp < 16)
				hash += "0";
			hash += Integer.toString(temp, 16);// 转换为16进制
		}
		hash = hash.toUpperCase();// 全部转换为大写
		return hash;
	}

	/**
	 * 线程随机休眠0到10分钟
	 */
	public static void sleepRandomTime() {
		try {
			Thread.sleep(getRandomNum(0, 600001));
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Intger List 转 int[]
	 * 
	 * @param list
	 * @return
	 */
	public static int[] ListToInts(List<Integer> list) {
		int[] results = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			results[i] = list.get(i).intValue();
		}
		return results;
	}

	/**
	 * String List 转 String[]
	 * 
	 * @param list
	 * @return
	 */
	public static String[] ListToStrs(List<String> list) {
		String[] results = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			results[i] = list.get(i);
		}
		return results;
	}

	/**
	 * 跟进奖励文本生成奖励信息
	 * 
	 * @param rewardStr
	 *            "[11,20]&[28,26]"
	 * @param sex
	 *            角色性别
	 */
	public static List<RewardInfo> getRewardInfo(String rewardStr) {
		rewardStr = rewardStr.replace("[", "").replace("]", "");
		String[] awards = rewardStr.split("&");
		List<RewardInfo> rewardInfoList = new ArrayList<ServiceUtils.RewardInfo>();
		for (String award : awards) {
			String[] info = award.split(",");
			try {

			} catch (Exception e) {
				log.error("奖励内容解释出错！");
			}
		}
		return rewardInfoList;
	}

	/**
	 * 根据奖励文本生成奖励信息
	 * 
	 * @param rewardStr
	 *            奖励文本格式："[11,20]&[28,26]"
	 * @param sex
	 *            性别：0男,1女
	 * @return
	 */
	public static List<RewardInfo> getRewardInfo(String rewardStr, int sex) {
		List<RewardInfo> rewardInfoList = new ArrayList<ServiceUtils.RewardInfo>();
		if ("-1".equals(rewardStr))
			return rewardInfoList;
		List<RewardInfo> rewardAll = getRewardInfo(rewardStr);
		for (RewardInfo reward : rewardAll) {
			if (reward.getSex() == 2 || reward.getSex() == sex) {
				rewardInfoList.add(reward);
			}
		}
		return rewardInfoList;
	}

	public static class RewardInfo {
		private int itemId;
		private int count;
		private boolean addDay;
		private int sex;
		private int level;

		public int getItemId() {
			return itemId;
		}

		public void setItemId(int itemId) {
			this.itemId = itemId;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public boolean isAddDay() {
			return addDay;
		}

		public void setAddDay(boolean addDay) {
			this.addDay = addDay;
		}

		public int getSex() {
			return sex;
		}

		public void setSex(int sex) {
			this.sex = sex;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}
	}

	/**
	 * 获取随机战斗模式
	 * 
	 * @param level
	 * @return
	 */
	public static int getRandomBattleMode(int level) {
		int randomNum = getRandomNum(1, 101);
		if (level < 11) {
			return 1;
		} else if (level < 16) {
			if (randomNum < 81) {
				return 1;
			} else if (randomNum < 91) {
				return 2;
			} else {
				return 3;
			}
		} else if (level < 21) {
			if (randomNum < 71) {
				return 1;
			} else if (randomNum < 91) {
				return 2;
			} else {
				return 3;
			}
		} else {
			if (randomNum < 71) {
				return 1;
			} else if (randomNum < 86) {
				return 2;
			} else {
				return 3;
			}
		}
	}

	/**
	 * 获取随机人数模式
	 * 
	 * @param level
	 * @return
	 */
	public static int getPlayerNumMode(int level) {
		int randomNum = getRandomNum(1, 101);
		if (level < 11) {
			return 1;
		} else if (level < 16) {
			if (randomNum < 81) {
				return 1;
			} else if (randomNum < 96) {
				return 2;
			} else {
				return 3;
			}
		} else if (level < 21) {
			if (randomNum < 71) {
				return 1;
			} else if (randomNum < 91) {
				return 2;
			} else {
				return 3;
			}
		} else {
			if (randomNum < 61) {
				return 1;
			} else if (randomNum < 81) {
				return 2;
			} else {
				return 3;
			}
		}
	}
}
