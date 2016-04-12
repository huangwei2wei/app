// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.network.AOByteBuffer;
import atavism.server.messages.ClientMessage;
import java.io.Serializable;

public class TerrainConfig implements Serializable, ClientMessage
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
    
    @Override
    public AOByteBuffer toBuffer() {
        final AOByteBuffer buf = new AOByteBuffer(500);
        buf.putOID(null);
        buf.putInt(66);
        buf.putString(this.getConfigType());
        buf.putString(this.getConfigData());
        buf.flip();
        return buf;
    }
}
