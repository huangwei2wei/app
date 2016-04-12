// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class CommandEvent extends Event
{
    private String command;
    private OID targetOid;
    
    public CommandEvent() {
        this.command = null;
        this.targetOid = null;
    }
    
    public CommandEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.command = null;
        this.targetOid = null;
    }
    
    public CommandEvent(final AOObject obj, final AOObject target, final String command) {
        super(obj);
        this.command = null;
        this.targetOid = null;
        this.setTarget(target.getOid());
        this.setCommand(command);
    }
    
    @Override
    public String getName() {
        return "CommandEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getObjectOid());
        buf.putInt(13);
        buf.putOID(this.getTarget());
        buf.putString(this.getCommand());
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setTarget(buf.getOID());
        this.setCommand(buf.getString());
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setTarget(final OID oid) {
        this.targetOid = oid;
    }
    
    public OID getTarget() {
        return this.targetOid;
    }
}
