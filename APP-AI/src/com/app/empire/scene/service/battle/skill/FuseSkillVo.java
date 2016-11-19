package com.app.empire.scene.service.battle.skill;

/**
 * 融合技能结构
 * 
 * @author laofan
 *
 */
public class FuseSkillVo {

	/**
	 * 融合技能ID（查融合技能表）
	 */
	private int	skillId;
	/**
	 * 技能颜色品质（2：绿 3：蓝 4：紫 5：橙）
	 * 
	 */
	private int	skillColor;

	/** BUFFERID */
	private int	bufferId;

	public FuseSkillVo(int skillId, int skillColor) {
		super();
		this.skillId = skillId;
		this.skillColor = skillColor;
	}

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public int getSkillColor() {
		return skillColor;
	}

	public void setSkillColor(int skillColor) {
		this.skillColor = skillColor;
	}

	public int getBufferId() {
		return bufferId;
	}

	public void setBufferId(int bufferId) {
		this.bufferId = bufferId;
	}

}
