// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.plugins;

import com.app.server.atavism.server.objects.Marker;
import com.app.server.atavism.server.plugins.InstanceClient;
import com.app.server.atavism.agis.objects.DetourActor;
import java.util.Calendar;
import com.app.server.atavism.agis.objects.ArenaQueue;
//import atavism.server.messages.LogoutMessage;
//import atavism.msgsys.ResponseMessage;
import java.util.LinkedList;
import com.app.server.atavism.server.util.Points;
import com.app.server.atavism.server.objects.World;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.engine.WorldNode;
import com.app.server.atavism.server.engine.WMWorldNode;
import com.app.server.atavism.agis.util.AgisDisplayContext;
//import atavism.server.messages.PropertyMessage;
//import atavism.msgsys.Message;
import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.objects.Entity;
import java.util.ArrayList;
import com.app.server.atavism.server.objects.EntityManager;
import java.util.List;
import com.app.server.atavism.server.math.Point;
import java.util.Iterator;
import com.app.server.atavism.server.objects.DisplayContext;
import java.io.Serializable;
import java.util.Map;
import com.app.server.atavism.server.objects.LightData;
import com.app.server.atavism.server.objects.Light;
import com.app.server.atavism.agis.objects.AgisMob;
import com.app.server.atavism.server.objects.ObjectTypes;
import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.objects.ObjectType;
import com.app.server.atavism.server.objects.AOObject;
import com.app.server.atavism.server.objects.Template;
import com.app.server.atavism.server.engine.EnginePlugin;
//import atavism.server.engine.Hook;
//import atavism.server.messages.LoginMessage;
//import atavism.msgsys.MessageCallback;
//import atavism.msgsys.IFilter;
//import atavism.msgsys.MessageTypeFilter;
import com.app.server.atavism.server.engine.Engine;
import com.app.server.atavism.server.plugins.WorldManagerClient;

import com.app.server.atavism.agis.objects.AgisObject;
import com.app.server.atavism.agis.objects.InstanceNavMeshManager;
import com.app.server.atavism.server.engine.OID;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.app.server.atavism.server.plugins.WorldManagerPlugin;

public class AgisWorldManagerPlugin extends WorldManagerPlugin {
	private static Logger log = Logger.getLogger("navmesh");
	public static Float defaultRunThreshold = 2.0f;
	private HashMap<OID, InstanceNavMeshManager> instanceNavMeshes = new HashMap<OID, InstanceNavMeshManager>();
	private long serverStartTime;
	private long currentSecondsRunning = 0L;
	public static final int MOVEMENT_STATE_RUNNING = 1;
	public static final int MOVEMENT_STATE_SWIMMING = 2;
	public static final int MOVEMENT_STATE_FLYING = 3;
	public static final String PROP_FOLLOW_TERRAIN = "follow_terrain";
	public static final String PROP_MOVEMENT_STATE = "movement_state";
	public static final String PROP_MOVEMENT_SPEED = "movement_speed";
	public static final String PROP_ACTION_STATE = "action_state";
	protected int waterHeight = Integer.MIN_VALUE;

