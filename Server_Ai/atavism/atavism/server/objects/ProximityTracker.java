// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.BasicWorldNode;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.math.Point;
import atavism.server.messages.PerceptionMessage;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.Message;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import atavism.server.util.Log;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import atavism.server.engine.OID;
import atavism.server.engine.Namespace;
import atavism.msgsys.MessageDispatch;

public class ProximityTracker implements MessageDispatch
{
    protected Namespace namespace;
    protected OID instanceOid;
    protected float hystericalMargin;
    protected ObjectTracker.NotifyReactionRadiusCallback notifyCallback;
    protected ObjectTracker.RemoteObjectFilter remoteObjectFilter;
    protected Updater updater;
    protected Thread updaterThread;
    protected boolean running;
    protected Map<OID, PerceiverData> perceiverDataMap;
    protected Lock lock;
    
    public ProximityTracker(final Namespace namespace, final OID instanceOid) {
        this.hystericalMargin = 0.0f;
        this.notifyCallback = null;
        this.remoteObjectFilter = null;
        this.updater = null;
        this.updaterThread = null;
        this.running = true;
        this.perceiverDataMap = new HashMap<OID, PerceiverData>();
        this.lock = LockFactory.makeLock("ProximityTrackerLock");
        this.initialize(namespace, instanceOid);
    }
    
    public ProximityTracker(final Namespace namespace, final OID instanceOid, final float hystericalMargin, final ObjectTracker.NotifyReactionRadiusCallback notifyCallback, final ObjectTracker.RemoteObjectFilter remoteObjectFilter) {
        this.hystericalMargin = 0.0f;
        this.notifyCallback = null;
        this.remoteObjectFilter = null;
        this.updater = null;
        this.updaterThread = null;
        this.running = true;
        this.perceiverDataMap = new HashMap<OID, PerceiverData>();
        this.lock = LockFactory.makeLock("ProximityTrackerLock");
        this.hystericalMargin = hystericalMargin;
        this.notifyCallback = notifyCallback;
        this.remoteObjectFilter = remoteObjectFilter;
        this.initialize(namespace, instanceOid);
    }
    
    private void initialize(final Namespace namespace, final OID instanceOid) {
        this.namespace = namespace;
        this.instanceOid = instanceOid;
        this.updater = new Updater();
        final Thread updaterThread = new Thread(this.updater);
        updaterThread.start();
    }
    
    public OID getInstanceOid() {
        return this.instanceOid;
    }
    
    public void addTrackedPerceiver(final OID perceiverOid, final InterpolatedWorldNode wnode, final Integer reactionRadius) {
        this.lock.lock();
        try {
            if (this.perceiverDataMap.containsKey(perceiverOid)) {
                Log.error("ProximityTracker.addTrackedPerceiver: perceiverOid " + perceiverOid + " is already in the set of local objects, for ProximityTracker instance " + this);
                return;
            }
            final PerceiverData perceiverData = new PerceiverData(perceiverOid, reactionRadius, wnode);
            this.perceiverDataMap.put(perceiverOid, perceiverData);
        }
        finally {
            this.lock.unlock();
        }
        if (Log.loggingDebug) {
            Log.debug("ProximityTracker.addTrackedPerceiver: perceiverOid=" + perceiverOid + " reactionRadius=" + reactionRadius + " instanceOid=" + this.instanceOid);
        }
    }
    
