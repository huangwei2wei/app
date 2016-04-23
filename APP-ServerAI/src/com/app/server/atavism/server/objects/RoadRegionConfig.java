// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.util.Collection;
import java.util.HashSet;
import com.app.server.atavism.server.util.LockFactory;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.io.Serializable;

public class RoadRegionConfig extends RegionConfig implements Serializable
{
    transient Lock lock;
    Set<Road> roadSet;
    public static String RegionType;
    private static final long serialVersionUID = 1L;
    
    public RoadRegionConfig() {
        this.lock = LockFactory.makeLock("RoadRegionLock");
        this.roadSet = new HashSet<Road>();
        this.setType(RoadRegionConfig.RegionType);
    }
    
    @Override
    public String toString() {
        return "[RoadConfig]";
    }
    
    public void addRoad(final Road road) {
        this.lock.lock();
        try {
            this.roadSet.add(road);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Set<Road> getRoads() {
        this.lock.lock();
        try {
            return new HashSet<Road>(this.roadSet);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    static {
        RoadRegionConfig.RegionType = (String)Entity.registerTransientPropertyKey("RoadRegion");
    }
}
