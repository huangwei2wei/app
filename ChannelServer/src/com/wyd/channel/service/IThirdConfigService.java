package com.wyd.channel.service;

import com.app.db.service.UniversalManager;
import com.wyd.channel.bean.ThirdConfig;

/**
 * 接口 <code>IThirdConfigService</code>第三方渠道配置信息业务逻辑层接口
 * @author zengxc
 */
public interface IThirdConfigService extends  UniversalManager{
    
    /**
     * 根据应用ID和渠道ID获取第三方充值配置信息
     * @param appId         应用ID     
     * @param channelId     渠道ID
     * @return
     */
    public ThirdConfig getThirdConfig(int appId, String channelId);
    
    /**
     * 根据第三方渠道应用ID和渠道ID获取第三方充值配置信息
     * @param appId         第三方渠道应用ID     
     * @param channelId     渠道ID
     * @return
     */
    public ThirdConfig getThirdConfigByGameId(String gameId, String channelId);
    
    
}
