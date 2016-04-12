package com.wyd.channel.service.impl;
import com.wyd.channel.info.ChannelInfo_SY;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.MD5_SY;

public class Access_SY {
    /* 飞流渠道编码 */
    public static final int     CHANNEl_SY  = 1088;
    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_SY channelInfo) {
        LoginResult channelLoginResult = new LoginResult();
        try {
            String[] parameters=channelInfo.getParameter();
            String userid=parameters[0];
            String[] str=parameters[1].split("&");
            String timestamp=str[0];
            String encryptSign=str[1];;
            String appkey=channelInfo.getAppKey();
            String checkSign=MD5_SY.getInstance().md5(userid+"&"+timestamp+"&"+appkey);
            if(checkSign.equals(encryptSign)){
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
