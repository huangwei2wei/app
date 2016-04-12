// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.msgsys.Message;
import java.util.ArrayList;
import atavism.server.engine.Namespace;
import java.util.Collection;
import atavism.server.marshalling.Marshallable;

public class SubObjectFilter extends PerceptionFilter implements INamespaceFilter, Marshallable
{
    Collection<Namespace> namespaces;
    
    public SubObjectFilter() {
    }
    
    public SubObjectFilter(final Collection<Namespace> namespaces) {
        this.setNamespaces(namespaces);
    }
    
    public Collection<Namespace> getNamespaces() {
        return this.namespaces;
    }
    
    public void setNamespaces(final Collection<Namespace> namespaces) {
        this.namespaces = new ArrayList<Namespace>(namespaces);
    }
    
    public boolean matchNamespace(final Message message) {
        if (message instanceof INamespaceMessage) {
            final INamespaceMessage namespaceMsg = (INamespaceMessage)message;
            final Namespace msgNamespace = namespaceMsg.getNamespace();
            return this.namespaces.contains(msgNamespace);
        }
        return false;
    }
    
    public boolean matchRemaining(final Message message) {
        return this.matchNamespace(message) && super.matchRemaining(message);
    }
    
    protected boolean matchPerception(final Message message) {
        return super.matchRemaining(message);
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.namespaces != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.namespaces != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.namespaces);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.namespaces = (Collection<Namespace>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
