// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.util.Collection;
import java.util.HashMap;
import atavism.server.util.LockFactory;
import java.util.List;
import atavism.msgsys.MessageType;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.LinkedList;

public class HookManager
{
    private LinkedList<Hook> nullList;
    private Lock lock;
    private Map<MessageType, List<Hook>> hooks;
    
    public HookManager() {
        this.nullList = new LinkedList<Hook>();
        this.lock = LockFactory.makeLock("HookManager");
        this.hooks = new HashMap<MessageType, List<Hook>>();
    }
    
    public void addHook(final MessageType msgType, final Hook hook) {
        this.lock.lock();
        try {
            List<Hook> hookList = this.hooks.get(msgType);
            if (hookList == null) {
                hookList = new LinkedList<Hook>();
                hookList.add(hook);
                this.hooks.put(msgType, hookList);
            }
            else {
                hookList = new LinkedList<Hook>(hookList);
                hookList.add(hook);
                this.hooks.put(msgType, hookList);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void removeHook(final MessageType msgType, final Hook hook) {
        this.lock.lock();
        try {
            List<Hook> hookList = this.hooks.get(msgType);
            if (hookList != null) {
                hookList = new LinkedList<Hook>(hookList);
                hookList.remove(hook);
                if (hookList.size() == 0) {
                    this.hooks.remove(msgType);
                }
                else {
                    this.hooks.put(msgType, hookList);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<Hook> getHooks(final MessageType msgType) {
        final List<Hook> hookList = this.hooks.get(msgType);
        if (hookList == null) {
            return this.nullList;
        }
        return hookList;
    }
}
