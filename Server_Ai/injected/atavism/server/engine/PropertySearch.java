// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Map;
import atavism.server.marshalling.Marshallable;

public class PropertySearch implements SearchClause, Marshallable
{
    private Map queryProps;
    
    public PropertySearch() {
    }
    
    public PropertySearch(final Map queryProps) {
        this.setProperties(queryProps);
    }
    
    public Map getProperties() {
        return this.queryProps;
    }
    
    public void setProperties(final Map queryProps) {
        this.queryProps = queryProps;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.queryProps != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.queryProps != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.queryProps);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.queryProps = (Map)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
