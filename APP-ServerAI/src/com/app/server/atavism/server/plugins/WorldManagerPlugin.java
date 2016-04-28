// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.engine.MobilePerceiver;
import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.QuadTree;
import com.app.server.atavism.server.engine.WMWorldNode;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.objects.AOObject;
import com.app.server.atavism.server.objects.DisplayContext;
import com.app.server.atavism.server.objects.Entity;
import com.app.server.atavism.server.objects.EntityManager;
import com.app.server.atavism.server.objects.ObjectType;
import com.app.server.atavism.server.objects.ObjectTypes;
import com.app.server.atavism.server.objects.RegionTrigger;
import com.app.server.atavism.server.objects.Template;
import com.app.server.atavism.server.objects.World;
import com.app.server.atavism.server.pathing.PathInfo;
import com.app.server.atavism.server.pathing.PathObject;
import com.app.server.atavism.server.pathing.PathSearcher;
import com.app.server.atavism.server.util.AORuntimeException;
import com.app.server.atavism.server.util.LockFactory;

public abstract class WorldManagerPlugin {
	protected static final Logger log = Logger.getLogger("navmesh");
	private int maxObjects = 30;
	private int maxDepth = 20;
	protected long newRegionSub;
	protected long mobSubId;
	protected long mobRPCSubId;
	protected long structSubId;
	protected long structRPCSubId;
	protected int defaultWorldManagerHysteresis = 20;
	protected Map<OID, QuadTree<WMWorldNode>> quadtrees;
	protected Map<String, RegionTrigger> regionTriggers;
	protected Set<String> propertyExclusions;
	protected PathInfo pathInfo;
	protected boolean askedForPathInfo;
	protected static PathObjectCache pathObjectCache = new PathObjectCache();
	public static final String REGION_MEMBERSHIP = (String) Entity.registerTransientPropertyKey("customRegions");

	public WorldManagerPlugin() {
		this.quadtrees = new HashMap<OID, QuadTree<WMWorldNode>>();
		this.regionTriggers = new HashMap<String, RegionTrigger>();
		this.propertyExclusions = new HashSet<String>();
		String wmAgentName;
		this.propertyExclusions.add("aoobj.wnode");
		this.propertyExclusions.add("aoobj.perceiver");
		this.propertyExclusions.add("aoobj.dc");
		this.propertyExclusions.add("aoobj.statemap");
	}

	public Map<String, String> getStatusMap() {
		final Map<String, String> status = new HashMap<String, String>();
		status.put("instances", Integer.toString(this.quadtrees.size()));
		status.put("entities", Integer.toString(EntityManager.getEntityCount()));
		return status;
	}

	public Entity getWorldManagerEntity(final OID oid) {
		return EntityManager.getEntityByNamespace(oid, Namespace.WORLD_MANAGER);
	}

	public Entity getWorldManagerEntityOrError(final OID oid) {
		final Entity entity = this.getWorldManagerEntity(oid);
		if (entity == null) {
			throw new AORuntimeException("Could not find wm entity for oid " + oid);
		}
		return entity;
	}

	public void registerWorldManagerEntity(final Entity entity) {
		EntityManager.registerEntityByNamespace(entity, Namespace.WORLD_MANAGER);
	}

	public boolean removeWorldManagerEntity(final OID oid) {
		return EntityManager.removeEntityByNamespace(oid, Namespace.WORLD_MANAGER);
	}

	public PathInfo getPathInfo() {
		return this.pathInfo;
	}

	public void setPathInfo(final PathInfo pathInfo) {
		PathSearcher.createPathSearcher(this.pathInfo = pathInfo, World.getGeometry());
	}

