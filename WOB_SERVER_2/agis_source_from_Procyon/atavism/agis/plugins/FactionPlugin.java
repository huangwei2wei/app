// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.util.List;
import atavism.server.messages.PerceptionMessage;
import atavism.msgsys.FilterUpdate;
import atavism.agis.objects.PlayerFactionData;
import atavism.agis.core.Agis;
import atavism.agis.objects.Faction;
import atavism.server.plugins.MobManagerPlugin;
import atavism.msgsys.Message;
import java.io.Serializable;
import atavism.server.util.Log;
import atavism.server.objects.ObjectTypes;
import java.util.Iterator;
import atavism.msgsys.MessageTrigger;
import atavism.server.messages.PerceptionTrigger;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.messages.PropertyMessage;
import atavism.server.messages.LogoutMessage;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Hook;
import atavism.server.objects.ObjectTracker;
import atavism.server.messages.PerceptionFilter;
import java.util.ArrayList;
import atavism.server.engine.OID;
import java.util.HashMap;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class FactionPlugin extends EnginePlugin
{
    private static final Logger log;
    public static String FACTION_PLUGIN_NAME;
    protected static HashMap<OID, ArrayList<OID>> objectsInRange;
    public static final int HatedRep = -3000;
    public static final int DislikedRep = -1500;
    public static final int NeutralRep = -500;
    public static final int FriendlyRep = 500;
    public static final int HonouredRep = 1500;
    public static final int ExaltedRep = 3000;
    public static final int Hated = -2;
    public static final int Disliked = -1;
    public static final int Neutral = 0;
    public static final int Friendly = 1;
    public static final int Honoured = 2;
    public static final int Exalted = 3;
    public static final int Attackable = -1;
    public static final int Healable = 1;
    public static final int Neither = 0;
    protected PerceptionFilter perceptionFilter;
    protected long perceptionSubId;
    
    static {
        log = new Logger("Faction");
        FactionPlugin.FACTION_PLUGIN_NAME = "FactionPlugin";
        FactionPlugin.objectsInRange = new HashMap<OID, ArrayList<OID>>();
    }
    
    public FactionPlugin() {
        super("Faction");
        this.setPluginType("Faction");
    }
    
    public String getName() {
        return FactionPlugin.FACTION_PLUGIN_NAME;
    }
    
    public void onActivate() {
        this.getHookManager().addHook(ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS, (Hook)new AttitudeGetHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, (Hook)new SpawnedHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, (Hook)new DespawnedHook());
        this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook)new LogoutHook());
        this.getHookManager().addHook(PropertyMessage.MSG_TYPE_PROPERTY, (Hook)new PropertyHook());
        this.getHookManager().addHook(FactionClient.MSG_TYPE_UPDATE_PVP_STATE, (Hook)new SetPvPHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_PERCEPTION_INFO, (Hook)new PerceptionHook());
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(ObjectTracker.MSG_TYPE_NOTIFY_REACTION_RADIUS);
        filter.addType(CombatClient.MSG_TYPE_FACTION_UPDATE);
        filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
        filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
        filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
        filter.addType(FactionClient.MSG_TYPE_UPDATE_PVP_STATE);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        (this.perceptionFilter = new PerceptionFilter()).addType(WorldManagerClient.MSG_TYPE_PERCEPTION_INFO);
        this.perceptionFilter.setMatchAllSubjects(true);
        final PerceptionTrigger perceptionTrigger = new PerceptionTrigger();
        this.perceptionSubId = Engine.getAgent().createSubscription((IFilter)this.perceptionFilter, (MessageCallback)this, 0, perceptionTrigger);
    }
    
    private void addObjectInRange(final OID subject, final OID target) {
        synchronized (FactionPlugin.objectsInRange) {
            if (FactionPlugin.objectsInRange.containsKey(subject)) {
                if (!FactionPlugin.objectsInRange.get(subject).contains(target)) {
                    FactionPlugin.objectsInRange.get(subject).add(target);
                }
            }
            else {
                final ArrayList<OID> targets = new ArrayList<OID>();
                targets.add(target);
                FactionPlugin.objectsInRange.put(subject, targets);
            }
        }
        // monitorexit(FactionPlugin.objectsInRange)
    }
    
    private void removeObjectInRange(final OID subject, final OID instanceOID) {
        synchronized (FactionPlugin.objectsInRange) {
            if (FactionPlugin.objectsInRange.containsKey(subject)) {
                for (final OID target : FactionPlugin.objectsInRange.get(subject)) {
                    if (FactionPlugin.objectsInRange.containsKey(target)) {
                        FactionPlugin.objectsInRange.get(target).remove(subject);
                    }
                }
                FactionPlugin.objectsInRange.remove(subject);
            }
        }
        // monitorexit(FactionPlugin.objectsInRange)
    }
    
    private void calculateInteractionState(final OID subjectOid, final OID targetOid) {
        final boolean subjectIsPlayer = WorldManagerClient.getObjectInfo(subjectOid).objType.equals(ObjectTypes.player);
        int subjectFaction = -1;
        int targetFaction = -1;
        try {
            subjectFaction = (int)EnginePlugin.getObjectProperty(subjectOid, WorldManagerClient.NAMESPACE, "faction");
            targetFaction = (int)EnginePlugin.getObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "faction");
        }
        catch (NullPointerException e) {
            Log.debug("FACTION: subject or target faction was null");
            return;
        }
        final String subjectTempFaction = (String)EnginePlugin.getObjectProperty(subjectOid, WorldManagerClient.NAMESPACE, "temporaryFaction");
        final String targetTempFaction = (String)EnginePlugin.getObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "temporaryFaction");
        int reaction = 0;
        if (subjectTempFaction != null && !subjectTempFaction.equals("") && targetTempFaction != null && !targetTempFaction.equals("")) {
            if (subjectTempFaction.equals(targetTempFaction)) {
                reaction = 1;
            }
            else {
                reaction = -2;
            }
        }
        else {
            reaction = this.determineFactionStanding(subjectOid, targetOid, subjectFaction, targetFaction);
        }
        if (subjectIsPlayer) {
            this.sendTargetReaction(subjectOid, targetOid, reaction);
        }
        int standing = -1;
        boolean needsFactionCheck = true;
        boolean canBeHealed = true;
        if (targetTempFaction != null && !targetTempFaction.equals("")) {
            if (subjectTempFaction != null && !subjectTempFaction.equals("")) {
                if (targetTempFaction.equals(subjectTempFaction)) {
                    standing = 1;
                    needsFactionCheck = false;
                }
                else {
                    standing = -2;
                    needsFactionCheck = false;
                }
            }
            canBeHealed = false;
        }
        if (needsFactionCheck) {
            standing = this.determineFactionStanding(targetOid, subjectOid, targetFaction, subjectFaction);
        }
        int targetType = 0;
        if (!canBeHealed && standing == 1) {
            targetType = 0;
        }
        else if (standing > 0) {
            targetType = 1;
        }
        else {
            targetType = -1;
        }
        this.sendTargetUpdate(subjectOid, targetOid, targetType, standing, subjectIsPlayer);
        this.addObjectInRange(subjectOid, targetOid);
    }
    
    private void sendTargetReaction(final OID subjectOid, final OID targetOid, final int reaction) {
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(subjectOid, targetOid);
        propMsg.setProperty("reaction", (Serializable)reaction);
        Engine.getAgent().sendBroadcast((Message)propMsg);
        Log.debug("ATTITUDE: setting reaction for target: " + targetOid + " against: " + subjectOid + " to " + reaction);
    }
    
    private void sendTargetUpdate(final OID subjectOid, final OID targetOid, final int targetType, final int standing, final boolean subjectIsPlayer) {
        CombatClient.setTargetType(subjectOid, targetOid, targetType, "");
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(subjectOid, targetOid);
        propMsg.setProperty("targetType", (Serializable)targetType);
        Engine.getAgent().sendBroadcast((Message)propMsg);
        Log.debug("ATTITUDE: sent target type update for subject: " + subjectOid + " who is player? " + subjectIsPlayer + " and has standing: " + standing + " towards target: " + targetOid);
        if (!subjectIsPlayer) {
            final OID instanceOid = WorldManagerClient.getObjectInfo(subjectOid).instanceOid;
            if (standing < 0) {
                MobManagerPlugin.getTracker(instanceOid).addAggroRadius(subjectOid, targetOid, 15);
            }
            else {
                MobManagerPlugin.getTracker(instanceOid).removeAggroRadius(subjectOid, targetOid);
            }
        }
    }
    
    private int determineInteractionState(final OID subjectOid, final OID targetOid) {
        final Integer domeID = (Integer)EnginePlugin.getObjectProperty(subjectOid, WorldManagerClient.NAMESPACE, "domeID");
        final Integer targetDomeID = (Integer)EnginePlugin.getObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "domeID");
        if (domeID == null || domeID == -1 || targetDomeID == null || targetDomeID == -1) {
            return 0;
        }
        final boolean subjectIsPlayer = WorldManagerClient.getObjectInfo(subjectOid).objType.equals(ObjectTypes.player);
        final boolean targetIsPlayer = WorldManagerClient.getObjectInfo(targetOid).objType.equals(ObjectTypes.player);
        if (!subjectIsPlayer && !targetIsPlayer) {
            return 1;
        }
        return -2;
    }
    
    private int determineFactionStanding(final OID subjectOid, final OID targetOid, final int subjectFaction, final int targetFaction) {
        if (subjectFaction == targetFaction || subjectFaction < 1 || targetFaction < 1) {
            return 1;
        }
        final boolean subjectIsPlayer = WorldManagerClient.getObjectInfo(subjectOid).objType.equals(ObjectTypes.player);
        final boolean targetIsPlayer = WorldManagerClient.getObjectInfo(targetOid).objType.equals(ObjectTypes.player);
        Log.debug("FACTI: subjectIsPlayer: " + subjectIsPlayer + ", subjectFaction = " + subjectFaction + ", target = " + targetFaction);
        if (subjectIsPlayer) {
            final Faction newFaction = (Faction)Agis.FactionManager.get(targetFaction);
            if (!newFaction.getIsPublic()) {
                return this.calculateStanding(newFaction.getDefaultReputation(subjectFaction));
            }
            final HashMap<Integer, PlayerFactionData> pfdMap = (HashMap<Integer, PlayerFactionData>)EnginePlugin.getObjectProperty(subjectOid, WorldManagerClient.NAMESPACE, "factionData");
            if (!pfdMap.containsKey(targetFaction)) {
                Log.debug("FACTION: player " + subjectOid + " has not met faction " + targetFaction);
                pfdMap.put(targetFaction, Faction.addFactionToPlayer(subjectOid, targetOid, newFaction, subjectFaction));
            }
            Log.debug("FACTION: getting target faction: " + targetFaction + " from players FactionDataMap");
            final PlayerFactionData pfd = pfdMap.get(targetFaction);
            Log.debug("FACTION: got faction from players FactionDataMap");
            final int reputation = pfd.getReputation();
            Log.debug("FACTION: players reputation with faction in question: " + reputation);
            return this.calculateStanding(reputation);
        }
        else {
            if (!targetIsPlayer) {
                final Faction newFaction = (Faction)Agis.FactionManager.get(targetFaction);
                final int reputation2 = newFaction.getDefaultReputation(subjectFaction);
                return this.calculateStanding(reputation2);
            }
            final Faction newFaction = (Faction)Agis.FactionManager.get(subjectFaction);
            if (!newFaction.getIsPublic()) {
                return this.calculateStanding(newFaction.getDefaultReputation(targetFaction));
            }
            final HashMap<Integer, PlayerFactionData> pfdMap = (HashMap<Integer, PlayerFactionData>)EnginePlugin.getObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "factionData");
            if (!pfdMap.containsKey(subjectFaction)) {
                Log.debug("FACTION: player " + subjectOid + " has not met faction " + subjectFaction);
                pfdMap.put(subjectFaction, Faction.addFactionToPlayer(targetOid, subjectOid, newFaction, targetFaction));
            }
            Log.debug("FACTION: getting subject faction: " + subjectFaction + " from players FactionDataMap");
            final PlayerFactionData pfd = pfdMap.get(subjectFaction);
            Log.debug("FACTION: got faction from players FactionDataMap");
            final int reputation = pfd.getReputation();
            Log.debug("FACTION: players reputation with faction in question: " + reputation);
            return this.calculateStanding(reputation);
        }
    }
    
    private int calculateStanding(final int reputation) {
        if (reputation < -1500) {
            return -2;
        }
        if (reputation < -500) {
            return -1;
        }
        if (reputation < 500) {
            return 0;
        }
        if (reputation < 1500) {
            return 1;
        }
        if (reputation < 3000) {
            return 2;
        }
        return 3;
    }
    
    class LogoutHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final LogoutMessage message = (LogoutMessage)msg;
            final OID playerOid = message.getSubject();
            return true;
        }
    }
    
    class SpawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.SpawnedMessage spawnedMsg = (WorldManagerClient.SpawnedMessage)msg;
            final OID objOid = spawnedMsg.getSubject();
            if (WorldManagerClient.getObjectInfo(objOid).objType == ObjectTypes.player) {
                Log.debug("SPAWNED: creating tracker for player: " + objOid);
                if (FactionPlugin.this.perceptionFilter.addTarget(objOid)) {
                    final FilterUpdate filterUpdate = new FilterUpdate(1);
                    filterUpdate.addFieldValue(1, (Object)objOid);
                    Engine.getAgent().applyFilterUpdate(FactionPlugin.this.perceptionSubId, filterUpdate);
                }
            }
            return true;
        }
    }
    
    class DespawnedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.DespawnedMessage despawnedMsg = (WorldManagerClient.DespawnedMessage)msg;
            final OID objOid = despawnedMsg.getSubject();
            if (FactionPlugin.this.perceptionFilter.hasTarget(objOid)) {
                FactionPlugin.this.perceptionFilter.removeTarget(objOid);
                final FilterUpdate filterUpdate = new FilterUpdate(1);
                filterUpdate.removeFieldValue(1, (Object)objOid);
                Engine.getAgent().applyFilterUpdate(FactionPlugin.this.perceptionSubId, filterUpdate);
            }
            FactionPlugin.this.removeObjectInRange(objOid, despawnedMsg.getInstanceOid());
            return true;
        }
    }
    
    class PerceptionHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final PerceptionMessage perceptionMessage = (PerceptionMessage)msg;
            final OID perceiverOid = perceptionMessage.getTarget();
            final List<PerceptionMessage.ObjectNote> gain = (List<PerceptionMessage.ObjectNote>)perceptionMessage.getGainObjects();
            final List<PerceptionMessage.ObjectNote> lost = (List<PerceptionMessage.ObjectNote>)perceptionMessage.getLostObjects();
            if (Log.loggingDebug) {
                Log.debug("FactionPlugin.PerceptionHook.processMessage: perceiverOid " + perceiverOid + " " + ((gain == null) ? 0 : gain.size()) + " gain and " + ((lost == null) ? 0 : lost.size()) + " lost");
            }
            if (gain != null) {
                for (final PerceptionMessage.ObjectNote note : gain) {
                    this.processNote(perceiverOid, note, true);
                }
            }
            if (lost != null) {
                for (final PerceptionMessage.ObjectNote note : lost) {
                    this.processNote(perceiverOid, note, false);
                }
            }
            return true;
        }
        
        protected void processNote(final OID perceiverOid, final PerceptionMessage.ObjectNote note, final boolean add) {
            final OID perceivedOid = note.getSubject();
            if (add) {
                Log.debug("FACTION: calculating states between perceived nodes: " + perceiverOid + " - " + perceivedOid);
                FactionPlugin.this.calculateInteractionState(perceiverOid, perceivedOid);
                FactionPlugin.this.calculateInteractionState(perceivedOid, perceiverOid);
                FactionPlugin.this.addObjectInRange(perceiverOid, perceivedOid);
            }
            else {
                synchronized (FactionPlugin.objectsInRange) {
                    if (FactionPlugin.objectsInRange.containsKey(perceiverOid)) {
                        FactionPlugin.objectsInRange.get(perceiverOid).remove(perceivedOid);
                    }
                    if (FactionPlugin.objectsInRange.containsKey(perceivedOid)) {
                        FactionPlugin.objectsInRange.get(perceivedOid).remove(perceiverOid);
                    }
                }
                // monitorexit(FactionPlugin.objectsInRange)
            }
        }
    }
    
    class SetPvPHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage cimsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID playerOid = cimsg.getSubject();
            Log.debug("PVP: got getBlueprintMessage");
            final boolean pvpState = (boolean)cimsg.getProperty("pvpState");
            if (pvpState) {
                EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)("pvp_" + playerOid.toString()));
            }
            else {
                EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "temporaryFaction", (Serializable)"");
            }
            return true;
        }
    }
    
    class PropertyHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final PropertyMessage propMsg = (PropertyMessage)msg;
            final OID objOid = propMsg.getSubject();
            final String faction = (String)propMsg.getProperty("faction");
            final String temporaryFaction = (String)propMsg.getProperty("temporaryFaction");
            if (faction != null || temporaryFaction != null) {
                Log.debug("PROPERTY: got faction property update for subject: " + objOid + " who has objectsInRange: " + FactionPlugin.objectsInRange.get(objOid));
                if (FactionPlugin.objectsInRange.containsKey(objOid)) {
                    for (final OID target : FactionPlugin.objectsInRange.get(objOid)) {
                        FactionPlugin.this.calculateInteractionState(objOid, target);
                        FactionPlugin.this.calculateInteractionState(target, objOid);
                    }
                }
                return true;
            }
            final Integer domeID = (Integer)propMsg.getProperty("domeID");
            if (domeID != null) {
                Log.debug("PROPERTY: got domeID property update for subject: " + objOid + " who has objectsInRange: " + FactionPlugin.objectsInRange.get(objOid));
                if (FactionPlugin.objectsInRange.containsKey(objOid)) {
                    for (final OID target2 : FactionPlugin.objectsInRange.get(objOid)) {
                        FactionPlugin.this.calculateInteractionState(objOid, target2);
                        FactionPlugin.this.calculateInteractionState(target2, objOid);
                    }
                }
                return true;
            }
            return true;
        }
    }
    
    class AttitudeGetHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ObjectTracker.NotifyReactionRadiusMessage nMsg = (ObjectTracker.NotifyReactionRadiusMessage)msg;
            final OID subjectOid = nMsg.getSubject();
            final OID targetOid = nMsg.getTarget();
            Log.debug("FACTION: get Attitude caught: " + nMsg);
            if (nMsg.getInRadius()) {
                FactionPlugin.this.calculateInteractionState(subjectOid, targetOid);
                FactionPlugin.this.calculateInteractionState(targetOid, subjectOid);
            }
            else {
                Log.debug("FACTION: target: " + targetOid + " is no longer in radius");
                synchronized (FactionPlugin.objectsInRange) {
                    if (FactionPlugin.objectsInRange.containsKey(subjectOid)) {
                        FactionPlugin.objectsInRange.get(subjectOid).remove(targetOid);
                    }
                    if (FactionPlugin.objectsInRange.containsKey(targetOid)) {
                        FactionPlugin.objectsInRange.get(targetOid).remove(subjectOid);
                    }
                }
                // monitorexit(FactionPlugin.objectsInRange)
            }
            Log.debug("FACTION: get Attitude completed");
            return true;
        }
    }
}
