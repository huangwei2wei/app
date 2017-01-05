package com.app.empire.world.server.handler.account;

import java.util.List;

import org.apache.log4j.Logger;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.account.RoleListMsgProto.RoleListMsg;
import com.app.empire.protocol.pb.account.RoleMsgProto.RoleMsg;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.exception.ErrorMessages;
import com.app.empire.world.model.Client;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 获取角色列表
 * 
 * @since JDK 1.6
 */
public class GetRoleListHandler implements IDataHandler {
	Logger log = Logger.getLogger(GetRoleListHandler.class);

	// 读取角色列表，但是现在只处理了新加角色
	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		Client client = session.getClient(data.getSessionId());
		if ((client == null) || (!(client.isLogin())))
			return;
		try {
			// long time = System.currentTimeMillis();
			List<Player> list = ServiceManager.getManager().getPlayerService().getPlayerList(client.getAccountId());
			RoleListMsg.Builder msg = RoleListMsg.newBuilder();
			for (Player p : list) {
				RoleMsg.Builder role = RoleMsg.newBuilder();
				role.setNickName(p.getNickname());
				role.setGold(p.getGold());
				role.setLv(p.getLv());
				role.setLvExp(p.getLvExp());
				role.setVipLv(p.getVipLv());
				role.setVipExp(p.getVipExp());
				role.setFight(p.getFight());
				msg.addRole(role);
			}

			// log.info("GetRoleList AccountId:"+client.getAccountId()+"-----------------time:"+(System.currentTimeMillis()-time));
			// int playerCount = 0;
			// if (list != null) {
			// playerCount = list.size();
			// }
			// String[] nickName = new String[playerCount]; // 角色名称
			// int[] gold = new int[playerCount]; // 钱
			// int[] lv = new int[playerCount]; // 玩家等级
			// int[] lvExp = new int[playerCount]; // 经验
			// int[] vipExp = new int[playerCount]; // vip经验
			// int[] vipLv = new int[playerCount]; // vip等级
			// String[] property = new String[playerCount]; // 属性
			// int[] fight = new int[playerCount]; // 战斗力
			//
			// for (int i = 0; i < playerCount; i++) {
			// Player p = list.get(i);
			// nickName[i] = p.getNickname();
			// gold[i] = p.getGold();
			// lv[i] = p.getLv();
			// lvExp[i] = p.getLvExp();
			// vipLv[i] = p.getVipLv();
			// vipExp[i] = p.getVipExp();
			// property[i] = p.getProperty();
			// fight[i] = p.getFight();
			// }
			//
			// GetRoleListOK sendActorList = new GetRoleListOK(data.getSessionId(), data.getSerial());
			// sendActorList.setPlayerCount(playerCount);
			// sendActorList.setNickName(nickName);
			// sendActorList.setGold(gold);
			// sendActorList.setLv(lv);
			// sendActorList.setLvExp(lvExp);
			// sendActorList.setVipLv(vipLv);
			// sendActorList.setVipExp(vipExp);
			// sendActorList.setProperty(property);
			// sendActorList.setFight(fight);
			//
			// // return sendActorList;
			// session.write(sendActorList);

			session.write(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_GetRoleListOK, data.getSessionId(), data.getSerial(), msg.build(), EnumTarget.CLIENT.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			this.log.error(e, e);
			throw new ProtocolException(ErrorMessages.LOGIN_GRLFAIL_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}