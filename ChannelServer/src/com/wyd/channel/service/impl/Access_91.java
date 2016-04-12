package com.wyd.channel.service.impl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import net.sf.json.JSONObject;
import com.wyd.channel.info.ChannelInfo_91;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.result.Result_91;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
import com.wyd.channel.utils.HttpClientUtil;
/**
 * 类 <code>Access_91</code> 91渠道接入基础类
 * @author sunzx
 *
 */
public class Access_91 implements IAccessService {
    private static Logger       log                = Logger.getLogger(Access_91.class);
    /** 91 IOS 渠道编码*/
    public static final int     CHANNEl_91_IOS     = 1004;
    /** 91 ANDROID 渠道编码*/
    public static final int     CHANNEl_91_ANDROID = 1025;
    /** 91 基金 渠道编码*/
    public static final int     CHANNEl_91_JJ      = 1066;
    /** 91 登录验证URL*/
    private static final String URL_91             = "http://service.sj.91.com/usercenter/AP.aspx";

    // 91 接入所需参数
    // public static final String APPID = "103100";
    // public static final String APPKEY = "26768671fa51baebc623bf10a9cc18ed651aba79c414dfcb";
    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        StringBuffer dataValue = new StringBuffer();
        dataValue.append(URL_91);
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
    public LoginResult getUserLoginResult(ChannelInfo_91 channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("AppId", channelInfo.getAppId());
        parameter.put("Act", "4"); // act为4时表示用户登录SessionId是否有效
        parameter.put("Uin", channelInfo.getParameter()[0]);
        parameter.put("SessionId", channelInfo.getParameter()[1]);
        parameter.put("Sign", this.getSign(channelInfo));
        Result_91 result_91 = null;
        LoginResult channelLoginResult = new LoginResult();
        try {
            String ret = (String) this.channelLogin(parameter);
            channelLoginResult.setThirdReturnMessage(ret);
            ret = ret.replace("ErrorCode", "errorCode");
            ret = ret.replace("ErrorDesc", "errorDesc");
            JSONObject jsonObject = JSONObject.fromObject(ret);
            result_91 = (Result_91) JSONObject.toBean(jsonObject, Result_91.class);
        } catch (Exception e) {
            log.error(e, e);
            result_91.setErrorCode(Common.STATUS_FAIL);
            result_91.setErrorDesc(Common.STATUS_FAIL_MESSAGE);
            e.printStackTrace();
        }
        if (!"1".equals(result_91.getErrorCode())) {
            channelLoginResult.setCode(Common.STATUS_FAIL);
        } else {
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
        }
        channelLoginResult.setMessage(channelInfo.getParameter()[0]);
        return channelLoginResult;
    }

    /**
     * 获取91渠道登录验证所需要加密串
     * @param channelInfo   渠道相关信息
     * @return              加密串
     */
    public String getSign(ChannelInfo_91 channelInfo) {
        StringBuffer sb = new StringBuffer();
        sb.append(channelInfo.getAppId());
        sb.append("4");// act为4时表示用户登录SessionId是否有效
        sb.append(channelInfo.getParameter()[0]);
        sb.append(channelInfo.getParameter()[1]);
        sb.append(channelInfo.getAppKey());
        return HexBin.HashToMD5Hex(sb.toString());
    }
}
