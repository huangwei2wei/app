// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Iterator;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import atavism.server.objects.DisplayContext;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class DCMap implements Serializable, Marshallable
{
    DisplayContext defaultDC;
    Map<DisplayContext, DisplayContext> map;
    Lock lock;
    private static final long serialVersionUID = 1L;
    
    public DCMap() {
        this.map = new HashMap<DisplayContext, DisplayContext>();
        this.lock = LockFactory.makeLock("DCMap");
    }
    
    public void add(final DisplayContext base, final DisplayContext target) {
        this.lock.lock();
        try {
            this.map.put(base, target);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public DisplayContext get(final DisplayContext base) {
        this.lock.lock();
        try {
            final DisplayContext dc = this.map.get(base);
            if (dc != null) {
                return dc;
            }
            for (final Map.Entry<DisplayContext, DisplayContext> entry : this.map.entrySet()) {
                final DisplayContext key = entry.getKey();
                if (key.subsetOf(base)) {
                    return entry.getValue();
                }
            }
            return this.defaultDC;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public DisplayContext getDefault() {
        return this.defaultDC;
    }
    
    public void setDefault(final DisplayContext dc) {
        this.defaultDC = dc;
    }
    
    public Map<DisplayContext, DisplayContext> getMap() {
        this.lock.lock();
        try {
            return new HashMap<DisplayContext, DisplayContext>(this.map);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setMap(final Map<DisplayContext, DisplayContext> map) {
        this.lock.lock();
        try {
            this.map = new HashMap<DisplayContext, DisplayContext>(map);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        final byte flags = (byte)(((this.defaultDC != null) ? 1 : 0) | ((this.map == null) ? 0 : 2));
        buf.putByte(flags);
        if (this.defaultDC != null) {
            MarshallingRuntime.marshalMarshallingObject(buf, (Object)this.defaultDC);
        }
        if (this.map != null) {
            MarshallingRuntime.marshalHashMap(buf, (Object)this.map);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flags = buf.getByte();
        if ((flags & 0x1) != 0x0) {
            MarshallingRuntime.unmarshalMarshallingObject(buf, (Object)this.defaultDC);
        }
        if ((flags & 0x2) != 0x0) {
            this.map = (Map<DisplayContext, DisplayContext>)MarshallingRuntime.unmarshalHashMap(buf);
        }
        return this;
    }
}
