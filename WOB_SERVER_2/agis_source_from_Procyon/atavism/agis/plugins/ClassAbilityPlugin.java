// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.agis.objects.SkillData;
import atavism.agis.objects.SkillInfo;
import atavism.agis.util.ExtendedCombatMessages;
import java.util.List;
import atavism.agis.core.Agis;
import atavism.server.objects.Template;
import atavism.agis.objects.LevelingMap;
import atavism.agis.objects.VitalityStatDef;
import atavism.agis.objects.CoordinatedEffect;
import atavism.agis.util.EventMessageHelper;
import java.util.Iterator;
import java.util.HashSet;
import java.util.LinkedList;
import atavism.server.objects.Entity;
import atavism.msgsys.Message;
import atavism.server.objects.EntityManager;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.objects.CombatInfo;
import atavism.agis.core.AgisAbility;
import java.io.Serializable;
import atavism.agis.objects.AgisStat;
import atavism.agis.core.AgisSkill;
import atavism.agis.objects.ClassAbilityObject;
import atavism.agis.database.ContentDatabase;
import atavism.agis.database.CombatDatabase;
import atavism.server.engine.Hook;
import atavism.server.engine.Namespace;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.Log;
import atavism.agis.objects.CharacterTemplate;
import atavism.server.util.Logger;
import atavism.agis.objects.AgisStatDef;
import java.util.Map;
import atavism.server.engine.OID;
import java.util.ArrayList;
import atavism.agis.objects.ProfessionObject;
import java.util.HashMap;
import atavism.server.engine.EnginePlugin;

public class ClassAbilityPlugin extends EnginePlugin
{
    protected static HashMap<String, ProfessionObject> professions;
    HashMap<Integer, ArrayList<OID>> playerabilitykey;
    protected static Map<String, AgisStatDef> statDefMap;
    private static final Logger log;
    static HashMap<Integer, Integer> levelXpRequirements;
    static Map<String, CharacterTemplate> characterTemplates;
    public static final int SKILL_GAIN_RATE = 1;
    public static final int SKILL_MAX = 15;
    public static final int POINTS_PER_SKILL_LEVEL = 10;
    public static final int MAX_LEVEL = 10;
    public static final int MAX_SKILL_ABILITIES = 10;
    public static final String KILL_EXP_STAT = "kill_exp";
    public static final String EXPERIENCE_STAT = "experience";
    public static final String EXPERIENCE_MAX_STAT = "experience-max";
    public static final String LEVEL_STAT = "level";
    
    static {
        ClassAbilityPlugin.professions = new HashMap<String, ProfessionObject>();
        ClassAbilityPlugin.statDefMap = new HashMap<String, AgisStatDef>();
        log = new Logger("ClassAbility");
        ClassAbilityPlugin.levelXpRequirements = new HashMap<Integer, Integer>();
        ClassAbilityPlugin.characterTemplates = new HashMap<String, CharacterTemplate>();
    }
    
    public ClassAbilityPlugin() {
        super("ClassAbility");
        this.playerabilitykey = new HashMap<Integer, ArrayList<OID>>();
        this.setPluginType("ClassAbility");
    }
    
