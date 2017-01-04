package com.app.db.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * CampaignTaskInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "campaign_task_info", catalog = "game_config")
public class CampaignTaskInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer conditionType;
	private String name;
	private String description;
	private Integer point;
	private Integer repair;
	private Integer param1;
	private Integer param2;
	private Integer param3;
	private Integer param4;
	private String strParam1;

	// Constructors

	/** default constructor */
	public CampaignTaskInfo() {
	}

	/** minimal constructor */
	public CampaignTaskInfo(Integer id) {
		this.id = id;
	}

	/** full constructor */
	public CampaignTaskInfo(Integer id, Integer conditionType, String name, String description, Integer point, Integer repair, Integer param1, Integer param2, Integer param3,
			Integer param4, String strParam1) {
		this.id = id;
		this.conditionType = conditionType;
		this.name = name;
		this.description = description;
		this.point = point;
		this.repair = repair;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
		this.strParam1 = strParam1;
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

	@Column(name = "conditionType")
	public Integer getConditionType() {
		return this.conditionType;
	}

	public void setConditionType(Integer conditionType) {
		this.conditionType = conditionType;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "point")
	public Integer getPoint() {
		return this.point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	@Column(name = "repair")
	public Integer getRepair() {
		return this.repair;
	}

	public void setRepair(Integer repair) {
		this.repair = repair;
	}

	@Column(name = "param1")
	public Integer getParam1() {
		return this.param1;
	}

	public void setParam1(Integer param1) {
		this.param1 = param1;
	}

	@Column(name = "param2")
	public Integer getParam2() {
		return this.param2;
	}

	public void setParam2(Integer param2) {
		this.param2 = param2;
	}

	@Column(name = "param3")
	public Integer getParam3() {
		return this.param3;
	}

	public void setParam3(Integer param3) {
		this.param3 = param3;
	}

	@Column(name = "param4")
	public Integer getParam4() {
		return this.param4;
	}

	public void setParam4(Integer param4) {
		this.param4 = param4;
	}

	@Column(name = "strParam1")
	public String getStrParam1() {
		return this.strParam1;
	}

	public void setStrParam1(String strParam1) {
		this.strParam1 = strParam1;
	}

}