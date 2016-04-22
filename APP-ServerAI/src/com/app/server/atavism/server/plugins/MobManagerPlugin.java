// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import com.app.server.atavism.server.objects.SpawnData;
import atavism.agis.objects.SpawnGenerator;
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

import atavism.msgsys.GenericResponseMessage;
import atavism.msgsys.Message;
import atavism.server.objects.ObjectTracker;
import atavism.server.plugins.ObjectManagerClient;
import com.app.server.atavism.server.plugins.ObjectManagerPlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.plugins.ObjectManagerPlugin.MasterObject;
import atavism.server.util.Log;

import com.app.server.atavism.server.engine.OID;
import java.util.Map;

import org.apache.log4j.Logger;

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
		MobManagerPlugin.log.debug("createObject: template=" + templateID + ", override=" + override + ", instanceOid=" + instanceOid + " calling into objectmanager to generate");
		if (instanceOid != null) {
			override.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_INSTANCE, instanceOid);
		}
		// final OID objId = ObjectManagerClient.generateObject(templateID, ObjectManagerPlugin.MOB_TEMPLATE, override);
		final OID objId = objectManagerPlugin.generateObject(templateID, ObjectManagerPlugin.MOB_TEMPLATE, override);// 创建怪物
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
				if (Log.loggingDebug) {
					MobManagerPlugin.log.debug("Creating ObjectTracker for instanceOid=" + instanceOID + "with types: " + MobManagerPlugin.trackedObjectTypes);
				}
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
		Log.debug("AJ: AggroRadius with instanceOid: " + instanceOID + " and trackers: " + MobManagerPlugin.trackers);
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
		log = new Logger("MobManagerPlugin");
	}

	class MobUnloadHook implements UnloadHook {
		@Override
		public void onUnload(final Entity entity) {
			if (entity instanceof ObjectStub) {
				((ObjectStub) entity).unload();
			}
		}
	}

	class MobDeleteHook implements DeleteHook {
		@Override
		public void onDelete(final Entity entity) {
			if (entity instanceof ObjectStub) {
				((ObjectStub) entity).unload();
			}
		}

		@Override
		public void onDelete(final OID oid, final Namespace namespace) {
		}
	}

	class CreateSpawnGenHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final MobManagerClient.CreateSpawnGeneratorMessage message = (MobManagerClient.CreateSpawnGeneratorMessage) msg;
			final SpawnData spawnData = message.getSpawnData();
			final ObjectFactory factory = ObjectFactory.getFactory(spawnData.getFactoryName());
			if (factory == null) {
				Engine.getAgent().sendBooleanResponse(message, false);
				if (Log.loggingDebug) {
					Log.debug("CreateSpawnGenHook: unknown factory=" + spawnData.getFactoryName());
				}
				return true;
			}
			SpawnGenerator spawnGen = null;
			String spawnGenClassName = (String) spawnData.getProperty("className");
			if (spawnGenClassName == null) {
				spawnGenClassName = spawnData.getClassName();
			}
			if (spawnGenClassName == null) {
				spawnGen = new SpawnGenerator(spawnData);
			} else {
				try {
					final Class spawnGenClass = MobManagerPlugin.spawnGeneratorClasses.get(spawnGenClassName);
					if (spawnGenClass == null) {
						throw new AORuntimeException("spawn generator class not registered");
					}
					spawnGen = (SpawnGenerator) spawnGenClass.newInstance();
					spawnGen.initialize(spawnData);
				} catch (Exception ex) {
					Log.exception("CreateSpawnGenHook: failed instantiating class " + spawnGenClassName, ex);
					Engine.getAgent().sendBooleanResponse(message, false);
					return true;
				}
			}
			spawnGen.setObjectFactory(factory);
			spawnGen.activate();
			Engine.getAgent().sendBooleanResponse(message, true);
			return true;
		}
	}

	class InstanceUnloadedHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final SubjectMessage message = (SubjectMessage) msg;
			final OID instanceOid = message.getSubject();
			SpawnGenerator.cleanupInstance(instanceOid);
			MobManagerPlugin.removeTracker(instanceOid);
			Engine.getAgent().sendResponse(new ResponseMessage(message));
			return true;
		}
	}

	class SetAggroRadiusHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final MobManagerClient.SetAggroRadiusMessage message = (MobManagerClient.SetAggroRadiusMessage) msg;
			final OID mob = message.getMob();
			final OID target = message.getTarget();
			final int reactionRadius = message.getRadius();
			MobManagerPlugin.setAggroRadiusTracker(mob, target, reactionRadius);
			return true;
		}
	}
}
