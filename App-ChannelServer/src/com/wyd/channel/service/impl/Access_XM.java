package com.wyd.channel.service.impl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
import com.wyd.channel.info.ChannelInfo_XM;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
import com.wyd.channel.utils.HttpClientUtil;
/**
 * 小米渠道信息
 * @author sunzx
 *
 */
public class Access_XM implements IAccessService {
    /** 小米渠道编码*/
    public static final int     CHANNEl_XM = 1049;
    /** 小米登录验证地址*/
    private static final String URL_XM     = "http://mis.migc.xiaomi.com/api/biz/service/verifySession.do";

    // 小米 接入所需参数
    // public static final String APPID = "10831";
    // public static final String APPKEY = "8a374ca4-dba8-4898-ded9-516e44cceccd";
    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        StringBuffer dataValue = new StringBuffer();
        dataValue.append(URL_XM);
        dataValue.append("?");
        Set<String> setHead = parameter.keySet();
        Iterator<String> iteratorHead = setHead.iterator();
        while (iteratorHead.hasNext()) {
            String key = iteratorHead.next();
            String value = (String) parameter.get(key);
            dataValue.append(key);
            dataValue.append("=");
            dataValue.append(value);
            dataValue.append("&");
        }
        try {
            String ret = HttpClientUtil.GetData(dataValue.toString());
            return ret;
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
    public LoginResult getUserLoginResult(ChannelInfo_XM channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("appId", channelInfo.getAppId());
        parameter.put("session", channelInfo.getParameter()[1]);
        parameter.put("uid", channelInfo.getParameter()[0]);
        StringBuffer bf = new StringBuffer();
        bf.append("appId=").append(channelInfo.getAppId()).append("&session=").append(channelInfo.getParameter()[1]).append("&uid=").append(channelInfo.getParameter()[0]);
        LoginResult channelLoginResult = new LoginResult();
        try {
            String signature = HexBin.HmacSHA1Encrypt(bf.toString(), channelInfo.getAppKey());
            parameter.put("signature", signature);
            String ret= (String) this.channelLogin(parameter);
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject= JSONObject.fromObject(ret);
            if(!"200".equals(jsonObject.get("errcode").toString())){
                channelLoginResult.setCode(Common.STATUS_FAIL);
                channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
                return channelLoginResult;
            }
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            channelLoginResult.setMessage(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
}
