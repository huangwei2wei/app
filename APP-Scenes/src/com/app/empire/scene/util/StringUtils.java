package com.app.empire.scene.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 字符串辅助类
 * </pre>
 */
public class StringUtils {

	/**
	 * 判断字符串是否超过最大长度
	 * 
	 * @param rawStr
	 * @param maxLen
	 * @return
	 */
	public static String verifyMaxLen(String rawStr, int maxLen) {
		if (rawStr == null || rawStr.trim().length() == 0) {
			return rawStr;
		}
		if (rawStr.length() > maxLen) {
			return rawStr.substring(0, maxLen);
		}
		return rawStr;
	}

	public static boolean verifyMaxByteLen(String str, int maxLen) {
		try {
			if (str.getBytes("UTF-8").length > maxLen) {
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * <pre>
	 * 判断字符串是否为 null 或者 空串
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * <pre>
	 * 是否为数字类型(负数返回false)
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (str.matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <pre>
	 * 判断指定字符串是否包含空白字符，包括\\s*|\t|\r|\n
	 * </pre>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean containWhitespace(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}

		char[] data = str.toCharArray();
		for (char i : data) {
			if (Character.isWhitespace(i)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @param str 1,2,3,4,5,6
	 * @return
	 */
	public static int[] getStr2Ids(String str) {
		int[] bufferIdArr = new int[0];
		if (str != null && !str.isEmpty()) {
			String[] s = str.split(",");
			bufferIdArr = new int[s.length];
			for (int i = 0; i < s.length; i++) {
				if (s[i].isEmpty()) {
					continue;
				}
				int id = Integer.valueOf(s[i]);
				bufferIdArr[i] = id;
			}
		}
		return bufferIdArr;
	}

	// public static void main(String[] args) {
	// StringUtils sss = new StringUtils();
	// int[] ii = sss.getBufferIds("1,2,3,4,5,6,");
	// System.out.println(ii);
	// }

}
