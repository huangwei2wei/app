// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.messages.PropertyMessage;
import atavism.agis.core.AgisAbility;
import atavism.agis.core.AgisAbilityState;
import atavism.agis.core.Cooldown;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import atavism.server.network.AOByteBuffer;
import java.util.LinkedList;
import atavism.msgsys.TargetMessage;
import atavism.msgsys.SubjectMessage;
import atavism.server.plugins.WorldManagerClient;
import java.util.HashMap;
import java.util.List;
import atavism.server.objects.ObjectType;
import atavism.server.math.Point;
import java.io.Serializable;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;

public class CombatClient
{
    public static Namespace NAMESPACE;
    public static Namespace TEST_NAMESPACE;
    public static final MessageType MSG_TYPE_SET_COMBAT_INFO_STATE;
    public static final MessageType MSG_TYPE_AUTO_ATTACK;
    public static final MessageType MSG_TYPE_START_ABILITY;
    public static final MessageType MSG_TYPE_COOLDOWN;
    public static final MessageType MSG_TYPE_ABILITY_PROGRESS;
    public static final MessageType MSG_TYPE_DAMAGE;
    public static final MessageType MSG_TYPE_RELEASE_OBJECT;
    public static final MessageType MSG_TYPE_ABILITY_STATUS;
    public static final MessageType MSG_TYPE_ABILITY_UPDATE;
    public static final MessageType MSG_TYPE_GET_ABILITY;
    public static final MessageType MSG_TYPE_SKILL_UPDATE;
    public static final MessageType MSG_TYPE_ADD_SKILL;
    public static final MessageType MSG_TYPE_TRAINING_FAILED;
    public static final MessageType MSG_TYPE_COMBAT_MOB_DEATH;
    public static final MessageType MSG_TYPE_COMBAT_ABILITY_USED;
    public static final MessageType MSG_TYPE_COMBAT_STOP_AUTO_ATTACK;
    public static final MessageType MSG_TYPE_TARGET_TYPE;
    public static final MessageType MSG_TYPE_INTERRUPT_ABILITY;
    public static final MessageType MSG_TYPE_COMBAT_LOGOUT;
    public static final MessageType MSG_TYPE_FACTION_UPDATE;
    public static final MessageType MSG_TYPE_GET_AOE_TARGETS;
    public static final MessageType MSG_TYPE_UPDATE_ACTIONBAR;
    public static final MessageType MSG_TYPE_APPLY_EFFECT;
    public static final MessageType MSG_TYPE_ALTER_EXP;
    public static final MessageType MSG_TYPE_UPDATE_BREATH;
    public static final MessageType MSG_TYPE_UPDATE_FATIGUE;
    public static final MessageType MSG_TYPE_ALTER_HEARTS;
    public static final MessageType MSG_TYPE_KNOCKED_OUT;
    public static final MessageType MSG_TYPE_UPDATE_HEALTH_PROPS;
    public static final MessageType MSG_TYPE_REGEN_HEALTH_MANA;
    public static final MessageType MSG_TYPE_DECREMENT_WEAPON_USES;
    public static final MessageType MSG_TYPE_DISMOUNT;
    public static final String MSG_ATTACK_STATUS = "combat_attackStatus";
    public static final MessageType MSG_CLIENT_LEVEL_LOADED;
    
