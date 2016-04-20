// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.engine.BasicWorldNode;
import com.app.server.atavism.server.engine.InterpolatedWorldNode;
import com.app.server.atavism.server.math.Point;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
//import atavism.server.util.Log;
import com.app.server.atavism.server.engine.Engine;
import com.app.server.atavism.server.plugins.WorldManagerClient;
import com.app.server.atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.engine.Namespace;

public class ObjectTracker {
	protected static final Logger log = Logger.getLogger("navmesh");
	protected Namespace namespace;
	protected OID instanceOid;
	protected EntityWithWorldNodeFactory entityFactory;
	protected float hystericalMargin;
	protected NotifyReactionRadiusCallback notifyCallback;
	protected RemoteObjectFilter remoteObjectFilter;
	protected Set<OID> localObjects;
	protected Map<OID, Entry> trackMap;
	protected Map<OID, NotifyData> reactionRadiusMap;
	protected TrackerFilter perceptionFilter;
	protected long perceptionSubId;
	protected Lock lock;

	public ObjectTracker(final Namespace namespace, final OID oid, final EntityWithWorldNodeFactory entityFactory, final Collection<ObjectType> subjectTypes) {
		this.hystericalMargin = 0.0f;
		this.notifyCallback = null;
		this.remoteObjectFilter = null;
		this.localObjects = new HashSet<OID>();
		this.trackMap = new HashMap<OID, Entry>();
		this.reactionRadiusMap = new HashMap<OID, NotifyData>();
		this.lock = LockFactory.makeLock("ObjectTrackerLock");
		this.initialize(namespace, oid, entityFactory, subjectTypes);
	}

	public ObjectTracker(final Namespace namespace, final OID instanceOid, final EntityWithWorldNodeFactory entityFactory, final float hystericalMargin,
			final NotifyReactionRadiusCallback notifyCallback, final RemoteObjectFilter remoteObjectFilter) {
		this.hystericalMargin = 0.0f;
		this.notifyCallback = null;
		this.remoteObjectFilter = null;
		this.localObjects = new HashSet<OID>();
		this.trackMap = new HashMap<OID, Entry>();
		this.reactionRadiusMap = new HashMap<OID, NotifyData>();
		this.lock = LockFactory.makeLock("ObjectTrackerLock");
		this.hystericalMargin = hystericalMargin;
		this.notifyCallback = notifyCallback;
		this.remoteObjectFilter = remoteObjectFilter;
		this.initialize(namespace, instanceOid, entityFactory, null);
	}

	private void initialize(final Namespace namespace, final OID oid, final EntityWithWorldNodeFactory entityFactory, final Collection<ObjectType> subjectTypes) {
		this.namespace = namespace;
		this.instanceOid = oid;
		this.entityFactory = entityFactory;
		log.debug("ObjectTracker.init created perceptionFilter: " + this.perceptionFilter.toString());
	}

	public OID getInstanceOid() {
		return this.instanceOid;
	}

	public void addLocalObject(final OID oid, final Integer reactionRadius) {
		this.lock.lock();
		try {
			if (this.localObjects.contains(oid)) {
				log.error("ObjectTracker.addLocalObject: oid " + oid + " is already in the set of local objects, for ObjectTracker instance " + this);
				return;
			}
			if (reactionRadius != null) {
				this.reactionRadiusMap.put(oid, new NotifyData(reactionRadius));
			}
			this.localObjects.add(oid);

		} finally {
			this.lock.unlock();
		}
		log.debug("ObjectTracker.addLocalObject: oid=" + oid + " reactionRadius=" + reactionRadius + " instanceOid=" + this.instanceOid);
	}

	public boolean hasLocalObject(final Long oid) {
		this.lock.lock();
		try {
			return this.localObjects.contains(oid);
		} finally {
			this.lock.unlock();
		}
	}

	public void addReactionRadius(final OID oid, final Integer reactionRadius) {

		log.debug("ObjectTracker.addReactionRadius: oid=" + oid + " reactionRadius=" + reactionRadius + " instanceOid=" + this.instanceOid);

		if (reactionRadius != null) {
			this.lock.lock();
			try {
				this.reactionRadiusMap.put(oid, new NotifyData(reactionRadius));
			} finally {
				this.lock.unlock();
			}
		}
	}

