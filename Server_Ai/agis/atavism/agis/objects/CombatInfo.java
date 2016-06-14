// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import java.util.LinkedList;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.Message;
import atavism.server.messages.PropertyMessage;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import atavism.server.objects.EntityManager;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.agis.plugins.AgisMobClient;
import atavism.agis.plugins.CombatPlugin;
import java.io.Serializable;
import atavism.server.objects.ObjectTypes;
import atavism.server.objects.ObjectType;
import atavism.server.engine.Namespace;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import atavism.server.util.Log;
import java.beans.Introspector;
import atavism.server.engine.InterpolatedWorldNode;
import atavism.agis.core.AgisEffect;
import java.util.HashSet;
import java.util.Set;
import atavism.agis.core.AgisAbilityState;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.agis.core.Cooldown;
import atavism.server.objects.Entity;

public class CombatInfo extends Entity implements Runnable, Cooldown.CooldownObject {
	protected OID target;
	boolean scheduled;
	private int id;
	protected int currentCategory;
	protected HashMap<Integer, SkillInfo> skills;
	protected HashMap<Integer, ArrayList<Integer>> abilities;
	protected HashMap<Integer, ArrayList<String>> actions;
	protected Map<String, Cooldown.State> cooldownMap;
	protected transient AgisAbilityState currentAction;
	protected transient Set<AgisAbilityState> activeAbilities;
	protected HashMap<Integer, HashSet<AgisEffect.EffectState>> effects;
	protected HashMap<OID, TargetInfo> friendlyTargets;
	protected HashMap<OID, TargetInfo> attackableTargets;
	protected ArrayList<OID> targetsInCombat;
	InterpolatedWorldNode node;
	HashMap<String, Long> vitalityStats;
	protected transient OID groupOid;
	protected transient OID groupMemberOid;
	protected transient boolean pendingGroupInvite;
	public static final String COMBAT_PROP_BACKREF_KEY = "combat.backref";
	public static final String COMBAT_PROP_USERFLAG = "combat.userflag";
	public static final String COMBAT_PROP_MOBFLAG = "combat.mobflag";
	public static final String COMBAT_PROP_AUTOATTACK_ABILITY = "combat.autoability";
	public static final String COMBAT_PROP_AUTOATTACK_BASE = "combat.autoabilitybase";
	public static final String COMBAT_PROP_REGEN_EFFECT = "combat.regeneffect";
	public static final String COMBAT_PROP_ENERGY = "energy";
	public static final String COMBAT_PROP_HEALTH = "health";
	public static final String COMBAT_PROP_COMBATSTATE = "combatstate";
	public static final String COMBAT_PROP_DEADSTATE = "deadstate";
	public static final String COMBAT_PROP_ATTACKABLE = "attackable";
	public static final String COMBAT_PROP_STATE = "state";
	public static final String COMBAT_PROP_ASPECT = "aspect";
	public static final String COMBAT_PROP_TEAM = "team";
	public static final String COMBAT_TAG_OWNER = "tagOwner";
	public static final String COMBAT_STATE_INCAPACITATED = "incapacitated";
	public static final String COMBAT_STATE_EVADE = "evade";
	public static final String COMBAT_STATE_IMMUNE = "immune";
	public static final int NUM_ACTIONS = 10;
	private static final long serialVersionUID = 1L;

