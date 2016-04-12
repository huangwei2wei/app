package com.app.empire.world.logs;

import org.apache.log4j.Logger;

public class GameLogService {
	private static Logger log = Logger.getLogger("gamedata");
	/** 标识号-创建角色 */
	private static final int MARKNUM_CREATE = 1;
	/** 标识号-角色登入 */
	private static final int MARKNUM_LOGIN = 2;
	/** 标识号-角色登出 */
	private static final int MARKNUM_LOGOUT = 3;
	/** 标识号-角色充值 */
	private static final int MARKNUM_RECHARGE = 4;
	/** 标识号-玩家获得代币记录 */
	private static final int MARKNUM_ADDMONEY = 5;
	/** 标识号-玩家消耗代币记录 */
	private static final int MARKNUM_USEMONEY = 6;
	/** 标识号-玩家在线数量 */
	private static final int MARKNUM_ONLINENUM = 7;
	/** 标识号-玩家升级记录 */
	private static final int MARKNUM_UPLIVEL = 8;
	/** 标识号-金币获得记录 */
	private static final int MARKNUM_ADDGOLD = 9;
	/** 标识号-金币消耗记录 */
	private static final int MARKNUM_USEGOLD = 10;
	/** 标识号-物品获得记录 */
	private static final int MARKNUM_GETITEM = 11;
	/** 标识号-玩家聊天记录 */
	private static final int MARKNUM_CHART = 12;
	/** 标识号-界面停留记录 */
	private static final int MARKNUM_INTERFACE = 13;
	/** 标识号-任务状态变更记录 */
	private static final int MARKNUM_TASKCHANGE = 14;
	/** 标识号-任务奖励提升记录 */
	private static final int MARKNUM_TASKREWARDCHANGE = 15;
	/** 标识号-发送邮件记录 */
	private static final int MARKNUM_SENDMAIL = 16;
	/** 标识号-删除邮件记录 */
	private static final int MARKNUM_DELETEMAIL = 17;
	/** 标识号-好友相关记录 */
	private static final int MARKNUM_FRIEND = 18;
	/** 标识号-玩家点击附近好友列表记录 */
	private static final int MARKNUM_GETNEARBYFRIENDLIST = 19;
	/** 标识号-附近好友相关记录 */
	private static final int MARKNUM_NEARBYFRIEND = 20;
	/** 标识号-VIP开通记录 */
	private static final int MARKNUM_OPENVIP = 21;
	/** 标识号-VIP礼包领取记录 */
	private static final int MARKNUM_GETVIPREWARD = 22;
	/** 标识号-获得活跃度相关记录 */
	private static final int MARKNUM_ADDACTIVITY = 23;
	/** 标识号-活跃度奖励领取记录 */
	private static final int MARKNUM_ACTIVITYREWARD = 24;
	/** 标识号-签到相关记录 */
	private static final int MARKNUM_REGIS = 25;
	/** 标识号-成就相关记录 */
	private static final int MARKNUM_TITLE = 26;
	/** 标识号-玩家查看公会记录 */
	private static final int MARKNUM_CHECKGUILD = 27;
	/** 标识号-玩家申请加入公会记录 */
	private static final int MARKNUM_APPLYGUILD = 28;
	/** 标识号-公会技能使用记录 */
	private static final int MARKNUM_GUILDSKILL = 29;
	/** 标识号-公会人员变更记录 */
	private static final int MARKNUM_GUILDCHANGE = 30;
	/** 标识号-宠物驯服记录 */
	private static final int MARKNUM_ADDPET = 31;
	/** 标识号-宠物出战记录 */
	private static final int MARKNUM_PLAYPET = 32;
	/** 标识号-宠物训练记录 */
	private static final int MARKNUM_TRAINPET = 33;
	/** 标识号-宠物传承记录 */
	private static final int MARKNUM_INHERITPET = 34;
	/** 标识号-求婚记录 */
	private static final int MARKNUM_PROPOSE = 35;
	/** 标识号-结婚记录 */
	private static final int MARKNUM_MARRY = 36;
	/** 标识号-离婚记录 */
	private static final int MARKNUM_DIVORCE = 37;
	/** 标识号-结婚转钻记录 */
	private static final int MARKNUM_SHIFTMONEY = 38;
	/** 标识号-玩家转生记录 */
	private static final int MARKNUM_REBIRTH = 39;
	/** 标识号-成长基金购买记录 */
	private static final int MARKNUM_BUYFUND = 40;
	/** 标识号-成长基金领取记录 */
	private static final int MARKNUM_GETFUND = 41;
	/** 标识号-玩家邀请好友奖励领取记录 */
	private static final int MARKNUM_INVITEREWARD = 42;
	/** 标识号-玩家镶嵌记录 */
	private static final int MARKNUM_MOSAIC = 43;
	/** 标识号-玩家升星记录 */
	private static final int MARKNUM_UPSTAR = 44;
	/** 标识号-玩家重铸记录 */
	private static final int MARKNUM_REHAB = 45;
	/** 标识号-玩家转移记录 */
	private static final int MARKNUM_SHIFT = 46;
	/** 标识号-玩家强化记录 */
	private static final int MARKNUM_STRENGTHEN = 47;
	/** 标识号-玩家对战记录 */
	private static final int MARKNUM_BATTLE = 48;
	/** 标识号-玩家点击开始游戏记录 */
	private static final int MARKNUM_STARTGAME = 49;
	/** 标识号-玩家点击查找房间记录 */
	private static final int MARKNUM_FINDROOM = 50;
	/** 标识号-玩家点击快速游戏记录 */
	private static final int MARKNUM_FASTGAME = 51;
	/** 标识号-玩家点击在线玩家列表记录 */
	private static final int MARKNUM_GETONLINEPLAYER = 52;
	/** 标识号-持续在线礼包记录 */
	private static final int MARKNUM_LASTONLINE = 53;
	/** 标识号-玩家排行榜查看记录 */
	private static final int MARKNUM_RANKING = 54;
	/** 标识号-玩家爱心许愿记录 */
	private static final int MARKNUM_WISH = 55;
	/** 标识号-玩家秘境探险记录 */
	private static final int MARKNUM_EXPLORE = 56;
	/** 标识号-玩家秘境探险奖励领取记录 */
	private static final int MARKNUM_EXPLOREREWARD = 57;
	/** 标识号-玩家物品回收记录 */
	private static final int MARKNUM_RECOVER = 58;
	/** 标识号-玩家绑定微博帐号 */
	private static final int MARKNUM_BINDWEIBO = 59;
	/** 标识号-玩家发送微博 */
	private static final int MARKNUM_SENDWEIBO = 60;
	/** 标识号-玩家挑战世界BOSS记录 */
	private static final int MARKNUM_DAREWORDBOSS = 61;
	/** 标识号-玩家世界BOSS加速记录 */
	private static final int MARKNUM_SPEEDUPWORDBOSS = 62;
	/** 标识号-世界BOSS被击杀记录 */
	private static final int MARKNUM_KILLWORDBOSS = 63;
	/** 标识号-弹王挑战赛记录 */
	private static final int MARKNUM_KINGBATTLE = 64;
	/** 标识号-副本挑战记录 */
	private static final int MARKNUM_DUPLICATEBATTLE = 65;
	/** 标识号-副本砸蛋记录 */
	private static final int MARKNUM_SMASHEGG = 66;
	/** 标识号-副本加速记录 */
	private static final int MARKNUM_SPEEDUP = 67;
	/** 标识号-排位赛记录 */
	private static final int MARKNUM_QUALIFYING = 68;
	/** 标识号-玩家点击充值界面记录 */
	private static final int MARKNUM_CLICKRECHARGE = 69;
	/** 标识号-徽章获得记录 */
	private static final int MARKNUM_ADDBADGE = 70;
	/** 标识号-徽章消耗记录 */
	private static final int MARKNUM_USEBADGE = 71;
	/** 标识号-公会职位变更记录 */
	private static final int MARKNUM_GUILDPOSTCHANGE = 72;
	/** 标识号-玩家作弊被踢下线 */
	private static final int MARKNUM_BATTLECHEAT = 73;
	/** 标识号-挑战单人副本 */
	private static final int MARKNUM_CHALLENGESINGLEMAP = 74;
	/** 标识号-单人副本挑战成功 */
	private static final int MARKNUM_CHALLENGESUCCESS = 75;
	/** 标识号-扫荡单人副本 */
	private static final int MARKNUM_RAIDSSINGLEMAP = 76;
	/** 标识号-单人副本数据异常 */
	private static final int MARKNUM_SINGLEMAPCHEAT = 77;
	/** 标识号-月卡获得记录数据 */
	private static final int MARKNUM_MONTHCARD = 78;

