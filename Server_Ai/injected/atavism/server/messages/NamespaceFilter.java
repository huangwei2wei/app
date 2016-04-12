// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Iterator;
import atavism.msgsys.Message;
import java.util.ArrayList;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;
import java.util.Collection;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.MessageTypeFilter;

public class NamespaceFilter extends MessageTypeFilter implements INamespaceFilter, Marshallable
{
    private Collection<Namespace> namespaces;
    private static final long serialVersionUID = 1L;
    
    public NamespaceFilter(final Collection<Namespace> namespaces) {
        this.setNamespaces(namespaces);
    }
    
    public NamespaceFilter() {
    }
    
    public NamespaceFilter(final MessageType msgType, final Collection<Namespace> namespaces) {
        this.addType(msgType);
        this.setNamespaces(namespaces);
    }
    
    public void setNamespaces(final Collection<Namespace> namespaces) {
        this.namespaces = new ArrayList<Namespace>(namespaces);
    }
    
    public Collection<Namespace> getNamespaces() {
        return this.namespaces;
    }
    
    @Override
    public boolean matchRemaining(final Message msg) {
        if (msg instanceof INamespaceMessage) {
            final INamespaceMessage namespaceMsg = (INamespaceMessage)msg;
            final Namespace msgNamespace = namespaceMsg.getNamespace();
            final boolean matches = this.namespaces.contains(msgNamespace);
            return matches;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[Some Namespace]";
    }
    
    @Override
    protected String toStringInternal() {
        String s = "";
        if (this.namespaces != null) {
            for (final Namespace ns : this.namespaces) {
                if (s != "") {
                    s += ",";
                }
                s += ns.getName();
            }
        }
        return super.toStringInternal() + " namespaces=" + s;
    }
    
    @Override
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
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.namespaces = (Collection<Namespace>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
