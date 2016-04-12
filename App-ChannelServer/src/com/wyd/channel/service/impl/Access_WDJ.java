package com.wyd.channel.service.impl;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import com.wyd.channel.info.ChannelInfo;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HttpClientUtil;
/**
 * 类 <code>Access_WDJ</code> 豌豆夹渠道接入基础类
 * @author sunzx
 *
 */
public class Access_WDJ implements IAccessService {
    /** 豌豆夹渠道编码*/
    public static final int    CHANNEl_WDJ = 1033;
    /** 豌豆夹渠道登录验证地址*/
    public static final String URL_WDJ     = "https://pay.wandoujia.com/api/uid/check";

    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        StringBuffer dataValue = new StringBuffer();
        dataValue.append(URL_WDJ);
        dataValue.append("?uid=");
        dataValue.append(parameter.get("uid").toString());
        dataValue.append("&token=");
        dataValue.append(URLEncoder.encode(parameter.get("token").toString()));
        try {
            return HttpClientUtil.GetData(dataValue.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("uid", channelInfo.getParameter()[0]);
        parameter.put("token", channelInfo.getParameter()[1]);
        LoginResult channelLoginResult = new LoginResult();
        try {
            String result = (String) this.channelLogin(parameter);
            channelLoginResult.setThirdReturnMessage(result);
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            channelLoginResult.setMessage(result);
            if("false".equals(result)){
                channelLoginResult.setCode(Common.STATUS_FAIL);
               channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE); 
            }
        } catch (IOException e) {
            e.printStackTrace();
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
}
