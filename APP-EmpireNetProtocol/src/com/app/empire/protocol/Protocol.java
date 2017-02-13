package com.app.empire.protocol;

/**
 * 1-10000 wroldServer，10001-20000 scenesServer。服务器内部服数据不受协议号影响，客户机发协议受协议号影响
 */
public class Protocol extends ScenesProtocol {
	public static final short	MAIN_ERROR								= 0;
	public static final short	ERROR_ProtocolError						= 0;
	/** 服务器间的消息协议 */
	public static final short	MAIN_SERVER								= 1;
	public static final short	SERVER_Heartbeat						= 1;	// 服务器间心跳
	public static final short	SERVER_WorldServerToAccountServer		= 2;	// world链接账号服务
	public static final short	SERVER_DispatchLogin					= 3;	// dis链接world
	public static final short	SERVER_UpdateServerInfo					= 4;	// dis告知ipd
																				// 服务器信息-服务已开启
	public static final short	SERVER_NotifyMaxPlayer					= 5;	// world告知dis人数
	public static final short	SERVER_SyncLoad							= 6;	// dis告知ipd在线人数等情况
	public static final short	SERVER_AccountLogin						= 7;
	public static final short	SERVER_AccountLoginOk					= 8;
	public static final short	SERVER_SessionClosed					= 9;	// dis告知world
																				// 用户下线
	public static final short	SERVER_SetClientIPAddress				= 10;	// 用户链接dis告知world用户ip
	public static final short	SERVER_Kick								= 11;	// 踢用户下线
	public static final short	SERVER_SyncPlayer						= 12;	// 玩家角色信息同步(系统内部使用)
	public static final short	SERVER_PlayerLogout						= 13;	// 玩家登出(主要用这个协议通知账号服务器)
	// 用户管理（广播，踢下线等）
	public static final short	SERVER_BroadPb							= 14;	// 广播玩家
	public static final short	SERVER_BroadCast						= 15;	// 广播频道所有用户
	public static final short	SERVER_ForceBroadCast					= 16;	// 广播线上所有用户（公告
	public static final short	SERVER_ShutDown							= 17;	// dis
																				// 重新绑定
	public static final short	SERVER_NotifyMaintance					= 18;	// 设置服务器状态
	/** 用户帐号相关协议（客户端共享） */
	public static final short	MAIN_ACCOUNT							= 2;
	public static final short	ACCOUNT_Heartbeat						= 1;	// 客户端对dis心跳
	public static final short	ACCOUNT_Login							= 2;	// 用户登录
	public static final short	ACCOUNT_LoginOk							= 3;	// 登录成功
	public static final short	ACCOUNT_RepeatLogin						= 4;	// 主账号重复登录
	public static final short	ACCOUNT_GetRoleList						= 5;	// 获取角色列表
	public static final short	ACCOUNT_GetRoleListOK					= 6;
	public static final short	ACCOUNT_RoleCreate						= 7;	// 角色创建
	public static final short	ACCOUNT_RoleLogin						= 8;	// 角色登录
	public static final short	ACCOUNT_RoleLoginOk						= 9;	// 角色登录成功
	public static final short	ACCOUNT_UpdatePlayerData				= 10;	// 推送修改玩家角色信息到前端
	public static final short	ACCOUNT_GetRandomName					= 11;	// nickName随机
	public static final short	ACCOUNT_GetRandomNameOk					= 12;
	public static final short	ACCOUNT_UpdatePlayerName				= 13;	// 修改玩家角色名称
	public static final short	ACCOUNT_UpdatePlayerNameOk				= 14;	// 修改玩家角色名称
	public static final short	ACCOUNT_SetToken						= 113;
	public static final short	ACCOUNT_LoginAgain						= 114;
	public static final short	ACCOUNT_LoginFail						= 115;
	/** map **/
	public static final short	MAIN_MAP								= 3;
	public static final short	MAP_ChangeMap							= 1;	// 请求变更场景
	public static final short	MAP_EnterMapResult						= 2;	// 申请场景入场结果
	public static final short	MAP_CreateMap							= 3;	// scene服务器创建地图

