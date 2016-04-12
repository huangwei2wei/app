// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.Message;

public class NamespaceMessage extends Message implements INamespaceMessage, Marshallable
{
    private Namespace namespace;
    private static final long serialVersionUID = 1L;
    
    public NamespaceMessage() {
    }
    
    public NamespaceMessage(final MessageType msgType) {
        super(msgType);
    }
    
    public NamespaceMessage(final MessageType msgType, final Namespace namespace) {
        super(msgType);
        this.setNamespace(namespace);
    }
    
    public Namespace getNamespace() {
        return this.namespace;
    }
    
    public void setNamespace(final Namespace namespace) {
        this.namespace = namespace;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.namespace != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.namespace != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.namespace);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.namespace = (Namespace)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
