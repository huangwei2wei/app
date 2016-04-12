package com.app.empire.protocol;
public class Protocol {
	public static final byte MAIN_ERROR = 0;
	public static final byte ERROR_ProtocolError = 0;
	/** 服务器间的消息协议 */
	public static final byte MAIN_SERVER = 1;
	public static final byte SERVER_Heartbeat = 1;// 服务器间心跳
	public static final byte SERVER_WorldServerToAccountServer = 2;// world链接账号服务
	public static final byte SERVER_DispatchLogin = 3;// dis链接world
	public static final byte SERVER_UpdateServerInfo = 4;// dis告知ipd 服务器信息-服务已开启
	public static final byte SERVER_NotifyMaxPlayer = 5;// world告知dis人数
	public static final byte SERVER_SyncLoad = 6;// dis告知ipd在线人数等情况
	public static final byte SERVER_AccountLogin = 7;
	public static final byte SERVER_AccountLoginOk = 8;
	public static final byte SERVER_SessionClosed = 9;// dis告知world 用户下线
	public static final byte SERVER_SetClientIPAddress = 10;// 用户链接dis告知world用户ip
	public static final byte SERVER_Kick = 11;// 踢用户下线
	public static final byte SERVER_SyncPlayer = 12;// 玩家角色信息同步(系统内部使用)
	public static final byte SERVER_PlayerLogout = 13; // 玩家登出(主要用这个协议通知账号服务器)
	// 用户管理（广播，踢下线等）
	public static final byte SERVER_BroadCast = 15;// 广播频道所有用户
	public static final byte SERVER_ForceBroadCast = 16;// 广播线上所有用户（公告
	public static final byte SERVER_ShutDown = 17;// dis 重新绑定
	public static final byte SERVER_NotifyMaintance = 18;// 设置服务器状态
	/** 用户帐号相关协议（客户端共享） */
	public static final byte MAIN_ACCOUNT = 2;
	public static final byte ACCOUNT_Heartbeat = 1;// 客户端对dis心跳
	public static final byte ACCOUNT_Login = 2;// 用户登录
	public static final byte ACCOUNT_LoginOk = 3;// 登录成功
	public static final byte ACCOUNT_RepeatLogin = 4;// 主账号重复登录
	public static final byte ACCOUNT_GetRoleList = 5;// 获取角色列表
	public static final byte ACCOUNT_GetRoleListOK = 6;
	public static final byte ACCOUNT_RoleCreate = 7;// 角色创建
	public static final byte ACCOUNT_RoleLogin = 8;// 角色登录
	public static final byte ACCOUNT_RoleLoginOk = 9;// 角色登录成功
	public static final byte ACCOUNT_UpdatePlayerData = 10;// 推送修改玩家角色信息到前端
	public static final byte ACCOUNT_GetRandomName = 11;// nickName随机
	public static final byte ACCOUNT_GetRandomNameOk = 12;
	public static final byte ACCOUNT_UpdatePlayerName = 13;// 修改玩家角色名称
	public static final byte ACCOUNT_UpdatePlayerNameOk = 14;// 修改玩家角色名称
	public static final byte ACCOUNT_SetToken = 113;
	public static final byte ACCOUNT_LoginAgain = 114;
	public static final byte ACCOUNT_LoginFail = 115;
	/** 同屏相关协议 */
	public static final byte MAIN_SYN = 3;
	public static final byte SYN_Move = 1;// 玩家角色移动发服务器
	public static final byte SYN_ReturnMove = 2; // 服务器推送给客户端
	public static final byte SYN_ReportPlace = 3;// 报告位置
	public static final byte SYN_ViewPlayerData = 4;// 推送视野范围内的玩家数据
	public static final byte SYN_JumpMap = 5;// 玩家跳地图
	public static final byte SYN_Attack = 6;// 玩家攻击包括技能
	public static final byte SYN_ReturnAttack = 6;// 广播玩家攻击包括技能

