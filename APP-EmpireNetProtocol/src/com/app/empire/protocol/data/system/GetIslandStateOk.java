package com.app.empire.protocol.data.system;

import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;

public class GetIslandStateOk extends AbstractData {
	private int islandState; // 0：非节日，1：春节，2：圣诞节
//	private boolean openDuiHuanMa; // true:打开礼品兑换，false：关闭
	private boolean openTapjoy; // true:打开，false：关闭
//	private boolean openVip; // 开启Vip
//	private boolean openLoveHeart;
//	private boolean openDailySign;
//	private boolean openSinaWeiBo;
//	private boolean openFirstReward;
//	private boolean openGrowUp;
//	private boolean openInviteCode;
//	private int intensiveLevel; // 强化功能开启等级
//	private int consortiaLevel; // 公会功能开启等级
//	private int friendLevel; // 好友功能开启等级
//	private int boosLevel; // 副本功能开启等级
//	private int drawLevel; // 兑换功能开启等级
//	private int qualifyingLevel; // 排位赛功能开启等级
	private boolean opeanNewTeach; // 开启新手教学
	private int noviceType; // 新手教程类型 0和怪打，1和玩家打
	private int bindAccLevel; // 弹出绑定帐号最低等级 默认5级
	private int bindAccDelta; // 弹出绑定帐号间隔时间 秒 默认300秒
	private boolean openSMSCode; // 是否开启短代
	private boolean popNotice; // 是否开始公告
	private boolean popGoldPeople; // 是否开始小金人
	private int worldChatExp; // 世界聊天获得经验
	private int colorChatExp; // 彩色聊天获得经验
//	private boolean openPurchase; // 是否打开首充闪烁
//	private boolean openSign; // 是否签到闪烁
//	private boolean openRecovery; // 是否打开回收系统
	private int probability_x; // 机器人邀请概率1
	private int probability_y; // 机器人邀请概率2
	private int inviteLevel; // 机器人邀请等级
	private int waitTime; // 小岛等待秒数
	private int battleWaitTime; // 战斗大厅等待秒数
//	private int petLevel; // 开启宠物等级
	private int petInheritanceLevel; // 开启宠物传承的等级
//	private boolean openDraw; // 开启抽奖
	private boolean openLinShiVip; // 是否开启临时会员
	private boolean openBind; // 是否开启绑定
	private int serviceMode; // 弹王挑战是否开启跨服模式（0否，1是）
//	private int challengeLevel; // 挑战赛开放等级
	private boolean challengeStarted; // 挑战赛是否已开始
//	private int exchangeLevel; // 兑换功能开启等级
//	private int openWorldBoss;// 开放世界boss等级(-1不开放）
//	private boolean worldBossStarted;// 世界boss是否已开始
//	private boolean openReincarnation;// 是否开放转生
	private int openTipLevel;//显示tip的级别
	private int crossLevel;// 默认跨服对战等级，0表示关闭跨服对战
//	private int openFBInvite;//开放FB好友邀请
	private String moreGame;//交叉推荐开关-1为不显示，显示时字段值作为URL
	private String squareTip; //小金人提示
	
	// 2.0新增字段
	private int[] buttonId;// 按钮id
	private byte[] buttonType;// 按钮类型 0主界面建筑按钮，1主界面左侧按钮，2主界面中部按钮，3主界面右侧按钮。
	private String[] buttonIcon;// 按钮的图标
	private String[] buttonTips;// 按钮的提示
	private int[] buttonStatus1Level;// 按钮状态 按钮显示不可用需求等级
	private int[] buttonStatus2Level;// 按钮状态 按钮显示可用返回提示需求等级
	private int[] buttonStatus3Level;// 按钮状态 按钮显示可用功能开放需求等级
	private int[] playerLevel;//玩家等级
	private int[] parabolaRange;//抛物线范围 百分比 去除%符号【此字段与playerLevel一一对应关系】
	private boolean  showItemsRemainimgDays;//物品剩余天数状态显示 0表示关闭，1显示打开
	//2.1新增字段
	private boolean  	openRechargeCritFlag;//充值赢暴击奖励开关   0表示关闭，1显示打开
	private boolean		soundRoomOpen;	//语音房间开关
	private boolean  	switchGPS;			//GPS系统功能总开关
	private int			accessFrequencyGPS; // 按钮id
	private boolean  soundHostile;		//开启敌对房间
	private boolean  downloadRewardSwitch;		//安卓分包下载奖励开关

