// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class SearchEntry implements Marshallable
{
    public Object key;
    public Object value;
    
    public SearchEntry() {
    }
    
    public SearchEntry(final Object key, final Object value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "[SearchEntry key=" + this.key + " value=" + this.value + "]";
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.key != null) {
            flag_bits = 1;
        }
        if (this.value != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.key != null) {
            MarshallingRuntime.marshalObject(buf, this.key);
        }
        if (this.value != null) {
            MarshallingRuntime.marshalObject(buf, this.value);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.key = MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.value = MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
