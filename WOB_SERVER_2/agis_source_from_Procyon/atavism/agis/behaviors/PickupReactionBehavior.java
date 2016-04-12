// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.objects.ObjectStub;
import atavism.server.messages.PropertyMessage;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.objects.ObjectTypes;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.Message;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.IFilter;
import atavism.server.objects.ObjectTracker;
import atavism.msgsys.SubjectFilter;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.server.util.Log;
import atavism.server.objects.SpawnData;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.ArrayList;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class PickupReactionBehavior extends Behavior implements MessageCallback
{
    protected ArrayList<OID> acceptableTargets;
    protected int radius;
    protected int itemID;
    protected HashMap<Integer, Integer> lootTables;
    protected boolean activated;
    Long eventSub;
    private static final long serialVersionUID = 1L;
    
    public PickupReactionBehavior() {
        this.acceptableTargets = new ArrayList<OID>();
        this.radius = 0;
        this.itemID = -1;
        this.lootTables = null;
        this.activated = false;
        this.eventSub = null;
    }
    
    public PickupReactionBehavior(final SpawnData data) {
        this.acceptableTargets = new ArrayList<OID>();
        this.radius = 0;
        this.itemID = -1;
        this.lootTables = null;
        this.activated = false;
        this.eventSub = null;
        final HashMap<String, Serializable> dataProps = (HashMap<String, Serializable>)data.getProperty("props");
        if (dataProps != null) {
            final ArrayList<OID> targets = dataProps.get("acceptableTargets");
            if (targets != null) {
                this.acceptableTargets = targets;
            }
            Log.error("PICKUP: creating pickup behav with targets:" + this.acceptableTargets);
            final Integer duration = dataProps.get("duration");
            if (duration != null) {
                final Despawn despawnTimer = new Despawn();
                Engine.getExecutor().schedule(despawnTimer, duration, TimeUnit.MILLISECONDS);
            }
        }
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        MobManagerPlugin.getTracker(this.obj.getInstanceOid()).addReactionRadius(this.obj.getOid(), this.radius);
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            this.activated = false;
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription(this.eventSub);
                this.eventSub = null;
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (!this.activated) {
            return;
        }
        if (msg.getMsgType() == ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS) {
            final ObjectTracker.NotifyReactionRadiusMessage nMsg = (ObjectTracker.NotifyReactionRadiusMessage)msg;
            if (nMsg.getInRadius()) {
                this.reaction(nMsg);
            }
        }
    }
    
    public void reaction(final ObjectTracker.NotifyReactionRadiusMessage nMsg) {
        final OID player = nMsg.getSubject();
        if (!WorldManagerClient.getObjectInfo(player).objType.equals(ObjectTypes.player)) {
            return;
        }
        if (!this.acceptableTargets.isEmpty() && !this.acceptableTargets.contains(player)) {
            return;
        }
        if (!this.activated) {
            return;
        }
        final WorldManagerClient.ExtensionMessage pickUpMsg = new WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_PICKUP_ITEM, (String)null, player);
        pickUpMsg.setProperty("itemID", (Serializable)this.itemID);
        pickUpMsg.setProperty("lootTables", (Serializable)this.lootTables);
        pickUpMsg.setProperty("count", (Serializable)1);
        Engine.getAgent().sendBroadcast((Message)pickUpMsg);
        this.activated = false;
        final PropertyMessage propMsg = new PropertyMessage(this.obj.getOid());
        propMsg.setProperty("objectEmpty", (Serializable)true);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public void setItemID(final int itemID) {
        this.itemID = itemID;
    }
    
    public int getItemID() {
        return this.itemID;
    }
    
    public void setLootTables(final HashMap<Integer, Integer> tables) {
        this.lootTables = tables;
    }
    
    public HashMap<Integer, Integer> getLootTables() {
        return this.lootTables;
    }
    
    public void setAcceptableTargets(final ArrayList<OID> targets) {
        this.acceptableTargets = targets;
    }
    
    public ArrayList<OID> getAcceptableTargets() {
        return this.acceptableTargets;
    }
    
    public void addAcceptableTarget(final OID target) {
        this.acceptableTargets.add(target);
    }
    
    public class Despawn implements Runnable
    {
        @Override
        public void run() {
            if (PickupReactionBehavior.this.activated) {
                PickupReactionBehavior.this.activated = false;
                final PropertyMessage propMsg = new PropertyMessage(PickupReactionBehavior.this.obj.getOid());
                propMsg.setProperty("objectEmpty", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)propMsg);
            }
        }
    }
}
