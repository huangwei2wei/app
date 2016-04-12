package com.app.empire.protocol.data.bulletin;
import com.app.empire.protocol.Protocol;
import com.app.protocol.data.AbstractData;
/**
 * 获取微博文字和图片链接
 * 
 * @see AbstractData
 * @author mazheng
 */
public class GetWeiboInfoOk extends AbstractData {
    private String content;
    private String picurl;
    private String wbAppKey;
    private String webAppSecret;
    private String webAppRedirectUri;
    private String wbUid;

    public GetWeiboInfoOk(int sessionId, int serial) {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetWeiboInfoOk, sessionId, serial);
    }

    public GetWeiboInfoOk() {
        super(Protocol.MAIN_BULLETIN, Protocol.BULLETIN_GetWeiboInfoOk);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getWbAppKey() {
        return wbAppKey;
    }

    public void setWbAppKey(String wbAppKey) {
        this.wbAppKey = wbAppKey;
    }

    public String getWebAppSecret() {
        return webAppSecret;
    }

    public void setWebAppSecret(String webAppSecret) {
        this.webAppSecret = webAppSecret;
    }

    public String getWebAppRedirectUri() {
        return webAppRedirectUri;
    }

    public void setWebAppRedirectUri(String webAppRedirectUri) {
        this.webAppRedirectUri = webAppRedirectUri;
    }

    public String getWbUid() {
        return wbUid;
    }

    public void setWbUid(String wbUid) {
        this.wbUid = wbUid;
    }

	
}
