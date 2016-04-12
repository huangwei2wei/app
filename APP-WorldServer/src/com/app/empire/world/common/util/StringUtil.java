package com.app.empire.world.common.util;

import java.util.regex.Pattern;

public class StringUtil {

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public final static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
}
