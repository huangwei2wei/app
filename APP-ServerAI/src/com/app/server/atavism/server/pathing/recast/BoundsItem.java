// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.recast;

public class BoundsItem
{
    public float[] bmin;
    public float[] bmax;
    public int i;
    
    public BoundsItem() {
        this.bmin = new float[2];
        this.bmax = new float[2];
        this.i = 0;
    }
}
