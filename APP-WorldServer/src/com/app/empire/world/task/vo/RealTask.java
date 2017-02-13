package com.app.empire.world.task.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import com.chuangyou.common.protobuf.pb.task.TaskUpdateRespProto.TaskUpdateRespMsg;
import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.StringUtils;
import com.chuangyou.xianni.chat.manager.ChatManager;
import com.chuangyou.xianni.constant.ChatConstant.Channel;
import com.chuangyou.xianni.entity.Option;
import com.chuangyou.xianni.entity.item.ItemAddType;
import com.chuangyou.xianni.entity.task.TaskCfg;
import com.chuangyou.xianni.entity.task.TaskInfo;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.retask.behavior.process.RealTaskPRocessBehavior;
import com.chuangyou.xianni.retask.constant.ConditionType;
import com.chuangyou.xianni.retask.factory.AbstractTaskTriggerFactory;
import com.chuangyou.xianni.retask.factory.RealTaskFactory;
import com.chuangyou.xianni.retask.iinterface.ITaskProcessBehavior;
import com.chuangyou.xianni.retask.iinterface.ITriggerObserver;
import com.chuangyou.xianni.retask.trigger.DropTaskTrigger;
import com.chuangyou.xianni.script.manager.ScriptInterfaceManager;
import com.chuangyou.xianni.script.manager.ScriptManager;
import com.chuangyou.xianni.task.manager.TaskManager;
import com.chuangyou.xianni.task.script.ITaskScript;

/**
 *  真正的任务
 * @author laofan
 *
 */
public class RealTask extends SimpleTask {

	/**
	 * 掉落观察者
	 */
	private final ITriggerObserver dropObserver;
	
	public RealTask(TaskCfg cfg, TaskInfo info, GamePlayer player) {
		super(cfg, info, player);
		// TODO Auto-generated constructor stub
		if(cfg.getDropId()>0){
			dropObserver = new DropTaskTrigger(player,this);
		}else{
			dropObserver = null;
		}
	}
	

	@Override
	public AbstractTaskTriggerFactory getFactory() {
		// TODO Auto-generated method stub
		return RealTaskFactory.getInstance();
	}

	@Override
	public ITaskProcessBehavior getTaskProcessBehavior() {
		// TODO Auto-generated method stub
		return new RealTaskPRocessBehavior(this, player);
	}
	
	
	/**
	 * 任务提交接口处理
	 * 1：触发任务提交脚本
	 * 2：设置状态改变
	 * 3：发奖励
	 * 4: 同步状态给客户端
	 * 5：再次取消监听
	 * 6：？？？？？？？ 提交时扣物品。交由外部去完成
	 */
	public void doTaskCommit(){
		this.removeTrigger();
		if(this.isTimeout())return;
		if(this.isFinish() && getInfo().getState()==TaskInfo.FINISH){			
			getInfo().setUpdateTime(new Date());
			getInfo().setState(TaskInfo.COMMIT);
			getInfo().setOp(Option.Update);
			
			doReward();
			
			doScript(getConfig().getCommitScriptId(), getInfo().getState());
			
			notifyMsg();
		}
	}
	
	/**
	 * 接收任务
	 * 1:设置任务状态
	 * 2:触发接受任务脚本
	 * 3:初始化进度
	 * 4:同步客户端
	 * 5：启动监听
	 */
	public void doAccept(){
		
		System.out.println("接收任务==============》"+getConfig().getTaskId());
		getInfo().setUpdateTime(new Date());
		getInfo().setState(TaskInfo.ACCEPT);
		getInfo().setOp(Option.Update);
		
		doScript(getConfig().getAcceptScriptId(), getInfo().getState());
		
		//刷私有怪----特殊处理
		if(getTaskCfg().getTaskTarget() == ConditionType.KILL_PRIVATE_MONSTER){
			doCreatePrivateMonster();
		}else{			
			this.initTask();
		}
		
		notifyMsg();
		
		this.addTrigger();		
		
	}
	
	private void doCreatePrivateMonster(){
		ArrayList<Integer> posList = getConfig().toMapPos();
		if(posList.size() == 4){
			if(getConfig().getTaskTime()<=0){
				Log.error("任务 ID:"+getConfig().getTaskId()+"刷私有怪任务任务时间不能填0");
				return;
			}
			System.out.println("刷任务怪一只："+player.getPlayerId());
			for(int i=0;i<getConfig().getTargetNum();i++){				
				ScriptInterfaceManager.createPrivateMonster(player.getPlayerId(),cfg.getTargetId(),posList.get(1),
						posList.get(2),
						posList.get(3),
						getConfig().getTaskTime()*1000, posList.get(0));
			}
		}
	}
	