	/** 聊天相关协议 */
	public static final short	MAIN_CHAT								= 4;
	public static final short	CHAT_SendMessage						= 1;
	public static final short	CHAT_ReceiveMessage						= 2;
	public static final short	CHAT_ChangeChannel						= 3;
	public static final short	CHAT_SyncChannels						= 4;
	public static final short	CHAT_RemoveChannels						= 5;	// 移除频道
	/** 系统间通信协议 */
	public static final short	MAIN_SYSTEM								= 5;
	public static final short	SYSTEM_NOP								= 1;
	public static final short	SYSTEM_SYNC								= 2;
	public static final short	SYSTEM_HttpClose						= 3;
	public static final short	SYSTEM_ShakeHands						= 4;	// 客户端对dis心跳
	public static final short	SYSTEM_TopHands							= 5;
	public static final short	SYSTEM_GetIslandState					= 7;
	public static final short	SYSTEM_GetIslandStateOk					= 8;
	public static final short	SYSTEM_GetSystemInfo					= 9;
	public static final short	SYSTEM_GetSystemInfoOk					= 10;
	public static final short	SYSTEM_GetNoviceRemark					= 11;
	public static final short	SYSTEM_GetNoviceRemarkOk				= 12;
	public static final short	SYSTEM_GetItemPriceAndVip				= 13;
	public static final short	SYSTEM_GetItemPriceAndVipOk				= 14;
	public static final short	SYSTEM_EarthPush						= 15;
	public static final short	SYSTEM_BattleShakeHands					= 16;
	public static final short	SYSTEM_GetPayAppRewardList				= 17;
	public static final short	SYSTEM_GetPayAppRewardListOk			= 18;
	public static final short	SYSTEM_GetPayAppReward					= 19;
	public static final short	SYSTEM_GetPayAppRewardOk				= 20;
	public static final short	SYSTEM_GetKeyProcess					= 21;
	public static final short	SYSTEM_GetKeyProcessOk					= 22;
	/** 邮件相关协议 */
	public static final short	MAIN_MAIL								= 6;
	public static final short	MAIL_GetMailList						= 1;	// 获取邮件列表
	public static final short	MAIL_GetMailListOk						= 2;
	public static final short	MAIL_LockMail							= 3;	// 查看邮件
	public static final short	MAIL_LockMailOk							= 4;
	public static final short	MAIL_ReceiveMail						= 5;	// 领取邮件
	public static final short	MAIL_ReceiveMailOk						= 6;
	public static final short	MAIL_DelMail							= 7;	// 删除邮件
	public static final short	MAIL_DelMailOk							= 8;
	public static final short	MAIL_NewMail							= 11;	// 有新邮件提醒

	/** 公告协议 */
	public static final short	MAIN_BULLETIN							= 7;
	public static final short	BULLETIN_GetBulletin					= 1;
	public static final short	BULLETIN_GetBulletinOk					= 2;
	public static final short	BULLETIN_GetAbout						= 3;
	public static final short	BULLETIN_GetAboutOk						= 4;
	public static final short	BULLETIN_GetHelp						= 5;
	public static final short	BULLETIN_GetHelpOk						= 6;
	public static final short	BULLETIN_GetWeiboInfo					= 7;
	public static final short	BULLETIN_GetWeiboInfoOk					= 8;
	public static final short	BULLETIN_WeiboShare						= 9;
	/** 充值相关协议 */
	public static final short	MAIN_PURCHASE							= 8;
	public static final short	PURCHASE_GetProductIdList				= 1;
	public static final short	PURCHASE_SendProductIdList				= 2;
	public static final short	PURCHASE_IOSSendProductCheckInfo		= 3;
	public static final short	PURCHASE_BuySuccess						= 4;
	public static final short	PURCHASE_BuyFailed						= 5;
	public static final short	PURCHASE_AndroidSendProductCheckInfo	= 6;
	public static final short	PURCHASE_SendProductId					= 7;
	public static final short	PURCHASE_GetRuleList					= 8;
	public static final short	PURCHASE_GetRuleListOk					= 9;
	public static final short	PURCHASE_GetCallBackUri					= 10;
	public static final short	PURCHASE_GetCallBackUriOk				= 11;
	public static final short	PURCHASE_RequestSmsCodeSerialid			= 14;
	public static final short	PURCHASE_RequestSmsCodeSerialidOk		= 15;
	public static final short	PURCHASE_SubmitSMSProduct				= 16;
	public static final short	PURCHASE_SMSProductBuySuccess			= 17;

	/** 英雄相关协议 **/
	public static final short	MAIN_HERO								= 9;
	public static final short	HERO_UpdateHeroData						= 1;	// 推送修改玩家英雄信息到前端
	public static final short	HERO_GetHeroList						= 2;	// 获取英雄列表
	public static final short	HERO_GetHeroListOK						= 3;
	public static final short	HERO_GetSkillList						= 4;	// 获取英雄技能列表
	public static final short	HERO_GetSkillListOK						= 5;
	public static final short	HERO_StudySkill							= 6;	// 英雄技能学习／升级
	public static final short	HERO_StudySkillOK						= 7;
	public static final short	HERO_ResetSkill							= 8;	// 重置技能
	public static final short	HERO_ResetSkillOK						= 9;
	public static final short	HERO_UseGoods							= 10;	// 英雄吃经验等
	public static final short	HERO_UseGoodsOK							= 11;

