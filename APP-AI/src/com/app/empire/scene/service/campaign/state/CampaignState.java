package com.app.empire.scene.service.campaign.state;

import com.chuangyou.xianni.campaign.Campaign;

public abstract class CampaignState {
	public static final int	PREPARE	= 1;	// 准备状态
	public static final int	OPENING	= 2;	// 开放状态
	public static final int	SUCCESS	= 3;	// 成功，但未结束
	public static final int	STOP	= 4;	// 删除
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
