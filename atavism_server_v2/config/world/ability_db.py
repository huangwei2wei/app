from java.lang import *
from atavism.agis import *
from atavism.agis.objects import *
from atavism.agis.core import *
from atavism.agis.events import *
from atavism.agis.util import *
from atavism.agis.effects import *
from atavism.agis.abilities import *
from atavism.server.math import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.plugins.WorldManagerClient import TargetedExtensionMessage
True=1
False=0


##
## Below are basic examples of how we can extend the Agis combat system.
## Additional complexity could be added as necessary based on your world's requirements.            
##

'''class CombatEffect (DamageEffect):
    def apply(self, state):
        params = state.getParams()
        result = params.get("result")
        target = state.getObject()

        if (result == "miss"):
            ##Throw a missed msg to the client
            ##We could also just invoke a DamageMessage with a zero dmg value
            msg = TargetedExtensionMessage(target.getOid());
            msg.setProperty("ext_msg_subtype", "ao.COMBAT_ABILITY_MISSED");
            msg.setProperty("target", target.getOid());
            msg.setProperty("attacker", state.getCaster().getOid());			
            Engine.getAgent().sendBroadcast(msg);
        else:
            #Since we extend DamageEffect we could modify the damage here
            # and then send our own DamageMessage
            AgisEffect.apply(self, state)


class MeleeCombatAbility (CombatAbility):
    def resolveHit(self, state):
        params = HashMap()
        attacker = state.getObject()
        target = state.getTarget()

        #compute chance to hit by calculating an attack and defense stength
        attackerDexterity = int(attacker.statGetCurrentValue("dexterity"))
        attacherStrength = int(attacker.statGetCurrentValue("strength"))
        attackerLevel = int(attacker.statGetCurrentValue("level"))
        attackerAttackStrength = attackerDexterity + attacherStrength

        targetDexterity = int(target.statGetCurrentValue("dexterity"))
        targetStrength = int(target.statGetCurrentValue("strength"))
        targetLevel = int(target.statGetCurrentValue("level"))
        targetDefenseStrength = targetDexterity + targetStrength

        #Calc a hit bonus by comparing level difference and attacker's attack strength with target's defense strength
        hitBonus = (attackerAttackStrength + (attackerLevel*5)) - (targetDefenseStrength + (targetLevel*5))

        dice = Dice(100)
        roll = dice.roll(1)

        if roll + hitBonus > 80:
            params.put("result","hit")
        else:
            params.put("result","miss")

        return params

class RangedCombatAbility (CombatAbility):
    def resolveHit(self, state):
        params = HashMap()
        attacker = state.getObject()
        target = state.getTarget()

        #Calculate hit chance based on dexterity and strength
        # We could also create ranged specific attack stats instead so they wouldnt need to be calculated on the fly.
        attackerDexterity = int(attacker.statGetCurrentValue("dexterity"))
        attacherStrength = int(attacker.statGetCurrentValue("strength"))
        attackerLevel = int(attacker.statGetCurrentValue("level"))
        #In this case dexterity is more important than strength in determining attack strength
        attackerAttackStrength = attackerDexterity * 2 + attacherStrength

        #When trying to dodge or block a ranged attack dexterity may be your only hope
        targetDefenseStrength = int(target.statGetCurrentValue("dexterity")) * 2
        targetLevel = int(target.statGetCurrentValue("level"))

        hitBonus = (attackerAttackStrength + (attackerLevel*5)) - (targetDefenseStrength + (targetLevel*5))

        dice = Dice(100)
        roll = dice.roll(1)

        if roll + hitBonus > 80:
            params.put("result","hit")
        else:
            params.put("result","miss")

        return params'''


##***********************************************


'''effect = CombatEffect(-500, "default weaponskill effect")
effect.setMinInstantDamage(5)
effect.setMaxInstantDamage(15)
effect.setDamageType("Physical")
Agis.EffectManager.register(effect.getID(), effect)'''


# Define Survival Abilities
ability = EffectAbility("Lesser Bandages")
ability.setTargetType(AgisAbility.TargetType.SELF)
ability.setMaxRange(1000)
#ability.addActivationEffect(Agis.EffectManager.get("minor heal effect"))
#ability.addCoordEffect(AgisAbility.ActivationState.COMPLETED, healCastingEffect)
ability.addCooldown(Cooldown("FIRSTAID", 15000));
ability.setActivationCost(5)
ability.setCostProperty("stamina")
ability.setActivationTime(5000)
#ability.setRequiredSkill(Agis.SkillManager.get("First Aid"), 1)
ability.setIcon("Interface\FantasyWorldIcons\SPELL_heal_A")
ability.setExperiencePerUse(1);
ability.setBaseExpThreshold(10);
ability.setMaxRank(3);
# define a leveling map so that one can control how it gains levels
lblm = LevelingMap()
lblm.setAllLevelModification(0.5, 0)
ability.setLevelingMap(lblm)
#Agis.AbilityManager.register(ability.getID(), ability)

