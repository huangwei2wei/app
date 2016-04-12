// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.msgsys.SubjectMessage;
import atavism.msgsys.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.LockFactory;
import atavism.server.objects.SpawnData;
import atavism.msgsys.MessageType;
import java.util.concurrent.locks.Lock;
import atavism.server.objects.ObjectStub;
import java.io.Serializable;
import atavism.msgsys.MessageDispatch;
import atavism.msgsys.MessageCallback;

/**
 * ÐÐÎª
 * @author doter
 *
 */
public abstract class Behavior implements MessageCallback, MessageDispatch, Serializable
{
    protected ObjectStub obj;
    protected transient Lock lock;
    public static MessageType MSG_TYPE_COMMAND;
    public static MessageType MSG_TYPE_EVENT;
    
    public Behavior() {
        this.lock = null;
        this.setupTransient();
    }
    
    public Behavior(final SpawnData data) {
        this.lock = null;
        this.setupTransient();
    }
    
    private void setupTransient() {
        this.lock = LockFactory.makeLock("BehavLock");
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    public ObjectStub getObjectStub() {
        return this.obj;
    }
    
    public void setObjectStub(final ObjectStub obj) {
        this.obj = obj;
    }
    
    public void initialize() {
    }
    
    public abstract void activate();
    
    public abstract void deactivate();
    
    @Override
    public abstract void handleMessage(final Message p0, final int p1);
    
    @Override
    public void dispatchMessage(final Message message, final int flags, final MessageCallback callback) {
        Engine.defaultDispatchMessage(message, flags, callback);
    }
    
    static {
        Behavior.MSG_TYPE_COMMAND = MessageType.intern("ao.COMMAND");
        Behavior.MSG_TYPE_EVENT = MessageType.intern("ao.EVENT");
    }
    
    public static class CommandMessage extends SubjectMessage
    {
        private String cmd;
        private static final long serialVersionUID = 1L;
        
        public CommandMessage() {
        }
        
        public CommandMessage(final OID objOid) {
            super(Behavior.MSG_TYPE_COMMAND, objOid);
        }
        
        public CommandMessage(final ObjectStub obj) {
            super(Behavior.MSG_TYPE_COMMAND, obj.getOid());
        }
        
        public CommandMessage(final String cmd) {
            this.setMsgType(Behavior.MSG_TYPE_COMMAND);
            this.cmd = cmd;
        }
        
        public CommandMessage(final OID objOid, final String cmd) {
            super(Behavior.MSG_TYPE_COMMAND, objOid);
            this.cmd = cmd;
        }
        
        public CommandMessage(final ObjectStub obj, final String cmd) {
            super(Behavior.MSG_TYPE_COMMAND, obj.getOid());
            this.cmd = cmd;
        }
        
        public void setCmd(final String cmd) {
            this.cmd = cmd;
        }
        
        public String getCmd() {
            return this.cmd;
        }
    }
    
    public static class EventMessage extends SubjectMessage
    {
        private String event;
        private static final long serialVersionUID = 1L;
        
        public EventMessage() {
            this.setMsgType(Behavior.MSG_TYPE_EVENT);
        }
        
        EventMessage(final OID objOid) {
            super(Behavior.MSG_TYPE_EVENT, objOid);
        }
        
        EventMessage(final ObjectStub obj) {
            super(Behavior.MSG_TYPE_EVENT, obj.getOid());
        }
        
        public void setEvent(final String event) {
            this.event = event;
        }
        
        public String getEvent() {
            return this.event;
        }
    }
}
