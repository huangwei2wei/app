package com.wyd.channel.service.impl;
import com.wyd.channel.info.ChannelInfo;
import com.wyd.channel.info.ChannelInfo_360;
import com.wyd.channel.info.ChannelInfo_91;
import com.wyd.channel.info.ChannelInfo_BD;
import com.wyd.channel.info.ChannelInfo_BDGAME;
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
import com.wyd.channel.utils.Common;
/**
 * @author zengxc
 * @version 创建时间：2014-3-13
 */
public class ChannelService {
	
    /**
     * 渠道登录验证
     * @param channelInfo   渠道信息<br/>
     * 具体说明如下：<br/>
     * 91渠道需要设置appId,appKey,parameter三个值<br/>
     * @return              验证结果
     */
    public static LoginResult channelLogin(ChannelInfo channelInfo) {
        int channel = channelInfo.getChannel();
        LoginResult channelLoginResult = new LoginResult();
        switch (channel) {
        case Access_91.CHANNEl_91_JJ:
        case Access_91.CHANNEl_91_IOS:
        case Access_91.CHANNEl_91_ANDROID:
            Access_91 access_91 = new Access_91();
            channelLoginResult = access_91.getUserLoginResult((ChannelInfo_91) channelInfo);
            break;
        case Access_UC.CHANNEl_UC:
            Access_UC access_uc = new Access_UC();
            channelLoginResult = access_uc.getUserLoginResult((ChannelInfo_UC) channelInfo);
            break;
        case Access_360.CHANNEl_360:
            Access_360 access_360 = new Access_360();
            channelLoginResult = access_360.getUserLoginResult((ChannelInfo_360) channelInfo);
            break;
        case Access_XM.CHANNEl_XM:
            Access_XM access_XM = new Access_XM();
            channelLoginResult = access_XM.getUserLoginResult((ChannelInfo_XM) channelInfo);
            break;
        case Access_MP.CHANNEl_MP:
            Access_MP access_MP = new Access_MP();
            channelLoginResult = access_MP.getUserLoginResult((ChannelInfo_MP)channelInfo);
            break;
        case Access_BD.CHANNEl_BD:
            Access_BD access_BD = new Access_BD();
            channelLoginResult = access_BD.getUserLoginResult((ChannelInfo_BD) channelInfo);
            break;
        case Access_PP.CHANNEl_PP:
            Access_PP access_PP = new Access_PP();
            channelLoginResult = access_PP.getUserLoginResult(channelInfo);
            break;
        case Access_WDJ.CHANNEl_WDJ:
            Access_WDJ access_WDJ = new Access_WDJ();
            channelLoginResult = access_WDJ.getUserLoginResult(channelInfo);
            break;
        case Access_LX.CHANNEl_LX:
            Access_LX access_LX = new Access_LX();
            channelLoginResult = access_LX.getUserLoginResult((ChannelInfo_LX) channelInfo);
            break;
        case Access_GF.CHANNEl_GF:
            Access_GF access_GF = new Access_GF();
            channelLoginResult = access_GF.getUserLoginResult(channelInfo);
            break;
        case Access_FL.CHANNEl_FL:
            Access_FL access_FL = new Access_FL();
            channelLoginResult = access_FL.getUserLoginResult((ChannelInfo_FL) channelInfo);
            break;
        case Access_SY.CHANNEl_SY:
            Access_SY access_SY = new Access_SY();
            channelLoginResult = access_SY.getUserLoginResult((ChannelInfo_SY) channelInfo);
            break;
        case Access_CY.CHANNEl_CY:
            Access_CY access_CY = new Access_CY();
            channelLoginResult = access_CY.getUserLoginResult((ChannelInfo_CY) channelInfo);
            break;
        case Access_DL.CHANNEl_DL:
            Access_DL access_DL = new Access_DL();
            channelLoginResult = access_DL.getUserLoginResult((ChannelInfo_DL) channelInfo);
            break;
        case Access_BDGAME.CHANNEl_BDGAME:
        	Access_BDGAME access_BDGAME = new Access_BDGAME();
            channelLoginResult = access_BDGAME.getUserLoginResult((ChannelInfo_BDGAME) channelInfo);
            break;
        case Access_YYH.CHANNEl_YYH:
        	Access_YYH access_YYH = new Access_YYH();
            channelLoginResult = access_YYH.getUserLoginResult((ChannelInfo_YYH) channelInfo);
            break;
        case Access_YAYAWAN.CHANNEl:
        	Access_YAYAWAN access_YAYAWAN = new Access_YAYAWAN();
            channelLoginResult = access_YAYAWAN.getUserLoginResult((ChannelInfo_YAYAWAN) channelInfo);
            break;
        case Access_EFJP.CHANNEl_EFJP:
            Access_EFJP access_EFJP = new Access_EFJP();
            channelLoginResult = access_EFJP.getUserLoginResult((ChannelInfo_EFJP) channelInfo);
            break;
        case Access_VTC.CHANNEl_IOS:
        case Access_VTC.CHANNEl_ANDROID:
        	Access_VTC access_VTC = new Access_VTC();
            channelLoginResult = access_VTC.getUserLoginResult((ChannelInfo_VTC) channelInfo);
            break;
        case Access_AS.CHANNEl:
        	Access_AS access_as = new Access_AS();
            channelLoginResult = access_as.getUserLoginResult(channelInfo);
            break;
        default:
            //客户端预留了一些需要服务端验证的渠道，但目前还不需要验证，默认返回验证成功
            channelLoginResult.setCode(Common.STATUS_SUCCESS);
            channelLoginResult.setMessage(Common.STATUS_SUCCESS);
            break;
        }
        return channelLoginResult;
    }
}
