package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Field entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "field", catalog = "game_config")
public class Field implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Integer type;
	private Integer activationLv;
	private Float random;
	private String completeCondition;
	private String completeAward;
	private String taskDes;
	private String coordinate;
	private Integer mapCopyId;

	// Constructors

	/** default constructor */
	public Field() {
	}

	/** minimal constructor */
	public Field(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public Field(Integer id, String name, Integer type, Integer activationLv, Float random, String completeCondition, String completeAward, String taskDes, String coordinate, Integer mapCopyId) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.activationLv = activationLv;
		this.random = random;
		this.completeCondition = completeCondition;
		this.completeAward = completeAward;
		this.taskDes = taskDes;
		this.coordinate = coordinate;
		this.mapCopyId = mapCopyId;
	}

	// Property accessors
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", length = 65535)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type")
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "activation_lv")
	public Integer getActivationLv() {
		return this.activationLv;
	}

	public void setActivationLv(Integer activationLv) {
		this.activationLv = activationLv;
	}

	@Column(name = "random", precision = 12, scale = 0)
	public Float getRandom() {
		return this.random;
	}

	public void setRandom(Float random) {
		this.random = random;
	}

	@Column(name = "complete_condition", length = 65535)
	public String getCompleteCondition() {
		return this.completeCondition;
	}

	public void setCompleteCondition(String completeCondition) {
		this.completeCondition = completeCondition;
	}

	@Column(name = "complete_award", length = 65535)
	public String getCompleteAward() {
		return this.completeAward;
	}

	public void setCompleteAward(String completeAward) {
		this.completeAward = completeAward;
	}

	@Column(name = "task_des", length = 65535)
	public String getTaskDes() {
		return this.taskDes;
	}

	public void setTaskDes(String taskDes) {
		this.taskDes = taskDes;
	}

	@Column(name = "coordinate", length = 65535)
	public String getCoordinate() {
		return this.coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	@Column(name = "map_copy_id")
	public Integer getMapCopyId() {
		return this.mapCopyId;
	}

	public void setMapCopyId(Integer mapCopyId) {
		this.mapCopyId = mapCopyId;
	}

}