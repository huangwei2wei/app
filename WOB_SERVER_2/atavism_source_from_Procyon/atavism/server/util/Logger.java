// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.util;

import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.Set;

public class Logger
{
    private String subj;
    private static Set<String> subjSet;
    private static Lock staticLock;
    
    public Logger(final String subj) {
        this.subj = null;
        this.subj = subj;
    }
    
    public void net(final String s) {
        if (!Log.loggingNet || !subjectStatus(this.subj)) {
            return;
        }
        Log.net(this.subj + ": " + s);
    }
    
    public void debug(final String s) {
        if (!Log.loggingDebug || !subjectStatus(this.subj)) {
            return;
        }
        Log.debug(this.subj + ": " + s);
    }
    
    public void info(final String s) {
        if (!Log.loggingInfo || !subjectStatus(this.subj)) {
            return;
        }
        Log.info(this.subj + ": " + s);
    }
    
    public void warn(final String s) {
        if (!Log.loggingWarn || !subjectStatus(this.subj)) {
            return;
        }
        Log.warn(this.subj + ": " + s);
    }
    
    public void error(final String s) {
        Log.error(this.subj + ": " + s);
    }
    
    public void dumpStack() {
        Log.dumpStack(this.subj + ": ");
    }
    
    public void dumpStack(final String context) {
        Log.dumpStack(this.subj + ": " + context);
    }
    
    public void dumpStack(final String context, final Thread thread) {
        Log.dumpStack(this.subj + ": " + context, thread);
    }
    
    public void exception(final Exception e) {
        Log.exception(this.subj + ": ", e);
    }
    
    public void exception(final String context, final Exception e) {
        Log.exception(this.subj + ": " + context, e);
    }
    
    public static void logSubject(final String subj) {
        Logger.staticLock.lock();
        try {
            Logger.subjSet.add(subj);
        }
        finally {
            Logger.staticLock.unlock();
        }
    }
    
    private static boolean subjectStatus(final String subj) {
        Logger.staticLock.lock();
        try {
            return Logger.subjSet.isEmpty() || Logger.subjSet.contains(subj);
        }
        finally {
            Logger.staticLock.unlock();
        }
    }
    
    static {
        Logger.subjSet = new HashSet<String>();
        Logger.staticLock = LockFactory.makeLock("LoggerStaticLock");
    }
}
