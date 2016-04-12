// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.LinkedList;
import java.util.List;
import atavism.server.marshalling.Marshallable;

public class SearchSelection implements Marshallable
{
    public static final int RESULT_KEYED = 1;
    public static final int RESULT_KEY_ONLY = 2;
    private int resultOption;
    private boolean selectAllProperties;
    private long propFlags;
    private List<String> properties;
    
    public SearchSelection() {
    }
    
    public SearchSelection(final long propFlags) {
        this.setPropFlags(propFlags);
    }
    
    public SearchSelection(final long propFlags, final int resultOption) {
        this.setPropFlags(propFlags);
        this.setResultOption(resultOption);
    }
    
    public SearchSelection(final List<String> properties) {
        this.setProperties(properties);
    }
    
    public SearchSelection(final List<String> properties, final int resultOption) {
        this.setProperties(properties);
        this.setResultOption(resultOption);
    }
    
    public int getResultOption() {
        return this.resultOption;
    }
    
    public void setResultOption(final int option) {
        this.resultOption = option;
    }
    
    public List<String> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final List<String> props) {
        this.properties = props;
    }
    
    public void addProperty(final String property) {
        if (this.properties == null) {
            this.properties = new LinkedList<String>();
        }
        this.properties.add(property);
    }
    
    public void removeProperty(final String property) {
        if (this.properties != null) {
            this.properties.remove(property);
        }
    }
    
    public boolean getAllProperties() {
        return this.selectAllProperties;
    }
    
    public void setAllProperties(final boolean selectAllProperties) {
        this.selectAllProperties = selectAllProperties;
    }
    
    public long getPropFlags() {
        return this.propFlags;
    }
    
    public void setPropFlags(final long flags) {
        this.propFlags = flags;
    }
    
    public void addPropFlag(final long flag) {
        this.propFlags |= flag;
    }
    
    public void removePropFlag(final long flag) {
        this.propFlags &= ~flag;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.properties != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        buf.putInt(this.resultOption);
        buf.putByte((byte)(byte)(this.selectAllProperties ? 1 : 0));
        buf.putLong(this.propFlags);
        if (this.properties != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.properties);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        this.resultOption = buf.getInt();
        this.selectAllProperties = (buf.getByte() != 0);
        this.propFlags = buf.getLong();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.properties = (List<String>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
