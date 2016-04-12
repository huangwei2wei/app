// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.engine.OID;
import atavism.server.util.AORuntimeException;
import atavism.server.engine.Engine;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import atavism.server.objects.Entity;
import atavism.agis.objects.QuestState;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.Event;

public class NewQuestStateEvent extends Event
{
    protected byte[] questStateData;
    
    public NewQuestStateEvent() {
        this.questStateData = null;
    }
    
    public NewQuestStateEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.questStateData = null;
    }
    
    public NewQuestStateEvent(final AgisMob player, final QuestState questState) {
        super((Entity)player);
        this.questStateData = null;
        try {
            final ByteArrayOutputStream ba = new ByteArrayOutputStream();
            final ObjectOutputStream os = new ObjectOutputStream(ba);
            os.writeObject(questState);
            this.setData(ba.toByteArray());
        }
        catch (IOException e) {
            throw new RuntimeException("newqueststateevent", e);
        }
    }
    
    public String getName() {
        return "NewQuestStateEvent";
    }
    
    public byte[] getData() {
        return this.questStateData;
    }
    
    public void setData(final byte[] questStateData) {
        this.questStateData = questStateData;
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(20);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        final byte[] data = this.getData();
        if (data.length > 10000) {
            throw new AORuntimeException("NewQuestStateEvent.toBytes: overflow");
        }
        buf.putInt(data.length);
        buf.putBytes(data, 0, data.length);
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        final OID playerId = buf.getOID();
        this.setObjectOid(playerId);
        buf.getInt();
        final int dataLen = buf.getInt();
        final byte[] data = new byte[dataLen];
        buf.getBytes(data, 0, dataLen);
        this.setData(data);
    }
}
