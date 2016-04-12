// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

public class AwaitPluginDependentsMessage extends Message
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
}
