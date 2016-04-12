// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class SoundData implements Serializable, Marshallable
{
    private String fileName;
    private String type;
    private Map<String, String> properties;
    private static final long serialVersionUID = 1L;
    
    public SoundData() {
        this.fileName = null;
        this.type = null;
        this.properties = null;
    }
    
    public SoundData(final String fileName, final String type, final Map<String, String> properties) {
        this.fileName = null;
        this.type = null;
        this.properties = null;
        this.setFileName(fileName);
        this.setType(type);
        this.setProperties(properties);
    }
    
    @Override
    public String toString() {
        return "[SoundData: FileName=" + this.getFileName() + ", Type=" + this.getType() + ", Properties=" + this.getProperties() + "]";
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setProperties(final Map<String, String> properties) {
        this.properties = properties;
    }
    
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    public void addProperty(final String key, final String value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties.put(key, value);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.fileName != null && this.fileName != "") {
            flag_bits = 1;
        }
        if (this.type != null && this.type != "") {
            flag_bits |= 0x2;
        }
        if (this.properties != null) {
            flag_bits |= 0x4;
        }
        buf.putByte(flag_bits);
        if (this.fileName != null && this.fileName != "") {
            buf.putString(this.fileName);
        }
        if (this.type != null && this.type != "") {
            buf.putString(this.type);
        }
        if (this.properties != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.properties);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.fileName = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.type = buf.getString();
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.properties = (Map<String, String>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