    public void onActivate() {
        if (Log.loggingDebug) {
            ClassAbilityPlugin.log.debug(String.valueOf(this.getName()) + " OnActivate Started");
        }
        super.onActivate();
        if (Log.loggingDebug) {
            ClassAbilityPlugin.log.debug(String.valueOf(this.getName()) + " base class onActivate ran");
        }
        this.registerHooks();
        if (Log.loggingDebug) {
            ClassAbilityPlugin.log.debug(String.valueOf(this.getName()) + " registered hooks");
        }
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(CombatClient.MSG_TYPE_SKILL_UPDATE);
        filter.addType(CombatClient.MSG_TYPE_ABILITY_UPDATE);
        filter.addType(CombatClient.MSG_TYPE_START_ABILITY);
        filter.addType(ClassAbilityClient.MSG_TYPE_HANDLE_EXP);
        filter.addType(CombatClient.MSG_TYPE_ALTER_EXP);
        filter.addType(CombatClient.MSG_TYPE_COMBAT_ABILITY_USED);
        filter.addType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_INCREASE);
        filter.addType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_DECREASE);
        filter.addType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_RESET);
        filter.addType(ClassAbilityClient.MSG_TYPE_LEVEL_CHANGE);
        filter.addType(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_ALTER_CURRENT);
        filter.addType(ClassAbilityClient.MSG_TYPE_PURCHASE_SKILL_POINT);
        filter.addType(ClassAbilityClient.MSG_TYPE_LEARN_ABILITY);
        filter.addType(CombatClient.MSG_TYPE_UPDATE_ACTIONBAR);
        Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
        final MessageTypeFilter filter2 = new MessageTypeFilter();
        filter2.addType(ClassAbilityClient.MSG_TYPE_COMBAT_GET_SKILL);
        filter2.addType(ClassAbilityClient.MSG_TYPE_COMBAT_GET_PLAYER_SKILL_LEVEL);
        Engine.getAgent().createSubscription((IFilter)filter2, (MessageCallback)this, 8);
        this.registerLoadHook(Namespace.CLASSABILITY, (EnginePlugin.LoadHook)new ClassAbilityLoadHook());
        this.registerSaveHook(Namespace.CLASSABILITY, (EnginePlugin.SaveHook)new ClassAbilitySaveHook());
        this.registerPluginNamespace(ClassAbilityClient.NAMESPACE, (EnginePlugin.GenerateSubObjectHook)new ClassAbilitySubObjectHook());
        if (Log.loggingDebug) {
            ClassAbilityPlugin.log.debug(String.valueOf(this.getName()) + " activated");
        }
        this.loadProgressionData();
    }
    
    public void registerHooks() {
        this.getHookManager().addHook(CombatClient.MSG_TYPE_ABILITY_UPDATE, (Hook)new ClassAbilityAddAbilityHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_ABILITY_PROGRESS, (Hook)new ClassAbilityAbilityProgressHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_START_ABILITY, (Hook)new ClassAbilityStartAbilityHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_HANDLE_EXP, (Hook)new ClassAbilityHandleXpHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_ALTER_EXP, (Hook)new AlterExpHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_COMBAT_ABILITY_USED, (Hook)new AbilityUsedHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_PURCHASE_SKILL_POINT, (Hook)new PurchaseSkillPointHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_INCREASE, (Hook)new SkillIncreaseHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_DECREASE, (Hook)new SkillDecreaseHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_RESET, (Hook)new SkillResetHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_LEVEL_CHANGE, (Hook)new LevelChangeHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_COMBAT_SKILL_ALTER_CURRENT, (Hook)new IncreaseSkillCurrentHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_COMBAT_GET_SKILL, (Hook)new SkillGetHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_COMBAT_GET_PLAYER_SKILL_LEVEL, (Hook)new GetSkillLevelHook());
        this.getHookManager().addHook(ClassAbilityClient.MSG_TYPE_LEARN_ABILITY, (Hook)new LearnAbilityHook());
        this.getHookManager().addHook(CombatClient.MSG_TYPE_UPDATE_ACTIONBAR, (Hook)new UpdateActionBarHook());
    }
    
    void loadProgressionData() {
        final CombatDatabase cDB = new CombatDatabase(false);
        ClassAbilityPlugin.levelXpRequirements = cDB.loadLevelExpRequirements();
        cDB.close();
        final ContentDatabase ctDB = new ContentDatabase(false);
        ClassAbilityPlugin.characterTemplates = ctDB.loadCharacterFactoryTemplates();
        ctDB.close();
    }
    
    public static void createStats(final ClassAbilityObject caobj, final AgisSkill skill, final Integer xp_use) {
        final AgisStat agisStat3;
        final AgisStat agisStat2;
        final AgisStat agisStat;
        final AgisStat tmp_exp = agisStat = (agisStat2 = (agisStat3 = new AgisStat(String.valueOf(skill.getName()) + "_exp")));
        final Integer value = 0;
        agisStat.base = value;
        agisStat2.current = value;
        agisStat3.min = value;
        tmp_exp.max = skill.getBaseExpThreshold();
        final AgisStat agisStat6;
        final AgisStat agisStat5;
        final AgisStat agisStat4;
        final AgisStat tmp_rank = agisStat4 = (agisStat5 = (agisStat6 = new AgisStat(String.valueOf(skill.getName()) + "_rank")));
        final Integer value2 = 0;
        agisStat4.base = value2;
        agisStat5.current = value2;
        agisStat6.min = value2;
        tmp_rank.max = skill.getMaxRank();
        caobj.setProperty(String.valueOf(skill.getName()) + "_exp", (Serializable)tmp_exp);
        caobj.setProperty(String.valueOf(skill.getName()) + "_rank", (Serializable)tmp_rank);
        caobj.setProperty(skill.getName(), (Serializable)xp_use);
    }
    
    public static void createStats(final ClassAbilityObject caobj, final AgisAbility ability, final Integer xp_use) {
        final AgisStat agisStat3;
        final AgisStat agisStat2;
        final AgisStat agisStat;
        final AgisStat tmp_exp = agisStat = (agisStat2 = (agisStat3 = new AgisStat(String.valueOf(ability.getName()) + "_exp")));
        final Integer value = 0;
        agisStat.base = value;
        agisStat2.current = value;
        agisStat3.min = value;
        tmp_exp.max = ability.getBaseExpThreshold();
        final AgisStat agisStat6;
        final AgisStat agisStat5;
        final AgisStat agisStat4;
        final AgisStat tmp_rank = agisStat4 = (agisStat5 = (agisStat6 = new AgisStat(String.valueOf(ability.getName()) + "_rank")));
        final Integer value2 = 0;
        agisStat4.base = value2;
        agisStat5.current = value2;
        agisStat6.min = value2;
        tmp_rank.max = ability.getMaxRank();
        caobj.setProperty(String.valueOf(ability.getName()) + "_exp", (Serializable)tmp_exp);
        caobj.setProperty(String.valueOf(ability.getName()) + "_rank", (Serializable)tmp_rank);
        caobj.setProperty(ability.getName(), (Serializable)xp_use);
    }
    
    public static void registerStat(final AgisStatDef stat) {
        registerStat(stat, new String[0]);
    }
    
    public static void registerStat(final AgisStatDef stat, final String... dependencies) {
        final String statName = stat.getName();
        if (!ClassAbilityPlugin.statDefMap.containsKey(statName)) {
            ClassAbilityPlugin.statDefMap.put(statName, stat);
            for (final String depName : dependencies) {
                final AgisStatDef depStat = ClassAbilityPlugin.statDefMap.get(depName);
                if (depStat != null) {
                    depStat.addDependent(stat);
                }
                else {
                    Log.error("no stat definition for dependency " + depName + " of stat " + statName);
                }
            }
        }
    }
    
    public static void registerProfession(final ProfessionObject profession) {
        ClassAbilityPlugin.log.debug("Registering Profession: " + profession);
        ClassAbilityPlugin.professions.put(profession.getName(), profession);
    }
    
    public static AgisStatDef lookupStatDef(final String name) {
        return ClassAbilityPlugin.statDefMap.get(name);
    }
    
    public static void sendSkillUpdate(final CombatInfo info) {
        final OID oid = info.getOwnerOid();
        final WorldManagerClient.TargetedExtensionMessage updateMsg = new WorldManagerClient.TargetedExtensionMessage(info.getOwnerOid());
        updateMsg.setExtensionType("ao.SKILL_UPDATE");
        final ClassAbilityObject caobj = (ClassAbilityObject)EntityManager.getEntityByNamespace(oid, ClassAbilityClient.NAMESPACE);
        if (caobj == null) {
            return;
        }
        final String playerclass = caobj.getPlayerClass();
        Engine.getAgent().sendBroadcast((Message)updateMsg);
    }
    
    public static ClassAbilityObject getClassAbilityObject(final OID oid) {
        ClassAbilityPlugin.log.debug("Checking the data for oid: " + oid);
        final Entity entity = EntityManager.getEntityByNamespace(oid, Namespace.CLASSABILITY);
        ClassAbilityPlugin.log.debug("What is this entity type? " + entity.getType() + " and Name? " + entity.getName() + " and OID: " + entity.getOid());
        return (ClassAbilityObject)entity;
    }
    
    public static void rewardExpForKill(final CombatInfo target, final OID attackerOid) {
        if (target == null) {
            Log.error("CombatAPI.handlePlayerXP : target is null");
            return;
        }
        final Integer xpPercent = (Integer)target.getProperty("kill_exp");
        final int targetLevel = target.statGetCurrentValue("level");
        ClassAbilityPlugin.log.debug("ANDREW - handle xp");
        if (xpPercent == null) {
            return;
        }
        float expPercent = 1.0f;
        final LinkedList<OID> handledOids = new LinkedList<OID>();
        final CombatInfo attacker = CombatPlugin.getCombatInfo(attackerOid);
        HashSet<OID> groupMembers = new HashSet<OID>();
        if (attacker.isGrouped()) {
            groupMembers = GroupClient.GetGroupMemberOIDs(attackerOid).memberOidSet;
            expPercent = expPercent / groupMembers.size() + 0.1f;
            ClassAbilityPlugin.log.debug("GROUP: xp hit, group memebers: " + groupMembers);
            for (final OID groupMemberOid : groupMembers) {
                if (!handledOids.contains(groupMemberOid)) {
                    final CombatInfo groupMember = CombatPlugin.getCombatInfo(groupMemberOid);
                    ClassAbilityPlugin.log.debug("GROUP: xp hit, giving exp to: " + groupMember.getOwnerOid());
                    final int attackerLevel = attacker.statGetCurrentValue("level");
                    final double xpval = 65.0 * Math.pow(1.1, targetLevel) * Math.pow(0.95, Math.abs(targetLevel - attackerLevel)) * expPercent;
                    final int xpValInt = (int)xpval;
                    giveExp(groupMemberOid, xpValInt);
                    handledOids.add(groupMemberOid);
                }
            }
        }
        else {
            ClassAbilityPlugin.log.debug("ANDREW - handle xp 2");
            final int attackerLevel2 = attacker.statGetCurrentValue("level");
            final double xpval2 = 65.0 * Math.pow(1.1, targetLevel) * Math.pow(0.95, Math.abs(targetLevel - attackerLevel2)) * expPercent;
            final int xpValInt2 = (int)xpval2;
            giveExp(attackerOid, xpValInt2);
        }
    }
    
    private static void giveExp(final OID oid, final int exp) {
        Log.debug("EXP: exp alter hit, oid: " + oid + "; xp amount: " + exp);
        final CombatInfo info = CombatPlugin.getCombatInfo(oid);
        int level = info.statGetCurrentValue("level");
        if (!ClassAbilityPlugin.levelXpRequirements.containsKey(level) || level >= 10 || exp == 0) {
            return;
        }
        int curXP = info.statGetCurrentValue("experience");
        int maxXP = ClassAbilityPlugin.levelXpRequirements.get(level);
        curXP += exp;
        WorldManagerClient.sendObjChatMsg(oid, 2, "You have received: " + exp + " experience points.");
        while (curXP > maxXP) {
            curXP -= maxXP;
            ++level;
            handleLevelUp(info, level);
            maxXP = ClassAbilityPlugin.levelXpRequirements.get(level);
        }
        info.statSetBaseValue("experience", curXP);
        Log.debug("EXP: experience stat is now: " + info.statGetBaseValue("experience") + ", " + curXP);
        final String abilityEvent = "CombatExpGained";
        EventMessageHelper.SendCombatEvent(info.getOwnerOid(), info.getOwnerOid(), abilityEvent, -1, -1, exp, -1);
    }
    
    public static void handleLevelUp(final CombatInfo player, final int newLevel) {
        Log.debug("ANDREW - level up. new level: " + newLevel);
        if (ClassAbilityPlugin.levelXpRequirements.containsKey(newLevel)) {
            player.statSetBaseValue("experience-max", ClassAbilityPlugin.levelXpRequirements.get(newLevel));
        }
        final String race = (String)EnginePlugin.getObjectProperty(player.getOwnerOid(), WorldManagerClient.NAMESPACE, "race");
        final String aspect = player.aspect();
        final CharacterTemplate tmpl = ClassAbilityPlugin.characterTemplates.get(String.valueOf(race) + aspect);
        player.statModifyBaseValue("dmg-base", 1);
        player.statSetBaseValue("level", newLevel);
        calculatePlayerStats(player, tmpl);
        player.statSetBaseValue(CombatPlugin.HEALTH_STAT, player.statGetCurrentValue(CombatPlugin.HEALTH_MAX_STAT));
        player.statSetBaseValue(CombatPlugin.MANA_STAT, player.statGetCurrentValue(CombatPlugin.MANA_MAX_STAT));
        ClassAbilityClient.levelChange(player.getOwnerOid(), newLevel);
        WorldManagerClient.sendObjChatMsg(player.getOwnerOid(), 2, "Congratulations, you have reached level " + newLevel + "!");
        final CoordinatedEffect cE = new CoordinatedEffect("LevelUpEffect");
        cE.sendSourceOid(true);
        cE.invoke(player.getOwnerOid(), player.getOwnerOid());
    }
    
    public static void calculatePlayerStats(final CombatInfo info, final CharacterTemplate tmpl) {
        final int level = info.statGetBaseValue("level");
        for (final String stat : tmpl.getStartingStats().keySet()) {
            if (CombatPlugin.statDefMap.get(stat) instanceof VitalityStatDef) {
                continue;
            }
            final int baseValue = tmpl.getStartingStats().get(stat).baseValue;
            final float levelIncrease = tmpl.getStartingStats().get(stat).levelIncrease;
            final float levelPercentIncrease = tmpl.getStartingStats().get(stat).levelPercentIncrease;
            float value = baseValue + (level - 1) * levelIncrease;
            value += value * ((level - 1) * (levelPercentIncrease / 100.0f));
            info.statSetBaseValue(stat, (int)value);
        }
        if (ClassAbilityPlugin.levelXpRequirements.containsKey(level)) {
            info.statSetBaseValue("experience-max", ClassAbilityPlugin.levelXpRequirements.get(level));
        }
    }
    
    public static void handleLevelingProfessionPlayer(final CombatInfo player, final int lvl) {
        String profession = null;
        try {
            profession = (String)EnginePlugin.getObjectProperty(player.getOid(), Namespace.WORLD_MANAGER, "class");
        }
        catch (Exception ex) {}
        if (profession == null) {
            return;
        }
        final ProfessionObject po = ClassAbilityPlugin.professions.get(profession);
        final LevelingMap lm = po.getLevelingMap();
        for (final String propname : player.getPropertyMap().keySet()) {
            if (!propname.equals("experience") && po.isBaseStat(propname) && player.getProperty(propname) instanceof AgisStat) {
                ClassAbilityPlugin.log.debug("Leveling up stat " + propname);
                final AgisStat stat = (AgisStat)player.getProperty(propname);
                ClassAbilityPlugin.log.debug("Leveling up " + propname + " : " + stat);
                final int checker = stat.base;
                ClassAbilityPlugin.log.debug("Base " + propname + " base stat: " + stat.base);
                final AgisStat agisStat = stat;
                agisStat.base += (int)(Object)new Float(stat.base * lm.getLevelPercentageModification(0)) + lm.getLevelFixedAmountModification(0);
                ClassAbilityPlugin.log.debug(String.valueOf(propname) + " base stat after global modification: " + stat.base);
                if (lm.hasLevelModification(lvl)) {
                    final AgisStat agisStat2 = stat;
                    agisStat2.base += (int)(Object)new Float(stat.base * lm.getLevelPercentageModification(lvl)) + lm.getLevelFixedAmountModification(lvl);
                    ClassAbilityPlugin.log.debug(String.valueOf(propname) + " base stat after level modification: " + stat.base);
                }
                if (po.hasStatLevelModification(propname, lvl)) {
                    final LevelingMap statlm = po.getStatsLevelingMap(propname);
                    final AgisStat agisStat3 = stat;
                    agisStat3.base += (int)(Object)new Float(stat.base * statlm.getLevelPercentageModification(lvl)) + statlm.getLevelFixedAmountModification(lvl);
                    ClassAbilityPlugin.log.debug(String.valueOf(propname) + " base stat after stat modification: " + stat.base);
                }
                ClassAbilityPlugin.log.debug(String.valueOf(propname) + " checking comparison: " + checker + " : " + stat.base);
                if (checker == stat.base) {
                    continue;
                }
                final AgisStat agisStat4 = stat;
                final AgisStat agisStat5 = stat;
                final Integer base = stat.base;
                agisStat5.max = base;
                agisStat4.current = base;
                player.setProperty(propname, (Serializable)stat);
                stat.setDirty(true);
                ClassAbilityPlugin.log.debug(String.valueOf(propname) + " updating base stat def");
                CombatPlugin.getBaseStatDef(propname).update(stat, player);
            }
        }
    }
    
    public static void handleSkillAbilityRanking(final ClassAbilityObject player, final int statid, final int lvl) {
    }
    
    public static int GetSkillPointCost(final int pointsPurchased) {
        return pointsPurchased * 100 + 100;
    }
    
    public static int GetStartingXpReq() {
        if (ClassAbilityPlugin.levelXpRequirements.containsKey(1)) {
            return ClassAbilityPlugin.levelXpRequirements.get(1);
        }
        return 0;
    }
    
    public static CharacterTemplate getCharacterTemplate(final String key) {
        return ClassAbilityPlugin.characterTemplates.get(key);
    }
    
    public class ClassAbilitySubObjectHook extends EnginePlugin.GenerateSubObjectHook
    {
        public ClassAbilitySubObjectHook() {
            super((EnginePlugin)ClassAbilityPlugin.this);
        }
        
        public EnginePlugin.SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
            if (Log.loggingDebug) {
                ClassAbilityPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            if (masterOid == null) {
                ClassAbilityPlugin.log.error("GenerateSubObjectHook: no master oid");
                return null;
            }
            if (Log.loggingDebug) {
                ClassAbilityPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            final Map<String, Serializable> props = (Map<String, Serializable>)template.getSubMap(ClassAbilityClient.NAMESPACE);
            if (props == null) {
                Log.warn("GenerateSubObjectHook: no props in ns " + ClassAbilityClient.NAMESPACE);
                return null;
            }
            final ClassAbilityObject tinfo = new ClassAbilityObject(masterOid);
            tinfo.setName(template.getName());
            Boolean persistent = (Boolean)template.get(Namespace.OBJECT_MANAGER, ":persistent");
            if (persistent == null) {
                persistent = false;
            }
            tinfo.setPersistenceFlag((boolean)persistent);
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                final String key = entry.getKey();
                final Serializable value = entry.getValue();
                if (!key.startsWith(":")) {
                    tinfo.setProperty(key, value);
                }
            }
            tinfo.setPlayerClass((String)tinfo.getProperty("class"));
            if (Log.loggingDebug) {
                ClassAbilityPlugin.log.debug("GenerateSubObjectHook: created entity " + tinfo);
            }
            EntityManager.registerEntityByNamespace((Entity)tinfo, ClassAbilityClient.NAMESPACE);
            return new EnginePlugin.SubObjData();
        }
    }
    
    public class ClassAbilityAddAbilityHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final CombatClient.AbilityUpdateMessage reqMsg = (CombatClient.AbilityUpdateMessage)msg;
            final List<CombatClient.AbilityUpdateMessage.Entry> skilllist = reqMsg.getAbilities();
            final OID oid = reqMsg.getSubject();
            final ClassAbilityObject caobj = (ClassAbilityObject)EntityManager.getEntityByNamespace(oid, ClassAbilityClient.NAMESPACE);
            if (caobj == null) {
                return true;
            }
            final String playerclass = caobj.getPlayerClass();
            for (final CombatClient.AbilityUpdateMessage.Entry e : skilllist) {
                ClassAbilityPlugin.log.debug("Adding ability to the player: " + oid + " ability: " + e.abilityID);
                if (Agis.AbilityManager.keySet().contains(e.abilityID)) {
                    if (playerclass == null) {
                        ClassAbilityPlugin.log.warn("They didn't define a class type for this player...");
                        return true;
                    }
                    if (!ClassAbilityPlugin.professions.get(playerclass).hasAbility(e.abilityID) || caobj.getProperty(String.valueOf(e.abilityID) + "_exp") != null) {
                        continue;
                    }
                    ClassAbilityPlugin.createStats(caobj, (AgisAbility)Agis.AbilityManager.get(e.abilityID), Integer.valueOf(ClassAbilityPlugin.professions.get(playerclass).getAbility(e.abilityID).getExperiencePerUse()));
                }
            }
            return true;
        }
    }
    
    class ClassAbilityStartAbilityHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final CombatClient.StartAbilityMessage abilityMsg = (CombatClient.StartAbilityMessage)msg;
            final OID oid = abilityMsg.getSubject();
            final int abilityID = abilityMsg.getAbilityID();
            ClassAbilityPlugin.log.debug("Processing Start Ability Message: " + oid + ", ability: " + abilityID);
            if (ClassAbilityPlugin.this.playerabilitykey.containsKey(abilityID)) {
                if (!ClassAbilityPlugin.this.playerabilitykey.get(abilityID).contains(oid)) {
                    ClassAbilityPlugin.this.playerabilitykey.get(abilityID).add(oid);
                }
            }
            else {
                final ArrayList<OID> a = new ArrayList<OID>();
                a.add(oid);
                ClassAbilityPlugin.this.playerabilitykey.put(abilityID, a);
            }
            ClassAbilityPlugin.log.debug("PlayerAbilityKey list for " + abilityID + " : [ " + ClassAbilityPlugin.this.playerabilitykey.get(abilityID).toString() + " ]");
            return true;
        }
    }
    
    class ClassAbilityAbilityProgressHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final CombatClient.AbilityProgressMessage abilityMsg = (CombatClient.AbilityProgressMessage)msg;
            final int abilityID = abilityMsg.getAbilityID();
            final String state = abilityMsg.getState();
            ClassAbilityPlugin.log.debug("Processing Progress Ability Message: " + state + ":" + AgisAbility.ActivationState.COMPLETED + ", ability: " + abilityID);
            if (state.equals(AgisAbility.ActivationState.COMPLETED.toString())) {
                final ArrayList<OID> oids = ClassAbilityPlugin.this.playerabilitykey.get(abilityID);
                if (oids == null) {
                    return true;
                }
                ClassAbilityPlugin.log.debug("Getting OIDS: [ " + oids.toString() + " ]");
                for (final OID oid : oids) {
                    final CombatInfo ci = (CombatInfo)EntityManager.getEntityByNamespace(oid, CombatClient.NAMESPACE);
                    if (ci == null) {
                        continue;
                    }
                    ClassAbilityPlugin.log.debug("Checking the current action state for " + ci.getName() + " ( " + ci.getOid() + " ) : " + ci.getCurrentAction());
                    if (ci.getCurrentAction() != null) {
                        continue;
                    }
                    final ClassAbilityObject caobj = (ClassAbilityObject)EntityManager.getEntityByNamespace(oid, ClassAbilityClient.NAMESPACE);
                    final AgisAbility ability = ClassAbilityPlugin.professions.get(caobj.getPlayerClass()).getAbility(abilityID);
                    if (ability == null) {
                        continue;
                    }
                    final AgisSkill skill = ability.getRequiredSkill();
                    if (skill != null && ClassAbilityPlugin.professions.get(caobj.getPlayerClass()).getSkillMap().get(skill.getName()) != null) {
                        caobj.updateBaseStat(skill.getID(), skill.getExperiencePerUse());
                    }
                    caobj.updateBaseStat(abilityID, ability.getExperiencePerUse());
                }
            }
            return true;
        }
    }
    
    class ClassAbilityLoadHook implements EnginePlugin.LoadHook
    {
        public void onLoad(final Entity e) {
        }
    }
    
    class ClassAbilitySaveHook implements EnginePlugin.SaveHook
    {
        public void onSave(final Entity e, final Namespace namespace) {
        }
    }
    
    public class ClassAbilityHandleXpHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage xpUpdateMsg = (WorldManagerClient.ExtensionMessage)msg;
            if (xpUpdateMsg.getProperty("attackers") != null) {
                final CombatInfo target = CombatPlugin.getCombatInfo(xpUpdateMsg.getSubject());
                final OID attacker = (OID)xpUpdateMsg.getProperty("attackers");
                ClassAbilityPlugin.log.debug("EXP: tagOwner 2: " + attacker);
                ClassAbilityPlugin.rewardExpForKill(target, attacker);
            }
            return true;
        }
    }
    
    class AlterExpHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final CombatClient.alterExpMessage QERMsg = (CombatClient.alterExpMessage)msg;
            final OID oid = QERMsg.getSubject();
            final int xpReward = QERMsg.getXpAmount();
            giveExp(oid, xpReward);
            return true;
        }
    }
    
    class PurchaseSkillPointHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage eMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID oid = eMsg.getSubject();
            Log.debug("SKILL: got purchase skill point for: " + oid);
            final CombatInfo info = CombatPlugin.getCombatInfo(oid);
            final SkillInfo sInfo = info.getCurrentSkillInfo();
            final int totalPoints = sInfo.getPointsSpent() + sInfo.getSkillPoints();
            final int cost = ClassAbilityPlugin.GetSkillPointCost(totalPoints);
            final int curXP = info.statGetCurrentValue("experience");
            if (curXP < cost) {
                Log.debug("SKILL: player: " + oid + " does not have enough experience to purchase a skill point");
                return true;
            }
            sInfo.setSkillPoints(sInfo.getSkillPoints() + 1);
            info.statSetBaseValue("experience", curXP - cost);
            Log.debug("SKILL: player: " + oid + " has purchased a skill point");
            ExtendedCombatMessages.sendSkills(oid, info.getCurrentSkillInfo());
            return true;
        }
    }
    
    class SkillIncreaseHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.skillIncreasedMessage EBMsg = (ClassAbilityClient.skillIncreasedMessage)msg;
            final OID oid = EBMsg.getSubject();
            final int skillType = (int)EBMsg.getProperty("skillType");
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            SkillInfo.increaseSkill(cInfo.getCurrentSkillInfo(), skillType, cInfo.aspect(), cInfo);
            ExtendedCombatMessages.sendSkills(oid, cInfo.getCurrentSkillInfo());
            return true;
        }
    }
    
    class SkillDecreaseHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.skillDecreasedMessage EBMsg = (ClassAbilityClient.skillDecreasedMessage)msg;
            final OID oid = EBMsg.getSubject();
            final int skillType = (int)EBMsg.getProperty("skillType");
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            SkillInfo.decreaseSkill(cInfo.getCurrentSkillInfo(), skillType, cInfo.aspect(), cInfo);
            ExtendedCombatMessages.sendSkills(oid, cInfo.getCurrentSkillInfo());
            return true;
        }
    }
    
    class AbilityUsedHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final CombatClient.abilityUsedMessage EBMsg = (CombatClient.abilityUsedMessage)msg;
            final OID oid = EBMsg.getSubject();
            final int skillType = (int)EBMsg.getProperty("skillType");
            Log.debug("SKILL: Ability used of skill type: " + skillType);
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            SkillInfo.skillUpAttempt(cInfo.getCurrentSkillInfo(), skillType, cInfo);
            ExtendedCombatMessages.sendSkills(oid, cInfo.getCurrentSkillInfo());
            return true;
        }
    }
    
    class SkillResetHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.skillResetMessage EBMsg = (ClassAbilityClient.skillResetMessage)msg;
            final OID oid = EBMsg.getSubject();
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            SkillInfo.resetSkills(cInfo.getCurrentSkillInfo(), cInfo);
            ExtendedCombatMessages.sendSkills(oid, cInfo.getCurrentSkillInfo());
            return true;
        }
    }
    
    class SkillGetHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.skillGetMessage EBMsg = (ClassAbilityClient.skillGetMessage)msg;
            final int templateNum = (int)EBMsg.getProperty("templateNum");
            Log.debug("ANDREW: Getting skill template");
            Engine.getAgent().sendObjectResponse(msg, Agis.SkillManager.get(templateNum));
            return true;
        }
    }
    
    class GetSkillLevelHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.GetPlayerSkillLevelMessage EBMsg = (ClassAbilityClient.GetPlayerSkillLevelMessage)msg;
            Log.debug("SKILL: got GetPlayerSkillLevelMessage");
            final OID oid = EBMsg.getSubject();
            final int skillType = EBMsg.getSkillType();
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            int skillLevel = -1;
            if (cInfo.getCurrentSkillInfo().getSkills().containsKey(skillType)) {
                skillLevel = cInfo.getCurrentSkillInfo().getSkills().get(skillType).getSkillLevel();
            }
            Engine.getAgent().sendIntegerResponse(msg, skillLevel);
            return true;
        }
    }
    
    class LevelChangeHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.levelChangeMessage EBMsg = (ClassAbilityClient.levelChangeMessage)msg;
            final OID oid = EBMsg.getSubject();
            final int newLevel = (int)EBMsg.getProperty("newLevel");
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            SkillInfo.levelChanged(cInfo.getCurrentSkillInfo(), cInfo, newLevel);
            ExtendedCombatMessages.sendSkills(oid, cInfo.getCurrentSkillInfo());
            return true;
        }
    }
    
    class IncreaseSkillCurrentHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.skillAlterCurrentMessage EBMsg = (ClassAbilityClient.skillAlterCurrentMessage)msg;
            final OID oid = EBMsg.getSubject();
            final int skillType = (int)EBMsg.getProperty("skillType");
            final int alterValue = (int)EBMsg.getProperty("alterValue");
            Log.debug("SKILL: Ability used of skill type: " + skillType);
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            SkillInfo.increaseSkillCurrent(cInfo.getCurrentSkillInfo(), skillType, alterValue, cInfo);
            ExtendedCombatMessages.sendSkills(oid, cInfo.getCurrentSkillInfo());
            return true;
        }
    }
    
    class LearnAbilityHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final ClassAbilityClient.LearnAbilityMessage laMsg = (ClassAbilityClient.LearnAbilityMessage)msg;
            final OID oid = laMsg.getSubject();
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            SkillInfo.learnAbility(cInfo, laMsg.getAbilityID());
            return true;
        }
    }
    
    class UpdateActionBarHook implements Hook
    {
        public boolean processMessage(final Message msg, final int flags) {
            final CombatClient.updateActionBarMessage UABMsg = (CombatClient.updateActionBarMessage)msg;
            final OID oid = UABMsg.getSubject();
            final int actionPosition = (int)UABMsg.getProperty("actionPosition");
            final String newAction = (String)UABMsg.getProperty("newAction");
            final CombatInfo cInfo = CombatPlugin.getCombatInfo(oid);
            final ArrayList<String> actions = cInfo.getCurrentActions();
            while (actions.size() <= actionPosition) {
                actions.add("");
            }
            actions.set(actionPosition, newAction);
            cInfo.setCurrentActions(actions);
            ExtendedCombatMessages.sendActions(oid, actions);
            return true;
        }
    }
}
