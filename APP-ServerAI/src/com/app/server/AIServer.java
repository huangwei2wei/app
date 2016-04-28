package com.app.server;

import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.agis.objects.InstanceNavMeshManager;
import com.app.server.atavism.agis.objects.MobFactory;
import com.app.server.atavism.agis.plugins.AgisMobPlugin;
import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.objects.ObjectStub;
import com.app.server.atavism.server.objects.SpawnData;
import com.app.server.atavism.server.plugins.WorldManagerClient;

///测试
public class AIServer {
	public static void main(String[] args) {
		Namespace.encacheNamespaceMapping();
		// final InstanceNavMeshManager navMeshManager = new InstanceNavMeshManager(instanceName, SPMsg.instanceOid);
		// AgisWorldManagerPlugin.this.instanceNavMeshes.put(SPMsg.instanceOid, navMeshManager);

		final InstanceNavMeshManager navMeshManager = new InstanceNavMeshManager("navmeshName", OID.fromLong(1));
		// AgisWorldManagerPlugin.this.instanceNavMeshes.put(SPMsg.instanceOid, navMeshManager);
		final MobFactory cFactory = new MobFactory(-1);
		System.out.println("aaa");
		final BasicWorldNode defaultLoc = new BasicWorldNode();
		defaultLoc.setLoc(new Point());
		final AOVector dir = new AOVector();
		defaultLoc.setDir(dir);

		final SpawnData spawnData = new SpawnData();
		spawnData.setTemplateID(-1);
		AgisMobPlugin.getAgisMobPlugin().createMob();// 创建模板

		ObjectStub obj = cFactory.makeObject(spawnData, defaultLoc.getInstanceOid(), defaultLoc.getLoc());// 创建对象

		// SpawnData sd = new SpawnData();
		// // sd.setProperty("id", (Serializable)(int)System.currentTimeMillis());
		// // sd.setTemplateID(this.mobTemplateID);
		// // final BehaviorTemplate behavTmpl = new BehaviorTemplate();
		// // behavTmpl.setHasCombat(true);
		// // behavTmpl.setWeaponsSheathed(false);
		// // behavTmpl.setRoamRadius(0);
		// // sd.setLoc(objInfo.loc);
		// // sd.setOrientation(objInfo.orient);
		// // sd.setInstanceOid(objInfo.instanceOid);
		// // sd.setSpawnRadius(0);
		// // sd.setCorpseDespawnTime(1000);
		// // sd.setRespawnTime(1000);
		// // sd.setNumSpawns(1);
		// // sd.setProperty("behaviourTemplate", (Serializable)behavTmpl);
		//
		// // SpawnData sd = new SpawnData();
		// ObjectStub obj = null;
		// final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(OID.fromLong(1l));
		// obj = cFactory.makeObject(sd, bwNode.getInstanceOid(), bwNode.getLoc());

		// cFactory.makeObject(instanceOid, loc);
		// cFactory.addBehav((Behavior) new BaseBehavior());
		// final DotBehavior behav = new DotBehavior();
		// behav.setRadius(1500);
		// cFactory.addBehav(behav);
	}

}
