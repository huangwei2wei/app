// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import atavism.server.messages.NamespaceFilter;
import com.app.server.atavism.server.pathing.PathFinderValue;
import com.app.server.atavism.server.pathing.PathObject;
import java.util.LinkedHashMap;
import com.app.server.atavism.server.objects.Boundary;
import atavism.msgsys.SubjectFilter;
import com.app.server.atavism.server.engine.QuadTreeElement;
import com.app.server.atavism.server.engine.PerceiverFilter;
import atavism.msgsys.SubjectMessage;
import com.app.server.atavism.server.engine.WorldNode;
import atavism.msgsys.ResponseMessage;
import com.app.server.atavism.server.pathing.PathInterpolator;
import com.app.server.atavism.server.pathing.PathLinear;
import com.app.server.atavism.server.pathing.PathSpline;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.objects.Color;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.objects.Light;
import atavism.server.engine.SoundManager;
import java.util.ListIterator;
import com.app.server.atavism.server.objects.LightData;
import atavism.server.objects.RoadRegionConfig;
import atavism.server.objects.FogRegionConfig;
import atavism.server.objects.SoundRegionConfig;
import atavism.server.objects.RegionConfig;
import com.app.server.atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import com.app.server.atavism.server.objects.SoundData;
import com.app.server.atavism.server.objects.DisplayContext;
import com.app.server.atavism.server.objects.Template;
import com.app.server.atavism.server.engine.BasicWorldNode;
import java.util.ArrayList;
import java.io.Serializable;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.pathing.PathSearcher;
import atavism.msgsys.SubscriptionHandle;
import atavism.msgsys.AgentHandle;
import atavism.msgsys.FilterUpdate;
import java.util.Iterator;
import atavism.server.messages.PerceptionMessage;
import com.app.server.atavism.server.engine.PerceiverNewsAndFrees;
import com.app.server.atavism.server.engine.FixedPerceiver;
import com.app.server.atavism.server.engine.Perceiver;
import com.app.server.atavism.server.engine.MobilePerceiver;
import atavism.msgsys.Message;
import com.app.server.atavism.server.objects.ObjectType;
import com.app.server.atavism.server.objects.ObjectTypes;
import atavism.server.objects.Fog;
import com.app.server.atavism.server.objects.Region;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.engine.QuadTreeNode;
import com.app.server.atavism.server.objects.AOObject;
import com.app.server.atavism.server.objects.Entity;
import com.app.server.atavism.server.objects.EntityManager;
import atavism.server.engine.Hook;
import java.util.List;
import atavism.msgsys.MessageTypeFilter;
import atavism.management.Management;
import atavism.msgsys.Filter;
import atavism.server.util.Log;
import atavism.msgsys.MessageTypeSessionIdFilter;
import atavism.msgsys.MessageType;
import atavism.msgsys.IFilter;
import atavism.server.messages.INamespaceFilter;
import java.util.Collection;
import com.app.server.atavism.server.engine.Namespace;
import java.util.LinkedList;
import com.app.server.atavism.server.objects.World;
import com.app.server.atavism.server.math.Geometry;
import java.io.IOException;
import com.app.server.atavism.server.util.AORuntimeException;
import com.app.server.atavism.server.engine.Engine;
import java.util.HashSet;
import java.util.HashMap;
import com.app.server.atavism.server.pathing.PathInfo;
import java.util.Set;
import atavism.server.objects.RegionTrigger;
import com.app.server.atavism.server.engine.QuadTree;
import com.app.server.atavism.server.engine.OID;
import java.util.Map;

import org.apache.log4j.Logger;

import atavism.server.messages.PerceptionFilter;
import atavism.server.messages.SubObjectFilter;
import atavism.server.messages.PerceptionUpdateTrigger;
import com.app.server.atavism.server.engine.WMWorldNode;
import com.app.server.atavism.server.engine.PerceiverCallback;
import atavism.msgsys.MessageCallback;
import com.app.server.atavism.server.engine.EnginePlugin;

public abstract class WorldManagerPlugin extends EnginePlugin implements MessageCallback, PerceiverCallback<WMWorldNode>, PerceptionUpdateTrigger {
	protected static final Logger log = Logger.getLogger("navmesh");
	FixedPerceiverMap fixedPerceiverMap;
	OidSubscriptionMap remoteMobSubscription;
	private int maxObjects;
	private int maxDepth;
	protected SubObjectFilter subObjectFilter;
	protected WorldManagerFilter newRegionFilter;
	protected long newRegionSub;
	protected PerceptionFilter mobFilter;
	protected long mobSubId;
	protected PerceptionFilter mobRPCFilter;
	protected long mobRPCSubId;
	protected PerceptionFilter structFilter;
	protected long structSubId;
	protected PerceptionFilter structRPCFilter;
	protected long structRPCSubId;
	protected int defaultWorldManagerHysteresis;
	protected Map<OID, QuadTree<WMWorldNode>> quadtrees;
	protected Map<String, RegionTrigger> regionTriggers;
	protected Set<String> propertyExclusions;
	protected PathInfo pathInfo;
	protected boolean askedForPathInfo;
	protected Updater updater;
	protected static PathObjectCache pathObjectCache;
	public static final String REGION_MEMBERSHIP;

	public WorldManagerPlugin() {
		this.fixedPerceiverMap = new FixedPerceiverMap();
		this.remoteMobSubscription = new OidSubscriptionMap();
		this.maxObjects = 30;
		this.maxDepth = 20;
		this.defaultWorldManagerHysteresis = 20;
		this.quadtrees = new HashMap<OID, QuadTree<WMWorldNode>>();
		this.regionTriggers = new HashMap<String, RegionTrigger>();
		this.propertyExclusions = new HashSet<String>();
		this.pathInfo = null;
		this.askedForPathInfo = false;
		this.updater = null;
		this.setPluginType("WorldManager");
		String wmAgentName;
		try {
			wmAgentName = Engine.getAgent().getDomainClient().allocName("PLUGIN", this.getPluginType() + "#");
		} catch (IOException e) {
			throw new AORuntimeException("Could not allocate world manager plugin name", e);
		}
		this.setName(wmAgentName);
		this.propertyExclusions.add("aoobj.wnode");
		this.propertyExclusions.add("aoobj.perceiver");
		this.propertyExclusions.add("aoobj.dc");
		this.propertyExclusions.add("aoobj.statemap");
		PerceptionFilter.addUpdateTrigger(this);
	}

	@Override
	public void onActivate() {
		try {
			this.registerHooks();
			final Integer maxObjects = Engine.getIntProperty("atavism.quad_tree_node_max_objects");
			final Integer maxDepth = Engine.getIntProperty("atavism.quad_tree_max_depth");
			if (maxObjects != null) {
				this.maxObjects = maxObjects;
			}
			if (maxDepth != null) {
				this.maxDepth = maxDepth;
			}
			final Integer minX = Engine.getIntProperty("atavism.geometry_min_x");
			final Integer maxX = Engine.getIntProperty("atavism.geometry_max_x");
			final Integer minZ = Engine.getIntProperty("atavism.geometry_min_z");
			final Integer maxZ = Engine.getIntProperty("atavism.geometry_max_z");
			if (minX != null && maxX != null && minZ != null && maxZ != null) {
				Geometry.GEO_MIN_X = minX;
				Geometry.GEO_MAX_X = maxX;
				Geometry.GEO_MIN_Z = minZ;
				Geometry.GEO_MAX_Z = maxZ;
			}
			final Geometry localGeo = World.getLocalGeometry();
			if (localGeo == null) {
				throw new RuntimeException("null local geometry");
			}
			final List<Namespace> namespaces = new LinkedList<Namespace>();
			namespaces.add(Namespace.WORLD_MANAGER);
			namespaces.add(Namespace.WM_INSTANCE);
			final WorldManagerFilter selectionFilter = new WorldManagerFilter(this.getName());
			(this.subObjectFilter = new SubObjectFilter()).setMatchSubjects(true);
			this.registerPluginNamespaces(namespaces, new WorldManagerGenerateSubObjectHook(), selectionFilter, this.subObjectFilter);
			final HostInstanceFilter hostInstanceFilter = new HostInstanceFilter(this.getName());
			hostInstanceFilter.addType(WorldManagerClient.MSG_TYPE_HOST_INSTANCE);
			Engine.getAgent().createSubscription(hostInstanceFilter, this, 8);
			(this.newRegionFilter = new WorldManagerFilter()).setNamespaces(namespaces);
			this.newRegionFilter.addType(WorldManagerClient.MSG_TYPE_NEW_REGION);
			this.newRegionFilter.addType(WorldManagerClient.MSG_TYPE_PLAYER_PATH_WM_REQ);
			this.newRegionSub = Engine.getAgent().createSubscription(this.newRegionFilter, this);
			this.registerLoadHook(Namespace.WORLD_MANAGER, new MobLoadHook());
			this.registerUnloadHook(Namespace.WORLD_MANAGER, new MobUnloadHook());
			this.registerDeleteHook(Namespace.WORLD_MANAGER, new MobDeleteHook());
			this.registerLoadHook(WorldManagerClient.INSTANCE_NAMESPACE, new InstanceLoadHook());
			this.registerUnloadHook(WorldManagerClient.INSTANCE_NAMESPACE, new InstanceUnloadHook());
			this.registerDeleteHook(WorldManagerClient.INSTANCE_NAMESPACE, new InstanceDeleteHook());
			final LinkedList<MessageType> types = new LinkedList<MessageType>();
			types.add(WorldManagerClient.MSG_TYPE_REFRESH_WNODE);
			types.add(WorldManagerClient.MSG_TYPE_MODIFY_DC);
			types.add(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
			types.add(WorldManagerClient.MSG_TYPE_UPDATEWNODE_REQ);
			types.add(WorldManagerClient.MSG_TYPE_ORIENT_REQ);
			types.add(WorldManagerClient.MSG_TYPE_MOB_PATH_REQ);
			types.add(WorldManagerClient.MSG_TYPE_REPARENT_WNODE_REQ);
			types.add(WorldManagerClient.MSG_TYPE_COM_REQ);
			(this.mobFilter = new PerceptionFilter(types)).setMatchSubjects(true);
			this.mobSubId = Engine.getAgent().createSubscription(this.mobFilter, this);
			types.clear();
			types.add(WorldManagerClient.MSG_TYPE_OBJINFO_REQ);
			types.add(WorldManagerClient.MSG_TYPE_DC_REQ);
			types.add(WorldManagerClient.MSG_TYPE_SPAWN_REQ);
			types.add(WorldManagerClient.MSG_TYPE_DESPAWN_REQ);
			types.add(WorldManagerClient.MSG_TYPE_SETWNODE_REQ);
			types.add(WorldManagerClient.MSG_TYPE_GETWNODE_REQ);
			types.add(WorldManagerClient.MSG_TYPE_GET_OBJECTS_IN);
			(this.mobRPCFilter = new PerceptionFilter(types)).setMatchSubjects(true);
			this.mobRPCSubId = Engine.getAgent().createSubscription(this.mobRPCFilter, this, 8);
			final Filter percFilter = new MessageTypeSessionIdFilter(WorldManagerClient.MSG_TYPE_PERCEIVER_REGIONS, Engine.getAgent().getName(), true);
			final Long percSub = Engine.getAgent().createSubscription(percFilter, this);
			if (percSub == null) {
				throw new AORuntimeException("create perceiver sub failed");
			}
			if (Log.loggingDebug) {
				Log.debug("created perceiver regions subscriptions: " + percSub);
			}
			final WorldManagerTransferFilter transferFilter = new WorldManagerTransferFilter();
			transferFilter.addGeometry(localGeo);
			final Hook transferHook = new WorldManagerTransferHook();
			this.registerTransferHook(transferFilter, transferHook);
			final MessageTypeSessionIdFilter remoteMobFilter = new MessageTypeSessionIdFilter(Engine.getAgent().getName());
			remoteMobFilter.matchesNullSessionId(false);
			remoteMobFilter.addType(WorldManagerClient.MSG_TYPE_NEW_REMOTE_OBJ);
			remoteMobFilter.addType(WorldManagerClient.MSG_TYPE_FREE_REMOTE_OBJ);
			Engine.getAgent().createSubscription(remoteMobFilter, this);
			types.clear();
			types.add(Management.MSG_TYPE_GET_PLUGIN_STATUS);
			Engine.getAgent().createSubscription(new MessageTypeFilter(types), this, 8);
			Engine.registerStatusReportingPlugin(this);
			this.startUpdater();
		} catch (Exception e) {
			Log.exception("WorldManagerPlugin.onActivate failed", e);
			throw new AORuntimeException("activate failed", e);
		}
	}

	@Override
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

	protected void startUpdater() {
		this.updater = new Updater();
		final Thread updateThread = new Thread(this.updater, "WMUpdater");
		updateThread.start();
	}

	public void sendRegionUpdate(final AOObject obj) {
		final WMWorldNode wnode = (WMWorldNode) obj.worldNode();
		final QuadTreeNode<WMWorldNode> quadNode = wnode.getQuadNode();
		if (quadNode != null) {
			final Point loc = wnode.getCurrentLoc();
			final List<Region> regionList = quadNode.getRegionByLoc(loc);
			if (regionList != null) {
				synchronized (this.updater) {
					this.updater.updateRegion(obj, regionList);
				}
			}
		}
	}

	Fog getInstanceFog(final OID instanceOid) {
		final WorldManagerInstance instance = (WorldManagerInstance) EntityManager.getEntityByNamespace(instanceOid, WorldManagerClient.INSTANCE_NAMESPACE);
		return instance.getGlobalFog();
	}

	protected void registerHooks() {
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_GET_OBJECTS_IN, new GetObjectsInHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_OBJINFO_REQ, new ObjectInfoReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DC_REQ, new DisplayContextReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWN_REQ, new SpawnReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWN_REQ, new DespawnReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT, new UpdateObjectHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_GETWNODE_REQ, new GetWNodeReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_COM_REQ, new ComReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_ORIENT_REQ, new OrientReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_REFRESH_WNODE, new RefreshWNodeHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_PERCEIVER_REGIONS, new PerceiverRegionsHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_NEW_REMOTE_OBJ, new NewRemoteObjHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_FREE_REMOTE_OBJ, new FreeRemoteObjHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_MOB_PATH_REQ, new MobPathReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_NEW_REGION, new NewRegionHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_MODIFY_DC, new ModifyDisplayContextHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_HOST_INSTANCE, new HostInstanceHook());
		this.getHookManager().addHook(Management.MSG_TYPE_GET_PLUGIN_STATUS, new GetPluginStatusHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_PLAYER_PATH_WM_REQ, new PlayerPathWMReqHook());
	}

