// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.recast;

import com.app.server.atavism.server.pathing.detour.DetourNumericReturn;
import com.app.server.atavism.server.math.IntVector2;

public class ContourSet
{
    public Contour[] Conts;
    public int NConts;
    public float[] BMin;
    public float[] BMax;
    public float Cs;
    public float Ch;
    public int Width;
    public int Height;
    public int BorderSize;
    public static int ContourRegMask;
    public static int BorderVertex;
    public static int AreaBorder;
    
    public ContourSet(final CompactHeightfield cfh, final float maxError, final int maxEdgeLen) {
        this(cfh, maxError, maxEdgeLen, 1);
    }
    
    public ContourSet(final CompactHeightfield cfh, final float maxError, final int maxEdgeLen, final int buildFlags) {
        final int w = cfh.Width;
        final int h = cfh.Height;
        final int borderSize = cfh.BorderSize;
        this.BMin = new float[3];
        this.BMax = new float[3];
        System.arraycopy(cfh.BMin, 0, this.BMin, 0, 3);
        System.arraycopy(cfh.BMax, 0, this.BMax, 0, 3);
        if (borderSize > 0) {
            final float pad = borderSize * cfh.Cs;
            final float[] bMin = this.BMin;
            final int n = 0;
            bMin[n] += pad;
            final float[] bMin2 = this.BMin;
            final int n2 = 2;
            bMin2[n2] += pad;
            final float[] bMax = this.BMax;
            final int n3 = 0;
            bMax[n3] -= pad;
            final float[] bMax2 = this.BMax;
            final int n4 = 2;
            bMax2[n4] -= pad;
        }
        this.Cs = cfh.Cs;
        this.Ch = cfh.Ch;
        this.Width = cfh.Width - cfh.BorderSize * 2;
        this.Height = cfh.Height - cfh.BorderSize * 2;
        this.BorderSize = cfh.BorderSize;
        int maxContours = Math.max(cfh.MaxRegions, 8);
        this.Conts = new Contour[maxContours];
        for (int i = 0; i < maxContours; ++i) {
            this.Conts[i] = new Contour();
        }
        this.NConts = 0;
        final char[] flags = new char[cfh.SpanCount];
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final CompactCell c = cfh.Cells[x + y * w];
                for (int j = (int)c.Index, ni = (int)(c.Index + c.Count); j < ni; ++j) {
                    if (j == 4782) {
                        final int z = 0;
                    }
                    int res = 0;
                    final CompactSpan s = cfh.Spans[j];
                    if (s.Reg == 0 || (s.Reg & CompactHeightfield.BorderReg) != 0x0) {
                        flags[j] = '\0';
                    }
                    else {
                        for (int dir = 0; dir < 4; ++dir) {
                            int r = 0;
                            if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                                final int ax = x + Helper.GetDirOffsetX(dir);
                                final int ay = y + Helper.GetDirOffsetY(dir);
                                final int ai = (int)cfh.Cells[ax + ay * w].Index + s.GetCon(dir);
                                r = cfh.Spans[ai].Reg;
                            }
                            if (r == cfh.Spans[j].Reg) {
                                res |= 1 << dir;
                            }
                        }
                        flags[j] = (char)(res ^ 0xF);
                    }
                }
            }
        }
        final IntArray verts = new IntArray(256);
        final IntArray simplified = new IntArray(64);
        for (int y2 = 0; y2 < h; ++y2) {
            for (int x2 = 0; x2 < w; ++x2) {
                final CompactCell c2 = cfh.Cells[x2 + y2 * w];
                for (int k = (int)c2.Index, ni2 = (int)(c2.Index + c2.Count); k < ni2; ++k) {
                    if (flags[k] == '\0' || flags[k] == '\u000f') {
                        flags[k] = '\0';
                    }
                    else {
                        final int reg = cfh.Spans[k].Reg;
                        if (reg != 0) {
                            if ((reg & CompactHeightfield.BorderReg) == 0x0) {
                                final long area = cfh.Areas[k];
                                verts.Resize(0);
                                simplified.Resize(0);
                                this.WalkContour(x2, y2, k, cfh, flags, verts);
                                this.SimplifyContour(verts, simplified, maxError, maxEdgeLen, buildFlags);
                                this.RemoveDegenerateSegments(simplified);
                                if (simplified.Size / 4 >= 3) {
                                    if (this.NConts >= maxContours) {
                                        final int oldMax = maxContours;
                                        maxContours *= 2;
                                        final Contour[] newConts = new Contour[maxContours];
                                        for (int l = 0; l < maxContours; ++l) {
                                            newConts[l] = new Contour();
                                        }
                                        for (int l = 0; l < this.NConts; ++l) {
                                            newConts[l] = this.Conts[l];
                                        }
                                        this.Conts = newConts;
                                    }
                                    final Contour cont = this.Conts[this.NConts++];
                                    cont.NVerts = simplified.Size / 4;
                                    cont.Verts = new int[cont.NVerts * 4];
                                    System.arraycopy(simplified.ToArray(), 0, cont.Verts, 0, cont.NVerts * 4);
                                    if (borderSize > 0) {
                                        for (int m = 0; m < cont.NVerts; ++m) {
                                            final int v = m * 4;
                                            final int[] verts2 = cont.Verts;
                                            final int n5 = v + 0;
                                            verts2[n5] -= borderSize;
                                            final int[] verts3 = cont.Verts;
                                            final int n6 = v + 2;
                                            verts3[n6] -= borderSize;
                                        }
                                    }
                                    cont.NRVerts = verts.Size / 4;
                                    cont.RVerts = new int[cont.NRVerts * 4];
                                    System.arraycopy(verts.ToArray(), 0, cont.RVerts, 0, cont.NRVerts * 4);
                                    if (borderSize > 0) {
                                        for (int m = 0; m < cont.NRVerts; ++m) {
                                            final int v = m * 4;
                                            final int[] rVerts = cont.RVerts;
                                            final int n7 = v + 0;
                                            rVerts[n7] -= borderSize;
                                            final int[] rVerts2 = cont.RVerts;
                                            final int n8 = v + 2;
                                            rVerts2[n8] -= borderSize;
                                        }
                                    }
                                    cont.Reg = reg;
                                    cont.Area = (short)area;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int i2 = 0; i2 < this.NConts; ++i2) {
            final Contour cont2 = this.Conts[i2];
            if (this.CalcAreaOfPolygon2D(cont2.Verts, cont2.NVerts) < 0) {
                int mergeIdx = -1;
                for (int j2 = 0; j2 < this.NConts; ++j2) {
                    if (i2 != j2) {
                        if (this.Conts[j2].NVerts > 0 && this.Conts[j2].Reg == cont2.Reg && this.CalcAreaOfPolygon2D(this.Conts[j2].Verts, this.Conts[j2].NVerts) > 0) {
                            mergeIdx = j2;
                            break;
                        }
                    }
                }
                if (mergeIdx != -1) {
                    final Contour mcont = this.Conts[mergeIdx];
                    final IntVector2 iaib = this.GetClosestIndices(mcont.Verts, mcont.NVerts, cont2.Verts, cont2.NVerts);
                    if (iaib.x != -1) {
                        if (iaib.y != -1) {
                            if (!this.MergeContours(mcont, cont2, iaib.x, iaib.y)) {}
                        }
                    }
                }
            }
        }
    }
    
    private void WalkContour(int x, int y, int i, final CompactHeightfield cfh, final char[] flags, final IntArray points) {
        char dir;
        for (dir = '\0'; (flags[i] & 1 << dir) == 0x0; ++dir) {}
        final char startDir = dir;
        final char tempDir = dir;
        final int starti = i;
        final long area = cfh.Areas[i];
        int iter = 0;
        while (++iter < 40000) {
            if ((flags[i] & 1 << dir) > 0) {
                Boolean isAreaBorder = false;
                int px = x;
                final DetourNumericReturn cornerHeight = this.GetCornerHeight(x, y, i, tempDir, cfh);
                final Boolean isBorderVertex = cornerHeight.boolValue;
                final int py = cornerHeight.intValue;
                int pz = y;
                if (dir == '\0') {
                    ++pz;
                }
                else if (dir == '\u0001') {
                    ++px;
                    ++pz;
                }
                else if (dir == '\u0002') {
                    ++px;
                }
                int r = 0;
                final CompactSpan s = cfh.Spans[i];
                if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                    final int ax = x + Helper.GetDirOffsetX(dir);
                    final int ay = y + Helper.GetDirOffsetY(dir);
                    final int ai = (int)cfh.Cells[ax + ay * cfh.Width].Index + s.GetCon(dir);
                    r = cfh.Spans[ai].Reg;
                    if (area != cfh.Areas[ai]) {
                        isAreaBorder = true;
                    }
                }
                if (isBorderVertex) {
                    r |= ContourSet.BorderVertex;
                }
                if (isAreaBorder) {
                    r |= ContourSet.AreaBorder;
                }
                points.Push(px);
                points.Push(py);
                points.Push(pz);
                points.Push(r);
                final int n = i;
                flags[n] &= (char)~(1 << dir);
                dir = (char)(dir + '\u0001' & '\u0003');
            }
            else {
                int ni = -1;
                final int nx = x + Helper.GetDirOffsetX(dir);
                final int ny = y + Helper.GetDirOffsetY(dir);
                final CompactSpan s2 = cfh.Spans[i];
                if (s2.GetCon(dir) != CompactHeightfield.NotConnected) {
                    final CompactCell nc = cfh.Cells[nx + ny * cfh.Width];
                    ni = (int)nc.Index + s2.GetCon(dir);
                }
                if (ni == -1) {
                    return;
                }
                x = nx;
                y = ny;
                i = ni;
                dir = (char)(dir + '\u0003' & '\u0003');
            }
            if (starti == i && startDir == dir) {
                break;
            }
        }
    }
    
    private DetourNumericReturn GetCornerHeight(final int x, final int y, final int i, final int dir, final CompactHeightfield cfh) {
        final DetourNumericReturn numericReturn = new DetourNumericReturn();
        numericReturn.boolValue = false;
        final CompactSpan s = cfh.Spans[i];
        int ch = s.Y;
        final int dirp = dir + 1 & 0x3;
        final long[] regs = { 0L, 0L, 0L, 0L };
        regs[0] = (cfh.Spans[i].Reg | cfh.Areas[i] << 16);
        if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
            final int ax = x + Helper.GetDirOffsetX(dir);
            final int ay = y + Helper.GetDirOffsetY(dir);
            final int ai = (int)cfh.Cells[ax + ay * cfh.Width].Index + s.GetCon(dir);
            final CompactSpan aspan = cfh.Spans[ai];
            ch = Math.max(ch, aspan.Y);
            regs[1] = (cfh.Spans[ai].Reg | cfh.Areas[ai] << 16);
            if (aspan.GetCon(dirp) != CompactHeightfield.NotConnected) {
                final int ax2 = ax + Helper.GetDirOffsetX(dirp);
                final int ay2 = ay + Helper.GetDirOffsetY(dirp);
                final int ai2 = (int)cfh.Cells[ax2 + ay2 * cfh.Width].Index + aspan.GetCon(dirp);
                final CompactSpan as2 = cfh.Spans[ai2];
                ch = Math.max(ch, as2.Y);
                regs[2] = (cfh.Spans[ai2].Reg | cfh.Areas[ai2] << 16);
            }
        }
        if (s.GetCon(dirp) != CompactHeightfield.NotConnected) {
            final int ax = x + Helper.GetDirOffsetX(dirp);
            final int ay = y + Helper.GetDirOffsetY(dirp);
            final int ai = (int)cfh.Cells[ax + ay * cfh.Width].Index + s.GetCon(dirp);
            final CompactSpan aspan = cfh.Spans[ai];
            ch = Math.max(ch, aspan.Y);
            regs[3] = (cfh.Spans[ai].Reg | cfh.Areas[ai] << 16);
            if (aspan.GetCon(dir) != CompactHeightfield.NotConnected) {
                final int ax2 = ax + Helper.GetDirOffsetX(dir);
                final int ay2 = ay + Helper.GetDirOffsetY(dir);
                final int ai2 = (int)cfh.Cells[ax2 + ay2 * cfh.Width].Index + aspan.GetCon(dir);
                final CompactSpan as2 = cfh.Spans[ai2];
                ch = Math.max(ch, as2.Y);
                regs[2] = (cfh.Spans[ai2].Reg | cfh.Areas[ai2] << 16);
            }
        }
        for (int j = 0; j < 4; ++j) {
            final int a = j;
            final int b = j + 1 & 0x3;
            final int c = j + 2 & 0x3;
            final int d = j + 3 & 0x3;
            final Boolean twoSameExts = (regs[a] & regs[b] & CompactHeightfield.BorderReg) != 0x0L && regs[a] == regs[b];
            final Boolean twoInts = ((regs[c] | regs[d]) & CompactHeightfield.BorderReg) == 0x0L;
            final Boolean intsSameArea = regs[c] >> 16 == regs[d] >> 16;
            final Boolean noZeros = regs[a] != 0L && regs[b] != 0L && regs[c] != 0L && regs[d] != 0L;
            if (twoSameExts && twoInts && intsSameArea && noZeros) {
                numericReturn.boolValue = true;
                break;
            }
        }
        numericReturn.intValue = ch;
        return numericReturn;
    }
    
    private void SimplifyContour(final IntArray points, final IntArray simplified, final float maxError, final int maxEdgeLen, final int buildFlags) {
        Boolean hasConnections = false;
        for (int i = 0; i < points.Size; i += 4) {
            if ((points.get(i + 3) & ContourSet.ContourRegMask) != 0x0) {
                hasConnections = true;
                break;
            }
        }
        if (hasConnections) {
            for (int i = 0, ni = points.Size / 4; i < ni; ++i) {
                final int ii = (i + 1) % ni;
                final Boolean differentRegs = (points.get(i * 4 + 3) & ContourSet.ContourRegMask) != (points.get(ii * 4 + 3) & ContourSet.ContourRegMask);
                final Boolean areaBorders = (points.get(i * 4 + 3) & ContourSet.AreaBorder) != (points.get(ii * 4 + 3) & ContourSet.AreaBorder);
                if (differentRegs || areaBorders) {
                    simplified.Push(points.get(i * 4 + 0));
                    simplified.Push(points.get(i * 4 + 1));
                    simplified.Push(points.get(i * 4 + 2));
                    simplified.Push(i);
                }
            }
        }
        if (simplified.Size == 0) {
            int llx = points.get(0);
            int lly = points.get(1);
            int llz = points.get(2);
            int lli = 0;
            int urx = points.get(0);
            int ury = points.get(1);
            int urz = points.get(2);
            int uri = 0;
            for (int j = 0; j < points.Size; j += 4) {
                final int x = points.get(j + 0);
                final int y = points.get(j + 1);
                final int z = points.get(j + 2);
                if (x < llx || (x == llx && z < llz)) {
                    llx = x;
                    lly = y;
                    llz = z;
                    lli = j / 4;
                }
                if (x > urx || (x == urx && z > urz)) {
                    urx = x;
                    ury = y;
                    urz = z;
                    uri = j / 4;
                }
            }
            simplified.Push(llx);
            simplified.Push(lly);
            simplified.Push(llz);
            simplified.Push(lli);
            simplified.Push(urx);
            simplified.Push(ury);
            simplified.Push(urz);
            simplified.Push(uri);
        }
        final int pn = points.Size / 4;
        int k = 0;
        while (k < simplified.Size / 4) {
            final int ii = (k + 1) % (simplified.Size / 4);
            final int ax = simplified.get(k * 4 + 0);
            final int az = simplified.get(k * 4 + 2);
            final int ai = simplified.get(k * 4 + 3);
            final int bx = simplified.get(ii * 4 + 0);
            final int bz = simplified.get(ii * 4 + 2);
            final int bi = simplified.get(ii * 4 + 3);
            float maxd = 0.0f;
            int maxi = -1;
            int cinc;
            int ci;
            int endi;
            if (bx > ax || (bx == ax && bz > az)) {
                cinc = 1;
                ci = (ai + cinc) % pn;
                endi = bi;
            }
            else {
                cinc = pn - 1;
                ci = (bi + cinc) % pn;
                endi = ai;
            }
            if ((points.get(ci * 4 + 3) & ContourSet.ContourRegMask) == 0x0 || (points.get(ci * 4 + 3) & ContourSet.AreaBorder) != 0x0) {
                while (ci != endi) {
                    final float d = this.DistancePtSeg(points.get(ci * 4 + 0), points.get(ci * 4 + 2), ax, az, bx, bz);
                    if (d > maxd) {
                        maxd = d;
                        maxi = ci;
                    }
                    ci = (ci + cinc) % pn;
                }
            }
            final float errorSqrd = maxError * maxError;
            if (maxi != -1 && maxd > maxError * maxError) {
                simplified.Resize(simplified.Size + 4);
                final int n = simplified.Size / 4;
                for (int l = n - 1; l > k; --l) {
                    simplified.set(l * 4 + 0, simplified.get((l - 1) * 4 + 0));
                    simplified.set(l * 4 + 1, simplified.get((l - 1) * 4 + 1));
                    simplified.set(l * 4 + 2, simplified.get((l - 1) * 4 + 2));
                    simplified.set(l * 4 + 3, simplified.get((l - 1) * 4 + 3));
                }
                simplified.set((k + 1) * 4 + 0, points.get(maxi * 4 + 0));
                simplified.set((k + 1) * 4 + 1, points.get(maxi * 4 + 1));
                simplified.set((k + 1) * 4 + 2, points.get(maxi * 4 + 2));
                simplified.set((k + 1) * 4 + 3, maxi);
            }
            else {
                ++k;
            }
        }
        if (maxEdgeLen > 0 && (buildFlags & 0x3) != 0x0) {
            k = 0;
            while (k < simplified.Size / 4) {
                final int ii = (k + 1) % (simplified.Size / 4);
                final int ax = simplified.get(k * 4 + 0);
                final int az = simplified.get(k * 4 + 2);
                final int ai = simplified.get(k * 4 + 3);
                final int bx = simplified.get(ii * 4 + 0);
                final int bz = simplified.get(ii * 4 + 2);
                final int bi = simplified.get(ii * 4 + 3);
                int maxi2 = -1;
                final int ci2 = (ai + 1) % pn;
                Boolean tess = false;
                if ((buildFlags & 0x1) != 0x0 && (points.get(ci2 * 4 + 3) & ContourSet.ContourRegMask) == 0x0) {
                    tess = true;
                }
                if ((buildFlags & 0x2) != 0x0 && (points.get(ci2 * 4 + 3) & ContourSet.AreaBorder) != 0x0) {
                    tess = true;
                }
                if (tess) {
                    final int dx = bx - ax;
                    final int dz = bz - az;
                    if (dx * dx + dz * dz > maxEdgeLen * maxEdgeLen) {
                        final int n2 = (bi < ai) ? (bi + pn - ai) : (bi - ai);
                        if (n2 > 1) {
                            if (bx > ax || (bx == ax && bz > az)) {
                                maxi2 = (int)(ai + n2 / 2.0f) % pn;
                            }
                            else {
                                maxi2 = (int)(ai + (n2 + 1) / 2.0f) % pn;
                            }
                        }
                    }
                }
                if (maxi2 != -1) {
                    simplified.Resize(simplified.Size + 4);
                    final int n3 = simplified.Size / 4;
                    for (int m = n3 - 1; m > k; --m) {
                        simplified.set(m * 4 + 0, simplified.get((m - 1) * 4 + 0));
                        simplified.set(m * 4 + 1, simplified.get((m - 1) * 4 + 1));
                        simplified.set(m * 4 + 2, simplified.get((m - 1) * 4 + 2));
                        simplified.set(m * 4 + 3, simplified.get((m - 1) * 4 + 3));
                    }
                    simplified.set((k + 1) * 4 + 0, points.get(maxi2 * 4 + 0));
                    simplified.set((k + 1) * 4 + 1, points.get(maxi2 * 4 + 1));
                    simplified.set((k + 1) * 4 + 2, points.get(maxi2 * 4 + 2));
                    simplified.set((k + 1) * 4 + 3, maxi2);
                }
                else {
                    ++k;
                }
            }
        }
        for (k = 0; k < simplified.Size / 4; ++k) {
            final int ai2 = (simplified.get(k * 4 + 3) + 1) % pn;
            final int bi2 = simplified.get(k * 4 + 3);
            simplified.set(k * 4 + 3, (points.get(ai2 * 4 + 3) & (ContourSet.ContourRegMask | ContourSet.AreaBorder)) | (points.get(bi2 * 4 + 3) & ContourSet.BorderVertex));
        }
    }
    
    private float DistancePtSeg(final int x, final int z, final int px, final int pz, final int qx, final int qz) {
        final float pqx = qx - px;
        final float pqz = qz - pz;
        float dx = x - px;
        float dz = z - pz;
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
        dx = px + t * pqx - x;
        dz = pz + t * pqz - z;
        return dx * dx + dz * dz;
    }
    
    private void RemoveDegenerateSegments(final IntArray simplified) {
        for (int i = 0; i < simplified.Size / 4; ++i) {
            int ni = i + 1;
            if (ni >= simplified.Size / 4) {
                ni = 0;
            }
            if (simplified.get(i * 4 + 0) == simplified.get(ni * 4 + 0) && simplified.get(i * 4 + 2) == simplified.get(ni * 4 + 2)) {
                for (int j = i; j < simplified.Size / 4 - 1; ++j) {
                    simplified.set(j * 4 + 0, simplified.get((j + 1) * 4 + 0));
                    simplified.set(j * 4 + 1, simplified.get((j + 1) * 4 + 1));
                    simplified.set(j * 4 + 2, simplified.get((j + 1) * 4 + 2));
                    simplified.set(j * 4 + 3, simplified.get((j + 1) * 4 + 3));
                }
                simplified.Resize(simplified.Size - 4);
            }
        }
    }
    
    private int CalcAreaOfPolygon2D(final int[] verts, final int nVerts) {
        int area = 0;
        int i = 0;
        int j = nVerts - 1;
        while (i < nVerts) {
            final int vi = i * 4;
            final int vj = j * 4;
            area += verts[vi + 0] * verts[vj + 2] - verts[vj + 0] * verts[vi + 2];
            j = i++;
        }
        return (area + 1) / 2;
    }
    
    private IntVector2 GetClosestIndices(final int[] vertsa, final int nvertsa, final int[] vertsb, final int nvertsb) {
        final IntVector2 iaib = new IntVector2(-1, -1);
        int closestDist = Integer.MAX_VALUE;
        for (int i = 0; i < nvertsa; ++i) {
            final int iNext = (i + 1) % nvertsa;
            final int iPrev = (i + nvertsa - 1) % nvertsa;
            final int va = i * 4;
            final int van = iNext * 4;
            final int vap = iPrev * 4;
            for (int j = 0; j < nvertsb; ++j) {
                final int vb = j * 4;
                if (this.ILeft(vertsa, vap, vertsa, va, vertsb, vb) && this.ILeft(vertsa, va, vertsa, van, vertsb, vb)) {
                    final int dx = vertsb[vb + 0] - vertsa[va + 0];
                    final int dz = vertsb[vb + 2] - vertsa[va + 2];
                    final int d = dx * dx + dz * dz;
                    if (d < closestDist) {
                        iaib.x = i;
                        iaib.y = j;
                        closestDist = d;
                    }
                }
            }
        }
        return iaib;
    }
    
    private Boolean ILeft(final int[] a, final int ia, final int[] b, final int ib, final int[] c, final int ic) {
        return (b[ib + 0] - a[ia + 0]) * (c[ic + 2] - a[ia + 2]) - (c[ic + 0] - a[ia + 0]) * (b[ib + 2] - a[ia + 2]) <= 0;
    }
    
    private Boolean MergeContours(final Contour ca, final Contour cb, final int ia, final int ib) {
        final int maxVerts = ca.NVerts + cb.NVerts + 2;
        final int[] verts = new int[maxVerts * 4];
        int nv = 0;
        for (int i = 0; i < ca.NVerts; ++i) {
            final int dst = nv * 4;
            final int src = (ia + i) % ca.NVerts * 4;
            verts[dst + 0] = ca.Verts[src + 0];
            verts[dst + 1] = ca.Verts[src + 1];
            verts[dst + 2] = ca.Verts[src + 2];
            verts[dst + 3] = ca.Verts[src + 3];
            ++nv;
        }
        for (int i = 0; i < cb.NVerts; ++i) {
            final int dst = nv * 4;
            final int src = (ib + i) % cb.NVerts * 4;
            verts[dst + 0] = cb.Verts[src + 0];
            verts[dst + 1] = cb.Verts[src + 1];
            verts[dst + 2] = cb.Verts[src + 2];
            verts[dst + 3] = cb.Verts[src + 3];
            ++nv;
        }
        ca.Verts = verts;
        ca.NVerts = nv;
        cb.Verts = null;
        cb.NVerts = 0;
        return true;
    }
    
    static {
        ContourSet.ContourRegMask = 65535;
        ContourSet.BorderVertex = 65536;
        ContourSet.AreaBorder = 131072;
    }
}
