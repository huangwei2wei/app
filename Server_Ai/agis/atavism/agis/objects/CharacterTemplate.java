// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import atavism.server.objects.Marker;
import atavism.server.plugins.ObjectManagerClient;
import atavism.agis.plugins.CombatPlugin;
import atavism.agis.plugins.CombatClient;
import atavism.server.objects.InstanceRestorePoint;
import atavism.agis.plugins.SocialClient;
import atavism.server.engine.Namespace;
import atavism.server.plugins.InventoryClient;
import atavism.server.math.AOVector;
import java.util.LinkedList;
import atavism.server.math.Quaternion;
import atavism.server.plugins.ProxyPlugin;
import atavism.server.util.Log;
import atavism.server.plugins.InstanceClient;
import atavism.server.objects.ObjectTypes;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.objects.DisplayContext;
import atavism.server.objects.Template;
import atavism.server.plugins.ObjectManagerPlugin;
import java.util.Map;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import java.util.ArrayList;
import java.util.HashMap;
import atavism.server.worldmgr.CharacterFactory;

public class CharacterTemplate extends CharacterFactory
{
    String aspect;
    HashMap<String, CharacterStatProgression> startingStats;
    ArrayList<Integer> startingSkills;
    String race;
    int faction;
    String instanceName;
    String portalName;
    Point spawnPoint;
    int health;
    int mana;
    int autoAttack;
    ArrayList<CharacterStartingItem> items;
    
    public CharacterTemplate() {
        this.startingStats = new HashMap<String, CharacterStatProgression>();
        this.faction = 1;
        this.autoAttack = -1;
        this.items = new ArrayList<CharacterStartingItem>();
    }
    
