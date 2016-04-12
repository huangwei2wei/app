package com.app.empire.world.entity.mysql.gameConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MapCopy entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "map_copy", catalog = "game_config")
public class MapCopy implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer sceneType;
	private String resname;
	private String name;
	private Integer type;
	private Integer nextId;
	private Integer preId;
	private Integer userLv;
	private Integer provisionsA;
	private Integer provisionsB;
	private Integer time;
	private Integer groupId;
	private Float heroPositionX;
	private Float heroPositionY;
	private Float heroPositionZ;
	private Integer victoryCondition;
	private Integer gold;
	private Integer heroExperience;
	private String drop;
	private String expGoodsId;
	private String info;
	private Integer resourceType;
	private String resourceName;
	private String statsGrade;

	// Constructors

	/** default constructor */
	public MapCopy() {
	}

	/** minimal constructor */
	public MapCopy(Integer id, Integer preId, Integer victoryCondition) {
		this.id = id;
		this.preId = preId;
		this.victoryCondition = victoryCondition;
	}

	/** full constructor */
	public MapCopy(Integer id, Integer sceneType, String resname, String name, Integer type, Integer nextId, Integer preId, Integer userLv, Integer provisionsA, Integer provisionsB, Integer time,
			Integer groupId, Float heroPositionX, Float heroPositionY, Float heroPositionZ, Integer victoryCondition, Integer gold, Integer heroExperience, String drop, String expGoodsId,
			String info, Integer resourceType, String resourceName, String statsGrade) {
		this.id = id;
		this.sceneType = sceneType;
		this.resname = resname;
		this.name = name;
		this.type = type;
		this.nextId = nextId;
		this.preId = preId;
		this.userLv = userLv;
		this.provisionsA = provisionsA;
		this.provisionsB = provisionsB;
		this.time = time;
		this.groupId = groupId;
		this.heroPositionX = heroPositionX;
		this.heroPositionY = heroPositionY;
		this.heroPositionZ = heroPositionZ;
		this.victoryCondition = victoryCondition;
		this.gold = gold;
		this.heroExperience = heroExperience;
		this.drop = drop;
		this.expGoodsId = expGoodsId;
		this.info = info;
		this.resourceType = resourceType;
		this.resourceName = resourceName;
		this.statsGrade = statsGrade;
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

	@Column(name = "scene_type")
	public Integer getSceneType() {
		return this.sceneType;
	}

	public void setSceneType(Integer sceneType) {
		this.sceneType = sceneType;
	}

	@Column(name = "resname", length = 65535)
	public String getResname() {
		return this.resname;
	}

	public void setResname(String resname) {
		this.resname = resname;
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

	@Column(name = "next_id")
	public Integer getNextId() {
		return this.nextId;
	}

	public void setNextId(Integer nextId) {
		this.nextId = nextId;
	}

	@Column(name = "pre_id", nullable = false)
	public Integer getPreId() {
		return this.preId;
	}

	public void setPreId(Integer preId) {
		this.preId = preId;
	}

	@Column(name = "user_lv")
	public Integer getUserLv() {
		return this.userLv;
	}

	public void setUserLv(Integer userLv) {
		this.userLv = userLv;
	}

	@Column(name = "provisions_a")
	public Integer getProvisionsA() {
		return this.provisionsA;
	}

	public void setProvisionsA(Integer provisionsA) {
		this.provisionsA = provisionsA;
	}

	@Column(name = "provisions_b")
	public Integer getProvisionsB() {
		return this.provisionsB;
	}

	public void setProvisionsB(Integer provisionsB) {
		this.provisionsB = provisionsB;
	}

	@Column(name = "time")
	public Integer getTime() {
		return this.time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	@Column(name = "group_id")
	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	@Column(name = "hero_position_x", precision = 12, scale = 0)
	public Float getHeroPositionX() {
		return this.heroPositionX;
	}

	public void setHeroPositionX(Float heroPositionX) {
		this.heroPositionX = heroPositionX;
	}

	@Column(name = "hero_position_y", precision = 12, scale = 0)
	public Float getHeroPositionY() {
		return this.heroPositionY;
	}

	public void setHeroPositionY(Float heroPositionY) {
		this.heroPositionY = heroPositionY;
	}

	@Column(name = "hero_position_z", precision = 12, scale = 0)
	public Float getHeroPositionZ() {
		return this.heroPositionZ;
	}

	public void setHeroPositionZ(Float heroPositionZ) {
		this.heroPositionZ = heroPositionZ;
	}

	@Column(name = "victory_condition", nullable = false)
	public Integer getVictoryCondition() {
		return this.victoryCondition;
	}

	public void setVictoryCondition(Integer victoryCondition) {
		this.victoryCondition = victoryCondition;
	}

	@Column(name = "gold")
	public Integer getGold() {
		return this.gold;
	}

	public void setGold(Integer gold) {
		this.gold = gold;
	}

	@Column(name = "hero_experience")
	public Integer getHeroExperience() {
		return this.heroExperience;
	}

	public void setHeroExperience(Integer heroExperience) {
		this.heroExperience = heroExperience;
	}

	@Column(name = "drop", length = 65535)
	public String getDrop() {
		return this.drop;
	}

	public void setDrop(String drop) {
		this.drop = drop;
	}

	@Column(name = "exp_goods_id", length = 65535)
	public String getExpGoodsId() {
		return this.expGoodsId;
	}

	public void setExpGoodsId(String expGoodsId) {
		this.expGoodsId = expGoodsId;
	}

	@Column(name = "info", length = 65535)
	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Column(name = "resource_type")
	public Integer getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(Integer resourceType) {
		this.resourceType = resourceType;
	}

	@Column(name = "resource_name", length = 65535)
	public String getResourceName() {
		return this.resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Column(name = "stats_grade", length = 65535)
	public String getStatsGrade() {
		return this.statsGrade;
	}

	public void setStatsGrade(String statsGrade) {
		this.statsGrade = statsGrade;
	}

}