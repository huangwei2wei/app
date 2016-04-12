package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Task entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "task", catalog = "game_config")
public class Task implements java.io.Serializable {

	// Fields

	private Integer id;
	private String name;
	private Short type;
	private Short subType;
	private Integer preTaskId;
	private Integer nextTaskId;
	private Integer activationLv;
	private String completeCondition;
	private String completeAward;
	private String taskDes;
	private String coordinate;
	private Integer mapCopyId;

	// Constructors

	/** default constructor */
	public Task() {
	}

	/** full constructor */
	public Task(String name, Short type, Short subType, Integer preTaskId, Integer nextTaskId, Integer activationLv, String completeCondition, String completeAward, String taskDes, String coordinate,
			Integer mapCopyId) {
		this.name = name;
		this.type = type;
		this.subType = subType;
		this.preTaskId = preTaskId;
		this.nextTaskId = nextTaskId;
		this.activationLv = activationLv;
		this.completeCondition = completeCondition;
		this.completeAward = completeAward;
		this.taskDes = taskDes;
		this.coordinate = coordinate;
		this.mapCopyId = mapCopyId;
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

	@Column(name = "name", nullable = false, length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "type", nullable = false)
	public Short getType() {
		return this.type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	@Column(name = "subType", nullable = false)
	public Short getSubType() {
		return this.subType;
	}

	public void setSubType(Short subType) {
		this.subType = subType;
	}

	@Column(name = "pre_task_id", nullable = false)
	public Integer getPreTaskId() {
		return this.preTaskId;
	}

	public void setPreTaskId(Integer preTaskId) {
		this.preTaskId = preTaskId;
	}

	@Column(name = "next_task_id", nullable = false)
	public Integer getNextTaskId() {
		return this.nextTaskId;
	}

	public void setNextTaskId(Integer nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	@Column(name = "activation_lv", nullable = false)
	public Integer getActivationLv() {
		return this.activationLv;
	}

	public void setActivationLv(Integer activationLv) {
		this.activationLv = activationLv;
	}

	@Column(name = "complete_condition", nullable = false, length = 128)
	public String getCompleteCondition() {
		return this.completeCondition;
	}

	public void setCompleteCondition(String completeCondition) {
		this.completeCondition = completeCondition;
	}

	@Column(name = "complete_award", nullable = false, length = 256)
	public String getCompleteAward() {
		return this.completeAward;
	}

	public void setCompleteAward(String completeAward) {
		this.completeAward = completeAward;
	}

	@Column(name = "task_des", nullable = false, length = 512)
	public String getTaskDes() {
		return this.taskDes;
	}

	public void setTaskDes(String taskDes) {
		this.taskDes = taskDes;
	}

	@Column(name = "coordinate", nullable = false, length = 65535)
	public String getCoordinate() {
		return this.coordinate;
	}

	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}

	@Column(name = "map_copy_id", nullable = false)
	public Integer getMapCopyId() {
		return this.mapCopyId;
	}

	public void setMapCopyId(Integer mapCopyId) {
		this.mapCopyId = mapCopyId;
	}

}