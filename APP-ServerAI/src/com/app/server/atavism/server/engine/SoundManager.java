// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import com.app.server.atavism.server.objects.Entity;

public class SoundManager
{
    public static final String AMBIENTSOUND;
    
    static {
        AMBIENTSOUND = (String)Entity.registerTransientPropertyKey("sndMgr.Ambient");
    }
}
