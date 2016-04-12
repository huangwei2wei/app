from java.util import *
from java.lang import *
from atavism.agis import *
from atavism.agis.core import *
from atavism.agis.objects import *
from atavism.agis.util import *
from atavism.agis.plugins import *
from atavism.msgsys import *
from atavism.server.math import *
from atavism.server.plugins import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from java.util.concurrent import *
import time

Log.debug("extensions_proxy.py: Loading...")

class WaveCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/wave: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "wave")
    
class BowCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/bow: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "bow")
    
class ClapCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/clap: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "clap")
    
class CryCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/cry: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "cry")
 
class LaughCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/laugh: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "laugh")
    
class CheerCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/cheer: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "cheer")
    
class NoCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/no: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "disagree")
    
class PointCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/point: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "point")
    
class ShrugCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/shrug: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "shrug")

class SaluteCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/salute: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "salute")

class TalkCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/talk: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "talk")

class RaiseHandCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        Log.debug("/raiseHand: oid=" + str(playerOid))
        AnimationClient.playSingleAnimation(playerOid,
                                            "raiseHand")
                                                                                        
globalPoid = 0
numMoved = 0

class testExecution(Runnable):
    def run(self):
        global globalPoid
        global numMoved
        Log.debug("ANDREW - globalPoid2 = " + str(globalPoid))
        WorldManagerClient.sendObjChatMsg(globalPoid, 1, "Scheduled Executor message: " + numMoved)
        if numMoved < 40:
            oldNode = WorldManagerClient.getWorldNode(globalPoid)
            loc = oldNode.getLoc()
            loc.add(500, 100, 0)
            tnode = BasicWorldNode()
            tnode.setLoc(loc)
            WorldManagerClient.updateWorldNode(globalPoid, tnode, true)
            numMoved = numMoved + 1
        
class ScheduleCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        global globalPoid
        cmd = cmdEvent.getCommand()
        globalPoid = cmdEvent.getObjectOid()
        Log.debug("ANDREW - globalPoid = " + str(globalPoid))
        #Engine.getExecutor().scheduleAtFixedRate(testExecution(), 5000, 500, TimeUnit.MILLISECONDS)


###
# Special Commands
###
class SetAdminLevelCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("ADMIN: splitCMD[1]=" + splitCmd[1])
        if splitCmd[1] == "admin":
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "adminLevel", 5)
        elif splitCmd[1] == "GM":
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "adminLevel", 3)
        elif splitCmd[1] == "player":
            EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "adminLevel", 1)
        return None

#proxyPlugin.registerCommand("/setAdminLevel", SetAdminLevelCommand())
        
###
# Admin Commands
# Requires adminLevel of 5+
###
adminLevelReq = 5

def checkAdminAccess(oid):
    adminLevel = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "adminLevel")
    if adminLevel is None:
        return False
    if adminLevel < adminLevelReq:
        return False
    return True

class SendServerChatMessageCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkAdminAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        spaceLoc = cmd.find(" ")
        message = cmd[spaceLoc+1:]
        oids = proxyPlugin.getPlayerOids()
        for oid in oids:
            WorldManagerClient.sendObjChatMsg(oid, 2, "[Server Message]: %s" % message)
        return None

class FlyCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkAdminAccess(playerOid) == False:
            return None
        
        movementState = AgisWorldManagerPlugin.MOVEMENT_STATE_FLYING
        # Check if it is set to stop
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        if len(splitCmd) > 1:
            stop = splitCmd[1]
            if stop == "stop" or stop == "0":
                movementState = AgisWorldManagerPlugin.MOVEMENT_STATE_RUNNING
                
        props = HashMap()
        props.put(AgisWorldManagerPlugin.PROP_MOVEMENT_STATE, movementState)
        eMsg = WorldManagerClient.ExtensionMessage(AgisMobClient.MSG_TYPE_SET_MOVEMENT_STATE, playerOid, props)
        Engine.getAgent().sendBroadcast(eMsg)
        return None

proxyPlugin.registerCommand("/serverMessage", SendServerChatMessageCommand())
proxyPlugin.registerCommand("/fly", FlyCommand())

### 
# GM Commands
# Requires adminLevel of 3+
###
gmLevelReq = 3

def checkGMAccess(oid):
    adminLevel = EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "adminLevel")
    if adminLevel is None:
        return False
    if adminLevel < gmLevelReq:
        return False
    return True
    
class GainSkillPointsCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        EnginePlugin.setObjectProperty(playerOid, CombatClient.NAMESPACE, "skillPoints", 100)
        return None

class GainSkillCurrentCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        skillType = int(splitCmd[1])
        skillBoost = int(splitCmd[2])
        CombatClient.skillAlterCurrent(playerOid, skillType, skillBoost)
        return None

class GainExpCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        expAmount = int(splitCmd[1])
        # Send some message to increase exp by the specified amount
        QERmsg = CombatClient.alterExpMessage(playerOid, expAmount);
        Engine.getAgent().sendBroadcast(QERmsg)
        return None

class InstanceChangeCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        #if checkGMAccess(playerOid) == False:
        #    return None
            
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        spaceLoc = cmd.find(" ")
        instanceName = cmd[spaceLoc+1:]
        instanceOid = InstanceClient.getInstanceOid(instanceName)
        if instanceOid == None:
            Log.debug("CHANGEI: Instance name is wrong: " + instanceName)
            return None
        Log.debug("CHANGEI: Instance name: " + instanceName + "; oid: " + str(instanceOid))
        node = BasicWorldNode()
        markerName = "spawn"
        spawn = InstanceClient.getMarker(instanceOid, markerName)
        node.setInstanceOid(instanceOid)
        node.setOrientation(spawn.getOrientation())
        node.setLoc(spawn.getPoint())
        direction = AOVector()
        node.setDir(direction)
        InstanceClient.objectInstanceEntry(playerOid, node, InstanceClient.InstanceEntryReqMessage.FLAG_NONE)
        return None

class GenerateItemCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        itemID = int(splitCmd[1])
        count = 1
        if len(splitCmd) > 2:
            count = int(splitCmd[2])
        Log.debug("CreateItemSubObjCommand: template=" + str(itemID))
        # add to inventory
        AgisInventoryClient.generateItem(playerOid, itemID, "", count, None)
        Log.debug("CommandPlugin: createitem: itemName=" + str(itemID))
        WorldManagerClient.sendObjChatMsg(playerOid, 1, "added item: " + str(itemID))

class GainCurrencyCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        currencyType = int(splitCmd[1])
        amount = int(splitCmd[2])
        AgisInventoryClient.alterCurrency(playerOid, currencyType, amount)
        return None

class LogoutUserCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):              
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        if checkGMAccess(playerOid) == False:
            return None
        if targetOid == playerOid:
            return None
        cmd = cmdEvent.getCommand()
        spaceLoc = cmd.find(" ")           
        username = cmd[spaceLoc+1:]           
        #muteoid = GroupClient.getPlayerByName(username)
        msg = WorldManagerClient.TargetedExtensionMessage(targetOid)
        msg.setProperty("ext_msg_subtype","Logout")                      
        Engine.getAgent().sendBroadcast(msg)
        WorldManagerClient.sendObjChatMsg(playerOid, 0, "User: " + targetOid + " has been kicked.")

class BanUserCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):              
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        if checkGMAccess(playerOid) == False:
            return None
        if targetOid == playerOid:
            return None
        cmd = cmdEvent.getCommand()
        spaceLoc = cmd.find(" ")
        duration = cmd[spaceLoc+1:]        
        #username = cmd[spaceLoc+1:]
        #banoid = GroupClient.getPlayerByName(username)
        msg = WorldManagerClient.TargetedExtensionMessage(targetOid)
        msg.setProperty("ext_msg_subtype","Logout")                      
        Engine.getAgent().sendBroadcast(msg)
        #AgisProxyPlugin.addUserToBanList(banoid, username)
        #WorldManagerClient.sendObjChatMsg(playerOid, 0, "User: " + username + " has been kicked and banned.")
        AgisProxyPlugin.addUserToBanList(targetOid, duration)
        WorldManagerClient.sendObjChatMsg(playerOid, 0, "User: " + targetOid + " has been kicked and banned for " + duration + " hours.")

class BanUserByNameCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):              
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        spaceLoc = cmd.find(" ")
        duration = int(splitCmd[1])
        username = str(splitCmd[2])
        #banoid = GroupClient.getPlayerByName(username)
        msg = WorldManagerClient.TargetedExtensionMessage(targetOid)
        msg.setProperty("ext_msg_subtype","Logout")                      
        Engine.getAgent().sendBroadcast(msg)
        #AgisProxyPlugin.addUserToBanList(banoid, username)
        #WorldManagerClient.sendObjChatMsg(playerOid, 0, "User: " + username + " has been kicked and banned.")
        AgisProxyPlugin.addUserToBanList(targetOid, duration)
        WorldManagerClient.sendObjChatMsg(playerOid, 0, "User: " + targetOid + " has been kicked and banned for " + duration + " hours.")

class UnbanUserCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        spaceLoc = cmd.find(" ")
        username = cmd[spaceLoc+1:]
        AgisProxyPlugin.removeUserFromBanList(username)
        WorldManagerClient.sendObjChatMsg(playerOid, 0, "User: " + username + " is no longer banned.")

class AttackableCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        if checkGMAccess(playerOid) == False:
            return None
        cmd = cmdEvent.getCommand()
        attackableState = int(splitCmd[1])
        if attackableState == 1:
            EnginePlugin.setObjectProperty(playerOid, CombatClient.NAMESPACE, "attackable", True)
        else:
            EnginePlugin.setObjectProperty(playerOid, CombatClient.NAMESPACE, "attackable", False)
        return None

proxyPlugin.registerCommand("/getSkillPoints", GainSkillPointsCommand())
proxyPlugin.registerCommand("/getSkillCurrent", GainSkillCurrentCommand())
proxyPlugin.registerCommand("/getExp", GainExpCommand())
proxyPlugin.registerCommand("/changeInstance", InstanceChangeCommand())
proxyPlugin.registerCommand("/generateItem", GenerateItemCommand())
proxyPlugin.registerCommand("/gi", GenerateItemCommand())
proxyPlugin.registerCommand("/getCurrency", GainCurrencyCommand())
proxyPlugin.registerCommand("/kick", LogoutUserCommand())
proxyPlugin.registerCommand("/ban", BanUserCommand())
proxyPlugin.registerCommand("/banUserName", BanUserByNameCommand())
proxyPlugin.registerCommand("/unban", UnbanUserCommand())
proxyPlugin.registerCommand("/attackable", AttackableCommand())


