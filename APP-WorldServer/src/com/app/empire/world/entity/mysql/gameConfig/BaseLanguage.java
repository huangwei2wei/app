package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BaseLanguage entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "base_language", catalog = "game_config")
public class BaseLanguage implements java.io.Serializable {

	// Fields

	private Integer id;
	private String msg;

	// Constructors

	/** default constructor */
	public BaseLanguage() {
	}

	/** full constructor */
	public BaseLanguage(String msg) {
		this.msg = msg;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "msg", nullable = false, length = 1024)
	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}