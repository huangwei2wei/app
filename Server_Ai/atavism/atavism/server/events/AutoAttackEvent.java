// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class AutoAttackEvent extends Event
{
    private boolean attackStatus;
    private OID targetOid;
    private String attackType;
    
    public AutoAttackEvent() {
        this.attackStatus = false;
        this.targetOid = null;
        this.attackType = null;
    }
    
    public AutoAttackEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.attackStatus = false;
        this.targetOid = null;
        this.attackType = null;
    }
    
    public AutoAttackEvent(final AOObject attacker, final AOObject target, final String attackType, final boolean attackStatus) {
        super(attacker);
        this.attackStatus = false;
        this.targetOid = null;
        this.attackType = null;
        this.setTargetOid(target.getOid());
        this.setAttackType(attackType);
        this.setAttackStatus(attackStatus);
    }
    
    @Override
    public String getName() {
        return "AutoAttackEvent";
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(200);
        buf.putOID(this.getAttackerOid());
        buf.putInt(msgId);
        buf.putOID(this.getTargetOid());
        buf.putString(this.getAttackType());
        buf.putInt(this.getAttackStatus() ? 1 : 0);
        buf.flip();
        return buf;
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setAttackerOid(buf.getOID());
        buf.getInt();
        this.setTargetOid(buf.getOID());
        this.setAttackType(buf.getString());
        this.setAttackStatus(buf.getInt() == 1);
    }
    
    public void setAttackerOid(final OID id) {
        this.setObjectOid(id);
    }
    
    public OID getAttackerOid() {
        return this.getObjectOid();
    }
    
    public void setTargetOid(final OID oid) {
        this.targetOid = oid;
    }
    
    public OID getTargetOid() {
        return this.targetOid;
    }
    
    public void setAttackType(final String s) {
        this.attackType = s;
    }
    
    public String getAttackType() {
        return this.attackType;
    }
    
    public void setAttackStatus(final boolean s) {
        this.attackStatus = s;
    }
    
    public boolean getAttackStatus() {
        return this.attackStatus;
    }
}
