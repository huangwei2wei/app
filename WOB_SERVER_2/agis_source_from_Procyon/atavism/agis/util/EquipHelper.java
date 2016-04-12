// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.AgisEquipSlot;
import java.util.Iterator;
import java.util.HashMap;
import atavism.agis.plugins.CombatPlugin;
import atavism.server.engine.EnginePlugin;
import java.io.Serializable;
import atavism.agis.plugins.CombatClient;
import atavism.server.util.Log;
import atavism.agis.objects.CombatInfo;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class EquipHelper
{
    public static void ItemAcquiredStatHelper(final OID oid, final AgisItem item, final boolean acquired, final CombatInfo obj) {
    }
    
    public static void UpdateEquiperStats(final OID oid, final AgisItem item, final boolean equipping, final CombatInfo obj) {
        Log.debug("EquipHelper.UpdateEquiperStats item =" + item);
        if (item == null) {
            return;
        }
        final String itemType = (String)item.getProperty("itemType");
        final String slot = (String)item.getProperty("slot");
        if (itemType.equals("Weapon")) {
            if (equipping) {
                final String subType = (String)item.getProperty("subType");
                final String weaponType = "Armed";
                Log.debug("ITEMS: weapon type for item " + item.getName() + " is: " + weaponType + " in slot: " + slot);
                if (slot.contains("Off Hand")) {
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "weapon2Type", subType });
                }
                else {
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "weaponType", subType });
                    final String attackType = (String)item.getProperty("attackType");
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "attackType", attackType });
                    final int speed = (int)item.getProperty("delay");
                    final int speed_mod = speed - obj.statGetBaseValue(CombatPlugin.ATTACK_SPEED_STAT);
                    obj.statAddModifier(CombatPlugin.ATTACK_SPEED_STAT, item.getOid(), speed_mod);
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "equipType", subType });
                    final Integer autoAttack = (Integer)item.getProperty("autoAttack");
                    if (autoAttack != null && autoAttack > 0) {
                        obj.overrideAutoAttackAbility(autoAttack);
                    }
                }
                final int damage = (int)item.getProperty("damage");
                obj.statAddModifier("dmg-base", item.getOid(), damage);
            }
            else {
                Log.debug("ITEMS: slot for item " + item.getName() + " is: " + slot);
                if (slot.contains("Off Hand")) {
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "weapon2Type", "" });
                }
                else {
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "weaponType", "Unarmed" });
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "attackType", "crush" });
                    EnginePlugin.setObjectPropertiesNoResponse(oid, CombatClient.NAMESPACE, new Serializable[] { "equipType", "" });
                    obj.statRemoveModifier(CombatPlugin.ATTACK_SPEED_STAT, item.getOid());
                    final Integer autoAttack2 = (Integer)item.getProperty("autoAttack");
                    if (autoAttack2 != null && autoAttack2 > 0) {
                        obj.resetAutoAttackAbility();
                    }
                }
                obj.statRemoveModifier("dmg-base", item.getOid());
            }
        }
        else {
            itemType.equals("Armor");
        }
        for (final String statName : CombatPlugin.STAT_LIST) {
            obj.statRemoveModifier(statName, item.getOid());
        }
        final HashMap<String, Integer> stats = (HashMap<String, Integer>)item.getProperty("bonusStats");
        for (final String statName2 : stats.keySet()) {
            final int value = stats.get(statName2);
            if (equipping) {
                Log.debug("EQUIP: equipping item altering stat: " + statName2 + " by: " + value);
                obj.statAddModifier(statName2, item.getOid(), value);
                if (statName2.equals(CombatPlugin.HEALTH_MOD_STAT)) {
                    obj.statModifyBaseValue(CombatPlugin.HEALTH_STAT, value * 10);
                }
                else {
                    if (!statName2.equals(CombatPlugin.MANA_MOD_STAT)) {
                        continue;
                    }
                    obj.statModifyBaseValue(CombatPlugin.MANA_STAT, value * 10);
                }
            }
        }
        obj.statSendUpdate(true);
    }
    
    public static void updateDisplay(final OID mobOid, final String displayVal, final AgisEquipSlot slot) {
        if (slot.equals(AgisEquipSlot.CHEST)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "chestDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.LEGS)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "legDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.HEAD)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "headDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.HANDS)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "handDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.FEET)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "feetDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.SHOULDER)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "shoulderDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.SHIRT)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "shirtDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.BACK)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "capeDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.BELT)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "beltDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.PRIMARYWEAPON)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "weaponDisplayID", displayVal });
        }
        else if (slot.equals(AgisEquipSlot.SECONDARYWEAPON)) {
            EnginePlugin.setObjectPropertiesNoResponse(mobOid, WorldManagerClient.NAMESPACE, new Serializable[] { "weapon2DisplayID", displayVal });
        }
    }
}