	/**
	 * 完成任务
	 * 1：设置任务状态
	 * 2：触发完成任务脚本
	 * 3: 同步客户端
	 * 
	 * 
	 */
	public void doFinish(){
		getInfo().setUpdateTime(new Date());
		getInfo().setState(TaskInfo.FINISH);
		getInfo().setOp(Option.Update);
		
		if(cfg.getTargetTrigger() == 1){  //取消监听
			removeTrigger();
		}
		
		doScript(getConfig().getCompleteScriptId(), getInfo().getState());
		
		notifyMsg();
	}
	
	

	/////////////////////////////////////////////////////////////////////////////////////
	
	public TaskInfo getInfo(){
		return (TaskInfo) this.info;
	}
	
	public TaskCfg getConfig(){
		return (TaskCfg) this.cfg;
	}
	
	
	/**
	 * 发奖
	 */
	private void doReward(){
		// 发奖
		if (getConfig().getExp() != 0) {
			// todo加经验
			player.getBasePlayer().addExp(getConfig().getExp());
		}
		if (getConfig().getXiu() != 0) {
			// todo加修为
			player.getBasePlayer().addRepair(getConfig().getXiu());
		}
		if (getConfig().getMoney() != 0) {
			player.getBasePlayer().addMoney((int) getConfig().getMoney(), ItemAddType.TASK_ADD);
		}
		if (getConfig().getBindCash() != 0) {
			player.getBasePlayer().addBindCash(getConfig().getBindCash(), ItemAddType.TASK_ADD);
		}
		if (!StringUtils.isNullOrEmpty(getConfig().getItems())) {
			Iterator<Entry<Integer, String[]>> it = getConfig().toItems().entrySet().iterator();
			while (it.hasNext()) {
				String[] s = it.next().getValue();
				player.getBagInventory().addItemInBagOrEmail(Integer.parseInt(s[0]), Integer.parseInt(s[1]), ItemAddType.TASK_ADD, Boolean.parseBoolean(s[2]));
			}
		}
	}
	
	/**
	 * 执行脚本
	 * @param scriptID
	 */
	public void doScript(String scriptID,byte state){
		//执行脚本
		if(!StringUtils.isNullOrEmpty(scriptID)){
			ITaskScript script= (ITaskScript) ScriptManager.getScriptById(scriptID);
			if(script!=null){
				try {
					if(state == 1){
						script.acceptTask(player.getPlayerId(), getInfo().getTaskId());
					}else if(state == 2){
						script.finishTask(player.getPlayerId(), getInfo().getTaskId());
					}else if(state == 3){
						script.commitTask(player.getPlayerId(), getInfo().getTaskId());
					}
				} catch (Exception e) {
					// TODO: handle exception
					ChatManager.sendSystemChatMsg(Channel.SYSTEM, "script error: " + script.getScriptId() + "..." + e.toString(), player.getPlayerId());
				}
			}else{
				Log.error("找不到脚本："+scriptID);
			}
		}
	}
	
	
	/**
	 * 发送消息
	 * 同步任务给客户端
	 * @param player
	 * @param info
	 */
	public void notifyMsg() {
		TaskUpdateRespMsg.Builder notify = TaskUpdateRespMsg.newBuilder();
		notify.setInfo(TaskManager.getTaskMsg(getInfo()));
		player.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_TASKUPDATE, notify));
	}

	/**
	 * 是否超时
	 * @return
	 */
	public boolean isTimeout(){
		if(getConfig().getTaskTime()>0){			
			if (System.currentTimeMillis() - getInfo().getCreateTime().getTime() > getConfig().getTaskTime() * 1000) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void addTrigger() {
		// TODO Auto-generated method stub
		if(this.getInfo().getState() == TaskInfo.ACCEPT || 
				(this.getInfo().getState() == TaskInfo.FINISH && getConfig().getTargetTrigger()==2)){
			super.addTrigger();
			if(this.dropObserver!=null){
				this.dropObserver.addTrigger();
			}
		}
	}


	@Override
	public void removeTrigger() {
		// TODO Auto-generated method stub
		super.removeTrigger();
		if(this.dropObserver!=null){
			this.dropObserver.removeTrigger();
		}
	}


	@Override
	public void initTask() {
		// TODO Auto-generated method stub
		super.initTask();
		if(this.isFinish()){
			this.doFinish();
		}
	}

}
