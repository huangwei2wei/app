// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import atavism.server.marshalling.Marshallable;

public class GenericMessage extends Message implements Marshallable
{
    protected Serializable data;
    protected Map<String, Serializable> properties;
    private static final long serialVersionUID = 1L;
    
    public GenericMessage() {
    }
    
    public GenericMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public Serializable getProperty(final String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }
    
    public void setProperty(final String key, final Serializable value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Serializable>();
        }
        this.properties.put(key, value);
    }
    
    public Map<String, Serializable> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Serializable> props) {
        this.properties = props;
    }
    
    public void addProperties(final Map<String, Serializable> props) {
        this.properties.putAll(props);
    }
    
    public Serializable getData() {
        return this.data;
    }
    
    public void setData(final Serializable data) {
        this.data = data;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.data != null) {
            flag_bits = 1;
        }
        if (this.properties != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.data != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.data);
        }
        if (this.properties != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.properties);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.data = (Serializable)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.properties = (Map<String, Serializable>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
