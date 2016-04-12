from atavism.agis import *
from atavism.agis.objects import *
from atavism.agis.core import *
from atavism.agis.events import *
from atavism.agis.util import *
from atavism.agis.plugins import *
from atavism.server.plugins import *
from atavism.server.math import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from java.lang import *
from java.util import LinkedList
# SQL imports for name checking code
from com.mysql.jdbc import *
from java.sql import Connection
from java.sql import DriverManager
from java.sql import SQLException

# Initialize JDBC driver
Class.forName("com.mysql.jdbc.Driver").newInstance() 

meshInfo = { "char_smoo.mesh" : [[ "Body-mesh.0", "Pac._1_-_Default" ]] }

# Add clothing meshes.  These should really be done as inventory items,
# but as a quick fix, we put it in the base display context of the object.
meshInfo["char_smoo.mesh"].extend([])

# set up the default display context
displayContext = DisplayContext("char_smoo.mesh", True)
for entry in meshInfo["char_smoo.mesh"]:
    displayContext.addSubmesh(DisplayContext.Submesh(entry[0], entry[1]))

# default player template
player = Template("DefaultPlayer", -1, ObjectManagerPlugin.MOB_TEMPLATE)

player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, displayContext)
player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_OBJECT_TYPE, ObjectTypes.player)
player.put(WorldManagerClient.NAMESPACE, WorldManagerClient.TEMPL_RUN_THRESHOLD, Float(5000))
player.put(WorldManagerClient.NAMESPACE, 
            WorldManagerClient.TEMPL_PERCEPTION_RADIUS, Integer(75000))

player.put(CombatClient.NAMESPACE, "combat.userflag", Boolean(True))
player.put(CombatClient.NAMESPACE, CombatInfo.COMBAT_PROP_DEADSTATE, Boolean(False))
player.put(CombatClient.NAMESPACE, CombatInfo.COMBAT_PROP_REGEN_EFFECT, "regen effect")
player.put(CombatClient.NAMESPACE, "health-max", AgisStat("health-max", 100))
player.put(CombatClient.NAMESPACE, "health", AgisStat("health", 100))
player.put(CombatClient.NAMESPACE, "mana-max", AgisStat("mana-max", 100))
player.put(CombatClient.NAMESPACE, "mana", AgisStat("mana", 100))
player.put(CombatClient.NAMESPACE, CombatInfo.COMBAT_PROP_AUTOATTACK_ABILITY, CombatPlugin.PLAYER_ATTACK_ABILITY)

ObjectManagerClient.registerTemplate(player)