###
# Player/General Commands
###
class SkillIncreaseCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        cmd = cmdEvent.getCommand()
        oid = cmdEvent.getObjectOid()
        Log.debug("SKILL: skill increase command; oid = " + str(oid))
        splitCmd = cmd.split(" ")
        skill = int(splitCmd[1])
        CombatClient.skillIncreased(oid, skill)

class SkillDecreaseCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        cmd = cmdEvent.getCommand()
        oid = cmdEvent.getObjectOid()
        Log.debug("SKILL: skill decrease command; oid = " + str(oid))
        splitCmd = cmd.split(" ")
        skill = int(splitCmd[1])
        CombatClient.skillDecreased(oid, skill)

class SkillResetCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        cmd = cmdEvent.getCommand()
        oid = cmdEvent.getObjectOid()
        Log.debug("SKILL: skill reset command; oid = " + str(oid))
        CombatClient.skillReset(oid)

class QuestResponseCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        questID = OID.fromString(splitCmd[1])
        acceptStatus = int(splitCmd[2])
        if acceptStatus == 0:
            QERmsg = QuestClient.QuestResponseMessage(targetOid, playerOid, questID, False)
        else:
            QERmsg = QuestClient.QuestResponseMessage(targetOid, playerOid, questID, True)
        Engine.getAgent().sendBroadcast(QERmsg)
        return None

class RequestQuestProgressCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        QERmsg = QuestClient.RequestQuestProgressMessage(targetOid, playerOid)
        Engine.getAgent().sendBroadcast(QERmsg)
        return None
        
class GetMerchantListCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        AgisInventoryClient.getMerchantList(playerOid, targetOid)
        return None

class AbandonQuestCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        questID = OID.fromString(splitCmd[1])
        Log.debug("AbandonQuest hit with oid: " + str(splitCmd[1]) + " and id: " + str(questID))
        QERmsg = QuestClient.AbandonQuestMessage(playerOid, questID)
        Engine.getAgent().sendBroadcast(QERmsg)
        return None

class PlayAnimationCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        animation = splitCmd[1]
        effect = CoordinatedEffect("PlayAnimation")
        effect.putArgument("animName", animation)
        effect.sendSourceOid(True)
        effect.invoke(playerOid, playerOid);
        return None
        
class GetNpcInteractionsCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        AgisMobClient.getNpcInteractions(targetOid, playerOid)
        return None
        
class StartNpcInteractionCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        interactionID = int(splitCmd[1])
        interactionType = splitCmd[2]
        AgisMobClient.startNpcInteraction(targetOid, playerOid, interactionID, interactionType)
        return None
        
class DialogueOptionChosenCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        dialogueID = int(splitCmd[1])
        interactionType = splitCmd[2]
        actionID = int(splitCmd[3])
        AgisMobClient.chooseDialogueOption(targetOid, playerOid, dialogueID, actionID, interactionType)
        return None
        
class CompleteQuestCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        questID = OID.fromString(splitCmd[1])
        itemChosen = int(splitCmd[2])
        QuestClient.completeQuest(targetOid, playerOid, questID, itemChosen)
        return None

class OpenMobCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        AgisInventoryClient.requestOpenMob(targetOid, playerOid)
        return None

class StopAutoAttackCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        cmd = cmdEvent.getCommand()
        oid = cmdEvent.getObjectOid()
        Log.debug("AUTOATTACK: stopping auto attack for: " + str(oid))
        CombatClient.stopAutoAttack(oid)

class HardwareInfoCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        cmd = cmdEvent.getCommand()
        oid = cmdEvent.getObjectOid()
        splitCmd = cmd.split(";")
        deviceName = splitCmd[1]
        driverVersion = splitCmd[2]
        videoMemory = splitCmd[3]
        systemMemory = splitCmd[4]
        fragmentVersion = splitCmd[5]
        vertexVersion = splitCmd[6]
        maxLights = splitCmd[7]
        textureUnits = splitCmd[8]
        windowSizeX = splitCmd[9]
        windowSizeY = splitCmd[10]
        # Write data to a file
        filename = "../compData/compData_" + str(oid) + ".txt"
        try:
            dataFile = open(filename,"r+")
        except IOError:
            dataFile = open(filename,"w")
        dataFile.readlines()
        dataFile.write("Video card: " + deviceName + "\n")
        dataFile.write("Driver version: " + driverVersion  + "\n")
        dataFile.write("Video card memory: " + videoMemory  + "\n")
        dataFile.write("System memory: " + systemMemory  + "\n")
        dataFile.write("Fragment shader version: " + fragmentVersion  + "\n")
        dataFile.write("Vertex shader version: " + vertexVersion  + "\n")
        dataFile.write("Max lights: " + maxLights  + "\n")
        dataFile.write("Texture units: " + textureUnits  + "\n")
        dataFile.write("Window size width: " + windowSizeX + "; height: " + windowSizeY + "\n")
        dataFile.write("----------"  + "\n")
        dataFile.close()
        # Write a few empty lines to the performanceData file 
        filename = "../compData/performanceData_" + str(oid) + ".csv"
        try:
            dataFile2 = open(filename,"r+")
        except IOError:
            dataFile2 = open(filename,"w")
        dataFile2.readlines()
        dataFile2.write(",,,\n")
        dataFile2.close()

class PerformanceInfoCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        cmd = cmdEvent.getCommand()
        oid = cmdEvent.getObjectOid()
        splitCmd = cmd.split(" ")
        framerate = splitCmd[1]
        renders = splitCmd[2]
        bytesRecieved = splitCmd[3]
        bytesSent = splitCmd[4]
        # Write data to a file
        filename = "../compData/performanceData_" + str(oid) + ".csv"
        try:
            dataFile = open(filename,"r+")
        except IOError:
            dataFile = open(filename,"w")
        dataFile.readlines()
        dataFile.write(framerate + ",")
        dataFile.write(renders  + ",")
        dataFile.write(bytesRecieved  + ",")
        dataFile.write(bytesSent  + "\n")
        dataFile.close()

class BugReportCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        cmd = cmdEvent.getCommand()
        oid = cmdEvent.getObjectOid()
        splitCmd = cmd.split(" ")
        bugType = splitCmd[1]
        whatHappened = splitCmd[2]
        whatShouldHappen = splitCmd[3]
        howToReproduce = splitCmd[4]
        # Write data to a file
        filename = "../bugReports/report_" + str(oid) + ".txt"
        try:
            dataFile = open(filename,"r+")
        except IOError:
            dataFile = open(filename,"w")
        dataFile.readlines()
        dataFile.write("===Bug Report=== \n")
        dataFile.write("Bug Type: " + bugType + "\n")
        dataFile.write("What Happened: " + whatHappened + "\n")
        dataFile.write("What should happen: " + whatShouldHappen + "\n")
        dataFile.write("How to reproduce: " + howToReproduce + "\n")
        dataFile.close()

class DuelCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        ArenaClient.duelChallenge(playerOid, targetOid)
        return None

class DuelAcceptCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        ArenaClient.duelChallengeAccept(playerOid)
        return None

class DuelDeclineCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        ArenaClient.duelChallengeDecline(playerOid)
        return None

class GetAttitudeCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        CombatClient.getAttitude(playerOid, targetOid)
        return None

class InvitePlayerToGroupCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        targetName = splitCmd[1]
        GroupClient.groupInviteByName(playerOid, targetName)

class LeaveArenaCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        ArenaClient.removePlayer(playerOid)
        return None

class DeleteItemStackCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        itemOid = OID.fromString(splitCmd[1])
        AgisInventoryClient.removeSpecificItem(playerOid, itemOid, True, 0)
        
class DeleteItemCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        itemOid = OID.fromString(splitCmd[1])
        AgisInventoryClient.removeSpecificItem(playerOid, itemOid, False, 1)

class GetItemInfoCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        itemOid = OID.fromString(splitCmd[1])
        itemOids = ArrayList()
        for i in range(0, len(splitCmd)):
            itemOids.add(splitCmd[i])
        AgisInventoryClient.getSpecificItemData(playerOid, targetOid, itemOid)

class PlaceBagCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("PLACEBAG: splitCMD[1]=" + splitCmd[1] + "; splitCMD[2]=" + splitCmd[2])
        itemOid = OID.fromString(splitCmd[1]) 
        bagSpotNum = int(splitCmd[2])
        AgisInventoryClient.placeBag(playerOid, itemOid, bagSpotNum)

class MoveBagCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("MOVEBAG: splitCMD[1]=" + splitCmd[1] + "; splitCMD[2]=" + splitCmd[2])
        bagSpotNum = int(splitCmd[1])
        newSpotNum = int(splitCmd[2])
        AgisInventoryClient.moveBag(playerOid, bagSpotNum, newSpotNum)

class RemoveBagCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("REMOVEBAG: splitCMD[1]=" + splitCmd[1] + "; splitCMD[2]=" + splitCmd[2])
        bagSpotNum = int(splitCmd[1]) 
        containerId = int(splitCmd[2])
        slotId = int(splitCmd[3])
        AgisInventoryClient.removeBag(playerOid, bagSpotNum, containerId, slotId)

class GetLootListCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        AgisInventoryClient.getLootList(playerOid, targetOid)
        return None

class LootItemCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("LOOT: splitCMD[1]=" + splitCmd[1])
        itemOid = OID.fromString(splitCmd[1])
        AgisInventoryClient.lootItem(playerOid, itemOid, targetOid)

class LootAllCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        AgisInventoryClient.lootAll(playerOid, targetOid)

class UpdateActionBarCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("LOOT: splitCMD[1]=" + splitCmd[1])
        actionPosition = int(splitCmd[1])
        newAction = splitCmd[2]
        CombatClient.updateActionBar(playerOid, actionPosition, newAction)

class PurchaseItemCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("SELL: splitCMD[1]=" + splitCmd[1])
        itemID = int(splitCmd[1])
        count = int(splitCmd[2])
        AgisInventoryClient.purchaseItemFromMerchant(playerOid, targetOid, itemID, count)

class SellItemCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("SELL: splitCMD[1]=" + splitCmd[1])
        itemOid = OID.fromString(splitCmd[1])
        AgisInventoryClient.sellItem(playerOid, targetOid, itemOid)

class SendCommandToPetCommand(ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        targetOid = cmdEvent.getTarget()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("PET: splitCMD[1]=" + splitCmd[1])
        command = splitCmd[1]
        AgisMobClient.sendPetCommand(playerOid, targetOid, command)

class GetArenaStatsCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("SELL: splitCMD[1]=" + splitCmd[1])
        statsType = int(splitCmd[1])
        ArenaClient.getArenaStats(playerOid, statsType)
        return None

class SetIntPropertyCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        propertyName = splitCmd[1]
        propertyValue = int(splitCmd[2])
        EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, propertyName, propertyValue)
        return None

class SetStringPropertyCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        propertyName = splitCmd[1]
        propertyValue = splitCmd[2]
        EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, propertyName, propertyValue)
        return None

class UpdateBreathCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        Log.debug("BREATH: splitCMD[1]=" + splitCmd[1])
        underwater = int(splitCmd[1])
        CombatClient.updateBreathStatus(playerOid, underwater)
        return None
        
class GetIslandsCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        AgisMobClient.getIslandsData(playerOid)
        return None
        
class SetJumpPropertyCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        propertyValue = Boolean(splitCmd[1])
        EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "jump", propertyValue)
        return None
        
class AddFriendCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        recipient = splitCmd[1]
        props = HashMap()
        props.put("playerOid", playerOid)
        props.put("friend", recipient)
        eMsg = WorldManagerClient.ExtensionMessage(GroupClient.MSG_TYPE_ADD_FRIEND, playerOid, props)
        Engine.getAgent().sendBroadcast(eMsg)
        return None
        
class SetArenaDifficultyCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        propertyValue = int(splitCmd[1])
        EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "arenaDifficulty", propertyValue)
        return None
        
class ReloadItemsCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        props = HashMap()
        eMsg = WorldManagerClient.ExtensionMessage(AgisInventoryClient.MSG_TYPE_RELOAD_ITEMS, playerOid, props)
        Engine.getAgent().sendBroadcast(eMsg)
        return None 
        
proxyPlugin.registerCommand("/wave", WaveCommand())
proxyPlugin.registerCommand("/bow", BowCommand())
proxyPlugin.registerCommand("/clap", ClapCommand())
proxyPlugin.registerCommand("/cry", CryCommand())
proxyPlugin.registerCommand("/laugh", LaughCommand())
proxyPlugin.registerCommand("/cheer", CheerCommand())
proxyPlugin.registerCommand("/no", NoCommand())
proxyPlugin.registerCommand("/point", PointCommand())
proxyPlugin.registerCommand("/shrug", ShrugCommand())
proxyPlugin.registerCommand("/salute", SaluteCommand())
proxyPlugin.registerCommand("/talk", TalkCommand())
proxyPlugin.registerCommand("/raiseHand", RaiseHandCommand())
proxyPlugin.registerCommand("/schedule", ScheduleCommand())
proxyPlugin.registerCommand("/skillIncrease", SkillIncreaseCommand())
proxyPlugin.registerCommand("/skillDecrease", SkillDecreaseCommand())
proxyPlugin.registerCommand("/skillReset", SkillResetCommand())
proxyPlugin.registerCommand("/questResponse", QuestResponseCommand())
#proxyPlugin.registerCommand("/requestQuestProgress", RequestQuestProgressCommand())
proxyPlugin.registerCommand("/requestNPCInteractions", RequestQuestProgressCommand())
proxyPlugin.registerCommand("/getMerchantList", GetMerchantListCommand())
proxyPlugin.registerCommand("/purchaseItem", PurchaseItemCommand())
proxyPlugin.registerCommand("/abandonQuest", AbandonQuestCommand())
proxyPlugin.registerCommand("/playAnimation", PlayAnimationCommand())
proxyPlugin.registerCommand("/getNpcInteractions", GetNpcInteractionsCommand())
proxyPlugin.registerCommand("/startInteraction", StartNpcInteractionCommand())
proxyPlugin.registerCommand("/dialogueOption", DialogueOptionChosenCommand())
proxyPlugin.registerCommand("/completeQuest", CompleteQuestCommand())
proxyPlugin.registerCommand("/openMob", OpenMobCommand())
proxyPlugin.registerCommand("/stopAttack", StopAutoAttackCommand())
proxyPlugin.registerCommand("/hardwareInfo", HardwareInfoCommand())
proxyPlugin.registerCommand("/performanceInfo", PerformanceInfoCommand())
proxyPlugin.registerCommand("/bugReport", BugReportCommand())
proxyPlugin.registerCommand("/duel", DuelCommand())
proxyPlugin.registerCommand("/duelAccept", DuelAcceptCommand())
proxyPlugin.registerCommand("/duelDecline", DuelDeclineCommand())
proxyPlugin.registerCommand("/getAttitude", GetAttitudeCommand())
proxyPlugin.registerCommand("/invite", InvitePlayerToGroupCommand())
proxyPlugin.registerCommand("/leaveArena", LeaveArenaCommand())
proxyPlugin.registerCommand("/deleteItemStack", DeleteItemStackCommand())
proxyPlugin.registerCommand("/deleteItem", DeleteItemCommand())
proxyPlugin.registerCommand("/getSpecificItemData", GetItemInfoCommand())
proxyPlugin.registerCommand("/placeBag", PlaceBagCommand())
proxyPlugin.registerCommand("/moveBag", MoveBagCommand())
proxyPlugin.registerCommand("/removeBag", RemoveBagCommand())
proxyPlugin.registerCommand("/getLootList", GetLootListCommand())
proxyPlugin.registerCommand("/lootItem", LootItemCommand())
proxyPlugin.registerCommand("/lootAll", LootAllCommand())
proxyPlugin.registerCommand("/updateActionBar", UpdateActionBarCommand())
proxyPlugin.registerCommand("/sellItem", SellItemCommand())
proxyPlugin.registerCommand("/petCommand", SendCommandToPetCommand())
proxyPlugin.registerCommand("/getArenaStats", GetArenaStatsCommand())
proxyPlugin.registerCommand("/setIntProperty", SetIntPropertyCommand())
proxyPlugin.registerCommand("/setStringProperty", SetStringPropertyCommand())
proxyPlugin.registerCommand("/updateBreath", UpdateBreathCommand())
proxyPlugin.registerCommand("/getIslands", GetIslandsCommand())
proxyPlugin.registerCommand("/jump", SetJumpPropertyCommand())
proxyPlugin.registerCommand("/addFriend", AddFriendCommand())
proxyPlugin.registerCommand("/arenaDifficulty", SetArenaDifficultyCommand())
proxyPlugin.registerCommand("/reloadItems", ReloadItemsCommand())

