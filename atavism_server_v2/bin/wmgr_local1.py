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

#Engine.msgSvrHostname = "localhost"
#Engine.msgSvrPort = 20374

Engine.setBasicInterpolatorInterval(5000)

# set the world geometry for this server
worldGeo = Geometry.maxGeometry()
World.setGeometry(worldGeo)

localGeo = Geometry.maxGeometry()
if (Engine.getProperty("atavism.dualworldmanagers") == "1"):
    Log.debug("wmgr_local1.py: using dual world manager config")
    localGeo = Geometry(-2147483647, -2, -2147483647, 2147483647)

World.setLocalGeometry(localGeo)

World.perceiverRadius = 100
#QuadTree.setMaxObjects(3)
World.setLocTolerance(20)

World.setDefaultPermission(AgisPermissionFactory())