    static {
        CombatClient.NAMESPACE = null;
        CombatClient.TEST_NAMESPACE = null;
        MSG_TYPE_SET_COMBAT_INFO_STATE = MessageType.intern("ao.SET_COMBAT_INFO_STATE");
        MSG_TYPE_AUTO_ATTACK = MessageType.intern("ao.AUTO_ATTACK");
        MSG_TYPE_START_ABILITY = MessageType.intern("ao.START_ABILITY");
        MSG_TYPE_COOLDOWN = MessageType.intern("ao.COOLDOWN");
        MSG_TYPE_ABILITY_PROGRESS = MessageType.intern("ao.ABILITY_PROGRESS");
        MSG_TYPE_DAMAGE = MessageType.intern("ao.DAMAGE");
        MSG_TYPE_RELEASE_OBJECT = MessageType.intern("ao.RELEASE_OBJECT");
        MSG_TYPE_ABILITY_STATUS = MessageType.intern("ao.ABILITY_STATUS");
        MSG_TYPE_ABILITY_UPDATE = MessageType.intern("ao.ABILITY_UPDATE");
        MSG_TYPE_GET_ABILITY = MessageType.intern("ao.GET_ABILITY");
        MSG_TYPE_SKILL_UPDATE = MessageType.intern("ao.SKILL_UPDATE");
        MSG_TYPE_ADD_SKILL = MessageType.intern("ao.ADD_SKILL");
        MSG_TYPE_TRAINING_FAILED = MessageType.intern("ao.TRAINING_FAILED");
        MSG_TYPE_COMBAT_MOB_DEATH = MessageType.intern("ao.COMBAT_MOB_DEATH");
        MSG_TYPE_COMBAT_ABILITY_USED = MessageType.intern("combat.ABILITY_USED");
        MSG_TYPE_COMBAT_STOP_AUTO_ATTACK = MessageType.intern("combat.STOP_AUTO_ATTACK");
        MSG_TYPE_TARGET_TYPE = MessageType.intern("combat.TARGET_TYPE");
        MSG_TYPE_INTERRUPT_ABILITY = MessageType.intern("combat.INTERRUPT_ABILITY");
        MSG_TYPE_COMBAT_LOGOUT = MessageType.intern("combat.LOGOUT");
        MSG_TYPE_FACTION_UPDATE = MessageType.intern("faction.UPDATE");
        MSG_TYPE_GET_AOE_TARGETS = MessageType.intern("combat.GET_AOE_TARGETS");
        MSG_TYPE_UPDATE_ACTIONBAR = MessageType.intern("combat.UPDATE_ACTIONBAR");
        MSG_TYPE_APPLY_EFFECT = MessageType.intern("combat.APPLY_EFFECT");
        MSG_TYPE_ALTER_EXP = MessageType.intern("combat.ALTER_EXP");
        MSG_TYPE_UPDATE_BREATH = MessageType.intern("combat.UPDATE_BREATH");
        MSG_TYPE_UPDATE_FATIGUE = MessageType.intern("combat.UPDATE_FATIGUE");
        MSG_TYPE_ALTER_HEARTS = MessageType.intern("ao.ALTER_HEARTS");
        MSG_TYPE_KNOCKED_OUT = MessageType.intern("ao.KNOCKED_OUT");
        MSG_TYPE_UPDATE_HEALTH_PROPS = MessageType.intern("ao.UPDATE_HEALTH_PROPS");
        MSG_TYPE_REGEN_HEALTH_MANA = MessageType.intern("ao.REGEN_HEALTH_MANA");
        MSG_TYPE_DECREMENT_WEAPON_USES = MessageType.intern("ao.DECREMENT_WEAPON_USES");
        MSG_TYPE_DISMOUNT = MessageType.intern("ao.DISMOUNT");
        MSG_CLIENT_LEVEL_LOADED = MessageType.intern("ao.CLIENT_LEVEL_LOADED");
    }
    
    public static void setCombatInfoState(final OID oid, final String state) {
        final SetCombatInfoStateMessage msg = new SetCombatInfoStateMessage(oid, state, false);
        Engine.getAgent().sendBroadcast((Message)msg);
        if (Log.loggingDebug) {
            Log.debug("CombatClient.setCombatInfoState: oid=" + oid);
        }
    }
    
    public static void clearCombatInfoState(final OID oid, final String state) {
        final SetCombatInfoStateMessage msg = new SetCombatInfoStateMessage(oid, state, true);
        Engine.getAgent().sendBroadcast((Message)msg);
        if (Log.loggingDebug) {
            Log.debug("CombatClient.clearCombatInfoState: oid=" + oid);
        }
    }
    
