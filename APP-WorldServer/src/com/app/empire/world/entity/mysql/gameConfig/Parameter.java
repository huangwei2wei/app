package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Parameter entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "parameter", catalog = "game_config")
public class Parameter implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Integer json;
	private String parameter;
	private String info;

	// Constructors

	/** default constructor */
	public Parameter() {
	}

	/** full constructor */
	public Parameter(String name, Integer json, String parameter, String info) {
		this.name = name;
		this.json = json;
		this.parameter = parameter;
		this.info = info;
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

	@Column(name = "name", nullable = false, length = 30)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "json", nullable = false)
	public Integer getJson() {
		return this.json;
	}

	public void setJson(Integer json) {
		this.json = json;
	}

	@Column(name = "parameter", nullable = false, length = 65535)
	public String getParameter() {
		return this.parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Column(name = "info", nullable = false, length = 1024)
	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}