// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import atavism.agis.plugins.AgisInventoryPlugin;
import atavism.agis.core.Agis;
import atavism.agis.objects.Currency;
import atavism.server.engine.EnginePlugin;
import java.util.Iterator;
import atavism.agis.objects.SkillData;
import atavism.agis.plugins.ClassAbilityPlugin;
import atavism.agis.objects.SkillInfo;
import java.util.ArrayList;
import atavism.agis.core.AgisAbility;
import atavism.agis.objects.CombatInfo;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.util.Log;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.engine.OID;

public class ExtendedCombatMessages
{
    public static void sendCombatText(final OID oid, final String dmg, final int dmgType) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "combat_text");
        props.put("DmgAmount", dmg);
        props.put("DmgType", dmgType);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Log.debug("ECM: Sending combat text with value " + dmg + " and type: " + dmgType);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendCombatChat(final CombatInfo obj, final String cmsg, final String msgType) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "combat_chat");
        props.put("Msg", cmsg);
        props.put("MsgType", msgType);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, obj.getOwnerOid(), obj.getOwnerOid(), false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendCombatText2(final OID caster, final OID target, final String dmg, final int dmgType) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "combat_text2");
        props.put("DmgAmount", dmg);
        props.put("DmgType", dmgType);
        props.put("target", (Serializable)target);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, caster, caster, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendAbilityFailMessage(final CombatInfo obj, final AgisAbility.AbilityResult result) {
        int failmessageType = 0;
        if (result == AgisAbility.AbilityResult.INVALID_TARGET) {
            failmessageType = 1;
        }
        else if (result == AgisAbility.AbilityResult.OUT_OF_RANGE) {
            failmessageType = 2;
        }
        else if (result == AgisAbility.AbilityResult.TOO_CLOSE) {
            failmessageType = 3;
        }
        else if (result == AgisAbility.AbilityResult.BUSY || result == AgisAbility.AbilityResult.NOT_READY) {
            failmessageType = 4;
        }
        else if (result == AgisAbility.AbilityResult.INSUFFICIENT_ENERGY) {
            failmessageType = 5;
        }
        else if (result == AgisAbility.AbilityResult.MISSING_REAGENT) {
            failmessageType = 6;
        }
        else if (result == AgisAbility.AbilityResult.MISSING_TOOL) {
            failmessageType = 7;
        }
        else if (result == AgisAbility.AbilityResult.INSUFFICIENT_VIGOR) {
            failmessageType = 12;
        }
        else if (result == AgisAbility.AbilityResult.EFFECT_MISSING) {
            failmessageType = 13;
        }
        else if (result == AgisAbility.AbilityResult.NO_TARGET) {
            failmessageType = 14;
        }
        else if (result == AgisAbility.AbilityResult.MISSING_WEAPON) {
            failmessageType = 15;
        }
        else if (result == AgisAbility.AbilityResult.PASSIVE) {
            failmessageType = 16;
        }
        else if (result == AgisAbility.AbilityResult.INTERRUPTED) {
            failmessageType = 17;
        }
        else if (result == AgisAbility.AbilityResult.DEAD) {
            failmessageType = 18;
        }
        Log.debug("ANDREW - sending  message: " + failmessageType);
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "ability_error");
        props.put("ErrorText", failmessageType);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, obj.getOwnerOid(), obj.getOwnerOid(), false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendErrorMessage(final OID oid, final String message) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "error_message");
        props.put("ErrorText", message);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendCooldownMessage(final OID oid, final String type, final long length) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "cooldown");
        props.put("CdType", type);
        props.put("CdLength", length);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendEffectMessage(final CombatInfo obj, final int type, final long length) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "effect");
        props.put("EffectType", type);
        props.put("EffectLength", length);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, obj.getOwnerOid(), obj.getOwnerOid(), false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendQuestProgressMessage(final OID oid, final String message) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "quest_progress");
        props.put("QuestProgressText", message);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendAnouncementMessage(final OID oid, final String message, final String type) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "announcement");
        props.put("AnnouncementText", message);
        props.put("AnnouncementType", type);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendAbilities(final OID oid, final ArrayList<Integer> abilities) {
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(oid, oid);
        propMsg.setProperty("abilities", (Serializable)abilities);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public static void sendActions(final OID oid, final ArrayList<String> actions) {
        final WorldManagerClient.TargetedPropertyMessage propMsg = new WorldManagerClient.TargetedPropertyMessage(oid, oid);
        Log.warn("ACTIONS: sending action message to OID: " + oid + " with actions: " + actions);
        propMsg.setProperty("actions", (Serializable)actions);
        Engine.getAgent().sendBroadcast((Message)propMsg);
    }
    
    public static void sendSkills(final OID oid, final SkillInfo skillInfo) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "skills");
        props.put("skillPoints", skillInfo.getSkillPoints());
        final int totalPoints = skillInfo.getSkillPoints() + skillInfo.getPointsSpent();
        props.put("totalSkillPoints", totalPoints);
        props.put("skillPointCost", ClassAbilityPlugin.GetSkillPointCost(totalPoints));
        int numSkills = 0;
        for (final SkillData skillData : skillInfo.getSkills().values()) {
            props.put("skill" + numSkills + "ID", skillData.getSkillID());
            props.put("skill" + numSkills + "Name", skillData.getSkillName());
            props.put("skill" + numSkills + "Current", skillData.getSkillCurrent());
            props.put("skill" + numSkills + "Level", skillData.getSkillLevel());
            props.put("skill" + numSkills + "Max", skillData.getSkillMaxLevel());
            ++numSkills;
        }
        props.put("numSkills", numSkills);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static void sendCurrencies(final OID oid, final HashMap<Integer, Integer> currencies) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "currencies");
        int numCurrencies = 0;
        final OID accountID = (OID)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
        for (final int currencyID : currencies.keySet()) {
            final Currency currency = (Currency)Agis.CurrencyManager.get(currencyID);
            Log.debug("CURRENCY: got currency to send: " + currencyID);
            props.put("currency" + numCurrencies + "ID", currency.getCurrencyID());
            props.put("currency" + numCurrencies + "Name", currency.getCurrencyName());
            props.put("currency" + numCurrencies + "Icon", currency.getCurrencyIcon());
            if (currency.getExternal()) {
                props.put("currency" + numCurrencies + "Current", AgisInventoryPlugin.aDB.getCharacterCoinAmount(accountID));
            }
            else {
                props.put("currency" + numCurrencies + "Current", currencies.get(currencyID));
            }
            ++numCurrencies;
        }
        props.put("numCurrencies", numCurrencies);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("AJCURRENCY: sending down currencies message to: " + oid + " with props: " + props);
    }
}
