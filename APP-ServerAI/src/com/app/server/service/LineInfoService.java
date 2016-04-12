package com.app.server.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.app.server.util.CryptionUtil;
/**
 * @author doter
 */
public class LineInfoService {

	/**
	 * 获取服务器线中人数最多但不满的
	 * 
	 * @param serverInfo
	 * @param version
	 * @return
	 */
	public Map<String, Object> getLineInfo(ServerInfo serverInfo, String version) {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		LineInfo lineInfo = null;

		// 分配线 （用户量多的）
		Map<Integer, LineInfo> lineMap = serverInfo.getLineMap();
		for (LineInfo info : lineMap.values()) {
			if (info.getMaintance())// 维护
				continue;
			// if (!info.getVersion().equals(version))// 版本不同不让获取
			// continue;
			int currOnline = info.getCurrOnline();// 当前在线人数
			int maxOnline = info.getMaxOnline();// 最大在线人数
			if (currOnline > 0 && maxOnline > 0) {
				if (currOnline >= maxOnline)
					continue;
			}
			if (lineInfo == null || currOnline > lineInfo.getCurrOnline())
				lineInfo = info;
		}

		if (null != lineInfo) {
			dataMap.put("address", lineInfo.getAddress());
			dataMap.put("version", lineInfo.getVersion());
			dataMap.put("area", lineInfo.getArea());
			dataMap.put("openudid", serverInfo.getConfig().getOpenudid());
			dataMap.put("group", lineInfo.getGroup());
			dataMap.put("serverId", lineInfo.getServerId());
			dataMap.put("lineId", lineInfo.getId());
			List<String> appendConfig = ServiceManager.getManager().getConfigService().getConfigList();
			dataMap.put("append", appendConfig);
		} else {
			// dataMap.put("bulletin", "人数已满");
			dataMap.put("bulletin", serverInfo.getConfig().getBulletin());
		}
		return dataMap;
	}
}
