package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BaseRandomName entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "base_random_name", catalog = "game_config")
public class BaseRandomName implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Short type;

	// Constructors

	/** default constructor */
	public BaseRandomName() {
	}

	/** full constructor */
	public BaseRandomName(String name, Short type) {
		this.name = name;
		this.type = type;
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

	@Column(name = "name", length = 8)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type")
	public Short getType() {
		return this.type;
	}

	public void setType(Short type) {
		this.type = type;
	}

}