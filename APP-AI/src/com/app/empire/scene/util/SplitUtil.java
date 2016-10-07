package com.app.empire.scene.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitUtil {

	private SplitUtil() {
	}

	public static int[] splitToInt(String str) {
		return splitToInt(str, ",");
	}

	public static int[] splitToInt(String str, String spStr) {
		if (str == null || str.trim().length() == 0) {
			return new int[0];
		}

		try {
			String[] temps = str.split(spStr);
			int len = temps.length;
			int[] results = new int[len];
			for (int i = 0; i < len; i++) {
				results[i] = Integer.parseInt(temps[i].trim());
			}
			return results;
		} catch (Exception e) {
			return new int[0];
		}
	}

	public static String concatToStr(int[] ints) {
		if (ints == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ints.length; i++) {
			sb.append(ints[i]).append(",");
		}
		if (sb.length() == 0) {
			return "";
		}
		return sb.substring(0, sb.length() - 1).toString();
	}

	public static void main(String[] str) {
		print(splitToInt("1"));
		print(splitToInt("1"));
		print(splitToInt("1,2,3"));
		print(splitToInt("1,2,3,"));
		print(concatToStr(new int[] { 1, 2, 3, 4 }));
	}

	public static void print(int[] results) {
		for (int i = 0; i < results.length; i++) {
			System.err.print(results[i] + ",");
		}
	}

	public static void print(String str) {
		System.err.println(str);
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		return isNum.matches();
	}

	/**
	 * 解析英雄模板ID
	 * 
	 * @param heros
	 * @return
	 */
	public static int[] parseHeroTemplate(String heros) {
		if (heros == null || heros.trim().length() == 0)
			return null;
		if (heros != null && heros.trim().length() > 0) {
			String[] heroArray = heros.split("[|]");
			if (heroArray.length > 0) {
				int[] heroTemplateIds = new int[heroArray.length];
				int idx = 0;
				for (String idAndPos : heroArray) {
					String[] idPos = idAndPos.split("[,]");
					heroTemplateIds[idx] = Integer.parseInt(idPos[0]);
					idx++;
				}
				return heroTemplateIds;
			}
		}
		return null;
	}
	
	public static int[][]  parseIntArray(String src,String separator1,String separator2){
	
		String[] strs = src.split("["+separator1+"]");
		
		int[][] buffInfos = new int[strs.length][2];
		
		for (int i = 0; i < strs.length;i++) {

			String[]  regs = strs[i].split(separator2);

			buffInfos[i][0] = Integer.parseInt(regs[0]);
			buffInfos[i][1] = Integer.parseInt(regs[1]);
		}
		return buffInfos;
	}

}
