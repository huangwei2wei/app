// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.plugins;

import java.util.HashMap;
import java.util.Random;

import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.objects.ObjectStub;
import com.app.server.atavism.server.objects.SpawnData;
import com.app.server.atavism.server.plugins.MobManagerPlugin;

public class AgisMobPlugin extends MobManagerPlugin {
	public static int lootObjectTmpl = -1;
	public static int lootObjectDespawn = 30;
	// private static HashMap<OID, HashMap<Integer, Dome>> domes = new HashMap<OID, HashMap<Integer, Dome>>();
	// private static HashMap<String, InstanceTemplate> instanceTemplates= new HashMap<String, InstanceTemplate>();
	// private static HashMap<Integer, ContentCategory> contentCategories= new HashMap<Integer, ContentCategory>();
	// private static HashMap<Integer, BuildingGrid> buildingGrids = new HashMap<Integer, BuildingGrid>();
	// private static HashMap<Integer, ResourceGrid> resourceGrids= new HashMap<Integer, ResourceGrid>();
	// private static HashMap<Integer, Dialogue> = new HashMap<Integer, Dialogue>();
	// public static HashMap<Integer, MerchantTable> merchantTables= new HashMap<Integer, MerchantTable>();
	private static HashMap<Integer, SpawnData> spawnInfo = new HashMap<Integer, SpawnData>();
	public static HashMap<OID, ObjectStub> arenaSpawns = new HashMap<OID, ObjectStub>();
	private static final int PET_WHISTLE = 12;
	private static final int BASE_CATEGORY = 0;
	public static final String BEHAVIOR_TMPL_PROP = "behaviourTemplate";
	private static final int LEGION_PORTAL_DISPLAY_ID = 27;
	public static final int PORTAL_Y_OFFSET = 0;
	private static final int ISLAND_TYPE_WORLD = 0;
	private static final int ISLAND_TYPE_ARENA = 2;
	public static final int TIME_MULTIPLIER = 1000;
	public static boolean MOB_DEATH_EXP = true;
	private static int numFactories = 0;
	private static int numInstances = 0;
	static Random random = new Random();

