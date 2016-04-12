package com.wyd.channel.service.impl;

import org.apache.log4j.Logger;

import com.wyd.channel.info.ChannelInfo_CY;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;

/**
 * 类 <code>Access_LX</code> 畅游渠道接入基础类
 *
 */
public class Access_CY {
    //畅游渠道号
    public static final int     CHANNEl_CY  = 1107;
    private static Logger log = Logger.getLogger(Access_CY.class);
    /**
     * 获取用户登录结果
     * 一，接收客户端传递过来的openId,ip,port,timestamp,sign
     * 二，把参数按openId+"|"+ip+"|"+port+"|"+timestamp+"|"+appkey拼装
     * 三，拼装后经过md5加密后与传递过来的sing对比如果一致就返回成功
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_CY channelInfo) {
        LoginResult channelLoginResult = new LoginResult();
        try {
            String[] parameters=channelInfo.getParameter();
            String[] param=parameters[1].split("&");
            String openId=parameters[0];
            String ip=param[0];
            String port=param[1];
            String timestamp=param[2];
            String sign=param[3];
            String appkey=channelInfo.getAppkey();
            String sign_content=openId+"|"+ip+"|"+port+"|"+timestamp+"|"+appkey;
            
            String checkSign=HexBin.HashToMD5Hex(sign_content);
            //equalsIgnoreCase：客户端传递过来的sign和服务端自行加密的存字母大小写不一致情况。
            if(checkSign.equalsIgnoreCase(sign)){
                channelLoginResult.setCode(Common.STATUS_SUCCESS);
                channelLoginResult.setMessage(Common.STATUS_SUCCESS);
            }else{
            	log.error("畅游登陆验签失败 "+sign+" | "+checkSign+" | "+sign_content);
                channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage("畅游登陆验签失败 "+sign+" | "+checkSign+" | "+sign_content);
            }
        } catch (Exception e) {
        	log.error("畅游登陆验签发生异常 ："+e.getMessage());
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(e.getMessage());
        }
        return channelLoginResult;
    }
}
