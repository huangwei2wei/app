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

import com.wyd.channel.info.ChannelInfo_YYH;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HttpClientUtil.MySSLProtocolSocketFactory;
/**
 * 应用汇登陆
 * @author zengxc
 *
 */
public class Access_YYH implements IAccessService{
    /** 应用汇 渠道编码*/
    public static final int     CHANNEl_YYH   = 1039;
    public static final String  URL_YYH      = "http://api.appchina.com/appchina-usersdk/user/get.json";
    private static Logger log = Logger.getLogger(Access_YYH.class);
	@Override
	public Object channelLogin(Map<String, Object> parameter)
			throws IOException {
		HttpClient client = new HttpClient();
        String reqUrl=URL_YYH+"?app_id="+parameter.get("app_id")+"&app_key="+parameter.get("app_key")+"&ticket="+parameter.get("ticket");
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        GetMethod method = new GetMethod(reqUrl);       
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
        String ret = null;
        try {
            client.executeMethod(method);
            ret = method.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("应用汇http请求出错："+ex.getMessage());
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
    public LoginResult getUserLoginResult(ChannelInfo_YYH channelInfo) {
    	LoginResult channelLoginResult = new LoginResult();
    	Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("ticket", channelInfo.getParameter()[1]);
        parameter.put("app_id", channelInfo.getAppId());
        parameter.put("app_key", channelInfo.getAppKey());
        try {
            String ret = (String) this.channelLogin(parameter);
            // 应用汇返回的JSON格式信息
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject = JSONObject.fromObject(ret);
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            if (!"0".equals(jsonObject.getString("status"))) {
            	log.error("应用汇登陆验签失败 ：app_id="+parameter.get("app_id")+"&app_key="+parameter.get("app_key")+"&ticket="+parameter.get("ticket"));
                channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage(jsonObject.getString("message"));
            }
            channelLoginResult.setMessage(jsonObject == null ? null : jsonObject.toString());
        } catch (IOException e) {
        	log.error("应用汇登陆验签发生异常 ："+e.getMessage());
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(e.getMessage());
        }
        return channelLoginResult;
    }

}
