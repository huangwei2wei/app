// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.msgsys.Message;
import java.util.ArrayList;
import atavism.server.engine.Namespace;
import java.util.Collection;

public class SubObjectFilter extends PerceptionFilter implements INamespaceFilter
{
    Collection<Namespace> namespaces;
    
    public SubObjectFilter() {
    }
    
    public SubObjectFilter(final Collection<Namespace> namespaces) {
        this.setNamespaces(namespaces);
    }
    
    @Override
    public Collection<Namespace> getNamespaces() {
        return this.namespaces;
    }
    
    @Override
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
    
    @Override
    public boolean matchRemaining(final Message message) {
        return this.matchNamespace(message) && super.matchRemaining(message);
    }
    
    protected boolean matchPerception(final Message message) {
        return super.matchRemaining(message);
    }
}
