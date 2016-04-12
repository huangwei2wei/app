// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.messages.PropertyMessage;
import atavism.agis.objects.BasicQuestState;
import atavism.agis.objects.QuestState;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;
import atavism.agis.objects.AgisStates;
import atavism.msgsys.Message;
import atavism.server.engine.OID;
import atavism.agis.plugins.QuestClient;
import atavism.msgsys.MessageTypeFilter;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.SubjectFilter;
import atavism.server.util.Log;
import java.util.ArrayList;
import atavism.server.util.Logger;
import java.util.List;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class OpenBehavior extends Behavior implements Runnable, MessageCallback
{
    Long eventSub;
    Long eventSub2;
    Long statusSub;
    List<Integer> itemsHeld;
    int itemLimit;
    int numItems;
    int respawnTime;
    static final Logger log;
    private static final long serialVersionUID = 1L;
    
    static {
        log = new Logger("OpenBehavior");
    }
    
    public OpenBehavior() {
        this.eventSub = null;
        this.eventSub2 = null;
        this.statusSub = null;
        this.itemsHeld = new ArrayList<Integer>();
        this.itemLimit = 0;
        this.numItems = 0;
        this.respawnTime = 30000;
    }
    
    public void initialize() {
        final OID mobOid = this.getObjectStub().getOid();
        if (Log.loggingDebug) {
            OpenBehavior.log.debug("QuestBehavior.initialize: my moboid=" + mobOid);
        }
        final SubjectFilter filter = new SubjectFilter(mobOid);
        filter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
        filter.addType(AgisInventoryClient.MSG_TYPE_REQ_OPEN_MOB);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(QuestClient.MSG_TYPE_QUEST_ITEM_UPDATE);
        this.eventSub2 = Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this);
    }
    
    public void activate() {
    }
    
    public void deactivate() {
        this.lock.lock();
        try {
            if (this.eventSub != null) {
                Engine.getAgent().removeSubscription(this.eventSub);
                this.eventSub = null;
            }
            if (this.eventSub2 != null) {
                Engine.getAgent().removeSubscription(this.eventSub2);
                this.eventSub2 = null;
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
        if (msg instanceof WorldManagerClient.UpdateMessage) {
            final WorldManagerClient.UpdateMessage updateMsg = (WorldManagerClient.UpdateMessage)msg;
            this.processUpdateMsg(updateMsg);
        }
        else if (msg instanceof AgisInventoryClient.RequestOpenMobMessage) {
            final AgisInventoryClient.RequestOpenMobMessage reqMsg = (AgisInventoryClient.RequestOpenMobMessage)msg;
            this.processReqOpenMobMsg(reqMsg);
        }
        else if (msg instanceof QuestClient.StateStatusChangeMessage) {
            final QuestClient.StateStatusChangeMessage nMsg = (QuestClient.StateStatusChangeMessage)msg;
            this.processStateStatusChangeMsg(nMsg);
        }
        else {
            if (!(msg instanceof QuestClient.QuestItemUpdateMessage)) {
                OpenBehavior.log.error("onMessage: got unknown msg: " + msg);
                return;
            }
            final QuestClient.QuestItemUpdateMessage nMsg2 = (QuestClient.QuestItemUpdateMessage)msg;
            this.processQuestItemUpdateMsg(nMsg2);
        }
    }
    
    private void processQuestItemUpdateMsg(final QuestClient.QuestItemUpdateMessage msg) {
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = msg.getSubject();
        final List<Integer> itemsRequired = msg.getItemsRequired();
        boolean hasAvailableItem = false;
        for (final int item : itemsRequired) {
            for (final int itemHeld : this.itemsHeld) {
                if (item == itemHeld) {
                    hasAvailableItem = true;
                    break;
                }
            }
        }
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(playerOid, myOid);
        propMsg.setProperty(AgisStates.ItemAvailable.toString(), (Serializable)hasAvailableItem);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    private void processStateStatusChangeMsg(final QuestClient.StateStatusChangeMessage msg) {
        final OID playerOid = msg.getSubject();
        final int questRef = msg.getQuestRef();
        this.handleQuestState(playerOid);
    }
    
    protected void giveItemsToPlayer(final OID myOid, final OID playerOid, final HashMap<Integer, QuestState> activeQuests) {
        for (final int item : this.itemsHeld) {
            for (final QuestState qs : activeQuests.values()) {
                if (!(qs instanceof BasicQuestState)) {
                    continue;
                }
                final BasicQuestState bqs = (BasicQuestState)qs;
                final List<BasicQuestState.CollectionGoalStatus> cgsList = bqs.getGoalsStatus();
                for (final BasicQuestState.CollectionGoalStatus cgs : cgsList) {
                    if (this.itemsHeld.contains(cgs.templateName) && cgs.currentCount < cgs.targetCount) {
                        this.giveItemToPlayer(playerOid, item);
                        break;
                    }
                }
            }
        }
        if (this.itemLimit != 0) {
            --this.numItems;
            if (this.numItems < 1) {
                final PropertyMessage propMsg = new PropertyMessage(this.obj.getOid());
                propMsg.setProperty("objectEmpty", (Serializable)true);
                Engine.getAgent().sendBroadcast((Message)propMsg);
            }
        }
    }
    
    public void run() {
        this.numItems = this.itemLimit;
    }
    
    protected void giveItemToPlayer(final OID playerOid, final int itemID) {
        this.lock.lock();
        try {
            final Template overrideTemplate = new Template();
            overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
            final OID itemOid = ObjectManagerClient.generateObject(itemID, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
            if (Log.loggingDebug) {
                Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid + ", bagOid=" + playerOid + ", adding to inventory");
            }
            final boolean rv = InventoryClient.addItem(playerOid, playerOid, playerOid, itemOid);
            if (Log.loggingDebug) {
                Log.debug("processReqConcludedMsg: createitem: oid=" + itemOid + ", added, rv=" + rv);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    private void processReqOpenMobMsg(final AgisInventoryClient.RequestOpenMobMessage reqMsg) {
        final OID myOid = this.getObjectStub().getOid();
        final OID playerOid = reqMsg.getPlayerOid();
        final HashMap<Integer, QuestState> activeQuests = QuestClient.getActiveQuests(playerOid);
        this.giveItemsToPlayer(myOid, playerOid, activeQuests);
    }
    
    public void processUpdateMsg(final WorldManagerClient.UpdateMessage msg) {
        final OID myOid = msg.getSubject();
        final OID playerOid = msg.getTarget();
        this.handleQuestState(playerOid);
    }
    
    protected void handleQuestState(final OID playerOid) {
        final OID myOid = this.getObjectStub().getOid();
        final HashMap<Integer, QuestState> activeQuests = QuestClient.getActiveQuests(playerOid);
        boolean hasAvailableItem = false;
        for (final QuestState qs : activeQuests.values()) {
            if (!(qs instanceof BasicQuestState)) {
                continue;
            }
            final BasicQuestState bqs = (BasicQuestState)qs;
            final List<BasicQuestState.CollectionGoalStatus> cgsList = bqs.getGoalsStatus();
            for (final BasicQuestState.CollectionGoalStatus cgs : cgsList) {
                if (this.itemsHeld.contains(cgs.templateName) && cgs.currentCount < cgs.targetCount) {
                    hasAvailableItem = true;
                }
            }
        }
        Log.debug("OPEN: sending hasitemavailable property update for player: " + playerOid + " and mob: " + myOid + "value = " + hasAvailableItem);
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(playerOid, myOid);
        propMsg.setProperty(AgisStates.ItemAvailable.toString(), (Serializable)hasAvailableItem);
        Engine.getAgent().sendBroadcast((Message)propMsg);
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
}
