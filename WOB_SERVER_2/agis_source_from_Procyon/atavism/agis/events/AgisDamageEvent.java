// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;

public class AgisDamageEvent extends Event
{
    private int dmg;
    private AOObject dmgSrc;
    
    public AgisDamageEvent() {
        this.dmg = 0;
        this.dmgSrc = null;
    }
    
    public AgisDamageEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.dmg = 0;
        this.dmgSrc = null;
    }
    
    public AgisDamageEvent(final AOObject src, final AOObject target, final int dmg) {
        super((Entity)target);
        this.dmg = 0;
        this.dmgSrc = null;
        this.setDmg(dmg);
        this.setDmgSrc(src);
    }
    
    public String getName() {
        return "AgisDamageEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(100);
        buf.putOID(this.getDmgSrc().getOid());
        buf.putInt(msgId);
        buf.putOID(this.getObjectOid());
        buf.putString("stun");
        buf.putInt(this.getDmg());
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setDmgSrc(AOObject.getObject(buf.getOID()));
        buf.getInt();
        this.setObjectOid(buf.getOID());
        buf.getString();
        this.setDmg(buf.getInt());
    }
    
    public void setDmgSrc(final AOObject dmgSrc) {
        this.dmgSrc = dmgSrc;
    }
    
    public AOObject getDmgSrc() {
        return this.dmgSrc;
    }
    
    public void setDmg(final int dmg) {
        this.dmg = dmg;
    }
    
    public int getDmg() {
        return this.dmg;
    }
}