###
# World Builder Commands
###
class GetMobTemplatesCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        AgisMobClient.getMobTemplates(playerOid)
        return None

class GetQuestTemplatesCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        AgisMobClient.getQuestTemplates(playerOid)
        return None

class CreateMobSpawnCommand (ProxyPlugin.CommandParser):
    def parse(self, cmdEvent):
        playerOid = cmdEvent.getObjectOid()
        cmd = cmdEvent.getCommand()
        splitCmd = cmd.split(" ")
        mobTemplate = str(splitCmd[1])
        roamRadius = int(splitCmd[2])
        Log.debug("Create spawn message: " + mobTemplate + ", radius: " + str(roamRadius))
        AgisMobClient.createMobSpawn(playerOid, mobTemplate, roamRadius)
        return None
        
proxyPlugin.registerCommand("/getMobTemplates", GetMobTemplatesCommand())
proxyPlugin.registerCommand("/getQuestTemplates", GetQuestTemplatesCommand())
proxyPlugin.registerCommand("/createMobSpawn", CreateMobSpawnCommand())  

proxyPlugin.addProxyExtensionHook("proxy.INSTANCE_ENTRY", InstanceEntryProxyHook())
proxyPlugin.addProxyExtensionHook("proxy.GENERATE_OBJECT", GenerateObjectProxyHook())