    public static void autoAttack(final OID oid, final OID targetOid, final boolean status) {
        final AutoAttackMessage msg = new AutoAttackMessage(oid, targetOid, Boolean.valueOf(status));
        Engine.getAgent().sendBroadcast((Message)msg);
        if (Log.loggingDebug) {
            Log.debug("CombatClient.autoAttack: oid=" + oid + " targetOid=" + targetOid);
        }
    }
    
    public static void startAbility(final int abilityID, final OID oid, final OID targetOid, final OID itemOid) {
        startAbility(abilityID, oid, targetOid, (Serializable)itemOid, null);
    }
    
    public static void startAbility(final int abilityID, final OID oid, final OID targetOid, final Serializable item, final Point loc) {
        final StartAbilityMessage msg = new StartAbilityMessage(oid, abilityID, targetOid, item, loc);
        Engine.getAgent().sendBroadcast((Message)msg);
        if (Log.loggingDebug) {
            Log.debug("CombatClient.startAbility: oid=" + oid + " abilityID=" + abilityID + " targetOid=" + targetOid + " item=" + item);
        }
    }
    
    public static void releaseObject(final OID oid) {
        final ReleaseObjectMessage msg = new ReleaseObjectMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
        if (Log.loggingDebug) {
            Log.debug("CombatClient.releaseObject: oid=" + oid);
        }
    }
    
    public static void abilityUsed(final OID oid, final int skillType) {
        final abilityUsedMessage msg = new abilityUsedMessage(oid, skillType);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("COMBAT CLIENT: abilityUsedMessage hit 2");
    }
    
    public static void stopAutoAttack(final OID oid) {
        final stopAutoAttackMessage msg = new stopAutoAttackMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
        if (Log.loggingDebug) {
            Log.debug("CombatClient.stopAutoAttack: oid=" + oid);
        }
    }
    
