// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class DropEvent extends Event
{
    private AOObject objToDrop;
    
    public DropEvent() {
        this.objToDrop = null;
    }
    
    public DropEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.objToDrop = null;
    }
    
    public DropEvent(final AOObject dropper, final AOObject obj) {
        super(dropper);
        this.objToDrop = null;
        this.setObjToDrop(obj);
    }
    
    @Override
    public String getName() {
        return "DropEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getObjToDrop().getOid());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setDropper(AOObject.getObject(buf.getOID()));
        buf.getInt();
        this.setObjToDrop(AOObject.getObject(buf.getOID()));
    }
    
    public void setDropper(final AOObject dropper) {
        this.setObject(dropper);
    }
    
    public void setObjToDrop(final AOObject obj) {
        this.objToDrop = obj;
    }
    
    public AOObject getObjToDrop() {
        return this.objToDrop;
    }
}