    public boolean hasTrackedPerceiver(final OID oid) {
        this.lock.lock();
        try {
            return this.perceiverDataMap.containsKey(oid);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void removeTrackedPerceiver(final OID perceiverOid) {
        this.lock.lock();
        try {
            final PerceiverData perceiverData = this.perceiverDataMap.get(perceiverOid);
            if (perceiverData != null) {
                if (Log.loggingDebug) {
                    Log.debug("ProximityTracker.removeTrackedPerceiver: perceiverOid " + perceiverOid + ", inRangeOids count " + perceiverData.inRangeOids.size());
                }
                for (final OID perceivedOid : perceiverData.perceivedOids) {
                    final PerceiverData perceivedData = this.perceiverDataMap.get(perceivedOid);
                    if (perceivedData != null) {
                        perceivedData.perceivedOids.remove(perceiverOid);
                        if (!perceivedData.inRangeOids.contains(perceiverOid)) {
                            continue;
                        }
                        perceivedData.inRangeOids.remove(perceiverOid);
                        this.performNotification(perceiverOid, perceivedOid, false, true);
                    }
                }
                perceiverData.perceivedOids.clear();
                perceiverData.inRangeOids.clear();
                this.perceiverDataMap.remove(perceiverOid);
            }
            else {
                Log.warn("ProximityTracker.removeTrackedPerceiver: For oid=" + perceiverOid + ", didn't find PerceiverData");
            }
        }
        finally {
            this.lock.unlock();
        }
        if (Log.loggingDebug) {
            Log.debug("ProximityTracker.removeTrackedPerceiver: oid=" + perceiverOid + " instanceOid=" + this.instanceOid);
        }
    }
    
    public List<OID> getOidsInRadius(final OID perceiverOid) {
        this.lock.lock();
        try {
            final PerceiverData perceiverData = this.perceiverDataMap.get(perceiverOid);
            if (perceiverData == null) {
                Log.error("ProximityTracker.getOidsInRadius: perceptionData for oid " + perceiverOid + " is null");
                return new LinkedList<OID>();
            }
            return new LinkedList<OID>(perceiverData.inRangeOids);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void dispatchMessage(final Message message, final int flags, final MessageCallback callback) {
        Engine.defaultDispatchMessage(message, flags, callback);
    }
    
    protected boolean maybeAddPerceivedObject(final PerceptionMessage.ObjectNote objectNote) {
        final ObjectType objType = objectNote.getObjectType();
        final OID perceivedOid = objectNote.getSubject();
        final OID perceiverOid = objectNote.getTarget();
        if (perceivedOid.equals(perceiverOid)) {
            return true;
        }
        boolean callbackNixedIt = false;
        if (this.remoteObjectFilter != null) {
            callbackNixedIt = !this.remoteObjectFilter.objectShouldBeTracked(perceivedOid, objectNote);
        }
        if (callbackNixedIt || !objType.isMob()) {
            return false;
        }
        if (Log.loggingDebug) {
            Log.debug("ProximityTracker.maybeAddPerceivedObject: oid=" + perceivedOid + " objType=" + objType + " detected by " + perceiverOid + ", instanceOid=" + this.instanceOid);
        }
        this.lock.lock();
        try {
            final PerceiverData perceiverData = this.perceiverDataMap.get(perceiverOid);
            if (perceiverData == null) {
                Log.error("ProximityTracker.maybeAddPerceivedObject: got perception msg with perceived obj oid=" + perceivedOid + " for unknown perceiver=" + perceiverOid);
                return false;
            }
            perceiverData.perceivedOids.add(perceivedOid);
            final PerceiverData perceivedData = this.perceiverDataMap.get(perceivedOid);
            if (perceivedData != null) {
                this.testProximity(perceiverData, perceivedData, true, false);
            }
        }
        finally {
            this.lock.unlock();
        }
        return true;
    }
    
    protected void testProximity(final PerceiverData perceiverData, final PerceiverData perceivedData, final boolean interpolatePerceiver, final boolean interpolatePerceived) {
        final Point perceiverLoc = interpolatePerceiver ? perceiverData.wnode.getLoc() : perceiverData.lastLoc;
        final Point perceivedLoc = interpolatePerceived ? perceivedData.wnode.getLoc() : perceivedData.lastLoc;
        final float distance = Point.distanceTo(perceiverLoc, perceivedLoc);
        final float reactionRadius = perceiverData.reactionRadius;
        final OID perceiverInstance = perceiverData.wnode.getInstanceOid();
        final OID perceivedInstance = perceivedData.wnode.getInstanceOid();
        final boolean sameInstance = perceiverInstance.equals(perceivedInstance);
        boolean inRadius = sameInstance && distance < reactionRadius;
        final boolean wasInRadius = perceiverData.inRangeOids.contains(perceivedData.perceiverOid);
        if (inRadius == wasInRadius) {
            return;
        }
        if (sameInstance && this.hystericalMargin != 0.0f) {
            if (wasInRadius) {
                inRadius = (distance < reactionRadius + this.hystericalMargin);
            }
            else {
                inRadius = (distance < reactionRadius - this.hystericalMargin);
            }
            if (inRadius == wasInRadius) {
                return;
            }
        }
        if (inRadius) {
            perceiverData.inRangeOids.add(perceivedData.perceiverOid);
            perceivedData.inRangeOids.add(perceiverData.perceiverOid);
        }
        else {
            perceiverData.inRangeOids.remove(perceivedData.perceiverOid);
            perceivedData.inRangeOids.remove(perceiverData.perceiverOid);
        }
        this.performNotification(perceiverData.perceiverOid, perceivedData.perceiverOid, inRadius, wasInRadius);
    }
    
    protected void performNotification(final OID perceiverOid, final OID perceivedOid, final boolean inRadius, final boolean wasInRadius) {
        if (Log.loggingDebug) {
            Log.debug("ProximityTracker.performNotification: perceiverOid " + perceiverOid + ", perceivedOid " + perceivedOid + ", inRadius " + inRadius + ", wasInRadius " + wasInRadius);
        }
        if (this.notifyCallback != null) {
            this.notifyCallback.notifyReactionRadius(perceivedOid, perceiverOid, inRadius, wasInRadius);
            this.notifyCallback.notifyReactionRadius(perceiverOid, perceivedOid, inRadius, wasInRadius);
        }
        else {
            ObjectTracker.NotifyReactionRadiusMessage nmsg = new ObjectTracker.NotifyReactionRadiusMessage(perceivedOid, perceiverOid, inRadius, wasInRadius);
            Engine.getAgent().sendBroadcast(nmsg);
            nmsg = new ObjectTracker.NotifyReactionRadiusMessage(perceiverOid, perceivedOid, inRadius, wasInRadius);
            Engine.getAgent().sendBroadcast(nmsg);
        }
    }
    
    protected void updateEntity(final PerceiverData perceiverData) {
        final OID perceiverOid = perceiverData.perceiverOid;
        this.lock.lock();
        try {
            for (final OID perceivedOid : perceiverData.perceivedOids) {
                if (perceiverOid.compareTo(perceivedOid) == 0) {
                    continue;
                }
                final PerceiverData perceivedData = this.perceiverDataMap.get(perceivedOid);
                if (perceivedData == null) {
                    continue;
                }
                this.testProximity(perceiverData, perceivedData, false, true);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void handlePerception(final PerceptionMessage perceptionMessage) {
        final OID targetOid = perceptionMessage.getTarget();
        final List<PerceptionMessage.ObjectNote> gain = perceptionMessage.getGainObjects();
        final List<PerceptionMessage.ObjectNote> lost = perceptionMessage.getLostObjects();
        if (Log.loggingDebug) {
            Log.debug("ProximityTracker.handlePerception: targetOid + " + targetOid + ", instanceOid=" + this.instanceOid + " " + ((gain == null) ? 0 : gain.size()) + " gain and " + ((lost == null) ? 0 : lost.size()) + " lost");
        }
        if (gain != null) {
            for (final PerceptionMessage.ObjectNote note : gain) {
                this.maybeAddPerceivedObject(note);
            }
        }
        if (lost != null) {
            for (final PerceptionMessage.ObjectNote note : lost) {
                this.maybeRemovePerceivedObject(note.getSubject(), note, targetOid);
            }
        }
    }
    
    public void handleUpdateWorldNode(final long oid, final WorldManagerClient.UpdateWorldNodeMessage wnodeMsg) {
        final PerceiverData perceiverData = this.perceiverDataMap.get(oid);
        if (perceiverData == null) {
            if (Log.loggingDebug) {
                Log.debug("ProximityTracker.handleMessage: ignoring updateWNMsg for oid " + oid + " because PerceptionData for oid not found");
            }
            return;
        }
        final BasicWorldNode bwnode = wnodeMsg.getWorldNode();
        if (Log.loggingDebug) {
            Log.debug("ProximityTracker.handleMessage: UpdateWnode for " + oid + ", loc " + bwnode.getLoc() + ", dir " + bwnode.getDir());
        }
        if (perceiverData.wnode != null) {
            perceiverData.previousLoc = perceiverData.lastLoc;
            perceiverData.wnode.setDirLocOrient(bwnode);
            perceiverData.wnode.setInstanceOid(bwnode.getInstanceOid());
            perceiverData.lastLoc = perceiverData.wnode.getLoc();
        }
        else {
            Log.error("ProximityTracker.handleMessage: In UpdateWorldNodeMessage for oid " + oid + ", perceiverData.wnode is null!");
        }
        this.updateEntity(perceiverData);
    }
    
    protected void maybeRemovePerceivedObject(final OID oid, final PerceptionMessage.ObjectNote objectNote, final OID targetOid) {
        if (this.remoteObjectFilter != null && this.remoteObjectFilter.objectShouldBeTracked(oid, objectNote)) {
            return;
        }
        this.removePerceivedObject(targetOid, oid);
    }
    
    protected void removePerceivedObject(final OID targetOid, final OID oid) {
        this.lock.lock();
        try {
            final PerceiverData perceiverData = this.perceiverDataMap.get(targetOid);
            if (perceiverData == null) {
                if (Log.loggingDebug) {
                    Log.debug("ProximityTracker.removePerceivedObject: No perceiverData for oid " + targetOid);
                }
                return;
            }
            perceiverData.perceivedOids.remove(oid);
            if (perceiverData.inRangeOids.contains(oid)) {
                this.performNotification(targetOid, oid, true, false);
                perceiverData.inRangeOids.remove(oid);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setRunning(final boolean running) {
        this.running = running;
    }
    
    class Updater implements Runnable
    {
        @Override
        public void run() {
            while (ProximityTracker.this.running) {
                try {
                    this.update();
                }
                catch (AORuntimeException e) {
                    Log.exception("ProximityTracker.Updater.run caught AORuntimeException", e);
                }
                catch (Exception e2) {
                    Log.exception("ProximityTracker.Updater.run caught exception", e2);
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e3) {
                    Log.warn("Updater: " + e3);
                    e3.printStackTrace();
                }
            }
        }
        
        protected void update() {
            Log.debug("Updater.update: in update");
            List<OID> perceiverOids = null;
            ProximityTracker.this.lock.lock();
            try {
                perceiverOids = new ArrayList<OID>(ProximityTracker.this.perceiverDataMap.keySet());
            }
            finally {
                ProximityTracker.this.lock.unlock();
            }
            for (final OID perceiverOid : perceiverOids) {
                final PerceiverData perceiverData = ProximityTracker.this.perceiverDataMap.get(perceiverOid);
                if (perceiverData != null) {
                    perceiverData.previousLoc = perceiverData.lastLoc;
                    perceiverData.lastLoc = perceiverData.wnode.getLoc();
                }
            }
            for (final OID perceiverOid : perceiverOids) {
                final PerceiverData perceiverData = ProximityTracker.this.perceiverDataMap.get(perceiverOid);
                if (perceiverData == null) {
                    continue;
                }
                if (perceiverData.previousLoc != null && Point.distanceToSquared(perceiverData.previousLoc, perceiverData.lastLoc) < 100.0f) {
                    continue;
                }
                final ArrayList<OID> perceivedOids = new ArrayList<OID>(perceiverData.perceivedOids);
                for (final OID perceivedOid : perceivedOids) {
                    final PerceiverData perceivedData = ProximityTracker.this.perceiverDataMap.get(perceivedOid);
                    if (perceivedData == null) {
                        continue;
                    }
                    ProximityTracker.this.testProximity(perceiverData, perceivedData, false, false);
                }
            }
        }
    }
    
    protected class PerceiverData
    {
        OID perceiverOid;
        Integer reactionRadius;
        Entity perceiverEntity;
        InterpolatedWorldNode wnode;
        Point lastLoc;
        Point previousLoc;
        Set<OID> perceivedOids;
        Set<OID> inRangeOids;
        
        public PerceiverData(final OID perceiverOid, final Integer reactionRadius, final InterpolatedWorldNode wnode) {
            this.perceivedOids = new HashSet<OID>();
            this.inRangeOids = new HashSet<OID>();
            this.perceiverOid = perceiverOid;
            this.reactionRadius = reactionRadius;
            this.wnode = wnode;
            this.lastLoc = wnode.getLoc();
        }
    }
}
