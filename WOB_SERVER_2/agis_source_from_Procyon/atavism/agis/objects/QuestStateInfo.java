// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.plugins.QuestClient;
import atavism.server.util.Log;
import atavism.server.objects.ObjectTypes;
import atavism.server.objects.ObjectType;
import atavism.server.engine.Namespace;
import atavism.server.engine.OID;
import atavism.server.engine.InterpolatedWorldNode;
import java.util.ArrayList;
import java.util.HashMap;
import atavism.server.objects.Entity;

public class QuestStateInfo extends Entity
{
    protected int id;
    protected int currentCategory;
    private HashMap<Integer, HashMap<Integer, QuestState>> activeQuests;
    private HashMap<Integer, ArrayList<Integer>> completedQuests;
    InterpolatedWorldNode node;
    protected transient OID groupOid;
    protected transient OID groupMemberOid;
    private static final long serialVersionUID = 1L;
    
    public QuestStateInfo() {
        this.activeQuests = new HashMap<Integer, HashMap<Integer, QuestState>>();
        this.completedQuests = new HashMap<Integer, ArrayList<Integer>>();
        this.groupOid = null;
        this.groupMemberOid = null;
        this.setNamespace(Namespace.QUEST);
    }
    
    public QuestStateInfo(final OID objOid) {
        super(objOid);
        this.activeQuests = new HashMap<Integer, HashMap<Integer, QuestState>>();
        this.completedQuests = new HashMap<Integer, ArrayList<Integer>>();
        this.groupOid = null;
        this.groupMemberOid = null;
        this.setNamespace(Namespace.QUEST);
    }
    
    public String toString() {
        return "[Entity: " + this.getName() + ":" + this.getOid() + "]";
    }
    