	class CreateMobHook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage) msg;
			final OID oid = OID.fromLong((Long) spawnMsg.getProperty("playerOid"));
			final String world = (String) EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "world");
			final OID instanceOid = WorldManagerClient.getObjectInfo(oid).instanceOid;
			final OID accountID = (OID) EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
			if (!AgisMobPlugin.accountHasDeveloperAccess(oid, accountID, world)) {
				return true;
			}
			final InstanceTemplate island = AgisMobPlugin.instanceTemplates.get(world);
			boolean existingMob = false;
			int templateID = (Integer) spawnMsg.getProperty("templateID");
			final String name = (String) spawnMsg.getProperty("name");
			final String subtitle = (String) spawnMsg.getProperty("subtitle");
			final String species = (String) spawnMsg.getProperty("species");
			final String subspecies = (String) spawnMsg.getProperty("subspecies");
			final String gender = (String) spawnMsg.getProperty("gender");
			final int displayCount = (Integer) spawnMsg.getProperty("displayCount");
			final float scale = (Float) spawnMsg.getProperty("scale");
			final int soundSet = 1;
			final int level = (Integer) spawnMsg.getProperty("level");
			final int animState = 1;
			final int offset = 1900;
			final int hitBox = 1500;
			final int runThreshold = 2500;
			final int health = 100;
			final boolean attackable = (Boolean) spawnMsg.getProperty("attackable");
			final int mobType = (Integer) spawnMsg.getProperty("mobType");
			final int faction = (Integer) spawnMsg.getProperty("faction");
			final String questCategory = "";
			final LinkedList<Integer> displays = new LinkedList<Integer>();
			for (int i = 0; i < displayCount; ++i) {
				final int displayID = (Integer) spawnMsg.getProperty("display" + i);
				displays.add(displayID);
			}
			final int numEquipment = (Integer) spawnMsg.getProperty("equipCount");
			final LinkedList<Integer> equipIDs = new LinkedList<Integer>();
			for (int j = 0; j < numEquipment; ++j) {
				final int equipID = (Integer) spawnMsg.getProperty("equip" + j + "ID");
				equipIDs.add(equipID);
			}
			final int numLootTables = (Integer) spawnMsg.getProperty("lootTableCount");
			final HashMap<Integer, Integer> lootTables = new HashMap<Integer, Integer>();
			for (int k = 0; k < numLootTables; ++k) {
				final int tableID = (Integer) spawnMsg.getProperty("lootTable" + k + "ID");
				final int tableChance = (Integer) spawnMsg.getProperty("lootTable" + k + "Chance");
				lootTables.put(tableID, tableChance);
			}
			String equipment = "";
			Template tmpl;
			if (templateID == -1) {
				templateID = AgisMobPlugin.this.mobDataBase.writeMobData(island.getCategory(), name, subtitle, mobType, soundSet, displays, animState, scale, offset, hitBox, runThreshold, gender,
						level, attackable, faction, species, subspecies, questCategory);
				if (templateID == -1) {
					Log.error("MOB: Got error when writing mob data to the database");
					return true;
				}
				for (final int equipID2 : equipIDs) {
					equipment = String.valueOf(equipment) + "*" + equipID2 + "; ";
					AgisMobPlugin.this.mobDataBase.writeMobEquipmentData(island.getCategory(), templateID, equipID2);
				}
				AgisMobPlugin.this.mobDataBase.writeMobLootTables(island.getCategory(), templateID, lootTables);
				tmpl = new Template(name, templateID, ObjectManagerPlugin.MOB_TEMPLATE);
			} else {
				existingMob = true;
				AgisMobPlugin.this.mobDataBase.editMobData(templateID, name, subtitle, mobType, soundSet, displays, animState, scale, offset, hitBox, runThreshold, gender, level, attackable, faction,
						species, subspecies, questCategory);
				tmpl = ObjectManagerClient.getTemplate(templateID, ObjectManagerPlugin.MOB_TEMPLATE);
				final String oldEquipment = (String) tmpl.get(InventoryClient.NAMESPACE, ":inv_items");
				final LinkedList<Integer> oldEquipIDs = new LinkedList<Integer>();
				String[] split;
				for (int length = (split = oldEquipment.split(";")).length, l = 0; l < length; ++l) {
					String itemName = split[l];
					boolean equip = false;
					itemName = itemName.trim();
					if (!itemName.isEmpty()) {
						if (itemName.startsWith("*")) {
							itemName = itemName.substring(1);
							equip = true;
						}
						final int itemID = Integer.parseInt(itemName);
						if (equip) {
							oldEquipIDs.add(itemID);
						}
					}
				}
				for (final int equipID3 : equipIDs) {
					equipment = String.valueOf(equipment) + "*" + equipID3 + "; ";
					if (!oldEquipIDs.contains(equipID3)) {
						AgisMobPlugin.this.mobDataBase.writeMobEquipmentData(island.getCategory(), templateID, equipID3);
					}
				}
				for (final int equipID3 : oldEquipIDs) {
					if (!equipIDs.contains(equipID3)) {
						AgisMobPlugin.this.mobDataBase.deleteMobEquipmentData(templateID, equipID3);
					}
				}
				AgisMobPlugin.this.mobDataBase.writeMobLootTables(island.getCategory(), templateID, lootTables);
			}
			tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ID, (Serializable) templateID);
			tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable) ObjectTypes.mob);
			tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_PERCEPTION_RADIUS, (Serializable) 75);
			tmpl.put(WorldManagerClient.NAMESPACE, "subTitle", (Serializable) subtitle);
			tmpl.put(WorldManagerClient.NAMESPACE, "mobType", (Serializable) mobType);
			if (mobType == -1) {
				tmpl.put(WorldManagerClient.NAMESPACE, "nameDisplay", (Serializable) false);
				tmpl.put(WorldManagerClient.NAMESPACE, "targetable", (Serializable) false);
			}
			tmpl.put(WorldManagerClient.NAMESPACE, "soundSet", (Serializable) soundSet);
			tmpl.put(WorldManagerClient.NAMESPACE, "displays", (Serializable) displays);
			tmpl.put(WorldManagerClient.NAMESPACE, "genderOptions", (Serializable) gender);
			tmpl.put(WorldManagerClient.NAMESPACE, "animationState", (Serializable) 1);
			final AOVector v = new AOVector(scale, scale, scale);
			tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_SCALE, (Serializable) v);
			tmpl.put(WorldManagerClient.NAMESPACE, "overheadOffset", (Serializable) 1900);
			tmpl.put(WorldManagerClient.NAMESPACE, "hitBox", (Serializable) 1500);
			tmpl.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_RUN_THRESHOLD, (Serializable) 2.5f);
			tmpl.put(CombatClient.NAMESPACE, "attackable", (Serializable) attackable);
			tmpl.put(WorldManagerClient.NAMESPACE, "faction", (Serializable) faction);
			tmpl.put(WorldManagerClient.NAMESPACE, "species", (Serializable) species);
			tmpl.put(WorldManagerClient.NAMESPACE, "subSpecies", (Serializable) subspecies);
			AgisMobPlugin.this.putMobCombatStats(tmpl, level, health);
			tmpl.put(InventoryClient.NAMESPACE, ":inv_items", (Serializable) equipment);
			tmpl.put(InventoryClient.NAMESPACE, "lootTables", (Serializable) lootTables);
			ObjectManagerClient.registerTemplate(tmpl);
			Log.debug("MOB: loaded template: [" + tmpl.getName() + "]");
			AgisMobPlugin.this.sendMobTemplates(oid);
			if (existingMob) {
				SpawnGenerator.respawnMatchingMobs(instanceOid, templateID);
			}
			return true;
		}
	}

}