	public AgisWorldManagerPlugin() {
		this.propertyExclusions.add(AgisObject.baseDCKey);
		this.serverStartTime = System.currentTimeMillis();
	}
	/**
	 * 生成 世界管理 子对象
	 * 
	 * @param template
	 * @param masterOid
	 * @return
	 */
	protected AOObject generateWorldManagerSubObject(final Template template, final OID masterOid) {
		final ObjectType objType = (ObjectType) template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE);
		AOObject obj = null;
		log.debug("AgisWorldManagerPlugin: generateWorldManagerSubObject: objectType=" + objType + ", template=" + template);
		if (objType == null) {
			log.warn("AgisWorldManagerPlugin: generateSubObject: no object type, using structure");
			obj = new AgisObject(masterOid);
			obj.setType(ObjectTypes.structure);
		} else if (objType == ObjectTypes.mob || objType == ObjectTypes.player) {
			obj = new AgisMob(masterOid);
			obj.setType(objType);
		} else if (objType == ObjectTypes.structure) {
			obj = new AgisObject(masterOid);
			obj.setType(ObjectTypes.structure);
		} else if (objType == ObjectTypes.light) {
			final Light l = new Light(masterOid);
			final LightData ld = (LightData) template.get(Namespace.WORLD_MANAGER, Light.LightDataPropertyKey);
			l.setLightData(ld);
			obj = (AOObject) l;
		} else {
			obj = new AgisObject(masterOid);
			obj.setType(objType);
		}
		final Map<String, Serializable> props = (Map<String, Serializable>) template.getSubMap(Namespace.WORLD_MANAGER);
		if (props == null) {
			log.warn("AgisWorldManagerPlugin.generateSubObject: no props in ns " + Namespace.WORLD_MANAGER);
			return null;
		}
		for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
			final String key = entry.getKey();
			final Serializable value = entry.getValue();
			if (!key.startsWith(":")) {
				obj.setProperty(key, value);
			}
		}
		if (obj.isUser() || obj.isMob() || obj.isStructure()) {
			final AgisObject agisObj = (AgisObject) obj;
			DisplayContext dc = (DisplayContext) props.get(WorldManagerClient.TEMPL_DISPLAY_CONTEXT);
			if (dc == null) {
				if (objType != ObjectTypes.terrainDecal) {
					log.warn("AgisWorldManagerPlugin.generateSubObject: obj has no display context, oid=" + masterOid);
				}
			} else {
				dc = (DisplayContext) dc.clone();
				dc.setObjRef(agisObj.getOid());
				agisObj.baseDC(dc);
				agisObj.displayContext(dc);
			}
		}
		return obj;
	}

	private List<OID> getInstanceObjectsIn(final OID instanceOid, final Point loc, final Integer radius, final ObjectType objectType) {
		final Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.WORLD_MANAGER);
		final List<OID> objectsIn = new ArrayList<OID>();
		if (objectType != null) {
			Entity[] array;
			for (int length = (array = entities).length, i = 0; i < length; ++i) {
				final Entity entity = array[i];
				final OID entityOid = entity.getOid();
				final AOObject obj = (AOObject) this.getWorldManagerEntity(entityOid);
				if (obj != null) {
					final BasicWorldNode entityWorldNode = obj.baseWorldNode();
					if (entity.getType().getTypeId() == objectType.getTypeId() && instanceOid.equals((Object) entityWorldNode.getInstanceOid())) {
						log.debug("[CYC][1] entityType: " + entity.getType() + ", objectType: " + objectType);
						final Point entityLoc = entityWorldNode.getLoc();
						log.debug("[CYC][1] loc: " + loc + ", entityLoc: " + entityLoc + ", entityName: " + entity.getName());
						if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
							objectsIn.add(entityOid);
						}
						log.debug("[CYC][1] distance: " + Math.round(Point.distanceTo(loc, entityLoc)) + ", radius: " + radius);
					}
				}
			}
		} else {
			Entity[] array2;
			for (int length2 = (array2 = entities).length, j = 0; j < length2; ++j) {
				final Entity entity = array2[j];
				final OID entityOid = entity.getOid();
				final BasicWorldNode entityWorldNode2 = ((AOObject) this.getWorldManagerEntity(entityOid)).baseWorldNode();
				if (instanceOid.equals((Object) entityWorldNode2.getInstanceOid())) {
					log.debug("[CYC][2] entityType: " + entity.getType());
					final Point entityLoc2 = entityWorldNode2.getLoc();
					log.debug("[CYC][2] loc: " + loc + ", entityLoc: " + entityLoc2 + ", entityName: " + entity.getName());
					if (Math.round(Point.distanceTo(loc, entityLoc2)) <= radius) {
						objectsIn.add(entityOid);
					}
					log.debug("[CYC][2] distance: " + Math.round(Point.distanceTo(loc, entityLoc2)) + ", radius: " + radius);
				}
			}
		}
		return objectsIn;
	}

	private List<OID> getObjectsIn(final Point loc, final Integer radius, final ObjectType objectType) {
		final Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.WORLD_MANAGER);
		final List<OID> objectsIn = new ArrayList<OID>();
		if (objectType != null) {
			Entity[] array;
			for (int length = (array = entities).length, i = 0; i < length; ++i) {
				final Entity entity = array[i];
				if (entity.getType().getTypeId() == objectType.getTypeId()) {
					final OID entityOid = entity.getOid();
					final BasicWorldNode entityWorldNode = ((AOObject) this.getWorldManagerEntity(entityOid)).baseWorldNode();
					final Point entityLoc = entityWorldNode.getLoc();
					if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
						objectsIn.add(entityOid);
					}
				}
			}
		} else {
			Entity[] array2;
			for (int length2 = (array2 = entities).length, j = 0; j < length2; ++j) {
				final Entity entity = array2[j];
				final OID entityOid = entity.getOid();
				final BasicWorldNode entityWorldNode = ((AOObject) this.getWorldManagerEntity(entityOid)).baseWorldNode();
				final Point entityLoc = entityWorldNode.getLoc();
				if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
					objectsIn.add(entityOid);
				}
			}
		}
		return objectsIn;
	}

	protected DisplayContext getDisplayContext(final OID objOid) {
		final Entity entity = this.getWorldManagerEntity(objOid);
		if (entity == null) {
			return null;
		}
		if (!(entity instanceof AOObject)) {
			return null;
		}
		final AOObject obj = (AOObject) entity;
		if (!(obj instanceof AgisObject)) {
			return obj.displayContext();
		}
		final DisplayContext dc = AgisDisplayContext.createFullDisplayContext((AgisObject) obj);
		return dc;
	}

}
