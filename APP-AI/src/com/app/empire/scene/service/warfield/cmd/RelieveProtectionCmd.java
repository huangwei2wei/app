package com.app.empire.scene.service.warfield.cmd;

import java.util.Date;
import java.util.Set;

import com.chuangyou.common.protobuf.pb.PlayerAttSnapProto.PlayerAttSnapMsg;
import com.chuangyou.common.util.Log;
import com.chuangyou.common.util.TimeUtil;
import com.chuangyou.xianni.campaign.Campaign;
import com.chuangyou.xianni.campaign.CampaignMgr;
import com.chuangyou.xianni.drop.helper.NotifyDropHalper;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Living;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.FieldMgr;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.warfield.helper.selectors.AllSelectorHelper;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;
import com.chuangyou.xianni.world.WorldMgr;

@Cmd(code = Protocol.S_RELIEVE_PROTECTION, desc = "玩家正式进入副本，取消保护")
public class RelieveProtectionCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		army.getPlayer().setProtection(false);
		if (army.getPet() != null)
			army.getPet().setProtection(false);
		// System.err.println("玩家 " + army.getPlayerId() + " 进入地图");
		// 进入新场景通知 新玩家集合同移动集合的差集
		// PlayerAttSnapMsg.Builder mine = PlayerAttSnapMsg.newBuilder();
		// mine.setPlayerId(army.getPlayerId());
		// mine.setType(army.getPlayer().getType());
		// mine.setSkinId(army.getPlayer().getSkin());
		// mine.setPostion(Vector3BuilderHelper.build(army.getPlayer().getPostion()));
		// mine.setTarget(Vector3BuilderHelper.build(army.getPlayer().getTargetPostion()));

		Field field = FieldMgr.getIns().getField(army.getFieldId());

		// 推送附近场景对象
		Set<Long> nears = army.getPlayer().getNears(new AllSelectorHelper(army.getPlayer()));
		
		System.err.println("RelieveProtectionCmd nears = " + nears.size());
		for (Long id : nears) {
			//自己不发送自己
			if (id == army.getPlayerId()) {
				continue;
			}
			Living l = field.getLiving(id);
			if (l == null) {
				continue;
			}
			PlayerAttSnapMsg.Builder snap = l.getAttSnapMsg();
			// PlayerAttSnapMsg.Builder snap = PlayerAttSnapMsg.newBuilder();
			// snap.setPlayerId(id);
			// snap.setType(l.getType());
			// snap.setSkinId(l.getSkin());
			// snap.setPostion(Vector3BuilderHelper.build(l.getPostion()));
			// snap.setTarget(Vector3BuilderHelper.build(l.getTargetPostion()));
			//Log.error(TimeUtil.getDateFormat(new Date()) + army.getPlayerId() + "(收件人)xxxxxxxx发送快照数据至客户端:" + " PlayerId(发件人):" + id);
			army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, snap));

			// 通知附近的玩家进入 <--------迁移至进入地图方法---------->
			ArmyProxy nearArmy = WorldMgr.getArmy(id);
			if (nearArmy == null) {
				continue;
			}
			nearArmy.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP, army.getPlayer().getAttSnapMsg()));
		}

		// 玩家进地图时，通知玩家场景内有公共可见的掉落物
		NotifyDropHalper.notifyPlayerFieldDropItems(field, field.getLiving(army.getPlayerId()));

		// 如果是副本地图，需要通知客户端，某些已经改变初始状态的节点
		Campaign campaign = null;
		if (field.getCampaignId() > 0 && (campaign = CampaignMgr.getCampagin(field.getCampaignId())) != null) {
			campaign.noticeChangeNode(army, field);
		}

	}

}
