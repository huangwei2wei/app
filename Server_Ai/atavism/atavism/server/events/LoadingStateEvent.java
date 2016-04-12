// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.Event;

public class LoadingStateEvent extends Event
{
    private Lock lock;
    private boolean loading;
    
    public LoadingStateEvent() {
        this.lock = LockFactory.makeLock("LoadingStateEventLock");
        this.loading = false;
    }
    
    public LoadingStateEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.lock = LockFactory.makeLock("LoadingStateEventLock");
        this.loading = false;
    }
    
    public LoadingStateEvent(final boolean loading) {
        this.lock = LockFactory.makeLock("LoadingStateEventLock");
        this.loading = false;
        this.setLoading(loading);
    }
    
    @Override
    public String getName() {
        return "LoadingStateEvent";
    }
    
    public void setLoading(final boolean loading) {
        this.loading = loading;
    }
    
    public boolean getLoading() {
        return this.loading;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(40);
        this.lock.lock();
        try {
            buf.putOID(null);
            buf.putInt(msgId);
            buf.putBoolean(this.loading);
            buf.flip();
            return buf;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        this.lock.lock();
        try {
            this.setLoading(buf.getBoolean());
        }
        finally {
            this.lock.unlock();
        }
    }
}
