package com.app.empire.scene.constant;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * 错误码
 * 
 * @author wanyi
 * @createTime 2015年4月17日下午2:17:46
 * 
 */
public class ErrorCode {
	/** 成功 */
	public final static int SUCCESS = 0;

	/** 无效的方法 */
	public final static int NOT_AVAILABLE_METHOD = -2;

	/** 未知错误 */
	public final static int UNKNOW_ERROR = -1;

	/** 数据已存在 **/
	public final static int DATA_IS_ALREDAY_EXISTS = 1;

	/** 角色名重复 */
	public final static int PLAYERNAME_IS_ALREADY_EXISTS = 201;

	/** 角色名过长 */
	public final static int PLAYERNAME_LENGTH_LIMIT = 202;

	/** 角色已存在 */
	public final static int ROLE_IS_ALREADY_EXISTS = 203;

	/** 角色不存在 */
	public final static int ROLE_NOT_EXISTS = 204;

	/** 角色名含有非法字符 */
	public final static int PLAYERNAME_ILLEGAL_CHARACTER = 205;

	/** 地图数据不存在 */
	public final static int MAP_DATA_IS_NOT_EXISTIS = 301;

	/** 属性值不够 */
	public final static int ATTR_IS_NOT_ENOUGTH = 401;

	/** CD 中 **/
	public final static int CD_IS_NOT_ENOUGTH = 402;

	/** buff作用域不能减 **/
	public final static int Buff_ActionScope_Is_Not_Enougth_To_Sub = 501;

	/** 宠物出战状态中 **/
	public final static int PetIsInFightState = 601;

	/** 宠物不能卸下，因为没有出战 **/
	public static final int PetCanotUnstallCaseNotFight = 602;

	/** 染色不存在 **/
	public static final int Dye_IS_ALREADY_EXISTS = 701;

	/** 好友已经存在 */
	public static final int Friend_IS_EXISTS = 801;

	/** 不能加自己为好友 */
	public static final int Friend_IsError_AddSelf = 802;

	/** 查找不到该玩家 */
	public static final int Friend_Search_NUll = 803;

	/** 邮件不存在 */
	public static final int Email_IS_NOT_EXIST = 901;

	/** 邮件无附件 */
	public static final int Email_Not_hava_Attachment = 902;

	/** 附件已经取过 */
	public static final int Email_Attackment_Has_Get = 903;

	/** 邮件列表为空 */
	public static final int Email_IS_NULL = 904;

	/** 邮件已经过期 */
	public static final int Email_Is_Expiration = 905;

	/** 一键提取附件中没有任何可以提取的附件 */
	public static final int Email_NO_Attackment = 906;

	/** 一健删除邮件中没有任何可以删除的邮件 */
	public static final int Email_NO_DELETE = 907;

	/** 背包已满 */
	public static final int BAG_IS_FULL = 908;

	/** 玩家不是自己的好友 */
	public static final int FRIEND_NOT = 909;

	/** 已经赞过 */
	public static final int ALREADY_GIVE_THUMBUP = 1001;

	/** 禁止留言 */
	public static final int MESSAGE_IS_NOT_ALLOWED = 1002;

	/** 设置留言权限错误 */
	public static final int SET_LEAVEWORDS_LIMITS_ERROR = 1003;

	/** 放置礼物失败 */
	public static final int PUT_GIFT_ERROR = 1004;

	/** 队伍已经存在 */
	public static final int Team_Is_Existed = 1300;

	/** 队伍已经不存在 */
	public static final int Team_Is_Not_Existed = 1301;

	/** 此队伍已经申请过啦 */
	public static final int Team_Is_Applied = 1302;

	/** 此人已经有队伍啦 */
	public static final int Team_SomeBody_Has_Team = 1303;

	/** 非队长，无此权限 */
	public static final int Team_No_GRANT = 1304;

	/** 玩家不在线 */
	public static final int Team_Somebody_Not_Online = 1305;

	/** 组队人数已满 */
	public static final int Team_Members_Full = 1306;

	/** 你已经有队伍啦 */
	public static final int Team_You_Has = 1307;

	/** 此有已经在这个队伍中 */
	public static final int Team_You_Exit = 1308;

	/** 小伙伴星级奖励领取时星级不够 */
	public static final int PARTNER_STARAWARD_STARLITTLE = 1401;

	/** 小伙伴星级奖励已领取过 */
	public static final int PARTNER_STARAWARD_RECEIVED = 1402;

	/** 阵容不存在 */
	public static final int PARTNER_FORMATION_ISNOT_EXISTED = 1403;

