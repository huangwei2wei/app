package com.app.server.service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.app.server.bean.ServerConfigBean;
public class ServerInfo {
    private ServerConfigBean             config;
    private Map<Integer, LineInfo>       lineMap;

    public ServerInfo() {
        lineMap = new ConcurrentHashMap<Integer, LineInfo>();
    }

    public ServerConfigBean getConfig() {
        return config;
    }

    public void setConfig(ServerConfigBean config) {
        this.config = config;
    }

    public Map<Integer, LineInfo> getLineMap() {
        return lineMap;
    }
}
