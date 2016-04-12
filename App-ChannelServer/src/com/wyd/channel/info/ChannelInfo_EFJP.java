package com.wyd.channel.info;
/**
 * EFJP渠道信息
 */
public class ChannelInfo_EFJP extends ChannelInfo {
    private String appkey;

    public String getAppkey() {
        return appkey;
    }

    public ChannelInfo_EFJP(String appkey) {
        this.appkey = appkey;
    }
}