proxyPlugin.registerExtensionSubtype("combat.PURCHASE_SKILL_POINT", ClassAbilityClient.MSG_TYPE_PURCHASE_SKILL_POINT)
proxyPlugin.registerExtensionSubtype("ao.COMPLETE_QUEST", QuestClient.MSG_TYPE_COMPLETE_QUEST)
proxyPlugin.registerExtensionSubtype("ao.DISMOUNT", CombatClient.MSG_TYPE_DISMOUNT)
# Inventory Client
proxyPlugin.registerExtensionSubtype("inventory.MOVE_ITEM", AgisInventoryClient.MSG_TYPE_MOVE_ITEM)
proxyPlugin.registerExtensionSubtype("inventory.PURCHASE_ITEM", AgisInventoryClient.MSG_TYPE_PURCHASE_ITEM)
proxyPlugin.registerExtensionSubtype("inventory.SELL_ITEM", AgisInventoryClient.MSG_TYPE_SELL_ITEM)
proxyPlugin.registerExtensionSubtype("inventory.GET_MERCHANT_LIST", AgisInventoryClient.MSG_TYPE_GET_MERCHANT_LIST)
proxyPlugin.registerExtensionSubtype("ao.SET_WEAPON", AgisInventoryClient.MSG_TYPE_SET_WEAPON)
proxyPlugin.registerExtensionSubtype("ao.PURCHASE_SKIN", AgisInventoryClient.MSG_TYPE_PURCHASE_SKIN)
proxyPlugin.registerExtensionSubtype("ao.USE_ACCOUNT_ITEM", AgisInventoryClient.MSG_TYPE_USE_ACCOUNT_ITEM)
proxyPlugin.registerExtensionSubtype("ao.SET_SKIN_COLOUR", AgisInventoryClient.MSG_TYPE_SET_SKIN_COLOUR)
proxyPlugin.registerExtensionSubtype("ao.ALTER_ITEM_COUNT", AgisInventoryClient.MSG_TYPE_ALTER_ITEM_COUNT)
proxyPlugin.registerExtensionSubtype("inventory.GET_MAIL", AgisInventoryClient.MSG_TYPE_GET_MAIL)
proxyPlugin.registerExtensionSubtype("inventory.MAIL_READ", AgisInventoryClient.MSG_TYPE_MAIL_READ)
proxyPlugin.registerExtensionSubtype("inventory.MAIL_TAKE_ITEM", AgisInventoryClient.MSG_TYPE_MAIL_TAKE_ITEM)
proxyPlugin.registerExtensionSubtype("inventory.RETURN_MAIL", AgisInventoryClient.MSG_TYPE_RETURN_MAIL)
proxyPlugin.registerExtensionSubtype("inventory.DELETE_MAIL", AgisInventoryClient.MSG_TYPE_DELETE_MAIL)
proxyPlugin.registerExtensionSubtype("inventory.SEND_MAIL", AgisInventoryClient.MSG_TYPE_SEND_MAIL)
# Mob Client
proxyPlugin.registerExtensionSubtype("ao.CREATE_QUEST", AgisMobClient.MSG_TYPE_CREATE_QUEST)
proxyPlugin.registerExtensionSubtype("ao.EDIT_QUEST", AgisMobClient.MSG_TYPE_EDIT_QUEST)
proxyPlugin.registerExtensionSubtype("mob.CREATE_MOB_SPAWN", AgisMobClient.MSG_TYPE_CREATE_MOB_SPAWN)
proxyPlugin.registerExtensionSubtype("ao.VERIFY_ISLAND_ACCESS", AgisMobClient.MSG_TYPE_VERIFY_ISLAND_ACCESS)
proxyPlugin.registerExtensionSubtype("ao.ENTER_WORLD", AgisMobClient.MSG_TYPE_ENTER_WORLD)
proxyPlugin.registerExtensionSubtype("ao.REQUEST_DEVELOPER_ACCESS", AgisMobClient.MSG_TYPE_REQUEST_DEVELOPER_ACCESS)
proxyPlugin.registerExtensionSubtype("ao.CREATE_ISLAND", AgisMobClient.MSG_TYPE_CREATE_ISLAND)
proxyPlugin.registerExtensionSubtype("ao.VIEW_MARKERS", AgisMobClient.MSG_TYPE_VIEW_MARKERS)
proxyPlugin.registerExtensionSubtype("ao.REQUEST_SPAWN_DATA", AgisMobClient.MSG_TYPE_REQUEST_SPAWN_DATA)
proxyPlugin.registerExtensionSubtype("ao.EDIT_SPAWN_MARKER", AgisMobClient.MSG_TYPE_EDIT_SPAWN_MARKER)
proxyPlugin.registerExtensionSubtype("ao.DELETE_SPAWN_MARKER", AgisMobClient.MSG_TYPE_DELETE_SPAWN_MARKER)
proxyPlugin.registerExtensionSubtype("mob.GET_TEMPLATES", AgisMobClient.MSG_TYPE_GET_TEMPLATES)
proxyPlugin.registerExtensionSubtype("ao.CREATE_MOB", AgisMobClient.MSG_TYPE_CREATE_MOB)
proxyPlugin.registerExtensionSubtype("ao.EDIT_MOB", AgisMobClient.MSG_TYPE_EDIT_MOB)
proxyPlugin.registerExtensionSubtype("ao.CREATE_FACTION", AgisMobClient.MSG_TYPE_CREATE_FACTION)
proxyPlugin.registerExtensionSubtype("ao.EDIT_FACTION", AgisMobClient.MSG_TYPE_EDIT_FACTION)
proxyPlugin.registerExtensionSubtype("ao.CREATE_LOOT_TABLE", AgisMobClient.MSG_TYPE_CREATE_LOOT_TABLE)
proxyPlugin.registerExtensionSubtype("ao.DOME_ENQUIRY", AgisMobClient.MSG_TYPE_DOME_ENQUIRY)
proxyPlugin.registerExtensionSubtype("ao.DOME_ENTRY_REQUEST", AgisMobClient.MSG_TYPE_DOME_ENTRY_REQUEST)
proxyPlugin.registerExtensionSubtype("ao.DOME_LEAVE_REQUEST", AgisMobClient.MSG_TYPE_DOME_LEAVE_REQUEST)
proxyPlugin.registerExtensionSubtype("ao.ACTIVATE_DOME_ABILITY", AgisMobClient.MSG_TYPE_ACTIVATE_DOME_ABILITY)
proxyPlugin.registerExtensionSubtype("ao.OBJECT_ACTIVATED", AgisMobClient.MSG_TYPE_OBJECT_ACTIVATED)
proxyPlugin.registerExtensionSubtype("ao.DETECT_BUILDING_GRIDS", AgisMobClient.MSG_TYPE_DETECT_BUILDING_GRIDS)
proxyPlugin.registerExtensionSubtype("ao.GET_BUILDING_GRID_DATA", AgisMobClient.MSG_TYPE_GET_BUILDING_GRID_DATA)
proxyPlugin.registerExtensionSubtype("ao.PURCHASE_BUILDING_GRID", AgisMobClient.MSG_TYPE_PURCHASE_BUILDING_GRID)
proxyPlugin.registerExtensionSubtype("ao.CREATE_BUILDING", AgisMobClient.MSG_TYPE_CREATE_BUILDING)
proxyPlugin.registerExtensionSubtype("ao.USE_TRAP_DOOR", AgisMobClient.MSG_TYPE_USE_TRAP_DOOR)
proxyPlugin.registerExtensionSubtype("ao.HARVEST_RESOURCE_GRID", AgisMobClient.MSG_TYPE_HARVEST_RESOURCE_GRID)
proxyPlugin.registerExtensionSubtype("ao.SET_BLOCK", AgisMobClient.MSG_TYPE_SET_BLOCK)
proxyPlugin.registerExtensionSubtype("ao.GET_INTERACTION_OPTIONS", AgisMobClient.MSG_TYPE_GET_INTERACTION_OPTIONS)
proxyPlugin.registerExtensionSubtype("ao.MSG_TYPE_START_DIALOGUE", AgisMobClient.MSG_TYPE_START_DIALOGUE)
proxyPlugin.registerExtensionSubtype("ao.PLAY_COORD_EFFECT", AgisMobClient.MSG_TYPE_PLAY_COORD_EFFECT)
proxyPlugin.registerExtensionSubtype("ao.MOVEMENT_STATE", AgisMobClient.MSG_TYPE_SET_MOVEMENT_STATE)
proxyPlugin.registerExtensionSubtype("ao.SET_UNDERWATER", AgisMobClient.MSG_TYPE_SET_UNDERWATER)
proxyPlugin.registerExtensionSubtype("ao.CHANGE_INSTANCE", AgisMobClient.MSG_TYPE_CHANGE_INSTANCE)
# Group Client
proxyPlugin.registerExtensionSubtype("ao.GET_FRIENDS", GroupClient.MSG_TYPE_GET_FRIENDS)
proxyPlugin.registerExtensionSubtype("ao.ADD_FRIEND", GroupClient.MSG_TYPE_ADD_FRIEND)
#Arena Client
proxyPlugin.registerExtensionSubtype("ao.ACTIVATE_ARENA_ABILITY", ArenaClient.MSG_TYPE_ACTIVATE_ARENA_ABILITY)
proxyPlugin.registerExtensionSubtype("ao.COMPLETE_TUTORIAL", ArenaClient.MSG_TYPE_COMPLETE_TUTORIAL)
proxyPlugin.registerExtensionSubtype("ao.CHANGE_RACE", ArenaClient.MSG_TYPE_CHANGE_RACE)
proxyPlugin.registerExtensionSubtype("arena.getTypes", ArenaClient.MSG_TYPE_GET_ARENA_TYPES)
proxyPlugin.registerExtensionSubtype("arena.joinQueue", ArenaClient.MSG_TYPE_JOIN_QUEUE)
proxyPlugin.registerExtensionSubtype("arena.leaveQueue", ArenaClient.MSG_TYPE_LEAVE_QUEUE)
proxyPlugin.registerExtensionSubtype("arena.pickupFlag", ArenaClient.MSG_TYPE_PICKUP_FLAG)
proxyPlugin.registerExtensionSubtype("arena.dropFlag", ArenaClient.MSG_TYPE_DROP_FLAG)
proxyPlugin.registerExtensionSubtype("arena.activateMachine", ArenaClient.MSG_TYPE_ACTIVATE_MACHINE)
proxyPlugin.registerExtensionSubtype("ao.SELECT_RACE", ArenaClient.MSG_TYPE_SELECT_RACE)
#Crafting Client
proxyPlugin.registerExtensionSubtype("crafting.HARVEST_RESOURCE", CraftingClient.MSG_TYPE_HARVEST_RESOURCE)
proxyPlugin.registerExtensionSubtype("crafting.GATHER_RESOURCE", CraftingClient.MSG_TYPE_GATHER_RESOURCE)
proxyPlugin.registerExtensionSubtype("crafting.CRAFT_ITEM", CraftingClient.MSG_TYPE_CRAFTING_CRAFT_ITEM)
proxyPlugin.registerExtensionSubtype("crafting.GRID_UPDATED", CraftingClient.MSG_TYPE_CRAFTING_GRID_UPDATED)
proxyPlugin.registerExtensionSubtype("crafting.GET_BLUEPRINTS", CraftingClient.MSG_TYPE_GET_BLUEPRINTS)
#Voxel Client
proxyPlugin.registerExtensionSubtype("voxel.CREATE_CLAIM", VoxelClient.MSG_TYPE_CREATE_CLAIM)
proxyPlugin.registerExtensionSubtype("voxel.EDIT_CLAIM", VoxelClient.MSG_TYPE_EDIT_CLAIM)
proxyPlugin.registerExtensionSubtype("voxel.PURCHASE_CLAIM", VoxelClient.MSG_TYPE_PURCHASE_CLAIM)
proxyPlugin.registerExtensionSubtype("voxel.SELL_CLAIM", VoxelClient.MSG_TYPE_SELL_CLAIM)
proxyPlugin.registerExtensionSubtype("voxel.DELETE_CLAIM", VoxelClient.MSG_TYPE_DELETE_CLAIM)
proxyPlugin.registerExtensionSubtype("voxel.CLAIM_PERMISSION", VoxelClient.MSG_TYPE_CLAIM_PERMISSION)
proxyPlugin.registerExtensionSubtype("voxel.CLAIM_ACTION", VoxelClient.MSG_TYPE_CLAIM_ACTION)
proxyPlugin.registerExtensionSubtype("voxel.PLACE_CLAIM_OBJECT", VoxelClient.MSG_TYPE_PLACE_CLAIM_OBJECT)
proxyPlugin.registerExtensionSubtype("voxel.EDIT_CLAIM_OBJECT", VoxelClient.MSG_TYPE_EDIT_CLAIM_OBJECT)
proxyPlugin.registerExtensionSubtype("voxel.GET_RESOURCES", VoxelClient.MSG_TYPE_GET_RESOURCES)
proxyPlugin.registerExtensionSubtype("voxel.NO_BUILD_CLAIM_TRIGGER", VoxelClient.MSG_TYPE_NO_BUILD_CLAIM_TRIGGER)
proxyPlugin.registerExtensionSubtype("voxel.UPGRADE_BUILDING_OBJECT", VoxelClient.MSG_TYPE_UPGRADE_BUILDING_OBJECT)
proxyPlugin.registerExtensionSubtype("voxel.GET_CLAIM_OBJECT_INFO", VoxelClient.MSG_TYPE_GET_CLAIM_OBJECT_INFO)
proxyPlugin.registerExtensionSubtype("voxel.ATTACK_BUILDING_OBJECT", VoxelClient.MSG_TYPE_ATTACK_BUILDING_OBJECT)
#Faction Client
proxyPlugin.registerExtensionSubtype("faction.UPDATE_PVP_STATE", FactionClient.MSG_TYPE_UPDATE_PVP_STATE)

Log.debug("extensions_proxy.py: LOADED")
