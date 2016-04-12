// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.List;
import java.util.Iterator;
import atavism.server.objects.AOObject;
import atavism.server.util.AORuntimeException;
import atavism.server.objects.Entity;
import atavism.server.engine.WorldNode;
import atavism.server.engine.PerceiverFilter;
import atavism.server.engine.BasicPerceiverFilter;
import atavism.server.engine.QuadTreeElement;
import atavism.server.engine.MobilePerceiver;
import atavism.server.objects.World;
import atavism.server.engine.WMWorldNode;
import atavism.server.util.Log;
import atavism.server.objects.ObjectTypes;
import java.io.Serializable;
import atavism.server.engine.OID;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.HashMap;
import atavism.agis.core.AgisAbilityState;
import atavism.agis.core.AgisSkill;
import java.util.Set;
import java.util.Collection;
import java.util.Map;

public class AgisMob extends AgisObject
{
    AgisEquipInfo equipInfo;
    protected Map<AgisEquipSlot, AgisItem> equipMap;
    AgisObject autoAttackTarget;
    long lastRecTime;
    long lastAttackTime;
    int strength;
    int intelligence;
    int ego;
    int presence;
    int comeliness;
    int dexterity;
    int constitution;
    int endurance;
    int currentEndurance;
    int pdBonus;
    int speedBonus;
    int resistPD;
    private int maxMoveSpeed;
    Collection<AgisQuest> questSet;
    Set<AgisQuest> concludeSet;
    private Map<AgisSkill, Integer> skillMap;
    Map<AgisMob, Map<AgisSkill, Integer>> dmgTable;
    private int totalDmgTaken;
    protected AgisAbilityState currentAbility;
    protected Set<AgisAbilityState> activeAbilities;
    private static final long serialVersionUID = 1L;
    
    public AgisMob() {
        this.equipInfo = null;
        this.equipMap = new HashMap<AgisEquipSlot, AgisItem>();
        this.autoAttackTarget = null;
        this.lastRecTime = 0L;
        this.lastAttackTime = 0L;
        this.strength = 0;
        this.intelligence = 0;
        this.ego = 0;
        this.presence = 0;
        this.comeliness = 0;
        this.dexterity = 0;
        this.constitution = 0;
        this.endurance = 0;
        this.currentEndurance = 0;
        this.pdBonus = 0;
        this.speedBonus = 0;
        this.resistPD = 0;
        this.maxMoveSpeed = 0;
        this.questSet = new LinkedList<AgisQuest>();
        this.concludeSet = new HashSet<AgisQuest>();
        this.skillMap = new HashMap<AgisSkill, Integer>();
        this.dmgTable = new HashMap<AgisMob, Map<AgisSkill, Integer>>();
        this.totalDmgTaken = 0;
        this.currentAbility = null;
        this.activeAbilities = new HashSet<AgisAbilityState>();
        this.init();
    }
    
    public AgisMob(final OID oid) {
        super(oid);
        this.equipInfo = null;
        this.equipMap = new HashMap<AgisEquipSlot, AgisItem>();
        this.autoAttackTarget = null;
        this.lastRecTime = 0L;
        this.lastAttackTime = 0L;
        this.strength = 0;
        this.intelligence = 0;
        this.ego = 0;
        this.presence = 0;
        this.comeliness = 0;
        this.dexterity = 0;
        this.constitution = 0;
        this.endurance = 0;
        this.currentEndurance = 0;
        this.pdBonus = 0;
        this.speedBonus = 0;
        this.resistPD = 0;
        this.maxMoveSpeed = 0;
        this.questSet = new LinkedList<AgisQuest>();
        this.concludeSet = new HashSet<AgisQuest>();
        this.skillMap = new HashMap<AgisSkill, Integer>();
        this.dmgTable = new HashMap<AgisMob, Map<AgisSkill, Integer>>();
        this.totalDmgTaken = 0;
        this.currentAbility = null;
        this.activeAbilities = new HashSet<AgisAbilityState>();
        this.init();
    }
    