	protected WorldManagerClient.ObjectInfo makeObjectInfo(final OID oid) {
		WorldManagerPlugin.log.debug("makeObjectInfo: oid=" + oid);
		final Entity entity = this.getWorldManagerEntity(oid);
		if (entity == null) {
			return null;
		}
		if (!(entity instanceof AOObject)) {
			throw new AORuntimeException("entity is not AOObject");
		}
		final AOObject obj = (AOObject) entity;
		InterpolatedWorldNode.InterpolatedDirLocOrientTime before = null;
		before = obj.getDirLocOrientTime();
		final CaptureInterpWorldNode capture = new CaptureInterpWorldNode((InterpolatedWorldNode) obj.getProperty("aoobj.wnode"));
		capture.getLoc();
		final InterpolatedWorldNode.InterpolatedDirLocOrientTime vals = capture.getDirLocOrientTime();
		final float distance = Point.distanceTo(vals.interpLoc, before.interpLoc);
		if (distance != 0.0f) {
			log.debug("DISTANCE " + distance + " TIME " + (vals.lastInterp - before.lastInterp) + " DIR " + vals.dir + " oid=" + obj.getOid());
		}
		final WorldManagerClient.ObjectInfo objInfo = new WorldManagerClient.ObjectInfo();
		objInfo.instanceOid = capture.getInstanceOid();
		objInfo.oid = obj.getOid();
		objInfo.name = obj.getName();
		objInfo.accountOid = null;
		objInfo.loc = vals.interpLoc;
		objInfo.orient = vals.orient;
		objInfo.scale = obj.scale();
		objInfo.objType = obj.getType();
		objInfo.dir = vals.dir;
		objInfo.lastInterp = vals.lastInterp;
		if (objInfo.objType == ObjectTypes.mob) {
			final Object pathMsgObject = obj.getProperty(WorldManagerClient.MOB_PATH_PROPERTY);
			if (pathMsgObject != null) {
//				final WorldManagerClient.MobPathMessage pathMsg = (WorldManagerClient.MobPathMessage) pathMsgObject;
//				objInfo.setProperty(WorldManagerClient.MOB_PATH_PROPERTY, pathMsg);// 设置属性
			}
		}
		final WMWorldNode wnode = (WMWorldNode) obj.worldNode();
		final Boolean b = wnode.getFollowsTerrain();
		objInfo.followsTerrain = (b != null && b);
		return objInfo;
	}

