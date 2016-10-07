package com.app.empire.scene.util;

import java.util.Random;

/**
 * <pre>
 * 随机辅助类
 * </pre>
 */
public class ThreadSafeRandom {

	private Random random = new Random();

	public int next() {
		synchronized (this) {
			return random.nextInt();
		}
	}

	public int next(int maxValue) {
		synchronized (this) {
			return random.nextInt(maxValue);
		}
	}

	public static void main(String[] args) {
		ThreadSafeRandom ts = new ThreadSafeRandom();
		for (int i = 0; i < 100; i++) {
			System.out.print(ts.next() + ",");
		}
	}

	/**
	 * 随机区间值，如 min=1 maxValue=5 随机，其结果值不包括5
	 * 
	 * @param minValue
	 *            开始值
	 * @param maxValue
	 *            结束值
	 * @return
	 */
	public int next(int minValue, int maxValue) {
		synchronized (this) {
			if (minValue < maxValue) {
				maxValue = maxValue + 1;
				return random.nextInt(maxValue - minValue) + minValue;
			}
		}
		return minValue;
	}

	/**
	 * 根据概率和随机值，返回是否成功
	 * 
	 * @param probability
	 *            概率权值
	 * @param maxProbability
	 *            全概率值
	 * @return
	 */
	public boolean isSuccessful(int probability, int maxProbability) {
		if (probability <= 0 || maxProbability <= 0) {
			return false;
		}
		if (probability >= maxProbability) {// 权重
			return true;
		}
		boolean success = false;
		int rand = next(1, maxProbability);
		if (rand <= probability) {
			success = true;
		}
		return success;
	}

	/**
	 * <pre>
	 * 计算哪种几率触发
	 * </pre>
	 * 
	 * @param random1
	 *            几率1
	 * @param random2
	 *            几率2
	 * @return
	 */
	public int calcRandom(int random1, int random2) {
		int randomValue = next(Math.max(random1, random2));
		if (randomValue < random1 && randomValue < random2) {
			return next(1, 2);
		} else if (randomValue < random1) {
			return 1;
		} else if (randomValue < random2) {
			return 2;
		}
		return 1;
	}
}
