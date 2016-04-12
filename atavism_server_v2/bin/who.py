proxyPlugin = Engine.getPlugin("Proxy1")

playerNames = proxyPlugin.getPlayerNames()
for name in playerNames:
    print name
print str(len(playerNames)) + " players logged in currently."