    public AgisMob(final String name) {
        this.equipInfo = null;
        this.equipMap = new HashMap<AgisEquipSlot, AgisItem>();
        this.autoAttackTarget = null;
        this.lastRecTime = 0L;
        this.lastAttackTime = 0L;
        this.strength = 0;
        this.intelligence = 0;
        this.ego = 0;
        this.presence = 0;
        this.comeliness = 0;
        this.dexterity = 0;
        this.constitution = 0;
        this.endurance = 0;
        this.currentEndurance = 0;
        this.pdBonus = 0;
        this.speedBonus = 0;
        this.resistPD = 0;
        this.maxMoveSpeed = 0;
        this.questSet = new LinkedList<AgisQuest>();
        this.concludeSet = new HashSet<AgisQuest>();
        this.skillMap = new HashMap<AgisSkill, Integer>();
        this.dmgTable = new HashMap<AgisMob, Map<AgisSkill, Integer>>();
        this.totalDmgTaken = 0;
        this.currentAbility = null;
        this.activeAbilities = new HashSet<AgisAbilityState>();
        this.init();
        this.setName(name);
    }
    
    public AgisMob(final String name, final Map<String, Serializable> propMap) {
        this.equipInfo = null;
        this.equipMap = new HashMap<AgisEquipSlot, AgisItem>();
        this.autoAttackTarget = null;
        this.lastRecTime = 0L;
        this.lastAttackTime = 0L;
        this.strength = 0;
        this.intelligence = 0;
        this.ego = 0;
        this.presence = 0;
        this.comeliness = 0;
        this.dexterity = 0;
        this.constitution = 0;
        this.endurance = 0;
        this.currentEndurance = 0;
        this.pdBonus = 0;
        this.speedBonus = 0;
        this.resistPD = 0;
        this.maxMoveSpeed = 0;
        this.questSet = new LinkedList<AgisQuest>();
        this.concludeSet = new HashSet<AgisQuest>();
        this.skillMap = new HashMap<AgisSkill, Integer>();
        this.dmgTable = new HashMap<AgisMob, Map<AgisSkill, Integer>>();
        this.totalDmgTaken = 0;
        this.currentAbility = null;
        this.activeAbilities = new HashSet<AgisAbilityState>();
        this.setName(name);
        this.setPropertyMap((Map)propMap);
        this.init();
    }
    
    protected void init() {
        this.setType(ObjectTypes.mob);
        if (Log.loggingDebug) {
            Log.debug("AgisMob.init: name=" + this.getName() + ", perceiver=" + this.perceiver());
        }
        if (this.perceiver() == null) {
            if (Log.loggingDebug) {
                Log.debug("AgisMob.init: generating perceiver");
            }
            final MobilePerceiver<WMWorldNode> p = (MobilePerceiver<WMWorldNode>)new MobilePerceiver((QuadTreeElement)this.worldNode(), World.perceiverRadius);
            p.setFilter((PerceiverFilter)new BasicPerceiverFilter());
            p.setRadius(World.perceiverRadius);
            this.perceiver((MobilePerceiver)p);
            if (Log.loggingDebug) {
                Log.debug("AgisMob.init: generated perceiver=" + p + ", func=" + this.perceiver());
            }
        }
    }
    
    public void worldNode(final WorldNode worldNode) {
        super.worldNode(worldNode);
        final MobilePerceiver<WMWorldNode> p = (MobilePerceiver<WMWorldNode>)this.perceiver();
        if (p != null) {
            ((WMWorldNode)worldNode).setPerceiver((MobilePerceiver)p);
            p.setElement((QuadTreeElement)worldNode);
        }
    }
    
    public static AgisMob convert(final Entity obj) {
        if (!(obj instanceof AgisMob)) {
            throw new AORuntimeException("AgisMob.convert: obj is not a agismob: " + obj);
        }
        return (AgisMob)obj;
    }
    
