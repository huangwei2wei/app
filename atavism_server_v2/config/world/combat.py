from java.lang import *
from java.util import *
from atavism.agis import *
from atavism.agis.core import *
from atavism.agis.objects import *
from atavism.agis.util import *
from atavism.agis.plugins import *
from atavism.server.math import *
from atavism.server.plugins import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from math import sqrt

False=0
True=1

# Derived stats do not have a min/max since they are based off of other values
class HealthMaxStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 999999999
        stat.min = 0
        endurance = info.statGetCurrentValue("endurance")
        hpbase = info.statGetCurrentValue("health-base")
        # endurance is a % multiplier for max-health which also uses a square root
        if endurance == 0:
            stat.base = hpbase
        else:
            calc = float(float(endurance) / float(100))
            Log.debug("ANDREW - healthmax calc: " + str(calc))
            calc = hpbase * calc
            Log.debug("ANDREW - healthmax calc2: " + str(calc))
            stat.base = int(calc)
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

'''class HealthStat (AgisStatDef):
    def update(self, stat, info):
        healthMax = info.statGetCurrentValue("health-max")
        stat.max = healthMax
        stat.min = 0
        if(info.dead()):
            stat.base = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

    def notifyFlags(self, stat, info, oldFlags, newFlags):
        if (info.dead()):
            CombatPlugin.stopRegen(info, stat.getName())
            return
        
        if ((oldFlags ^ newFlags) & AgisStatDef.AGIS_STAT_FLAG_MAX):
            if (newFlags & AgisStatDef.AGIS_STAT_FLAG_MAX):
                CombatPlugin.stopRegen(info, stat.getName())
            else:
                regenEffect = Agis.EffectManager.get(CombatPlugin.HEALTH_REGEN_EFFECT)
                CombatPlugin.startRegen(info, stat.getName(), regenEffect)

        if (((oldFlags ^ newFlags) & AgisStatDef.AGIS_STAT_FLAG_MIN)
            and (newFlags & AgisStatDef.AGIS_STAT_FLAG_MIN)):
            CombatPlugin.handleDeath(info)
            #duelID = EnginePlugin.getObjectProperty(info.getOwnerOid(), Namespace.WORLD_MANAGER, "duelID")
            #if duelID is not None:
            #    if duelID != -1:
            #        ArenaClient.duelDefeat(info.getOwnerOid())
            CombatPlugin.stopRegen(info, stat.getName())
            
            EnginePlugin.setObjectPropertyNoResponse(info.getOwnerOid(), Namespace.WORLD_MANAGER, 
                                                 WorldManagerClient.WORLD_PROP_NOMOVE, Boolean(True))
            EnginePlugin.setObjectPropertyNoResponse(info.getOwnerOid(), Namespace.WORLD_MANAGER, 
                                                 WorldManagerClient.WORLD_PROP_NOTURN, Boolean(True))'''

class ManaMaxStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 999999999
        stat.min = 0
        manabase = info.statGetCurrentValue("mana-base")
        willpower = info.statGetCurrentValue("willpower")
        # willpower is a % multiplier for max-mana
        if willpower == 0 or manabase == 0:
            stat.base = manabase
        else:
            stat.base = manabase * (willpower / 100)
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

'''class ManaStat (AgisStatDef):
    def update(self, stat, info):
        # Mana is derived from Intelligence at a rate of 2 X the value. 

        ManaMax = info.statGetCurrentValue("mana-max")           
        stat.max = int(ManaMax)
        stat.min = 0
        if(info.dead()):
            stat.base = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)
            
    def notifyFlags(self, stat, info, oldFlags, newFlags):
        if (info.dead()):
            CombatPlugin.stopRegen(info, stat.getName())
            return

        if ((oldFlags ^ newFlags) & AgisStatDef.AGIS_STAT_FLAG_MAX):
            if (newFlags & AgisStatDef.AGIS_STAT_FLAG_MAX):
                CombatPlugin.stopRegen(info, stat.getName())
            else:
                regenEffect = Agis.EffectManager.get(CombatPlugin.ENERGY_REGEN_EFFECT)
                CombatPlugin.startRegen(info, stat.getName(), regenEffect)'''


class BreathStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 100
        stat.min = 0
        if(info.dead()):
            stat.base = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

    def notifyFlags(self, stat, info, oldFlags, newFlags):
        if (info.dead()):
            return

        if (((oldFlags ^ newFlags) & AgisStatDef.AGIS_STAT_FLAG_MIN)
            and (newFlags & AgisStatDef.AGIS_STAT_FLAG_MIN)):
            # Apply some kind of damage effect? or just kill them?
            CombatPlugin.handleDeath(info)

class FatigueStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 100
        stat.min = 0
        if(info.dead()):
            stat.base = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

    def notifyFlags(self, stat, info, oldFlags, newFlags):
        if (info.dead()):
            return

        if (((oldFlags ^ newFlags) & AgisStatDef.AGIS_STAT_FLAG_MIN)
            and (newFlags & AgisStatDef.AGIS_STAT_FLAG_MIN)):
            # Apply some kind of damage effect? or just kill them?
            CombatPlugin.handleDeath(info)