	static {
		try {
			final BeanInfo info = Introspector.getBeanInfo(CombatInfo.class);
			final PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; ++i) {
				final PropertyDescriptor pd = propertyDescriptors[i];
				if (pd.getName().equals("currentAction")) {
					pd.setValue("transient", Boolean.TRUE);
				}
			}
		} catch (Exception e) {
			Log.error("failed beans initalization");
		}
	}

	public CombatInfo() {
		this.target = null;
		this.scheduled = false;
		this.skills = new HashMap<Integer, SkillInfo>();
		this.abilities = new HashMap<Integer, ArrayList<Integer>>();
		this.actions = new HashMap<Integer, ArrayList<String>>();
		this.cooldownMap = new HashMap<String, Cooldown.State>();
		this.effects = new HashMap<Integer, HashSet<AgisEffect.EffectState>>();
		this.friendlyTargets = new HashMap<OID, TargetInfo>();
		this.attackableTargets = new HashMap<OID, TargetInfo>();
		this.targetsInCombat = new ArrayList<OID>();
		this.vitalityStats = new HashMap<String, Long>();
		this.groupOid = null;
		this.groupMemberOid = null;
		this.pendingGroupInvite = false;
		this.setNamespace(Namespace.COMBAT);
	}

	public CombatInfo(final OID objOid, final int id) {
		super(objOid);
		this.target = null;
		this.scheduled = false;
		this.skills = new HashMap<Integer, SkillInfo>();
		this.abilities = new HashMap<Integer, ArrayList<Integer>>();
		this.actions = new HashMap<Integer, ArrayList<String>>();
		this.cooldownMap = new HashMap<String, Cooldown.State>();
		this.effects = new HashMap<Integer, HashSet<AgisEffect.EffectState>>();
		this.friendlyTargets = new HashMap<OID, TargetInfo>();
		this.attackableTargets = new HashMap<OID, TargetInfo>();
		this.targetsInCombat = new ArrayList<OID>();
		this.vitalityStats = new HashMap<String, Long>();
		this.groupOid = null;
		this.groupMemberOid = null;
		this.pendingGroupInvite = false;
		this.setNamespace(Namespace.COMBAT);
		this.setState("");
		this.id = id;
	}

	public String toString() {
		return "[Entity: " + this.getName() + ":" + this.getOid() + "]";
	}

	public ObjectType getType() {
		return ObjectTypes.combatInfo;
	}

	public void overrideAutoAttackAbility(final int abilityID) {
		this.setProperty("combat.autoability", (Serializable) abilityID);
	}

	public void resetAutoAttackAbility() {
		this.setProperty("combat.autoability", this.getProperty("combat.autoabilitybase"));
	}

	public int getAutoAttackAbility() {
		return (int) this.getProperty("combat.autoability");
	}

	public void setAutoAttack(final OID newTarget) {
		this.lock.lock();
		try {
			Log.debug("AUTO: getting old target");
			final OID oldTarget = this.target;
			if (oldTarget != null && oldTarget.equals((Object) newTarget)) {
				Log.debug("AUTO: old target = new target");
				return;
			}
			this.target = newTarget;
			Log.debug("AUTO: got new target: " + this.target);
			if (oldTarget == null) {
				this.setCombatState(true);
				if (!this.scheduled) {
					Log.debug("AUTO: scheduling auto attack for new target");
					this.schedule(500L);
				}
			} else {
				CombatPlugin.removeAttacker(oldTarget, this.getOwnerOid());
			}
			if (this.target == null) {
				this.setCombatState(false);
				Log.debug("AUTO: new target is null");
			} else {
				CombatPlugin.addAttacker(this.target, this.getOwnerOid());
			}
			Log.debug("AUTO: finished set auto attack");
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void stopAutoAttack() {
		this.lock.lock();
		try {
			if (this.target != null) {
				CombatPlugin.removeAttacker(this.target, this.getOwnerOid());
			}
			this.setCombatState(false);
			this.target = null;
			final Long petOwner = (Long) this.getProperty("petOwner");
			if (petOwner != null) {
				AgisMobClient.petTargetLost(this.getOid());
			}
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public OID getAutoAttackTarget() {
		this.lock.lock();
		try {
			return this.target;
		} finally {
			this.lock.unlock();
		}
	}

	public long getAttackDelay() {
		Log.debug("AUTO: Getting attack delay");
		return this.statGetCurrentValue("attack_speed");
	}

	protected void schedule(final long delay) {
		if (Log.loggingDebug) {
			Log.debug("CombatInfo.schedule: scheduling obj=" + this + " for delay=" + delay);
		}
		Engine.getExecutor().schedule(this, delay, TimeUnit.MILLISECONDS);
		this.scheduled = true;
	}

	protected void cancel() {
		Engine.getExecutor().remove(this);
		this.scheduled = false;
	}

	public void run() {
		Lock targetLock = null;
		this.lock.lock();
		try {
			Log.debug("AUTO: combat info - run");
			final int abilityID = (int) this.getProperty("combat.autoability");
			if (this.target == null) {
				this.scheduled = false;
			} else if (abilityID > 0 && this.getAttackDelay() > 0L) {
				final Entity e = EntityManager.getEntityByNamespace(this.target, Namespace.COMBAT);
				if (e == null) {
					this.target = null;
				} else {
					targetLock = e.getLock();
					if (targetLock != null) {
						while (!targetLock.tryLock()) {
							this.lock.unlock();
							Thread.yield();
							Log.debug("AUTO: yielding in run");
							this.lock.lock();
						}
					}
					Log.debug("AUTO: About to resolve auto attack");
					CombatPlugin.resolveAutoAttack(this);//×Ô¶¯¹¥»÷
					Log.debug("AUTO: About to schedule next auto attack");
					this.schedule(this.getAttackDelay());
					Log.debug("AUTO: scheduled next auto attack");
				}
			}
		} catch (Exception e2) {
			Log.exception("CombatInfo.run: got exception", e2);
			Log.debug("CombatInfo.run: got exception: " + e2);
			return;
		} finally {
			if (targetLock != null) {
				targetLock.unlock();
			}
			this.lock.unlock();
			Log.debug("AUTO: finished combat info - run");
		}
		if (targetLock != null) {
			targetLock.unlock();
		}
		this.lock.unlock();
		Log.debug("AUTO: finished combat info - run");
	}

	public int getID() {
		return this.id;
	}

	public void setID(final int id) {
		this.id = id;
	}

	public int getCurrentCategory() {
		return this.currentCategory;
	}

	public void setCurrentCategory(final int category) {
		Log.debug("QSI: setting current category to: " + category + " from: " + this.currentCategory);
		this.currentCategory = category;
	}

	public boolean categoryUpdated(final int category) {
		boolean createSkills = false;
		if (!this.skills.containsKey(category)) {
			createSkills = true;
		}
		this.currentCategory = category;
		Engine.getPersistenceManager().setDirty((Entity) this);
		return createSkills;
	}

	public void addSkill(final int skillID) {
	}

	public SkillInfo getCurrentSkillInfo() {
		this.lock.lock();
		try {
			return this.getSkillInfo(this.currentCategory);
		} finally {
			this.lock.unlock();
		}
	}

	public void setCurrentSkillInfo(final SkillInfo skills) {
		this.lock.lock();
		try {
			this.skills.put(this.currentCategory, skills);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public HashMap<Integer, SkillInfo> getSkillInfos() {
		this.lock.lock();
		try {
			return new HashMap<Integer, SkillInfo>(this.skills);
		} finally {
			this.lock.unlock();
		}
	}

	public void setSkillInfos(final HashMap<Integer, SkillInfo> skills) {
		this.lock.lock();
		try {
			this.skills = new HashMap<Integer, SkillInfo>(skills);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public SkillInfo getSkillInfo(final int category) {
		this.lock.lock();
		try {
			if (!this.skills.containsKey(category)) {
				this.skills.put(category, new SkillInfo(category));
			}
			return this.skills.get(category);
		} finally {
			this.lock.unlock();
		}
	}

	public ArrayList<Integer> getCurrentAbilities() {
		this.lock.lock();
		try {
			return this.getAbilities(this.currentCategory);
		} finally {
			this.lock.unlock();
		}
	}

	public void setCurrentAbilities(final ArrayList<Integer> abilities) {
		this.lock.lock();
		try {
			this.abilities.put(this.currentCategory, abilities);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public HashMap<Integer, ArrayList<Integer>> getAbilities() {
		this.lock.lock();
		try {
			return new HashMap<Integer, ArrayList<Integer>>(this.abilities);
		} finally {
			this.lock.unlock();
		}
	}

	public void setAbilities(final HashMap<Integer, ArrayList<Integer>> abilities) {
		this.lock.lock();
		try {
			this.abilities = new HashMap<Integer, ArrayList<Integer>>(abilities);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public ArrayList<Integer> getAbilities(final int category) {
		this.lock.lock();
		try {
			if (!this.abilities.containsKey(category)) {
				this.abilities.put(category, new ArrayList<Integer>());
			}
			return this.abilities.get(category);
		} finally {
			this.lock.unlock();
		}
	}

	public void addAction(final int abilityNum) {
		final ArrayList<String> currentActions = this.getCurrentActions();
		for (int i = 0; i < 10; ++i) {
			if (currentActions.get(i).equals("")) {
				Log.debug("SKILL: adding new action to spot " + i);
				currentActions.set(i, "a" + abilityNum);
				return;
			}
		}
	}

	public void removeAction(final int abilityNum) {
		final ArrayList<String> currentActions = this.getCurrentActions();
		for (int i = 0; i < currentActions.size(); ++i) {
			if (currentActions.get(i).equals("a" + abilityNum)) {
				Log.debug("SKILL: removing action at spot " + i);
				currentActions.set(i, "");
				return;
			}
		}
	}

	public ArrayList<String> getCurrentActions() {
		this.lock.lock();
		try {
			return this.getActions(this.currentCategory);
		} finally {
			this.lock.unlock();
		}
	}

	public void setCurrentActions(final ArrayList<String> actions) {
		this.lock.lock();
		try {
			this.actions.put(this.currentCategory, actions);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
		Engine.getPersistenceManager().setDirty((Entity) this);
	}

	public HashMap<Integer, ArrayList<String>> getActions() {
		this.lock.lock();
		try {
			return new HashMap<Integer, ArrayList<String>>(this.actions);
		} finally {
			this.lock.unlock();
		}
	}

	public void setActions(final HashMap<Integer, ArrayList<String>> actions) {
		this.lock.lock();
		try {
			this.actions = new HashMap<Integer, ArrayList<String>>(actions);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
		Engine.getPersistenceManager().setDirty((Entity) this);
	}

	public ArrayList<String> getActions(final int category) {
		this.lock.lock();
		try {
			if (!this.actions.containsKey(category)) {
				this.actions.put(category, new ArrayList<String>());
			}
			return this.actions.get(category);
		} finally {
			this.lock.unlock();
		}
	}

	public void addCooldownState(final Cooldown.State state) {
		this.lock.lock();
		try {
			this.cooldownMap.put(state.getID(), state);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void removeCooldownState(final Cooldown.State state) {
		this.lock.lock();
		try {
			this.cooldownMap.remove(state.getID());
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public Cooldown.State getCooldownState(final String id) {
		this.lock.lock();
		try {
			return this.cooldownMap.get(id);
		} finally {
			this.lock.unlock();
		}
	}

	public Map<String, Cooldown.State> getCooldownMap() {
		return this.cooldownMap;
	}

	public void setCurrentAction(final AgisAbilityState action) {
		this.currentAction = action;
	}

	public AgisAbilityState getCurrentAction() {
		return this.currentAction;
	}

	public void addActiveAbility(final AgisAbilityState abilityState) {
		this.lock.lock();
		try {
			this.activeAbilities.add(abilityState);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void removeActiveAbility(final AgisAbilityState abilityState) {
		this.lock.lock();
		try {
			this.activeAbilities.remove(abilityState);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public Set<AgisEffect.EffectState> getCurrentEffects() {
		this.lock.lock();
		try {
			return this.getEffects(this.currentCategory);
		} finally {
			this.lock.unlock();
		}
	}

	public void addEffect(final AgisEffect.EffectState effectState) {
		this.lock.lock();
		try {
			this.getEffects(this.currentCategory).add(effectState);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
		this.addEffectsProperty(effectState.getEffect().getID());
	}

	public void removeEffect(final AgisEffect.EffectState effectState) {
		this.lock.lock();
		try {
			this.getEffects(this.currentCategory).remove(effectState);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
		this.removeEffectsProperty(effectState.getEffectID());
	}

	public HashMap<Integer, HashSet<AgisEffect.EffectState>> getEffects() {
		this.lock.lock();
		try {
			return new HashMap<Integer, HashSet<AgisEffect.EffectState>>(this.effects);
		} finally {
			this.lock.unlock();
		}
	}

	public void setEffects(final HashMap<Integer, HashSet<AgisEffect.EffectState>> effects) {
		this.lock.lock();
		try {
			this.effects = new HashMap<Integer, HashSet<AgisEffect.EffectState>>(effects);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public Set<AgisEffect.EffectState> getEffects(final int category) {
		this.lock.lock();
		try {
			if (!this.effects.containsKey(category)) {
				this.effects.put(category, new HashSet<AgisEffect.EffectState>());
			}
			return this.effects.get(category);
		} finally {
			this.lock.unlock();
		}
	}

	public void addFriendlyTarget(final OID oid, final TargetInfo info) {
		this.lock.lock();
		try {
			this.friendlyTargets.put(oid, info);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void removeFriendlyTarget(final OID oid) {
		this.lock.lock();
		try {
			this.friendlyTargets.remove(oid);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public HashMap<OID, TargetInfo> getFriendlyTargets() {
		return this.friendlyTargets;
	}

	public void setFriendlyTargets(final HashMap<OID, TargetInfo> targets) {
		this.friendlyTargets = targets;
	}

	public void addAttackableTarget(final OID oid, final TargetInfo info) {
		this.lock.lock();
		try {
			this.attackableTargets.put(oid, info);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void removeAttackableTarget(final OID oid) {
		this.lock.lock();
		try {
			this.attackableTargets.remove(oid);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public HashMap<OID, TargetInfo> getAttackableTargets() {
		return new HashMap<OID, TargetInfo>(this.attackableTargets);
	}

	public void setAttackableTargets(final HashMap<OID, TargetInfo> targets) {
		this.attackableTargets = new HashMap<OID, TargetInfo>(targets);
	}

	public void addTargetInCombat(final OID oid) {
		this.lock.lock();
		try {
			this.targetsInCombat.add(oid);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void removeTargetInCombat(final OID oid) {
		this.lock.lock();
		try {
			this.targetsInCombat.remove(oid);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public ArrayList<OID> getTargetsInCombat() {
		return new ArrayList<OID>(this.targetsInCombat);
	}

	public void setTargetsInCombat(final ArrayList<OID> targetsInCombat) {
		this.targetsInCombat = new ArrayList<OID>(targetsInCombat);
	}

	public boolean isUser() {
		return this.getBooleanProperty("combat.userflag");
	}

	public boolean isMob() {
		return this.getBooleanProperty("combat.mobflag");
	}

	public boolean attackable() {
		return this.getBooleanProperty("attackable");
	}

	public boolean dead() {
		return this.getBooleanProperty("deadstate");
	}

	public String aspect() {
		return this.getStringProperty("aspect");
	}

	public String team() {
		return this.getStringProperty("team");
	}

	public void team(final String newTeam) {
		this.setProperty("team", (Serializable) newTeam);
		final PropertyMessage propMsg = new PropertyMessage(this.getOwnerOid());
		propMsg.setProperty("team", (Serializable) newTeam);
		Engine.getAgent().sendBroadcast((Message) propMsg);
	}

	public OID getOwnerOid() {
		return this.getOid();
	}

	public boolean inCombat() {
		return this.getBooleanProperty("combatstate");
	}

	public void setCombatState(final boolean state) {
		this.setProperty("combatstate", (Serializable) new Boolean(state));
		final PropertyMessage propMsg = new PropertyMessage(this.getOwnerOid());
		propMsg.setProperty("combatstate", (Serializable) new Boolean(state));
		Engine.getAgent().sendBroadcast((Message) propMsg);
		if (!state) {
			if (!this.dead()) {
				this.setProperty("tagOwner", (Serializable) null);
			}
			EnginePlugin.setObjectPropertyNoResponse(this.getOwnerOid(), WorldManagerClient.NAMESPACE, "weaponsSheathed", (Serializable) true);
		} else {
			EnginePlugin.setObjectPropertyNoResponse(this.getOwnerOid(), WorldManagerClient.NAMESPACE, "weaponsSheathed", (Serializable) false);
		}
	}

	public void setDeadState(final boolean state) {
		this.setProperty("deadstate", (Serializable) new Boolean(state));
		final PropertyMessage propMsg = new PropertyMessage(this.getOwnerOid());
		propMsg.setProperty("deadstate", (Serializable) new Boolean(state));
		Engine.getPersistenceManager().setDirty((Entity) this);
		Engine.getAgent().sendBroadcast((Message) propMsg);
	}

	public String getState() {
		return this.getStringProperty("state");
	}

	public void setState(final String state) {
		this.setProperty("state", (Serializable) state);
		final PropertyMessage propMsg = new PropertyMessage(this.getOwnerOid());
		propMsg.setProperty("state", (Serializable) state);
		Engine.getPersistenceManager().setDirty((Entity) this);
		Engine.getAgent().sendBroadcast((Message) propMsg);
	}

	public void clearState(final String state) {
		final String currentState = (String) this.getProperty("state");
		if (currentState != null && currentState.equals(state)) {
			this.setProperty("state", (Serializable) "");
			final PropertyMessage propMsg = new PropertyMessage(this.getOwnerOid());
			propMsg.setProperty("state", (Serializable) "");
			Engine.getPersistenceManager().setDirty((Entity) this);
			Engine.getAgent().sendBroadcast((Message) propMsg);
		}
	}

	public void sendStatusUpdate() {
	}

	private void addEffectsProperty(final int effectID) {
		final OID targetOwnerID = this.getOwnerOid();
		final LinkedList<Integer> effects = (LinkedList<Integer>) this.getProperty("effects");
		effects.add(effectID);
		this.setProperty("effects", (Serializable) effects);
		final PropertyMessage propMsg = new PropertyMessage(targetOwnerID);
		propMsg.setProperty("effects", (Serializable) effects);
		Engine.getAgent().sendBroadcast((Message) propMsg);
	}

	private void removeEffectsProperty(final Integer effectID) {
		Log.debug("EFFECT: removing effect ID: " + effectID);
		final OID targetOwnerID = this.getOwnerOid();
		final LinkedList<Integer> effects = (LinkedList<Integer>) this.getProperty("effects");
		effects.remove(effectID);
		this.setProperty("effects", (Serializable) effects);
		final PropertyMessage propMsg = new PropertyMessage(targetOwnerID);
		propMsg.setProperty("effects", (Serializable) effects);
		Engine.getAgent().sendBroadcast((Message) propMsg);
	}

	public InterpolatedWorldNode getWorldNode() {
		return this.node;
	}

	public void setWorldNode(final InterpolatedWorldNode node) {
		this.node = node;
	}

	public void statModifyBaseValue(final String statName, final int delta) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			stat.modifyBaseValue(delta);
			final AgisStatDef statDef = CombatPlugin.lookupStatDef(statName);
			statDef.update(stat, this);
			this.statSendUpdate(false);
			Engine.getPersistenceManager().setDirty((Entity) this);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void statSetBaseValue(final String statName, final int value) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			stat.setBaseValue(value);
			final AgisStatDef statDef = CombatPlugin.lookupStatDef(statName);
			statDef.update(stat, this);
			this.statSendUpdate(false);
			Engine.getPersistenceManager().setDirty((Entity) this);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void statSetMaxValue(final String statName, final int value) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			stat.setMaxValue(value);
			final AgisStatDef statDef = CombatPlugin.lookupStatDef(statName);
			statDef.update(stat, this);
			this.statSendUpdate(false);
			Engine.getPersistenceManager().setDirty((Entity) this);
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void statAddModifier(final String statName, final Object id, final int delta) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			if (stat == null) {
				Log.error("CombatInfo.statAddModifier: statName=" + statName + " does not exist for this=" + this);
			} else {
				stat.addModifier(id, delta);
				final AgisStatDef statDef = CombatPlugin.lookupStatDef(statName);
				if (statDef == null) {
					Log.error("CombatInfo.statAddModifier: statName=" + statName + " is not registered with CombatPlugin.");
				} else {
					statDef.update(stat, this);
					this.statSendUpdate(false);
					Engine.getPersistenceManager().setDirty((Entity) this);
				}
			}
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void statAddPercentModifier(final String statName, final Object id, final float percent) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			if (stat == null) {
				Log.error("CombatInfo.statAddPercentModifier: statName=" + statName + " does not exist for this=" + this);
			} else {
				stat.addPercentModifier(id, percent);
				final AgisStatDef statDef = CombatPlugin.lookupStatDef(statName);
				if (statDef == null) {
					Log.error("CombatInfo.statAddModifier: statName=" + statName + " is not registered with CombatPlugin.");
				} else {
					statDef.update(stat, this);
					this.statSendUpdate(false);
					Engine.getPersistenceManager().setDirty((Entity) this);
				}
			}
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void statRemoveModifier(final String statName, final Object id) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			Log.debug("STAT: removing modifier with stat: " + statName + " and current value: " + stat.getCurrentValue());
			stat.removeModifier(id);
			final AgisStatDef statDef = CombatPlugin.lookupStatDef(statName);
			statDef.update(stat, this);
			this.statSendUpdate(false);
			Engine.getPersistenceManager().setDirty((Entity) this);
			Log.debug("STAT: removed modifier from stat: " + statName + " and current value: " + stat.getCurrentValue());
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void statRemovePercentModifier(final String statName, final Object id) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			Log.debug("STAT: removing percent modifier with stat: " + statName + " and current value: " + stat.getCurrentValue());
			stat.removePercentModifier(id);
			final AgisStatDef statDef = CombatPlugin.lookupStatDef(statName);
			statDef.update(stat, this);
			this.statSendUpdate(false);
			Engine.getPersistenceManager().setDirty((Entity) this);
			Log.debug("STAT: removed percent modifier from stat: " + statName + " and current value: " + stat.getCurrentValue());
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public int statGetCurrentValue(final String statName) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			return stat.getCurrentValue();
		} finally {
			this.lock.unlock();
		}
	}

	public int statGetBaseValue(final String statName) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			return stat.getBaseValue();
		} finally {
			this.lock.unlock();
		}
	}

	public int statGetMinValue(final String statName) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			return stat.getMinValue();
		} finally {
			this.lock.unlock();
		}
	}

	public int statGetMaxValue(final String statName) {
		this.lock.lock();
		try {
			final AgisStat stat = (AgisStat) this.getProperty(statName);
			return stat.getMaxValue();
		} finally {
			this.lock.unlock();
		}
	}

	public void statSendUpdate(final boolean sendAll) {
		this.statSendUpdate(sendAll, null);
	}

	public void statSendUpdate(final boolean sendAll, final OID targetOid) {
		this.lock.lock();
		try {
			PropertyMessage propMsg = null;
			WorldManagerClient.TargetedPropertyMessage targetPropMsg = null;
			if (targetOid == null) {
				propMsg = new PropertyMessage(this.getOwnerOid());
			} else {
				targetPropMsg = new WorldManagerClient.TargetedPropertyMessage(targetOid, this.getOwnerOid());
			}
			int count = 0;
			for (final Object value : this.getPropertyMap().values()) {
				if (value instanceof AgisStat) {
					final AgisStat stat = (AgisStat) value;
					if (!sendAll && !stat.isDirty()) {
						continue;
					}
					if (propMsg != null) {
						propMsg.setProperty(stat.getName(), (Serializable) stat.getCurrentValue());
					} else {
						targetPropMsg.setProperty(stat.getName(), (Serializable) stat.getCurrentValue());
					}
					if (!sendAll) {
						stat.setDirty(false);
					}
					++count;
				}
			}
			if (count > 0) {
				Engine.getPersistenceManager().setDirty((Entity) this);
				if (propMsg != null) {
					Engine.getAgent().sendBroadcast((Message) propMsg);
				} else {
					Engine.getAgent().sendBroadcast((Message) targetPropMsg);
				}
			}
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public void runCombatTick() {
		for (final String statName : this.vitalityStats.keySet()) {
			if (this.vitalityStats.get(statName) < System.currentTimeMillis()) {
				final VitalityStatDef statDef = (VitalityStatDef) CombatPlugin.lookupStatDef(statName);
				this.applyStatShift(statName);
				final int updateInterval = statDef.getShiftInterval();
				this.vitalityStats.put(statName, System.currentTimeMillis() + updateInterval * 1000);
			}
		}
	}

	private void applyStatShift(final String statName) {
		final AgisStat stat = (AgisStat) this.getProperty(statName);
		final VitalityStatDef statDef = (VitalityStatDef) CombatPlugin.lookupStatDef(statName);
		final int shiftDirection = statDef.getShiftDirection(stat, this);
		this.lock.lock();
		try {
			if (shiftDirection != 0) {
				final int delta = stat.getShift(shiftDirection);
				Log.debug("SHIFT: applying shift with delta: " + delta);
				stat.modifyBaseValue(delta);
				statDef.update(stat, this);
				this.statSendUpdate(false);
				Engine.getPersistenceManager().setDirty((Entity) this);
			}
		} finally {
			this.lock.unlock();
		}
		this.lock.unlock();
	}

	public HashMap<String, Long> getVitalityStats() {
		return this.vitalityStats;
	}

	public void setVitalityStats(final HashMap<String, Long> vitalityStats) {
		this.vitalityStats = vitalityStats;
	}

	public void addVitalityStat(final AgisStat stat, final long updateInterval) {
		Log.debug("COMBAT: adding vitality stat: " + stat.name);
		final VitalityStatDef statDef = (VitalityStatDef) CombatPlugin.lookupStatDef(stat.name);
		stat.setBaseShiftValue(statDef.getShiftValue(), statDef.getReverseShiftValue());
		this.vitalityStats.put(stat.name, System.currentTimeMillis() + updateInterval * 1000L);
	}

	public void setGroupOid(final OID groupOid) {
		this.groupOid = groupOid;
	}

	public OID getGroupOid() {
		return this.groupOid;
	}

	public void setGroupMemberOid(final OID groupMemberOid) {
		this.groupMemberOid = groupMemberOid;
	}

	public OID getGroupMemberOid() {
		return this.groupMemberOid;
	}

	public boolean isGrouped() {
		return this.groupOid != null;
	}

	public void setPendingGroupInvite(final boolean flag) {
		this.pendingGroupInvite = flag;
	}

	public boolean isPendingGroupInvite() {
		return this.pendingGroupInvite;
	}
}
