package com.app.empire.world.service.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;

import com.app.empire.world.common.util.HttpClientUtil;
import com.app.empire.world.service.factory.ServiceManager;

/**
 * 推广渠道激活服务
 */
public class ExtensionService implements Runnable {
	// 激活验证地址
	private static String extensionUrl = null;
	private List<String> dataList = new ArrayList<String>();

	public void start() {
		Thread t = new Thread(this);
		t.setName("ExtensionService-Thread");
		t.start();
	}

	public void run() {
		extensionUrl = ServiceManager.getManager().getConfiguration().getString("extensionurl");
		while (true) {
			try {
				synchronized (ExtensionService.this) {
					if (0 == dataList.size()) {
						this.wait();
					}
				}
				String udid = dataList.remove(0);
				List<NameValuePair> dataList = new ArrayList<NameValuePair>();
				dataList.add(new NameValuePair("appid", ServiceManager.getManager().getConfiguration().getString("iTunesid")));
				dataList.add(new NameValuePair("userid", udid));
				HttpClientUtil.PostData(extensionUrl, dataList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void add(String udid) {
		if (null != extensionUrl) {
			synchronized (ExtensionService.this) {
				dataList.add(udid);
				this.notify();
			}
		}
	}
}
