// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import com.app.server.atavism.server.objects.SpawnData;
import com.app.server.atavism.agis.objects.SpawnGenerator;
import java.util.HashMap;
import com.app.server.atavism.server.objects.EntityWithWorldNodeFactory;
import com.app.server.atavism.server.objects.ObjectStubFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.app.server.atavism.server.pathing.PathSearcher;
import com.app.server.atavism.server.objects.World;
import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.objects.Entity;
import com.app.server.atavism.server.objects.EntityManager;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import java.io.Serializable;
import com.app.server.atavism.server.objects.Template;
import com.app.server.atavism.server.objects.ObjectStub;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.util.AORuntimeException;
import com.app.server.atavism.server.objects.ObjectFactory;
import com.app.server.atavism.server.objects.WEObjFactory;
import com.app.server.atavism.server.engine.Engine;
import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.pathing.PathInfo;

import com.app.server.atavism.server.objects.ObjectType;
import java.util.Collection;

import com.app.server.atavism.server.objects.ObjectTracker;
import com.app.server.atavism.server.plugins.ObjectManagerClient;
import com.app.server.atavism.server.plugins.ObjectManagerPlugin;
import com.app.server.atavism.server.plugins.WorldManagerClient;
import com.app.server.atavism.server.plugins.ObjectManagerPlugin.MasterObject;

import com.app.server.atavism.server.engine.OID;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MobManagerPlugin {
	private static Map<String, Class> spawnGeneratorClasses;
	private static Map<OID, ObjectTracker> trackers;
	private static Collection<ObjectType> trackedObjectTypes;
	protected static final Logger log = Logger.getLogger("navmesh");
	protected PathInfo pathInfo;
	protected boolean askedForPathInfo;
	protected static ObjectManagerPlugin objectManagerPlugin;

	public MobManagerPlugin() {
		this.pathInfo = null;
		this.askedForPathInfo = false;
	}

	public void onActivate() {
		try {
			MobManagerPlugin.log.debug("onActivate()");
			ObjectFactory.register("WEObjFactory", new WEObjFactory());
		} catch (Exception e) {
			throw new AORuntimeException("activate failed", e);
		}
	}

	public static ObjectStub createObject(final int templateID, final OID instanceOid, final Point loc, final Quaternion orient) {
		return createObject(templateID, instanceOid, loc, orient, true);
	}

	public static ObjectStub createObject(final int templateID, final OID instanceOid, final Point loc, final Quaternion orient, final boolean followsTerrain) {
		MobManagerPlugin.log.debug("createObject: template=" + templateID + ", point=" + loc + ", calling into objectmanager to generate");
		final Template override = new Template();
		override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_INSTANCE, instanceOid);
		override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_LOC, loc);
		if (orient != null) {
			override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ORIENT, orient);
		}
		override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_FOLLOWS_TERRAIN, new Boolean(followsTerrain));
		return createObject(templateID, override, null);
	}

	public static ObjectStub createObject(final int templateID, final Template override, final OID instanceOid) {
		if (instanceOid != null) {
			override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_INSTANCE, instanceOid);
		}
		// final OID objId = ObjectManagerClient.generateObject(templateID, ObjectManagerPlugin.MOB_TEMPLATE, override);
		System.out.println(ObjectManagerPlugin.getObjectManagerPlugin());
		final OID objId = ObjectManagerPlugin.getObjectManagerPlugin().generateObject(templateID, ObjectManagerPlugin.MOB_TEMPLATE, override);// 创建怪物
		MobManagerPlugin.log.debug("generated object oid=" + objId);
		if (objId == null) {
			log.warn("MobManagerPlugin: oid is null, skipping");
			return null;
		}
		final BasicWorldNode bwNode = WorldManagerClient.getWorldNode(objId);
		final InterpolatedWorldNode iwNode = new InterpolatedWorldNode(bwNode);
		final ObjectStub obj = new ObjectStub(objId, iwNode, templateID);
		EntityManager.registerEntityByNamespace(obj, Namespace.MOB);
		MobManagerPlugin.log.debug("createObject: obj=" + obj);
		return obj;
	}

	public PathInfo getPathInfo() {
		return this.pathInfo;
	}

	public void setPathInfo(final PathInfo pathInfo) {
		PathSearcher.createPathSearcher(this.pathInfo = pathInfo, World.getGeometry());
	}

	public static void setTrackedObjectTypes(final Collection<ObjectType> objectTypes) {
		if (objectTypes != null) {
			MobManagerPlugin.trackedObjectTypes = new ArrayList<ObjectType>(objectTypes);
		} else {
			MobManagerPlugin.trackedObjectTypes = null;
		}
	}

	public static List<ObjectType> getTrackedObjectTypes() {
		return new ArrayList<ObjectType>(MobManagerPlugin.trackedObjectTypes);
	}

	public static ObjectTracker getTracker(final OID instanceOID) {
		synchronized (MobManagerPlugin.trackers) {
			ObjectTracker tracker = MobManagerPlugin.trackers.get(instanceOID);
			if (tracker == null) {
				MobManagerPlugin.log.debug("Creating ObjectTracker for instanceOid=" + instanceOID + "with types: " + MobManagerPlugin.trackedObjectTypes);
				tracker = new ObjectTracker(Namespace.MOB, instanceOID, new ObjectStubFactory(), MobManagerPlugin.trackedObjectTypes);
				MobManagerPlugin.trackers.put(instanceOID, tracker);
			}
			return tracker;
		}
	}

	public static void removeTracker(final OID instanceOid) {
		synchronized (MobManagerPlugin.trackers) {
			MobManagerPlugin.trackers.remove(instanceOid);
		}
	}

	public static void setAggroRadiusTracker(final OID mob, final OID target, final int reactionRadius) {
		final OID instanceOID = WorldManagerClient.getObjectInfo(mob).instanceOid;
		log.debug("AJ: AggroRadius with instanceOid: " + instanceOID + " and trackers: " + MobManagerPlugin.trackers);
		synchronized (MobManagerPlugin.trackers) {
			final ObjectTracker tracker = MobManagerPlugin.trackers.get(instanceOID);
			if (tracker == null) {
				return;
			}
			if (reactionRadius == -1) {
				tracker.removeAggroRadius(mob, target);
			} else {
				tracker.addAggroRadius(mob, target, reactionRadius);
			}
		}
	}

	public static void registerSpawnGeneratorClass(final String name, final Class spawnGenClass) {
		synchronized (MobManagerPlugin.spawnGeneratorClasses) {
			MobManagerPlugin.spawnGeneratorClasses.put(name, spawnGenClass);
		}
	}

	public static Class getSpawnGeneratorClass(final String name) {
		return MobManagerPlugin.spawnGeneratorClasses.get(name);
	}

	static {
		MobManagerPlugin.spawnGeneratorClasses = new HashMap<String, Class>();
		MobManagerPlugin.trackers = new HashMap<OID, ObjectTracker>();
	}

}
