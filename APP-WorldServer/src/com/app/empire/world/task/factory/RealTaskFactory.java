package com.app.empire.world.task.factory;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.entity.task.ITaskCfg;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.retask.behavior.init.KillPrivateMonsterInitBehavior;
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
import com.chuangyou.xianni.retask.trigger.ItemTaskTrigger;
import com.chuangyou.xianni.retask.trigger.KillMonsterTaskTrigger;
import com.chuangyou.xianni.retask.trigger.MountTaskTrigger;
import com.chuangyou.xianni.retask.trigger.NpcDialogTaskTrigger;
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
 * 
 * @author laofan
 *
 */
public class RealTaskFactory extends AbstractTaskTriggerFactory {

	
	private static volatile RealTaskFactory instance;
	
	
	public static RealTaskFactory getInstance(){
		if(instance==null){
			synchronized (RealTaskFactory.class) {
				if(instance == null){
					instance = new RealTaskFactory();
				}
			}
		}
		return instance;
	}
	
	
	
	@Override
	public ITriggerObserver createObserver(ITaskCfg cfg, GamePlayer player, ITask task) {
		// TODO Auto-generated method stub
		ITriggerObserver trigger = null;
		switch(cfg.getTaskTarget()){
			case ConditionType.KILL_MONST:
				trigger = new KillMonsterTaskTrigger(player, task);
				break;
			case ConditionType.PASS_FB:
				trigger = new PassFbTaskTrigger(player, task);
				break;
			case ConditionType.NPC_DIALOG:
				trigger = new NpcDialogTaskTrigger(player, task);
				break;
			case ConditionType.PATCH:
			case ConditionType.GET_ITEM:
			case ConditionType.COMMIT_ITEM:
				trigger = new ItemTaskTrigger(player, task);
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
			case ConditionType.KILL_PRIVATE_MONSTER:
				trigger = new KillMonsterTaskTrigger(player, task);
				break;
			case ConditionType.PLAYER_LV:
				trigger = new PlayerLvTaskTrigger(player, task);
				break;
			case ConditionType.PLAYER_FIGHT:
				trigger = new PlayerFightTaskTrigger(player, task);
				break;
			case ConditionType.SKILL_STAGE:
//				trigger = new (player, task);
				break;	
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
			Log.error("RealTaskFactory工厂里没有此类型监听器："+cfg.getTaskTarget());
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
			case ConditionType.NPC_DIALOG:
				trigger = new ZeroInitBehavior(player, task);
				break;
			case ConditionType.PATCH:
			case ConditionType.GET_ITEM:
			case ConditionType.COMMIT_ITEM:
				trigger = new ItemTaskTrigger(player, task);
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
			case ConditionType.KILL_PRIVATE_MONSTER:
				trigger = new KillPrivateMonsterInitBehavior(player, task);
				break;
			case ConditionType.PLAYER_LV:
				trigger = new PlayerLvTaskTrigger(player, task);
				break;
			case ConditionType.PLAYER_FIGHT:
				trigger = new PlayerFightTaskTrigger(player, task);
				break;
			case ConditionType.SKILL_STAGE:
//				trigger = new (player, task);
				break;	
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
			Log.error("RealTaskFactory工厂里没有此类型初始化处理器："+cfg.getTaskTarget());
		}
		return trigger;
	}

}
