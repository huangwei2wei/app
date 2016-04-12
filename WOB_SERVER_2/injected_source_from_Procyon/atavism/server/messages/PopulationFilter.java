// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.msgsys.Message;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.objects.ObjectType;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.MessageTypeFilter;

public class PopulationFilter extends MessageTypeFilter implements Marshallable
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.objectType != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.objectType != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.objectType);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.objectType = (ObjectType)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
