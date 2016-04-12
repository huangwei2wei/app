from atavism.agis import *
from atavism.agis.objects import *
from atavism.agis.util import *
from atavism.server.math import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from atavism.msgsys import *
from atavism.simpleclient import *
from java.lang import *

loginType = 4

loginResponseHandler = LoginResponseHandler()
SimpleClient.getDispatcher().registerHandler(loginType,
                                             loginResponseHandler)

# set up the login response handler to exit when it gets the msg
# this is useful for a monitoring script
LoginResponseHandler.EXIT_ON_MSG = 1

# start a thread that will exit after 30 seconds
# this is in case the server actually accepts our
# connection, but never sends us a login response
class ExitThread (Runnable):
    def run(self):
        Log.debug("ExitThread: waiting for 30 seconds")
        Thread.sleep(30000)
        Log.error("ExitThread: exited due to no login response")
        System.exit(1)

exitThread = ExitThread()
Thread(exitThread).start()

Log.debug("completed simpleclient.py")
