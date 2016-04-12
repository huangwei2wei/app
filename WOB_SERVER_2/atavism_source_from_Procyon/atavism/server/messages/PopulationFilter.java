// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.msgsys.Message;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.objects.ObjectType;
import atavism.msgsys.MessageTypeFilter;

public class PopulationFilter extends MessageTypeFilter
{
    private ObjectType objectType;
    
    public PopulationFilter() {
    }
    
    public PopulationFilter(final ObjectType type) {
        this.objectType = type;
        this.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
        this.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
    }
    
    public ObjectType getType() {
        return this.objectType;
    }
    
    @Override
    public boolean matchRemaining(final Message message) {
        if (message instanceof WorldManagerClient.SpawnedMessage) {
            return this.objectType == ((WorldManagerClient.SpawnedMessage)message).getType();
        }
        return message instanceof WorldManagerClient.DespawnedMessage && this.objectType == ((WorldManagerClient.DespawnedMessage)message).getType();
    }
    
    @Override
    public String toString() {
        return "[PopulationFilter " + this.toStringInternal() + "]";
    }
    
    @Override
    protected String toStringInternal() {
        return "type=" + this.objectType + " " + super.toStringInternal();
    }
}
