// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import atavism.server.math.Point;
import atavism.server.pathing.PathLocAndDir;
import atavism.server.pathing.PathInterpolator;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.util.Log;
import java.util.HashSet;

public class BasicInterpolator implements Interpolator<BasicInterpolatable>, Runnable
{
    transient HashSet<BasicInterpolatable> interpSet;
    
    public BasicInterpolator() {
        this.interpSet = new HashSet<BasicInterpolatable>();
    }
    
    public BasicInterpolator(final int updateInterval) {
        this.interpSet = new HashSet<BasicInterpolatable>();
        this.startUpdates(updateInterval);
    }
    
    @Override
    public synchronized void register(final BasicInterpolatable obj) {
        this.interpSet.add(obj);
    }
    
    @Override
    public synchronized void unregister(final BasicInterpolatable obj) {
        this.interpSet.remove(obj);
    }
    
    @Override
    public void interpolate(final BasicInterpolatable obj) {
        final long time = System.currentTimeMillis();
        final long lastInterp = obj.getLastInterp();
        final long timeDelta = time - lastInterp;
        if (timeDelta < 100L) {
            return;
        }
        final PathInterpolator pathInterpolator = obj.getPathInterpolator();
        PathLocAndDir locAndDir = null;
        Point interpLoc = null;
        AOVector dir;
        if (pathInterpolator != null) {
            Log.debug("BasicInterpolator.interpolate calling pathInterpolator");
            locAndDir = pathInterpolator.interpolate(time);
            if (locAndDir == null) {
                dir = new AOVector(0.0f, 0.0f, 0.0f);
                final Point p = pathInterpolator.getLastPoint();
                if (p != null) {
                    interpLoc = p;
                }
            }
            else {
                interpLoc = locAndDir.getLoc();
                dir = locAndDir.getDir();
                if (Log.loggingDebug) {
                    Log.debug("BasicInterpolator.interpolate pathInterpolator returned loc = " + interpLoc);
                }
            }
        }
        else {
            dir = obj.getDir();
            if (dir == null || dir.isZero()) {
                obj.setLastInterp(time);
                return;
            }
            interpLoc = obj.getInterpLoc();
            if (interpLoc == null) {
                return;
            }
            final AOVector dirCopy = new AOVector(dir);
            dirCopy.scale((float)(timeDelta / 1000.0));
            interpLoc.add((int)dirCopy.getX(), (int)dirCopy.getY(), (int)dirCopy.getZ());
        }
        final AOVector ndir = new AOVector(dir.getX(), 0.0f, dir.getZ());
        final float length = ndir.length();
        Quaternion orient;
        if (length != 0.0f) {
            ndir.normalize();
            orient = Quaternion.fromVectorRotation(new AOVector(0.0f, 0.0f, 1.0f), ndir);
        }
        else {
            orient = Quaternion.Identity;
        }
        obj.setPathInterpolatorValues(time, dir, interpLoc, orient);
    }
    
    public void startUpdates(final int interval) {
        if (Log.loggingDebug) {
            Log.debug("BasicInterpolator.startUpdates: updating with interval=" + interval);
        }
        Engine.getExecutor().scheduleAtFixedRate(this, interval, interval, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void run() {
        if (Log.loggingDebug) {
            Log.debug("BasicInterpolator.run: interpolating all objects");
        }
        final HashSet<BasicInterpolatable> objects;
        synchronized (this) {
            objects = (HashSet<BasicInterpolatable>)this.interpSet.clone();
        }
        for (final BasicInterpolatable obj : objects) {
            this.interpolate(obj);
        }
    }
}
