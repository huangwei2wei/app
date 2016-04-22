// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.util.HashMap;
import java.lang.reflect.Constructor;
import com.app.server.atavism.server.util.AORuntimeException;
import com.app.server.atavism.server.plugins.MobManagerPlugin;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.Behavior;
import java.util.Map;
import org.apache.log4j.Logger;

public class WEObjFactory extends ObjectFactory {
	protected static final Logger log = Logger.getLogger("navmesh");
	protected static Map<String, Class<Behavior>> behavClassMap = new HashMap<String, Class<Behavior>>();
	private static final Class[] constructorArgs = new Class[]{SpawnData.class};
	protected SpawnData spawnData = null;

	public WEObjFactory() {
		super(0);
	}
	public WEObjFactory(final int templateID) {
		super(templateID);
	}

	@Override
	public ObjectStub makeObject(final SpawnData spawnData, final OID instanceOid, final Point loc) {
		final int templateID = spawnData.getTemplateID();
		final String behavNames = (String) spawnData.getProperty("Behaviors");
		log.debug("WEObjFactory.makeObject: templateID=" + templateID + " instanceOid=" + instanceOid + " Behaviors=" + behavNames + " propsize=" + spawnData.getPropertyMap().size());
		final ObjectStub obj = MobManagerPlugin.createObject(templateID, spawnData.getInstanceOid(), loc, spawnData.getOrientation());
		if (behavNames != null) {
			for (String behavName : behavNames.split(",")) {
				try {
					behavName = behavName.trim();
					if (behavName.length() != 0) {
						final Class behavClass = WEObjFactory.behavClassMap.get(behavName);
						if (behavClass == null) {
							log.error("WEObjFactory.makeObject: unknown behavior=" + behavName + ", templateName=" + this.templateName + " instanceOid=" + instanceOid + " Behaviors=" + behavNames);
						} else {
							final Constructor<Behavior> constructor = behavClass.getConstructor((Class<?>[]) WEObjFactory.constructorArgs);
							if (constructor == null) {
								log.error("WEObjFactory.makeObject: missing constructor with signature (SpawnData) on class " + behavClass);
							} else {
								final Object[] args = {spawnData};
								final Behavior behav = constructor.newInstance(args);
								obj.addBehavior(behav);
							}
						}
					}
				} catch (Exception e) {
					throw new AORuntimeException("can't create behavior", e);
				}
			}
		}
		return obj;
	}

	public static void registerBehaviorClass(final String name, final String className) {
		try {
			final Class<Behavior> behavClass = (Class<Behavior>) Class.forName(className);
			WEObjFactory.behavClassMap.put(name, behavClass);
		} catch (ClassNotFoundException e) {
			throw new AORuntimeException("behavior class not found", e);
		}
	}

}
