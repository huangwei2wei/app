package com.wyd.channel.service.impl;
import com.wyd.channel.info.ChannelInfo_EFJP;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
public class Access_EFJP {
    /* EFUN日文渠道编码 */
    public static final int CHANNEl_EFJP = 1166;

    /**
     * 获取用户登录结果
     * @param channelInfo 渠道信息
     * @return 用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_EFJP channelInfo) {
        LoginResult channelLoginResult = new LoginResult();
        try {
            String appKey = channelInfo.getAppkey();
            String userid = channelInfo.getParameter()[0];
            String timestamp = channelInfo.getParameter()[1];
            String sign = channelInfo.getParameter()[2];
            String vk = HexBin.getMD5(appKey + userid + timestamp);
//            System.out.println("sign:" + sign + "-----vk:" + vk);
            if (sign.equalsIgnoreCase(vk)) {
                channelLoginResult.setCode(Common.STATUS_SUCCESS);
                channelLoginResult.setMessage(Common.STATUS_SUCCESS);
            } else {
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
