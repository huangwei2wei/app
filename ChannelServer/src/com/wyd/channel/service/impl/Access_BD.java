package com.wyd.channel.service.impl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import com.wyd.channel.info.ChannelInfo_BD;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.result.Result_BD;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
import com.wyd.channel.utils.HttpClientUtil;
/**
 * 类 <code>Access_BD</code> 多酷渠道接入基础类
 * @author sunzx
 *
 */
public class Access_BD implements IAccessService {
    /** 多酷渠道编码*/
    public static final int     CHANNEl_BD = 1064;
    /** 多酷渠道验证URL*/
    private static final String URL_BD     = "http://sdk.m.duoku.com/openapi/sdk/checksession";

    // BD 接入所需参数
    // public static final String APPID = "178";
    // public static final String APPKEY = "d5379a96ebcb418cafb0b248d1b0e12e";
    // public static final String APP_SECERT = "6ba3b523f5f6af0ad33b57be3a351435";
    
    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        StringBuffer dataValue = new StringBuffer();
        dataValue.append(URL_BD);
        dataValue.append("?appid=");
        dataValue.append(parameter.get("appid").toString());
        dataValue.append("&appkey=");
        dataValue.append(parameter.get("appkey").toString());
        dataValue.append("&uid=");
        dataValue.append(parameter.get("uid").toString());
        dataValue.append("&sessionid=");
        dataValue.append(parameter.get("sessionid").toString());
        dataValue.append("&clientsecret=");
        dataValue.append(parameter.get("clientsecret").toString());
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
    public LoginResult getUserLoginResult(ChannelInfo_BD channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("appid", channelInfo.getAppId());
        parameter.put("appkey", channelInfo.getAppKey());
        parameter.put("uid", channelInfo.getParameter()[0]);
        parameter.put("sessionid", channelInfo.getParameter()[1]);
        parameter.put("clientsecret", HexBin.HashToMD5Hex(this.getClientSecret(channelInfo)).toLowerCase());
        LoginResult channelLoginResult = new LoginResult();
        try {
            String ret=(String) this.channelLogin(parameter);
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject = JSONObject.fromObject(ret);
            Result_BD result_BD = (Result_BD) JSONObject.toBean(jsonObject, Result_BD.class);
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            channelLoginResult.setMessage(result_BD.getError_msg());
            String errorCode=result_BD.getError_code();
            if(!"0".equals(errorCode)){
                channelLoginResult.setCode(errorCode);
                channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }

    /**
     * 对参数对得加密并返回加密串
     * @param channelInfo   渠道信息
     * @return              加密串
     */
    public String getClientSecret(ChannelInfo_BD channelInfo) {
        StringBuffer clientsecret = new StringBuffer();
        clientsecret.append(channelInfo.getAppId());
        clientsecret.append(channelInfo.getAppKey());
        clientsecret.append(channelInfo.getParameter()[0]);
        clientsecret.append(channelInfo.getParameter()[1]);
        clientsecret.append(channelInfo.getAppSecrt());
        return clientsecret.toString();
    }
}
