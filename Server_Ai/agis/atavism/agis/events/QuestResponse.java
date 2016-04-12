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

public class QuestResponse extends Event
{
    OID questId;
    boolean response;
    OID questNpcOid;
    
    public QuestResponse() {
        this.questId = null;
        this.questNpcOid = null;
    }
    
    public QuestResponse(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.questId = null;
        this.questNpcOid = null;
    }
    
    public QuestResponse(final AgisMob player, final AgisMob questNpc, final OID questId, final boolean response) {
        super((Entity)player);
        this.questId = null;
        this.questNpcOid = null;
        this.setQuestId(questId);
        this.setResponse(response);
        this.setQuestNpcOid(questNpc.getOid());
    }
    
    public String getName() {
        return "QuestResponse";
    }
    
    public void setQuestId(final OID id) {
        this.questId = id;
    }
    
    public OID getQuestId() {
        return this.questId;
    }
    
    public void setResponse(final boolean response) {
        this.response = response;
    }
    
    public boolean getResponse() {
        return this.response;
    }
    
    public AgisMob getQuestNpc() {
        try {
            return AgisMob.convert((Entity)AOObject.getObject(this.questNpcOid));
        }
        catch (AORuntimeException e) {
            throw new RuntimeException("QuestResponse", (Throwable)e);
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
        final AOByteBuffer buf = new AOByteBuffer(32);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getQuestNpc().getOid());
        buf.putOID(this.getQuestId());
        buf.putInt((int)(this.getResponse() ? 1 : 0));
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID playerId = buf.getOID();
        this.setObjectOid(playerId);
        buf.getInt();
        this.setQuestNpcOid(buf.getOID());
        this.setQuestId(buf.getOID());
        this.setResponse(buf.getInt() == 1);
    }
}