	protected WorldManagerClient.PerceptionInfo makePerceptionInfo(final OID oid, final AOObject object) {
		final WorldManagerClient.ObjectInfo objectInfo = this.makeObjectInfo(oid);
		final WorldManagerClient.PerceptionInfo info = new WorldManagerClient.PerceptionInfo(objectInfo);
		final ObjectType objectType = object.getType();
		if (objectType != ObjectTypes.light && objectType != WorldManagerClient.TEMPL_OBJECT_TYPE_TERRAIN_DECAL && objectType != WorldManagerClient.TEMPL_OBJECT_TYPE_POINT_SOUND) {
			info.displayContext = this.getDisplayContext(oid);
		}
		return info;
	}

	protected void sendWMMessage(final Message msg) {
		Engine.getAgent().sendBroadcast(msg);
	}

	protected OID getPerceiverOid(final MobilePerceiver<WMWorldNode> mobileP) {
		final WMWorldNode pWnode = mobileP.getElement();
		final AOObject pObj = pWnode.getObject();
		return pObj.getOid();
	}

	public void newObjectForFixedPerceiver(final Perceiver<WMWorldNode> p, final WMWorldNode newWnode) {
		final FixedPerceiver<WMWorldNode> fixedP = (FixedPerceiver<WMWorldNode>) (FixedPerceiver) p;
		Engine.getAgent().sendBroadcast(this.makeNewObjectForFixedPerceiverMessage(fixedP, newWnode));
	}

