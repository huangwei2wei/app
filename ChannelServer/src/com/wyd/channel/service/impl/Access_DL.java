package com.wyd.channel.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;

import com.wyd.channel.info.ChannelInfo_DL;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
import com.wyd.channel.utils.HttpClientUtil.MySSLProtocolSocketFactory;

public class Access_DL implements IAccessService{
    /** 当乐 基金 渠道编码*/
    public static final int     CHANNEl_DL      = 1002;
    /** 当乐 登录验证URL*/
    private static final String URL_DL             = "http://connect.d.cn/open/member/info/";
    private static Logger log = Logger.getLogger(Access_DL.class);
    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        HttpClient client = new HttpClient();
        String reqUrl=URL_DL+"?app_id="+parameter.get("app_id")+"&mid="+parameter.get("mid")+"&token="+parameter.get("token")+"&sig="+parameter.get("sig");
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        GetMethod method = new GetMethod(reqUrl);       
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
        String ret = null;
        try {
            client.executeMethod(method);
            ret = method.getResponseBodyAsString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            method.abort();
            method.releaseConnection();
        }
        return ret;
    }
    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_DL channelInfo) {
    	LoginResult channelLoginResult = new LoginResult();
    	Map<String, Object> parameter = new HashMap<String, Object>();
        String mid=channelInfo.getParameter()[0];
        parameter.put("mid", mid);
        String token=channelInfo.getParameter()[1];
        parameter.put("token", token);
        parameter.put("app_id", channelInfo.getAppId());
        /**
         * 如果mid与token也就是参数1与参数2相等，表示这是旧版本验证，直接返回成功。
         */
        if(mid.equals(token)){
        	channelLoginResult.setCode(Common.STATUS_SUCCESS);
        	channelLoginResult.setMessage(Common.STATUS_SUCCESS);
        	return channelLoginResult;
        }       
        
        
        String sign_content=token+"|"+channelInfo.getAppKey();
        parameter.put("sig", HexBin.HashToMD5Hex(sign_content));
        
        try {
            String ret = (String) this.channelLogin(parameter);
            // 当乐返回的全部信息
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject = JSONObject.fromObject(ret);
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            if (!"0".equals(jsonObject.getString("error_code"))) {
            	log.error("当乐登陆验签失败 ：app_id="+parameter.get("app_id")+"&mid="+parameter.get("mid")+"&token="+parameter.get("token")+"&sig="+parameter.get("sig"));
                channelLoginResult.setCode(Common.STATUS_FAIL);
            }
            channelLoginResult.setMessage(jsonObject == null ? null : jsonObject.toString());
        } catch (IOException e) {
        	log.error("当乐登陆验签发生异常 ："+e.getMessage());
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
}