	public void addAggroRadius(final OID oid, final OID target, final Integer reactionRadius) {
		log.debug("ObjectTracker.addAggroRadius: oid=" + oid + " reactionRadius=" + reactionRadius + " instanceOid=" + this.instanceOid);
		if (reactionRadius != null) {
			this.lock.lock();
			try {
				if (this.reactionRadiusMap.containsKey(oid)) {
					final NotifyData nData = this.reactionRadiusMap.get(oid);
					nData.addOidAggroRange(target, reactionRadius);
					this.reactionRadiusMap.put(oid, nData);
					log.debug("Added target: " + target + " to existing reaction radius entry");
				} else {
					final NotifyData nData = new NotifyData(reactionRadius);
					nData.addOidAggroRange(target, reactionRadius);
					this.reactionRadiusMap.put(oid, nData);
					log.debug("Added target: " + target + " to new reaction radius entry");
				}
			} finally {
				this.lock.unlock();
			}
		}
	}

	public void removeReactionRadius(final OID oid) {
		log.debug("ObjectTracker.removeReactionRadius: oid=" + oid + " instanceOid=" + this.instanceOid);
		this.lock.lock();
		try {
			this.reactionRadiusMap.remove(oid);
		} finally {
			this.lock.unlock();
		}
	}

	public void removeAggroRadius(final OID oid, final OID target) {
		log.debug("ObjectTracker.removeAggroRadius: oid=" + oid + " instanceOid=" + this.instanceOid);
		this.lock.lock();
		try {
			if (this.reactionRadiusMap.containsKey(oid)) {
				final NotifyData nData = this.reactionRadiusMap.get(oid);
				nData.removeOidAggroRange(target);
				this.reactionRadiusMap.put(oid, nData);
			}
		} finally {
			this.lock.unlock();
		}
	}

	public boolean removeLocalObject(final OID oid) {
		log.debug("removeLocalObject: oid=" + oid + " instanceOid=" + this.instanceOid);
		this.lock.lock();
		try {
			this.localObjects.remove(oid);
			if (this.perceptionFilter.removeTarget(oid)) {
				final FilterUpdate filterUpdate = new FilterUpdate(1);
				filterUpdate.removeFieldValue(1, oid);
				Engine.getAgent().applyFilterUpdate(this.perceptionSubId, filterUpdate);
			}
			this.reactionRadiusMap.remove(oid);
			final List<OID> trackersToRemove = new ArrayList<OID>();
			for (final OID objOid : this.trackMap.keySet()) {
				while (this.removeRemoteObject(objOid, oid, trackersToRemove)) {
				}
			}
			for (final OID objOid : trackersToRemove) {
				this.trackMap.remove(objOid);
			}
			for (final Map.Entry<OID, NotifyData> entry : this.reactionRadiusMap.entrySet()) {
				final NotifyData notifyData = entry.getValue();
				notifyData.removeOidInRadius(oid);
				if (notifyData.hasAggroRadius(oid)) {
					notifyData.removeOidInAggroRadius(oid);
					notifyData.removeOidAggroRange(oid);
				}
			}
		} finally {
			this.lock.unlock();
		}
		if (Log.loggingDebug) {
			Log.debug("ObjectTracker.removeLocalObject: oid=" + oid + " instanceOid=" + this.instanceOid);
		}
		return true;
	}

	protected boolean maybeAddRemoteObject(final PerceptionMessage.ObjectNote objectNote) {
		final ObjectType objType = objectNote.getObjectType();
		final OID oid = objectNote.getSubject();
		final OID trackerOid = objectNote.getTarget();
		boolean callbackNixedIt = false;
		if (this.remoteObjectFilter != null) {
			callbackNixedIt = !this.remoteObjectFilter.objectShouldBeTracked(oid, objectNote);
		}
		if (callbackNixedIt || !objType.isMob()) {
			if (Log.loggingDebug) {
				Log.debug("ObjectTracker.maybeAddRemoteObject: ignoring oid=" + oid + " objType=" + objType + " detected by " + trackerOid + ", instanceOid=" + this.instanceOid);
			}
			return false;
		}
		if (Log.loggingDebug) {
			Log.debug("ObjectTracker.maybeAddRemoteObject: oid=" + oid + " objType=" + objType + " detected by " + trackerOid + ", instanceOid=" + this.instanceOid);
		}
		this.lock.lock();
		try {
			if (this.localObjects.contains(oid)) {
				return false;
			}
			Entry tracker = this.trackMap.get(oid);
			if (tracker == null) {
				tracker = new Entry(oid);
				this.trackMap.put(oid, tracker);
				tracker.activate(objType);
			}
			tracker.add(trackerOid);
		} finally {
			this.lock.unlock();
		}
		return true;
	}

