package com.wyd.channel.info;


/**
 * 91渠道信息
 * @author sunzx
 *
 */
public class ChannelInfo_91 extends ChannelInfo {
    private String appId; // 应用ID
    private String appKey; // 应用密匙

    public ChannelInfo_91(){
        
    }
    
    public ChannelInfo_91(String appId, String appKey){
        this.appId = appId;
        this.appKey = appKey;
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
