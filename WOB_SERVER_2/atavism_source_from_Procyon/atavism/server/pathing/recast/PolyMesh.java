// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

import atavism.server.math.IntVector3;

public class PolyMesh
{
    public int[] Verts;
    public int[] Polys;
    public int[] Regs;
    public int[] Flags;
    public short[] Areas;
    public int NVerts;
    public int NPolys;
    public int MaxPolys;
    public int Nvp;
    public float[] BMin;
    public float[] BMax;
    public float Cs;
    public float Ch;
    public int BorderSize;
    public static int VertexBucketCount;
    public static int MeshNullIdx;
    
    public PolyMesh(final ContourSet cset, final int nvp) {
        this.BMin = new float[3];
        this.BMax = new float[3];
        System.arraycopy(cset.BMin, 0, this.BMin, 0, 3);
        System.arraycopy(cset.BMax, 0, this.BMax, 0, 3);
        this.Cs = cset.Cs;
        this.Ch = cset.Ch;
        this.BorderSize = cset.BorderSize;
        int maxVertices = 0;
        int maxTris = 0;
        int maxVertsPerCont = 0;
        for (int i = 0; i < cset.NConts; ++i) {
            if (cset.Conts[i].NVerts >= 3) {
                maxVertices += cset.Conts[i].NVerts;
                maxTris += cset.Conts[i].NVerts - 2;
                maxVertsPerCont = Math.max(maxVertsPerCont, cset.Conts[i].NVerts);
            }
        }
        final short[] vflags = new short[maxVertices];
        this.Verts = new int[maxVertices * 3];
        this.Polys = new int[maxTris * nvp * 2];
        this.Regs = new int[maxTris];
        this.Areas = new short[maxTris];
        this.NVerts = 0;
        this.NPolys = 0;
        this.Nvp = nvp;
        this.MaxPolys = maxTris;
        for (int j = 0; j < maxTris * nvp * 2; ++j) {
            this.Polys[j] = 65535;
        }
        final int[] nextVert = new int[maxVertices];
        final int[] firstVert = new int[PolyMesh.VertexBucketCount];
        for (int k = 0; k < firstVert.length; ++k) {
            firstVert[k] = -1;
        }
        final int[] indices = new int[maxVertsPerCont];
        final int[] tris = new int[maxVertsPerCont * 3];
        final int[] polys = new int[(maxVertsPerCont + 1) * nvp];
        final int[] tmpPoly = new int[nvp];
        for (int l = 0; l < cset.NConts; ++l) {
            final Contour cont = cset.Conts[l];
            if (cont.NVerts >= 3) {
                for (int m = 0; m < cont.NVerts; ++m) {
                    indices[m] = m;
                }
                int ntris = this.Triangulate(cont.NVerts, cont.Verts, indices, tris);
                if (ntris <= 0) {
                    ntris = -ntris;
                }
                for (int j2 = 0; j2 < cont.NVerts; ++j2) {
                    final int v = j2 * 4;
                    indices[j2] = this.AddVertex(cont.Verts[v + 0], cont.Verts[v + 1], cont.Verts[v + 2], firstVert, nextVert);
                    if ((cont.Verts[v + 3] & ContourSet.BorderVertex) != 0x0) {
                        vflags[indices[j2]] = 1;
                    }
                }
                int npolys = 0;
                for (int j3 = 0; j3 < polys.length; ++j3) {
                    polys[j3] = 65535;
                }
                for (int j3 = 0; j3 < ntris; ++j3) {
                    final int t = j3 * 3;
                    if (tris[t + 0] != tris[t + 1] && tris[t + 0] != tris[t + 2] && tris[t + 1] != tris[t + 2]) {
                        polys[npolys * nvp + 0] = indices[tris[t + 0]];
                        polys[npolys * nvp + 1] = indices[tris[t + 1]];
                        polys[npolys * nvp + 2] = indices[tris[t + 2]];
                        ++npolys;
                    }
                }
                if (npolys != 0) {
                    if (nvp > 3) {
                        while (true) {
                            int bestMergeVal = 0;
                            int bestPa = 0;
                            int bestPb = 0;
                            int bestEa = 0;
                            int bestEb = 0;
                            for (int j4 = 0; j4 < npolys - 1; ++j4) {
                                final int pj = j4 * nvp;
                                for (int k2 = j4 + 1; k2 < npolys; ++k2) {
                                    final int pk = k2 * nvp;
                                    int ea = 0;
                                    int eb = 0;
                                    final IntVector3 returnVec = this.GetPolyMergeValue(polys, pj, pk, this.Verts, ea, eb, nvp);
                                    final int v2 = returnVec.getX();
                                    ea = returnVec.getY();
                                    eb = returnVec.getZ();
                                    if (v2 > bestMergeVal) {
                                        bestMergeVal = v2;
                                        bestPa = j4;
                                        bestPb = k2;
                                        bestEa = ea;
                                        bestEb = eb;
                                    }
                                }
                            }
                            if (bestMergeVal <= 0) {
                                break;
                            }
                            final int pa = bestPa * nvp;
                            final int pb = bestPb * nvp;
                            this.MergePolys(polys, pa, pb, bestEa, bestEb, tmpPoly, nvp);
                            System.arraycopy(polys, (npolys - 1) * nvp, polys, pb, nvp);
                            --npolys;
                        }
                    }
                    for (int j3 = 0; j3 < npolys; ++j3) {
                        final int p = this.NPolys * nvp * 2;
                        final int q = j3 * nvp;
                        for (int k3 = 0; k3 < nvp; ++k3) {
                            this.Polys[p + k3] = polys[q + k3];
                        }
                        this.Regs[this.NPolys] = cont.Reg;
                        this.Areas[this.NPolys] = cont.Area;
                        ++this.NPolys;
                    }
                }
            }
        }
        for (int l = 0; l < this.NVerts; ++l) {}
        if (!this.BuildMeshAdjacency(nvp)) {}
        if (this.BorderSize > 0) {
            final int w = cset.Width;
            final int h = cset.Height;
            for (int i2 = 0; i2 < this.NPolys; ++i2) {
                for (int p2 = i2 * 2 * nvp, j3 = 0; j3 < nvp && this.Polys[p2 + j3] != PolyMesh.MeshNullIdx; ++j3) {
                    if (this.Polys[p2 + nvp + j3] == PolyMesh.MeshNullIdx) {
                        int nj = j3 + 1;
                        if (nj >= nvp || this.Polys[p2 + nj] == PolyMesh.MeshNullIdx) {
                            nj = 0;
                        }
                        final int va = this.Polys[p2 + j3] * 3;
                        final int vb = this.Polys[p2 + nj] * 3;
                        if (this.Verts[va + 0] == 0 && this.Verts[vb + 0] == 0) {
                            this.Polys[p2 + nvp + j3] = 32768;
                        }
                        else if (this.Verts[va + 2] == h && this.Verts[vb + 2] == h) {
                            this.Polys[p2 + nvp + j3] = 32769;
                        }
                        else if (this.Verts[va + 0] == w && this.Verts[vb + 0] == w) {
                            this.Polys[p2 + nvp + j3] = 32770;
                        }
                        else if (this.Verts[va + 2] == 0 && this.Verts[vb + 2] == 0) {
                            this.Polys[p2 + nvp + j3] = 32771;
                        }
                    }
                }
            }
        }
        this.Flags = new int[this.NPolys];
    }
    
