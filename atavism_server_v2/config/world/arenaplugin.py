from java.lang import *
from java.util import *
from java.util.concurrent import *
from atavism.agis import *
from atavism.agis.core import *
from atavism.agis.objects import *
from atavism.agis.util import *
from atavism.agis.plugins import *
from atavism.agis.behaviors import *
from atavism.msgsys import *
from atavism.server.math import *
from atavism.server.plugins import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from java.util import ArrayList
#import time
#False=0
#True=1

Engine.registerPlugin("atavism.agis.plugins.ArenaPlugin");

#class StartChecks(Runnable):
#    def run(self):
#        ArenaClient.startArenaCheck()

###
# Arena Template 0
###
'''arenaID = 0
arenaType = 0
duration = 300 # This is in seconds
victoryCondition = 1 # See notes at bottom of file
goalType = 0

# This variable needs to be match the length of all the ArrayLists created below
# Failure to do so will cause the entire arena system to crash
numTeams = 2
raceSpecific = False
teamRaces = ArrayList()
# You need to make a new ArrayList for each team
teamOne = ArrayList()
#teamOne.add("Smoo")
teamTwo = ArrayList()
#teamTwo.add("Ghost")
teamRaces.add(teamOne)
teamRaces.add(teamTwo)

# Variables used for PvC arenas
numRounds = 0
spawns = ArrayList()
round0Spawns = ArrayList()
spawns.add(round0Spawns)

worldFile = "arena 0 template"
arenaName = "Deathmatch 1v1"

teamNames = ArrayList()
teamNames.add("Dragons")
teamNames.add("Bears")

teamMinSizes = ArrayList()
teamMinSizes.add(1)
teamMinSizes.add(1)

teamSizes = ArrayList()
teamSizes.add(1)
teamSizes.add(1)

teamGoals = ArrayList()
teamGoals.add(-1)
teamGoals.add(-1) # Use -1 for no goal

template = ArenaTemplate(arenaID, arenaType, numTeams, teamNames, teamMinSizes, teamSizes, 
teamGoals, duration, victoryCondition, goalType, worldFile, arenaName, raceSpecific, teamRaces,
numRounds, spawns)
# Payments for victory and defeat
victoryPayment = HashMap()
victoryPayment.put(4, 10)
defeatPayment = HashMap()
defeatPayment.put(4, 4)
template.setVictoryPayment(victoryPayment)
template.setDefeatPayment(defeatPayment)
template.setVictoryExp(100)
template.setDefeatExp(0)
ArenaPlugin.addArenaTemplate(template)

###
# Arena Template 1
###
arenaID = 1
arenaType = 0
duration = 300 # This is in seconds
victoryCondition = 1 # See notes at bottom of file
goalType = 0

# This variable needs to be match the length of all the ArrayLists created below
# Failure to do so will cause the entire arena system to crash
numTeams = 2
raceSpecific = False
teamRaces = ArrayList()
# You need to make a new ArrayList for each team
teamOne = ArrayList()
#teamOne.add("Smoo")
teamTwo = ArrayList()
#teamTwo.add("Ghost")
teamRaces.add(teamOne)
teamRaces.add(teamTwo)

# Variables used for PvC arenas
numRounds = 0
spawns = ArrayList()
round0Spawns = ArrayList()
spawns.add(round0Spawns)

worldFile = "arena 0 template"
arenaName = "Deathmatch 2v2"
teamspawn = ArrayList()

teamNames = ArrayList()
teamNames.add("Dragons")
teamNames.add("Bears")

teamMinSizes = ArrayList()
teamMinSizes.add(2)
teamMinSizes.add(2)

teamSizes = ArrayList()
teamSizes.add(2)
teamSizes.add(2)

teamGoals = ArrayList()
teamGoals.add(-1)
teamGoals.add(-1) # Use -1 for no goal

template = ArenaTemplate(arenaID, arenaType, numTeams, teamNames, teamMinSizes, teamSizes, 
teamGoals, duration, victoryCondition, goalType, worldFile, arenaName, raceSpecific, teamRaces,
numRounds, spawns)
victoryPayment = HashMap()
victoryPayment.put(4, 10)
defeatPayment = HashMap()
defeatPayment.put(4, 4)
template.setVictoryPayment(victoryPayment)
template.setDefeatPayment(defeatPayment)
template.setVictoryExp(100)
template.setDefeatExp(0)
ArenaPlugin.addArenaTemplate(template)

###
# Arena Template 2 - Jawclamp Battle
###
arenaID = 2
arenaType = 1
duration = 300 # This is in seconds
victoryCondition = 1 # See notes at bottom of file
goalType = 0

# This variable needs to be match the length of all the ArrayLists created below
# Failure to do so will cause the entire arena system to crash
numTeams = 1
raceSpecific = False
teamRaces = ArrayList()
# You need to make a new ArrayList for each team
teamOne = ArrayList()
#teamTwo.add("Ghost")
teamRaces.add(teamOne)

# Variables used for PvC arenas
numRounds = 6
spawns = ArrayList()
round0Spawns = ArrayList()
round0Spawns.add(205)
spawns.add(round0Spawns)
round1Spawns = ArrayList()
round1Spawns.add(206)
spawns.add(round1Spawns)
round2Spawns = ArrayList()
round2Spawns.add(207)
spawns.add(round2Spawns)
round3Spawns = ArrayList()
round3Spawns.add(189)
spawns.add(round3Spawns)
round4Spawns = ArrayList()
round4Spawns.add(243)
spawns.add(round4Spawns)
round5Spawns = ArrayList()
round5Spawns.add(244)
spawns.add(round5Spawns)

worldFile = "arena 0 template"
arenaName = "Lone Survival"
teamspawn = ArrayList()

teamNames = ArrayList()
teamNames.add("Challengers")

teamMinSizes = ArrayList()
teamMinSizes.add(1)

teamSizes = ArrayList()
teamSizes.add(1)

teamGoals = ArrayList()
teamGoals.add(-1)

#template = ArenaTemplate(arenaID, arenaType, numTeams, teamNames, teamMinSizes, teamSizes, 
#teamGoals, duration, victoryCondition, goalType, worldFile, arenaName, raceSpecific, teamRaces,
#numRounds, spawns)
#ArenaPlugin.addArenaTemplate(template)

###
# Arena Template 3 - Red Walker Battle
###
arenaID = 3
arenaType = 1
duration = 300 # This is in seconds
victoryCondition = 1 # See notes at bottom of file
goalType = 0

# This variable needs to be match the length of all the ArrayLists created below
# Failure to do so will cause the entire arena system to crash
numTeams = 1
raceSpecific = False
teamRaces = ArrayList()
# You need to make a new ArrayList for each team
teamOne = ArrayList()
#teamTwo.add("Ghost")
teamRaces.add(teamOne)

# Variables used for PvC arenas
numRounds = 1
spawns = ArrayList()
round0Spawns = ArrayList()
round0Spawns.add(199)
round0Spawns.add(204)
spawns.add(round0Spawns)

worldFile = "arena 0 template"
arenaName = "Team Survival"
teamspawn = ArrayList()

teamNames = ArrayList()
teamNames.add("Challengers")

teamMinSizes = ArrayList()
teamMinSizes.add(1)

teamSizes = ArrayList()
teamSizes.add(1)

teamGoals = ArrayList()
teamGoals.add(-1)

#template = ArenaTemplate(arenaID, arenaType, numTeams, teamNames, teamMinSizes, teamSizes, 
#teamGoals, duration, victoryCondition, goalType, worldFile, arenaName, raceSpecific, teamRaces,
#numRounds, spawns)
#ArenaPlugin.addArenaTemplate(template)

###
# Arena Template 4 - Resource Battle 1
###
arenaID = 0
arenaType = 0
duration = 300 # This is in seconds
victoryCondition = 1 # See notes at bottom of file
goalType = 0

# This variable needs to be match the length of all the ArrayLists created below
# Failure to do so will cause the entire arena system to crash
numTeams = 2
raceSpecific = True
teamRaces = ArrayList()
# You need to make a new ArrayList for each team
teamOne = ArrayList()
teamOne.add("Human")
teamTwo = ArrayList()
teamTwo.add("Orc")
teamRaces.add(teamOne)
teamRaces.add(teamTwo)

# Variables used for PvC arenas
numRounds = 0
spawns = ArrayList()
round0Spawns = ArrayList()
spawns.add(round0Spawns)

worldFile = "arena 0 template"
arenaName = "Resource Battle"

teamNames = ArrayList()
teamNames.add("Legion")
teamNames.add("Outcast")

teamMinSizes = ArrayList()
teamMinSizes.add(1)
teamMinSizes.add(1)

teamSizes = ArrayList()
teamSizes.add(1)
teamSizes.add(1)

teamGoals = ArrayList()
teamGoals.add(-1)
teamGoals.add(-1) # Use -1 for no goal

template = ArenaTemplate(arenaID, arenaType, numTeams, teamNames, teamMinSizes, teamSizes, 
teamGoals, duration, victoryCondition, goalType, worldFile, arenaName, raceSpecific, teamRaces,
numRounds, spawns)
# Payments for victory and defeat
victoryPayment = HashMap()
victoryPayment.put(4, 10)
defeatPayment = HashMap()
defeatPayment.put(4, 4)
template.setVictoryPayment(victoryPayment)
template.setDefeatPayment(defeatPayment)
template.setVictoryExp(200)
template.setDefeatExp(50)
resourceGoals = HashMap()
resourceGoals.put("Gold", 300)

#ArenaPlugin.addArenaTemplate(template)

# Make sure all arena templates are added before this line
#Engine.getExecutor().schedule(StartChecks(), 15, TimeUnit.SECONDS)'''

'''VictoryCondition notes:
The victoryCondition varaible is used to determine which team wins when the duration of the 
arena has expired. 
If you want the team that is closest to their goal (percentage-wise) to win use: -1
Otherwise use the team number that will win if no team reached their goal. 

So if you want the second team to win at the end of the game if team one didn't reach their 
goal then use: 1
'''
