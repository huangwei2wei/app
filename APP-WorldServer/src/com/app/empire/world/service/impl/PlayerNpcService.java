package com.app.empire.world.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.empire.world.common.util.CommonUtil;
import com.app.empire.world.common.util.DateUtil;
import com.app.empire.world.dao.mongo.impl.PlayerNpcDao;
import com.app.empire.world.entity.mongo.Npc;
import com.app.empire.world.entity.mongo.PlayerNpc;
import com.app.empire.world.entity.mysql.gameConfig.TradeNpc;
import com.app.empire.world.entity.mysql.gameConfig.TradeVip;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;

@Service
public class PlayerNpcService {
	@Autowired
	private PlayerNpcDao playerNpcDao;// 玩家npc

	/**
	 * 获取玩家 Npc
	 * 
	 * @param worldPlayer
	 * @return
	 */
	public PlayerNpc getPlayerNpc(WorldPlayer worldPlayer) {
		int playerId = worldPlayer.getPlayer().getId();
		PlayerNpc playerNpc = playerNpcDao.getNpc(playerId);
		if (playerNpc == null) {
			playerNpc = new PlayerNpc();
			playerNpc.setPlayerId(playerId);
			Npc npc = new Npc();
			npc.setNpcType(1);
			npc.setLv(1);
			Npc npc2 = new Npc();
			npc2.setNpcType(2);
			npc2.setLv(1);
			Map<Integer, Npc> npcMap = new HashMap<Integer, Npc>();
			npcMap.put(1, npc);
			npcMap.put(2, npc2);
			playerNpc.setNpc(npcMap);
			playerNpcDao.insert(playerNpc);
		}
		return playerNpc;
	}

	/**
	 * 升级玩家 npc 等级
	 * 
	 * @param worldPlayer
	 * @param npcType npc 类型
	 */
	public PlayerNpc upgradePlayerNpc(WorldPlayer worldPlayer, int npcType) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerNpc playerNpc = playerNpcDao.getNpc(worldPlayer.getPlayer().getId());
		if (playerNpc == null)
			throw new PlayerDataException(gameConfigService.getMsg(2));// 协议错误
		Npc npc = playerNpc.getNpc().get(npcType);
		int lv = npc.getLv() + 1;// npc 等级

