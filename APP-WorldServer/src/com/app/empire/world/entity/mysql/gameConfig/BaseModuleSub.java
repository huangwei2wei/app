package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * BaseModuleSub entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "base_module_sub", catalog = "game_config")
public class BaseModuleSub implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer moduleId;
	private String subModuleName;
	private Integer isMain;
	private Integer location;
	private String openCondition;
	private Integer noviceId;
	private String useCondition;
	private String buyCondition;

	// Constructors

	/** default constructor */
	public BaseModuleSub() {
	}

	/** full constructor */
	public BaseModuleSub(Integer moduleId, String subModuleName, Integer isMain, Integer location, String openCondition, Integer noviceId, String useCondition, String buyCondition) {
		this.moduleId = moduleId;
		this.subModuleName = subModuleName;
		this.isMain = isMain;
		this.location = location;
		this.openCondition = openCondition;
		this.noviceId = noviceId;
		this.useCondition = useCondition;
		this.buyCondition = buyCondition;
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

	@Column(name = "module_id", nullable = false)
	public Integer getModuleId() {
		return this.moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	@Column(name = "sub_module_name", nullable = false, length = 10)
	public String getSubModuleName() {
		return this.subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	@Column(name = "isMain", nullable = false)
	public Integer getIsMain() {
		return this.isMain;
	}

	public void setIsMain(Integer isMain) {
		this.isMain = isMain;
	}

	@Column(name = "location", nullable = false)
	public Integer getLocation() {
		return this.location;
	}

	public void setLocation(Integer location) {
		this.location = location;
	}

	@Column(name = "open_condition", nullable = false, length = 256)
	public String getOpenCondition() {
		return this.openCondition;
	}

	public void setOpenCondition(String openCondition) {
		this.openCondition = openCondition;
	}

	@Column(name = "novice_id", nullable = false)
	public Integer getNoviceId() {
		return this.noviceId;
	}

	public void setNoviceId(Integer noviceId) {
		this.noviceId = noviceId;
	}

	@Column(name = "use_condition", nullable = false, length = 1024)
	public String getUseCondition() {
		return this.useCondition;
	}

	public void setUseCondition(String useCondition) {
		this.useCondition = useCondition;
	}

	@Column(name = "buy_condition", nullable = false, length = 256)
	public String getBuyCondition() {
		return this.buyCondition;
	}

	public void setBuyCondition(String buyCondition) {
		this.buyCondition = buyCondition;
	}

}