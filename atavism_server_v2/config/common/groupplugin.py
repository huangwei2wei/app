from atavism.agis import *
from atavism.agis.core import *
from atavism.agis.objects import *
from atavism.agis.util import *
from atavism.server.math import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from atavism.server.plugins import *

Log.debug("groupplugin.py: starting")
classesPlugin = Engine.registerPlugin("atavism.agis.plugins.GroupPlugin")
Log.debug("groupplugin.py: done")