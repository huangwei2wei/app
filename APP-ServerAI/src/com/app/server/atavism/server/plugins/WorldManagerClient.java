// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.Namespace;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.objects.Entity;
import com.app.server.atavism.server.objects.Marker;
import com.app.server.atavism.server.objects.ObjectType;
import com.app.server.atavism.server.objects.ObjectTypes;

public class WorldManagerClient {
	private static Logger log = Logger.getLogger("navmesh");

	public static final int NO_FLAGS = 0;
	public static final int SAVE_NOW = 1;
	public static final byte modifyDisplayContextActionReplace = 1;
	public static final byte modifyDisplayContextActionAdd = 2;
	public static final byte modifyDisplayContextActionAddChild = 3;
	public static final byte modifyDisplayContextActionRemove = 4;
	public static final byte modifyDisplayContextActionRemoveChild = 5;
	public static final String WMGR_LAST_SAVED_PROP = (String) Entity.registerTransientPropertyKey("wmgr.lastSaved");
	public static Long WMGR_SAVE_INTERVAL_MS = Long.valueOf(60000L);
	public static final String WORLD_PROP_NOMOVE = "world.nomove";
	public static final String WORLD_PROP_NOTURN = "world.noturn";
	public static final String ACCOUNT_PROPERTY = "accountId";
	public static String TEMPL_LOC = ":loc";
	public static String TEMPL_INSTANCE = ":instance";
	public static String TEMPL_NAME = ":entityName";
	public static String TEMPL_ORIENT = ":orient";
	public static String TEMPL_SCALE = ":scale";
	public static String TEMPL_PERCEPTION_RADIUS = ":percRadius";
	public static String TEMPL_FOLLOWS_TERRAIN = ":followsTerrain";
	public static String TEMPL_RUN_THRESHOLD = "runThreshold";
	public static String TEMPL_TERRAIN_DECAL_DATA = "terrainDecal";
	public static String TEMPL_SOUND_DATA_LIST = "soundData";
	public static String TEMPL_ID = "templateID";
	public static String TEMPL_OBJECT_TYPE = ":objType";
	public static ObjectType TEMPL_OBJECT_TYPE_MOB = ObjectTypes.mob;
	public static ObjectType TEMPL_OBJECT_TYPE_PLAYER = ObjectTypes.player;
	public static ObjectType TEMPL_OBJECT_TYPE_LIGHT = ObjectTypes.light;
	public static ObjectType TEMPL_OBJECT_TYPE_ITEM = ObjectTypes.item;
	public static ObjectType TEMPL_OBJECT_TYPE_STRUCTURE = ObjectTypes.structure;
	public static ObjectType TEMPL_OBJECT_TYPE_TERRAIN_DECAL = ObjectTypes.terrainDecal;
	public static ObjectType TEMPL_OBJECT_TYPE_POINT_SOUND = ObjectTypes.pointSound;
	public static ObjectType TEMPL_OBJECT_TYPE_MARKER = Marker.OBJECT_TYPE;
	public static String TEMPL_DISPLAY_CONTEXT = ":displayContext";
	public static final String TEMPL_WORLDMGR_NAME = ":wmName";
	public static String MOB_PATH_PROPERTY = "MobPathMsg";
	public static String MSG_PROP_LOC = "msgPropLoc";
	public static Namespace NAMESPACE = null;
	public static Namespace INSTANCE_NAMESPACE = null;

//	public static BasicWorldNode getWorldNode(final OID oid) {
//		final SubjectMessage msg = new SubjectMessage(WorldManagerClient.MSG_TYPE_GETWNODE_REQ, oid);
//		final BasicWorldNode wnode = (BasicWorldNode) Engine.getAgent().sendRPCReturnObject(msg);
//		log.debug("WorldManagerClient.getWorldNode: oid=" + oid + " wnode=" + wnode);
//		return wnode;
//	}

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

		// public AOByteBuffer toBuffer(final OID notifyOid) {
		// Log.debug("INFO: creating buffer with notifyOid: " + notifyOid);
		// final AOByteBuffer buf = new AOByteBuffer(220);
		// buf.putOID(notifyOid);
		// buf.putInt(8);
		// buf.putOID(this.oid);
		// buf.putString((this.name == null) ? "unknown" : this.name);
		// buf.putPoint((this.loc == null) ? new Point() : this.loc);
		// buf.putQuaternion((this.orient == null) ? new Quaternion() : this.orient);
		// buf.putAOVector((this.scale == null) ? new AOVector(1.0f, 1.0f, 1.0f) : this.scale);
		// buf.putInt(this.objType.getTypeId());
		// buf.putInt(this.followsTerrain ? 1 : 0);
		// buf.putAOVector(this.dir);
		// buf.putLong(this.lastInterp);
		// buf.flip();
		// return buf;
		// }

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

}
