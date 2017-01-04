package com.app.empire.world.service;

public interface IInventory {

	/**
	 * 加载数据从DB
	 * 
	 * @return
	 */
	public boolean loadFromDataBase();

	/**
	 * 卸载数据
	 * 
	 * @return
	 */
	public boolean unloadData();

	/**
	 * 数据同步到DB
	 * 
	 * @return
	 */
	public boolean saveToDatabase();

}
