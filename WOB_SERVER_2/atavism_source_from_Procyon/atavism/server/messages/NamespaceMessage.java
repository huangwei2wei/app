// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;
import atavism.msgsys.Message;

public class NamespaceMessage extends Message implements INamespaceMessage
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
    
    @Override
    public Namespace getNamespace() {
        return this.namespace;
    }
    
    @Override
    public void setNamespace(final Namespace namespace) {
        this.namespace = namespace;
    }
}
