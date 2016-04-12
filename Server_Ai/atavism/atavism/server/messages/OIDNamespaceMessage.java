// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.engine.Namespace;
import atavism.msgsys.SubjectMessage;

public class OIDNamespaceMessage extends SubjectMessage implements INamespaceMessage
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
    
    @Override
    public Namespace getNamespace() {
        return this.namespace;
    }
    
    @Override
    public void setNamespace(final Namespace namespace) {
        this.namespace = namespace;
    }
}
