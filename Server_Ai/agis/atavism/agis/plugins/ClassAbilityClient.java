// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.messages.PropertyMessage;
import atavism.agis.objects.SkillTemplate;
import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import java.util.Iterator;
import java.util.Collection;
import atavism.agis.core.Agis;
import atavism.agis.core.AgisAbility;
import java.util.ArrayList;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.util.Logger;
import atavism.server.engine.Namespace;

public class ClassAbilityClient
{
    public static Namespace NAMESPACE;
    private static final Logger log;
    public static final MessageType MSG_TYPE_STAT_XP_UPDATE;
    public static final MessageType MSG_TYPE_HANDLE_EXP;
    public static final MessageType MSG_TYPE_LEVEL_CHANGE;
    public static final MessageType MSG_TYPE_COMBAT_SKILL_INCREASE;
    public static final MessageType MSG_TYPE_COMBAT_SKILL_DECREASE;
    public static final MessageType MSG_TYPE_COMBAT_SKILL_RESET;
    public static final MessageType MSG_TYPE_COMBAT_SKILL_ALTER_CURRENT;
    public static final MessageType MSG_TYPE_COMBAT_GET_SKILL;
    public static final MessageType MSG_TYPE_COMBAT_GET_PLAYER_SKILL_LEVEL;
    public static final MessageType MSG_TYPE_PURCHASE_SKILL_POINT;
    public static final MessageType MSG_TYPE_LEARN_ABILITY;
    
    static {
        ClassAbilityClient.NAMESPACE = null;
        log = new Logger("ClassesPlugin");
        MSG_TYPE_STAT_XP_UPDATE = MessageType.intern("ao.STAT_XP_UPDATE");
        MSG_TYPE_HANDLE_EXP = MessageType.intern("ao.HANDLE_EXP");
        MSG_TYPE_LEVEL_CHANGE = MessageType.intern("ao.LEVEL_CHANGE");
        MSG_TYPE_COMBAT_SKILL_INCREASE = MessageType.intern("combat.SKILL_INCREASE");
        MSG_TYPE_COMBAT_SKILL_DECREASE = MessageType.intern("combat.SKILL_DECREASE");
        MSG_TYPE_COMBAT_SKILL_RESET = MessageType.intern("combat.SKILL_RESET");
        MSG_TYPE_COMBAT_SKILL_ALTER_CURRENT = MessageType.intern("combat.SKILL_ALTER_CURRENT");
        MSG_TYPE_COMBAT_GET_SKILL = MessageType.intern("combat.GET_SKILL");
        MSG_TYPE_COMBAT_GET_PLAYER_SKILL_LEVEL = MessageType.intern("combat.GET_PLAYER_SKILL_LEVEL");
        MSG_TYPE_PURCHASE_SKILL_POINT = MessageType.intern("combat.PURCHASE_SKILL_POINT");
        MSG_TYPE_LEARN_ABILITY = MessageType.intern("combat.LEARN_ABILITY");
    }
    
