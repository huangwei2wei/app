// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import atavism.server.util.LockFactory;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class RoadRegionConfig extends RegionConfig implements Serializable, Marshallable
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.roadSet != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.roadSet != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.roadSet);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.roadSet = (Set<Road>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
