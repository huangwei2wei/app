package com.wyd.channel.service.impl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import net.sf.json.JSONObject;
import com.wyd.channel.info.ChannelInfo_MP;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
/**
 * 类 <code>Access_MP</code> 冒泡渠道接入基础类
 * @author sunzx
 *
 */
public class Access_MP implements IAccessService {
    /** 冒泡渠道编码*/
    public static final int     CHANNEl_MP = 1034;
    /** 冒泡渠道登录验证URL*/
    private static final String URL_MP     = "http://sdkpassport.51mrp.com/ticket";

    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(URL_MP);
        try {
            postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
            String data=JSONObject.fromObject(parameter).toString();
            if (null != data && data.length() > 0) {
                byte[] dataByte = data.getBytes();
                InputStream inputStream = new ByteArrayInputStream(dataByte);
                RequestEntity re = new InputStreamRequestEntity(inputStream, dataByte.length);
                postMethod.setRequestEntity(re);
            }
            httpClient.executeMethod(postMethod);
            String ret=postMethod.getResponseBodyAsString();
            return ret;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException(ex.getMessage());
        } finally {
            postMethod.abort();
            postMethod.releaseConnection();
        }
    }

    /**
     * 获取用户登录结果
     * @param channelInfo   渠道信息
     * @return              用户登录结果
     */
    public LoginResult getUserLoginResult(ChannelInfo_MP channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("skyid", channelInfo.getParameter()[0]);
        String[] str = channelInfo.getParameter()[1].split("\\+");
        parameter.put("appid", str[0]);
        parameter.put("ticket", str[1]);
        String signString = mpSign(parameter,channelInfo);
        parameter.put("sign", signString);
        LoginResult channelLoginResult = new LoginResult();
        try {
            String ret =(String) this.channelLogin(parameter);
            channelLoginResult.setThirdReturnMessage(ret.toString());
            JSONObject jsonObject=JSONObject.fromObject(ret);
            String statusMessage=null;
            if("200".equals(jsonObject.get("code").toString())){
                statusMessage="通讯正常";
                channelLoginResult.setCode(Common.STATUS_SUCCESS);
            }else if("602".equals(jsonObject.get("code").toString())){
                channelLoginResult.setCode(Common.STATUS_FAIL);
                statusMessage="签名不正确";
            }else if("604".equals(jsonObject.get("code").toString())){
                channelLoginResult.setCode(Common.STATUS_FAIL);
                statusMessage="系统错误";
            }else if("605".equals(jsonObject.get("code").toString())){
                channelLoginResult.setCode(Common.STATUS_FAIL);
                statusMessage="提交的数据错误";}
            channelLoginResult.setMessage(statusMessage);
        } catch (IOException e) {
            e.printStackTrace();
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
    
    
    /**
     * 冒泡签名方式
     * @param params
     * @return
     */
    private static String mpSign(Map<String, Object> params,ChannelInfo_MP channelInfo){
        ArrayList<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
        String key = keys.get(i);
        String value = params.get(key).toString();
        if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
        prestr = prestr + key + "=" + value;
        } else {
        prestr = prestr + key + "=" + value + "&";
        }
        }
        String sign = HexBin.HashToMD5Hex(prestr+"&sign="+channelInfo.getAppkey());
        return sign;
    }
}
