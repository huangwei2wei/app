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

public class AcquireResponseEvent extends Event
{
    private boolean success;
    private AOObject targetObject;
    
    public AcquireResponseEvent() {
        this.success = false;
        this.targetObject = null;
    }
    
    public AcquireResponseEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.success = false;
        this.targetObject = null;
    }
    
    public AcquireResponseEvent(final AOObject targetOwner, final AOObject objToAcquire, final boolean success) {
        super(targetOwner);
        this.success = false;
        this.targetObject = null;
        this.setTargetObject(objToAcquire);
        this.setSuccessStatus(success);
    }
    
    @Override
    public String getName() {
        return "AcquireResponse";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getTargetObject().getOid());
        buf.putInt(this.getSuccessStatus() ? 1 : 0);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setTargetOwner(AOObject.getObject(buf.getOID()));
        buf.getInt();
        this.setTargetObject(AOObject.getObject(buf.getOID()));
        this.setSuccessStatus(buf.getInt() == 1);
    }
    
    public void setTargetOwner(final AOObject targetOwner) {
        this.setObject(targetOwner);
    }
    
    public void setTargetObject(final AOObject obj) {
        this.targetObject = obj;
    }
    
    public AOObject getTargetObject() {
        return this.targetObject;
    }
    
    public void setSuccessStatus(final boolean val) {
        this.success = val;
    }
    
    public boolean getSuccessStatus() {
        return this.success;
    }
}
