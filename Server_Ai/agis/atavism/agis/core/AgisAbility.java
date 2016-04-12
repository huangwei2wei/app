// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.util.EventMessageHelper;
import atavism.server.engine.EnginePlugin;
import atavism.agis.objects.AgisItem;
import atavism.server.plugins.InventoryClient;
import atavism.agis.objects.TargetInfo;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.plugins.CombatClient;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.engine.BasicWorldNode;
import atavism.agis.plugins.CombatPlugin;
import atavism.server.math.Point;
import atavism.server.util.Log;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.agis.objects.CombatInfo;
import java.util.List;
import java.util.Iterator;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Collection;
import java.util.HashMap;
import atavism.server.util.LockFactory;
import atavism.agis.objects.LevelingMap;
import atavism.agis.objects.CoordinatedEffect;
import java.util.Set;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Lock;

public class AgisAbility
{
    protected transient Lock lock;
    String name;
    int id;
    protected int skillType;
    int abilityType;
    int duelID;
    protected long activationTime;
    protected int activationCost;
    protected String costProp;
    protected long channelPulseTime;
    protected int channelPulses;
    protected int channelCost;
    protected long activePulseTime;
    protected int activePulseCost;
    protected String icon;
    protected String displayName;
    protected String tooltip;
    protected int minRange;
    protected int maxRange;
    protected int areaOfEffectRadius;
    protected Map<String, Cooldown> cooldownMap;
    public String weaponReq;
    protected boolean decrementWeaponUses;
    protected ArrayList<Integer> reagentList;
    protected boolean consumeReagents;
    protected ArrayList<Integer> toolList;
    protected int vigor;
    protected String stanceReq;
    protected String castingAnim;
    protected String castingAffinity;
    protected boolean reqTarget;
    protected TargetType targetType;
    protected LinkedList<String> specificTargets;
    protected LinkedList<TargetSpecies> targetableSpecies;
    protected int targetDeath;
    public LinkedList<Integer> attackerEffectReqs;
    public LinkedList<Integer> targetEffectReqs;
    public LinkedList<Integer> attackerEffectConsumption;
    public LinkedList<Integer> targetEffectConsumption;
    protected boolean useGlobalCooldown;
    protected boolean stationary;
    protected boolean channelled;
    protected boolean persistent;
    protected Map<ActivationState, Set<CoordinatedEffect>> coordEffectMap;
    protected String completeAnimation;
    protected String completeSound;
    protected boolean sendSkillUpChance;
    String slashCommand;
    AgisSkill requiredSkill;
    int requiredSkillLevel;
    int exp_per_use;
    LevelingMap lm;
    int exp_max;
    int rank_max;
    public static final int RESULT_HIT = 1;
    public static final int RESULT_CRITICAL = 2;
    public static final int RESULT_MISSED = 3;
    public static final int RESULT_PARRIED = 4;
    public static final int RESULT_DODGED = 5;
    public static final int RESULT_BLOCKED = 6;
    public static final int RESULT_EVADED = 10;
    public static final int RESULT_IMMUNE = 11;
    
    public AgisAbility(final String name) {
        this.lock = LockFactory.makeLock("AgisAbilityLock");
        this.name = null;
        this.id = -1;
        this.skillType = -1;
        this.abilityType = -1;
        this.duelID = -1;
        this.activationTime = 0L;
        this.activationCost = 0;
        this.costProp = null;
        this.channelPulseTime = 0L;
        this.channelPulses = 0;
        this.channelCost = 0;
        this.activePulseTime = 0L;
        this.activePulseCost = 0;
        this.icon = null;
        this.displayName = null;
        this.tooltip = null;
        this.minRange = 0;
        this.maxRange = 0;
        this.areaOfEffectRadius = 0;
        this.cooldownMap = new HashMap<String, Cooldown>();
        this.weaponReq = "";
        this.decrementWeaponUses = false;
        this.reagentList = new ArrayList<Integer>();
        this.consumeReagents = true;
        this.toolList = new ArrayList<Integer>();
        this.vigor = 0;
        this.stanceReq = "";
        this.castingAnim = "";
        this.castingAffinity = "";
        this.reqTarget = true;
        this.targetType = TargetType.UNINIT;
        this.specificTargets = null;
        this.targetableSpecies = new LinkedList<TargetSpecies>();
        this.targetDeath = 1;
        this.attackerEffectReqs = new LinkedList<Integer>();
        this.targetEffectReqs = new LinkedList<Integer>();
        this.attackerEffectConsumption = new LinkedList<Integer>();
        this.targetEffectConsumption = new LinkedList<Integer>();
        this.useGlobalCooldown = true;
        this.stationary = false;
        this.channelled = false;
        this.persistent = false;
        this.coordEffectMap = new HashMap<ActivationState, Set<CoordinatedEffect>>();
        this.sendSkillUpChance = true;
        this.slashCommand = null;
        this.requiredSkill = null;
        this.requiredSkillLevel = -1;
        this.exp_per_use = 0;
        this.lm = new LevelingMap();
        this.exp_max = 100;
        this.rank_max = 3;
        this.setName(name);
    }
    
    @Override
    public String toString() {
        return "[AgisAbility: " + this.getName() + "]";
    }
    
    @Override
    public boolean equals(final Object other) {
        final AgisAbility otherAbility = (AgisAbility)other;
        final boolean val = this.getName().equals(otherAbility.getName());
        return val;
    }
    
