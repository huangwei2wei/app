// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.server.util.AORuntimeException;
import atavism.server.objects.AOObject;
import atavism.server.objects.Entity;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class ConcludeQuest extends Event
{
    protected OID questNpcOid;
    
    public ConcludeQuest() {
        this.questNpcOid = null;
    }
    
    public ConcludeQuest(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.questNpcOid = null;
    }
    
    public ConcludeQuest(final AgisMob player, final AgisMob questNpc) {
        super((Entity)player);
        this.questNpcOid = null;
        this.setQuestNpcOid(questNpc.getOid());
    }
    
    public String getName() {
        return "ConcludeQuest";
    }
    
    public AgisMob getQuestNpc() {
        try {
            return AgisMob.convert((Entity)AOObject.getObject(this.questNpcOid));
        }
        catch (AORuntimeException e) {
            throw new RuntimeException("concludequest", (Throwable)e);
        }
    }
    
    public OID getQuestNpcOid() {
        return this.questNpcOid;
    }
    
    public void setQuestNpcOid(final OID questNpcOid) {
        this.questNpcOid = questNpcOid;
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getQuestNpc().getOid());
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID playerId = buf.getOID();
        this.setObjectOid(playerId);
        buf.getInt();
        final OID questNpcId = buf.getOID();
        this.setQuestNpcOid(questNpcId);
    }
}
