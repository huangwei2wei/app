// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class DetachEvent extends Event
{
    private OID objToDetach;
    private String socketName;
    
    public DetachEvent() {
        this.objToDetach = null;
        this.socketName = null;
    }
    
    public DetachEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.objToDetach = null;
        this.socketName = null;
    }
    
    public DetachEvent(final OID detacherOid, final OID detachObjOid, final String socketName) {
        this.objToDetach = null;
        this.socketName = null;
        super.setObjectOid(detacherOid);
        this.setObjToDetach(detachObjOid);
        this.setSocketName(socketName);
    }
    
    @Override
    public String getName() {
        return "DetachEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getDetacher());
        buf.putInt(msgId);
        buf.putOID(this.getObjToDetach());
        buf.putString(this.getSocketName());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setDetacher(buf.getOID());
        buf.getInt();
        this.setObjToDetach(buf.getOID());
        this.setSocketName(buf.getString());
    }
    
    public void setDetacher(final OID detacher) {
        this.setObjectOid(detacher);
    }
    
    public OID getDetacher() {
        return this.getObjectOid();
    }
    
    public void setObjToDetach(final OID oid) {
        this.objToDetach = oid;
    }
    
    public OID getObjToDetach() {
        return this.objToDetach;
    }
    
    public void setSocketName(final String socketName) {
        this.socketName = socketName;
    }
    
    public String getSocketName() {
        return this.socketName;
    }
}
