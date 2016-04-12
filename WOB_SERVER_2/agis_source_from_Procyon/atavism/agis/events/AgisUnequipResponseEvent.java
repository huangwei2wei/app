// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.objects.AOObject;
import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.Event;

public class AgisUnequipResponseEvent extends Event
{
    private AgisItem objToUnequip;
    private String slotName;
    private boolean status;
    
    public AgisUnequipResponseEvent() {
        this.objToUnequip = null;
        this.slotName = null;
        this.status = false;
    }
    
    public AgisUnequipResponseEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.objToUnequip = null;
        this.slotName = null;
        this.status = false;
    }
    
    public AgisUnequipResponseEvent(final AgisMob unequipper, final AgisItem objToUnequip, final String slotName, final boolean status) {
        super((Entity)unequipper);
        this.objToUnequip = null;
        this.slotName = null;
        this.status = false;
        this.setObjToUnequip(objToUnequip);
        this.setSlotName(slotName);
        this.setStatus(status);
    }
    
    public String getName() {
        return "UnequipResponseEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getObjToUnequip().getOid());
        buf.putString(this.getSlotName());
        buf.putBoolean(this.getStatus());
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setUnequipper(AgisMob.convert(AOObject.getObject(buf.getOID())));
        buf.getInt();
        this.setObjToUnequip(AgisItem.convert(AOObject.getObject(buf.getOID())));
        this.setSlotName(buf.getString());
        this.setStatus(buf.getBoolean());
    }
    
    public void setUnequipper(final AgisMob mob) {
        this.setObject((AOObject)mob);
    }
    
    public void setObjToUnequip(final AgisItem obj) {
        this.objToUnequip = obj;
    }
    
    public AgisItem getObjToUnequip() {
        return this.objToUnequip;
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
