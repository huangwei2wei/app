// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.objects.ObjectStub;
import java.util.LinkedList;
import java.util.Iterator;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.msgsys.Message;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.IFilter;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.SubjectFilter;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import atavism.agis.plugins.AgisMobPlugin;
import atavism.server.engine.Engine;
import atavism.server.util.Log;
import atavism.server.objects.SpawnData;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.util.ArrayList;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class LootBehavior extends Behavior implements MessageCallback
{
    protected ArrayList<OID> acceptableTargets;
    protected int radius;
    protected int itemID;
    protected HashMap<Integer, Integer> lootTables;
    protected boolean activated;
    Long eventSub;
    private static final long serialVersionUID = 1L;
    
    public LootBehavior() {
        this.acceptableTargets = new ArrayList<OID>();
        this.radius = 0;
        this.itemID = -1;
        this.lootTables = null;
        this.activated = false;
        this.eventSub = null;
    }
    
    public LootBehavior(final SpawnData data) {
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
            Log.debug("LOOT: creating loot behav with targets:" + this.acceptableTargets);
            final Despawn despawnTimer = new Despawn();
            Engine.getExecutor().schedule(despawnTimer, AgisMobPlugin.lootObjectDespawn, TimeUnit.SECONDS);
        }
    }
    
    public void initialize() {
        final SubjectFilter filter = new SubjectFilter(this.obj.getOid());
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
        this.activated = true;
        if (this.acceptableTargets != null) {
            for (final OID target : this.acceptableTargets) {
                final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(target, this.obj.getOid());
                propMsg.setProperty("lootable", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)propMsg);
                Log.debug("LOOT: set lootable to player: " + target + " for obj: " + this.obj.getOid());
            }
            AgisInventoryClient.generateLoot(this.obj.getOid());
        }
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            this.activated = false;
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription((long)this.eventSub);
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
        if (msg instanceof PropertyMessage) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final OID subject = propMsg.getSubject();
            final LinkedList<OID> loot = (LinkedList<OID>)propMsg.getProperty("loot");
            if (loot != null && loot.isEmpty() && this.activated) {
                Log.debug("LOOT: despawning loot object as it is now empty");
                this.activated = false;
                final PropertyMessage newPropMsg = new PropertyMessage(this.obj.getOid());
                newPropMsg.setProperty("objectEmpty", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)newPropMsg);
            }
        }
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
            if (LootBehavior.this.activated) {
                LootBehavior.this.activated = false;
                final PropertyMessage propMsg = new PropertyMessage(LootBehavior.this.obj.getOid());
                propMsg.setProperty("objectEmpty", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)propMsg);
            }
        }
    }
}
