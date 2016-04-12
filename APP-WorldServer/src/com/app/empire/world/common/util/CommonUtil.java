package com.app.empire.world.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CommonUtil {

	/**
	 * 自定义字符串格式化
	 * 
	 * @param str bd:1,be:0:15,aa:1:4:4,ab:1:4:4:5,ac:1:4:4:1:4:4:5
	 */
	public static Map<String, List<Object>> strToMap(String str) {
		Map<String, List<Object>> dataMap = new HashMap<String, List<Object>>();
		if (str == null || str.equals(""))
			return dataMap;
		String[] arrStr = str.split(",");
		for (String str1 : arrStr) {
			String[] arrStr2 = str1.split(":");
			ArrayList<Object> arr = new ArrayList<Object>();
			for (int i = 0; i < arrStr2.length; i++) {
				if (i == 0 && !dataMap.containsKey(arrStr2[i]))
					dataMap.put(arrStr2[i], arr);
				else
					arr.add(arrStr2[i]);
			}
		}
		return dataMap;
	}

	/**
	 * 自定义字符串格式化
	 * 
	 * @param str bd:1:2,be:0:15,aa:1:4:4,ab:1:4:5,ac:1:4:5
	 * @return {"key1_key2":key3,"key1_key2":key3}
	 */
	public static Map<String, String> strToMap2(String str) {
		String[] arrStr = str.split(",");
		Map<String, String> dataMap = new HashMap<String, String>();
		for (String str1 : arrStr) {
			String[] arrStr2 = str1.split(":");
			String key = arrStr2[0] + "_" + arrStr2[1];
			dataMap.put(key, arrStr2[2]);
		}
		return dataMap;
	}

	/**
	 * map 转自定义字符串
	 * 
	 * @param map {"ab":[1,2,3,4,5,6,7,8],"ac":[1,2,3,4,5,6,7,8]}
	 */
	public static String mapToStr(Map<String, List<Object>> map) {
		StringBuffer strBuff = new StringBuffer("");
		boolean isRun = false;
		for (String str : map.keySet()) {
			strBuff.append(str);
			strBuff.append(":");
			List<Object> arr = map.get(str);
			for (int i = 0; i < arr.size(); i++) {
				strBuff.append(arr.get(i));
				if (i == arr.size() - 1) {
					strBuff.append(",");
				} else {
					strBuff.append(":");
				}
				isRun = true;
			}
		}
		if (isRun) {
			return strBuff.substring(0, strBuff.length() - 1);
		}
		return strBuff.toString();
	}
	/**
	 * map 转自定义字符串 key->val
	 * 
	 * @param map {"ab":123,"ac":123.02}
	 */
	public static String mapToStr2(Map<String, Double> map) {
		StringBuffer strBuff = new StringBuffer("");
		boolean isRun = false;
		for (Entry<String, Double> entry : map.entrySet()) {
			strBuff.append(entry.getKey());
			strBuff.append(":");
			strBuff.append(entry.getValue());
			strBuff.append(",");
			isRun = true;
		}
		if (isRun) {
			return strBuff.substring(0, strBuff.length() - 1);
		}
		return strBuff.toString();
	}

	/**
	 * 字典模式随机最大只支持3位小数,否则报错 N 个中出一个
	 * 
	 * @param List HashMap<String, Double> dic 概率{"a":0.005,"b":0.658}
	 */
	public static String randDict(Map<String, Double> map) {
		double total = 0;
		for (Entry<String, Double> item : map.entrySet()) {
			total += item.getValue();
		}
		double r = Math.random() * total;
		double start = 0;
		double end = 0;
		for (Entry<String, Double> item : map.entrySet()) {
			double v = item.getValue();
			end += v;
			if (r >= start && r <= end) {
				return item.getKey();
			}
			start += v;
		}
		return null;
	}

	/**
	 * 判定随机
	 * 
	 * @param value
	 * @return
	 */
	public static boolean random(double value) {
		double v = Math.random();
		if (v >= value)
			return true;
		return false;
	}

	/**
	 * 主线副本掉落物品
	 * 
	 * @param dropGoods 格式 600001:2:0.5,600002:3:0.3
	 * @return 600001:2,600002:3
	 */
	public static String dropGoods(String dropGoods) {
		Map<String, List<Object>> drop = strToMap(dropGoods);
		StringBuffer sb = new StringBuffer();
		for (Entry<String, List<Object>> entry : drop.entrySet()) {
			List<Object> val = entry.getValue();
			int num = Integer.parseInt(val.get(0).toString());
			double random = Double.parseDouble(val.get(1).toString());
			int getNum = 0;
			for (int i = 0; i < num; i++) {
				if (random(random))
					getNum++;
			}
			if (getNum > 0) {
				sb.append(entry.getKey());
				sb.append(":");
				sb.append(getNum);
				sb.append(",");
			}
		}
		if (sb.length() > 0)
			return sb.substring(0, sb.lastIndexOf(","));
		else
			return "";
	}
	/**
	 * 主线副本类型随机掉落物品
	 * 
	 * @param arts
	 */
	@SuppressWarnings("rawtypes")
	public static String dropGoods(Map<Integer, Map> dropGoods) {
		StringBuffer sb = new StringBuffer();
		if (dropGoods != null) {
			for (Entry<Integer, Map> entry : dropGoods.entrySet()) {
				Map configData = entry.getValue();
				int goodsExtId = Integer.parseInt(configData.get("goodsExtId").toString());
				int sum = Integer.parseInt(configData.get("sum").toString());
				double random = Double.parseDouble(configData.get("random").toString());
				if (random(random)) {
					sb.append(goodsExtId);
					sb.append(":");
					sb.append(sum);
					sb.append(",");
				}
			}
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.lastIndexOf(","));
		} else {
			return "";
		}
	}
	public static String mapToStrForInt(Map<String, List<Integer>> map) {
		StringBuffer strBuff = new StringBuffer("");
		boolean isRun = false;
		for (Entry<String, List<Integer>> entry : map.entrySet()) {
			String str = entry.getKey();
			List<Integer> arr = entry.getValue();
			if (arr == null)
				continue;
			strBuff.append(str);
			strBuff.append(":");
			for (int i = 0; i < arr.size(); i++) {
				strBuff.append(arr.get(i));
				if (i == arr.size() - 1) {
					strBuff.append(",");
				} else {
					strBuff.append(":");
				}
				isRun = true;
			}
		}
		if (isRun) {
			return strBuff.substring(0, strBuff.length() - 1);
		}
		return strBuff.toString();
	}

	public static void main(String[] arts) {
		// Map<String, List<Object>> strToMap = strToMap("bd:1,be:0:15,aa:1:4:4,ab:1:4:4:5,ac:1:4:4:1:4:4:5.5");
		// System.out.println(strToMap);
		// System.out.println(mapToStr(strToMap));
		//
		// HashMap<String, Double> dic = new HashMap<String, Double>();
		// dic.put("a", 0.01);
		// dic.put("b", 0.02);
		// String r = randDict(dic);
		// System.out.println(r);
		// int ii = 0;
		// for (int i = 0; i < 10; i++) {
		// boolean l = random(0.5);
		// if (l) {
		// ii++;
		// }
		// }
		// System.out.println(ii);

		// StringBuffer sb = new StringBuffer();
		// sb.append(1);
		// sb.append(":");
		// sb.append(10);
		// sb.append(",");
		//
		// System.out.println(sb.substring(0, sb.lastIndexOf(",")));
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("aa", 123.36);
		map.put("a2a", 123.36);
		map.put("a3a", 123.36);
		System.out.println(mapToStr2(map));

		Map<String, List<Object>> strToMap = strToMap("1:104,2:1:105,5:1:106,1:2:111,2:2:112,4:2:113,1:3:114,2:3:115,4:3:116,3:4:117,4:4:118,3:5:119,3:6:120");
		System.out.println(strToMap);
		System.out.println(strToMap2("1:1:104,2:1:105,5:1:106,1:2:111,2:2:112,4:2:113,1:3:114,2:3:115,4:3:116,3:4:117,4:4:118,3:5:119,3:6:120"));

	}
}
