package com.wyd.channel.service.impl;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.wyd.channel.info.ChannelInfo_BDGAME;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
/**
 * 百度游戏渠道信息
 * 百度文档v2.5
 */
public class Access_BDGAME{

    /** 百度游戏渠道编码*/
    public static final int     CHANNEl_BDGAME = 1101;
    private static Logger log = Logger.getLogger(Access_BDGAME.class);
    /**
     * 
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_BDGAME channelInfo) {
        LoginResult channelLoginResult = new LoginResult();
        try {
            String secret_key=channelInfo.getSecret_key();
            String api_key=channelInfo.getApi_key();
            String[] parameters=channelInfo.getParameter();
            String uid=parameters[0];
            String []param=parameters[1].split("\\+");
            String sid=param[0];
            String timestamp=param[1];
            String serverUrl=param[2];
            //注意如果没有值就要是null，否则后面加密会不一致。
            serverUrl=StringUtils.hasText(serverUrl)?serverUrl:"null";
            String sign=param[3];
            String signContent=secret_key+"api_key"+api_key+ "server_id"+sid+"timestamp"+timestamp+"user_id"+uid+"server_url"+serverUrl;
            String checkSign=HexBin.HashToMD5Hex(signContent);
            if(checkSign.equalsIgnoreCase(sign)){
            	log.info("百度登陆验证成功！");
                channelLoginResult.setCode(Common.STATUS_SUCCESS);
                channelLoginResult.setMessage(Common.STATUS_SUCCESS);
            }else{
            	log.error("百度登陆验签失败 "+sign+" | "+checkSign+" | "+signContent);
                channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
            }
        } catch (Exception e) {
        	log.error("百度登陆验签发生异常 ："+e.getMessage());
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
   
}
