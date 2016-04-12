// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.server.objects.AOObject;
import atavism.agis.objects.AgisQuest;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;

public class QuestCompleted extends AgisEvent
{
    OID questId;
    
    public QuestCompleted() {
        this.questId = null;
    }
    
    public QuestCompleted(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.questId = null;
    }
    
    public QuestCompleted(final AgisMob user, final AgisQuest quest) {
        this.questId = null;
        this.setObject((AOObject)user);
        this.setQuestId(quest.getOid());
    }
    
    public String getName() {
        return "QuestCompleted";
    }
    
    public void setQuestId(final OID id) {
        this.questId = id;
    }
    
    public OID getQuestId() {
        return this.questId;
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getQuestId());
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setQuestId(buf.getOID());
    }
}
