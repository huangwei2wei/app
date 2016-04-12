package com.app.empire.world.model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.app.empire.protocol.data.backpack.UpdateGoodsData;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerGoods;
import com.app.empire.world.entity.mongo.PlayerTask;
import com.app.empire.world.logs.GameLogService;
import com.app.empire.world.model.Client;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;

/**
 * 封装游戏世界角色类， 游戏角色的基本属性操作都在本类中继承或实现。
 * 
 * @author doter
 */
public class WorldPlayer {
	private Logger log = Logger.getLogger(WorldPlayer.class);
	private long lastSendMsgTime;// 最后发送聊天时间
	private int sendMsgCount;// 连续发送消息数量
	private ConnectSession connSession = null; // 当前所对应的Session
	private Client accountClient = null; // 当前所对应的帐户Client
	private Player player;
	private PlayerTask playerTask = null; // 玩家进行中的任务列表
	private ConcurrentHashMap<Integer, PlayerGoods> needPushPlayerGoods = new ConcurrentHashMap<Integer, PlayerGoods>(); // 需要推送的物品
	private ConcurrentHashMap<Integer, PlayerHeroVo> PlayerHeroMap = new ConcurrentHashMap<Integer, PlayerHeroVo>();// 英雄流水id->obj

	/**
	 * 构造函数，初始化游戏人物各项数值
	 * 
	 * @param player
	 * @throws Exception
	 */
	public WorldPlayer(Player player) {
		this.player = player;
	}

	// 初始化
	public void init() {
	}

	// 当前所对应的Session
	public void setConnectSession(ConnectSession session) {
		this.connSession = session;
	}

	public ConnectSession getConnectSession() {
		return this.connSession;
	}

	// 当前所对应的帐户Client
	public void setAccountClient(Client client) {
		this.accountClient = client;
	}

	public Client getClient() {
		return this.accountClient;
	}

	public void sendData(AbstractData data) {
		if (this.connSession != null)
			connSession.write(data, player.getId());
	}

	public Player getPlayer() {
		return player;
	}

	public long getLastSendMsgTime() {
		return this.lastSendMsgTime;
	}

	public void setLastSendMsgTime(long lastSendMsgTime) {
		this.lastSendMsgTime = lastSendMsgTime;
	}

	public int getSendMsgCount() {
		return sendMsgCount;
	}

	public void setSendMsgCount(int sendMsgCount) {
		this.sendMsgCount = sendMsgCount;
	}

	public String getName() {
		return this.player.getNickname();
	}

	public int getGameAccountId() {
		return this.player.getAccountId();
	}

