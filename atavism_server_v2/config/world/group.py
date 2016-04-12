from atavism.agis.plugins import GroupPlugin

##
# Group Configuration File
##

# Register stats for group object to track and send to each client
#GroupPlugin.RegisterStat("health")
#GroupPlugin.RegisterStat("health-max")
#GroupPlugin.RegisterStat("mana")
#GroupPlugin.RegisterStat("mana-max")
GroupPlugin.RegisterStat("arena_level")

# Set maximum group size
GroupPlugin.SetMaxGroupSize(4)
