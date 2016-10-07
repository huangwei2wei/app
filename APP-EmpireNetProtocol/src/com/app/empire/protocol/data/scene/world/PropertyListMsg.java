package com.app.empire.protocol.data.scene.world;

import java.util.List;

/**
 * 属性列表
 * 
 * @author doter
 * 
 */
public class PropertyListMsg {
	private List<PropertyMsg> propertys;

	public List<PropertyMsg> getPropertys() {
		return propertys;
	}

	public void setPropertys(List<PropertyMsg> propertys) {
		this.propertys = propertys;
	}

}
