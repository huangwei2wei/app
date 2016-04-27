// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import com.app.server.atavism.server.util.LockFactory;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
//import atavism.server.util.Log;
import com.app.server.atavism.server.engine.WMWorldNode;
import com.app.server.atavism.server.engine.MobilePerceiver;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.math.Point;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.WorldNode;
//import com.app.server.atavism.server.util.AORuntimeException;
//import com.app.server.atavism.server.engine.Event;
import java.io.Serializable;
import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.Namespace;
import java.util.concurrent.locks.Lock;

public class AOObject extends Entity {
	public static final String stateMapKey = "aoobj.statemap";
	public static final String wnodeKey = "aoobj.wnode";
	public static final String perceiverKey = "aoobj.perceiver";
	public static final String aoidKey = "aoobj.aoid";
	public static final String dcKey = "aoobj.dc";
	private String scaleKey = "aoobj.scale";

	private String permCBKey = "aoobj.permCB";

	private static AOObjectCreateHook createHook = null;

	public static Lock transferLock = LockFactory.makeLock("objXferLock");
	private static final long serialVersionUID = 1L;

	public AOObject() {
		init();
	}

	public AOObject(String name) {
		super(name);
		init();
	}

	public AOObject(OID oid) {
		super(oid);
		init();
	}

	private void init() {
		AOObjectCreateHook hook = getObjCreateHook();
		if (hook != null)
			hook.objectCreateHook(this);
	}

	public OID getMasterOid() {
		return getOid();
	}

	public boolean isMob() {
		return getType().isMob();
	}

	public boolean isItem() {
		return getType() == ObjectTypes.item;
	}

	public boolean isLight() {
		return getType() == ObjectTypes.light;
	}

	public boolean isUser() {
		return getType().isPlayer();
	}

	public boolean isStructure() {
		return getType().isStructure();
	}

	public String toString() {
		return "[AOObject: " + getName() + ":" + getOid() + ", type=" + getType() + "]";
	}

	public ObjState setState(String state, ObjState obj) {
		this.lock.lock();
		try {
			StateMap stateMap = getStateMap();
			ObjState localObjState = stateMap.setState(state, obj);
			return localObjState;
		} finally {
			this.lock.unlock();
		}
	}

	public ObjState getState(String s) {
		this.lock.lock();
		try {
			StateMap stateMap = getStateMap();
			ObjState localObjState = stateMap.getState(s);
			return localObjState;
		} finally {
			this.lock.unlock();
		}
	}

	private StateMap getStateMap() {
		this.lock.lock();
		try {
			StateMap stateMap = (StateMap) getProperty("aoobj.statemap");
			if (stateMap == null) {
				stateMap = new StateMap();
				setProperty("aoobj.statemap", stateMap);
			}
			StateMap localStateMap1 = stateMap;
			return localStateMap1;
		} finally {
			this.lock.unlock();
		}
	}

	// public void sendEvent(Event event) {
	// throw new AORuntimeException("legacy code");
	// }

	public WorldNode worldNode() {
		return (WorldNode) getProperty("aoobj.wnode");
	}

	public void worldNode(WorldNode worldNode) {
		setProperty("aoobj.wnode", worldNode);
	}

	public BasicWorldNode baseWorldNode() {
		return new BasicWorldNode((InterpolatedWorldNode) getProperty("aoobj.wnode"));
	}

	public Point getLoc() {
		WorldNode node = worldNode();
		return node == null ? null : node.getLoc();
	}

	public Point getCurrentLoc() {
		WorldNode node = worldNode();
		return node == null ? null : node.getCurrentLoc();
	}

	public Quaternion getOrientation() {
		WorldNode node = worldNode();
		return node == null ? null : node.getOrientation();
	}

	public AOVector getDirection() {
		InterpolatedWorldNode iwn = (InterpolatedWorldNode) getProperty("aoobj.wnode");
		return iwn.getDir();
	}

	public InterpolatedWorldNode.InterpolatedDirLocOrientTime getDirLocOrientTime() {
		InterpolatedWorldNode iwn = (InterpolatedWorldNode) getProperty("aoobj.wnode");
		return iwn.getDirLocOrientTime();
	}

	public MobilePerceiver<WMWorldNode> perceiver() {
		this.lock.lock();
		try {
			MobilePerceiver localMobilePerceiver = (MobilePerceiver) getProperty("aoobj.perceiver");
			return localMobilePerceiver;
		} finally {
			this.lock.unlock();
		}
	}

