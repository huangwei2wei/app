// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class PluginAvailableMessage extends Message
{
    private String pluginType;
    private String pluginName;
    private static final long serialVersionUID = 1L;
    
    public PluginAvailableMessage() {
    }
    
    public PluginAvailableMessage(final String pluginType, final String pluginName) {
        super(MessageTypes.MSG_TYPE_PLUGIN_AVAILABLE);
        this.pluginType = pluginType;
        this.pluginName = pluginName;
    }
    
    public String getPluginType() {
        return this.pluginType;
    }
    
    public String getPluginName() {
        return this.pluginName;
    }
}
