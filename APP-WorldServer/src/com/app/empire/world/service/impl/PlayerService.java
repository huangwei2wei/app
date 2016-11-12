package com.app.empire.world.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.app.empire.protocol.data.account.RoleLogin;
import com.app.empire.protocol.data.account.UpdatePlayerData;
import com.app.empire.world.WorldServer;
//import com.app.empire.protocol.data.cache.PlayerInfo;
//import com.app.empire.protocol.data.cache.UpdatePlayer;
import com.app.empire.world.common.util.Common;
import com.app.empire.world.common.util.KeywordsUtil;
import com.app.empire.world.common.util.ServiceUtils;
import com.app.empire.world.dao.mongo.impl.PlayerDao;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mysql.gameConfig.PlayerLv;
import com.app.empire.world.entity.mysql.gameConfig.BaseRandomName;
import com.app.empire.world.exception.CreatePlayerException;
import com.app.empire.world.exception.ErrorMessages;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.exception.TipMessages;
import com.app.empire.world.logs.GameLogService;
import com.app.empire.world.model.Client;
import com.app.empire.world.model.player.WorldPlayer;
import com.app.empire.world.service.base.impl.GameConfigService;
import com.app.empire.world.service.factory.ServiceManager;
/**
 * 类 <code>PlayerService</code>处理与玩家相关操作业务处理逻辑层
 * 
 * @since JDK 1.6
 */
@Service
public class PlayerService implements Runnable {
	private Logger log = Logger.getLogger(PlayerService.class);
	private Logger onlineLog = Logger.getLogger("onlineLog");

