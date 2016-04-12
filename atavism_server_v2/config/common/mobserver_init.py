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
from java.lang import *

# this file gets run before the mobserver manager plugin gets
# registered.  these are the default Atavism behaviors
# if you want to add your own, add it to config/<world>/mobserver_init.py

#//////////////////////////////////////////////////////////////////
#//
#// standard npc behavior
#//
#//////////////////////////////////////////////////////////////////
WEObjFactory.registerBehaviorClass("BaseBehavior", "atavism.server.engine.BaseBehavior")
WEObjFactory.registerBehaviorClass("CombatBehavior", "atavism.agis.behaviors.CombatBehavior")
WEObjFactory.registerBehaviorClass("RadiusRoamBehavior", "atavism.agis.behaviors.RadiusRoamBehavior")
WEObjFactory.registerBehaviorClass("PatrolBehavior", "atavism.agis.behaviors.PatrolBehavior")