	/** 小伙伴不存在 */
	public static final int PARTNER_ISNOT_EXISTED = 1404;

	/** 道具数量不足 */
	public static final int Prop_Use_Error = 1405;

	/** 临时的悟性不存 */
	public static final int PARTNER_TEMP_WUXING_ISNOTEXISTED = 1406;

	/** 黄点分配数据错误 */
	public static final int PARTNER_CONFIG_POINT_ERROR = 1407;

	/** 神通点不能超过10 */
	public static final int PARTNER_MAGICAL_POINT_OVERSIZE = 1408;

	/** 神通点分配数据错误 */
	public static final int PARTNER_MAGICAL_POINT_ERROR = 1409;

	/** 技能等级不能超过小伙伴等级 */
	public static final int PARTNER_SKILL_LEVEL_OVERSIZE = 1410;

	/** 小伙伴已升至最大星级 */
	public static final int PARTNER_STARLEVEL_MAX = 1411;

	/** 技能一键升级失败 */
	public static final int PARTNER_ONEKEYUP_SKILL_ERROR = 1412;

	/** 经验值错误 */
	public static final int PARTNER_EXP_ERROR = 1413;

	/** 数值为负 **/
	public static final int NUM_IS_Negative = 1414;

	/** 道具不存在 **/
	public static final int Prop_Is_Not_Existed = 1501;

	/** 查看背包失败 **/
	public static final int Get_Bag_Info_Error = 1502;

	/** 交换背包物品失败 **/
	public static final int Exchange_Bag_Prop_Error = 1503;

	/** 获取物品装备栏位置失败 **/
	public static final int Get_EquipBar_Postion_Error = 1504;

	/** 卸下装备失败 **/
	public static final int Disboard_Equip_Error = 1505;

	/** 获取装备分解道具失败 **/
	public static final int Get_Resolve_Prop_Error = 1506;

	/** 已安装 **/
	public static final int Already_Install = 1507;

	/** 出售道具失败 **/
	public static final int Sell_Prop_Error = 1508;

	/** 道具数量不够 **/
	public static final int Prop_Is_Not_Enougth = 1509;

	/** GM命令为空 */
	public static final int GM_IS_NULL = 1801;

	/** 查无此装备 */
	public static final int Equip_IS_NULL = 1901;

	/** 装备经验不足 */
	public static final int EQUIP_UNENOUGH_EXP = 1902;

	/** 人物等级不足 */
	public static final int RoleLevel_UnEnough = 1903;

	/** 材料不足 */
	public static final int Item_UnEnough = 1904;

	/** 提供的物品不存在 */
	public static final int Item_IS_NOT_Existed = 1905;

	/** 装备孔已经满级 */
	public static final int EQUIP_BAR_IS_MAX_LV = 1906;

	/** 普通孔未满级 */
	public static final int Equip_Is_NOT_MAXLV = 1907;

	/** 背包格子数已达到上限 */
	public static final int Bag_Grid_Unlock_Max = 1908;

	/** 元宝不足 */
	public static final int Cash_UnEnough = 1909;

	/** 坐骑已达到最高等级 */
	public static final int Mount_Level_Max = 1910;

	/** 货币不足 */
	public static final int Money_UnEnough = 1911;

	/** 坐骑已达到最高阶级 */
	public static final int Mount_Grade_Max = 1912;

	/** 坐骑装备不存在 */
	public static final int Mount_Equip_Not_Existed = 1913;

	/** 坐骑装备达到最大等级 */
	public static final int Mount_Equip_Level_Max = 1914;

	/** 坐骑使用属性丹数量已超过上限 */
	public static final int Mount_UseDan_max = 1915;

	/** 骑兵阶数达到最高阶 */
	public static final int Mount_Weapon_Grade_Max = 1916;

	/** 未达到目标阶数 */
	public static final int Mount_Grade_UnGet = 1917;

	/** 坐骑未获得 */
	public static final int Mount_Special_UnGet = 1918;

	/** 法宝未获得 */
	public static final int Magicwp_UnGet = 1919;

	/** 法宝已达到最高等级 */
	public static final int Magicwp_Level_Max = 1920;

	/** 禁制未激活 */
	public static final int Magicwp_Ban_UnGet = 1921;

	/** 禁制已达到最大等级 */
	public static final int Magicwp_Ban_Level_Max = 1922;

	/** 法宝使用属性丹数量已超过上限 */
	public static final int Magicwp_UseDan_max = 1923;

	/** 法宝等级不足 */
	public static final int Magicwp_Level_UnEnough = 1924;

	/** 法宝已达到最高阶 */
	public static final int Magicwp_Grade_Max = 1925;

