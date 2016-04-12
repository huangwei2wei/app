// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

public class CompactHeightfield
{
    public int Width;
    public int Height;
    public int SpanCount;
    public int WalkableHeight;
    public int WalkableClimb;
    public int BorderSize;
    public int MaxDistance;
    public int MaxRegions;
    public float[] BMin;
    public float[] BMax;
    public float Cs;
    public float Ch;
    public CompactCell[] Cells;
    public CompactSpan[] Spans;
    public int[] Dist;
    public long[] Areas;
    public static int NotConnected;
    public static int BorderReg;
    
    public CompactHeightfield(final int walkableHeight, final int walkableClimb, final HeightField hf) {
        final int w = hf.Width;
        final int h = hf.Height;
        final int spanCount = hf.GetHeightFieldSpanCount();
        this.Width = w;
        this.Height = h;
        this.SpanCount = spanCount;
        this.WalkableHeight = walkableHeight;
        this.WalkableClimb = walkableClimb;
        this.MaxRegions = 0;
        this.BMin = new float[3];
        this.BMax = new float[3];
        System.arraycopy(hf.Bmin, 0, this.BMin, 0, 3);
        System.arraycopy(hf.Bmax, 0, this.BMax, 0, 3);
        final float[] bMax = this.BMax;
        final int n = 1;
        bMax[n] += walkableHeight * hf.Ch;
        this.Cs = hf.Cs;
        this.Ch = hf.Ch;
        this.Cells = new CompactCell[w * h];
        this.Spans = new CompactSpan[spanCount];
        this.Areas = new long[spanCount];
        final int MaxHeight = 65535;
        int idx = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                Span s = hf.Spans[x + y * w];
                if (s != null) {
                    this.Cells[x + y * w].Index = idx;
                    this.Cells[x + y * w].Count = 0L;
                    while (s != null) {
                        if (s.Area != HeightField.NullArea) {
                            final int bot = (int)s.SMax;
                            final int top = (s.Next != null) ? ((int)s.Next.SMin) : MaxHeight;
                            this.Spans[idx].Y = Math.max(0, Math.min(bot, MaxHeight));
                            this.Spans[idx].H = Math.max(0, Math.min(top - bot, 255));
                            this.Areas[idx] = s.Area;
                            ++idx;
                            final CompactCell compactCell = this.Cells[x + y * w];
                            ++compactCell.Count;
                        }
                        s = s.Next;
                    }
                }
            }
        }
        final int MaxLayers = CompactHeightfield.NotConnected - 1;
        int tooHighNeighbor = 0;
        for (int y2 = 0; y2 < h; ++y2) {
            for (int x2 = 0; x2 < w; ++x2) {
                final CompactCell c = this.Cells[x2 + y2 * w];
                for (int i = (int)c.Index, ni = (int)(c.Index + c.Count); i < ni; ++i) {
                    for (int dir = 0; dir < 4; ++dir) {
                        this.Spans[i].SetCon(dir, CompactHeightfield.NotConnected);
                        final int nx = x2 + Helper.GetDirOffsetX(dir);
                        final int ny = y2 + Helper.GetDirOffsetY(dir);
                        if (nx >= 0 && ny >= 0 && nx < w) {
                            if (ny < h) {
                                final CompactCell nc = this.Cells[nx + ny * w];
                                for (int k = (int)nc.Index, nk = (int)(nc.Index + nc.Count); k < nk; ++k) {
                                    final CompactSpan ns = this.Spans[k];
                                    final int bot2 = Math.max(this.Spans[i].Y, ns.Y);
                                    final int top2 = Math.min(this.Spans[i].Y + this.Spans[i].H, ns.Y + ns.H);
                                    if (top2 - bot2 >= walkableHeight && Math.abs(ns.Y - this.Spans[i].Y) <= walkableClimb) {
                                        final int lidx = k - (int)nc.Index;
                                        if (lidx >= 0 && lidx <= MaxLayers) {
                                            this.Spans[i].SetCon(dir, lidx);
                                            break;
                                        }
                                        tooHighNeighbor = Math.max(tooHighNeighbor, lidx);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public Boolean ErodeWalkableArea(final int radius) {
        final int w = this.Width;
        final int h = this.Height;
        final short[] dist = new short[this.SpanCount];
        for (int i = 0; i < this.SpanCount; ++i) {
            dist[i] = 255;
        }
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int j = (int)c.Index, ni = (int)(c.Index + c.Count); j < ni; ++j) {
                    if (this.Areas[j] == HeightField.NullArea) {
                        dist[j] = 0;
                    }
                    else {
                        final CompactSpan s = this.Spans[j];
                        int nc = 0;
                        for (int dir = 0; dir < 4; ++dir) {
                            if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                                final int nx = x + Helper.GetDirOffsetX(dir);
                                final int ny = y + Helper.GetDirOffsetY(dir);
                                final int nidx = (int)this.Cells[nx + ny * w].Index + s.GetCon(dir);
                                if (this.Areas[nidx] != HeightField.NullArea) {
                                    ++nc;
                                }
                            }
                        }
                        if (nc != 4) {
                            dist[j] = 0;
                        }
                    }
                }
            }
        }
        for (int y2 = 0; y2 < h; ++y2) {
            for (int x2 = 0; x2 < w; ++x2) {
                final CompactCell c2 = this.Cells[x2 + y2 * w];
                for (int k = (int)c2.Index, ni2 = (int)(c2.Index + c2.Count); k < ni2; ++k) {
                    final CompactSpan s2 = this.Spans[k];
                    if (s2.GetCon(0) != CompactHeightfield.NotConnected) {
                        final int ax = x2 + Helper.GetDirOffsetX(0);
                        final int ay = y2 + Helper.GetDirOffsetY(0);
                        final int ai = (int)this.Cells[ax + ay * w].Index + s2.GetCon(0);
                        final CompactSpan aspan = this.Spans[ai];
                        short nd = (short)Math.min(dist[ai] + 2, 255);
                        if (nd < dist[k]) {
                            dist[k] = nd;
                        }
                        if (aspan.GetCon(3) != CompactHeightfield.NotConnected) {
                            final int aax = ax + Helper.GetDirOffsetX(3);
                            final int aay = ay + Helper.GetDirOffsetY(3);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(3);
                            nd = (short)Math.min(dist[aai] + 3, 255);
                            if (nd < dist[k]) {
                                dist[k] = nd;
                            }
                        }
                    }
                    if (s2.GetCon(3) != CompactHeightfield.NotConnected) {
                        final int ax = x2 + Helper.GetDirOffsetX(3);
                        final int ay = y2 + Helper.GetDirOffsetY(3);
                        final int ai = (int)this.Cells[ax + ay * w].Index + s2.GetCon(3);
                        final CompactSpan aspan = this.Spans[ai];
                        short nd = (short)Math.min(dist[ai] + 2, 255);
                        if (nd < dist[k]) {
                            dist[k] = nd;
                        }
                        if (aspan.GetCon(2) != CompactHeightfield.NotConnected) {
                            final int aax = ax + Helper.GetDirOffsetX(2);
                            final int aay = ay + Helper.GetDirOffsetY(2);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(2);
                            nd = (short)Math.min(dist[aai] + 3, 255);
                            if (nd < dist[k]) {
                                dist[k] = nd;
                            }
                        }
                    }
                }
            }
        }
        for (int y2 = h - 1; y2 >= 0; --y2) {
            for (int x2 = w - 1; x2 >= 0; --x2) {
                final CompactCell c2 = this.Cells[x2 + y2 * w];
                for (int k = (int)c2.Index, ni2 = (int)(c2.Index + c2.Count); k < ni2; ++k) {
                    final CompactSpan s2 = this.Spans[k];
                    if (s2.GetCon(2) != CompactHeightfield.NotConnected) {
                        final int ax = x2 + Helper.GetDirOffsetX(2);
                        final int ay = y2 + Helper.GetDirOffsetY(2);
                        final int ai = (int)this.Cells[ax + ay * w].Index + s2.GetCon(2);
                        final CompactSpan aspan = this.Spans[ai];
                        short nd = (short)Math.min(dist[ai] + 2, 255);
                        if (nd < dist[k]) {
                            dist[k] = nd;
                        }
                        if (aspan.GetCon(1) != CompactHeightfield.NotConnected) {
                            final int aax = ax + Helper.GetDirOffsetX(1);
                            final int aay = ay + Helper.GetDirOffsetY(1);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(1);
                            nd = (short)Math.min(dist[aai] + 3, 255);
                            if (nd < dist[k]) {
                                dist[k] = nd;
                            }
                        }
                    }
                    if (s2.GetCon(1) != CompactHeightfield.NotConnected) {
                        final int ax = x2 + Helper.GetDirOffsetX(1);
                        final int ay = y2 + Helper.GetDirOffsetY(1);
                        final int ai = (int)this.Cells[ax + ay * w].Index + s2.GetCon(1);
                        final CompactSpan aspan = this.Spans[ai];
                        short nd = (short)Math.min(dist[ai] + 2, 255);
                        if (nd < dist[k]) {
                            dist[k] = nd;
                        }
                        if (aspan.GetCon(0) != CompactHeightfield.NotConnected) {
                            final int aax = ax + Helper.GetDirOffsetX(0);
                            final int aay = ay + Helper.GetDirOffsetY(0);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(0);
                            nd = (short)Math.min(dist[aai] + 3, 255);
                            if (nd < dist[k]) {
                                dist[k] = nd;
                            }
                        }
                    }
                }
            }
        }
        final short thr = (short)(radius * 2);
        for (int l = 0; l < this.SpanCount; ++l) {
            if (dist[l] < thr) {
                this.Areas[l] = HeightField.NullArea;
            }
        }
        return true;
    }
    
    public Boolean BuildDistanceField() {
        final int[] src = new int[this.SpanCount];
        final int[] dst = new int[this.SpanCount];
        final int maxDist = this.CalculateDistanceField(src, 0);
        this.MaxDistance = maxDist;
        this.Dist = this.BoxBlur(1, src, dst);
        return true;
    }
    
    private int[] BoxBlur(int thr, final int[] src, final int[] dst) {
        final int w = this.Width;
        final int h = this.Height;
        thr *= 2;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int i = (int)c.Index, ni = (int)(c.Index + c.Count); i < ni; ++i) {
                    final CompactSpan s = this.Spans[i];
                    final int cd = src[i];
                    if (cd <= thr) {
                        dst[i] = cd;
                    }
                    else {
                        int d = cd;
                        for (int dir = 0; dir < 4; ++dir) {
                            if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                                final int ax = x + Helper.GetDirOffsetX(dir);
                                final int ay = y + Helper.GetDirOffsetY(dir);
                                final int ai = (int)this.Cells[ax + ay * w].Index + s.GetCon(dir);
                                d += src[ai];
                                final CompactSpan aspan = this.Spans[ai];
                                final int dir2 = dir + 1 & 0x3;
                                if (aspan.GetCon(dir2) != CompactHeightfield.NotConnected) {
                                    final int ax2 = ax + Helper.GetDirOffsetX(dir2);
                                    final int ay2 = ay + Helper.GetDirOffsetY(dir2);
                                    final int ai2 = (int)this.Cells[ax2 + ay2 * w].Index + aspan.GetCon(dir2);
                                    d += src[ai2];
                                }
                                else {
                                    d += cd;
                                }
                            }
                            else {
                                d += cd * 2;
                            }
                        }
                        dst[i] = (d + 5) / 9;
                    }
                }
            }
        }
        return dst;
    }
    
    private int CalculateDistanceField(final int[] src, int maxDist) {
        final int w = this.Width;
        final int h = this.Height;
        for (int i = 0; i < this.SpanCount; ++i) {
            src[i] = 65535;
        }
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int j = (int)c.Index, ni = (int)(c.Index + c.Count); j < ni; ++j) {
                    final CompactSpan s = this.Spans[j];
                    final long area = this.Areas[j];
                    int nc = 0;
                    for (int dir = 0; dir < 4; ++dir) {
                        if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                            final int ax = x + Helper.GetDirOffsetX(dir);
                            final int ay = y + Helper.GetDirOffsetY(dir);
                            final int ai = (int)this.Cells[ax + ay * w].Index + s.GetCon(dir);
                            if (area == this.Areas[ai]) {
                                ++nc;
                            }
                        }
                    }
                    if (nc != 4) {
                        src[j] = 0;
                    }
                }
            }
        }
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int j = (int)c.Index, ni = (int)(c.Index + c.Count); j < ni; ++j) {
                    final CompactSpan s = this.Spans[j];
                    if (s.GetCon(0) != CompactHeightfield.NotConnected) {
                        final int ax2 = x + Helper.GetDirOffsetX(0);
                        final int ay2 = y + Helper.GetDirOffsetY(0);
                        final int ai2 = (int)this.Cells[ax2 + ay2 * w].Index + s.GetCon(0);
                        final CompactSpan aspan = this.Spans[ai2];
                        if (src[ai2] + 2 < src[j]) {
                            src[j] = src[ai2] + 2;
                        }
                        if (aspan.GetCon(3) != CompactHeightfield.NotConnected) {
                            final int aax = ax2 + Helper.GetDirOffsetX(3);
                            final int aay = ay2 + Helper.GetDirOffsetY(3);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(3);
                            if (src[aai] + 3 < src[j]) {
                                src[j] = src[aai] + 3;
                            }
                        }
                    }
                    if (s.GetCon(3) != CompactHeightfield.NotConnected) {
                        final int ax2 = x + Helper.GetDirOffsetX(3);
                        final int ay2 = y + Helper.GetDirOffsetY(3);
                        final int ai2 = (int)this.Cells[ax2 + ay2 * w].Index + s.GetCon(3);
                        final CompactSpan aspan = this.Spans[ai2];
                        if (src[ai2] + 2 < src[j]) {
                            src[j] = src[ai2] + 2;
                        }
                        if (aspan.GetCon(2) != CompactHeightfield.NotConnected) {
                            final int aax = ax2 + Helper.GetDirOffsetX(2);
                            final int aay = ay2 + Helper.GetDirOffsetY(2);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(2);
                            if (src[aai] + 3 < src[j]) {
                                src[j] = src[aai] + 3;
                            }
                        }
                    }
                }
            }
        }
        for (int y = h - 1; y >= 0; --y) {
            for (int x = w - 1; x >= 0; --x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int j = (int)c.Index, ni = (int)(c.Index + c.Count); j < ni; ++j) {
                    final CompactSpan s = this.Spans[j];
                    if (s.GetCon(2) != CompactHeightfield.NotConnected) {
                        final int ax2 = x + Helper.GetDirOffsetX(2);
                        final int ay2 = y + Helper.GetDirOffsetY(2);
                        final int ai2 = (int)this.Cells[ax2 + ay2 * w].Index + s.GetCon(2);
                        final CompactSpan aspan = this.Spans[ai2];
                        if (src[ai2] + 2 < src[j]) {
                            src[j] = src[ai2] + 2;
                        }
                        if (aspan.GetCon(1) != CompactHeightfield.NotConnected) {
                            final int aax = ax2 + Helper.GetDirOffsetX(1);
                            final int aay = ay2 + Helper.GetDirOffsetY(1);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(1);
                            if (src[aai] + 3 < src[j]) {
                                src[j] = src[aai] + 3;
                            }
                        }
                    }
                    if (s.GetCon(1) != CompactHeightfield.NotConnected) {
                        final int ax2 = x + Helper.GetDirOffsetX(1);
                        final int ay2 = y + Helper.GetDirOffsetY(1);
                        final int ai2 = (int)this.Cells[ax2 + ay2 * w].Index + s.GetCon(1);
                        final CompactSpan aspan = this.Spans[ai2];
                        if (src[ai2] + 2 < src[j]) {
                            src[j] = src[ai2] + 2;
                        }
                        if (aspan.GetCon(0) != CompactHeightfield.NotConnected) {
                            final int aax = ax2 + Helper.GetDirOffsetX(0);
                            final int aay = ay2 + Helper.GetDirOffsetY(0);
                            final int aai = (int)this.Cells[aax + aay * w].Index + aspan.GetCon(0);
                            if (src[aai] + 3 < src[j]) {
                                src[j] = src[aai] + 3;
                            }
                        }
                    }
                }
            }
        }
        maxDist = 0;
        for (int i = 0; i < this.SpanCount; ++i) {
            maxDist = Math.max(src[i], maxDist);
        }
        return maxDist;
    }
    
    public Boolean BuildRegions(final int borderSize, final int minRegionArea, final int mergeRegionArea) {
        final int w = this.Width;
        final int h = this.Height;
        final IntArray stack = new IntArray(1024);
        final IntArray visited = new IntArray(1024);
        int[] srcReg = new int[this.SpanCount];
        int[] srcDist = new int[this.SpanCount];
        int[] dstReg = new int[this.SpanCount];
        int[] dstDist = new int[this.SpanCount];
        int regionId = 1;
        int level = this.MaxDistance + 1 & 0xFFFFFFFE;
        final int expandIters = 8;
        if (borderSize > 0) {
            final int bw = Math.min(w, borderSize);
            final int bh = Math.min(h, borderSize);
            this.PaintRectRegion(0, bw, 0, h, regionId | CompactHeightfield.BorderReg, srcReg);
            ++regionId;
            this.PaintRectRegion(w - bw, w, 0, h, regionId | CompactHeightfield.BorderReg, srcReg);
            ++regionId;
            this.PaintRectRegion(0, w, 0, bh, regionId | CompactHeightfield.BorderReg, srcReg);
            ++regionId;
            this.PaintRectRegion(0, w, h - bh, h, regionId | CompactHeightfield.BorderReg, srcReg);
            ++regionId;
            this.BorderSize = borderSize;
        }
        while (level > 0) {
            level = ((level >= 2) ? (level - 2) : 0);
            if (this.ExpandRegions(expandIters, level, srcReg, srcDist, dstReg, dstDist, stack) != srcReg) {
                int[] at = srcReg;
                srcReg = dstReg;
                dstReg = at;
                at = srcDist;
                srcDist = dstDist;
                dstDist = at;
            }
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    final CompactCell c = this.Cells[x + y * w];
                    for (int i = (int)c.Index, ni = (int)(c.Index + c.Count); i < ni; ++i) {
                        if (this.Dist[i] >= level && srcReg[i] == 0) {
                            if (this.Areas[i] != HeightField.NullArea) {
                                if (this.FloodRegion(x, y, i, level, regionId, srcReg, srcDist, stack)) {
                                    ++regionId;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.ExpandRegions(expandIters * 8, 0, srcReg, srcDist, dstReg, dstDist, stack) != srcReg) {
            int[] t = srcReg;
            srcReg = dstReg;
            dstReg = t;
            t = srcDist;
            srcDist = dstDist;
            dstDist = t;
        }
        this.MaxRegions = regionId;
        if (!this.FilterSmallRegions(minRegionArea, mergeRegionArea, srcReg)) {
            return false;
        }
        for (int j = 0; j < this.SpanCount; ++j) {
            this.Spans[j].Reg = srcReg[j];
        }
        return true;
    }
    
    private Boolean FilterSmallRegions(final int minRegionArea, final int mergeRegionArea, final int[] srcReg) {
        final int w = this.Width;
        final int h = this.Height;
        final int nreg = this.MaxRegions + 1;
        final Region[] regions = new Region[nreg];
        for (int i = 0; i < nreg; ++i) {
            regions[i] = new Region(i);
        }
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int j = (int)c.Index, ni = (int)(c.Index + c.Count); j < ni; ++j) {
                    final int r = srcReg[j];
                    if (r > 0) {
                        if (r < nreg) {
                            final Region region;
                            final Region reg = region = regions[r];
                            ++region.SpanCount;
                            for (int k = (int)c.Index; k < ni; ++k) {
                                if (j != k) {
                                    final int floorId = srcReg[k];
                                    if (floorId > 0) {
                                        if (floorId < nreg) {
                                            this.AddUniqueFloorRegion(reg, floorId);
                                        }
                                    }
                                }
                            }
                            if (reg.Connections.Size <= 0) {
                                reg.AreaType = (short)this.Areas[j];
                                int ndir = -1;
                                for (int dir = 0; dir < 4; ++dir) {
                                    if (this.IsSolidEdge(srcReg, x, y, j, dir)) {
                                        ndir = dir;
                                        break;
                                    }
                                }
                                if (ndir != -1) {
                                    this.WalkContour(x, y, j, ndir, srcReg, reg);
                                }
                            }
                        }
                    }
                }
            }
        }
        final IntArray stack = new IntArray(32);
        final IntArray trace = new IntArray(32);
        for (int l = 0; l < nreg; ++l) {
            final Region reg2 = regions[l];
            if (reg2.Id > 0) {
                if ((reg2.Id & CompactHeightfield.BorderReg) == 0x0) {
                    if (reg2.SpanCount != 0) {
                        if (!reg2.Visited) {
                            Boolean connectsToBorder = false;
                            int spanCount = 0;
                            stack.Resize(0);
                            trace.Resize(0);
                            reg2.Visited = true;
                            stack.Push(l);
                            while (stack.Size > 0) {
                                final int ri = stack.Pop();
                                final Region creg = regions[ri];
                                spanCount += creg.SpanCount;
                                trace.Push(ri);
                                for (int m = 0; m < creg.Connections.Size; ++m) {
                                    if ((creg.Connections.get(m) & CompactHeightfield.BorderReg) != 0x0) {
                                        connectsToBorder = true;
                                    }
                                    else {
                                        final Region neireg = regions[creg.Connections.get(m)];
                                        if (!neireg.Visited) {
                                            if (neireg.Id > 0) {
                                                if ((neireg.Id & CompactHeightfield.BorderReg) == 0x0) {
                                                    stack.Push(neireg.Id);
                                                    neireg.Visited = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (spanCount < minRegionArea && !connectsToBorder) {
                                for (int j2 = 0; j2 < trace.Size; ++j2) {
                                    regions[trace.get(j2)].SpanCount = 0;
                                    regions[trace.get(j2)].Id = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
        int mergeCount = 0;
        do {
            mergeCount = 0;
            for (final Region reg3 : regions) {
                if (reg3.Id > 0) {
                    if ((reg3.Id & CompactHeightfield.BorderReg) == 0x0) {
                        if (reg3.SpanCount != 0) {
                            if (reg3.SpanCount <= mergeRegionArea || !this.IsRegionConnectedToBorder(reg3)) {
                                int smallest = Integer.MAX_VALUE;
                                int mergeId = reg3.Id;
                                for (int k = 0; k < reg3.Connections.Size; ++k) {
                                    if ((reg3.Connections.get(k) & CompactHeightfield.BorderReg) == 0x0) {
                                        final Region mreg = regions[reg3.Connections.get(k)];
                                        if (mreg.Id > 0) {
                                            if ((mreg.Id & CompactHeightfield.BorderReg) == 0x0) {
                                                if (mreg.SpanCount < smallest && this.CanMergeWithRegion(reg3, mreg) && this.CanMergeWithRegion(mreg, reg3)) {
                                                    smallest = mreg.SpanCount;
                                                    mergeId = mreg.Id;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (mergeId != reg3.Id) {
                                    final int oldId = reg3.Id;
                                    final Region target = regions[mergeId];
                                    if (this.MergeRegions(target, reg3)) {
                                        for (int j3 = 0; j3 < nreg; ++j3) {
                                            if (regions[j3].Id != 0) {
                                                if ((regions[j3].Id & CompactHeightfield.BorderReg) == 0x0) {
                                                    if (regions[j3].Id == oldId) {
                                                        regions[j3].Id = mergeId;
                                                    }
                                                    final Region reg4 = regions[j3];
                                                    this.ReplaceNeighbor(reg4, oldId, mergeId);
                                                }
                                            }
                                        }
                                        ++mergeCount;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (mergeCount > 0);
        for (int j = 0; j < nreg; ++j) {
            regions[j].Remap = false;
            if (regions[j].Id != 0) {
                if ((regions[j].Id & CompactHeightfield.BorderReg) == 0x0) {
                    regions[j].Remap = true;
                }
            }
        }
        int regIdGen = 0;
        for (int i2 = 0; i2 < nreg; ++i2) {
            if (regions[i2].Remap) {
                final int oldId2 = regions[i2].Id;
                final int newId = ++regIdGen;
                for (int k = i2; k < nreg; ++k) {
                    if (regions[k].Id == oldId2) {
                        regions[k].Id = newId;
                        regions[k].Remap = false;
                    }
                }
            }
        }
        this.MaxRegions = regIdGen;
        for (int i2 = 0; i2 < this.SpanCount; ++i2) {
            if ((srcReg[i2] & CompactHeightfield.BorderReg) == 0x0) {
                srcReg[i2] = regions[srcReg[i2]].Id;
            }
        }
        return true;
    }
    
    private Boolean MergeRegions(final Region rega, final Region regb) {
        final int aid = rega.Id;
        final int bid = regb.Id;
        final IntArray acon = new IntArray(rega.Connections.Size);
        for (int i = 0; i < rega.Connections.Size; ++i) {
            acon.set(i, rega.Connections.get(i));
        }
        final IntArray bcon = regb.Connections;
        int insa = -1;
        for (int j = 0; j < acon.Size; ++j) {
            if (acon.get(j) == bid) {
                insa = j;
                break;
            }
        }
        if (insa == -1) {
            return false;
        }
        int insb = -1;
        for (int k = 0; k < bcon.Size; ++k) {
            if (bcon.get(k) == aid) {
                insb = k;
                break;
            }
        }
        if (insb == -1) {
            return false;
        }
        rega.Connections.Resize(0);
        for (int k = 0, ni = acon.Size; k < ni - 1; ++k) {
            rega.Connections.Push(acon.get((insa + 1 + k) % ni));
        }
        for (int k = 0, ni = bcon.Size; k < ni - 1; ++k) {
            rega.Connections.Push(bcon.get((insb + 1 + k) % ni));
        }
        this.RemoveAdjacentNeighbors(rega);
        for (int l = 0; l < regb.Floors.Size; ++l) {
            this.AddUniqueFloorRegion(rega, regb.Floors.get(l));
        }
        rega.SpanCount += regb.SpanCount;
        regb.SpanCount = 0;
        regb.Connections.Resize(0);
        return true;
    }
    
    private void ReplaceNeighbor(final Region reg, final int oldId, final int newId) {
        Boolean neiChanged = false;
        for (int i = 0; i < reg.Connections.Size; ++i) {
            if (reg.Connections.get(i) == oldId) {
                reg.Connections.set(i, newId);
                neiChanged = true;
            }
        }
        for (int i = 0; i < reg.Floors.Size; ++i) {
            if (reg.Floors.get(i) == oldId) {
                reg.Floors.set(i, newId);
            }
        }
        if (neiChanged) {
            this.RemoveAdjacentNeighbors(reg);
        }
    }
    
    private void RemoveAdjacentNeighbors(final Region reg) {
        int i = 0;
        while (i < reg.Connections.Size && reg.Connections.Size > 1) {
            final int ni = (i + 1) % reg.Connections.Size;
            if (reg.Connections.get(i) == reg.Connections.get(ni)) {
                for (int j = 0; j < reg.Connections.Size - 1; ++j) {
                    reg.Connections.set(j, reg.Connections.get(j + 1));
                }
                reg.Connections.Pop();
            }
            else {
                ++i;
            }
        }
    }
    
    private Boolean CanMergeWithRegion(final Region rega, final Region regb) {
        if (rega.AreaType != regb.AreaType) {
            return false;
        }
        int n = 0;
        for (int i = 0; i < rega.Connections.Size; ++i) {
            if (rega.Connections.get(i) == regb.Id) {
                ++n;
            }
        }
        if (n > 1) {
            return false;
        }
        for (int i = 0; i < rega.Floors.Size; ++i) {
            if (rega.Floors.get(i) == regb.Id) {
                return false;
            }
        }
        return true;
    }
    
    private Boolean IsRegionConnectedToBorder(final Region reg) {
        for (int i = 0; i < reg.Connections.Size; ++i) {
            if (reg.Connections.get(i) == 0) {
                return true;
            }
        }
        return false;
    }
    
    private void WalkContour(int x, int y, int i, int dir, final int[] srcReg, final Region cont) {
        final int startDir = dir;
        final int starti = i;
        final CompactSpan ss = this.Spans[i];
        int curReg = 0;
        if (ss.GetCon(dir) != CompactHeightfield.NotConnected) {
            final int ax = x + Helper.GetDirOffsetX(dir);
            final int ay = y + Helper.GetDirOffsetY(dir);
            final int ai = (int)this.Cells[ax + ay * this.Width].Index + ss.GetCon(dir);
            curReg = srcReg[ai];
        }
        cont.Connections.Push(curReg);
        int iter = 0;
        while (++iter < 40000) {
            final CompactSpan s = this.Spans[i];
            if (this.IsSolidEdge(srcReg, x, y, i, dir)) {
                int r = 0;
                if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                    final int ax2 = x + Helper.GetDirOffsetX(dir);
                    final int ay2 = y + Helper.GetDirOffsetY(dir);
                    final int ai2 = (int)this.Cells[ax2 + ay2 * this.Width].Index + s.GetCon(dir);
                    r = srcReg[ai2];
                }
                if (r != curReg) {
                    curReg = r;
                    cont.Connections.Push(curReg);
                }
                dir = (dir + 1 & 0x3);
            }
            else {
                int ni = -1;
                final int nx = x + Helper.GetDirOffsetX(dir);
                final int ny = y + Helper.GetDirOffsetY(dir);
                if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                    final CompactCell nc = this.Cells[nx + ny * this.Width];
                    ni = (int)nc.Index + s.GetCon(dir);
                }
                if (ni == -1) {
                    return;
                }
                x = nx;
                y = ny;
                i = ni;
                dir = (dir + 3 & 0x3);
            }
            if (starti == i && startDir == dir) {
                break;
            }
        }
        if (cont.Connections.Size > 1) {
            int j = 0;
            while (j < cont.Connections.Size) {
                final int nj = (j + 1) % cont.Connections.Size;
                if (cont.Connections.get(j) == cont.Connections.get(nj)) {
                    for (int k = j; k < cont.Connections.Size - 1; ++k) {
                        cont.Connections.set(k, cont.Connections.get(k + 1));
                    }
                    cont.Connections.Pop();
                }
                else {
                    ++j;
                }
            }
        }
    }
    
    private Boolean IsSolidEdge(final int[] srcReg, final int x, final int y, final int i, final int dir) {
        final CompactSpan s = this.Spans[i];
        int r = 0;
        if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
            final int ax = x + Helper.GetDirOffsetX(dir);
            final int ay = y + Helper.GetDirOffsetY(dir);
            final int ai = (int)this.Cells[ax + ay * this.Width].Index + s.GetCon(dir);
            r = srcReg[ai];
        }
        if (r == srcReg[i]) {
            return false;
        }
        return true;
    }
    
    private void AddUniqueFloorRegion(final Region reg, final int n) {
        for (int i = 0; i < reg.Floors.Size; ++i) {
            if (reg.Floors.get(i) == n) {
                return;
            }
        }
        reg.Floors.Push(n);
    }
    
    private Boolean FloodRegion(final int x, final int y, final int i, final int level, final int r, final int[] srcReg, final int[] srcDist, final IntArray stack) {
        final int w = this.Width;
        final long area = this.Areas[i];
        stack.Resize(0);
        stack.Push(x);
        stack.Push(y);
        stack.Push(i);
        srcReg[i] = r;
        srcDist[i] = 0;
        final int lev = (level >= 2) ? (level - 2) : 0;
        int count = 0;
        while (stack.Size > 0) {
            final int ci = stack.Pop();
            final int cy = stack.Pop();
            final int cx = stack.Pop();
            final CompactSpan cs = this.Spans[ci];
            int ar = 0;
            for (int dir = 0; dir < 4; ++dir) {
                if (cs.GetCon(dir) != CompactHeightfield.NotConnected) {
                    final int ax = cx + Helper.GetDirOffsetX(dir);
                    final int ay = cy + Helper.GetDirOffsetY(dir);
                    final int ai = (int)this.Cells[ax + ay * w].Index + cs.GetCon(dir);
                    if (this.Areas[ai] == area) {
                        final int nr = srcReg[ai];
                        if ((nr & CompactHeightfield.BorderReg) == 0x0) {
                            if (nr != 0 && nr != r) {
                                ar = nr;
                            }
                            final CompactSpan aspan = this.Spans[ai];
                            final int dir2 = dir + 1 & 0x3;
                            if (aspan.GetCon(dir2) != CompactHeightfield.NotConnected) {
                                final int ax2 = ax + Helper.GetDirOffsetX(dir2);
                                final int ay2 = ay + Helper.GetDirOffsetY(dir2);
                                final int ai2 = (int)this.Cells[ax2 + ay2 * w].Index + aspan.GetCon(dir2);
                                if (this.Areas[ai2] == area) {
                                    final int nr2 = srcReg[ai2];
                                    if (nr2 != 0 && nr2 != r) {
                                        ar = nr2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (ar != 0) {
                srcReg[ci] = 0;
            }
            else {
                ++count;
                for (int dir = 0; dir < 4; ++dir) {
                    if (cs.GetCon(dir) != CompactHeightfield.NotConnected) {
                        final int ax = cx + Helper.GetDirOffsetX(dir);
                        final int ay = cy + Helper.GetDirOffsetY(dir);
                        final int ai = (int)this.Cells[ax + ay * w].Index + cs.GetCon(dir);
                        if (this.Areas[ai] == area) {
                            if (this.Dist[ai] >= lev && srcReg[ai] == 0) {
                                srcReg[ai] = r;
                                srcDist[ai] = 0;
                                stack.Push(ax);
                                stack.Push(ay);
                                stack.Push(ai);
                            }
                        }
                    }
                }
            }
        }
        return count > 0;
    }
    
    private int[] ExpandRegions(final int maxIter, final int level, int[] srcReg, int[] srcDist, int[] dstReg, int[] dstDist, final IntArray stack) {
        final int w = this.Width;
        final int h = this.Height;
        stack.Resize(0);
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int i = (int)c.Index, ni = (int)(c.Index + c.Count); i < ni; ++i) {
                    if (this.Dist[i] >= level && srcReg[i] == 0 && this.Areas[i] != HeightField.NullArea) {
                        stack.Push(x);
                        stack.Push(y);
                        stack.Push(i);
                    }
                }
            }
        }
        int iter = 0;
        while (stack.Size > 0) {
            int failed = 0;
            System.arraycopy(srcReg, 0, dstReg, 0, 4 * this.SpanCount);
            System.arraycopy(srcDist, 0, dstDist, 0, 4 * this.SpanCount);
            for (int j = 0; j < stack.Size; j += 3) {
                final int x2 = stack.get(j + 0);
                final int y2 = stack.get(j + 1);
                final int k = stack.get(j + 2);
                if (k < 0) {
                    ++failed;
                }
                else {
                    int r = srcReg[k];
                    int d2 = 32767;
                    final long area = this.Areas[k];
                    final CompactSpan s = this.Spans[k];
                    for (int dir = 0; dir < 4; ++dir) {
                        if (s.GetCon(dir) != CompactHeightfield.NotConnected) {
                            final int ax = x2 + Helper.GetDirOffsetX(dir);
                            final int ay = y2 + Helper.GetDirOffsetY(dir);
                            final int ai = (int)this.Cells[ax + ay * w].Index + s.GetCon(dir);
                            if (this.Areas[ai] == area) {
                                if (srcReg[ai] > 0 && (srcReg[ai] & CompactHeightfield.BorderReg) == 0x0 && srcDist[ai] + 2 < d2) {
                                    r = srcReg[ai];
                                    d2 = srcDist[ai] + 2;
                                }
                            }
                        }
                    }
                    if (r != 0) {
                        stack.set(j + 2, -1);
                        dstReg[k] = r;
                        dstDist[k] = d2;
                    }
                    else {
                        ++failed;
                    }
                }
            }
            int[] temp = srcReg;
            srcReg = dstReg;
            dstReg = temp;
            temp = srcDist;
            srcDist = dstDist;
            dstDist = temp;
            if (failed * 3 == stack.Size) {
                break;
            }
            if (level > 0 && ++iter >= maxIter) {
                break;
            }
        }
        return srcReg;
    }
    
    private void PaintRectRegion(final int minx, final int maxx, final int miny, final int maxy, final int regId, final int[] srcReg) {
        final int w = this.Width;
        for (int y = miny; y < maxy; ++y) {
            for (int x = minx; x < maxx; ++x) {
                final CompactCell c = this.Cells[x + y * w];
                for (int i = (int)c.Index, ni = (int)(c.Index + c.Count); i < ni; ++i) {
                    if (this.Areas[i] != HeightField.NullArea) {
                        srcReg[i] = regId;
                    }
                }
            }
        }
    }
    
    static {
        CompactHeightfield.NotConnected = 63;
        CompactHeightfield.BorderReg = 32768;
    }
}