    public static void setTargetType(final OID oid, final OID targetOid, final int targetType, final String species) {
        final TargetTypeMessage msg = new TargetTypeMessage(oid, targetOid, targetType, species);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static List<Long> getAoeTargets(final OID subjectOid, final Point loc, final Integer radius, final ObjectType objectType) {
        final getAoeTargetsMessage msg = new getAoeTargetsMessage(subjectOid, loc, radius, objectType);
        final List<Long> objectsIn = (List<Long>)Engine.getAgent().sendRPCReturnObject((Message)msg);
        Log.debug("COMBAT CLIENT: getAoeTargetsMessage hit 2");
        return objectsIn;
    }
    
    public static void updateActionBar(final OID oid, final int actionPosition, final String newAction) {
        final updateActionBarMessage msg = new updateActionBarMessage(oid, actionPosition, newAction);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("COMBAT CLIENT: updateActionBarMessage hit 2");
    }
    
    public static void applyEffect(final OID oid, final int effectID) {
        final applyEffectMessage msg = new applyEffectMessage(oid, effectID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("COMBAT CLIENT: applyEffectMessage hit 2");
    }
    
    public static void alterExp(final OID oid, final int expAmount) {
        final alterExpMessage msg = new alterExpMessage(oid, expAmount);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("COMBAT CLIENT: alterExpMessage hit 2");
    }
    
    public static void updateBreathStatus(final OID oid, final boolean underwater) {
        final updateBreathStatusMessage msg = new updateBreathStatusMessage(oid, underwater);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("COMBAT CLIENT: updateBreathStatusMessage hit 2");
    }
    
    public static void updateFatigueStatus(final OID oid, final boolean fatigue) {
        final updateFatigueStatusMessage msg = new updateFatigueStatusMessage(oid, fatigue);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("COMBATCLIENT: updateFatigueStatusMessage hit 2");
    }
    
    public static HashMap<String, String> getAbilityInfo(final String abilityName) {
        final WorldManagerClient.ExtensionMessage getAbilityMsg = new WorldManagerClient.ExtensionMessage();
        getAbilityMsg.setExtensionType("ao.GET_ABILITY");
        getAbilityMsg.setMsgType(CombatClient.MSG_TYPE_GET_ABILITY);
        getAbilityMsg.setProperty("abilityName", (Serializable)abilityName);
        return (HashMap<String, String>)Engine.getAgent().sendRPCReturnObject((Message)getAbilityMsg);
    }
    
    public static void addAbility(final String abilityName, final OID playerOid) {
        final WorldManagerClient.ExtensionMessage addAbilityMsg = new WorldManagerClient.ExtensionMessage();
        addAbilityMsg.setExtensionType("ao.ADD_ABILITY");
        addAbilityMsg.setProperty("abilityName", (Serializable)abilityName);
        addAbilityMsg.setSubject(playerOid);
        Engine.getAgent().sendBroadcast((Message)addAbilityMsg);
    }
    
    public static class SetCombatInfoStateMessage extends SubjectMessage
    {
        protected String state;
        protected boolean clearState;
        private static final long serialVersionUID = 1L;
        
        public SetCombatInfoStateMessage() {
            super(CombatClient.MSG_TYPE_SET_COMBAT_INFO_STATE);
        }
        
        public SetCombatInfoStateMessage(final OID oid, final String state, final boolean clearState) {
            super(CombatClient.MSG_TYPE_SET_COMBAT_INFO_STATE, oid);
            this.setState(state);
            this.setClearState(clearState);
        }
        
        public void setState(final String state) {
            this.state = state;
        }
        
        public String getState() {
            return this.state;
        }
        
        public void setClearState(final boolean clearState) {
            this.clearState = clearState;
        }
        
        public boolean getClearState() {
            return this.clearState;
        }
    }
    
    public static class CombatTargetMessage extends SubjectMessage
    {
        private OID targetOid;
        private static final long serialVersionUID = 1L;
        
        public CombatTargetMessage() {
            this.targetOid = null;
        }
        
        public CombatTargetMessage(final MessageType type) {
            super(type);
            this.targetOid = null;
        }
        
        public CombatTargetMessage(final MessageType type, final OID oid, final OID targetOid) {
            super(type, oid);
            this.targetOid = null;
            this.setTargetOid(targetOid);
        }
        
        public OID getTargetOid() {
            return this.targetOid;
        }
        
        public void setTargetOid(final OID oid) {
            this.targetOid = oid;
        }
    }
    
    public static class AutoAttackMessage extends CombatTargetMessage
    {
        Boolean status;
        private static final long serialVersionUID = 1L;
        
        public AutoAttackMessage() {
            super(CombatClient.MSG_TYPE_AUTO_ATTACK);
        }
        
        public AutoAttackMessage(final OID oid, final OID targetOid, final Boolean status) {
            super(CombatClient.MSG_TYPE_AUTO_ATTACK, oid, targetOid);
            this.setAttackStatus(status);
        }
        
        public void setAttackStatus(final Boolean status) {
            this.status = status;
        }
        
        public Boolean getAttackStatus() {
            return this.status;
        }
    }
    
    public static class AbilityUpdateMessage extends TargetMessage
    {
        List<Entry> entries;
        private static final long serialVersionUID = 1L;
        
        public AbilityUpdateMessage() {
            super(CombatClient.MSG_TYPE_ABILITY_UPDATE);
            this.entries = new LinkedList<Entry>();
        }
        
        public AbilityUpdateMessage(final OID targetOid, final OID subjectOid) {
            super(CombatClient.MSG_TYPE_ABILITY_UPDATE, targetOid, subjectOid);
            this.entries = new LinkedList<Entry>();
        }
        
        public void addAbility(final int abilityID, final String iconName, final String category) {
            final Entry entry = new Entry(abilityID, iconName, category);
            this.entries.add(entry);
        }
        
        public List<Entry> getAbilities() {
            return this.entries;
        }
        
        public AOByteBuffer toBuffer() {
            final AOByteBuffer buf = new AOByteBuffer(500);
            buf.putOID(this.getSubject());
            buf.putInt(56);
            buf.putInt(this.entries.size());
            for (final Entry entry : this.entries) {
                buf.putInt(entry.abilityID);
                buf.putString(entry.iconName);
                buf.putString(entry.category);
            }
            buf.flip();
            return buf;
        }
        
        class Entry implements Serializable
        {
            public int abilityID;
            public String iconName;
            public String category;
            private static final long serialVersionUID = 1L;
            
            public Entry(final int abilityID, final String iconName, final String category) {
                this.abilityID = abilityID;
                this.iconName = iconName;
                this.category = category;
            }
        }
    }
    
    public static class StartAbilityMessage extends CombatTargetMessage
    {
        private int abilityID;
        private Serializable item;
        private Point location;
        private static final long serialVersionUID = 1L;
        
        public StartAbilityMessage() {
            super(CombatClient.MSG_TYPE_START_ABILITY);
            this.abilityID = -1;
            this.item = null;
            this.location = null;
        }
        
        public StartAbilityMessage(final OID oid, final int abilityID, final OID targetOid, final Serializable item) {
            super(CombatClient.MSG_TYPE_START_ABILITY, oid, targetOid);
            this.abilityID = -1;
            this.item = null;
            this.location = null;
            this.setAbilityID(abilityID);
            this.setItem(item);
        }
        
        public StartAbilityMessage(final OID oid, final int abilityID, final OID targetOid, final Serializable item, final Point loc) {
            super(CombatClient.MSG_TYPE_START_ABILITY, oid, targetOid);
            this.abilityID = -1;
            this.item = null;
            this.location = null;
            this.setAbilityID(abilityID);
            this.setItem(item);
            this.setLocation(loc);
        }
        
        public int getAbilityID() {
            return this.abilityID;
        }
        
        public void setAbilityID(final int abilityID) {
            this.abilityID = abilityID;
        }
        
        public Serializable getItem() {
            return this.item;
        }
        
        public void setItem(final Serializable item) {
            this.item = item;
        }
        
        public Point getLocation() {
            return this.location;
        }
        
        public void setLocation(final Point loc) {
            this.location = loc;
        }
    }
    
    public static class DamageMessage extends SubjectMessage
    {
        protected Integer dmg;
        protected String dmgType;
        protected OID attackerOid;
        private static final long serialVersionUID = 1L;
        
        public DamageMessage() {
            super(CombatClient.MSG_TYPE_DAMAGE);
        }
        
        public DamageMessage(final OID targetOid, final OID attackerOid, final Integer dmg, final String dmgType) {
            super(CombatClient.MSG_TYPE_DAMAGE, targetOid);
            this.attackerOid = attackerOid;
            this.setDmg(dmg);
            this.setDmgType(dmgType);
        }
        
        public void setDmg(final Integer dmg) {
            this.dmg = dmg;
        }
        
        public Integer getDmg() {
            return this.dmg;
        }
        
        public OID getTargetOid() {
            return this.getSubject();
        }
        
        public OID getAttackerOid() {
            return this.attackerOid;
        }
        
        public void setDmgType(final String dmgType) {
            this.dmgType = dmgType;
        }
        
        public String getDmgType() {
            return this.dmgType;
        }
        
        public AOByteBuffer toBuffer() {
            final AOByteBuffer buf = new AOByteBuffer(200);
            buf.putOID(this.getAttackerOid());
            buf.putInt(23);
            buf.putOID(this.getTargetOid());
            buf.putString(this.dmgType);
            buf.putInt((int)this.dmg);
            buf.flip();
            return buf;
        }
    }
    
    public static class CooldownMessage extends SubjectMessage
    {
        protected Set<Entry> cooldowns;
        private static final long serialVersionUID = 1L;
        
        public CooldownMessage() {
            this.cooldowns = new HashSet<Entry>();
        }
        
        public CooldownMessage(final OID oid) {
            super(CombatClient.MSG_TYPE_COOLDOWN, oid);
            this.cooldowns = new HashSet<Entry>();
        }
        
        public CooldownMessage(final Cooldown.State state) {
            super(CombatClient.MSG_TYPE_COOLDOWN, state.getObject().getOid());
            this.cooldowns = new HashSet<Entry>();
            this.addCooldown(state);
        }
        
        public void addCooldown(final String id, final long duration, final long endTime) {
            final Entry entry = new Entry(id, duration, endTime);
            this.cooldowns.add(entry);
        }
        
        public void addCooldown(final Cooldown.State state) {
            this.addCooldown(state.getID(), state.getDuration(), state.getEndTime());
        }
        
        public class Entry
        {
            protected String cooldownID;
            protected long duration;
            protected long endTime;
            private static final long serialVersionUID = 1L;
            
            public Entry() {
            }
            
            public Entry(final String id, final long duration, final long endTime) {
                this.setCooldownID(id);
                this.setDuration(duration);
                this.setEndTime(endTime);
            }
            
            public String getCooldownID() {
                return this.cooldownID;
            }
            
            public void setCooldownID(final String cd) {
                this.cooldownID = cd;
            }
            
            public long getDuration() {
                return this.duration;
            }
            
            public void setDuration(final long duration) {
                this.duration = duration;
            }
            
            public long getEndTime() {
                return this.endTime;
            }
            
            public void setEndTime(final long endTime) {
                this.endTime = endTime;
            }
        }
    }
    
    public static class AbilityProgressMessage extends SubjectMessage
    {
        protected int abilityID;
        protected String state;
        protected long duration;
        protected long endTime;
        private static final long serialVersionUID = 1L;
        
        public AbilityProgressMessage() {
        }
        
        public AbilityProgressMessage(final AgisAbilityState state) {
            super(CombatClient.MSG_TYPE_ABILITY_PROGRESS, state.getSource().getOid());
            this.setAbilityID(state.getAbility().getID());
            this.setState(state.getState().toString());
            this.setDuration(state.getDuration());
            this.setEndTime(this.calculateEndTime(state));
        }
        
        protected long calculateEndTime(final AgisAbilityState state) {
            final AgisAbility ability = state.getAbility();
            switch (state.getState()) {
                case ACTIVATING: {
                    return state.getNextWakeupTime();
                }
                case CHANNELLING: {
                    final int pulsesRemaining = ability.getChannelPulses() - state.getNextPulse() - 1;
                    final long endTime = state.getNextWakeupTime() + pulsesRemaining * ability.getChannelPulseTime();
                    return endTime;
                }
                default: {
                    return 0L;
                }
            }
        }
        
        public int getAbilityID() {
            return this.abilityID;
        }
        
        public void setAbilityID(final int id) {
            this.abilityID = id;
        }
        
        public String getState() {
            return this.state;
        }
        
        public void setState(final String state) {
            this.state = state;
        }
        
        public long getDuration() {
            return this.duration;
        }
        
        public void setDuration(final long duration) {
            this.duration = duration;
        }
        
        public long getEndTime() {
            return this.endTime;
        }
        
        public void setEndTime(final long time) {
            this.endTime = time;
        }
    }
    
    public static class ReleaseObjectMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        
        public ReleaseObjectMessage() {
        }
        
        public ReleaseObjectMessage(final OID oid) {
            super(CombatClient.MSG_TYPE_RELEASE_OBJECT, oid);
        }
    }
    
    public static class QuestMobDeath extends SubjectMessage
    {
        String mobName;
        int mobID;
        LinkedList<String> questCategories;
        private static final long serialVersionUID = 1L;
        
        public QuestMobDeath() {
            super(CombatClient.MSG_TYPE_COMBAT_MOB_DEATH);
        }
        
        public QuestMobDeath(final OID playerOid, final int mobID, final String mobName, final LinkedList<String> questCategories) {
            super(CombatClient.MSG_TYPE_COMBAT_MOB_DEATH, playerOid);
            this.setMobID(mobID);
            this.setMobName(mobName);
            this.setQuestCategories(questCategories);
            Log.debug("ANDREW - QuestMobDeath message");
        }
        
        public String getMobName() {
            return this.mobName;
        }
        
        public void setMobName(final String mobName) {
            this.mobName = mobName;
        }
        
        public int getMobID() {
            return this.mobID;
        }
        
        public void setMobID(final int mobID) {
            this.mobID = mobID;
        }
        
        public LinkedList<String> getQuestCategories() {
            return this.questCategories;
        }
        
        public void setQuestCategories(final LinkedList<String> questCategories) {
            this.questCategories = questCategories;
        }
    }
    
    public static class abilityUsedMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public abilityUsedMessage() {
        }
        
        public abilityUsedMessage(final OID oid, final int skillType) {
            super(oid);
            this.setMsgType(CombatClient.MSG_TYPE_COMBAT_ABILITY_USED);
            this.setProperty("skillType", (Serializable)skillType);
            Log.debug("COMBAT CLIENT: abilityUsedMessage hit 1");
        }
    }
    
