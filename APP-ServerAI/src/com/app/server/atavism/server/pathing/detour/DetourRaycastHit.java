// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

import java.util.EnumSet;

public class DetourRaycastHit
{
    public int pathCount;
    public float t;
    public EnumSet<Status> status;
    
    public DetourRaycastHit() {
        this.status = EnumSet.noneOf(Status.class);
    }
}
