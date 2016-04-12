// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import java.util.Iterator;
import atavism.msgsys.Message;
import java.util.ArrayList;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;
import java.util.Collection;
import atavism.msgsys.MessageTypeFilter;

public class NamespaceFilter extends MessageTypeFilter implements INamespaceFilter
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
    
    @Override
    public void setNamespaces(final Collection<Namespace> namespaces) {
        this.namespaces = new ArrayList<Namespace>(namespaces);
    }
    
    @Override
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
}
