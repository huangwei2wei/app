package com.app.empire.scene.service.role.helper;

import com.app.empire.scene.util.pool.MemoryObject;

public class Hatred implements MemoryObject, Comparable<Hatred> {

	// 仇恨度
	private int hatred;
	// 仇恨对象
	private int target;
	// 第一次攻击时间
	private long firstAttack;
	// 最后一次攻击时间
	private long lastAttack;

	public int getHatred() {
		return hatred;
	}

	public void setHatred(int hatred) {
		this.hatred = hatred;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public long getFirstAttack() {
		return firstAttack;
	}

	public void setFirstAttack(long firstAttack) {
		this.firstAttack = firstAttack;
	}

	public long getLastAttack() {
		return lastAttack;
	}

	public void setLastAttack(long lastAttack) {
		this.lastAttack = lastAttack;
	}

	@Override
	public int compareTo(Hatred o) {
		return o.getHatred() - getHatred();
	}

	@Override
	public void release() {
		this.firstAttack = 0;
		this.hatred = 0;
		this.lastAttack = 0;
		this.target = 0;
	}

}