	public List<OID> getOidsInRadius(final OID oid) {
		this.lock.lock();
		try {
			final NotifyData nd = this.reactionRadiusMap.get(oid);
			if (nd != null) {
				return nd.getOidsInRadius();
			}
			return new LinkedList<OID>();
		} finally {
			this.lock.unlock();
		}
	}

	protected boolean removeRemoteObject(final OID objOid, final OID oid, final List<OID> trackersToRemove) {
		this.lock.lock();
		try {
			if (this.localObjects.contains(objOid)) {
				return false;
			}
			final Entry tracker = this.trackMap.get(objOid);
			if (tracker == null || (trackersToRemove != null && trackersToRemove.contains(objOid))) {
				return false;
			}
			final boolean rv = tracker.remove(oid);
			if (tracker.isEmpty()) {
				tracker.deactivate();
				if (trackersToRemove != null) {
					trackersToRemove.add(objOid);
				} else {
					this.trackMap.remove(objOid);
				}
			}
			return rv;
		} finally {
			this.lock.unlock();
		}
	}

	public void updateEntity(final EntityWithWorldNode ewwn) {
		this.lock.lock();
		Map<OID, NotifyData> mapCopy;
		try {
			mapCopy = new HashMap<OID, NotifyData>(this.reactionRadiusMap);
		} finally {
			this.lock.unlock();
		}
		final InterpolatedWorldNode wnode = ewwn.getWorldNode();
		final Entity entity = ewwn.getEntity();
		final OID oid = entity.getOid();
		if (entity.getType() == ObjectTypes.player) {
			Log.debug("Checking player perceiver for obj: " + oid);
		}
		for (final Map.Entry<OID, NotifyData> entry : mapCopy.entrySet()) {
			final OID notifyOid = entry.getKey();
			if (oid.equals(notifyOid)) {
				continue;
			}
			final NotifyData notifyData = entry.getValue();
			final EntityWithWorldNode perceiver = (EntityWithWorldNode) EntityManager.getEntityByNamespace(notifyOid, this.namespace);
			if (perceiver != null) {
				final InterpolatedWorldNode perceiverNode = perceiver.getWorldNode();
				if (perceiverNode == null) {
					Log.error("REACT: percieverNode is null for: " + perceiver.getOid());
				} else {
					final Point perceiverLocation = perceiverNode.getLoc();
					final float distance = Point.distanceTo(perceiverLocation, wnode.getLoc());
					if (notifyData.hasAggroRadius(oid)) {
						final float aggroRadius = notifyData.getAggroRadius(oid);
						if (distance < aggroRadius) {
							if (!notifyData.isOidInAggroRadius(oid)) {
								notifyData.addOidInAggroRadius(oid);
								final NotifyAggroRadiusMessage nmsg = new NotifyAggroRadiusMessage(notifyOid, oid);
								Engine.getAgent().sendBroadcast(nmsg);
							}
						} else {
							notifyData.removeOidInAggroRadius(oid);
						}
					}
					final float reactionRadius = notifyData.getReactionRadius();
					boolean inRadius = distance < reactionRadius;
					final boolean wasInRadius = notifyData.isOidInRadius(oid);
					if (inRadius == wasInRadius) {
						continue;
					}
					if (this.hystericalMargin != 0.0f) {
						if (wasInRadius) {
							inRadius = (distance < reactionRadius + this.hystericalMargin);
						} else {
							inRadius = (distance < reactionRadius - this.hystericalMargin);
						}
						if (inRadius == wasInRadius) {
							continue;
						}
					}
					if (inRadius) {
						notifyData.addOidInRadius(oid);
					} else {
						notifyData.removeOidInRadius(oid);
					}
					if (this.notifyCallback != null) {
						this.notifyCallback.notifyReactionRadius(notifyOid, oid, inRadius, wasInRadius);
					} else {
						final NotifyReactionRadiusMessage nmsg2 = new NotifyReactionRadiusMessage(notifyOid, oid, inRadius, wasInRadius);
						Engine.getAgent().sendBroadcast(nmsg2);
					}
				}
			} else {
				Log.warn("ObjectTracker.updateEntity: No perceiver for oid " + notifyOid + " in namespace " + this.namespace);
			}
		}
	}

