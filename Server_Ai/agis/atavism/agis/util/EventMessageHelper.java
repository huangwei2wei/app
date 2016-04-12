// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.engine.OID;

public class EventMessageHelper
{
    public static final String COMBAT_PHYSICAL_DAMAGE = "CombatPhysicalDamage";
    public static final String COMBAT_MAGICAL_DAMAGE = "CombatMagicalDamage";
    public static final String COMBAT_PHYSICAL_CRITICAL = "CombatPhysicalCritical";
    public static final String COMBAT_MAGICAL_CRITICAL = "CombatMagicalCritical";
    public static final String COMBAT_HEAL = "CombatHeal";
    public static final String COMBAT_HEALTH_TRANSFER = "CombatHealthTransfer";
    public static final String COMBAT_MISSED = "CombatMissed";
    public static final String COMBAT_DODGED = "CombatDodged";
    public static final String COMBAT_BLOCKED = "CombatBlocked";
    public static final String COMBAT_PARRIED = "CombatParried";
    public static final String COMBAT_EVADED = "CombatEvaded";
    public static final String COMBAT_IMMUNE = "CombatImmune";
    public static final String COMBAT_BUFF_GAINED = "CombatBuffGained";
    public static final String COMBAT_DEBUFF_GAINED = "CombatDebuffGained";
    public static final String COMBAT_BUFF_LOST = "CombatBuffLost";
    public static final String COMBAT_DEBUFF_LOST = "CombatDebuffLost";
    public static final String COMBAT_COOLDOWN_EXTENDED = "CombatCooldownExtended";
    public static final String COMBAT_REPUTATION_CHANGED = "CombatReputationChanged";
    public static final String COMBAT_EXP_GAINED = "CombatExpGained";
    public static final String COMBAT_ABILITY_LEARNED = "CombatAbilityLearned";
    public static final String COMBAT_CASTING_STARTED = "CastingStarted";
    public static final String COMBAT_CASTING_CANCELLED = "CastingCancelled";
    public static final String ITEM_HARVESTED = "ItemHarvested";
    public static final String ITEM_LOOTED = "ItemLooted";
    
    public static void SendCombatEvent(final OID casterOID, final OID targetOID, final String eventType, final int abilityID, final int effectID, final int val, final int val2) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "combat_event");
        props.put("event", eventType);
        props.put("caster", (Serializable)casterOID);
        props.put("target", (Serializable)targetOID);
        props.put("abilityID", abilityID);
        props.put("effectID", effectID);
        props.put("value1", val);
        props.put("value2", val2);
        WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, casterOID, targetOID, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
        if (!casterOID.equals((Object)targetOID)) {
            msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, targetOID, casterOID, false, (Map)props);
            Engine.getAgent().sendBroadcast((Message)msg);
        }
        Log.debug("EventMessageHelper.SendCombatEvent Event: " + eventType);
    }
    
    public static void SendInventoryEvent(final OID playerOid, final String eventType, final int itemID, final int count, final String data) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "inventory_event");
        props.put("event", eventType);
        props.put("itemID", itemID);
        props.put("count", count);
        props.put("data", data);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Log.debug("EventMessageHelper.SendInventoryEvent Event: " + eventType + " with item: " + itemID);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void SendGeneralEvent(final OID playerOid, final String eventType, final int val, final String data) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "general_event");
        props.put("event", eventType);
        props.put("val", val);
        props.put("data", data);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, playerOid, playerOid, false, (Map)props);
        Log.debug("EventMessageHelper.SendGeneralEvent Event: " + eventType);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
}
