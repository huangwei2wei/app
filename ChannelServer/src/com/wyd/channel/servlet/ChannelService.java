package com.wyd.channel.servlet;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.wyd.channel.bean.ChannelLogin;
import com.wyd.channel.bean.ChannelLoginHandle;
import com.wyd.channel.bean.ChannelLoginResult;
import com.wyd.channel.bean.ThirdConfig;
import com.wyd.channel.info.ChannelInfo;
import com.wyd.channel.info.ChannelInfo_360;
import com.wyd.channel.info.ChannelInfo_91;
import com.wyd.channel.info.ChannelInfo_BD;
import com.wyd.channel.info.ChannelInfo_BDGAME;
import com.wyd.channel.info.ChannelInfo_BYKN;
import com.wyd.channel.info.ChannelInfo_CY;
import com.wyd.channel.info.ChannelInfo_DL;
import com.wyd.channel.info.ChannelInfo_EFJP;
import com.wyd.channel.info.ChannelInfo_FL;
import com.wyd.channel.info.ChannelInfo_LX;
import com.wyd.channel.info.ChannelInfo_MP;
import com.wyd.channel.info.ChannelInfo_SY;
import com.wyd.channel.info.ChannelInfo_UC;
import com.wyd.channel.info.ChannelInfo_VTC;
import com.wyd.channel.info.ChannelInfo_XM;
import com.wyd.channel.info.ChannelInfo_YAYAWAN;
import com.wyd.channel.info.ChannelInfo_YYH;
import com.wyd.channel.result.LoginResult;
import com.wyd.channel.service.IThirdConfigService;
import com.wyd.channel.service.factory.ServiceManager;
import com.wyd.channel.service.impl.Access_360;
import com.wyd.channel.service.impl.Access_91;
import com.wyd.channel.service.impl.Access_BD;
import com.wyd.channel.service.impl.Access_BDGAME;
import com.wyd.channel.service.impl.Access_BYKN;
import com.wyd.channel.service.impl.Access_CY;
import com.wyd.channel.service.impl.Access_DL;
import com.wyd.channel.service.impl.Access_EFJP;
import com.wyd.channel.service.impl.Access_FL;
import com.wyd.channel.service.impl.Access_LX;
import com.wyd.channel.service.impl.Access_MP;
import com.wyd.channel.service.impl.Access_SY;
import com.wyd.channel.service.impl.Access_UC;
import com.wyd.channel.service.impl.Access_VTC;
import com.wyd.channel.service.impl.Access_XM;
import com.wyd.channel.service.impl.Access_YAYAWAN;
import com.wyd.channel.service.impl.Access_YYH;
import com.wyd.channel.utils.ChannelUtil;

public class ChannelService {
	private Logger log;
	private static ChannelService instance = null;
	private ConcurrentHashMap<Integer, ChannelLoginHandle> handleMap;// KEY:对象ID,VAL:对象

	public ChannelService() {
		log = Logger.getLogger(ChannelService.class);
		handleMap = new ConcurrentHashMap<Integer, ChannelLoginHandle>();
	}

	public static ChannelService getInstance() {
		synchronized (ChannelService.class) {
			if (null == instance) {
				instance = new ChannelService();
			}
		}
		return instance;
	}

	public ChannelLoginHandle getLoginHandle(int id) {
		return handleMap.get(id);
	}

	/**
	 * 从集合里删除
	 * 
	 * @param handle
	 */
	public void remove(ChannelLoginHandle handle) {
		handleMap.remove(handle.getId());
	}

	/**
	 * 只删除已完成
	 * 
	 * @param handle
	 */
	public void complete(ChannelLoginHandle handle) {
		if (handle.getState() == 1)
			remove(handle);
	}

	public ChannelLoginHandle createLoginHandle(ChannelLogin loginParamter) {
		ChannelLoginHandle handle = new ChannelLoginHandle(loginParamter);
		handleMap.put(handle.getId(), handle);
		ServiceManager.getManager().getHttpThreadPool().execute(new ChannelThread(handle));
		return handle;
	}
	public ConcurrentHashMap<Integer, ChannelLoginHandle> getHandleMap() {
		return handleMap;
	}

	public class ChannelThread implements Runnable {
		private ChannelLoginHandle handle;

		public ChannelThread(ChannelLoginHandle handle) {
			this.handle = handle;
		}

		public void run() {
			channelLogin(handle);

		}