	/** 法宝洗炼未激活 */
	public static final int Magicwp_Refine_Grade_UnOpen = 1926;

	/** 法宝已激活 */
	public static final int Magicwp_HasGet = 1927;

	/** 特殊法宝不能激活 */
	public static final int Magicwp_Special_NotOpera = 1928;

	/** 禁制装备位置未激活 */
	public static final int Magicwp_Ban_Pos_UnGet = 1929;

	/** 法宝未洗炼 */
	public static final int Magicwp_Refine_UnRefine = 1930;

	/** 碎片索引无效 */
	public static final int Magicwp_Ban_FragmentIndex_Invalid = 1931;

	/** 禁制ID不存在 */
	public static final int Magicwp_Ban_UnExist = 1932;

	/** 法宝不存在 */
	public static final int Magicwp_UnExist = 1933;

	/** VIP等级不足 */
	public static final int Vip_UnEnough = 1934;

	/** 宠物已激活 */
	public static final int Pet_Open_Already = 1935;

	/** 宠物不存在 */
	public static final int Pet_UnExist = 1936;

	/** 宠物未激活 */
	public static final int Pet_UnGet = 1937;

	/** 宠物资质已达到上限 */
	public static final int Pet_Talent_Max = 1938;

	/** 宠物等级已达到上限 */
	public static final int Pet_Level_Max = 1939;

	/** 宠物等级超过人物等级 */
	public static final int Pet_Level_More_RoleLevel = 1940;

	/** 宠物炼体已达到最高等级 */
	public static final int Pet_Physique_Max = 1941;

	/** 已是最高品质 */
	public static final int Pet_Quality_Max = 1942;

	/** 宠物已经达到最高阶 */
	public static final int Pet_Grade_Max = 1943;

	/** 获取玩家指定时装失败 */
	public static final int Fashion_Get_Fail = 1944;
	/** 时装已达最大品质 */
	public static final int Fashion_Quality_Max = 1945;
	/** 时装已处于激活状态 */
	public static final int Fashion_Beactive = 1946;
	/** 时装激活道具不足 */
	public static final int Fashion_Beactive_Less = 1947;
	/** 宠物炼魂已达最高等级 */
	public static final int Pet_Soul_Max = 1948;
	/** 宠物技能不存在 */
	public static final int Pet_Skill_NotFound = 1949;
	/** 宠物技能已处于激活状态 */
	public static final int Pet_Skill_Beactived = 1950;
	/** 宠物技能已处于解封状态 */
	public static final int Pet_Skill_Opened = 1951;
	/** 宠物技能已处于装备状态 */
	public static final int Pet_Skill_Equiped = 1952;
	/** 宠物技能已达最高等级 */
	public static final int Pet_Skill_LvMax = 1953;
	/** 宠物技能装备位无效 */
	public static final int Pet_Skill_Invalid_EquipIndex = 1954;
	/** 宠物技能装备位已开启 */
	public static final int Pet_Skill_EquipGrid_Unlocked = 1955;

	/** 特殊宠物不能培养 */
	public static final int Pet_Special_CannotTrain = 1956;

	/** 装备加持已达到最大等级 */
	public static final int EQUIPBAR_GRADE_MAX = 1957;
	/** 装备加持材料不足 */
	public static final int EQUIPBAR_GRADEUP_ITEM_NOTENOUGH = 1958;

	/** 背包类型未找到 */
	public static final int BAG_NOT_FIND = 1959;

	/** 武器觉醒等级已达到上限 */
	public static final int EQUIP_AWAKEN_MAX = 1960;

	/** 物品不是宝石 */
	public static final int ITEM_NOT_STONE = 1961;

	/** 宝石部位不对 */
	public static final int STONE_POS_ERROR = 1962;

	/** 此装备不可分解 */
	public static final int EQUIP_UN_RESOLVE = 1963;

	/** 神器达到最高等级 */
	public static final int ARTIFACT_MAX_LEVEL = 1964;

	/** 神器达到最高星级 */
	public static final int ARTIFACT_MAX_GRADE = 1965;

	/** 神器升星符不足 */
	public static final int ARTIFACT_GRADEUP_STARAMULET = 1966;

	/** 神器宝石已激活 */
	public static final int ARTIFACT_STONE_ALREADY_ACTIVATE = 1967;

	/** 神器宝石未激活 */
	public static final int ARTIFACT_STONE_NOT_ACTIVATE = 1968;

	/** 神器宝石已达到最高级 */
	public static final int ARTIFACT_STONE_LEVEL_MAX = 1969;

	/** 神器未激活 */
	public static final int ARTIFACT_NOT_ACTIVATE = 1970;

