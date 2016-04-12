from atavism.server.engine import *
from atavism.server.util import *
from atavism.server.plugins import *

# Uncomment if you want to set a log level for this process
# that is different from the server's default log level
#Log.setLogLevel(1)

# Engine.MAX_NETWORK_BUF_SIZE = 1000

ProxyPlugin.MaxConcurrentUsers = 1000