	/** 聊天相关协议 */
	public static final byte MAIN_CHAT = 4;
	public static final byte CHAT_SendMessage = 1;
	public static final byte CHAT_ReceiveMessage = 2;
	public static final byte CHAT_ChangeChannel = 3;
	public static final byte CHAT_SyncChannels = 4;
	public static final byte CHAT_RemoveChannels = 5;// 移除频道
	/** 系统间通信协议 */
	public static final byte MAIN_SYSTEM = 5;
	public static final byte SYSTEM_NOP = 1;
	public static final byte SYSTEM_SYNC = 2;
	public static final byte SYSTEM_HttpClose = 3;
	public static final byte SYSTEM_ShakeHands = 4;// 客户端对dis心跳
	public static final byte SYSTEM_TopHands = 5;
	public static final byte SYSTEM_GetIslandState = 7;
	public static final byte SYSTEM_GetIslandStateOk = 8;
	public static final byte SYSTEM_GetSystemInfo = 9;
	public static final byte SYSTEM_GetSystemInfoOk = 10;
	public static final byte SYSTEM_GetNoviceRemark = 11;
	public static final byte SYSTEM_GetNoviceRemarkOk = 12;
	public static final byte SYSTEM_GetItemPriceAndVip = 13;
	public static final byte SYSTEM_GetItemPriceAndVipOk = 14;
	public static final byte SYSTEM_EarthPush = 15;
	public static final byte SYSTEM_BattleShakeHands = 16;
	public static final byte SYSTEM_GetPayAppRewardList = 17;
	public static final byte SYSTEM_GetPayAppRewardListOk = 18;
	public static final byte SYSTEM_GetPayAppReward = 19;
	public static final byte SYSTEM_GetPayAppRewardOk = 20;
	public static final byte SYSTEM_GetKeyProcess = 21;
	public static final byte SYSTEM_GetKeyProcessOk = 22;
	/** 邮件相关协议 */
	public static final byte MAIN_MAIL = 6;
	public static final byte MAIL_GetMailList = 1;// 获取邮件列表
	public static final byte MAIL_GetMailListOk = 2;
	public static final byte MAIL_LockMail = 3;// 查看邮件
	public static final byte MAIL_LockMailOk = 4;
	public static final byte MAIL_ReceiveMail = 5;// 领取邮件
	public static final byte MAIL_ReceiveMailOk = 6;
	public static final byte MAIL_DelMail = 7;// 删除邮件
	public static final byte MAIL_DelMailOk = 8;
	public static final byte MAIL_NewMail = 11;// 有新邮件提醒

	/** 公告协议 */
	public static final byte MAIN_BULLETIN = 7;
	public static final byte BULLETIN_GetBulletin = 1;
	public static final byte BULLETIN_GetBulletinOk = 2;
	public static final byte BULLETIN_GetAbout = 3;
	public static final byte BULLETIN_GetAboutOk = 4;
	public static final byte BULLETIN_GetHelp = 5;
	public static final byte BULLETIN_GetHelpOk = 6;
	public static final byte BULLETIN_GetWeiboInfo = 7;
	public static final byte BULLETIN_GetWeiboInfoOk = 8;
	public static final byte BULLETIN_WeiboShare = 9;
	/** 充值相关协议 */
	public static final byte MAIN_PURCHASE = 8;
	public static final byte PURCHASE_GetProductIdList = 1;
	public static final byte PURCHASE_SendProductIdList = 2;
	public static final byte PURCHASE_IOSSendProductCheckInfo = 3;
	public static final byte PURCHASE_BuySuccess = 4;
	public static final byte PURCHASE_BuyFailed = 5;
	public static final byte PURCHASE_AndroidSendProductCheckInfo = 6;
	public static final byte PURCHASE_SendProductId = 7;
	public static final byte PURCHASE_GetRuleList = 8;
	public static final byte PURCHASE_GetRuleListOk = 9;
	public static final byte PURCHASE_GetCallBackUri = 10;
	public static final byte PURCHASE_GetCallBackUriOk = 11;
	public static final byte PURCHASE_RequestSmsCodeSerialid = 14;
	public static final byte PURCHASE_RequestSmsCodeSerialidOk = 15;
	public static final byte PURCHASE_SubmitSMSProduct = 16;
	public static final byte PURCHASE_SMSProductBuySuccess = 17;

	/** 英雄相关协议 **/
	public static final byte MAIN_HERO = 9;
	public static final byte HERO_UpdateHeroData = 1;// 推送修改玩家英雄信息到前端
	public static final byte HERO_GetHeroList = 2;// 获取英雄列表
	public static final byte HERO_GetHeroListOK = 3;
	public static final byte HERO_GetSkillList = 4;// 获取英雄技能列表
	public static final byte HERO_GetSkillListOK = 5;
	public static final byte HERO_StudySkill = 6;// 英雄技能学习／升级
	public static final byte HERO_StudySkillOK = 7;
	public static final byte HERO_ResetSkill = 8;// 重置技能
	public static final byte HERO_ResetSkillOK = 9;
	public static final byte HERO_UseGoods = 10;// 英雄吃经验等
	public static final byte HERO_UseGoodsOK = 11;

	/** 装备相关协议 **/
	public static final byte MAIN_EQUIP = 10;
	public static final byte EQUIP_GetEquipList = 1; // 获取装备列表
	public static final byte EQUIP_GetEquipListOk = 2;
	public static final byte EQUIP_WearEquip = 3; // 穿戴装备
	public static final byte EQUIP_WearEquipOk = 4;
	public static final byte EQUIP_MergeEquip = 5; // 合成装备
	public static final byte EQUIP_MergeEquipOk = 6;
	public static final byte EQUIP_RefineEquip = 7; // 精炼装备
	public static final byte EQUIP_RefineEquipOk = 8;
	public static final byte EQUIP_ActivateAchieve = 9; // 激活成就
	public static final byte EQUIP_ActivateAchieveOk = 10;

