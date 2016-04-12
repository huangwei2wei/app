package com.wyd.channel.info;


/**
 * UC渠道信息
 * @author sunzx
 *
 */
public class ChannelInfo_UC extends ChannelInfo {
    private String cpId;
    private String gameId;
    private String serviceId;
    private String apiKey;

    public ChannelInfo_UC(){
        
    }
    
    public ChannelInfo_UC(String cpId, String gameId, String serviceId, String apiKey){
        this.cpId = cpId;
        this.gameId = gameId;
        this.serviceId = serviceId;
        this.apiKey = apiKey;
    }
    
    public String getCpId() {
        return cpId;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
