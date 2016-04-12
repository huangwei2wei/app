// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.objects.AOObject;
import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.agis.objects.AgisItem;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class EquipEvent extends Event
{
    private OID objToEquipId;
    private String slotName;
    
    public EquipEvent() {
        this.objToEquipId = null;
        this.slotName = null;
    }
    
    public EquipEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.objToEquipId = null;
        this.slotName = null;
    }
    
    public EquipEvent(final AgisMob equipper, final AgisItem equipObj, final String slotName) {
        super((Entity)equipper);
        this.objToEquipId = null;
        this.slotName = null;
        this.setObjToEquipId(equipObj.getOid());
        this.setSlotName(slotName);
    }
    
    public String getName() {
        return "EquipEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getObjToEquip().getOid());
        buf.putString(this.getSlotName());
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setObjToEquipId(buf.getOID());
        this.setSlotName(buf.getString());
    }
    
    public void setObjToEquipId(final AgisItem obj) {
        this.objToEquipId = obj.getOid();
    }
    
    public void setObjToEquipId(final OID id) {
        this.objToEquipId = id;
    }
    
    public OID getObjToEquipId() {
        return this.objToEquipId;
    }
    
    public AgisItem getObjToEquip() {
        return AgisItem.convert(AOObject.getObject(this.objToEquipId));
    }
    
    public void setSlotName(final String slotName) {
        this.slotName = slotName;
    }
    
    public String getSlotName() {
        return this.slotName;
    }
}
