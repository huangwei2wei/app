package com.app.empire.scene.service.role.objects;

import com.app.empire.scene.service.team.Team;
import com.app.empire.scene.service.team.TeamMgr;
import com.chuangyou.common.protobuf.pb.PlayerLeaveGridProto.PlayerLeaveGridMsg;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.PrivateMonsterMgr;
import com.chuangyou.xianni.warfield.spawn.MonsterSpawnNode;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

public class PrivateMonster extends Monster {
	private long	creater;
	private long	bornTime;
	private long	expiredTime;

	public PrivateMonster(long creater, int liveTime) {
		super(null);
		this.creater = creater;
		PrivateMonsterMgr.add(this);
		bornTime = System.currentTimeMillis();
		expiredTime = System.currentTimeMillis() + liveTime;

	}

	public boolean canSee(long id) {
		if (id == creater) {
			return true;
		}
		// 判断与创建人，是否属于同一个小组
		Team team = TeamMgr.getTeam(creater);
		if (team == null) {
			return false;
		}
		return team.inTeam(id);
	}

	// 移出视野
	public void disappear(long playerId) {
		ArmyProxy army = WorldMgr.getArmy(playerId);
		if (army != null) {
			PlayerLeaveGridMsg.Builder leaveMsg = PlayerLeaveGridMsg.newBuilder();
			leaveMsg.setId(id);
			PBMessage leavepkg = MessageUtil.buildMessage(Protocol.U_LEAVE_GRID, leaveMsg);
			army.sendPbMessage(leavepkg);
		}
	}

	// 进入视野
	public void appear(long playerId) {
		ArmyProxy army = WorldMgr.getArmy(playerId);
		if (army != null) {
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, getAttSnapMsg()));
		}
	}

	public long getCreater() {
		return creater;
	}

	public boolean onDie(Living killer) {
		if (super.onDie(killer)) {
			if (node != null) {
				MonsterSpawnNode mnode = (MonsterSpawnNode) node;
				mnode.lvingDie(this);
			}
			notifyCenter(this.getSkin(), killer.getArmyId());
			this.clear();
			PrivateMonsterMgr.remove(this);
		}
		return true;

	}

	public long getBornTime() {
		return bornTime;
	}

	// 是否过期
	public boolean expired() {
		return System.currentTimeMillis() > expiredTime;
	}
}
