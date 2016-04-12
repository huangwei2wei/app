package com.wyd.channel.info;

public class ChannelInfo_BDGAME extends ChannelInfo{
    private String secret_key;
    private String api_key;
    
    public ChannelInfo_BDGAME(){
        
    }
    
    public ChannelInfo_BDGAME(String api_key, String secret_key){
        this.secret_key = secret_key;
        this.api_key = api_key;
        
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }
    
    
}
