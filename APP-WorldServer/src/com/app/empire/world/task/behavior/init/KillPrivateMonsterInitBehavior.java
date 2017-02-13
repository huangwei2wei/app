package com.app.empire.world.task.behavior.init;

import com.chuangyou.common.protobuf.pb.gather.SearchPrivateMonsterInnerProto.SearchPrivateMonsterInnerMsg;
import com.chuangyou.xianni.entity.task.TaskInfo;
import com.chuangyou.xianni.netty.GatewayLinkedSet;
import com.chuangyou.xianni.player.GamePlayer;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.retask.iinterface.ITask;
import com.chuangyou.xianni.retask.iinterface.ITaskInitBehavior;

public class KillPrivateMonsterInitBehavior implements ITaskInitBehavior {


	private final GamePlayer player;
	private final ITask task;
	
	
	public KillPrivateMonsterInitBehavior(GamePlayer player, ITask task) {
		this.player = player;
		this.task = task;
	}

	@Override
	public ITask getTask() {
		// TODO Auto-generated method stub
		return task;
	}

	@Override
	public void initTask() {
		// TODO Auto-generated method stub
		if(((TaskInfo)getTask().getTaskInfo()).getState() == TaskInfo.ACCEPT){
			//todo查询是否有私有任务怪
			SearchPrivateMonsterInnerMsg.Builder msg = SearchPrivateMonsterInnerMsg.newBuilder();
			msg.setPlayerId(player.getPlayerId());
			msg.setMonsterId(getTask().getTaskCfg().getTargetId());
			msg.setTaskId(((TaskInfo)getTask().getTaskInfo()).getTaskId());
			PBMessage pkg = MessageUtil.buildMessage(Protocol.S_SEARCH_PRIVATE_MONSTER,msg);
			GatewayLinkedSet.send2Server(pkg);
		}
	}

}