	public void writeLog(Object message) {
		StringBuffer msg = new StringBuffer();
		msg.append("玩家:playerId=");
		msg.append(this.player.getId());
		msg.append("---playerName:");
		msg.append(this.getName());
		msg.append("---message=");
		msg.append(message);
		log.info(msg);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	// /**
	// * 活力值增涨一点(但不超过最大值) 跨天回复到最大值
	// */
	// public int energyUp(int add) {
	// Date vigorUpdateTime = playerInfo.getVigorUpdateTime();
	// int vigor = playerInfo.getVigor();
	// Calendar cal = Calendar.getInstance();
	// if (!DateUtil.isSameDate(vigorUpdateTime, cal.getTime())) {
	// // 购买次数清零
	// buyVigorCount = 0;
	// buyLimitedCount.clear();
	// }
	// int max = getMaxVigor();
	// if (getVigor() >= max) {
	// return 0;
	// }
	// vigor += add;
	// playerInfo.setVigor(vigor);
	// Map<String, String> info = new HashMap<String, String>();
	// info.put("vigor", getVigor() + "");
	// ServiceManager.getManager().getPlayerService().sendUpdatePlayer(info,
	// this);
	// return add;
	// }

	// /**
	// * 根据上次更新时间到当前时间每5分钟增加一点。
	// */
	// public void addVigorFormTime() {
	// Date vigorUpdateTime = playerInfo.getVigorUpdateTime();
	// if (vigorUpdateTime == null)
	// return;
	// Calendar cal = Calendar.getInstance();
	// long cha = cal.getTime().getTime() - vigorUpdateTime.getTime();
	// long min = cha / (1000 * 60);
	// // 6分钟加一点 注意：更改这个时间同时也要把定时器时间更改过来
	// int addVigor = (int) min / 6;
	// if (addVigor > 0) {
	// addVigor = addVigor > 120 ? 120 : addVigor;
	// for (int i = 0; i < addVigor; i++) {
	// vigorUp(1);
	// }
	// playerInfo.setVigorUpdateTime(cal.getTime());
	// }
	// }
	//
	// /**
	// * 获取活力
	// *
	// * @return
	// */
	// public int getVigor() {
	// return playerInfo.getVigor();
	// }
	//
	// public Date getVigorUpdateTime() {
	// return playerInfo.getVigorUpdateTime();
	// }
	//
	// public void setVigorUpdateTime(Date time) {
	// playerInfo.setVigorUpdateTime(time);
	// }

	// /**
	// * 扣除活力 输入正数
	// *
	// * @return
	// */
	// public void useVigor(int val) {
	// int vigor = getVigor();
	// if (getVigor() < 0)
	// return;
	// vigor -= val;
	// vigor = vigor < 0 ? 0 : vigor;
	// playerInfo.setVigor(vigor);
	// Map<String, String> info = new HashMap<String, String>();
	// info.put("vigor", getVigor() + "");
	// ServiceManager.getManager().getPlayerService().sendUpdatePlayer(info,
	// this);
	// }
	//
	// /**
	// * 增加购买次数
	// */
	// public void addBuyVigorCount() {
	// buyVigorCount++;
	// Map<String, String> info = new HashMap<String, String>();
	// info.put("buyVigorTimes", buyVigorCount + "");
	// ServiceManager.getManager().getPlayerService().sendUpdatePlayer(info,
	// this);
	// }

	// /**
	// * 已购买次数
	// *
	// * @return
	// */
	// public int getBuyVigorCount() {
	// return buyVigorCount;
	// }

	public PlayerTask getPlayerTask() {
		if (this.playerTask == null) {// 加载任务
			ServiceManager.getManager().getPlayerTaskService().getPlayerListByAccountId(this);
		}
		return this.playerTask;
	}

	public void setPlayerTask(PlayerTask playerTask) {
		this.playerTask = playerTask;
	}

	/**
	 * 玩家下线处理
	 */
	public void logout() {
		ServiceManager.getManager().getHttpThreadPool().execute(new LoginOutThread(this));
	}

	/**
	 * 玩家上线处理
	 */
	public void login() {
		ServiceManager.getManager().getHttpThreadPool().execute(new LoginThread(this));
	}

	public class LoginOutThread implements Runnable {
		private WorldPlayer player;
		public LoginOutThread(WorldPlayer player) {
			this.player = player;
		}
		public void run() {
			// 记录玩家退出日志
			GameLogService.logout(player.getPlayer().getId(), player.getPlayer().getLv(), (int) (player.getPlayer().getLoginTime().getTime() / 1000));
			// String area = WorldServer.config.getAreaId().toUpperCase();
		}
	}

	public class LoginThread implements Runnable {
		private WorldPlayer player;
		public LoginThread(WorldPlayer player) {
			this.player = player;
		}
		@Override
		public void run() {
			//
		}
	}

	/**
	 * 添加需要推送的物品
	 * 
	 * @param playerGoods
	 */
	public void addPushPlayerGoods(PlayerGoods playerGoods) {
		this.needPushPlayerGoods.put(playerGoods.getId(), playerGoods);
	}
	/**
	 * 推送背包物品的数量更新情况
	 */
	public void pushUpdateGoods() {
		if (this.needPushPlayerGoods.size() == 0)
			return;
		UpdateGoodsData updateGoods = new UpdateGoodsData();
		List<Integer> id = new ArrayList<Integer>();// 背包物品流水id
		List<Integer> goodsId = new ArrayList<Integer>();// 物品id
		List<Integer> goodsNum = new ArrayList<Integer>();// 背包物品当前数量,0表示删除
		List<Long> upTime = new ArrayList<Long>();// 更新时间
		for (Entry<Integer, PlayerGoods> entry : this.needPushPlayerGoods.entrySet()) {
			PlayerGoods playerGoods = entry.getValue();
			id.add(playerGoods.getId());
			goodsId.add(playerGoods.getGoodsId());
			goodsNum.add(playerGoods.getGoodsNum());
			upTime.add(playerGoods.getDateTime().getTime());
		}
		updateGoods.setId((ArrayUtils.toPrimitive(id.toArray(new Integer[id.size()]))));
		updateGoods.setGoodsId((ArrayUtils.toPrimitive(goodsId.toArray(new Integer[goodsId.size()]))));
		updateGoods.setGoodsNum((ArrayUtils.toPrimitive(goodsNum.toArray(new Integer[goodsNum.size()]))));
		updateGoods.setUpTime(ArrayUtils.toPrimitive(upTime.toArray(new Long[upTime.size()])));
		this.sendData(updateGoods);
		needPushPlayerGoods.clear();
	}

	public ConcurrentHashMap<Integer, PlayerHeroVo> getPlayerHeroMap() {
		return PlayerHeroMap;
	}

}
