// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.util.Log;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class NotifyNewObjectEvent extends Event
{
    private OID newObjOid;
    
    public NotifyNewObjectEvent() {
        this.newObjOid = null;
    }
    
    public NotifyNewObjectEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.newObjOid = null;
    }
    
    public NotifyNewObjectEvent(final AOObject notifyObj, final AOObject newObj) {
        super(notifyObj);
        this.newObjOid = null;
        this.setNewObjectOid(newObj.getOid());
        Log.debug("NotifyNewObjectEvent: checking obj to notify");
    }
    
    @Override
    public String getName() {
        return "NotifyNewObjectEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getNewObjectOid());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setNewObjectOid(buf.getOID());
    }
    
    public void setObjToNotify(final AOObject obj) {
        this.setObjectOid(obj.getOid());
    }
    
    public void setObjToNotifyOid(final OID oid) {
        this.setObjectOid(oid);
    }
    
    public OID getObjToNotifyOid() {
        return this.getObjectOid();
    }
    
    public void setNewObjectOid(final OID oid) {
        this.newObjOid = oid;
    }
    
    public OID getNewObjectOid() {
        return this.newObjOid;
    }
}