class ExperienceStat (AgisStatDef):
    def update(self, stat, info):
        xpMax = info.statGetCurrentValue("experience-max")
        stat.max = xpMax * 2
        stat.min = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

class ExpMaxStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 5000000
        stat.min = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

class LevelStat (AgisStatDef):
    def update(self, stat, info):
        expstat = info.getProperty("experience")
        expmaxstat = info.getProperty("experience-max")
        
        if expstat >= expmaxstat and stat.max != stat.base: 
            if stat.base == None:
                stat.base = stat.current = 0
            
            # they have gained a level
            stat.base = stat.current = stat.base + 1
            
            # NOTE: This line determines what the next level should be. Put your level incrementation
            #       logic here. Right now it just takes the current level and multiplies by 10 and sets
            #       that as the value for the max experience to be gained to level again.
            #
            #       ie: if current level = 1, then it will take 1 * 100, or 100 experience points to level
            #           if the current level is 2, then 200, etc.
            expstat = expstat - expmaxstat
            expmaxstat = stat.base * 300
                        
            expstat.setDirty(True)
            expmaxstat.setDirty(True)
            stat.setDirty(True)
            
            ClassAbilityClient.sendXPUpdate(info.getOid(), stat.getName(), stat.current)
            ClassAbilityPlugin.handleLevelingPlayer(info, stat.base)
            
        
        AgisStatDef.update(self, stat, info)

class VigorStat (AgisStatDef):
    def update(self, stat, info):
        vigorMax = info.statGetCurrentValue("vigor-max")
        stat.max = vigorMax
        stat.min = 0
        if(info.dead()):
            stat.base = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

class DmgBaseStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 2000
        stat.min = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

class HealthBaseStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 500000
        stat.min = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

class ManaBaseStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 500
        stat.min = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)
            
class LevelBaseStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 50
        stat.min = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

        
# Temp base stat Defs
'''class BaseStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 300
        stat.min = 10
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)'''

class DmgModifierStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 100
        stat.min = -100
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)

'''class AttackSpeedStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 10000
        stat.min = 500
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)'''

'''class ResistanceStat (AgisStatDef):
    def update(self, stat, info):
        stat.max = 100000
        stat.min = 0
        stat.setDirty(True)
        AgisStatDef.update(self, stat, info)'''

# Register                Class               Stat                Dependencies
#CombatPlugin.registerStat(ArmorStat("armor"))
CombatPlugin.registerStat(HealthBaseStat("health-base"))
CombatPlugin.registerStat(ManaBaseStat("mana-base"))
CombatPlugin.registerStat(DmgBaseStat("dmg-base"))
#CombatPlugin.registerStat(AgisStatDef("dmg-base"))
CombatPlugin.registerStat(DmgModifierStat("dmg-dealt-mod"))
CombatPlugin.registerStat(DmgModifierStat("dmg-taken-mod"))
#CombatPlugin.registerStat(AttackSpeedStat("attack_speed"))
#CombatPlugin.registerStat(HealthMaxStat("health-max"), True, [CombatPlugin.HEALTH_MOD_STAT, "health-base"])
#CombatPlugin.registerStat(ManaMaxStat("mana-max"), True, [CombatPlugin.MANA_MOD_STAT, "mana-base"])
#CombatPlugin.registerStat(HealthStat("health"), True, ["health-max"])
#CombatPlugin.registerStat(ManaStat("mana"), True, ["mana-max"])
CombatPlugin.registerStat(ExpMaxStat("experience-max"))
CombatPlugin.registerStat(ExperienceStat("experience"), False, ["experience-max"])
#CombatPlugin.registerStat(LevelStat("level"), ["experience", "experience-max"])
CombatPlugin.registerStat(LevelBaseStat("level"), True)

# Skills and Abilities also can gain experience, so we need to setup the catch for their Ranks

# SKILLS
'''ClassAbilityPlugin.registerStat(AgisStatDef("Sword_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Axe_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Dagger_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Thrown Weapons_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("First Aid_exp"))
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Sword_rank"), [ "Sword_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Axe_rank"), [ "Axe_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Dagger_rank"), [ "Dagger_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Thrown Weapons_rank"), [ "Thrown Weapons_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("First Aid_rank"), [ "First Aid_exp" ])

# ABILITIES
ClassAbilityPlugin.registerStat(AgisStatDef("Wounding Thrust_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Whirling Dervish_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Cleave_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Pierce_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Flying Dagger_exp"))
ClassAbilityPlugin.registerStat(AgisStatDef("Lesser Bandages_exp"))
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Wounding Thrust_rank"), [ "Wounding Thrust_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Whirling Dervish_rank"), [ "Whirling Dervish_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Cleave_rank"), [ "Cleave_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Pierce_rank"), [ "Pierce_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Flying Dagger_rank"), [ "Flying Dagger_exp" ])
ClassAbilityPlugin.registerStat(ClassAbilityRankStat("Lesser Bandages_rank"), [ "Lesser Bandages_exp" ])'''

Engine.registerPlugin("atavism.agis.plugins.CombatPlugin");
