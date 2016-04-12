package com.wyd.channel.info;
/**
 * BD渠道信息
 * @author sunzx
 *
 */
public class ChannelInfo_BD extends ChannelInfo {
    private String appId;
    private String appKey;
    private String appSecrt;

    public ChannelInfo_BD(){
        
    }
    
    public ChannelInfo_BD(String appId, String appKey, String appSecrt){
        this.appId = appId;
        this.appKey = appKey;
        this.appSecrt = appSecrt;
        
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

    public String getAppSecrt() {
        return appSecrt;
    }

    public void setAppSecrt(String appSecrt) {
        this.appSecrt = appSecrt;
    }
}
