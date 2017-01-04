package com.app.empire.scene.service.warField.spawn;

import org.apache.log4j.Logger;

import com.app.db.mysql.entity.FieldSpawn;
import com.app.db.mysql.entity.NpcInfo;
import com.app.empire.scene.service.ServiceManager;
import com.app.empire.scene.service.role.helper.IDMakerHelper;
import com.app.empire.scene.service.role.objects.Gather;
import com.app.empire.scene.service.role.objects.Living;
import com.app.empire.scene.service.warField.field.Field;
import com.app.empire.scene.util.Vector3;

/**
 * 采集物结点
 * 
 * @author laofan
 * 
 */
public class GatherSpawnNode extends SpwanNode {
	protected Logger log = Logger.getLogger(Living.class);
	private Gather gather;

	public GatherSpawnNode(FieldSpawn spwanInfo, Field field) {
		super(spwanInfo, field);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start() {
		super.start();
		// System.out.println("spawn - skin = " + spwanInfo.getEntityId());
		NpcInfo npcInfo = ServiceManager.getManager().getGameConfigService().getNpcInfo().get(spwanInfo.getEntityId());
		if (npcInfo != null) {
			gather = new Gather(IDMakerHelper.nextID(), this, npcInfo.getName());
			// npc.setSkin(npcInfo.getSkin());
			gather.setSkin(spwanInfo.getEntityId());
			gather.setPostion(new Vector3(spwanInfo.getBoundX()/ Vector3.Accuracy, spwanInfo.getBoundY()/ Vector3.Accuracy, spwanInfo.getBoundZ()/ Vector3.Accuracy));
			System.out.println("gatherID :" + gather.getId() + " skinId :" + gather.getSkin() + "  mapId:" + spwanInfo.getMapid());
			field.enterField(gather);
		} else {
			log.error(spwanInfo.getId() + "----------" + spwanInfo.getEntityId() + " 在NpcInfo里面未找到配置");
		}

	}

	@Override
	public void over() {
		// TODO Auto-generated method stub
		if (gather != null) {
			if (gather.getField() != null) {
				gather.getField().leaveField(gather);
			}
			gather.destory();
			gather.clearData();
			gather = null;
		}
		super.over();
	}

}
