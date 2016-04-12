// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.detour;

import java.util.EnumSet;

public class QueryData
{
    public EnumSet<Status> Status;
    public Node LastBestNode;
    public float LastBestNodeCost;
    public long StartRef;
    public long EndRef;
    public float[] StartPos;
    public float[] EndPos;
    public QueryFilter Filter;
    
    public QueryData() {
        this.Status = EnumSet.noneOf(Status.class);
        this.StartPos = new float[3];
        this.EndPos = new float[3];
    }
}