	/** 背包相关协议 **/
	public static final byte MAIN_BACKPACK = 11;
	public static final byte BACKPACK_UpdateGoodsData = 1; // 更新背包物品变化数据
	public static final byte BACKPACK_GetBackpackList = 2; // 获取背包列表
	public static final byte BACKPACK_GetBackpackListOk = 3;
	public static final byte BACKPACK_UseGoods = 4; // 使用物品
	public static final byte BACKPACK_UseGoodsOk = 5; // 使用物品
	public static final byte BACKPACK_SellGoods = 6; // 出售物品
	public static final byte BACKPACK_SellGoodsOk = 7; // 出售物品

	/** 副本相关协议 **/
	public static final byte MAIN_COPYMAP = 12;
	public static final byte COPYMAP_GetList = 1; // 获取过关列表
	public static final byte COPYMAP_GetListOK = 2;
	public static final byte COPYMAP_AcessCopyMap = 3;// 领取副本
	public static final byte COPYMAP_AcessCopyMapOk = 4;
	public static final byte COPYMAP_CompleteCopyMap = 5;// 完成副本
	public static final byte COPYMAP_CompleteCopyMapOk = 6;
	public static final byte COPYMAP_SaveTeam = 7;// 保存战队
	public static final byte COPYMAP_SaveTeamOk = 8;
	public static final byte COPYMAP_GetTeam = 9;// 获取战队
	public static final byte COPYMAP_GetTeamOk = 10;

	/** PVP房间服务 **/
	public static final byte MAIN_PVPROOM = 13;
	public static final byte PVPROOM_RoomPlayerInfo = 1;// PVP 房间内的玩家数据列表
	public static final byte PVPROOM_GetRoomList = 2;// 获取房间列表
	public static final byte PVPROOM_GetRoomListOk = 3;
	public static final byte PVPROOM_CreateRoom = 4;// 创建房间
	public static final byte PVPROOM_CreateRoomOk = 5;
	public static final byte PVPROOM_IntoRoom = 6;// 进入房间
	public static final byte PVPROOM_IntoRoomOk = 7;// 进入房间Ok
	public static final byte PVPROOM_OutRoom = 8;// 退出房间
	public static final byte PVPROOM_OutRoomOk = 9;// 退出房间Ok
	public static final byte PVPROOM_Start = 10;// 房主开始
	public static final byte PVPROOM_LoadComplete = 11;// 加载完成
	public static final byte PVPROOM_LoadCompleteOk = 12;// 加载完成
	public static final byte PVPROOM_StartStatus = 13;// 开始状态

	/** 商店 **/
	public static final byte MAIN_SHOP = 14;
	public static final byte SHOP_GetShop = 1;// 获取商店
	public static final byte SHOP_GetShopOk = 2;
	public static final byte SHOP_Buy = 3;// 购买物品
	public static final byte SHOP_BuyOk = 4;
	public static final byte SHOP_Refresh = 5;// 手动刷新
	public static final byte SHOP_RefreshOk = 6;
	/** NPC **/
	public static final byte MAIN_NPC = 15;
	public static final byte NPC_GetNpc = 1;// 获取Npc
	public static final byte NPC_GetNpcOK = 2;
	public static final byte NPC_Upgrade = 3;// 升级
	public static final byte NPC_UpgradeOK = 4;
	public static final byte NPC_Buy = 5;// 购买
	public static final byte NPC_BuyOK = 6;
	public static final byte NPC_Receive = 7;// 兑换
	public static final byte NPC_ReceiveOK = 8;
	/** pvp对战协议 **/
	public static final byte MAIN_PVP = 16;
	public static final byte PVP_Skill = 1;// 技能伤害
	/** AI */
	public static final byte MAIN_AI = 17;
	public static final byte AI_CommandMessage = 1;

	/** 客户端日志上传控制 */
	public static final byte MAIN_ERRORLOG = 107;
	public static final byte ERRORLOG_GetLogList = 1;
	public static final byte ERRORLOG_SendLogList = 2;
	public static final byte ERRORLOG_GetLog = 3;
	public static final byte ERRORLOG_SendLog = 4;
	/** 系统提示(包含错误提示和获取安卓充值价格列表信息) */
	public static final byte MAIN_ERRORCODE = 108;
	public static final byte ERRORCODE_GetList = 1;
	public static final byte ERRORCODE_GetListOk = 2;
	public static final byte ERRORCODE_CheckList = 3;
	public static final byte ERRORCODE_CheckOk = 4;
	public static final byte ERRORCODE_GetSmsCodeList = 12;
	public static final byte ERRORCODE_GetSmsCodeListOk = 13;
	public static final byte ERRORCODE_GetSmsCodeNewList = 14;
	public static final byte ERRORCODE_GetSmsCodeNewListOk = 15;

	/** 协议测试 */
	public static final byte MAIN_TEST = 127;
	public static final byte TEST_Test = 1;

}
