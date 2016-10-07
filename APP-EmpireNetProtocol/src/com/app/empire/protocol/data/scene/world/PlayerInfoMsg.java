package com.app.empire.protocol.data.scene.world;

public class PlayerInfoMsg {
	private int playerId; // 角色ID
	private int userId; // 用户ID
	private String nickName; // 昵称
	private int level; // 等级
	private int exp; // 当前经验
	private int toalExp; // 总经验g
	private int money; // 金币
	private int bindCash; // 绑定仙玉
	private int vipLevel; // VIP等级
	private int fight; // 战斗力
	private int skinId; // 皮肤
	private int fashionId; // 时装ID
	private int weaponId; // 武器ID
	private int mountId; // 坐骑ID
	private int magicWeaponId; // 法宝ID
	private int wingId; // 翅膀ID
	private int pBagCount; // 用户背包格子数
	private PostionMsg postionMsg; // 当前位置
	private int cash; // 非绑定仙玉
	private int repair; // 修为
	private int points; // 积分
	private int job; // 职业
	private int equipExp; // 装备经验
	private int weaponAwaken; // 当前武器觉醒等级

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getToalExp() {
		return toalExp;
	}

	public void setToalExp(int toalExp) {
		this.toalExp = toalExp;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getBindCash() {
		return bindCash;
	}

	public void setBindCash(int bindCash) {
		this.bindCash = bindCash;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public int getFight() {
		return fight;
	}

	public void setFight(int fight) {
		this.fight = fight;
	}

	public int getSkinId() {
		return skinId;
	}

	public void setSkinId(int skinId) {
		this.skinId = skinId;
	}

	public int getFashionId() {
		return fashionId;
	}

	public void setFashionId(int fashionId) {
		this.fashionId = fashionId;
	}

	public int getWeaponId() {
		return weaponId;
	}

	public void setWeaponId(int weaponId) {
		this.weaponId = weaponId;
	}

	public int getMountId() {
		return mountId;
	}

	public void setMountId(int mountId) {
		this.mountId = mountId;
	}

	public int getMagicWeaponId() {
		return magicWeaponId;
	}

	public void setMagicWeaponId(int magicWeaponId) {
		this.magicWeaponId = magicWeaponId;
	}

	public int getWingId() {
		return wingId;
	}

	public void setWingId(int wingId) {
		this.wingId = wingId;
	}

	public int getpBagCount() {
		return pBagCount;
	}

	public void setpBagCount(int pBagCount) {
		this.pBagCount = pBagCount;
	}

	public PostionMsg getPostionMsg() {
		return postionMsg;
	}

	public void setPostionMsg(PostionMsg postionMsg) {
		this.postionMsg = postionMsg;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public int getRepair() {
		return repair;
	}

	public void setRepair(int repair) {
		this.repair = repair;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public int getEquipExp() {
		return equipExp;
	}

	public void setEquipExp(int equipExp) {
		this.equipExp = equipExp;
	}

	public int getWeaponAwaken() {
		return weaponAwaken;
	}

	public void setWeaponAwaken(int weaponAwaken) {
		this.weaponAwaken = weaponAwaken;
	}

}
