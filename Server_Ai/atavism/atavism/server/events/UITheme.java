// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.Iterator;
import atavism.server.engine.OID;
import atavism.server.engine.Engine;
import java.util.Collection;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.List;
import atavism.server.engine.Event;

public class UITheme extends Event
{
    List<String> uiThemes;
    transient Lock lock;
    
    public UITheme() {
        this.uiThemes = new LinkedList<String>();
        this.lock = LockFactory.makeLock("UIThemeEventLock");
    }
    
    public UITheme(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.uiThemes = new LinkedList<String>();
        this.lock = LockFactory.makeLock("UIThemeEventLock");
    }
    
    public UITheme(final List<String> uiThemes) {
        this.uiThemes = new LinkedList<String>();
        this.lock = LockFactory.makeLock("UIThemeEventLock");
        this.setThemes(uiThemes);
    }
    
    @Override
    public String getName() {
        return "UITheme";
    }
    
    public void setThemes(final List<String> uiThemes) {
        this.lock.lock();
        try {
            this.uiThemes = new LinkedList<String>(uiThemes);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addTheme(final String theme) {
        this.lock.lock();
        try {
            this.uiThemes.add(theme);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<String> getThemes() {
        this.lock.lock();
        try {
            return new LinkedList<String>(this.uiThemes);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final int themeCount = this.uiThemes.size();
        final AOByteBuffer buf = new AOByteBuffer(200 * themeCount + 20);
        buf.putOID(null);
        buf.putInt(msgId);
        this.lock.lock();
        try {
            buf.putInt(themeCount);
            for (final String theme : this.uiThemes) {
                buf.putString(theme);
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
        buf.rewind();
        buf.getOID();
        buf.getInt();
        final List<String> uiThemes = new LinkedList<String>();
        for (int numThemes = buf.getInt(); numThemes > 0; --numThemes) {
            final String theme = buf.getString();
            uiThemes.add(theme);
        }
        this.setThemes(uiThemes);
    }
}
