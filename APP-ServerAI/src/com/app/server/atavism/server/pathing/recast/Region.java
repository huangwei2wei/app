// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.recast;

public class Region
{
    public int SpanCount;
    public int Id;
    public short AreaType;
    public Boolean Remap;
    public Boolean Visited;
    public IntArray Connections;
    public IntArray Floors;
    
    public Region(final int i) {
        this.SpanCount = 0;
        this.Id = i;
        this.AreaType = 0;
        this.Remap = false;
        this.Visited = false;
        this.Connections = new IntArray();
        this.Floors = new IntArray();
    }
}
