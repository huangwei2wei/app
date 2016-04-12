from atavism.mars import *
from atavism.mars.core import *
from atavism.mars.objects import *
from atavism.mars.util import *
from atavism.server.math import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from atavism.server.plugins import *

Log.debug("trainerplugin.py: starting")
Engine.registerPlugin("atavism.mars.plugins.TrainerPlugin")
Log.debug("trainerplugin.py: done")
