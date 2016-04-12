package com.wyd.channel.service.impl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import net.sf.json.JSONObject;
import com.wyd.channel.bean.Data;
import com.wyd.channel.info.ChannelInfo_UC;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.result.Result_UC;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HexBin;
import com.wyd.channel.utils.HttpClientUtil;
/**
 * 类 <code>Access_UC</code> UC渠道接入基础类
 * @author sunzx
 *
 */
public class Access_UC implements IAccessService {
    /** UC渠道登录验证URL*/
    private static final String URL_UC     = "http://sdk.g.uc.cn/ss/";
    /** UC渠道编码*/
    public static final int     CHANNEl_UC = 1003;

    // UC 接入所需参数
    // public static final String CPID = "20037";
    // public static final String GAMEID = "106258";
    // public static final String SERVICEID = "1324";
    // public static final String APIKEY = "f03056aab926ec81ea60aee47627e82c";
    @Override
    public Object channelLogin(Map<String, Object> parameter) throws IOException {
        String sid = parameter.get("sid").toString();
        String cpid = parameter.get("cpid").toString();
        String gameid = parameter.get("gameid").toString();
        String serviceid = parameter.get("serviceid").toString();
        String apikey = parameter.get("apikey").toString();
        StringBuffer dataValue = new StringBuffer();
        dataValue.append("{");
        dataValue.append("\"id\":");
        dataValue.append(System.currentTimeMillis());
        dataValue.append(",");
        dataValue.append("\"service\":");
        dataValue.append("\"ucid.user.sidInfo\"");
        dataValue.append(",");
        dataValue.append("\"data\":");
        dataValue.append("{\"sid\":\"" + sid + "\"}");
        dataValue.append(",");
        dataValue.append("\"game\":");
        dataValue.append("{\"cpId\":" + cpid + ",\"gameId\":" + gameid + ",\"channelId\":\"2\",\"serverId\":" + serviceid + "}");
        dataValue.append(",");
        dataValue.append("\"sign\":");
        dataValue.append("\"" + HexBin.HashToMD5Hex(cpid + "sid=" + sid + apikey).toLowerCase() + "\"");
        dataValue.append("}");
        try {
            return HttpClientUtil.PostData(URL_UC, dataValue.toString());
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
    public LoginResult getUserLoginResult(ChannelInfo_UC channelInfo) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("sid", channelInfo.getParameter()[1]);
        parameter.put("cpid", channelInfo.getCpId());
        parameter.put("gameid", channelInfo.getGameId());
        parameter.put("serviceid", channelInfo.getServiceId());
        parameter.put("apikey", channelInfo.getApiKey());
        Result_UC result_uc = null;
        LoginResult channelLoginResult = new LoginResult();
        try {
            String ret = (String) this.channelLogin(parameter);
            channelLoginResult.setThirdReturnMessage(ret);
            JSONObject jsonObject = JSONObject.fromObject(ret);
            result_uc = (Result_UC) JSONObject.toBean(jsonObject, Result_UC.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        channelLoginResult.setCode(Common.STATUS_SUCCESS);
        if (null == result_uc || result_uc.getData()==null || !StringUtils.hasText(result_uc.getData().getUcid())) {
            result_uc = new Result_UC();
            com.wyd.channel.bean.State state = new com.wyd.channel.bean.State();
            //state.setCode(Integer.parseInt(Common.STATUS_FAIL));
            channelLoginResult.setCode(Common.STATUS_FAIL);
            state.setMsg(Common.STATUS_FAIL_MESSAGE);
            Data data = new Data();
            data.setUcid("-1");
            data.setNickName("");
            result_uc.setState(state);
            result_uc.setData(data);
        }       
        channelLoginResult.setMessage(result_uc.getData().getUcid());
        return channelLoginResult;
    }
    
    public static void main(String[] args){
        ChannelInfo_UC channelInfo = new ChannelInfo_UC();
        channelInfo.setCpId("20037");
        channelInfo.setGameId("106258");
        channelInfo.setServiceId("1324");
        channelInfo.setApiKey("f03056aab926ec81ea60aee47627e82c");
        String[] parameter = new String[]{"sst19cbb7715-30d1-4dc1-8c8f-575e2d8f760c105399"};
        channelInfo.setParameter(parameter);
        channelInfo.setChannel(1003);
        Access_UC access_uc = new Access_UC();
        LoginResult channelLoginResult = access_uc.getUserLoginResult((ChannelInfo_UC) channelInfo);
        System.out.println("code: " + channelLoginResult.getCode());
        System.out.println("message: " + channelLoginResult.getMessage());
    }
}
