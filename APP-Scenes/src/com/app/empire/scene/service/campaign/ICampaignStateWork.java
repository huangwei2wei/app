package com.app.empire.scene.service.campaign;

public interface ICampaignStateWork {
	void prepare();

	void start();

	void success();

	void fail();

	void stop();
}
