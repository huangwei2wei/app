package com.wyd.channel.service.impl;
import java.util.HashMap;
import java.util.Map;
import com.wyd.channel.info.ChannelInfo;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
/**
 * 类 <code>Access_GF</code> 机锋渠道接入基础类
 * @author sunzx
 *
 */
public class Access_GF implements IAccessService {
    /** 机锋渠道编号*/
    public static final int    CHANNEl_GF = 1005;
    public static final String URL_GF     = "";

    @Override
    public String channelLogin(Map<String, Object> parameter) {
        // StringBuffer request = new StringBuffer();
        // request.append("<request>");
        // request.append("<uid>");
        // request.append(parameter.get("UID").toString());
        // request.append("</uid>");
        // request.append("<username>");
        // request.append(parameter.get("UserName").toString());
        // request.append("</username>");
        // request.append("</request>");
        // String ret=DemoUtil.doEncryptRequest(request.toString(), URL_GF);
        return parameter.get("UID").toString();
    }

    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("UID", channelInfo.getParameter()[0]);
        parameter.put("UserName", channelInfo.getParameter()[1]);
        LoginResult channelLoginResult = new LoginResult();
        String ret = this.channelLogin(parameter);
        channelLoginResult.setThirdReturnMessage(ret);
        channelLoginResult.setCode(Common.STATUS_SUCCESS);
        channelLoginResult.setMessage(ret);
        return channelLoginResult;
    }
}