	/** 神器不存在 */
	public static final int ARTIFACT_NOT_EXIST = 1971;

	/** 玩家未设置城市 */
	public static final int PLAYER_NO_CITY = 1972;

	/** 玩家已经在帮派里了 */
	public static final int PLAYER_ALREAD_IN_GUILD = 1973;

	/** 玩家还没有加入帮派 */
	public static final int PLAYER_NO_GUILD = 1974;

	/** 帮派不存在 */
	public static final int GUILD_NOT_EXIST = 1975;

	/** 在帮派中的权限不足 */
	public static final int GUILD_POWER_UNENOUGH = 1976;

	/** 玩家没有申请 */
	public static final int GUILD_APPLY_NOT_EXIST = 1977;

	/** 帮派有人的情况下帮主不能退出帮派，请先移交给其他成员 */
	public static final int GUILD_LEADER_EXIT = 1978;

	/** 帮派名字过长 */
	public static final int GUILD_NAME_MAXLENGTH = 1979;

	/** 帮派名字重复 */
	public static final int GUILD_NAME_REPEAT = 1980;

	/** 帮派公告过长 */
	public static final int GUILD_NOTICE_MAXLENGTH = 1981;

	/** 要设置的职位已满 */
	public static final int GUILD_JOB_FULL = 1982;

	/** 帮派主殿已达到最高等级 */
	public static final int GUILD_MAINGUILD_MAXLEVEL = 1983;

	/** 帮派物资不足 */
	public static final int GUILD_SUPPLY_UNENOUGH = 1984;

	/** 蒧经阁达到最高等级 */
	public static final int GUILD_SKILLSHOP_MAXLEVEL = 1985;

	/** 帮派商店达到最高等级 */
	public static final int GUILD_SHOP_MAXLEVEL = 1986;

	/** 帮派仓库达到最高等级 */
	public static final int GUILD_WAREHOUSE_MAXLEVEL = 1987;

	/** 重复申请 */
	public static final int GUILD_APPLY_REPEAT = 1988;

	/** 申请人已经加入了一个帮派 */
	public static final int GUILD_APPLY_ALREADY_JOIN = 1989;

	/** 帮派成员数量已满 */
	public static final int GUILD_MEMBER_MAX = 1990;

	/** 帮派商店等级不足 */
	public static final int GUILD_SHOP_LEVEL_LIMIT = 1991;

	/** 帮派贡献不足 */
	public static final int GUILD_CONTRIBUTION_UNENOUGH = 1992;

	/** 技能等级不能超过蒧经阁等级 */
	public static final int GUILD_SKILLSHOP_UNENOUGH = 1993;

	/** 库存不足 */
	public static final int GUILD_WAREHOUSE_ITEM_UNENOUGH = 1994;

	/** 成员无效 */
	public static final int GUILD_MEMBER_INVALID = 1995;

	/** 邮件内容长度太长 */
	public static final int MAIL_CONTENT_MAX = 1996;

	/** 物品不能存入帮派仓库 */
	public static final int ITEM_NOT_SAVE_GUILD = 1997;

	/** 玩家境界不足 */
	public static final int PLAYER_STATELV_UNENOUGH = 1998;

	/** 帮派中正在进行夺权,只能同时进行一场夺权挑战 */
	public static final int GUILD_SEIZE_DOING = 1999;

	/** 你已经在副本中，不能进行夺权 */
	public static final int GUILD_SEIZE_ALREADY_INCAMPAIGN = 2000;

	/** 夺权目标的职位不能夺权 */
	public static final int GUILD_JOB_NO_SEIZE = 2001;

	/** 夺权目标当前未达到夺权条件 */
	public static final int GUILD_TARGET_NOT_SEIZE = 2002;

	/** 夺权目标上位时间不足5分钟 */
	public static final int GUILD_TARGET_SEIZE_WIN_CD = 2003;

	/** 夺权无法开始 */
	public static final int GUILD_SEIZE_UNKNOWN_ERROR = 2004;

	/** 有成员正在夺权，不能转让掌门 */
	public static final int GUILD_APPOINT_LEADER_SEIZE = 2005;

	/** 该成员正在夺权，不能踢出门派 */
	public static final int GUILD_REMOVE_MEMBER_SEIZE = 2006;

	/** 请先升级门派等级 */
	public static final int GUILD_LEVEL_UNENOUGH = 2007;

	/** 物品不能使用 */
	public static final int ITEM_CANNOT_USE = 2008;

	/** 此NPC未绑定脚本 */
	public static final int NPC_NO_HAVE_SCRIPT = 2501;

