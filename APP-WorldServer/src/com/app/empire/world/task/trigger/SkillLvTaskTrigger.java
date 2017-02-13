package com.app.empire.world.task.trigger;

import java.util.List;
import com.chuangyou.xianni.constant.SkillConstant.SkillMainType;
import com.chuangyou.xianni.entity.hero.HeroSkill;
import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.event.ObjectEvent;
import com.chuangyou.xianni.event.ObjectListener;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskInitBehavior;

/**
 * 技能升级相关任务
 * 
 * @author laofan
 *
 */
public class SkillLvTaskTrigger extends BaseTaskTrigger implements ITaskInitBehavior {

	public SkillLvTaskTrigger(GamePlayer player, ITask task) {
		super(player, task);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addTrigger() {
		// TODO Auto-generated method stub
		this.listener = new ObjectListener() {

			@Override
			public void onEvent(ObjectEvent event) {
				// TODO Auto-generated method stub
				HeroSkill skillInfo = (HeroSkill) event.getObject();
				if (skillInfo != null) {
					if (getTask().getTaskCfg().getTargetId() == 1) {
						if (skillInfo.getType() == SkillMainType.ACTIVE || skillInfo.getType() == SkillMainType.COMMON_ATTACK) {
							if (skillInfo.getSubType() == getTask().getTaskCfg().getTargetId1()) {
								getTask().updateProcess(skillInfo.getSkillLV());
							}
						}
					}
					if (getTask().getTaskCfg().getTargetId() == 2 && skillInfo.getType() == SkillMainType.COMMON_ATTACK) {
						getTask().updateProcess(getCountPu(player.getSkillInventory().getToalSkills(), getTask().getTaskCfg().getTargetId1()));
					}
					if (getTask().getTaskCfg().getTargetId() == 3 && skillInfo.getType() == SkillMainType.ACTIVE) {
						getTask().updateProcess(getCountMain(player.getSkillInventory().getToalSkills(), getTask().getTaskCfg().getTargetId1()));
					}
				}

			}
		};
		player.addListener(listener, EventNameType.SKILL_LEVEL);
	}

	@Override
	public void removeTrigger() {
		// TODO Auto-generated method stub
		player.removeListener(listener, EventNameType.SKILL_LEVEL);
	}

	private int getCountPu(List<HeroSkill> toalSkills, int level) {
		int count = 0;
		for (HeroSkill skill : toalSkills) {
			if (skill != null) {
				if (skill.getType() == SkillMainType.COMMON_ATTACK && skill.getSkillLV() >= level) {
					count++;
				}
			}
		}
		return count;
	}

	private int getCountMain(List<HeroSkill> toalList, int level) {
		int count = 0;
		for (HeroSkill skill : toalList) {
			if (skill != null) {
				if (skill.getType() == SkillMainType.ACTIVE && skill.getSkillLV() >= level) {
					count++;
				}
			}
		}
		return count;
	}

	private int getLv() {
		List<HeroSkill> skills = player.getSkillInventory().getToalSkills();
		for (HeroSkill skill : skills) {
			if (skill != null) {
				if ((skill.getType() == SkillMainType.ACTIVE || skill.getType() == SkillMainType.COMMON_ATTACK) && skill.getSubType() >= getTask().getTaskCfg().getTargetId1()) {
					return skill.getSkillLV();
				}
			}
		}
		return 0;
	}

	@Override
	public void initTask() {
		// TODO Auto-generated method stub
		if (getTask().getTaskCfg().getTargetId() == 1) {
			getTask().getTaskInfo().setProcess(getLv());
		}
		if (getTask().getTaskCfg().getTargetId() == 2) {
			getTask().getTaskInfo().setProcess(getCountPu(player.getSkillInventory().getToalSkills(), getTask().getTaskCfg().getTargetId1()));
		}
		if (getTask().getTaskCfg().getTargetId() == 3) {
			getTask().getTaskInfo().setProcess(getCountMain(player.getSkillInventory().getToalSkills(), getTask().getTaskCfg().getTargetId1()));
		}

	}

}
