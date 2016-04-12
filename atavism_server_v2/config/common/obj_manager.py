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

objectManager = ObjectManagerPlugin()

# Register TemplateHook if defined.  This guarantees that templates are
# defined with the ObjectManagerPlugin because subsequent plugins depend
# on the templates.
#
try:
    objectManager.registerActivateHook(TemplateHook())
except NameError:
    pass

Engine.registerPlugin(objectManager)

Engine.registerPlugin("atavism.agis.plugins.AgisInventoryPlugin")
