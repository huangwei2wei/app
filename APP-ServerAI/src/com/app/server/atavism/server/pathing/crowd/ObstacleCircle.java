// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.crowd;

public class ObstacleCircle
{
    public float[] p;
    public float[] vel;
    public float[] dvel;
    public float rad;
    public float[] dp;
    public float[] np;
    
    public ObstacleCircle() {
        this.p = new float[3];
        this.vel = new float[3];
        this.dvel = new float[3];
        this.dp = new float[3];
        this.np = new float[3];
    }
}