    public AgisItem getItemBySlot(final AgisEquipSlot slot) {
        this.lock.lock();
        try {
            return this.equipMap.get(slot);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public AgisEquipSlot getSlotByItem(final AgisItem item) {
        this.lock.lock();
        try {
            for (final Map.Entry<AgisEquipSlot, AgisItem> entry : this.equipMap.entrySet()) {
                final AgisEquipSlot slot = entry.getKey();
                final AgisItem curItem = entry.getValue();
                if (AOObject.equals((Entity)item, (Entity)curItem)) {
                    return slot;
                }
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setEquipInfo(final AgisEquipInfo equipInfo) {
        this.equipInfo = equipInfo;
        if (Log.loggingDebug) {
            AgisMob.log.debug("setEquipInfo: mob=" + this + ", equipInfo=" + equipInfo);
        }
    }
    
    public AgisEquipInfo getEquipInfo() {
        return this.equipInfo;
    }
    
    public List<AgisEquipSlot> getEquippableSlots() {
        if (this.equipInfo == null) {
            throw new AORuntimeException("AgisMob.getEquippableSlots: equipinfo is null for mob " + this);
        }
        return this.equipInfo.getEquippableSlots();
    }
    
    public Set<AgisItem> getEquippedItems() {
        this.lock.lock();
        try {
            return new HashSet<AgisItem>(this.equipMap.values());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void putItemIntoSlot(final AgisEquipSlot slot, final AgisItem item) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                Log.debug("AgisObject: putting item " + item + " into equip slot " + slot.getName() + " for obj " + this);
            }
            if (!this.getEquippableSlots().contains(slot)) {
                AgisMob.log.error("mob " + this.getName() + ", item=" + item + ", mob does not have this slot " + slot);
                throw new AORuntimeException("mob does not have this slot");
            }
            this.equipMap.put(slot, item);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public AgisItem clearSlot(final AgisEquipSlot slot) {
        this.lock.lock();
        try {
            return this.equipMap.remove(slot);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setEquipMap(final Map<AgisEquipSlot, AgisItem> equipMap) {
        this.lock.lock();
        try {
            if (Log.loggingDebug) {
                AgisMob.log.debug("setEquipMap: thismob=" + this.getName() + ", new equipMap size=" + equipMap.size());
            }
            if (equipMap == null) {
                throw new RuntimeException("equipMap is null");
            }
            this.equipMap = new HashMap<AgisEquipSlot, AgisItem>(equipMap);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Map<AgisEquipSlot, AgisItem> getEquipMap() {
        this.lock.lock();
        try {
            return new HashMap<AgisEquipSlot, AgisItem>(this.equipMap);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int getOCV() {
        return Math.round(this.getDexterity() / 3.0f);
    }
    
    @Override
    public int getDCV() {
        return Math.round(this.getDexterity() / 3.0f);
    }
    
    public int getCV() {
        return Math.round(this.getDexterity() / 3.0f);
    }
    
    public AgisObject getAutoAttackTarget() {
        return this.autoAttackTarget;
    }
    
    public long getLastRecTime() {
        this.lock.lock();
        try {
            return this.lastRecTime;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setLastRecTime(final long time) {
        this.lock.lock();
        try {
            this.lastRecTime = time;
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public long getLastAttackTime() {
        this.lock.lock();
        try {
            return this.lastAttackTime;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setLastAttackTime() {
        this.lock.lock();
        try {
            this.lastAttackTime = System.currentTimeMillis();
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public long timeSinceLastAttack() {
        return System.currentTimeMillis() - this.getLastAttackTime();
    }
    
    public void setStrength(final int str) {
        this.strength = str;
    }
    
    public int getStrength() {
        return this.strength;
    }
    
    public void modifyStrength(final int delta) {
        this.lock.lock();
        try {
            final int strength = this.getStrength();
            this.setStrength(strength + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setIntelligence(final int intelligence) {
        this.intelligence = intelligence;
    }
    
    public int getIntelligence() {
        return this.intelligence;
    }
    
    public void modifyIntelligence(final int delta) {
        this.lock.lock();
        try {
            final int intelligence = this.getIntelligence();
            this.setIntelligence(intelligence + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setEgo(final int ego) {
        this.ego = ego;
    }
    
    public int getEgo() {
        return this.ego;
    }
    
    public void modifyEgo(final int delta) {
        this.lock.lock();
        try {
            final int ego = this.getEgo();
            this.setEgo(ego + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setPresence(final int pre) {
        this.presence = pre;
    }
    
    public int getPresence() {
        return this.presence;
    }
    
    public void modifyPresence(final int delta) {
        this.lock.lock();
        try {
            final int presence = this.getPresence();
            this.setPresence(presence + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setComeliness(final int comeliness) {
        this.comeliness = comeliness;
    }
    
    public int getComeliness() {
        return this.comeliness;
    }
    
    public void modifyComeliness(final int delta) {
        this.lock.lock();
        try {
            final int comeliness = this.getComeliness();
            this.setComeliness(comeliness + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setDexterity(final int dex) {
        this.dexterity = dex;
    }
    
    public int getDexterity() {
        return this.dexterity;
    }
    
    public void modifyDexterity(final int delta) {
        this.lock.lock();
        try {
            final int dexterity = this.getDexterity();
            this.setDexterity(dexterity + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int getBaseRecovery() {
        return (this.getStrength() + this.getConstitution()) / 5;
    }
    
    public int getConstitution() {
        return this.constitution;
    }
    
    public void setConstitution(final int con) {
        this.constitution = con;
    }
    
    public void modifyConstitution(final int delta) {
        this.lock.lock();
        try {
            final int constitution = this.getConstitution();
            this.setConstitution(constitution + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setEndurance(final int end) {
        this.endurance = end;
    }
    
    public int getEndurance() {
        return this.endurance;
    }
    
    public void modifyEndurance(final int delta) {
        this.lock.lock();
        try {
            final int endurace = this.getEndurance();
            this.setEndurance(endurace + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setCurrentEndurance(final int end) {
        this.currentEndurance = end;
    }
    
    public void modifyCurrentEndurance(final int delta) {
        this.lock.lock();
        try {
            final int end = this.getCurrentEndurance();
            this.setCurrentEndurance(end + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int getCurrentEndurance() {
        return this.currentEndurance;
    }
    
    public void setPDBonus(final int bonus) {
        this.pdBonus = bonus;
    }
    
    public int getPDBonus() {
        return this.pdBonus;
    }
    
    public void modifyPDBonus(final int delta) {
        this.lock.lock();
        try {
            final int pdBonus = this.getPDBonus();
            this.setPDBonus(pdBonus + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Override
    public int getPD() {
        final int rv = Math.round(this.getStrength() / 5.0f);
        this.lock.lock();
        try {
            return rv + this.getPDBonus();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setSpeedBonus(final int bonus) {
        this.speedBonus = bonus;
    }
    
    public int getSpeedBonus() {
        return this.speedBonus;
    }
    
    public void modifySpeedBonus(final int delta) {
        this.lock.lock();
        try {
            final int speedBonus = this.getSpeedBonus();
            this.setSpeedBonus(speedBonus + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int getSpeed() {
        final int rv = 10 + this.getDexterity();
        this.lock.lock();
        try {
            final int bonus = this.getSpeedBonus();
            return rv + bonus;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setResistantPD(final int pd) {
        this.resistPD = pd;
    }
    
    @Override
    public int getResistantPD() {
        return this.resistPD;
    }
    
    public void modifyResistantPD(final int delta) {
        this.lock.lock();
        try {
            final int resistantPD = this.getResistantPD();
            this.setResistantPD(resistantPD + delta);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void setMaxMoveSpeed(final int speed) {
        this.maxMoveSpeed = speed;
    }
    
    public int getMaxMoveSpeed() {
        return this.maxMoveSpeed;
    }
    
    public void addQuestPrototype(final AgisQuest quest) {
        this.lock.lock();
        try {
            this.questSet.add(quest);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void addConcludeQuest(final AgisQuest quest) {
        this.lock.lock();
        try {
            if (quest == null) {
                throw new RuntimeException("quest is null");
            }
            this.concludeSet.add(quest);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public LinkedList<AgisQuest> getQuestPrototypes() {
        this.lock.lock();
        try {
            return new LinkedList<AgisQuest>(this.questSet);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<AgisQuest> getConcludableQuests() {
        this.lock.lock();
        try {
            return new HashSet<AgisQuest>(this.concludeSet);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addSkill(final AgisSkill skill) {
        this.lock.lock();
        try {
            this.skillMap.put(skill, 0);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public boolean hasSkill(final AgisSkill skill) {
        this.lock.lock();
        try {
            return this.skillMap.containsKey(skill);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setSkillMap(final Map<AgisSkill, Integer> skillMap) {
        this.skillMap = new HashMap<AgisSkill, Integer>(skillMap);
    }
    
    public int getXPforSkill(final AgisSkill skill) {
        this.lock.lock();
        try {
            final Integer xp = this.skillMap.get(skill);
            return (xp == null) ? 0 : xp;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addSkillXP(final AgisSkill skill, final int newXp) {
        this.lock.lock();
        try {
            final Integer curXp = this.skillMap.get(skill);
            if (curXp == null) {
                AgisMob.log.warn("AgisMob.addSKillXp: mob " + this.getName() + " does not have skill " + skill.getName());
                return;
            }
            this.skillMap.put(skill, curXp + newXp);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Map<AgisSkill, Integer> getSkillMap() {
        this.lock.lock();
        try {
            return new HashMap<AgisSkill, Integer>(this.skillMap);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<AgisMob> getAttackers() {
        this.lock.lock();
        try {
            return new HashSet<AgisMob>(this.dmgTable.keySet());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<AgisSkill> getAttackerSkills(final AgisMob attacker) {
        this.lock.lock();
        try {
            final Map<AgisSkill, Integer> attackerDmgMap = this.dmgTable.get(attacker);
            return new HashSet<AgisSkill>(attackerDmgMap.keySet());
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int getDmgForSkill(final AgisMob attacker, final AgisSkill skill) {
        this.lock.lock();
        try {
            final Map<AgisSkill, Integer> attackerDmgMap = this.dmgTable.get(attacker);
            if (attackerDmgMap == null) {
                return 0;
            }
            final Integer dmg = attackerDmgMap.get(skill);
            if (dmg == null) {
                return 0;
            }
            return dmg;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addDamage(final AgisMob attacker, final AgisSkill skill, final int dmg) {
        this.lock.lock();
        try {
            Map<AgisSkill, Integer> attackerDmgMap = this.dmgTable.get(attacker);
            if (attackerDmgMap == null) {
                attackerDmgMap = new HashMap<AgisSkill, Integer>();
            }
            Integer curDmg = attackerDmgMap.get(skill);
            if (curDmg == null) {
                curDmg = 0;
            }
            attackerDmgMap.put(skill, new Integer(curDmg + dmg));
            this.dmgTable.put(attacker, attackerDmgMap);
            if (Log.loggingDebug) {
                AgisMob.log.debug("addDamage: attacker=" + attacker.getName() + ", skill=" + skill.getName() + ", prevDmg=" + curDmg + ", newDmg=" + dmg + ", newTotal=" + (curDmg + dmg));
            }
            this.totalDmgTaken += dmg;
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int getDamageTaken() {
        return this.totalDmgTaken;
    }
    
    public AgisAbilityState getCurrentAbility() {
        return this.currentAbility;
    }
    
    public void setCurrentAbility(final AgisAbilityState state) {
        this.currentAbility = state;
    }
    
    public Set<AgisAbilityState> getActiveAbilities() {
        return new HashSet<AgisAbilityState>(this.activeAbilities);
    }
    
    protected void setActiveAbilities(final Set<AgisAbilityState> abilities) {
        this.activeAbilities = new HashSet<AgisAbilityState>(abilities);
    }
    
    public void addActiveAbility(final AgisAbilityState state) {
        this.activeAbilities.add(state);
    }
    
    public void removeActiveAbility(final AgisAbilityState state) {
        this.activeAbilities.remove(state);
    }
}
