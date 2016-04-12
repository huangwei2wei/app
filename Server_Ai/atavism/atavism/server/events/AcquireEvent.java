// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.server.util.Log;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class AcquireEvent extends Event
{
    private AOObject targetOwner;
    private AOObject targetObject;
    
    public AcquireEvent() {
        this.targetOwner = null;
        this.targetObject = null;
    }
    
    public AcquireEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.targetOwner = null;
        this.targetObject = null;
    }
    
    public AcquireEvent(final AOObject targetOwner, final AOObject objToAcquire) {
        this.targetOwner = null;
        this.targetObject = null;
        this.setTargetOwner(targetOwner);
        this.setTargetObject(objToAcquire);
    }
    
    @Override
    public String getName() {
        return "AcquireEvent";
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID playerId = buf.getOID();
        this.setTargetOwner(AOObject.getObject(playerId));
        buf.getInt();
        final OID objId = buf.getOID();
        this.setTargetObject(AOObject.getObject(objId));
        if (this.getTargetObject() == null && Log.loggingDebug) {
            Log.debug("AcquireEvent.parseBytes: targetobject is null, oid=" + objId);
        }
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getTargetOwner().getOid());
        buf.putInt(msgId);
        buf.putOID(this.getTargetObject().getOid());
        buf.flip();
        return buf;
    }
    
    public void setTargetOwner(final AOObject targetOwner) {
        this.targetOwner = targetOwner;
    }
    
    public AOObject getTargetOwner() {
        return this.targetOwner;
    }
    
    public void setTargetObject(final AOObject targetObject) {
        this.targetObject = targetObject;
    }
    
    public AOObject getTargetObject() {
        return this.targetObject;
    }
}
