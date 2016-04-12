// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

import atavism.agis.objects.Dialogue;
import atavism.agis.objects.ResourceGrid;
import java.util.Iterator;
import java.util.List;
import atavism.agis.objects.LootTable;
import atavism.agis.objects.Currency;
import atavism.agis.objects.AgisBasicQuest;
import atavism.agis.objects.Faction;
import atavism.server.plugins.InventoryClient;
import atavism.agis.objects.AgisStat;
import atavism.agis.plugins.CombatClient;
import atavism.server.math.AOVector;
import java.util.LinkedList;
import atavism.server.objects.ObjectTypes;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import atavism.agis.objects.BehaviorTemplate;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.util.Log;
import atavism.server.objects.SpawnData;
import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.Serializable;
import atavism.server.plugins.InstanceClient;
import atavism.agis.util.HelperFunctions;
import atavism.server.objects.Template;
import java.util.ArrayList;

public class MobDatabase
{
    protected static Queries queries;
    
    public MobDatabase(final boolean keepAlive) {
        if (MobDatabase.queries == null) {
            MobDatabase.queries = new Queries(keepAlive);
        }
    }
    
    public ArrayList<Template> loadInstanceTemplates() {
        final ArrayList<Template> list = new ArrayList<Template>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM instanceTemplates where isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final Template tmpl = new Template(HelperFunctions.readEncodedString(rs.getBytes("name")));
                    tmpl.put(InstanceClient.NAMESPACE, "templateID", (Serializable)rs.getInt("id"));
                    final String worldFile = "$WORLD_DIR/" + HelperFunctions.readEncodedString(rs.getBytes("worldFile"));
                    tmpl.put(InstanceClient.NAMESPACE, "worldFileName", (Serializable)worldFile);
                    tmpl.put(InstanceClient.NAMESPACE, "populationLimit", (Serializable)rs.getInt("populationLimit"));
                    tmpl.put(InstanceClient.NAMESPACE, "instanceType", (Serializable)rs.getInt("instanceType"));
                    tmpl.put(InstanceClient.NAMESPACE, "createOnStartup", (Serializable)rs.getBoolean("createOnStartup"));
                    list.add(tmpl);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public HashMap<Integer, SpawnData> loadSpawnData(final String instanceName) {
        final HashMap<Integer, SpawnData> list = new HashMap<Integer, SpawnData>();
        try {
            Log.debug("DB: loading spawnData from instance: " + instanceName);
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM spawn_data where instance = '" + instanceName + "' AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final SpawnData sd = new SpawnData();
                    final String markerName = HelperFunctions.readEncodedString(rs.getBytes("markerName"));
                    if (markerName == null || markerName.equals("")) {
                        sd.setProperty("markerName", (Serializable)"");
                        sd.setLoc(new Point(rs.getFloat("locX"), rs.getFloat("locY"), rs.getFloat("locZ")));
                        sd.setOrientation(new Quaternion(rs.getFloat("orientX"), rs.getFloat("orientY"), rs.getFloat("orientZ"), rs.getFloat("orientW")));
                    }
                    else {
                        sd.setProperty("markerName", (Serializable)markerName);
                    }
                    sd.setProperty("instance", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("instance")));
                    sd.setCategory(rs.getInt("category"));
                    final int mobID = rs.getInt("mobTemplate");
                    if (mobID == -1) {
                        continue;
                    }
                    sd.setTemplateID(mobID);
                    Log.debug("DB: loading spawnData with template: " + mobID);
                    sd.setNumSpawns(rs.getInt("numSpawns"));
                    sd.setSpawnRadius(rs.getInt("spawnRadius"));
                    sd.setRespawnTime(rs.getInt("respawnTime"));
                    sd.setCorpseDespawnTime(rs.getInt("corpseDespawnTime"));
                    sd.setProperty("id", (Serializable)rs.getInt("id"));
                    Log.debug("DB: loading mobBehaviour with template: " + mobID);
                    final BehaviorTemplate behavTmpl = new BehaviorTemplate(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")));
                    final String baseAction = HelperFunctions.readEncodedString(rs.getBytes("baseAction"));
                    if (baseAction != null && !baseAction.equals("")) {
                        behavTmpl.setBaseAction(baseAction);
                    }
                    behavTmpl.setWeaponsSheathed(rs.getBoolean("weaponSheathed"));
                    behavTmpl.setRoamRadius(rs.getInt("roamRadius"));
                    behavTmpl.setHasCombat(rs.getBoolean("combat"));
                    Log.debug("DB: loading quests with template: " + mobID);
                    final String startsQuests = HelperFunctions.readEncodedString(rs.getBytes("startsQuests"));
                    ArrayList<Integer> questList = new ArrayList<Integer>();
                    if (startsQuests != null && !startsQuests.equals("")) {
                        final String[] questNums = startsQuests.split(",");
                        Log.debug("MOB: num start quests: " + questNums.length);
                        for (int i = 0; i < questNums.length; ++i) {
                            final String questID = questNums[i].replace(",", "");
                            Log.debug("MOB: added start quest " + questID + " to behav: " + behavTmpl.getName());
                            if (!questList.equals("")) {
                                questList.add(Integer.parseInt(questID));
                            }
                        }
                    }
                    behavTmpl.setStartsQuests(questList);
                    Log.debug("MOB: moving onto end quests");
                    final String endsQuests = HelperFunctions.readEncodedString(rs.getBytes("endsQuests"));
                    questList = new ArrayList<Integer>();
                    if (endsQuests != null && !endsQuests.equals("")) {
                        final String[] questNums2 = endsQuests.split(",");
                        Log.debug("MOB: num end quests: " + questNums2.length);
                        for (int j = 0; j < questNums2.length; ++j) {
                            final String questID2 = questNums2[j].replace(",", "");
                            Log.debug("MOB: added end quest " + questID2 + " to behav: " + behavTmpl.getName());
                            if (!questList.equals("")) {
                                questList.add(Integer.parseInt(questID2));
                            }
                        }
                    }
                    behavTmpl.setEndsQuests(questList);
                    final String startsDialogues = HelperFunctions.readEncodedString(rs.getBytes("startsDialogues"));
                    final ArrayList<Integer> dialogueList = new ArrayList<Integer>();
                    if (startsDialogues != null && !startsDialogues.equals("")) {
                        final String[] dialogueNums = startsDialogues.split(",");
                        Log.debug("MOB: num start dialogues: " + dialogueNums.length);
                        for (int k = 0; k < dialogueNums.length; ++k) {
                            final String dialogueID = dialogueNums[k].replace(",", "");
                            if (!dialogueID.equals("")) {
                                dialogueList.add(Integer.parseInt(dialogueID));
                                Log.debug("MOB: added start dialogue " + dialogueID + " to behav: " + behavTmpl.getName());
                            }
                        }
                    }
                    behavTmpl.setStartsDialogues(dialogueList);
                    behavTmpl.setMerchantTable(rs.getInt("merchantTable"));
                    final Integer questOpenLootTable = rs.getInt("questOpenLootTable");
                    if (questOpenLootTable != null) {
                        behavTmpl.setQuestOpenLoot(questOpenLootTable);
                    }
                    final Boolean isChest = rs.getBoolean("isChest");
                    behavTmpl.setIsChest(isChest);
                    final Integer pickupItem = rs.getInt("pickupItem");
                    if (pickupItem != null) {
                        behavTmpl.setPickupItem(pickupItem);
                    }
                    sd.setProperty("behaviourTemplate", (Serializable)behavTmpl);
                    list.put(rs.getInt("id"), sd);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public int getSpawnCount(final String instanceName) {
        int spawnCount = 0;
        try {
            Log.debug("DB: loading spawnData from instance: " + instanceName);
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT COUNT(*) FROM spawn_data where instance = '" + instanceName + "' where isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                rs.next();
                spawnCount = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return spawnCount;
    }
    
    public SpawnData loadSpawnData(final int spawnID) {
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `loot_tables` where id = " + spawnID);
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final SpawnData sd = new SpawnData();
                    sd.setCategory(rs.getInt("category"));
                    final String markerName = HelperFunctions.readEncodedString(rs.getBytes("markerName"));
                    if (markerName == null || markerName.equals("")) {
                        sd.setProperty("markerName", (Serializable)"");
                        sd.setProperty("locX", (Serializable)rs.getInt("locX"));
                        sd.setProperty("locY", (Serializable)rs.getInt("locY"));
                        sd.setProperty("locZ", (Serializable)rs.getInt("locZ"));
                        sd.setProperty("orientX", (Serializable)rs.getInt("orientX"));
                        sd.setProperty("orientY", (Serializable)rs.getInt("orientY"));
                        sd.setProperty("orientZ", (Serializable)rs.getInt("orientZ"));
                        sd.setProperty("orientW", (Serializable)rs.getInt("orientW"));
                    }
                    else {
                        sd.setProperty("markerName", (Serializable)markerName);
                    }
                    sd.setProperty("instance", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("instance")));
                    final int mobID = rs.getInt("mobTemplate");
                    if (mobID == -1) {
                        continue;
                    }
                    sd.setTemplateID(mobID);
                    sd.setNumSpawns(rs.getInt("numSpawns"));
                    sd.setSpawnRadius(rs.getInt("spawnRadius"));
                    sd.setRespawnTime(rs.getInt("respawnTime"));
                    sd.setCorpseDespawnTime(rs.getInt("corpseDespawnTime"));
                    return sd;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public ArrayList<Template> loadMobTemplates(final int category) {
        final ArrayList<Template> list = new ArrayList<Template>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `mob_templates` where category=" + category + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int mobID = rs.getInt("id");
                    final String mobName = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    mobName.trim();
                    final Template tmpl = new Template(mobName, mobID, ObjectManagerPlugin.MOB_TEMPLATE);
                    tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ID, (Serializable)mobID);
                    tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)ObjectTypes.mob);
                    tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, (Serializable)75);
                    final String subTitle = HelperFunctions.readEncodedString(rs.getBytes("subTitle"));
                    if (subTitle == null) {
                        tmpl.put(WorldManagerClient.NAMESPACE, "subTitle", (Serializable)"");
                    }
                    else {
                        tmpl.put(WorldManagerClient.NAMESPACE, "subTitle", (Serializable)subTitle);
                    }
                    final int mobType = rs.getInt("mobType");
                    tmpl.put(WorldManagerClient.NAMESPACE, "mobType", (Serializable)mobType);
                    if (mobType == 1) {
                        tmpl.put(WorldManagerClient.NAMESPACE, "nameDisplay", (Serializable)false);
                        tmpl.put(WorldManagerClient.NAMESPACE, "targetable", (Serializable)false);
                    }
                    final LinkedList<String> displays = new LinkedList<String>();
                    if (HelperFunctions.readEncodedString(rs.getBytes("display1")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display1")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display1")));
                    }
                    if (HelperFunctions.readEncodedString(rs.getBytes("display2")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display2")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display2")));
                    }
                    if (HelperFunctions.readEncodedString(rs.getBytes("display3")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display3")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display3")));
                    }
                    if (HelperFunctions.readEncodedString(rs.getBytes("display4")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display4")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display4")));
                    }
                    if (displays.isEmpty()) {
                        Log.warn("No displays found for: " + mobName);
                    }
                    tmpl.put(WorldManagerClient.NAMESPACE, "displays", (Serializable)displays);
                    final float scaleVal = rs.getFloat("scale");
                    final AOVector v = new AOVector(scaleVal, scaleVal, scaleVal);
                    tmpl.put(WorldManagerClient.NAMESPACE, "scale", (Serializable)v);
                    tmpl.put(CombatClient.NAMESPACE, "hitBox", (Serializable)rs.getInt("hitBox"));
                    tmpl.put(WorldManagerClient.NAMESPACE, "animationState", (Serializable)rs.getInt("baseAnimationState"));
                    tmpl.put(WorldManagerClient.NAMESPACE, "faction", (Serializable)rs.getInt("faction"));
                    tmpl.put(CombatClient.NAMESPACE, "attackable", (Serializable)rs.getBoolean("attackable"));
                    final int level = rs.getInt("minLevel");
                    tmpl.put(CombatClient.NAMESPACE, "level", (Serializable)new AgisStat("level", level));
                    final int dmg_base = rs.getInt("minDmg");
                    tmpl.put(CombatClient.NAMESPACE, "dmg-base", (Serializable)new AgisStat("dmg-base", dmg_base));
                    final String attackType = HelperFunctions.readEncodedString(rs.getBytes("dmgType"));
                    tmpl.put(CombatClient.NAMESPACE, "attackType", (Serializable)attackType);
                    tmpl.put(CombatClient.NAMESPACE, "weaponType", (Serializable)"Unarmed");
                    final int exp_val = 100;
                    tmpl.put(CombatClient.NAMESPACE, "combat.autoability", (Serializable)rs.getInt("autoAttack"));
                    tmpl.put(CombatClient.NAMESPACE, "combat.mobflag", (Serializable)true);
                    tmpl.put(CombatClient.NAMESPACE, "kill_exp", (Serializable)exp_val);
                    tmpl.put(CombatClient.NAMESPACE, ":statOverrides", (Serializable)this.loadMobStats(mobID));
                    final LinkedList<Integer> effectsList = new LinkedList<Integer>();
                    tmpl.put(CombatClient.NAMESPACE, "effects", (Serializable)effectsList);
                    tmpl.put(WorldManagerClient.NAMESPACE, "species", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("species")));
                    tmpl.put(WorldManagerClient.NAMESPACE, "race", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("species")));
                    tmpl.put(WorldManagerClient.NAMESPACE, "subSpecies", (Serializable)HelperFunctions.readEncodedString(rs.getBytes("subSpecies")));
                    tmpl.put(WorldManagerClient.NAMESPACE, "speed_walk", (Serializable)rs.getFloat("speed_walk"));
                    tmpl.put(WorldManagerClient.NAMESPACE, "speed_run", (Serializable)rs.getFloat("speed_run"));
                    String equipment = "";
                    final Integer primaryWeapon = rs.getInt("primaryWeapon");
                    if (primaryWeapon != null && primaryWeapon > 0) {
                        equipment = String.valueOf(equipment) + "*" + primaryWeapon + "; ";
                    }
                    final Integer secondaryWeapon = rs.getInt("secondaryWeapon");
                    if (secondaryWeapon != null && secondaryWeapon > 0) {
                        equipment = String.valueOf(equipment) + "*" + secondaryWeapon + "; ";
                    }
                    tmpl.put(InventoryClient.NAMESPACE, ":inv_items", (Serializable)equipment);
                    Log.debug("ITEMS: mob " + tmpl.getName() + " now has equipment: " + equipment);
                    tmpl.put(InventoryClient.NAMESPACE, "lootTables", (Serializable)this.loadMobLoot(mobID));
                    final String specialUse = HelperFunctions.readEncodedString(rs.getBytes("specialUse"));
                    if (specialUse != null && !specialUse.equals("")) {
                        tmpl.put(WorldManagerClient.NAMESPACE, "specialUse", (Serializable)specialUse);
                    }
                    list.add(tmpl);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public ArrayList<HashMap<String, Serializable>> getMobTemplates(final int category, final int baseCategory) {
        Log.warn("MOB: getting mob templates");
        final ArrayList<HashMap<String, Serializable>> list = new ArrayList<HashMap<String, Serializable>>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `mob_templates` where category=" + category + " OR category=" + baseCategory + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
                    final int mobID = rs.getInt("id");
                    map.put("id", mobID);
                    final String mobName = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    mobName.trim();
                    map.put("name", mobName);
                    Log.warn("MOB: found mob template: " + mobName);
                    map.put("subTitle", HelperFunctions.readEncodedString(rs.getBytes("subTitle")));
                    map.put("mobType", rs.getInt("mobType"));
                    Log.warn("MOB: has mob type: " + rs.getInt("mobType"));
                    final LinkedList<String> displays = new LinkedList<String>();
                    if (HelperFunctions.readEncodedString(rs.getBytes("display1")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display1")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display1")));
                    }
                    if (HelperFunctions.readEncodedString(rs.getBytes("display2")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display2")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display2")));
                    }
                    if (HelperFunctions.readEncodedString(rs.getBytes("display3")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display3")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display3")));
                    }
                    if (HelperFunctions.readEncodedString(rs.getBytes("display4")) != null && !HelperFunctions.readEncodedString(rs.getBytes("display4")).equals("")) {
                        displays.add(HelperFunctions.readEncodedString(rs.getBytes("display4")));
                    }
                    Log.warn("MOB: about to check if any displays were found");
                    if (displays.isEmpty()) {
                        Log.warn("No displays found for: " + mobName);
                    }
                    map.put("displays", displays);
                    map.put("scale", rs.getFloat("scale"));
                    map.put("level", rs.getInt("minLevel"));
                    map.put("attackable", rs.getBoolean("attackable"));
                    map.put("faction", rs.getInt("faction"));
                    map.put("species", HelperFunctions.readEncodedString(rs.getBytes("species")));
                    map.put("subSpecies", HelperFunctions.readEncodedString(rs.getBytes("subSpecies")));
                    Log.warn("MOB: added mob template: " + mobName);
                    list.add(map);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        Log.warn("MOB: returning mob template list with " + list.size() + " templates");
        return list;
    }
    
    private HashMap<String, Integer> loadMobStats(final int mobID) {
        final HashMap<String, Integer> statOverrides = new HashMap<String, Integer>();
        Log.debug("MOB: loading in stat data for mob: " + mobID);
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `mob_stat` where mobTemplate=" + mobID + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String stat = HelperFunctions.readEncodedString(rs.getBytes("stat"));
                    final int value = rs.getInt("value");
                    statOverrides.put(stat, value);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        Log.debug("MOB: finished loading in stat data for mob: " + mobID);
        return statOverrides;
    }
    
    private BehaviorTemplate loadPatrolPath(final BehaviorTemplate behavTmpl, final int id) {
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `patrolPaths` where id=" + id);
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final ArrayList<String> patrolMarkers = new ArrayList<String>();
                    final String baseMarker = HelperFunctions.readEncodedString(rs.getBytes("baseMarker"));
                    final int firstMarker = rs.getInt("firstMarkerNum");
                    final int lastMarker = rs.getInt("lastMarkerNum");
                    for (int i = firstMarker; i <= lastMarker; ++i) {
                        final String markerName = String.valueOf(baseMarker) + "_" + i;
                        patrolMarkers.add(markerName);
                    }
                    if (rs.getBoolean("travelReverse")) {
                        for (int i = lastMarker - 1; i > firstMarker; --i) {
                            final String markerName = String.valueOf(baseMarker) + "_" + i;
                            patrolMarkers.add(markerName);
                        }
                    }
                    behavTmpl.setPatrolMarkers(patrolMarkers);
                    Log.debug("PATROL: added patrol marker list: " + patrolMarkers);
                    final Integer pauseDuration = rs.getInt("pauseDuration");
                    if (pauseDuration != null) {
                        behavTmpl.setPatrolPause(pauseDuration);
                    }
                    else {
                        behavTmpl.setPatrolPause(0);
                    }
                    final ArrayList<Integer> pauseSpots = new ArrayList<Integer>();
                    final Integer pauseSpot1 = rs.getInt("pauseSpot1");
                    if (pauseSpot1 != null) {
                        pauseSpots.add(pauseSpot1);
                    }
                    final Integer pauseSpot2 = rs.getInt("pauseSpot2");
                    if (pauseSpot2 != null) {
                        pauseSpots.add(pauseSpot2);
                    }
                    behavTmpl.setPatrolPauses(pauseSpots);
                    Log.debug("MOB: finished loading in patrol path data for behav: " + behavTmpl.getName());
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return behavTmpl;
    }
    
    private HashMap<Integer, Integer> loadMobLoot(final int mobID) {
        final HashMap<Integer, Integer> lootTables = new HashMap<Integer, Integer>();
        Log.debug("MOB: loading in loot data for mob: " + mobID);
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `mob_loot` where mobTemplate=" + mobID + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int lootTable = rs.getInt("lootTable");
                    final int tableChance = rs.getInt("dropChance");
                    lootTables.put(lootTable, tableChance);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        Log.debug("MOB: finished loading in loot data for mob: " + mobID);
        return lootTables;
    }
    
    public ArrayList<Faction> loadFactions(final int category) {
        final ArrayList<Faction> list = new ArrayList<Faction>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM factions where category=" + category + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final Faction f = new Faction(rs.getInt("id"), HelperFunctions.readEncodedString(rs.getBytes("name")), HelperFunctions.readEncodedString(rs.getBytes("factionGroup")), rs.getInt("category"));
                    f.setIsPublic(rs.getBoolean("public"));
                    f.setDefaultStance(rs.getInt("defaultStance"));
                    f.setDefaultStances(this.loadFactionStances(rs.getInt("id")));
                    list.add(f);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public HashMap<Integer, Integer> loadFactionStances(final int factionID) {
        final HashMap<Integer, Integer> factionStances = new HashMap<Integer, Integer>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `faction_stances` where factionID = " + factionID + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    factionStances.put(rs.getInt("otherFaction"), rs.getInt("defaultStance"));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return factionStances;
    }
    
    public HashMap<Integer, AgisBasicQuest> loadQuests(final int category) {
        final HashMap<Integer, AgisBasicQuest> list = new HashMap<Integer, AgisBasicQuest>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM quests where category=" + category + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final AgisBasicQuest q = new AgisBasicQuest();
                    final int questID = rs.getInt("id");
                    q.setID(questID);
                    q.setName(HelperFunctions.readEncodedString(rs.getBytes("name")));
                    q.setFaction(rs.getInt("faction"));
                    q.setRepeatable(rs.getBoolean("repeatable"));
                    q.setSecondaryGrades(rs.getInt("numGrades") - 1);
                    q.setDesc(HelperFunctions.readEncodedString(rs.getBytes("description")));
                    q.setObjective(HelperFunctions.readEncodedString(rs.getBytes("objectiveText")));
                    q.setProgressText(HelperFunctions.readEncodedString(rs.getBytes("progressText")));
                    int deliveryItem = rs.getInt("deliveryItem1");
                    if (deliveryItem != -1) {
                        q.addDeliveryItem(deliveryItem);
                    }
                    deliveryItem = rs.getInt("deliveryItem2");
                    if (deliveryItem != -1) {
                        q.addDeliveryItem(deliveryItem);
                    }
                    deliveryItem = rs.getInt("deliveryItem3");
                    if (deliveryItem != -1) {
                        q.addDeliveryItem(deliveryItem);
                    }
                    final int questPrereq = rs.getInt("questPrereq");
                    if (questPrereq != -1) {
                        q.addQuestPrereq(questPrereq);
                    }
                    final int questStartedReq = rs.getInt("questStartedReq");
                    if (questStartedReq != -1) {
                        q.setQuestStartedReq(questStartedReq);
                    }
                    q.setQuestLevelReq(rs.getInt("levelReq"));
                    String req = HelperFunctions.readEncodedString(rs.getBytes("raceReq"));
                    if (req != null && !req.contains("none")) {
                        q.setQuestRaceReq(req);
                    }
                    req = HelperFunctions.readEncodedString(rs.getBytes("aspectReq"));
                    if (req != null) {
                        q.setQuestAspectReq(req);
                    }
                    q.setQuestRepReq(rs.getInt("repLevelReq"));
                    this.loadQuestObjectives(q, questID);
                    final int rewardLevel = 0;
                    q.setCompletionText(rewardLevel, HelperFunctions.readEncodedString(rs.getBytes("completionText")));
                    q.setXpReward(rewardLevel, rs.getInt("experience"));
                    for (int i = 1; i < 5; ++i) {
                        final int item = rs.getInt("item" + i);
                        if (item != -1) {
                            final int itemCount = rs.getInt("item" + i + "count");
                            if (itemCount > 0) {
                                q.addReward(rewardLevel, item, itemCount);
                            }
                        }
                        final int itemToChoose = rs.getInt("chooseItem" + i);
                        if (itemToChoose != -1) {
                            final int itemCount2 = rs.getInt("chooseItem" + i + "count");
                            if (itemCount2 > 0) {
                                q.addRewardToChoose(rewardLevel, itemToChoose, itemCount2);
                            }
                        }
                    }
                    for (int i = 1; i < 3; ++i) {
                        final int currency = rs.getInt("currency" + i);
                        if (currency != -1) {
                            final int currencyCount = rs.getInt("currency" + i + "count");
                            q.setCurrencyReward(rewardLevel, currency, currencyCount);
                        }
                        final int faction = rs.getInt("rep" + i);
                        if (faction != -1) {
                            final int repCount = rs.getInt("rep" + i + "gain");
                            q.setRepReward(0, faction, repCount);
                        }
                    }
                    Log.debug("QDB: loaded quest rewards for quest: " + questID + ". Has experience:" + q.getXpReward().get(rewardLevel) + ". and completionText: " + q.getCompletionText());
                    list.put(questID, q);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void loadQuestObjectives(final AgisBasicQuest q, final int questID) {
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM quest_objectives where questID=" + questID + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final String objectiveType = HelperFunctions.readEncodedString(rs.getBytes("objectiveType"));
                    final boolean primaryObjective = rs.getBoolean("primaryObjective");
                    int objectiveLevel = 0;
                    if (!primaryObjective) {
                        objectiveLevel = 1;
                    }
                    final int target = rs.getInt("target");
                    final int targetCount = rs.getInt("targetCount");
                    final String targetText = HelperFunctions.readEncodedString(rs.getBytes("targetText"));
                    if (objectiveType.equals("mob")) {
                        final AgisBasicQuest.KillGoal goal = new AgisBasicQuest.KillGoal(objectiveLevel, target, targetText, targetCount);
                        q.addKillGoal(goal);
                    }
                    else if (objectiveType.equals("item")) {
                        final AgisBasicQuest.CollectionGoal goal2 = new AgisBasicQuest.CollectionGoal(objectiveLevel, target, targetText, targetCount);
                        q.addCollectionGoal(goal2);
                    }
                    else if (objectiveType.equals("mobCategory")) {
                        final AgisBasicQuest.CategoryKillGoal goal3 = new AgisBasicQuest.CategoryKillGoal(objectiveLevel, targetText, targetText, targetCount);
                        q.addCategoryKillGoal(goal3);
                    }
                    else {
                        if (!objectiveType.equals("task")) {
                            continue;
                        }
                        final AgisBasicQuest.TaskGoal goal4 = new AgisBasicQuest.TaskGoal(objectiveLevel, target, targetText, targetCount);
                        q.addTaskGoal(goal4);
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void loadQuestRewards(final AgisBasicQuest q, final int questID) {
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM quest_rewards where questID=" + questID + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int rewardLevel = rs.getInt("rewardLevel");
                    q.setCompletionText(rewardLevel, HelperFunctions.readEncodedString(rs.getBytes("completionText")));
                    q.setXpReward(rewardLevel, rs.getInt("experience"));
                    for (int i = 1; i < 5; ++i) {
                        final int item = rs.getInt("item" + i);
                        if (item != -1) {
                            final int itemCount = rs.getInt("item" + i + "count");
                            q.addReward(rewardLevel, item, itemCount);
                        }
                        final int itemToChoose = rs.getInt("itemToChoose" + i);
                        if (itemToChoose != -1) {
                            final int itemToChooseCount = rs.getInt("itemToChoose" + i + "count");
                            q.addRewardToChoose(rewardLevel, itemToChoose, itemToChooseCount);
                        }
                    }
                    for (int i = 1; i < 3; ++i) {
                        final int currency = rs.getInt("currency" + i);
                        if (currency != -1) {
                            final int currencyCount = rs.getInt("currency" + i + "count");
                            q.setCurrencyReward(rewardLevel, currency, currencyCount);
                        }
                        final int faction = rs.getInt("reputation" + i);
                        if (faction != -1) {
                            final int repCount = rs.getInt("reputation" + i + "count");
                            q.setRepReward(0, faction, repCount);
                        }
                    }
                    Log.debug("QDB: loaded quest rewards for quest: " + questID + ". Has experience:" + q.getXpReward().get(rewardLevel) + ". and completionText: " + q.getCompletionText());
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Currency> loadCurrencies(final int category) {
        final ArrayList<Currency> currencies = new ArrayList<Currency>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `currencies` where isSubCurrency < 1 AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final Currency newCurrency = new Currency();
                    final int currencyID = rs.getInt("id");
                    newCurrency.setCurrencyID(currencyID);
                    final String currencyName = HelperFunctions.readEncodedString(rs.getBytes("name"));
                    newCurrency.setCurrencyName(currencyName);
                    final String currencyIcon = HelperFunctions.readEncodedString(rs.getBytes("icon"));
                    newCurrency.setCurrencyIcon(currencyIcon);
                    final String currencyDescription = HelperFunctions.readEncodedString(rs.getBytes("description"));
                    newCurrency.setCurrencyDescription(currencyDescription);
                    final int currencyMax = rs.getInt("maximum");
                    newCurrency.setCurrencyMax(currencyMax);
                    final boolean external = rs.getBoolean("external");
                    newCurrency.setExternal(external);
                    final int subCurrency1ID = rs.getInt("subCurrency1");
                    if (subCurrency1ID > 0) {
                        final Currency subCurrency = this.loadSubCurrency(subCurrency1ID);
                        subCurrency.setParentCurrency(currencyID);
                        currencies.add(subCurrency);
                    }
                    final int subCurrency2ID = rs.getInt("subCurrency2");
                    if (subCurrency2ID > 0) {
                        final Currency subCurrency2 = this.loadSubCurrency(subCurrency2ID);
                        subCurrency2.setParentCurrency(subCurrency1ID);
                        currencies.add(subCurrency2);
                    }
                    currencies.add(newCurrency);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return currencies;
    }
    
    public Currency loadSubCurrency(final int currencyId) {
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `currencies` where id=" + currencyId + " AND isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null && rs.next()) {
                final Currency newCurrency = new Currency();
                final int currencyID = rs.getInt("id");
                newCurrency.setCurrencyID(currencyID);
                final String currencyName = HelperFunctions.readEncodedString(rs.getBytes("name"));
                newCurrency.setCurrencyName(currencyName);
                final String currencyIcon = HelperFunctions.readEncodedString(rs.getBytes("icon"));
                newCurrency.setCurrencyIcon(currencyIcon);
                final String currencyDescription = HelperFunctions.readEncodedString(rs.getBytes("description"));
                newCurrency.setCurrencyDescription(currencyDescription);
                final int currencyMax = rs.getInt("maximum");
                newCurrency.setCurrencyMax(currencyMax);
                return newCurrency;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public HashMap<Integer, LootTable> loadLootTables(final int category) {
        final HashMap<Integer, LootTable> list = new HashMap<Integer, LootTable>();
        try {
            PreparedStatement ps;
            if (category == -1) {
                ps = MobDatabase.queries.prepare("SELECT * FROM loot_tables where isactive = 1");
            }
            else {
                ps = MobDatabase.queries.prepare("SELECT * FROM loot_tables where category=" + category + " AND isactive = 1");
            }
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    Log.debug("LOOT: loading loot table:" + rs.getInt("id"));
                    final LootTable lTbl = new LootTable();
                    lTbl.setID(rs.getInt("id"));
                    lTbl.setName(HelperFunctions.readEncodedString(rs.getBytes("name")));
                    final ArrayList<Integer> items = new ArrayList<Integer>();
                    final ArrayList<Integer> itemCounts = new ArrayList<Integer>();
                    final ArrayList<Integer> itemChances = new ArrayList<Integer>();
                    for (int i = 1; i <= 10; ++i) {
                        final int itemID = rs.getInt("item" + i);
                        if (itemID != -1) {
                            items.add(itemID);
                            itemCounts.add(rs.getInt("item" + i + "Count"));
                            itemChances.add(rs.getInt("item" + i + "Chance"));
                        }
                    }
                    lTbl.setItems(items);
                    lTbl.setItemCounts(itemCounts);
                    lTbl.setItemChances(itemChances);
                    list.put(lTbl.getID(), lTbl);
                    Log.debug("LOOT: put loot table:" + lTbl.getID());
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void loadLootTableDrops(final LootTable lTbl) {
        final ArrayList<Integer> items = new ArrayList<Integer>();
        final ArrayList<Integer> itemCounts = new ArrayList<Integer>();
        final ArrayList<Integer> itemChances = new ArrayList<Integer>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM loot_table_drops where lootTable=" + lTbl.getID() + " where isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    items.add(rs.getInt("item"));
                    itemCounts.add(rs.getInt("itemCount"));
                    itemChances.add(rs.getInt("dropChance"));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        lTbl.setItems(items);
        lTbl.setItemCounts(itemCounts);
        lTbl.setItemChances(itemChances);
    }
    
    public int writeQuest(final int category, final AgisBasicQuest q) {
        Log.debug("Writing quest data to database");
        int questPrereq = -1;
        if (q.getQuestPrereqs().size() > 0) {
            questPrereq = q.getQuestPrereqs().get(0);
        }
        int inserted = -1;
        String tableName = "quests";
        String columnNames = "category,name,faction,numGrades,repeatable,description,objectiveText,progressText,deliveryItem1,questPrereq,levelReq";
        try {
            final PreparedStatement stmt = MobDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, category);
            stmt.setString(2, q.getName());
            stmt.setInt(3, q.getFaction());
            stmt.setInt(4, q.getSecondaryGrades());
            stmt.setBoolean(5, q.getRepeatable());
            stmt.setString(6, q.getDesc());
            stmt.setString(7, q.getObjective());
            stmt.setString(8, q.getProgressText());
            stmt.setInt(9, q.getDeliveryItems().get(0));
            stmt.setInt(10, questPrereq);
            stmt.setInt(11, q.getQuestLevelReq());
            inserted = MobDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        if (inserted == -1) {
            return inserted;
        }
        this.writeQuestObjectives(inserted, q);
        tableName = "questRewards";
        columnNames = "questID,rewardLevel,completionText,experience";
        try {
            final PreparedStatement stmt = MobDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?)");
            stmt.setInt(1, inserted);
            stmt.setInt(2, 0);
            stmt.setString(3, q.getCompletionText().get(0));
            stmt.setInt(4, q.getXpReward().get(0));
            MobDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        this.writeQuestRewards(inserted, q);
        Log.debug("Wrote quest data to database");
        return inserted;
    }
    
    public int editQuest(final int questID, final AgisBasicQuest q) {
        Log.debug("Writing quest data to database");
        int questPrereq = -1;
        if (q.getQuestPrereqs().size() > 0) {
            questPrereq = q.getQuestPrereqs().get(0);
        }
        String tableName = "quests";
        int updated;
        try {
            final PreparedStatement stmt = MobDatabase.queries.prepare("UPDATE " + tableName + " set name=?, faction=?, numGrades=?, " + "repeatable=?, description=?, objectiveText=?, progressText=?, deliveryItem1=?, " + "questPrereq=?, levelReq=? where id=?");
            stmt.setString(1, q.getName());
            stmt.setInt(2, q.getFaction());
            stmt.setInt(3, q.getSecondaryGrades());
            stmt.setBoolean(4, q.getRepeatable());
            stmt.setString(5, q.getDesc());
            stmt.setString(6, q.getObjective());
            stmt.setString(7, q.getProgressText());
            stmt.setInt(8, q.getDeliveryItems().get(0));
            stmt.setInt(9, questPrereq);
            stmt.setInt(10, q.getQuestLevelReq());
            stmt.setInt(11, questID);
            Log.debug("QUESTDB: updating quest with statement: " + stmt.toString());
            updated = MobDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        if (updated == -1) {
            return updated;
        }
        tableName = "questObjectives";
        final String deleteString = "DELETE FROM `" + tableName + "` WHERE questID = " + questID;
        MobDatabase.queries.executeUpdate(deleteString);
        this.writeQuestObjectives(questID, q);
        tableName = "questRewards";
        try {
            final PreparedStatement stmt2 = MobDatabase.queries.prepare("UPDATE " + tableName + " set completionText = ?, experience = ? where questID=? AND rewardLevel=0");
            stmt2.setString(1, q.getCompletionText().get(0));
            stmt2.setInt(2, q.getXpReward().get(0));
            stmt2.setInt(3, questID);
            Log.debug("QUESTDB: placing item stmt=" + stmt2.toString());
            MobDatabase.queries.executeUpdate(stmt2);
        }
        catch (SQLException e2) {
            return -1;
        }
        this.writeQuestRewards(questID, q);
        Log.debug("Wrote quest data to database");
        return updated;
    }
    
    public void writeQuestObjectives(final int questID, final AgisBasicQuest q) {
        final String tableName = "questObjectives";
        final List<AgisBasicQuest.CollectionGoal> cGoals = q.getCollectionGoals();
        for (int i = 1; i <= cGoals.size(); ++i) {
            final AgisBasicQuest.CollectionGoal cGoal = cGoals.get(i - 1);
            try {
                final String columnNames = "questID,primaryObjective,objectiveType,target,targetCount,targetText";
                final PreparedStatement stmt = MobDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?)");
                stmt.setInt(1, questID);
                stmt.setInt(2, 1);
                stmt.setString(3, "item");
                stmt.setInt(4, cGoal.templateID);
                stmt.setInt(5, cGoal.num);
                stmt.setString(6, cGoal.templateName);
                MobDatabase.queries.executeInsert(stmt);
            }
            catch (SQLException e) {
                return;
            }
        }
        final List<AgisBasicQuest.KillGoal> kGoals = q.getKillGoals();
        for (int j = 1; j <= kGoals.size(); ++j) {
            final AgisBasicQuest.KillGoal kGoal = kGoals.get(j - 1);
            try {
                final String columnNames2 = "questID,primaryObjective,objectiveType,target,targetCount,targetText";
                final PreparedStatement stmt2 = MobDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames2 + ") values (?, ?, ?, ?, ?, ?)");
                stmt2.setInt(1, questID);
                stmt2.setInt(2, 1);
                stmt2.setString(3, "mob");
                stmt2.setInt(4, kGoal.mobID);
                stmt2.setInt(5, kGoal.num);
                stmt2.setString(6, kGoal.mobName);
                MobDatabase.queries.executeInsert(stmt2);
            }
            catch (SQLException e2) {
                return;
            }
        }
    }
    
    public void writeQuestRewards(final int questID, final AgisBasicQuest q) {
        final String tableName = "questRewards";
        int numRewards = 1;
        if (q.getRewards().containsKey(0)) {
            final HashMap<Integer, Integer> rewards = q.getRewards().get(0);
            for (final int item : rewards.keySet()) {
                try {
                    final PreparedStatement stmt = MobDatabase.queries.prepare("UPDATE " + tableName + " set item" + numRewards + " = ?, item" + numRewards + "count = ? where questID=? AND rewardLevel=0");
                    stmt.setInt(1, item);
                    stmt.setInt(2, rewards.get(item));
                    stmt.setInt(3, questID);
                    Log.debug("QUESTDB: placing item stmt=" + stmt.toString());
                    MobDatabase.queries.executeUpdate(stmt);
                }
                catch (SQLException e) {
                    return;
                }
                ++numRewards;
            }
        }
        if (q.getRewardsToChoose().containsKey(0)) {
            final HashMap<Integer, Integer> rewards = q.getRewardsToChoose().get(0);
            numRewards = 1;
            for (final int item : rewards.keySet()) {
                try {
                    final PreparedStatement stmt = MobDatabase.queries.prepare("UPDATE " + tableName + " set itemToChoose" + numRewards + " = ?, itemToChoose" + numRewards + "count = ? where questID=? AND rewardLevel=0");
                    stmt.setInt(1, item);
                    stmt.setInt(2, rewards.get(item));
                    stmt.setInt(3, questID);
                    Log.debug("QUESTDB: placing itemToChoose stmt=" + stmt.toString());
                    MobDatabase.queries.executeUpdate(stmt);
                }
                catch (SQLException e) {
                    return;
                }
                ++numRewards;
            }
        }
        final HashMap<Integer, Integer> currencyRewards = q.getCurrencyRewards().get(0);
        int numCurrencyRewards = 1;
        for (final int currency : currencyRewards.keySet()) {
            try {
                final PreparedStatement stmt2 = MobDatabase.queries.prepare("UPDATE " + tableName + " set currency" + numCurrencyRewards + " = ?, currency" + numCurrencyRewards + "count = ? where questID=? AND rewardLevel=0");
                stmt2.setInt(1, currency);
                stmt2.setInt(2, currencyRewards.get(currency));
                stmt2.setInt(3, questID);
                Log.debug("QUESTDB: placing currency stmt=" + stmt2.toString());
                MobDatabase.queries.executeUpdate(stmt2);
            }
            catch (SQLException e2) {
                return;
            }
            ++numCurrencyRewards;
        }
        final HashMap<Integer, Integer> repRewards = q.getRepRewards().get(0);
        int numRepRewards = 1;
        for (final int faction : repRewards.keySet()) {
            try {
                final PreparedStatement stmt3 = MobDatabase.queries.prepare("UPDATE " + tableName + " set reputation" + numRepRewards + " = ?, reputation" + numRepRewards + "count = ? where questID=? AND rewardLevel=0");
                stmt3.setInt(1, faction);
                stmt3.setInt(2, repRewards.get(faction));
                stmt3.setInt(3, questID);
                Log.debug("QUESTDB: placing rep stmt=" + stmt3.toString());
                MobDatabase.queries.executeUpdate(stmt3);
            }
            catch (SQLException e3) {
                return;
            }
            ++numRepRewards;
        }
    }
    
    public int writeSpawnData(final SpawnData sd, final Point loc, final Quaternion orient, final BehaviorTemplate behavTmpl, final String instanceName) {
        Log.error("Writing spawn data to database");
        String startsQuests = "";
        for (final int questID : behavTmpl.getStartsQuests()) {
            Log.debug("Starts quests:" + behavTmpl.getStartsQuests());
            startsQuests = String.valueOf(startsQuests) + questID + ",";
        }
        String endsQuests = "";
        for (final int questID2 : behavTmpl.getEndsQuests()) {
            Log.debug("Ends quests:" + behavTmpl.getEndsQuests());
            endsQuests = String.valueOf(endsQuests) + questID2 + ",";
        }
        String startsDialogues = "";
        for (final int dialogueID : behavTmpl.getStartsDialogues()) {
            startsDialogues = String.valueOf(startsDialogues) + dialogueID + ",";
        }
        int inserted = -1;
        try {
            final String tableName = "spawn_data";
            final String columnNames = "category,name,mobTemplate,markerName,locX,locY,locZ,orientX,orientY,orientZ,orientW,instance,numSpawns,spawnRadius,respawnTime,corpseDespawnTime,combat,roamRadius,startsQuests,endsQuests,startsDialogues,baseAction,weaponSheathed,merchantTable,questOpenLootTable,isChest,pickupItem";
            final PreparedStatement stmt = MobDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, sd.getCategory());
            stmt.setString(2, "spawn");
            stmt.setInt(3, sd.getTemplateID());
            stmt.setString(4, "");
            stmt.setFloat(5, loc.getX());
            stmt.setFloat(6, loc.getY());
            stmt.setFloat(7, loc.getZ());
            stmt.setFloat(8, orient.getX());
            stmt.setFloat(9, orient.getY());
            stmt.setFloat(10, orient.getZ());
            stmt.setFloat(11, orient.getW());
            stmt.setString(12, instanceName);
            stmt.setInt(13, sd.getNumSpawns());
            stmt.setInt(14, sd.getSpawnRadius());
            stmt.setInt(15, sd.getRespawnTime());
            stmt.setInt(16, sd.getCorpseDespawnTime());
            stmt.setBoolean(17, behavTmpl.getHasCombat());
            stmt.setInt(18, behavTmpl.getRoamRadius());
            stmt.setString(19, startsQuests);
            stmt.setString(20, endsQuests);
            stmt.setString(21, startsDialogues);
            stmt.setString(22, behavTmpl.getBaseAction());
            stmt.setBoolean(23, behavTmpl.getWeaponsSheathed());
            stmt.setInt(24, behavTmpl.getMerchantTable());
            stmt.setInt(25, behavTmpl.getQuestOpenLoot());
            stmt.setBoolean(26, behavTmpl.getIsChest());
            stmt.setInt(27, behavTmpl.getPickupItem());
            Log.error("Spawn Data statement = " + stmt.toString());
            inserted = MobDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException e) {
            Log.error("Failed to write spawn data to database with id= " + inserted);
            return -1;
        }
        Log.error("Wrote spawn data to database with id= " + inserted);
        return inserted;
    }
    
    public int editSpawnData(final SpawnData sd, final int spawnID, final Point loc, final Quaternion orient, final BehaviorTemplate behavTmpl) {
        Log.debug("Editing spawn data to database");
        String startsQuests = "";
        for (final int questID : behavTmpl.getStartsQuests()) {
            Log.debug("Starts quests:" + behavTmpl.getStartsQuests());
            startsQuests = String.valueOf(startsQuests) + questID + ",";
        }
        String endsQuests = "";
        for (final int questID2 : behavTmpl.getEndsQuests()) {
            Log.debug("Ends quests:" + behavTmpl.getEndsQuests());
            endsQuests = String.valueOf(endsQuests) + questID2 + ",";
        }
        String startsDialogues = "";
        for (final int dialogueID : behavTmpl.getStartsDialogues()) {
            startsDialogues = String.valueOf(startsDialogues) + dialogueID + ",";
        }
        final String tableName = "spawn_data";
        final PreparedStatement stmt = MobDatabase.queries.prepare("UPDATE " + tableName + " set mobTemplate=?, locX=?, locY=?, locZ=?, orientX=?, orientY=?," + " orientZ=?, orientW=?, numSpawns=?, spawnRadius=?, respawnTime=?, corpseDespawnTime=?, combat=?, roamRadius=?," + " startsQuests=?, endsQuests=?, startsDialogues=?, baseAction=?, weaponSheathed=?, merchantTable=?, questOpenLootTable=?," + " isChest=?, pickupItem=? where id=?");
        int updated;
        try {
            stmt.setInt(1, sd.getTemplateID());
            stmt.setFloat(2, loc.getX());
            stmt.setFloat(3, loc.getY());
            stmt.setFloat(4, loc.getZ());
            stmt.setFloat(5, orient.getX());
            stmt.setFloat(6, orient.getY());
            stmt.setFloat(7, orient.getZ());
            stmt.setFloat(8, orient.getW());
            stmt.setInt(9, sd.getNumSpawns());
            stmt.setInt(10, sd.getSpawnRadius());
            stmt.setInt(11, sd.getRespawnTime());
            stmt.setInt(12, sd.getCorpseDespawnTime());
            stmt.setBoolean(13, behavTmpl.getHasCombat());
            stmt.setInt(14, behavTmpl.getRoamRadius());
            stmt.setString(15, startsQuests);
            stmt.setString(16, endsQuests);
            stmt.setString(17, startsDialogues);
            stmt.setString(18, behavTmpl.getBaseAction());
            stmt.setBoolean(19, behavTmpl.getWeaponsSheathed());
            stmt.setInt(20, behavTmpl.getMerchantTable());
            stmt.setInt(21, behavTmpl.getQuestOpenLoot());
            stmt.setBoolean(22, behavTmpl.getIsChest());
            stmt.setInt(23, behavTmpl.getPickupItem());
            stmt.setInt(24, spawnID);
            updated = MobDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            e.printStackTrace();
            updated = -1;
        }
        Log.debug("Edited spawn data to database");
        return updated;
    }
    
    public void deleteSpawnData(final int spawnID) {
        final String tableName = "spawn_data";
        final String deleteString = "DELETE FROM `" + tableName + "` WHERE id = " + spawnID;
        MobDatabase.queries.executeUpdate(deleteString);
    }
    
    public int writeNpcDisplayData(final String name, final String race, final String gender) {
        Log.debug("Writing npc appearance data to database");
        final String tableName = "npcDisplay";
        final String columnNames = "name,race,gender,skinColour";
        final String values = "'" + name + "','" + race + "','" + gender;
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = MobDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote npc appearance data to database");
        return inserted;
    }
    
    public int writeMobData(final int category, final String name, final String subtitle, final int mobType, final int soundSet, final LinkedList<Integer> displays, final int animState, final float scale, final int offset, final int hitBox, final int runThreshold, final String gender, final int level, final boolean attackable, final int faction, final String species, final String subSpecies, final String questCategory) {
        Log.debug("Writing mob data to database");
        int inserted = -1;
        final String tableName = "mob_templates";
        final String columnNames = "category,name,subTitle,mobType,soundSet,display1,display2,display3,display4,baseAnimationState,scale,overheadOffset,hitBox,runThreshold,gender,level,attackable,faction,species,subSpecies,questCategory";
        try {
            final PreparedStatement stmt = MobDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, category);
            stmt.setString(2, name);
            stmt.setString(3, subtitle);
            stmt.setInt(4, mobType);
            stmt.setInt(5, soundSet);
            for (int i = 0; i <= 4; ++i) {
                if (displays.size() > i) {
                    stmt.setInt(6 + i, displays.get(i));
                }
                else {
                    stmt.setInt(6 + i, -1);
                }
            }
            stmt.setInt(10, animState);
            stmt.setFloat(11, scale);
            stmt.setInt(12, offset);
            stmt.setInt(13, hitBox);
            stmt.setInt(14, runThreshold);
            stmt.setString(15, gender);
            stmt.setInt(16, level);
            stmt.setBoolean(17, attackable);
            stmt.setInt(18, faction);
            stmt.setString(19, species);
            stmt.setString(20, subSpecies);
            stmt.setString(21, questCategory);
            inserted = MobDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException ex) {}
        Log.debug("Wrote mob data to database");
        return inserted;
    }
    
    public int writeMobCombatData(final int mobID, final int health, final String attackType) {
        Log.debug("Writing mob combat data to database");
        final String tableName = "mobCombatStats";
        final String columnNames = "id,health,attackType";
        final String values = String.valueOf(mobID) + "," + health + ",'" + attackType + "'";
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = MobDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote mob combat data to database");
        return inserted;
    }
    
    public int writeMobEquipmentData(final int category, final int mobID, final int item) {
        Log.debug("Writing mob equip data to database");
        final String tableName = "mobEquipment";
        final String columnNames = "category,mobTemplate,item";
        final String values = String.valueOf(category) + "," + mobID + "," + item;
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = MobDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote mob equip data to database");
        return inserted;
    }
    
    public void writeMobLootTables(final int category, final int mobTemplate, final HashMap<Integer, Integer> lootTables) {
        Log.debug("Writing mob loot tables to database");
        final String tableName = "mobLoot";
        final String deleteString = "DELETE FROM `" + tableName + "` WHERE mobTemplate = " + mobTemplate;
        MobDatabase.queries.executeUpdate(deleteString);
        final String columnNames = "category,mobTemplate,lootTable,dropChance";
        for (final int tableID : lootTables.keySet()) {
            try {
                final PreparedStatement stmt = MobDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?)");
                stmt.setInt(1, category);
                stmt.setInt(2, mobTemplate);
                stmt.setInt(3, tableID);
                stmt.setInt(4, lootTables.get(tableID));
                MobDatabase.queries.executeInsert(stmt);
            }
            catch (SQLException ex) {}
        }
        Log.debug("Wrote mob loot tables to database");
    }
    
    public int editMobData(final int templateID, final String name, final String subtitle, final int mobType, final int soundSet, final LinkedList<Integer> displays, final int animState, final float scale, final int offset, final int hitBox, final int runThreshold, final String gender, final int level, final boolean attackable, final int faction, final String species, final String subSpecies, final String questCategory) {
        Log.debug("Writing mob data to database");
        int updated = -1;
        final String tableName = "mobTemplates";
        try {
            final PreparedStatement stmt = MobDatabase.queries.prepare("UPDATE " + tableName + " set name=?, subTitle=?, mobType=?, soundSet=?, display1=?," + "display2=?, display3=?, display4=?, baseAnimationState=?, scale=?, overheadOffset=?, hitBox=?," + "runThreshold=?, gender=?, level=?, attackable=?, faction=?, species=?, subSpecies=?, questCategory=? where id=?");
            stmt.setString(1, name);
            stmt.setString(2, subtitle);
            stmt.setInt(3, mobType);
            stmt.setInt(4, soundSet);
            for (int i = 0; i <= 4; ++i) {
                if (displays.size() > i) {
                    stmt.setInt(5 + i, displays.get(i));
                }
                else {
                    stmt.setInt(5 + i, -1);
                }
            }
            stmt.setInt(9, animState);
            stmt.setFloat(10, scale);
            stmt.setInt(11, offset);
            stmt.setInt(12, hitBox);
            stmt.setInt(13, runThreshold);
            stmt.setString(14, gender);
            stmt.setInt(15, level);
            stmt.setBoolean(16, attackable);
            stmt.setInt(17, faction);
            stmt.setString(18, species);
            stmt.setString(19, subSpecies);
            stmt.setString(20, questCategory);
            stmt.setInt(21, templateID);
            Log.debug("MOBDB: placing mob stmt=" + stmt.toString());
            updated = MobDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException ex) {}
        Log.debug("Wrote mob data to database");
        return updated;
    }
    
    public int editMobCombatData(final int mobID, final int health, final String attackType) {
        Log.debug("Writing mob combat data to database");
        final String tableName = "mobCombatStats";
        final String updateString = "UPDATE `" + tableName + "` set health=" + health + ", attackType='" + attackType + "' where id=" + mobID;
        final int updated = MobDatabase.queries.executeUpdate(updateString);
        Log.debug("Wrote mob combat data to database");
        return updated;
    }
    
    public int deleteMobEquipmentData(final int mobID, final int item) {
        Log.debug("Deleting mob equip data to database");
        final String tableName = "mobEquipment";
        final String deleteString = "DELETE FROM `" + tableName + "` WHERE mobTemplate = " + mobID + " AND item = " + item;
        final int deleted = MobDatabase.queries.executeUpdate(deleteString);
        Log.debug("Deleting mob equip data to database");
        return deleted;
    }
    
    public int writeFactionData(final int category, final String name, final String group, final boolean isPublic, final int defaultStance) {
        Log.debug("Writing faction data to database");
        final String tableName = "factions";
        final String columnNames = "category,name,group,public,defaultStance";
        final String values = String.valueOf(category) + ",'" + name + "','" + group + "'," + isPublic + "," + defaultStance;
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = MobDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote faction data to database");
        return inserted;
    }
    
    public int writeFactionStanceData(final int factionID, final int otherFaction, final int defaultStance) {
        Log.debug("Writing faction stance data to database");
        final String tableName = "factionStances";
        final String columnNames = "factionID,otherFaction,defaultStance";
        final String values = String.valueOf(factionID) + "," + otherFaction + "," + defaultStance;
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = MobDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote faction stance data to database");
        return inserted;
    }
    
    public int writeLootTable(final int category, final LootTable lTbl) {
        Log.debug("Writing loot table data to database");
        final String tableName = "lootTables";
        final String columnNames = "category,name";
        final String values = String.valueOf(category) + ",'" + lTbl.getName() + "'";
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = MobDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote loot table data to database");
        return inserted;
    }
    
    public int editLootTable(final int tableID, final LootTable lTable) {
        Log.debug("Writing loot table data to database");
        String tableName = "lootTables";
        String updateString = "UPDATE `" + tableName + "` set name='" + lTable.getName() + "' where id=" + tableID;
        final int updated = MobDatabase.queries.executeUpdate(updateString);
        tableName = "lootTableDrops";
        updateString = "DELETE from " + tableName + " where lootTable=" + tableID;
        MobDatabase.queries.executeUpdate(updateString);
        Log.debug("Wrote loot table data to database");
        return updated;
    }
    
    public int writeLootTableDrops(final int tableID, final int item, final int itemCount, final int dropChance) {
        Log.debug("Writing loot table data to database");
        final String tableName = "lootTableDrops";
        final String columnNames = "lootTable,item,itemCount,dropChance";
        final String values = String.valueOf(tableID) + "," + item + "," + itemCount + "," + dropChance;
        final String insertString = "INSERT INTO `" + tableName + "` (" + columnNames + ") VALUES (" + values + ")";
        final int inserted = MobDatabase.queries.executeInsert(insertString);
        Log.debug("Wrote loot table data to database");
        return inserted;
    }
    
    public HashMap<Integer, ResourceGrid> loadResourceGrids(final String instance) {
        final HashMap<Integer, ResourceGrid> grids = new HashMap<Integer, ResourceGrid>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM `resource_grids` where instance=?");
            ps.setString(1, instance);
            Log.debug("GRID: " + ps.toString());
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final ResourceGrid bg = new ResourceGrid();
                    bg.setID(rs.getInt("id"));
                    bg.setInstance(instance);
                    final Point p = new Point();
                    p.setX(rs.getFloat("locX"));
                    p.setY(rs.getFloat("locY"));
                    p.setZ(rs.getFloat("locZ"));
                    bg.setPosition(p);
                    bg.setResourceType(HelperFunctions.readEncodedString(rs.getBytes("type")));
                    bg.setCount(rs.getInt("count"));
                    bg.setRotation(rs.getFloat("rotation"));
                    grids.put(bg.getID(), bg);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return grids;
    }
    
    public int resourceGridUpdated(final ResourceGrid grid) {
        Log.debug("GRID: Updating resource grid in the database");
        final String tableName = "resource_grids";
        int updated = -1;
        try {
            final PreparedStatement stmt = MobDatabase.queries.prepare("UPDATE " + tableName + " set count=? where id=?");
            stmt.setInt(1, grid.getCount());
            stmt.setInt(2, grid.getID());
            Log.debug("MOBDB: placing resource grid stmt=" + stmt.toString());
            updated = MobDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException ex) {}
        Log.debug("GRID: Updated resource grid data in the database");
        return updated;
    }
    
    public HashMap<Integer, Dialogue> loadDialogues() {
        final HashMap<Integer, Dialogue> dialogues = new HashMap<Integer, Dialogue>();
        try {
            final PreparedStatement ps = MobDatabase.queries.prepare("SELECT * FROM dialogue where isactive = 1");
            final ResultSet rs = MobDatabase.queries.executeSelect(ps);
            if (rs != null) {
                while (rs.next()) {
                    final int id = rs.getInt("id");
                    final Dialogue d = new Dialogue(id, HelperFunctions.readEncodedString(rs.getBytes("name")), HelperFunctions.readEncodedString(rs.getBytes("text")));
                    d.setOpeningDialogue(rs.getBoolean("openingDialogue"));
                    d.setRepeatable(rs.getBoolean("repeatable"));
                    for (int i = 1; i < 3; ++i) {
                        final String action = HelperFunctions.readEncodedString(rs.getBytes("option" + i + "action"));
                        final int actionID = rs.getInt("option" + i + "actionID");
                        if (action != null && !action.equals("") && actionID > 0) {
                            final String text = HelperFunctions.readEncodedString(rs.getBytes("option" + i + "text"));
                            d.addOption(text, action, actionID);
                        }
                    }
                    dialogues.put(id, d);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return dialogues;
    }
    
    public void close() {
        MobDatabase.queries.close();
    }
}
