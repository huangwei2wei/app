package com.wyd.channel.service.impl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import net.sf.json.JSONObject;
import com.wyd.channel.info.ChannelInfo_LX;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
/**
 * 类 <code>Access_LX</code> 联想乐商城渠道接入基础类
 * @author sunzx
 *
 */
public class Access_LX implements IAccessService {
    /** 联想乐商城渠道号*/
    public static final int    CHANNEl_LX = 1083;
    /** 联想乐商城登录验证地址*/
    public static final String URL_LX     = "https://passport.lenovo.com/interserver/authen/1.2/getaccountid";

    // public static final String REALM="dandandao.zhwyd.com";
    @Override
    public JSONObject channelLogin(Map<String, Object> parameter) throws IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(URL_LX);
        try {
            postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 20000);
            postMethod.addParameter("lpsust", parameter.get("lpsust").toString());
            postMethod.addParameter("realm", parameter.get("realm").toString());
            httpClient.executeMethod(postMethod);
            String retData = postMethod.getResponseBodyAsString();
            Map<Object, Object> map = new HashMap<Object, Object>();
            map.put("thirdReturnMessage", retData);
            int statusCode = postMethod.getStatusCode();
            map.put("statusCode", statusCode);
            Document doc = Jsoup.parse(retData);
            if (statusCode == 200) {
                Element identityInfo = doc.getElementsByTag("IdentityInfo").first();
                map.put("AccountID", identityInfo.getElementsByTag("AccountID").first().text());
                if (identityInfo.getElementsByTag("Username").first() != null) {
                    map.put("Username", identityInfo.getElementsByTag("Username").first().text());
                }
                if (identityInfo.getElementsByTag("DeviceID").first() != null) {
                    map.put("DeviceID", identityInfo.getElementsByTag("DeviceID").first().text());
                }
                map.put("verified", identityInfo.getElementsByTag("verified").first().text());
            } else {
                Element error = doc.getElementsByTag("Error").first();
                map.put("Code", error.getElementsByTag("Code").first().text());
                map.put("Timestamp", error.getElementsByTag("Timestamp").first().text());
                if (error.getElementsByTag("Message").first() != null){
                    map.put("Message", error.getElementsByTag("Message").first().text());
                }
				if (error.getElementsByTag("Detail").first() != null)
					map.put("Detail", error.getElementsByTag("Detail").first().text());
				if (error.getElementsByTag("Source").first() != null)
					map.put("Source", error.getElementsByTag("Source").first().text());
				if (error.getElementsByTag("URL").first() != null)
					map.put("URL", error.getElementsByTag("URL").first().text());
            }
            return JSONObject.fromObject(map);
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
    public LoginResult getUserLoginResult(ChannelInfo_LX channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("lpsust", channelInfo.getParameter()[1]);
        parameter.put("realm", channelInfo.getRealm());
        LoginResult channelLoginResult = new LoginResult();
        try {
            JSONObject jsonObject = this.channelLogin(parameter);
            //百度多酷返回的全部信息
            channelLoginResult.setThirdReturnMessage(jsonObject.get("thirdReturnMessage").toString());
            //转成jsonObject
            jsonObject.remove("thirdReturnMessage");
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            if(!"200".equals(jsonObject.get("statusCode").toString())){
                channelLoginResult.setCode(Common.STATUS_FAIL);
            }
            jsonObject.remove("statusCode");
            //channelLoginResult.setMessage(jsonObject == null ? null : jsonObject.toString());
            channelLoginResult.setMessage(jsonObject == null ? null : jsonObject.getString("AccountID"));

        } catch (IOException e) {
            e.printStackTrace();
            channelLoginResult.setCode(Common.STATUS_FAIL);
            channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
        }
        return channelLoginResult;
    }
}
