package com.app.empire.scene.service.drop.objects;

public class DropItem {

	/**
	 * 唯一ID
	 */
	private long id;
	
	/**
	 * 掉落物模板ID
	 */
	private int dropItemTempId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDropItemTempId() {
		return dropItemTempId;
	}

	public void setDropItemTempId(int dropItemTempId) {
		this.dropItemTempId = dropItemTempId;
	}
}
