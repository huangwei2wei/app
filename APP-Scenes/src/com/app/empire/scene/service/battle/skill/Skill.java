package com.app.empire.scene.service.battle.skill;

import com.app.db.mysql.entity.SkillActioninfo;
import com.app.db.mysql.entity.SkillInfo;

/**
 * <pre>
 * 战斗中使用的技能
 * </pre>
 */
public class Skill {
	private SkillInfo skillinfo;
	private SkillActioninfo templateInfo;
	private int skillId; // 技能ID
	private long lastUsed; // 最后一次使用时间
	private int level;

	public Skill(SkillActioninfo templateInfo) {
		this(templateInfo, 1);
	}

	public Skill(SkillActioninfo templateInfo, int level) {
		this.templateInfo = templateInfo;
		this.skillId = templateInfo.getTemplateId();
		this.level = level;
	}

	public int getActionId() {
		return templateInfo.getTemplateId();
	}

	public SkillActioninfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(SkillActioninfo templateInfo) {
		this.templateInfo = templateInfo;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public long getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(long lastUsed) {
		this.lastUsed = lastUsed;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public SkillInfo getSkillinfo() {
		return skillinfo;
	}

	public void setSkillinfo(SkillInfo skillinfo) {
		this.skillinfo = skillinfo;
	}

	public boolean canUse() {
		if (System.currentTimeMillis() - this.lastUsed >= (templateInfo.getCooldown() * 1000 - 1000)) {
			this.lastUsed = System.currentTimeMillis();
			return true;
		}
		return false;
	}

}
