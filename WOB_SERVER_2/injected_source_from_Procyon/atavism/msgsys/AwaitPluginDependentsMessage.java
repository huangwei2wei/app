// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;

public class AwaitPluginDependentsMessage extends Message implements Marshallable
{
    private String pluginType;
    private String pluginName;
    private static final long serialVersionUID = 1L;
    
    public AwaitPluginDependentsMessage() {
    }
    
    public AwaitPluginDependentsMessage(final String pluginType, final String pluginName) {
        super(MessageTypes.MSG_TYPE_AWAIT_PLUGIN_DEPENDENTS);
        this.pluginType = pluginType;
        this.pluginName = pluginName;
    }
    
    public String getPluginType() {
        return this.pluginType;
    }
    
    public String getPluginName() {
        return this.pluginName;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.pluginType != null && this.pluginType != "") {
            flag_bits = 1;
        }
        if (this.pluginName != null && this.pluginName != "") {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.pluginType != null && this.pluginType != "") {
            buf.putString(this.pluginType);
        }
        if (this.pluginName != null && this.pluginName != "") {
            buf.putString(this.pluginName);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.pluginType = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.pluginName = buf.getString();
        }
        return this;
    }
}
