// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.engine.Namespace;

public class SocialClient
{
    public static Namespace NAMESPACE;
    
    static {
        SocialClient.NAMESPACE = Namespace.intern("social");
    }
}
