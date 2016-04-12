// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.engine.Namespace;

public interface INamespaceMessage
{
    Namespace getNamespace();
    
    void setNamespace(final Namespace p0);
}
