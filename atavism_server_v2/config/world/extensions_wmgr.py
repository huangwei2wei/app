from atavism.server.engine import *
from atavism.agis.plugins import *
from atavism.agis.objects import *
from atavism.server.objects import *
from atavism.server.math import *
from java.util import LinkedList

wmgr.registerRegionTrigger("instanceEntry", InstanceEntryRegionTrigger())

trigger = MessageRegionTrigger(MessageRegionTrigger.TARGET_MODE)
trigger.setPropertyExclusions(wmgr.getPropertyExclusions())
props = HashMap()
props.put("specialProperty","specialValue")
trigger.setMessageProperties(props)
wmgr.registerRegionTrigger("enterMessage", trigger)

trigger = MessageRegionTrigger(MessageRegionTrigger.TARGET_MODE)
trigger.setPropertyExclusions(wmgr.getPropertyExclusions())
props = HashMap()
props.put("specialProperty","specialValue")
trigger.setMessageProperties(props)
wmgr.registerRegionTrigger("leaveMessage", trigger)

class ZoneTriggers(RegionTrigger):
    def enter(self, obj, region):
        Log.debug("ZONE: %s entered by player: %s" % (region.getName(), obj))
        oid = obj.getMasterOid()
        zoneName = region.getName()
        oldZoneName = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "zone")
        if oldZoneName == zoneName:
            return
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "zone", zoneName)
        # Get the list of zones and add the subzone the player was in to the list
        zones = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "zones")
        zones.add(zoneName)
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "zones", zones)
        CombatClient.updateFatigueStatus(oid, 0)
    
    def leave(self, obj, region):
        Log.debug("ZONE: %s left by player: %s" % (region.getName(), obj))
        oid = obj.getMasterOid()
        zoneName = region.getName()
        # Get the list of zones to change the property to the last one they entered (but havent left)
        zones = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "zones")
        zones.remove(zoneName)
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "zones", zones)
        oldZoneName = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "zone")
        Log.debug("ZONE: comparing zone: %s with old zone: %s" % (zoneName, oldZoneName))
        if zoneName == oldZoneName:
            if (len(zones) == 0):
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "zone", "")
                # Start Fatigue Effect
                CombatClient.updateFatigueStatus(oid, 1)
            else:
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "zone", zones[0])

class SubzoneTriggers(RegionTrigger):
    def enter(self, obj, region):
        Log.debug("SUBZONE: %s entered by player: %s" % (region.getName(), obj))
        oid = obj.getMasterOid()
        subzoneName = region.getName()
        oldSubzoneName = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzone")
        if oldSubzoneName == subzoneName:
            return
        #oldSubzoneName = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzone")
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzone", subzoneName)
        # Get the list of subzones and add the subzone the player was in to the list
        subzones = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzones")
        subzones.add(subzoneName)
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzones", subzones)
    
    def leave(self, obj, region):
        Log.debug("SUBZONE: %s left by player: %s" % (region.getName(), obj))
        oid = obj.getMasterOid()
        #wnode = WorldManagerClient.getWorldNode(oid)
        #wmnode = WMWorldNode(wnode)
        newLoc = obj.getLoc()
        subzoneName = region.getName()
        # Get the list of subzones to change the property to the last one they entered (but havent left)
        subzones = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzones")
        subzones.remove(subzoneName)
        EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzones", subzones)
        oldSubzoneName = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzone")
        Log.debug("SUBZONE: comparing subzone: %s with old subzone: %s" % (subzoneName, oldSubzoneName))
        if subzoneName == oldSubzoneName:
            if (len(subzones) == 0):
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzone", "")
            else:
                EnginePlugin.setObjectProperty(oid, WorldManagerClient.NAMESPACE, "subzone", subzones[0])

class ArenaTriggers(RegionTrigger):
    def enter(self, obj, region):
        pass
    
    def leave(self, obj, region):
        Log.debug("ANDREW - arenazone: %s left by player: %s" % (region, obj))
        oid = obj.getMasterOid()
        ArenaClient.removePlayer(oid)


wmgr.registerRegionTrigger("zoneEnter", ZoneTriggers())
wmgr.registerRegionTrigger("zoneLeave", ZoneTriggers())
wmgr.registerRegionTrigger("subzoneEnter", SubzoneTriggers())
wmgr.registerRegionTrigger("subzoneLeave", SubzoneTriggers())
wmgr.registerRegionTrigger("arenaLeave", ArenaTriggers())
