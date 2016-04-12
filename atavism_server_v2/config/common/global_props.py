from atavism.agis import *
from atavism.agis.objects import *
from atavism.agis.core import *
from atavism.agis.util import *
from atavism.server.math import *
from atavism.server.util import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *

True=1
False=0

# define the standard mob equippable slots
defaultSlots = AgisEquipInfo("default")
defaultSlots.addEquipSlot(AgisEquipSlot.PRIMARYWEAPON)

World.addTheme("agis.toc")

# Change to False to allow flying/swimming (with page up/down)
World.FollowsTerrainOverride = True
