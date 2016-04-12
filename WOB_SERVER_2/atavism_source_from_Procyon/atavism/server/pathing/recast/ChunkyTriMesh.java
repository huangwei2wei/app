// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

import java.util.Comparator;
import java.util.Arrays;
import atavism.server.math.IntVector2;

public class ChunkyTriMesh
{
    public ChunkyTriMeshNode[] Nodes;
    public int NNodes;
    public int[] Tris;
    public int NTris;
    public int MaxTrisPerChunk;
    
    public ChunkyTriMesh(final RecastVertex[] verts, final Integer[] tris, final int ntris, final int trisPerChunk) {
        final int nchunks = (ntris + trisPerChunk - 1) / trisPerChunk;
        this.Nodes = new ChunkyTriMeshNode[nchunks * 4];
        this.Tris = new int[ntris * 3];
        this.NTris = ntris;
        final BoundsItem[] items = new BoundsItem[ntris];
        for (int i = 0; i < ntris; ++i) {
            final int t = i * 3;
            items[i] = new BoundsItem();
            final BoundsItem it = items[i];
            it.i = i;
            it.bmin[0] = (it.bmax[0] = verts[tris[t]].X);
            it.bmin[1] = (it.bmax[1] = verts[tris[t]].Z);
            for (int j = 1; j < 3; ++j) {
                final int v = tris[t + j];
                if (verts[v].X < it.bmin[0]) {
                    it.bmin[0] = verts[v].X;
                }
                if (verts[v].Z < it.bmin[1]) {
                    it.bmin[1] = verts[v].Z;
                }
                if (verts[v].X > it.bmax[0]) {
                    it.bmax[0] = verts[v].X;
                }
                if (verts[v].Z > it.bmax[1]) {
                    it.bmax[1] = verts[v].Z;
                }
            }
        }
        final IntVector2 curTriNode = new IntVector2(0, 0);
        this.Subdivide(items, ntris, 0, ntris, trisPerChunk, curTriNode, nchunks * 4, tris);
        this.NNodes = curTriNode.x;
        this.MaxTrisPerChunk = 0;
        for (int k = 0; k < this.NNodes; ++k) {
            final Boolean isLeaf = this.Nodes[k].i >= 0;
            if (isLeaf) {
                if (this.Nodes[k].n > this.MaxTrisPerChunk) {
                    this.MaxTrisPerChunk = this.Nodes[k].n;
                }
            }
        }
    }
    
    public int GetChunksOverlappingRect(final float[] bmin, final float[] bmax, final int[] ids, final int maxIds) {
        int i = 0;
        int n = 0;
        while (i < this.NNodes) {
            final ChunkyTriMeshNode node = this.Nodes[i];
            final Boolean overlap = this.CheckOverlapRect(bmin, bmax, node.bmin, node.bmax);
            final Boolean isLeafNode = node.i >= 0;
            if (isLeafNode && overlap && n < maxIds) {
                ids[n] = i;
                ++n;
            }
            if (overlap || isLeafNode) {
                ++i;
            }
            else {
                final int escapeIndex = -node.i;
                i += escapeIndex;
            }
        }
        return n;
    }
    
    private Boolean CheckOverlapRect(final float[] amin, final float[] amax, final float[] bmin, final float[] bmax) {
        Boolean overlap = true;
        overlap = (amin[0] <= bmax[0] && amax[0] >= bmin[0] && overlap);
        overlap = (amin[1] <= bmax[1] && amax[1] >= bmin[1] && overlap);
        return overlap;
    }
    
    private void Subdivide(final BoundsItem[] items, final int nitems, final int imin, final int imax, final int trisPerChunk, final IntVector2 curNodeTri, final int maxNodes, final Integer[] tris) {
        final int inum = imax - imin;
        final int icur = curNodeTri.x;
        if (curNodeTri.x > maxNodes) {
            return;
        }
        ChunkyTriMeshNode node = this.Nodes[curNodeTri.x];
        if (this.Nodes[curNodeTri.x] == null) {
            node = new ChunkyTriMeshNode();
            this.Nodes[curNodeTri.x] = node;
        }
        ++curNodeTri.x;
        if (inum <= trisPerChunk) {
            this.CalcExtends(items, nitems, imin, imax, node);
            node.i = curNodeTri.y;
            node.n = inum;
            for (int i = imin; i < imax; ++i) {
                final int src = items[i].i * 3;
                final int dst = curNodeTri.y * 3;
                ++curNodeTri.y;
                this.Tris[dst + 0] = tris[src + 0];
                this.Tris[dst + 1] = tris[src + 1];
                this.Tris[dst + 2] = tris[src + 2];
            }
        }
        else {
            this.CalcExtends(items, nitems, imin, imax, node);
            final int axis = this.LongestAxis(node.bmax[0] - node.bmin[0], node.bmax[1] - node.bmin[1]);
            if (axis == 0) {
                Arrays.sort(items, imin, inum, new CompareItemX());
            }
            else if (axis == 1) {
                Arrays.sort(items, imin, inum, new CompareItemY());
            }
            final int isplit = imin + inum / 2;
            this.Subdivide(items, nitems, imin, isplit, trisPerChunk, curNodeTri, maxNodes, tris);
            this.Subdivide(items, nitems, isplit, imax, trisPerChunk, curNodeTri, maxNodes, tris);
            final int iescape = curNodeTri.x - icur;
            node.i = -iescape;
        }
    }
    
    private void CalcExtends(final BoundsItem[] items, final int nitems, final int imin, final int imax, final ChunkyTriMeshNode node) {
        node.bmin[0] = items[imin].bmin[0];
        node.bmin[1] = items[imin].bmin[1];
        node.bmax[0] = items[imin].bmax[0];
        node.bmax[1] = items[imin].bmax[1];
        for (int i = imin + 1; i < imax; ++i) {
            final BoundsItem it = items[i];
            if (it.bmin[0] < node.bmin[0]) {
                node.bmin[0] = it.bmin[0];
            }
            if (it.bmin[1] < node.bmin[1]) {
                node.bmin[1] = it.bmin[1];
            }
            if (it.bmax[0] > node.bmax[0]) {
                node.bmax[0] = it.bmax[0];
            }
            if (it.bmax[1] > node.bmax[1]) {
                node.bmax[1] = it.bmax[1];
            }
        }
    }
    
    private int LongestAxis(final float x, final float y) {
        return (y > x) ? 1 : 0;
    }
    
    public class CompareItemX implements Comparator<Object>
    {
        @Override
        public int compare(final Object va, final Object vb) {
            final BoundsItem a = (BoundsItem)va;
            final BoundsItem b = (BoundsItem)vb;
            if (a != null && b != null) {
                if (a.bmin[0] < b.bmin[0]) {
                    return -1;
                }
                if (a.bmin[0] > b.bmin[0]) {
                    return 1;
                }
            }
            return 0;
        }
    }
    
    public class CompareItemY implements Comparator<Object>
    {
        @Override
        public int compare(final Object va, final Object vb) {
            final BoundsItem a = (BoundsItem)va;
            final BoundsItem b = (BoundsItem)vb;
            if (a != null && b != null) {
                if (a.bmin[1] < b.bmin[1]) {
                    return -1;
                }
                if (a.bmin[1] > b.bmin[1]) {
                    return 1;
                }
            }
            return 0;
        }
    }
}