	@Override
	public void dispatchMessage(final Message message, final int flags, final MessageCallback callback) {
		Engine.defaultDispatchMessage(message, flags, callback);
	}

	protected void handlePerception(final PerceptionMessage perceptionMessage) {
		final OID targetOid = perceptionMessage.getTarget();
		final List<PerceptionMessage.ObjectNote> gain = perceptionMessage.getGainObjects();
		final List<PerceptionMessage.ObjectNote> lost = perceptionMessage.getLostObjects();
		if (Log.loggingDebug) {
			Log.debug("ObjectTracker.handlePerception: start instanceOid=" + this.instanceOid + " " + ((gain == null) ? 0 : gain.size()) + " gain and " + ((lost == null) ? 0 : lost.size()) + " lost");
		}
		if (gain != null) {
			for (final PerceptionMessage.ObjectNote note : gain) {
				this.maybeAddRemoteObject(note);
			}
		}
		if (lost != null) {
			for (final PerceptionMessage.ObjectNote note : lost) {
				this.maybeRemoveRemoteObject(note.getSubject(), note, targetOid);
			}
		}
	}

	protected void maybeRemoveRemoteObject(final OID subjectOid, final PerceptionMessage.ObjectNote objectNote, final OID targetOid) {
		if (this.remoteObjectFilter != null && this.remoteObjectFilter.objectShouldBeTracked(subjectOid, objectNote)) {
			return;
		}
		this.removeRemoteObject(subjectOid, targetOid, null);
	}

	@Override
	public void handleMessage(final Message msg, final int flags) {
		if (msg instanceof PerceptionMessage) {
			this.handlePerception((PerceptionMessage) msg);
		} else if (msg instanceof WorldManagerClient.UpdateWorldNodeMessage) {
			final WorldManagerClient.UpdateWorldNodeMessage wnodeMsg = (WorldManagerClient.UpdateWorldNodeMessage) msg;
			final OID oid = wnodeMsg.getSubject();
			final EntityWithWorldNode obj = (EntityWithWorldNode) EntityManager.getEntityByNamespace(oid, this.namespace);
			if (obj == null) {
				if (Log.loggingDebug) {
					Log.debug("ObjectTracker.handleMessage: ignoring updateWNMsg for oid " + oid + " because EntityWithWorldNode for oid not found");
				}
				return;
			}
			final BasicWorldNode bwnode = wnodeMsg.getWorldNode();
			InterpolatedWorldNode iwnode = obj.getWorldNode();
			if (iwnode != null) {
				obj.setDirLocOrient(bwnode);
			} else {
				iwnode = new InterpolatedWorldNode(bwnode);
				obj.setWorldNode(iwnode);
			}
			this.updateEntity(obj);
		} else {
			Log.error("ObjectTracker.handleMessage: unknown message type=" + msg.getMsgType() + " class=" + msg.getClass().getName());
		}
	}

	static {
		MSG_TYPE_NOTIFY_REACTION_RADIUS = MessageType.intern("ao.NOTIFY_REACTION_RADIUS");
		MSG_TYPE_NOTIFY_AGGRO_RADIUS = MessageType.intern("ao.NOTIFY_AGGRO_RADIUS");
	}

	public static class NotifyReactionRadiusMessage extends TargetMessage {
		protected boolean inRadius;
		protected boolean wasInRadius;
		private static final long serialVersionUID = 1L;

		public NotifyReactionRadiusMessage() {
		}

		public NotifyReactionRadiusMessage(final OID notifyOid, final OID subjectOid, final boolean inRadius, final boolean wasInRadius) {
			super(ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS, notifyOid, subjectOid);
			this.inRadius = inRadius;
			this.wasInRadius = wasInRadius;
		}

		public void setInRadius(final boolean value) {
			this.inRadius = value;
		}

		public boolean getInRadius() {
			return this.inRadius;
		}

		public void setWasInRadius(final boolean value) {
			this.wasInRadius = value;
		}

		public boolean getWasInRadius() {
			return this.wasInRadius;
		}
	}

	public static class NotifyAggroRadiusMessage extends TargetMessage {
		private static final long serialVersionUID = 1L;

		public NotifyAggroRadiusMessage() {
		}

