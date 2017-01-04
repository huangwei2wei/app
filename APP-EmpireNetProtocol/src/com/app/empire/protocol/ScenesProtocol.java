package com.app.empire.protocol;

/**
 * 1-10000 wroldServer，10001-20000 scenesServer。服务器内部服数据不受协议号影响，客户机发协议受协议号影响
 */
public class ScenesProtocol {
	/** 协议测试 */
	public static final short MAIN_TEST = 91;
	public static final short TEST_Test = 1;
	public static final short TEST_Test2 = 2;

	/** 角色数据 **/
	public static final short MAIN_PLAYER = 10001;
	public static final short PLAYER_PLAYERINFO = 1;// 回写scene服务器数据
	public static final short PLAYER_BUFFER = 2;
	public static final short PLAYER_PROPERTY = 3;// 属性变更

	/** 英雄数据 **/
	public static final short MAIN_HERO = 10002;

	/** 战斗 **/
	public static final short MAIN_BATTLE = 10003;
	public static final short BATTLE_Move = 1;// 玩家移动请求或相应
	public static final short BATTLE_MOVESTOP = 2;// 玩家移动请求或相应
	public static final short BATTLE_Leave = 3;// 玩家离开
	public static final short BATTLE_Enter = 4;// 玩家进入

	public static final short BATTLE_EnterMapResult = 5;//

	public static final short BATTLE_AttackSkill = 5;// 通知附近人，玩家施放技能

	public static final short BATTLE_Snapshot = 6; // 玩家战斗属性快照-玩家进入视野时发送
	public static final short BATTLE_DAMAGE = 7;// 同步伤害给附近人
	public static final short BATTLE_LIVINGSTATUS = 8;// 状态变更消息
	public static final short BATTLE_SnareTargets = 9;// 陷阱人数变化
	public static final short BATTLE_PlayerSkillLiving = 10;// 同步 玩家杀死怪物或人 到worldserver

	/** map **/
	public static final short MAIN_MAP = 10004;
	public static final short MAP_ChangeMap = 1;// 请求变更场景
	public static final short MAP_EnterMapResult = 2;// 申请场景入场结果
	public static final short MAP_CreateMap = 3;// scene服务器创建地图
	/** campaign **/
	public static final short MAIN_CAMPAIGN = 10005;
	public static final short CAMPAIGN_Statu = 1;// 玩家副本状态
	public static final short CAMPAIGN_Info = 2;// 副本当前信息
	public static final short CAMPAIGN_CampaignTaskInfo = 3;// 副本任务记录
	public static final short CAMPAIGN_NodeInfo = 4; // 副本节点
	/** 掉落 **/
	public static final short MAIN_DROP = 10006;//
	public static final short DROP_ItemPackage = 1;// 掉落物品
	public static final short DROP_ItemRemove = 2;// 删除掉落物品

}