	public void perceiver(MobilePerceiver<WMWorldNode> p) {
		this.lock.lock();
		try {
			MobilePerceiver perceiver = perceiver();
			if (perceiver == p) {
				log.warn("AOObject.setPerceiver: new/cur perceiver same");
			}
			if (perceiver != null) {
				perceiver.setElement(null);
				log.warn("AOObject.setPerceiver: perceiv is already not null");
			}
			log.debug("AOObject.setPerceiver: obj oid=" + getOid() + ", perceiver=" + p);
			setProperty("aoobj.perceiver", p);
			if (p != null)
				p.setElement((WMWorldNode) worldNode());
		} finally {
			this.lock.unlock();
		}
	}

	public OID atavismID() {
		return (OID) getProperty("aoobj.aoid");
	}

	public void atavismID(OID id) {
		setProperty("aoobj.aoid", id);
	}

	public void displayContext(DisplayContext dc) {
		DisplayContext dcCopy = null;
		if (dc != null) {
			dcCopy = (DisplayContext) dc.clone();
			dcCopy.setObjRef(getOid());
		}
		setProperty("aoobj.dc", dcCopy);
	}

	public DisplayContext displayContext() {
		DisplayContext dc = (DisplayContext) getProperty("aoobj.dc");
		return dc;
	}

	public void scale(float scale) {
		scale(new AOVector(scale, scale, scale));
	}

	public void scale(AOVector scale) {
		setProperty(this.scaleKey, (AOVector) scale.clone());
	}

	public AOVector scale() {
		return (AOVector) getProperty(this.scaleKey);
	}

	public static void registerObjCreateHook(AOObjectCreateHook hook) {
		createHook = hook;
	}

	public static AOObjectCreateHook getObjCreateHook() {
		return createHook;
	}

	public void permissionCallback(PermissionCallback cb) {
		setProperty(this.permCBKey, cb);
	}

	public PermissionCallback permissionCallback() {
		return (PermissionCallback) getProperty(this.permCBKey);
	}

	public static void writeObject(ObjectOutput out, Object obj) throws IOException {
		out.writeBoolean(obj == null);
		if (obj != null)
			out.writeObject(obj);
	}

	public static Object readObject(ObjectInput in) throws IOException, ClassNotFoundException {
		boolean isNull = in.readBoolean();
		if (!isNull) {
			return in.readObject();
		}
		return null;
	}

	public static void writeString(ObjectOutput out, String string) throws IOException {
		if (string == null)
			out.writeUTF("");
		else
			out.writeUTF(string);
	}

	public static Collection<AOObject> getAllObjects() {
		Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.WORLD_MANAGER);
		Set objSet = new HashSet();
		for (Entity e : entities) {
			if ((e instanceof AOObject)) {
				objSet.add((AOObject) e);
			}
		}
		return objSet;
	}

	public static AOObject getObject(OID oid) {
		return (AOObject) EntityManager.getEntityByNamespace(oid, Namespace.WORLD_MANAGER);
	}

	static {
		try {
			BeanInfo info = Introspector.getBeanInfo(AOObject.class);
			PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();

			for (int i = 0; i < propertyDescriptors.length; i++);
		} catch (Exception e) {
			log.error("failed aoobject beans initalization");
		}
	}

	public static class StateMap implements Serializable {
		Lock lock = null;
		Map<String, ObjState> map = new HashMap();
		private static final long serialVersionUID = 1L;

		public StateMap() {
			setupTransient();
		}

		private void setupTransient() {
			this.lock = LockFactory.makeLock("StateMapLock");
		}

		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.defaultReadObject();
			setupTransient();
		}

		public ObjState setState(String state, ObjState objState) {
			this.lock.lock();
			try {
				ObjState localObjState = (ObjState) this.map.put(state, objState);
				return localObjState;
			} finally {
				this.lock.unlock();
			}
		}

		public ObjState getState(String state) {
			this.lock.lock();
			try {
				ObjState localObjState = (ObjState) this.map.get(state);
				return localObjState;
			} finally {
				this.lock.unlock();
			}
		}

		public void setMap(Map<String, ObjState> map) {
			this.lock.lock();
			try {
				this.map = new HashMap(map);
			} finally {
				this.lock.unlock();
			}
		}

		public Map<String, ObjState> getMap() {
			this.lock.lock();
			try {
				HashMap localHashMap = new HashMap(this.map);
				return localHashMap;
			} finally {
				this.lock.unlock();
			}
		}
	}
}