package com.app.db.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public abstract class IEntity {
	@Id
	public int id;
	// 映射忽略的字段，该字段不会保存到mongodb。
	@Transient
	protected int op;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOp() {
		return op;
	}

	public void setOp(int op) {
		this.op = op;
	}

}
