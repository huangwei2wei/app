from atavism.server.util import *
# from atavism.simpleclient import *
from java.lang import *

# PlayerClient instance

Log.debug("playerclient.py starting PlayerThread");

#playerClient = PlayerClient("--zero_y --position (63505,71222,300303) --polygon 54848,315218,53685,284092,-69679,284014,-69527,314322")
playerClient = PlayerClient("--random_start")

Log.debug("completed playerclient.py")
