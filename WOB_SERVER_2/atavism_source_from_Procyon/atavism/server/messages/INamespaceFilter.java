// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.engine.Namespace;
import java.util.Collection;
import atavism.msgsys.IMessageTypeFilter;

public interface INamespaceFilter extends IMessageTypeFilter
{
    Collection<Namespace> getNamespaces();
    
    void setNamespaces(final Collection<Namespace> p0);
}
