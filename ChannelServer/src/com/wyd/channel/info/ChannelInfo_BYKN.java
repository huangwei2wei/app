package com.wyd.channel.info;

public class ChannelInfo_BYKN extends ChannelInfo{
    private String gameId;
    private String key;
    
    
    public ChannelInfo_BYKN(String gameId,String key){
        this.gameId=gameId;
        this.key=key;
    }
    
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
}
