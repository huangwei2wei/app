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
from atavism.agis.plugins import *
True=1
False=0


# Define Profession Skills

# create a profession object
profession = ProfessionObject("Soldier")
# add skills to the profession
#profession.addSkill("Sword")
#profession.addSkill("Axe")
#profession.addSkill("Dagger")
#profession.addSkill("First Aid")
#profession.addSkill("Thrown Weapons")
#profession.addSkill("First Aid")
# add abilities to the profession
#profession.addAbility("Wounding Thrust")
#profession.addAbility("Whirling Dervish")
#profession.addAbility("Cleave")
#profession.addAbility("Pierce")
#profession.addAbility("Lesser Bandages")
#profession.addAbility("Flying Dagger")
#profession.addAbility("Lesser Bandages")

# define what are the base stats for modification by this profession
profession.addBaseStat("Strength")
profession.addBaseStat("Dexterity")

# create a level increment map for the Soldier Profession
soldierlvlmap = LevelingMap()
soldierlvlmap.setAllLevelModification(0.0, 2)
soldierlvlmap.setLevelPercentageModification(2, 0.01)
soldierlvlmap.setLevelFixedAmountModification(3, 10)
# apply leveling map to soldier profession
profession.applyLevelingMap(soldierlvlmap)

# create a stat specific leveling map for strength
strengthlvlmap = LevelingMap()
strengthlvlmap.setLevelPercentageModification(2, 0.01)
strengthlvlmap.setLevelPercentageModification(3, 0.2)
# apply leveling map to the soldier's strength stat
profession.applyStatsLevelingMap("Strength", strengthlvlmap)

# create a stat specific leveling map for dexterity
dexteritylvlmap = LevelingMap()
dexteritylvlmap.setLevelFixedAmountModification(2, 10)
dexteritylvlmap.setLevelFixedAmountModification(3, 20)
# apply leveling map to the soldier's dexterity stat
profession.applyStatsLevelingMap("Dexterity", dexteritylvlmap)

# Now register that profession with the classabilityplugin
ClassAbilityPlugin.registerProfession(profession)

# create a profession object
profession = ProfessionObject("Warrior")
# add skills to the profession
#profession.addSkill("Sword")
#profession.addSkill("Axe")
#profession.addSkill("Dagger")
#profession.addSkill("First Aid")
#profession.addSkill("Thrown Weapons")
#profession.addSkill("First Aid")
# add abilities to the profession
#profession.addAbility("Wounding Thrust")
#profession.addAbility("Whirling Dervish")
#profession.addAbility("Cleave")
#profession.addAbility("Pierce")
#profession.addAbility("Lesser Bandages")
#profession.addAbility("Flying Dagger")
#profession.addAbility("Lesser Bandages")

# define what are the base stats for modification by this profession
profession.addBaseStat("Strength")
profession.addBaseStat("Dexterity")

# create a level increment map for the Soldier Profession
soldierlvlmap = LevelingMap()
soldierlvlmap.setAllLevelModification(0.0, 2)
soldierlvlmap.setLevelPercentageModification(2, 0.01)
soldierlvlmap.setLevelFixedAmountModification(3, 10)
# apply leveling map to soldier profession
profession.applyLevelingMap(soldierlvlmap)

# create a stat specific leveling map for strength
strengthlvlmap = LevelingMap()
strengthlvlmap.setLevelPercentageModification(2, 0.01)
strengthlvlmap.setLevelPercentageModification(3, 0.2)
# apply leveling map to the soldier's strength stat
profession.applyStatsLevelingMap("Strength", strengthlvlmap)

# create a stat specific leveling map for dexterity
dexteritylvlmap = LevelingMap()
dexteritylvlmap.setLevelFixedAmountModification(2, 10)
dexteritylvlmap.setLevelFixedAmountModification(3, 20)
# apply leveling map to the soldier's dexterity stat
profession.applyStatsLevelingMap("Dexterity", dexteritylvlmap)

# Now register that profession with the classabilityplugin
ClassAbilityPlugin.registerProfession(profession)
