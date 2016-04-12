// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

import atavism.server.math.IntVector2;
import atavism.server.pathing.detour.DetourNumericReturn;

public class DetailPolyMesh
{
    public long[] Meshes;
    public float[] Verts;
    public short[] Tris;
    public int NMeshes;
    public int NVerts;
    public int NTris;
    public static int UnsetHeight;
    public static int Undef;
    public static int Hull;
    protected float[] DistPtTriV0;
    protected float[] DistPtTriV1;
    protected float[] DistPtTriV2;
    
    public DetailPolyMesh(final PolyMesh mesh, final CompactHeightfield chf, final float sampleDist, final float sampleMaxError) {
        this.DistPtTriV0 = new float[3];
        this.DistPtTriV1 = new float[3];
        this.DistPtTriV2 = new float[3];
        if (mesh.NVerts == 0 || mesh.NPolys == 0) {
            return;
        }
        final int nvp = mesh.Nvp;
        final float cs = mesh.Cs;
        final float ch = mesh.Ch;
        final float[] orig = mesh.BMin;
        final int borderSize = mesh.BorderSize;
        final IntArray edges = new IntArray(64);
        final IntArray tris = new IntArray(512);
        final IntArray stack = new IntArray(512);
        final IntArray samples = new IntArray(512);
        final float[] verts = new float[768];
        final HeightPatch hp = new HeightPatch();
        int nPolyVerts = 0;
        int maxhw = 0;
        int maxhh = 0;
        final int[] bounds = new int[mesh.NPolys * 4];
        final float[] poly = new float[nvp * 3];
        for (int i = 0; i < mesh.NPolys; ++i) {
            final int p = i * nvp * 2;
            final int xmin = i * 4 + 0;
            final int xmax = i * 4 + 1;
            final int ymin = i * 4 + 2;
            final int ymax = i * 4 + 3;
            bounds[xmin] = chf.Width;
            bounds[xmax] = 0;
            bounds[ymin] = chf.Height;
            bounds[ymax] = 0;
            for (int j = 0; j < nvp && mesh.Polys[p + j] != PolyMesh.MeshNullIdx; ++j) {
                final int v = mesh.Polys[p + j] * 3;
                bounds[xmin] = Math.min(bounds[xmin], mesh.Verts[v + 0]);
                bounds[xmax] = Math.max(bounds[xmax], mesh.Verts[v + 0]);
                bounds[ymin] = Math.min(bounds[ymin], mesh.Verts[v + 2]);
                bounds[ymax] = Math.max(bounds[ymax], mesh.Verts[v + 2]);
                ++nPolyVerts;
            }
            bounds[xmin] = Math.max(0, bounds[xmin] - 1);
            bounds[xmax] = Math.min(chf.Width, bounds[xmax] + 1);
            bounds[ymin] = Math.max(0, bounds[ymin] - 1);
            bounds[ymax] = Math.min(chf.Height, bounds[ymax] + 1);
            if (bounds[xmin] < bounds[xmax]) {
                if (bounds[ymin] < bounds[ymax]) {
                    maxhw = Math.max(maxhw, bounds[xmax] - bounds[xmin]);
                    maxhh = Math.max(maxhh, bounds[ymax] - bounds[ymin]);
                }
            }
        }
        hp.Data = new int[maxhw * maxhh];
        this.NMeshes = mesh.NPolys;
        this.Meshes = new long[this.NMeshes * 4];
        int vcap = nPolyVerts + nPolyVerts / 2;
        int tcap = vcap * 2;
        this.NVerts = 0;
        this.Verts = new float[vcap * 3];
        this.NTris = 0;
        this.Tris = new short[tcap * 4];
        for (int k = 0; k < mesh.NPolys; ++k) {
            final int p2 = k * nvp * 2;
            int npoly = 0;
            for (int j = 0; j < nvp && mesh.Polys[p2 + j] != PolyMesh.MeshNullIdx; ++j) {
                final int v = mesh.Polys[p2 + j] * 3;
                poly[j * 3 + 0] = mesh.Verts[v + 0] * cs;
                poly[j * 3 + 1] = mesh.Verts[v + 1] * ch;
                poly[j * 3 + 2] = mesh.Verts[v + 2] * cs;
                ++npoly;
            }
            hp.XMin = bounds[k * 4 + 0];
            hp.YMin = bounds[k * 4 + 2];
            hp.Width = bounds[k * 4 + 1] - bounds[k * 4 + 0];
            hp.Height = bounds[k * 4 + 3] - bounds[k * 4 + 2];
            final int[] tempPoly = new int[nvp];
            System.arraycopy(mesh.Polys, p2, tempPoly, 0, nvp);
            this.GetHeightData(chf, tempPoly, npoly, mesh.Verts, borderSize, hp, stack);
            final DetourNumericReturn polyDetail = this.BuildPolyDetail(poly, npoly, sampleDist, sampleMaxError, chf, hp, verts, tris, edges, samples);
            if (!polyDetail.boolValue) {
                return;
            }
            final int nverts = polyDetail.intValue;
            for (int l = 0; l < nverts; ++l) {
                final float[] array = verts;
                final int n = l * 3 + 0;
                array[n] += orig[0];
                final float[] array2 = verts;
                final int n2 = l * 3 + 1;
                array2[n2] += orig[1] + chf.Ch;
                final float[] array3 = verts;
                final int n3 = l * 3 + 2;
                array3[n3] += orig[2];
            }
            for (int l = 0; l < npoly; ++l) {
                final float[] array4 = poly;
                final int n4 = l * 3 + 0;
                array4[n4] += orig[0];
                final float[] array5 = poly;
                final int n5 = l * 3 + 1;
                array5[n5] += orig[1];
                final float[] array6 = poly;
                final int n6 = l * 3 + 2;
                array6[n6] += orig[2];
            }
            final int ntris = tris.Size / 4;
            this.Meshes[k * 4 + 0] = this.NVerts;
            this.Meshes[k * 4 + 1] = nverts;
            this.Meshes[k * 4 + 2] = this.NTris;
            this.Meshes[k * 4 + 3] = ntris;
            if (this.NVerts + nverts > vcap) {
                while (this.NVerts + nverts > vcap) {
                    vcap += 256;
                }
                final float[] newv = new float[vcap * 3];
                if (this.NVerts > 0) {
                    System.arraycopy(this.Verts, 0, newv, 0, 3 * this.NVerts);
                }
                this.Verts = newv;
            }
            for (int m = 0; m < nverts; ++m) {
                this.Verts[this.NVerts * 3 + 0] = verts[m * 3 + 0];
                this.Verts[this.NVerts * 3 + 1] = verts[m * 3 + 1];
                this.Verts[this.NVerts * 3 + 2] = verts[m * 3 + 2];
                ++this.NVerts;
            }
            if (this.NTris + ntris > tcap) {
                while (this.NTris + ntris > tcap) {
                    tcap += 256;
                }
                final short[] newt = new short[tcap * 4];
                if (this.NTris > 0) {
                    System.arraycopy(this.Tris, 0, newt, 0, 4 * this.NTris);
                }
                this.Tris = newt;
            }
            for (int m = 0; m < ntris; ++m) {
                final int t = m * 4;
                this.Tris[this.NTris * 4 + 0] = (short)tris.get(t + 0);
                this.Tris[this.NTris * 4 + 1] = (short)tris.get(t + 1);
                this.Tris[this.NTris * 4 + 2] = (short)tris.get(t + 2);
                this.Tris[this.NTris * 4 + 3] = this.GetTriFlags(verts, tris.get(t + 0) * 3, verts, tris.get(t + 1) * 3, verts, tris.get(t + 2) * 3, poly, npoly);
                ++this.NTris;
            }
        }
    }
    