# character factory
class SampleFactory (CharacterFactory):
    def createCharacter(self, worldName, uid, properties):
        ot = Template()

        name = properties.get("characterName")
                   
        name = name.lower()
        name = name.title()
        
        meshName = None
        gender = properties.get("gender")
        race = properties.get("race")
        skinColouring = properties.get("skinColour")
        meshName = properties.get("prefab")
        
        umaData = HashMap()
        umaData["Gender"] = gender
        umaData["Race"] = race
        umaData["skinColour"] = properties.get("skinColour")
        umaData["hairColour"] = properties.get("hairColour")
        umaData["hairStyle"] = properties.get("hairStyle")
        '''umaData["facialHairStyle"] = properties.get("facialHairStyle")
        umaData["hairSize"] = float(properties.get("hairSize"))
        umaData["height"] = float(properties.get("height"))
        umaData["upperMuscle"] = float(properties.get("upperMuscle"))
        umaData["upperWeight"] = float(properties.get("upperWeight"))
        umaData["lowerMuscle"] = float(properties.get("lowerMuscle"))
        umaData["lowerWeight"] = float(properties.get("lowerWeight"))
        umaData["breastSize"] = float(properties.get("breastSize"))
        umaData["foreheadSize"] = float(properties.get("foreheadSize"))
        umaData["foreheadPosition"] = float(properties.get("foreheadPosition"))
        umaData["noseSize"] = float(properties.get("noseSize"))
        umaData["nosePosition"] = float(properties.get("nosePosition"))
        umaData["noseCurve"] = float(properties.get("noseCurve"))
        umaData["noseWidth"] = float(properties.get("noseWidth"))
        umaData["noseFlatten"] = float(properties.get("noseFlatten"))
        umaData["earSize"] = float(properties.get("earSize"))
        umaData["earPosition"] = float(properties.get("earPosition"))
        umaData["cheekSize"] = float(properties.get("cheekSize"))
        umaData["cheekPosition"] = float(properties.get("cheekPosition"))
        umaData["lowerCheckPosition"] = float(properties.get("lowerCheckPosition"))
        umaData["lipsSize"] = float(properties.get("lipsSize"))
        umaData["mouthSize"] = float(properties.get("mouthSize"))
        umaData["jawPosition"] = float(properties.get("jawPosition"))
        umaData["chinSize"] = float(properties.get("chinSize"))
        umaData["chinPosition"] = float(properties.get("chinPosition"))'''
        
        # TODO: change the instance used based on race?
        #instanceName = "default"
        instanceName = properties.get("instanceName")
        # Set racial/gender based property values
        portrait = ""
        scaleFactor = 1.0
        overheadOffset = 1900
        soundSet = 4
        
        spawnPoint = "spawn"
        zone = properties.get("instanceName")
        subzone = ""
        #meshName = "human_male"
        #meshName = "char_smoo.mesh"

        if meshName:
            displayContext = DisplayContext(meshName, True)
            displayContext.addSubmesh(DisplayContext.Submesh("", ""))
            #submeshInfo = meshInfo[meshName]
            #for entry in submeshInfo:
            #    displayContext.addSubmesh(DisplayContext.Submesh(entry[0], entry[1]))
            ot.put(WorldManagerClient.NAMESPACE,
                   WorldManagerClient.TEMPL_DISPLAY_CONTEXT, displayContext)
        
        # Do a few extra stat calcs
        #manamax = int(float(mana_base) * willpower / 100)
        #healthmax = int(float(health_base) * endurance / 100)
        
        # get default instance oid
        instanceOid = InstanceClient.getInstanceOid(instanceName)
        if not instanceOid:
            Log.error("SampleFactory: no 'default' instance")
            properties.put("errorMessage", "No default instance")
            return 0

        # set the spawn location
        Log.debug("SPAWN: spawn marker name=" + str(spawnPoint) + " and race=" + str(race))
        spawnMarker = InstanceClient.getMarker(instanceOid, spawnPoint)
        #spawnMarker.getPoint().setY(0)

        # override template
        ot.put(WorldManagerClient.NAMESPACE,
               WorldManagerClient.TEMPL_NAME, name)
        ot.put(WorldManagerClient.NAMESPACE,
               WorldManagerClient.TEMPL_INSTANCE, instanceOid)
        ot.put(WorldManagerClient.NAMESPACE,
               WorldManagerClient.TEMPL_LOC, spawnMarker.getPoint())
        ot.put(WorldManagerClient.NAMESPACE,
               WorldManagerClient.TEMPL_ORIENT, spawnMarker.getOrientation())
        ot.put(WorldManagerClient.NAMESPACE, "accountId", uid)
        ot.put(WorldManagerClient.NAMESPACE, "model", meshName)       
        ot.put(WorldManagerClient.NAMESPACE, "race", race)
        ot.put(WorldManagerClient.NAMESPACE, "charactername", name)
        ot.put(WorldManagerClient.NAMESPACE, "world", instanceName)
        ot.put(WorldManagerClient.NAMESPACE, "category", 1)
        ot.put(WorldManagerClient.NAMESPACE, "zone", zone)
        zones = LinkedList()
        ot.put(WorldManagerClient.NAMESPACE, "zones", zones)
        ot.put(WorldManagerClient.NAMESPACE, "subzone", subzone)
        subzones = LinkedList()
        ot.put(WorldManagerClient.NAMESPACE, "subzones", subzones)
        ot.put(WorldManagerClient.NAMESPACE, "hearthLoc", spawnMarker.getPoint())
        ot.put(WorldManagerClient.NAMESPACE, "hearthInstance", instanceName)
        ot.put(WorldManagerClient.NAMESPACE, "umaData", umaData)
        ot.put(WorldManagerClient.NAMESPACE, "primaryItem", 0)
        ot.put(WorldManagerClient.NAMESPACE, "secondaryItem", 0)
        ot.put(WorldManagerClient.NAMESPACE, "playerAppearance", 0)
        ot.put(WorldManagerClient.NAMESPACE, "scaleFactor", scaleFactor)
        ot.put(WorldManagerClient.NAMESPACE, "portrait", portrait)
        ot.put(WorldManagerClient.NAMESPACE, "soundSet", soundSet)
        ot.put(WorldManagerClient.NAMESPACE, "walk_speed", 3)
        ot.put(WorldManagerClient.NAMESPACE, "movementState", 1)
        
        factionData = HashMap()
        factionData.put(4, PlayerFactionData(4, "Randarock", 1000, "The Legion", 1))
        faction = 1
        ot.put(WorldManagerClient.NAMESPACE, "factionData", factionData)
        ot.put(WorldManagerClient.NAMESPACE, "faction", faction)
        
        # Other Properties
        ot.put(WorldManagerClient.NAMESPACE, "busy", False)
        ot.put(Namespace.QUEST, ":currentQuests", "")
        ot.put(SocialClient.NAMESPACE, ":channels", "")
        lastGames = LinkedList() # Keeps track of the time when the last 3 games were played
        #ot.put(WorldManagerClient.NAMESPACE, "lastGames", lastGames)
        #ot.put(InventoryClient.NAMESPACE, InventoryClient.TEMPL_ITEMS, "*1;*2;*5;*6;*7;*8;*9;*10")
        ot.put(InventoryClient.NAMESPACE, InventoryClient.TEMPL_ITEMS, "")
        
        #restorePoint = InstanceRestorePoint("default", spawnMarker.getPoint())
        restorePoint = InstanceRestorePoint(instanceName, spawnMarker.getPoint())
        restorePoint.setFallbackFlag(True)
        restoreStack = LinkedList()
        restoreStack.add(restorePoint)
        ot.put(Namespace.OBJECT_MANAGER,
               ObjectManagerClient.TEMPL_INSTANCE_RESTORE_STACK, restoreStack)
        ot.put(Namespace.OBJECT_MANAGER,
               ObjectManagerClient.TEMPL_CURRENT_INSTANCE_NAME, instanceName)
        
        ot.put(Namespace.OBJECT_MANAGER,
                ObjectManagerClient.TEMPL_PERSISTENT, Boolean(True))
        
        # Combat Properties/stats
        ot.put(CombatClient.NAMESPACE, "aspect", "Earth")
        ot.put(CombatClient.NAMESPACE, "attackable", Boolean(True))
        
        ot.put(CombatClient.NAMESPACE, "attackType", "crush")
        ot.put(CombatClient.NAMESPACE, "weaponType", "Unarmed")
        
        effectsList = LinkedList()
        ot.put(CombatClient.NAMESPACE, "effects", effectsList)
        
        # Arena Properties
        ot.put(WorldManagerClient.NAMESPACE, "arenaID", -1)

        # generate the object
        objOid = ObjectManagerClient.generateObject(-1, ObjectManagerPlugin.MOB_TEMPLATE, ot)
        Log.debug("SampleFactory: generated obj oid=" + str(objOid))
        return objOid

sampleFactory = SampleFactory()
LoginPlugin.getCharacterGenerator().setCharacterFactory(sampleFactory)
