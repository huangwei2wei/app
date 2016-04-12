// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.server.math.Quaternion;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.objects.DisplayContext;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.agis.arenas.BomberArena;

public class ArenaBomb extends ArenaObject
{
    private int detonationTime;
    private int blastSize;
    private BomberArena arena;
    private OID bombOwner;
    private Point blastCentre;
    private static final long serialVersionUID = 1L;
    
    public ArenaBomb() {
    }
    
    public ArenaBomb(final int id, final Point loc, final OID instanceOID, final String objectType, final DisplayContext dc, final HashMap<String, Serializable> props, final OID bombOwner, final int blastSize, final Point blastCentre, final BomberArena arena) {
        this(id, loc, new Quaternion(0.0f, 0.0f, 0.0f, 1.0f), instanceOID, objectType, dc, props, bombOwner, blastSize, blastCentre, arena);
    }
    
    public ArenaBomb(final int id, final Point loc, final Quaternion orientation, final OID instanceOID, final String objectType, final DisplayContext dc, final HashMap<String, Serializable> props, final OID bombOwner, final int blastSize, final Point blastCentre, final BomberArena arena) {
        this.id = id;
        this.loc = loc;
        this.orientation = orientation;
        this.instanceOID = instanceOID;
        this.objectType = objectType;
        this.dc = dc;
        this.bombOwner = bombOwner;
        this.props = props;
        this.teamToReactTo = -1;
        this.detonationTime = 4;
        this.arena = arena;
        this.blastSize = blastSize;
        this.blastCentre = blastCentre;
        this.spawn();
        Engine.getExecutor().schedule(this, this.detonationTime, TimeUnit.SECONDS);
        Log.debug("BOMB: scheduling bomb to explode in " + this.detonationTime + " seconds.");
    }
    
    @Override
    public void run() {
        if (this.active) {
            Log.debug("BOMB: calling bombExploded");
            this.arena.bombExploded(this);
        }
        this.active = false;
    }
    
    public int getBlastSize() {
        return this.blastSize;
    }
    
    public OID getBombOwner() {
        return this.bombOwner;
    }
    
    public Point getBlastCentre() {
        return this.blastCentre;
    }
}
