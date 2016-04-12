// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.Collection;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import java.util.Iterator;
import atavism.server.objects.Entity;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.util.LockFactory;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import atavism.server.util.AnimationCommand;
import java.util.List;
import atavism.server.engine.Event;

public class NotifyPlayAnimationEvent extends Event
{
    private List<AnimationCommand> animList;
    private transient Lock lock;
    
    public NotifyPlayAnimationEvent() {
        this.animList = new LinkedList<AnimationCommand>();
        this.lock = LockFactory.makeLock("NotifyPlayAnimationEventLock");
    }
    
    public NotifyPlayAnimationEvent(final OID oid) {
        super(oid);
        this.animList = new LinkedList<AnimationCommand>();
        this.lock = LockFactory.makeLock("NotifyPlayAnimationEventLock");
    }
    
    public NotifyPlayAnimationEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.animList = new LinkedList<AnimationCommand>();
        this.lock = LockFactory.makeLock("NotifyPlayAnimationEventLock");
    }
    
    public NotifyPlayAnimationEvent(final AOObject object) {
        super(object);
        this.animList = new LinkedList<AnimationCommand>();
        this.lock = LockFactory.makeLock("NotifyPlayAnimationEventLock");
    }
    
    @Override
    public String getName() {
        return "NotifyPlayAnimationEvent";
    }
    
    @Override
    public String toString() {
        this.lock.lock();
        try {
            String s = "[NotifyPlayAnimationEvent obj=" + this.getObjectOid() + ", size=" + this.animList.size();
            for (final AnimationCommand ac : this.animList) {
                s = s + ", [command=" + ac.getCommand() + ", animName=" + ac.getAnimName() + ", looping=" + ac.isLoop() + "]";
            }
            return s;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public AOByteBuffer toBytes() {
        this.lock.lock();
        try {
            final int msgId = Engine.getEventServer().getEventID(this.getClass());
            final AOByteBuffer buf = new AOByteBuffer(200);
            buf.putOID(this.getObjectOid());
            buf.putInt(msgId);
            if (this.animList == null) {
                if (Log.loggingDebug) {
                    Log.debug("PlayAnimation.toBytes: animList is empty for obj " + this.getObjectOid());
                }
                buf.putInt(0);
            }
            else {
                buf.putInt(this.animList.size());
                for (final AnimationCommand ac : this.animList) {
                    buf.putString(ac.getCommand());
                    if (!ac.getCommand().equals("clear")) {
                        buf.putString(ac.getAnimName());
                        buf.putInt(ac.isLoop() ? 1 : 0);
                    }
                }
            }
            buf.flip();
            return buf;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        this.lock.lock();
        try {
            buf.rewind();
            final OID oid = buf.getOID();
            if (Log.loggingDebug) {
                Log.debug("PlayAnimation.parseBytes: oid=" + oid);
            }
            this.setObjectOid(oid);
            buf.getInt();
            final List<AnimationCommand> list = new LinkedList<AnimationCommand>();
            int len = buf.getInt();
            if (Log.loggingDebug) {
                Log.debug("PlayAnimation.parseBytes: obj=" + this.getObjectOid() + ", listsize=" + len);
            }
            while (len > 0) {
                final String command = buf.getString();
                final String animName = buf.getString();
                final boolean isLoop = buf.getInt() == 1;
                final AnimationCommand ac = new AnimationCommand();
                if (command.equals("add")) {
                    ac.setCommand("add");
                    ac.setAnimName(animName);
                    ac.isLoop(isLoop);
                }
                else if (command.equals("clear")) {
                    ac.setCommand("clear");
                }
                list.add(ac);
                --len;
            }
            this.setAnimList(list);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addAnim(final AnimationCommand ac) {
        this.lock.lock();
        try {
            if (this.animList == null) {
                this.animList = new LinkedList<AnimationCommand>();
            }
            this.animList.add(ac);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setAnimList(final List<AnimationCommand> animList) {
        this.lock.lock();
        try {
            this.animList = new LinkedList<AnimationCommand>(animList);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List getAnimList() {
        this.lock.lock();
        try {
            return new LinkedList(this.animList);
        }
        finally {
            this.lock.unlock();
        }
    }
}
