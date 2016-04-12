// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.LockFactory;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class RegionConfig implements Serializable, Marshallable
{
    private Map<String, Object> propMap;
    private String type;
    protected transient Lock lock;
    private static final long serialVersionUID = 1L;
    
    public RegionConfig() {
        this.propMap = new HashMap<String, Object>();
        this.setupTransient();
    }
    
    public RegionConfig(final String type) {
        this.propMap = new HashMap<String, Object>();
        this.setupTransient();
        this.setType(type);
    }
    
    private void setupTransient() {
        this.lock = LockFactory.makeLock("RegionConfigLock");
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    @Override
    public String toString() {
        return "[RegionConfig type=" + this.type + "]";
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public void setProperty(final String key, final Object value) {
        this.lock.lock();
        try {
            this.propMap.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object getProperty(final String key) {
        this.lock.lock();
        try {
            return this.propMap.get(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.propMap != null) {
            flag_bits = 1;
        }
        if (this.type != null && this.type != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.propMap != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.propMap);
        }
        if (this.type != null && this.type != "") {
            buf.putString(this.type);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.propMap = (Map<String, Object>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.type = buf.getString();
        }
        return this;
    }
}
