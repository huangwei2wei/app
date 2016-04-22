// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.objects.Marker;
import atavism.server.plugins.InstanceClient;
import atavism.agis.objects.DetourActor;
import java.util.Calendar;
import atavism.agis.objects.ArenaQueue;
import atavism.server.messages.LogoutMessage;
import atavism.msgsys.ResponseMessage;
import java.util.LinkedList;
import atavism.server.util.Points;
import atavism.server.objects.World;
import atavism.server.math.AOVector;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.math.Quaternion;
import atavism.server.engine.WorldNode;
import atavism.server.engine.WMWorldNode;
import atavism.server.util.Logger;
import atavism.agis.util.AgisDisplayContext;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.Message;
import atavism.server.engine.BasicWorldNode;
import atavism.server.objects.Entity;
import java.util.ArrayList;
import atavism.server.objects.EntityManager;
import java.util.List;
import atavism.server.math.Point;
import java.util.Iterator;
import atavism.server.objects.DisplayContext;
import java.io.Serializable;
import java.util.Map;
import atavism.server.objects.LightData;
import atavism.server.objects.Light;
import atavism.agis.objects.AgisMob;
import atavism.server.objects.ObjectTypes;
import atavism.server.util.Log;
import atavism.server.engine.Namespace;
import atavism.server.objects.ObjectType;
import atavism.server.objects.AOObject;
import atavism.server.objects.Template;
import atavism.server.engine.EnginePlugin;
import atavism.server.engine.Hook;
import atavism.server.messages.LoginMessage;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.MessageTypeFilter;
import atavism.agis.objects.AgisObject;
import atavism.agis.objects.InstanceNavMeshManager;
import atavism.server.engine.OID;
import java.util.HashMap;
import atavism.server.plugins.WorldManagerPlugin;

public class AgisWorldManagerPlugin extends WorldManagerPlugin {
	public static Float defaultRunThreshold;
	private HashMap<OID, InstanceNavMeshManager> instanceNavMeshes;
	private long serverStartTime;
	private long currentSecondsRunning;
	public static final int MOVEMENT_STATE_RUNNING = 1;
	public static final int MOVEMENT_STATE_SWIMMING = 2;
	public static final int MOVEMENT_STATE_FLYING = 3;
	public static final String PROP_FOLLOW_TERRAIN = "follow_terrain";
	public static final String PROP_MOVEMENT_STATE = "movement_state";
	public static final String PROP_MOVEMENT_SPEED = "movement_speed";
	public static final String PROP_ACTION_STATE = "action_state";
	protected int waterHeight;

	static {
		AgisWorldManagerPlugin.defaultRunThreshold = 2.0f;
	}

	public AgisWorldManagerPlugin() {
		this.instanceNavMeshes = new HashMap<OID, InstanceNavMeshManager>();
		this.currentSecondsRunning = 0L;
		this.waterHeight = Integer.MIN_VALUE;
		this.propertyExclusions.add(AgisObject.baseDCKey);
	}

	public void onActivate() {
		super.onActivate();
		final MessageTypeFilter filter = new MessageTypeFilter();
		filter.addType(AgisMobClient.MSG_TYPE_ADD_TARGET_TO_CHECK);
		filter.addType(AgisMobClient.MSG_TYPE_REMOVE_TARGET_TO_CHECK);
		filter.addType(AgisMobClient.MSG_TYPE_SET_MOVEMENT_STATE);
		filter.addType(AgisMobClient.MSG_TYPE_SET_UNDERWATER);
		filter.addType(AgisMobClient.MSG_TYPE_CHANGE_INSTANCE);
		filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
		filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
		filter.addType(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS);
		Engine.getAgent().createSubscription((IFilter) filter, (MessageCallback) this);
		final MessageTypeFilter filter2 = new MessageTypeFilter();
		filter2.addType(CombatClient.MSG_TYPE_GET_AOE_TARGETS);
		filter2.addType(LoginMessage.MSG_TYPE_LOGIN);
		Engine.getAgent().createSubscription((IFilter) filter2, (MessageCallback) this, 8);
		this.serverStartTime = System.currentTimeMillis();
	}

