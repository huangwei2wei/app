// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import com.app.server.atavism.server.pathing.PathLocAndDir;
import com.app.server.atavism.server.pathing.PathInterpolator;
import com.app.server.atavism.server.pathing.PathSpline;
import com.app.server.atavism.server.pathing.PathLinear;
import com.app.server.atavism.server.objects.LightData;
import com.app.server.atavism.server.objects.SoundData;
import com.app.server.atavism.server.objects.Region;
import com.app.server.atavism.server.messages.PropertyMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import atavism.server.messages.IPropertyMessage;
import com.app.server.atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import atavism.server.util.AnimationCommand;
import atavism.msgsys.HasTarget;
import atavism.server.messages.BracketedMessage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashSet;
import atavism.server.objects.Road;
import java.util.Set;
import atavism.server.objects.Fog;
import atavism.server.objects.FogRegionConfig;
import atavism.server.engine.EventParser;
import com.app.server.atavism.server.objects.Color;
import atavism.msgsys.ITargetSessionId;
import atavism.msgsys.ResponseMessage;
import atavism.msgsys.TargetMessage;
import atavism.server.objects.RoadSegment;
import atavism.server.messages.ClientMessage;
import java.util.HashMap;
import atavism.server.network.AOByteBuffer;
import java.util.Map;
import com.app.server.atavism.server.math.AOVector;
import atavism.server.objects.Marker;
import com.app.server.atavism.server.objects.ObjectTypes;
import com.app.server.atavism.server.objects.Entity;
import atavism.server.engine.EnginePlugin;
import java.io.Serializable;
import com.app.server.atavism.server.math.Geometry;
import com.app.server.atavism.server.math.Quaternion;
import java.util.Collection;
import com.app.server.atavism.server.objects.DisplayContext;
import java.util.List;
import com.app.server.atavism.server.math.Point;
import atavism.msgsys.NoRecipientsException;
import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import com.app.server.atavism.server.engine.Engine;
import atavism.server.util.Log;
import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.objects.ObjectType;
import atavism.msgsys.MessageType;

public class WorldManagerClient {
	public static final int NO_FLAGS = 0;
	public static final int SAVE_NOW = 1;
	public static final byte modifyDisplayContextActionReplace = 1;
	public static final byte modifyDisplayContextActionAdd = 2;
	public static final byte modifyDisplayContextActionAddChild = 3;
	public static final byte modifyDisplayContextActionRemove = 4;
	public static final byte modifyDisplayContextActionRemoveChild = 5;
	public static final String WMGR_LAST_SAVED_PROP;
	public static Long WMGR_SAVE_INTERVAL_MS;
	public static final String WORLD_PROP_NOMOVE = "world.nomove";
	public static final String WORLD_PROP_NOTURN = "world.noturn";
	public static final String ACCOUNT_PROPERTY = "accountId";
	public static String TEMPL_LOC;
	public static String TEMPL_INSTANCE;
	public static String TEMPL_NAME;
	public static String TEMPL_ORIENT;
	public static String TEMPL_SCALE;
	public static String TEMPL_PERCEPTION_RADIUS;
	public static String TEMPL_FOLLOWS_TERRAIN;
	public static String TEMPL_RUN_THRESHOLD;
	public static String TEMPL_TERRAIN_DECAL_DATA;
	public static String TEMPL_SOUND_DATA_LIST;
	public static String TEMPL_ID;
	public static String TEMPL_OBJECT_TYPE;

	public static String TEMPL_DISPLAY_CONTEXT;
	public static final String TEMPL_WORLDMGR_NAME = ":wmName";
	public static String MOB_PATH_PROPERTY;
	public static String MSG_PROP_LOC;
	public static Namespace NAMESPACE;
	public static Namespace INSTANCE_NAMESPACE = null;

	public static boolean setWorldNode(final OID oid, final BasicWorldNode wnode) {
		return setWorldNode(oid, wnode, 0);
	}

