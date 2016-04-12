// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.agis.arenas.CaptureTheFlagArena;
import atavism.server.math.Quaternion;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.objects.DisplayContext;
import atavism.server.engine.OID;
import atavism.server.math.Point;

public class ArenaFlagPlatform extends ArenaObject
{
    private ArenaFlag flag;
    private static final long serialVersionUID = 1L;
    
    public ArenaFlagPlatform() {
    }
    
    public ArenaFlagPlatform(final int id, final Point loc, final OID instanceOID, final String objectType, final DisplayContext dc, final HashMap<String, Serializable> props) {
        this(id, loc, new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), instanceOID, objectType, dc, props);
    }
    
    public ArenaFlagPlatform(final int id, final Point loc, final Quaternion orientation, final OID instanceOID, final String objectType, final DisplayContext dc, final HashMap<String, Serializable> props) {
        this.id = id;
        this.loc = loc;
        this.orientation = orientation;
        this.props = props;
        this.instanceOID = instanceOID;
        this.objectType = objectType;
        this.dc = dc;
        this.teamToReactTo = -1;
        this.flag = null;
        this.spawn();
    }
    
    public ArenaFlag spawnFlag(final CaptureTheFlagArena arena, final int team) {
        return this.flag = new ArenaFlag(this.id, this.loc, this.instanceOID, "Flag", null, team, false, arena);
    }
    
    public void flagTaken() {
        this.flag = null;
    }
    
    public boolean hasFlag() {
        return this.flag != null;
    }
}
