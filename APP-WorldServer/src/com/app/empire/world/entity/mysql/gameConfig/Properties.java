package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Properties entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "properties", catalog = "game_config")
public class Properties implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer type;
	private String name;
	private String flag;
	private Integer dataType;
	private String dataShow;
	private String effect;
	private Boolean isShow;
	private String color1;
	private String color2;

	// Constructors

	/** default constructor */
	public Properties() {
	}

	/** full constructor */
	public Properties(Integer type, String name, String flag, Integer dataType, String dataShow, String effect, Boolean isShow, String color1, String color2) {
		this.type = type;
		this.name = name;
		this.flag = flag;
		this.dataType = dataType;
		this.dataShow = dataShow;
		this.effect = effect;
		this.isShow = isShow;
		this.color1 = color1;
		this.color2 = color2;
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

	@Column(name = "type", nullable = false)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "name", nullable = false, length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "flag", nullable = false, length = 10)
	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Column(name = "data_type", nullable = false)
	public Integer getDataType() {
		return this.dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	@Column(name = "data_show", nullable = false, length = 65535)
	public String getDataShow() {
		return this.dataShow;
	}

	public void setDataShow(String dataShow) {
		this.dataShow = dataShow;
	}

	@Column(name = "effect", nullable = false)
	public String getEffect() {
		return this.effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	@Column(name = "is_show", nullable = false)
	public Boolean getIsShow() {
		return this.isShow;
	}

	public void setIsShow(Boolean isShow) {
		this.isShow = isShow;
	}

	@Column(name = "color1", nullable = false, length = 10)
	public String getColor1() {
		return this.color1;
	}

	public void setColor1(String color1) {
		this.color1 = color1;
	}

	@Column(name = "color2", nullable = false, length = 10)
	public String getColor2() {
		return this.color2;
	}

	public void setColor2(String color2) {
		this.color2 = color2;
	}

}