package com.app.empire.world.service.base.impl;

/**
 * 充值订单检测服务（未使用）
 * 
 * @author Administrator
 */
public class TradeService {
	/**
	 * 爱心许愿
	 */
	public static final int ORIGIN_LOVE = -6;
	/**
	 * 抽奖获得
	 */
	public static final int ORIGIN_DRAW = -5;
	/**
	 * 礼包获得
	 */
	public static final int ORIGIN_GIFT = -4;
	/**
	 * 爱心返利
	 */
	public static final int ORIGIN_LOVEGET = -3;
	/**
	 * 结婚获得
	 */
	public static final int ORIGIN_MARRYGET = -2;
	/**
	 * gm给予
	 */
	public static final int ORIGIN_GM = -1;
	/**
	 * 任务获得
	 */
	public static final int ORIGIN_TASK = 0;
	/**
	 * 充值获得
	 */
	public static final int ORIGIN_RECH = 1;
	/**
	 * 对战获得
	 */
	public static final int ORIGIN_BATT = 2;
	/**
	 * 购买物品消耗
	 */
	public static final int ORIGIN_BUY = 3;
	/**
	 * 使用公会技能消耗
	 */
	public static final int ORIGIN_KILL = 4;

	/**
	 * 砸蛋消耗
	 */
	public static final int ORIGIN_EGGS = 6;
	/**
	 * 公会升级
	 */
	public static final int ORIGIN_COMMUNITYUP = 7;
	/**
	 * 技能使用
	 */
	public static final int ORIGIN_SKILLUSE = 8;
	/**
	 * 兑换
	 */
	public static final int ORIGIN_EXCHANGE = 9;
	/**
	 * 兑换刷新
	 */
	public static final int ORIGIN_EXCHANGEUPDATE = 10;
	/**
	 * 结婚
	 */
	public static final int ORIGIN_MARRY = 11;
	/**
	 * 解除订婚
	 */
	public static final int ORIGIN_DH = 12;
	/**
	 * 解除结婚
	 */
	public static final int ORIGIN_JH = 13;
	/**
	 * 结婚赠送
	 */
	public static final int ORIGIN_MARRYGIVE = 14;
	/**
	 * tapjoy获得
	 */
	public static final int ORIGIN_TAPJOY = 15;
	/**
	 * 购买基金
	 */
	public static final int ORIGIN_FUND_BUY = 16;
	/**
	 * 领取基金
	 */
	public static final int ORIGIN_FUND_RECEIVE = 17;
	/**
	 * 抽奖获得
	 */
	public static final int ORIGIN_LOTTERY = 18;
	/**
	 * 首冲奖励获得
	 */
	public static final int ORIGIN_RECHARGE_RWARD = 19;
	/**
	 * 签到获得
	 */
	public static final int ORIGIN_SIGN = 20;
	/**
	 * 邀请玩家获得
	 */
	public static final int ORIGIN_INVITE = 21;
	/**
	 * 装备属性转化
	 */
	public static final int ORIGIN_CHANGE_ATTRIBUTE = 22;
	/**
	 * 装备镶嵌拆卸
	 */
	public static final int ORIGIN_GET_OFF = 23;
	/**
	 * 转生消耗
	 */
	public static final int ORIGIN_REBIRTH = 24;
	/**
	 * 外部抽奖获得
	 */
	public static final int ORIGIN_OUTLOTTERY = 25;
	/**
	 * 外放包购买
	 */
	public static final int ORIGIN_SHORTMAIL = 26;
	/**
	 * 抽奖使用
	 */
	public static final int ORIGIN_DRAWUSED = 27;
	/**
	 * 抽奖刷新使用
	 */
	public static final int ORIGIN_DRAWREF = 28;
	/**
	 * 促销获得
	 */
	public static final int ORIGIN_PROMOT = 29;
	/**
	 * 召唤宠物
	 */
	public static final int ORIGIN_GETPET = 30;
	/**
	 * 活动赠送
	 */
	public static final int ORIGIN_ACTIVITY_EXTRA = 31;
	/**
	 * 购买红包
	 */
	public static final int ORIGIN_BUYREWARD = 32;
	/**
	 * 提升任务
	 */
	public static final int ORIGIN_QUICKUPTASK = 33;
	/**
	 * 单人副本掉落
	 */
	public static final int ORIGIN_SINGLEMAP = 34;
	/**
	 * 充值暴击奖励
	 */
	public static final int ORIGIN_RECHARGECRIT = 35;
	/**
	 * 充值免费点
	 */
	public static final int ORIGIN_FREEPOINT = 36;
	/**
	 * 宠物训练加速
	 */
	public static final int ORIGIN_TRAIN = 37;
	/**
	 * 宠物培养
	 */
	public static final int ORIGIN_CULTURE = 38;
	/**
	 * 宠物传承
	 */
	public static final int ORIGIN_INHERITANCE = 39;
	/**
	 * 宠物栏开槽
	 */
	public static final int ORIGIN_PETOPENFIELDS = 40;
	/**
	 * 宠物训练购买金币
	 */
	public static final int ORIGIN_PETBUYGOLD = 41;
	/**
	 * 世界BOSS清除CDTime
	 */
	public static final int ORIGIN_WOELDBOSSACCELERATE = 42;
	/**
	 * 单人副本购买活力值
	 */
	public static final int ORIGIN_BUYVIGOR = 43;
	/**
	 * 卡牌兑换刷新列表
	 */
	public static final int ORIGIN_RECARDEXCHANGE = 44;
	/**
	 * 签到补签
	 */
	public static final int ORIGIN_SUPPLSIGN = 45;
	/**
	 * 快速完成任务
	 */
	public static final int ORIGIN_PAYFORTASK = 46;
	/**
	 * 每日月卡返利获得
	 */
	public static final int ORIGIN_RECEIVE_REBATE = 47;
	/**
	 * 登录奖励获得
	 */
	public static final int ORIGIN_LOGIN_REWARD = 48;
	/**
	 * 等级奖励获得
	 */
	public static final int ORIGIN_LEVEL_REWARD = 49;
	/**
	 * 在线奖励获得
	 */
	public static final int ORIGIN_ONLINE_REWARD = 50;
	/**
	 * 在线抽奖奖励获得
	 */
	public static final int ORIGIN_LOTTERY_REWARD = 51;
	/**
	 * 购买勋章
	 */
	public static final int ORIGIN_BADGEID = 52;
	/**
	 * 购买金币
	 */
	public static final int ORIGIN_GOLD = 53;
	/**
	 * 购买星魂碎片
	 */
	public static final int ORIGIN_STARSOULDEBRISID = 54;

	/**
	 * 活跃度奖励获得
	 */
	public static final int ACTIVE_REWARD = 55;
	/**
	 * 分包下载奖励
	 */
	public static final int ORIGIN_DOWNLOADREWARD = 56;
	/**
	 * 兑换码奖励
	 */
	public static final int ORIGIN_EXCHANGE_CODE = 57;
	/**
	 * 每日首冲奖励获得
	 */
	public static final int DAY_ORIGIN_RECHARGE_RWARD = 58;
	/**
	 * vip 奖励
	 */
	public static final int VIP_RWARD = 59;

}