    public ObjectType getType() {
        return ObjectTypes.questStateInfo;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public int getCurrentCategory() {
        return this.currentCategory;
    }
    
    public void setCurrentCategory(final int category) {
        Log.debug("QSI: setting current category to: " + category + " from: " + this.currentCategory);
        this.currentCategory = category;
    }
    
    public void categoryUpdated(final int category) {
        for (final QuestState qs : this.activeQuests.get(this.currentCategory).values()) {
            qs.deactivate();
            final WorldManagerClient.TargetedExtensionMessage rMsg = new WorldManagerClient.TargetedExtensionMessage(QuestClient.MSG_TYPE_REMOVE_QUEST_RESP, "ao.REMOVE_QUEST_RESP", qs.getPlayerOid(), qs.getQuestOid());
            Engine.getAgent().sendBroadcast((Message)rMsg);
        }
        this.currentCategory = category;
        if (!this.activeQuests.containsKey(category)) {
            this.activeQuests.put(category, new HashMap<Integer, QuestState>());
        }
        for (final QuestState qs : this.activeQuests.get(this.currentCategory).values()) {
            qs.activate();
        }
        if (!this.completedQuests.containsKey(category)) {
            this.completedQuests.put(category, new ArrayList<Integer>());
        }
        Engine.getPersistenceManager().setDirty((Entity)this);
    }
    
    public HashMap<Integer, Boolean> getAllQuests() {
        final HashMap<Integer, Boolean> allQuests = new HashMap<Integer, Boolean>();
        for (final int key : this.getActiveQuests(this.currentCategory).keySet()) {
            allQuests.put(key, false);
        }
        for (final int key : this.getCompletedQuests(this.currentCategory)) {
            allQuests.put(key, true);
        }
        Log.debug("QSI: all quests: " + allQuests);
        return allQuests;
    }
    
    public void addActiveQuest(final int questID, final QuestState qs) {
        if (Log.loggingDebug) {
            Log.debug("QuestStateInfo.addActiveQuest: adding quest=" + questID + " to obj=" + this + " in category=" + this.currentCategory);
        }
        this.lock.lock();
        try {
            if (this.getActiveQuests(this.currentCategory).containsKey(questID)) {
                return;
            }
            this.getActiveQuests(this.currentCategory).put(questID, qs);
            qs.activate();
            Engine.getPersistenceManager().setDirty((Entity)this);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void removeActiveQuest(final int questID) {
        if (Log.loggingDebug) {
            Log.debug("QuestStateInfo.removeActiveQuest: removing quest=" + questID + " from obj=" + this);
        }
        this.lock.lock();
        try {
            final QuestState qs = this.getActiveQuests(this.currentCategory).remove(questID);
            qs.deactivate();
            Engine.getPersistenceManager().setDirty((Entity)this);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, QuestState> getCurrentActiveQuests() {
        this.lock.lock();
        try {
            return this.getActiveQuests(this.currentCategory);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setCurrentActiveQuests(final HashMap<Integer, QuestState> activeQuests) {
        this.lock.lock();
        try {
            this.activeQuests.put(this.currentCategory, new HashMap<Integer, QuestState>(activeQuests));
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, HashMap<Integer, QuestState>> getActiveQuests() {
        this.lock.lock();
        try {
            return new HashMap<Integer, HashMap<Integer, QuestState>>(this.activeQuests);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setActiveQuests(final HashMap<Integer, HashMap<Integer, QuestState>> activeQuests) {
        this.lock.lock();
        try {
            this.activeQuests = new HashMap<Integer, HashMap<Integer, QuestState>>(activeQuests);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, QuestState> getActiveQuests(final int category) {
        this.lock.lock();
        try {
            if (!this.activeQuests.containsKey(category)) {
                this.activeQuests.put(category, new HashMap<Integer, QuestState>());
            }
            return this.activeQuests.get(category);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addCompletedQuest(final int questID) {
        if (Log.loggingDebug) {
            Log.debug("QuestStateInfo.addCompletedQuest: adding quest=" + questID + " to obj=" + this);
        }
        this.lock.lock();
        try {
            if (this.getCompletedQuests(this.currentCategory).contains(questID)) {
                return;
            }
            this.getCompletedQuests(this.currentCategory).add(questID);
            Engine.getPersistenceManager().setDirty((Entity)this);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void removeCompletedQuest(final int questID) {
        if (Log.loggingDebug) {
            Log.debug("QuestStateInfo.removeCompletedQuest: removing quest=" + questID + " from obj=" + this);
        }
        this.lock.lock();
        try {
            this.getCompletedQuests(this.currentCategory).remove(questID);
            Engine.getPersistenceManager().setDirty((Entity)this);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public ArrayList<Integer> getCurrentCompletedQuests() {
        this.lock.lock();
        try {
            return this.getCompletedQuests(this.currentCategory);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setCurrentCompletedQuests(final ArrayList<Integer> completedQuests) {
        this.lock.lock();
        try {
            this.completedQuests.put(this.currentCategory, new ArrayList<Integer>(completedQuests));
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public HashMap<Integer, ArrayList<Integer>> getCompletedQuests() {
        this.lock.lock();
        try {
            return new HashMap<Integer, ArrayList<Integer>>(this.completedQuests);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setCompletedQuests(final HashMap<Integer, ArrayList<Integer>> completedQuests) {
        this.lock.lock();
        try {
            this.completedQuests = new HashMap<Integer, ArrayList<Integer>>(completedQuests);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public ArrayList<Integer> getCompletedQuests(final int category) {
        this.lock.lock();
        try {
            if (!this.completedQuests.containsKey(category)) {
                this.completedQuests.put(category, new ArrayList<Integer>());
            }
            return this.completedQuests.get(category);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean concludeQuest(final int questID, final boolean repeatable) {
        if (!this.activeQuests.get(this.currentCategory).containsKey(questID)) {
            return false;
        }
        boolean concluded = false;
        this.lock.lock();
        try {
            concluded = this.activeQuests.get(this.currentCategory).get(questID).handleConclude();
            if (concluded) {
                final QuestState qs = this.activeQuests.get(this.currentCategory).remove(questID);
                if (!repeatable) {
                    this.getCompletedQuests(this.currentCategory).add(questID);
                }
                Engine.getPersistenceManager().setDirty((Entity)this);
                qs.sendStateStatusChange();
                Log.debug("QSI: Moved quest: " + questID + " from active to completed.");
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        return concluded;
    }
    
    public InterpolatedWorldNode getWorldNode() {
        return this.node;
    }
    
    public void setWorldNode(final InterpolatedWorldNode node) {
        this.node = node;
    }
    
    public void setGroupOid(final OID groupOid) {
        this.groupOid = groupOid;
    }
    
    public OID getGroupOid() {
        return this.groupOid;
    }
    
    public void setGroupMemberOid(final OID groupMemberOid) {
        this.groupMemberOid = groupMemberOid;
    }
    
    public OID getGroupMemberOid() {
        return this.groupMemberOid;
    }
    
    public boolean isGrouped() {
        return this.groupOid != null;
    }
}
