package com.app.empire.scene.server.handler.account;

import org.apache.log4j.Logger;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.army.ArmyInfoMsgProto.ArmyInfoMsg;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.objects.Pet;
import com.app.empire.scene.service.role.objects.Player;
import com.app.empire.scene.service.world.ArmyProxy;
import com.app.empire.scene.service.world.SimplePlayerInfo;
import com.app.empire.scene.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.handler.IDataHandler;

public class RoleLoginOkHandler implements IDataHandler {
	Logger	log	= Logger.getLogger(RoleLoginOkHandler.class);

	@Override
	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		PbAbstractData pbData = (PbAbstractData) data;
		ArmyInfoMsg msg = ArmyInfoMsg.parseFrom(pbData.getBytes());
		// ArmyInfoMsg msg = (ArmyInfoMsg) data;

		int playerId = msg.getPlayerInfo().getPlayerId();
		System.out.println("玩家："+playerId+" 登录");
		SimplePlayerInfo simPlayer = new SimplePlayerInfo();
		simPlayer.readProto(msg.getPlayerInfo());

		// 初始化英雄数据
		Player player = new Player(playerId);
		// Team t = TeamMgr.getTeam(playerId);
		// if (t == null) {
		// player.setTeamId(0);
		// } else {
		// player.setTeamId(t.getTeamid());
		// }
		player.setSimpleInfo(simPlayer);
		// 初始化觉醒技能buff
		player.updateWeaponBuff();
		player.readHeroInfo(msg.getHeoBattleInfo());

		Pet pet = null;
		if (msg.getPetBattleInfo().getPetTempId() > 0) {
			// 初始化宠物数据
			pet = new Pet(playerId, IDMakerHelper.nextID());
			pet.readPetInfo(msg.getPetBattleInfo());
		}
		ArmyProxy army = new ArmyProxy(playerId, session, "center", simPlayer, player, pet);
		session.playerLogin(army);

		// PBMessage message = MessageUtil.buildMessage(Protocol.U_ARMY_HERO_INFO, msg.getHeoBattleInfo());
		// army.sendPbMessage(message);
		// 通知客户端，已经登录成功
		// LoginInOK ok = new LoginInOK(data.getSessionId(), data.getSerial());
		// ok.setResult(1);
		// ok.setTime(System.currentTimeMillis());
		session.write(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLoginOk, data.getSessionId(), data.getSerial(), msg, EnumTarget.SCENESSERVER.getValue());

	}

}
