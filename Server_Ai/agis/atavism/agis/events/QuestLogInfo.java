// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import java.util.Iterator;
import atavism.server.engine.Engine;
import java.util.Collection;
import java.util.LinkedList;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import java.util.List;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class QuestLogInfo extends Event
{
    OID playerId;
    OID questId;
    String title;
    String desc;
    String obj;
    List<QuestInfo.Reward> rewards;
    transient Lock lock;
    
    public QuestLogInfo() {
        this.rewards = null;
        this.lock = LockFactory.makeLock("QuestLogInfo");
    }
    
    public QuestLogInfo(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.rewards = null;
        this.lock = LockFactory.makeLock("QuestLogInfo");
    }
    
    public String getName() {
        return "QuestLogInfo";
    }
    
    void setPlayerOid(final OID id) {
        this.playerId = id;
    }
    
    void setQuestId(final OID id) {
        this.questId = id;
    }
    
    void setTitle(final String title) {
        this.title = title;
    }
    
    void setDesc(final String desc) {
        this.desc = desc;
    }
    
    void setObjective(final String obj) {
        this.obj = obj;
    }
    
    public void setRewards(final List<QuestInfo.Reward> rewards) {
        this.lock.lock();
        try {
            this.rewards = new LinkedList<QuestInfo.Reward>(rewards);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public List<QuestInfo.Reward> getRewards() {
        this.lock.lock();
        try {
            return new LinkedList<QuestInfo.Reward>(this.rewards);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(500);
        buf.putOID(this.playerId);
        buf.putInt(msgId);
        buf.putOID(this.questId);
        buf.putString(this.title);
        buf.putString(this.desc);
        buf.putString(this.obj);
        this.lock.lock();
        try {
            final int size = this.rewards.size();
            buf.putInt(size);
            for (final QuestInfo.Reward reward : this.rewards) {
                buf.putString(reward.name);
                buf.putString(reward.icon);
                buf.putInt(reward.count);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
        buf.flip();
        return buf;
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setPlayerOid(buf.getOID());
        buf.getInt();
        this.setQuestId(buf.getOID());
        this.setTitle(buf.getString());
        this.setDesc(buf.getString());
        this.setObjective(buf.getString());
        this.lock.lock();
        try {
            this.rewards = new LinkedList<QuestInfo.Reward>();
            for (int size = buf.getInt(); size > 0; --size) {
                final String name = buf.getString();
                final String icon = buf.getString();
                final int count = buf.getInt();
                final QuestInfo.Reward reward = new QuestInfo.Reward(name, icon, count);
                this.rewards.add(reward);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
}
