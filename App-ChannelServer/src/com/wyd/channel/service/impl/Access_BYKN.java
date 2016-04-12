package com.wyd.channel.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.wyd.channel.info.ChannelInfo_BYKN;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.DigestUtil_BYKN;
/**
 * 类 <code>Access_BYKN</code> 博雅科诺渠道接入基础类
 * @author huyunyan
 *
 */
public class Access_BYKN implements IAccessService{
    /** 博雅科诺渠道登录验证URL*/
    private static final String URL_BYKN    = "http://app2.bykernel.com/v2/user/info";
    /** 博雅科诺渠道编码*/
    public static final int     CHANNEl_BYKN = 7001;
    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(URL_BYKN);
        String ret = null;
        try {
            postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
            postMethod.addParameter("gameId",parameter.get("gameId").toString());
            postMethod.addParameter("sid",parameter.get("sid").toString());
            postMethod.addParameter("channel",parameter.get("channel").toString());
            postMethod.addParameter("sign",parameter.get("sign").toString());
            httpClient.executeMethod(postMethod);
            ret = postMethod.getResponseBodyAsString();
        } catch (Exception ex) {
            ex.printStackTrace();         
        } finally {
            postMethod.abort();
            postMethod.releaseConnection();
        }
        return ret;
       
    }
    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_BYKN channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        //parameters是客户端传过来的参数，格式：sid&channel
        String []parameters=channelInfo.getParameter()[1].split("&");
        String gameId=channelInfo.getGameId();
        String sid=parameters[0];
        String channel=parameters[1];
        //System.out.println("sid:"+sid+" channel: "+channel);
        parameter.put("gameId",gameId );
        parameter.put("sid", sid);
        parameter.put("channel", channel);
        parameter.put("sign", DigestUtil_BYKN.getHmac(new String[]{gameId,channel,sid}, channelInfo.getKey()));
        LoginResult channelLoginResult = new LoginResult();
        try {
            String ret = (String) this.channelLogin(parameter);
           // System.out.println(ret);
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject = JSONObject.fromObject(ret);
            if("true".equals(jsonObject.getString("success"))){
                JSONObject data=jsonObject.getJSONObject("data");
                channelLoginResult.setCode(Common.STATUS_SUCCESS);
                channelLoginResult.setMessage(data.getString("userId"));
            }else{
                channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelLoginResult;
    }
    
}
