package com.app.empire.scene.service.world;

import com.chuangyou.common.util.Log;
import com.chuangyou.xianni.proto.PBMessage;
import com.chuangyou.xianni.socket.Command;
import io.netty.channel.Channel;

/**
 * 命令基类
 */
public abstract class AbstractCommand implements Command {

	@Override
	public void execute(Channel channel, PBMessage packet) {
		ArmyProxy army = null;
		try {
			long userId = packet.getPlayerId();
			army = WorldMgr.getArmy(userId);
			if (army == null) {
				Log.warn("code " + packet.getCode() + " not found player " + userId + ",can not continue execute.");
				return;
			}
			execute(army, packet);
		} catch (Exception e) {
			if (army != null) {
				Log.error("player" + "code = " + packet.getCode() + ", has exception :", e);
			} else {
				Log.error("code = " + packet.getCode() + ", has exception :", e);
			}
		}
	}

	/**
	 * 子类处理逻辑
	 * 
	 * @param player
	 * @param packet
	 * @throws Exception
	 */
	public abstract void execute(ArmyProxy army, PBMessage packet) throws Exception;
}