	public GetIslandStateOk(int sessionId, int serial) {
		super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetIslandStateOk, sessionId, serial);
	}

	public GetIslandStateOk() {
		super(Protocol.MAIN_SYSTEM, Protocol.SYSTEM_GetIslandStateOk);
	}

	public int getIslandState() {
		return islandState;
	}

	public void setIslandState(int islandState) {
		this.islandState = islandState;
	}

	public boolean isOpenTapjoy() {
		return openTapjoy;
	}

	public void setOpenTapjoy(boolean openTapjoy) {
		this.openTapjoy = openTapjoy;
	}

	public boolean isOpeanNewTeach() {
		return opeanNewTeach;
	}

	public void setOpeanNewTeach(boolean opeanNewTeach) {
		this.opeanNewTeach = opeanNewTeach;
	}

	public int getNoviceType() {
		return noviceType;
	}

	public void setNoviceType(int noviceType) {
		this.noviceType = noviceType;
	}

	public int getBindAccLevel() {
		return bindAccLevel;
	}

	public void setBindAccLevel(int bindAccLevel) {
		this.bindAccLevel = bindAccLevel;
	}

	public int getBindAccDelta() {
		return bindAccDelta;
	}

	public void setBindAccDelta(int bindAccDelta) {
		this.bindAccDelta = bindAccDelta;
	}

	public boolean isOpenSMSCode() {
		return openSMSCode;
	}

	public void setOpenSMSCode(boolean openSMSCode) {
		this.openSMSCode = openSMSCode;
	}

	public boolean isPopNotice() {
		return popNotice;
	}

	public void setPopNotice(boolean popNotice) {
		this.popNotice = popNotice;
	}

	public boolean isPopGoldPeople() {
		return popGoldPeople;
	}

	public void setPopGoldPeople(boolean popGoldPeople) {
		this.popGoldPeople = popGoldPeople;
	}

	public int getWorldChatExp() {
		return worldChatExp;
	}

	public void setWorldChatExp(int worldChatExp) {
		this.worldChatExp = worldChatExp;
	}

	public int getColorChatExp() {
		return colorChatExp;
	}

	public void setColorChatExp(int colorChatExp) {
		this.colorChatExp = colorChatExp;
	}

	public int getProbability_x() {
		return probability_x;
	}

	public void setProbability_x(int probability_x) {
		this.probability_x = probability_x;
	}

	public int getProbability_y() {
		return probability_y;
	}

	public void setProbability_y(int probability_y) {
		this.probability_y = probability_y;
	}

	public int getInviteLevel() {
		return inviteLevel;
	}

	public void setInviteLevel(int inviteLevel) {
		this.inviteLevel = inviteLevel;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public int getBattleWaitTime() {
		return battleWaitTime;
	}

	public void setBattleWaitTime(int battleWaitTime) {
		this.battleWaitTime = battleWaitTime;
	}

	public int getPetInheritanceLevel() {
		return petInheritanceLevel;
	}

	public void setPetInheritanceLevel(int petInheritanceLevel) {
		this.petInheritanceLevel = petInheritanceLevel;
	}

	public boolean isOpenLinShiVip() {
		return openLinShiVip;
	}

	public void setOpenLinShiVip(boolean openLinShiVip) {
		this.openLinShiVip = openLinShiVip;
	}

	public boolean isOpenBind() {
		return openBind;
	}

	public void setOpenBind(boolean openBind) {
		this.openBind = openBind;
	}

	public int getServiceMode() {
		return serviceMode;
	}

	public void setServiceMode(int serviceMode) {
		this.serviceMode = serviceMode;
	}

	public boolean isChallengeStarted() {
		return challengeStarted;
	}

	public void setChallengeStarted(boolean challengeStarted) {
		this.challengeStarted = challengeStarted;
	}

//	public boolean isWorldBossStarted() {
//		return worldBossStarted;
//	}
//
//	public void setWorldBossStarted(boolean worldBossStarted) {
//		this.worldBossStarted = worldBossStarted;
//	}

	public int getOpenTipLevel() {
		return openTipLevel;
	}

	public void setOpenTipLevel(int openTipLevel) {
		this.openTipLevel = openTipLevel;
	}

	public int getCrossLevel() {
		return crossLevel;
	}

	public void setCrossLevel(int crossLevel) {
		this.crossLevel = crossLevel;
	}

	public String getMoreGame() {
		return moreGame;
	}

	public void setMoreGame(String moreGame) {
		this.moreGame = moreGame;
	}

	public String getSquareTip() {
		return squareTip;
	}

	public void setSquareTip(String squareTip) {
		this.squareTip = squareTip;
	}

	public int[] getButtonId() {
		return buttonId;
	}

	public void setButtonId(int[] buttonId) {
		this.buttonId = buttonId;
	}

	public byte[] getButtonType() {
		return buttonType;
	}

	public void setButtonType(byte[] buttonType) {
		this.buttonType = buttonType;
	}

	public String[] getButtonIcon() {
		return buttonIcon;
	}

	public void setButtonIcon(String[] buttonIcon) {
		this.buttonIcon = buttonIcon;
	}

	public String[] getButtonTips() {
		return buttonTips;
	}

	public void setButtonTips(String[] buttonTips) {
		this.buttonTips = buttonTips;
	}

	public int[] getButtonStatus1Level() {
		return buttonStatus1Level;
	}

	public void setButtonStatus1Level(int[] buttonStatus1Level) {
		this.buttonStatus1Level = buttonStatus1Level;
	}

	public int[] getButtonStatus2Level() {
		return buttonStatus2Level;
	}

	public void setButtonStatus2Level(int[] buttonStatus2Level) {
		this.buttonStatus2Level = buttonStatus2Level;
	}

	public int[] getButtonStatus3Level() {
		return buttonStatus3Level;
	}

	public void setButtonStatus3Level(int[] buttonStatus3Level) {
		this.buttonStatus3Level = buttonStatus3Level;
	}

	public int[] getPlayerLevel() {
		return playerLevel;
	}

	public void setPlayerLevel(int[] playerLevel) {
		this.playerLevel = playerLevel;
	}

	public int[] getParabolaRange() {
		return parabolaRange;
	}

	
	public void setParabolaRange(int[] parabolaRange) {
		this.parabolaRange = parabolaRange;
	}
	
	public boolean getShowItemsRemainimgDays() {
		return showItemsRemainimgDays;
	}

	public void setShowItemsRemainimgDays(boolean showItemsRemainimgDays) {
		this.showItemsRemainimgDays = showItemsRemainimgDays;
	}

	public boolean getOpenRechargeCritFlag() {
		return openRechargeCritFlag;
	}

	public void setOpenRechargeCritFlag(boolean openRechargeCritFlag) {
		this.openRechargeCritFlag = openRechargeCritFlag;
	}
	

	public boolean getSoundRoomOpen() {
		return soundRoomOpen;
	}

	public void setSoundRoomOpen(boolean soundRoomOpen) {
		this.soundRoomOpen = soundRoomOpen;
	}

	public boolean getSwitchGPS() {
		return switchGPS;
	}

	public void setSwitchGPS(boolean switchGPS) {
		this.switchGPS = switchGPS;
	}

	public int getAccessFrequencyGPS() {
		return accessFrequencyGPS;
	}

	public void setAccessFrequencyGPS(int accessFrequencyGPS) {
		this.accessFrequencyGPS = accessFrequencyGPS;
	}

	public boolean isSoundHostile() {
		return soundHostile;
	}

	public void setSoundHostile(boolean soundHostile) {
		this.soundHostile = soundHostile;
	}

	public boolean getDownloadRewardSwitch() {
		return downloadRewardSwitch;
	}

	public void setDownloadRewardSwitch(boolean downloadRewardSwitch) {
		this.downloadRewardSwitch = downloadRewardSwitch;
	}
	
	

}