	private List<OID> getInstanceObjectsIn(final OID instanceOid, final Point loc, final Integer radius, final ObjectType objectType) {
		final Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.WORLD_MANAGER);
		final List<OID> objectsIn = new ArrayList<OID>();
		if (objectType != null) {
			for (final Entity entity : entities) {
				final OID entityOid = entity.getOid();
				final BasicWorldNode entityWorldNode = ((AOObject) this.getWorldManagerEntity(entityOid)).baseWorldNode();
				if (entity.getType().isA(objectType) && instanceOid.equals(entityWorldNode.getInstanceOid())) {
					log.debug("[CYC][1] entityType: " + entity.getType() + ", objectType: " + objectType);
					final Point entityLoc = entityWorldNode.getLoc();
					log.debug("[CYC][1] loc: " + loc + ", entityLoc: " + entityLoc + ", entityName: " + entity.getName());
					if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
						objectsIn.add(entityOid);
					}
					log.debug("[CYC][1] distance: " + Math.round(Point.distanceTo(loc, entityLoc)) + ", radius: " + radius);
				}
			}
		} else {
			for (final Entity entity : entities) {
				final OID entityOid = entity.getOid();
				final BasicWorldNode entityWorldNode = ((AOObject) this.getWorldManagerEntity(entityOid)).baseWorldNode();
				if (instanceOid.equals(entityWorldNode.getInstanceOid())) {
					log.debug("[CYC][2] entityType: " + entity.getType());
					final Point entityLoc = entityWorldNode.getLoc();
					log.debug("[CYC][2] loc: " + loc + ", entityLoc: " + entityLoc + ", entityName: " + entity.getName());
					if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
						objectsIn.add(entityOid);
					}
					log.debug("[CYC][2] distance: " + Math.round(Point.distanceTo(loc, entityLoc)) + ", radius: " + radius);
				}
			}
		}
		return objectsIn;
	}

	private List<OID> getObjectsIn(final Point loc, final Integer radius, final ObjectType objectType) {
		final Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.WORLD_MANAGER);
		final List<OID> objectsIn = new ArrayList<OID>();
		if (objectType != null) {
			for (final Entity entity : entities) {
				if (entity.getType().isA(objectType)) {
					final OID entityOid = entity.getOid();
					final BasicWorldNode entityWorldNode = ((AOObject) this.getWorldManagerEntity(entityOid)).baseWorldNode();
					final Point entityLoc = entityWorldNode.getLoc();
					if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
						objectsIn.add(entityOid);
					}
				}
			}
		} else {
			for (final Entity entity : entities) {
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

	protected AOObject generateWorldManagerSubObject(final Template template, final OID masterOid) {
		final AOObject wObj = new AOObject(masterOid);
		final Map<String, Serializable> props = template.getSubMap(Namespace.WORLD_MANAGER);
		if (props == null) {
			log.warn("WorldManagerPlugin.generateSubObject: no props in ns " + Namespace.WORLD_MANAGER);
			return null;
		}
		for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
			final String key = entry.getKey();
			final Serializable value = entry.getValue();
			if (!key.startsWith(":")) {
				wObj.setProperty(key, value);
			}
		}
		final ObjectType objType = (ObjectType) template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE);
		if (objType != null) {
			wObj.setType(objType);
		}
		return wObj;
	}

	protected Integer spawnObject(final AOObject obj, final QuadTree<WMWorldNode> quadtree) {
		final WMWorldNode wnode = (WMWorldNode) obj.worldNode();
		if (wnode == null) {
			throw new AORuntimeException("obj has no world node: " + obj);
		}
		final AOObject backRef = wnode.getObject();
		if (backRef == null) {
			throw new AORuntimeException("obj wnode backref is null: " + obj);
		}
		if (!backRef.getOid().equals(obj.getOid())) {
			throw new AORuntimeException("obj wnode backref does not match self: " + obj);
		}
		final MobilePerceiver<WMWorldNode> p = obj.perceiver();
		OID mobilePerceiverOid = null;
		if (p != null) {
			mobilePerceiverOid = obj.getMasterOid();
			WorldManagerPlugin.log.debug("spawnObject: registering perceiver cb: " + obj + ", masterOid " + mobilePerceiverOid);
			if (wnode.getPerceiver() == null) {
				throw new AORuntimeException("wnode doesnt have perceiver, obj=" + obj);
			}
			// p.registerCallback(this);
		}
		WorldManagerPlugin.log.debug("spawnObject: no perceiver for obj " + obj);

		wnode.isLocal(true);
		wnode.isSpawned(true);
		final Integer newsAndFressCount = quadtree.addElementReturnCountForPerceiver(wnode, mobilePerceiverOid);
		WorldManagerPlugin.log.debug("spawnObject: spawned obj: " + obj + ", wnode=" + wnode);
		return newsAndFressCount;
	}

	protected void sendDCMessage(final AOObject obj) {
	}

	protected abstract DisplayContext getDisplayContext(final OID p0);

	protected void sendPropertyMessage(final OID notifyOid, final AOObject updateObj) {
	}

	protected void sendTargetedPropertyMessage(final OID targetOid, final AOObject updateObj) {
	}

	protected void sendWNodeMessage(final OID oid, final AOObject updateObj) {
	}

	public void registerRegionTrigger(final String name, final RegionTrigger trigger) {
		this.regionTriggers.put(name, trigger);
	}

	public Set<String> getPropertyExclusions() {
		return this.propertyExclusions;
	}

	class CaptureInterpWorldNode extends InterpolatedWorldNode {
		public static final long serialVersionUID = 1L;

		CaptureInterpWorldNode(final InterpolatedWorldNode node) {
			node.lock.lock();
			try {
				this.instanceOid = node.getInstanceOid();
				this.interpLoc = node.getInterpLoc();
				this.lastInterp = node.getLastInterp();
				this.rawLoc = node.getRawLoc();
				this.dir = node.getDir();
				this.pathInterpolator = node.getPathInterpolator();
				this.spawned = node.isSpawned();
				this.orient = node.getOrientation();
				this.lastUpdate = node.getLastUpdate();
			} finally {
				node.lock.unlock();
			}
		}

		@Override
		public void setPathInterpolatorValues(final long time, final AOVector newDir, final Point newLoc, final Quaternion orientation) {
			this.lastInterp = time;
			this.interpLoc = (Point) newLoc.clone();
			this.dir = newDir;
			this.orient = orientation;
		}
	}

	/**
	 * 生成子对象
	 * 
	 * @param template
	 * @param namespace
	 * @param masterOid
	 * @return
	 */
	public AOObject generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
		WorldManagerPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + " namespace=" + namespace + " template=" + template);
		Boolean persistent = (Boolean) template.get(Namespace.OBJECT_MANAGER, ":persistent");
		if (persistent == null) {
			persistent = false;
		}
		if (namespace == WorldManagerClient.INSTANCE_NAMESPACE) {
			return null;// this.generateInstanceSubObject(masterOid, persistent);
		}
		final Map<String, Serializable> props = template.getSubMap(Namespace.WORLD_MANAGER);
		if (props == null) {
			log.warn("GenerateSubObjectHook: no props in ns " + Namespace.WORLD_MANAGER);
			return null;
		}
		final OID instanceOid = (OID) props.get(WorldManagerClient.TEMPL_INSTANCE);
		if (instanceOid == null) {
			log.error("GenerateSubObjectHook: missing instanceOid");
			return null;
		}
		if (WorldManagerPlugin.this.quadtrees.get(instanceOid) == null) {
			log.error("GenerateSubObjectHook: unknown instanceOid=" + instanceOid);
			return null;
		}
		final Point loc = (Point) props.get(WorldManagerClient.TEMPL_LOC);
		if (loc == null) {
			log.warn("GenerateSubObjectHook: no loc in templ");
			return null;
		}
		String objName = (String) props.get(WorldManagerClient.TEMPL_NAME);
		if (objName == null) {
			objName = template.getName();
			if (objName == null) {
				objName = "(null)";
			}
		}
		Quaternion orient = (Quaternion) props.get(WorldManagerClient.TEMPL_ORIENT);
		if (orient == null) {
			orient = new Quaternion();
		}
		AOVector scale = (AOVector) props.get(WorldManagerClient.TEMPL_SCALE);
		if (scale == null) {
			scale = new AOVector(1.0f, 1.0f, 1.0f);
		}
		final Integer perceptionRadius = (Integer) props.get(WorldManagerClient.TEMPL_PERCEPTION_RADIUS);
		final AOObject wObj = WorldManagerPlugin.this.generateWorldManagerSubObject(template, masterOid);
		wObj.setName(objName);
		wObj.scale(scale);
		DisplayContext dc = (DisplayContext) props.get(WorldManagerClient.TEMPL_DISPLAY_CONTEXT);
		if (dc != null) {
			dc = (DisplayContext) dc.clone();
			dc.setObjRef(wObj.getOid());
			wObj.displayContext(dc);
		} else {
			log.debug("GenerateSubObjectHook: object has no display context, oid=" + masterOid);
		}
		WorldManagerPlugin.log.debug("GenerateSubObjectHook: created entity " + wObj + ", loc=" + loc);
		WMWorldNode wnode;
		if (perceptionRadius != null) {
			wnode = new WMWorldNode(perceptionRadius);
		} else {
			wnode = new WMWorldNode();
		}
		wnode.setInstanceOid(instanceOid);
		wnode.setLoc(loc);
		wnode.setOrientation(orient);
		Boolean followsTerrain = (Boolean) props.get(WorldManagerClient.TEMPL_FOLLOWS_TERRAIN);
		if (followsTerrain == null) {
			followsTerrain = Boolean.TRUE;
		}
		wnode.setFollowsTerrain(followsTerrain);
		wObj.worldNode(wnode);
		wnode.setObject(wObj);
		wObj.setPersistenceFlag(persistent);
		EntityManager.registerEntityByNamespace(wObj, Namespace.WORLD_MANAGER);// 注册aoobject
		// if (persistent) {
		// Engine.getPersistenceManager().persistEntity(wObj);
		// }
		// WorldManagerPlugin.this.subscribeForMob(masterOid);
		return wObj;
	}
	
	/**
	 * 获取BasicWorldNode
	 * @param oid
	 * @return
	 */
	public BasicWorldNode getWNode(OID oid){
		final AOObject obj = (AOObject) WorldManagerPlugin.this.getWorldManagerEntity(oid);
		if (obj == null) {
			log.error("GetWNodeReqHook: could not find obj for oid=" + oid);
			return null;
		}
		return obj.baseWorldNode();
	}
	 
	

	public static class PathObjectCache {
		protected Map<String, PathObject> cache;
		protected static transient Lock lock;

		public PathObjectCache() {
			this.cache = new HashMap<String, PathObject>();
		}

		public PathObject getPathObject(final String roomId) {
			PathObjectCache.lock.lock();
			try {
				return this.cache.get(roomId);
			} finally {
				PathObjectCache.lock.unlock();
			}
		}

		public void setPathObject(final String roomId, final PathObject pathObject) {
			PathObjectCache.lock.lock();
			try {
				this.cache.put(roomId, pathObject);
			} finally {
				PathObjectCache.lock.unlock();
			}
		}

		static {
			PathObjectCache.lock = LockFactory.makeLock("PathObjectCache");
		}
	}
}
