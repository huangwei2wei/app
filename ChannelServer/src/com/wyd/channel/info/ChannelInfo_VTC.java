package com.wyd.channel.info;

public class ChannelInfo_VTC extends ChannelInfo{
	private String clientIP;
	private String ostype;//系统类型ANDROID:1 IOS:2
	public ChannelInfo_VTC(String clientIP,String ostype) {
		this.clientIP = clientIP;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	public String getOstype() {
		return ostype;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}
    
}
