package com.app.empire.world.task.trigger;

import java.util.Map;

import com.chuangyou.xianni.entity.equip.EquipBarInfo;
import com.chuangyou.xianni.entity.rank.RankTempInfo;
import com.chuangyou.xianni.event.EventNameType;
import com.chuangyou.xianni.event.ObjectEvent;
import com.chuangyou.xianni.event.ObjectListener;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.rank.RankServerManager;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskInitBehavior;

/**
 * 装备相关任务
 * @author laofan
 *
 */
public class EquipTaskTrigger extends BaseTaskTrigger implements ITaskInitBehavior{

	

	public EquipTaskTrigger(GamePlayer player, ITask task) {
		super(player, task);
		// TODO Auto-generated constructor stub
	}


	protected int getCountLv(Map<Short, EquipBarInfo> map,int num){
		int count=0;
		for (EquipBarInfo ebi : map.values()) {
			if(ebi.getLevel()>=num){
				count ++;
			}
		}
		return count;
	}
	
	protected int getCountGrade(Map<Short, EquipBarInfo> map,int num){
		int count=0;
		for (EquipBarInfo ebi : map.values()) {
			if(ebi.getGrade()>=num){
				count ++;
			}
		}
		return count;
	}
	

	/**
	 *  获取穿在身上装备数量
	 * @return
	 */
	protected int getNumInBody(){
		return player.getBagInventory().getHeroEquipmentBag().getAllItemCount();
	}

	public void initListener() {
		// TODO Auto-generated method stub
		if(this.listener!=null)return;
		this.listener = new ObjectListener() {
			
			@Override
			public void onEvent(ObjectEvent event) {
				// TODO Auto-generated method stub
				int targetId =  getTask().getTaskCfg().getTargetId();
				EquipBarInfo equipInfo = (EquipBarInfo) event.getObject();
				if(targetId == 6){
					getTask().updateProcess(getNumInBody());
					return;
				}
				if(equipInfo!=null){
					if(targetId == 1){ 
						if(getTask().getTaskCfg().getTargetId1() == equipInfo.getPosition()){
//							getTask().getTaskInfo().setProcess(equipInfo.getLevel());
							getTask().updateProcess(equipInfo.getLevel());
						}
					}else if(targetId == 2){
						getTask().updateProcess(getCountLv(player.getEquipInventory().getEquipBarInfoMap(), getTask().getTaskCfg().getTargetId1()));
//						info.setProcess(getCountLv(player.getEquipInventory().getEquipBarInfoMap(), getTask().getTaskCfg().getTargetId1()));
					}else if(targetId == 3){  
						if(getTask().getTaskCfg().getTargetId1() == equipInfo.getPosition()){
							getTask().updateProcess(equipInfo.getGrade());
//							info.setProcess(equipInfo.getGrade());
						}
					}else if(targetId == 4){
						getTask().updateProcess(getCountGrade(player.getEquipInventory().getEquipBarInfoMap(), getTask().getTaskCfg().getTargetId1()));
//						info.setProcess(getCountGrade(player.getEquipInventory().getEquipBarInfoMap(), getTask().getTaskCfg().getTargetId1()));
					}
				}else if(targetId == 5){
					RankTempInfo rankInfo = RankServerManager.getInstance().getRankTempInfo(player.getPlayerId());
					if(rankInfo!=null){
						getTask().updateProcess((int)rankInfo.getEquip());
//						info.setProcess((int)rankInfo.getEquip());
					}
				}
				
				
			}
		};
	}

	@Override
	public void addTrigger() {
		// TODO Auto-generated method stub
		this.initListener();
		player.addListener(this.listener, EventNameType.EQUIP);
	}

	@Override
	public void removeTrigger() {
		// TODO Auto-generated method stub
		player.removeListener(this.listener, EventNameType.EQUIP);
	}


	@Override
	public void initTask() {
		// TODO Auto-generated method stub
		int targetId =  getTask().getTaskCfg().getTargetId();
		if(targetId == 1){ 
			EquipBarInfo equipInfo = player.getEquipInventory().getEquipBarByPos((short) getTask().getTaskCfg().getTargetId1());
			if(equipInfo!=null){
				getTask().getTaskInfo().setProcess(equipInfo.getLevel());
			}
		}else if(targetId == 2){ 
			getTask().getTaskInfo().setProcess(getCountLv(player.getEquipInventory().getEquipBarInfoMap(), getTask().getTaskCfg().getTargetId1()));
		}else if(targetId == 3){ 
			EquipBarInfo equipInfo = player.getEquipInventory().getEquipBarByPos((short) getTask().getTaskCfg().getTargetId1());
			if(equipInfo!=null){
				getTask().getTaskInfo().setProcess(equipInfo.getGrade());
			}
		}else if(targetId == 4){
			getTask().getTaskInfo().setProcess(getCountGrade(player.getEquipInventory().getEquipBarInfoMap(),getTask().getTaskCfg().getTargetId1()));
		}else if(targetId == 5){
			RankTempInfo rankInfo = RankServerManager.getInstance().getRankTempInfo(player.getPlayerId());
			if(rankInfo!=null){
				getTask().getTaskInfo().setProcess((int)rankInfo.getEquip());
			}
		}else if(targetId == 6){
			getTask().getTaskInfo().setProcess(this.getNumInBody());
		}
	}

}