    public static class stopAutoAttackMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public stopAutoAttackMessage() {
        }
        
        public stopAutoAttackMessage(final OID oid) {
            super(oid);
            this.setMsgType(CombatClient.MSG_TYPE_COMBAT_STOP_AUTO_ATTACK);
        }
    }
    
    public static class TargetTypeMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        protected OID targetOid;
        protected int targetType;
        protected String species;
        
        public TargetTypeMessage() {
        }
        
        public TargetTypeMessage(final OID oid, final OID targetOid, final int targetType, final String species) {
            super(CombatClient.MSG_TYPE_TARGET_TYPE, oid);
            this.setMsgType(CombatClient.MSG_TYPE_TARGET_TYPE);
            this.setTargetOid(targetOid);
            this.setTargetType(targetType);
            this.setSpecies(species);
        }
        
        public OID getTargetOid() {
            return this.targetOid;
        }
        
        public void setTargetOid(final OID targetOid) {
            this.targetOid = targetOid;
        }
        
        public int getTargetType() {
            return this.targetType;
        }
        
        public void setTargetType(final int targetType) {
            this.targetType = targetType;
        }
        
        public String getSpecies() {
            return this.species;
        }
        
        public void setSpecies(final String species) {
            this.species = species;
        }
    }
    
    public static class interruptAbilityMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        
        public interruptAbilityMessage() {
            super(CombatClient.MSG_TYPE_INTERRUPT_ABILITY);
        }
        
        public interruptAbilityMessage(final OID oid) {
            super(CombatClient.MSG_TYPE_INTERRUPT_ABILITY, oid);
        }
    }
    
    public static class CombatLogoutMessage extends SubjectMessage
    {
        OID playerOid;
        private static final long serialVersionUID = 1L;
        
        public CombatLogoutMessage() {
            super(CombatClient.MSG_TYPE_COMBAT_LOGOUT);
        }
        
        public CombatLogoutMessage(final OID oid) {
            super(CombatClient.MSG_TYPE_COMBAT_LOGOUT, oid);
        }
        
        public CombatLogoutMessage(final OID oid, final OID player) {
            super(CombatClient.MSG_TYPE_COMBAT_LOGOUT, oid);
            this.setPlayerOid(player);
        }
        
        public OID getPlayerOid() {
            return this.playerOid;
        }
        
        public void setPlayerOid(final OID player) {
            this.playerOid = player;
        }
    }
    
    public static class FactionUpdateMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public FactionUpdateMessage() {
        }
        
        public FactionUpdateMessage(final OID oid) {
            super(oid);
            this.setMsgType(CombatClient.MSG_TYPE_FACTION_UPDATE);
            Log.debug("COMBAT CLIENT: FactionUpdateMessage hit");
        }
    }
    
    public static class getAoeTargetsMessage extends SubjectMessage
    {
        private static final long serialVersionUID = 1L;
        private Point loc;
        private int radius;
        private ObjectType objectType;
        
        public getAoeTargetsMessage() {
        }
        
        public getAoeTargetsMessage(final OID oid, final Point loc, final Integer radius, final ObjectType objectType) {
            super(CombatClient.MSG_TYPE_GET_AOE_TARGETS, oid);
            this.loc = loc;
            this.radius = radius;
            this.objectType = objectType;
            Log.debug("COMBAT CLIENT: getAoeTargetsMessage hit 1");
        }
        
        public Point getLoc() {
            return this.loc;
        }
        
        public void setLoc(final Point loc) {
            this.loc = loc;
        }
        
        public int getRadius() {
            return this.radius;
        }
        
        public void setRadius(final int radius) {
            this.radius = radius;
        }
        
        public ObjectType getObjectType() {
            return this.objectType;
        }
        
        public void setObjectType(final ObjectType type) {
            this.objectType = type;
        }
    }
    
    public static class updateActionBarMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public updateActionBarMessage() {
        }
        
        public updateActionBarMessage(final OID oid, final int actionPosition, final String newAction) {
            super(oid);
            this.setMsgType(CombatClient.MSG_TYPE_UPDATE_ACTIONBAR);
            this.setProperty("actionPosition", (Serializable)actionPosition);
            this.setProperty("newAction", (Serializable)newAction);
            Log.debug("COMBAT CLIENT: updateActionBarMessage hit 1");
        }
    }
    
    public static class applyEffectMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public applyEffectMessage() {
        }
        
        public applyEffectMessage(final OID oid, final int effectID) {
            super(oid);
            this.setMsgType(CombatClient.MSG_TYPE_APPLY_EFFECT);
            this.setProperty("effectID", (Serializable)effectID);
            Log.debug("COMBAT CLIENT: applyEffectMessage hit 1");
        }
    }
    
    public static class alterExpMessage extends SubjectMessage
    {
        int xpAmount;
        private static final long serialVersionUID = 1L;
        
        public alterExpMessage() {
            super(CombatClient.MSG_TYPE_ALTER_EXP);
        }
        
        public alterExpMessage(final OID playerOid, final int xpAmount) {
            super(CombatClient.MSG_TYPE_ALTER_EXP, playerOid);
            this.setXpAmount(xpAmount);
            Log.debug("COMBATCLIENT: alterExpMessage message");
        }
        
        public int getXpAmount() {
            return this.xpAmount;
        }
        
        public void setXpAmount(final int xpAmount) {
            this.xpAmount = xpAmount;
        }
    }
    
    public static class updateBreathStatusMessage extends SubjectMessage
    {
        boolean underwater;
        private static final long serialVersionUID = 1L;
        
        public updateBreathStatusMessage() {
            super(CombatClient.MSG_TYPE_UPDATE_BREATH);
        }
        
        public updateBreathStatusMessage(final OID playerOid, final boolean underwater) {
            super(CombatClient.MSG_TYPE_UPDATE_BREATH, playerOid);
            this.setUnderwater(underwater);
            Log.debug("COMBATCLIENT: updateBreathStatusMessage message");
        }
        
        public boolean getUnderwater() {
            return this.underwater;
        }
        
        public void setUnderwater(final boolean underwater) {
            this.underwater = underwater;
        }
    }
    
    public static class updateFatigueStatusMessage extends SubjectMessage
    {
        boolean fatigue;
        private static final long serialVersionUID = 1L;
        
        public updateFatigueStatusMessage() {
            super(CombatClient.MSG_TYPE_UPDATE_FATIGUE);
        }
        
        public updateFatigueStatusMessage(final OID playerOid, final boolean fatigue) {
            super(CombatClient.MSG_TYPE_UPDATE_FATIGUE, playerOid);
            this.setFatigue(fatigue);
            Log.debug("COMBATCLIENT: updateFatigueStatusMessage 1");
        }
        
        public boolean getFatigue() {
            return this.fatigue;
        }
        
        public void setFatigue(final boolean fatigue) {
            this.fatigue = fatigue;
        }
    }
}