    public OID createCharacter(final String worldName, final OID atavismID, final Map properties) {
        final HashMap<String, Serializable> props = (HashMap<String, Serializable>)properties;
        final String name = props.get("characterName");
        final String errorMsg = this.checkName(name);
        if (this.checkName(name) != null) {
            properties.put("errorMessage", errorMsg);
            return null;
        }
        final Template player = new Template("DefaultPlayer", -1, ObjectManagerPlugin.MOB_TEMPLATE);
        final String meshName = props.get("prefab");
        final DisplayContext dc = new DisplayContext(meshName, true);
        dc.addSubmesh(new DisplayContext.Submesh("", ""));
        player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)dc);
        player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)ObjectTypes.player);
        player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, (Serializable)75);
        OID instanceOid = InstanceClient.getInstanceOid(this.instanceName);
        Log.debug("POP: getting population for instance: " + this.instanceName + " and Oid:" + instanceOid);
        InstanceClient.InstanceInfo instanceInfo = InstanceClient.getInstanceInfo(instanceOid, -8193);
        if (instanceInfo.populationLimit != -1 && instanceInfo.playerPopulation >= instanceInfo.populationLimit) {
            Log.debug("POP: got population: " + instanceInfo.playerPopulation + " and limit: " + instanceInfo.populationLimit);
            instanceOid = ProxyPlugin.handleFullInstance(instanceInfo.templateName, instanceInfo);
            instanceInfo = InstanceClient.getInstanceInfo(instanceOid, -8193);
        }
        if (instanceOid == null) {
            Log.error("SampleFactory: no 'default' instance");
            properties.put("errorMessage", "No default instance");
            return null;
        }
        Log.debug("SPAWN: spawn marker name=" + this.portalName);
        final Marker spawnMarker = InstanceClient.getMarker(instanceOid, this.portalName);
        player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_NAME, (Serializable)name);
        player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_INSTANCE, (Serializable)instanceOid);
        player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_LOC, (Serializable)this.spawnPoint);
        player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ORIENT, (Serializable)Quaternion.Identity);
        player.put(WorldManagerClient.NAMESPACE, "accountId", (Serializable)atavismID);
        player.put(WorldManagerClient.NAMESPACE, "model", (Serializable)meshName);
        player.put(WorldManagerClient.NAMESPACE, "race", (Serializable)this.race);
        player.put(WorldManagerClient.NAMESPACE, "gender", (Serializable)props.get("gender"));
        player.put(WorldManagerClient.NAMESPACE, "charactername", (Serializable)name);
        player.put(WorldManagerClient.NAMESPACE, "world", (Serializable)this.instanceName);
        player.put(WorldManagerClient.NAMESPACE, "category", (Serializable)1);
        player.put(WorldManagerClient.NAMESPACE, "zone", (Serializable)"");
        final LinkedList zones = new LinkedList();
        player.put(WorldManagerClient.NAMESPACE, "zones", (Serializable)zones);
        player.put(WorldManagerClient.NAMESPACE, "subzone", (Serializable)"");
        final LinkedList subzones = new LinkedList();
        player.put(WorldManagerClient.NAMESPACE, "subzones", (Serializable)subzones);
        player.put(WorldManagerClient.NAMESPACE, "hearthLoc", (Serializable)this.spawnPoint);
        player.put(WorldManagerClient.NAMESPACE, "hearthInstance", (Serializable)this.instanceName);
        final AOVector scale = new AOVector(1.0f, 1.0f, 1.0f);
        player.put(WorldManagerClient.NAMESPACE, "scaleFactor", (Serializable)scale);
        player.put(WorldManagerClient.NAMESPACE, "walk_speed", (Serializable)3);
        player.put(WorldManagerClient.NAMESPACE, "movement_state", (Serializable)1);
        final HashMap factionData = new HashMap();
        player.put(WorldManagerClient.NAMESPACE, "factionData", (Serializable)factionData);
        player.put(WorldManagerClient.NAMESPACE, "faction", (Serializable)this.faction);
        String startingItems = "";
        for (final CharacterStartingItem item : this.items) {
            Log.debug("ITEM: adding item: " + item.itemID + " to character");
            if (item.equipped) {
                startingItems = String.valueOf(startingItems) + "*" + item.itemID + ";";
            }
            else {
                for (int i = 0; i < item.count; ++i) {
                    startingItems = String.valueOf(startingItems) + item.itemID + ";";
                }
            }
            Log.debug("ITEM: starting items is now: " + startingItems);
        }
        if (startingItems.endsWith(";")) {
            startingItems = startingItems.substring(0, startingItems.length() - 1);
        }
        Log.debug("ITEM: character is starting with items: " + startingItems);
        player.put(InventoryClient.NAMESPACE, ":inv_items", (Serializable)startingItems);
        player.put(WorldManagerClient.NAMESPACE, "busy", (Serializable)false);
        player.put(Namespace.QUEST, ":currentQuests", (Serializable)"");
        player.put(SocialClient.NAMESPACE, ":channels", (Serializable)"");
        player.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
        final InstanceRestorePoint restorePoint = new InstanceRestorePoint(this.instanceName, this.spawnPoint);
        restorePoint.setFallbackFlag(true);
        final LinkedList restoreStack = new LinkedList();
        restoreStack.add(restorePoint);
        player.put(Namespace.OBJECT_MANAGER, "instanceStack", (Serializable)restoreStack);
        player.put(Namespace.OBJECT_MANAGER, "currentInstanceName", (Serializable)this.instanceName);
        player.put(CombatClient.NAMESPACE, "aspect", (Serializable)this.aspect);
        player.put(CombatClient.NAMESPACE, "attackable", (Serializable)true);
        player.put(CombatClient.NAMESPACE, "attackType", (Serializable)"crush");
        player.put(CombatClient.NAMESPACE, "weaponType", (Serializable)"Unarmed");
        player.put(CombatClient.NAMESPACE, "combat.userflag", (Serializable)true);
        player.put(CombatClient.NAMESPACE, "deadstate", (Serializable)false);
        player.put(CombatClient.NAMESPACE, "hitBox", (Serializable)1);
        for (final String stat : this.startingStats.keySet()) {
            if (stat.equals(CombatPlugin.HEALTH_STAT)) {
                this.health = this.startingStats.get(stat).baseValue;
            }
            else if (stat.equals(CombatPlugin.MANA_STAT)) {
                this.mana = this.startingStats.get(stat).baseValue;
            }
            else {
                final int value = this.startingStats.get(stat).baseValue;
                player.put(CombatClient.NAMESPACE, stat, (Serializable)new AgisStat(stat, value));
            }
        }
        player.put(CombatClient.NAMESPACE, ":health", (Serializable)this.health);
        player.put(CombatClient.NAMESPACE, ":mana", (Serializable)this.mana);
        player.put(CombatClient.NAMESPACE, "combat.autoability", (Serializable)this.autoAttack);
        player.put(CombatClient.NAMESPACE, "combat.autoabilitybase", (Serializable)this.autoAttack);
        player.put(CombatClient.NAMESPACE, ":startingSkills", (Serializable)this.startingSkills);
        final LinkedList effectsList = new LinkedList();
        player.put(CombatClient.NAMESPACE, "effects", (Serializable)effectsList);
        final HashMap<String, HashMap<String, Serializable>> customPropMapping = new HashMap<String, HashMap<String, Serializable>>();
        for (final String prop : props.keySet()) {
            if (prop.startsWith("custom:")) {
                final String propName = prop.substring(7);
                final Serializable propValue = props.get(prop);
                Log.debug("CUSTOM: got custom property: " + propName + " with value: " + propValue);
                if (propName.contains(":")) {
                    final String[] mapNames = propName.split(":");
                    final String mapName = mapNames[0];
                    Log.debug("CUSTOM: got custom mapping: " + mapName + " with property: " + mapNames[1]);
                    if (customPropMapping.containsKey(mapName)) {
                        final HashMap<String, Serializable> mapProps = customPropMapping.get(mapName);
                        if (propValue instanceof Double) {
                            mapProps.put(mapNames[1], propValue);
                        }
                        else {
                            mapProps.put(mapNames[1], propValue);
                        }
                        customPropMapping.put(mapName, mapProps);
                    }
                    else {
                        final HashMap<String, Serializable> mapProps = new HashMap<String, Serializable>();
                        if (propValue instanceof Double) {
                            mapProps.put(mapNames[1], propValue);
                        }
                        else {
                            mapProps.put(mapNames[1], propValue);
                        }
                        customPropMapping.put(mapName, mapProps);
                    }
                }
                else {
                    if (propValue instanceof Double) {
                        player.put(WorldManagerClient.NAMESPACE, propName, (Serializable)propValue);
                    }
                    else {
                        player.put(WorldManagerClient.NAMESPACE, propName, propValue);
                    }
                    Log.debug("CUSTOM: added custom property: " + propName + " with value: " + propValue);
                }
            }
        }
        for (final String mapName2 : customPropMapping.keySet()) {
            player.put(WorldManagerClient.NAMESPACE, mapName2, (Serializable)customPropMapping.get(mapName2));
            Log.debug("CUSTOM: added custom mapping: " + mapName2 + " with numProps: " + customPropMapping.get(mapName2).size());
        }
        final OID objOid = ObjectManagerClient.generateObject(-1, ObjectManagerPlugin.MOB_TEMPLATE, player);
        Log.debug("SampleFactory: generated obj oid=" + objOid);
        return objOid;
    }
    
    private String checkName(final String name) {
        if (name == null || name.equals("")) {
            return "Invalid name";
        }
        if (name.length() > 14) {
            return "Your characters name must contain less than 15 letters";
        }
        if (name.length() < 3) {
            return "Your characters name must contain more than 2 letters";
        }
        if (!name.matches("[a-zA-Z]+")) {
            return "Your characters name can only contain letters";
        }
        return null;
    }
    
    public void setAspect(final String aspect) {
        this.aspect = aspect;
    }
    
    public void setRace(final String race) {
        this.race = race;
    }
    
    public void setFaction(final int faction) {
        this.faction = faction;
    }
    
    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }
    
    public void setPortalName(final String portalName) {
        this.portalName = portalName;
    }
    
    public void setSpawnPoint(final Point spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
    
    public void setHealth(final int health) {
        this.health = health;
    }
    
    public void setMana(final int mana) {
        this.mana = mana;
    }
    
    public void setAutoAttack(final int autoAttackAbility) {
        this.autoAttack = autoAttackAbility;
    }
    
    public void setStartingStats(final HashMap<String, CharacterStatProgression> stats) {
        this.startingStats = stats;
    }
    
    public HashMap<String, CharacterStatProgression> getStartingStats() {
        return this.startingStats;
    }
    
    public void AddStatProgression(final String name, final int baseValue, final float levelIncrease, final float levelPercentIncrease) {
        final CharacterStatProgression statProgress = new CharacterStatProgression();
        statProgress.baseValue = baseValue;
        statProgress.levelIncrease = levelIncrease;
        statProgress.levelPercentIncrease = levelPercentIncrease;
        this.startingStats.put(name, statProgress);
    }
    
    public void setStartingSkills(final ArrayList<Integer> skills) {
        this.startingSkills = skills;
    }
    
    public void addStartingItem(final int itemID, final int count, final boolean equipped) {
        final CharacterStartingItem item = new CharacterStartingItem();
        item.itemID = itemID;
        item.count = count;
        item.equipped = equipped;
        this.items.add(item);
    }
    
    public class CharacterStatProgression
    {
        public String statName;
        public int baseValue;
        public float levelIncrease;
        public float levelPercentIncrease;
    }
    
    public class CharacterStartingItem
    {
        public int itemID;
        public int count;
        public boolean equipped;
    }
}