	protected void registerHooks() {
		super.registerHooks();
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SETWNODE_REQ, (Hook) new SetWNodeReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATEWNODE_REQ, (Hook) new UpdateWNodeReqHook());
		this.getHookManager().addHook(EnginePlugin.MSG_TYPE_SET_PROPERTY, (Hook) new NoMovePropertyHook());
		this.getHookManager().addHook(EnginePlugin.MSG_TYPE_SET_PROPERTY_NONBLOCK, (Hook) new NoMovePropertyHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_REPARENT_WNODE_REQ, (Hook) new ReparentWNodeReqHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, (Hook) new SpawnedHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, (Hook) new DespawnedHook());
		this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook) new LoginHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_GET_AOE_TARGETS, (Hook) new GetTargetsInAreaHook());
		this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SET_MOVEMENT_STATE, (Hook) new SetMovementStateHook());
		this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SET_UNDERWATER, (Hook) new SetUnderwaterHook());
		this.getHookManager().addHook(AgisMobClient.MSG_TYPE_CHANGE_INSTANCE, (Hook) new ChangeInstanceHook());
		this.getHookManager().addHook(AgisMobClient.MSG_TYPE_SPAWN_INSTANCE_MOBS, (Hook) new SpawnInstanceMobsHook());
	}
	@Override
	protected AOObject generateWorldManagerSubObject(final Template template, final OID masterOid) {
		final ObjectType objType = (ObjectType) template.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE);
		AOObject obj = null;
		if (Log.loggingDebug) {
			Log.debug("AgisWorldManagerPlugin: generateWorldManagerSubObject: objectType=" + objType + ", template=" + template);
		}
		if (objType == null) {
			Log.warn("AgisWorldManagerPlugin: generateSubObject: no object type, using structure");
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
			Log.warn("AgisWorldManagerPlugin.generateSubObject: no props in ns " + Namespace.WORLD_MANAGER);
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
					Log.warn("AgisWorldManagerPlugin.generateSubObject: obj has no display context, oid=" + masterOid);
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
						Log.debug("[CYC][1] entityType: " + entity.getType() + ", objectType: " + objectType);
						final Point entityLoc = entityWorldNode.getLoc();
						Log.debug("[CYC][1] loc: " + loc + ", entityLoc: " + entityLoc + ", entityName: " + entity.getName());
						if (Math.round(Point.distanceTo(loc, entityLoc)) <= radius) {
							objectsIn.add(entityOid);
						}
						Log.debug("[CYC][1] distance: " + Math.round(Point.distanceTo(loc, entityLoc)) + ", radius: " + radius);
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
					Log.debug("[CYC][2] entityType: " + entity.getType());
					final Point entityLoc2 = entityWorldNode2.getLoc();
					Log.debug("[CYC][2] loc: " + loc + ", entityLoc: " + entityLoc2 + ", entityName: " + entity.getName());
					if (Math.round(Point.distanceTo(loc, entityLoc2)) <= radius) {
						objectsIn.add(entityOid);
					}
					Log.debug("[CYC][2] distance: " + Math.round(Point.distanceTo(loc, entityLoc2)) + ", radius: " + radius);
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

	protected void sendDCMessage(final AOObject obj, final boolean forceInstantLoad) {
		if (Log.loggingDebug) {
			AgisWorldManagerPlugin.log.debug("sendDCMessage: obj=" + obj);
		}
		if (!(obj instanceof AgisObject)) {
			return;
		}
		final DisplayContext dc = obj.displayContext();
		this.sendDCMessage(obj.getOid(), dc, forceInstantLoad);
	}

	protected void sendDCMessage(final OID oid, final DisplayContext dc, final boolean forceInstantLoad) {
		if (Log.loggingDebug) {
			AgisWorldManagerPlugin.log.debug("sendDCMessage: obj=" + oid);
		}
		if (dc == null) {
			AgisWorldManagerPlugin.log.warn("sendDCMessage: obj has no dc: " + oid);
			return;
		}
		final WorldManagerClient.DisplayContextMessage dcMsg = new WorldManagerClient.DisplayContextMessage(oid, dc);
		dcMsg.setForceInstantLoad(forceInstantLoad);
		Engine.getAgent().sendBroadcast((Message) dcMsg);
	}

	protected void sendPropertyMessage(final OID notifyOid, final AOObject updateObj) {
		if (!(updateObj instanceof AgisObject)) {
			if (Log.loggingDebug) {
				AgisWorldManagerPlugin.log.debug("AgisWorldManagerPlugin.sendPropertyMessage: skipping, obj is not agisobject: " + updateObj);
			}
			return;
		}
		final AgisObject mObj = (AgisObject) updateObj;
		final OID updateOid = updateObj.getMasterOid();
		final PropertyMessage propMessage = new PropertyMessage(updateOid, notifyOid);
		for (final String key : mObj.getPropertyMap().keySet()) {
			if (this.propertyExclusions.contains(key)) {
				continue;
			}
			propMessage.setProperty(key, mObj.getProperty(key));
		}
		Log.debug("AgisWorldManagerPlugin.sendPropertyMessage: sending property message for obj=" + updateObj + " to=" + notifyOid + " msg=" + propMessage);
		Engine.getAgent().sendBroadcast((Message) propMessage);
	}

	protected void sendTargetedPropertyMessage(final OID targetOid, final AOObject updateObj) {
		if (!(updateObj instanceof AgisObject)) {
			if (Log.loggingDebug) {
				AgisWorldManagerPlugin.log.debug("AgisWorldManagerPlugin.sendTargetedPropertyMessage: skipping, obj is not agisobject: " + updateObj);
			}
			return;
		}
		final AgisObject mObj = (AgisObject) updateObj;
		final OID updateOid = updateObj.getMasterOid();
		final WorldManagerClient.TargetedPropertyMessage propMessage = new WorldManagerClient.TargetedPropertyMessage(targetOid, updateOid);
		for (final String key : mObj.getPropertyMap().keySet()) {
			if (this.propertyExclusions.contains(key)) {
				continue;
			}
			propMessage.setProperty(key, mObj.getProperty(key));
		}
		Log.debug("AgisWorldManagerPlugin.sendTargetedPropertyMessage: subject=" + updateObj + " target=" + targetOid + " msg=" + propMessage);
		Engine.getAgent().sendBroadcast((Message) propMessage);
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

	class SetWNodeReqHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.SetWorldNodeReqMessage setNodeMsg = (WorldManagerClient.SetWorldNodeReqMessage) msg;
			final BasicWorldNode wnode = setNodeMsg.getWorldNode();
			final OID oid = setNodeMsg.getSubject();
			final Entity entity = AgisWorldManagerPlugin.this.getWorldManagerEntity(oid);
			boolean rv = false;
			if (entity == null) {
				AgisWorldManagerPlugin.log.warn("SetWNodeReqHook: cannot find entity oid=" + oid);
			} else if (entity instanceof AOObject) {
				final AOObject obj = (AOObject) entity;
				if (obj.worldNode().isSpawned()) {
					AgisWorldManagerPlugin.log.warn("SetWNodeReqHook: cannot set worldnode, object currently spawned oid=" + oid);
				} else {
					Quaternion currentOrient = null;
					if (obj.worldNode() != null) {
						currentOrient = obj.worldNode().getOrientation();
					}
					final WMWorldNode newWnode = new WMWorldNode(wnode);
					if (newWnode.getOrientation() == null) {
						newWnode.setOrientation(currentOrient);
					}
					newWnode.setPerceptionRadius(((WMWorldNode) obj.worldNode()).getPerceptionRadius());
					if (Log.loggingDebug) {
						AgisWorldManagerPlugin.log.debug("SetWNodeReqHook: obj=" + obj + ", newWnode=" + newWnode + ", perceiver=" + obj.perceiver());
					}
					obj.worldNode((WorldNode) newWnode);
					newWnode.setObject(obj);
					if ((setNodeMsg.getFlags() & 0x1) != 0x0) {
						Engine.getPersistenceManager().persistEntity((Entity) obj);
					} else {
						Engine.getPersistenceManager().setDirty((Entity) obj);
					}
					if (Log.loggingDebug) {
						AgisWorldManagerPlugin.log.debug("SetWNodeReqHook: done oid=" + oid + ", wnode=" + obj.worldNode());
					}
					rv = true;
				}
			} else {
				AgisWorldManagerPlugin.log.debug("SetWNodeReqHook: not aoobject oid=" + oid);
			}
			Engine.getAgent().sendBooleanResponse(msg, rv);
			return true;
		}
	}

	class UpdateWNodeReqHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.UpdateWorldNodeReqMessage updateMsg = (WorldManagerClient.UpdateWorldNodeReqMessage) msg;
			final BasicWorldNode wnode = updateMsg.getWorldNode();
			final OID masterOid = updateMsg.getSubject();
			final Entity entity = AgisWorldManagerPlugin.this.getWorldManagerEntity(masterOid);
			final Boolean updateOverride = updateMsg.getOverride();
			if (entity == null) {
				AgisWorldManagerPlugin.log.error("UpdateWNodeReqHook: could not find entity, masterOid=" + masterOid);
				return false;
			}
			if (!(entity instanceof AOObject)) {
				AgisWorldManagerPlugin.log.error("UpdateWNodeReqHook: entity is not an obj: " + entity);
				return false;
			}
			final AOObject obj = (AOObject) entity;
			final InterpolatedWorldNode curWnode = (InterpolatedWorldNode) obj.worldNode();
			final boolean nomove = obj.getBooleanProperty("world.nomove");
			final boolean noturn = obj.getBooleanProperty("world.noturn");
			boolean sendCorrection = false;
			final Point oldLoc = curWnode.getLoc();
			if (Log.loggingDebug) {
				Log.debug("UpdateWNodeReqHook: oldLoc=" + oldLoc + " nomove=" + nomove + " noturn=" + noturn);
			}
			final BasicWorldNode newNode = new BasicWorldNode(curWnode);
			Quaternion orient = wnode.getOrientation();
			if (orient != null) {
				if (!updateOverride && noturn) {
					if (!curWnode.getOrientation().equals(orient)) {
						orient = curWnode.getOrientation();
						newNode.setOrientation(orient);
						sendCorrection = true;
					}
				} else {
					if (updateOverride) {
						newNode.setOrientation(orient);
						sendCorrection = true;
					}
					curWnode.setOrientation(orient);
				}
			}
			AOVector dir = wnode.getDir();
			if (dir != null) {
				if (nomove && !dir.isZero()) {
					dir = new AOVector(0.0f, 0.0f, 0.0f);
					newNode.setDir(dir);
					sendCorrection = true;
				}
				curWnode.setDir(dir);
			}
			Point newLoc = wnode.getLoc();
			if (Log.loggingDebug) {
				Log.debug("UpdateWNodeReqHook: masterOid " + masterOid + ", oldLoc " + oldLoc + ", newLoc " + newLoc + ", override " + updateOverride);
			}
			if (newLoc != null) {
				if (!updateOverride && nomove) {
					if (Point.distanceTo(oldLoc, newLoc) > 0.0f) {
						newLoc = oldLoc;
						newNode.setLoc(newLoc);
						sendCorrection = true;
					}
				} else if (!updateOverride && !Points.isClose(oldLoc, newLoc, World.getLocTolerance())) {
					newLoc = curWnode.getRawLoc();
					newNode.setLoc(newLoc);
					sendCorrection = true;
				} else if (updateOverride) {
					newNode.setLoc(newLoc);
					sendCorrection = true;
				}
			}
			if (Log.loggingDebug) {
				AgisWorldManagerPlugin.log.debug("UpdateWNodeReqHook: set world node, entity=" + entity + ", new wnode=" + curWnode);
			}
			if (sendCorrection) {
				if (Log.loggingDebug) {
					AgisWorldManagerPlugin.log.debug("UpdateWNodeReqHook: sending world node correction " + newNode);
				}
				WorldManagerClient.correctWorldNode(masterOid, newNode);
			}
			if (updateMsg.getPreMessage() != null) {
				Engine.getAgent().sendBroadcast(updateMsg.getPreMessage());
			}
			if (!newLoc.equals((Object) oldLoc)) {
				curWnode.setLoc(newLoc);
			}
			if (updateMsg.getPostMessage() != null) {
				Engine.getAgent().sendBroadcast(updateMsg.getPostMessage());
			}
			if (Point.distanceTo(oldLoc, newLoc) > 0.1f) {
				final CombatClient.interruptAbilityMessage interruptMsg = new CombatClient.interruptAbilityMessage(masterOid);
				Engine.getAgent().sendBroadcast((Message) interruptMsg);
			}
			final BasicWorldNode updateNode = new BasicWorldNode(curWnode);
			final WorldManagerClient.UpdateWorldNodeMessage upMsg = new WorldManagerClient.UpdateWorldNodeMessage(masterOid, updateNode);
			Engine.getAgent().sendBroadcast((Message) upMsg);
			return true;
		}
	}

	class ReparentWNodeReqHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ReparentWNodeReqMessage rMsg = (WorldManagerClient.ReparentWNodeReqMessage) msg;
			final OID oid = rMsg.getSubject();
			final OID parentOid = rMsg.getParentOid();
			if (Log.loggingDebug) {
				AgisWorldManagerPlugin.log.debug("ReparentWNodeReqHook: oid=" + oid + " parent=" + parentOid);
			}
			final Entity entity = AgisWorldManagerPlugin.this.getWorldManagerEntity(oid);
			InterpolatedWorldNode parentWnode = null;
			if (entity == null) {
				AgisWorldManagerPlugin.log.error("ReparentWNodeReqHook: could not find entity: " + oid);
				return false;
			}
			if (!(entity instanceof AOObject)) {
				AgisWorldManagerPlugin.log.error("ReparentWNodeReqHook: entity is not an obj: " + entity);
				return false;
			}
			final AOObject obj = (AOObject) entity;
			final InterpolatedWorldNode wnode = (InterpolatedWorldNode) obj.worldNode();
			if (parentOid != null) {
				final Entity parent = AgisWorldManagerPlugin.this.getWorldManagerEntity(parentOid);
				if (parent == null) {
					AgisWorldManagerPlugin.log.error("ReparentWNodeReqHook: could not find parent: " + parent);
					return false;
				}
				if (!(parent instanceof AOObject)) {
					AgisWorldManagerPlugin.log.error("ReparentWNodeReqHook: parent is not an obj: " + parent);
					return false;
				}
				final AOObject parentObj = (AOObject) parent;
				parentWnode = (InterpolatedWorldNode) parentObj.worldNode();
			}
			final InterpolatedWorldNode oldParentWnode = (InterpolatedWorldNode) wnode.getParent();
			if (oldParentWnode != null) {
				oldParentWnode.removeChild((WorldNode) wnode);
			}
			wnode.setParent((WorldNode) parentWnode);
			if (parentWnode != null) {
				parentWnode.addChild((WorldNode) wnode);
				wnode.setLoc(parentWnode.getLoc());
				wnode.setDir(parentWnode.getDir());
				wnode.setOrientation(parentWnode.getOrientation());
			}
			final BasicWorldNode bwnode = new BasicWorldNode(wnode);
			final WorldManagerClient.UpdateWorldNodeMessage updateMsg = new WorldManagerClient.UpdateWorldNodeMessage(oid, bwnode);
			Engine.getAgent().sendBroadcast((Message) updateMsg);
			final WorldManagerClient.WorldNodeCorrectMessage correctMsg = new WorldManagerClient.WorldNodeCorrectMessage(oid, bwnode);
			Engine.getAgent().sendBroadcast((Message) correctMsg);
			return true;
		}
	}

	class NoMovePropertyHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final EnginePlugin.SetPropertyMessage rMsg = (EnginePlugin.SetPropertyMessage) msg;
			final OID oid = rMsg.getSubject();
			if (rMsg.containsKey("world.nomove")) {
				final Boolean noMove = (Boolean) rMsg.getProperty("world.nomove");
				final AOObject obj = (AOObject) AgisWorldManagerPlugin.this.getWorldManagerEntity(oid);
				if (noMove && obj != null && obj.getType().isMob() && obj.getProperty(WorldManagerClient.MOB_PATH_PROPERTY) != null) {
					AgisWorldManagerPlugin.log.debug("NoMovePropertyHook: stopping object");
					final WorldManagerClient.MobPathCorrectionMessage correction = new WorldManagerClient.MobPathCorrectionMessage(oid, System.currentTimeMillis(), "linear", 0.0f, "",
							(List) new LinkedList());
					Engine.getAgent().sendBroadcast((Message) correction);
					final WorldManagerClient.MobPathMessage cancellation = new WorldManagerClient.MobPathMessage(oid, System.currentTimeMillis(), "linear", 0.0f, "", (List) new LinkedList());
					Engine.getAgent().sendBroadcast((Message) cancellation);
					final BasicWorldNode wnode = obj.baseWorldNode();
					wnode.setDir(new AOVector(0.0f, 0.0f, 0.0f));
					final WorldManagerClient.UpdateWorldNodeMessage upMsg = new WorldManagerClient.UpdateWorldNodeMessage(oid, wnode);
					Engine.getAgent().sendBroadcast((Message) upMsg);
				}
			}
			return true;
		}
	}

	class LoginHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final LoginMessage message = (LoginMessage) msg;
			final OID playerOid = message.getSubject();
			Engine.getAgent().sendResponse(new ResponseMessage((Message) message));
			return true;
		}
	}

	class LogoutHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final LogoutMessage message = (LogoutMessage) msg;
			final OID playerOid = message.getSubject();
			Log.debug("LogoutHook: playerOid=" + playerOid);
			int arenaID = -1;
			try {
				arenaID = (int) EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "arenaID");
			} catch (NullPointerException e) {
				Log.warn("ARENA PLUGIN: player " + playerOid + " does not have an arenaID property");
			}
			if (arenaID != -1) {
				ArenaClient.removePlayer(playerOid);
			}
			final ArrayList<ArenaQueue> queues = ArenaPlugin.getArenaQueues();
			for (int k = 0; k < queues.size(); ++k) {
				queues.get(k).removePlayer(playerOid);
			}
			int challengeID = -1;
			try {
				challengeID = (int) EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "duelChallengeID");
			} catch (NullPointerException ex) {
			}
			if (challengeID != -1) {
				EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "factionOverride", (Serializable) "");
				final String name = WorldManagerClient.getObjectInfo(playerOid).name;
				Log.debug("ARENA PLUGIN: removing player who is logging out from their duel");
				ArenaClient.duelChallengeDisconnect(playerOid, name, challengeID);
			}
			Log.debug("ARENA PLUGIN: checking duelID for player logging out");
			int duelID = -1;
			try {
				duelID = (int) EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "duelID");
			} catch (NullPointerException ex2) {
			}
			Log.debug("ARENA PLUGIN: checking duelID for player logging out; ID is " + duelID);
			if (duelID != -1) {
				EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "factionOverride", (Serializable) "");
				final String name2 = WorldManagerClient.getObjectInfo(playerOid).name;
				Log.debug("ARENA PLUGIN: removing player who is logging out from their duel");
				ArenaClient.duelDisconnect(playerOid, name2, duelID);
			}
			Engine.getAgent().sendResponse(new ResponseMessage((Message) message));
			return true;
		}
	}

	class SpawnedHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.SpawnedMessage spawnedMsg = (WorldManagerClient.SpawnedMessage) msg;
			final OID objOid = spawnedMsg.getSubject();
			final WorldManagerClient.ObjectInfo objInfo = WorldManagerClient.getObjectInfo(objOid);
			if (objInfo.objType == ObjectTypes.player) {
				final long timeDif = (int) (System.currentTimeMillis() - AgisWorldManagerPlugin.this.serverStartTime) / 1000;
				final Map<String, Serializable> props = new HashMap<String, Serializable>();
				props.put("ext_msg_subtype", "server_time");
				props.put("server_time_running", timeDif);
				final Calendar cal = Calendar.getInstance();
				props.put("year", cal.get(1));
				props.put("month", cal.get(2));
				props.put("day", cal.get(5));
				props.put("hour", cal.get(11));
				props.put("minute", cal.get(12));
				props.put("second", cal.get(13));
				final WorldManagerClient.TargetedExtensionMessage teMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, objOid, objOid, false, (Map) props);
				Engine.getAgent().sendBroadcast((Message) teMsg);
			} else if (objInfo.objType == ObjectTypes.mob) {
				final OID instanceOid = spawnedMsg.getInstanceOid();
				if (AgisWorldManagerPlugin.this.instanceNavMeshes.containsKey(instanceOid)) {
					final Entity entity = AgisWorldManagerPlugin.this.getWorldManagerEntity(objOid);
					final DetourActor actor = new DetourActor(objOid, (AOObject) entity);
					AgisWorldManagerPlugin.this.instanceNavMeshes.get(instanceOid).addActor(objOid, objInfo.loc, actor);
				}
			}
			return true;
		}
	}

	class DespawnedHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.DespawnedMessage despawnedMsg = (WorldManagerClient.DespawnedMessage) msg;
			final OID objOid = despawnedMsg.getSubject();
			final OID instanceOid = despawnedMsg.getInstanceOid();
			if (AgisWorldManagerPlugin.this.instanceNavMeshes.containsKey(instanceOid)) {
				AgisWorldManagerPlugin.this.instanceNavMeshes.get(instanceOid).removeActor(objOid);
			}
			return true;
		}
	}

	class SetMovementStateHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage) msg;
			final OID playerOid = spawnMsg.getSubject();
			final int movementState = (int) spawnMsg.getProperty("movement_state");
			Log.debug("STATE: setting movement state to " + movementState + " for " + playerOid);
			EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "movement_state", (Serializable) movementState);
			boolean followTerrain = true;
			if (movementState != 1) {
				followTerrain = false;
			}
			EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "follow_terrain", (Serializable) followTerrain);
			final Entity entity = AgisWorldManagerPlugin.this.getWorldManagerEntity(playerOid);
			final AOObject obj = (AOObject) entity;
			final InterpolatedWorldNode curWnode = (InterpolatedWorldNode) obj.worldNode();
			curWnode.setFollowsTerrain(followTerrain);
			return true;
		}
	}

	class SetUnderwaterHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage) msg;
			final OID playerOid = spawnMsg.getSubject();
			final boolean underwater = (boolean) spawnMsg.getProperty("underwater");
			Log.debug("STATE: setting underwater to " + underwater + " for " + playerOid);
			EnginePlugin.setObjectProperty(playerOid, CombatClient.NAMESPACE, "underwater", (Serializable) underwater);
			return true;
		}
	}

	class ChangeInstanceHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage) msg;
			final OID playerOid = spawnMsg.getSubject();
			final String instanceName = (String) spawnMsg.getProperty("instanceName");
			final String markerName = (String) spawnMsg.getProperty("marker");
			Log.debug("CHANGEI: player " + playerOid + " is changing instance to " + instanceName + " with marker " + markerName);
			final OID instanceOid = InstanceClient.getInstanceOid(instanceName);
			if (instanceOid == null) {
				Log.debug("CHANGEI: Instance name is wrong: " + instanceName);
				return true;
			}
			final BasicWorldNode node = new BasicWorldNode();
			node.setInstanceOid(instanceOid);
			if (markerName == null || markerName.equals("")) {
				final Point loc = new Point((AOVector) spawnMsg.getProperty("loc"));
				node.setInstanceOid(instanceOid);
				node.setLoc(loc);
			} else {
				final Marker marker = InstanceClient.getMarker(instanceOid, markerName);
				node.setOrientation(marker.getOrientation());
				node.setLoc(marker.getPoint());
			}
			final AOVector direction = new AOVector();
			node.setDir(direction);
			InstanceClient.objectInstanceEntry(playerOid, node, 0);
			return true;
		}
	}
	/**
	 * Ìí¼ÓµØÍ¼³¡¾°
	 * 
	 * @author doter
	 * 
	 */
	class SpawnInstanceMobsHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final AgisMobClient.SpawnInstanceMobsMessage SPMsg = (AgisMobClient.SpawnInstanceMobsMessage) msg;
			final String instanceName = InstanceClient.getInstanceInfo(SPMsg.instanceOid, 4).templateName;
			final InstanceNavMeshManager navMeshManager = new InstanceNavMeshManager(instanceName, SPMsg.instanceOid);
			AgisWorldManagerPlugin.this.instanceNavMeshes.put(SPMsg.instanceOid, navMeshManager);
			return true;
		}
	}

	class GetTargetsInAreaHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.getAoeTargetsMessage message = (CombatClient.getAoeTargetsMessage) msg;
			final OID subjectOid = message.getSubject();
			final BasicWorldNode subjectWorldNode = ((AOObject) AgisWorldManagerPlugin.this.getWorldManagerEntity(subjectOid)).baseWorldNode();
			final OID instanceOid = subjectWorldNode.getInstanceOid();
			List<OID> objectsIn = null;
			if (instanceOid != null) {
				objectsIn = AgisWorldManagerPlugin.this.getInstanceObjectsIn(instanceOid, message.getLoc(), message.getRadius(), message.getObjectType());
			} else {
				objectsIn = AgisWorldManagerPlugin.this.getObjectsIn(message.getLoc(), message.getRadius(), message.getObjectType());
			}
			Engine.getAgent().sendObjectResponse((Message) message, (Object) objectsIn);
			return true;
		}
	}
}
