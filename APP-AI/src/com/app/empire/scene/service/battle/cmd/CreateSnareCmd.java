package com.chuangyou.xianni.battle.cmd;

import com.chuangyou.common.protobuf.pb.battle.CreateSnareMsgProto.CreateSnareMsg;
import com.chuangyou.common.protobuf.pb.battle.CreateSnareRspMsgProto.CreateSnareRspMsg;
import com.chuangyou.common.util.AccessTextFile;
import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.battle.mgr.BattleTempMgr;
import com.chuangyou.xianni.battle.snare.SnareCreateFilter;
import com.chuangyou.xianni.common.Vector3BuilderHelper;
import com.chuangyou.xianni.entity.skill.SnareTemplateInfo;
import com.chuangyou.xianni.proto.MessageUtil;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.protocol.Protocol;
import com.chuangyou.xianni.role.objects.Snare;
import com.chuangyou.xianni.socket.Cmd;
import com.chuangyou.xianni.warfield.field.Field;
import com.chuangyou.xianni.world.AbstractCommand;
import com.chuangyou.xianni.world.ArmyProxy;

@Cmd(code = Protocol.S_CREATE_SNARE, desc = "创建陷阱")
public class CreateSnareCmd extends AbstractCommand {

	@Override
	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
		CreateSnareMsg msg = CreateSnareMsg.parseFrom(packet.getBytes());


		if (!SnareCreateFilter.checkFilter(army.getPlayerId(), msg.getIndexId(), msg.getSnareid())) {
			Log.info("CreateSnareCmd error" + army.getPlayerId() + "----" + msg.toString());
			return;
		}

		if (msg.getBornPos() == null) {
			Log.error("msg.getBornPos is null," + army.getPlayerId() + "----" + msg.toString());
			return;
		}

		SnareTemplateInfo info = BattleTempMgr.getSnareTemp(msg.getSnareid());
		if (info == null) {
			Log.error("BattleTempMgr.getSnareTemp(msg.getSnareid()) is null ,msg : " + msg.toString());
			return;
		}

		Snare snare = new Snare(info, army.getPlayer(), null);
		army.getPlayer().addSnare(snare);
		snare.setArmyId(army.getPlayerId());
		snare.setPostion(Vector3BuilderHelper.get(msg.getBornPos()));

		Field field = army.getPlayer().getField();
		if (field != null) {
			field.enterField(snare);
			// 保证通知到创建人(陷阱创建在视野外???)
			// army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_RESP_ATT_SNAP,
			// snare.getAttSnapMsg()));
		}
		// 返回客户端创建结果
		CreateSnareRspMsg.Builder rspBuilder = CreateSnareRspMsg.newBuilder();
		rspBuilder.setLivingId(snare.getId());
		rspBuilder.setCreateTime(snare.getCreateTime());
		rspBuilder.setBornPos(msg.getBornPos());
		if (msg.getTargetPos() != null) {
			rspBuilder.setTargetPos(msg.getTargetPos());
		}
		rspBuilder.setLockingId(msg.getLockingId());
		army.sendPbMessage(MessageUtil.buildMessage(Protocol.U_SNARE_CREATE_RESULT, rspBuilder));
	}

}
