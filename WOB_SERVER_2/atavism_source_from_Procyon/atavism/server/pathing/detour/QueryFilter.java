// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;

import atavism.server.pathing.recast.Helper;

public class QueryFilter
{
    private float[] _areaCost;
    public int IncludeFlags;
    public int ExcludeFlags;
    
    public QueryFilter() {
        this._areaCost = new float[NavMeshBuilder.MaxAreas];
        for (int i = 0; i < NavMeshBuilder.MaxAreas; ++i) {
            this._areaCost[i] = 1.0f;
        }
        this.ExcludeFlags = 0;
        this.IncludeFlags = 65535;
    }
    
    public Boolean PassFilter(final long refId, final MeshTile tile, final Poly poly) {
        return (poly.Flags & this.IncludeFlags) != 0x0 && (poly.Flags & this.ExcludeFlags) == 0x0;
    }
    
    public float GetCost(final float pax, final float pay, final float paz, final float pbx, final float pby, final float pbz, final long prevRef, final MeshTile prevTile, final Poly prevPoly, final long curRef, final MeshTile curTile, final Poly curPoly, final long nextRef, final MeshTile nextTile, final Poly nextPoly) {
        return Helper.VDist(pax, pay, paz, pbx, pby, pbz) * this._areaCost[curPoly.getArea()];
    }
    
    public float GetAreaCost(final int i) {
        return this._areaCost[i];
    }
    
    public void SetAreaCost(final int i, final float cost) {
        this._areaCost[i] = cost;
    }
}
