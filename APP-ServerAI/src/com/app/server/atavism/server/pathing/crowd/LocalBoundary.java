// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.crowd;

import org.apache.log4j.Logger;

import com.app.server.atavism.server.objects.Vector2;
//import com.app.server.atavism.server.util.Log;
import com.app.server.atavism.server.pathing.detour.NavMeshBuilder;
import com.app.server.atavism.server.pathing.detour.QueryFilter;
import com.app.server.atavism.server.pathing.detour.NavMeshQuery;
import com.app.server.atavism.server.pathing.recast.Helper;

public class LocalBoundary
{
	private Logger log = Logger.getLogger("navmesh");
    private final int MaxLocalSegs = 8;
    private final int MaxLocalPolys = 18;
    private float[] _center;
    private Segment[] _segs;
    private int _nsegs;
    private long[] _polys;
    private int _npolys;
    
    public LocalBoundary() {
        this._center = new float[3];
        this._segs = new Segment[8];
        this._polys = new long[18];
        for (int i = 0; i < 8; ++i) {
            this._segs[i] = new Segment();
        }
        this.Reset();
    }
    
    private void AddSegment(final float dist, final float[] s) {
        Segment seg = null;
        if (this._nsegs <= 0) {
            seg = this._segs[0];
        }
        else if (dist >= this._segs[this._nsegs - 1].d) {
            if (this._nsegs >= 8) {
                return;
            }
            seg = this._segs[this._nsegs];
        }
        else {
            int i;
            for (i = 0; i < this._nsegs && dist > this._segs[i].d; ++i) {}
            final int tgt = i + 1;
            final int n = Math.min(this._nsegs - i, 8 - tgt);
            if (n < 0) {
                System.arraycopy(this._segs, i, this._segs, tgt, n);
            }
            seg = this._segs[i];
        }
        seg.d = dist;
        System.arraycopy(s, 0, seg.s, 0, 6);
        if (this._nsegs < 8) {
            ++this._nsegs;
        }
    }
    
    public void Reset() {
        this._nsegs = 0;
        this._npolys = 0;
        Helper.VSet(this._center, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    }
    
    public void Update(final long refId, final float[] pos, final float collisionQueryRange, final NavMeshQuery navQuery, final QueryFilter filter) {
        final int MaxSegsPerPoly = NavMeshBuilder.VertsPerPoly * 3;
        if (refId <= 0L) {
            this.Reset();
            return;
        }
        Helper.VCopy(this._center, pos);
        final long[] parentPoly = null;
        this._npolys = navQuery.FindLocalNeighbourhood(refId, pos, collisionQueryRange, filter, this._polys, parentPoly, 18).intValue;
        this._nsegs = 0;
        final float[] segs = new float[MaxSegsPerPoly * 6];
        int nsegs = 0;
        final long[] parentrefs = null;
        for (int j = 0; j < this._npolys; ++j) {
            nsegs = navQuery.GetPolyWallSegments(this._polys[j], filter, segs, parentrefs, MaxSegsPerPoly).intValue;
            for (int k = 0; k < nsegs; ++k) {
                final int s = k * 6;
                final Vector2 distSqr = Helper.DistancePtSegSqr2D(pos[0], pos[1], pos[2], segs[s + 0], segs[s + 1], segs[s + 2], segs[s + 3], segs[s + 4], segs[s + 5]);
                if (distSqr.x <= collisionQueryRange * collisionQueryRange) {
                    final float[] tempS = new float[6];
                    System.arraycopy(segs, s, tempS, 0, 6);
                    log.debug("Adding segment");
                    this.AddSegment((float)distSqr.x, tempS);
                }
            }
        }
    }
    
    public Boolean IsValid(final NavMeshQuery navQuery, final QueryFilter filter) {
        if (this._npolys <= 0) {
            return false;
        }
        for (int i = 0; i < this._npolys; ++i) {
            if (!navQuery.IsValidPolyRef(this._polys[i], filter)) {
                return false;
            }
        }
        return true;
    }
    
    public float[] getCenter() {
        return this._center;
    }
    
    public int SegmentCount() {
        return this._nsegs;
    }
    
    public float[] GetSegment(final int i) {
        return this._segs[i].s;
    }
    
    class Segment
    {
        public float[] s;
        public float d;
        
        Segment() {
            this.s = new float[6];
        }
    }
}