	@Autowired
	private PlayerDao playerDao;
	/**
	 * 玩家playerID与WorldPlayer对应关系HashMap，原名players
	 */
	private ConcurrentHashMap<Integer, WorldPlayer> worldPlayers = new ConcurrentHashMap<Integer, WorldPlayer>();
	public ConcurrentHashMap<Integer, WorldPlayer> getWorldPlayers() {
		return worldPlayers;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(60000L);
				// updatePlayerOnLineTime();
				int onlineNum = worldPlayers.size();
				this.onlineLog.info(TipMessages.ONLINE_PLAYER_NUM + onlineNum);
				GameLogService.onlineNum(onlineNum);
			} catch (Exception e) {
				this.log.error(e, e);
			}
		}
	}

	public void start() {
		Thread t = new Thread(this);
		t.setName("PlayerService-Thread");
		t.start();
	}

	/**
	 * 创建WorldPlayer对象
	 * 
	 * @param player
	 * @return
	 */
	private WorldPlayer createWorldPlayer(Player player) {
		if (worldPlayers.containsKey(player.getId())) {
			return worldPlayers.get(player.getId());
		} else {
			WorldPlayer worldPlayer = new WorldPlayer(player);
			worldPlayers.put(worldPlayer.getPlayer().getId(), worldPlayer);
			worldPlayer.init();
			return worldPlayer;
		}
	}

	/**
	 * 检查玩家是否在线 *
	 * 
	 * @param playerId
	 * @return
	 */
	public boolean playerIsOnline(int playerId) {
		return this.worldPlayers.containsKey(playerId);
	}

	/**
	 * 获取已经加载的玩家
	 * 
	 * @param playerId
	 * @return
	 */
	public WorldPlayer getPlayer(int playerId) {
		return this.worldPlayers.get(playerId);
	}

	/**
	 * 根据角色名称从数据库中查询玩家信息
	 * 
	 * @param actorName
	 * @return
	 */
	public Player getPlayerByName(String actorName) {
		return playerDao.getPlayerByName(actorName);
	}

	/**
	 * 根据玩家id从数据库中查询玩家信息
	 * 
	 * @param playerId
	 * @return
	 */
	public Player getPlayerById(int playerId) {
		return playerDao.getPlayerById(playerId);
	}
	/**
	 * 获取玩家帐号分区下的所有角色 支持一个账号多个角色
	 * 
	 * @param accountId
	 * @return
	 */
	public List<Player> getPlayerList(int accountId) {
		return playerDao.getPlayerListByAccountId(accountId);
	}

	/** 更新玩家在线时间 */
	private void updatePlayerOnLineTime(Player player) {
		try {
			player.setLoginOutTime(new Date());
			long loginTime = player.getLoginTime().getTime();
			long loginOutTime = player.getLoginOutTime().getTime();
			int longTime = (int) (loginOutTime - loginTime);
			if (longTime > 0) {
				int onLineTime = player.getOnLineTime();
				onLineTime += longTime;
				player.setOnLineTime(onLineTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从playerService中，释放对应的worldPlayer对象
	 * 
	 * @param player
	 * @return
	 */
	public boolean release(WorldPlayer worldPlayer) {
		if (worldPlayer == null)
			return false;
		Player player = worldPlayer.getPlayer();
		clearPlayer(worldPlayer);
		updatePlayerOnLineTime(player);
		savePlayerData(player);// 下线保存玩家角色数据
		System.out.println(worldPlayer.getPlayer().getNickname() + " 下线-----");
		writeLog("注销保存玩家信息：id=" + worldPlayer.getPlayer().getId() + ",name=" + worldPlayer.getName() + ",level=" + worldPlayer.getPlayer().getLv());
		return false;
	}
	/**
	 * 踢玩家下线
	 * 
	 * @param playerId
	 */
	public void killLine(int accountId) {
		WorldPlayer worldPlayer = this.worldPlayers.remove(accountId);
		if (null != worldPlayer) {
			worldPlayer.getConnectSession().killSession(worldPlayer.getClient().getSessionId());
			worldPlayer.getConnectSession().removeClient(worldPlayer.getClient());
		}
	}
	/**
	 * 从playerService里注销玩家
	 * 
	 * @param player
	 */
	public void clearPlayer(WorldPlayer player) {
		player.logout();
		this.worldPlayers.remove(player.getPlayer().getId());
	}

	/**
	 * 保存玩家信息
	 * 
	 * @param player 玩家信息
	 */
	public void savePlayerData(Player player) {
		try {
			synchronized (player) {
				playerDao.save(player);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * 创建游戏角色
	 * 
	 * @param gameAccountId
	 * @param playerName
	 * @param accountName
	 * @param sex
	 * @param alignment
	 * @param model
	 * @return
	 * @throws CreatePlayerException
	 */
	public Player createPlayer(int accountId, String nickname, String accountName, int hero_ext_id, int channel, String clientModel, String systemName, String systemVersion)
			throws CreatePlayerException {
		try {
			String name = nickname.trim();
			if (name.getBytes("gbk").length == 0)
				throw new CreatePlayerException(ErrorMessages.PLAYER_CREATENAME);
			if (!(ServiceUtils.checkString(name, false)))
				throw new CreatePlayerException(ErrorMessages.PLAYER_NAME_WRONG);

			if (name.length() > 16)
				throw new CreatePlayerException(ErrorMessages.PLAYER_NAME_LONG_CHAR);

			if (name.length() < 1)
				throw new CreatePlayerException(ErrorMessages.PLAYER_NAME_SHORT);
			if (KeywordsUtil.isInvalidName(name.toLowerCase()))
				throw new CreatePlayerException(ErrorMessages.PLAYER_NAME_WRONG);
			String newName = KeywordsUtil.filterKeywords(name);
			if (!(newName.equals(name))) {
				throw new CreatePlayerException(ErrorMessages.PLAYER_NAME_WRONG);
			}
			if (ServiceUtils.isNumeric(name)) {
				throw new CreatePlayerException(ErrorMessages.PLAYER_NAMEALLNUM_FAILED);
			}
			int count = 0;
			List<Player> listPlayer = this.playerDao.getPlayerListByAccountId(accountId);
			if (listPlayer != null) {
				count = listPlayer.size();
			}
			if (count >= 1) {
				throw new CreatePlayerException(ErrorMessages.PLAYER_CREATECOUNT);
			}
			if (this.playerDao.getPlayerByName(name) != null) {
				throw new CreatePlayerException(ErrorMessages.PLAYER_SAMENAME);
			}

			// 创建角色信息
			Player newPlayer = new Player();
			newPlayer.setAccountId(accountId);
			newPlayer.setAccountName(accountName);
			newPlayer.setNickname(nickname);
			newPlayer.setCreateTime(new Date());
			newPlayer.setLoginTime(new Date());
			newPlayer.setLv(1);
			newPlayer.setLvExp(0);
			newPlayer.setVipLv(0);
			newPlayer.setVipExp(0);
			newPlayer.setGold(999999);
			newPlayer.setPower(10000);
			newPlayer.setDiamond(999999);
			newPlayer.setClientModel(clientModel);
			newPlayer.setSystemName(systemName);
			newPlayer.setSystemVersion(systemVersion);
			newPlayer.setProperty("");
			newPlayer.setFight(0);
			newPlayer.setServerId(WorldServer.serverConfig.getMachineCode());
			newPlayer = this.playerDao.insert(newPlayer);
			// 记录角色创建日志
			GameLogService.createPlayer(newPlayer.getId(), newPlayer.getNickname());

			return newPlayer;
		} catch (CreatePlayerException e) {
			throw e;
		} catch (Exception e) {
			this.log.error(e, e);
			throw new CreatePlayerException(ErrorMessages.PLAYER_CREATE_FAILED);
		}
	}

	/**
	 * 根据角色名称，账号id 读取角色相关信息 没有则创建新角色
	 * 
	 * @param name 角色名称
	 * @param accountId 账号id
	 * @return
	 * @throws Exception
	 */
	public WorldPlayer loadWorldPlayer(Client client, RoleLogin roleLoginData) throws Exception {
		int accountId = client.getAccountId();
		int playerId = client.getPlayerId();
		String nickname = roleLoginData.getNickname();
		WorldPlayer worldPlayer;
		if (playerId == -1) {
			Player player = playerDao.getPlayerByName(accountId, nickname);
			boolean isNewPlayer = false;
			// 不存在就创建角色
			if (player == null) {
				player = createPlayer(accountId, nickname, client.getName(), roleLoginData.getHeroExtId(), client.getChannel(), roleLoginData.getClientModel(), roleLoginData.getSystemName(),
						roleLoginData.getSystemVersion());
				isNewPlayer = true;
			}
			worldPlayer = createWorldPlayer(player);
			if (isNewPlayer)// 新角色给玩家发送一个英雄
				ServiceManager.getManager().getPlayerHeroService().addHero(worldPlayer, 101);
			this.log.info("createWorldPlayer ID[" + player.getId() + "]Level[" + player.getLv() + "] load from db");
		} else {
			worldPlayer = this.worldPlayers.get(playerId);
		}

		// this.log.info("GAMEACCOUNTID[" + accountId + "]FAIL TO LOGIN " + nickname);
		return worldPlayer;
	}

	/**
	 * 同步并重置每日任务
	 * 
	 * @param worldPlayer 玩家信息
	 * @param nowTime 当天凌晨时间点
	 */
	public void updateTask(WorldPlayer worldPlayer) {
		// try {
		// if (null == worldPlayer.getTaskIngList() ||
		// worldPlayer.getTaskIngList().isEmpty() || null ==
		// worldPlayer.getTitleIngList()
		// || worldPlayer.getTitleIngList().isEmpty()) {
		// worldPlayer.initialPlayerTaskTitle();
		// }
		//
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}

	/**
	 * 重命名角色名称
	 * 
	 * @param player
	 * @param newPlayerName
	 * @throws Exception
	 */
	public void updateName(WorldPlayer worldPlayer, String nickname) throws Exception {
		try {
			String name = nickname.trim();
			if (name.length() == 0) {
				throw new Exception(Common.ERRORKEY + ErrorMessages.PLAYER_CREATENAME);
			}
			if (name.getBytes("GBK").length > 16) {
				throw new Exception(Common.ERRORKEY + ErrorMessages.PLAYER_NAME_LONG);
			}
			if (name.getBytes("GBK").length < 2) {
				throw new Exception(Common.ERRORKEY + ErrorMessages.PLAYER_NAME_SHORT);
			}
			if (KeywordsUtil.isInvalidName(name.toLowerCase())) {
				throw new Exception(Common.ERRORKEY + ErrorMessages.PLAYER_NAME_WRONG);
			}
			if (!(ServiceUtils.checkString(name, false))) {
				throw new Exception(Common.ERRORKEY + ErrorMessages.PLAYER_NAME_WRONG);
			}
			if (ServiceUtils.isNumeric(name)) {
				throw new CreatePlayerException(ErrorMessages.PLAYER_NAMEALLNUM_FAILED);
			}
			String newName = KeywordsUtil.filterKeywords(name);
			if (!(newName.equals(name))) {
				throw new Exception(Common.ERRORKEY + ErrorMessages.PLAYER_NAME_WRONG);
			}
			if (this.playerDao.getPlayerByName(name) != null) {
				throw new Exception(Common.ERRORKEY + ErrorMessages.PLAYER_SAMENAME);
			}
			worldPlayer.getPlayer().setNickname(name);
			savePlayerData(worldPlayer.getPlayer());
			writeLog("修改昵称保存玩家信息：id=" + worldPlayer.getPlayer().getId() + "---name=" + worldPlayer.getName() + "---level=" + worldPlayer.getPlayer().getLv());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 随机获取昵称
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String randomName() {
		Map<Integer, Map> randomNameMap = ServiceManager.getManager().getGameConfigService().getGameConfig().get(BaseRandomName.class.getSimpleName());
		Integer[] keys = randomNameMap.keySet().toArray(new Integer[0]);
		Random random = new Random();
		Integer randomKey = keys[random.nextInt(keys.length)];
		String randomValue = randomNameMap.get(randomKey).get("name").toString();
		return randomValue;
	}

	/**
	 * 增加角色经验
	 * 
	 * @param playerId
	 * @param exp
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void addPlayerEXP(WorldPlayer worldPlayer, int exp) throws PlayerDataException {
		Player player = worldPlayer.getPlayer();
		int lv = player.getLv();
		int lvExp = player.getLvExp() + exp;
		int maxLv = ServiceManager.getManager().getConfiguration().getInt("maxlevel");
		if (lv < maxLv) {// 等级上线
			Map<Integer, Map> basePlayerLv = ServiceManager.getManager().getGameConfigService().getGameConfig().get(PlayerLv.class.getSimpleName());
			Integer needExperience = (Integer) basePlayerLv.get(lv).get("experience");
			while (lvExp >= needExperience) {// 升级
				lv++;
				lvExp = lvExp - needExperience;
				needExperience = (Integer) basePlayerLv.get(lv).get("experience");
			}
		}
		player.setLv(lv);
		player.setLvExp(lvExp);
		/** 推送 */
		Map<String, String> info = new HashMap<String, String>();
		info.put("lv", player.getLv() + "");
		info.put("lvExp", player.getLvExp() + "");
		sendUpdatePlayer(info, worldPlayer);
	}
	/***
	 * 给玩家加金币
	 * 
	 * @param worldPlayer
	 * @param gold 　货币数量 可正负
	 * @param type 　货币类型　１金币，2 粮草
	 * @param origin 　金币来源
	 * @param remark 　备注
	 * @return
	 * @throws Exception
	 */
	public boolean addPlayerMoney(WorldPlayer worldPlayer, String type, int value) throws PlayerDataException {
		if (value == 0)
			return true;
		GameConfigService gameConfigService = ServiceManager.getManager().getGameConfigService();
		Player player = worldPlayer.getPlayer();
		Map<String, String> info = new HashMap<String, String>();
		switch (type) {
			case "gold" :// 金币
				int gold = player.getGold() + value;
				if (gold < 0)
					throw new PlayerDataException(gameConfigService.getMsg(37));// 金币不足
				player.setGold(gold);
				writeLog("玩家增加金币 ：id=" + player.getId() + "---name=" + player.getNickname() + ",gold=" + value);
				info.put("gold", String.valueOf(gold));
				break;
			case "diamond" :// 钻石
				int diamond = player.getDiamond() + value;
				if (diamond < 0)
					throw new PlayerDataException(gameConfigService.getMsg(38));// 钻石不足
				player.setDiamond(diamond);
				writeLog("玩家增加钻石 ：id=" + player.getId() + "---name=" + player.getNickname() + ",diamond=" + value);
				info.put("diamond", String.valueOf(diamond));
				break;
			case "power" :// 粮草
				int power = player.getPower() + value;
				if (power < 0)
					throw new PlayerDataException(gameConfigService.getMsg(12));// 粮草不足
				if (value < 0) // 减粮草增加角色经验
					this.addPlayerEXP(worldPlayer, Math.abs(value));
				player.setPower(power);
				info.put("power", String.valueOf(power));
				break;
			case "" :
				break;
			default :
				break;
		}
		// savePlayerData(player);
		sendUpdatePlayer(info, worldPlayer);
		return true;
	}
	public void writeLog(Object message) {
		log.info(message);
	}

	/**
	 * 增加点卷(充值后调用)
	 * 
	 * @param player
	 * @param amount 增加的代币数量
	 * @param giftAmount 赠送的钻石数量
	 * @param origin 增加的途径
	 * @param price 价格
	 * @param orderNum 单号
	 * @param remark 备注
	 */
	public void addTicket(WorldPlayer worldPlayer, int amount, int giftAmount, int origin, float price, String orderNum, String remark, String channelId, String cardType) {

		// if (amount > 0) {
		// try {
		// // 记录充值日志
		// if (origin == TradeService.ORIGIN_RECH) {
		// Client client = worldPlayer.getClient();
		// int accountId = null == client ? 0 : client.getAccountId();
		// GameLogService.recharge(worldPlayer.getId(), worldPlayer.getLevel(),
		// accountId, channelId, cardType, orderNum, price, amount, giftAmount,
		// remark);
		// }
		// Player player = worldPlayer.getPlayer();
		// player.setAmount(player.getAmount() + amount + giftAmount);
		// savePlayerData(player);
		// // 保存代币变更记录
		// PlayerBill playerBill = new PlayerBill();
		// playerBill.setPlayerId(player.getId());
		// playerBill.setCreateTime(new Date());
		// playerBill.setAmount(amount);
		// playerBill.setOrigin(origin);
		// playerBill.setRemark(remark);
		// playerBill.setChargePrice(price);
		// playerBill.setOrderNum(orderNum);
		// playerBill.setChannelId(channelId);
		// playerBill.setCardType(cardType);
		// playerBill.setGiftAmount(giftAmount);
		// if (origin == TradeService.ORIGIN_RECH) {
		// IPlayerBillService billService =
		// ServiceManager.getManager().getPlayerBillService();
		// if (billService.playerIsFirstCharge(worldPlayer.getPlayer())) {
		// playerBill.setIsFirstRecharge(Common.PLAYER_BILL_STATUS_N);
		// } else {
		// playerBill.setIsFirstRecharge(Common.PLAYER_BILL_STATUS_Y);
		// }
		// // 每日首充
		// if (!billService.playerIsEveryDayFirstCharge(player)) {
		// // 获取配置
		// Map<Integer, Integer> map =
		// ServiceManager.getManager().getVersionService().getRechargeIntervalMap();
		// Integer limitNum =
		// ServiceManager.getManager().getVersionService().getSpecialMarkByKey("numberCaps");
		// limitNum = (limitNum == null) ? 10 : limitNum;
		// int num = 1;
		// for (int i = 10; i > 0; i--) {
		// Integer val = map.get(i);
		// if (val != null) {
		// if (val > 0 && amount > val) {
		// num = i;
		// break;
		// }
		// }
		// }
		// num += worldPlayer.getPlayerInfo().getEveryDayFirstChargeNum();
		// if (num > limitNum)
		// num = limitNum;
		// // 记录可以领取的每日首充次数
		// worldPlayer.getPlayerInfo().setEveryDayFirstChargeNum(num);
		// worldPlayer.updatePlayerInfo();
		// }
		// // vip 计算
		// int oldVipExp = player.getVipExp();
		// int totalExp = oldVipExp + amount;
		// totalExp = totalExp > 150000 ? 150000 : totalExp;
		// if (oldVipExp < 150000) {
		// int vipLevel = getVIPLevel(totalExp);
		// // 保存用户信息及推送给前段
		// worldPlayer.setVipInfo(vipLevel, totalExp);
		// vipExpLog.info("VIP经验变更记录：" + "-玩家ID:" + player.getId() + "-玩家名称：" +
		// player.getName() + "-充值钻石：" + amount + "-原经验：" + oldVipExp + "-现经验："
		// + totalExp);
		// }
		// int vipLevel = worldPlayer.getPlayer().getVipLevel();
		// BuySuccess buySuccess = new BuySuccess();
		// buySuccess.setOrderNum(orderNum);
		// buySuccess.setTisket(player.getAmount());
		// buySuccess.setVipLv(vipLevel);
		// buySuccess.setNeedTicket(getVIPExp(vipLevel + 1) - totalExp);
		// buySuccess.setVipExp(totalExp);
		// buySuccess.setAddTicket(amount);
		// buySuccess.setGiftTicket(giftAmount);
		// worldPlayer.sendData(buySuccess);
		// worldPlayer.updateButtonInfo(Common.BUTTON_ID_RECHARGE, false, 30);
		// } else {
		// playerBill.setIsFirstRecharge(Common.PLAYER_BILL_STATUS_Y);
		// }
		// ServiceManager.getManager().getPlayerBillService().save(playerBill);
		// // 非充值暴击，保存邮件
		// if (TradeService.ORIGIN_RECHARGECRIT != origin) {
		// Mail mail = new Mail();
		// mail.setContent(TipMessages.TICKETNOTICECONTENT1 + amount +
		// TipMessages.TICKETNOTICECONTENT2);
		// mail.setIsRead(false);
		// mail.setReceivedId(player.getId());
		// mail.setSendId(0);
		// mail.setSendName(TipMessages.SYSNAME_MESSAGE);
		// mail.setSendTime(new Date());
		// String ticketNotice = TipMessages.TICKETNOTICE;
		// if (origin == TradeService.DAY_ORIGIN_RECHARGE_RWARD) {
		// ticketNotice = ticketNotice + " [" +
		// TipMessages.DAYORIGINRECHARGERWARD + "]";
		// } else if (origin == TradeService.ORIGIN_RECHARGE_RWARD) {
		// ticketNotice = ticketNotice + " [" + TipMessages.ORIGINRECHARGERWARD
		// + "]";
		// }
		// mail.setTheme(ticketNotice);
		// mail.setType(1);
		// mail.setBlackMail(false);
		// mail.setIsStick(Common.IS_STICK);
		// ServiceManager.getManager().getMailService().saveMail(mail, null);
		// }
		// if (TradeService.ORIGIN_RECH == origin) {
		// // PurchaseUtils.removePurchases(orderNum);
		// ServiceManager.getManager().getTaskService().czticket(worldPlayer,
		// amount);
		// worldPlayer.setCanGiveDiamond(amount);
		// }
		// Map<String, String> info = new HashMap<String, String>();
		// info.put("blueDiamond", worldPlayer.getDiamond() + "");
		// sendUpdatePlayer(info, worldPlayer);
		// // 调用充值暴击累积返利的方法
		// if (TradeService.ORIGIN_RECH == origin &&
		// ServiceManager.getManager().getVersionService().isOpenRechargeCritFlag())
		// {
		// delRechargeCrit(amount, worldPlayer);
		// }
		// GameLogService.addMoney(worldPlayer.getId(), worldPlayer.getLevel(),
		// origin, amount, remark);
		// ServiceManager.getManager().getTaskService().addDiamond(worldPlayer,
		// amount);
		// } catch (Exception e) {
		// e.printStackTrace();
		// if (TradeService.ORIGIN_RECH == origin) {
		// BuyFailed buyFailed = new BuyFailed();
		// buyFailed.setOrderNum(orderNum);
		// buyFailed.setCode(1);
		// worldPlayer.sendData(buyFailed);
		// }
		// }
		// }
	}

	/* 推送修改后的数据，如金币，经验，属性，等级等 */
	private void sendUpdatePlayer(Map<String, String> info, WorldPlayer player) {
		UpdatePlayerData updatePlayerInfo = new UpdatePlayerData();
		updatePlayerInfo.setKey(info.keySet().toArray(new String[info.size()]));
		updatePlayerInfo.setValue(info.values().toArray(new String[info.size()]));
		player.sendData(updatePlayerInfo);
	}
	// 定时触发
	public void sysPlayersVigorUp() {
		// System.out.println("sysPlayersVigorUp");
	}
	/**
	 * GM获取玩家列表
	 * 
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map getPlayerList(String user, int userType, long regBeginTime, long regEndTime, long loginBeginTime, long loginEndTime, int page, int pageSize) {
		Map mapData = new HashMap();
		Page<Player> players = playerDao.getPlayerList(user, userType, regBeginTime, regEndTime, loginBeginTime, loginEndTime, page, pageSize);
		long total = players.getTotalElements();
		ArrayList<Player> playerList = new ArrayList<Player>();
		for (Player player : players) {
			playerList.add(player);
		}
		mapData.put("total", total);
		mapData.put("list", playerList);
		return mapData;
	}

	/**
	 * 根据账号类型获取玩家列表（gm 使用）
	 * 
	 * @param user 帐号名，或者ID，或者昵称 多个用户用 “,” 英文逗号隔开
	 * @param userType 0玩家ID，1玩家账号，2玩家昵称
	 * @return
	 */
	public List<Player> getPlayerList(List<String> user, int userType) {
		return playerDao.getPlayerList(user, userType);
	}

}