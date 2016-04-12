// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import atavism.server.util.Log;
import atavism.server.objects.RemoteAccountConnector;

public class RemoteDatabaseAccountConnector extends RemoteAccountConnector
{
    public boolean verifyAccount(final String accountName, final String password) {
        Log.debug("CONNECTOR: verifying account with remote database");
        return true;
    }
}
