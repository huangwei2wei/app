// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.SubjectMessage;

public class OIDNamespaceMessage extends SubjectMessage implements INamespaceMessage, Marshallable
{
    private Namespace namespace;
    private static final long serialVersionUID = 1L;
    
    public OIDNamespaceMessage() {
    }
    
    public OIDNamespaceMessage(final MessageType msgType) {
        this.setMsgType(msgType);
    }
    
    public OIDNamespaceMessage(final MessageType msgType, final OID oid) {
        super(msgType, oid);
    }
    
    public OIDNamespaceMessage(final MessageType msgType, final OID oid, final Namespace namespace) {
        super(msgType, oid);
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
