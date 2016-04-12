// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

import java.util.EnumSet;
import atavism.server.pathing.detour.NavMesh;
import atavism.server.pathing.detour.Status;
import atavism.server.pathing.detour.DetourRaycastHit;
import atavism.server.pathing.detour.DetourStatusReturn;
import atavism.server.util.Log;
import atavism.server.pathing.detour.QueryFilter;
import atavism.server.pathing.detour.NavMeshQuery;
import atavism.server.pathing.recast.Helper;

public class PathCorridor
{
    private float[] _pos;
    private float[] _target;
    private long[] _path;
    private int _npath;
    private int _maxPath;
    
    public PathCorridor() {
        this._pos = new float[3];
        this._target = new float[3];
        this._path = null;
        this._npath = 0;
        this._maxPath = 0;
    }
    
    public Boolean Init(final int maxPath) {
        try {
            if (this._path != null) {
                throw new Exception("Path already exists, reset before initializing");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this._path = new long[maxPath];
        this._npath = 0;
        this._maxPath = maxPath;
        return true;
    }
    
    public void Reset(final long refId, final float[] pos) {
        Helper.VCopy(this._pos, pos);
        Helper.VCopy(this._target, pos);
        this._path[0] = refId;
        this._npath = 1;
    }
    
    public int FindCorners(final float[] cornerVerts, final short[] cornerFlags, final long[] cornerPolys, final int maxCorners, final NavMeshQuery navQuery, final QueryFilter filter) {
        try {
            if (this._path == null || this._npath == 0) {
                throw new Exception("Corridor must be initialised first");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final float MinTargetDist = 0.01f;
        final DetourStatusReturn statusReturn = navQuery.FindStraightPath(this._pos, this._target, this._path, this._npath, cornerVerts, cornerFlags, cornerPolys, maxCorners);
        int ncorners = statusReturn.intValue;
        Log.debug("CROWD: got nCorners: " + ncorners + " with pos: " + this._pos[0] + "," + this._pos[1] + "," + this._pos[2] + " and target: " + this._target[0] + "," + this._target[1] + "," + this._target[2]);
        while (ncorners > 0) {
            Log.debug("CROWD: cornerFlags: " + cornerFlags[0] + " against: " + NavMeshQuery.StraightPathOffMeshConnection + " with distance: " + Helper.VDist2DSqr(cornerVerts[0], cornerVerts[1], cornerVerts[2], this._pos[0], this._pos[1], this._pos[2]) + " and min: " + MinTargetDist + " and cornerVerts: " + cornerVerts[0] + "," + cornerVerts[1] + "," + cornerVerts[2]);
            if ((cornerFlags[0] & NavMeshQuery.StraightPathOffMeshConnection) != 0x0) {
                break;
            }
            if (Helper.VDist2DSqr(cornerVerts[0], cornerVerts[1], cornerVerts[2], this._pos[0], this._pos[1], this._pos[2]) > MinTargetDist * MinTargetDist) {
                break;
            }
            if (--ncorners <= 0) {
                continue;
            }
            System.arraycopy(cornerFlags, 1, cornerFlags, 0, ncorners);
            System.arraycopy(cornerPolys, 1, cornerPolys, 0, ncorners);
            System.arraycopy(cornerVerts, 3, cornerVerts, 0, ncorners * 3);
        }
        for (int i = 0; i < ncorners; ++i) {
            if ((cornerFlags[i] & NavMeshQuery.StraightPathOffMeshConnection) != 0x0) {
                ncorners = i + 1;
                break;
            }
        }
        return ncorners;
    }
    
    public void OptimizePathVisibility(final float[] next, final float pathOptimizationRange, final NavMeshQuery navQuery, final QueryFilter filter) {
        try {
            if (this._path == null) {
                throw new Exception("Corridor must be initialised first");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        float[] goal = new float[3];
        Helper.VCopy(goal, next);
        float dist = Helper.VDist2D(this._pos, goal);
        if (dist < 0.01f) {
            return;
        }
        dist = Math.min(dist + 0.01f, pathOptimizationRange);
        final float[] delta = Helper.VSub(goal[0], goal[1], goal[2], this._pos[0], this._pos[1], this._pos[2]);
        goal = Helper.VMad(goal, this._pos, delta, pathOptimizationRange / dist);
        final int MaxRes = 32;
        final long[] res = new long[MaxRes];
        final float[] norm = new float[3];
        final DetourRaycastHit raycastHit = navQuery.Raycast(this._path[0], this._pos, goal, filter, norm, res, MaxRes);
        final int nres = raycastHit.pathCount;
        if (nres > 1 && raycastHit.t > 0.99f) {
            this._npath = MergeCorridorStartShortcut(this._path, this._npath, this._maxPath, res, nres);
        }
    }
    
    public Boolean OptimizePathTopology(final NavMeshQuery navQuery, final QueryFilter filter) {
        if (this._npath < 3) {
            return false;
        }
        final int MaxIters = 32;
        final int MaxRes = 32;
        final long[] res = new long[MaxRes];
        navQuery.InitSlicedFindPath(this._path[0], this._path[this._npath - 1], this._pos, this._target, filter);
        navQuery.UpdateSlicedFindPath(MaxIters);
        final DetourStatusReturn statusReturn = navQuery.FinalizeSlicedFindPathPartial(this._path, this._npath, res, MaxRes);
        final int nres = statusReturn.intValue;
        if (statusReturn.status.contains(Status.Success) && nres > 0) {
            this._npath = MergeCorridorStartShortcut(this._path, this._npath, this._maxPath, res, nres);
            return true;
        }
        return false;
    }
    
    public Boolean MoveOverOffmeshConnection(final long offMeshConRef, final long[] refs, final float[] startPos, final float[] endPos, final NavMeshQuery navQuery) {
        long prefRef = 0L;
        long polyRef;
        int npos;
        for (polyRef = this._path[0], npos = 0; npos < this._npath && polyRef != offMeshConRef; polyRef = this._path[npos], ++npos) {
            prefRef = polyRef;
        }
        if (npos == this._npath) {
            return false;
        }
        for (int i = npos; i < this._npath; ++i) {
            this._path[i - npos] = this._path[i];
        }
        this._npath -= npos;
        refs[0] = prefRef;
        refs[1] = polyRef;
        final NavMesh nav = navQuery.NavMesh;
        final EnumSet<Status> status = nav.GetOffMeshConnectionPolyEndPoints(refs[0], refs[1], startPos, endPos);
        if (status.contains(Status.Success)) {
            Helper.VCopy(this._pos, endPos);
            return true;
        }
        return false;
    }
    /**
     * ÐÞ¸´¿ªÊ¼Â·¾¶
     * @param safeRef
     * @param safePos
     * @return
     */
    public Boolean FixPathStart(final long safeRef, final float[] safePos) {
        Helper.VCopy(this._pos, safePos);
        if (this._npath < 3 && this._npath > 0) {
            this._path[2] = this._path[this._npath - 1];
            this._path[0] = safeRef;
            this._path[1] = 0L;
            this._npath = 3;
        }
        else {
            this._path[0] = safeRef;
            this._path[1] = 0L;
        }
        return true;
    }
    
    public Boolean TrimInvalidPath(final long safeRef, final float[] safePos, final NavMeshQuery navQuery, final QueryFilter filter) {
        int n;
        for (n = 0; n < this._npath && navQuery.IsValidPolyRef(this._path[n], filter); ++n) {}
        if (n == this._npath) {
            return true;
        }
        if (n == 0) {
            Helper.VCopy(this._pos, safePos);
            this._path[0] = safeRef;
            this._npath = 1;
        }
        else {
            this._npath = n;
        }
        final float[] tgt = new float[3];
        Helper.VCopy(tgt, this._target);
        navQuery.ClosestPointOnPolyBoundary(this._path[this._npath - 1], tgt, this._target);
        return true;
    }
    
    public Boolean IsValid(final int maxLookAhead, final NavMeshQuery navQuery, final QueryFilter filter) {
        for (int n = Math.min(this._npath, maxLookAhead), i = 0; i < n; ++i) {
            if (!navQuery.IsValidPolyRef(this._path[i], filter)) {
                return false;
            }
        }
        return true;
    }
    
    public void MovePosition(final float[] npos, final NavMeshQuery navQuery, final QueryFilter filter) {
        final float[] result = new float[3];
        final int MaxVisited = 16;
        final long[] visited = new long[MaxVisited];
        final int nvisited = navQuery.MoveAlongSurface(this._path[0], this._pos, npos, filter, result, visited, MaxVisited).intValue;
        this._npath = MergeCorridorStartMoved(this._path, this._npath, this._maxPath, visited, nvisited);
        final float h = this._pos[1];
        result[1] = navQuery.GetPolyHeight(this._path[0], result, h).floatValue;
        Helper.VCopy(this._pos, result);
    }
    
    public void MoveTargetPosition(final float[] npos, final NavMeshQuery navQuery, final QueryFilter filter) {
        final float[] result = new float[3];
        final int MaxVisited = 16;
        final long[] visited = new long[MaxVisited];
        final int nvisited = navQuery.MoveAlongSurface(this._path[this._npath - 1], this._target, npos, filter, result, visited, MaxVisited).intValue;
        this._npath = MergeCorridorEndMoved(this._path, this._npath, this._maxPath, visited, nvisited);
        Helper.VCopy(this._target, result);
    }
    
    public void SetCorridor(final float[] target, final long[] path, final int npath) {
        Helper.VCopy(this._target, target);
        System.arraycopy(path, 0, this._path, 0, npath);
        this._npath = npath;
        Log.debug("CORRIDOR: set target to: " + this._target[0] + "," + target[2]);
    }
    
    public float[] Pos() {
        return this._pos;
    }
    
    public float[] Target() {
        return this._target;
    }
    
    public long FirstPoly() {
        return (this._npath > 0) ? this._path[0] : 0L;
    }
    
    public long LastPoly() {
        return (this._npath > 0) ? this._path[this._npath - 1] : 0L;
    }
    
    public long[] GetPath() {
        return this._path;
    }
    
    public int PathCount() {
        return this._npath;
    }
    
    public static int MergeCorridorStartMoved(final long[] path, final int npath, final int maxPath, final long[] visited, final int nvisited) {
        int furthestPath = -1;
        int furthestVisited = -1;
        for (int i = npath - 1; i >= 0; --i) {
            Boolean found = false;
            for (int j = nvisited - 1; j >= 0; --j) {
                if (path[i] == visited[j]) {
                    furthestPath = i;
                    furthestVisited = j;
                    found = true;
                }
            }
            if (found) {
                break;
            }
        }
        if (furthestPath == -1 || furthestVisited == -1) {
            return npath;
        }
        final int req = nvisited - furthestVisited;
        final int orig = Math.min(furthestPath + 1, npath);
        int size = Math.max(0, npath - orig);
        if (req + size > maxPath) {
            size = maxPath - req;
        }
        if (size >= 0) {
            System.arraycopy(path, orig, path, req, size);
        }
        for (int k = 0; k < req; ++k) {
            path[k] = visited[nvisited - 1 - k];
        }
        return req + size;
    }
    
    public static int MergeCorridorEndMoved(final long[] path, final int npath, final int maxPath, final long[] visited, final int nvisited) {
        int furthestPath = -1;
        int furthestVisited = -1;
        for (int i = 0; i < npath; ++i) {
            Boolean found = false;
            for (int j = nvisited - 1; j >= 0; --j) {
                if (path[i] == visited[j]) {
                    furthestPath = i;
                    furthestVisited = j;
                    found = true;
                }
            }
            if (found) {
                break;
            }
        }
        if (furthestPath == -1 || furthestVisited == -1) {
            return npath;
        }
        final int ppos = furthestPath + 1;
        final int vpos = furthestVisited + 1;
        final int count = Math.min(nvisited - vpos, maxPath - ppos);
        if (count >= 0) {
            System.arraycopy(visited, vpos, path, ppos, count);
        }
        return ppos + count;
    }
    
    public static int MergeCorridorStartShortcut(final long[] path, final int npath, final int maxPath, final long[] visited, final int nvisited) {
        int furthestPath = -1;
        int furthestVisited = -1;
        for (int i = npath - 1; i >= 0; --i) {
            Boolean found = false;
            for (int j = nvisited - 1; j >= 0; --j) {
                if (path[i] == visited[j]) {
                    furthestPath = i;
                    furthestVisited = j;
                    found = true;
                }
            }
            if (found) {
                break;
            }
        }
        if (furthestPath == -1 || furthestVisited == -1) {
            return npath;
        }
        final int req = furthestVisited;
        if (req <= 0) {
            return npath;
        }
        final int orig = furthestPath;
        int size = Math.max(0, npath - orig);
        if (req + size > maxPath) {
            size = maxPath - req;
        }
        if (size > 0) {
            System.arraycopy(path, orig, path, req, size);
        }
        for (int k = 0; k < req; ++k) {
            path[k] = visited[k];
        }
        return req + size;
    }
}
