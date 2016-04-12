// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

import java.util.EnumSet;

public class DetourStatusReturn
{
    public int intValue;
    public EnumSet<Status> status;
    
    public DetourStatusReturn() {
        this.status = EnumSet.noneOf(Status.class);
    }
}
