// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class OID implements Serializable, Marshallable, Comparable<OID>
{
    private static final long serialVersionUID = 1L;
    private long data;
    
    public static OID fromLong(final long l) {
        if (l == 0L) {
            return null;
        }
        final OID rv = new OID();
        rv.data = l;
        return rv;
    }
    
    public long toLong() {
        return this.data;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final String hexString = Long.toHexString(this.data);
        for (int i = hexString.length(); i < 16; ++i) {
            sb.append('0');
        }
        sb.append(hexString);
        return sb.toString();
    }
    
    public static OID fromString(final String oidString) throws NumberFormatException {
        final Long oidLong = Long.parseLong(oidString, 16);
        return fromLong(oidLong);
    }
    
    public void setData(final long l) {
        this.data = l;
    }
    
    public long getData() {
        return this.data;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof OID && ((OID)other).data == this.data;
    }
    
    @Override
    public int hashCode() {
        return (int)this.data;
    }
    
    @Override
    public int compareTo(final OID other) {
        if (this.data < other.data) {
            return -1;
        }
        if (this.data > other.data) {
            return 1;
        }
        return 0;
    }
    
    public static OID parseLong(final String str) throws NumberFormatException {
        return fromLong(Long.parseLong(str));
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        buf.putOID(this);
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        return buf.getOID();
    }
}
