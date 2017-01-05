package com.app.empire.world.server.handler.account;

import java.util.Date;

import org.apache.log4j.Logger;

import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.pb.account.RoleLoginMsgProto.RoleLoginMsg;
import com.app.empire.protocol.pb.account.RoleLoginOkMsgProto.RoleLoginOkMsg;
import com.app.empire.protocol.pb.army.PropertyListMsgProto.PropertyListMsg;
import com.app.empire.protocol.pb.army.PropertyMsgProto.PropertyMsg;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.Property;
import com.app.empire.world.exception.CreatePlayerException;
import com.app.empire.world.exception.ErrorMessages;
import com.app.empire.world.model.Client;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;
import com.app.protocol.data.PbAbstractData;
import com.app.protocol.exception.ProtocolException;
import com.app.protocol.handler.IDataHandler;

/**
 * 游戏角色登陆协议处理
 * 
 * @since JDK 1.7
 */
public class RoleLoginHandler implements IDataHandler {
	private Logger log = Logger.getLogger(RoleLoginHandler.class);

	public void handle(AbstractData data) throws Exception {
		ConnectSession session = (ConnectSession) data.getHandlerSource();
		// RoleLogin login = (RoleLogin) data;
		PbAbstractData msg = (PbAbstractData) data;
		RoleLoginMsg login = RoleLoginMsg.parseFrom(msg.getBytes());
		Client client = session.getClient(data.getSessionId());
		if (client == null || !client.isLogin()) {
			log.error("client is null sessionId:" + data.getSessionId());
			return;
		}

		if (session.isFull()) {
			session.removeClient(client);
			throw new ProtocolException(ErrorMessages.LOGIN_FULL_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}

		WorldPlayer worldPlayer = null;
		try {
			worldPlayer = ServiceManager.getManager().getPlayerService().loadWorldPlayer(client, login);
			if (worldPlayer == null) {
				this.log.info("AccountId[" + client.getAccountId() + "]login.getPlayerName()[" + login.getNickname() + "]LOGIN ERROR");
				return;
			}
			// 返回loginPlayerOk
			long nTime = System.currentTimeMillis();
			Player player = worldPlayer.getPlayer();
			Date bsTime = player.getBsTime();
			Date beTime = player.getBeTime();
			if (bsTime != null && beTime != null && nTime >= bsTime.getTime() && nTime <= beTime.getTime()) {
				throw new ProtocolException(ErrorMessages.LOGIN_FREEZE_MESSAGE, data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
			}
			// 角色登录成功
			session.loginPlayer(worldPlayer, data, client);
			player.setLoginTime(new Date());// 设置登录时间
			// RoleLoginOk playerLoginOk = new RoleLoginOk(data.getSessionId(), data.getSerial());

			RoleLoginOkMsg.Builder playerLoginOk = RoleLoginOkMsg.newBuilder();
			playerLoginOk.setPlayerId(player.getId());
			playerLoginOk.setNickname(player.getNickname());
			playerLoginOk.setHeroExtId(player.getHeadId());
			playerLoginOk.setLv(player.getLv());
			playerLoginOk.setLvExp(player.getLvExp());
			playerLoginOk.setVipLv(player.getVipLv());
			playerLoginOk.setVipExp(player.getVipExp());
			playerLoginOk.setDiamond(player.getDiamond());
			playerLoginOk.setGold(player.getGold());
			playerLoginOk.setPower(player.getPower());
			if (player.getProperty() != null) {
				PropertyListMsg.Builder proList = PropertyListMsg.newBuilder();
				for (Property entry : player.getProperty().values()) {
					PropertyMsg.Builder pro = PropertyMsg.newBuilder();
					pro.setType(entry.getKey());
					pro.setBasePoint(entry.getVal());
					proList.addPropertys(pro);
				}
				playerLoginOk.setProperty(proList);
			}
			playerLoginOk.setFight(player.getFight());
			// log.info("udid:" + client.getName());
			// return playerLoginOk;

			// session.write(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLoginOk, data.getSessionId(), data.getSerial(), playerLoginOk.build(), EnumTarget.CLIENT.getValue());
			session.write(Protocol.MAIN_ACCOUNT, Protocol.ACCOUNT_RoleLoginOk, data.getSessionId(), data.getSerial(), worldPlayer.getArmyPacket().build(),
					EnumTarget.SCENESSERVER.getValue());
			System.out.println(data.getSessionId() + "    " + data.getSerial());
			return;
		} catch (CreatePlayerException ex) {
			this.log.error(ex, ex);
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		} catch (ProtocolException ex) {
			ServiceManager.getManager().getPlayerService().release(worldPlayer);
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			this.log.error(ex, ex);
			ServiceManager.getManager().getPlayerService().release(worldPlayer);
			throw new ProtocolException(ex.getMessage(), data.getSerial(), data.getSessionId(), data.getType(), data.getSubType());
		}
	}
}