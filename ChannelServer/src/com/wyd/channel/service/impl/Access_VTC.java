package com.wyd.channel.service.impl;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.wyd.channel.info.ChannelInfo_VTC;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HttpClientUtil.MySSLProtocolSocketFactory;
/**
 * VTC越南语
 * @author zengxc
 *
 */
public class Access_VTC {
    /** 安卓 渠道编码*/
    public static final int     CHANNEl_ANDROID = 1174;
    /** IOS 渠道编码*/
    public static final int     CHANNEl_IOS     = 1175;
    //TODO:正式服要修改
    /** 登录验证URL*/
    private static final String LOGIN_URL       = "http://test.authen.ombom.goplay.vn/Login/";
    private static final String AUTO_URL        = "http://test.authen.ombom.goplay.vn/AutoLogin/";
    private static Logger log = Logger.getLogger(Access_VTC.class);
    public Object channelLogin(String reqUrl) throws IOException {
        HttpClient client = new HttpClient();        
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
    public LoginResult getUserLoginResult(ChannelInfo_VTC channelInfo) {
    	//
    	LoginResult channelLoginResult = new LoginResult();
    	String parameterJosnStr=channelInfo.getParameter()[0];
    	JSONObject parameterJosn = JSONObject.fromObject(parameterJosnStr);
        String userid=parameterJosn.getString("userid");
        String username=parameterJosn.getString("userName");
        String md5key=parameterJosn.getString("md5key");
        String cpid=parameterJosn.getString("cpid");        
        String checksum = parameterJosn.getString("checksum");
        String parameter="userid="+userid+"&username="+username+"&md5key="+md5key+"&cpid="+cpid+"&ipclient="+channelInfo.getClientIP()+"&ostype="+channelInfo.getOstype();
        String url=LOGIN_URL;
        //checksum 有值表明是auto login
        if(StringUtils.hasText(checksum)){
        	url=AUTO_URL;
        	parameter="userid="+userid+"&username="+username+"&checksum="+checksum+"&cpid="+cpid+"&ipclient="+channelInfo.getClientIP()+"&ostype="+channelInfo.getOstype();
        }        
        String reqUrl = url+"?"+parameter;        
        try {
            String ret = (String) channelLogin(reqUrl);
            // VTC返回的全部信息
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject = JSONObject.fromObject(ret);
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            int code = jsonObject.getInt("code"); 
            if (code<0) {
            	log.error("越南语登陆验签失败("+code+") ："+parameter);
                channelLoginResult.setCode(Common.STATUS_FAIL);
            }
            channelLoginResult.setMessage(ret);
        } catch (IOException e) {
        	log.error("越南语登陆验签发生异常 ："+e.getMessage());
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
}
