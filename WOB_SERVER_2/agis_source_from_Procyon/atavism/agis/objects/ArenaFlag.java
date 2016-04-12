// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.objects.DisplayContext;
import atavism.server.util.Log;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.server.math.Quaternion;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import atavism.agis.arenas.CaptureTheFlagArena;

public class ArenaFlag extends ArenaObject
{
    protected int team;
    protected boolean reactToAllTeams;
    protected CaptureTheFlagArena arena;
    private static final long serialVersionUID = 1L;
    
    public ArenaFlag() {
    }
    
    public ArenaFlag(final int id, final Point loc, final OID instanceOID, final String objectType, final HashMap<String, Serializable> props, final int team, final boolean reactToAllTeams, final CaptureTheFlagArena arena) {
        this(id, loc, new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), instanceOID, objectType, props, team, reactToAllTeams, arena);
    }
    
    public ArenaFlag(final int id, final Point loc, final Quaternion orientation, final OID instanceOID, final String objectType, final HashMap<String, Serializable> props, final int team, final boolean reactToAllTeams, final CaptureTheFlagArena arena) {
        this.id = id;
        this.loc = loc;
        this.orientation = orientation;
        this.instanceOID = instanceOID;
        this.objectType = objectType;
        this.arena = arena;
        this.dc = this.getFlagDC(team);
        this.props = props;
        this.teamToReactTo = -1;
        this.team = team;
        this.reactToAllTeams = reactToAllTeams;
        this.spawn();
    }
    
    @Override
    protected void spawn() {
        final Template markerTemplate = new Template();
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME, (Serializable)(String.valueOf(this.objectType) + this.id));
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, (Serializable)WorldManagerClient.TEMPL_OBJECT_TYPE_STRUCTURE);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, (Serializable)this.instanceOID);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, (Serializable)this.loc);
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_ORIENT, (Serializable)this.orientation);
        markerTemplate.put(WorldManagerClient.NAMESPACE, "targetable", (Serializable)false);
        if (this.props != null) {
            for (final String propName : this.props.keySet()) {
                markerTemplate.put(Namespace.WORLD_MANAGER, propName, (Serializable)this.props.get(propName));
            }
        }
        markerTemplate.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_DISPLAY_CONTEXT, (Serializable)this.dc);
        markerTemplate.put(Namespace.WORLD_MANAGER, "StaticAnim", (Serializable)"base");
        this.objectOID = ObjectManagerClient.generateObject(-1, "BaseTemplate", markerTemplate);
        if (this.objectOID != null) {
            WorldManagerClient.spawn(this.objectOID);
            this.active = true;
        }
        Engine.getExecutor().schedule(this, 500L, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void run() {
        Log.debug("FLAG: about to mark flag as clickable with team: " + this.team);
        this.arena.setFlagClickable(this, this.reactToAllTeams);
    }
    
    public int getTeam() {
        return this.team;
    }
    
    public DisplayContext getFlagDC(final int team) {
        final DisplayContext flagDC = new DisplayContext(this.arena.getFlagMesh(), true);
        flagDC.setDisplayID(this.arena.getFlagDisplayID(team));
        return flagDC;
    }
}
