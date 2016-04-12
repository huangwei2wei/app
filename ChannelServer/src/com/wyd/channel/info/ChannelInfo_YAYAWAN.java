package com.wyd.channel.info;
/**
 * 丫丫玩渠道信息
 * @author zengxc
 *
 */
public class ChannelInfo_YAYAWAN extends ChannelInfo{
    private String appId; // 应用ID
    private String appKey; // 应用密匙
    
    public ChannelInfo_YAYAWAN(String appId,String appKey){
        this.appId=appId;
        this.appKey=appKey;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getAppKey() {
        return appKey;
    }
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    
    
}
