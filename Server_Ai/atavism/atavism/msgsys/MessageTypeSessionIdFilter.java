// 
// Decompiled by Procyon v0.5.30
// 

package atavism.msgsys;

import java.util.Iterator;
import atavism.server.util.LockFactory;
import java.util.HashSet;
import java.util.Set;
import atavism.server.util.Logger;
import java.util.concurrent.locks.Lock;

public class MessageTypeSessionIdFilter extends MessageTypeFilter
{
    transient Lock lock;
    static final Logger log;
    Set<MessageType> types;
    String targetSessionId;
    boolean matchesNullSessionId;
    private static final long serialVersionUID = 1L;
    
    public MessageTypeSessionIdFilter() {
        this.lock = null;
        this.types = new HashSet<MessageType>();
        this.targetSessionId = null;
        this.matchesNullSessionId = false;
        this.setupTransient();
    }
    
    public MessageTypeSessionIdFilter(final String targetSessionId) {
        this.lock = null;
        this.types = new HashSet<MessageType>();
        this.targetSessionId = null;
        this.matchesNullSessionId = false;
        this.setupTransient();
        this.setTargetSessionId(targetSessionId);
    }
    
    public MessageTypeSessionIdFilter(final String targetSessionId, final MessageType type) {
        this.lock = null;
        this.types = new HashSet<MessageType>();
        this.targetSessionId = null;
        this.matchesNullSessionId = false;
        this.setupTransient();
        this.setTargetSessionId(targetSessionId);
        this.addType(type);
    }
    
    public MessageTypeSessionIdFilter(final MessageType type, final String targetSessionId, final boolean matchNullFlag) {
        this.lock = null;
        this.types = new HashSet<MessageType>();
        this.targetSessionId = null;
        this.matchesNullSessionId = false;
        this.setupTransient();
        this.setTargetSessionId(targetSessionId);
        this.addType(type);
        this.matchesNullSessionId(matchNullFlag);
    }
    
    void setupTransient() {
        this.lock = LockFactory.makeLock("MessageTypeSessionIdFilterLock");
    }
    
    public String getName() {
        return "MessageTypeSessionIdFilter";
    }
    
    public void matchesNullSessionId(final boolean flag) {
        this.matchesNullSessionId = flag;
    }
    
    public boolean matchesNullSessionId() {
        return this.matchesNullSessionId;
    }
    
    public void setTargetSessionId(final String s) {
        this.targetSessionId = s;
    }
    
    public String getTargetSessionId() {
        return this.targetSessionId;
    }
    
    public boolean matchesRemaining(final Message msg) {
        final MessageType msgType = msg.getMsgType();
        this.lock.lock();
        try {
            boolean typeMatched = false;
            for (final MessageType t : this.types) {
                if (msgType == t) {
                    typeMatched = true;
                    break;
                }
            }
            if (!typeMatched) {
                return false;
            }
        }
        finally {
            this.lock.unlock();
        }
        String msgTargetSessionId = null;
        if (msg instanceof ITargetSessionId) {
            msgTargetSessionId = ((ITargetSessionId)msg).getTargetSessionId();
        }
        return (this.matchesNullSessionId() && msgTargetSessionId == null) || (msgTargetSessionId != null && msgTargetSessionId.equals(this.getTargetSessionId()));
    }
    
    public Set<MessageType> getTypes() {
        return this.types;
    }
    
    static {
        log = new Logger("MessageTypeSessionIdFilter");
    }
}
