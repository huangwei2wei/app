package com.app.empire.scene.service.warfield.spawn;

import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.entity.NpcInfo;
import com.app.empire.scene.entity.FieldSpawn;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.objects.NPC;
//import com.chuangyou.xianni.role.template.NpcInfoTemplateMgr;
import com.app.empire.scene.service.warfield.field.Field;

public class NpcSpawnNode extends SpwanNode {

	public NpcSpawnNode(FieldSpawn spwanInfo, Field field) {
		super(spwanInfo, field);
	}

	@Override
	public void start() {
		super.start();
		// System.out.println("spawn - skin = " + spwanInfo.getEntityId());
		NpcInfo npcInfo = ServiceManager.getManager().getGameConfigService().getNpcInfo().get(spwanInfo.getEntityId());
		if (npcInfo != null) {
			NPC npc = new NPC(IDMakerHelper.nextID(), npcInfo.getName(), this);
			// npc.setSkin(npcInfo.getSkin());
			npc.setSkin(npcInfo.getNpcId());
			npc.setPostion(new Vector3(spwanInfo.getBound_x() / Vector3.Accuracy, spwanInfo.getBound_y() / Vector3.Accuracy, spwanInfo.getBound_z() / Vector3.Accuracy));
			field.enterField(npc);

			// System.out.println("npcId :" + npc.getId() + " skinId :" +
			// npc.getSkin());
		} else {
			Log.error(spwanInfo.getId() + "----------" + spwanInfo.getEntityId() + " 在NpcInfo里面未找到配置");
		}
	}
}
