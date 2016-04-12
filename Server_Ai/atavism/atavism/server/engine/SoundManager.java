// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.objects.Entity;

public class SoundManager
{
    public static final String AMBIENTSOUND;
    
    static {
        AMBIENTSOUND = (String)Entity.registerTransientPropertyKey("sndMgr.Ambient");
    }
}
