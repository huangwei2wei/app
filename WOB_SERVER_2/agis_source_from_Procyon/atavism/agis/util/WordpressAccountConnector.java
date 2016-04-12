// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import atavism.server.util.Log;
import atavism.server.objects.RemoteAccountConnector;

public class WordpressAccountConnector extends RemoteAccountConnector
{
    private String url;
    
    public WordpressAccountConnector() {
        this.url = "http://yourdomain.com/verifyWordpressAccount.php";
    }
    
    public boolean verifyAccount(final String accountName, final String password) {
        Log.debug("CONNECTOR: verifying account with wordpress connection");
        return false;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