	/* =====================副本错误码2601-2699================ */
	/** 副本不存在 */
	public static final int CAMPAIGN_NOT_EXIST = 2601;
	/** 副本不允许进入 */
	public static final int CAMPAIGN_CAN_NOT_JOIN = 2602;
	/* ======================副本错误码END===================== */

	// =============================分隔线以上为重构之前的=============================================
	/** NPC商店出售不足或单人购买已达上限 */
	public static final int NPC_SHOP_NOT_ENGOUCH = 5000;

	public static final int MOUNT_FIGHT_ALREADY = 5001;
	/** NPC不存在 */
	public static final int NPC_TYPE_IS_NOT_EXISTED = 5002;
	/** 购买失败 */
	public static final int NPC_SHOP_FAIL = 5003;
	/** 任务未完成。不能提交 */
	public static final int TASK_UN_FINISH = 5004;
	/** 任务超时 */
	public static final int TASK_OUT_OF_DATE = 5005;

	/** 聊天频道不存在 */
	public static final int CHAT_CHANNEL_NOT_EXIST = 5006;

	/** 发言过于频繁 */
	public static final int CHAT_COOLING = 5007;

	/** 该频道不可发言 */
	public static final int CHAT_CHANNEL_NOT_SPEAK = 5008;

	/** 私聊对象不存在 */
	public static final int CHAT_PRIVATE_TARGET_NOT_EXIST = 5009;

	/** 聊天内容过长 */
	public static final int CHAT_CONTENT_MAXLENGTH = 5010;

	/** 不可跳级 **/
	public static final int SKILL_UP_ERROR = 6001;
	/** 技能配置数据不足 **/
	public static final int SKILL_UP_ERROR2 = 6002;
	/** 前置技能条件不足 **/
	public static final int SKILL_UP_ERROR3 = 6003;
	/** 修为不足 **/
	public static final int SKILL_UP_ERROR4 = 6004;
	/** 已经至最高级 **/
	public static final int SKILL_UP_ERROR5 = 6005;
	/** 升级条件不足 **/
	public static final int SKILL_UP_ERROR6 = 6006;
	/** 队伍申请列表已满 */
	public static final int TEAM_APPLY_LIST_MAX = 6007;
	/** 资源不足,升级失败 */
	public static final int SKILL_UP_ERROR7 = 6008;
	/** 等级不足，不能切换 **/
	public static final int CHANGE_BATTLE = 6009;
	/** 当前状态，不能切换 **/
	public static final int CHANGE_BATTLE1 = 6010;
	/** CD 时间未过，不能切换 **/
	public static final int CHANGE_BATTLE2 = 6011;
	/** 查无此商品 */
	public static final int NO_THE_GOODS = 6012;
	/** 价格不对 */
	public static final int ERROR_PRICE = 6013;
	/** VIP等级不够 */
	public static final int VIP_LEVEL_UNENOUGH = 6014;
	/** 数据错误 **/
	public static final int INVERSEBEAD_ERROR = 6015;
	/** 此五行印记已至最高级 **/
	public static final int INVERSEBEAD_ERROR2 = 6016;
	/** 上一个印记没有升满 **/
	public static final int INVERSEBEAD_ERROR3 = 6017;
	/** vip 礼包不存在 **/
	public static final int VIPBAG_ERROR = 6020;
	/** vip 等级不足 **/
	public static final int VIPBAG_ERROR2 = 6021;
	/** 您不是 vip **/
	public static final int VIPBAG_ERROR3 = 6022;
	/** 已经购买过了 **/
	public static final int VIPBAG_ERROR4 = 6023;
	/** 只能领取对应礼包 **/
	public static final int VIPBAG_ERROR5 = 6024;
	/** 这周已经领取了 **/
	public static final int VIPBAG_ERROR6 = 6025;
	/** 点赞每天只能一次 */
	public static final int LIKE_ONLY_ONE = 6026;

	/** 巅峰之战尚未开启 */
	public static final int PVP_1V1_NOT_OPTEN = 7001;
	/**
	 * 空间主人禁止你留言
	 */
	public static final int SPACE_NO_MSG_ERROR = 7002;
	/** 福利领取条件不足 */
	public static final int WELFARE_INSUFFICIENT_CONDITION_ERROR = 7003;

	// public static final int CHANGE_BATTLE3 = 6009;
	// public static final int CHANGE_BATTLE4 = 6009;
	// public static final int CHANGE_BATTLE5 = 6009;
	// public static final int CHANGE_BATTLE6 = 6009;
	// public static final int CHANGE_BATTLE7 = 6009;

 

}
