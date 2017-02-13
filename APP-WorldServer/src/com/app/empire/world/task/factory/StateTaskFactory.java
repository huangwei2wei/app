package com.app.empire.world.task.factory;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.entity.task.ITaskCfg;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.behavior.init.ZeroInitBehavior;
import com.chuangyou.xianni.retask.constant.ConditionType;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskInitBehavior;
import com.chuangyou.xianni.retask.iinterface.ITriggerObserver;
import com.chuangyou.xianni.retask.trigger.ActiveTaskTrigger;
import com.chuangyou.xianni.retask.trigger.ArtifactTaskTrigger;
import com.chuangyou.xianni.retask.trigger.AvatarTaskTrigger;
import com.chuangyou.xianni.retask.trigger.EquipTaskTrigger;
import com.chuangyou.xianni.retask.trigger.InverseBeadTaskTrigger;
import com.chuangyou.xianni.retask.trigger.KillMonsterTaskTrigger;
import com.chuangyou.xianni.retask.trigger.MountTaskTrigger;
import com.chuangyou.xianni.retask.trigger.PassFbTaskTrigger;
import com.chuangyou.xianni.retask.trigger.PlayerFightTaskTrigger;
import com.chuangyou.xianni.retask.trigger.PlayerLvTaskTrigger;
import com.chuangyou.xianni.retask.trigger.QteTaskTrigger;
import com.chuangyou.xianni.retask.trigger.SkillLvTaskTrigger;
import com.chuangyou.xianni.retask.trigger.StateLvTaskTrigger;
import com.chuangyou.xianni.retask.trigger.StateTaskTrigger;
import com.chuangyou.xianni.retask.trigger.TriggerTaskTrigger;
import com.chuangyou.xianni.retask.trigger.magicwp.MagicWpTaskTrigger;
import com.chuangyou.xianni.retask.trigger.pet.PetTaskTrigger;
import com.chuangyou.xianni.retask.trigger.soul.SoulTaskTrigger;

/**
 * 境界任务观察者&初始化形为生成器
 * 双锁---单例
 * @author laofan
 *
 */
public class StateTaskFactory extends AbstractTaskTriggerFactory {

	private static volatile StateTaskFactory instance;
	
	
	public static StateTaskFactory getInstance(){
		if(instance==null){
			synchronized (StateTaskFactory.class) {
				if(instance == null){
					instance = new StateTaskFactory();
				}
			}
		}
		return instance;
	}
	
	
	@Override
	public ITriggerObserver createObserver(ITaskCfg cfg,GamePlayer player,ITask task) {
		// TODO Auto-generated method stub
		ITriggerObserver trigger = null;
		switch(cfg.getTaskTarget()){
			case ConditionType.KILL_MONST:
				trigger = new KillMonsterTaskTrigger(player, task);
				break;
			case ConditionType.PASS_FB:
				trigger = new PassFbTaskTrigger(player, task);
				break;
			case ConditionType.TRIGGER:
				trigger = new TriggerTaskTrigger(player, task);
				break;
			case ConditionType.T_SYSTEM:
				trigger = new InverseBeadTaskTrigger(player, task);
				break;
			case ConditionType.QTE:
				trigger = new QteTaskTrigger(player, task);
				break;
			case ConditionType.PLAYER_LV:
				trigger = new PlayerLvTaskTrigger(player, task);
				break;
			case ConditionType.PLAYER_FIGHT:
				trigger = new PlayerFightTaskTrigger(player, task);
				break;
//			case ConditionType.SKILL_STAGE:
//				trigger = new (player, task);
//				break;	
			case ConditionType.EQUIP:
				trigger = new EquipTaskTrigger(player, task);
				break;
			case ConditionType.SOUL:
				trigger = new SoulTaskTrigger(player, task);
				break;
			case ConditionType.MOUNT:
				trigger = new MountTaskTrigger(player, task);
				break;
			case ConditionType.ARTIFACTDATA:
				trigger = new ArtifactTaskTrigger(player, task);
				break;
			case ConditionType.MAGICWP:
				trigger = new MagicWpTaskTrigger(player, task);
				break;
			case ConditionType.PET:
				trigger = new PetTaskTrigger(player, task);
				break;
			case ConditionType.Dart:
				break;
			case ConditionType.STATE_LV:
				trigger = new StateTaskTrigger(player, task);
				break;
			case ConditionType.SKILL:
				trigger = new SkillLvTaskTrigger(player, task);
				break;
			case ConditionType.AVTAR:
				trigger = new AvatarTaskTrigger(player, task);
				break;
			case ConditionType.ACTIVE:
				trigger = new ActiveTaskTrigger(player, task);
				break;
			case ConditionType.STATE_TASK:
				trigger = new StateLvTaskTrigger(player, task);
				break;	
		}
		if(trigger == null){
			Log.error("StateTaskFactory工厂里没有此类型监听器："+cfg.getTaskTarget());
		}
		return trigger;		
	}

	@Override
	public ITaskInitBehavior createInitBehavior(ITaskCfg cfg,GamePlayer player,ITask task) {
		// TODO Auto-generated method stub
		ITaskInitBehavior trigger = null;
		switch(cfg.getTaskTarget()){
			case ConditionType.KILL_MONST:
				trigger = new ZeroInitBehavior(player, task);
				break;
			case ConditionType.PASS_FB:
				trigger = new ZeroInitBehavior(player, task);
				break;
			case ConditionType.TRIGGER:
				trigger = new ZeroInitBehavior(player, task);
				break;
			case ConditionType.T_SYSTEM:
				trigger = new InverseBeadTaskTrigger(player, task);
				break;
			case ConditionType.QTE:
				trigger = new ZeroInitBehavior(player, task);
				break;
			case ConditionType.PLAYER_LV:
				trigger = new PlayerLvTaskTrigger(player, task);
				break;
			case ConditionType.PLAYER_FIGHT:
				trigger = new PlayerFightTaskTrigger(player, task);
				break;
//			case ConditionType.SKILL_STAGE:
//				trigger = new (player, task);
//				break;	
			case ConditionType.EQUIP:
				trigger = new EquipTaskTrigger(player, task);
				break;
			case ConditionType.SOUL:
				trigger = new SoulTaskTrigger(player, task);
				break;
			case ConditionType.MOUNT:
				trigger = new MountTaskTrigger(player, task);
				break;
			case ConditionType.ARTIFACTDATA:
				trigger = new ArtifactTaskTrigger(player, task);
				break;
			case ConditionType.MAGICWP:
				trigger = new MagicWpTaskTrigger(player, task);
				break;
			case ConditionType.PET:
				trigger = new PetTaskTrigger(player, task);
				break;
			case ConditionType.Dart:
				break;
			case ConditionType.STATE_LV:
				trigger = new StateTaskTrigger(player, task);
				break;
			case ConditionType.SKILL:
				trigger = new SkillLvTaskTrigger(player, task);
				break;
			case ConditionType.AVTAR:
				trigger = new AvatarTaskTrigger(player, task);
				break;
			case ConditionType.ACTIVE:
				trigger = new ZeroInitBehavior(player, task);
				break;
			case ConditionType.STATE_TASK:
				trigger = new StateLvTaskTrigger(player, task);
				break;
		}
		if(trigger == null){
			Log.error("StateTaskFactory工厂里没有此类型初始化形为处理器："+cfg.getTaskTarget());
		}
		return trigger;
	}

}
