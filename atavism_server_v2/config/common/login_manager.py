from atavism.agis import *
from atavism.agis.objects import *
from atavism.agis.util import *
from atavism.server.math import *
from atavism.server.events import *
from atavism.server.objects import *
from atavism.server.engine import *
from atavism.server.util import *
from atavism.msgsys import *

# test master key
pubkey = """AAAAAAAAAAwAAAADRFNBMIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2
            EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7
            ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUA
            l2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdR
            WVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx
            +2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBALJagda9mnhVzF1Z
            +p0XcU9uzzjtNe+J3MoYiioRKlXlk3KV1oX+L3uj27bBnZHWYe6hi7pN9aex+AjC7FjnPtyyBE+F
            v7HHp1/YOcVqn2oq9VglOSJV/qhn+FVaRCF5s9tYPyyIVvwMi0oLZgNysRRrVoqpHfmftqbYFhYo
            EfYp"""

SecureTokenManager.getInstance().registerMasterPublicKey(Base64.decode(pubkey))

# production master key id=1
pubkey = """AAAAAAAAAAEAAAADRFNBMIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2
            EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7
            ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUA
            l2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdR
            WVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx
            +2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAI1obFDPxAHhfzeY
            pMSxJhplwKldDVBpG3TNAj18FaoqMsWq2mjI1VN2i9jLjhhbl7DgFIWvXBqaJ8BO75uGMQL+uEvl
            GQaQ7ClgGpWn0YLxUd1Hja+Q7SmKnkWmrhMYiq84O/2GP6hTfmidVd7STy3PoXuSf50Ph2tumuai
            UyZ5"""

SecureTokenManager.getInstance().registerMasterPublicKey(Base64.decode(pubkey))

# production master key id=7
pubkey = """AAAAAAAAAAcAAAADRFNBMIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2
            EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7
            ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUA
            l2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdR
            WVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx
            +2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGADI9k/sOlM8WiR4HY
            AX92O1+/9MRHsnZ34IpnZD6fSyOIPN/NkPOPTVCS915GVOSDeELAQ7kznTDKx6eOJU7eO4Tnsdct
            b4MAiwvuCjdleGmFTLiK8OLA5GJWseDu8dNsh3tyJI1Tn595M2X9ise4TYQ+fJj8k1WDuWAds1mm
            Vd8="""

SecureTokenManager.getInstance().registerMasterPublicKey(Base64.decode(pubkey))

Engine.registerPlugin("atavism.agis.plugins.AgisLoginPlugin")
