package com.app.server;

 

import com.app.server.atavism.agis.objects.InstanceNavMeshManager;
import com.app.server.atavism.agis.objects.MobFactory;
import com.app.server.atavism.agis.plugins.AgisWorldManagerPlugin;
import com.app.server.atavism.server.engine.Behavior;
import com.app.server.atavism.server.engine.OID;

///测试
public class AIServer {
	public static void main(String[] args) {
		System.out.println("a");
		
//		final InstanceNavMeshManager navMeshManager = new InstanceNavMeshManager(instanceName, SPMsg.instanceOid);
//		AgisWorldManagerPlugin.this.instanceNavMeshes.put(SPMsg.instanceOid, navMeshManager);
		
		
		final InstanceNavMeshManager navMeshManager = new InstanceNavMeshManager("navmeshName", OID.fromLong(1));
		// AgisWorldManagerPlugin.this.instanceNavMeshes.put(SPMsg.instanceOid, navMeshManager);
		final MobFactory cFactory = new MobFactory(500);
		
//		cFactory.makeObject(instanceOid, loc);
//		cFactory.addBehav((Behavior) new BaseBehavior());
//		final DotBehavior behav = new DotBehavior();
//		behav.setRadius(1500);
//		cFactory.addBehav(behav);
	}

}
