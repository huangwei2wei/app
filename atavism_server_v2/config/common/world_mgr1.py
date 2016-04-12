from atavism.server.engine import *
from atavism.agis.plugins import *
from atavism.agis.objects import *

wmgr = AgisWorldManagerPlugin()

Engine.registerPlugin(wmgr)
