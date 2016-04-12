// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import java.util.Iterator;
import atavism.agis.objects.AgisStates;
import java.io.Serializable;
import atavism.msgsys.Message;
import atavism.server.engine.OID;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.messages.PropertyMessage;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.util.Log;
import java.util.ArrayList;
import atavism.server.util.Logger;
import java.util.List;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class ChestBehavior extends Behavior implements Runnable, MessageCallback
{
    Long eventSub;
    Long statusSub;
    List<Integer> itemsHeld;
    int itemLimit;
    int numItems;
    boolean singleItemPickup;
    int respawnTime;
    static final Logger log;
    private static final long serialVersionUID = 1L;
    
    static {
        log = new Logger("ChestBehavior");
    }
    
    public ChestBehavior() {
        this.eventSub = null;
        this.statusSub = null;
        this.itemsHeld = new ArrayList<Integer>();
        this.itemLimit = 0;
        this.numItems = 0;
        this.singleItemPickup = false;
        this.respawnTime = 300000;
    }
    
    public void initialize() {
        final OID mobOid = this.getObjectStub().getOid();
        if (Log.loggingDebug) {
            ChestBehavior.log.debug("QuestBehavior.initialize: my moboid=" + mobOid);
        }
        final SubjectFilter filter = new SubjectFilter(mobOid);
        filter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
        filter.addType(AgisInventoryClient.MSG_TYPE_REQ_OPEN_MOB);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
        if (!this.singleItemPickup) {
            AgisInventoryClient.generateLoot(this.getObjectStub().getOid());
        }
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription(this.eventSub);
                this.eventSub = null;
            }
            if (this.statusSub != null) {
                Engine.getAgent().removeSubscription(this.statusSub);
                this.statusSub = null;
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof AgisInventoryClient.RequestOpenMobMessage) {
            final AgisInventoryClient.RequestOpenMobMessage reqMsg = (AgisInventoryClient.RequestOpenMobMessage)msg;
            this.processReqOpenMobMsg(reqMsg);
        }
        else if (msg instanceof WorldManagerClient.UpdateMessage) {
            final WorldManagerClient.UpdateMessage updateMsg = (WorldManagerClient.UpdateMessage)msg;
            this.processUpdateMsg(updateMsg);
        }
        else {
            if (!(msg instanceof PropertyMessage)) {
                ChestBehavior.log.error("onMessage: got unknown msg: " + msg);
                return;
            }
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final Boolean lootable = (Boolean)propMsg.getProperty("lootable");
            if (lootable != null && !lootable) {
                Log.debug("CHEST: got lootable prop: " + lootable);
                final PropertyMessage propMsg2 = new PropertyMessage(this.obj.getOid());
                propMsg2.setProperty("objectEmpty", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)propMsg2);
            }
        }
    }
    
    public void processUpdateMsg(final WorldManagerClient.UpdateMessage msg) {
        final OID myOid = msg.getSubject();
        final OID playerOid = msg.getTarget();
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(playerOid, myOid);
        propMsg.setProperty(AgisStates.ItemAvailable.toString(), (Serializable)true);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    protected void giveItemsToPlayer(final OID myOid, final OID playerOid) {
        for (final int item : this.itemsHeld) {
            this.giveItemToPlayer(playerOid, item);
        }
        if (this.itemLimit != 0) {
            --this.numItems;
            if (this.numItems < 1) {
                final PropertyMessage propMsg = new PropertyMessage(this.obj.getOid());
                propMsg.setProperty("objectEmpty", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)propMsg);
                final WorldManagerClient.TargetedPropertyMessage propMsg2 = new WorldManagerClient.TargetedPropertyMessage(playerOid, myOid);
                propMsg2.setProperty(AgisStates.ItemAvailable.toString(), (Serializable)false);
                Engine.getAgent().sendBroadcast((Message)propMsg2);
            }
        }
    }
    
    public void run() {
        this.numItems = this.itemLimit;
    }
    
    protected void giveItemToPlayer(final OID playerOid, final int item) {
        this.lock.lock();
        try {
            final Template overrideTemplate = new Template();
            overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
            final OID itemOid = ObjectManagerClient.generateObject(item, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
            if (Log.loggingDebug) {
                Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid + ", bagOid=" + playerOid + ", adding to inventory");
            }
            final boolean rv = InventoryClient.addItem(playerOid, playerOid, playerOid, itemOid);
            if (Log.loggingDebug) {
                Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid + ", added, rv=" + rv);
            }
            WorldManagerClient.sendObjChatMsg(playerOid, 2, "You have received something... ");
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    private void processReqOpenMobMsg(final AgisInventoryClient.RequestOpenMobMessage reqMsg) {
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = reqMsg.getPlayerOid();
        if (this.singleItemPickup) {
            this.giveItemsToPlayer(myOid, playerOid);
        }
        else {
            AgisInventoryClient.getLootList(playerOid, myOid);
        }
    }
    
    public void setItemsHeld(final ArrayList<Integer> items) {
        this.itemsHeld = items;
    }
    
    public List getItemsHeld() {
        return this.itemsHeld;
    }
    
    public void setItemLimit(final int itemLimit) {
        this.itemLimit = itemLimit;
    }
    
    public int getItemLimit() {
        return this.itemLimit;
    }
    
    public void setNumItems(final int numItems) {
        this.numItems = numItems;
    }
    
    public int getNumItems() {
        return this.numItems;
    }
    
    public void setRespawnTime(final int time) {
        this.respawnTime = time;
    }
    
    public int getRespawnTime() {
        return this.respawnTime;
    }
    
    public void setSingleItemPickup(final boolean singleItemPickup) {
        this.singleItemPickup = singleItemPickup;
    }
    
    public boolean getSingleItemPickup() {
        return this.singleItemPickup;
    }
}
