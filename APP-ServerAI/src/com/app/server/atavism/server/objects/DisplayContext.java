// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import com.app.server.atavism.server.util.LockFactory;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import com.app.server.atavism.agis.objects.AgisAttachSocket;
import com.app.server.atavism.agis.objects.AgisEquipSlot;
import java.util.Set;
import com.app.server.atavism.server.engine.OID;
import java.util.Map;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class DisplayContext implements Cloneable, Serializable {
	private Logger log = Logger.getLogger("navmesh");
	public String meshFile;
	private Map<String, DisplayContext> childDCMap;
	private OID objRef;
	private Set<Submesh> submeshes;
	private Map<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>> displayInfoMap;
	private boolean attachableFlag;
	private boolean castShadow;
	private boolean receiveShadow;
	private int displayID;
	protected transient Lock lock;
	private static final long serialVersionUID = 1L;

	public DisplayContext() {
		this.meshFile = null;
		this.childDCMap = new HashMap<String, DisplayContext>();
		this.objRef = null;
		this.submeshes = new HashSet<Submesh>();
		this.displayInfoMap = new HashMap<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>>();
		this.attachableFlag = false;
		this.castShadow = false;
		this.receiveShadow = false;
		this.displayID = -1;
		this.setupTransient();
	}

	public DisplayContext(final OID oid) {
		this.meshFile = null;
		this.childDCMap = new HashMap<String, DisplayContext>();
		this.objRef = null;
		this.submeshes = new HashSet<Submesh>();
		this.displayInfoMap = new HashMap<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>>();
		this.attachableFlag = false;
		this.castShadow = false;
		this.receiveShadow = false;
		this.displayID = -1;
		this.setupTransient();
		this.objRef = oid;
	}

	public DisplayContext(final String meshfile) {
		this.meshFile = null;
		this.childDCMap = new HashMap<String, DisplayContext>();
		this.objRef = null;
		this.submeshes = new HashSet<Submesh>();
		this.displayInfoMap = new HashMap<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>>();
		this.attachableFlag = false;
		this.castShadow = false;
		this.receiveShadow = false;
		this.displayID = -1;
		this.setupTransient();
		this.setMeshFile(meshfile);
	}

	public DisplayContext(final String meshfile, final boolean castShadow) {
		this.meshFile = null;
		this.childDCMap = new HashMap<String, DisplayContext>();
		this.objRef = null;
		this.submeshes = new HashSet<Submesh>();
		this.displayInfoMap = new HashMap<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>>();
		this.attachableFlag = false;
		this.castShadow = false;
		this.receiveShadow = false;
		this.displayID = -1;
		this.setupTransient();
		this.setMeshFile(meshfile);
		this.setCastShadow(castShadow);
	}

	public DisplayContext(final OID oid, final String meshfile) {
		this.meshFile = null;
		this.childDCMap = new HashMap<String, DisplayContext>();
		this.objRef = null;
		this.submeshes = new HashSet<Submesh>();
		this.displayInfoMap = new HashMap<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>>();
		this.attachableFlag = false;
		this.castShadow = false;
		this.receiveShadow = false;
		this.displayID = -1;
		this.setupTransient();
		this.objRef = oid;
		this.setMeshFile(meshfile);
	}

	protected void setupTransient() {
		this.lock = LockFactory.makeLock("DisplayContextLock");
	}

	public boolean subsetOf(final DisplayContext other) {
		if (!this.getMeshFile().equals(other.getMeshFile())) {
			return false;
		}
		final Set<Submesh> otherSubmeshes = other.getSubmeshes();
		for (final Submesh submesh : this.getSubmeshes()) {
			if (!otherSubmeshes.contains(submesh)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(final Object other) {
		final DisplayContext otherDC = (DisplayContext) other;
		return otherDC.getMeshFile().equals(this.getMeshFile()) && this.subsetOf(otherDC) && otherDC.subsetOf(this);
	}

	@Override
	public int hashCode() {
		int hash = this.meshFile.hashCode();
		for (final Submesh subMesh : this.getSubmeshes()) {
			hash ^= subMesh.hashCode();
		}
		return hash;
	}

	@Override
	public String toString() {
		final Set<Submesh> subMeshes = this.getSubmeshes();
		String s = "[DisplayContext: meshFile=" + this.getMeshFile() + ", attachableFlag=" + this.getAttachableFlag() + ", castShadow=" + this.getCastShadow() + ", receiveShadow="
				+ this.getReceiveShadow() + ", numSubmeshes=" + subMeshes.size();
		for (final Submesh subMesh : subMeshes) {
			s = s + ", submesh=" + subMesh;
		}
		return s + "]";
	}

	public Object clone() {
		this.lock.lock();
		try {
			final DisplayContext dc = new DisplayContext(this.getObjRef());
			dc.setMeshFile(this.getMeshFile());
			dc.setSubmeshes(this.submeshes);
			dc.setAttachableFlag(this.getAttachableFlag());
			dc.setDisplayInfo(this.getDisplayInfo());
			dc.setChildDCMap(this.getChildDCMap());
			dc.setCastShadow(this.getCastShadow());
			dc.setReceiveShadow(this.getReceiveShadow());
			dc.setDisplayID(this.getDisplayID());
			return dc;
		} finally {
			this.lock.unlock();
		}
	}

	public void setObjRef(final OID oid) {
		this.objRef = oid;
	}

	public OID getObjRef() {
		return this.objRef;
	}

	public String getMeshFile() {
		return this.meshFile;
	}

	public void setMeshFile(final String mesh) {
		this.meshFile = mesh;
	}

	public void addSubmesh(final Submesh submesh) {
		this.lock.lock();
		try {
			this.submeshes.add(submesh);
		} finally {
			this.lock.unlock();
		}
	}

	public void addSubmeshes(final Collection<Submesh> submeshes) {
		this.lock.lock();
		try {
			this.submeshes.addAll(submeshes);
		} finally {
			this.lock.unlock();
		}
	}

	public void removeSubmesh(final Submesh submesh) {
		this.lock.lock();
		try {
			this.submeshes.remove(submesh);
		} finally {
			this.lock.unlock();
		}
	}

	public void removeSubmeshes(final Collection<Submesh> submeshes) {
		this.lock.lock();
		try {
			log.debug("DisplayContext.removeSubmeshes: removelist=" + submeshes + ", currentDC=" + this);
			this.submeshes.removeAll(submeshes);
			log.debug("DisplayContext.removeSubmeshes: updated dc=" + this);
		} finally {
			this.lock.unlock();
		}
	}

	public void addChildDC(final String handle, final DisplayContext dc) {
		this.lock.lock();
		try {
			this.childDCMap.put(handle, (DisplayContext) dc.clone());
		} finally {
			this.lock.unlock();
		}
	}

	public DisplayContext getChildDC(final String handle) {
		this.lock.lock();
		try {
			return this.childDCMap.get(handle);
		} finally {
			this.lock.unlock();
		}
	}

	public DisplayContext removeChildDC(final String handle) {
		this.lock.lock();
		try {
			return this.childDCMap.remove(handle);
		} finally {
			this.lock.unlock();
		}
	}

	public void setChildDCMap(final Map<String, DisplayContext> map) {
		this.lock.lock();
		try {
			this.childDCMap = new HashMap<String, DisplayContext>(map);
		} finally {
			this.lock.unlock();
		}
	}

	public Map<String, DisplayContext> getChildDCMap() {
		this.lock.lock();
		try {
			return new HashMap<String, DisplayContext>(this.childDCMap);
		} finally {
			this.lock.unlock();
		}
	}

	public void setSubmeshes(final Set<Submesh> submeshes) {
		this.lock.lock();
		try {
			this.submeshes = new HashSet<Submesh>(submeshes);
		} finally {
			this.lock.unlock();
		}
	}

	public Set<Submesh> getSubmeshes() {
		this.lock.lock();
		try {
			return new HashSet<Submesh>(this.submeshes);
		} finally {
			this.lock.unlock();
		}
	}

	public boolean getAttachableFlag() {
		return this.attachableFlag;
	}

	public void setAttachableFlag(final boolean b) {
		this.attachableFlag = b;
	}

	public void setAttachInfo(final DisplayState displayState, final AgisEquipSlot equipSlot, final AgisAttachSocket socket) {
		this.lock.lock();
		try {
			this.setAttachableFlag(true);
			Map<AgisEquipSlot, AgisAttachSocket> attachMap = this.displayInfoMap.get(displayState);
			if (attachMap == null) {
				attachMap = new HashMap<AgisEquipSlot, AgisAttachSocket>();
				this.displayInfoMap.put(displayState, attachMap);
			}
			attachMap.put(equipSlot, socket);
		} finally {
			this.lock.unlock();
		}
	}

	public AgisAttachSocket getAttachInfo(final DisplayState ds, final AgisEquipSlot equipSlot) {
		this.lock.lock();
		try {
			if (!this.getAttachableFlag()) {
				log.error("DisplayContext.getAttachInfo: not attachable");
				return null;
			}
			final Map<AgisEquipSlot, AgisAttachSocket> attachMap = this.displayInfoMap.get(ds);
			if (attachMap == null) {
				log.warn("DisplayContext.getAttachInfo: could not find displayState " + ds);
				return null;
			}
			return attachMap.get(equipSlot);
		} finally {
			this.lock.unlock();
		}
	}

	public void setDisplayInfo(final Map<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>> map) {
		this.lock.lock();
		try {
			if (!this.displayInfoMap.isEmpty()) {
				throw new RuntimeException("displaycontext: setting display info on existing non empty map");
			}
			for (final Map.Entry<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>> entry : map.entrySet()) {
				final DisplayState ds = entry.getKey();
				final Map<AgisEquipSlot, AgisAttachSocket> attachMap = new HashMap<AgisEquipSlot, AgisAttachSocket>(entry.getValue());
				this.displayInfoMap.put(ds, attachMap);
			}
		} finally {
			this.lock.unlock();
		}
	}

	public Map<DisplayState, Map<AgisEquipSlot, AgisAttachSocket>> getDisplayInfo() {
		return this.displayInfoMap;
	}

	public void printAttachInfo() {
		this.lock.lock();
		try {
			for (final DisplayState ds : this.displayInfoMap.keySet()) {
				log.debug("DisplayContext.printAttachInfo: state=" + ds);
				this.printAttachInfo(this.displayInfoMap.get(ds));
			}
		} finally {
			this.lock.unlock();
		}
	}

	protected void printAttachInfo(final Map<AgisEquipSlot, AgisAttachSocket> map) {
		this.lock.lock();
		try {
			for (final AgisEquipSlot slot : map.keySet()) {
				final AgisAttachSocket socket = map.get(slot);
				log.debug("DisplayContext.printAttachInfo: slot=" + slot + ", socket=" + socket);
			}
		} finally {
			this.lock.unlock();
		}
	}

	public void setCastShadow(final boolean cast) {
		this.castShadow = cast;
	}

	public boolean getCastShadow() {
		return this.castShadow;
	}

	public void setReceiveShadow(final boolean receive) {
		this.receiveShadow = receive;
	}

	public boolean getReceiveShadow() {
		return this.receiveShadow;
	}

	public void setDisplayID(final int displayID) {
		this.displayID = displayID;
	}

	public int getDisplayID() {
		return this.displayID;
	}

	private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
		out.defaultWriteObject();
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		this.setupTransient();
		in.defaultReadObject();
	}

	public static class Submesh implements Serializable {
		public String name;
		public String material;
		private static final long serialVersionUID = 1L;

		public Submesh() {
			this.name = null;
			this.material = null;
		}

		public Submesh(final String name, final String material) {
			this.name = null;
			this.material = null;
			this.name = name;
			this.material = material;
		}

		@Override
		public String toString() {
			return "[Submesh: name=" + this.name + ", material=" + this.material + "]";
		}

		@Override
		public boolean equals(final Object other) {
			final Submesh otherSub = (Submesh) other;
			return this.name.equals(otherSub.getName()) && this.material.equals(otherSub.getMaterial());
		}

		@Override
		public int hashCode() {
			return ((this.getName() == null || this.getName().equals("")) ? 0 : this.getName().hashCode()) ^ ((this.getMaterial() == null) ? 0 : this.getMaterial().hashCode());
		}

		public void setName(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public void setMaterial(final String material) {
			this.material = material;
		}

		public String getMaterial() {
			return this.material;
		}
	}
}
