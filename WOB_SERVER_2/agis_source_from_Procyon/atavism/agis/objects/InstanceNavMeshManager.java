// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import atavism.server.math.Point;
import atavism.server.math.AOVector;
import atavism.server.pathing.crowd.CrowdAgentDebugInfo;
import atavism.server.util.Log;
import java.util.concurrent.TimeUnit;
import atavism.server.engine.Engine;
import atavism.server.pathing.recast.NavMeshParamXmlLoader;
import atavism.server.pathing.crowd.Crowd;
import atavism.server.pathing.detour.QueryFilter;
import atavism.server.pathing.detour.NavMeshQuery;
import atavism.server.pathing.detour.NavMesh;
import atavism.server.engine.OID;
import java.util.concurrent.locks.Lock;
import java.util.ArrayList;

public class InstanceNavMeshManager implements Runnable
{
    private ArrayList<DetourActor> actors;
    private ArrayList<DetourActor> actorsToAdd;
    private ArrayList<DetourActor> actorsToRemove;
    protected transient Lock lock;
    private OID instanceOid;
    private NavMesh navMesh;
    private NavMeshQuery navMeshQuery;
    private QueryFilter filter;
    private Crowd crowd;
    private long lastUpdate;
    
    public InstanceNavMeshManager(final String instanceName, final OID instanceOid) {
        this.actors = new ArrayList<DetourActor>();
        this.actorsToAdd = new ArrayList<DetourActor>();
        this.actorsToRemove = new ArrayList<DetourActor>();
        this.lock = null;
        this.navMesh = new NavMesh();
        this.instanceOid = instanceOid;
        this.loadWorldNavMesh(instanceName);
    }
    
    public boolean loadWorldNavMesh(final String name) {
        final String navMeshFilePath = "..\\navmesh\\" + name + "\\" + name + ".xml";
        final NavMeshParamXmlLoader loader = new NavMeshParamXmlLoader(navMeshFilePath);
        final boolean navMeshLoaded = loader.load(this.navMesh);
        if (navMeshLoaded) {
            (this.navMeshQuery = new NavMeshQuery()).Init(this.navMesh, 2048);
            this.filter = new QueryFilter();
            this.filter.IncludeFlags = 15;
            this.filter.ExcludeFlags = 0;
            this.filter.SetAreaCost(1, 1.0f);
            this.filter.SetAreaCost(2, 10.0f);
            this.filter.SetAreaCost(3, 1.0f);
            this.filter.SetAreaCost(4, 1.0f);
            this.filter.SetAreaCost(5, 2.0f);
            this.filter.SetAreaCost(6, 1.5f);
            (this.crowd = new Crowd()).setFilter(this.filter);
            this.crowd.Init(5000, 0.5f, this.navMesh);
            this.lastUpdate = System.currentTimeMillis();
            Engine.getExecutor().scheduleAtFixedRate(this, 500L, 250L, TimeUnit.MILLISECONDS);
        }
        else {
            Log.debug("NAVMESH: navMesh not loaded for instance: " + name);
        }
        return navMeshLoaded;
    }
    
    @Override
    public void run() {
        Log.debug("NAVMESH: running Update for instance: " + this.instanceOid);
        try {
            for (final DetourActor actor : this.actorsToAdd) {
                this.actors.add(actor);
                Log.debug("DETOUR: added Actor: " + actor.getAgentId());
            }
            this.actorsToAdd.clear();
            for (final DetourActor actor : this.actorsToRemove) {
                this.crowd.RemoveAgent(actor.getAgentId());
                this.actors.remove(actor);
            }
            this.actorsToRemove.clear();
            final float timeDif = (System.currentTimeMillis() - this.lastUpdate) / 1000.0f;
            final CrowdAgentDebugInfo info = new CrowdAgentDebugInfo();
            this.crowd.Update(timeDif, info);
            this.lastUpdate = System.currentTimeMillis();
            for (final DetourActor actor2 : this.actors) {
                if (actor2 != null) {
                    if (this.crowd.GetAgent(actor2.getAgentId()) == null) {
                        continue;
                    }
                    final float[] pos = this.crowd.GetAgent(actor2.getAgentId()).npos;
                    final float[] vel = this.crowd.GetAgent(actor2.getAgentId()).vel;
                    actor2.updateDirLoc(new AOVector(vel[0], vel[1], vel[2]), new Point(pos[0], pos[1], pos[2]));
                }
            }
        }
        catch (Exception e) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Log.error("ERROR: caught exception in InstanceMavMesh.Update: " + sw.toString());
        }
        Log.debug("NAVMESH: completed actor update");
    }
    
    public void addActor(final OID actorOid, final Point loc, final DetourActor actor) {
        if (this.navMeshQuery == null) {
            Log.debug("DETOUR: no navMeshQuery so actor not added.");
            return;
        }
        this.actorsToAdd.add(actor);
        final float[] pos = { loc.getX(), loc.getY(), loc.getZ() };
        actor.addToNavMeshManager(this, this.crowd.AddAgent(pos, actor.getParams()));
        actor.activate();
        Log.debug("DETOUR: added Actor to addList: " + actorOid);
    }
    
    public void setActorTarget(final OID actorOid, final Point loc) {
        final DetourActor actor = this.getDetourActorByOid(actorOid);
        if (actor != null) {
            final float[] endPos = { loc.getX(), loc.getY(), loc.getZ() };
            final float[] extents = { 4.0f, 4.0f, 4.0f };
            final float[] nearestPt = new float[3];
            final long endRef = this.navMeshQuery.FindNearestPoly(endPos, extents, this.filter, nearestPt).longValue;
            this.crowd.RequestMoveTarget(actor.getAgentId(), endRef, nearestPt);
        }
    }
    
    public void resetActorTarget(final OID actorOid) {
        final DetourActor actor = this.getDetourActorByOid(actorOid);
        if (actor != null) {
            this.crowd.ResetMoveTarget(actor.getAgentId());
        }
    }
    
    public void setActorSpeed(final OID actorOid, final float speed) {
        final DetourActor actor = this.getDetourActorByOid(actorOid);
        if (actor != null) {
            this.crowd.GetAgent(actor.getAgentId()).DesiredSpeed = speed;
        }
    }
    
    public void removeActor(final OID actorOid) {
        Log.debug("DETOUR: remove actor: " + actorOid);
        final DetourActor actor = this.getDetourActorByOid(actorOid);
        if (actor != null) {
            actor.deactivate();
            this.actorsToRemove.add(actor);
        }
    }
    
    DetourActor getDetourActorByOid(final OID actorOid) {
        for (int i = 0; i < this.actors.size(); ++i) {
            if (this.actors.get(i).getOid().equals((Object)actorOid)) {
                return this.actors.get(i);
            }
        }
        return null;
    }
    
    public OID getInstanceOid() {
        return this.instanceOid;
    }
}
