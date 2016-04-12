// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.util.AORuntimeException;
import atavism.server.objects.AOObject;
import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.Event;

public class AgisEquipResponseEvent extends Event
{
    private AgisItem objToEquip;
    private boolean success;
    private String slotName;
    
    public AgisEquipResponseEvent() {
        this.objToEquip = null;
        this.success = false;
        this.slotName = null;
    }
    
    public AgisEquipResponseEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.objToEquip = null;
        this.success = false;
        this.slotName = null;
    }
    
    public AgisEquipResponseEvent(final AgisMob equipper, final AgisItem obj, final String slotName, final boolean success) {
        super((Entity)equipper);
        this.objToEquip = null;
        this.success = false;
        this.slotName = null;
        this.setObjToEquip(obj);
        this.setSlotName(slotName);
        this.setSuccess(success);
    }
    
    public String getName() {
        return "AgisEquipResponseEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getObjToEquip().getOid());
        buf.putString(this.getSlotName());
        buf.putInt((int)(this.getSuccess() ? 1 : 0));
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final AOObject obj = AOObject.getObject(buf.getOID());
        if (!obj.isMob()) {
            throw new AORuntimeException("EquipResponseEvent.parseBytes: not a mob");
        }
        this.setEquipper(AgisMob.convert(obj));
        buf.getInt();
        this.setObjToEquip(AgisItem.convert(AOObject.getObject(buf.getOID())));
        this.setSlotName(buf.getString());
        this.setSuccess(buf.getInt() == 1);
    }
    
    public void setEquipper(final AgisMob mob) {
        this.setObject((AOObject)mob);
    }
    
    public void setObjToEquip(final AgisItem item) {
        this.objToEquip = item;
    }
    
    public AOObject getObjToEquip() {
        return this.objToEquip;
    }
    
    public void setSuccess(final boolean success) {
        this.success = success;
    }
    
    public boolean getSuccess() {
        return this.success;
    }
    
    public void setSlotName(final String slotName) {
        this.slotName = slotName;
    }
    
    public String getSlotName() {
        return this.slotName;
    }
}
