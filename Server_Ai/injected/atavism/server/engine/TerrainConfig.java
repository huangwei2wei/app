// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import atavism.server.messages.ClientMessage;
import java.io.Serializable;

public class TerrainConfig implements Serializable, ClientMessage, Marshallable
{
    private String configType;
    private String configData;
    public static final String configTypeFILE = "file";
    public static final String configTypeXMLSTRING = "xmlstring";
    private static final long serialVersionUID = 1L;
    
    @Override
    public String toString() {
        if (this.getConfigType() == "file") {
            return "[TerrainConfig type=" + this.getConfigType() + " file=" + this.getConfigData() + "]";
        }
        if (this.getConfigType() == "xmlstring") {
            return "[TerrainConfig type=" + this.getConfigType() + " size=" + ((this.getConfigData() == null) ? -1 : this.getConfigData().length()) + "]";
        }
        return "[TerrainConfig null]";
    }
    
    public void setConfigType(final String type) {
        this.configType = type;
    }
    
    public String getConfigType() {
        return this.configType;
    }
    
    public String getConfigData() {
        return this.configData;
    }
    
    public void setConfigData(final String configData) {
        this.configData = configData;
    }
    
    public AOByteBuffer toBuffer() {
        final AOByteBuffer buf = new AOByteBuffer(500);
        buf.putOID((OID)null);
        buf.putInt(66);
        buf.putString(this.getConfigType());
        buf.putString(this.getConfigData());
        buf.flip();
        return buf;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.configType != null && this.configType != "") {
            flag_bits = 1;
        }
        if (this.configData != null && this.configData != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.configType != null && this.configType != "") {
            buf.putString(this.configType);
        }
        if (this.configData != null && this.configData != "") {
            buf.putString(this.configData);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.configType = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.configData = buf.getString();
        }
        return this;
    }
}
