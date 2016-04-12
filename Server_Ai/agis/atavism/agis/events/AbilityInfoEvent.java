// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import atavism.server.engine.Engine;
import java.util.Iterator;
import atavism.agis.core.AgisAbility;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.Set;
import atavism.server.engine.Event;

public class AbilityInfoEvent extends Event
{
    protected int abilityID;
    protected String icon;
    protected String desc;
    protected Set<String> cooldowns;
    protected Map<String, String> props;
    transient Lock lock;
    
    public AbilityInfoEvent() {
        this.cooldowns = null;
        this.props = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityInfoEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.cooldowns = null;
        this.props = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
    }
    
    public AbilityInfoEvent(final AgisAbility ability) {
        this.cooldowns = null;
        this.props = null;
        this.lock = LockFactory.makeLock("AbilityInfoEvent");
        this.setAbilityID(ability.getID());
        this.setIcon(ability.getIcon());
        this.setDesc("");
        for (final String cooldownID : ability.getCooldownMap().keySet()) {
            this.addCooldown(cooldownID);
        }
        this.setProperty("targetType", ability.getTargetType().toString());
        this.setProperty("minRange", Integer.toString(ability.getMinRange()));
        this.setProperty("maxRange", Integer.toString(ability.getMaxRange()));
        this.setProperty("costProp", ability.getCostProperty());
        this.setProperty("cost", Integer.toString(ability.getActivationCost()));
    }
    
    public String getName() {
        return "AbilityInfoEvent";
    }
    
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID((Class)this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(400);
        this.lock.lock();
        try {
            buf.putInt(-1);
            buf.putInt(msgId);
            buf.putInt(this.abilityID);
            buf.putString(this.icon);
            buf.putString(this.desc);
            int size = this.cooldowns.size();
            buf.putInt(size);
            for (final String cooldown : this.cooldowns) {
                buf.putString(cooldown);
            }
            size = this.props.size();
            buf.putInt(size);
            for (final Map.Entry<String, String> entry : this.props.entrySet()) {
                buf.putString((String)entry.getKey());
                buf.putString((String)entry.getValue());
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
        this.lock.lock();
        try {
            buf.rewind();
            buf.getInt();
            buf.getInt();
            this.setAbilityID(buf.getInt());
            this.setIcon(buf.getString());
            this.setDesc(buf.getString());
            int size = buf.getInt();
            this.cooldowns = new HashSet<String>(size);
            while (size-- > 0) {
                final String cooldown = buf.getString();
                this.cooldowns.add(cooldown);
            }
            size = buf.getInt();
            this.props = new HashMap<String, String>(size);
            while (size-- > 0) {
                final String key = buf.getString();
                final String value = buf.getString();
                this.setProperty(key, value);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public int getAbilityID() {
        return this.abilityID;
    }
    
    public void setAbilityID(final int id) {
        this.abilityID = id;
    }
    
    public String getIcon() {
        return this.icon;
    }
    
    public void setIcon(final String icon) {
        this.icon = icon;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    public void setDesc(final String desc) {
        this.desc = desc;
    }
    
    public void addCooldown(final String cooldownID) {
        this.lock.lock();
        try {
            if (this.cooldowns == null) {
                this.cooldowns = new HashSet<String>();
            }
            this.cooldowns.add(cooldownID);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public Set<String> getCooldowns() {
        this.lock.lock();
        try {
            return new HashSet<String>(this.cooldowns);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public String getProperty(final String key) {
        return this.props.get(key);
    }
    
    public void setProperty(final String key, final String value) {
        this.lock.lock();
        try {
            if (this.props == null) {
                this.props = new HashMap<String, String>();
            }
            this.props.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
}
