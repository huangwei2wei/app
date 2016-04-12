package com.app.db.mongo.entity;

import java.io.Serializable;

public abstract class IEntity implements Serializable {

	public int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
