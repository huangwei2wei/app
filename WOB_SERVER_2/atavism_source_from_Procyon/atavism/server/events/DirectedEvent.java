// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.HashSet;
import java.util.Iterator;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Logger;
import atavism.server.objects.AOObject;
import java.util.Collection;
import atavism.server.engine.Event;

public class DirectedEvent extends Event
{
    private Event containedEvent;
    private Collection<AOObject> recipientCol;
    private String mMessage;
    static final Logger log;
    
    public DirectedEvent() {
        this.containedEvent = null;
        this.recipientCol = null;
        this.mMessage = null;
    }
    
    public DirectedEvent(final Collection<AOObject> recipients, final Event event) {
        this.containedEvent = null;
        this.recipientCol = null;
        this.mMessage = null;
        this.setRecipients(recipients);
        this.setContainedEvent(event);
    }
    
    @Override
    public String getName() {
        return "DirectedEvent";
    }
    
    public void setContainedEvent(final Event e) {
        this.containedEvent = e;
    }
    
    public Event getContainedEvent() {
        return this.containedEvent;
    }
    
    public void setRecipients(final Collection<AOObject> c) {
        this.recipientCol = c;
    }
    
    public Collection<AOObject> getRecipients() {
        return this.recipientCol;
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(1000);
        buf.putOID(null);
        buf.putInt(msgId);
        if (this.recipientCol == null) {
            throw new AORuntimeException("DirectedEvent: recipient list size is 0");
        }
        buf.putInt(this.recipientCol.size());
        for (final AOObject e : this.recipientCol) {
            buf.putOID(e.getOid());
        }
        final AOByteBuffer subEventBuf = this.getContainedEvent().toBytes();
        buf.putByteBuffer(subEventBuf);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        buf.getOID();
        buf.getInt();
        final Collection<AOObject> col = new HashSet<AOObject>();
        for (int len = buf.getInt(), i = 0; i < len; ++i) {
            final OID oid = buf.getOID();
            final AOObject e = AOObject.getObject(oid);
            if (e == null) {
                DirectedEvent.log.warn("could not find entity with oid " + oid);
            }
            else {
                col.add(e);
            }
        }
        this.setRecipients(col);
        final AOByteBuffer subBuf = buf.getByteBuffer();
        final Event subEvent = Engine.getEventServer().parseBytes(subBuf, this.getConnection());
        this.setContainedEvent(subEvent);
    }
    
    public void setMessage(final String msg) {
        this.mMessage = msg;
    }
    
    public String getMessage() {
        return this.mMessage;
    }
    
    static {
        log = new Logger("ComEvent");
    }
}
