package com.app.empire.scene.service.warfield.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.app.empire.scene.service.role.helper.RoleConstants;
import com.app.empire.scene.service.role.objects.Living;

public class GridItem {
	// public int X;
	// public int Z;

	public int id;

	private ConcurrentHashMap<Integer, List<Long>> livings;

	public GridItem() {
		livings = new ConcurrentHashMap<Integer, List<Long>>();
	}

	public ConcurrentHashMap<Integer, List<Long>> getLivings() {
		return livings;
	}

	/**
	 * 添加
	 * 
	 * @param role
	 */
	public void addLiving(Living living) {
		synchronized (livings) {
			if (!livings.containsKey(living.getType()))
				livings.put(living.getType(), new ArrayList<Long>());
			List<Long> contianer = livings.get(living.getType());
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
				System.err.println("[Error]remove" + living.getId() + " id = " + id);
				return;
			}
			List<Long> contianer = livings.get(living.getType());
			// if(living.getId() < 200)
			// System.err.println("removeLiving - " + living.getId() + " id = "
			// + id + " contianer.size() = " + contianer.size());
			contianer.remove(living.getId());
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
