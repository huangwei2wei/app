package com.wyd.combined.server.impl;
import java.util.List;
import com.wyd.combined.bean.Empireaccount;
import com.wyd.combined.bean.Player;
import com.wyd.combined.server.factory.ServiceManager;
/**
 * @author zguoqiu
 * @version 创建时间：2013-4-16 上午10:34:38 类说明
 */
public class CombinedService {
    /**
     * 合服
     * 
     * @param s1
     * @param s2
     */
    public void combined(String s1, String s2) {
        int sid1 = Integer.parseInt(s1.split("_")[1]);
        int palyerCount = ServiceManager.getManager().getWorldService().getCountByAreaId(sid1);
        int pageCount = (int) Math.ceil(palyerCount / 1000d);
        for (int i = 0; i < pageCount; i++) {
            System.out.println("正在同步玩家:" + (i * 1000) + "/" + palyerCount);
            List<Object> playerList = ServiceManager.getManager().getWorldService().getPlayerByAreaId(sid1, i);
            for (Object obj : playerList) {
                Player palyer = (Player) obj;
                Empireaccount empireaccount1 = (Empireaccount) ServiceManager.getManager().getAccountService().get(Empireaccount.class, palyer.getAccountId());
                if (null == empireaccount1) {
                    continue;
                }
                int accountId = empireaccount1.getAccountId();
                Empireaccount empireaccount2 = ServiceManager.getManager().getAccountService().getEmpireaccount(accountId, s2);
                if (null == empireaccount2) {
                    empireaccount1.setServerid(s2);
                    ServiceManager.getManager().getAccountService().update(empireaccount1);
                } else {
                    palyer.setAccountId(empireaccount2.getId());
                    ServiceManager.getManager().getWorldService().update(palyer);
                }
            }
        }
        System.out.println("正在同步玩家:" + palyerCount + "/" + palyerCount);
        ServiceManager.getManager().getAccountService().deleteEmpireaccount(s1);
        ServiceManager.getManager().getWorldService().updatePlayerInfo(s1, s2);
        ServiceManager.getManager().getWorldService().deleteOldAreaInfo(s1);
    }
}