	public static boolean setWorldNode(final OID oid, final BasicWorldNode wnode, final int flags) {
		Log.debug("WorldManagerClient.setWorldNode: oid=" + oid + " node=" + wnode + " flags=" + flags);
		final SetWorldNodeReqMessage msg = new SetWorldNodeReqMessage(oid, wnode, flags);
		final Boolean rc = Engine.getAgent().sendRPCReturnBoolean(msg);
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.setWorldNode: oid=" + oid + " got response rc=" + rc);
		}
		return rc;
	}
	/**
	 * 更新世界节点
	 * 
	 * @param oid
	 * @param wnode
	 */
	public static void updateWorldNode(final OID oid, final BasicWorldNode wnode) {
		updateWorldNode(oid, wnode, false, null, null);
	}

	public static void updateWorldNode(final OID oid, final BasicWorldNode wnode, final boolean override) {
		updateWorldNode(oid, wnode, override, null, null);
	}
	/**
	 * 更新世界节点
	 * 
	 * @param oid
	 * @param wnode
	 */
	public static void updateWorldNode(final OID oid, final BasicWorldNode wnode, final boolean override, final Message preMessage, final Message postMessage) {
		final UpdateWorldNodeReqMessage msg = new UpdateWorldNodeReqMessage(oid, wnode);
		msg.setOverride(override);
		msg.setPreMessage(preMessage);
		msg.setPostMessage(postMessage);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static BasicWorldNode getWorldNode(final OID oid) {
		final SubjectMessage msg = new SubjectMessage(WorldManagerClient.MSG_TYPE_GETWNODE_REQ, oid);
		final BasicWorldNode wnode = (BasicWorldNode) Engine.getAgent().sendRPCReturnObject(msg);
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.getWorldNode: oid=" + oid + " wnode=" + wnode);
		}
		return wnode;
	}

	public static void correctWorldNode(final OID oid, final BasicWorldNode wnode) {
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.correctWorldNode: loc=" + wnode.getLoc());
		}
		final WorldNodeCorrectMessage msg = new WorldNodeCorrectMessage(oid, wnode);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void reparentWorldNode(final OID oid, final OID parentOid) {
		Log.debug("WorldManagerClient.reparentWorldNode: sending message");
		final ReparentWNodeReqMessage msg = new ReparentWNodeReqMessage(oid, parentOid);
		Engine.getAgent().sendBroadcast(msg);
		Log.debug("WorldManagerClient.reparentWorldNode: sent message");
	}

	public static Integer spawn(final OID oid) {
		return spawn(oid, null, null);
	}

	public static Integer spawn(final OID oid, final Message preMessage, final Message postMessage) {
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.spawn: oid=" + oid);
		}
		final SpawnReqMessage msg = new SpawnReqMessage(oid);
		msg.setPreMessage(preMessage);
		msg.setPostMessage(postMessage);
		final Integer result = (Integer) Engine.getAgent().sendRPCReturnObject(msg);
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.spawn: response for oid=" + oid + " result=" + result);
		}
		return result;
	}

	public static boolean despawn(final OID oid) {
		return despawn(oid, null, null);
	}

	public static boolean despawn(final OID oid, final Message preMessage, final Message postMessage) {
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.despawn: oid=" + oid);
		}
		Boolean rc = false;
		try {
			final DespawnReqMessage msg = new DespawnReqMessage(oid);
			msg.setPreMessage(preMessage);
			msg.setPostMessage(postMessage);
			rc = Engine.getAgent().sendRPCReturnBoolean(msg);
		} catch (NoRecipientsException nre) {
			Log.exception("WorldManagerClient.despawn(): ", nre);
		}
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.despawn: response oid=" + oid + " rc=" + rc);
		}
		return rc;
	}

	public static ObjectInfo getObjectInfo(final OID oid) {
		final ObjInfoReqMessage msg = new ObjInfoReqMessage(oid);
		ObjInfoRespMessage respMsg = null;
		try {
			respMsg = (ObjInfoRespMessage) Engine.getAgent().sendRPC(msg);
		} catch (NoRecipientsException e) {
			Log.warn("WorldManagerClient.getObjectInfo, no recipiends for oid " + oid);
			return null;
		}
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.getObjectInfo: oid=" + oid + " info=" + respMsg.getObjInfo());
		}
		return respMsg.getObjInfo();
	}

	public static List<OID> getObjectsIn(final OID instanceOid, final Point loc, final Integer radius, final ObjectType objectType) {
		final GetObjectsInMessage msg = new GetObjectsInMessage(instanceOid, loc, radius, objectType);
		final List<OID> objectsIn = (List<OID>) Engine.getAgent().sendRPCReturnObject(msg);
		return objectsIn;
	}

	public static DisplayContext getDisplayContext(final OID oid) {
		final DisplayContextReqMessage msg = new DisplayContextReqMessage(oid);
		final DisplayContext dc = (DisplayContext) Engine.getAgent().sendRPCReturnObject(msg);
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.getDisplayContext: oid=" + oid + " dc=" + dc);
		}
		return dc;
	}

	public static void modifyDisplayContext(final OID oid, final byte action, final String base, final List<DisplayContext.Submesh> submeshes) {
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.modifyDisplayContext: oid=" + oid + " action=" + action + " base=" + base + " submeshCount=" + submeshes.size());
		}
		final ModifyDisplayContextMessage msg = new ModifyDisplayContextMessage(oid, action, base, submeshes, null, null);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void modifyDisplayContext(final OID oid, final byte action, final List<DisplayContext.Submesh> submeshes) {
		if (Log.loggingDebug) {
			Log.debug("WorldManagerClient.modifyDisplayContext: oid=" + oid + " action=" + action + " submeshCount=" + submeshes.size());
		}
		final ModifyDisplayContextMessage msg = new ModifyDisplayContextMessage(oid, action, submeshes);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void updateObject(final OID notifyObj, final OID updateObj) {
		final UpdateMessage msg = new UpdateMessage(notifyObj, updateObj);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void refreshWNode(final OID objId) {
		final RefreshWNodeMessage msg = new RefreshWNodeMessage(objId);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void sendChatMsg(final OID objId, final String chatterName, final int channelId, final String text) {
		final ComReqMessage msg = new ComReqMessage(objId, chatterName, channelId, text);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void sendObjChatMsg(final OID objOid, final int channelId, final String text) {
		sendObjChatMsg(objOid, "", channelId, text);
	}

	public static void sendObjChatMsg(final OID objOid, final String senderName, final int channelId, final String text) {
		final TargetedComMessage comMsg = new TargetedComMessage(objOid, objOid, senderName, channelId, text);
		Engine.getAgent().sendBroadcast(comMsg);
	}

	public static void sendSysChatMsg(final String text) {
		final SysChatMessage sysMsg = new SysChatMessage(text);
		Engine.getAgent().sendBroadcast(sysMsg);
	}

	public static void sendOrientMsg(final OID objId, final Quaternion q) {
		final OrientReqMessage msg = new OrientReqMessage(objId, q);
		Engine.getAgent().sendBroadcast(msg);
	}

	public static void sendPerceiverRegionsMsg(final OID instanceOid, final Geometry region, final String targetSessionId) {
		if (region == null) {
			throw new RuntimeException("region geometry is null");
		}
		final PerceiverRegionsMessage msg = new PerceiverRegionsMessage(instanceOid, region);
		if (targetSessionId != null) {
			msg.setTargetSessionId(targetSessionId);
		}
		Engine.getAgent().sendBroadcast(msg);
	}

	public static Boolean hostInstance(final OID instanceOid, final String wmPluginName) {
		final HostInstanceMessage message = new HostInstanceMessage(wmPluginName, instanceOid);
		return Engine.getAgent().sendRPCReturnBoolean(message);
	}

	@Deprecated
	public static Serializable getObjectProperty(final OID oid, final String key) {
		return EnginePlugin.getObjectProperty(oid, Namespace.WORLD_MANAGER, key);
	}

	@Deprecated
	public static Serializable setObjectProperty(final OID oid, final String key, final Serializable val) {
		return EnginePlugin.setObjectProperty(oid, Namespace.WORLD_MANAGER, key, val);
	}

	@Deprecated
	public static void setObjectPropertyNoResponse(final OID oid, final String key, final Serializable val) {
		EnginePlugin.setObjectPropertyNoResponse(oid, Namespace.WORLD_MANAGER, key, val);
	}

	static {
		WMGR_LAST_SAVED_PROP = (String) Entity.registerTransientPropertyKey("wmgr.lastSaved");
		WorldManagerClient.WMGR_SAVE_INTERVAL_MS = 60000L;
		WorldManagerClient.MSG_TYPE_NEW_DIRLIGHT = MessageType.intern("ao.NEW_DIRLIGHT");
		WorldManagerClient.MSG_TYPE_FREE_OBJECT = MessageType.intern("ao.FREE_OBJECT");
		WorldManagerClient.MSG_TYPE_SET_AMBIENT = MessageType.intern("ao.SET_AMBIENT");
		WorldManagerClient.MSG_TYPE_TERRAIN_REQ = MessageType.intern("ao.TERRAIN_REQ");
		WorldManagerClient.MSG_TYPE_OBJINFO_REQ = MessageType.intern("ao.OBJINFO_REQ");
		WorldManagerClient.MSG_TYPE_DC_REQ = MessageType.intern("ao.DC_REQ");
		WorldManagerClient.MSG_TYPE_FOG = MessageType.intern("ao.FOG");
		WorldManagerClient.MSG_TYPE_SPAWN_REQ = MessageType.intern("ao.SPAWN_REQ");
		WorldManagerClient.MSG_TYPE_SPAWNED = MessageType.intern("ao.SPAWNED");
		WorldManagerClient.MSG_TYPE_DESPAWN_REQ = MessageType.intern("ao.DESPAWN_REQ");
		WorldManagerClient.MSG_TYPE_DESPAWNED = MessageType.intern("ao.DESPAWNED");
		WorldManagerClient.MSG_TYPE_UPDATE_OBJECT = MessageType.intern("ao.UPDATE_OBJECT");
		WorldManagerClient.MSG_TYPE_DISPLAY_CONTEXT = MessageType.intern("ao.DISPLAY_CONTEXT");
		WorldManagerClient.MSG_TYPE_ANIMATION = MessageType.intern("ao.ANIMATION");
		WorldManagerClient.MSG_TYPE_SETWNODE_REQ = MessageType.intern("ao.SETWNODE_REQ");
		WorldManagerClient.MSG_TYPE_UPDATEWNODE_REQ = MessageType.intern("ao.UPDATEWNODE_REQ");
		WorldManagerClient.MSG_TYPE_GETWNODE_REQ = MessageType.intern("ao.GETWNODE_REQ");
		WorldManagerClient.MSG_TYPE_GET_OBJECTS_IN = MessageType.intern("ao.GET_OBJECTS_IN");
		WorldManagerClient.MSG_TYPE_TARGETED_PROPERTY = MessageType.intern("ao.TARGETED_PROPERTY");
		WorldManagerClient.MSG_TYPE_COM_REQ = MessageType.intern("ao.COM_REQ");
		WorldManagerClient.MSG_TYPE_COM = MessageType.intern("ao.COM");
		WorldManagerClient.MSG_TYPE_SYS_CHAT = MessageType.intern("ao.SYS_CHAT");
		WorldManagerClient.MSG_TYPE_UPDATEWNODE = MessageType.intern("ao.UPDATEWNODE");
		WorldManagerClient.MSG_TYPE_WNODECORRECT = MessageType.intern("ao.WNODECORRECT");
		WorldManagerClient.MSG_TYPE_ORIENT_REQ = MessageType.intern("ao.ORIENT_REQ");
		WorldManagerClient.MSG_TYPE_ORIENT = MessageType.intern("ao.ORIENT");
		WorldManagerClient.MSG_TYPE_REFRESH_WNODE = MessageType.intern("ao.REFRESH_WNODE");
		WorldManagerClient.MSG_TYPE_PERCEIVER_REGIONS = MessageType.intern("ao.PERCEIVER_REGIONS");
		WorldManagerClient.MSG_TYPE_NEW_REMOTE_OBJ = MessageType.intern("ao.NEW_REMOTE_OBJ");
		WorldManagerClient.MSG_TYPE_FREE_REMOTE_OBJ = MessageType.intern("ao.FREE_REMOTE_OBJ");
		WorldManagerClient.MSG_TYPE_ROAD = MessageType.intern("ao.ROAD");
		WorldManagerClient.MSG_TYPE_FREE_ROAD = MessageType.intern("ao.FREE_ROAD");
		WorldManagerClient.MSG_TYPE_NEW_REGION = MessageType.intern("ao.NEW_REGION");
		WorldManagerClient.MSG_TYPE_SOUND = MessageType.intern("ao.SOUND");
		WorldManagerClient.MSG_TYPE_MODIFY_DC = MessageType.intern("ao.MODIFY_DC");
		WorldManagerClient.MSG_TYPE_DETACH = MessageType.intern("ao.DETACH");
		WorldManagerClient.MSG_TYPE_REPARENT_WNODE_REQ = MessageType.intern("ao.REPARENT_WNODE_REQ");
		WorldManagerClient.MSG_TYPE_EXTENSION = MessageType.intern("ao.EXTENSION");
		WorldManagerClient.MSG_TYPE_MOB_PATH = MessageType.intern("ao.MOB_PATH");
		WorldManagerClient.MSG_TYPE_MOB_PATH_REQ = MessageType.intern("ao.MOB_PATH_REQ");
		WorldManagerClient.MSG_TYPE_MOB_PATH_CORRECTION = MessageType.intern("ao.MOB_PATH_CORRECTION");
		WorldManagerClient.MSG_TYPE_DIR_LOC_ORIENT = MessageType.intern("ao.DIR_LOC_ORIENT");
		WorldManagerClient.MSG_TYPE_PERCEPTION = MessageType.intern("ao.PERCEPTION");
		WorldManagerClient.MSG_TYPE_PERCEPTION_INFO = MessageType.intern("ao.PERCEPTION_INFO");
		WorldManagerClient.MSG_TYPE_P2P_EXTENSION = MessageType.intern("ao.P2P_EXTENSION");
		WorldManagerClient.MSG_TYPE_HOST_INSTANCE = MessageType.intern("ao.HOST_INSTANCE");
		WorldManagerClient.MSG_TYPE_PLAYER_PATH_WM_REQ = MessageType.intern("ao.PLAYER_PATH_WM_REQ");
		WorldManagerClient.TEMPL_LOC = ":loc";
		WorldManagerClient.TEMPL_INSTANCE = ":instance";
		WorldManagerClient.TEMPL_NAME = ":entityName";
		WorldManagerClient.TEMPL_ORIENT = ":orient";
		WorldManagerClient.TEMPL_SCALE = ":scale";
		WorldManagerClient.TEMPL_PERCEPTION_RADIUS = ":percRadius";
		WorldManagerClient.TEMPL_FOLLOWS_TERRAIN = ":followsTerrain";
		WorldManagerClient.TEMPL_RUN_THRESHOLD = "runThreshold";
		WorldManagerClient.TEMPL_TERRAIN_DECAL_DATA = "terrainDecal";
		WorldManagerClient.TEMPL_SOUND_DATA_LIST = "soundData";
		WorldManagerClient.TEMPL_ID = "templateID";
		WorldManagerClient.TEMPL_OBJECT_TYPE = ":objType";
		WorldManagerClient.TEMPL_OBJECT_TYPE_MOB = ObjectTypes.mob;
		WorldManagerClient.TEMPL_OBJECT_TYPE_PLAYER = ObjectTypes.player;
		WorldManagerClient.TEMPL_OBJECT_TYPE_LIGHT = ObjectTypes.light;
		WorldManagerClient.TEMPL_OBJECT_TYPE_ITEM = ObjectTypes.item;
		WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE = ObjectTypes.structure;
		WorldManagerClient.TEMPL_OBJECT_TYPE_TERRAIN_DECAL = ObjectTypes.terrainDecal;
		WorldManagerClient.TEMPL_OBJECT_TYPE_POINT_SOUND = ObjectTypes.pointSound;
		WorldManagerClient.TEMPL_OBJECT_TYPE_MARKER = Marker.OBJECT_TYPE;
		WorldManagerClient.TEMPL_DISPLAY_CONTEXT = ":displayContext";
		WorldManagerClient.MOB_PATH_PROPERTY = "MobPathMsg";
		WorldManagerClient.MSG_PROP_LOC = "msgPropLoc";
		WorldManagerClient.NAMESPACE = null;
		WorldManagerClient.INSTANCE_NAMESPACE = null;
	}

	public static class ObjectInfo implements Serializable {
		public OID instanceOid;
		public OID oid;
		public String name;
		public OID accountOid;
		public Point loc;
		public Quaternion orient;
		public AOVector scale;
		public ObjectType objType;
		public boolean followsTerrain;
		public AOVector dir;
		public long lastInterp;
		private Map<String, Serializable> propMap;
		private static final long serialVersionUID = 1L;

		public ObjectInfo() {
			this.propMap = null;
		}

		@Override
		public String toString() {
			return "[ObjectInfo: name=" + this.name + ", oid=" + this.oid + ", loc=" + this.loc + ", orient=" + this.orient + ", scale=" + this.scale + ", objType=" + this.objType
					+ ", followsTerrain=" + this.followsTerrain + "]";
		}

		public AOByteBuffer toBuffer(final OID notifyOid) {
			Log.debug("INFO: creating buffer with notifyOid: " + notifyOid);
			final AOByteBuffer buf = new AOByteBuffer(220);
			buf.putOID(notifyOid);
			buf.putInt(8);
			buf.putOID(this.oid);
			buf.putString((this.name == null) ? "unknown" : this.name);
			buf.putPoint((this.loc == null) ? new Point() : this.loc);
			buf.putQuaternion((this.orient == null) ? new Quaternion() : this.orient);
			buf.putAOVector((this.scale == null) ? new AOVector(1.0f, 1.0f, 1.0f) : this.scale);
			buf.putInt(this.objType.getTypeId());
			buf.putInt(this.followsTerrain ? 1 : 0);
			buf.putAOVector(this.dir);
			buf.putLong(this.lastInterp);
			buf.flip();
			return buf;
		}

		public void setProperty(final String key, final Serializable val) {
			if (this.propMap == null) {
				this.propMap = new HashMap<String, Serializable>();
			}
			this.propMap.put(key, val);
		}

		public Serializable getProperty(final String key) {
			if (this.propMap == null) {
				return null;
			}
			return this.propMap.get(key);
		}
	}

	public static class RoadInfo implements ClientMessage, Serializable {
		private OID oid;
		private String name;
		private Point start;
		private Point end;
		private static final long serialVersionUID = 1L;

		public RoadInfo() {
		}

		public RoadInfo(final RoadSegment segment) {
			this.setOid(segment.getOid());
			this.setName(segment.getName());
			this.setStart(segment.getStart());
			this.setEnd(segment.getEnd());
		}

		public RoadInfo(final OID oid, final String name, final Point start, final Point end) {
			this.setOid(oid);
			this.setName(name);
			this.setStart(start);
			this.setEnd(end);
		}

		@Override
		public String toString() {
			return "[RoadInfo: oid=" + this.getOid() + ", name=" + this.getName() + ", start=" + this.getStart() + ", end=" + this.getEnd() + "]";
		}

		public OID getOid() {
			return this.oid;
		}

		public void setOid(final OID oid) {
			this.oid = oid;
		}

		public String getName() {
			return this.name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public Point getStart() {
			return this.start;
		}

		public void setStart(final Point start) {
			this.start = start;
		}

		public Point getEnd() {
			return this.end;
		}

		public void setEnd(final Point end) {
			this.end = end;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(200);
			buf.putOID(this.getOid());
			buf.putInt(54);
			buf.putString(this.getName());
			buf.putInt(2);
			buf.putPoint(this.start);
			buf.putPoint(this.end);
			buf.flip();
			return buf;
		}
	}

	public static class FreeRoadMessage extends TargetMessage implements ClientMessage, Serializable {
		private static final long serialVersionUID = 1L;

		public FreeRoadMessage() {
			super(WorldManagerClient.MSG_TYPE_FREE_ROAD);
		}

		public FreeRoadMessage(final OID oid) {
			super(WorldManagerClient.MSG_TYPE_FREE_ROAD, oid, oid);
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(20);
			buf.putOID(this.getTarget());
			buf.putInt(69);
			buf.flip();
			return buf;
		}
	}

	public static class TerrainReqMessage extends SubjectMessage {
		private static final long serialVersionUID = 1L;

		public TerrainReqMessage() {
			super(WorldManagerClient.MSG_TYPE_TERRAIN_REQ);
		}

		TerrainReqMessage(final OID oid) {
			super(WorldManagerClient.MSG_TYPE_TERRAIN_REQ, oid);
		}
	}

	public static class ObjInfoReqMessage extends SubjectMessage {
		private static final long serialVersionUID = 1L;

		public ObjInfoReqMessage() {
		}

		ObjInfoReqMessage(final OID oid) {
			super(WorldManagerClient.MSG_TYPE_OBJINFO_REQ, oid);
		}
	}

	public static class ObjInfoRespMessage extends ResponseMessage implements ITargetSessionId {
		private String targetSessionId;
		private ObjectInfo objInfo;
		private static final long serialVersionUID = 1L;

		public ObjInfoRespMessage() {
			this.objInfo = null;
		}

		public ObjInfoRespMessage(final Message msg, final String targetSessionId, final ObjectInfo objInfo) {
			super(msg);
			this.objInfo = null;
			this.setTargetSessionId(targetSessionId);
			this.setObjInfo(objInfo);
		}

		@Override
		public String getTargetSessionId() {
			return this.targetSessionId;
		}

		@Override
		public void setTargetSessionId(final String targetSessionId) {
			this.targetSessionId = targetSessionId;
		}

		public void setObjInfo(final ObjectInfo objInfo) {
			this.objInfo = objInfo;
		}

		public ObjectInfo getObjInfo() {
			return this.objInfo;
		}
	}

	public static class GetObjectsInMessage extends SubjectMessage {
		private Point loc;
		private Integer radius;
		private ObjectType objectType;
		private static final long serialVersionUID = 1L;

		public GetObjectsInMessage() {
		}

		public GetObjectsInMessage(final OID subjectOid, final Point loc, final Integer radius, final ObjectType objectType) {
			super(WorldManagerClient.MSG_TYPE_GET_OBJECTS_IN, subjectOid);
			this.loc = loc;
			this.radius = radius;
			this.objectType = objectType;
		}

		public Point getLoc() {
			return this.loc;
		}

		public Integer getRadius() {
			return this.radius;
		}

		public ObjectType getObjectType() {
			return this.objectType;
		}
	}

	public static class DisplayContextReqMessage extends SubjectMessage {
		private static final long serialVersionUID = 1L;

		public DisplayContextReqMessage() {
			super(WorldManagerClient.MSG_TYPE_DC_REQ);
		}

		DisplayContextReqMessage(final OID oid) {
			super(WorldManagerClient.MSG_TYPE_DC_REQ, oid);
		}
	}

	public static class SetAmbientLightMessage extends TargetMessage {
		private Color color;
		private static final long serialVersionUID = 1L;

		public SetAmbientLightMessage() {
			this.setMsgType(WorldManagerClient.MSG_TYPE_SET_AMBIENT);
		}

		public SetAmbientLightMessage(final OID oid, final Color color) {
			super(WorldManagerClient.MSG_TYPE_SET_AMBIENT, oid, oid);
			this.setColor(color);
		}

		public void setColor(final Color color) {
			this.color = color;
		}

		public Color getColor() {
			return this.color;
		}
	}

	public static class FogMessage extends TargetMessage implements EventParser {
		private FogRegionConfig fogConfig;
		private static final long serialVersionUID = 1L;

		public FogMessage() {
			super(WorldManagerClient.MSG_TYPE_FOG);
			this.fogConfig = null;
		}

		public FogMessage(final OID oid, final FogRegionConfig fogConfig) {
			super(WorldManagerClient.MSG_TYPE_FOG, oid, oid);
			this.fogConfig = null;
			this.setFogConfig(fogConfig);
		}

		public FogMessage(final OID oid, final Fog fog) {
			super(WorldManagerClient.MSG_TYPE_FOG, oid, oid);
			this.fogConfig = null;
			final FogRegionConfig config = new FogRegionConfig();
			config.setColor(fog.getColor());
			config.setNear(fog.getStart());
			config.setFar(fog.getEnd());
			this.setFogConfig(config);
		}

		public AOByteBuffer toBuffer() {
			final int msgId = Engine.getEventServer().getEventID(this.getClass());
			final AOByteBuffer buf = new AOByteBuffer(32);
			buf.putOID(null);
			buf.putInt(msgId);
			buf.putColor(this.fogConfig.getColor());
			buf.putInt(this.fogConfig.getNear());
			buf.putInt(this.fogConfig.getFar());
			buf.flip();
			return buf;
		}

		@Override
		public void parseBytes(final AOByteBuffer buf) {
			buf.getOID();
			buf.getInt();
			final FogRegionConfig config = new FogRegionConfig();
			config.setColor(buf.getColor());
			config.setNear(buf.getInt());
			config.setFar(buf.getInt());
		}

		public void setFogConfig(final FogRegionConfig fogConfig) {
			this.fogConfig = fogConfig;
		}

		public FogRegionConfig getFogConfig() {
			return this.fogConfig;
		}
	}

	public static class RoadMessage extends TargetMessage {
		private Set<Road> roads;
		private static final long serialVersionUID = 1L;

		public RoadMessage() {
			super(WorldManagerClient.MSG_TYPE_ROAD);
			this.roads = new HashSet<Road>();
		}

		public RoadMessage(final OID oid, final Set<Road> roads) {
			super(WorldManagerClient.MSG_TYPE_ROAD, oid, oid);
			this.roads = new HashSet<Road>();
			this.setRoads(roads);
		}

		public void setRoads(final Set<Road> roads) {
			this.roads = roads;
		}

		public Set<Road> getRoads() {
			return new HashSet<Road>(this.roads);
		}

		public List<AOByteBuffer> toBuffer() {
			final List<AOByteBuffer> bufList = new LinkedList<AOByteBuffer>();
			for (final Road road : this.roads) {
				final AOByteBuffer buf = new AOByteBuffer(1000);
				buf.putOID(road.getOid());
				buf.putInt(54);
				buf.putString(road.getName());
				final List<Point> points = road.getPoints();
				buf.putInt(points.size());
				for (final Point p : points) {
					buf.putPoint(p);
				}
				buf.putInt(road.getHalfWidth());
				buf.flip();
				bufList.add(buf);
			}
			return bufList;
		}
	}

	public static class SpawnReqMessage extends SubjectMessage implements BracketedMessage {
		private Message preMessage;
		private Message postMessage;
		private static final long serialVersionUID = 1L;

		public SpawnReqMessage() {
			super(WorldManagerClient.MSG_TYPE_SPAWN_REQ);
		}

		SpawnReqMessage(final OID oid) {
			super(WorldManagerClient.MSG_TYPE_SPAWN_REQ, oid);
		}

		@Override
		public Message getPreMessage() {
			return this.preMessage;
		}

		public void setPreMessage(final Message message) {
			this.preMessage = message;
		}

		@Override
		public Message getPostMessage() {
			return this.postMessage;
		}

		public void setPostMessage(final Message message) {
			this.postMessage = message;
		}
	}

	public static class SpawnedMessage extends SubjectMessage {
		private OID instanceOid;
		private ObjectType objectType;
		private static final long serialVersionUID = 1L;

		public SpawnedMessage() {
			super(WorldManagerClient.MSG_TYPE_SPAWNED);
		}

		public SpawnedMessage(final OID oid, final OID instanceOid, final ObjectType objectType) {
			super(WorldManagerClient.MSG_TYPE_SPAWNED, oid);
			this.setInstanceOid(instanceOid);
			this.setType(objectType);
		}

		public void setInstanceOid(final OID oid) {
			this.instanceOid = oid;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setType(final ObjectType type) {
			this.objectType = type;
		}

		public ObjectType getType() {
			return this.objectType;
		}
	}

	public static class DespawnReqMessage extends SubjectMessage implements BracketedMessage {
		private Message preMessage;
		private Message postMessage;
		private static final long serialVersionUID = 1L;

		public DespawnReqMessage() {
			super(WorldManagerClient.MSG_TYPE_DESPAWN_REQ);
		}

		DespawnReqMessage(final OID oid) {
			super(WorldManagerClient.MSG_TYPE_DESPAWN_REQ, oid);
		}

		@Override
		public Message getPreMessage() {
			return this.preMessage;
		}

		public void setPreMessage(final Message message) {
			this.preMessage = message;
		}

		@Override
		public Message getPostMessage() {
			return this.postMessage;
		}

		public void setPostMessage(final Message message) {
			this.postMessage = message;
		}
	}

	public static class DespawnedMessage extends SubjectMessage {
		private OID instanceOid;
		private ObjectType type;
		private static final long serialVersionUID = 1L;

		public DespawnedMessage() {
			super(WorldManagerClient.MSG_TYPE_DESPAWNED);
		}

		public DespawnedMessage(final OID oid, final OID instanceOid, final ObjectType type) {
			super(WorldManagerClient.MSG_TYPE_DESPAWNED, oid);
			this.setInstanceOid(instanceOid);
			this.setType(type);
		}

		public void setInstanceOid(final OID oid) {
			this.instanceOid = oid;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setType(final ObjectType type) {
			this.type = type;
		}

		public ObjectType getType() {
			return this.type;
		}
	}

	public static class SetWorldNodeReqMessage extends SubjectMessage {
		private BasicWorldNode wnode;
		private int flags;
		private static final long serialVersionUID = 1L;

		public SetWorldNodeReqMessage() {
			super(WorldManagerClient.MSG_TYPE_SETWNODE_REQ);
			this.wnode = null;
		}

		SetWorldNodeReqMessage(final OID oid, final BasicWorldNode wnode, final int flags) {
			super(WorldManagerClient.MSG_TYPE_SETWNODE_REQ, oid);
			this.wnode = null;
			this.setWorldNode(wnode);
			this.setFlags(flags);
		}

		public void setWorldNode(final BasicWorldNode wnode) {
			this.wnode = wnode;
		}

		public BasicWorldNode getWorldNode() {
			return this.wnode;
		}

		public void setFlags(final int flags) {
			this.flags = flags;
		}

		public int getFlags() {
			return this.flags;
		}
	}

	public static class UpdateWorldNodeReqMessage extends SubjectMessage implements BracketedMessage {
		private Message preMessage;
		private Message postMessage;
		private BasicWorldNode wnode;
		protected boolean override;
		private static final long serialVersionUID = 1L;

		public UpdateWorldNodeReqMessage() {
			this.wnode = null;
			this.override = false;
		}

		public UpdateWorldNodeReqMessage(final OID oid, final BasicWorldNode wnode) {
			super(WorldManagerClient.MSG_TYPE_UPDATEWNODE_REQ, oid);
			this.wnode = null;
			this.override = false;
			this.setWorldNode(wnode);
		}

		public void setWorldNode(final BasicWorldNode wnode) {
			this.wnode = wnode;
		}

		public BasicWorldNode getWorldNode() {
			return this.wnode;
		}

		public void setOverride(final boolean override) {
			this.override = override;
		}

		public boolean getOverride() {
			return this.override;
		}

		@Override
		public Message getPreMessage() {
			return this.preMessage;
		}

		public void setPreMessage(final Message message) {
			this.preMessage = message;
		}

		@Override
		public Message getPostMessage() {
			return this.postMessage;
		}

		public void setPostMessage(final Message message) {
			this.postMessage = message;
		}
	}

	public static class UpdateWorldNodeMessage extends SubjectMessage implements ClientMessage {
		private BasicWorldNode wnode;
		transient AOByteBuffer eventBuf;
		private static final long serialVersionUID = 1L;

		public UpdateWorldNodeMessage() {
			super(WorldManagerClient.MSG_TYPE_UPDATEWNODE);
			this.wnode = null;
		}

		public UpdateWorldNodeMessage(final OID oid, final BasicWorldNode wnode) {
			super(WorldManagerClient.MSG_TYPE_UPDATEWNODE, oid);
			this.wnode = null;
			this.setWorldNode(wnode);
		}

		@Override
		public String toString() {
			return "[UpdateWorldNodeMessage " + this.getWorldNode() + "]";
		}

		public void setWorldNode(final BasicWorldNode wnode) {
			this.wnode = wnode;
		}

		public BasicWorldNode getWorldNode() {
			return this.wnode;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final BasicWorldNode bnode = this.getWorldNode();
			final AOByteBuffer buf = new AOByteBuffer(64);
			buf.putOID(this.getSubject());
			buf.putInt(2);
			buf.putLong(System.currentTimeMillis());
			buf.putAOVector(bnode.getDir());
			buf.putPoint(bnode.getLoc());
			buf.flip();
			return buf;
		}

		public void setEventBuf(final AOByteBuffer buf) {
			this.eventBuf = buf;
		}

		public AOByteBuffer getEventBuf() {
			return this.eventBuf;
		}
	}

	public static class DirLocOrientMessage extends SubjectMessage implements ClientMessage {
		private BasicWorldNode wnode;
		private static final long serialVersionUID = 1L;

		public DirLocOrientMessage() {
			super(WorldManagerClient.MSG_TYPE_DIR_LOC_ORIENT);
			this.wnode = null;
		}

		public DirLocOrientMessage(final OID oid, final BasicWorldNode wnode) {
			super(WorldManagerClient.MSG_TYPE_DIR_LOC_ORIENT, oid);
			this.wnode = null;
			this.setWorldNode(wnode);
		}

		@Override
		public String toString() {
			return "[DirLocOrient oid=" + this.getSubject() + ", wnode=" + this.getWorldNode() + "]";
		}

		public void setWorldNode(final BasicWorldNode wnode) {
			this.wnode = wnode;
		}

		public BasicWorldNode getWorldNode() {
			return this.wnode;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final BasicWorldNode bnode = this.getWorldNode();
			final AOByteBuffer buf = new AOByteBuffer(128);
			buf.putOID(this.getSubject());
			buf.putInt(79);
			buf.putLong(System.currentTimeMillis());
			final AOVector dir = bnode.getDir();
			buf.putAOVector((dir == null) ? new AOVector() : dir);
			final Point loc = bnode.getLoc();
			buf.putPoint((loc == null) ? new Point() : loc);
			final Quaternion q = bnode.getOrientation();
			buf.putQuaternion((q == null) ? new Quaternion() : q);
			buf.flip();
			return buf;
		}
	}

	public static class WorldNodeCorrectMessage extends SubjectMessage implements ClientMessage {
		private BasicWorldNode wnode;
		private static final long serialVersionUID = 1L;

		public WorldNodeCorrectMessage() {
			this.wnode = null;
			this.setMsgType(WorldManagerClient.MSG_TYPE_WNODECORRECT);
		}

		public WorldNodeCorrectMessage(final OID oid, final BasicWorldNode wnode) {
			super(WorldManagerClient.MSG_TYPE_WNODECORRECT, oid);
			this.wnode = null;
			this.setWorldNode(wnode);
		}

		@Override
		public String toString() {
			return "[WorldNodeCorrectMessage oid=" + this.getSubject() + ", wnode=" + this.getWorldNode() + "]";
		}

		public void setWorldNode(final BasicWorldNode wnode) {
			this.wnode = wnode;
		}

		public BasicWorldNode getWorldNode() {
			return this.wnode;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(128);
			buf.putOID(this.getSubject());
			buf.putInt(79);
			buf.putLong(System.currentTimeMillis());
			final AOVector dir = this.wnode.getDir();
			buf.putAOVector((dir == null) ? new AOVector() : dir);
			final Point loc = this.wnode.getLoc();
			buf.putPoint((loc == null) ? new Point() : loc);
			final Quaternion q = this.wnode.getOrientation();
			buf.putQuaternion((q == null) ? new Quaternion() : q);
			buf.flip();
			return buf;
		}

		public void fromBuffer(final AOByteBuffer buf) {
			final OID oid = buf.getOID();
			final int msgNumber = buf.getInt();
			if (msgNumber != 79) {
				Log.error("WorldNodeCorrectMessage.fromBuffer: msgNumber " + msgNumber + " is not 79");
				return;
			}
			buf.getLong();
			final AOVector dir = buf.getAOVector();
			final Point loc = buf.getPoint();
			final Quaternion orient = buf.getQuaternion();
			final BasicWorldNode wnode = new BasicWorldNode();
			wnode.setDir(dir);
			wnode.setLoc(loc);
			wnode.setOrientation(orient);
			this.setWorldNode(wnode);
			this.setSubject(oid);
		}
	}

	public static class ReparentWNodeReqMessage extends SubjectMessage {
		private OID parentOid;
		private static final long serialVersionUID = 1L;

		public ReparentWNodeReqMessage() {
			super(WorldManagerClient.MSG_TYPE_REPARENT_WNODE_REQ);
		}

		public ReparentWNodeReqMessage(final OID oid, final OID parentOid) {
			super(WorldManagerClient.MSG_TYPE_REPARENT_WNODE_REQ, oid);
			this.setParentOid(parentOid);
		}

		@Override
		public String toString() {
			return "[ReparentWNodeReqMessage oid= + getSubject(), parent=" + this.parentOid + "]";
		}

		public void setParentOid(final OID parentOid) {
			this.parentOid = parentOid;
		}

		public OID getParentOid() {
			return this.parentOid;
		}
	}

	public static class PerceptionInfo {
		public ObjectInfo objectInfo;
		public DisplayContext displayContext;

		public PerceptionInfo() {
		}

		public PerceptionInfo(final ObjectInfo info) {
			this.objectInfo = info;
		}
	}

	public static class NewRemoteObjectMessage extends SubjectMessage implements ITargetSessionId {
		private OID instanceOid;
		private Point loc;
		private Quaternion orient;
		private ObjectType type;
		int perceptionRadius;
		private String targetSessionId;
		private static final long serialVersionUID = 1L;

		public NewRemoteObjectMessage() {
			super(WorldManagerClient.MSG_TYPE_NEW_REMOTE_OBJ);
		}

		public NewRemoteObjectMessage(final String targetSessionId, final OID instanceOid, final OID newObjId, final Point loc, final Quaternion orient, final int perceptionRadius,
				final ObjectType type) {
			super(WorldManagerClient.MSG_TYPE_NEW_REMOTE_OBJ, newObjId);
			this.setTargetSessionId(targetSessionId);
			this.setInstanceOid(instanceOid);
			this.setLoc(loc);
			this.setOrient(orient);
			this.setPerceptionRadius(perceptionRadius);
			this.setType(type);
		}

		public void setInstanceOid(final OID oid) {
			this.instanceOid = oid;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setLoc(final Point loc) {
			this.loc = loc;
		}

		public Point getLoc() {
			return this.loc;
		}

		public void setOrient(final Quaternion orient) {
			this.orient = orient;
		}

		public Quaternion getOrient() {
			return this.orient;
		}

		public void setPerceptionRadius(final int perceptionRadius) {
			this.perceptionRadius = perceptionRadius;
		}

		public int getPerceptionRadius() {
			return this.perceptionRadius;
		}

		public void setType(final ObjectType type) {
			this.type = type;
		}

		public ObjectType getType() {
			return this.type;
		}

		@Override
		public String getTargetSessionId() {
			return this.targetSessionId;
		}

		@Override
		public void setTargetSessionId(final String targetSessionId) {
			this.targetSessionId = targetSessionId;
		}
	}

	public static class FreeRemoteObjectMessage extends SubjectMessage implements ITargetSessionId {
		private OID instanceOid;
		private String targetSessionId;
		private static final long serialVersionUID = 1L;

		public FreeRemoteObjectMessage() {
			super(WorldManagerClient.MSG_TYPE_FREE_REMOTE_OBJ);
		}

		public FreeRemoteObjectMessage(final String targetSessionId, final OID instanceOid, final OID objId) {
			super(WorldManagerClient.MSG_TYPE_FREE_REMOTE_OBJ, objId);
			this.setTargetSessionId(targetSessionId);
			this.setInstanceOid(instanceOid);
		}

		public void setInstanceOid(final OID oid) {
			this.instanceOid = oid;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		@Override
		public String getTargetSessionId() {
			return this.targetSessionId;
		}

		@Override
		public void setTargetSessionId(final String targetSessionId) {
			this.targetSessionId = targetSessionId;
		}
	}

	public static class UpdateMessage extends SubjectMessage implements HasTarget {
		OID target;
		private static final long serialVersionUID = 1L;

		public UpdateMessage() {
			super(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
		}

		UpdateMessage(final OID notifyOid, final OID updateOid) {
			super(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT, updateOid);
			this.target = notifyOid;
			if (notifyOid == null || updateOid == null) {
				throw new RuntimeException("null oid");
			}
		}

		@Override
		public OID getTarget() {
			return this.target;
		}

		@Override
		public void setTarget(final OID target) {
			this.target = target;
		}
	}

	public static class DisplayContextMessage extends SubjectMessage {
		private DisplayContext dc;
		private boolean forceInstantLoad;
		private static final long serialVersionUID = 1L;

		public DisplayContextMessage() {
			super(WorldManagerClient.MSG_TYPE_DISPLAY_CONTEXT);
			this.forceInstantLoad = false;
		}

		public DisplayContextMessage(final OID dcObjOid, final DisplayContext dc) {
			super(WorldManagerClient.MSG_TYPE_DISPLAY_CONTEXT, dcObjOid);
			this.forceInstantLoad = false;
			this.setDisplayContext(dc);
		}

		public DisplayContext getDisplayContext() {
			return this.dc;
		}

		public void setDisplayContext(final DisplayContext dc) {
			this.dc = dc;
		}

		public boolean getForceInstantLoad() {
			return this.forceInstantLoad;
		}

		public void setForceInstantLoad(final boolean forceInstantLoad) {
			this.forceInstantLoad = forceInstantLoad;
		}
	}

	public static class DetachMessage extends SubjectMessage {
		private String socketName;
		private OID objBeingDetached;
		private static final long serialVersionUID = 1L;

		public DetachMessage() {
			super(WorldManagerClient.MSG_TYPE_DETACH);
			this.objBeingDetached = null;
		}

		public DetachMessage(final OID dcObjOid, final OID objBeingDetached, final String socketName) {
			super(WorldManagerClient.MSG_TYPE_DETACH, dcObjOid);
			this.objBeingDetached = null;
			this.setSocketName(socketName);
			this.setObjBeingDetached(objBeingDetached);
		}

		public String getSocketName() {
			return this.socketName;
		}

		public void setSocketName(final String socket) {
			this.socketName = socket;
		}

		public OID getObjBeingDetached() {
			return this.objBeingDetached;
		}

		public void setObjBeingDetached(final OID oid) {
			this.objBeingDetached = oid;
		}
	}

	public static class ComReqMessage extends SubjectMessage {
		private String chatterName;
		int channel;
		private String msgString;
		private static final long serialVersionUID = 1L;

		public ComReqMessage() {
			this.channel = -1;
		}

		public ComReqMessage(final OID objOid, final String chatterName, final int channel, final String msgString) {
			super(WorldManagerClient.MSG_TYPE_COM_REQ, objOid);
			this.channel = -1;
			this.setChatterName(chatterName);
			this.setChannel(channel);
			this.setString(msgString);
		}

		public String getChatterName() {
			return this.chatterName;
		}

		public void setChatterName(final String chatterName) {
			this.chatterName = chatterName;
		}

		public String getString() {
			return this.msgString;
		}

		public void setString(final String msgString) {
			this.msgString = msgString;
		}

		public int getChannel() {
			return this.channel;
		}

		public void setChannel(final int channel) {
			this.channel = channel;
		}
	}

	public static class ComMessage extends SubjectMessage implements ClientMessage {
		private String chatterName;
		int channel;
		private String msgString;
		private static final long serialVersionUID = 1L;

		public ComMessage() {
			super(WorldManagerClient.MSG_TYPE_COM);
			this.channel = -1;
		}

		public ComMessage(final OID objOid, final String chatterName, final int channel, final String msgString) {
			super(WorldManagerClient.MSG_TYPE_COM, objOid);
			this.channel = -1;
			this.setChatterName(chatterName);
			this.setChannel(channel);
			this.setString(msgString);
		}

		@Override
		public String toString() {
			return "[ComMessage: objOid=" + this.getSubject() + ", channel=" + this.getChannel() + ", msg=" + this.getString() + "]";
		}

		public String getChatterName() {
			return this.chatterName;
		}

		public void setChatterName(final String chatterName) {
			this.chatterName = chatterName;
		}

		public String getString() {
			return this.msgString;
		}

		public void setString(final String msgString) {
			this.msgString = msgString;
		}

		public int getChannel() {
			return this.channel;
		}

		public void setChannel(final int channel) {
			this.channel = channel;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(400);
			buf.putOID(this.getSubject());
			buf.putInt(3);
			buf.putString(this.getChatterName());
			buf.putInt(this.getChannel());
			buf.putString(this.getString());
			buf.flip();
			return buf;
		}

		public void fromBuffer(final AOByteBuffer buf) {
			buf.getOID();
			final int msgNumber = buf.getInt();
			if (msgNumber != 3) {
				Log.error("ComMessage.fromBuffer: msgNumber " + msgNumber + " is not 3");
				return;
			}
			this.channel = buf.getInt();
			this.msgString = buf.getString();
		}
	}

	public static class TargetedComMessage extends TargetMessage implements ClientMessage {
		private String chatterName;
		int channel;
		private String msgString;
		private static final long serialVersionUID = 1L;

		public TargetedComMessage() {
			super(WorldManagerClient.MSG_TYPE_COM);
			this.channel = -1;
		}

		public TargetedComMessage(final OID targetOid, final OID subjectOid, final String chatterName, final int channel, final String msgString) {
			super(WorldManagerClient.MSG_TYPE_COM, targetOid, subjectOid);
			this.channel = -1;
			this.setChatterName(chatterName);
			this.setChannel(channel);
			this.setString(msgString);
		}

		@Override
		public String toString() {
			return "[ComMessage: targetOid=" + this.getTarget() + ", subjectOid=" + this.getSubject() + ", channel=" + this.getChannel() + ", msg=" + this.getString() + "]";
		}

		public String getChatterName() {
			return this.chatterName;
		}

		public void setChatterName(final String chatterName) {
			this.chatterName = chatterName;
		}

		public String getString() {
			return this.msgString;
		}

		public void setString(final String msgString) {
			this.msgString = msgString;
		}

		public int getChannel() {
			return this.channel;
		}

		public void setChannel(final int channel) {
			this.channel = channel;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(400);
			buf.putOID(this.getSubject());
			buf.putInt(3);
			buf.putString(this.getChatterName());
			buf.putInt(this.getChannel());
			buf.putString(this.getString());
			buf.flip();
			return buf;
		}

		public void fromBuffer(final AOByteBuffer buf) {
			this.subject = buf.getOID();
			final int msgNumber = buf.getInt();
			if (msgNumber != 3) {
				Log.error("ComMessage.fromBuffer: msgNumber " + msgNumber + " is not 3");
				return;
			}
			this.chatterName = buf.getString();
			this.channel = buf.getInt();
			this.msgString = buf.getString();
		}
	}

	public static class SysChatMessage extends Message implements ClientMessage {
		private String msgString;
		private static final long serialVersionUID = 1L;

		public SysChatMessage() {
			super(WorldManagerClient.MSG_TYPE_SYS_CHAT);
		}

		public SysChatMessage(final String msgString) {
			super(WorldManagerClient.MSG_TYPE_SYS_CHAT);
			this.setString(msgString);
		}

		public String getString() {
			return this.msgString;
		}

		public void setString(final String msgString) {
			this.msgString = msgString;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(200);
			buf.putOID(null);
			buf.putInt(3);
			buf.putInt(0);
			buf.putString(this.getString());
			buf.flip();
			return buf;
		}
	}

	public static class OrientReqMessage extends SubjectMessage {
		Quaternion q;
		private static final long serialVersionUID = 1L;

		public OrientReqMessage() {
			super(WorldManagerClient.MSG_TYPE_ORIENT_REQ);
		}

		public OrientReqMessage(final OID objOid, final Quaternion q) {
			super(WorldManagerClient.MSG_TYPE_ORIENT_REQ, objOid);
			this.setQuaternion(q);
		}

		public Quaternion getQuaternion() {
			return this.q;
		}

		public void setQuaternion(final Quaternion q) {
			this.q = q;
		}
	}

	public static class OrientMessage extends SubjectMessage implements ClientMessage {
		private Quaternion q;
		private static final long serialVersionUID = 1L;

		public OrientMessage() {
			super(WorldManagerClient.MSG_TYPE_ORIENT);
		}

		public OrientMessage(final OID objOid, final Quaternion q) {
			super(WorldManagerClient.MSG_TYPE_ORIENT, objOid);
			this.setQuaternion(q);
		}

		public Quaternion getQuaternion() {
			return this.q;
		}

		public void setQuaternion(final Quaternion q) {
			this.q = q;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(32);
			buf.putOID(this.getSubject());
			buf.putInt(9);
			buf.putQuaternion(this.getQuaternion());
			buf.flip();
			return buf;
		}
	}

	public static class AnimationMessage extends SubjectMessage {
		private LinkedList<AnimationCommand> animationList;
		protected transient Lock lock;
		private static final long serialVersionUID = 1L;

		public AnimationMessage() {
			super(WorldManagerClient.MSG_TYPE_ANIMATION);
			this.lock = null;
			this.setupTransient();
		}

		public AnimationMessage(final OID objOid, final OID notifyOid, final AnimationCommand anim) {
			super(WorldManagerClient.MSG_TYPE_ANIMATION, objOid);
			this.lock = null;
			this.setupTransient();
			final List<AnimationCommand> l = new LinkedList<AnimationCommand>();
			l.add(anim);
			this.setAnimationList(l);
		}

		public AnimationMessage(final OID objOid, final List<AnimationCommand> animList) {
			super(WorldManagerClient.MSG_TYPE_ANIMATION, objOid);
			this.lock = null;
			this.setupTransient();
			this.setAnimationList(animList);
		}

		public List<AnimationCommand> getAnimationList() {
			this.lock.lock();
			try {
				return new LinkedList<AnimationCommand>(this.animationList);
			} finally {
				this.lock.unlock();
			}
		}

		public void setAnimationList(final List<AnimationCommand> animList) {
			this.lock.lock();
			try {
				this.animationList = new LinkedList<AnimationCommand>(animList);
			} finally {
				this.lock.unlock();
			}
		}

		void setupTransient() {
			this.lock = LockFactory.makeLock("AnimationMessageLock");
		}
	}

	public static class TargetedPropertyMessage extends TargetMessage implements Serializable, IPropertyMessage {
		protected transient Lock lock;
		Map<String, Serializable> propertyMap;
		protected Collection<String> removedProperties;
		private static final long serialVersionUID = 1L;

		public TargetedPropertyMessage() {
			super(WorldManagerClient.MSG_TYPE_TARGETED_PROPERTY);
			this.lock = null;
			this.propertyMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
			this.setupTransient();
		}

		public TargetedPropertyMessage(final MessageType msgType) {
			super(msgType);
			this.lock = null;
			this.propertyMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
			this.setupTransient();
		}

		public TargetedPropertyMessage(final MessageType msgType, final OID target) {
			super(msgType, target);
			this.lock = null;
			this.propertyMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
			this.setupTransient();
		}

		public TargetedPropertyMessage(final OID target, final OID subject) {
			super(WorldManagerClient.MSG_TYPE_TARGETED_PROPERTY, target, subject);
			this.lock = null;
			this.propertyMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
			this.setupTransient();
		}

		public TargetedPropertyMessage(final MessageType msgType, final OID target, final OID subject) {
			super(msgType, target, subject);
			this.lock = null;
			this.propertyMap = new HashMap<String, Serializable>();
			this.removedProperties = new HashSet<String>();
			this.setupTransient();
		}

		public void put(final String key, final Serializable val) {
			this.setProperty(key, val);
		}

		@Override
		public void setProperty(final String key, final Serializable val) {
			this.lock.lock();
			try {
				this.propertyMap.put(key, val);
				this.removedProperties.remove(key);
			} finally {
				this.lock.unlock();
			}
		}

		public void setProperty(final String key, final Serializable val, final boolean clone) {
			if (!clone) {
				this.setProperty(key, val);
				return;
			}
			this.lock.lock();
			try {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(val);
				final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				final ObjectInputStream ois = new ObjectInputStream(bais);
				final Serializable valDeepCopy = (Serializable) ois.readObject();
				this.propertyMap.put(key, valDeepCopy);
				this.removedProperties.remove(key);
			} catch (ClassNotFoundException e) {
				this.propertyMap.put(key, null);
				this.removedProperties.remove(key);
			} catch (IOException e2) {
				this.propertyMap.put(key, null);
				this.removedProperties.remove(key);
			} finally {
				this.lock.unlock();
			}
		}

		public Serializable get(final String key) {
			return this.getProperty(key);
		}

		@Override
		public Serializable getProperty(final String key) {
			this.lock.lock();
			try {
				return this.propertyMap.get(key);
			} finally {
				this.lock.unlock();
			}
		}

		@Override
		public void removeProperty(final String key) {
			this.lock.lock();
			try {
				this.propertyMap.remove(key);
				this.removedProperties.add(key);
			} finally {
				this.lock.unlock();
			}
		}

		public Set<String> keySet() {
			this.lock.lock();
			try {
				return this.propertyMap.keySet();
			} finally {
				this.lock.unlock();
			}
		}

		public Map<String, Serializable> getPropertyMapRef() {
			return this.propertyMap;
		}

		public AOByteBuffer toBuffer(final String version) {
			return this.toBufferInternal(version, null);
		}

		public AOByteBuffer toBuffer(final String version, final Set<String> filteredProps) {
			return this.toBufferInternal(version, filteredProps);
		}

		public AOByteBuffer toBufferInternal(final String version, final Set<String> filteredProps) {
			this.lock.lock();
			try {
				final AOByteBuffer buf = new AOByteBuffer(2048);
				buf.putOID(this.getSubject());
				buf.putInt(62);
				buf.putFilteredPropertyMap(this.propertyMap, filteredProps);
				buf.putFilteredPropertyCollection(this.removedProperties, filteredProps);
				buf.flip();
				return buf;
			} finally {
				this.lock.unlock();
			}
		}

		void setupTransient() {
			this.lock = LockFactory.makeLock("TargetedPropertyMessageLock");
		}
	}

	public static class ExtensionMessage extends PropertyMessage {
		private static final long serialVersionUID = 1L;

		public ExtensionMessage() {
			super(WorldManagerClient.MSG_TYPE_EXTENSION);
		}

		public ExtensionMessage(final OID objOid) {
			super(WorldManagerClient.MSG_TYPE_EXTENSION, objOid);
		}

		public ExtensionMessage(final MessageType msgType, final String subType, final OID objOid) {
			super(msgType, objOid);
			this.setExtensionType(subType);
		}

		public ExtensionMessage(final MessageType msgType, final OID objOid, final Map<String, Serializable> propertyMap) {
			super(msgType, objOid);
			this.propertyMap = propertyMap;
		}

		public void setExtensionType(final String type) {
			this.setProperty("ext_msg_subtype", type);
		}

		public String getExtensionType() {
			return (String) this.getProperty("ext_msg_subtype");
		}

		@Override
		public AOByteBuffer toBuffer(final String version) {
			this.lock.lock();
			try {
				final AOByteBuffer buf = new AOByteBuffer(2048);
				buf.putOID(this.getSubject());
				buf.putInt(83);
				final byte flags = 0;
				buf.putByte(flags);
				buf.putPropertyMap(this.propertyMap);
				buf.flip();
				return buf;
			} finally {
				this.lock.unlock();
			}
		}
	}

	public static class TargetedExtensionMessage extends TargetedPropertyMessage {
		private boolean clientTargeted;
		private static final long serialVersionUID = 1L;

		public TargetedExtensionMessage() {
			super(WorldManagerClient.MSG_TYPE_EXTENSION);
			this.clientTargeted = false;
		}

		public TargetedExtensionMessage(final OID target) {
			super(WorldManagerClient.MSG_TYPE_EXTENSION, target);
			this.clientTargeted = false;
		}

		public TargetedExtensionMessage(final OID target, final OID subject) {
			super(WorldManagerClient.MSG_TYPE_EXTENSION, target, subject);
			this.clientTargeted = false;
		}

		public TargetedExtensionMessage(final String subType, final OID target) {
			super(WorldManagerClient.MSG_TYPE_EXTENSION, target);
			this.clientTargeted = false;
			this.setExtensionType(subType);
		}

		public TargetedExtensionMessage(final MessageType msgType, final String subType, final OID target, final OID subject) {
			super(msgType, target, subject);
			this.clientTargeted = false;
			if (subType != null) {
				this.setExtensionType(subType);
			}
		}

		public TargetedExtensionMessage(final MessageType msgType, final OID target, final OID subject, final Boolean clientTargeted, final Map<String, Serializable> propertyMap) {
			super(msgType, target, subject);
			this.clientTargeted = false;
			this.clientTargeted = clientTargeted;
			this.propertyMap = propertyMap;
		}

		public void setExtensionType(final String type) {
			this.setProperty("ext_msg_subtype", type);
		}

		public String getExtensionType() {
			return (String) this.getProperty("ext_msg_subtype");
		}

		public Set<String> getKeys() {
			return this.propertyMap.keySet();
		}

		@Override
		public AOByteBuffer toBuffer(final String version) {
			this.lock.lock();
			try {
				final AOByteBuffer buf = new AOByteBuffer(2048);
				buf.putOID(this.getSubject());
				buf.putInt(83);
				final OID oid = this.getTarget();
				final byte flags = (byte) (((oid != null) ? 1 : 0) | (this.clientTargeted ? 2 : 0));
				buf.putByte(flags);
				if (oid != null) {
					buf.putOID(oid);
				}
				buf.putPropertyMap(this.propertyMap);
				buf.flip();
				return buf;
			} finally {
				this.lock.unlock();
			}
		}
	}

	public static class RefreshWNodeMessage extends SubjectMessage {
		private static final long serialVersionUID = 1L;

		public RefreshWNodeMessage() {
			super(WorldManagerClient.MSG_TYPE_REFRESH_WNODE);
		}

		public RefreshWNodeMessage(final OID objOid) {
			super(WorldManagerClient.MSG_TYPE_REFRESH_WNODE, objOid);
		}
	}

	public static class PerceiverRegionsMessage extends Message implements ITargetSessionId {
		private OID instanceOid;
		private Geometry region;
		private String targetSessionId;
		private static final long serialVersionUID = 1L;

		public PerceiverRegionsMessage() {
			super(WorldManagerClient.MSG_TYPE_PERCEIVER_REGIONS);
			this.region = null;
		}

		public PerceiverRegionsMessage(final OID instanceOid2, final Geometry region) {
			this.region = null;
			this.setMsgType(WorldManagerClient.MSG_TYPE_PERCEIVER_REGIONS);
			this.setInstanceOid(instanceOid2);
			this.setRegion(region);
		}

		public void setInstanceOid(final OID oid) {
			this.instanceOid = oid;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setRegion(final Geometry g) {
			this.region = g;
		}

		public Geometry getRegion() {
			return this.region;
		}

		@Override
		public String getTargetSessionId() {
			return this.targetSessionId;
		}

		@Override
		public void setTargetSessionId(final String targetSessionId) {
			this.targetSessionId = targetSessionId;
		}
	}

	public static class NewRegionMessage extends Message {
		private OID instanceOid;
		private Region region;
		private static final long serialVersionUID = 1L;

		public NewRegionMessage() {
			super(WorldManagerClient.MSG_TYPE_NEW_REGION);
		}

		public NewRegionMessage(final OID instanceOid, final Region region) {
			super(WorldManagerClient.MSG_TYPE_NEW_REGION);
			this.setInstanceOid(instanceOid);
			this.setRegion(region);
		}

		public void setInstanceOid(final OID oid) {
			this.instanceOid = oid;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setRegion(final Region r) {
			this.region = r;
		}

		public Region getRegion() {
			return this.region;
		}
	}

	public static class SoundMessage extends SubjectMessage implements ClientMessage, HasTarget {
		public static final byte soundTypePoint = 1;
		public static final byte soundTypeAmbient = 2;
		byte soundType;
		List<SoundData> soundData;
		List<String> soundOff;
		private boolean clearFlag;
		private OID target;
		private static final long serialVersionUID = 1L;

		public SoundMessage() {
			super(WorldManagerClient.MSG_TYPE_SOUND);
			this.soundData = null;
			this.soundOff = null;
			this.target = null;
		}

		public SoundMessage(final OID oid) {
			super(WorldManagerClient.MSG_TYPE_SOUND, oid);
			this.soundData = null;
			this.soundOff = null;
			this.target = null;
			this.setType((byte) 1);
		}

		@Override
		public OID getTarget() {
			return this.target;
		}

		@Override
		public void setTarget(final OID target) {
			this.target = target;
		}

		public void setSoundData(final List<SoundData> soundData) {
			this.soundData = new LinkedList<SoundData>(soundData);
		}

		public List<SoundData> getSoundData() {
			return this.soundData;
		}

		public void addSound(final SoundData data) {
			if (this.soundData == null) {
				this.soundData = new LinkedList<SoundData>();
			}
			this.soundData.add(data);
		}

		public void setType(final byte type) {
			this.soundType = type;
		}

		public byte getType() {
			return this.soundType;
		}

		public void setClearFlag(final boolean val) {
			this.clearFlag = val;
		}

		public boolean getClearFlag() {
			return this.clearFlag;
		}

		public void addSound(final String fileName, final boolean looping) {
			this.addSound(fileName, looping, 1.0f);
		}

		public void addSound(final String fileName, final boolean looping, final float gain) {
			if (this.soundData == null) {
				this.soundData = new LinkedList<SoundData>();
			}
			final HashMap<String, String> properties = new HashMap<String, String>();
			if (looping) {
				properties.put("Loop", "true");
			} else {
				properties.put("Loop", "false");
			}
			properties.put("Gain", "" + gain);
			final SoundData data = new SoundData(fileName, "Positional", properties);
			this.soundData.add(data);
		}

		public void removeSound(final String fileName) {
			if (this.soundOff == null) {
				this.soundOff = new LinkedList<String>();
			}
			this.soundOff.add(fileName);
		}

		@Override
		public String toString() {
			String typeStr = "POINT";
			if (this.soundType == 2) {
				typeStr = "AMBIENT";
			}
			return "[SoundMessage: OID=" + this.getSubject() + ", TYPE=" + typeStr + ", ON=" + this.soundData + ", OFF=" + this.soundOff + ", CLEAR=" + this.clearFlag;
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(400);
			if (this.soundType == 2) {
				buf.putOID(null);
			} else {
				buf.putOID(this.getSubject());
			}
			buf.putInt(78);
			if (Log.loggingDebug) {
				Log.debug("sending SoundControl: " + this);
			}
			try {
				final boolean cflag = this.getClearFlag();
				int numEntries = 0;
				if (this.soundData != null) {
					numEntries = this.soundData.size();
				}
				if (cflag) {
					++numEntries;
				} else if (this.soundOff != null) {
					numEntries += this.soundOff.size();
				}
				buf.putInt(numEntries);
				if (cflag) {
					buf.putString("clear");
				} else if (this.soundOff != null) {
					for (final String fileName : this.soundOff) {
						buf.putString("off");
						buf.putString(fileName);
					}
				}
				if (this.soundData != null) {
					for (final SoundData data : this.soundData) {
						buf.putString("on");
						buf.putString(data.getFileName());
						final Map<String, Serializable> props = new HashMap<String, Serializable>(data.getProperties());
						buf.putPropertyMap(props);
					}
				}
			} finally {
			}
			buf.flip();
			return buf;
		}
	}

	public static class ModifyDisplayContextMessage extends SubjectMessage {
		String handle;
		DisplayContext childDC;
		byte action;
		String base;
		List<DisplayContext.Submesh> submeshes;
		transient Lock lock;
		private static final long serialVersionUID = 1L;

		public ModifyDisplayContextMessage() {
			super(WorldManagerClient.MSG_TYPE_MODIFY_DC);
			this.childDC = null;
			this.base = null;
			this.lock = LockFactory.makeLock("ModifyDCMsgLock");
		}

		public ModifyDisplayContextMessage(final OID oid, final byte action, final String base, final Collection<DisplayContext.Submesh> submeshes, final String childDCHandle,
				final DisplayContext childDC) {
			super(WorldManagerClient.MSG_TYPE_MODIFY_DC, oid);
			this.childDC = null;
			this.base = null;
			this.lock = LockFactory.makeLock("ModifyDCMsgLock");
			this.setAction(action);
			this.setBase(base);
			this.setSubmeshes(submeshes);
			this.setChildDCHandle(childDCHandle);
			this.setChildDC(childDC);
		}

		public ModifyDisplayContextMessage(final OID oid, final byte action, final Collection<DisplayContext.Submesh> submeshes) {
			super(WorldManagerClient.MSG_TYPE_MODIFY_DC, oid);
			this.childDC = null;
			this.base = null;
			this.lock = LockFactory.makeLock("ModifyDCMsgLock");
			this.setAction(action);
			this.setSubmeshes(submeshes);
		}

		public ModifyDisplayContextMessage(final OID oid, final byte action, final DisplayContext.Submesh submesh) {
			super(WorldManagerClient.MSG_TYPE_MODIFY_DC, oid);
			this.childDC = null;
			this.base = null;
			this.lock = LockFactory.makeLock("ModifyDCMsgLock");
			this.setAction(action);
			final List<DisplayContext.Submesh> l = new LinkedList<DisplayContext.Submesh>();
			l.add(submesh);
			this.submeshes = l;
		}

		public void setAction(final byte action) {
			this.action = action;
		}

		public byte getAction() {
			return this.action;
		}

		public void setBase(final String base) {
			this.base = base;
		}

		public String getBase() {
			return this.base;
		}

		public void setSubmeshes(final Collection<DisplayContext.Submesh> submeshes) {
			this.lock.lock();
			try {
				if (submeshes != null) {
					this.submeshes = new LinkedList<DisplayContext.Submesh>(submeshes);
				}
			} finally {
				this.lock.unlock();
			}
		}

		public List<DisplayContext.Submesh> getSubmeshes() {
			this.lock.lock();
			try {
				if (this.submeshes == null) {
					return null;
				}
				return new LinkedList<DisplayContext.Submesh>(this.submeshes);
			} finally {
				this.lock.unlock();
			}
		}

		public void setChildDCHandle(final String handle) {
			this.handle = handle;
		}

		public String getChildDCHandle() {
			return this.handle;
		}

		public void setChildDC(final DisplayContext dc) {
			this.childDC = dc;
		}

		public DisplayContext getChildDC() {
			return this.childDC;
		}
	}

	public static class NewDirLightMessage extends TargetMessage {
		private LightData lightData;
		private static final long serialVersionUID = 1L;

		public NewDirLightMessage() {
			super(WorldManagerClient.MSG_TYPE_NEW_DIRLIGHT);
		}

		public NewDirLightMessage(final OID objOid, final OID lightOid, final LightData lightData) {
			super(WorldManagerClient.MSG_TYPE_NEW_DIRLIGHT, objOid, lightOid);
			this.setLightData(lightData);
		}

		public void setLightData(final LightData lightData) {
			this.lightData = lightData;
		}

		public LightData getLightData() {
			return this.lightData;
		}
	}

	public static class FreeObjectMessage extends TargetMessage implements ClientMessage, EventParser {
		private static final long serialVersionUID = 1L;

		public FreeObjectMessage() {
		}

		public FreeObjectMessage(final OID playerOid, final OID objOid) {
			super(WorldManagerClient.MSG_TYPE_FREE_OBJECT, playerOid, objOid);
		}

		@Override
		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(24);
			buf.putOID(this.getTarget());
			buf.putInt(10);
			buf.putOID(this.getSubject());
			buf.flip();
			return buf;
		}

		@Override
		public void parseBytes(final AOByteBuffer buf) {
			this.setTarget(buf.getOID());
			buf.getInt();
			this.setSubject(buf.getOID());
		}
	}

	public static class MobPathReqMessage extends MobPathMessageBaseClass {
		private static final long serialVersionUID = 1L;

		public MobPathReqMessage() {
		}

		public MobPathReqMessage(final OID oid, final long startTime, final String interpKind, final float speed, final String terrainString, final List<Point> pathPoints) {
			super(oid, startTime, interpKind, speed, terrainString, pathPoints);
		}

		public MobPathReqMessage(final OID oid) {
			super(oid, 0L, "linear", 0.0f, "", new LinkedList<Point>());
		}

		@Override
		protected MessageType getMobPathMsgType() {
			return WorldManagerClient.MSG_TYPE_MOB_PATH_REQ;
		}

		@Override
		protected String getMobPathMsgTypeTitle() {
			return "MobPathMessageReq";
		}
	}

	public static class MobPathMessage extends MobPathMessageBaseClass {
		private static final long serialVersionUID = 1L;

		public MobPathMessage() {
		}

		public MobPathMessage(final OID oid, final long startTime, final String interpKind, final float speed, final String terrainString, final List<Point> pathPoints) {
			super(oid, startTime, interpKind, speed, terrainString, pathPoints);
		}

		@Override
		protected MessageType getMobPathMsgType() {
			return WorldManagerClient.MSG_TYPE_MOB_PATH;
		}

		@Override
		protected String getMobPathMsgTypeTitle() {
			return "MobPathMessage";
		}

		public boolean pathExpired() {
			if (this.pathPoints == null || this.pathPoints.size() < 2) {
				return true;
			}
			float pathTime = 0.0f;
			Point curr = this.pathPoints.get(0);
			for (int i = 1; i < this.pathPoints.size(); ++i) {
				final Point next = this.pathPoints.get(i);
				final float dist = Point.distanceTo(curr, next);
				final float diffTime = dist / this.speed;
				pathTime += diffTime;
				curr = next;
			}
			return this.startTime + pathTime < System.currentTimeMillis();
		}
	}

	public static class MobPathCorrectionMessage extends MobPathMessageBaseClass {
		private static final long serialVersionUID = 1L;

		public MobPathCorrectionMessage() {
		}

		public MobPathCorrectionMessage(final OID oid, final long startTime, final String interpKind, final float speed, final String terrainString, final List<Point> pathPoints) {
			super(oid, startTime, interpKind, speed, terrainString, pathPoints);
		}

		@Override
		protected MessageType getMobPathMsgType() {
			return WorldManagerClient.MSG_TYPE_MOB_PATH_CORRECTION;
		}

		@Override
		protected String getMobPathMsgTypeTitle() {
			return "MobPathCorrectionMessage";
		}
	}

	public abstract static class MobPathMessageBaseClass extends SubjectMessage {
		long startTime;
		String interpKind;
		float speed;
		String terrainString;
		List<Point> pathPoints;

		protected abstract MessageType getMobPathMsgType();

		protected abstract String getMobPathMsgTypeTitle();

		public MobPathMessageBaseClass() {
			this.interpKind = "linear";
			this.terrainString = "";
			this.pathPoints = null;
		}

		public MobPathMessageBaseClass(final OID oid, final Long startTime, final String interpKind, final float speed, final String terrainString, final List<Point> pathPoints) {
			this.interpKind = "linear";
			this.terrainString = "";
			this.pathPoints = null;
			this.setMsgType(this.getMobPathMsgType());
			this.setSubject(oid);
			this.setStartTime(startTime);
			this.setInterpKind(interpKind);
			this.setSpeed(speed);
			this.setTerrainString(terrainString);
			this.setPathPoints(pathPoints);
		}

		@Override
		public String toString() {
			return "[" + this.getMobPathMsgTypeTitle() + " oid=" + this.getSubject() + ", interpKind=" + this.interpKind + ", speed=" + this.speed + ", terrainString=" + this.terrainString
					+ ", pathPoints=" + this.getPathPoints() + ", super=" + super.toString() + "]";
		}

		public void setStartTime(final long startTime) {
			this.startTime = startTime;
		}

		public long getStartTime() {
			return this.startTime;
		}

		public void setInterpKind(final String interpKind) {
			this.interpKind = interpKind;
		}

		public String getInterpKind() {
			return this.interpKind;
		}

		public void setSpeed(final float speed) {
			this.speed = speed;
		}

		public float getSpeed() {
			return this.speed;
		}

		public void setTerrainString(final String terrainString) {
			this.terrainString = terrainString;
		}

		public String getTerrainString() {
			return this.terrainString;
		}

		public void setPathPoints(final List<Point> pathPoints) {
			this.pathPoints = pathPoints;
		}

		public List<Point> getPathPoints() {
			return this.pathPoints;
		}

		public Point getPositionAtTime(final Long when) {
			if (this.pathPoints == null || this.pathPoints.size() == 0) {
				return null;
			}
			if (when <= this.startTime) {
				return this.pathPoints.get(0);
			}
			final PathInterpolator interp = (this.interpKind == "linear") ? new PathLinear(null, this.startTime, this.speed, this.terrainString, this.pathPoints) : new PathSpline(null,
					this.startTime, this.speed, this.terrainString, this.pathPoints);
			final PathLocAndDir locAndDir = interp.interpolate(when);
			if (locAndDir == null) {
				return this.pathPoints.get(this.pathPoints.size() - 1);
			}
			return locAndDir.getLoc();
		}

		public AOByteBuffer toBuffer() {
			final AOByteBuffer buf = new AOByteBuffer(400);
			buf.putOID(this.getSubject());
			buf.putInt(73);
			buf.putLong(this.startTime);
			buf.putString(this.interpKind);
			buf.putFloat(this.speed);
			buf.putString(this.terrainString);
			buf.putInt(this.pathPoints.size());
			for (final Point point : this.pathPoints) {
				buf.putPoint(point);
			}
			buf.flip();
			return buf;
		}
	}

	public static class LoadSubObjectMessage extends ObjectManagerClient.LoadSubObjectMessage {
		private Point location;
		private OID instanceOid;
		private static final long serialVersionUID = 1L;

		public LoadSubObjectMessage() {
		}

		public LoadSubObjectMessage(final OID oid, final Namespace namespace, final Point location, final OID instanceOid) {
			super(oid, namespace);
			this.location = location;
			this.instanceOid = instanceOid;
		}

		public Point getLocation() {
			return this.location;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}
	}

	public static class HostInstanceMessage extends Message {
		private String pluginName;
		private OID instanceOid;
		private static final long serialVersionUID = 1L;

		public HostInstanceMessage() {
		}

		public HostInstanceMessage(final String pluginName, final OID instanceOid2) {
			super(WorldManagerClient.MSG_TYPE_HOST_INSTANCE);
			this.setPluginName(pluginName);
			this.setInstanceOid(instanceOid2);
		}

		public String getPluginName() {
			return this.pluginName;
		}

		public void setPluginName(final String name) {
			this.pluginName = name;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setInstanceOid(final OID instanceOid) {
			this.instanceOid = instanceOid;
		}
	}

	public static class PlayerPathWMReqMessage extends Message {
		private float avatarWidth;
		private List<AOVector> boundary;
		private AOVector dest;
		private Quaternion destOrientation;
		private OID instanceOid;
		private List<List<AOVector>> obstacles;
		private OID playerOid;
		private String roomId;
		private float speed;
		private AOVector start;
		private Quaternion startOrientation;
		private static final long serialVersionUID = 1L;

		public PlayerPathWMReqMessage() {
			super(WorldManagerClient.MSG_TYPE_PLAYER_PATH_WM_REQ);
		}

		public PlayerPathWMReqMessage(final OID playerOid, final OID instanceOid, final String roomId, final AOVector start, final float speed, final Quaternion startOrientation, final AOVector dest,
				final Quaternion destOrientation, final List<AOVector> boundary, final List<List<AOVector>> obstacles, final float avatarWidth) {
			super(WorldManagerClient.MSG_TYPE_PLAYER_PATH_WM_REQ);
			this.playerOid = playerOid;
			this.instanceOid = instanceOid;
			this.start = start;
			this.speed = speed;
			this.startOrientation = startOrientation;
			this.dest = dest;
			this.destOrientation = destOrientation;
			this.boundary = boundary;
			this.obstacles = obstacles;
			this.avatarWidth = avatarWidth;
		}

		public float getAvatarWidth() {
			return this.avatarWidth;
		}

		public void setAvatarWidth(final float avatarWidth) {
			this.avatarWidth = avatarWidth;
		}

		public List<AOVector> getBoundary() {
			return this.boundary;
		}

		public void setBoundary(final List<AOVector> boundary) {
			this.boundary = boundary;
		}

		public AOVector getDest() {
			return this.dest;
		}

		public void setDest(final AOVector dest) {
			this.dest = dest;
		}

		public Quaternion getDestOrientation() {
			return this.destOrientation;
		}

		public void setDestOrientation(final Quaternion destOrientation) {
			this.destOrientation = destOrientation;
		}

		public OID getInstanceOid() {
			return this.instanceOid;
		}

		public void setInstanceOid(final OID instanceOid) {
			this.instanceOid = instanceOid;
		}

		public List<List<AOVector>> getObstacles() {
			return this.obstacles;
		}

		public void setObstacles(final List<List<AOVector>> obstacles) {
			this.obstacles = obstacles;
		}

		public OID getPlayerOid() {
			return this.playerOid;
		}

		public void setPlayerOid(final OID playerOid) {
			this.playerOid = playerOid;
		}

		public String getRoomId() {
			return this.roomId;
		}

		public void setRoomId(final String roomId) {
			this.roomId = roomId;
		}

		public float getSpeed() {
			return this.speed;
		}

		public void setSpeed(final float speed) {
			this.speed = speed;
		}

		public AOVector getStart() {
			return this.start;
		}

		public void setStart(final AOVector start) {
			this.start = start;
		}

		public Quaternion getStartOrientation() {
			return this.startOrientation;
		}

		public void setStartOrientation(final Quaternion startOrientation) {
			this.startOrientation = startOrientation;
		}
	}
}