    public static void sendXPUpdate(final OID oid, final String statName, final int statCurrentValue) {
        ClassAbilityClient.log.debug("Sending Client Stat XP Increase Message");
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ao.STAT_XP_UPDATE");
        props.put("stat", "Stat XP Increased: " + statName + " : " + statCurrentValue);
        props.put("playerOid", (Serializable)oid);
        final WorldManagerClient.TargetedExtensionMessage sendXPUpdate = new WorldManagerClient.TargetedExtensionMessage(ClassAbilityClient.MSG_TYPE_STAT_XP_UPDATE, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)sendXPUpdate);
    }
    
    public static void CheckSkillAbilities(final OID playerOid, final String skill, final int level) {
        if (skill == null) {
            ClassAbilityClient.log.warn("ClassAbilityClient.CheckSkillAbilities - Skill is null");
        }
        final ArrayList<AgisAbility> skillAbilities = new ArrayList<AgisAbility>();
        final Collection<AgisAbility> abilities = Agis.AbilityManager.getMap().values();
        for (final AgisAbility ability : abilities) {
            if (ability.getRequiredSkill() == null) {
                ClassAbilityClient.log.warn("ClassAbilityClient.CheckSkillAbilities - Required Skill for ability " + ability.getName() + " is null");
            }
            else {
                if (!ability.getRequiredSkill().getName().equals(skill) || ability.getRequiredSkillLevel() > level) {
                    continue;
                }
                ClassAbilityClient.log.debug("ClassAbilityClient.CheckSkillAbilities: Adding ability to skillAbilities : " + ability.getName());
                skillAbilities.add(ability);
            }
        }
        final CombatInfo player = CombatPlugin.getCombatInfo(playerOid);
        final ArrayList<Integer> currentAbilities = player.getCurrentAbilities();
        for (final AgisAbility ability2 : skillAbilities) {
            if (!currentAbilities.contains(ability2.getName())) {
                ClassAbilityClient.log.debug("ClassAbilityClient.CheckSkillAbilities: Adding new ability : " + ability2.getName());
                CombatPlugin.sendAbilityUpdate(player);
            }
        }
        ClassAbilityClient.log.debug("ClassAbilityClient.CheckSkillAbilities: Finished");
    }
    
    public static void levelChange(final OID oid, final int level) {
        final levelChangeMessage msg = new levelChangeMessage(oid, level);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("CLASSABILITY - client levelChange hit 1");
    }
    
    public static void skillIncreased(final OID oid, final int skillType) {
        final skillIncreasedMessage msg = new skillIncreasedMessage(oid, skillType);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("CLASSABILITY CLIENT: skillIncreasedMessage hit 2");
    }
    
    public static void skillDecreased(final OID oid, final int skillType) {
        final skillDecreasedMessage msg = new skillDecreasedMessage(oid, skillType);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("CLASSABILITY CLIENT: skillDecreasedMessage hit 2");
    }
    
    public static void skillReset(final OID oid) {
        final skillResetMessage msg = new skillResetMessage(oid);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("CLASSABILITY CLIENT: skillResetMessage hit 2");
    }
    
    public static void skillAlterCurrent(final OID oid, final int skillType, final int alterValue) {
        final skillAlterCurrentMessage msg = new skillAlterCurrentMessage(oid, skillType, alterValue);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("CLASSABILITY CLIENT: skillAlterCurrentMessage hit 2");
    }
    
    public static SkillTemplate getSkillTemplate(final int num) {
        final skillGetMessage msg = new skillGetMessage(num);
        final SkillTemplate tmpl = (SkillTemplate)Engine.getAgent().sendRPCReturnObject((Message)msg);
        Log.debug("CLASSABILITY CLIENT: skillGetMessage hit 2");
        return tmpl;
    }
    
    public static int getPlayerSkillLevel(final OID playerOid, final int skillType) {
        final GetPlayerSkillLevelMessage msg = new GetPlayerSkillLevelMessage(playerOid, skillType);
        final int skillLevel = Engine.getAgent().sendRPCReturnInt((Message)msg);
        Log.debug("CLASSABILITY CLIENT: GetPlayerSkillLevelMessage hit 2");
        return skillLevel;
    }
    
    public static void learnAbility(final OID oid, final int abilityID) {
        final LearnAbilityMessage msg = new LearnAbilityMessage(oid, abilityID);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("CLASSABILITY CLIENT: sent LearnAbilityMessage");
    }
    
    public static class levelChangeMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public levelChangeMessage() {
        }
        
        public levelChangeMessage(final OID oid, final int level) {
            super(oid);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_LEVEL_CHANGE);
            this.setProperty("level", (Serializable)level);
            Log.debug("CLASSABILITY - client levelChange hit 2");
        }
    }
    
    public static class skillIncreasedMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public skillIncreasedMessage() {
        }
        
        public skillIncreasedMessage(final OID oid, final int skillType) {
            super(oid);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_INCREASE);
            this.setProperty("skillType", (Serializable)skillType);
            Log.debug("CLASSABILITY CLIENT: skillIncreasedMessage hit 1");
        }
    }
    
    public static class skillDecreasedMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public skillDecreasedMessage() {
        }
        
        public skillDecreasedMessage(final OID oid, final int skillType) {
            super(oid);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_DECREASE);
            this.setProperty("skillType", (Serializable)skillType);
            Log.debug("CLASSABILITY CLIENT: skillDecreasedMessage hit 1");
        }
    }
    
    public static class skillResetMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public skillResetMessage() {
        }
        
        public skillResetMessage(final OID oid) {
            super(oid);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_RESET);
            Log.debug("CLASSABILITY CLIENT: skillResetMessage hit 1");
        }
    }
    
    public static class skillAlterCurrentMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public skillAlterCurrentMessage() {
        }
        
        public skillAlterCurrentMessage(final OID oid, final int skillType, final int alterValue) {
            super(oid);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_ALTER_CURRENT);
            this.setProperty("skillType", (Serializable)skillType);
            this.setProperty("alterValue", (Serializable)alterValue);
            Log.debug("CLASSABILITY CLIENT: skillAlterCurrentMessage hit 1");
        }
    }
    
    public static class skillGetMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public skillGetMessage() {
        }
        
        public skillGetMessage(final int num) {
            this.setProperty("templateNum", (Serializable)num);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_COMBAT_GET_SKILL);
            Log.debug("CLASSABILITY CLIENT: skillGetMessage hit 1");
        }
    }
    
    public static class GetPlayerSkillLevelMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        int skillType;
        
        public GetPlayerSkillLevelMessage() {
        }
        
        public GetPlayerSkillLevelMessage(final OID oid, final int skillType) {
            super(oid);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_COMBAT_GET_PLAYER_SKILL_LEVEL);
            this.setSkillType(skillType);
            Log.debug("CLASSABILITY CLIENT: GetPlayerSkillLevelMessage hit 1");
        }
        
        public int getSkillType() {
            return this.skillType;
        }
        
        public void setSkillType(final int skillType) {
            this.skillType = skillType;
        }
    }
    
    public static class LearnAbilityMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        int abilityID;
        
        public LearnAbilityMessage() {
        }
        
        public LearnAbilityMessage(final OID oid, final int abilityID) {
            super(oid);
            this.setMsgType(ClassAbilityClient.MSG_TYPE_LEARN_ABILITY);
            this.setAbilityID(abilityID);
        }
        
        public void setAbilityID(final int id) {
            this.abilityID = id;
        }
        
        public int getAbilityID() {
            return this.abilityID;
        }
    }
}