		public NotifyAggroRadiusMessage(final OID notifyOid, final OID subjectOid) {
			super(ObjectTracker.MSG_TYPE_NOTIFY_AGGRO_RADIUS, notifyOid, subjectOid);
		}
	}

	protected class Entry extends LinkedList<OID> {
		protected OID oid;
		private static final long serialVersionUID = 1L;

		public Entry(final OID oid) {
			this.oid = oid;
		}

		public void activate(final ObjectType objType) {
			final EntityWithWorldNode obj = ObjectTracker.this.entityFactory.createEntity(this.oid, null, -1);
			final Entity entity = obj.getEntity();
			entity.setType(objType);
			if (Log.loggingDebug) {
				Log.debug("ObjectTracker.Entry.activate: obj=" + obj + " objType=" + objType);
			}
			EntityManager.registerEntityByNamespace((Entity) obj, ObjectTracker.this.namespace);
		}

		public void deactivate() {
			if (Log.loggingDebug) {
				Log.debug("ObjectTracker.Entry.deactivate: oid=" + this.oid + " instanceOid=" + ObjectTracker.this.instanceOid + " namespace=" + ObjectTracker.this.namespace.getName());
			}
			EntityManager.removeEntityByNamespace(this.oid, ObjectTracker.this.namespace);
		}
	}

	protected class NotifyData {
		protected Integer reactionRadius;
		protected Set<OID> oidsInRadius;
		protected HashMap<OID, Integer> aggroRadii;
		protected Set<OID> oidsInAggroRadius;

		NotifyData(final Integer reactionRadius) {
			this.reactionRadius = reactionRadius;
			this.oidsInRadius = new HashSet<OID>();
			this.aggroRadii = new HashMap<OID, Integer>();
			this.oidsInAggroRadius = new HashSet<OID>();
		}

		Integer getReactionRadius() {
			return this.reactionRadius;
		}

		boolean isOidInRadius(final OID oid) {
			return this.oidsInRadius.contains(oid);
		}

		boolean addOidInRadius(final OID oid) {
			return this.oidsInRadius.add(oid);
		}

		boolean removeOidInRadius(final OID oid) {
			return this.oidsInRadius.remove(oid);
		}

		List<OID> getOidsInRadius() {
			return new LinkedList<OID>(this.oidsInRadius);
		}

		void addOidAggroRange(final OID oid, final int aggroRange) {
			this.aggroRadii.put(oid, aggroRange);
		}

		void removeOidAggroRange(final OID target) {
			this.aggroRadii.remove(target);
		}

		int getAggroRadius(final OID oid) {
			return this.aggroRadii.get(oid);
		}

		boolean hasAggroRadius(final OID oid) {
			return this.aggroRadii.containsKey(oid);
		}

		boolean isOidInAggroRadius(final OID oid) {
			return this.oidsInAggroRadius.contains(oid);
		}

		boolean addOidInAggroRadius(final OID oid) {
			return this.oidsInAggroRadius.add(oid);
		}

		boolean removeOidInAggroRadius(final OID oid) {
			return this.oidsInAggroRadius.remove(oid);
		}
	}

	public static class TrackerFilter extends PerceptionFilter {
		private OID trackedInstanceOid;

		@Override
		public boolean matchRemaining(final Message msg) {
			Log.debug("TrackerFilter.match checking message with data: " + msg.toString());
			if (!super.matchRemaining(msg)) {
				return false;
			}
			if (msg instanceof WorldManagerClient.UpdateWorldNodeMessage) {
				final WorldManagerClient.UpdateWorldNodeMessage message = (WorldManagerClient.UpdateWorldNodeMessage) msg;
				final OID instanceOid = message.getWorldNode().getInstanceOid();
				Log.debug("TrackerFilter.match checking message with subject: " + message.getSubject() + " instanceOid: " + instanceOid);
				return instanceOid.equals(this.trackedInstanceOid);
			}
			return true;
		}

		public OID getTrackedInstanceOid() {
			return this.trackedInstanceOid;
		}

		public void setTrackedInstanceOid(final OID instanceOid) {
			this.trackedInstanceOid = instanceOid;
		}
	}

	public interface RemoteObjectFilter {
		boolean objectShouldBeTracked(final OID p0, final PerceptionMessage.ObjectNote p1);
	}

	public interface NotifyReactionRadiusCallback {
		void notifyReactionRadius(final OID p0, final OID p1, final boolean p2, final boolean p3);
	}
}
