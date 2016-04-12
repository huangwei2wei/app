package com.wyd.channel.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.wyd.channel.info.ChannelInfo_YAYAWAN;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
/**
 *  YaYa玩登陆
 * @author zengxc
 *
 */
public class Access_YAYAWAN implements IAccessService{
    /** YaYa玩 渠道编码*/
    public static final int     CHANNEl   = 1098;
    public static final String  URL_YYW   = "http://passport.yayawan.com/oauth/userinfo";
    private static Logger log = Logger.getLogger(Access_YAYAWAN.class);
	@Override
	public Object channelLogin(Map<String, Object> parameter)
			throws IOException {
		HttpClient httpClient = new HttpClient();
	    PostMethod postMethod = new PostMethod(URL_YYW);
	    String ret = null;
	    try {
	    	postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
	    	postMethod.addParameter("uid",parameter.get("uid").toString());
	    	postMethod.addParameter("token",parameter.get("token").toString());
	    	postMethod.addParameter("app_id",parameter.get("app_id").toString());
	    	postMethod.addParameter("sign",parameter.get("sign").toString());
	    	httpClient.executeMethod(postMethod);
            ret = postMethod.getResponseBodyAsString();
        } catch (Exception ex) {
            log.error("YaYa玩http请求出错："+ex.getMessage());
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
    public LoginResult getUserLoginResult(ChannelInfo_YAYAWAN channelInfo) {
    	LoginResult channelLoginResult = new LoginResult();
    	Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("uid", channelInfo.getParameter()[0]);
        String token=channelInfo.getParameter()[1];
        parameter.put("token", token);
        parameter.put("app_id", channelInfo.getAppId());
        String sign_content=token+"|"+channelInfo.getAppKey();
        String checkSign=HexBin.HashToMD5Hex(sign_content);
        parameter.put("sign", checkSign);
        try {
            String ret = (String) this.channelLogin(parameter);
            if(!StringUtils.hasText(ret)){
            	log.error("YaYa玩登陆验签失败,返回值为空。 ：uid="+parameter.get("uid")+"&token="+parameter.get("token")+"&app_id="+parameter.get("app_id")+"&sign="+parameter.get("sign"));
            	channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage("YaYa玩登陆验签失败,返回值为空。 ：uid="+parameter.get("uid")+"&token="+parameter.get("token")+"&app_id="+parameter.get("app_id")+"&sign="+parameter.get("sign"));
                return channelLoginResult;
            }
            // 丫丫玩返回的JSON格式信息
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject = JSONObject.fromObject(ret);
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            if (!"0".equals(jsonObject.getString("error_code"))) {
            	log.error("YaYa玩登陆验签失败 ：uid="+parameter.get("uid")+"&token="+parameter.get("token")+"&app_id="+parameter.get("app_id")+"&sign="+parameter.get("sign"));
                channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage(ret);
            }
            channelLoginResult.setMessage(jsonObject == null ? null : jsonObject.toString());
        } catch (Exception e) {
        	log.error("YaYa玩登陆验签发生异常 ："+e.getMessage());
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(e.getMessage());
        }
        return channelLoginResult;
    }

}
