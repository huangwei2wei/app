package com.app.empire.scene.util;

import java.util.Arrays;

public class ArrayMergeUtil {

	public static int[] IntArrayMerge(int[] array1, int[] array2) {
		int[] newArray = new int[array1.length + array2.length];
		System.arraycopy(array1, 0, newArray, 0, array1.length);
		System.arraycopy(array2, 0, newArray, array1.length, array2.length);
		return newArray;
	}

	/**
	 * <pre>
	 * 判断该数组是否存在相同的值，排除小于等于指定的值
	 * </pre>
	 * 
	 * @param data
	 * @param except
	 * @return
	 */
	public static boolean isSame(int[] data, int except) {
		if (data == null || data.length <= 0) {
			return false;
		}

		int[] temp = Arrays.copyOfRange(data, 0, data.length);
		Arrays.sort(temp);
		for (int i = 0; i < temp.length - 1; i++) {
			if (temp[i] <= except) {
				continue;
			}
			if (temp[i] == temp[i + 1]) {
				return true;
			}
		}
		return false;
	}
}
