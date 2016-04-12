from atavism.server.engine import *
from atavism.server.util import *
from atavism.server.plugins import *
from atavism.agis.util import *

ms = MasterServer.getMasterServer()
#ms.setTCPPort(9005)
#ms.setRDPPort(9010)

#connector = RemotePhpAccountConnector()
#connector.setUrl("http://www.yourdomain.com/verifyAccount.php")
#ms.setRemoteConnector(connector)

# Uncomment if you want to set a log level for this process
# that is different from the server's default log level
#Log.setLogLevel(1)
privKey = "AAAAAAAAAAcAAAADRFNBMIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUNX/FZu+bWSNTErP890FXgMHUIoQ="

SecureTokenManager.getInstance().initMaster(Base64.decode(privKey))

#flashPolicyPlugin = FlashPolicyPlugin()
#Engine.registerPlugin(flashPolicyPlugin)