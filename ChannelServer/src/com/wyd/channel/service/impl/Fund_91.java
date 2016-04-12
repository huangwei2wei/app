package com.wyd.channel.service.impl;
import java.io.IOException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import com.wyd.channel.service.IFundService;
import com.wyd.channel.utils.HexBin;
import com.wyd.channel.utils.HttpClientUtil;
/**
 * 类 <code>Fund_91</code> 91基金接入基础类
 * @author sunzx
 *
 */
public class Fund_91 implements IFundService {
    private static final String URL_Fund_91 = "http://AccPoint.91.com/ForAP/AddV2.ashx";
    private Logger              log         = Logger.getLogger("fundlog");
    public static final String  APPID       = "103100";
    public static final String  APPKEY      = "26768671fa51baebc623bf10a9cc18ed651aba79c414dfcb";

    public Object addFund(String jsonDate) {
        StringBuffer dataValue = new StringBuffer();
        dataValue.append(URL_Fund_91);
        dataValue.append("?");
        dataValue.append("AppId=");
        dataValue.append(APPID);
        dataValue.append("&");
        dataValue.append("Act=1");
        dataValue.append("&");
        dataValue.append("Platform=1");
        dataValue.append("&");
        dataValue.append("Sign=");
        dataValue.append(HexBin.getMD5(jsonDate + APPKEY));
        JSONObject jsonObject = null;
        try {
            String ret = HttpClientUtil.PostData(dataValue.toString(), jsonDate);
            jsonObject = JSONObject.fromObject(ret);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public Object addFund(JSONObject jsonObject) {
        String remark = null;
        String uin = jsonObject.getString("uin");
        int playerId = jsonObject.getInt("playerid");
        int level = 0;
        int fund = 0;
        int ruleId = 0;
        switch (jsonObject.getInt("type")) {
        case 0:
            level = jsonObject.getInt("level");
            remark = "等级达到" + level + "级";
            switch (level) {
            case 10:
                ruleId = 2730;
                fund = 100;
                break;
            case 20:
                ruleId = 2731;
                fund = 300;
                break;
            case 30:
                ruleId = 2732;
                fund = 600;
                break;
            case 40:
                ruleId = 2733;
                fund = 1200;
                break;
            case 50:
                ruleId = 2734;
                fund = 2000;
                break;
            }
            break;
        case 1:
            int loginTime = jsonObject.getInt("logintime");
            remark = "连续登录" + loginTime + "天";
            switch (loginTime) {
            case 2:
                ruleId = 2726;
                fund = 100;
                break;
            case 3:
                ruleId = 2727;
                fund = 150;
                break;
            case 7:
                ruleId = 2728;
                fund = 250;
                break;
            case 15:
                ruleId = 2729;
                fund = 500;
                break;
            }
            break;
        case 2:
            ruleId = 2735;
            fund = 200;
            remark = "首次充值";
            break;
        case 3:
            ruleId = 2736;
            fund = 1400;
            remark = "单笔充值大于10元";
            break;
        }
        if (fund > 0) {
            JSONObject postData = new JSONObject();
            postData.put("RuleId", ruleId);
            postData.put("Uin", uin);
            postData.put("Point", fund);
            postData.put("Operate", remark);
            jsonObject = (JSONObject) addFund(postData.toString());
            StringBuffer sb = new StringBuffer();
            sb.append("playerId:");
            sb.append(playerId);
            sb.append("---uin:");
            sb.append(uin);
            sb.append("---ruleId:");
            sb.append(ruleId);
            sb.append("---fund:");
            sb.append(fund);
            sb.append("---remark:");
            sb.append(remark);
            sb.append("---return:");
            sb.append(jsonObject.toString());
            log.info(sb.toString());
        }
        return null;
    }
}
