// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.Engine;
import atavism.server.objects.Entity;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class QuestAvailableEvent extends Event
{
    private OID questGiverOid;
    private boolean isAvailableFlag;
    private boolean isConcludableFlag;
    
    public QuestAvailableEvent() {
        this.questGiverOid = null;
        this.isAvailableFlag = false;
        this.isConcludableFlag = false;
    }
    
    public QuestAvailableEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.questGiverOid = null;
        this.isAvailableFlag = false;
        this.isConcludableFlag = false;
    }
    
    public QuestAvailableEvent(final AgisMob user, final AgisMob questGiver, final boolean isAvail, final boolean isConclude) {
        super((Entity)user);
        this.questGiverOid = null;
        this.isAvailableFlag = false;
        this.isConcludableFlag = false;
        this.setQuestGiverOid(questGiver.getOid());
        this.isAvailable(isAvail);
        this.isConcludable(isConclude);
    }
    
    public String getName() {
        return "QuestAvailableEvent";
    }
    
    public void setQuestGiverOid(final OID oid) {
        this.questGiverOid = oid;
    }
    
    public OID getQuestGiverOid() {
        return this.questGiverOid;
    }
    
    public void isAvailable(final boolean flag) {
        this.isAvailableFlag = flag;
    }
    
    public boolean isAvailable() {
        return this.isAvailableFlag;
    }
    
    public void isConcludable(final boolean flag) {
        this.isConcludableFlag = flag;
    }
    
    public boolean isConcludable() {
        return this.isConcludableFlag;
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(32);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getQuestGiverOid());
        buf.putBoolean(this.isAvailable());
        buf.putBoolean(this.isConcludable());
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID userId = buf.getOID();
        this.setObjectOid(userId);
        buf.getInt();
        final OID questGiverOid = buf.getOID();
        this.setQuestGiverOid(questGiverOid);
        this.isAvailable(buf.getBoolean());
        this.isConcludable(buf.getBoolean());
    }
}
