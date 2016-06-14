// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.msgsys.ResponseMessage;
import atavism.agis.util.EquipHelper;
import atavism.server.math.Point;
import atavism.agis.objects.VitalityStatDef;
import atavism.server.objects.Template;
import atavism.agis.objects.CharacterTemplate;
import java.util.concurrent.TimeUnit;
import atavism.agis.objects.SkillData;
import atavism.agis.objects.TargetInfo;
import atavism.agis.objects.AgisStat;
import atavism.agis.effects.MountEffect;
import java.io.Serializable;
import atavism.server.objects.Marker;
import atavism.server.plugins.InstanceClient;
import atavism.server.engine.BasicWorldNode;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.agis.objects.SkillInfo;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.msgsys.Message;
import atavism.agis.objects.AgisItem;
import atavism.agis.objects.CombatInfo;
import atavism.server.messages.LogoutMessage;
import atavism.server.engine.Hook;
import java.util.Iterator;
import java.util.ArrayList;
import atavism.agis.objects.SkillTemplate;
import atavism.server.util.Log;
import atavism.agis.core.AgisAbility;
import atavism.agis.core.Agis;
import atavism.agis.core.AgisEffect;
import atavism.agis.database.CombatDatabase;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.Namespace;
import atavism.server.messages.LoginMessage;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.messages.PropertyMessage;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.MessageTypeFilter;
import java.util.HashSet;
import atavism.server.util.LockFactory;
import java.util.LinkedList;
import java.util.HashMap;
import atavism.agis.objects.AgisStatDef;
import java.util.Set;
import atavism.server.engine.OID;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class CombatPlugin extends EnginePlugin {
	public static String COMBAT_PLUGIN_NAME;
	protected static final Logger log;
	protected static Lock lock;
	protected static Map<OID, Set<OID>> autoAttackReverseMap;
	protected static Map<OID, OID> lastAttackerMap;
	protected static Map<String, AgisStatDef> statDefMap;
	protected static Set<AgisStatDef> baseStats;
	public static Set<String> publicStats;
	HashMap<OID, CombatStatTick> statTicks;
	public static final int STAT_BASE = 100;
	public static final int HEALTH_BASE = 500;
	public static final int MANA_BASE = 10;
	public static final int FLATRESIST_BASE = 0;
	public static final int PERCENTRESIST_BASE = 0;
	public static String HEALTH_MAX_STAT;
	public static String MANA_MAX_STAT;
	public static String HEALTH_BASE_STAT;
	public static String MANA_BASE_STAT;
	public static String HEALTH_STAT;
	public static String MANA_STAT;
	public static String DAMAGE_DEALT_MODIFIER;
	public static String DAMAGE_TAKEN_MODIFIER;
	public static String ATTACK_SPEED_STAT;
	public static String HEALTH_MOD_STAT;
	public static String MANA_MOD_STAT;
	public static String PHYSICAL_POWER_STAT;
	public static String MAGICAL_POWER_STAT;
	public static String PHYSICAL_ACCURACY_STAT;
	public static String MAGICAL_ACCURACY_STAT;
	public static HashMap<String, String> DAMAGE_TYPES;
	public static LinkedList<String> STAT_LIST;
	public static final String PROP_HITBOX = "hitBox";
	public static final int ATTACK_ABILITY = 1;
	public static final int PLAYER_ATTACK_ABILITY = 1;
	public static final int DEFAULT_MOVEMENT_SPEED = 7;

	static {
		CombatPlugin.COMBAT_PLUGIN_NAME = "Combat";
		log = new Logger("CombatPlugin");
		CombatPlugin.lock = LockFactory.makeLock("CombatPlugin");
		CombatPlugin.autoAttackReverseMap = new HashMap<OID, Set<OID>>();
		CombatPlugin.lastAttackerMap = new HashMap<OID, OID>();
		CombatPlugin.statDefMap = new HashMap<String, AgisStatDef>();
		CombatPlugin.baseStats = new HashSet<AgisStatDef>();
		CombatPlugin.publicStats = new HashSet<String>();
		CombatPlugin.HEALTH_MAX_STAT = "health-max";
		CombatPlugin.MANA_MAX_STAT = "mana-max";
		CombatPlugin.HEALTH_BASE_STAT = "health-base";
		CombatPlugin.MANA_BASE_STAT = "mana-base";
		CombatPlugin.HEALTH_STAT = "health";
		CombatPlugin.MANA_STAT = "mana";
		CombatPlugin.DAMAGE_DEALT_MODIFIER = "dmg-dealt-mod";
		CombatPlugin.DAMAGE_TAKEN_MODIFIER = "dmg-taken-mod";
		CombatPlugin.ATTACK_SPEED_STAT = "attack_speed";
		CombatPlugin.HEALTH_MOD_STAT = null;
		CombatPlugin.MANA_MOD_STAT = null;
		CombatPlugin.PHYSICAL_POWER_STAT = null;
		CombatPlugin.MAGICAL_POWER_STAT = null;
		CombatPlugin.PHYSICAL_ACCURACY_STAT = null;
		CombatPlugin.MAGICAL_ACCURACY_STAT = null;
	}

	public CombatPlugin() {
		super(CombatPlugin.COMBAT_PLUGIN_NAME);
		this.statTicks = new HashMap<OID, CombatStatTick>();
		this.setPluginType("Combat");
	}

	public void onActivate() {
		try {
			CombatPlugin.log.debug("CombatPlugin.onActivate()");
			this.registerHooks();
			final MessageTypeFilter filter = new MessageTypeFilter();
			filter.addType(CombatClient.MSG_TYPE_SET_COMBAT_INFO_STATE);
			filter.addType(CombatClient.MSG_TYPE_AUTO_ATTACK);
			filter.addType(CombatClient.MSG_TYPE_START_ABILITY);
			filter.addType(CombatClient.MSG_TYPE_RELEASE_OBJECT);
			filter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
			filter.addType(PropertyMessage.MSG_TYPE_PROPERTY);
			filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
			filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
			filter.addType(CombatClient.MSG_TYPE_ADD_SKILL);
			filter.addType(AgisInventoryClient.MSG_TYPE_ITEM_ACQUIRE_STATUS_CHANGE);
			filter.addType(AgisInventoryClient.MSG_TYPE_ITEM_EQUIP_STATUS_CHANGE);
			filter.addType(CombatClient.MSG_TYPE_COMBAT_STOP_AUTO_ATTACK);
			filter.addType(CombatClient.MSG_TYPE_TARGET_TYPE);
			filter.addType(ArenaClient.MSG_TYPE_REMOVE_EFFECTS);
			filter.addType(CombatClient.MSG_TYPE_APPLY_EFFECT);
			filter.addType(AgisMobClient.MSG_TYPE_UPDATE_PET_STATS);
			filter.addType(CombatClient.MSG_TYPE_UPDATE_BREATH);
			filter.addType(CombatClient.MSG_TYPE_UPDATE_FATIGUE);
			filter.addType(CombatClient.MSG_TYPE_UPDATE_HEALTH_PROPS);
			filter.addType(CombatClient.MSG_TYPE_REGEN_HEALTH_MANA);
			filter.addType(CombatClient.MSG_TYPE_DISMOUNT);
			Engine.getAgent().createSubscription((IFilter) filter, (MessageCallback) this);
			final MessageTypeFilter filter2 = new MessageTypeFilter();
			filter2.addType(LoginMessage.MSG_TYPE_LOGIN);
			Engine.getAgent().createSubscription((IFilter) filter2, (MessageCallback) this, 8);
			this.registerLoadHook(Namespace.COMBAT, (EnginePlugin.LoadHook) new CombatLoadHook());
			this.registerUnloadHook(Namespace.COMBAT, (EnginePlugin.UnloadHook) new CombatUnloadHook());
			this.registerSaveHook(Namespace.COMBAT, (EnginePlugin.SaveHook) new CombatSaveHook());
			this.registerPluginNamespace(Namespace.COMBAT, (EnginePlugin.GenerateSubObjectHook) new CombatPluginGenerateSubObjectHook());
		} catch (Exception e) {
			throw new AORuntimeException("onActivate failed", (Throwable) e);
		}
		this.loadCombatDataFromDatabase();
	}

	public void loadCombatDataFromDatabase() {
		final CombatDatabase cDB = new CombatDatabase(false);
		if (CombatPlugin.STAT_LIST == null) {
			this.loadStatData(cDB);
		}
		final ArrayList<AgisEffect> effects = cDB.loadCombatEffects();
		for (final AgisEffect effect : effects) {
			Agis.EffectManager.register(effect.getID(), (Object) effect);
		}
		final ArrayList<AgisAbility> abilities = cDB.loadAbilities();
		for (final AgisAbility ability : abilities) {
			Agis.AbilityManager.register(ability.getID(), (Object) ability);
			Log.debug("ABILITY: added " + ability.getName() + " to the database.");
		}
		final HashMap<Integer, SkillTemplate> skillTemplates = cDB.loadSkills();
		for (final SkillTemplate tmpl : skillTemplates.values()) {
			Agis.SkillManager.register(tmpl.getSkillID(), (Object) tmpl);
		}
		cDB.close();
	}

	private void loadStatData(final CombatDatabase cDB) {
		CombatPlugin.STAT_LIST = cDB.LoadStats();
		CombatPlugin.DAMAGE_TYPES = cDB.LoadDamageTypes();
	}

	protected void registerHooks() {
		this.getHookManager().addHook(CombatClient.MSG_TYPE_SET_COMBAT_INFO_STATE, (Hook) new UpdateCombatInfoStateHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_AUTO_ATTACK, (Hook) new AutoAttackHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_START_ABILITY, (Hook) new StartAbilityHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_RELEASE_OBJECT, (Hook) new ReleaseObjectHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT, (Hook) new UpdateObjectHook());
		this.getHookManager().addHook(PropertyMessage.MSG_TYPE_PROPERTY, (Hook) new PropertyHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, (Hook) new DespawnedHook());
		this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, (Hook) new SpawnedHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_TARGET_TYPE, (Hook) new TargetTypeUpdateHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_ADD_SKILL, (Hook) new AddSkillHook());
		this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_ITEM_ACQUIRE_STATUS_CHANGE, (Hook) new ItemAcquireStatusChangeHook());
		this.getHookManager().addHook(AgisInventoryClient.MSG_TYPE_ITEM_EQUIP_STATUS_CHANGE, (Hook) new ItemEquipStatusChangeHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_COMBAT_STOP_AUTO_ATTACK, (Hook) new StopAutoAttackHook());
		this.getHookManager().addHook(ArenaClient.MSG_TYPE_REMOVE_EFFECTS, (Hook) new RemoveArenaEffectsHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_APPLY_EFFECT, (Hook) new ApplyEffectHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_UPDATE_HEALTH_PROPS, (Hook) new SetHealthPropertiesHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_REGEN_HEALTH_MANA, (Hook) new RegenerateHealthHook());
		this.getHookManager().addHook(CombatClient.MSG_TYPE_DISMOUNT, (Hook) new DismountHook());
		this.getHookManager().addHook(LoginMessage.MSG_TYPE_LOGIN, (Hook) new LoginHook());
		this.getHookManager().addHook(LogoutMessage.MSG_TYPE_LOGOUT, (Hook) new LogoutHook());
	}

	/**
	 * 自动攻击
	 * 
	 * @param info
	 */
	public static void resolveAutoAttack(final CombatInfo info) {
		if (Log.loggingDebug) {
			CombatPlugin.log.debug("CombatPlugin.resolveAutoAttack: info=" + info);
		}
		final OID targetOid = info.getAutoAttackTarget();// 获取自动攻击的目标
		final CombatInfo target = getCombatInfo(targetOid);// 获取战斗信息
		if (target == null) {
			return;
		}
		final int abilityID = (int) info.getProperty("combat.autoability");
		if (abilityID < 1) {
			return;
		}
		final int autoattack = abilityID;
		if (!info.isUser()) {
			final int maxHealth = info.statGetCurrentValue("health-max");
			final int curHealth = info.statGetCurrentValue("health");
			final float healthPercent = curHealth / maxHealth;
			final boolean useHeal = false;
		}
		final AgisAbility ability = (AgisAbility) Agis.AbilityManager.get(abilityID);
		if (Log.loggingDebug) {
			CombatPlugin.log.debug("CombatPlugin.resolveAutoAttack: abilityID " + abilityID + ", ability " + ability);
		}
		if (abilityID != autoattack) {
			final String casterName = WorldManagerClient.getObjectInfo(info.getOwnerOid()).name;
			WorldManagerClient.sendObjChatMsg(target.getOwnerOid(), 0, String.valueOf(casterName) + " begins casting " + ability.getName());
		}
		int duelID = -1;
		int duelID2 = -1;
		if (info.isUser() && target.isUser()) {
			try {
				duelID = (int) EnginePlugin.getObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "duelID");
				duelID2 = (int) EnginePlugin.getObjectProperty(info.getOwnerOid(), WorldManagerClient.NAMESPACE, "duelID");
			} catch (NullPointerException ex) {
			}
		}
		if (duelID == duelID2) {
			ability.setDuelID(duelID);
		} else {
			ability.setDuelID(-1);
		}
		Log.debug("AUTO: calling auto attack");
		AgisAbility.startAbility(ability, info, target, null);
		Log.debug("AUTO: finished auto attack");
	}

	public static void sendAbilityUpdate(final CombatInfo info) {
		if (Log.loggingDebug) {
			CombatPlugin.log.debug("CombatPlugin: sending AbilityUpdate for obj=" + info);
		}
		final CombatClient.AbilityUpdateMessage msg = new CombatClient.AbilityUpdateMessage(info.getOwnerOid(),
				info.getOwnerOid());
		for (final int abilityID : info.getCurrentAbilities()) {
			final AgisAbility ability = (AgisAbility) Agis.AbilityManager.get(abilityID);
			if (Log.loggingDebug) {
				CombatPlugin.log.debug("CombatPlug: adding ability to message. ability=" + ability);
			}
			msg.addAbility(ability.getID(), ability.getIcon(), "")
		}
		Engine.getAgent().sendBroadcast((Message) msg);
	}

	public static CombatInfo getCombatInfo(final OID oid) {
		return (CombatInfo) EntityManager.getEntityByNamespace(oid, Namespace.COMBAT);
	}

	public static void registerCombatInfo(final CombatInfo cinfo) {
		EntityManager.registerEntityByNamespace((Entity) cinfo, Namespace.COMBAT);
	}

	protected void createNewSkillInfo(final CombatInfo info, final OID mobOid, final ArrayList<Integer> skills) {
		final SkillInfo skillInfo = new SkillInfo(info.getCurrentCategory());
		final ArrayList<Integer> abilities = new ArrayList<Integer>();
		final ArrayList<String> actions = new ArrayList<String>();
		abilities.add(1);
		for (final int skill : skills) {
			Log.debug("SKILL: adding skill: " + skill);
			final SkillTemplate tmpl = (SkillTemplate) Agis.SkillManager.get(skill);
			Log.debug("SKILL: 1 adding skill: " + tmpl.getSkillName());
			skillInfo.addSkill(tmpl);
			Log.debug("SKILL: 2 adding skill: " + skill);
			final ArrayList<Integer> abilityIDs = tmpl.getStartAbilityIDs();
			Log.debug("SKILL: got " + abilityIDs.size() + " abilities");
			for (final int ability : abilityIDs) {
				abilities.add(ability);
				actions.add("a" + ability);
			}
		}
		info.setCurrentAbilities(abilities);
		info.setCurrentActions(actions);
		info.setCurrentSkillInfo(skillInfo);
		Log.debug("AJ: Sending combat messages");
		ExtendedCombatMessages.sendAbilities(info.getOwnerOid(), info.getCurrentAbilities());
		ExtendedCombatMessages.sendActions(info.getOwnerOid(), info.getCurrentActions());
		ExtendedCombatMessages.sendSkills(info.getOwnerOid(), info.getCurrentSkillInfo());
	}

	private void relocateReleasedPlayer(final OID oid) {
		Log.debug("RELEASE: moving player: " + oid);
		final OID instanceOid = WorldManagerClient.getObjectInfo(oid).instanceOid;
		final BasicWorldNode wnode = new BasicWorldNode();
		final Marker marker = InstanceClient.getMarker(instanceOid, "spawn");
		wnode.setLoc(marker.getPoint());
		WorldManagerClient.updateWorldNode(oid, wnode, true);
	}

	private boolean arenaRelease(final OID oid) {
		final String factionOverride = (String) EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "factionOverride");
		if (factionOverride == null || factionOverride.equals("")) {
			Log.debug("RELEASE: factionOverride was emtpy for player: " + oid);
			return false;
		}
		final int teamStringLoc = factionOverride.lastIndexOf("_");
		final String teamNum = factionOverride.substring(teamStringLoc + 5);
		final String markerName = "death_" + teamNum;
		Log.debug("RELEASE: Player is in arena and on team " + teamNum);
		final OID instanceOid = WorldManagerClient.getObjectInfo(oid).instanceOid;
		final BasicWorldNode wnode = new BasicWorldNode();
		final Marker marker = InstanceClient.getMarker(instanceOid, markerName);
		if (marker == null) {
			Log.debug("RELEASE: arena marker not found: " + markerName);
			return false;
		}
		Log.debug("RELEASE: arena marker found: " + markerName);
		wnode.setLoc(marker.getPoint());
		WorldManagerClient.updateWorldNode(oid, wnode, true);
		return true;
	}

	public static void handleDeath(final CombatInfo obj) {
		obj.setDeadState(true);
		obj.statSetBaseValue(CombatPlugin.HEALTH_STAT, 0);
		final OID oid = obj.getOwnerOid();
		AgisEffect.removeNonPassiveEffects(obj);
		Log.debug("DEATH: effects removed");
		if (!obj.isMob()) {
			ArenaClient.duelDefeat(oid);
			final OID accountId = (OID) EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "accountId");
			DataLoggerClient.logData("PLAYER_DIED", oid, null, accountId, null);
		}
	}

	private static void sendDeathBox(final OID oid) {
		final Map<String, Serializable> props = new HashMap<String, Serializable>();
		props.put("ext_msg_subtype", "deathBox");
		props.put("boxType", "normal");
		int arenaID = -1;
		try {
			arenaID = (int) EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "arenaID");
		} catch (NullPointerException ex) {
		}
		if (arenaID != -1) {
			props.put("boxType", "arena");
		}
		final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false,
				(Map) props);
		Engine.getAgent().sendBroadcast((Message) msg);
	}

	void SpawnLootObject() {
	}

	private void dismountPlayer(final OID oid) {
		final CombatInfo player = getCombatInfo(oid);
		AgisEffect.EffectState mountEffect = null;
		for (final AgisEffect.EffectState state : player.getCurrentEffects()) {
			Log.debug("MOUNT: found effect: " + state.getEffectID());
			if (state.getEffect() != null && state.getEffect() instanceof MountEffect) {
				mountEffect = state;
				Log.debug("MOUNT: found effect to remove: " + mountEffect.getEffectID());
				break;
			}
		}
		if (mountEffect != null) {
			AgisEffect.removeEffect(mountEffect, player);
		}
	}

	public static void addAttacker(final OID target, final OID attacker) {
		CombatPlugin.lock.lock();
		try {
			Set<OID> attackers = CombatPlugin.autoAttackReverseMap.get(target);
			if (attackers == null) {
				attackers = new HashSet<OID>();
				CombatPlugin.autoAttackReverseMap.put(target, attackers);
			}
			attackers.add(attacker);
			CombatPlugin.lastAttackerMap.put(target, attacker);
		} finally {
			CombatPlugin.lock.unlock();
		}
		CombatPlugin.lock.unlock();
		final CombatInfo info = getCombatInfo(target);
		if (info.isMob()) {
			final OID tagOwner = (OID) info.getProperty("tagOwner");
			if (tagOwner == null) {
				info.setProperty("tagOwner", (Serializable) attacker);
			}
		}
	}

	public static void removeAttacker(final OID target, final OID attacker) {
		CombatPlugin.lock.lock();
		try {
			final Set<OID> attackers = CombatPlugin.autoAttackReverseMap.get(target);
			if (attackers != null) {
				attackers.remove(attacker);
				if (attackers.isEmpty()) {
					CombatPlugin.autoAttackReverseMap.remove(target);
				}
			}
		} finally {
			CombatPlugin.lock.unlock();
		}
		CombatPlugin.lock.unlock();
	}

	public static Set<OID> getAttackers(final OID target) {
		CombatPlugin.lock.lock();
		try {
			return CombatPlugin.autoAttackReverseMap.get(target);
		} finally {
			CombatPlugin.lock.unlock();
		}
	}

	public static void clearAttackers(final OID target) {
		CombatPlugin.lock.lock();
		try {
			CombatPlugin.autoAttackReverseMap.remove(target);
		} finally {
			CombatPlugin.lock.unlock();
		}
		CombatPlugin.lock.unlock();
	}

	public static void registerStat(final AgisStatDef stat) {
		registerStat(stat, false, new String[0]);
	}

	public static void registerStat(final AgisStatDef stat, final boolean isPublic) {
		registerStat(stat, isPublic, new String[0]);
	}

	public static void registerStat(final AgisStatDef stat, final boolean isPublic, final String... dependencies) {
		final String statName = stat.getName();
		if (CombatPlugin.statDefMap.containsKey(statName)) {
			throw new AORuntimeException("stat already defined");
		}
		CombatPlugin.statDefMap.put(statName, stat);
		if (dependencies.length == 0) {
			CombatPlugin.baseStats.add(stat);
		}
		for (final String depName : dependencies) {
			final AgisStatDef depStat = CombatPlugin.statDefMap.get(depName);
			if (depStat != null) {
				depStat.addDependent(stat);
			} else {
				Log.error("no stat definition for dependency " + depName + " of stat " + statName);
			}
		}
		if (isPublic) {
			CombatPlugin.publicStats.add(statName);
		}
	}

	public static AgisStatDef lookupStatDef(final String name) {
		return CombatPlugin.statDefMap.get(name);
	}

	public void applySkillTraining(final OID playerOid, final int skill) {
		Log.debug("CombatPlugin.applySkillTraining : skill = " + skill);
		final CombatInfo player = getCombatInfo(playerOid);
	}

	public static AgisStatDef getBaseStatDef(final String name) {
		for (final AgisStatDef statdef : CombatPlugin.baseStats) {
			if (statdef.getName().equals(name)) {
				return statdef;
			}
		}
		return null;
	}

	public HashMap<String, AgisStat> getStartingStats(final int level, final boolean isMob, final int baseHealth, final int healthMod, final int baseMana, final int manaMod,
			final HashMap<String, Integer> statOverrides) {
		final HashMap<String, AgisStat> statMap = new HashMap<String, AgisStat>();
		final int dmg_dealt_mod = 0;
		final int dmg_taken_mod = 0;
		final int dmg_base = 20;
		statMap.put("dmg-dealt-mod", new AgisStat("dmg-dealt-mod", dmg_dealt_mod));
		statMap.put("dmg-taken-mod", new AgisStat("dmg-taken-mod", dmg_taken_mod));
		statMap.put("experience-max", new AgisStat("experience-max", ClassAbilityPlugin.GetStartingXpReq()));
		statMap.put("experience", new AgisStat("experience", 0));
		statMap.put("level", new AgisStat("level", level));
		if (isMob) {
			if (CombatPlugin.STAT_LIST == null) {
				final CombatDatabase cDB = new CombatDatabase(false);
				this.loadStatData(cDB);
				cDB.close();
			}
			for (final String stat : CombatPlugin.STAT_LIST) {
				final AgisStatDef statDef = CombatPlugin.statDefMap.get(stat);
				float value = statDef.getMobStartingValue() + (level - 1) * statDef.getMobLevelIncrease();
				if (statOverrides != null && statOverrides.containsKey(stat)) {
					value = statOverrides.get(stat);
					Log.debug("STAT: using mob stat override for stat: " + stat + " with value: " + value);
				} else {
					value += value * ((level - 1) * (statDef.getMobLevelPercentIncrease() / 100.0f));
				}
				if (CombatPlugin.DAMAGE_TYPES.containsValue(stat)) {
					statMap.put(stat, new AgisStat(stat, (int) value));
				} else {
					statMap.put(stat, new AgisStat(stat, (int) value));
				}
				Log.debug("STAT: gave mob stat: " + stat);
			}
			if (CombatPlugin.statDefMap.containsKey(CombatPlugin.HEALTH_MOD_STAT)) {
				statMap.put(CombatPlugin.HEALTH_BASE_STAT, new AgisStat(CombatPlugin.HEALTH_BASE_STAT, 0));
				final AgisStatDef statDef2 = CombatPlugin.statDefMap.get(CombatPlugin.HEALTH_MOD_STAT);
				float value2 = statDef2.getMobStartingValue() + (level - 1) * statDef2.getMobLevelIncrease();
				value2 += value2 * ((level - 1) * (statDef2.getMobLevelPercentIncrease() / 100.0f));
				statMap.put(CombatPlugin.HEALTH_MAX_STAT, new AgisStat(CombatPlugin.HEALTH_MAX_STAT, (int) (value2 * 10.0f)));
				statMap.put(CombatPlugin.HEALTH_STAT, new AgisStat(CombatPlugin.HEALTH_STAT, (int) (value2 * 10.0f)));
			} else {
				statMap.put(CombatPlugin.HEALTH_BASE_STAT, new AgisStat(CombatPlugin.HEALTH_BASE_STAT, 60 + level * 10));
				statMap.put(CombatPlugin.HEALTH_MAX_STAT, new AgisStat(CombatPlugin.HEALTH_MAX_STAT, 60 + level * 10));
				statMap.put(CombatPlugin.HEALTH_STAT, new AgisStat(CombatPlugin.HEALTH_STAT, 60 + level * 10));
			}
			if (CombatPlugin.statDefMap.containsKey(CombatPlugin.MANA_MOD_STAT)) {
				statMap.put(CombatPlugin.MANA_BASE_STAT, new AgisStat(CombatPlugin.MANA_BASE_STAT, 0));
				final AgisStatDef statDef2 = CombatPlugin.statDefMap.get(CombatPlugin.MANA_MOD_STAT);
				float value2 = statDef2.getMobStartingValue() + (level - 1) * statDef2.getMobLevelIncrease();
				value2 += value2 * ((level - 1) * (statDef2.getMobLevelPercentIncrease() / 100.0f));
				statMap.put(CombatPlugin.MANA_MAX_STAT, new AgisStat(CombatPlugin.MANA_MAX_STAT, (int) (value2 * 10.0f)));
				statMap.put(CombatPlugin.MANA_STAT, new AgisStat(CombatPlugin.MANA_STAT, (int) (value2 * 10.0f)));
			} else {
				statMap.put(CombatPlugin.MANA_BASE_STAT, new AgisStat(CombatPlugin.MANA_BASE_STAT, 15 + level * 5));
				statMap.put(CombatPlugin.MANA_MAX_STAT, new AgisStat(CombatPlugin.MANA_MAX_STAT, 15 + level * 5));
				statMap.put(CombatPlugin.MANA_STAT, new AgisStat(CombatPlugin.MANA_STAT, 15 + level * 5));
			}
		} else {
			if (CombatPlugin.statDefMap.containsKey(CombatPlugin.HEALTH_MOD_STAT)) {
				statMap.put(CombatPlugin.HEALTH_BASE_STAT, new AgisStat(CombatPlugin.HEALTH_BASE_STAT, baseHealth));
				final int maxHealth = calculateStartingHealth(baseHealth, healthMod);
				statMap.put(CombatPlugin.HEALTH_MAX_STAT, new AgisStat(CombatPlugin.HEALTH_MAX_STAT, maxHealth));
				statMap.put(CombatPlugin.HEALTH_STAT, new AgisStat(CombatPlugin.HEALTH_STAT, maxHealth));
			} else {
				statMap.put(CombatPlugin.HEALTH_BASE_STAT, new AgisStat(CombatPlugin.HEALTH_BASE_STAT, baseHealth));
				statMap.put(CombatPlugin.HEALTH_MAX_STAT, new AgisStat(CombatPlugin.HEALTH_MAX_STAT, baseHealth));
				statMap.put(CombatPlugin.HEALTH_STAT, new AgisStat(CombatPlugin.HEALTH_STAT, baseHealth));
			}
			if (CombatPlugin.statDefMap.containsKey(CombatPlugin.MANA_MOD_STAT)) {
				statMap.put(CombatPlugin.MANA_BASE_STAT, new AgisStat(CombatPlugin.MANA_BASE_STAT, baseMana));
				final int maxMana = calculateStartingMana(baseMana, manaMod);
				statMap.put(CombatPlugin.MANA_MAX_STAT, new AgisStat(CombatPlugin.MANA_MAX_STAT, maxMana));
				statMap.put(CombatPlugin.MANA_STAT, new AgisStat(CombatPlugin.MANA_STAT, maxMana));
				Log.debug("CHAR: using manaMod to set maxMana to: " + maxMana);
			} else {
				statMap.put(CombatPlugin.MANA_BASE_STAT, new AgisStat(CombatPlugin.MANA_BASE_STAT, baseMana));
				statMap.put(CombatPlugin.MANA_MAX_STAT, new AgisStat(CombatPlugin.MANA_MAX_STAT, baseMana));
				statMap.put(CombatPlugin.MANA_STAT, new AgisStat(CombatPlugin.MANA_STAT, baseMana));
				Log.debug("CHAR: using basemana to set maxMana to: " + baseMana);
			}
			statMap.put("dmg-base", new AgisStat("dmg-base", dmg_base));
		}
		return statMap;
	}

	public static int calculateStartingHealth(final int baseHealth, final int health_mod) {
		return baseHealth + health_mod * 10;
	}

	public static int calculateStartingMana(final int baseMana, final int mana_mod) {
		return baseMana + mana_mod * 10;
	}

	class CombatLoadHook implements EnginePlugin.LoadHook {
		public void onLoad(final Entity e) {
			final CombatInfo info = (CombatInfo) e;
			info.setAttackableTargets(new HashMap<OID, TargetInfo>());
			info.setFriendlyTargets(new HashMap<OID, TargetInfo>());
			AgisEffect.removeNonContinuousEffects(info, true);
			if (info.dead()) {
				sendDeathBox(info.getOwnerOid());
			}
			final String race = (String) EnginePlugin.getObjectProperty(info.getOwnerOid(), WorldManagerClient.NAMESPACE, "race");
			final String aspect = info.aspect();
			final CharacterTemplate tmpl = ClassAbilityPlugin.getCharacterTemplate(String.valueOf(race) + aspect);
			ClassAbilityPlugin.calculatePlayerStats(info, tmpl);
			for (final SkillData sData : info.getCurrentSkillInfo().getSkills().values()) {
				SkillInfo.applyStatModifications(info, (SkillTemplate) Agis.SkillManager.get(sData.getSkillID()), sData.getSkillLevel());
			}
			Log.debug("COMBAT: Sending combat messages");
			ExtendedCombatMessages.sendAbilities(info.getOwnerOid(), info.getCurrentAbilities());
			ExtendedCombatMessages.sendActions(info.getOwnerOid(), info.getCurrentActions());
			ExtendedCombatMessages.sendSkills(info.getOwnerOid(), info.getCurrentSkillInfo());
			final CombatStatTick statTick = new CombatStatTick(info);
			Engine.getExecutor().schedule(statTick, 10L, TimeUnit.SECONDS);
			CombatPlugin.this.statTicks.put(info.getOwnerOid(), statTick);
		}
	}

	class CombatUnloadHook implements EnginePlugin.UnloadHook {
		public void onUnload(final Entity e) {
			final CombatInfo info = (CombatInfo) e;
			Log.debug("COMBAT: Unloading CombatInfo for Entity: " + e.getOid());
			CombatPlugin.this.statTicks.get(info.getOwnerOid()).disable();
			AgisEffect.removeNonContinuousEffects(info, false);
		}
	}

	class CombatSaveHook implements EnginePlugin.SaveHook {
		public void onSave(final Entity e, final Namespace namespace) {
		}
	}

	class CombatPluginGenerateSubObjectHook extends EnginePlugin.GenerateSubObjectHook {
		public CombatPluginGenerateSubObjectHook() {
			super((EnginePlugin) CombatPlugin.this);
		}

		public EnginePlugin.SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
			if (Log.loggingDebug) {
				CombatPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
			}
			final Map<String, Serializable> props = (Map<String, Serializable>) template.getSubMap(Namespace.COMBAT);
			if (props == null) {
				Log.warn("GenerateSubObjectHook: no props in ns " + Namespace.COMBAT);
				return null;
			}
			final CombatInfo cinfo = new CombatInfo(masterOid, template.getTemplateID());
			cinfo.setName(template.getName());
			cinfo.setCurrentCategory(1);
			Boolean persistent = (Boolean) template.get(Namespace.OBJECT_MANAGER, ":persistent");
			if (persistent == null) {
				persistent = false;
			}
			cinfo.setPersistenceFlag((boolean) persistent);
			for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
				final String key = entry.getKey();
				final Serializable value = entry.getValue();
				if (!key.startsWith(":")) {
					cinfo.setProperty(key, value);
					Log.warn("STAT: added property: " + key);
				}
			}
			int level = 1;
			for (final Map.Entry<String, Serializable> entry2 : props.entrySet()) {
				final String key2 = entry2.getKey();
				final Serializable value2 = entry2.getValue();
				if (!key2.startsWith(":")) {
					cinfo.setProperty(key2, value2);
					if (!key2.equals("level")) {
						continue;
					}
					final AgisStat stat = (AgisStat) value2;
					level = stat.getBaseValue();
				}
			}
			int baseHealth = 0;
			if (props.containsKey(":health")) {
				baseHealth = props.get(":health");
				Log.debug("CHAR: set base health to: " + baseHealth);
			}
			int healthMod = 0;
			if (props.containsKey(CombatPlugin.HEALTH_MOD_STAT)) {
				final AgisStat stat2 = (AgisStat) cinfo.getProperty(CombatPlugin.HEALTH_MOD_STAT);
				healthMod = stat2.getCurrentValue();
				Log.debug("CHAR: set health mod to: " + healthMod);
			}
			int baseMana = 0;
			if (props.containsKey(":mana")) {
				baseMana = props.get(":mana");
				Log.debug("CHAR: set base mana to: " + baseMana);
			}
			int manaMod = 0;
			if (props.containsKey(CombatPlugin.MANA_MOD_STAT)) {
				final AgisStat stat = (AgisStat) cinfo.getProperty(CombatPlugin.MANA_MOD_STAT);
				manaMod = stat.getCurrentValue();
				Log.debug("CHAR: set mana mod to: " + manaMod);
			}
			HashMap<String, Integer> statOverrides = null;
			if (props.containsKey(":statOverrides")) {
				statOverrides = props.get(":statOverrides");
			}
			final HashMap<String, AgisStat> statMap = CombatPlugin.this.getStartingStats(level, cinfo.isMob(), baseHealth, healthMod, baseMana, manaMod, statOverrides);
			for (final AgisStat stat3 : statMap.values()) {
				cinfo.setProperty(stat3.getName(), (Serializable) stat3);
			}
			for (final Map.Entry<String, AgisStatDef> statEntry : CombatPlugin.statDefMap.entrySet()) {
				final String statName = statEntry.getKey();
				AgisStat stat4 = (AgisStat) cinfo.getProperty(statName);
				if (stat4 == null) {
					Log.debug("STAT: stat is null - " + statName);
					stat4 = new AgisStat(statName);
					cinfo.setProperty(statName, (Serializable) stat4);
				}
				if (CombatPlugin.lookupStatDef(stat4.getName()) instanceof VitalityStatDef) {
					final VitalityStatDef statDef = (VitalityStatDef) CombatPlugin.lookupStatDef(stat4.getName());
					Log.debug("STAT: adding vitality stat with shift inteval: " + statDef.getShiftInterval());
					if (!statDef.checkShiftTarget(cinfo) || statDef.getShiftInterval() <= 0) {
						continue;
					}
					cinfo.addVitalityStat(stat4, statDef.getShiftInterval());
				}
			}
			for (final AgisStatDef statDef2 : CombatPlugin.baseStats) {
				final String statName = statDef2.getName();
				final AgisStat stat4 = (AgisStat) cinfo.getProperty(statName);
				Log.warn("STAT: updating stat: " + stat4);
				statDef2.update(stat4, cinfo);
			}
			if (Log.loggingDebug) {
				CombatPlugin.log.debug("GenerateSubObjectHook: created entity " + cinfo);
			}
			CombatPlugin.registerCombatInfo(cinfo);
			if (cinfo.isUser()) {
				final ArrayList<Integer> skills = props.get(":startingSkills");
				CombatPlugin.this.createNewSkillInfo(cinfo, masterOid, skills);
			}
			if (persistent) {
				Engine.getPersistenceManager().persistEntity((Entity) cinfo);
			}
			final CombatStatTick statTick = new CombatStatTick(cinfo);
			Engine.getExecutor().schedule(statTick, 10L, TimeUnit.SECONDS);
			CombatPlugin.this.statTicks.put(masterOid, statTick);
			return new EnginePlugin.SubObjData();
		}
	}

	class AutoAttackHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.AutoAttackMessage autoAtkMsg = (CombatClient.AutoAttackMessage) msg;
			final OID oid = autoAtkMsg.getSubject();
			final CombatInfo obj = CombatPlugin.getCombatInfo(oid);
			if (obj == null) {
				return true;
			}
			final OID targetOid = autoAtkMsg.getTargetOid();
			final CombatInfo target = CombatPlugin.getCombatInfo(targetOid);
			final Boolean status = autoAtkMsg.getAttackStatus();
			final Lock objLock = obj.getLock();
			Lock targetLock = null;
			if (target != null) {
				targetLock = target.getLock();
			}
			try {
				objLock.lock();
				while (targetLock != null && !targetLock.tryLock()) {
					objLock.unlock();
					Thread.yield();
					objLock.lock();
				}
				if (Log.loggingDebug) {
					CombatPlugin.log.debug("AutoAttackHook.processMessage: oid=" + oid + ", targetOid=" + targetOid + ", status=" + status);
				}
				if (!status || obj.dead() || target == null || target.dead()) {
					obj.stopAutoAttack();
					if (target != null) {
						final OID tagOwner = (OID) target.getProperty("tagOwner");
						if (tagOwner != null && tagOwner.equals((Object) oid)) {
							OID newOwner = null;
							for (final OID attacker : CombatPlugin.getAttackers(targetOid)) {
								if (!attacker.equals((Object) oid)) {
									newOwner = attacker;
								}
							}
							target.setProperty("tagOwner", (Serializable) newOwner);
						}
					}
					final Long petOwner = (Long) obj.getProperty("petOwner");
					if (petOwner != null) {
						AgisMobClient.petTargetLost(obj.getOid());
					}
				} else {
					obj.setAutoAttack(targetOid);
				}
				return true;
			} finally {
				if (targetLock != null) {
					targetLock.unlock();
				}
				objLock.unlock();
			}
		}
	}

	class StopAutoAttackHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.stopAutoAttackMessage EBMsg = (CombatClient.stopAutoAttackMessage) msg;
			final OID oid = EBMsg.getSubject();
			Log.debug("COMBATPLUGIN: stop autoAttack caught: " + EBMsg);
			final CombatInfo info = CombatPlugin.getCombatInfo(oid);
			info.stopAutoAttack();
			return true;
		}
	}

	class StartAbilityHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.StartAbilityMessage abilityMsg = (CombatClient.StartAbilityMessage) msg;
			final OID oid = abilityMsg.getSubject();
			final OID targetOid = abilityMsg.getTargetOid();
			final int abilityID = abilityMsg.getAbilityID();
			final Point loc = abilityMsg.getLocation();
			final AgisItem item = (AgisItem) abilityMsg.getItem();
			CombatPlugin.log.debug("StartAbilityHook.processMessage: oid=" + oid + ", targetOid=" + targetOid + " ability=" + abilityID + ", loc=" + loc);
			final CombatInfo obj = CombatPlugin.getCombatInfo(oid);
			final CombatInfo target = CombatPlugin.getCombatInfo(targetOid);
			final AgisAbility ability = (AgisAbility) Agis.AbilityManager.get(abilityID);
			int duelID = -1;
			int duelID2 = -1;
			if (obj.isUser() && target.isUser()) {
				try {
					duelID = (int) EnginePlugin.getObjectProperty(targetOid, WorldManagerClient.NAMESPACE, "duelID");
					duelID2 = (int) EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "duelID");
				} catch (NullPointerException ex) {
				}
			}
			if (duelID == duelID2) {
				ability.setDuelID(duelID);
			} else {
				ability.setDuelID(-1);
			}
			AgisAbility.startAbility(ability, obj, target, item, loc);
			return true;
		}
	}

	class ReleaseObjectHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.ReleaseObjectMessage releaseMsg = (CombatClient.ReleaseObjectMessage) msg;
			final OID oid = releaseMsg.getSubject();
			if (Log.loggingDebug) {
				CombatPlugin.log.debug("ReleaseObjectHook.processMessage: oid=" + oid);
			}
			final CombatInfo info = CombatPlugin.getCombatInfo(oid);
			if (info == null) {
				return true;
			}
			if (!info.dead()) {
				return true;
			}
			info.setCombatState(false);
			info.setDeadState(false);
			Log.debug("RELEASE: 4");
			for (final Map.Entry<String, AgisStatDef> statEntry : CombatPlugin.statDefMap.entrySet()) {
				final String statName = statEntry.getKey();
				final AgisStat stat = (AgisStat) info.getProperty(statName);
				if (CombatPlugin.lookupStatDef(stat.getName()) instanceof VitalityStatDef) {
					final VitalityStatDef statDef = (VitalityStatDef) CombatPlugin.lookupStatDef(stat.getName());
					final int maxval = info.statGetMaxValue(statName);
					int currentval = info.statGetCurrentValue(statName);
					currentval = maxval / 2;
					Log.debug("RELEASE: setting vitality stat: " + statName + " to current: " + currentval);
					info.statSetBaseValue(statName, currentval);
				}
			}
			Log.debug("RELEASE: 5");
			EnginePlugin.setObjectPropertiesNoResponse(info.getOwnerOid(), Namespace.WORLD_MANAGER,
					new Serializable[] { "world.nomove", new Boolean(false), "world.noturn", new Boolean(false) });
			Log.debug("RELEASE: 7");
			CombatPlugin.this.relocateReleasedPlayer(oid);
			return true;
		}
	}

	class DismountHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage) msg;
			final OID playerOid = eMsg.getSubject();
			Log.debug("MOUNT: got dismount message");
			CombatPlugin.this.dismountPlayer(playerOid);
			return true;
		}
	}

	class UpdateCombatInfoStateHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.SetCombatInfoStateMessage stateMsg = (CombatClient.SetCombatInfoStateMessage) msg;
			final OID subjectOid = stateMsg.getSubject();
			final CombatInfo info = CombatPlugin.getCombatInfo(subjectOid);
			if (info == null) {
				return false;
			}
			final String state = stateMsg.getState();
			if (!stateMsg.getClearState()) {
				Log.debug("STATE: setting combat info state to: " + state);
				info.setState(state);
			} else {
				Log.debug("STATE: clearing combat info state: " + state);
				info.clearState(state);
			}
			return true;
		}
	}

	class UpdateObjectHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.UpdateMessage updateReq = (WorldManagerClient.UpdateMessage) msg;
			final OID subjectOid = updateReq.getSubject();
			final OID targetOid = updateReq.getTarget();
			final CombatInfo info = CombatPlugin.getCombatInfo(subjectOid);
			if (info == null) {
				return false;
			}
			if (Log.loggingDebug) {
				CombatPlugin.log.debug("UpdateObjectHook.processMessage: sending properties for subjectOid=" + subjectOid);
			}
			final WorldManagerClient.TargetedPropertyMessage propMessage = new WorldManagerClient.TargetedPropertyMessage(targetOid, subjectOid);
			for (final Map.Entry<String, Serializable> kvp : info.getPropertyMap().entrySet()) {
				if (!(kvp.getValue() instanceof AgisStat)) {
					propMessage.setProperty((String) kvp.getKey(), (Serializable) kvp.getValue(), true);
				}
			}
			Engine.getAgent().sendBroadcast((Message) propMessage);
			info.statSendUpdate(true, targetOid);
			return true;
		}
	}

	class PropertyHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final PropertyMessage propMsg = (PropertyMessage) msg;
			final OID objOid = propMsg.getSubject();
			final Boolean dead = (Boolean) propMsg.getProperty("deadstate");
			if (dead != null && dead) {
				final CombatInfo obj = CombatPlugin.getCombatInfo(objOid);
				obj.stopAutoAttack();
				final Set<OID> attackers = CombatPlugin.getAttackers(objOid);
				if (attackers != null) {
					CombatPlugin.clearAttackers(objOid);
					if (!obj.isMob()) {
						return true;
					}
					final String name = obj.getName();
					final Integer mobID = (Integer) EnginePlugin.getObjectProperty(obj.getOwnerOid(), WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_ID);
					Log.debug("DEATH: mob " + mobID);
					final LinkedList<String> questCategories = (LinkedList<String>) EnginePlugin.getObjectProperty(obj.getOwnerOid(), WorldManagerClient.NAMESPACE,
							"questCategories");
					if (questCategories == null) {
						Log.debug("QuestCategories is null");
					} else {
						Log.debug("QuestCategories = " + questCategories);
					}
					for (final OID attacker : attackers) {
						final CombatInfo info = CombatPlugin.getCombatInfo(attacker);
						if (info != null) {
							info.stopAutoAttack();
						}
						final CombatClient.QuestMobDeath QMDmsg = new CombatClient.QuestMobDeath(attacker, mobID, name, questCategories);
						Engine.getAgent().sendBroadcast((Message) QMDmsg);
					}
				}
			}
			return true;
		}
	}

	class DespawnedHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.DespawnedMessage despawnedMsg = (WorldManagerClient.DespawnedMessage) msg;
			final OID objOid = despawnedMsg.getSubject();
			final CombatInfo obj = CombatPlugin.getCombatInfo(objOid);
			if (obj == null) {
				return false;
			}
			if (Log.loggingDebug) {
				CombatPlugin.log.debug("DespawnedHook: got a despawned message for oid=" + objOid);
			}
			if (obj != null) {
				obj.setCombatState(false);
			}
			return true;
		}
	}

	class SpawnedHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.SpawnedMessage spawnedMsg = (WorldManagerClient.SpawnedMessage) msg;
			final OID objOid = spawnedMsg.getSubject();
			final CombatInfo info = CombatPlugin.getCombatInfo(objOid);
			if (info != null && info.isUser()) {
				Log.debug("AJ: Sending combat messages");
				ExtendedCombatMessages.sendAbilities(info.getOwnerOid(), info.getCurrentAbilities());
				ExtendedCombatMessages.sendActions(info.getOwnerOid(), info.getCurrentActions());
				ExtendedCombatMessages.sendSkills(info.getOwnerOid(), info.getCurrentSkillInfo());
			}
			return true;
		}
	}

	class SetHealthPropertiesHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage spawnedMsg = (WorldManagerClient.ExtensionMessage) msg;
			final OID objOid = spawnedMsg.getSubject();
			final CombatInfo info = CombatPlugin.getCombatInfo(objOid);
			if (info.isUser()) {
				final int level = (int) info.getProperty("level");
				final int health = 20 + (level - 1) * 2;
				info.statSetBaseValue("health", health);
				info.statSetBaseValue("health-max", health);
			}
			return true;
		}
	}

	class RegenerateHealthHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage healthMsg = (WorldManagerClient.ExtensionMessage) msg;
			final OID objOid = healthMsg.getSubject();
			final CombatInfo info = CombatPlugin.getCombatInfo(objOid);
			int health = info.statGetCurrentValue("health");
			final int amount = (int) healthMsg.getProperty("amount");
			final int healthMax = info.statGetCurrentValue("health-max");
			health += amount;
			if (health > healthMax) {
				health = healthMax;
			}
			info.statSetBaseValue("health", health);
			return true;
		}
	}

	class TargetTypeUpdateHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.TargetTypeMessage ttMsg = (CombatClient.TargetTypeMessage) msg;
			final OID objOid = ttMsg.getSubject();
			final int targetType = Integer.valueOf(ttMsg.getTargetType());
			final String species = ttMsg.getSpecies();
			final OID target = ttMsg.getTargetOid();
			final CombatInfo info = CombatPlugin.getCombatInfo(objOid);
			if (info == null) {
				return true;
			}
			Log.debug("TARGET: got target type update. Setting target: " + target + " to type: " + targetType + " for mob: " + objOid);
			info.removeAttackableTarget(target);
			info.removeFriendlyTarget(target);
			final TargetInfo tInfo = new TargetInfo();
			tInfo.setOid(target);
			tInfo.setSpecies(species);
			if (targetType == -1) {
				info.addAttackableTarget(target, tInfo);
			} else if (targetType == 1) {
				info.addFriendlyTarget(target, tInfo);
			}
			return true;
		}
	}

	class AddSkillHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage reqMsg = (WorldManagerClient.ExtensionMessage) msg;
			CombatPlugin.this.applySkillTraining((OID) reqMsg.getProperty("playerOid"), (int) reqMsg.getProperty("skill"));
			return true;
		}
	}

	class AddAbilityHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage reqMsg = (WorldManagerClient.ExtensionMessage) msg;
			final CombatInfo player = CombatPlugin.getCombatInfo(reqMsg.getSubject());
			return true;
		}
	}

	class GetAbilityHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final WorldManagerClient.ExtensionMessage getMsg = (WorldManagerClient.ExtensionMessage) msg;
			final AgisAbility ability = (AgisAbility) Agis.AbilityManager.get((Integer) getMsg.getProperty("abilityID"));
			final HashMap<String, String> abilityInfo = new HashMap<String, String>();
			abilityInfo.put("name", ability.getName());
			abilityInfo.put("id", new StringBuilder().append(ability.getID()).toString());
			abilityInfo.put("icon", ability.getIcon());
			Engine.getAgent().sendObjectResponse(msg, (Object) abilityInfo);
			return true;
		}
	}

	class RemoveArenaEffectsHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final ArenaClient.removeEffectsMessage EBMsg = (ArenaClient.removeEffectsMessage) msg;
			final OID oid = EBMsg.getSubject();
			final String type = (String) EBMsg.getProperty("type");
			final int ID = (int) EBMsg.getProperty("ID");
			final CombatInfo info = CombatPlugin.getCombatInfo(oid);
			return true;
		}
	}

	class ItemAcquireStatusChangeHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final AgisInventoryClient.ItemAcquiredStatusMessage EBMsg = (AgisInventoryClient.ItemAcquiredStatusMessage) msg;
			final OID oid = EBMsg.getSubject();
			Log.debug("ITEM ACQUIRE CHANGE: item " + EBMsg.getItem() + " was acquired? " + EBMsg.getAcquired());
			final CombatInfo info = CombatPlugin.getCombatInfo(oid);
			EquipHelper.ItemAcquiredStatHelper(oid, EBMsg.getItem(), EBMsg.getAcquired(), info);
			return true;
		}
	}

	class ItemEquipStatusChangeHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final AgisInventoryClient.ItemEquipStatusMessage EBMsg = (AgisInventoryClient.ItemEquipStatusMessage) msg;
			final OID oid = EBMsg.getSubject();
			Log.debug("ITEM EQUIP CHANGE: " + EBMsg);
			final CombatInfo info = CombatPlugin.getCombatInfo(oid);
			EquipHelper.UpdateEquiperStats(oid, EBMsg.getItem(), EBMsg.getEquipped(), info);
			return true;
		}
	}

	class ApplyEffectHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final CombatClient.applyEffectMessage upsMsg = (CombatClient.applyEffectMessage) msg;
			final OID oid = upsMsg.getSubject();
			final int effectID = (int) upsMsg.getProperty("effectID");
			Log.debug("COMBATPLUGIN: about to apply effect: " + effectID + " to object: " + oid);
			final CombatInfo info = CombatPlugin.getCombatInfo(oid);
			final AgisEffect effect = (AgisEffect) Agis.EffectManager.get(effectID);
			final Map<String, Integer> params = new HashMap<String, Integer>();
			params.put("skillType", -1);
			params.put("hitRoll", 50);
			AgisEffect.applyEffect(effect, info, info, -1, params);
			return true;
		}
	}

	class LoginHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final LoginMessage message = (LoginMessage) msg;
			final OID playerOid = message.getSubject();
			final OID instanceOid = message.getInstanceOid();
			Log.debug("LoginHook: playerOid=" + playerOid + " instanceOid=" + instanceOid);
			final CombatInfo info = CombatPlugin.getCombatInfo(playerOid);
			EnginePlugin.setObjectProperty(playerOid, CombatClient.NAMESPACE, "combatstate", (Serializable) false);
			EnginePlugin.setObjectPropertyNoResponse(playerOid, WorldManagerClient.NAMESPACE, "weaponsSheathed", (Serializable) true);
			Engine.getAgent().sendResponse(new ResponseMessage((Message) message));
			return true;
		}
	}

	class LogoutHook implements Hook {
		public boolean processMessage(final Message msg, final int flags) {
			final LogoutMessage message = (LogoutMessage) msg;
			final OID playerOid = message.getSubject();
			Log.debug("LOGOUT: combat logout started for: " + playerOid);
			Engine.getAgent().sendResponse(new ResponseMessage((Message) message));
			Log.debug("LOGOUT: combat logout finished for: " + playerOid);
			return true;
		}
	}

	class CombatStatTick implements Runnable {
		CombatInfo info;
		OID oid;
		boolean active;

		public CombatStatTick(final CombatInfo info) {
			this.active = true;
			this.info = info;
			this.oid = info.getOwnerOid();
		}

		@Override
		public void run() {
			if (this.info != null && this.active) {
				this.info.runCombatTick();
				Engine.getExecutor().schedule(this, 1L, TimeUnit.SECONDS);
			} else {
				CombatPlugin.this.statTicks.remove(this.oid);
			}
		}

		public void disable() {
			this.active = false;
			Log.debug("STAT: disabling vitality stat ticks");
		}
	}
}
