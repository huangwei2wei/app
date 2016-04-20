// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.util.HashMap;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.plugins.MobManagerPlugin;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.engine.OID;
import java.util.Map;

public class ObjectFactory {
	protected int templateID = -1;
	protected String templateName = null;
	private static Map<String, ObjectFactory> factories;

	public ObjectFactory(final int templateID) {
		this.templateID = templateID;
	}

	public ObjectStub makeObject(final OID instanceOid, final Point loc) {
		final ObjectStub obj = MobManagerPlugin.createObject(this.templateID, instanceOid, loc, null);
		return obj;
	}

	public ObjectStub makeObject(final OID instanceOid, final Template override) {
		final ObjectStub obj = MobManagerPlugin.createObject(this.templateID, override, instanceOid);
		return obj;
	}

	public ObjectStub makeObject(final SpawnData spawnData, final OID instanceOid, final Point loc) {
		int tID = spawnData.getTemplateID();
		if (tID == -1) {
			tID = this.templateID;
		}
		final Quaternion orient = spawnData.getOrientation();
		final ObjectStub obj = MobManagerPlugin.createObject(tID, instanceOid, loc, orient);
		return obj;
	}

	public ObjectStub makeObject(final SpawnData spawnData, final OID instanceOid, final Template override) {
		int tID = spawnData.getTemplateID();
		if (tID == -1) {
			tID = this.templateID;
		}
		final ObjectStub obj = MobManagerPlugin.createObject(tID, override, instanceOid);
		return obj;
	}

	public int getTemplateID() {
		return this.templateID;
	}

	public void setTemplateID(final int templateID) {
		this.templateID = templateID;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	public static void register(final String factoryName, final ObjectFactory factory) {
		ObjectFactory.factories.put(factoryName, factory);
	}

	public static ObjectFactory getFactory(final String factoryName) {
		return ObjectFactory.factories.get(factoryName);
	}

	static {
		ObjectFactory.factories = new HashMap<String, ObjectFactory>();
	}
}