    @Override
    public int hashCode() {
        final int hash = this.getName().hashCode();
        return hash;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setSkillType(final int skillType) {
        this.skillType = skillType;
    }
    
    public int getSkillType() {
        return this.skillType;
    }
    
    public void setAbilityType(final int abilityType) {
        this.abilityType = abilityType;
    }
    
    public int getAbilityType() {
        return this.abilityType;
    }
    
    public void setDuelID(final int duelID) {
        this.duelID = duelID;
    }
    
    public int getDuelID() {
        return this.duelID;
    }
    
    public long getActivationTime() {
        return this.activationTime;
    }
    
    public void setActivationTime(final long time) {
        this.activationTime = time;
    }
    
    public boolean isInstant() {
        return this.activationTime == 0L;
    }
    
    public int getActivationCost() {
        return this.activationCost;
    }
    
    public void setActivationCost(final int cost) {
        this.activationCost = cost;
    }
    
    public String getCostProperty() {
        return this.costProp;
    }
    
    public void setCostProperty(final String name) {
        this.costProp = name;
    }
    
    public long getChannelPulseTime() {
        return this.channelPulseTime;
    }
    
    public void setChannelPulseTime(final long time) {
        this.channelPulseTime = time;
    }
    
    public int getChannelPulses() {
        return this.channelPulses;
    }
    
    public void setChannelPulses(final int pulses) {
        this.channelPulses = pulses;
    }
    
    public int getChannelCost() {
        return this.channelCost;
    }
    
    public void setChannelCost(final int cost) {
        this.channelCost = cost;
    }
    
    public long getActivePulseTime() {
        return this.activePulseTime;
    }
    
    public void setActivePulseTime(final long time) {
        this.activePulseTime = time;
    }
    
    public int getActiveCost() {
        return this.activePulseCost;
    }
    
    public int getActivePulseCost() {
        return this.activePulseCost;
    }
    
    public void setActiveCost(final int cost) {
        this.activePulseCost = cost;
    }
    
    public void setActivePulseCost(final int cost) {
        this.activePulseCost = cost;
    }
    
    public String getIcon() {
        return this.icon;
    }
    
    public void setIcon(final String icon) {
        this.icon = icon;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getTooltip() {
        return this.tooltip;
    }
    
    public void setTooltip(final String tooltip) {
        this.tooltip = tooltip;
    }
    
    public int getMinRange() {
        return this.minRange;
    }
    
    public void setMinRange(final int range) {
        this.minRange = range;
    }
    
    public int getMaxRange() {
        return this.maxRange;
    }
    
    public void setMaxRange(final int range) {
        this.maxRange = range;
    }
    
    public int getAreaOfEffectRadius() {
        return this.areaOfEffectRadius;
    }
    
    public void setAreaOfEffectRadius(final int radius) {
        this.areaOfEffectRadius = radius;
    }
    
    public void addCooldown(final Cooldown cd) {
        try {
            this.lock.lock();
            this.cooldownMap.put(cd.getID(), cd);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void removeCooldown(final String id) {
        try {
            this.lock.lock();
            this.cooldownMap.remove(id);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Map<String, Cooldown> getCooldownMap() {
        try {
            this.lock.lock();
            return new HashMap<String, Cooldown>(this.cooldownMap);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setCooldownMap(final Map<String, Cooldown> cooldownMap) {
        try {
            this.lock.lock();
            this.cooldownMap = new HashMap<String, Cooldown>(cooldownMap);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setWeaponReq(final String weaponType) {
        this.weaponReq = weaponType;
    }
    
    public String getWeaponReq() {
        return this.weaponReq;
    }
    
    public boolean getDecrementWeaponUses() {
        return this.decrementWeaponUses;
    }
    
    public void setDecrementWeaponUses(final boolean decrement) {
        this.decrementWeaponUses = decrement;
    }
    
    public void addReagent(final Integer reagent) {
        try {
            this.lock.lock();
            this.reagentList.add(reagent);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public ArrayList<Integer> getReagentList() {
        try {
            this.lock.lock();
            return new ArrayList<Integer>(this.reagentList);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setReagentList(final ArrayList<Integer> reagentList) {
        try {
            this.lock.lock();
            this.reagentList = new ArrayList<Integer>(reagentList);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public boolean getConsumeReagents() {
        return this.consumeReagents;
    }
    
    public void setConsumeReagents(final boolean consume) {
        this.consumeReagents = consume;
    }
    
    public void addTool(final int tool) {
        try {
            this.lock.lock();
            this.toolList.add(tool);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public ArrayList<Integer> getToolList() {
        try {
            this.lock.lock();
            return new ArrayList<Integer>(this.toolList);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setToolList(final ArrayList<Integer> toolList) {
        try {
            this.lock.lock();
            this.toolList = new ArrayList<Integer>(toolList);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int getVigor() {
        return this.vigor;
    }
    
    public void setVigor(final int value) {
        this.vigor = value;
    }
    
    public String getStance() {
        return this.stanceReq;
    }
    
    public void setStance(final String stance) {
        this.stanceReq = stance;
    }
    
    public String getCastingAnim() {
        return this.castingAnim;
    }
    
    public void setCastingAnim(final String anim) {
        this.castingAnim = anim;
    }
    
    public String getCastingAffinity() {
        return this.castingAffinity;
    }
    
    public void setCastingAffinity(final String affinity) {
        this.castingAffinity = affinity;
    }
    
    public boolean getReqTarget() {
        return this.reqTarget;
    }
    
    public void setReqTarget(final boolean req) {
        this.reqTarget = req;
    }
    
    public TargetType getTargetType() {
        return this.targetType;
    }
    
    public void setTargetType(final TargetType type) {
        this.targetType = type;
    }
    
    public LinkedList<String> getSpecificTargets() {
        return this.specificTargets;
    }
    
    public void setSpecificTargets(final LinkedList<String> targets) {
        this.specificTargets = targets;
    }
    
    public void addSpecificTarget(final String name) {
        if (this.specificTargets == null) {
            this.specificTargets = new LinkedList<String>();
        }
        this.specificTargets.add(name);
    }
    
    public LinkedList<TargetSpecies> getTargetableSpecies() {
        return this.targetableSpecies;
    }
    
    public void setTargetableSpecies(final LinkedList<TargetSpecies> targets) {
        this.targetableSpecies = targets;
    }
    
    public void addTargetableSpecies(final TargetSpecies species) {
        this.targetableSpecies.add(species);
    }
    
    public int getTargetDeath() {
        return this.targetDeath;
    }
    
    public void setTargetDeath(final int type) {
        this.targetDeath = type;
    }
    
    public void addAttackerEffectReq(final int effectReq) {
        this.attackerEffectReqs.add(effectReq);
    }
    
    public LinkedList<Integer> GetAttackerEffectReqs() {
        return this.attackerEffectReqs;
    }
    
    public void addTargetEffectReq(final int effectReq) {
        this.targetEffectReqs.add(effectReq);
    }
    
    public LinkedList<Integer> GetTargetEffectReqs() {
        return this.targetEffectReqs;
    }
    
    public void addAttackerEffectConsumption(final int effectReq) {
        this.attackerEffectConsumption.add(effectReq);
    }
    
    public LinkedList<Integer> GetAttackerEffectConsumption() {
        return this.attackerEffectConsumption;
    }
    
    public void addTargetEffectConsumption(final int effectReq) {
        this.targetEffectConsumption.add(effectReq);
    }
    
    public LinkedList<Integer> GetTargetEffectConsumption() {
        return this.targetEffectConsumption;
    }
    
    public boolean getUseGlobalCooldown() {
        return this.useGlobalCooldown;
    }
    
    public void setUseGlobalCooldown(final boolean val) {
        this.useGlobalCooldown = val;
    }
    
    public boolean getStationary() {
        return this.stationary;
    }
    
    public void setStationary(final boolean val) {
        this.stationary = val;
    }
    
    public boolean getChannelled() {
        return this.channelled;
    }
    
    public void setChannelled(final boolean val) {
        this.channelled = val;
    }
    
    public boolean getPersistent() {
        return this.persistent;
    }
    
    public void setPersistent(final boolean val) {
        this.persistent = val;
    }
    
    public boolean addCoordEffect(final ActivationState state, final CoordinatedEffect effect) {
        Set<CoordinatedEffect> effectSet = this.coordEffectMap.get(state);
        if (effectSet == null) {
            effectSet = new HashSet<CoordinatedEffect>();
            this.coordEffectMap.put(state, effectSet);
        }
        return effectSet.add(effect);
    }
    
    public boolean removeCoordEffect(final ActivationState state, final CoordinatedEffect effect) {
        final Set<CoordinatedEffect> effectSet = this.coordEffectMap.get(state);
        return effectSet != null && effectSet.remove(effect);
    }
    
    public Collection<CoordinatedEffect> getCoordEffects(final ActivationState state) {
        Set<CoordinatedEffect> effectSet = this.coordEffectMap.get(state);
        if (effectSet == null) {
            effectSet = new HashSet<CoordinatedEffect>();
            this.coordEffectMap.put(state, effectSet);
        }
        return effectSet;
    }
    
    protected void changeCoordinatedEffect(final String result) {
        final Set<CoordinatedEffect> effectSet = this.coordEffectMap.get(ActivationState.COMPLETED);
        if (effectSet == null) {
            return;
        }
        for (final CoordinatedEffect effect : effectSet) {
            final String argument = (String)effect.getArgument("result");
            if (argument != null) {
                effect.putArgument("result", result);
            }
        }
    }
    
    public String getCompleteAnimation() {
        return this.completeAnimation;
    }
    
    public void setCompleteAnimation(final String anim) {
        this.completeAnimation = anim;
    }
    
    public String getCompleteSound() {
        return this.completeSound;
    }
    
    public void setCompleteSound(final String sound) {
        this.completeSound = sound;
    }
    
    public List<CombatInfo> getPotentialTargets(final AgisAbilityState state) {
        final List<CombatInfo> targets = new LinkedList<CombatInfo>();
        targets.add(state.getTarget());
        return targets;
    }
    
    public ArrayList<CombatInfo> getAoETargets(final CombatInfo caster) {
        final ArrayList<CombatInfo> targetsInArea = new ArrayList<CombatInfo>();
        if (this.targetType == TargetType.AREA_ENEMY) {
            for (final OID target : caster.getAttackableTargets().keySet()) {
                final BasicWorldNode node = WorldManagerClient.getWorldNode(caster.getOwnerOid());
                final Point loc = node.getLoc();
                final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(target);
                if (targetNode == null) {
                    Log.error("ABILITY: target node was null");
                }
                final Point targetLoc = targetNode.getLoc();
                if (Point.distanceTo(loc, targetLoc) <= this.areaOfEffectRadius) {
                    targetsInArea.add(CombatPlugin.getCombatInfo(target));
                    Log.error("Added aoe target: " + target);
                }
                else {
                    Log.error("Target: " + target + " was too far away");
                }
            }
        }
        else if (this.targetType == TargetType.AREA_FRIENDLY) {
            for (final OID target : caster.getFriendlyTargets().keySet()) {
                final BasicWorldNode node = WorldManagerClient.getWorldNode(caster.getOwnerOid());
                final Point loc = node.getLoc();
                final BasicWorldNode targetNode = WorldManagerClient.getWorldNode(target);
                if (targetNode == null) {
                    Log.error("ABILITY: target node was null");
                }
                final Point targetLoc = targetNode.getLoc();
                if (Point.distanceTo(loc, targetLoc) <= this.areaOfEffectRadius) {
                    targetsInArea.add(CombatPlugin.getCombatInfo(target));
                }
            }
        }
        return targetsInArea;
    }
    
    public void beginActivation(final AgisAbilityState state) {
        Log.debug("AgisAbility.beginActivation:");
    }
    
    public void completeActivation(final AgisAbilityState state) {
        Log.debug("AgisAbility.completeActivation:");
        final CombatInfo combatInfo = state.getSource();
        final CombatInfo target = state.getTarget();
        if (!this.reagentList.isEmpty() && this.consumeReagents) {
            for (final int reagent : this.reagentList) {
                AgisInventoryClient.removeGenericItem(combatInfo.getOwnerOid(), reagent, false, 1);
            }
        }
        if (this.costProp != null) {
            combatInfo.statModifyBaseValue(this.costProp, -this.activationCost);
            combatInfo.sendStatusUpdate();
        }
        if (this.attackerEffectConsumption.size() > 0) {
            final OID oid = combatInfo.getOwnerOid();
            for (int i = 0; i < this.attackerEffectConsumption.size(); ++i) {
                final int effectConsumed = this.attackerEffectConsumption.get(i);
                for (final AgisEffect.EffectState existingState : state.getSource().getCurrentEffects()) {
                    if (effectConsumed == existingState.getEffect().getID()) {
                        Log.debug("ANDREW - attacker effect being consumed - effect num/pos: " + existingState.getEffect().getIcon());
                        AgisEffect.removeEffect(existingState);
                        break;
                    }
                }
            }
        }
        if (this.targetEffectConsumption.size() > 0) {
            final OID oid = target.getOwnerOid();
            for (int i = 0; i < this.targetEffectConsumption.size(); ++i) {
                final int effectConsumed = this.targetEffectConsumption.get(i);
                for (final AgisEffect.EffectState existingState : state.getTarget().getCurrentEffects()) {
                    if (effectConsumed == existingState.getEffect().getID()) {
                        Log.debug("ANDREW - target effect being consumed - effect num/pos: " + existingState.getEffect().getIcon());
                        AgisEffect.removeEffect(existingState);
                        break;
                    }
                }
            }
        }
        state.getItem();
        Log.debug("SKILL: checking send ability skill up for skill: " + this.skillType + " with sendSkill: " + this.sendSkillUpChance);
        if (combatInfo.isUser() && this.skillType != -1 && this.sendSkillUpChance) {
            Log.debug("SKILL: sending ability Used");
            CombatClient.abilityUsed(combatInfo.getOwnerOid(), this.skillType);
        }
        this.sendSkillUpChance = true;
        final Collection<Cooldown> cooldowns = state.getAbility().getCooldownMap().values();
        Cooldown.activateCooldowns(cooldowns, combatInfo, 100);
        Log.debug("AgisAbility.completeActivation: finished");
        if (this.decrementWeaponUses) {
            final WorldManagerClient.ExtensionMessage weaponMsg = new WorldManagerClient.ExtensionMessage(CombatClient.MSG_TYPE_DECREMENT_WEAPON_USES, (String)null, combatInfo.getOwnerOid());
            weaponMsg.setProperty("abilityID", (Serializable)this.id);
            Engine.getAgent().sendBroadcast((Message)weaponMsg);
        }
    }
    
    public void beginChannelling(final AgisAbilityState state) {
        Log.debug("AgisAbility.beginChannelling:");
    }
    
    public void pulseChannelling(final AgisAbilityState state) {
        final CombatInfo source = state.getSource();
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.pulseChannelling: cost=" + this.channelCost);
        }
        if (this.costProp != null) {
            source.statModifyBaseValue(this.costProp, -this.channelCost);
            source.sendStatusUpdate();
        }
    }
    
    public void completeChannelling(final AgisAbilityState state) {
        Log.debug("AgisAbility.completeChannelling:");
    }
    
    public void beginActivated(final AgisAbilityState state) {
        Log.debug("AgisAbility.beginActivated:");
    }
    
    public void pulseActivated(final AgisAbilityState state) {
        Log.debug("AgisAbility.pulseActivated:");
        final CombatInfo source = state.getSource();
        if (this.costProp != null) {
            source.statModifyBaseValue(this.costProp, -this.activePulseCost);
            source.sendStatusUpdate();
        }
    }
    
    public void endActivated(final AgisAbilityState state) {
        Log.debug("AgisAbility.endActivated:");
    }
    
    public void interrupt(final AgisAbilityState state) {
        Log.debug("AgisAbility.interrupt:");
    }
    
    public void setSlashCommand(final String slashCommand) {
        this.slashCommand = slashCommand;
    }
    
    public String getSlashCommand() {
        return this.slashCommand;
    }
    
    public void setRequiredSkill(final AgisSkill skill, final int level) {
        this.requiredSkill = skill;
        this.requiredSkillLevel = level;
    }
    
    public AgisSkill getRequiredSkill() {
        return this.requiredSkill;
    }
    
    public int getRequiredSkillLevel() {
        return this.requiredSkillLevel;
    }
    
    protected AbilityResult checkTarget(final CombatInfo caster, final CombatInfo target) {
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.checkTarget: obj=" + caster + " isUser=" + caster.isUser() + "target=" + target + " attackable=" + ((target == null) ? "N/A" : target.attackable()));
        }
        Log.debug("ABILITY: target type is: " + this.targetType);
        Log.debug("TARGET: friendly targets: " + caster.getFriendlyTargets());
        Log.debug("TARGET: attackable targets: " + caster.getAttackableTargets());
        switch (this.targetType) {
            case NONE:
            case SELF: {
                return AbilityResult.SUCCESS;
            }
            case ANY: {
                return AbilityResult.SUCCESS;
            }
            case AREA_ENEMY: {
                return AbilityResult.SUCCESS;
            }
            case AREA_FRIENDLY: {
                return AbilityResult.SUCCESS;
            }
            case FRIENDNOTSELF: {
                if (target.equals(caster)) {
                    return AbilityResult.INVALID_TARGET;
                }
                if (caster.getFriendlyTargets().containsKey(target.getOwnerOid())) {
                    return AbilityResult.SUCCESS;
                }
                return AbilityResult.INVALID_TARGET;
            }
            case FRIEND: {
                if (target.equals(caster) || caster.getFriendlyTargets().containsKey(target.getOwnerOid())) {
                    return AbilityResult.SUCCESS;
                }
                return AbilityResult.INVALID_TARGET;
            }
            case GROUP: {
                if (caster.isUser() && target.attackable()) {
                    return AbilityResult.INVALID_TARGET;
                }
                return AbilityResult.SUCCESS;
            }
            case ENEMY: {
                if (target.equals(caster)) {
                    return AbilityResult.INVALID_TARGET;
                }
                if (caster.isUser() && !target.attackable()) {
                    return AbilityResult.INVALID_TARGET;
                }
                if (caster.getAttackableTargets().containsKey(target.getOwnerOid())) {
                    return AbilityResult.SUCCESS;
                }
                return AbilityResult.INVALID_TARGET;
            }
            case OTHER: {
                if (target.equals(caster)) {
                    return AbilityResult.INVALID_TARGET;
                }
                return AbilityResult.SUCCESS;
            }
            default: {
                return AbilityResult.INVALID_TARGET;
            }
        }
    }
    
    protected AbilityResult checkTargetSpecies(final CombatInfo caster, final CombatInfo target) {
        final OID targetOid = target.getOwnerOid();
        String targetSpecies = "";
        if (caster.equals(target)) {
            return AbilityResult.SUCCESS;
        }
        if (caster.getAttackableTargets().containsKey(targetOid)) {
            targetSpecies = caster.getAttackableTargets().get(targetOid).getSpecies();
        }
        else if (caster.getFriendlyTargets().containsKey(targetOid)) {
            targetSpecies = caster.getFriendlyTargets().get(targetOid).getSpecies();
        }
        for (final TargetSpecies species : this.targetableSpecies) {
            switch (species) {
                case BEAST: {
                    if (!targetSpecies.equals("Beast")) {
                        return AbilityResult.INVALID_TARGET;
                    }
                    continue;
                }
                case HUMANOID: {
                    if (!targetSpecies.equals("Humanoid")) {
                        return AbilityResult.INVALID_TARGET;
                    }
                    continue;
                }
                case ELEMENTAL: {
                    if (!targetSpecies.equals("Elemental")) {
                        return AbilityResult.INVALID_TARGET;
                    }
                    continue;
                }
                case UNDEAD: {
                    if (!targetSpecies.equals("Undead")) {
                        return AbilityResult.INVALID_TARGET;
                    }
                    continue;
                }
                case PLAYER: {
                    if (!target.isUser()) {
                        return AbilityResult.INVALID_TARGET;
                    }
                    continue;
                }
                case NONPLAYER: {
                    if (target.isUser()) {
                        return AbilityResult.INVALID_TARGET;
                    }
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkSpecificTarget(final CombatInfo caster, final CombatInfo target) {
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.checkSpecificTarget: obj=" + caster + " isUser=" + caster.isUser() + "target=" + target + " attackable=" + ((target == null) ? "N/A" : target.attackable()));
        }
        if (this.specificTargets != null) {
            boolean acceptableTarget = false;
            Log.debug("Checking specific target names: " + target.getName());
            for (int i = 0; i < this.specificTargets.size(); ++i) {
                if (target.getName().equals(this.specificTargets.get(i))) {
                    acceptableTarget = true;
                }
            }
            if (!acceptableTarget) {
                return AbilityResult.INVALID_TARGET;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkState(final CombatInfo obj, final CombatInfo target) {
        if (obj.getState() != null && obj.getState().equals("incapacitated")) {
            return AbilityResult.BUSY;
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkHasAbility(final CombatInfo obj, final CombatInfo target) {
        if (obj.isUser() && this.skillType > 0 && obj.getAutoAttackAbility() != this.id) {
            Log.debug("AGISABILITY: checking if player knows the ability: " + this.id);
            final ArrayList<Integer> currentAbilities = obj.getCurrentAbilities();
            if (!currentAbilities.contains(this.id)) {
                Log.debug("AGISABILITY: player does not know this ability: " + this.id);
                return AbilityResult.UNKNOWN;
            }
        }
        Log.debug("AGISABILITY: player knows the ability");
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkAbilityType(final CombatInfo obj, final CombatInfo target) {
        if (this.abilityType == 2) {
            Log.debug("AGISABILITY: ability is passive, cannot be activated");
            return AbilityResult.PASSIVE;
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkDeath(final CombatInfo obj, final CombatInfo target) {
        if (obj.dead()) {
            return AbilityResult.DEAD;
        }
        if (this.targetDeath == 0) {
            if (target.dead()) {
                return AbilityResult.SUCCESS;
            }
            return AbilityResult.INVALID_TARGET;
        }
        else {
            if (this.targetDeath != 1) {
                return AbilityResult.SUCCESS;
            }
            if (target.dead()) {
                return AbilityResult.INVALID_TARGET;
            }
            return AbilityResult.SUCCESS;
        }
    }
    
    protected AbilityResult checkRange(final CombatInfo obj, final CombatInfo target, final float rangeTolerance) {
        switch (this.targetType) {
            case ENEMY:
            case FRIEND:
            case GROUP: {
                final BasicWorldNode casterWNode = WorldManagerClient.getWorldNode(obj.getOwnerOid());
                final BasicWorldNode targetWNode = WorldManagerClient.getWorldNode(target.getOwnerOid());
                if (casterWNode == null) {
                    Log.error("RANGE CHECK: wnode is null for caster: " + obj.getOwnerOid());
                    return AbilityResult.SUCCESS;
                }
                if (targetWNode == null) {
                    Log.error("RANGE CHECK: wnode is null for target: " + target.getOwnerOid());
                    return AbilityResult.SUCCESS;
                }
                final Point casterLoc = casterWNode.getLoc();
                final Point targetLoc = targetWNode.getLoc();
                final int hitbox = (int)obj.getProperty("hitBox") - 1;
                final int targetHitbox = (int)target.getProperty("hitBox") - 1;
                final int distance = (int)Point.distanceTo(casterLoc, targetLoc) - hitbox - targetHitbox;
                Log.debug("AgisAbility.checkRange: range=" + distance + " casterLoc=" + casterLoc + " targetLoc=" + targetLoc);
                if (distance > this.getMaxRange() * rangeTolerance) {
                    return AbilityResult.OUT_OF_RANGE;
                }
                if (distance < this.getMinRange() / rangeTolerance && this.getMinRange() > 0) {
                    return AbilityResult.TOO_CLOSE;
                }
                return AbilityResult.SUCCESS;
            }
            default: {
                return AbilityResult.SUCCESS;
            }
        }
    }
    
    protected AbilityResult checkReady(final CombatInfo obj, final CombatInfo target) {
        if (obj.getCurrentAction() != null) {
            return AbilityResult.BUSY;
        }
        if (!Cooldown.checkReady(this.cooldownMap.values(), obj)) {
            return AbilityResult.NOT_READY;
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkCost(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        if (this.costProp == null) {
            if (Log.loggingDebug) {
                Log.debug("AgisAbility.checkCost: costProp=" + this.costProp);
            }
            return AbilityResult.SUCCESS;
        }
        final Integer costValue = obj.statGetCurrentValue(this.costProp);
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.checkCost: costProp=" + this.costProp + " value=" + costValue);
        }
        switch (state) {
            case INIT:
            case ACTIVATING: {
                if (this.getActivationCost() > obj.statGetCurrentValue(this.costProp)) {
                    return AbilityResult.INSUFFICIENT_ENERGY;
                }
                break;
            }
            case CHANNELLING: {
                if (this.getChannelCost() > obj.statGetCurrentValue(this.costProp)) {
                    return AbilityResult.INSUFFICIENT_ENERGY;
                }
                break;
            }
            case ACTIVATED: {
                if (this.getActiveCost() > obj.statGetCurrentValue(this.costProp)) {
                    return AbilityResult.INSUFFICIENT_ENERGY;
                }
                break;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkVigor(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        if (this.getVigor() > -1 || obj.isMob()) {
            Log.debug("AgisAbility.checkCost: vigor=" + this.vigor);
            return AbilityResult.SUCCESS;
        }
        final int vigorcost = this.getVigor() * -1;
        switch (state) {
            case INIT:
            case ACTIVATING: {
                if (vigorcost > obj.statGetCurrentValue("vigor")) {
                    Log.debug("ANDREW - vigorcost: " + vigorcost + "current vigor: " + obj.statGetCurrentValue("vigor"));
                    return AbilityResult.INSUFFICIENT_VIGOR;
                }
                break;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkEffects(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        LinkedList<Integer> effects = (LinkedList<Integer>)obj.getProperty("effects");
        for (int i = 0; i < this.attackerEffectReqs.size(); ++i) {
            boolean effectPresent = false;
            for (int j = 0; j < effects.size(); ++j) {
                if (this.attackerEffectReqs.get(i).equals(effects.get(j))) {
                    effectPresent = true;
                }
            }
            if (!effectPresent) {
                Log.debug("ANDREW - attacker missing effect: " + this.attackerEffectReqs.get(i));
                Log.debug("ANDREW - attacker missing effect. Attacker effects: " + effects);
                return AbilityResult.EFFECT_MISSING;
            }
        }
        effects = (LinkedList<Integer>)target.getProperty("effects");
        for (int i = 0; i < this.targetEffectReqs.size(); ++i) {
            boolean effectPresent = false;
            for (int j = 0; j < effects.size(); ++j) {
                if (this.targetEffectReqs.get(i).equals(effects.get(j))) {
                    effectPresent = true;
                }
                else {
                    Log.debug("ANDREW - target missing effect: " + this.targetEffectReqs.get(i) + "; " + effects.get(j));
                }
            }
            if (!effectPresent) {
                Log.debug("ANDREW - target missing effect: " + this.targetEffectReqs.get(i));
                Log.debug("ANDREW - target missing effect. Target effects: " + effects);
                return AbilityResult.EFFECT_MISSING;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkStance(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        final OID playerOid = obj.getOwnerOid();
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkPosition(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkEquip(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        String weapType = obj.getStringProperty("weaponType");
        final String weapType2 = obj.getStringProperty("weapon2Type");
        if (weapType2 != null && !weapType2.equals("") && !weapType2.equals("null")) {
            weapType = String.valueOf(weapType) + obj.getStringProperty("weapon2Type");
        }
        Log.debug("WEAPON: checking for weapon requirement: " + this.weaponReq + " against users weapon: " + weapType);
        if (this.weaponReq.equals("Melee Weapon")) {
            if (weapType.contains("Unarmed")) {
                return AbilityResult.MISSING_WEAPON;
            }
        }
        else if (this.weaponReq.equals("1 Hand")) {
            if (!weapType.contains("1H")) {
                return AbilityResult.MISSING_WEAPON;
            }
        }
        else if (this.weaponReq.equals("2 Hand")) {
            if (!weapType.contains("2H")) {
                return AbilityResult.MISSING_WEAPON;
            }
        }
        else if (this.weaponReq.equals("Hammer")) {
            if (!weapType.contains("Hammer")) {
                return AbilityResult.MISSING_WEAPON;
            }
        }
        else if (this.weaponReq.equals("Sword")) {
            if (!weapType.contains("Sword")) {
                return AbilityResult.MISSING_WEAPON;
            }
        }
        else if (this.weaponReq.equals("Axe")) {
            if (!weapType.contains("Axe")) {
                return AbilityResult.MISSING_WEAPON;
            }
        }
        else if (this.weaponReq.equals("Staff")) {
            if (!weapType.contains("Staff")) {
                return AbilityResult.MISSING_WEAPON;
            }
        }
        else if (!this.weaponReq.equals("") && !weapType.contains(this.weaponReq)) {
            return AbilityResult.MISSING_WEAPON;
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkReagent(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        if ((state == ActivationState.INIT || state == ActivationState.ACTIVATING) && !this.reagentList.isEmpty()) {
            final List<OID> itemList = (List<OID>)InventoryClient.findItems(obj.getOwnerOid(), (ArrayList)this.reagentList);
            if (itemList == null || itemList.contains(null)) {
                return AbilityResult.MISSING_REAGENT;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    protected AbilityResult checkTool(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        if ((state == ActivationState.INIT || state == ActivationState.ACTIVATING) && !this.toolList.isEmpty()) {
            final List<OID> itemList = (List<OID>)InventoryClient.findItems(obj.getOwnerOid(), (ArrayList)this.toolList);
            if (itemList == null || itemList.contains(null)) {
                return AbilityResult.MISSING_TOOL;
            }
        }
        return AbilityResult.SUCCESS;
    }
    
    public AbilityResult checkAbility(final CombatInfo obj, final CombatInfo target) {
        return this.checkAbility(obj, target, ActivationState.INIT);
    }
    
    protected AbilityResult checkAbility(final CombatInfo obj, final CombatInfo target, final ActivationState state) {
        AbilityResult result = AbilityResult.SUCCESS;
        if (state == ActivationState.INIT) {
            result = this.checkReady(obj, target);
            if (result != AbilityResult.SUCCESS) {
                return result;
            }
            result = this.checkTarget(obj, target);
            if (result != AbilityResult.SUCCESS) {
                Log.debug("ABILITY: checkTarget failed");
                if (this.id == obj.getAutoAttackAbility()) {
                    obj.stopAutoAttack();
                }
                return result;
            }
            result = this.checkTargetSpecies(obj, target);
            if (result != AbilityResult.SUCCESS) {
                Log.debug("ABILITY: checkTargetSpecies failed");
                if (this.id == obj.getAutoAttackAbility()) {
                    obj.stopAutoAttack();
                }
                return result;
            }
            result = this.checkSpecificTarget(obj, target);
            if (result != AbilityResult.SUCCESS) {
                Log.debug("ABILITY: checkSpecificTarget failed");
                if (this.id == obj.getAutoAttackAbility()) {
                    obj.stopAutoAttack();
                }
                return result;
            }
            result = this.checkState(obj, target);
            if (result != AbilityResult.SUCCESS) {
                Log.debug("ABILITY: checkState failed");
                if (this.id == obj.getAutoAttackAbility()) {
                    obj.stopAutoAttack();
                }
                return result;
            }
            result = this.checkDeath(obj, target);
            if (result != AbilityResult.SUCCESS) {
                Log.debug("ABILITY: checkDeath failed");
                if (this.id == obj.getAutoAttackAbility()) {
                    obj.stopAutoAttack();
                }
                return result;
            }
            result = this.checkEffects(obj, target, state);
            if (result != AbilityResult.SUCCESS) {
                return result;
            }
        }
        result = this.checkHasAbility(obj, target);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        result = this.checkAbilityType(obj, target);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        result = this.checkTool(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        result = this.checkReagent(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        result = this.checkCost(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        result = this.checkVigor(obj, target, state);
        if (result != AbilityResult.SUCCESS) {
            return result;
        }
        if (state == ActivationState.INIT) {
            result = this.checkRange(obj, target, 1.0f);
        }
        else {
            result = this.checkRange(obj, target, 1.2f);
        }
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.checkAbility result=" + result);
        }
        return result;
    }
    
    protected AgisAbilityState generateState(final CombatInfo source, final CombatInfo target, final AgisItem item, final Point loc) {
        return new AgisAbilityState(this, source, target, item, loc);
    }
    
    public static void startAbility(final AgisAbility ability, final CombatInfo source, final CombatInfo target, final AgisItem item) {
        startAbility(ability, source, target, item, null);
    }
    
    public static void startAbility(final AgisAbility ability, final CombatInfo source, final CombatInfo target, final AgisItem item, final Point loc) {
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.startAbility ability=" + ability.getName() + " source=" + source + " target=" + target + " item=" + item + " loc=" + loc);
        }
        final AgisAbilityState state = ability.generateState(source, target, item, loc);
        Log.debug("AgisAbility.startAbility generated state");
        state.updateState();
    }
    
    public static void abortAbility(final AgisAbilityState state) {
        interruptAbility(state, AbilityResult.ABORT);
        final CombatInfo combatInfo = state.getSource();
        final Collection<Cooldown> cooldowns = state.getAbility().getCooldownMap().values();
        Cooldown.abortAllCooldowns(cooldowns, combatInfo);
    }
    
    public static void interruptAbility(final AgisAbilityState state, final AbilityResult reason) {
        if (Log.loggingDebug) {
            Log.debug("AgisAbility.interruptAbility: reason=" + reason + " state=" + state.getState());
        }
        if (state.getState() != ActivationState.INIT) {
            Engine.getExecutor().remove(state);
            if (state.getSource().getCurrentAction() == state) {
                state.getSource().setCurrentAction(null);
                EnginePlugin.setObjectPropertyNoResponse(state.getSource().getOwnerOid(), WorldManagerClient.NAMESPACE, "casting", (Serializable)"");
                EnginePlugin.setObjectPropertyNoResponse(state.getSource().getOwnerOid(), WorldManagerClient.NAMESPACE, "castingAffinity", (Serializable)"");
                EventMessageHelper.SendCombatEvent(state.getSourceOid(), state.getTargetOid(), "CastingCancelled", state.getAbility().getID(), -1, -1, -1);
            }
            if (state.getState() == ActivationState.COMPLETED) {
                return;
            }
        }
        state.getAbility().interrupt(state);
    }
    
    public AgisEffect getResultVal(final String result, final boolean caster) {
        Log.debug("RESULT: getting effect for result: " + result);
        return null;
    }
    
    public int getExperiencePerUse() {
        return this.exp_per_use;
    }
    
    public void setExperiencePerUse(final int xp) {
        this.exp_per_use = xp;
    }
    
    public void setLevelingMap(final LevelingMap lm) {
        this.lm = lm;
    }
    
    public LevelingMap getLevelingMap() {
        return this.lm;
    }
    
    public int getBaseExpThreshold() {
        return this.exp_max;
    }
    
    public void setBaseExpThreshold(final int max) {
        this.exp_max = max;
    }
    
    public int getMaxRank() {
        return this.rank_max;
    }
    
    public void setMaxRank(final int rank) {
        this.rank_max = rank;
    }
    
    public enum AbilityResult
    {
        SUCCESS("SUCCESS", 0), 
        OUT_OF_RANGE("OUT_OF_RANGE", 1), 
        INVALID_TARGET("INVALID_TARGET", 2), 
        NO_TARGET("NO_TARGET", 3), 
        DEAD("DEAD", 4), 
        UNKNOWN("UNKNOWN", 5), 
        PASSIVE("PASSIVE", 6), 
        NOT_READY("NOT_READY", 7), 
        TOO_CLOSE("TOO_CLOSE", 8), 
        OUT_OF_LOS("OUT_OF_LOS", 9), 
        INSUFFICIENT_ENERGY("INSUFFICIENT_ENERGY", 10), 
        BAD_ASPECT("BAD_ASPECT", 11), 
        MISSING_REAGENT("MISSING_REAGENT", 12), 
        MISSING_TOOL("MISSING_TOOL", 13), 
        BUSY("BUSY", 14), 
        ABORT("ABORT", 15), 
        INSUFFICIENT_VIGOR("INSUFFICIENT_VIGOR", 16), 
        NOT_IN_FRONT("NOT_IN_FRONT", 17), 
        NOT_BEHIND("NOT_BEHIND", 18), 
        NOT_BESIDE("NOT_BESIDE", 19), 
        EFFECT_MISSING("EFFECT_MISSING", 20), 
        WRONG_STANCE("WRONG_STANCE", 21), 
        MISSING_WEAPON("MISSING_WEAPON", 22), 
        INTERRUPTED("INTERRUPTED", 23);
        
        private AbilityResult(final String s, final int n) {
        }
    }
    
    public enum ActivationState
    {
        INIT("INIT", 0), 
        ACTIVATING("ACTIVATING", 1), 
        CHANNELLING("CHANNELLING", 2), 
        ACTIVATED("ACTIVATED", 3), 
        COMPLETED("COMPLETED", 4), 
        CANCELLED("CANCELLED", 5), 
        INTERRUPTED("INTERRUPTED", 6), 
        FAILED("FAILED", 7);
        
        private ActivationState(final String s, final int n) {
        }
    }
    
    public enum TargetSpecies
    {
        UNINIT("UNINIT", 0), 
        ANY("ANY", 1), 
        BEAST("BEAST", 2), 
        HUMANOID("HUMANOID", 3), 
        ELEMENTAL("ELEMENTAL", 4), 
        UNDEAD("UNDEAD", 5), 
        PLAYER("PLAYER", 6), 
        NONPLAYER("NONPLAYER", 7);
        
        private TargetSpecies(final String s, final int n) {
        }
    }
    
    public enum TargetType
    {
        UNINIT("UNINIT", 0), 
        NONE("NONE", 1), 
        ENEMY("ENEMY", 2), 
        FRIENDNOTSELF("FRIENDNOTSELF", 3), 
        FRIEND("FRIEND", 4), 
        GROUP("GROUP", 5), 
        SELF("SELF", 6), 
        ANY("ANY", 7), 
        AREA_ENEMY("AREA_ENEMY", 8), 
        AREA_FRIENDLY("AREA_FRIENDLY", 9), 
        OTHER("OTHER", 10);
        
        private TargetType(final String s, final int n) {
        }
    }
    
    public static class Entry
    {
        protected String abilityName;
        protected int abilityID;
        protected String icon;
        protected String category;
        
        public Entry() {
        }
        
        public Entry(final String abilityName, final String icon, final String category) {
            this.setAbilityName(abilityName);
            this.setIcon(icon);
            this.setCategory(category);
        }
        
        public Entry(final AgisAbility ability, final String category) {
            this.setAbilityName(ability.getName());
            this.setIcon(ability.getIcon());
            this.setCategory(category);
        }
        
        public String getAbilityName() {
            return this.abilityName;
        }
        
        public void setAbilityName(final String abilityName) {
            this.abilityName = abilityName;
        }
        
        public int getAbilityID() {
            return this.abilityID;
        }
        
        public void setAbilityID(final int abilityID) {
            this.abilityID = abilityID;
        }
        
        public String getIcon() {
            return this.icon;
        }
        
        public void setIcon(final String icon) {
            this.icon = icon;
        }
        
        public String getCategory() {
            return this.category;
        }
        
        public void setCategory(final String category) {
            this.category = category;
        }
        
        public AgisAbility getAbility() {
            return (AgisAbility)Agis.AbilityManager.get(this.abilityID);
        }
    }
}
