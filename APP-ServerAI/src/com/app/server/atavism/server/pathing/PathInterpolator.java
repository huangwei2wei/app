// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import com.app.server.atavism.server.math.AOVector;
import com.app.server.atavism.server.math.Point;
import java.util.List;
import com.app.server.atavism.server.engine.OID;

public abstract class PathInterpolator
{
    protected OID oid;
    protected float speed;
    protected String terrainString;
    protected List<Point> path;
    protected float totalTime;
    protected long startTime;
    
    public PathInterpolator(final OID oid, final long startTime, final float speed, final String terrainString, final List<Point> path) {
        this.oid = oid;
        this.startTime = startTime;
        this.speed = speed;
        this.terrainString = terrainString;
        this.path = path;
    }
    
    @Override
    public abstract String toString();
    
    public abstract PathLocAndDir interpolate(final float p0);
    
    public PathLocAndDir interpolate(final long systemTime) {
        return this.interpolate((systemTime - this.startTime) / 1000.0f);
    }
    
    public Point zeroYIfOnTerrain(final AOVector loc, final int pointIndex) {
        final Point iloc = new Point(loc);
        return iloc;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public float getSpeed() {
        return this.speed;
    }
    
    public String getTerrainString() {
        return this.terrainString;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public float getTotalTime() {
        return this.totalTime;
    }
    
    public Point getLastPoint() {
        final int len = this.path.size();
        if (len > 0) {
            return this.path.get(len - 1);
        }
        return null;
    }
}
