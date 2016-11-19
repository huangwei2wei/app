package com.app.empire.scene.service.battle.skill;

import com.chuangyou.xianni.entity.skill.SkillActionTemplateInfo;
import com.chuangyou.xianni.entity.skill.SkillTempateInfo;

/**
 * <pre>
 * 战斗中使用的技能
 * </pre>
 */
public class Skill {
	private SkillTempateInfo		skillTempateInfo;
	private SkillActionTemplateInfo	templateInfo;
	private int						skillId;			// 技能ID
	private long					lastUsed;			// 最后一次使用时间
	private int						level;

	public Skill(SkillActionTemplateInfo templateInfo) {
		this(templateInfo, 1);
	}

	public Skill(SkillActionTemplateInfo templateInfo, int level) {
		this.templateInfo = templateInfo;
		this.skillId = templateInfo.getTemplateId();
		this.level = level;
	}

	public int getActionId() {
		return templateInfo.getTemplateId();
	}

	public SkillActionTemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(SkillActionTemplateInfo templateInfo) {
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

	public SkillTempateInfo getSkillTempateInfo() {
		return skillTempateInfo;
	}

	public void setSkillTempateInfo(SkillTempateInfo skillTempateInfo) {
		this.skillTempateInfo = skillTempateInfo;
	}

	public boolean canUse() {
		if (System.currentTimeMillis() - this.lastUsed >= (templateInfo.getCooldown() * 1000 - 1000)) {
			this.lastUsed = System.currentTimeMillis();
			return true;
		}
		return false;
	}

}
