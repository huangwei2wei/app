// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class ActivateItemEvent extends Event
{
    private OID targetOid;
    private OID itemOid;
    
    public ActivateItemEvent() {
        this.targetOid = null;
        this.itemOid = null;
    }
    
    public ActivateItemEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.targetOid = null;
        this.itemOid = null;
    }
    
    @Override
    public String getName() {
        return "ActivateItemEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getTargetOid());
        buf.putOID(this.getItemOid());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setTargetOid(buf.getOID());
        this.setItemOid(buf.getOID());
    }
    
    public void setTargetOid(final OID oid) {
        this.targetOid = oid;
    }
    
    public OID getTargetOid() {
        return this.targetOid;
    }
    
    public void setItemOid(final OID oid) {
        this.itemOid = oid;
    }
    
    public OID getItemOid() {
        return this.itemOid;
    }
}
