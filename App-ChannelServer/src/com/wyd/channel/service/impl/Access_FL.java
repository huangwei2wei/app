package com.wyd.channel.service.impl;
import com.wyd.channel.info.ChannelInfo_FL;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.RSAUtil_FL;

public class Access_FL {
    /* 飞流渠道编码 */
    public static final int     CHANNEl_FL  = 1077;
    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_FL channelInfo) {
        LoginResult channelLoginResult = new LoginResult();
        try {
            String[] parameters=channelInfo.getParameter();
            String uuid=parameters[0];
            int index=parameters[1].indexOf("[FLINDEX]");
            String timestamp=parameters[1].substring(0,index);
            String encryptSign=parameters[1].substring(index+"[FLINDEX]".length());;
            String gameId=channelInfo.getGameId();
            String decryptSign=RSAUtil_FL.decrypt(Integer.valueOf(gameId),encryptSign);
            if(uuid.equals(decryptSign.split("&")[1])&&timestamp.equals(decryptSign.split("&")[0])){
                channelLoginResult.setCode(Common.STATUS_SUCCESS);
                channelLoginResult.setMessage(Common.STATUS_SUCCESS);
            }else{
                channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
    
}
