package com.wyd.channel.info;
/**
 * 渠道信息
 * @author sunzx
 *
 */
public class ChannelInfo {
    private int      channel;  // 渠道编号
    private String[] parameter; // 渠道参数

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String[] getParameter() {
        return parameter;
    }

    public void setParameter(String[] parameter) {
        this.parameter = parameter;
    }
}