    private int Triangulate(int n, final int[] verts, final int[] indices, final int[] tris) {
        int ntris = 0;
        int trisIndex = 0;
        for (int i = 0; i < n; ++i) {
            final int i2 = this.Next(i, n);
            final int i3 = this.Next(i2, n);
            if (this.Diagonal(i, i3, n, verts, indices)) {
                indices[i2] |= Integer.MIN_VALUE;
            }
        }
        while (n > 3) {
            int minLen = -1;
            int mini = -1;
            for (int j = 0; j < n; ++j) {
                final int i4 = this.Next(j, n);
                if ((indices[i4] & Integer.MIN_VALUE) != 0x0) {
                    final int p0 = (indices[j] & 0xFFFFFFF) * 4;
                    final int p2 = indices[this.Next(i4, n) & 0xFFFFFFF] * 4;
                    final int dx = verts[p2 + 0] - verts[p0 + 0];
                    final int dy = verts[p2 + 2] - verts[p0 + 2];
                    final int len = dx * dx + dy * dy;
                    if (minLen < 0 || len < minLen) {
                        minLen = len;
                        mini = j;
                    }
                }
            }
            if (mini == -1) {
                return -ntris;
            }
            int k = mini;
            int j2 = this.Next(k, n);
            final int j3 = this.Next(j2, n);
            tris[trisIndex++] = (indices[k] & 0xFFFFFFF);
            tris[trisIndex++] = (indices[j2] & 0xFFFFFFF);
            tris[trisIndex++] = (indices[j3] & 0xFFFFFFF);
            ++ntris;
            --n;
            for (int l = j2; l < n; ++l) {
                indices[l] = indices[l + 1];
            }
            if (j2 >= n) {
                j2 = 0;
            }
            k = this.Prev(j2, n);
            if (this.Diagonal(this.Prev(k, n), j2, n, verts, indices)) {
                indices[k] |= Integer.MIN_VALUE;
            }
            else {
                final int n2 = k;
                indices[n2] &= 0xFFFFFFF;
            }
            if (this.Diagonal(k, this.Next(j2, n), n, verts, indices)) {
                indices[j2] |= Integer.MIN_VALUE;
            }
            else {
                final int n3 = j2;
                indices[n3] &= 0xFFFFFFF;
            }
        }
        tris[trisIndex++] = (indices[0] & 0xFFFFFFF);
        tris[trisIndex++] = (indices[1] & 0xFFFFFFF);
        tris[trisIndex++] = (indices[2] & 0xFFFFFFF);
        return ++ntris;
    }
    
