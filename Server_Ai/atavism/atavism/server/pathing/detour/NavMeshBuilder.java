// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;

import java.util.Comparator;
import java.util.Arrays;
import atavism.server.pathing.recast.Helper;
import atavism.server.pathing.recast.PolyMesh;
/**
 * 导航网格生成器
 * @author doter
 *
 */
public class NavMeshBuilder
{
    public MeshHeader Header;
    public float[] NavVerts;
    public Poly[] NavPolys;
    public Link[] NavLinks;
    public PolyDetail[] NavDMeshes;
    public float[] NavDVerts;
    public short[] NavDTris;
    public BVNode[] NavBvTree;
    public OffMeshConnection[] OffMeshCons;
    public static int MaxAreas;
    public static int VertsPerPoly;
    public static short PolyTypeGround;//多种类型的地面
    public static short PolyTypeOffMeshConnection;//多种类型的关网状连接
    public static int ExtLink;
    public static short OffMeshConBiDir;
    
    public NavMeshBuilder() {
    }
    
    public NavMeshBuilder(final NavMeshCreateParams param) {
        try {
            if (param.Nvp > NavMeshBuilder.VertsPerPoly) {
                throw new Exception("Too many Verts per Poly for NavMeshBuilder");
            }
            if (param.VertCount >= 65535) {
                throw new Exception("Too many total verticies for NavMeshBuilder");
            }
            if (param.VertCount == 0 || param.Verts == null) {
                throw new Exception("No vertices, cannot generate nav mesh");
            }
            if (param.PolyCount == 0 || param.Polys == null) {
                throw new Exception("No Polygons, cannot generate nav mesh");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final int nvp = param.Nvp;
        short[] offMeshConClass = new short[0];
        int storedOffMeshConCount = 0;
        int offMeshConLinkCount = 0;
        if (param.OffMeshConCount > 0) {
            offMeshConClass = new short[param.OffMeshConCount * 2];
            float hmin = Float.MAX_VALUE;
            float hmax = Float.MIN_VALUE;
            if (param.DetailVerts != null && param.DetailVertsCount > 0) {
                for (int i = 0; i < param.DetailVertsCount; ++i) {
                    final int h = i * 3 + 1;
                    hmin = Math.min(hmin, param.DetailVerts[h]);
                    hmax = Math.max(hmax, param.DetailVerts[h]);
                }
            }
            else {
                for (int i = 0; i < param.VertCount; ++i) {
                    final int iv = i * 3;
                    final float h2 = param.BMin[1] + param.Verts[iv + 1] * param.Ch;
                    hmin = Math.min(hmin, h2);
                    hmax = Math.max(hmax, h2);
                }
            }
            hmin -= param.WalkableClimb;
            hmax += param.WalkableClimb;
            final float[] bmin = new float[3];
            final float[] bmax = new float[3];
            System.arraycopy(param.BMin, 0, bmin, 0, 3);
            System.arraycopy(param.BMax, 0, bmax, 0, 3);
            bmin[1] = hmin;
            bmax[1] = hmax;
            for (int j = 0; j < param.OffMeshConCount; ++j) {
                final int p0 = (j * 2 + 0) * 3;
                final int p2 = (j * 2 + 1) * 3;
                offMeshConClass[j * 2 + 0] = this.ClassifyOffMeshPoint(param.OffMeshConVerts[p0 + 0], param.OffMeshConVerts[p0 + 1], param.OffMeshConVerts[p0 + 2], bmin, bmax);
                offMeshConClass[j * 2 + 1] = this.ClassifyOffMeshPoint(param.OffMeshConVerts[p2 + 0], param.OffMeshConVerts[p2 + 1], param.OffMeshConVerts[p2 + 2], bmin, bmax);
                if (offMeshConClass[j * 2 + 0] == 255 && (param.OffMeshConVerts[p0 + 1] < bmin[1] || param.OffMeshConVerts[p0 + 1] > bmax[1])) {
                    offMeshConClass[j * 2 + 0] = 0;
                }
                if (offMeshConClass[j * 2 + 0] == 255) {
                    ++offMeshConLinkCount;
                }
                if (offMeshConClass[j * 2 + 1] == 255) {
                    ++offMeshConLinkCount;
                }
                if (offMeshConClass[j * 2 + 0] == 255) {
                    ++storedOffMeshConCount;
                }
            }
        }
        final int totPolyCount = param.PolyCount + storedOffMeshConCount;
        final int totVertCount = param.VertCount + storedOffMeshConCount * 2;
        int edgeCount = 0;
        int portalCount = 0;
        for (int j = 0; j < param.PolyCount; ++j) {
            for (int p3 = j * 2 * nvp, k = 0; k < nvp && param.Polys[p3 + k] != PolyMesh.MeshNullIdx; ++k) {
                ++edgeCount;
                if ((param.Polys[p3 + nvp + k] & 0x8000) != 0x0) {
                    final int dir = param.Polys[p3 + nvp + k] & 0xF;
                    if (dir != 15) {
                        ++portalCount;
                    }
                }
            }
        }
        final int maxLinkCount = edgeCount + portalCount * 2 + offMeshConLinkCount * 2;
        int uniqueDetailVertCount = 0;
        int detailTryCount = 0;
        if (param.DetailMeshes != null) {
            detailTryCount = param.DetailTriCount;
            for (int l = 0; l < param.PolyCount; ++l) {
                final int p4 = l * nvp * 2;
                int ndv = (int)param.DetailMeshes[l * 4 + 1];
                int nv = 0;
                for (int m = 0; m < nvp && param.Polys[p4 + m] != PolyMesh.MeshNullIdx; ++m) {
                    ++nv;
                }
                ndv -= nv;
                uniqueDetailVertCount += ndv;
            }
        }
        else {
            uniqueDetailVertCount = 0;
            detailTryCount = 0;
            for (int l = 0; l < param.PolyCount; ++l) {
                final int p4 = l * nvp * 2;
                int nv2 = 0;
                for (int j2 = 0; j2 < nvp && param.Polys[p4 + j2] != PolyMesh.MeshNullIdx; ++j2) {
                    ++nv2;
                }
                detailTryCount += nv2 - 2;
            }
        }
        this.Header = new MeshHeader(Helper.NavMeshMagic, Helper.NavMeshVersion, param.TileX, param.TileY, param.TileLayer, param.UserId, totPolyCount, totVertCount, maxLinkCount, param.PolyCount, uniqueDetailVertCount, detailTryCount, 1.0f / param.Cs, param.PolyCount, param.WalkableHeight, param.WalkableRadius, param.WalkableClimb, storedOffMeshConCount, param.BuildBvTree ? (param.PolyCount * 2) : 0, new float[3], new float[3]);
        System.arraycopy(param.BMin, 0, this.Header.BMin, 0, 3);
        System.arraycopy(param.BMax, 0, this.Header.BMax, 0, 3);
        this.NavVerts = new float[totVertCount * 3];
        this.NavPolys = new Poly[totPolyCount];
        for (int l = 0; l < totPolyCount; ++l) {
            this.NavPolys[l] = new Poly();
        }
        this.NavLinks = new Link[maxLinkCount];
        for (int l = 0; l < maxLinkCount; ++l) {
            this.NavLinks[l] = new Link();
        }
        this.NavDMeshes = new PolyDetail[param.PolyCount];
        for (int l = 0; l < param.PolyCount; ++l) {
            this.NavDMeshes[l] = new PolyDetail();
        }
        this.NavDVerts = new float[3 * uniqueDetailVertCount];
        this.NavDTris = new short[4 * detailTryCount];
        this.NavBvTree = (param.BuildBvTree ? new BVNode[param.PolyCount * 2] : new BVNode[0]);
        if (param.BuildBvTree) {
            for (int l = 0; l < param.PolyCount * 2; ++l) {
                this.NavBvTree[l] = new BVNode();
            }
        }
        this.OffMeshCons = new OffMeshConnection[storedOffMeshConCount];
        for (int l = 0; l < storedOffMeshConCount; ++l) {
            this.OffMeshCons[l] = new OffMeshConnection();
        }
        final int offMeshVertsBase = param.VertCount;
        final int offMeshPolyBase = param.PolyCount;
        for (int i2 = 0; i2 < param.VertCount; ++i2) {
            final int iv2 = i2 * 3;
            final int v = i2 * 3;
            this.NavVerts[v + 0] = param.BMin[0] + param.Verts[iv2 + 0] * param.Cs;
            this.NavVerts[v + 1] = param.BMin[1] + param.Verts[iv2 + 1] * param.Ch;
            this.NavVerts[v + 2] = param.BMin[2] + param.Verts[iv2 + 2] * param.Cs;
        }
        int n = 0;
        for (int i3 = 0; i3 < param.OffMeshConCount; ++i3) {
            if (offMeshConClass[i3 * 2 + 0] == 255) {
                final int linkv = i3 * 2 * 3;
                final int v2 = (offMeshVertsBase + n * 2) * 3;
                System.arraycopy(param.OffMeshConVerts, linkv, this.NavVerts, v2, 3);
                System.arraycopy(param.OffMeshConVerts, linkv + 3, this.NavVerts, v2 + 3, 3);
                ++n;
            }
        }
        int src = 0;
        for (int i4 = 0; i4 < param.PolyCount; ++i4) {
            final Poly p5 = this.NavPolys[i4];
            p5.VertCount = 0;
            p5.Flags = param.PolyFlags[i4];
            p5.setArea(param.PolyAreas[i4]);
            p5.setType(NavMeshBuilder.PolyTypeGround);
            for (int j3 = 0; j3 < nvp && param.Polys[src + j3] != PolyMesh.MeshNullIdx; ++j3) {
                p5.Verts[j3] = param.Polys[src + j3];
                if ((param.Polys[src + nvp + j3] & 0x8000) != 0x0) {
                    final int dir2 = param.Polys[src + nvp + j3] & 0xF;
                    if (dir2 == 15) {
                        p5.Neis[j3] = 0;
                    }
                    else if (dir2 == 0) {
                        p5.Neis[j3] = (NavMeshBuilder.ExtLink | 0x4);
                    }
                    else if (dir2 == 1) {
                        p5.Neis[j3] = (NavMeshBuilder.ExtLink | 0x2);
                    }
                    else if (dir2 == 2) {
                        p5.Neis[j3] = (NavMeshBuilder.ExtLink | 0x0);
                    }
                    else if (dir2 == 3) {
                        p5.Neis[j3] = (NavMeshBuilder.ExtLink | 0x6);
                    }
                }
                else {
                    p5.Neis[j3] = param.Polys[src + nvp + j3] + 1;
                }
                final Poly poly = p5;
                ++poly.VertCount;
            }
            src += nvp * 2;
        }
        n = 0;
        for (int i4 = 0; i4 < param.OffMeshConCount; ++i4) {
            if (offMeshConClass[i4 * 2 + 0] == 255) {
                final Poly p5 = this.NavPolys[offMeshPolyBase + n];
                p5.VertCount = 2;
                p5.Verts[0] = offMeshVertsBase + n * 2 + 0;
                p5.Verts[1] = offMeshVertsBase + n * 2 + 1;
                p5.Flags = param.OffMeshConFlags[i4];
                p5.setArea((short)param.OffMeshConAreas[i4]);
                p5.setType(NavMeshBuilder.PolyTypeOffMeshConnection);
                ++n;
            }
        }
        if (param.DetailMeshes != null) {
            int vbase = 0;
            for (int i5 = 0; i5 < param.PolyCount; ++i5) {
                final PolyDetail dtl = this.NavDMeshes[i5];
                final int vb = (int)param.DetailMeshes[i5 * 4 + 0];
                final int ndv2 = (int)param.DetailMeshes[i5 * 4 + 1];
                final int nv3 = this.NavPolys[i5].VertCount;
                dtl.VertBase = vbase;
                dtl.VertCount = (short)(ndv2 - nv3);
                dtl.TriBase = param.DetailMeshes[i5 * 4 + 2];
                dtl.TriCount = (short)param.DetailMeshes[i5 * 4 + 3];
                if (ndv2 - nv3 > 0) {
                    System.arraycopy(param.DetailVerts, (vb + nv3) * 3, this.NavDVerts, vbase * 3, (ndv2 - nv3) * 3);
                    vbase += (short)(ndv2 - nv3);
                }
            }
            System.arraycopy(param.DetailTris, 0, this.NavDTris, 0, param.DetailTriCount * 4);
        }
        else {
            int tbase = 0;
            for (int i5 = 0; i5 < param.PolyCount; ++i5) {
                final PolyDetail dtl = this.NavDMeshes[i5];
                final int nv4 = this.NavPolys[i5].VertCount;
                dtl.VertBase = 0L;
                dtl.VertCount = 0;
                dtl.TriBase = tbase;
                dtl.TriCount = (short)(nv4 - 2);
                for (int j4 = 2; j4 < nv4; ++j4) {
                    final int t = tbase * 4;
                    this.NavDTris[t + 0] = 0;
                    this.NavDTris[t + 1] = (short)(j4 - 1);
                    this.NavDTris[t + 2] = (short)j4;
                    this.NavDTris[t + 3] = 4;
                    if (j4 == 2) {
                        final short[] navDTris = this.NavDTris;
                        final int n2 = t + 3;
                        navDTris[n2] |= 0x1;
                    }
                    if (j4 == nv4 - 1) {
                        final short[] navDTris2 = this.NavDTris;
                        final int n3 = t + 3;
                        navDTris2[n3] |= 0x10;
                    }
                    ++tbase;
                }
            }
        }
        if (param.BuildBvTree) {
            this.CreateBVTree(param.Verts, param.VertCount, param.Polys, param.PolyCount, nvp, param.Cs, param.Ch, param.PolyCount * 2);
        }
        n = 0;
        for (int i4 = 0; i4 < param.OffMeshConCount; ++i4) {
            if (offMeshConClass[i4 * 2 + 0] == 255) {
                final OffMeshConnection con = this.OffMeshCons[n];
                con.Poly = offMeshPolyBase + n;
                final int endPts = i4 * 2 * 3;
                System.arraycopy(param.OffMeshConVerts, endPts, con.Pos, 0, 3);
                System.arraycopy(param.OffMeshConVerts, endPts + 3, con.Pos, 3, 3);
                con.Rad = param.OffMeshConRad[i4];
                con.Flags = (short)((param.OffMeshConDir[i4] > 0) ? NavMeshBuilder.OffMeshConBiDir : 0);
                con.Side = offMeshConClass[i4 * 2 + 1];
                if (param.OffMeshConUserId != null) {
                    con.UserId = param.OffMeshConUserId[i4];
                }
                ++n;
            }
        }
    }
    
    private int CreateBVTree(final int[] verts, final int nverts, final int[] polys, final int npolys, final int nvp, final float cs, final float ch, final int nnodes) {
        final BVNode[] items = new BVNode[npolys];
        for (int i = 0; i < npolys; ++i) {
            items[i] = new BVNode();
        }
        for (int i = 0; i < npolys; ++i) {
            final BVNode it = items[i];
            it.I = i;
            final int[] p = new int[polys.length - i * nvp * 2];
            System.arraycopy(polys, i * nvp * 2, p, 0, p.length);
            it.BMin[0] = (it.BMax[0] = verts[p[0] * 3 + 0]);
            it.BMin[1] = (it.BMax[1] = verts[p[0] * 3 + 1]);
            it.BMin[2] = (it.BMax[2] = verts[p[0] * 3 + 2]);
            for (int j = 1; j < nvp && p[j] != PolyMesh.MeshNullIdx; ++j) {
                final int x = verts[p[j] * 3 + 0];
                final int y = verts[p[j] * 3 + 1];
                final int z = verts[p[j] * 3 + 2];
                if (x < it.BMin[0]) {
                    it.BMin[0] = x;
                }
                if (y < it.BMin[1]) {
                    it.BMin[1] = y;
                }
                if (z < it.BMin[2]) {
                    it.BMin[2] = z;
                }
                if (x > it.BMax[0]) {
                    it.BMax[0] = x;
                }
                if (y > it.BMax[1]) {
                    it.BMax[1] = y;
                }
                if (z > it.BMax[2]) {
                    it.BMax[2] = z;
                }
            }
            it.BMin[1] = (int)Math.floor(it.BMin[1] * ch / cs);
            it.BMax[1] = (int)Math.ceil(it.BMax[1] * ch / cs);
        }
        final int curNode = 0;
        final BVNode[] temp = this.NavBvTree;
        this.Subdivide(items, npolys, 0, npolys, curNode, temp);
        this.NavBvTree = temp;
        return curNode;
    }
    
    private int Subdivide(final BVNode[] items, final int nitems, final int imin, final int imax, int curNode, final BVNode[] nodes) {
        final int inum = imax - imin;
        final int icur = curNode;
        final BVNode node = nodes[curNode++];
        if (inum == 1) {
            node.BMin[0] = items[imin].BMin[0];
            node.BMin[1] = items[imin].BMin[1];
            node.BMin[2] = items[imin].BMin[2];
            node.BMax[0] = items[imin].BMax[0];
            node.BMax[1] = items[imin].BMax[1];
            node.BMax[2] = items[imin].BMax[2];
            node.I = items[imin].I;
        }
        else {
            final int[] tempBMin = new int[3];
            final int[] tempBMax = new int[3];
            this.CalcExtends(items, nitems, imin, imax, tempBMin, tempBMax);
            System.arraycopy(tempBMin, 0, node.BMin, 0, 3);
            System.arraycopy(tempBMax, 0, node.BMax, 0, 3);
            final int axis = this.LongestAxis(node.BMax[0] - node.BMin[0], node.BMax[1] - node.BMin[1], node.BMax[2] - node.BMin[2]);
            if (axis == 0) {
                Arrays.sort(items, imin, inum, new CompareNodeX());
            }
            else if (axis == 1) {
                Arrays.sort(items, imin, inum, new CompareNodeY());
            }
            else {
                Arrays.sort(items, imin, inum, new CompareNodeZ());
            }
            final int isplit = imin + inum / 2;
            this.Subdivide(items, nitems, imin, isplit, curNode, nodes);
            this.Subdivide(items, nitems, isplit, imax, curNode, nodes);
            final int iescape = curNode - icur;
            node.I = -iescape;
        }
        return curNode;
    }
    
    private void CalcExtends(final BVNode[] items, final int nitems, final int imin, final int imax, final int[] bmin, final int[] bmax) {
        bmin[0] = items[imin].BMin[0];
        bmin[1] = items[imin].BMin[1];
        bmin[2] = items[imin].BMin[2];
        bmax[0] = items[imin].BMax[0];
        bmax[1] = items[imin].BMax[1];
        bmax[2] = items[imin].BMax[2];
        for (int i = imin + 1; i < imax; ++i) {
            final BVNode it = items[i];
            if (it.BMin[0] < bmin[0]) {
                bmin[0] = it.BMin[0];
            }
            if (it.BMin[1] < bmin[1]) {
                bmin[1] = it.BMin[1];
            }
            if (it.BMin[2] < bmin[2]) {
                bmin[2] = it.BMin[2];
            }
            if (it.BMax[0] > bmax[0]) {
                bmax[0] = it.BMax[0];
            }
            if (it.BMax[1] > bmax[1]) {
                bmax[1] = it.BMax[1];
            }
            if (it.BMax[2] > bmax[2]) {
                bmax[2] = it.BMax[2];
            }
        }
    }
    
    private int LongestAxis(final int x, final int y, final int z) {
        int axis = 0;
        float maxVal = x;
        if (y > maxVal) {
            axis = 1;
            maxVal = y;
        }
        if (z > maxVal) {
            axis = 2;
            maxVal = z;
        }
        return axis;
    }
    
    private short ClassifyOffMeshPoint(final float ptx, final float pty, final float ptz, final float[] bmin, final float[] bmax) {
        final short XP = 1;
        final short ZP = 2;
        final short XM = 4;
        final short ZM = 8;
        short outcode = 0;
        outcode |= (short)((ptx >= bmax[0]) ? 1 : 0);
        outcode |= (short)((ptz >= bmax[2]) ? 2 : 0);
        outcode |= (short)((ptx < bmin[0]) ? 4 : 0);
        outcode |= (short)((ptz < bmin[2]) ? 8 : 0);
        switch (outcode) {
            case 1: {
                return 0;
            }
            case 3: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 6: {
                return 3;
            }
            case 4: {
                return 4;
            }
            case 12: {
                return 5;
            }
            case 8: {
                return 6;
            }
            case 9: {
                return 7;
            }
            default: {
                return 255;
            }
        }
    }
    
    static {
        NavMeshBuilder.MaxAreas = 64;
        NavMeshBuilder.VertsPerPoly = 6;
        NavMeshBuilder.PolyTypeGround = 0;
        NavMeshBuilder.PolyTypeOffMeshConnection = 1;
        NavMeshBuilder.ExtLink = 32768;
        NavMeshBuilder.OffMeshConBiDir = 1;
    }
}
