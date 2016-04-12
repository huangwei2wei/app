package com.wyd.channel.info;


/**
 * 360渠道信息
 * @author sunzx
 *
 */
public class ChannelInfo_360 extends ChannelInfo {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    public ChannelInfo_360(){
        
    }
    
    public ChannelInfo_360(String grantType, String clientId, String clientSecret, String redirectUri){
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }
    
    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