	/** 装备相关协议 **/
	public static final short	MAIN_EQUIP								= 10;
	public static final short	EQUIP_GetEquipList						= 1;	// 获取装备列表
	public static final short	EQUIP_GetEquipListOk					= 2;
	public static final short	EQUIP_WearEquip							= 3;	// 穿戴装备
	public static final short	EQUIP_WearEquipOk						= 4;
	public static final short	EQUIP_MergeEquip						= 5;	// 合成装备
	public static final short	EQUIP_MergeEquipOk						= 6;
	public static final short	EQUIP_RefineEquip						= 7;	// 精炼装备
	public static final short	EQUIP_RefineEquipOk						= 8;
	public static final short	EQUIP_ActivateAchieve					= 9;	// 激活成就
	public static final short	EQUIP_ActivateAchieveOk					= 10;

	/** 背包相关协议 **/
	public static final short	MAIN_BACKPACK							= 11;
	public static final short	BACKPACK_UpdateGoodsData				= 1;	// 更新背包物品变化数据
	public static final short	BACKPACK_GetBackpackList				= 2;	// 获取背包列表
	public static final short	BACKPACK_GetBackpackListOk				= 3;
	public static final short	BACKPACK_UseGoods						= 4;	// 使用物品
	public static final short	BACKPACK_UseGoodsOk						= 5;	// 使用物品
	public static final short	BACKPACK_SellGoods						= 6;	// 出售物品
	public static final short	BACKPACK_SellGoodsOk					= 7;	// 出售物品

	/** 副本相关协议 **/
	public static final short	MAIN_COPYMAP							= 12;
	public static final short	COPYMAP_GetList							= 1;	// 获取过关列表
	public static final short	COPYMAP_GetListOK						= 2;
	public static final short	COPYMAP_AcessCopyMap					= 3;	// 领取副本
	public static final short	COPYMAP_AcessCopyMapOk					= 4;
	public static final short	COPYMAP_CompleteCopyMap					= 5;	// 完成副本
	public static final short	COPYMAP_CompleteCopyMapOk				= 6;
	public static final short	COPYMAP_SaveTeam						= 7;	// 保存战队
	public static final short	COPYMAP_SaveTeamOk						= 8;
	public static final short	COPYMAP_GetTeam							= 9;	// 获取战队
	public static final short	COPYMAP_GetTeamOk						= 10;

	/** 商店 **/
	public static final short	MAIN_SHOP								= 14;
	public static final short	SHOP_GetShop							= 1;	// 获取商店
	public static final short	SHOP_GetShopOk							= 2;
	public static final short	SHOP_Buy								= 3;	// 购买物品
	public static final short	SHOP_BuyOk								= 4;
	public static final short	SHOP_Refresh							= 5;	// 手动刷新
	public static final short	SHOP_RefreshOk							= 6;
	/** NPC **/
	public static final short	MAIN_NPC								= 15;
	public static final short	NPC_GetNpc								= 1;	// 获取Npc
	public static final short	NPC_GetNpcOK							= 2;
	public static final short	NPC_Upgrade								= 3;	// 升级
	public static final short	NPC_UpgradeOK							= 4;
	public static final short	NPC_Buy									= 5;	// 购买
	public static final short	NPC_BuyOK								= 6;
	public static final short	NPC_Receive								= 7;	// 兑换
	public static final short	NPC_ReceiveOK							= 8;
	/** pvp对战协议 **/
	public static final short	MAIN_PVP								= 16;
	public static final short	PVP_Skill								= 1;	// 技能伤害
	/** AI */
	public static final short	MAIN_AI									= 17;
	public static final short	AI_CommandMessage						= 1;

	// /***** 以下是 scene 服的协议 ******/
	/** 玩家登录scene */
	public static final short	MAIN_WORLD								= 101;
	/** 登录scene 服 **/
	public static final short	WORLD_LoginIn							= 1;
	public static final short	WORLD_LoginInOK							= 2;

	/** 客户端日志上传控制 */
	public static final short	MAIN_ERRORLOG							= 107;
	public static final short	ERRORLOG_GetLogList						= 1;
	public static final short	ERRORLOG_SendLogList					= 2;
	public static final short	ERRORLOG_GetLog							= 3;
	public static final short	ERRORLOG_SendLog						= 4;
	/** 系统提示(包含错误提示和获取安卓充值价格列表信息) */
	public static final short	MAIN_ERRORCODE							= 108;
	public static final short	ERRORCODE_GetList						= 1;
	public static final short	ERRORCODE_GetListOk						= 2;
	public static final short	ERRORCODE_CheckList						= 3;
	public static final short	ERRORCODE_CheckOk						= 4;
	public static final short	ERRORCODE_GetSmsCodeList				= 12;
	public static final short	ERRORCODE_GetSmsCodeListOk				= 13;
	public static final short	ERRORCODE_GetSmsCodeNewList				= 14;
	public static final short	ERRORCODE_GetSmsCodeNewListOk			= 15;

	// /** 协议测试 */
	// public static final short MAIN_TEST = 50;
	// public static final short TEST_Test = 1;
	// public static final short TEST_Test2 = 2;
}
