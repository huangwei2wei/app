package com.app.empire.scene.service.warField.spawn;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.db.mysql.entity.NpcInfo;
import com.app.empire.scene.util.Vector3;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.objects.NPC;
import com.app.empire.scene.service.warField.field.Field;

public class NpcSpawnNode extends SpwanNode {
	private Logger log = Logger.getLogger(NpcSpawnNode.class);

	public NpcSpawnNode(FieldSpawn spwanInfo, Field field) {
		super(spwanInfo, field);
	}

	@Override
	public void start() {
		super.start();
		NpcInfo npcInfo = ServiceManager.getManager().getGameConfigService().getNpcInfo().get(spwanInfo.getEntityId());
		if (npcInfo != null) {
			NPC npc = new NPC(IDMakerHelper.nextID(), npcInfo.getName(), this);
			npc.setSkin(npcInfo.getNpcId());
			npc.setPostion(new Vector3(spwanInfo.getBoundX() / Vector3.Accuracy, spwanInfo.getBoundY() / Vector3.Accuracy, spwanInfo.getBoundZ() / Vector3.Accuracy));
			field.enterField(npc);
			children.put(npc.getId(), npc);
		} else {
			log.error(spwanInfo.getId() + "----------" + spwanInfo.getEntityId() + " 在NpcInfo里面未找到配置");
		}
	}
}
