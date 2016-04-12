// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class DropResponseEvent extends Event
{
    private AOObject dropper;
    private String slotName;
    private boolean status;
    
    public DropResponseEvent() {
        this.dropper = null;
        this.slotName = null;
    }
    
    public DropResponseEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.dropper = null;
        this.slotName = null;
    }
    
    public DropResponseEvent(final AOObject dropper, final AOObject obj, final String slot, final boolean status) {
        super((Entity)obj);
        this.dropper = null;
        this.slotName = null;
        this.setDropper(dropper);
        this.setSlotName(slot);
        this.setStatus(status);
    }
    
    public String getName() {
        return "DropEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getDropper().getOid());
        buf.putInt(msgId);
        buf.putOID(this.getObjectOid());
        buf.putString(this.getSlotName());
        buf.putInt((int)(this.getStatus() ? 1 : 0));
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID playerId = buf.getOID();
        this.setDropper(AOObject.getObject(playerId));
        buf.getInt();
        final OID objId = buf.getOID();
        this.setObjectOid(objId);
        this.setSlotName(buf.getString());
        this.setStatus(buf.getInt() == 1);
    }
    
    public void setDropper(final AOObject dropper) {
        this.dropper = dropper;
    }
    
    public AOObject getDropper() {
        return this.dropper;
    }
    
    public void setSlotName(final String slotName) {
        this.slotName = slotName;
    }
    
    public String getSlotName() {
        return this.slotName;
    }
    
    public void setStatus(final boolean status) {
        this.status = status;
    }
    
    public boolean getStatus() {
        return this.status;
    }
}
