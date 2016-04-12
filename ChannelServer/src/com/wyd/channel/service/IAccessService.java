package com.wyd.channel.service;
import java.io.IOException;
import java.util.Map;
/**
 * 渠道登录接口
 * @author Administrator
 */
public interface IAccessService {
    /**
     * 渠道登录验证
     * @param parameter 验证参数
     * @return          验证结果
     */
    public Object channelLogin(Map<String, Object> parameter) throws IOException;
}
