package com.app.empire.scene.util;

import java.util.List;
import java.util.Map;

public class AttributeUtil {

	/**
	 * 将属性码列表放入Map
	 * @param attMap
	 * @param attList
	 */
	public static void putAttListToMap(Map<Integer, Integer> attMap, List<Integer> attList) {
		for (Integer attNum : attList) {
			AttributeUtil.putAttNumToMap(attMap, attNum);
		}
	}
	/**
	 * 将属性Map放入主Map
	 * @param attMap
	 * @param sourceMap
	 */
	public static void putAttToMap(Map<Integer, Integer> attMap, Map<Integer, Integer> sourceMap) {
		for (int attType : sourceMap.keySet()) {
			if (attMap.get(attType) == null) {
				attMap.put(attType, 0);
			}
			attMap.put(attType, attMap.get(attType) + sourceMap.get(attType));
		}
	}
	
	/**
	 * 合并MAP中某个属性值
	 * @param attMap
	 * @param sourceMap
	 * @param attType
	 */
	public static void putAttToMap(Map<Integer, Integer> attMap, Map<Integer, Integer> sourceMap,int attType){
		if(attMap.get(attType)==null){
			attMap.put(attType, 0);
		}
		if(sourceMap.get(attType)==null){
			sourceMap.put(attType, 0);
		}
		attMap.put(attType, attMap.get(attType)+sourceMap.get(attType));
	}
	

	/**
	 * 将属性码解析成属性后放入Map
	 * @param attMap
	 * @param attNum
	 */
	public static void putAttNumToMap(Map<Integer, Integer> attMap, int attNum) {
		if (attNum > 0) {
			int attType = (int) (attNum / 1000000);
			int attValue = attNum % 1000000;
			if (attMap.get(attType) == null) {
				attMap.put(attType, 0);
			}
			attMap.put(attType, attMap.get(attType) + attValue);
		}
	}
	
	/**
	 * 将属性放入Map
	 * @param attMap
	 * @param attType
	 * @param attValue
	 */
	public static void putAttNumToMap(Map<Integer, Integer> attMap, int attType,int attValue) {
		if (attType > 0) {
			if (attMap.get(attType) == null) {
				attMap.put(attType, 0);
			}
			attMap.put(attType, attMap.get(attType) + attValue);
		}
	}
	
}
