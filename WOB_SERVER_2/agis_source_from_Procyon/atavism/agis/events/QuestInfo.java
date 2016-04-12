// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import java.util.Iterator;
import atavism.server.engine.Engine;
import java.util.Collection;
import java.util.LinkedList;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.agis.objects.AgisMob;
import atavism.server.util.AORuntimeException;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import java.util.List;
import atavism.server.engine.OID;
import atavism.server.engine.Event;

public class QuestInfo extends Event
{
    String title;
    String desc;
    String objective;
    OID questId;
    List<Reward> rewards;
    protected OID questNpcOid;
    transient Lock lock;
    
    public QuestInfo() {
        this.title = null;
        this.desc = null;
        this.objective = null;
        this.questId = null;
        this.rewards = null;
        this.questNpcOid = null;
        this.lock = LockFactory.makeLock("QuestInfoLock");
    }
    
    public QuestInfo(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.title = null;
        this.desc = null;
        this.objective = null;
        this.questId = null;
        this.rewards = null;
        this.questNpcOid = null;
        this.lock = LockFactory.makeLock("QuestInfoLock");
    }
    
    public String toString() {
        try {
            return "[Event=QuestInfo: player=" + this.getObjectOid() + ",npc=" + this.getQuestNpc().getName() + ",questId=" + this.getQuestId() + ",title=" + this.getTitle() + ",desc=" + this.getDesc() + ",objective=" + this.getObjective() + "]";
        }
        catch (AORuntimeException e) {
            throw new RuntimeException("questinfo.tostring", (Throwable)e);
        }
    }
    
    public String getName() {
        return "QuestInfo";
    }
    
    public AgisMob getQuestNpc() {
        return AgisMob.convert(AOObject.getObject(this.questNpcOid));
    }
    
    public OID getQuestNpcOid() {
        return this.questNpcOid;
    }
    
    public void setQuestNpcOid(final OID questNpcOid) {
        this.questNpcOid = questNpcOid;
    }
    
    public void setTitle(final String s) {
        this.title = s;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setDesc(final String s) {
        this.desc = s;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    public void setObjective(final String s) {
        this.objective = s;
    }
    
    public String getObjective() {
        return this.objective;
    }
    
    public void setQuestId(final OID oid) {
        this.questId = oid;
    }
    
    public OID getQuestId() {
        return this.questId;
    }
    
    public void setRewards(final List<Reward> rewards) {
        this.lock.lock();
        try {
            this.rewards = new LinkedList<Reward>(rewards);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public List<Reward> getRewards() {
        this.lock.lock();
        try {
            return new LinkedList<Reward>(this.rewards);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(500);
        buf.putOID(this.getObjectOid());
        buf.putInt(msgId);
        buf.putOID(this.getQuestNpc().getOid());
        buf.putOID(this.getQuestId());
        buf.putString(this.getTitle());
        buf.putString(this.getDesc());
        buf.putString(this.getObjective());
        this.lock.lock();
        try {
            if (this.rewards == null) {
                buf.putInt(0);
            }
            else {
                final int size = this.rewards.size();
                buf.putInt(size);
                for (final Reward reward : this.rewards) {
                    buf.putString(reward.name);
                    buf.putString(reward.icon);
                    buf.putInt(reward.count);
                }
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
        final OID playerId = buf.getOID();
        this.setObjectOid(playerId);
        buf.getInt();
        final OID questNpcId = buf.getOID();
        this.setQuestNpcOid(questNpcId);
        this.setQuestId(buf.getOID());
        this.setTitle(buf.getString());
        this.setDesc(buf.getString());
        this.setObjective(buf.getString());
        this.lock.lock();
        try {
            this.rewards = new LinkedList<Reward>();
            for (int size = buf.getInt(); size > 0; --size) {
                final String name = buf.getString();
                final String icon = buf.getString();
                final int count = buf.getInt();
                final Reward reward = new Reward(name, icon, count);
                this.rewards.add(reward);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public static class Reward
    {
        public String name;
        public String icon;
        public int count;
        
        public Reward(final String name, final String icon, final int count) {
            this.name = null;
            this.icon = null;
            this.count = 0;
            this.name = name;
            this.icon = icon;
            this.count = count;
        }
    }
}
