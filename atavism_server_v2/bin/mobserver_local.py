from atavism.agis import *
from atavism.server.worldmgr import *
from atavism.agis.objects import *
from atavism.agis.util import *
from atavism.server.math import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from atavism.msgsys import *

# Uncomment if you want to set a log level for this process
# that is different from the server's default log level
#Log.setLogLevel(1)

#
# set us up as a mob server
#
#Engine.setServerID(10)
#Engine.isEntityManager(true)
#Engine.setWorldID(3)
#Engine.setPort(5200)

#Engine.msgSvrHostname = "localhost"
#Engine.msgSvrPort = 20374

Engine.setBasicInterpolatorInterval(5000)

World.setGeometry(Geometry.maxGeometry())

#
# add world servers to the world server manager
#
# Log.debug("Connnecting to world servers")
# wsMgr = Engine.getWSManager()
# wsMgr.addWorldServer(WorldServer("localhost", 5090, 0))
# wsMgr.addWorldServer(WorldServer("localhost", 5091, 1))

Log.debug("mobserver_local: done with local config")
