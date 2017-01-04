//package com.app.empire.scene.service.campaign;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.chuangyou.xianni.entity.campaign.CampaignTaskTemplateInfo;
//import com.chuangyou.xianni.sql.dao.CampaignTaskTemplateInfoDao;
//import com.chuangyou.xianni.sql.dao.DBManager;
//
//public class CampaignTaskTempMgr {
//	private static Map<Integer, CampaignTaskTemplateInfo> taskTemp = new HashMap<>();
//
//	public static boolean init() {
//		CampaignTaskTemplateInfoDao dao = DBManager.getCampaignTaskTemplateInfoDao();
//		List<CampaignTaskTemplateInfo> ctinfos = dao.getAll();
//		if (ctinfos != null) {
//			for (CampaignTaskTemplateInfo info : ctinfos) {
//				taskTemp.put(info.getTaskId(), info);
//			}
//		}
//		return true;
//	}
//
//	public static CampaignTaskTemplateInfo get(int taskId) {
//		return taskTemp.get(taskId);
//	}
// }