		/**
		 * 
		 * 渠道登录验证
		 * 
		 * @param channel
		 *            渠道编号
		 * @param parameter
		 *            登录验证参数
		 * @return
		 */
		public void channelLogin(ChannelLoginHandle handle) {
			try {
				IThirdConfigService thirdConfigService = ServiceManager.getManager().getThirdConfigService();
				ThirdConfig config;
				String appKey = null, appId = null;
				ChannelLogin channelLogin = handle.getLoginParamter();
				ChannelInfo channelInfo = new ChannelInfo();
				channelInfo.setChannel(channelLogin.getChannel());
				channelInfo.setParameter(channelLogin.getParameter());
				ChannelLoginResult channelLoginResult = new ChannelLoginResult();
				int channel = channelLogin.getChannel();
				String[] parameters = channelLogin.getParameter();
				for (int i = 0; i < parameters.length; i++) {
					log.info("渠道号： " + channel + " 客户端传过来第" + (i + 1) + "个参数：" + parameters[i]);
				}

				switch (channel) {
				case Access_91.CHANNEl_91_JJ:
					// client.setUin(channelInfo.getParameter()[0]);
				case Access_91.CHANNEl_91_IOS:
				case Access_91.CHANNEl_91_ANDROID:
					channelInfo = new ChannelInfo_91(ChannelUtil.APPID_91, ChannelUtil.APPKEY_91);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_UC.CHANNEl_UC:
					channelInfo = new ChannelInfo_UC(ChannelUtil.CPID_UC, ChannelUtil.GAMEID_UC, ChannelUtil.SERVICEID_UC, ChannelUtil.APIKEY_UC);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_360.CHANNEl_360:
					channelInfo = new ChannelInfo_360(ChannelUtil.GRANT_TYPE_360, ChannelUtil.CLIENT_ID_360, ChannelUtil.CLIENT_SECRET_360, ChannelUtil.REDIRECT_URI_360);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_XM.CHANNEl_XM:
					channelInfo = new ChannelInfo_XM(ChannelUtil.APPID_XM, ChannelUtil.APPKEY_XM);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_MP.CHANNEl_MP:
					channelInfo = new ChannelInfo_MP();
					((ChannelInfo_MP) channelInfo).setAppkey(ChannelUtil.APPKEY_MP);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_BD.CHANNEl_BD:
					channelInfo = new ChannelInfo_BD(ChannelUtil.APPID_BD, ChannelUtil.APPKEY_BD, ChannelUtil.APP_SECERT_BD);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_LX.CHANNEl_LX:
					channelInfo = new ChannelInfo_LX();
					((ChannelInfo_LX) channelInfo).setRealm(ChannelUtil.REALM_LX);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_FL.CHANNEl_FL:
					channelInfo = new ChannelInfo_FL();
					((ChannelInfo_FL) channelInfo).setGameId(ChannelUtil.GAMEID_FL);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_SY.CHANNEl_SY:
					channelInfo = new ChannelInfo_SY();
					((ChannelInfo_SY) channelInfo).setAppKey(ChannelUtil.APPKEY_SY);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_BDGAME.CHANNEl_BDGAME:
					channelInfo = new ChannelInfo_BDGAME(ChannelUtil.APIKEY_BDGAME, ChannelUtil.SECRETKEY_BDGAME);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_CY.CHANNEl_CY:
					// 动态读取key
					config = thirdConfigService.getThirdConfig(1, ChannelUtil.CHANNEl_CY);
					appKey = config == null ? ChannelUtil.APPKEY_CY : config.getGameKey();
					channelInfo = new ChannelInfo_CY();
					((ChannelInfo_CY) channelInfo).setAppkey(appKey);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_DL.CHANNEl_DL:
					// 动态读取key
					config = thirdConfigService.getThirdConfig(1, ChannelUtil.CHANNEl_DL);
					appKey = config == null ? ChannelUtil.APPKEY_DL : config.getGameKey();
					appId = config == null ? ChannelUtil.APPID_DL : config.getGameId();
					channelInfo = new ChannelInfo_DL();
					((ChannelInfo_DL) channelInfo).setAppId(appId);
					((ChannelInfo_DL) channelInfo).setAppKey(appKey);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_YYH.CHANNEl_YYH:
					// 动态读取key
					config = thirdConfigService.getThirdConfig(1, ChannelUtil.CHANNEl_YYH);
					appKey = config == null ? ChannelUtil.APPKEY_YYH : config.getGameKey();
					appId = config == null ? ChannelUtil.APPID_YYH : config.getGameId();
					channelInfo = new ChannelInfo_YYH(appId, appKey);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_YAYAWAN.CHANNEl:
					// 动态读取key
					config = thirdConfigService.getThirdConfig(1, ChannelUtil.CHANNEl_YAYAWAN);
					appKey = config == null ? ChannelUtil.APPKEY_YAYAWAN : config.getGameKey();
					appId = config == null ? ChannelUtil.APPID_YAYAWAN : config.getGameId();
					channelInfo = new ChannelInfo_YAYAWAN(appId, appKey);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_EFJP.CHANNEl_EFJP:
					appKey = ChannelUtil.APPKEY_EFUNJP;
					channelInfo = new ChannelInfo_EFJP(appKey);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_BYKN.CHANNEl_BYKN:
					channelInfo = new ChannelInfo_BYKN(ChannelUtil.GAMEID_BYKN, ChannelUtil.KEY_BYKN);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				case Access_VTC.CHANNEl_IOS:
				case Access_VTC.CHANNEl_ANDROID:
					String ostype = channel == Access_VTC.CHANNEl_ANDROID ? "1" : "2";
					// TODO:IP
					String ip = "192.168.1.1";
					channelInfo = new ChannelInfo_VTC(ip, ostype);
					channelInfo.setChannel(channel);
					channelInfo.setParameter(parameters);
					break;
				
				}

				LoginResult loginResult = com.wyd.channel.service.impl.ChannelService.channelLogin(channelInfo);
				channelLoginResult.setCode(loginResult.getCode() == null ? "" : loginResult.getCode());
				channelLoginResult.setMessage(loginResult.getMessage() == null ? "" : loginResult.getMessage());
				String thirdReturnMessage = loginResult.getThirdReturnMessage() == null ? "" : loginResult.getThirdReturnMessage();
				log.info("渠道号： " + channel + " 第三方平台服务器返回的message: " + thirdReturnMessage + " 服务端返回的code: " + channelLoginResult.getCode() + " 服务端返回的message：" + channelLoginResult.getMessage());
				// 处理完
				handle.setState(1);
				handle.setLoginResult(channelLoginResult);
			} catch (Exception ex) {
				ex.printStackTrace();
				handle.setState(1);
				handle.setLoginResult(new ChannelLoginResult("-2","发生异常："+ex.getMessage()));
			}
				
			
		}
	}
}
