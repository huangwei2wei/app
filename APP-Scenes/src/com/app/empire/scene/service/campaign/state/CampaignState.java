package com.app.empire.scene.service.campaign.state;

import com.app.empire.scene.service.campaign.Campaign;

public abstract class CampaignState {
	public static final int	PREPARE	= 1;	// 准备状态
	public static final int	START	= 2;	// 开始状态
	public static final int	SUCCESS	= 3;	// 成功，但未结束
	public static final int	STOP	= 4;	// 删除
	public static final int	FAIL	= 5;	// 失败
	protected long			currentMillis;
	protected int			code;
	protected Campaign		campaign;		// 所属副本

	public CampaignState(Campaign campaign) {
		this.campaign = campaign;
	}

	public abstract void work();

	public int getCode() {
		return code;
	}
}
