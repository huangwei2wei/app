// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.util.LinkedList;
import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import java.util.HashSet;
import atavism.server.engine.Namespace;
import java.util.Collection;
import atavism.server.util.Log;
import atavism.server.math.Quaternion;
import atavism.server.math.AOVector;
import atavism.server.math.Point;
import java.util.Set;
import atavism.server.engine.BasicWorldNode;
import atavism.server.engine.Event;
import java.util.List;
import atavism.server.network.ClientConnection;
import atavism.server.engine.OID;

public class Player
{
    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_LOGIN_PENDING = 1;
    public static final int STATUS_LOGIN_OK = 2;
    public static final int STATUS_LOGOUT = 3;
    public static final int LOAD_PENDING = 0;
    public static final int LOAD_COMPLETE = 1;
    private String name;
    private OID oid;
    private ClientConnection connection;
    private String version;
    private List<String> capabilities;
    private int status;
    private int loadingState;
    private List<Event> deferredEvents;
    private long loginTime;
    private long lastActivityTime;
    private long lastContactTime;
    BasicWorldNode lastLocUpdate;
    private Set<OID> ignoredOids;
    
    public Player(final OID playerOid, final ClientConnection conn) {
        this.name = "";
        this.loadingState = 0;
        this.oid = playerOid;
        this.connection = conn;
        (this.lastLocUpdate = new BasicWorldNode()).setLoc(new Point(0.0f, 0.0f, 0.0f));
        this.lastLocUpdate.setDir(new AOVector(0.0f, 0.0f, 0.0f));
        this.lastLocUpdate.setOrientation(new Quaternion(0.0f, 0.0f, 0.0f, 0.0f));
    }
    
    @Override
    public String toString() {
        return "[oid=" + this.oid + " name=" + this.name + " status=" + statusToString(this.status) + "]";
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof Player && this.oid.compareTo(((Player)other).oid) == 0;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public ClientConnection getConnection() {
        return this.connection;
    }
    
    public void clearConnection() {
        this.connection = null;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public boolean supportsLoadingState() {
        return this.version.startsWith("1.") && !this.version.startsWith("1.0");
    }
    
    public void setVersion(final String vers) {
        this.version = vers;
    }
    
    public List<String> getCapabilities() {
        return this.capabilities;
    }
    
    public boolean hasCapability(final String cap) {
        return this.capabilities != null && this.capabilities.contains(cap);
    }
    
    public void setCapabilities(final List<String> caps) {
        this.capabilities = caps;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void setStatus(final int s) {
        Log.debug("Player: oid=" + this.oid + ", setting status to " + s);
        this.status = s;
    }
    
    public static String statusToString(final int s) {
        switch (s) {
            case 0: {
                return "UNKNOWN";
            }
            case 1: {
                return "LOGIN_PENDING";
            }
            case 2: {
                return "OK";
            }
            case 3: {
                return "LOGOUT";
            }
            default: {
                return s + " (??)";
            }
        }
    }
    
    public int getLoadingState() {
        return this.loadingState;
    }
    
    public void setLoadingState(final int state) {
        Log.debug("Set player loading state to " + state);
        this.loadingState = state;
    }
    
    public List<Event> getDeferredEvents() {
        return this.deferredEvents;
    }
    
    public void setDeferredEvents(final List<Event> events) {
        this.deferredEvents = events;
    }
    
    public long getLoginTime() {
        return this.loginTime;
    }
    
    public void setLoginTime(final long time_ms) {
        this.loginTime = time_ms;
    }
    
    public long getLastActivityTime() {
        return this.lastActivityTime;
    }
    
    public void setLastActivityTime(final long time_ms) {
        this.lastActivityTime = time_ms;
        this.lastContactTime = time_ms;
    }
    
    public long getLastContactTime() {
        return this.lastContactTime;
    }
    
    public void setLastContactTime(final long time_ms) {
        this.lastContactTime = time_ms;
    }
    
    public synchronized void updateIgnoredOids(final List<OID> nowIgnored, final List<OID> noLongerIgnored) {
        if (noLongerIgnored != null) {
            this.ignoredOids.removeAll(noLongerIgnored);
        }
        if (nowIgnored != null) {
            this.ignoredOids.addAll(nowIgnored);
        }
        this.setIgnoredOidsProperty();
    }
    
    public synchronized void setIgnoredOids(final Collection<OID> newIgnoredOids) {
        this.initializeIgnoredOids(newIgnoredOids);
        this.setIgnoredOidsProperty();
    }
    
    public synchronized void setIgnoredOidsProperty() {
        EnginePlugin.setObjectProperty(this.oid, Namespace.WORLD_MANAGER, "ignored_oids", (HashSet)this.ignoredOids);
    }
    
    public synchronized boolean oidIgnored(final OID oid) {
        return this.ignoredOids != null && this.ignoredOids.contains(oid);
    }
    
    public synchronized int ignoredOidCount() {
        return (this.ignoredOids == null) ? 0 : this.ignoredOids.size();
    }
    
    public void initializeIgnoredOids(final Collection<OID> ignoredOids) {
        this.ignoredOids = new HashSet<OID>();
        if (ignoredOids != null) {
            this.ignoredOids.addAll(ignoredOids);
        }
    }
    
    public synchronized List<OID> getIgnoredOids() {
        final List<OID> oids = new LinkedList<OID>();
        if (this.ignoredOids == null) {
            return oids;
        }
        oids.addAll(this.ignoredOids);
        return oids;
    }
}
