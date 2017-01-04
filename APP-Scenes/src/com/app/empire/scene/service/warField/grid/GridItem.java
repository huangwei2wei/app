package com.app.empire.scene.service.warField.grid;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.app.empire.scene.service.role.objects.Living;

public class GridItem {
	// public int X;
	// public int Z;
	private Logger log = Logger.getLogger(Grid.class);
	public int id;

	private ConcurrentHashMap<Integer, Set<Integer>> livings;// type -> set

	public GridItem() {
		livings = new ConcurrentHashMap<Integer, Set<Integer>>();
	}

	public ConcurrentHashMap<Integer, Set<Integer>> getLivings() {
		return livings;
	}

	/**
	 * 添加
	 * 
	 * @param role
	 */
	public void addLiving(Living living) {
		synchronized (livings) {
			if (!livings.containsKey(living.getType())) {
				livings.put(living.getType(), new HashSet<Integer>());
			}
			Set<Integer> contianer = livings.get(living.getType());
			// if(living.getId() < 200)
			// System.err.println("addLiving - " + living.getId() + " id = " +
			// id+ " contianer.size() = " + contianer.size());
			living.setGridIndex(id - 1);
			contianer.add(living.getId());
		}
	}

	/**
	 * 移除
	 * 
	 * @param role
	 */
	public void removeLiving(Living living) {
		synchronized (livings) {
			if (!livings.containsKey(living.getType())) {
				log.error("[Error]remove" + living.getId() + " id = " + id + "  type:" + living.getType(), new Exception());
				return;
			}
			Set<Integer> contianer = livings.get(living.getType());
			// if(living.getId() < 200)

			boolean res = contianer.remove(living.getId());
			// System.err.println(living.getField().getFieldInfo().getName() + " res:"+res+ " removeLiving - " + living.getId() + " id = " + id + " contianer.size() = " +
			// contianer.size());
		}
	}

	// /**
	// * 判断是否是同一个Grid
	// * @param item
	// * @return
	// */
	// public boolean Equels(GridItem item)
	// {
	// if(X == item.X && Z == item.Z) return true;
	// return false;
	// }
}
