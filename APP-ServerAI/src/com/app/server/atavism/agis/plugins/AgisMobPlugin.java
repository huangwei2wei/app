// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.plugins;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.objects.ObjectStub;
import com.app.server.atavism.server.objects.ObjectTypes;
import com.app.server.atavism.server.objects.SpawnData;
import com.app.server.atavism.server.objects.Template;
import com.app.server.atavism.server.plugins.MobManagerPlugin;
import com.app.server.atavism.server.plugins.ObjectManagerPlugin;
import com.app.server.atavism.server.plugins.WorldManagerClient;

public class AgisMobPlugin extends MobManagerPlugin {
	private static AgisMobPlugin agisMobPlugin = new AgisMobPlugin();
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
	
	public static AgisMobPlugin getAgisMobPlugin(){
		return agisMobPlugin;
	}
	
	public void createMob() {
		// final WorldManagerClient.ExtensionMessage spawnMsg = (WorldManagerClient.ExtensionMessage) msg;
		// final OID oid = OID.fromLong((Long) spawnMsg.getProperty("playerOid"));

		boolean existingMob = false;
		int templateID = Integer.valueOf(-1);
		final String name = "name";
		final String subtitle = "subtitle";
		final String species = "species";
		final String subspecies = "subspecies";
		final String gender = "gender";
		final int displayCount = 0;
		final float scale = (Float) 1.0f;// 比例
		final int soundSet = 1;
		final int level = Integer.valueOf(1);
		final int animState = 1;
		final int offset = 1900;
		final int hitBox = 1500;
		final int runThreshold = 2500;
		final int health = 100;
		final boolean attackable = (Boolean) true;
		final int mobType = Integer.valueOf(1);
		final int faction = Integer.valueOf(1);
		final String questCategory = "";
		final LinkedList<Integer> displays = new LinkedList<Integer>();
		for (int i = 0; i < displayCount; ++i) {
			displays.add(0);
		}
		Template tmpl = new Template(name, -1, ObjectManagerPlugin.MOB_TEMPLATE);
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
		putMobCombatStats(tmpl, level, health);
		// tmpl.put(InventoryClient.NAMESPACE, ":inv_items", (Serializable) equipment);
		// tmpl.put(InventoryClient.NAMESPACE, "lootTables", (Serializable) lootTables);
		// ObjectManagerClient.registerTemplate(tmpl);
		ObjectManagerPlugin.getObjectManagerPlugin().registerTemplate(tmpl);
		log.debug("MOB: loaded template: [" + tmpl.getName() + "]");
		// AgisMobPlugin.this.sendMobTemplates(oid);
	}

	protected void putMobCombatStats(final Template tmpl, final int level, final int health) {
		final String attackType = "slash";
		final int exp_val = 100;
		tmpl.put(CombatClient.NAMESPACE, "combat.autoability", (Serializable) 1);
		tmpl.put(CombatClient.NAMESPACE, "combat.mobflag", (Serializable) true);
		tmpl.put(CombatClient.NAMESPACE, "kill_exp", (Serializable) exp_val);
		tmpl.put(CombatClient.NAMESPACE, "weaponType", (Serializable) "Unarmed");
		tmpl.put(CombatClient.NAMESPACE, "attackType", (Serializable) attackType);
		final LinkedList<Integer> effectsList = new LinkedList<Integer>();
		tmpl.put(CombatClient.NAMESPACE, "effects", (Serializable) effectsList);
	}

}
