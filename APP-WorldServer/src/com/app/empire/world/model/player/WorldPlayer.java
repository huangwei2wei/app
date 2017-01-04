package com.app.empire.world.model.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.aspectj.bridge.MessageUtil;

import com.app.db.mysql.entity.FieldInfo;
import com.app.empire.protocol.Protocol;
import com.app.empire.protocol.data.backpack.UpdateGoodsData;
import com.app.empire.protocol.pb.army.ArmyInfoMsgProto.ArmyInfoMsg;
import com.app.empire.protocol.pb.army.HeroInfoMsgProto.HeroInfoMsg;
import com.app.empire.protocol.pb.army.PlayerPositionInfoProto.PlayerPositionInfoMsg;
import com.app.empire.protocol.pb.army.PropertyListMsgProto.PropertyListMsg;
import com.app.empire.protocol.pb.army.PropertyMsgProto.PropertyMsg;
import com.app.empire.protocol.pb.map.ReqChangeMapMsgProto.ReqChangeMapMsg;
import com.app.empire.protocol.pb.player.PlayerInfoMsgProto.PlayerInfoMsg;
import com.app.empire.protocol.pb.warField.PostionMsgProto.PostionMsg;
import com.app.empire.protocol.pb.warField.Vector3Proto.PBVector3;
import com.app.empire.world.entity.mongo.HeroSkill;
import com.app.empire.world.entity.mongo.Player;
import com.app.empire.world.entity.mongo.PlayerGoods;
import com.app.empire.world.entity.mongo.PlayerPostion;
import com.app.empire.world.entity.mongo.PlayerTask;
import com.app.empire.world.entity.mongo.Property;
import com.app.empire.world.exception.PlayerDataException;
import com.app.empire.world.logs.GameLogService;
import com.app.empire.world.model.Client;
import com.app.empire.world.service.factory.ServiceManager;
import com.app.empire.world.service.map.manager.MapProxyManager;
import com.app.empire.world.service.skill.SkillInventory;
import com.app.empire.world.session.ConnectSession;
import com.app.protocol.data.AbstractData;
import com.app.protocol.data.AbstractData.EnumTarget;

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
	// //////////////////////////////////////////////////////////////////////////////
	SkillInventory skillInventory = null;

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
		skillInventory = new SkillInventory(this);
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

	/**
	 * 加经验
	 * 
	 * @param exp
	 * @throws PlayerDataException
	 */
	public void addEXP(int exp) throws PlayerDataException {
		int playerLv = getPlayer().getLv();// 角色等级
		/** 推送 */
	}

	public SkillInventory getSkillInventory() {
		return skillInventory;
	}

	/** 回到出生点 */
	public void backBornPoint() {
		int born_map = 1001;
		FieldInfo fieldTemp = ServiceManager.getManager().getGameConfigService().getFieldInfoConfig().get(born_map);
		ReqChangeMapMsg.Builder reqBuilder = ReqChangeMapMsg.newBuilder();
		PostionMsg.Builder postion = PostionMsg.newBuilder();
		postion.setMapId(born_map);
		postion.setMapKey(born_map);
		PBVector3.Builder builder = PBVector3.newBuilder();
		builder.setX((int) (fieldTemp.getX()));
		builder.setY((int) (fieldTemp.getY()));
		builder.setZ((int) (fieldTemp.getZ()));
		postion.setPostion(builder);
		reqBuilder.setPostionMsg(postion);
		connSession.write(Protocol.MAIN_MAP, Protocol.MAP_ChangeMap, reqBuilder.build(), EnumTarget.SCENESSERVER.getValue());
		// PBMessage message = MessageUtil.buildMessage(Protocol.S_ENTERSCENE, getPlayerId(), reqBuilder);
		// sendPbMessage(message);
	}

	/**
	 * 构建协议数据
	 * 
	 * @return
	 */
	public ArmyInfoMsg.Builder getArmyPacket() {
		ArmyInfoMsg.Builder army = ArmyInfoMsg.newBuilder();
		army.setPlayerInfo(getPlayerInfoProto());
		army.setHeoBattleInfo(getHeoBattleInfo());
		army.setPostionInfo(getPos());
		return army;
	}

	// 构建角色数据
	public PlayerInfoMsg.Builder getPlayerInfoProto() {
		PlayerInfoMsg.Builder msg = PlayerInfoMsg.newBuilder();
		msg.setPlayerId(player.getId());
		msg.setNickName(player.getNickname());
		msg.setFight(player.getFight());
		msg.setLevel(player.getLv());
		msg.setExp(player.getLvExp());
		msg.setMoney(player.getGold());
		msg.setJob(1);
		msg.setSkinId(1);
		msg.setCash(player.getDiamond());
		PlayerPostion playerPostion = player.getPostion();
		if (playerPostion != null) {
			PostionMsg.Builder pos = PostionMsg.newBuilder();
			pos.setMapId(playerPostion.getMapId());
			pos.setMapKey(playerPostion.getMapTempId());
			PBVector3.Builder pbv = PBVector3.newBuilder();
			pbv.setX(playerPostion.getX());
			pbv.setY(playerPostion.getY());
			pbv.setZ(playerPostion.getZ());
			pos.setPostion(pbv);
			msg.setPostionMsg(pos);
		}

		return msg;
	}

	// 构建英雄战斗数据
	public HeroInfoMsg.Builder getHeoBattleInfo() {
		HeroInfoMsg.Builder msg = HeroInfoMsg.newBuilder();
		msg.setPlayerId(this.player.getId());
		Map<Integer, HeroSkill> heroSkills = this.skillInventory.getSkill();
		for (HeroSkill heroSkill : heroSkills.values()) {
			msg.addBattleSkills(heroSkill.getSkillExtId());
		}
		msg.addBattleSkills(1001);
		// 属性
		if (player.getProperty() != null) {
			PropertyListMsg.Builder proList = PropertyListMsg.newBuilder();
			for (Property entry : player.getProperty().values()) {
				PropertyMsg.Builder pro = PropertyMsg.newBuilder();
				pro.setType(entry.getKey());
				pro.setBasePoint(entry.getVal());
				proList.addPropertys(pro);
			}
			msg.setPropertis(proList);
		}
		return msg;
	}

	// 构建玩家位置数据
	public PlayerPositionInfoMsg.Builder getPos() {
		PlayerPositionInfoMsg.Builder playerPositionInfoMsg = PlayerPositionInfoMsg.newBuilder();
		PostionMsg.Builder posMsg = PostionMsg.newBuilder();
		posMsg.setMapId(player.getPostion().getMapId());
		posMsg.setMapKey(player.getPostion().getMapTempId());
		PBVector3.Builder vec = PBVector3.newBuilder();
		vec.setX(player.getPostion().getX());
		vec.setY(player.getPostion().getY());
		vec.setZ(player.getPostion().getZ());
		posMsg.setPostion(vec);
		playerPositionInfoMsg.setCurPos(posMsg);
		return playerPositionInfoMsg;
	}
}
