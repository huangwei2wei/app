// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.crowd;

public enum UpdateFlags
{
    None(0), 
    AnticipateTurns(1), 
    ObstacleAvoidance(2), 
    Separation(4), 
    OptimizeVisibility(8), 
    OptimizeTopology(16);
    
    private final int id;
    
    private UpdateFlags(final int id) {
        this.id = id;
    }
    
    public int getValue() {
        return this.id;
    }
}