	protected WorldManagerClient.NewRemoteObjectMessage makeNewObjectForFixedPerceiverMessage(final FixedPerceiver<WMWorldNode> fixedP, final WMWorldNode newWnode) {
		final String remoteSessionId = this.fixedPerceiverMap.getSessionId(fixedP);
		if (remoteSessionId == null) {
			throw new RuntimeException("unknown remoteSessionId " + remoteSessionId);
		}
		final AOObject newObj = newWnode.getObject();
		final OID newOid = newObj.getOid();
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("newObjectForFixedPerceiver: objOid=" + newOid + ", newWnode=" + newWnode + ", remoteSessionId=" + remoteSessionId);
		}
		final WorldManagerClient.NewRemoteObjectMessage newObjMsg = new WorldManagerClient.NewRemoteObjectMessage(remoteSessionId, newWnode.getInstanceOid(), newOid, newWnode.getLoc(),
				newWnode.getOrientation(), newWnode.getPerceptionRadius(), newObj.getType());
		return newObjMsg;
	}

	public void freeObjectForFixedPerceiver(final Perceiver<WMWorldNode> p, final WMWorldNode freeWnode) {
		final FixedPerceiver<WMWorldNode> fixedP = (FixedPerceiver<WMWorldNode>) (FixedPerceiver) p;
		Engine.getAgent().sendBroadcast(this.makeFreeObjectForFixedPerceiverMessage(fixedP, freeWnode));
	}

	protected WorldManagerClient.FreeRemoteObjectMessage makeFreeObjectForFixedPerceiverMessage(final FixedPerceiver<WMWorldNode> fixedP, final WMWorldNode freeWnode) {
		final String remoteSessionId = this.fixedPerceiverMap.getSessionId(fixedP);
		if (remoteSessionId == null) {
			throw new RuntimeException("unknown remoteSessionId " + remoteSessionId);
		}
		final AOObject freeObj = freeWnode.getObject();
		final OID freeOid = freeObj.getOid();
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("freeFixedObj: objOid=" + freeOid + ", wnode=" + freeWnode + ", remoteSessionId=" + remoteSessionId);
		}
		final WorldManagerClient.FreeRemoteObjectMessage msg = new WorldManagerClient.FreeRemoteObjectMessage(remoteSessionId, freeWnode.getInstanceOid(), freeOid);
		return msg;
	}

	@Override
	public Integer processNewsAndFrees(final Perceiver<WMWorldNode> p, final PerceiverNewsAndFrees<WMWorldNode> newsAndFrees, final OID perceiverOid) {
		if (Log.loggingDebug) {
			Log.debug("processNewsAndFrees: perceiverOid " + perceiverOid + " freeCount=" + newsAndFrees.getFreedElements().size() + " newCount=" + newsAndFrees.getNewElements().size());
		}
		if (p instanceof MobilePerceiver) {
			final MobilePerceiver<WMWorldNode> mobileP = (MobilePerceiver<WMWorldNode>) (MobilePerceiver) p;
			final OID pOid = this.getPerceiverOid(mobileP);
			final OID instanceOid = mobileP.getElement().getInstanceOid();
			final QuadTree<WMWorldNode> quadtree = this.quadtrees.get(instanceOid);
			if (quadtree == null) {
				Log.error("processNewsAndFrees: unknown instanceOid=" + instanceOid + " oid=" + pOid);
				return null;
			}
			if (Log.loggingDebug) {
				Log.debug("processNewsAndFrees: perceiverOid " + perceiverOid + ", pOid " + pOid);
			}
			if (quadtree.spawningNewsAndFrees != null && !quadtree.spawningNewsAndFrees.getMap().values().contains(newsAndFrees)) {
				System.out.println("perceiverOid " + perceiverOid + ", pOid " + pOid + " freeCount=" + newsAndFrees.getFreedElements().size() + " newCount=" + newsAndFrees.getNewElements().size());
				Thread.dumpStack();
			}
			if (World.DEBUG_OID.equals(perceiverOid) || World.DEBUG_OID.equals(pOid)) {
				Log.info("processNewsAndFrees: oid=" + perceiverOid + " pOid=" + pOid + " newCount=" + newsAndFrees.getNewElements().size() + " freeCount=" + newsAndFrees.getFreedElements().size());
			}
			final PerceptionMessage message = new PerceptionMessage(WorldManagerClient.MSG_TYPE_PERCEPTION_INFO, pOid);
			int count = 0;
			for (final WMWorldNode lostNode : newsAndFrees.getFreedElements()) {
				final AOObject lostObj = lostNode.getObject();
				final OID lostOid = lostObj.getOid();
				message.lostObject(pOid, lostOid, lostObj.getType());
				++count;
			}
			for (final WMWorldNode gainNode : newsAndFrees.getNewElements()) {
				final AOObject gainObj = gainNode.getObject();
				final OID gainOid = gainObj.getOid();
				final PerceptionMessage.ObjectNote note = new PerceptionMessage.ObjectNote(pOid, gainOid, gainObj.getType());
				note.setObjectInfo(this.makePerceptionInfo(gainOid, gainObj));
				message.gainObject(note);
				++count;
			}
			Engine.getAgent().sendBroadcast(message);
			Log.debug("PERCEP2: sending Perception Message with oid: " + message.getTarget() + " and gainCount=" + message.getGainObjectCount() + " and lostCount=" + message.getLostObjectCount());
			message.setMsgType(WorldManagerClient.MSG_TYPE_PERCEPTION);
			if (message.getGainObjects() != null) {
				for (final PerceptionMessage.ObjectNote gainNote : message.getGainObjects()) {
					gainNote.setObjectInfo(null);
				}
			}
			Engine.getAgent().sendBroadcast(message);
			if (pOid.equals(perceiverOid)) {
				return count;
			}
			return null;
		} else {
			if (p instanceof FixedPerceiver) {
				final FixedPerceiver<WMWorldNode> fixedP = (FixedPerceiver<WMWorldNode>) (FixedPerceiver) p;
				for (final WMWorldNode freeNode : newsAndFrees.getFreedElements()) {
					this.freeObjectForFixedPerceiver(fixedP, freeNode);
				}
				for (final WMWorldNode newNode : newsAndFrees.getNewElements()) {
					this.newObjectForFixedPerceiver(fixedP, newNode);
				}
				return null;
			}
			throw new RuntimeException("unknown perceiver type");
		}
	}

	@Override
	public void preUpdate(final PerceptionFilter filter, final FilterUpdate.Instruction instruction, final AgentHandle sender, final SubscriptionHandle sub) {
		if (instruction.opCode == 2 && instruction.fieldId == 1) {
			MessageType perceptionType = null;
			if (filter.getMessageTypes().contains(WorldManagerClient.MSG_TYPE_PERCEPTION_INFO)) {
				perceptionType = WorldManagerClient.MSG_TYPE_PERCEPTION_INFO;
			} else {
				if (!filter.getMessageTypes().contains(WorldManagerClient.MSG_TYPE_PERCEPTION)) {
					return;
				}
				perceptionType = WorldManagerClient.MSG_TYPE_PERCEPTION;
			}
			final OID targetOid = (OID) instruction.value;
			final AOObject obj = (AOObject) this.getWorldManagerEntity(targetOid);
			if (obj == null) {
				return;
			}
			WMWorldNode targetNode = (WMWorldNode) obj.worldNode();
			if (targetNode == null) {
				return;
			}
			final QuadTree<WMWorldNode> quadtree = this.quadtrees.get(targetNode.getInstanceOid());
			quadtree.getLock().lock();
			Collection<WMWorldNode> perceivables;
			PerceptionMessage message;
			try {
				targetNode = (WMWorldNode) obj.worldNode();
				if (targetNode == null) {
					return;
				}
				perceivables = quadtree.getElementPerceivables(targetNode);
				message = new PerceptionMessage(perceptionType, targetOid);
				for (final WMWorldNode gainNode : perceivables) {
					final AOObject gainObj = gainNode.getObject();
					final OID gainOid = gainObj.getOid();
					if (gainOid.equals(targetOid)) {
						continue;
					}
					final PerceptionMessage.ObjectNote note = new PerceptionMessage.ObjectNote(targetOid, gainOid, gainObj.getType());
					if (perceptionType == WorldManagerClient.MSG_TYPE_PERCEPTION_INFO) {
						note.setObjectInfo(this.makePerceptionInfo(gainOid, gainObj));
					}
					message.gainObject(note);
				}
			} finally {
				quadtree.getLock().unlock();
			}
			if (perceivables.size() > 0) {
				if (Log.loggingDebug) {
					Log.debug("PerceptionUpdateTrigger: sending initial perception for oid=" + targetOid + " agent=" + sender.getAgentName());
				}
				Engine.getAgent().sendDirect(message, sender, sub);
			}
		}
	}

	@Override
	public void postUpdate(final PerceptionFilter filter, final FilterUpdate.Instruction instruction, final AgentHandle sender, final SubscriptionHandle sub) {
	}

	public PathInfo getPathInfo() {
		return this.pathInfo;
	}

	public void setPathInfo(final PathInfo pathInfo) {
		PathSearcher.createPathSearcher(this.pathInfo = pathInfo, World.getGeometry());
	}

	protected WorldManagerClient.ObjectInfo makeObjectInfo(final OID oid) {
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("makeObjectInfo: oid=" + oid);
		}
		final Entity entity = this.getWorldManagerEntity(oid);
		if (entity == null) {
			return null;
		}
		if (!(entity instanceof AOObject)) {
			throw new AORuntimeException("entity is not AOObject");
		}
		final AOObject obj = (AOObject) entity;
		InterpolatedWorldNode.InterpolatedDirLocOrientTime before = null;
		if (Log.loggingDebug) {
			before = obj.getDirLocOrientTime();
		}
		final CaptureInterpWorldNode capture = new CaptureInterpWorldNode((InterpolatedWorldNode) obj.getProperty("aoobj.wnode"));
		capture.getLoc();
		final InterpolatedWorldNode.InterpolatedDirLocOrientTime vals = capture.getDirLocOrientTime();
		if (Log.loggingDebug) {
			final float distance = Point.distanceTo(vals.interpLoc, before.interpLoc);
			if (distance != 0.0f) {
				Log.debug("DISTANCE " + distance + " TIME " + (vals.lastInterp - before.lastInterp) + " DIR " + vals.dir + " oid=" + obj.getOid());
			}
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
				final WorldManagerClient.MobPathMessage pathMsg = (WorldManagerClient.MobPathMessage) pathMsgObject;
				objInfo.setProperty(WorldManagerClient.MOB_PATH_PROPERTY, pathMsg);
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
					Log.debug("[CYC][1] entityType: " + entity.getType() + ", objectType: " + objectType);
					final Point entityLoc = entityWorldNode.getLoc();
					Log.debug("[CYC][1] loc: " + loc + ", entityLoc: " + entityLoc + ", entityName: " + entity.getName());
					if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
						objectsIn.add(entityOid);
					}
					Log.debug("[CYC][1] distance: " + Math.round(Point.distanceTo(loc, entityLoc)) + ", radius: " + radius);
				}
			}
		} else {
			for (final Entity entity : entities) {
				final OID entityOid = entity.getOid();
				final BasicWorldNode entityWorldNode = ((AOObject) this.getWorldManagerEntity(entityOid)).baseWorldNode();
				if (instanceOid.equals(entityWorldNode.getInstanceOid())) {
					Log.debug("[CYC][2] entityType: " + entity.getType());
					final Point entityLoc = entityWorldNode.getLoc();
					Log.debug("[CYC][2] loc: " + loc + ", entityLoc: " + entityLoc + ", entityName: " + entity.getName());
					if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
						objectsIn.add(entityOid);
					}
					Log.debug("[CYC][2] distance: " + Math.round(Point.distanceTo(loc, entityLoc)) + ", radius: " + radius);
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

	WorldManagerInstance createInstanceEntity(final OID instanceOid) {
		final WorldManagerInstance instance = new WorldManagerInstance(instanceOid);
		this.initializeInstance(instance);
		EntityManager.registerEntityByNamespace(instance, WorldManagerClient.INSTANCE_NAMESPACE);
		return instance;
	}

	void initializeInstance(final WorldManagerInstance instance) {
		final Geometry localGeo = Geometry.maxGeometry();
		if (localGeo == null) {
			throw new RuntimeException("null local geometry");
		}
		final QuadTree<WMWorldNode> quadtree = new QuadTree<WMWorldNode>(Geometry.maxGeometry(), this.defaultWorldManagerHysteresis);
		this.quadtrees.put(instance.getOid(), quadtree);
		quadtree.setMaxObjects(this.maxObjects);
		quadtree.setMaxDepth(this.maxDepth);
		quadtree.setLocalGeometry(localGeo);
		instance.setQuadTree(quadtree);
	}

	void hostInstance(final OID masterOid, final Geometry localGeo) {
		final WorldManagerFilter.InstanceGeometry instanceGeo = new WorldManagerFilter.InstanceGeometry();
		instanceGeo.instanceOid = masterOid;
		(instanceGeo.geometry = new ArrayList<Geometry>(1)).add(localGeo);
		FilterUpdate filterUpdate = new FilterUpdate();
		filterUpdate.addFieldValue(1, instanceGeo);
		((WorldManagerFilter) WorldManagerPlugin.selectionFilter).applyFilterUpdate(filterUpdate);
		Engine.getAgent().applyFilterUpdate(WorldManagerPlugin.selectionSubscription, filterUpdate, 1);
		filterUpdate = new FilterUpdate();
		filterUpdate.addFieldValue(1, instanceGeo);
		this.newRegionFilter.applyFilterUpdate(filterUpdate);
		Engine.getAgent().applyFilterUpdate(this.newRegionSub, filterUpdate, 1);
	}

	void unhostInstance(final OID oid) {
		FilterUpdate filterUpdate = new FilterUpdate();
		filterUpdate.removeFieldValue(1, oid);
		((WorldManagerFilter) WorldManagerPlugin.selectionFilter).applyFilterUpdate(filterUpdate);
		Engine.getAgent().applyFilterUpdate(WorldManagerPlugin.selectionSubscription, filterUpdate, 1);
		filterUpdate = new FilterUpdate();
		filterUpdate.removeFieldValue(1, oid);
		this.newRegionFilter.applyFilterUpdate(filterUpdate);
		Engine.getAgent().applyFilterUpdate(this.newRegionSub, filterUpdate, 1);
	}

	public String getInstanceInfoString(final OID instanceOid) {
		String info = "";
		final Entity entity = EntityManager.getEntityByNamespace(instanceOid, WorldManagerClient.INSTANCE_NAMESPACE);
		if (entity != null) {
			info += "Entity:               yes\n";
			info = info + "Entity class:         " + entity.getClass().getSimpleName() + "\n";
			info = info + "Entity name:          " + entity.getName() + "\n";
		} else {
			info += "Entity:               no\n";
		}
		final QuadTree<WMWorldNode> quadtree = this.quadtrees.get(instanceOid);
		if (quadtree != null) {
			info += "Quad tree:            yes\n";
		} else {
			info += "Quad tree:            no\n";
		}
		final boolean rc = this.subObjectFilter.hasSubject(instanceOid);
		info = info + "Sub-object filter:    " + rc + "\n";
		List<Geometry> geometry = this.newRegionFilter.getInstance(instanceOid);
		if (geometry != null) {
			info += "New region filter:    yes\n";
			info = info + "New region geometry:  " + geometry.get(0) + "\n";
		} else {
			info += "New region filter:    no\n";
		}
		final WorldManagerFilter filter = (WorldManagerFilter) WorldManagerPlugin.selectionFilter;
		geometry = filter.getInstance(instanceOid);
		if (geometry != null) {
			info += "Selection filter:     yes\n";
			info = info + "Selection geometry:   " + geometry.get(0) + "\n";
		} else {
			info += "Selection filter:     no\n";
		}
		return info;
	}

	protected AOObject generateWorldManagerSubObject(final Template template, final OID masterOid) {
		final AOObject wObj = new AOObject(masterOid);
		final Map<String, Serializable> props = template.getSubMap(Namespace.WORLD_MANAGER);
		if (props == null) {
			Log.warn("WorldManagerPlugin.generateSubObject: no props in ns " + Namespace.WORLD_MANAGER);
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

	protected void subscribeForMob(final OID oid) {
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("subscribeForMob: oid=" + oid);
		}
		if (this.mobFilter.addSubjectIfMissing(oid)) {
			final FilterUpdate filterUpdate = new FilterUpdate();
			filterUpdate.addFieldValue(2, oid);
			Engine.getAgent().applyFilterUpdate(this.mobSubId, filterUpdate, 1);
		} else {
			Log.debug("subscribeForMob: mobFilter double bind oid=" + oid);
		}
		if (this.mobRPCFilter.addSubjectIfMissing(oid)) {
			final FilterUpdate filterUpdate = new FilterUpdate();
			filterUpdate.addFieldValue(2, oid);
			Engine.getAgent().applyFilterUpdate(this.mobRPCSubId, filterUpdate, 1);
		} else {
			Log.debug("subscribeForMob: mobRPCFilter double bind oid=" + oid);
		}
		this.subscribeForObject(oid);
	}

	protected void subscribeForObject(final OID masterOid) {
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("subscribeForObject: oid=" + masterOid);
		}
		if (this.subObjectFilter.addSubjectIfMissing(masterOid)) {
			final FilterUpdate filterUpdate = new FilterUpdate();
			filterUpdate.addFieldValue(2, masterOid);
			Engine.getAgent().applyFilterUpdate(WorldManagerPlugin.subObjectSubscription, filterUpdate, 1);
		} else {
			Log.debug("subscribeForObject: subObjectFilter double bind oid=" + masterOid);
		}
	}

	protected void unsubscribeForMob(final OID oid) {
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("unsubscribeForObject: oid=" + oid);
		}
		if (this.mobFilter.removeSubject(oid)) {
			final FilterUpdate filterUpdate = new FilterUpdate();
			filterUpdate.removeFieldValue(2, oid);
			Engine.getAgent().applyFilterUpdate(this.mobSubId, filterUpdate, 1);
		} else {
			Log.debug("unsubscribeForObject: mobFilter double remove oid=" + oid);
		}
		if (this.mobRPCFilter.removeSubject(oid)) {
			final FilterUpdate filterUpdate = new FilterUpdate();
			filterUpdate.removeFieldValue(2, oid);
			Engine.getAgent().applyFilterUpdate(this.mobRPCSubId, filterUpdate, 1);
		} else {
			Log.debug("unsubscribeForObject: mobRPCFilter double remove oid=" + oid);
		}
		this.unsubscribeForObject(oid);
	}

	protected void unsubscribeForObject(final OID oid) {
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("unsubscribeForObject: oid=" + oid);
		}
		if (this.subObjectFilter.removeSubject(oid)) {
			final FilterUpdate filterUpdate = new FilterUpdate();
			filterUpdate.removeFieldValue(2, oid);
			Engine.getAgent().applyFilterUpdate(WorldManagerPlugin.subObjectSubscription, filterUpdate, 1);
		} else {
			Log.debug("unsubscribeForObject: subObjectFilter double remove oid=" + oid);
		}
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
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("spawnObject: registering perceiver cb: " + obj + ", masterOid " + mobilePerceiverOid);
			}
			if (wnode.getPerceiver() == null) {
				throw new AORuntimeException("wnode doesnt have perceiver, obj=" + obj);
			}
			p.registerCallback(this);
		} else if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("spawnObject: no perceiver for obj " + obj);
		}
		wnode.isLocal(true);
		wnode.isSpawned(true);
		final Integer newsAndFressCount = quadtree.addElementReturnCountForPerceiver(wnode, mobilePerceiverOid);
		if (Log.loggingDebug) {
			WorldManagerPlugin.log.debug("spawnObject: spawned obj: " + obj + ", wnode=" + wnode);
		}
		return newsAndFressCount;
	}

	protected void despawnObject(final AOObject obj) {
		if (obj.isUser()) {
			Engine.getPersistenceManager().setDirty(obj);
		}
		final WMWorldNode wnode = (WMWorldNode) obj.worldNode();
		if (wnode == null) {
			throw new AORuntimeException("obj has no world node: " + obj);
		}
		final QuadTree<WMWorldNode> quadtree = this.quadtrees.get(wnode.getInstanceOid());
		if (quadtree == null) {
			WorldManagerPlugin.log.error("despawnObject: unknown instanceOid=" + wnode.getInstanceOid() + " oid=" + obj.getOid());
			return;
		}
		quadtree.getLock().lock();
		try {
			if (wnode.getQuadNode() == null) {
				return;
			}
			final AOObject backRef = wnode.getObject();
			if (backRef == null) {
				throw new AORuntimeException("obj wnode backref is null: " + obj);
			}
			if (!backRef.getOid().equals(obj.getOid())) {
				throw new AORuntimeException("obj wnode backref does not match: " + obj);
			}
			wnode.isSpawned(false);
			final boolean elementRemoved = quadtree.removeElement(wnode);
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("despawnObject: despawned obj: " + obj + " with node removed: " + elementRemoved);
			}
		} finally {
			quadtree.getLock().unlock();
		}
		if (obj.isUser()) {
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("despawnObject: removing regions for oid=" + obj.getOid());
			}
			final List<Region> regionList = new ArrayList<Region>(0);
			synchronized (this.updater) {
				this.updater.updateRegion(obj, regionList);
			}
		}
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

	protected void sendObjectSoundMessage(final OID notifyOid, final AOObject updateObj, final List<SoundData> soundData) {
		final WorldManagerClient.SoundMessage soundMsg = new WorldManagerClient.SoundMessage(updateObj.getOid());
		soundMsg.setSoundData(soundData);
		Engine.getAgent().sendBroadcast(soundMsg);
	}

	public void registerRegionTrigger(final String name, final RegionTrigger trigger) {
		this.regionTriggers.put(name, trigger);
	}

	public Set<String> getPropertyExclusions() {
		return this.propertyExclusions;
	}

	static {
		log = new Logger("WorldManagerPlugin");
		WorldManagerPlugin.pathObjectCache = new PathObjectCache();
		REGION_MEMBERSHIP = (String) Entity.registerTransientPropertyKey("customRegions");
	}

	class Updater implements Runnable {
		Lock dirLightLock;

		Updater() {
			this.dirLightLock = LockFactory.makeLock("DirLightLock");
		}

		@Override
		public void run() {
			while (true) {
				try {
					this.update();
				} catch (AORuntimeException e) {
					Log.exception("WorldManagerPluging.Updater.run caught AORuntimeException", e);
				} catch (Exception e2) {
					Log.exception("WorldManagerPluging.Updater.run caught exception", e2);
				}
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e3) {
					Log.warn("Updater: " + e3);
					e3.printStackTrace();
				}
			}
		}

		protected void update() {
			WorldManagerPlugin.log.debug("Update.update: in update");
			for (final Entity e : EntityManager.getAllEntitiesByNamespace(Namespace.WORLD_MANAGER)) {
				Label_0420 : {
					if (e instanceof AOObject) {
						final AOObject obj = (AOObject) e;
						final WMWorldNode wnode = (WMWorldNode) obj.worldNode();
						if (wnode != null) {
							if (wnode.isSpawned()) {
								if (wnode.isLocal()) {
									Point loc = null;
									if (obj.isMob() || obj.isUser()) {
										if (obj.getPersistenceFlag()) {
											Long lastSaved = (Long) obj.getProperty(WorldManagerClient.WMGR_LAST_SAVED_PROP);
											if (lastSaved == null) {
												lastSaved = 0L;
											}
											final Long currentTime = System.currentTimeMillis();
											final Long elapsed = currentTime - lastSaved;
											if (elapsed > WorldManagerClient.WMGR_SAVE_INTERVAL_MS && Log.loggingDebug) {
												Log.debug("update: elapsedTime=" + elapsed + ", marking obj dirty: " + obj);
												obj.setProperty(WorldManagerClient.WMGR_LAST_SAVED_PROP, currentTime);
												Engine.getPersistenceManager().setDirty(obj);
											}
										}
										loc = wnode.getLoc();
										if (this.zoneObject(obj, wnode, loc)) {
											break Label_0420;
										}
									}
									if (obj.isMob() && wnode.getPathInterpolator() != null) {
										if (Log.loggingDebug) {
											WorldManagerPlugin.log.debug("Update.update: sending out wnode update for oid " + obj.getOid());
										}
										WorldManagerPlugin.this.sendWNodeMessage(obj.getOid(), obj);
									}
									if (obj.isUser()) {
										if (Log.loggingDebug) {
											WorldManagerPlugin.log.debug("Update.update: updating regions for oid " + obj.getOid());
										}
										final QuadTreeNode<WMWorldNode> quadNode = wnode.getQuadNode();
										if (quadNode != null) {
											final List<Region> regionList = quadNode.getRegionByLoc(loc);
											if (regionList != null) {
												synchronized (this) {
													this.updateRegion(obj, regionList);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		private boolean zoneObject(final AOObject obj, final WMWorldNode wnode, final Point loc) {
			final QuadTreeNode<WMWorldNode> quadNode = wnode.getQuadNode();
			if (quadNode == null) {
				return false;
			}
			final QuadTree<WMWorldNode> quadtree = WorldManagerPlugin.this.quadtrees.get(wnode.getInstanceOid());
			if (quadtree == null) {
				WorldManagerPlugin.log.error("zoneObject: unknown instanceOid=" + wnode.getInstanceOid() + " oid=" + obj.getOid());
				return false;
			}
			if (quadNode.getNodeType() == QuadTreeNode.NodeType.LOCAL || !quadNode.containsPointWithHysteresis(loc)) {
				return false;
			}
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("Update.update: obj moved into non-local node, obj=" + obj + ", wnode=" + wnode);
			}
			final OID oid = obj.getOid();
			if (!quadtree.removeElement(wnode)) {
				throw new AORuntimeException("Update.update: failed to remove element from quadtree: oid=" + oid);
			}
			if (!WorldManagerPlugin.this.removeWorldManagerEntity(oid)) {
				Log.warn("Update.update: could not remove entity " + oid);
			} else if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("Update.update: removed entity from map " + oid);
			}
			try {
				Thread.sleep(2L);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			final HashMap<String, Serializable> propMap = new HashMap<String, Serializable>();
			propMap.put(WorldManagerClient.MSG_PROP_LOC, loc);
			if (!WorldManagerPlugin.this.transferObject(propMap, obj)) {
				WorldManagerPlugin.log.error("Update.update: transfer failed for obj " + obj);
			}
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("Update.update: done zoning oid " + oid);
			}
			return true;
		}

		RegionConfig getPriorityRegionConfig(final List<Region> regionList, final String configType) {
			Region highestPriRegion = null;
			for (final Region region : regionList) {
				final RegionConfig config = region.getConfig(configType);
				if (config == null) {
					continue;
				}
				if (highestPriRegion == null) {
					highestPriRegion = region;
				} else {
					if (highestPriRegion.getPriority() <= region.getPriority()) {
						continue;
					}
					highestPriRegion = region;
				}
			}
			return (highestPriRegion == null) ? null : highestPriRegion.getConfig(configType);
		}

		List<RegionConfig> getRegionConfigs(final List<Region> regionList, final String configType) {
			LinkedList<RegionConfig> configs = null;
			for (final Region region : regionList) {
				final RegionConfig config = region.getConfig(configType);
				if (config != null) {
					if (configs == null) {
						configs = new LinkedList<RegionConfig>();
					}
					configs.add(config);
				}
			}
			return configs;
		}

		List<Region> getCustomRegionConfigs(final List<Region> regionList) {
			List<Region> result = null;
			for (final Region region : regionList) {
				if (region.getProperty("onEnter") != null || region.getProperty("onLeave") != null) {
					if (result == null) {
						result = new LinkedList<Region>();
					}
					result.add(region);
				}
			}
			return result;
		}

		void updateRegion(final AOObject obj, final List<Region> regionList) {
			final List<RegionConfig> sConfig = this.getRegionConfigs(regionList, SoundRegionConfig.RegionType);
			this.updateSoundRegion(obj, sConfig);
			final FogRegionConfig fConfig = (FogRegionConfig) this.getPriorityRegionConfig(regionList, FogRegionConfig.RegionType);
			this.updateFogRegion(obj, fConfig);
			final RoadRegionConfig rConfig = (RoadRegionConfig) this.getPriorityRegionConfig(regionList, RoadRegionConfig.RegionType);
			this.updateRoadRegion(obj, rConfig);
			final RegionConfig dirLightConfig = this.getPriorityRegionConfig(regionList, LightData.DirLightRegionType);
			this.updateDirLightRegion(obj, dirLightConfig);
			final RegionConfig ambientConfig = this.getPriorityRegionConfig(regionList, LightData.AmbientLightRegionType);
			this.updateAmbientLightRegion(obj, ambientConfig);
			final List<Region> customRegions = this.getCustomRegionConfigs(regionList);
			this.updateCustomRegions(obj, customRegions);
		}

		void updateFogRegion(final AOObject obj, final FogRegionConfig fogConfig) {
			final FogRegionConfig curFog = (FogRegionConfig) obj.getProperty(FogRegionConfig.RegionType);
			if (fogConfig == null) {
				if (curFog != null) {
					obj.setProperty(FogRegionConfig.RegionType, null);
					final Fog fog = WorldManagerPlugin.this.getInstanceFog(obj.worldNode().getInstanceOid());
					final WorldManagerClient.FogMessage fogMsg = new WorldManagerClient.FogMessage(obj.getOid(), fog);
					Engine.getAgent().sendBroadcast(fogMsg);
				}
			} else if (curFog == null || !fogConfig.equals(curFog)) {
				if (Log.loggingDebug) {
					WorldManagerPlugin.log.debug("updateFogRegion: new fog region: oldFog=" + curFog + ", newFog=" + fogConfig);
				}
				obj.setProperty(FogRegionConfig.RegionType, fogConfig);
				final WorldManagerClient.FogMessage fogMsg = new WorldManagerClient.FogMessage(obj.getOid(), fogConfig);
				Engine.getAgent().sendBroadcast(fogMsg);
			}
		}

		void updateCustomRegions(final AOObject obj, final List<Region> newRegions) {
			List<Region> currentRegions = (List<Region>) obj.getProperty(WorldManagerPlugin.REGION_MEMBERSHIP);
			if (currentRegions == null) {
				if (newRegions == null) {
					return;
				}
				currentRegions = new LinkedList<Region>();
				obj.setProperty(WorldManagerPlugin.REGION_MEMBERSHIP, (Serializable) currentRegions);
			}
			final List<Region> left = new LinkedList<Region>();
			final ListIterator<Region> iter = currentRegions.listIterator();
			while (iter.hasNext()) {
				final Region currentRegion = iter.next();
				boolean inside = false;
				if (newRegions != null) {
					for (final Region newRegion : newRegions) {
						if (newRegion == currentRegion) {
							inside = true;
						}
					}
				}
				if (!inside) {
					left.add(currentRegion);
					iter.remove();
				}
			}
			this.handleLeaveRegion(obj, left);
			final List<Region> entered = new LinkedList<Region>();
			if (newRegions != null) {
				for (final Region newRegion2 : newRegions) {
					boolean existing = false;
					for (final Region currentRegion2 : currentRegions) {
						if (currentRegion2 == newRegion2) {
							existing = true;
						}
					}
					if (!existing) {
						entered.add(newRegion2);
					}
				}
				currentRegions.addAll(entered);
				this.handleEnterRegion(obj, entered);
			}
		}

		void handleLeaveRegion(final AOObject obj, final List<Region> left) {
			for (final Region region : left) {
				final String onLeave = (String) region.getProperty("onLeave");
				if (onLeave == null) {
					continue;
				}
				this.handleRegionChange(obj, region, "leave", onLeave);
			}
		}

		void handleEnterRegion(final AOObject obj, final List<Region> left) {
			for (final Region region : left) {
				final String onEnter = (String) region.getProperty("onEnter");
				if (onEnter == null) {
					continue;
				}
				this.handleRegionChange(obj, region, "enter", onEnter);
			}
		}

		void handleRegionChange(final AOObject obj, final Region region, final String action, final String triggerName) {
			if (Log.loggingDebug) {
				Log.debug("Custom RegionTrigger: " + obj + " regionName=" + region.getName() + " action=" + action + " trigger=" + triggerName);
			}
			final RegionTrigger trigger = WorldManagerPlugin.this.regionTriggers.get(triggerName);
			if (trigger == null) {
				Log.error("unknown RegionTrigger name=" + triggerName + ", object=" + obj + " region=" + region + " action=" + action);
				return;
			}
			try {
				if (action.equals("enter")) {
					trigger.enter(obj, region);
				} else if (action.equals("leave")) {
					trigger.leave(obj, region);
				}
			} catch (Exception e) {
				Log.exception("RegionTrigger exception trigger name=" + triggerName, e);
			}
		}

		void updateSoundRegion(final AOObject obj, final List<RegionConfig> soundConfig) {
			List<SoundData> curSound = (List<SoundData>) obj.getProperty(SoundManager.AMBIENTSOUND);
			if (curSound == null) {
				if (soundConfig == null) {
					return;
				}
				curSound = new LinkedList<SoundData>();
				obj.setProperty(SoundManager.AMBIENTSOUND, (Serializable) curSound);
			}
			final List<SoundData> turnOff = new LinkedList<SoundData>();
			final ListIterator<SoundData> iter = curSound.listIterator();
			while (iter.hasNext()) {
				final SoundData data = iter.next();
				final String fileName = data.getFileName();
				boolean on = false;
				if (soundConfig != null) {
					for (final RegionConfig config : soundConfig) {
						final SoundRegionConfig sConfig = (SoundRegionConfig) config;
						if (sConfig.containsSound(fileName)) {
							on = true;
							break;
						}
					}
				}
				if (!on) {
					turnOff.add(data);
					iter.remove();
				}
			}
			final List<SoundData> turnOn = new LinkedList<SoundData>();
			if (soundConfig != null) {
				for (final RegionConfig config2 : soundConfig) {
					final SoundRegionConfig sConfig2 = (SoundRegionConfig) config2;
					for (final SoundData data2 : sConfig2.getSoundData()) {
						boolean on2 = false;
						final String fileName2 = data2.getFileName();
						for (final SoundData curData : curSound) {
							if (curData.getFileName().equals(fileName2)) {
								on2 = true;
							}
						}
						if (!on2) {
							turnOn.add(data2);
						}
					}
				}
			}
			if (turnOn.size() == 0 && turnOff.size() == 0) {
				return;
			}
			final WorldManagerClient.SoundMessage soundMsg = new WorldManagerClient.SoundMessage(obj.getOid());
			soundMsg.setTarget(obj.getOid());
			soundMsg.setType((byte) 2);
			for (final SoundData data3 : turnOff) {
				soundMsg.removeSound(data3.getFileName());
			}
			final List<String> turnedOn = new LinkedList<String>();
			for (final SoundData data4 : turnOn) {
				if (!turnedOn.contains(data4.getFileName())) {
					soundMsg.addSound(data4);
					curSound.add(data4);
				}
			}
			if (curSound.size() == 0) {
				obj.setProperty(SoundManager.AMBIENTSOUND, null);
			}
			Engine.getAgent().sendBroadcast(soundMsg);
		}

		void updateRoadRegion(final AOObject obj, final RoadRegionConfig roadConfig) {
			final RoadRegionConfig curRoadRegion = (RoadRegionConfig) obj.getProperty(RoadRegionConfig.RegionType);
			if (roadConfig == null) {
				if (curRoadRegion != null) {
					obj.setProperty(RoadRegionConfig.RegionType, null);
					final WorldManagerClient.FreeRoadMessage freeRoadMsg = new WorldManagerClient.FreeRoadMessage(obj.getOid());
					Engine.getAgent().sendBroadcast(freeRoadMsg);
				}
				return;
			}
			if (curRoadRegion != null) {
				return;
			}
			final WorldManagerClient.RoadMessage roadMsg = new WorldManagerClient.RoadMessage(obj.getOid(), roadConfig.getRoads());
			Engine.getAgent().sendBroadcast(roadMsg);
			obj.setProperty(RoadRegionConfig.RegionType, roadConfig);
			WorldManagerPlugin.log.debug("updateRoadRegion: sent road region");
		}

		void updateDirLightRegion(final AOObject obj, final RegionConfig regionConfig) {
			final OID curLightOid = (OID) obj.getProperty(LightData.DirLightRegionType);
			final OID masterOid = obj.getMasterOid();
			if (regionConfig == null) {
				if (curLightOid != null) {
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("updateDirLightRegion: free light: " + curLightOid);
					}
					final Message freeMsg = new WorldManagerClient.FreeObjectMessage(masterOid, curLightOid);
					Engine.getAgent().sendBroadcast(freeMsg);
					obj.setProperty(LightData.DirLightRegionType, null);
				}
				return;
			}
			Light dirLight = null;
			this.dirLightLock.lock();
			try {
				dirLight = (Light) regionConfig.getProperty("spawnLight");
				if (dirLight == null) {
					final Quaternion orient = (Quaternion) regionConfig.getProperty("orient");
					final Color specular = (Color) regionConfig.getProperty("specular");
					final Color diffuse = (Color) regionConfig.getProperty("diffuse");
					final String name = (String) regionConfig.getProperty("name");
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("updateDirLightRegion: none found, creating spawned light, diffuse=" + diffuse + ", specular=" + specular + ", orient=" + orient);
					}
					dirLight = new Light();
					dirLight.setOid((OID) regionConfig.getProperty("_oid"));
					dirLight.setName("light_" + dirLight.getOid());
					final LightData lightData = new LightData();
					lightData.setName(name);
					lightData.setDiffuse(diffuse);
					lightData.setSpecular(specular);
					lightData.setAttenuationRange(100.0f);
					lightData.setAttenuationConstant(1.0f);
					lightData.setOrientation(orient);
					dirLight.setLightData(lightData);
					regionConfig.setProperty("spawnLight", dirLight);
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("updateDirLightRegion: spawned dir light=" + dirLight + ", ld=" + dirLight.getLightData());
					}
				}
			} finally {
				this.dirLightLock.unlock();
			}
			final OID regionLightOid = dirLight.getOid();
			if (curLightOid != null && curLightOid.equals(regionLightOid)) {
				return;
			}
			if (curLightOid != null) {
				if (Log.loggingDebug) {
					WorldManagerPlugin.log.debug("updateDirLightRegion: need to free existing light: " + curLightOid);
				}
				final Message freeMsg2 = new WorldManagerClient.FreeObjectMessage(masterOid, curLightOid);
				Engine.getAgent().sendBroadcast(freeMsg2);
			}
			if (curLightOid == null || !curLightOid.equals(regionLightOid)) {
				final LightData lightData2 = dirLight.getLightData();
				if (Log.loggingDebug) {
					WorldManagerPlugin.log.debug("updateDirLightRegion: sending over new light: " + regionLightOid + ", ld=" + lightData2);
				}
				final Message newDirLightMsg = new WorldManagerClient.NewDirLightMessage(masterOid, dirLight.getOid(), lightData2);
				Engine.getAgent().sendBroadcast(newDirLightMsg);
				if (Log.loggingDebug) {
					WorldManagerPlugin.log.debug("updateDirLightRegion: setting users new current light to " + regionLightOid);
				}
				obj.setProperty(LightData.DirLightRegionType, regionLightOid);
			}
		}

		void updateAmbientLightRegion(final AOObject obj, final RegionConfig ambientConfig) {
			final Color curAmbient = (Color) obj.getProperty(LightData.AmbientLightRegionType);
			if (ambientConfig == null) {
				if (curAmbient != null) {
					WorldManagerPlugin.log.debug("updateAmbientLightRegion: user had light, but region does not, sending black");
					obj.setProperty(LightData.AmbientLightRegionType, null);
					Engine.getAgent().sendBroadcast(new WorldManagerClient.SetAmbientLightMessage(obj.getMasterOid(), new Color(0, 0, 0)));
				}
			} else {
				final Color ambientColor = (Color) ambientConfig.getProperty("color");
				if (ambientColor == null) {
					WorldManagerPlugin.log.error("updateAmbientLight: ambient color not defined");
					return;
				}
				if (ambientColor.equals(curAmbient)) {
					return;
				}
				WorldManagerPlugin.log.debug("updateAmbientLightRegion: colors differ, updating client");
				obj.setProperty(LightData.AmbientLightRegionType, ambientColor);
				Engine.getAgent().sendBroadcast(new WorldManagerClient.SetAmbientLightMessage(obj.getMasterOid(), ambientColor));
			}
		}
	}

	class MobLoadHook implements LoadHook {
		@Override
		public void onLoad(final Entity entity) {
			WorldManagerPlugin.this.subscribeForMob(entity.getOid());
		}
	}

	class MobUnloadHook implements UnloadHook {
		@Override
		public void onUnload(final Entity entity) {
			if (!(entity instanceof AOObject)) {
				return;
			}
			WorldManagerPlugin.this.unsubscribeForMob(entity.getOid());
			WorldManagerPlugin.this.despawnObject((AOObject) entity);
			Engine.getPersistenceManager().clearDirty(entity);
			final AOObject aObj = (AOObject) entity;
			if (aObj.getType() == ObjectTypes.mob) {
				Log.debug("UNLOAD: not saving mob: " + aObj.getName());
				return;
			}
			entity.lock();
			byte[] entityData;
			try {
				entityData = entity.toBytes();
			} finally {
				entity.unlock();
			}
			Engine.getDatabase().saveObject(null, entityData, WorldManagerClient.NAMESPACE);
		}
	}

	class MobDeleteHook implements DeleteHook {
		@Override
		public void onDelete(final Entity entity) {
			if (!(entity instanceof AOObject)) {
				return;
			}
			WorldManagerPlugin.this.unsubscribeForMob(entity.getOid());
			WorldManagerPlugin.this.despawnObject((AOObject) entity);
			Engine.getPersistenceManager().clearDirty(entity);
		}

		@Override
		public void onDelete(final OID oid, final Namespace namespace) {
		}
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

	class MobPathReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.MobPathReqMessage pathReqMsg = (WorldManagerClient.MobPathReqMessage) msg;
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("Received MobPathReqMessage " + pathReqMsg);
			}
			final OID oid = pathReqMsg.getSubject();
			final AOObject obj = (AOObject) WorldManagerPlugin.this.getWorldManagerEntity(oid);
			if (obj == null) {
				Log.error("MobPathReqHook: unknown oid=" + oid);
				return true;
			}
			final InterpolatedWorldNode wnode = (InterpolatedWorldNode) obj.worldNode();
			final float speed = pathReqMsg.getSpeed();
			final boolean nomove = obj.getBooleanProperty("world.nomove");
			if (nomove) {
				final WorldManagerClient.MobPathCorrectionMessage correction = new WorldManagerClient.MobPathCorrectionMessage(oid, System.currentTimeMillis(), "linear", 0.0f, "",
						new LinkedList<Point>());
				Engine.getAgent().sendBroadcast(correction);
			} else {
				if (wnode.getPathInterpolator() != null) {
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("MobPathReqHook: calling getLoc on oid " + oid);
					}
					wnode.getLoc();
				}
				final long startTime = pathReqMsg.getStartTime();
				final String interpKind = pathReqMsg.getInterpKind();
				final String terrainString = pathReqMsg.getTerrainString();
				final List<Point> pathPoints = pathReqMsg.getPathPoints();
				final WorldManagerClient.MobPathMessage pathMsg = new WorldManagerClient.MobPathMessage(oid, startTime, interpKind, speed, terrainString, pathPoints);
				obj.setProperty(WorldManagerClient.MOB_PATH_PROPERTY, pathMsg);
				Engine.getAgent().sendBroadcast(pathMsg);
				if (Log.loggingDebug) {
					WorldManagerPlugin.log.debug("Sending MobPathMessage " + pathMsg);
				}
				if (speed != 0.0f) {
					final PathInterpolator pathInterpolator = interpKind.equalsIgnoreCase("spline") ? new PathSpline(oid, startTime, speed, terrainString, pathPoints) : new PathLinear(oid, startTime,
							speed, terrainString, pathPoints);
					wnode.setPathInterpolator(pathInterpolator);
				} else {
					wnode.setPathInterpolator(null);
				}
			}
			return true;
		}
	}

	class GetObjectsInHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.GetObjectsInMessage message = (WorldManagerClient.GetObjectsInMessage) msg;
			final OID subjectOid = message.getSubject();
			final BasicWorldNode subjectWorldNode = ((AOObject) WorldManagerPlugin.this.getWorldManagerEntity(subjectOid)).baseWorldNode();
			final OID instanceOid = subjectWorldNode.getInstanceOid();
			List<OID> objectsIn = null;
			if (instanceOid != null) {
				objectsIn = WorldManagerPlugin.this.getInstanceObjectsIn(instanceOid, message.getLoc(), message.getRadius(), message.getObjectType());
			} else {
				objectsIn = WorldManagerPlugin.this.getObjectsIn(message.getLoc(), message.getRadius(), message.getObjectType());
			}
			Engine.getAgent().sendObjectResponse(message, objectsIn);
			return true;
		}
	}

	class ObjectInfoReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ObjInfoReqMessage objInfoMsg = (WorldManagerClient.ObjInfoReqMessage) msg;
			final OID oid = objInfoMsg.getSubject();
			final WorldManagerClient.ObjectInfo objInfo = WorldManagerPlugin.this.makeObjectInfo(oid);
			final ResponseMessage respMsg = new WorldManagerClient.ObjInfoRespMessage(objInfoMsg, msg.getSenderName(), objInfo);
			Engine.getAgent().sendResponse(respMsg);
			return true;
		}
	}

	class DisplayContextReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.DisplayContextReqMessage rMsg = (WorldManagerClient.DisplayContextReqMessage) msg;
			final OID oid = rMsg.getSubject();
			final DisplayContext dc = WorldManagerPlugin.this.getDisplayContext(oid);
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("DisplayContextHook: oid=" + oid + ", dc=" + dc);
			}
			Engine.getAgent().sendObjectResponse(msg, dc);
			return true;
		}
	}

	class SpawnReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.SpawnReqMessage wrldMsg = (WorldManagerClient.SpawnReqMessage) msg;
			final OID oid = wrldMsg.getSubject();
			if (Log.loggingDebug) {
				Log.debug("SpawnReqHook: spawning oid=" + oid + ", msg=" + msg);
			}
			final Entity entity = WorldManagerPlugin.this.getWorldManagerEntity(oid);
			if (entity == null || !(entity instanceof AOObject)) {
				WorldManagerPlugin.log.error("SpawnReqHook: entity null or not found, oid=" + oid);
				Engine.getAgent().sendObjectResponse(msg, -1);
				return false;
			}
			final AOObject obj = (AOObject) entity;
			final WorldNode wnode = obj.worldNode();
			if (wnode.isSpawned()) {
				WorldManagerPlugin.log.error("SpawnReqHook: object already spawned oid=" + oid);
				Engine.getAgent().sendObjectResponse(msg, -3);
				return false;
			}
			final QuadTree<WMWorldNode> quadtree = WorldManagerPlugin.this.quadtrees.get(wnode.getInstanceOid());
			if (quadtree == null) {
				WorldManagerPlugin.log.error("SpawnReqHook: unknown instanceOid=" + wnode.getInstanceOid() + " oid=" + oid);
				Engine.getAgent().sendObjectResponse(msg, -2);
				return false;
			}
			if (wrldMsg.getPreMessage() != null) {
				Engine.getAgent().sendBroadcast(wrldMsg.getPreMessage());
			}
			Integer newsAndFreesCount = null;
			try {
				newsAndFreesCount = WorldManagerPlugin.this.spawnObject(obj, quadtree);
			} catch (Exception e) {
				Log.exception("spawnObject failed", e);
				Engine.getAgent().sendObjectResponse(msg, -1);
				return false;
			}
			if (wrldMsg.getPostMessage() != null) {
				Engine.getAgent().sendBroadcast(wrldMsg.getPostMessage());
			}
			final Message spawnedMsg = new WorldManagerClient.SpawnedMessage(oid, wnode.getInstanceOid(), obj.getType());
			Engine.getAgent().sendBroadcast(spawnedMsg);
			Engine.getAgent().sendObjectResponse(msg, newsAndFreesCount);
			if (obj.isUser()) {
				WorldManagerPlugin.this.sendRegionUpdate(obj);
			}
			return true;
		}
	}

	class DespawnReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.DespawnReqMessage wrldMsg = (WorldManagerClient.DespawnReqMessage) msg;
			final OID oid = wrldMsg.getSubject();
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("DespawnReqHook: oid=" + oid);
			}
			final Entity entity = WorldManagerPlugin.this.getWorldManagerEntity(oid);
			if (entity == null || !(entity instanceof AOObject)) {
				WorldManagerPlugin.log.error("DespawnReqHook: entity null or not found, oid=" + oid);
				Engine.getAgent().sendBooleanResponse(msg, Boolean.FALSE);
				return false;
			}
			if (wrldMsg.getPreMessage() != null) {
				Engine.getAgent().sendBroadcast(wrldMsg.getPreMessage());
			}
			final AOObject obj = (AOObject) entity;
			final WorldNode wnode = obj.worldNode();
			try {
				WorldManagerPlugin.this.despawnObject(obj);
			} catch (Exception e) {
				Log.exception("despawnObject failed", e);
				Engine.getAgent().sendBooleanResponse(msg, Boolean.FALSE);
				return false;
			}
			if (wrldMsg.getPostMessage() != null) {
				Engine.getAgent().sendBroadcast(wrldMsg.getPostMessage());
			}
			if (wnode != null) {
				final Message despawnedMsg = new WorldManagerClient.DespawnedMessage(oid, wnode.getInstanceOid(), obj.getType());
				Engine.getAgent().sendBroadcast(despawnedMsg);
			}
			Engine.getAgent().sendBooleanResponse(msg, Boolean.TRUE);
			return true;
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
	public SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {

		WorldManagerPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + " namespace=" + namespace + " template=" + template);

		Boolean persistent = (Boolean) template.get(Namespace.OBJECT_MANAGER, ":persistent");
		if (persistent == null) {
			persistent = false;
		}
		if (namespace == WorldManagerClient.INSTANCE_NAMESPACE) {
			return this.generateInstanceSubObject(masterOid, persistent);
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
		WorldManagerPlugin.this.subscribeForMob(masterOid);
		return new SubObjData();
	}
	public SubObjData generateInstanceSubObject(final OID masterOid, final Boolean persistent) {
		final WorldManagerInstance instance = WorldManagerPlugin.this.createInstanceEntity(masterOid);
		instance.setPersistenceFlag(persistent);
		if (persistent) {
			// Engine.getPersistenceManager().persistEntity(instance);
		}
		WorldManagerPlugin.this.subscribeForObject(masterOid);
		WorldManagerPlugin.this.hostInstance(masterOid, instance.getQuadTree().getLocalGeometry());
		return new SubObjData();
	}

	// class WorldManagerGenerateSubObjectHook extends GenerateSubObjectHook {
	// public WorldManagerGenerateSubObjectHook() {
	// super(WorldManagerPlugin.this);
	// }
	// @Override
	// public SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
	// if (Log.loggingDebug) {
	// WorldManagerPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + " namespace=" + namespace + " template=" + template);
	// }
	// Boolean persistent = (Boolean) template.get(Namespace.OBJECT_MANAGER, ":persistent");
	// if (persistent == null) {
	// persistent = false;
	// }
	// if (namespace == WorldManagerClient.INSTANCE_NAMESPACE) {
	// return this.generateInstanceSubObject(masterOid, persistent);
	// }
	// final Map<String, Serializable> props = template.getSubMap(Namespace.WORLD_MANAGER);
	// if (props == null) {
	// Log.warn("GenerateSubObjectHook: no props in ns " + Namespace.WORLD_MANAGER);
	// return null;
	// }
	// final OID instanceOid = (OID) props.get(WorldManagerClient.TEMPL_INSTANCE);
	// if (instanceOid == null) {
	// Log.error("GenerateSubObjectHook: missing instanceOid");
	// return null;
	// }
	// if (WorldManagerPlugin.this.quadtrees.get(instanceOid) == null) {
	// Log.error("GenerateSubObjectHook: unknown instanceOid=" + instanceOid);
	// return null;
	// }
	// final Point loc = (Point) props.get(WorldManagerClient.TEMPL_LOC);
	// if (loc == null) {
	// Log.warn("GenerateSubObjectHook: no loc in templ");
	// return null;
	// }
	// String objName = (String) props.get(WorldManagerClient.TEMPL_NAME);
	// if (objName == null) {
	// objName = template.getName();
	// if (objName == null) {
	// objName = "(null)";
	// }
	// }
	// Quaternion orient = (Quaternion) props.get(WorldManagerClient.TEMPL_ORIENT);
	// if (orient == null) {
	// orient = new Quaternion();
	// }
	// AOVector scale = (AOVector) props.get(WorldManagerClient.TEMPL_SCALE);
	// if (scale == null) {
	// scale = new AOVector(1.0f, 1.0f, 1.0f);
	// }
	// final Integer perceptionRadius = (Integer) props.get(WorldManagerClient.TEMPL_PERCEPTION_RADIUS);
	// final AOObject wObj = WorldManagerPlugin.this.generateWorldManagerSubObject(template, masterOid);
	// wObj.setName(objName);
	// wObj.scale(scale);
	// DisplayContext dc = (DisplayContext) props.get(WorldManagerClient.TEMPL_DISPLAY_CONTEXT);
	// if (dc != null) {
	// dc = (DisplayContext) dc.clone();
	// dc.setObjRef(wObj.getOid());
	// wObj.displayContext(dc);
	// } else {
	// Log.debug("GenerateSubObjectHook: object has no display context, oid=" + masterOid);
	// }
	// if (Log.loggingDebug) {
	// WorldManagerPlugin.log.debug("GenerateSubObjectHook: created entity " + wObj + ", loc=" + loc);
	// }
	// WMWorldNode wnode;
	// if (perceptionRadius != null) {
	// wnode = new WMWorldNode(perceptionRadius);
	// } else {
	// wnode = new WMWorldNode();
	// }
	// wnode.setInstanceOid(instanceOid);
	// wnode.setLoc(loc);
	// wnode.setOrientation(orient);
	// Boolean followsTerrain = (Boolean) props.get(WorldManagerClient.TEMPL_FOLLOWS_TERRAIN);
	// if (followsTerrain == null) {
	// followsTerrain = Boolean.TRUE;
	// }
	// wnode.setFollowsTerrain(followsTerrain);
	// wObj.worldNode(wnode);
	// wnode.setObject(wObj);
	// wObj.setPersistenceFlag(persistent);
	// EntityManager.registerEntityByNamespace(wObj, Namespace.WORLD_MANAGER);// ע��aoobject
	// if (persistent) {
	// Engine.getPersistenceManager().persistEntity(wObj);
	// }
	// WorldManagerPlugin.this.subscribeForMob(masterOid);
	// return new SubObjData();
	// }
	//
	// public SubObjData generateInstanceSubObject(final OID masterOid, final Boolean persistent) {
	// final WorldManagerInstance instance = WorldManagerPlugin.this.createInstanceEntity(masterOid);
	// instance.setPersistenceFlag(persistent);
	// if (persistent) {
	// Engine.getPersistenceManager().persistEntity(instance);
	// }
	// WorldManagerPlugin.this.subscribeForObject(masterOid);
	// WorldManagerPlugin.this.hostInstance(masterOid, instance.getQuadTree().getLocalGeometry());
	// return new SubObjData();
	// }
	// }

	class InstanceLoadHook implements LoadHook {
		@Override
		public void onLoad(final Entity entity) {
			final WorldManagerInstance instance = (WorldManagerInstance) entity;
			WorldManagerPlugin.this.initializeInstance(instance);
			WorldManagerPlugin.this.subscribeForObject(entity.getOid());
		}
	}

	class InstanceUnloadHook implements UnloadHook {
		@Override
		public void onUnload(final Entity entity) {
			WorldManagerPlugin.this.quadtrees.remove(entity.getOid());
			WorldManagerPlugin.this.unsubscribeForObject(entity.getOid());
			WorldManagerPlugin.this.unhostInstance(entity.getOid());
		}
	}

	class InstanceDeleteHook implements DeleteHook {
		@Override
		public void onDelete(final Entity entity) {
			WorldManagerPlugin.this.quadtrees.remove(entity.getOid());
			WorldManagerPlugin.this.unsubscribeForObject(entity.getOid());
			WorldManagerPlugin.this.unhostInstance(entity.getOid());
		}

		@Override
		public void onDelete(final OID oid, final Namespace namespace) {
		}
	}

	class WorldManagerTransferHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final TransferObjectMessage transferMsg = (TransferObjectMessage) msg;
			final AOObject obj = (AOObject) transferMsg.getEntity();
			if (Log.loggingDebug) {
				Log.debug("WorldManagerTransferHook: obj=" + obj + ", sessionid=" + msg.getSenderName());
			}
			final WMWorldNode wnode = new WMWorldNode();
			wnode.setInstanceOid(obj.worldNode().getInstanceOid());
			wnode.setLoc(obj.getLoc());
			wnode.setOrientation(obj.getOrientation());
			wnode.setFollowsTerrain(Boolean.TRUE);
			obj.worldNode(wnode);
			wnode.setObject(obj);
			final QuadTree<WMWorldNode> quadtree = WorldManagerPlugin.this.quadtrees.get(wnode.getInstanceOid());
			if (quadtree == null) {
				WorldManagerPlugin.log.error("WorldManagerTransferHook: unknown instanceOid=" + wnode.getInstanceOid() + " oid=" + obj.getOid());
				Engine.getAgent().sendBooleanResponse(msg, Boolean.FALSE);
				return false;
			}
			final OID masterOid = obj.getMasterOid();
			WorldManagerPlugin.this.registerWorldManagerEntity(obj);
			WorldManagerPlugin.this.subscribeForMob(masterOid);
			if (Log.loggingDebug) {
				Log.debug("WorldManagerTransferHook: bound obj " + obj);
			}
			WorldManagerPlugin.this.spawnObject(obj, quadtree);
			if (Log.loggingDebug) {
				Log.debug("WorldManagerTransferHook: complete, spawned obj " + obj);
			}
			Engine.getAgent().sendBooleanResponse(msg, Boolean.TRUE);
			return true;
		}
	}

	class UpdateObjectHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.UpdateMessage updateReq = (WorldManagerClient.UpdateMessage) msg;
			final OID notifyOid = updateReq.getTarget();
			final OID updateOid = updateReq.getSubject();
			if (Log.loggingDebug) {
				Log.debug("UpdateObjectHook: notifyOid=" + notifyOid + " updateOid=" + updateOid);
			}
			final Entity updateEntity = WorldManagerPlugin.this.getWorldManagerEntity(updateOid);
			if (updateEntity == null) {
				WorldManagerPlugin.log.warn("UpdateObjectHook: could not find sub object for oid=" + updateOid);
				return false;
			}
			if (!(updateEntity instanceof AOObject)) {
				WorldManagerPlugin.log.warn("UpdateObjectHook: updateObj is not AOObject: " + updateOid);
				return false;
			}
			final AOObject updateObj = (AOObject) updateEntity;
			if (updateObj.worldNode() == null) {
				WorldManagerPlugin.log.warn("UpdateObjectHook: updateObj has no world node: " + updateOid);
				return false;
			}
			WorldManagerPlugin.this.sendTargetedPropertyMessage(notifyOid, updateObj);
			WorldManagerPlugin.this.sendWNodeMessage(notifyOid, updateObj);
			final List<SoundData> soundData = (List<SoundData>) updateEntity.getProperty(WorldManagerClient.TEMPL_SOUND_DATA_LIST);
			if (soundData != null) {
				WorldManagerPlugin.this.sendObjectSoundMessage(notifyOid, updateObj, soundData);
			}
			return true;
		}
	}

	class GetWNodeReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final SubjectMessage getWNodeMsg = (SubjectMessage) msg;
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("GetWNodeReqHook: got get wnode msg=" + getWNodeMsg);
			}
			final OID oid = getWNodeMsg.getSubject();
			final AOObject obj = (AOObject) WorldManagerPlugin.this.getWorldManagerEntity(oid);
			if (obj == null) {
				Log.error("GetWNodeReqHook: could not find obj for oid=" + oid);
				Engine.getAgent().sendObjectResponse(msg, null);
				return true;
			}
			final BasicWorldNode newWNode = obj.baseWorldNode();
			Engine.getAgent().sendObjectResponse(msg, newWNode);
			return true;
		}
	}

	class ComReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ComReqMessage comReqMsg = (WorldManagerClient.ComReqMessage) msg;
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("ComReqHook: got com msg from " + comReqMsg.getSubject() + ", msg=" + comReqMsg.getString());
			}
			final WorldManagerClient.ComMessage comMsg = new WorldManagerClient.ComMessage(comReqMsg.getSubject(), comReqMsg.getChatterName(), comReqMsg.getChannel(), comReqMsg.getString());
			Engine.getAgent().sendBroadcast(comMsg);
			return true;
		}
	}

	class OrientReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.OrientReqMessage orientReqMsg = (WorldManagerClient.OrientReqMessage) msg;
			final OID oid = orientReqMsg.getSubject();
			final Quaternion q = orientReqMsg.getQuaternion();
			final Entity entity = WorldManagerPlugin.this.getWorldManagerEntity(oid);
			if (entity == null) {
				WorldManagerPlugin.log.error("OrientReqHook: could not find sub object for oid=" + oid);
				return true;
			}
			final AOObject obj = (AOObject) entity;
			final WorldNode wnode = obj.worldNode();
			final InterpolatedWorldNode bnode = (InterpolatedWorldNode) wnode;
			bnode.setOrientation(q);
			final WorldManagerClient.OrientMessage orientMsg = new WorldManagerClient.OrientMessage(oid, q);
			Engine.getAgent().sendBroadcast(orientMsg);
			return true;
		}
	}

	class RefreshWNodeHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.RefreshWNodeMessage rMsg = (WorldManagerClient.RefreshWNodeMessage) msg;
			final OID oid = rMsg.getSubject();
			final Entity entity = WorldManagerPlugin.this.getWorldManagerEntity(oid);
			if (entity == null) {
				WorldManagerPlugin.log.error("RefreshWNodeHook: could not find sub object for oid=" + oid);
				return true;
			}
			final AOObject obj = (AOObject) entity;
			final BasicWorldNode copyNode = obj.baseWorldNode();
			final WorldManagerClient.UpdateWorldNodeMessage uMsg = new WorldManagerClient.UpdateWorldNodeMessage(oid, copyNode);
			Engine.getAgent().sendBroadcast(uMsg);
			return true;
		}
	}

	class PerceiverRegionsHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.PerceiverRegionsMessage pMsg = (WorldManagerClient.PerceiverRegionsMessage) msg;
			final String otherSessionId = pMsg.getSenderName();
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("PerceiverRegionsHook: otherSessionId=" + otherSessionId);
			}
			if (otherSessionId == null) {
				throw new AORuntimeException("other session id is null");
			}
			final String mySessionId = Engine.getAgent().getName();
			if (mySessionId.equals(otherSessionId)) {
				Log.debug("PerceiverRegionsHook: ignoring, session id is same as self");
				return false;
			}
			final OID instanceOid = pMsg.getInstanceOid();
			final QuadTree<WMWorldNode> quadtree = WorldManagerPlugin.this.quadtrees.get(instanceOid);
			if (quadtree == null) {
				Log.error("PerceiverRegionsHook: unknown instanceOid=" + instanceOid);
				return false;
			}
			final Geometry region = pMsg.getRegion();
			if (region == null) {
				throw new AORuntimeException("region is null");
			}
			if (WorldManagerPlugin.this.fixedPerceiverMap.getPerceiver(otherSessionId) != null) {
				WorldManagerPlugin.log.warn("PerceiverRegionsHook: map exists");
				return true;
			}
			this.setupPerceiver(otherSessionId, quadtree, region);
			if (pMsg.getTargetSessionId() == null) {
				WorldManagerPlugin.log.debug("PerceiverRegionsHook: sending our own geo msg out");
				final Geometry myFixedPerceiverRegions = Geometry.maxGeometry();
				WorldManagerClient.sendPerceiverRegionsMsg(instanceOid, myFixedPerceiverRegions, otherSessionId);
			} else {
				WorldManagerPlugin.log.debug("PerceiverRegionsHook: PerceiverRegionsMsg has a target - so not sending my own out");
			}
			return true;
		}

		private void setupPerceiver(final String remoteSessionId, final QuadTree<WMWorldNode> quadtree, final Geometry region) {
			final FixedPerceiver<WMWorldNode> p = new FixedPerceiver<WMWorldNode>(region);
			p.registerCallback(WorldManagerPlugin.this);
			p.setFilter(new PerceiverFilter<WMWorldNode>() {
				static final long serialVersionUID = 1L;

				@Override
				public boolean matches(final Perceiver<WMWorldNode> p, final WMWorldNode node) {
					final boolean val = node.isLocal();
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("PerceiverFilter: node local? " + val);
					}
					return val;
				}
			});
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("setupPerceiver: adding remote session " + remoteSessionId + " into our map");
			}
			WorldManagerPlugin.this.fixedPerceiverMap.register(remoteSessionId, p);
			WorldManagerPlugin.log.debug("setupPerceiver: adding fixed perceiver to quad tree");
			quadtree.addFixedPerceiver(p);
			WorldManagerPlugin.log.debug("setupPerceiver: done adding fixed perceiver");
		}
	}

	static class FixedPerceiverMap {
		protected Lock lock;
		Map<String, FixedPerceiver<WMWorldNode>> sessionPerceiverMap;
		Map<FixedPerceiver<WMWorldNode>, String> perceiverSessionMap;

		public FixedPerceiverMap() {
			this.lock = LockFactory.makeLock("FixedPerceiverLock");
			this.sessionPerceiverMap = new HashMap<String, FixedPerceiver<WMWorldNode>>();
			this.perceiverSessionMap = new HashMap<FixedPerceiver<WMWorldNode>, String>();
		}

		public void register(final String remoteSessionId, final FixedPerceiver<WMWorldNode> p) {
			this.lock.lock();
			try {
				this.sessionPerceiverMap.put(remoteSessionId, p);
				this.perceiverSessionMap.put(p, remoteSessionId);
			} finally {
				this.lock.unlock();
			}
		}

		public String getSessionId(final FixedPerceiver<WMWorldNode> p) {
			this.lock.lock();
			try {
				return this.perceiverSessionMap.get(p);
			} finally {
				this.lock.unlock();
			}
		}

		public FixedPerceiver<WMWorldNode> getPerceiver(final String sessionId) {
			this.lock.lock();
			try {
				return this.sessionPerceiverMap.get(sessionId);
			} finally {
				this.lock.unlock();
			}
		}
	}

	class NewRemoteObjHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.NewRemoteObjectMessage rMsg = (WorldManagerClient.NewRemoteObjectMessage) msg;
			final String targetSessionId = rMsg.getTargetSessionId();
			if (!targetSessionId.equals(Engine.getAgent().getName())) {
				throw new RuntimeException("session ids dont match");
			}
			final OID newObjOid = rMsg.getSubject();
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("NewRemoteObjHook: oid=" + newObjOid);
			}
			if (newObjOid == null) {
				throw new RuntimeException("no remote newobjoid");
			}
			final OID instanceOid = rMsg.getInstanceOid();
			final Point loc = rMsg.getLoc();
			final Quaternion orient = rMsg.getOrient();
			final int radius = rMsg.getPerceptionRadius();
			final QuadTree<WMWorldNode> quadtree = WorldManagerPlugin.this.quadtrees.get(instanceOid);
			if (quadtree == null) {
				WorldManagerPlugin.log.error("NewRemoteObjHook: unknown instanceOid=" + instanceOid + " oid=" + newObjOid);
				return false;
			}
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("NewRemoteObjHook: creating world node for oid " + newObjOid);
			}
			final WMWorldNode node = new WMWorldNode(radius);
			node.isLocal(false);
			node.setInstanceOid(rMsg.getInstanceOid());
			node.setLoc(loc);
			node.setOrientation(orient);
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("NewRemoteObjHook: created world node for oid " + newObjOid + ", wnode=" + node);
			}
			final AOObject newObj = new AOObject(newObjOid);
			newObj.setType(rMsg.getType());
			newObj.worldNode(node);
			node.setObject(newObj);
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("NewRemoteObjHook: created obj for oid " + newObjOid + ", obj=" + newObj);
			}
			WorldManagerPlugin.this.registerWorldManagerEntity(newObj);
			Log.debug("NewRemoteObjHook: placing obj into qtree, obj=" + newObj);
			quadtree.addElement(node);
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("NewRemoteObjHook: placed obj into qtree, obj=" + newObj);
			}
			final HashSet<MessageType> types = new HashSet<MessageType>();
			types.add(WorldManagerClient.MSG_TYPE_UPDATEWNODE);
			types.add(WorldManagerClient.MSG_TYPE_MOB_PATH);
			final SubjectFilter newSubFilter = new SubjectFilter(types, newObjOid);
			final Long newSub = Engine.getAgent().createSubscription(newSubFilter, WorldManagerPlugin.this);
			WorldManagerPlugin.this.remoteMobSubscription.put(newObjOid, newSub);
			return true;
		}
	}

	class FreeRemoteObjHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.FreeRemoteObjectMessage rMsg = (WorldManagerClient.FreeRemoteObjectMessage) msg;
			final String targetSessionId = rMsg.getTargetSessionId();
			if (!targetSessionId.equals(Engine.getAgent().getName())) {
				throw new RuntimeException("session ids dont match");
			}
			final OID freeObjOid = rMsg.getSubject();
			if (freeObjOid == null) {
				throw new RuntimeException("no remote objoid");
			}
			final OID instanceOid = rMsg.getInstanceOid();
			final QuadTree<WMWorldNode> quadtree = WorldManagerPlugin.this.quadtrees.get(instanceOid);
			if (quadtree == null) {
				WorldManagerPlugin.log.error("FreeRemoteObjHook: unknown instanceOid=" + instanceOid + " oid=" + freeObjOid);
				return false;
			}
			final Entity entity = WorldManagerPlugin.this.getWorldManagerEntity(freeObjOid);
			if (entity == null) {
				throw new RuntimeException("could not find entity " + freeObjOid);
			}
			final AOObject obj = (AOObject) entity;
			final WMWorldNode wnode = (WMWorldNode) obj.worldNode();
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("FreeRemoteObjHook: removing obj " + obj + ", unsubscribing");
			}
			final Long removeSub = WorldManagerPlugin.this.remoteMobSubscription.removeSub(freeObjOid);
			if (removeSub == null) {
				throw new RuntimeException("no existing remote sub");
			}
			Engine.getAgent().removeSubscription(removeSub);
			WorldManagerPlugin.log.debug("FreeRemoteObjHook: unsubscribed");
			quadtree.removeElement(wnode);
			WorldManagerPlugin.this.removeWorldManagerEntity(freeObjOid);
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("FreeRemoteObjHook: removed obj " + freeObjOid);
			}
			return true;
		}
	}

	class UpdateWNodeHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.UpdateWorldNodeMessage uMsg = (WorldManagerClient.UpdateWorldNodeMessage) msg;
			final OID objOid = uMsg.getSubject();
			final BasicWorldNode inNode = uMsg.getWorldNode();
			if (!(inNode instanceof BasicWorldNode)) {
				throw new RuntimeException("inWorldNode not BasicWorldNode");
			}
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("UpdateWNodeHook: inNode=" + inNode);
			}
			final Entity entity = WorldManagerPlugin.this.getWorldManagerEntity(objOid);
			if (entity == null) {
				Log.warn("UpdateWNodeHook: entity not found oid=" + objOid);
				return true;
			}
			if (!(entity instanceof AOObject)) {
				throw new RuntimeException("not aoobject");
			}
			final AOObject obj = (AOObject) entity;
			final WorldNode node = obj.worldNode();
			if (!(node instanceof WMWorldNode)) {
				throw new RuntimeException("not a wmwnode");
			}
			final WMWorldNode wnode = (WMWorldNode) node;
			wnode.setLoc(inNode.getLoc());
			wnode.setOrientation(inNode.getOrientation());
			wnode.setDir(inNode.getDir());
			return true;
		}
	}

	class HostInstanceHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.HostInstanceMessage message = (WorldManagerClient.HostInstanceMessage) msg;
			final Geometry localGeo = Geometry.maxGeometry();
			WorldManagerPlugin.this.hostInstance(message.getInstanceOid(), localGeo);
			Engine.getAgent().sendBooleanResponse(message, true);
			return true;
		}
	}

	class NewRegionHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.NewRegionMessage rMsg = (WorldManagerClient.NewRegionMessage) msg;
			final Region region = rMsg.getRegion();
			final Boundary b = region.getBoundary();
			final OID instanceOid = rMsg.getInstanceOid();
			final QuadTree<WMWorldNode> quadtree = WorldManagerPlugin.this.quadtrees.get(instanceOid);
			if (quadtree == null) {
				WorldManagerPlugin.log.error("NewRegionHook: unknown instanceOid=" + instanceOid + " boundary=" + b + " region=" + region);
				return false;
			}
			final RegionConfig dirLightConfig = region.getConfig(LightData.DirLightRegionType);
			if (dirLightConfig != null) {
				dirLightConfig.setProperty("_oid", Engine.getOIDManager().getNextOid());
			}
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("NewRegionHook: boundary=" + b + " region=" + region + " instanceOid=" + instanceOid);
			}
			quadtree.addRegion(region);
			return true;
		}
	}

	class ModifyDisplayContextHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ModifyDisplayContextMessage dMsg = (WorldManagerClient.ModifyDisplayContextMessage) msg;
			final OID oid = dMsg.getSubject();
			final byte action = dMsg.getAction();
			final String base = dMsg.getBase();
			final List<DisplayContext.Submesh> submeshes = dMsg.getSubmeshes();
			final String handle = dMsg.getChildDCHandle();
			final DisplayContext childDC = dMsg.getChildDC();
			final AOObject obj = (AOObject) WorldManagerPlugin.this.getWorldManagerEntity(oid);
			if (obj == null) {
				Log.warn("ModifyDisplayContextHook: no obj: " + oid);
				return false;
			}
			obj.getLock().lock();
			try {
				if (action == 1) {
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("ModifyDisplayContextHook: obj " + oid + ", action=REPLACE, submeshes.size() " + submeshes.size());
						if (submeshes.size() > 0) {
							WorldManagerPlugin.log.debug("ModifyDisplayContextHook: first submesh " + submeshes.get(0));
						}
					}
					obj.displayContext(new DisplayContext(oid, base));
					obj.displayContext().addSubmeshes(submeshes);
					WorldManagerPlugin.this.sendDCMessage(obj);
				} else if (action == 2) {
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("ModifyDisplayContextHook: obj " + oid + ", action=ADD");
					}
					obj.displayContext().addSubmeshes(submeshes);
					WorldManagerPlugin.this.sendDCMessage(obj);
				} else if (action == 3) {
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("ModifyDisplayContextHook: obj " + oid + ", action=ADD_CHILD");
					}
					if (handle == null) {
						throw new AORuntimeException("ModifyDisplayContextHook: obj=" + oid + ", handle is null");
					}
					obj.displayContext().addChildDC(handle, childDC);
					WorldManagerPlugin.this.sendDCMessage(obj);
				} else if (action == 5) {
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("ModifyDisplayContextHook: obj " + oid + ", action=REMOVE_CHILD");
					}
					if (handle == null) {
						throw new AORuntimeException("ModifyDisplayContextHook: obj=" + oid + ", handle is null");
					}
					final DisplayContext rv = obj.displayContext().removeChildDC(handle);
					if (rv == null) {
						Log.error("ModifyDisplayContextHook: obj=" + oid + " did not find child to remove");
						return false;
					}
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("ModifyDisplayContextHook: sending out detach msg for oid " + oid + ", dcObjRef=" + childDC.getObjRef() + ", socket=" + handle);
					}
					if (childDC.getObjRef() == null) {
						Log.error("ModifyDisplayContextHook: remove child dc, obj ref is null");
						return false;
					}
					final WorldManagerClient.DetachMessage detachMsg = new WorldManagerClient.DetachMessage(oid, childDC.getObjRef(), handle);
					Engine.getAgent().sendBroadcast(detachMsg);
				} else {
					if (action != 4) {
						throw new AORuntimeException("unknown action type");
					}
					if (Log.loggingDebug) {
						WorldManagerPlugin.log.debug("ModifyDisplayContextHook: obj " + oid + ", action=REMOVE");
					}
					obj.displayContext().removeSubmeshes(submeshes);
					WorldManagerPlugin.this.sendDCMessage(obj);
				}
			} finally {
				obj.getLock().unlock();
			}
			Engine.getPersistenceManager().setDirty(obj);
			return true;
		}
	}

	class GetPluginStatusHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final LinkedHashMap<String, Serializable> status = new LinkedHashMap<String, Serializable>();
			status.put("plugin", WorldManagerPlugin.this.getName());
			status.put("entity", EntityManager.getEntityCount());
			status.put("instance", WorldManagerPlugin.this.quadtrees.size());
			Engine.getAgent().sendObjectResponse(msg, status);
			return true;
		}
	}

	class PlayerPathWMReqHook implements Hook {
		@Override
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.PlayerPathWMReqMessage reqMsg = (WorldManagerClient.PlayerPathWMReqMessage) msg;
			final OID playerOid = reqMsg.getPlayerOid();
			final String roomId = reqMsg.getRoomId();
			PathObject pathObject = WorldManagerPlugin.pathObjectCache.getPathObject(roomId);
			final List<AOVector> boundary = reqMsg.getBoundary();
			final List<List<AOVector>> obstacles = reqMsg.getObstacles();
			final float avatarWidth = reqMsg.getAvatarWidth();
			if (Log.loggingDebug) {
				WorldManagerPlugin.log.debug("PlayerPathReqWMHook.processMessage: Received a PLAYER_PATH_REQ message for player " + playerOid + " and roomId " + roomId + " with boundary " + boundary
						+ " and obstacles " + obstacles);
			}
			if (boundary == null != (obstacles == null)) {
				WorldManagerPlugin.log.error("PlayerPathReqWMHook.processMessage: For player " + playerOid + ", received a PLAYER_PATH_REQ message for roomId " + roomId + ", but boundary is "
						+ boundary + " but obstacles is " + obstacles);
				return false;
			}
			if (pathObject == null && boundary == null) {
				WorldManagerPlugin.log.error("PlayerPathReqWMHook.processMessage: For player " + playerOid + ", received a PLAYER_PATH_REQ message for roomId " + roomId
						+ ", but didn't find roomId in the cache and no no boundary or obstacles were supplied.");
				return false;
			}
			if (boundary != null) {
				pathObject = new PathObject("Player " + playerOid + ", roomId " + roomId, avatarWidth, boundary, obstacles);
				WorldManagerPlugin.pathObjectCache.setPathObject(roomId, pathObject);
			}
			final PathFinderValue value = PathSearcher.findPath(playerOid, pathObject, reqMsg.getStart(), reqMsg.getDest(), avatarWidth / 2.0f);
			if (value == null) {
				WorldManagerPlugin.log.error("PlayerPathReqWMHook.processMessage: For player " + playerOid + ", roomId " + roomId + ", start " + reqMsg.getStart() + ", dest " + reqMsg.getDest()
						+ ", cound not generate path!");
			} else {
				final List<Point> pathPoints = new LinkedList<Point>();
				for (final AOVector p : value.getPath()) {
					pathPoints.add(new Point(p));
				}
				final WorldManagerClient.MobPathMessage mobPathMsg = new WorldManagerClient.MobPathMessage(playerOid, System.currentTimeMillis(), "spline", reqMsg.getSpeed(),
						value.getTerrainString(), pathPoints);
				Engine.getAgent().sendBroadcast(mobPathMsg);
			}
			return true;
		}
	}

	public static class WorldManagerFilter extends NamespaceFilter {
		public static final int FIELD_INSTANCES = 1;
		private String pluginName;
		private Map<OID, List<Geometry>> instanceGeometry;

		public WorldManagerFilter() {
			this.instanceGeometry = new HashMap<OID, List<Geometry>>();
		}

		public WorldManagerFilter(final String pluginName) {
			this.instanceGeometry = new HashMap<OID, List<Geometry>>();
			this.setPluginName(pluginName);
		}

		public String getPluginName() {
			return this.pluginName;
		}

		public void setPluginName(final String pluginName) {
			this.pluginName = pluginName;
		}

		public void addInstance(final OID instanceOid, final Geometry geometry) {
			final List<Geometry> geoList = new ArrayList<Geometry>();
			geoList.add(geometry);
			this.instanceGeometry.put(instanceOid, geoList);
		}

		public void removeInstance(final OID instanceOid) {
			this.instanceGeometry.remove(instanceOid);
		}

		public List<Geometry> getInstance(final OID instanceOid) {
			return this.instanceGeometry.get(instanceOid);
		}

		@Override
		public synchronized boolean matchRemaining(final Message message) {
			OID instanceOid = null;
			Point location = null;
			final MessageType type = message.getMsgType();
			Namespace namespace = null;
			if (!super.matchRemaining(message) && type != WorldManagerClient.MSG_TYPE_NEW_REGION && type != WorldManagerClient.MSG_TYPE_PLAYER_PATH_WM_REQ) {
				return false;
			}
			if (type == ObjectManagerClient.MSG_TYPE_GENERATE_SUB_OBJECT && message instanceof ObjectManagerClient.GenerateSubObjectMessage) {
				final ObjectManagerClient.GenerateSubObjectMessage genMsg = (ObjectManagerClient.GenerateSubObjectMessage) message;
				final Template template = genMsg.getTemplate();
				final String targetPlugin = (String) template.get(Namespace.WM_INSTANCE, ":wmName");
				if (targetPlugin != null) {
					return targetPlugin.equals(this.pluginName);
				}
				location = (Point) template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC);
				if (location == null) {
					Log.error("WorldManagerFilter: generate msg has null loc, oid=" + genMsg.getSubject());
					return false;
				}
				instanceOid = (OID) template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE);
				if (instanceOid == null) {
					Log.error("WorldManagerFilter: generate msg has null instanceOid, oid=" + genMsg.getSubject());
					return false;
				}
			} else if (type == ObjectManagerClient.MSG_TYPE_LOAD_SUBOBJECT) {
				if (message instanceof WorldManagerClient.LoadSubObjectMessage) {
					final WorldManagerClient.LoadSubObjectMessage loadMsg = (WorldManagerClient.LoadSubObjectMessage) message;
					instanceOid = loadMsg.getInstanceOid();
					location = loadMsg.getLocation();
				} else if (message instanceof ObjectManagerClient.LoadSubObjectMessage) {
					final ObjectManagerClient.LoadSubObjectMessage loadMsg2 = (ObjectManagerClient.LoadSubObjectMessage) message;
					instanceOid = loadMsg2.getSubject();
					namespace = loadMsg2.getNamespace();
				}
			} else {
				if (type == WorldManagerClient.MSG_TYPE_NEW_REGION) {
					final WorldManagerClient.NewRegionMessage regionMsg = (WorldManagerClient.NewRegionMessage) message;
					instanceOid = regionMsg.getInstanceOid();
					final List<Geometry> localGeometry = this.instanceGeometry.get(instanceOid);
					return localGeometry != null;
				}
				if (type == WorldManagerClient.MSG_TYPE_PLAYER_PATH_WM_REQ) {
					final WorldManagerClient.PlayerPathWMReqMessage reqMsg = (WorldManagerClient.PlayerPathWMReqMessage) message;
					instanceOid = reqMsg.getInstanceOid();
				}
			}
			if (instanceOid == null) {
				return false;
			}
			final List<Geometry> localGeometry2 = this.instanceGeometry.get(instanceOid);
			if (localGeometry2 == null) {
				return false;
			}
			if (namespace == Namespace.WM_INSTANCE && location == null) {
				return true;
			}
			for (final Geometry geometry : localGeometry2) {
				if (geometry.contains(location)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public synchronized boolean applyFilterUpdate(final FilterUpdate update) {
			final List<FilterUpdate.Instruction> instructions = update.getInstructions();
			for (final FilterUpdate.Instruction instruction : instructions) {
				switch (instruction.opCode) {
					case 2 : {
						if (instruction.fieldId == 1) {
							final InstanceGeometry instanceGeo = (InstanceGeometry) instruction.value;
							if (Log.loggingDebug) {
								Log.debug("WorldManagerFilter ADD INSTANCE " + instruction.value);
							}
							this.instanceGeometry.put(instanceGeo.instanceOid, instanceGeo.geometry);
							continue;
						}
						Log.error("WorldManagerFilter: invalid fieldId " + instruction.fieldId);
						continue;
					}
					case 3 : {
						if (instruction.fieldId == 1) {
							if (Log.loggingDebug) {
								Log.debug("WorldManagerFilter REMOVE INSTANCE " + instruction.value);
							}
							this.instanceGeometry.remove(instruction.value);
							continue;
						}
						Log.error("WorldManagerFilter: invalid fieldId " + instruction.fieldId);
						continue;
					}
					case 1 : {
						Log.error("WorldManagerFilter: OP_SET is not supported");
						continue;
					}
					default : {
						Log.error("WorldManagerFilter: invalid opCode " + instruction.opCode);
						continue;
					}
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "[WorldManagerFilter " + this.toStringInternal() + "]";
		}

		@Override
		protected String toStringInternal() {
			return super.toStringInternal() + " pluginName=" + this.pluginName + " instances=" + this.instanceGeometry.size();
		}

		public static class InstanceGeometry {
			OID instanceOid;
			List<Geometry> geometry;
		}
	}

	public static class HostInstanceFilter extends MessageTypeFilter {
		private String pluginName;

		public HostInstanceFilter() {
		}

		public HostInstanceFilter(final String pluginName) {
			this.setPluginName(pluginName);
		}

		public String getPluginName() {
			return this.pluginName;
		}

		public void setPluginName(final String pluginName) {
			this.pluginName = pluginName;
		}

		@Override
		public synchronized boolean matchRemaining(final Message message) {
			if (message.getMsgType() == WorldManagerClient.MSG_TYPE_HOST_INSTANCE) {
				final WorldManagerClient.HostInstanceMessage hostMsg = (WorldManagerClient.HostInstanceMessage) message;
				return this.pluginName.equals(hostMsg.getPluginName());
			}
			return false;
		}
	}

	public static class WorldManagerInstance extends Entity {
		private transient QuadTree<WMWorldNode> quadtree;
		private transient Fog globalFog;
		private static final long serialVersionUID = 1L;

		public WorldManagerInstance() {
		}

		public WorldManagerInstance(final OID instanceOid) {
			super(instanceOid);
		}

		public QuadTree<WMWorldNode> getQuadTree() {
			return this.quadtree;
		}

		public void setQuadTree(final QuadTree<WMWorldNode> quadtree) {
			this.quadtree = quadtree;
		}

		public Fog getGlobalFog() {
			if (this.globalFog == null) {
				final InstanceClient.InstanceInfo instanceInfo = InstanceClient.getInstanceInfo(this.getOid(), 16);
				this.globalFog = instanceInfo.fog;
			}
			return this.globalFog;
		}
	}

	public static class LocationNamespaceFilter extends NamespaceFilter {
		Geometry geometry;
		private static final long serialVersionUID = 1L;

		public LocationNamespaceFilter() {
			this.geometry = null;
		}

		public boolean matchesRest(final Message msg) {
			if (!(msg instanceof ObjectManagerClient.GenerateSubObjectMessage)) {
				return false;
			}
			final ObjectManagerClient.GenerateSubObjectMessage genObjMsg = (ObjectManagerClient.GenerateSubObjectMessage) msg;
			final Template t = genObjMsg.getTemplate();
			final Point loc = (Point) t.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC);
			if (loc == null) {
				Log.warn("LocationNamespaceFilter: subobj msg has null loc");
				return false;
			}
			final boolean rv = this.getGeometry().contains(loc);
			if (Log.loggingDebug) {
				Log.debug("LocationNamespaceFilter: geometry=" + this.getGeometry() + ", loc=" + loc + ", rv=" + rv);
			}
			return rv;
		}

		public void setGeometry(final Geometry g) {
			this.geometry = g;
		}

		public Geometry getGeometry() {
			return this.geometry;
		}
	}

	public static class WorldManagerTransferFilter extends TransferFilter {
		List<Geometry> geoList;
		private static final long serialVersionUID = 1L;

		public WorldManagerTransferFilter() {
			this.geoList = new LinkedList<Geometry>();
		}

		public WorldManagerTransferFilter(final Geometry g) {
			this.geoList = new LinkedList<Geometry>();
			this.addGeometry(g);
		}

		public void addGeometry(final Geometry g) {
			this.geoList.add(g);
		}

		public boolean matchesGeometry(final Point loc) {
			for (final Geometry g : this.geoList) {
				if (g.contains(loc)) {
					Log.debug("WorldManagerTransferFilter.matchesGeometry: matched.  loc=" + loc + ", geometry=" + g);
					return true;
				}
			}
			Log.debug("WorldManagerTransferFilter.matchesGeometry: no geometries matched");
			return false;
		}

		@Override
		public boolean matchesMap(final Map propMap, final Message msg) {
			final Point loc = (Point) propMap.get(WorldManagerClient.MSG_PROP_LOC);
			if (loc == null) {
				Log.debug("WorldManagerTransferFilter.matchesMap: no loc, msg=" + msg);
				return false;
			}
			return this.matchesGeometry(loc);
		}
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
