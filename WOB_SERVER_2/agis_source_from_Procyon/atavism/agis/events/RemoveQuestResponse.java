// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.agis.objects.QuestState;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class RemoveQuestResponse extends Event
{
    OID playerId;
    OID questId;
    
    public RemoveQuestResponse() {
        this.playerId = null;
        this.questId = null;
    }
    
    public RemoveQuestResponse(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.playerId = null;
        this.questId = null;
    }
    
    public RemoveQuestResponse(final QuestState questState) {
        this.playerId = null;
        this.questId = null;
        this.setPlayerOid(questState.getPlayerOid());
    }
    
    public String getName() {
        return "RemoveQuestResponse";
    }
    
    void setPlayerOid(final OID id) {
        this.playerId = id;
    }
    
    void setQuestId(final OID id) {
        this.questId = id;
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.playerId);
        buf.putInt(msgId);
        buf.putOID(this.questId);
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setPlayerOid(buf.getOID());
        buf.getInt();
        this.setQuestId(buf.getOID());
    }
}