	/**
	 * 保存日志内容
	 * 
	 * @param markNum
	 *            标识号
	 * @param data
	 *            日志数据内容
	 */
	private static void saveData(int markNum, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(markNum);
		sb.append("\t");
		sb.append(System.currentTimeMillis());
		sb.append("\t");
		sb.append(data);
		log.info(sb.toString());
	}

	/**
	 * 1：创建角色
	 */
	public static void createPlayer(int playerId, String playerName) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(playerName);
		saveData(MARKNUM_CREATE, sb.toString());
	}

	/**
	 * 2：玩家登入
	 * 
	 * @param playerId
	 */
	public static void login(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_LOGIN, sb.toString());
	}

	/**
	 * 3：玩家登出
	 * 
	 * @param playerId
	 * @param onLineTime
	 *            本次上线时长（分钟）
	 */
	public static void logout(int playerId, int level, int onLineTime) {
		StringBuffer sb = new StringBuffer("玩家退出playerId:");
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(onLineTime);
		saveData(MARKNUM_LOGOUT, sb.toString());
	}

	/**
	 * 4：角色充值记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param accountId
	 *            帐号ID
	 * @param clientChannel
	 *            客户端渠道号
	 * @param payChannel
	 *            充值渠道号
	 * @param orderNum
	 *            订单号
	 * @param money
	 *            充值金额
	 * @param stone
	 *            充值钻石数量
	 * @param giftStone
	 *            充值赠送钻石数量
	 * @param remark
	 *            备注
	 */
	public static void recharge(int playerId, int level, int accountId, String clientChannel, String payChannel, String orderNum,
			float money, int stone, int giftStone, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(accountId);
		sb.append("\t");
		sb.append(clientChannel);
		sb.append("\t");
		sb.append(payChannel);
		sb.append("\t");
		sb.append(orderNum);
		sb.append("\t");
		sb.append(money);
		sb.append("\t");
		sb.append(stone);
		sb.append("\t");
		sb.append(giftStone);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_RECHARGE, sb.toString());
	}

	/**
	 * 5：角色获得代币记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param origin
	 *            获得方式代码
	 * @param stone
	 *            获得钻石数量
	 * @param remark
	 *            备注
	 */
	public static void addMoney(int playerId, int level, int origin, int stone, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(stone);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_ADDMONEY, sb.toString());
	}

	/**
	 * 6：角色消耗代币记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param origin
	 *            获得方式代码
	 * @param stone
	 *            获得钻石数量
	 * @param remark
	 *            备注
	 */
	public static void useMoney(int playerId, int level, int origin, int stone, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(stone);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_USEMONEY, sb.toString());
	}

	/**
	 * 7：角色在线数量 记录玩家当前在线数量
	 * 
	 * @param num
	 */
	public static void onlineNum(int num) {
		StringBuffer sb = new StringBuffer();
		sb.append("角色在线数量:");
		sb.append(num);
		saveData(MARKNUM_ONLINENUM, sb.toString());
	}

	/**
	 * 8：角色升级记录 记录角色升级信息
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void upLivel(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level - 1);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_UPLIVEL, sb.toString());
	}

	/**
	 * 9：金币获得记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param origin
	 *            获得方式代码
	 * @param quantity
	 *            获得金币数量
	 * @param remark
	 *            备注
	 */
	public static void addGold(int playerId, int level, String origin, int quantity, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(quantity);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_ADDGOLD, sb.toString());
	}

	/**
	 * 10：金币消耗记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param origin
	 *            消耗方式代码
	 * @param quantity
	 *            获得金币数量
	 * @param remark
	 *            备注
	 */
	public static void useGold(int playerId, int level, String origin, int quantity, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(quantity);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_USEGOLD, sb.toString());
	}

	/**
	 * 11：物品获得记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param itemId
	 *            物品ID
	 * @param type
	 *            物品类型
	 * @param subType
	 *            物品子类型
	 * @param num
	 *            增加数量
	 * @param unit
	 *            单位（1天,2数量）
	 * @param origin
	 *            来源
	 * @param useDiamond
	 *            消耗钻石
	 * @param useGold
	 *            消耗金币
	 * @param useBadge
	 *            消耗金币
	 * @param remark
	 *            备注
	 */
	public static void getItem(int playerId, int level, int itemId, int type, int subType, int num, int unit, int origin, int useDiamond,
			int useGold, int useBadge, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(type);
		sb.append("\t");
		sb.append(subType);
		sb.append("\t");
		sb.append(num);
		sb.append("\t");
		sb.append(unit);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		sb.append("\t");
		sb.append(useBadge);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_GETITEM, sb.toString());
	}

	/**
	 * 12：聊天记录
	 * 
	 * @param sendId
	 *            发送ID
	 * @param sendLevel
	 *            发送角色等级
	 * @param receiveId
	 *            接收ID
	 * @param receiveLevel
	 *            接收角色等级
	 * @param channel
	 *            频道
	 * @param interfaceId
	 *            界面UID
	 * @param content
	 *            聊天内容
	 */
	public static void chart(int sendId, int sendLevel, int receiveId, int receiveLevel, String channel, int interfaceId, String content) {
		StringBuffer sb = new StringBuffer();
		sb.append(sendId);
		sb.append("\t");
		sb.append(sendLevel);
		sb.append("\t");
		sb.append(receiveId);
		sb.append("\t");
		sb.append(receiveLevel);
		sb.append("\t");
		sb.append(channel);
		sb.append("\t");
		sb.append(interfaceId);
		sb.append("\t");
		sb.append(content);
		saveData(MARKNUM_CHART, sb.toString());
	}

	/**
	 * 13：界面停留记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param interfaceId
	 *            界面ID
	 * @param time
	 *            停留时间
	 */
	public static void remainInterface(int playerId, int level, int interfaceId, long time) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(interfaceId);
		sb.append("\t");
		sb.append(time);
		saveData(MARKNUM_INTERFACE, sb.toString());
	}

	/**
	 * 14：任务相关记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param taskId
	 *            任务ID
	 * @param taskType
	 *            任务类型
	 * @param status
	 *            状态
	 */
	public static void taskStatusChange(int playerId, int level, int taskId, int taskType, int status) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(taskId);
		sb.append("\t");
		sb.append(taskType);
		sb.append("\t");
		sb.append(status);
		saveData(MARKNUM_TASKCHANGE, sb.toString());
	}

	/**
	 * 15：任务奖励提升记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param taskId
	 *            任务ID
	 * @param useDiamond
	 *            消耗钻石
	 */
	public static void taskRewardChange(int playerId, int level, int taskId, int useDiamond) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(taskId);
		sb.append("\t");
		sb.append(useDiamond);
		saveData(MARKNUM_TASKREWARDCHANGE, sb.toString());
	}

	/**
	 * 16：发送邮件记录
	 * 
	 * @param sendId
	 *            发送ID
	 * @param sendLevel
	 *            发送角色等级
	 * @param receiveId
	 *            接收ID
	 * @param receiveLevel
	 *            接收角色等级
	 * @param content
	 *            邮件内容
	 */
	public static void sendMail(int sendId, int sendLevel, int receiveId, int receiveLevel, String content) {
		StringBuffer sb = new StringBuffer();
		sb.append(sendId);
		sb.append("\t");
		sb.append(sendLevel);
		sb.append("\t");
		sb.append(receiveId);
		sb.append("\t");
		sb.append(receiveLevel);
		sb.append("\t");
		sb.append(content);
		saveData(MARKNUM_SENDMAIL, sb.toString());
	}

	/**
	 * 17：删除邮件记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param mailId
	 *            邮件ID
	 * @param sORr
	 *            收件/发件(1收件,2发件)
	 */
	public static void deleteMail(int playerId, int level, int mailId, int sORr) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(mailId);
		sb.append("\t");
		sb.append(sORr);
		saveData(MARKNUM_DELETEMAIL, sb.toString());
	}

	/**
	 * 18：好友相关记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param aORr
	 *            1增加/2删除
	 * @param fORb
	 *            1好友/2黑名单
	 * @param friendId
	 *            好友ID
	 */
	public static void friend(int playerId, int level, int aORr, int fORb, int friendId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(aORr);
		sb.append("\t");
		sb.append(fORb);
		sb.append("\t");
		sb.append(friendId);
		saveData(MARKNUM_FRIEND, sb.toString());
	}

	/**
	 * 19：玩家点击附近好友列表记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void getNearbyFriendList(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_GETNEARBYFRIENDLIST, sb.toString());
	}

	/**
	 * 20：附近好友相关记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param aORr
	 *            1增加/2删除
	 * @param friendId
	 *            好友ID
	 */
	public static void nearbyFriend(int playerId, int level, int aORr, int friendId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(aORr);
		sb.append("\t");
		sb.append(friendId);
		saveData(MARKNUM_NEARBYFRIEND, sb.toString());
	}

	/**
	 * 21：VIP开通记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param vipLevel
	 *            VIP等级
	 * @param oORt
	 *            正式/临时
	 * @param days
	 *            开通天数
	 * @param remark
	 *            备注
	 */
	public static void openVIP(int playerId, int level, int vipLevel, int oORt, int days, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(vipLevel);
		sb.append("\t");
		sb.append(oORt);
		sb.append("\t");
		sb.append(days);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_OPENVIP, sb.toString());
	}

	/**
	 * 22：VIP礼包领取记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param vipLevel
	 *            VIP等级
	 * @param packsId
	 *            礼包ID
	 */
	public static void getVIPReward(int playerId, int level, int vipLevel, int packsId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(vipLevel);
		sb.append("\t");
		sb.append(packsId);
		saveData(MARKNUM_GETVIPREWARD, sb.toString());
	}

	/**
	 * 23：获得活跃度相关记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param taskId
	 *            任务ID
	 * @param value
	 *            增加值
	 * @param result
	 *            增加后的值
	 */
	public static void addActivity(int playerId, int level, int taskId, int value, int result) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(taskId);
		sb.append("\t");
		sb.append(value);
		sb.append("\t");
		sb.append(result);
		saveData(MARKNUM_ADDACTIVITY, sb.toString());
	}

	/**
	 * 24：活跃度奖励领取记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param progress
	 *            领取进度
	 * @param itemIds
	 *            获得物品
	 */
	public static void activityReward(int playerId, int level, int progress, String itemIds) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(progress);
		sb.append("\t");
		sb.append(itemIds);
		saveData(MARKNUM_ACTIVITYREWARD, sb.toString());
	}

	/**
	 * 25：签到相关记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param continuousDays
	 *            连续签到数
	 * @param totalDays
	 *            累计签到数
	 * @param items
	 *            获得物品
	 */
	public static void regis(int playerId, int level, int continuousDays, int totalDays, String items) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(continuousDays);
		sb.append("\t");
		sb.append(totalDays);
		sb.append("\t");
		sb.append(items);
		saveData(MARKNUM_REGIS, sb.toString());
	}

	/**
	 * 26：成就相关记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param titleId
	 *            达成成就ID
	 */
	public static void title(int playerId, int level, int titleId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(titleId);
		saveData(MARKNUM_TITLE, sb.toString());
	}

	/**
	 * 27：玩家查看公会记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param guildId
	 *            公会ID
	 * @param guildLevel
	 *            公会等级
	 * @param guildPrestige
	 *            公会威望排名
	 * @param guildRank
	 *            公会战况排名
	 */
	public static void checkGuild(int playerId, int level, int guildId, int guildLevel, int guildPrestige, int guildRank) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(guildId);
		sb.append("\t");
		sb.append(guildLevel);
		sb.append("\t");
		sb.append(guildPrestige);
		sb.append("\t");
		sb.append(guildRank);
		saveData(MARKNUM_CHECKGUILD, sb.toString());
	}

	/**
	 * 28：玩家申请加入公会记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param guildId
	 *            公会ID
	 * @param guildLevel
	 *            公会等级
	 * @param guildPrestige
	 *            公会威望排名
	 * @param guildRank
	 *            公会战况排名
	 */
	public static void applyGuild(int playerId, int level, int guildId, int guildLevel, int guildPrestige, int guildRank) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(guildId);
		sb.append("\t");
		sb.append(guildLevel);
		sb.append("\t");
		sb.append(guildPrestige);
		sb.append("\t");
		sb.append(guildRank);
		saveData(MARKNUM_APPLYGUILD, sb.toString());
	}

	/**
	 * 29：公会技能使用记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param guildId
	 *            公会ID
	 * @param guildLevel
	 *            公会等级
	 * @param skillId
	 *            技能ID
	 * @param useDiamond
	 *            消耗钻石
	 * @param useGold
	 *            消耗金币
	 * @param useBadge
	 *            消耗贡献度
	 */
	public static void guildSkill(int playerId, int level, int guildId, int guildLevel, int skillId, int useDiamond, int useGold,
			int useBadge) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(guildId);
		sb.append("\t");
		sb.append(guildLevel);
		sb.append("\t");
		sb.append(skillId);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		sb.append("\t");
		sb.append(useBadge);
		saveData(MARKNUM_GUILDSKILL, sb.toString());
	}

	/**
	 * 30：公会人员变更记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param guildId
	 *            公会ID
	 * @param guildLevel
	 *            公会等级
	 * @param cROiORbORd
	 *            1创建公会/2入会/3退会/4解散公会
	 * @param executorId
	 *            操作人Id
	 * @param executorlevel
	 *            操作人等级
	 * @param executorPost
	 *            操作人职位
	 */
	public static void guildChange(int playerId, int level, int guildId, int guildLevel, int cROiORbORd, int executorId, int executorlevel,
			int executorPost) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(guildId);
		sb.append("\t");
		sb.append(guildLevel);
		sb.append("\t");
		sb.append(cROiORbORd);
		sb.append("\t");
		sb.append(executorId);
		sb.append("\t");
		sb.append(executorlevel);
		sb.append("\t");
		sb.append(executorPost);
		saveData(MARKNUM_GUILDCHANGE, sb.toString());
	}

	/**
	 * 31：宠物驯服记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param petId
	 *            宠物ID
	 * @param origin
	 *            来源(1.购买,2.GM发放,3.10级发放)
	 * @param useDiamond
	 *            消耗钻石
	 * @param useGold
	 *            消耗金币
	 * @param useBadge
	 *            消耗徽章
	 */
	public static void addPet(int playerId, int level, int petId, int origin, int useDiamond, int useGold, int useBadge) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(petId);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		sb.append("\t");
		sb.append(useBadge);
		saveData(MARKNUM_ADDPET, sb.toString());
	}

	/**
	 * 32：宠物出战记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param playId
	 *            出战ID
	 * @param restId
	 *            休息ID
	 */
	public static void playPet(int playerId, int level, int playId, int restId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(playId);
		sb.append("\t");
		sb.append(restId);
		saveData(MARKNUM_PLAYPET, sb.toString());
	}

	/**
	 * 33：宠物训练记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param petId
	 *            宠物ID
	 * @param isUpLevel
	 *            是否升级
	 * @param useDiamond
	 *            消耗钻石
	 * @param useGold
	 *            消耗金币
	 * @param useBadge
	 *            消耗徽章
	 */
	public static void trainPet(int playerId, int level, int petId, boolean isUpLevel, int useDiamond, int useGold, int useBadge) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(petId);
		sb.append("\t");
		sb.append(isUpLevel);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		sb.append("\t");
		sb.append(useBadge);
		saveData(MARKNUM_TRAINPET, sb.toString());
	}

	/**
	 * 34：宠物传承记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param petId1
	 *            传承宠物ID
	 * @param petLevel1
	 *            传承宠物ID
	 * @param petId2
	 *            接受传承宠物ID
	 * @param petLevel2
	 *            接受传承宠物等级
	 * @param useDiamond
	 *            消耗钻石
	 * @param useGold
	 *            消耗金币
	 * @param useBadge
	 *            消耗徽章
	 */
	public static void inheritPet(int playerId, int level, int petId1, int petLevel1, int petId2, int petLevel2, int useDiamond,
			int useGold, int useBadge) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(petId1);
		sb.append("\t");
		sb.append(petLevel1);
		sb.append("\t");
		sb.append(petId2);
		sb.append("\t");
		sb.append(petLevel2);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		sb.append("\t");
		sb.append(useBadge);
		saveData(MARKNUM_INHERITPET, sb.toString());
	}

	/**
	 * 35：求婚记录
	 * 
	 * @param pId
	 *            求婚人ID
	 * @param pSex
	 *            求婚人性别
	 * @param pLevel
	 *            求婚人等级
	 * @param bpId
	 *            被求婚人ID
	 * @param bpSex
	 *            被求婚人性别
	 * @param bpLevel
	 *            被求婚人等级
	 * @param itemId
	 *            消耗道具ID
	 */
	public static void propose(int pId, int pSex, int pLevel, int bpId, int bpSex, int bpLevel, int itemId) {
		StringBuffer sb = new StringBuffer();
		sb.append(pId);
		sb.append("\t");
		sb.append(pSex);
		sb.append("\t");
		sb.append(pLevel);
		sb.append("\t");
		sb.append(bpId);
		sb.append("\t");
		sb.append(bpSex);
		sb.append("\t");
		sb.append(bpLevel);
		sb.append("\t");
		sb.append(itemId);
		saveData(MARKNUM_PROPOSE, sb.toString());
	}

	/**
	 * 36：结婚记录
	 * 
	 * @param bgId
	 *            新郎ID
	 * @param bgLevel
	 *            新郎等级
	 * @param bId
	 *            新娘ID
	 * @param bLevel
	 *            新娘等级
	 * @param marryType
	 *            婚礼类型
	 * @param useDiamond
	 *            消耗钻石
	 * @param bgGifts
	 *            新郎发放红包数（钻石）
	 * @param bGifts
	 *            新娘发放红包数（钻石）
	 * @param bgOnline
	 *            新郎结婚礼堂在线时长
	 * @param bOnline
	 *            新娘结婚礼堂在线时长
	 */
	public static void marry(int bgId, int bgLevel, int bId, int bLevel, int marryType, int useDiamond, int bgGifts, int bGifts,
			long bgOnline, long bOnline) {
		StringBuffer sb = new StringBuffer();
		sb.append(bgId);
		sb.append("\t");
		sb.append(bgLevel);
		sb.append("\t");
		sb.append(bId);
		sb.append("\t");
		sb.append(bLevel);
		sb.append("\t");
		sb.append(marryType);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(bgGifts);
		sb.append("\t");
		sb.append(bGifts);
		sb.append("\t");
		sb.append(bgOnline);
		sb.append("\t");
		sb.append(bOnline);
		saveData(MARKNUM_MARRY, sb.toString());
	}

	/**
	 * 37：离婚记录
	 * 
	 * @param type
	 *            1解除订婚/2离婚
	 * @param mId
	 *            男方ID
	 * @param mLevel
	 *            男方等级
	 * @param wId
	 *            女方ID
	 * @param wLevel
	 *            女方等级
	 * @param iId
	 *            主动离婚人ID
	 * @param useDiamond
	 *            离婚消耗钻石数
	 */
	public static void divorce(int type, int mId, int mLevel, int wId, int wLevel, int iId, int useDiamond) {
		StringBuffer sb = new StringBuffer();
		sb.append(mId);
		sb.append("\t");
		sb.append(mLevel);
		sb.append("\t");
		sb.append(wId);
		sb.append("\t");
		sb.append(wLevel);
		sb.append("\t");
		sb.append(iId);
		sb.append("\t");
		sb.append(useDiamond);
		saveData(MARKNUM_DIVORCE, sb.toString());
	}

	/**
	 * 38：结婚转钻记录
	 * 
	 * @param outId
	 *            转出人ID
	 * @param outSex
	 *            转出人性别
	 * @param outLevel
	 *            转出人等级
	 * @param inId
	 *            转入人ID
	 * @param inSex
	 *            转入人性别
	 * @param inLevel
	 *            转入人等级
	 * @param useDiamond
	 *            转移钻石数量
	 */
	public static void shiftMoney(int outId, int outSex, int outLevel, int inId, int inSex, int inLevel, int useDiamond) {
		StringBuffer sb = new StringBuffer();
		sb.append(outId);
		sb.append("\t");
		sb.append(outSex);
		sb.append("\t");
		sb.append(outLevel);
		sb.append("\t");
		sb.append(inId);
		sb.append("\t");
		sb.append(inSex);
		sb.append("\t");
		sb.append(inLevel);
		sb.append("\t");
		sb.append(useDiamond);
		saveData(MARKNUM_SHIFTMONEY, sb.toString());
	}

	/**
	 * 39：玩家转生记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param bLevel
	 *            转生前等级
	 * @param aLevel
	 *            转生前等级
	 * @param isPerfect
	 *            是否完美转生
	 * @param itemId
	 *            消耗物品ID
	 * @param useDiamond
	 *            消耗钻石
	 */
	public static void rebirth(int playerId, int bLevel, int aLevel, boolean isPerfect, int itemId, int useDiamond) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(bLevel);
		sb.append("\t");
		sb.append(aLevel);
		sb.append("\t");
		sb.append(isPerfect);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(useDiamond);
		saveData(MARKNUM_REBIRTH, sb.toString());
	}

	/**
	 * 40：成长基金购买记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param fundType
	 *            购买基金类型
	 * @param fundPrice
	 *            购买基金价格
	 */
	public static void buyFund(int playerId, int level, int fundType, int fundPrice) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(fundType);
		sb.append("\t");
		sb.append(fundPrice);
		saveData(MARKNUM_BUYFUND, sb.toString());
	}

	/**
	 * 41：成长基金领取记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param reward
	 *            领取基金数额
	 */
	public static void getFund(int playerId, int level, int reward) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(reward);
		saveData(MARKNUM_GETFUND, sb.toString());
	}

	/**
	 * 42：玩家邀请好友奖励领取记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param rewardId
	 *            奖励ID
	 * @param rewardItems
	 *            奖励脚本
	 */
	public static void inviteReward(int playerId, int level, int rewardId, String rewardItems) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(rewardId);
		sb.append("\t");
		sb.append(rewardItems);
		saveData(MARKNUM_INVITEREWARD, sb.toString());
	}

	/**
	 * 43：玩家镶嵌记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param itemId
	 *            物品ID
	 * @param gemId
	 *            宝石ID
	 * @param gemType
	 *            宝石类型
	 * @param useDiamond
	 *            消耗钻石数
	 * @param useGold
	 *            消耗金币数
	 */
	public static void mosaic(int playerId, int level, int itemId, int gemId, int gemType, int useDiamond, int useGold) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(gemId);
		sb.append("\t");
		sb.append(gemType);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		saveData(MARKNUM_MOSAIC, sb.toString());
	}

	/**
	 * 44：玩家升星记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param itemId
	 *            物品ID
	 * @param isSuccess
	 *            是否升星成功
	 * @param star
	 *            物品升星后星级
	 * @param useItem
	 *            消耗物品ID
	 * @param useDiamond
	 *            消耗钻石数
	 * @param useGold
	 *            消耗金币数
	 */
	public static void upStar(int playerId, int level, int itemId, boolean isSuccess, int star, String useItem, int useDiamond, int useGold) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(isSuccess);
		sb.append("\t");
		sb.append(star);
		sb.append("\t");
		sb.append(useItem);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		saveData(MARKNUM_UPSTAR, sb.toString());
	}

	/**
	 * 45：玩家重铸记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param itemId
	 *            物品ID
	 * @param bSkill
	 *            重铸前武器被动技能
	 * @param aSkill
	 *            重铸后武器被动技能
	 * @param useItem
	 *            消耗物品ID
	 * @param useDiamond
	 *            消耗钻石数
	 * @param useGold
	 *            消耗金币数
	 */
	public static void rehab(int playerId, int level, int itemId, String bSkill, String aSkill, int useItem, int useDiamond, int useGold) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(bSkill);
		sb.append("\t");
		sb.append(aSkill);
		sb.append("\t");
		sb.append(useItem);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		saveData(MARKNUM_REHAB, sb.toString());
	}

	/**
	 * 46：玩家转移记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param outItemId
	 *            转出的物品ID
	 * @param inItemId
	 *            转入的物品ID
	 * @param sLevel
	 *            转移的强化等级
	 * @param sSkill
	 *            转移的武器技能
	 * @param sStar
	 *            转移的星级
	 * @param sGem
	 *            转移的宝石
	 * @param useDiamond
	 *            消耗的钻石数
	 * @param useGold
	 *            消耗的钻石数
	 */
	public static void shift(int playerId, int level, int outItemId, int inItemId, int sLevel, String sSkill, int sStar, String sGem,
			int useDiamond, int useGold) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(outItemId);
		sb.append("\t");
		sb.append(inItemId);
		sb.append("\t");
		sb.append(sLevel);
		sb.append("\t");
		sb.append(sSkill);
		sb.append("\t");
		sb.append(sStar);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		saveData(MARKNUM_SHIFT, sb.toString());
	}

	/**
	 * 47：玩家强化记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param itemId
	 *            装备ID
	 * @param isSuccess
	 *            强化是否成功
	 * @param sLevel
	 *            强化后等级
	 * @param useItem
	 *            消耗的物品
	 * @param useDiamond
	 *            消耗钻石数
	 * @param useGold
	 *            消耗金币数
	 */
	public static void strengthen(int playerId, int level, int itemId, boolean isSuccess, int sLevel, String useItem, int useDiamond,
			int useGold) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(isSuccess);
		sb.append("\t");
		sb.append(sLevel);
		sb.append("\t");
		sb.append(useItem);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		saveData(MARKNUM_STRENGTHEN, sb.toString());
	}

	/**
	 * 48：玩家对战记录
	 * 
	 * @param playerBattleInfo
	 *            玩家ID*等级*获得经验*阵营*回合数*是否强退*是否掉线*伤害输出*是否自杀*射击次数*命中次数(多个玩家用‘，’号分割)
	 * @param mapId
	 *            地图ID
	 * @param battleType
	 *            竞技类型
	 * @param startMode
	 *            匹配模式
	 * @param playerNum
	 *            玩家数量
	 * @param totalRound
	 *            回合数
	 * @param useTools
	 *            使用道具
	 */
	public static void battle(String playerBattleInfo, int mapId, int battleType, int startMode, int playerNum, int totalRound,
			String useTools) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerBattleInfo);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(battleType);
		sb.append("\t");
		sb.append(startMode);
		sb.append("\t");
		sb.append(playerNum);
		sb.append("\t");
		sb.append(totalRound);
		sb.append("\t");
		sb.append(useTools);
		saveData(MARKNUM_BATTLE, sb.toString());
	}

	/**
	 * 49：玩家点击开始游戏记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void startGame(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_STARTGAME, sb.toString());
	}

	/**
	 * 50：玩家点击查找房间记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void findRoom(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_FINDROOM, sb.toString());
	}

	/**
	 * 51：玩家点击快速游戏记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void fastGame(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_FASTGAME, sb.toString());
	}

	/**
	 * 52：玩家点击在线玩家列表记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void getOnlinePlayer(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_GETONLINEPLAYER, sb.toString());
	}

	/**
	 * 53：持续在线礼包记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param progress
	 *            礼包为当日第几个
	 */
	public static void lastOnline(int playerId, int level, int progress) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(progress);
		saveData(MARKNUM_LASTONLINE, sb.toString());
	}

	/**
	 * 54：玩家排行榜查看记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param rank
	 *            查看的榜单
	 */
	public static void ranking(int playerId, int level, int rank) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(rank);
		saveData(MARKNUM_RANKING, sb.toString());
	}

	/**
	 * 55：玩家爱心许愿记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param itemId
	 *            获得物品
	 * @param count
	 *            获得数量
	 * @param unit
	 *            单位(1天数，2数量)
	 * @param rebates
	 *            累计可返利钻石数
	 */
	public static void wish(int playerId, int level, int itemId, int count, int unit, int rebates) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(count);
		sb.append("\t");
		sb.append(unit);
		sb.append("\t");
		sb.append(rebates);
		saveData(MARKNUM_WISH, sb.toString());
	}

	/**
	 * 56：玩家秘境探险记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param exploreType
	 *            探险类型
	 * @param isSuccess
	 *            是否成功加星
	 * @param star
	 *            当前星级
	 * @param useDiamond
	 *            消耗钻石
	 * @param useGold
	 *            消耗金币
	 * @param useBadge
	 *            消耗徽章
	 */
	public static void explore(int playerId, int level, int exploreType, boolean isSuccess, int star, int useDiamond, int useGold,
			int useBadge) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(exploreType);
		sb.append("\t");
		sb.append(isSuccess);
		sb.append("\t");
		sb.append(star);
		sb.append("\t");
		sb.append(useDiamond);
		sb.append("\t");
		sb.append(useGold);
		sb.append("\t");
		sb.append(useBadge);
		saveData(MARKNUM_EXPLORE, sb.toString());
	}

	/**
	 * 57：玩家秘境探险奖励领取记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param exploreType
	 *            探险类型
	 * @param star
	 *            当前星级
	 * @param addItrms
	 *            获得物品
	 */
	public static void exploreReward(int playerId, int level, int exploreType, int star, String addItrms) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(exploreType);
		sb.append("\t");
		sb.append(star);
		sb.append("\t");
		sb.append(addItrms);
		saveData(MARKNUM_EXPLOREREWARD, sb.toString());
	}

	/**
	 * 58：玩家物品回收记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param itemId
	 *            回收物品
	 * @param count
	 *            回收数量
	 * @param addGold
	 *            获得金币数
	 */
	public static void recover(int playerId, int level, int itemId, int count, int addGold) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(count);
		sb.append("\t");
		sb.append(addGold);
		saveData(MARKNUM_RECOVER, sb.toString());
	}

	/**
	 * 59：玩家绑定微博帐号
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void bindWeibo(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_BINDWEIBO, sb.toString());
	}

	/**
	 * 60：玩家发送微博
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param interfaceId
	 *            玩家当前所在界面
	 */
	public static void sendWeibo(int playerId, int level, int interfaceId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(interfaceId);
		saveData(MARKNUM_SENDWEIBO, sb.toString());
	}

	/**
	 * 61：玩家挑战世界BOSS记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param round
	 *            回合数
	 * @param dps
	 *            伤害输出
	 * @param useTools
	 *            使用道具
	 */
	public static void dareWordBoss(int playerId, int level, int round, int dps, String useTools) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(round);
		sb.append("\t");
		sb.append(dps);
		sb.append("\t");
		sb.append(useTools);
		saveData(MARKNUM_DAREWORDBOSS, sb.toString());
	}

	/**
	 * 62：玩家世界BOSS加速记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param useDiamond
	 *            消耗钻石
	 */
	public static void speedUpWordBoss(int playerId, int level, int useDiamond) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(useDiamond);
		saveData(MARKNUM_SPEEDUPWORDBOSS, sb.toString());
	}

	/**
	 * 63：世界BOSS被击杀记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 */
	public static void killWordBoss(int playerId, int level) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		saveData(MARKNUM_KILLWORDBOSS, sb.toString());
	}

	/**
	 * 64：弹王挑战赛记录
	 * 
	 * @param playerBattleInfo
	 *            ID*等级*获得经验*获得积分*阵营*回合数*是否强退*是否掉线*伤害输出*是否自杀*射击次数*命中次数(多个玩家用‘，’
	 *            号分割)
	 * @param mapId
	 *            比赛地图ID
	 * @param round
	 *            回合数
	 * @param useTools
	 *            使用道具
	 */
	public static void kingBattle(String playerBattleInfo, int mapId, int round, String useTools) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerBattleInfo);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(round);
		sb.append("\t");
		sb.append(useTools);
		saveData(MARKNUM_KINGBATTLE, sb.toString());
	}

	/**
	 * 65：副本挑战记录
	 * 
	 * @param playerBattleInfo
	 *            玩家ID*等级*获得经验*阵营*回合数*是否强退*是否掉线*伤害输出*是否自杀*射击次数*命中次数
	 * @param mapId
	 *            地图ID
	 * @param difficulty
	 *            挑战难度
	 * @param round
	 *            回合数
	 * @param bossHP
	 *            BOSS剩余血量
	 * @param isSuccess
	 *            是否通关
	 * @param useTools
	 *            使用道具
	 */
	public static void duplicateBattle(String playerBattleInfo, int mapId, int difficulty, int round, int bossHP, boolean isSuccess,
			String useTools) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerBattleInfo);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(difficulty);
		sb.append("\t");
		sb.append(round);
		sb.append("\t");
		sb.append(bossHP);
		sb.append("\t");
		sb.append(isSuccess);
		sb.append("\t");
		sb.append(useTools);
		saveData(MARKNUM_DUPLICATEBATTLE, sb.toString());
	}

	/**
	 * 66：副本砸蛋记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param mapId
	 *            地图ID
	 * @param itemId
	 *            获得物品
	 * @param useDiamond
	 *            消耗钻石
	 */
	public static void smashEgg(int playerId, int level, int mapId, int itemId, int useDiamond) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(itemId);
		sb.append("\t");
		sb.append(useDiamond);
		saveData(MARKNUM_SMASHEGG, sb.toString());
	}

	/**
	 * 67：副本加速记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param mapId
	 *            地图ID
	 * @param useDiamond
	 *            消耗钻石
	 */
	public static void speedUp(int playerId, int level, int mapId, int useDiamond) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(useDiamond);
		saveData(MARKNUM_SPEEDUP, sb.toString());
	}

	/**
	 * 68：排位赛记录
	 * 
	 * @param playerBattleInfo
	 *            玩家ID*等级*获得积分*阵营*回合数*是否强退*是否掉线*伤害输出*是否自杀*射击次数*命中次数
	 * @param mapId
	 *            地图ID
	 * @param round
	 *            回合数
	 * @param useTools
	 *            使用道具
	 */
	public static void qualifying(String playerBattleInfo, int mapId, int round, String useTools) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerBattleInfo);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(round);
		sb.append("\t");
		sb.append(useTools);
		saveData(MARKNUM_QUALIFYING, sb.toString());
	}

	/**
	 * 69：玩家点击充值界面记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param interfaceId
	 *            入口界面ID
	 */
	public static void clickRecharge(int playerId, int level, int interfaceId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(interfaceId);
		saveData(MARKNUM_CLICKRECHARGE, sb.toString());
	}

	/**
	 * 70：徽章获得记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param origin
	 *            获得方式
	 * @param quantity
	 *            获得徽章数量
	 * @param remark
	 *            备注
	 */
	// TODO 没有统一的徽章获得接口需后期统一接口后进行记录
	public static void addBadge(int playerId, int level, String origin, int quantity, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(quantity);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_ADDBADGE, sb.toString());
	}

	/**
	 * 71：徽章消耗记录
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param origin
	 *            获得方式
	 * @param quantity
	 *            获得徽章数量
	 * @param remark
	 *            备注
	 */
	// TODO 没有统一的徽章消耗接口需后期统一接口后进行记录
	public static void useBadge(int playerId, int level, String origin, int quantity, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(quantity);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_USEBADGE, sb.toString());
	}

	/**
	 * 72：公会职位变更
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param guildId
	 *            公会ID
	 * @param guildLevel
	 *            公会等级
	 * @param post1
	 *            原职位
	 * @param post2
	 *            变更后职位
	 * @param executorId
	 *            操作角色Id
	 * @param executorlevel
	 *            操作角色等级
	 * @param executorPost
	 *            操作角色职位
	 */
	public static void guildPostChange(int playerId, int level, int guildId, int guildLevel, int post1, int post2, int executorId,
			int executorlevel, int executorPost) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(guildId);
		sb.append("\t");
		sb.append(guildLevel);
		sb.append("\t");
		sb.append(post1);
		sb.append("\t");
		sb.append(post2);
		sb.append("\t");
		sb.append(executorId);
		sb.append("\t");
		sb.append(executorlevel);
		sb.append("\t");
		sb.append(executorPost);
		saveData(MARKNUM_GUILDPOSTCHANGE, sb.toString());
	}

	/**
	 * 73：玩家作弊被踢下线
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param mapId
	 *            地图ID
	 * @param battleMode
	 *            战斗模式
	 * @param attackType
	 *            攻击类型
	 * @param failType
	 *            验证失败类型
	 * @param remark
	 *            备注
	 */
	public static void battleCheat(int playerId, int level, int mapId, int battleMode, int attackType, int failType, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(battleMode);
		sb.append("\t");
		sb.append(attackType);
		sb.append("\t");
		sb.append(failType);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_BATTLECHEAT, sb.toString());
	}

	/**
	 * 74: 挑战单人副本
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param isVIP
	 *            玩家是否是VIP
	 * @param mapId
	 *            挑战关卡ID
	 */
	public static void challengeSingleMap(int playerId, int level, boolean isVIP, int mapId) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(isVIP);
		sb.append("\t");
		sb.append(mapId);
		saveData(MARKNUM_CHALLENGESINGLEMAP, sb.toString());
	}

	/**
	 * 75: 单人副本挑战成功
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param isVIP
	 *            玩家是否是VIP
	 * @param mapId
	 *            挑战关卡ID
	 * @param addDiamond
	 *            抽取钻石数量
	 * @param addGold
	 *            抽取金币数量
	 * @param addBadge
	 *            抽取徽章数量
	 * @param useTools
	 *            使用道具
	 */
	public static void challengeSuccess(int playerId, int level, boolean isVIP, int mapId, int addDiamond, int addGold, int addBadge,
			String useTools) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(isVIP);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(addDiamond);
		sb.append("\t");
		sb.append(addGold);
		sb.append("\t");
		sb.append(addBadge);
		sb.append("\t");
		sb.append(useTools);
		saveData(MARKNUM_CHALLENGESUCCESS, sb.toString());
	}

	/**
	 * 76: 扫荡单人副本
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param isVIP
	 *            玩家是否是VIP
	 * @param mapId
	 *            挑战关卡ID
	 * @param addDiamond
	 *            抽取钻石数量
	 * @param addGold
	 *            抽取金币数量
	 * @param addBadge
	 *            抽取徽章数量
	 */
	public static void raidsSingleMap(int playerId, int level, boolean isVIP, int mapId, int addDiamond, int addGold, int addBadge) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(isVIP);
		sb.append("\t");
		sb.append(mapId);
		sb.append("\t");
		sb.append(addDiamond);
		sb.append("\t");
		sb.append(addGold);
		sb.append("\t");
		sb.append(addBadge);
		saveData(MARKNUM_RAIDSSINGLEMAP, sb.toString());
	}

	/**
	 * 77：单人副本数据异常
	 * 
	 * @param playerId
	 *            角色ID
	 * @param level
	 *            角色等级
	 * @param remark
	 *            异常信息
	 */
	public static void singleMapCheat(int playerId, int level, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(level);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_SINGLEMAPCHEAT, sb.toString());
	}

	/**
	 * 78：月卡获得记录数据
	 * 
	 * @param playerId
	 *            角色ID
	 * @param cardId
	 *            月卡id
	 * @param origin
	 *            获得途径 1充值获得 2.GM工具获得
	 * @param orderNum
	 *            订单号
	 * @param remark
	 *            异常信息
	 */
	public static void addMonthCardData(int playerId, int cardId, int origin, String orderNum, String remark) {
		StringBuffer sb = new StringBuffer();
		sb.append(playerId);
		sb.append("\t");
		sb.append(cardId);
		sb.append("\t");
		sb.append(origin);
		sb.append("\t");
		sb.append(orderNum);
		sb.append("\t");
		sb.append(remark);
		saveData(MARKNUM_MONTHCARD, sb.toString());
	}
}
