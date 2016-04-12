// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.HashMap;
import java.util.Map;
import atavism.server.marshalling.Marshallable;

public class GenericResponseMessage extends ResponseMessage implements Marshallable
{
    protected Object data;
    protected Map<String, Object> properties;
    private static final long serialVersionUID = 1L;
    
    public GenericResponseMessage() {
    }
    
    public GenericResponseMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public GenericResponseMessage(final Message requestMessage) {
        super(requestMessage);
    }
    
    public GenericResponseMessage(final Message requestMessage, final Object data) {
        super(requestMessage);
        this.setData(data);
    }
    
    public Object getProperty(final String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }
    
    public void setProperty(final String key, final Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.put(key, value);
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Object> props) {
        this.properties = props;
    }
    
    public void addProperties(final Map<String, Object> props) {
        this.properties.putAll(props);
    }
    
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object data) {
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
            MarshallingRuntime.marshalObject(buf, this.data);
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
            this.data = MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.properties = (Map<String, Object>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
