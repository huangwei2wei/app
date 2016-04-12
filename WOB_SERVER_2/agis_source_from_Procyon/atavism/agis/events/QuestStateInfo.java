// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import java.util.LinkedList;
import java.util.Iterator;
import atavism.server.engine.Engine;
import atavism.agis.objects.QuestState;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import java.util.List;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class QuestStateInfo extends Event
{
    OID playerId;
    OID questId;
    List<String> objStatus;
    
    public QuestStateInfo() {
        this.playerId = null;
        this.questId = null;
        this.objStatus = null;
    }
    
    public QuestStateInfo(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.playerId = null;
        this.questId = null;
        this.objStatus = null;
    }
    
    public QuestStateInfo(final AgisMob agisMob, final QuestState questState) {
        this.playerId = null;
        this.questId = null;
        this.objStatus = null;
        this.setPlayerOid(agisMob.getOid());
        this.setObjectiveStatus(questState.getObjectiveStatus().get(0));
    }
    
    public String getName() {
        return "QuestStateInfo";
    }
    
    void setPlayerOid(final OID id) {
        this.playerId = id;
    }
    
    void setQuestId(final OID id) {
        this.questId = id;
    }
    
    void setObjectiveStatus(final List<String> objStatus) {
        this.objStatus = objStatus;
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(500);
        buf.putOID(this.playerId);
        buf.putInt(msgId);
        buf.putOID(this.questId);
        buf.putInt(this.objStatus.size());
        final Iterator<String> iter = this.objStatus.iterator();
        while (iter.hasNext()) {
            buf.putString((String)iter.next());
        }
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setPlayerOid(buf.getOID());
        buf.getInt();
        this.setQuestId(buf.getOID());
        final List<String> l = new LinkedList<String>();
        for (int len = buf.getInt(); len > 0; --len) {
            l.add(buf.getString());
        }
        this.setObjectiveStatus(l);
    }
}
