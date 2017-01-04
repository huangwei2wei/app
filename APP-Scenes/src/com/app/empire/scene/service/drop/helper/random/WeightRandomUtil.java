package com.app.empire.scene.service.drop.helper.random;

import java.util.List;
import java.util.Random;

import com.app.db.mysql.entity.DropItemInfo;

/**
 * 带权重的随机
 * 
 * @author laofan
 * 
 */
public class WeightRandomUtil {

	public interface Weightable<T> {
		public int getWeight(T t);
	}

	/**
	 * 获取带权重的随机出来的元素
	 * 
	 * @param list
	 * @return
	 */
	public static <T extends DropItemInfo> T getRandomWeight(List<T> list) {
		int total = 0;
		for (DropItemInfo info : list) {
			total += info.getWeight();
		}
		if (total > 0) {
			int r = new Random().nextInt(total);
			int flag = 0;
			for (T info : list) {
				if (r >= flag && r < flag + info.getWeight()) {
					return info;
				}
				flag += info.getWeight();
			}
		}
		return null;
	}

	/**
	 * 获取带权重的随机出来的元素,总权重值固定
	 * 
	 * @param total
	 * @param list
	 * @return
	 */
	public static <T extends DropItemInfo> T getRandomWeight(int total, List<T> list) {
		if (total > 0) {
			int r = new Random().nextInt(total);
			int flag = 0;
			for (T info : list) {
				if (r >= flag && r < flag + info.getWeight()) {
					return info;
				}
				flag += info.getWeight();
			}
		}
		return null;
	}

	/**
	 * 获取带权重的随机出来的元素
	 * 
	 * @param list
	 * @return
	 */
	public static <T> T getRandomWeight(List<T> list, Weightable<? super T> c) {
		int total = 0;
		for (T t : list) {
			total += c.getWeight(t);
		}
		if (total > 0) {
			int r = new Random().nextInt(total);
			int flag = 0;
			for (T t : list) {
				if (r >= flag && r < flag + c.getWeight(t)) {
					return t;
				}
				flag += c.getWeight(t);
			}
		}
		return null;
	}
}