    private Boolean Diagonal(final int i, final int j, final int n, final int[] verts, final int[] indices) {
        return this.InCone(i, j, n, verts, indices) && this.Diagonalie(i, j, n, verts, indices);
    }
    
    private Boolean Diagonalie(final int i, final int j, final int n, final int[] verts, final int[] indices) {
        final int d0 = (indices[i] & 0xFFFFFFF) * 4;
        final int d2 = (indices[j] & 0xFFFFFFF) * 4;
        for (int k = 0; k < n; ++k) {
            final int k2 = this.Next(k, n);
            if (k != i && k2 != i && k != j && k2 != j) {
                final int p0 = (indices[k] & 0xFFFFFFF) * 4;
                final int p2 = (indices[k2] & 0xFFFFFFF) * 4;
                if (!this.VEqual(verts[d0 + 0], verts[d0 + 2], verts[p0 + 0], verts[p0 + 2]) && !this.VEqual(verts[d2 + 0], verts[d2 + 2], verts[p0 + 0], verts[p0 + 2]) && !this.VEqual(verts[d0 + 0], verts[d0 + 2], verts[p2 + 0], verts[p2 + 2])) {
                    if (!this.VEqual(verts[d2 + 0], verts[d2 + 2], verts[p2 + 0], verts[p2 + 2])) {
                        if (this.Intersect(verts[d0 + 0], verts[d0 + 2], verts[d2 + 0], verts[d2 + 2], verts[p0 + 0], verts[p0 + 2], verts[p2 + 0], verts[p2 + 2])) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private Boolean Intersect(final int ax, final int az, final int bx, final int bz, final int cx, final int cz, final int dx, final int dz) {
        if (this.IntersectProp(ax, az, bx, bz, cx, cz, dx, dz)) {
            return true;
        }
        if (this.Between(ax, az, bx, bz, cx, cz) || this.Between(ax, az, bx, bz, dx, dz) || this.Between(cx, cz, dx, dz, ax, az) || this.Between(cx, cz, dx, dz, bx, bz)) {
            return true;
        }
        return false;
    }
    
    private Boolean IntersectProp(final int ax, final int az, final int bx, final int bz, final int cx, final int cz, final int dx, final int dz) {
        if (this.Collinear(ax, az, bx, bz, cx, cz) || this.Collinear(ax, az, bx, bz, dx, dz) || this.Collinear(cx, cz, dx, dz, ax, az) || this.Collinear(cx, cz, dx, dz, bx, bz)) {
            return false;
        }
        return this.Xorb(this.Left(ax, az, bx, bz, cx, cz), this.Left(ax, az, bx, bz, dx, dz)) && this.Xorb(this.Left(cx, cz, dx, dz, ax, az), this.Left(cx, cz, dx, dz, bx, bz));
    }
    
    private Boolean Xorb(final Boolean x, final Boolean y) {
        return !x ^ !y;
    }
    
    private Boolean Between(final int ax, final int az, final int bx, final int bz, final int cx, final int cz) {
        if (!this.Collinear(ax, az, bx, bz, cx, cz)) {
            return false;
        }
        if (ax != bx) {
            return (ax <= cx && cx <= bx) || (ax >= cx && cx >= bx);
        }
        return (az <= cz && cz <= bz) || (az >= cz && cz >= bz);
    }
    
    private Boolean VEqual(final int ax, final int az, final int bx, final int bz) {
        return ax == bx && az == bz;
    }
    
    private Boolean InCone(final int i, final int j, final int n, final int[] verts, final int[] indices) {
        final int pi = (indices[i] & 0xFFFFFFF) * 4;
        final int pj = (indices[j] & 0xFFFFFFF) * 4;
        final int pi2 = (indices[this.Next(i, n)] & 0xFFFFFFF) * 4;
        final int pin1 = (indices[this.Prev(i, n)] & 0xFFFFFFF) * 4;
        if (this.LeftOn(verts[pin1 + 0], verts[pin1 + 2], verts[pi + 0], verts[pi + 2], verts[pi2 + 0], verts[pi2 + 2])) {
            return this.Left(verts[pi + 0], verts[pi + 2], verts[pj + 0], verts[pj + 2], verts[pin1 + 0], verts[pin1 + 2]) && this.Left(verts[pj + 0], verts[pj + 2], verts[pi + 0], verts[pi + 2], verts[pi2 + 0], verts[pi2 + 2]);
        }
        return !this.LeftOn(verts[pi + 0], verts[pi + 2], verts[pj + 0], verts[pj + 2], verts[pi2 + 0], verts[pi2 + 2]) || !this.LeftOn(verts[pj + 0], verts[pj + 2], verts[pi + 0], verts[pi + 2], verts[pin1 + 0], verts[pin1 + 2]);
    }
    
    private Boolean LeftOn(final int ax, final int az, final int bx, final int bz, final int cx, final int cz) {
        return this.Area2(ax, az, bx, bz, cx, cz) <= 0;
    }
    
    private Boolean Left(final int ax, final int az, final int bx, final int bz, final int cx, final int cz) {
        return this.Area2(ax, az, bx, bz, cx, cz) < 0;
    }
    
    private Boolean Collinear(final int ax, final int az, final int bx, final int bz, final int cx, final int cz) {
        return this.Area2(ax, az, bx, bz, cx, cz) == 0;
    }
    
    private int Area2(final int ax, final int az, final int bx, final int bz, final int cx, final int cz) {
        return (bx - ax) * (cz - az) - (cx - ax) * (bz - az);
    }
    
    private int Next(final int i, final int n) {
        return (i + 1 < n) ? (i + 1) : 0;
    }
    
    private int Prev(final int i, final int n) {
        return (i - 1 >= 0) ? (i - 1) : (n - 1);
    }
    
    private int AddVertex(final int x, final int y, final int z, final int[] firstVert, final int[] nextVert) {
        final int bucket = this.ComputeVertexHash(x, 0, z);
        for (int i = firstVert[bucket]; i != -1; i = nextVert[i]) {
            final int v = i * 3;
            if (this.Verts[v + 0] == x && Math.abs(this.Verts[v + 1] - y) <= 2 && this.Verts[v + 2] == z) {
                return i;
            }
        }
        int i = this.NVerts;
        ++this.NVerts;
        final int v2 = i * 3;
        this.Verts[v2 + 0] = x;
        this.Verts[v2 + 1] = y;
        this.Verts[v2 + 2] = z;
        nextVert[i] = firstVert[bucket];
        return firstVert[bucket] = i;
    }
    
    private int ComputeVertexHash(final int x, final int y, final int z) {
        final long h1 = -1918454973L;
        final long h2 = -669632447L;
        final long h3 = -887442657L;
        final long n = h1 * x + h2 * y + h3 * z;
        return (int)(n & PolyMesh.VertexBucketCount - 1);
    }
    
    private IntVector3 GetPolyMergeValue(final int[] polys, final int pa, final int pb, final int[] verts, int ea, int eb, final int nvp) {
        final IntVector3 returnVec = new IntVector3();
        final int na = this.CountPolyVerts(polys, pa, nvp);
        final int nb = this.CountPolyVerts(polys, pb, nvp);
        if (na + nb - 2 > nvp) {
            returnVec.setX(-1);
            return returnVec;
        }
        ea = -1;
        eb = -1;
        for (int i = 0; i < na; ++i) {
            int va0 = polys[pa + i];
            int va2 = polys[pa + (i + 1) % na];
            if (va0 > va2) {
                final int temp = va0;
                va0 = va2;
                va2 = temp;
            }
            for (int j = 0; j < nb; ++j) {
                int vb0 = polys[pb + j];
                int vb2 = polys[pb + (j + 1) % nb];
                if (vb0 > vb2) {
                    final int temp2 = vb0;
                    vb0 = vb2;
                    vb2 = temp2;
                }
                if (va0 == vb0 && va2 == vb2) {
                    ea = i;
                    eb = j;
                    break;
                }
            }
        }
        if (ea == -1 || eb == -1) {
            returnVec.setX(-1);
            return returnVec;
        }
        int va3 = polys[pa + (ea + na - 1) % na];
        int vb3 = polys[pa + ea];
        int vc = polys[pb + (eb + 2) % nb];
        if (!this.ULeft(verts[va3 * 3 + 0], verts[va3 * 3 + 2], verts[vb3 * 3 + 0], verts[vb3 * 3 + 2], verts[vc * 3 + 0], verts[vc * 3 + 2])) {
            returnVec.setX(-1);
            return returnVec;
        }
        va3 = polys[pb + (eb + nb - 1) % nb];
        vb3 = polys[pb + eb];
        vc = polys[pa + (ea + 2) % na];
        if (!this.ULeft(verts[va3 * 3 + 0], verts[va3 * 3 + 2], verts[vb3 * 3 + 0], verts[vb3 * 3 + 2], this.Verts[vc * 3 + 0], verts[vc * 3 + 2])) {
            returnVec.setX(-1);
            return returnVec;
        }
        va3 = polys[pa + ea];
        vb3 = polys[pa + (ea + 1) % na];
        final int dx = verts[va3 * 3 + 0] - verts[vb3 * 3 + 0];
        final int dz = verts[va3 * 3 + 2] - verts[vb3 * 3 + 2];
        returnVec.setX(dx * dx + dz * dz);
        returnVec.setY(ea);
        returnVec.setZ(eb);
        return returnVec;
    }
    
    private Boolean ULeft(final int ax, final int az, final int bx, final int bz, final int cx, final int cz) {
        return (bx - ax) * (cz - az) - (cx - ax) * (bz - az) < 0;
    }
    
    private void MergePolys(final int[] polys, final int pa, final int pb, final int ea, final int eb, final int[] tmpPoly, final int nvp) {
        final int na = this.CountPolyVerts(polys, pa, nvp);
        final int nb = this.CountPolyVerts(polys, pb, nvp);
        for (int i = 0; i < nvp; ++i) {
            tmpPoly[i] = 65535;
        }
        int n = 0;
        for (int j = 0; j < na - 1; ++j) {
            tmpPoly[n++] = polys[pa + (ea + 1 + j) % na];
        }
        for (int j = 0; j < nb - 1; ++j) {
            tmpPoly[n++] = polys[pb + (eb + 1 + j) % nb];
        }
        System.arraycopy(tmpPoly, 0, polys, pa, nvp);
    }
    
    private Boolean CanRemoveVertex(final int rem) {
        final int nvp = this.Nvp;
        int numRemovedVerts = 0;
        int numTouchedVerts = 0;
        int numRemainingEdges = 0;
        for (int i = 0; i < this.NPolys; ++i) {
            final int p = i * nvp * 2;
            final int nv = this.CountPolyVerts(this.Polys, p, nvp);
            int numRemoved = 0;
            int numVerts = 0;
            for (int j = 0; j < nv; ++j) {
                if (this.Polys[p + j] == rem) {
                    ++numTouchedVerts;
                    ++numRemoved;
                }
                ++numVerts;
            }
            if (numRemoved > 0) {
                numRemovedVerts += numRemoved;
                numRemainingEdges += numVerts - (numRemoved + 1);
            }
        }
        if (numRemainingEdges <= 2) {
            return false;
        }
        final int maxEdges = numTouchedVerts * 2;
        int nedges = 0;
        final int[] edges = new int[maxEdges * 3];
        for (int k = 0; k < this.NPolys; ++k) {
            final int p2 = k * nvp * 2;
            final int nv2 = this.CountPolyVerts(this.Polys, p2, nvp);
            int l = 0;
            int m = nv2 - 1;
            while (l < nv2) {
                if (this.Polys[p2 + l] == rem || this.Polys[p2 + m] == rem) {
                    int a = this.Polys[p2 + l];
                    int b = this.Polys[p2 + m];
                    if (b == rem) {
                        final int temp = a;
                        a = b;
                        b = temp;
                    }
                    Boolean exists = false;
                    for (int m2 = 0; m2 < nedges; ++m2) {
                        final int e = m2 * 3;
                        if (edges[e + 1] == b) {
                            final int[] array = edges;
                            final int n = e + 2;
                            ++array[n];
                            exists = true;
                        }
                    }
                    if (!exists) {
                        final int e2 = nedges * 3;
                        edges[e2 + 0] = a;
                        edges[e2 + 1] = b;
                        edges[e2 + 2] = 1;
                        ++nedges;
                    }
                }
                m = l++;
            }
        }
        int numOpenEdges = 0;
        for (int i2 = 0; i2 < nedges; ++i2) {
            if (edges[i2 * 3 + 2] < 2) {
                ++numOpenEdges;
            }
        }
        if (numOpenEdges > 2) {
            return false;
        }
        return true;
    }
    
    private Boolean RemoveVertex(final int rem, final int maxTris) {
        final int nvp = this.Nvp;
        int numRemovedVerts = 0;
        for (int i = 0; i < this.NPolys; ++i) {
            final int p = i * nvp * 2;
            for (int nv = this.CountPolyVerts(this.Polys, p, nvp), j = 0; j < nv; ++j) {
                if (this.Polys[p + j] == rem) {
                    ++numRemovedVerts;
                }
            }
        }
        int nedges = 0;
        final int[] edges = new int[numRemovedVerts * nvp * 4];
        int nhole = 0;
        final int[] hole = new int[numRemovedVerts * nvp];
        int nhreg = 0;
        final int[] hreg = new int[numRemovedVerts * nvp];
        int nharea = 0;
        final int[] harea = new int[numRemovedVerts * nvp];
        for (int k = 0; k < this.NPolys; ++k) {
            final int p2 = k * nvp * 2;
            final int nv2 = this.CountPolyVerts(this.Polys, p2, nvp);
            Boolean hasRem = false;
            for (int l = 0; l < nv2; ++l) {
                if (this.Polys[p2 + l] == rem) {
                    hasRem = true;
                }
            }
            if (hasRem) {
                int l = 0;
                int m = nv2 - 1;
                while (l < nv2) {
                    if (this.Polys[p2 + l] != rem && this.Polys[p2 + m] != rem) {
                        final int e = nedges * 4;
                        edges[e + 0] = this.Polys[p2 + m];
                        edges[e + 1] = this.Polys[p2 + l];
                        edges[e + 2] = this.Regs[k];
                        edges[e + 3] = this.Areas[k];
                        ++nedges;
                    }
                    m = l++;
                }
                final int p3 = (this.NPolys - 1) * nvp * 2;
                System.arraycopy(this.Polys, p3, this.Polys, p2, nvp);
                for (int j2 = p2 + nvp; j2 < p2 + nvp + nvp; ++j2) {
                    this.Polys[j2] = 65535;
                }
                this.Regs[k] = this.Regs[this.NPolys - 1];
                this.Areas[k] = this.Areas[this.NPolys - 1];
                --this.NPolys;
                --k;
            }
        }
        for (int k = rem; k < this.NVerts - 1; ++k) {
            this.Verts[k * 3 + 0] = this.Verts[(k + 1) * 3 + 0];
            this.Verts[k * 3 + 1] = this.Verts[(k + 1) * 3 + 1];
            this.Verts[k * 3 + 2] = this.Verts[(k + 1) * 3 + 2];
        }
        --this.NVerts;
        for (int k = 0; k < this.NPolys; ++k) {
            final int p2 = k * nvp * 2;
            for (int nv2 = this.CountPolyVerts(this.Polys, p2, nvp), j3 = 0; j3 < nv2; ++j3) {
                if (this.Polys[p2 + j3] > rem) {
                    final int[] polys2 = this.Polys;
                    final int n = p2 + j3;
                    --polys2[n];
                }
            }
        }
        for (int k = 0; k < nedges; ++k) {
            if (edges[k * 4 + 0] > rem) {
                final int[] array = edges;
                final int n2 = k * 4 + 0;
                --array[n2];
            }
            if (edges[k * 4 + 1] > rem) {
                final int[] array2 = edges;
                final int n3 = k * 4 + 1;
                --array2[n3];
            }
        }
        nhole = this.PushBack(edges[0], hole, nhole);
        nhreg = this.PushBack(edges[2], hreg, nhreg);
        nharea = this.PushBack(edges[3], harea, nharea);
        while (nedges > 0) {
            Boolean match = false;
            for (int i2 = 0; i2 < nedges; ++i2) {
                final int ea = edges[i2 * 4 + 0];
                final int eb = edges[i2 * 4 + 1];
                final int r = edges[i2 * 4 + 2];
                final int a = edges[i2 * 4 + 3];
                Boolean add = false;
                if (hole[0] == eb) {
                    nhole = this.PushFront(ea, hole, nhole);
                    nhreg = this.PushFront(r, hreg, nhreg);
                    nharea = this.PushFront(a, harea, nharea);
                    add = true;
                }
                else if (hole[nhole - 1] == ea) {
                    nhole = this.PushBack(eb, hole, nhole);
                    nhreg = this.PushBack(r, hreg, nhreg);
                    nharea = this.PushBack(a, harea, nharea);
                    add = true;
                }
                if (add) {
                    edges[i2 * 4 + 0] = edges[(nedges - 1) * 4 + 0];
                    edges[i2 * 4 + 1] = edges[(nedges - 1) * 4 + 1];
                    edges[i2 * 4 + 2] = edges[(nedges - 1) * 4 + 2];
                    edges[i2 * 4 + 3] = edges[(nedges - 1) * 4 + 3];
                    --nedges;
                    match = true;
                    --i2;
                }
            }
            if (!match) {
                break;
            }
        }
        final int[] tris = new int[nhole * 3];
        final int[] tverts = new int[nhole * 4];
        final int[] thole = new int[nhole];
        for (int i3 = 0; i3 < nhole; ++i3) {
            final int pi = hole[i3];
            tverts[i3 * 4 + 0] = this.Verts[pi * 3 + 0];
            tverts[i3 * 4 + 1] = this.Verts[pi * 3 + 1];
            tverts[i3 * 4 + 2] = this.Verts[pi * 3 + 2];
            tverts[i3 * 4 + 3] = 0;
            thole[i3] = i3;
        }
        int ntris = this.Triangulate(nhole, tverts, thole, tris);
        if (ntris < 0) {
            ntris = -ntris;
        }
        final int[] polys = new int[ntris * nvp];
        final int[] pregs = new int[ntris];
        final int[] pareas = new int[ntris];
        final int[] tmpPoly = new int[nvp];
        int npolys = 0;
        for (int i4 = 0; i4 < ntris * nvp; ++i4) {
            polys[i4] = 65535;
        }
        for (int j4 = 0; j4 < ntris; ++j4) {
            final int t = j4 * 3;
            if (tris[t + 0] != tris[t + 1] && tris[t + 0] != tris[t + 2] && tris[t + 1] != tris[t + 2]) {
                polys[npolys * nvp + 0] = hole[tris[t + 0]];
                polys[npolys * nvp + 1] = hole[tris[t + 1]];
                polys[npolys * nvp + 2] = hole[tris[t + 2]];
                pregs[npolys] = hreg[tris[t + 0]];
                pareas[npolys] = harea[tris[t + 0]];
                ++npolys;
            }
        }
        if (npolys == 0) {
            return true;
        }
        if (nvp > 3) {
            while (true) {
                int bestMergeVal = 0;
                int bestPa = 0;
                int bestPb = 0;
                int bestEa = 0;
                int bestEb = 0;
                for (int j5 = 0; j5 < npolys - 1; ++j5) {
                    final int pj = j5 * nvp;
                    for (int k2 = j5 + 1; k2 < npolys; ++k2) {
                        final int pk = k2 * nvp;
                        int ea2 = 0;
                        int eb2 = 0;
                        final IntVector3 returnVec = this.GetPolyMergeValue(polys, pj, pk, this.Verts, ea2, eb2, nvp);
                        final int v = returnVec.getX();
                        ea2 = returnVec.getY();
                        eb2 = returnVec.getZ();
                        if (v > bestMergeVal) {
                            bestMergeVal = v;
                            bestPa = j5;
                            bestPb = k2;
                            bestEa = ea2;
                            bestEb = eb2;
                        }
                    }
                }
                if (bestMergeVal <= 0) {
                    break;
                }
                final int pa = bestPa * nvp;
                final int pb = bestPb * nvp;
                this.MergePolys(polys, pa, pb, bestEa, bestEb, tmpPoly, nvp);
                System.arraycopy(polys, pb, polys, (npolys - 1) * nvp, nvp);
                pregs[bestPb] = pregs[npolys - 1];
                pareas[bestPb] = pareas[npolys - 1];
                --npolys;
            }
        }
        for (int i4 = 0; i4 < npolys && this.NPolys < maxTris; ++i4) {
            int j6;
            int p4;
            for (p4 = (j6 = this.NPolys * nvp * 2); j6 < p4 + nvp * 2; ++j6) {
                this.Polys[j6] = 65535;
            }
            for (j6 = 0; j6 < nvp; ++j6) {
                this.Polys[p4 + j6] = polys[i4 * nvp + j6];
            }
            this.Regs[this.NPolys] = pregs[i4];
            this.Areas[this.NPolys] = (short)pareas[i4];
            ++this.NPolys;
        }
        return true;
    }
    
    private int PushFront(final int v, final int[] arr, int an) {
        for (int i = ++an - 1; i > 0; --i) {
            arr[i] = arr[i - 1];
        }
        arr[0] = v;
        return an;
    }
    
    private int PushBack(final int v, final int[] arr, int an) {
        arr[an] = v;
        return ++an;
    }
    
    private int CountPolyVerts(final int[] polys, final int p, final int nvp) {
        for (int i = 0; i < nvp; ++i) {
            if (polys[p + i] == PolyMesh.MeshNullIdx) {
                return i;
            }
        }
        return nvp;
    }
    
    private Boolean BuildMeshAdjacency(final int vertsPerPoly) {
        final int maxEdgeCount = this.NPolys * vertsPerPoly;
        final int[] firstEdge = new int[this.NVerts];
        final int[] nextEdge = new int[maxEdgeCount];
        int edgeCount = 0;
        final Edge[] edges = new Edge[maxEdgeCount];
        for (int i = 0; i < maxEdgeCount; ++i) {
            edges[i] = new Edge();
        }
        for (int i = 0; i < this.NVerts; ++i) {
            firstEdge[i] = PolyMesh.MeshNullIdx;
        }
        for (int i = 0; i < this.NPolys; ++i) {
            for (int t = i * vertsPerPoly * 2, j = 0; j < vertsPerPoly && this.Polys[t + j] != PolyMesh.MeshNullIdx; ++j) {
                final int v0 = this.Polys[t + j];
                final int v2 = (j + 1 >= vertsPerPoly || this.Polys[t + j + 1] == PolyMesh.MeshNullIdx) ? this.Polys[t + 0] : this.Polys[t + j + 1];
                if (v0 < v2) {
                    final Edge edge = edges[edgeCount];
                    edge.Vert[0] = v0;
                    edge.Vert[1] = v2;
                    edge.Poly[0] = i;
                    edge.PolyEdge[0] = j;
                    edge.Poly[1] = i;
                    edge.PolyEdge[1] = 0;
                    nextEdge[edgeCount] = firstEdge[v0];
                    firstEdge[v0] = edgeCount;
                    ++edgeCount;
                }
            }
        }
        for (int i = 0; i < this.NPolys; ++i) {
            for (int t = i * vertsPerPoly * 2, j = 0; j < vertsPerPoly && this.Polys[t + j] != PolyMesh.MeshNullIdx; ++j) {
                final int v0 = this.Polys[t + j];
                final int v2 = (j + 1 >= vertsPerPoly || this.Polys[t + j + 1] == PolyMesh.MeshNullIdx) ? this.Polys[t + 0] : this.Polys[t + j + 1];
                if (v0 > v2) {
                    for (int e = firstEdge[v2]; e < PolyMesh.MeshNullIdx; e = nextEdge[e]) {
                        final Edge edge2 = edges[e];
                        if (edge2.Vert[1] == v0 && edge2.Poly[0] == edge2.Poly[1]) {
                            edge2.Poly[1] = i;
                            edge2.PolyEdge[1] = j;
                            break;
                        }
                    }
                }
            }
        }
        for (final Edge e2 : edges) {
            if (e2.Poly[0] != e2.Poly[1]) {
                final int p0 = e2.Poly[0] * vertsPerPoly * 2;
                final int p2 = e2.Poly[1] * vertsPerPoly * 2;
                this.Polys[p0 + vertsPerPoly + e2.PolyEdge[0]] = e2.Poly[1];
                this.Polys[p2 + vertsPerPoly + e2.PolyEdge[1]] = e2.Poly[0];
            }
        }
        return true;
    }
    
    static {
        PolyMesh.VertexBucketCount = 4096;
        PolyMesh.MeshNullIdx = 65535;
    }
}