		String key = npcType + "_" + lv;
		Map<String, Map> tradeNpcMap = gameConfigService.getGameConfig4MulKey().get(TradeNpc.class.getSimpleName());
		if (!tradeNpcMap.containsKey(key))
			throw new PlayerDataException(gameConfigService.getMsg(5));// 已经是最高级
		int consume = Integer.parseInt(tradeNpcMap.get(key).get("consume").toString());// 需要消耗的钻石
		int needPlayerLv = Integer.parseInt(tradeNpcMap.get(key).get("teamLv").toString());// 需要角色等级
		int needTime = Integer.parseInt(tradeNpcMap.get(key).get("time").toString());// 升级需要时间
		if (worldPlayer.getPlayer().getLv() < needPlayerLv)
			throw new PlayerDataException(gameConfigService.getMsg(39));// 战队等级不足
		Date upgradeTime = npc.getUpgradeTime();// 升级开始时间
		if (System.currentTimeMillis() - upgradeTime.getTime() < needTime * 1000)
			throw new PlayerDataException(gameConfigService.getMsg(40));// CD时间未到
		ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "diamond", -consume);// 扣钻石
		npc.setLv(lv);
		npc.setUpgradeTime(new Date());
		this.playerNpcDao.save(playerNpc);
		return playerNpc;
	}

	/**
	 * 购买金币或粮草
	 * 
	 * @param worldServer
	 * @param npcType 1 购买金币 2，购买粮草
	 * @return 获得的数量
	 */
	public int buyNpc(WorldPlayer worldPlayer, int npcType) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerNpc playerNpc = playerNpcDao.getNpc(worldPlayer.getPlayer().getId());
		if (playerNpc == null)
			throw new PlayerDataException(gameConfigService.getMsg(2));// 协议错误

		int vipLv = worldPlayer.getPlayer().getVipLv();
		Npc npc = playerNpc.getNpc().get(npcType);
		Map<String, Integer> buyCount = npc.getBuyCount();
		String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
		int count = 0;// 当前购买数量
		if (buyCount.containsKey(dateStr))
			count = buyCount.get(dateStr);
		count++;

		int lv = npc.getLv();
		String key = npcType + "_" + lv;
		Map tradeNpcMap = gameConfigService.getGameConfig4MulKey().get(TradeNpc.class.getSimpleName()).get(key);
		Map tradeVipMap = gameConfigService.getGameConfig().get(TradeVip.class.getSimpleName()).get(vipLv);
		int baseValues = Integer.parseInt(tradeNpcMap.get("baseValues").toString());// 获得的数量
		boolean critRandom = CommonUtil.random(Double.parseDouble(tradeNpcMap.get("critRate").toString()));// 是否暴击
		int needDiamond = 0;// 需要钻石
		int crit = 1;// 暴击倍数
		int canBuyCount = 0;// 能够购买次数
		if (npcType == 1) {// 金币
			needDiamond = Integer.parseInt(tradeVipMap.get("goldDiamond").toString());
			canBuyCount = Integer.parseInt(tradeVipMap.get("goldCount").toString());
			if (critRandom)
				crit = Integer.parseInt(tradeVipMap.get("goldCritMultiple").toString());
		} else if (npcType == 2) {// 粮草
			needDiamond = Integer.parseInt(tradeVipMap.get("provisionsDiamond").toString());
			canBuyCount = Integer.parseInt(tradeVipMap.get("provisionsCount").toString());
			if (critRandom)
				crit = Integer.parseInt(tradeVipMap.get("provisionsCritMultiple").toString());
		}
		if (count > canBuyCount)
			throw new PlayerDataException(gameConfigService.getMsg(41));// 今天已购买完，明天再来吧

		baseValues *= crit;
		PlayerService playerService = ServiceManager.getManager().getPlayerService();
		playerService.addPlayerMoney(worldPlayer, "diamond", -needDiamond);// 扣钻石
		if (npcType == 1) // 金币
			playerService.addPlayerMoney(worldPlayer, "gold", baseValues);// 加金币
		else if (npcType == 2) // 粮草
			playerService.addPlayerMoney(worldPlayer, "power", baseValues);// 加粮草

		buyCount.clear();
		buyCount.put(dateStr, count);
		this.playerNpcDao.save(playerNpc);
		return baseValues;
	}

	/**
	 * 领取金币或粮草
	 * 
	 * @param worldServer
	 * @param npcType 1 购买金币 2，购买粮草
	 * @return
	 */
	public int receiveNpc(WorldPlayer worldPlayer, int npcType) throws PlayerDataException {
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		PlayerNpc playerNpc = playerNpcDao.getNpc(worldPlayer.getPlayer().getId());
		if (playerNpc == null)
			throw new PlayerDataException(gameConfigService.getMsg(2));// 协议错误
		Npc npc = playerNpc.getNpc().get(npcType);
		int lv = npc.getLv();// npc 等级
		long time1 = 0;// 升级前的时间
		long time2 = 0;// 升级后的时间
		long nowTime = System.currentTimeMillis();// 现在时间
		long upgradeTime = npc.getUpgradeTime().getTime();// 升级时间
		long receiveTime = npc.getReceiveTime().getTime();// 上次领取时间

		Map tradeNpcMap = gameConfigService.getGameConfig4MulKey().get(TradeNpc.class.getSimpleName()).get(npcType + "_" + (lv - 1));
		Map tradeNpcMap2 = gameConfigService.getGameConfig4MulKey().get(TradeNpc.class.getSimpleName()).get(npcType + "_" + lv);

		int maxCapacity = 0;// 上限值
		int needTime = Integer.parseInt(tradeNpcMap.get("time").toString());// 需要的时间（s）
		long okTime = upgradeTime + needTime * 1000;// 升级成功时间

		if (nowTime > okTime) {// 升级成功
			maxCapacity = Integer.parseInt(tradeNpcMap2.get("maxCapacity").toString());
			if (receiveTime > okTime) {// 只有升级成功后的部分
				time2 = nowTime - receiveTime;
			} else {// 有两部分
				time1 = okTime - receiveTime;
				time2 = nowTime - okTime;
			}
		} else {// 还未升级成功
			maxCapacity = Integer.parseInt(tradeNpcMap.get("maxCapacity").toString());
			time1 = nowTime - upgradeTime;
		}
		int value = 0;
		if (time1 > 0) {
			int output = Integer.parseInt(tradeNpcMap.get("output").toString());
			value += output * (time1 / 60000);
		}
		if (time2 > 0) {
			int output = Integer.parseInt(tradeNpcMap2.get("output").toString());
			value += output * (time2 / 60000);
		}
		if (value / maxCapacity < 0.1)
			throw new PlayerDataException(gameConfigService.getMsg(44));// 资源不足不能领取

		value = value > maxCapacity ? maxCapacity : value;
		if (npcType == 1)
			ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "gold", value);
		else
			ServiceManager.getManager().getPlayerService().addPlayerMoney(worldPlayer, "power", value);

		npc.setReceiveTime(new Date());
		this.playerNpcDao.save(playerNpc);
		return value;
	}
}
