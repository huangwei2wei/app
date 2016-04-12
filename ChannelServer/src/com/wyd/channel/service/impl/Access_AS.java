package com.wyd.channel.service.impl;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.wyd.channel.info.ChannelInfo;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.utils.Common;
import com.wyd.channel.utils.HttpClientUtil;

/**
 * 类 <code>Access_AS</code> 爱思渠道接入基础类
 * 
 * @author zengxc
 * 
 */
public class Access_AS {
	private static Logger log = Logger.getLogger(Access_AS.class);
	/** 渠道编码 */
	public static final int CHANNEl = 1210;
	public static final String LOGIN_URL = "https://pay.i4.cn/member_third.action";

	public LoginResult getUserLoginResult(ChannelInfo channelInfo) {
		LoginResult channelLoginResult = new LoginResult();
		String token = channelInfo.getParameter()[1];
		try {
			String ret = HttpClientUtil.GetData(LOGIN_URL + "?token=" + token);
			JSONObject jsonObject = JSONObject.fromObject(ret);
			int status = jsonObject.getInt("status");
			channelLoginResult.setCode(Common.STATUS_SUCCESS);
			if (status != 0) {
				log.error("爱思渠道登陆失败：" + status);
				channelLoginResult.setCode(Common.STATUS_FAIL);
			}
			channelLoginResult.setMessage(ret);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("爱思渠道登陆出错：", e);
			channelLoginResult.setCode(Common.STATUS_FAIL);
			channelLoginResult.setMessage(Common.STATUS_FAIL_MESSAGE);
		}
		return channelLoginResult;
	}
}
