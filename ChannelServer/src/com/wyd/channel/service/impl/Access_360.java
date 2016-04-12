package com.wyd.channel.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import com.wyd.channel.info.ChannelInfo_360;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IAccessService;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HttpClientUtil;

/**
 * 类 <code>Access_360</code> 360渠道接入基础类
 * 
 * @author sunzx
 * 
 */
public class Access_360 implements IAccessService {
	/* 360渠道编码 */
	public static final int CHANNEl_360 = 1018;
	private static final String URL_360 = "https://openapi.360.cn/oauth2/access_token";
	private static final String URL_360_INFO = "https://openapi.360.cn/user/me.json?access_token=";
	
	@Override
	public Object channelLogin(Map<String, Object> parameter) throws IOException {
		String code = parameter.get("code").toString();
		String gcrantType = parameter.get("gcrantType").toString();
		String clientId = parameter.get("clientId").toString();
		String clientSecret = parameter.get("clientSecret").toString();
		String redirectUri = parameter.get("redirectUri").toString();
		StringBuffer dataValue = new StringBuffer();
		dataValue.append(URL_360);
		dataValue.append("?grant_type=");
		dataValue.append(gcrantType);
		dataValue.append("&code=");
		dataValue.append(code);
		dataValue.append("&client_id=");
		dataValue.append(clientId);
		dataValue.append("&client_secret=");
		dataValue.append(clientSecret);
		dataValue.append("&redirect_uri=");
		dataValue.append(redirectUri);
		try {
			return HttpClientUtil.GetData(dataValue.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * 获取用户登录结果
	 * 
	 * @param channelInfo
	 *            渠道信息
	 * @return 用户登录结果
	 */
	public LoginResult getUserLoginResult(ChannelInfo_360 channelInfo) {
		JSONObject jobj = JSONObject.fromObject(channelInfo.getParameter()[1]);
		Map<String, Object> parameter = new HashMap<String, Object>();
		parameter.put("code", jobj.getString("code"));
		parameter.put("gcrantType", channelInfo.getGrantType());
		parameter.put("clientId", channelInfo.getClientId());
		parameter.put("clientSecret", channelInfo.getClientSecret());
		parameter.put("redirectUri", channelInfo.getRedirectUri());
		LoginResult channelLoginResult = new LoginResult();
		try {
			String ret = (String) this.channelLogin(parameter);
			if (ret == null || "".equals(ret)) {
				channelLoginResult.setCode(Common.STATUS_FAIL);
				channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
				return channelLoginResult;
			}
			channelLoginResult.setThirdReturnMessage(ret);
			System.out.println(ret);
			JSONObject jsonObject = JSONObject.fromObject(ret);
			try {
				String token = jsonObject.getString("access_token");
				ret = HttpClientUtil.GetData(URL_360_INFO + token);
				jsonObject = JSONObject.fromObject(ret);
				jsonObject.put("token", token);
				channelLoginResult.setCode(Common.STATUS_SUCCESS);
				channelLoginResult.setMessage(jsonObject.toString());
			} catch (Exception e) {
				e.printStackTrace();
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