    private void GetHeightData(final CompactHeightfield chf, final int[] p, final int npoly, final int[] verts, final int bs, final HeightPatch hp, final IntArray stack) {
        for (int i = 0; i < hp.Width * hp.Height; ++i) {
            hp.Data[i] = 0;
        }
        stack.Resize(0);
        final int[] offset = { 0, 0, -1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, 1, -1, 1, -1, 0 };
        for (int j = 0; j < npoly; ++j) {
            int cx = 0;
            int cz = 0;
            int ci = -1;
            int dmin = DetailPolyMesh.UnsetHeight;
            for (int k = 0; k < 9; ++k) {
                final int ax = verts[p[j] * 3 + 0] + offset[k * 2 + 0];
                final int ay = verts[p[j] * 3 + 1];
                final int az = verts[p[j] * 3 + 2] + offset[k * 2 + 1];
                if (ax >= hp.XMin && ax < hp.XMin + hp.Width && az >= hp.YMin) {
                    if (az < hp.YMin + hp.Height) {
                        final CompactCell c = chf.Cells[ax + bs + (az + bs) * chf.Width];
                        for (int l = (int)c.Index, ni = (int)(c.Index + c.Count); l < ni; ++l) {
                            final CompactSpan s = chf.Spans[l];
                            final int d = Math.abs(ay - s.Y);
                            if (d < dmin) {
                                cx = ax;
                                cz = az;
                                ci = l;
                                dmin = d;
                            }
                        }
                    }
                }
            }
            if (ci != -1) {
                stack.Push(cx);
                stack.Push(cz);
                stack.Push(ci);
            }
        }
        int pcx = 0;
        int pcz = 0;
        for (int m = 0; m < npoly; ++m) {
            pcx += verts[p[m] * 3 + 0];
            pcz += verts[p[m] * 3 + 2];
        }
        pcx /= npoly;
        pcz /= npoly;
        for (int i2 = 0; i2 < stack.Size; i2 += 3) {
            final int cx2 = stack.get(i2 + 0);
            final int cy = stack.get(i2 + 1);
            final int idx = cx2 - hp.XMin + (cy - hp.YMin) * hp.Width;
            hp.Data[idx] = 1;
        }
        while (stack.Size > 0) {
            final int ci2 = stack.Pop();
            final int cy2 = stack.Pop();
            final int cx3 = stack.Pop();
            if (Math.abs(cx3 - pcx) <= 1 && Math.abs(cy2 - pcz) <= 1) {
                stack.Resize(0);
                stack.Push(cx3);
                stack.Push(cy2);
                stack.Push(ci2);
                break;
            }
            final CompactSpan cs = chf.Spans[ci2];
            for (int dir = 0; dir < 4; ++dir) {
                if (cs.GetCon(dir) != CompactHeightfield.NotConnected) {
                    final int ax2 = cx3 + Helper.GetDirOffsetX(dir);
                    final int ay2 = cy2 + Helper.GetDirOffsetY(dir);
                    if (ax2 >= hp.XMin && ax2 < hp.XMin + hp.Width && ay2 >= hp.YMin) {
                        if (ay2 < hp.YMin + hp.Height) {
                            if (hp.Data[ax2 - hp.XMin + (ay2 - hp.YMin) * hp.Width] == 0) {
                                final int ai = (int)chf.Cells[ax2 + bs + (ay2 + bs) * chf.Width].Index + cs.GetCon(dir);
                                final int idx2 = ax2 - hp.XMin + (ay2 - hp.YMin) * hp.Width;
                                hp.Data[idx2] = 1;
                                stack.Push(ax2);
                                stack.Push(ay2);
                                stack.Push(ai);
                            }
                        }
                    }
                }
            }
        }
        for (int i2 = 0; i2 < hp.Data.length; ++i2) {
            hp.Data[i2] = DetailPolyMesh.UnsetHeight;
        }
        for (int i2 = 0; i2 < stack.Size; i2 += 3) {
            final int cx2 = stack.get(i2 + 0);
            final int cy = stack.get(i2 + 1);
            final int ci3 = stack.get(i2 + 2);
            final int idx3 = cx2 - hp.XMin + (cy - hp.YMin) * hp.Width;
            final CompactSpan cs2 = chf.Spans[ci3];
            hp.Data[idx3] = cs2.Y;
        }
        final int RetractSize = 256;
        int head = 0;
        while (head * 3 < stack.Size) {
            final int cx3 = stack.get(head * 3 + 0);
            final int cy3 = stack.get(head * 3 + 1);
            final int ci4 = stack.get(head * 3 + 2);
            if (++head >= RetractSize) {
                head = 0;
                if (stack.Size > RetractSize * 3) {
                    System.arraycopy(stack.Data, RetractSize * 3, stack.Data, 0, stack.Size - RetractSize * 3);
                }
                stack.Resize(stack.Size - RetractSize * 3);
            }
            final CompactSpan cs2 = chf.Spans[ci4];
            for (int dir2 = 0; dir2 < 4; ++dir2) {
                if (cs2.GetCon(dir2) != CompactHeightfield.NotConnected) {
                    final int ax3 = cx3 + Helper.GetDirOffsetX(dir2);
                    final int ay3 = cy3 + Helper.GetDirOffsetY(dir2);
                    if (ax3 >= hp.XMin && ax3 < hp.XMin + hp.Width && ay3 >= hp.YMin) {
                        if (ay3 < hp.YMin + hp.Height) {
                            if (hp.Data[ax3 - hp.XMin + (ay3 - hp.YMin) * hp.Width] == DetailPolyMesh.UnsetHeight) {
                                final int ai2 = (int)chf.Cells[ax3 + bs + (ay3 + bs) * chf.Width].Index + cs2.GetCon(dir2);
                                final CompactSpan aspan = chf.Spans[ai2];
                                final int idx4 = ax3 - hp.XMin + (ay3 - hp.YMin) * hp.Width;
                                hp.Data[idx4] = aspan.Y;
                                stack.Push(ax3);
                                stack.Push(ay3);
                                stack.Push(ai2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private DetourNumericReturn BuildPolyDetail(final float[] inArray, final int nin, final float sampleDist, final float sampleMaxError, final CompactHeightfield chf, final HeightPatch hp, final float[] verts, final IntArray tris, final IntArray edges, final IntArray samples) {
        final DetourNumericReturn numericReturn = new DetourNumericReturn();
        final int MaxVerts = 127;
        final int MaxTris = 255;
        final int MaxVertsPerEdge = 32;
        final float[] edge = new float[(MaxVertsPerEdge + 1) * 3];
        final int[] hull = new int[MaxVerts];
        int nhull = 0;
        for (int i = 0; i < nin; ++i) {
            System.arraycopy(inArray, i * 3, verts, i * 3, 3);
        }
        numericReturn.intValue = nin;
        final float cs = chf.Cs;
        final float ics = 1.0f / cs;
        if (sampleDist > 0.0f) {
            int j = 0;
            int k = nin - 1;
            while (j < nin) {
                int vj = k * 3;
                int vi = j * 3;
                Boolean swapped = false;
                if (Math.abs(inArray[vj + 0] - inArray[vi + 0]) < 1.0E-6f) {
                    if (inArray[vj + 2] > inArray[vi + 2]) {
                        final int temp = vj;
                        vj = vi;
                        vi = temp;
                        swapped = true;
                    }
                }
                else if (inArray[vj + 0] > inArray[vi + 0]) {
                    final int temp = vj;
                    vj = vi;
                    vi = temp;
                    swapped = true;
                }
                final float dx = inArray[vi + 0] - inArray[vj + 0];
                final float dy = inArray[vi + 1] - inArray[vj + 1];
                final float dz = inArray[vi + 2] - inArray[vj + 2];
                final float d = (float)Math.sqrt(dx * dx + dz * dz);
                int nn = 1 + (int)Math.floor(d / sampleDist);
                if (nn >= MaxVertsPerEdge) {
                    nn = MaxVertsPerEdge - 1;
                }
                if (numericReturn.intValue + nn >= MaxVerts) {
                    nn = MaxVerts - 1 - numericReturn.intValue;
                }
                for (int l = 0; l <= nn; ++l) {
                    final float u = l / nn;
                    final int pos = l * 3;
                    edge[pos + 0] = inArray[vj + 0] + dx * u;
                    edge[pos + 1] = inArray[vj + 1] + dy * u;
                    edge[pos + 2] = inArray[vj + 2] + dz * u;
                    edge[pos + 1] = this.GetHeight(edge[pos + 0], edge[pos + 1], edge[pos + 2], cs, ics, chf.Ch, hp) * chf.Ch;
                }
                final int[] idx = new int[MaxVertsPerEdge];
                idx[0] = 0;
                idx[1] = nn;
                int nidx = 2;
                int m = 0;
                while (m < nidx - 1) {
                    final int a = idx[m];
                    final int b = idx[m + 1];
                    final int va = a * 3;
                    final int vb = b * 3;
                    float maxd = 0.0f;
                    int maxi = -1;
                    for (int m2 = a + 1; m2 < b; ++m2) {
                        final float dev = this.DistancePtSeg(edge[m2 * 3 + 0], edge[m2 * 3 + 1], edge[m2 * 3 + 2], edge[va + 0], edge[va + 1], edge[va + 2], edge[vb + 0], edge[vb + 1], edge[vb + 2]);
                        if (dev > maxd) {
                            maxd = dev;
                            maxi = m2;
                        }
                    }
                    if (maxi != -1 && maxd > sampleMaxError * sampleMaxError) {
                        for (int m2 = nidx; m2 > m; --m2) {
                            idx[m2] = idx[m2 - 1];
                        }
                        idx[m + 1] = maxi;
                        ++nidx;
                    }
                    else {
                        ++m;
                    }
                }
                hull[nhull++] = k;
                if (swapped) {
                    for (m = nidx - 2; m > 0; --m) {
                        System.arraycopy(edge, idx[m] * 3, verts, numericReturn.intValue * 3, 3);
                        hull[nhull++] = numericReturn.intValue;
                        final DetourNumericReturn detourNumericReturn = numericReturn;
                        ++detourNumericReturn.intValue;
                    }
                }
                else {
                    for (m = 1; m < nidx - 1; ++m) {
                        System.arraycopy(edge, idx[m] * 3, verts, numericReturn.intValue * 3, 3);
                        hull[nhull++] = numericReturn.intValue;
                        final DetourNumericReturn detourNumericReturn2 = numericReturn;
                        ++detourNumericReturn2.intValue;
                    }
                }
                k = j++;
            }
        }
        edges.Resize(0);
        tris.Resize(0);
        this.DelaunayHull(numericReturn.intValue, verts, nhull, hull, tris, edges);
        if (tris.Size == 0) {
            for (int j = 2; j < numericReturn.intValue; ++j) {
                tris.Push(0);
                tris.Push(j - 1);
                tris.Push(j);
                tris.Push(0);
            }
            numericReturn.boolValue = true;
            return numericReturn;
        }
        if (sampleDist > 0.0f) {
            final float[] bmin = new float[3];
            final float[] bmax = new float[3];
            System.arraycopy(inArray, 0, bmin, 0, 3);
            System.arraycopy(inArray, 0, bmax, 0, 3);
            for (int i2 = 1; i2 < nin; ++i2) {
                bmin[0] = Math.min(bmin[0], inArray[i2 * 3 + 0]);
                bmin[1] = Math.min(bmin[1], inArray[i2 * 3 + 1]);
                bmin[2] = Math.min(bmin[2], inArray[i2 * 3 + 2]);
                bmax[0] = Math.max(bmax[0], inArray[i2 * 3 + 0]);
                bmax[1] = Math.max(bmax[1], inArray[i2 * 3 + 1]);
                bmax[2] = Math.max(bmax[2], inArray[i2 * 3 + 2]);
            }
            final int x0 = (int)Math.floor(bmin[0] / sampleDist);
            final int x2 = (int)Math.ceil(bmax[0] / sampleDist);
            final int z0 = (int)Math.floor(bmin[2] / sampleDist);
            final int z2 = (int)Math.ceil(bmax[2] / sampleDist);
            samples.Resize(0);
            for (int z3 = z0; z3 < z2; ++z3) {
                for (int x3 = x0; x3 < x2; ++x3) {
                    final float[] pt = { x3 * sampleDist, (bmax[1] + bmin[1]) * 0.5f, z3 * sampleDist };
                    if (this.DistToPoly(nin, inArray, pt[0], pt[1], pt[2]) <= -sampleDist / 2.0f) {
                        samples.Push(x3);
                        samples.Push(this.GetHeight(pt[0], pt[1], pt[2], cs, ics, chf.Ch, hp));
                        samples.Push(z3);
                        samples.Push(0);
                    }
                }
            }
            for (int nsamples = samples.Size / 4, iter = 0; iter < nsamples; ++iter) {
                if (numericReturn.intValue >= MaxVerts) {
                    break;
                }
                final float[] bestpt = { 0.0f, 0.0f, 0.0f };
                float bestd = 0.0f;
                int besti = -1;
                for (int i3 = 0; i3 < nsamples; ++i3) {
                    final int s = i3 * 4;
                    if (samples.get(s + 3) == 0) {
                        final float[] pt2 = { samples.get(s + 0) * sampleDist + this.GetJitterX(i3) * cs * 0.1f, samples.get(s + 1) * chf.Ch, samples.get(s + 2) * sampleDist + this.GetJitterY(i3) * cs * 0.1f };
                        final float d2 = this.DistToTriMesh(pt2[0], pt2[1], pt2[2], verts, numericReturn.intValue, tris, tris.Size / 4);
                        if (d2 >= 0.0f) {
                            if (d2 > bestd) {
                                bestd = d2;
                                besti = i3;
                                System.arraycopy(pt2, 0, bestpt, 0, 3);
                            }
                        }
                    }
                }
                if (bestd <= sampleMaxError) {
                    break;
                }
                if (besti == -1) {
                    break;
                }
                samples.set(besti * 4 + 3, 1);
                System.arraycopy(bestpt, 0, verts, numericReturn.intValue * 3, 3);
                final DetourNumericReturn detourNumericReturn3 = numericReturn;
                ++detourNumericReturn3.intValue;
                edges.Resize(0);
                tris.Resize(0);
                this.DelaunayHull(numericReturn.intValue, verts, nhull, hull, tris, edges);
            }
        }
        final int ntris = tris.Size / 4;
        if (ntris > MaxTris) {
            tris.Resize(MaxTris * 4);
        }
        numericReturn.boolValue = true;
        return numericReturn;
    }
    
    private void DelaunayHull(final int npts, final float[] pts, final int nhull, final int[] hull, final IntArray tris, final IntArray edges) {
        final int maxEdges = npts * 10;
        edges.Resize(maxEdges * 4);
        final IntVector2 nEdgesFaces = new IntVector2(0, 0);
        int i = 0;
        int j = nhull - 1;
        while (i < nhull) {
            this.AddEdge(edges, nEdgesFaces, maxEdges, hull[j], hull[i], DetailPolyMesh.Hull, DetailPolyMesh.Undef);
            j = i++;
        }
        for (int currentEdge = 0; currentEdge < nEdgesFaces.x; ++currentEdge) {
            if (edges.get(currentEdge * 4 + 2) == DetailPolyMesh.Undef) {
                this.CompleteFacet(pts, npts, edges, nEdgesFaces, maxEdges, currentEdge);
            }
            if (edges.get(currentEdge * 4 + 3) == DetailPolyMesh.Undef) {
                this.CompleteFacet(pts, npts, edges, nEdgesFaces, maxEdges, currentEdge);
            }
        }
        tris.Resize(nEdgesFaces.y * 4);
        for (int k = 0; k < nEdgesFaces.y * 4; ++k) {
            tris.set(k, -1);
        }
        for (int k = 0; k < nEdgesFaces.x; ++k) {
            final int e = k * 4;
            if (edges.get(e + 3) >= 0) {
                final int t = edges.get(e + 3) * 4;
                if (tris.get(t + 0) == -1) {
                    tris.set(t + 0, edges.get(e + 0));
                    tris.set(t + 1, edges.get(e + 1));
                }
                else if (tris.get(t + 0) == edges.get(e + 1)) {
                    tris.set(t + 2, edges.get(e + 0));
                }
                else if (tris.get(t + 1) == edges.get(e + 0)) {
                    tris.set(t + 2, edges.get(e + 1));
                }
            }
            if (edges.get(e + 2) >= 0) {
                final int t = edges.get(e + 2) * 4;
                if (tris.get(t + 0) == -1) {
                    tris.set(t + 0, edges.get(e + 1));
                    tris.set(t + 1, edges.get(e + 0));
                }
                else if (tris.get(t + 0) == edges.get(e + 0)) {
                    tris.set(t + 2, edges.get(e + 1));
                }
                else if (tris.get(t + 1) == edges.get(e + 1)) {
                    tris.set(t + 2, edges.get(e + 0));
                }
            }
        }
        for (int k = 0; k < tris.Size / 4; ++k) {
            final int t2 = k * 4;
            if (tris.get(t2 + 0) == -1 || tris.get(t2 + 1) == -1 || tris.get(t2 + 2) == -1) {
                tris.set(t2 + 0, tris.get(tris.Size - 4));
                tris.set(t2 + 1, tris.get(tris.Size - 3));
                tris.set(t2 + 2, tris.get(tris.Size - 2));
                tris.set(t2 + 3, tris.get(tris.Size - 1));
                tris.Resize(tris.Size - 4);
                --k;
            }
        }
    }
    
    private void CompleteFacet(final float[] pts, final int npts, final IntArray edges, final IntVector2 nEdgesFaces, final int maxEdges, int e) {
        final float EPS = 1.0E-5f;
        final int edge = e * 4;
        int s = 0;
        int t = 0;
        if (edges.get(edge + 2) == DetailPolyMesh.Undef) {
            s = edges.get(edge + 0);
            t = edges.get(edge + 1);
        }
        else {
            if (edges.get(edge + 3) != DetailPolyMesh.Undef) {
                return;
            }
            s = edges.get(edge + 1);
            t = edges.get(edge + 0);
        }
        int pt = npts;
        final float[] c = { 0.0f, 0.0f, 0.0f };
        float r = -1.0f;
        for (int u = 0; u < npts; ++u) {
            if (u != s) {
                if (u != t) {
                    if (this.VCross2(pts[s * 3 + 0], pts[s * 3 + 1], pts[s * 3 + 2], pts[t * 3 + 0], pts[t * 3 + 1], pts[t * 3 + 2], pts[u * 3 + 0], pts[u * 3 + 1], pts[u * 3 + 2]) > EPS) {
                        if (r < 0.0f) {
                            pt = u;
                            r = this.CircumCircle(pts[s * 3 + 0], pts[s * 3 + 1], pts[s * 3 + 2], pts[t * 3 + 0], pts[t * 3 + 1], pts[t * 3 + 2], pts[u * 3 + 0], pts[u * 3 + 1], pts[u * 3 + 2], c, r).floatValue;
                        }
                        else {
                            final float d = this.VDist2(c[0], c[1], c[2], pts[u * 3 + 0], pts[u * 3 + 1], pts[u * 3 + 2]);
                            final float tol = 0.001f;
                            if (d <= r * (1.0f + tol)) {
                                if (d < r * (1.0f - tol)) {
                                    pt = u;
                                    r = this.CircumCircle(pts[s * 3 + 0], pts[s * 3 + 1], pts[s * 3 + 2], pts[t * 3 + 0], pts[t * 3 + 1], pts[t * 3 + 2], pts[u * 3 + 0], pts[u * 3 + 1], pts[u * 3 + 2], c, r).floatValue;
                                }
                                else if (!this.OverlapEdges(pts, edges, nEdgesFaces.x, s, u)) {
                                    if (!this.OverlapEdges(pts, edges, nEdgesFaces.x, t, u)) {
                                        pt = u;
                                        r = this.CircumCircle(pts[s * 3 + 0], pts[s * 3 + 1], pts[s * 3 + 2], pts[t * 3 + 0], pts[t * 3 + 1], pts[t * 3 + 2], pts[u * 3 + 0], pts[u * 3 + 1], pts[u * 3 + 2], c, r).floatValue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (pt < npts) {
            this.UpdateLeftFace(edges, e * 4, s, t, nEdgesFaces.y);
            e = this.FindEdge(edges, nEdgesFaces.x, pt, s);
            if (e == DetailPolyMesh.Undef) {
                this.AddEdge(edges, nEdgesFaces, maxEdges, pt, s, nEdgesFaces.y, DetailPolyMesh.Undef);
            }
            else {
                this.UpdateLeftFace(edges, e * 4, pt, s, nEdgesFaces.y);
            }
            e = this.FindEdge(edges, nEdgesFaces.x, t, pt);
            if (e == DetailPolyMesh.Undef) {
                this.AddEdge(edges, nEdgesFaces, maxEdges, t, pt, nEdgesFaces.y, DetailPolyMesh.Undef);
            }
            else {
                this.UpdateLeftFace(edges, e * 4, t, pt, nEdgesFaces.y);
            }
            ++nEdgesFaces.y;
        }
        else {
            this.UpdateLeftFace(edges, e * 4, s, t, DetailPolyMesh.Hull);
        }
    }
    
    private Boolean OverlapEdges(final float[] pts, final IntArray edges, final int nedges, final int s1, final int t1) {
        for (int i = 0; i < nedges; ++i) {
            final int s2 = i * 4 + 0;
            final int t2 = i * 4 + 1;
            if (edges.get(s2) != s1 && edges.get(s2) != t1 && edges.get(t2) != s1) {
                if (edges.get(t2) != t1) {
                    if (this.OverlapSegSeg2d(pts[edges.get(s2) * 3 + 0], pts[edges.get(s2) * 3 + 1], pts[edges.get(s2) * 3 + 2], pts[edges.get(t2) * 3 + 0], pts[edges.get(t2) * 3 + 1], pts[edges.get(t2) * 3 + 2], pts[s1 * 3 + 0], pts[s1 * 3 + 1], pts[s1 * 3 + 1], pts[t1 * 3 + 0], pts[t1 * 3 + 1], pts[t1 * 3 + 1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private Boolean OverlapSegSeg2d(final float ax, final float ay, final float az, final float bx, final float by, final float bz, final float cx, final float cy, final float cz, final float dx, final float dy, final float dz) {
        final float a1 = this.VCross2(ax, ay, az, bx, by, bz, dx, dy, dz);
        final float a2 = this.VCross2(ax, ay, az, bx, by, bz, cx, cy, cz);
        if (a1 * a2 < 0.0f) {
            final float a3 = this.VCross2(cx, cy, cz, dx, dy, dz, ax, ay, az);
            final float a4 = a3 + a2 - a1;
            if (a3 * a4 < 0.0f) {
                return true;
            }
        }
        return false;
    }
    
    private DetourNumericReturn CircumCircle(final float p1x, final float p1y, final float p1z, final float p2x, final float p2y, final float p2z, final float p3x, final float p3y, final float p3z, final float[] c, final float r) {
        final DetourNumericReturn numericReturn = new DetourNumericReturn();
        final float EPS = 1.0E-6f;
        final float cp = this.VCross2(p1x, p1y, p1z, p2x, p2y, p2z, p3x, p3y, p3z);
        if (Math.abs(cp) > EPS) {
            final float p1Sq = this.Dot(p1x, p1y, p1z, p1x, p1y, p1z);
            final float p2Sq = this.Dot(p2x, p2y, p2z, p2x, p2y, p2z);
            final float p3Sq = this.Dot(p3x, p3y, p3z, p3x, p3y, p3z);
            c[0] = (p1Sq * (p2z - p3z) + p2Sq * (p3z - p1z) + p3Sq * (p1z - p2z)) / (2.0f * cp);
            c[2] = (p1Sq * (p3x - p2x) + p2Sq * (p1x - p3x) + p3Sq * (p2x - p1x)) / (2.0f * cp);
            numericReturn.floatValue = this.VDist2(c[0], c[1], c[2], p1x, p1y, p1z);
            numericReturn.boolValue = true;
            return numericReturn;
        }
        c[0] = p1x;
        c[2] = p1z;
        numericReturn.floatValue = 0.0f;
        numericReturn.boolValue = false;
        return numericReturn;
    }
    
    private float VDist2(final float px, final float py, final float pz, final float qx, final float qy, final float qz) {
        return (float)Math.sqrt(this.VDistSq2(px, py, pz, qx, qy, qz));
    }
    
    private float VDistSq2(final float px, final float py, final float pz, final float qx, final float qy, final float qz) {
        final float dx = qx - px;
        final float dy = qz - pz;
        return dx * dx + dy * dy;
    }
    
    private float VCross2(final float p1X, final float p1Y, final float p1Z, final float p2X, final float p2Y, final float p2Z, final float p3X, final float p3Y, final float p3Z) {
        final float u1 = p2X - p1X;
        final float v1 = p2Z - p1Z;
        final float u2 = p3X - p1X;
        final float v2 = p3Z - p1Z;
        return u1 * v2 - v1 * u2;
    }
    
    private void UpdateLeftFace(final IntArray edges, final int e, final int s, final int t, final int f) {
        if (edges.get(e + 0) == s && edges.get(e + 1) == t && edges.get(e + 2) == DetailPolyMesh.Undef) {
            edges.set(e + 2, f);
        }
        else if (edges.get(e + 1) == s && edges.get(e + 0) == t && edges.get(e + 3) == DetailPolyMesh.Undef) {
            edges.set(e + 3, f);
        }
    }
    
    private int AddEdge(final IntArray edges, final IntVector2 nEdgesFaces, final int maxEdges, final int s, final int t, final int l, final int r) {
        if (nEdgesFaces.x >= maxEdges) {
            return DetailPolyMesh.Undef;
        }
        final int e = this.FindEdge(edges, nEdgesFaces.x, s, t);
        if (e == DetailPolyMesh.Undef) {
            final int edge = nEdgesFaces.x * 4;
            edges.set(edge + 0, s);
            edges.set(edge + 1, t);
            edges.set(edge + 2, l);
            edges.set(edge + 3, r);
            return nEdgesFaces.x++;
        }
        return DetailPolyMesh.Undef;
    }
    
    private int FindEdge(final IntArray edges, final int nedges, final int s, final int t) {
        for (int i = 0; i < nedges; ++i) {
            final int e = i * 4;
            if ((edges.get(e + 0) == s && edges.get(e + 1) == t) || (edges.get(e + 0) == t && edges.get(e + 1) == s)) {
                return i;
            }
        }
        return DetailPolyMesh.Undef;
    }
    
    private float DistancePtSeg(final float ptx, final float pty, final float ptz, final float px, final float py, final float pz, final float qx, final float qy, final float qz) {
        final float pqx = qx - px;
        final float pqy = qy - py;
        final float pqz = qz - pz;
        float dx = ptx - px;
        float dy = pty - py;
        float dz = ptz - pz;
        final float d = pqx * pqx + pqy * pqy + pqz * pqz;
        float t = pqx * dx + pqy * dy + pqz * dz;
        if (d > 0.0f) {
            t /= d;
        }
        if (t < 0.0f) {
            t = 0.0f;
        }
        else if (t > 1.0f) {
            t = 1.0f;
        }
        dx = px + t * pqx - ptx;
        dy = py + t * pqy - pty;
        dz = pz + t * pqz - ptz;
        return dx * dx + dy * dy + dz * dz;
    }
    
    private float DistToPoly(final int nvert, final float[] verts, final float px, final float py, final float pz) {
        float dmin = Float.MAX_VALUE;
        int i = 0;
        int j = 0;
        Boolean c = false;
        i = 0;
        j = nvert - 1;
        while (i < nvert) {
            final int vi = i * 3;
            final int vj = j * 3;
            if (verts[vi + 2] > pz != verts[vj + 2] > pz && px < (verts[vj + 0] - verts[vi + 0]) * (pz - verts[vi + 2]) / (verts[vj + 2] - verts[vi + 2]) + verts[vi + 0]) {
                c = !c;
            }
            final float[] pxpypz = { px, py, pz };
            dmin = Math.min(dmin, this.DistancePtSeg2d(pxpypz, 0, verts, vj, verts, vi));
            j = i++;
        }
        return c ? (-dmin) : dmin;
    }
    
    private int GetHeight(final float fx, final float fy, final float fz, final float cs, final float ics, final float ch, final HeightPatch hp) {
        int ix = (int)Math.floor(fx * ics + 0.01f);
        int iz = (int)Math.floor(fz * ics + 0.01f);
        ix = Math.max(0, Math.min(hp.Width, ix - hp.XMin));
        iz = Math.max(0, Math.min(hp.Height, iz - hp.YMin));
        int h = hp.Data[ix + iz * hp.Width];
        if (h == DetailPolyMesh.UnsetHeight) {
            final int[] offset = { -1, 0, -1, -1, 0, -1, 1, -1, 1, 0, 1, 1, 0, 1, -1, 1 };
            float dmin = Float.MAX_VALUE;
            for (int i = 0; i < 8; ++i) {
                final int nx = ix + offset[i * 2 + 0];
                final int nz = iz + offset[i * 2 + 1];
                if (nx >= 0 && nz >= 0 && nx < hp.Width) {
                    if (nz < hp.Height) {
                        final int nh = hp.Data[nx + nz * hp.Width];
                        if (nh != DetailPolyMesh.UnsetHeight) {
                            final float d = Math.abs(nh * ch - fy);
                            if (d < dmin) {
                                h = nh;
                                dmin = d;
                            }
                        }
                    }
                }
            }
        }
        return h;
    }
    
    private float GetJitterX(final int i) {
        return (i * -1918454973 & 0xFFFF) / 65535.0f * 2.0f - 1.0f;
    }
    
    private float GetJitterY(final int i) {
        return (i * -669632447 & 0xFFFF) / 65535.0f * 2.0f - 1.0f;
    }
    
    private float DistToTriMesh(final float px, final float py, final float pz, final float[] verts, final int nverts, final IntArray tris, final int ntris) {
        float dmin = Float.MAX_VALUE;
        for (int i = 0; i < ntris; ++i) {
            final int va = tris.get(i * 4 + 0) * 3;
            final int vb = tris.get(i * 4 + 1) * 3;
            final int vc = tris.get(i * 4 + 2) * 3;
            final float d = this.DistPtTri(px, py, pz, verts[va + 0], verts[va + 1], verts[va + 2], verts[vb + 0], verts[vb + 1], verts[vb + 2], verts[vc + 0], verts[vc + 1], verts[vc + 2]);
            if (d < dmin) {
                dmin = d;
            }
        }
        if (dmin == Float.MAX_VALUE) {
            return -1.0f;
        }
        return dmin;
    }
    
    private float DistPtTri(final float px, final float py, final float pz, final float ax, final float ay, final float az, final float bx, final float by, final float bz, final float cx, final float cy, final float cz) {
        this.Sub(this.DistPtTriV0, cx, cy, cz, ax, ay, az);
        this.Sub(this.DistPtTriV1, bx, by, bz, ax, ay, az);
        this.Sub(this.DistPtTriV2, px, py, pz, ax, ay, az);
        final float dot00 = this.Dot(this.DistPtTriV0, this.DistPtTriV0);
        final float dot2 = this.Dot(this.DistPtTriV0, this.DistPtTriV1);
        final float dot3 = this.Dot(this.DistPtTriV0, this.DistPtTriV2);
        final float dot4 = this.Dot(this.DistPtTriV1, this.DistPtTriV1);
        final float dot5 = this.Dot(this.DistPtTriV1, this.DistPtTriV2);
        final float invDenom = 1.0f / (dot00 * dot4 - dot2 * dot2);
        final float u = (dot4 * dot3 - dot2 * dot5) * invDenom;
        final float v = (dot00 * dot5 - dot2 * dot3) * invDenom;
        final float EPS = 1.0E-4f;
        if (u >= -EPS && v >= -EPS && u + v <= 1.0f + EPS) {
            final float y = ay + this.DistPtTriV0[1] * u + this.DistPtTriV1[1] * v;
            return Math.abs(y - py);
        }
        return Float.MAX_VALUE;
    }
    
    private float Dot(final float[] a, final float[] b) {
        return a[0] * b[0] + a[2] * b[2];
    }
    
    private float Dot(final float ax, final float ay, final float az, final float bx, final float by, final float bz) {
        return ax * bx + az * bz;
    }
    
    private void Sub(final float[] dest, final float v1x, final float v1y, final float v1z, final float v2x, final float v2y, final float v2z) {
        dest[0] = v1x - v2x;
        dest[1] = v1y - v2y;
        dest[2] = v1z - v2z;
    }
    
    private short GetTriFlags(final float[] vertsa, final int va, final float[] vertsb, final int vb, final float[] vertsc, final int vc, final float[] vpoly, final int npoly) {
        short flags = 0;
        flags |= (short)(this.GetEdgeFlags(vertsa, va, vertsb, vb, vpoly, npoly) << 0);
        flags |= (short)(this.GetEdgeFlags(vertsb, vb, vertsc, vc, vpoly, npoly) << 2);
        flags |= (short)(this.GetEdgeFlags(vertsc, vc, vertsa, va, vpoly, npoly) << 4);
        return flags;
    }
    
    private short GetEdgeFlags(final float[] vertsa, final int va, final float[] vertsb, final int vb, final float[] vpoly, final int npoly) {
        final float thrSqr = 1.0000001E-6f;
        int i = 0;
        int j = npoly - 1;
        while (i < npoly) {
            if (this.DistancePtSeg2d(vertsa, va, vpoly, j * 3, vpoly, i * 3) < thrSqr && this.DistancePtSeg2d(vertsb, vb, vpoly, j * 3, vpoly, i * 3) < thrSqr) {
                return 1;
            }
            j = i++;
        }
        return 0;
    }
    
    private float DistancePtSeg2d(final float[] vpt, final int pt, final float[] vp, final int p, final float[] vq, final int q) {
        final float pqx = vq[q + 0] - vp[p + 0];
        final float pqz = vq[q + 2] - vp[p + 2];
        float dx = vpt[pt + 0] - vp[p + 0];
        float dz = vpt[pt + 2] - vp[p + 2];
        final float d = pqx * pqx + pqz * pqz;
        float t = pqx * dx + pqz * dz;
        if (d > 0.0f) {
            t /= d;
        }
        if (t < 0.0f) {
            t = 0.0f;
        }
        else if (t > 1.0f) {
            t = 1.0f;
        }
        dx = vp[p + 0] + t * pqx - vpt[pt + 0];
        dz = vp[p + 2] + t * pqz - vpt[pt + 2];
        return dx * dx + dz * dz;
    }
    
    static {
        DetailPolyMesh.UnsetHeight = 65535;
        DetailPolyMesh.Undef = -1;
        DetailPolyMesh.Hull = -2;
    }
}
