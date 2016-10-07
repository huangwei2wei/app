//package com.app.empire.scene.service.warfield.touchPoint.cmd;
//
//import com.chuangyou.common.protobuf.pb.TouchPointProto.TouchPointMsg;
//import com.chuangyou.xianni.proto.PBMessage;
//import com.chuangyou.xianni.protocol.Protocol;
//import com.chuangyou.xianni.socket.Cmd;
//import com.chuangyou.xianni.touchPoint.TouchPointSpwanNode;
//import com.chuangyou.xianni.warfield.FieldMgr;
//import com.chuangyou.xianni.warfield.field.Field;
//import com.chuangyou.xianni.warfield.spawn.SpwanNode;
//import com.chuangyou.xianni.world.AbstractCommand;
//import com.chuangyou.xianni.world.ArmyProxy;
//
//@Cmd(code = Protocol.S_TOUCH_POINT, desc = "玩家接触到可以触发功能的地图节点")
//public class TouchPointCmd extends AbstractCommand {
//
//	@Override
//	public void execute(ArmyProxy army, PBMessage packet) throws Exception {
//		// TODO Auto-generated method stub
//
//		TouchPointMsg req = TouchPointMsg.parseFrom(packet.getBytes());
//
//		Field field = FieldMgr.getIns().getField(army.getFieldId());
//		// TODO 测试代码---等待客户端修改协议
//		// NpcInfo npcInfo = NpcInfoTemplateMgr.getNpcInfo(req.getPointId());
//		// ITouchPointTrigger script = (ITouchPointTrigger)
//		// ScriptManager.getScriptById(npcInfo.getScriptId());
//		// script.action(army.getPlayerId(), npcInfo.getNpcId());
//		// if (true) {
//		// return;
//		// }
//		SpwanNode sn = field.getSpawnNode(req.getSpwanId());
//		if (sn == null) {
//			return;
//		} else {
//			sn.active(army);
//		}
//		// TODO 需要校验，玩家是否在当前地图中
////		if (sn == null || !(sn instanceof TouchPointSpwanNode)) {
////			return;
////		}
////		TouchPointSpwanNode tp = (TouchPointSpwanNode) sn;
////		tp.action(army);
//
//	}
//
//}
