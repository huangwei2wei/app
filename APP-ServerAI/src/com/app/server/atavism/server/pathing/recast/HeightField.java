// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.recast;

import java.util.List;

public class HeightField
{
    public int Width;
    public int Height;
    public float[] Bmin;
    public float[] Bmax;
    public float Cs;
    public float Ch;
    public Span[] Spans;
    public SpanPool Pools;
    public Span Freelist;
    protected float[] d;
    public static long NullArea;
    
    public HeightField(final int width, final int height, final float[] bmin, final float[] bmax, final float cs, final float ch) {
        this.Bmin = new float[3];
        this.Bmax = new float[3];
        this.d = new float[12];
        this.Width = width;
        this.Height = height;
        System.arraycopy(bmin, 0, this.Bmin, 0, 3);
        System.arraycopy(bmax, 0, this.Bmax, 0, 3);
        this.Cs = cs;
        this.Ch = ch;
        this.Spans = new Span[width * height];
    }
    
    public void RasterizeTriangles(final Geometry geom, final short[] areas, final int flagMergeThr) {
        this.RasterizeTriangles(geom, geom.Triangles, geom.NumTriangles, areas, flagMergeThr);
    }
    
    public void RasterizeTriangles(final Geometry geom, final List<Integer> triangles, final int numTriangles, final short[] areas, final int flagMergeThr) {
        final float ics = 1.0f / this.Cs;
        final float ich = 1.0f / this.Ch;
        for (int i = 0; i < numTriangles; ++i) {
            final int v0 = triangles.get(i * 3 + 0);
            final int v2 = triangles.get(i * 3 + 1);
            final int v3 = triangles.get(i * 3 + 2);
            this.RasterizeTriangle(geom.Vertexes, v0, v2, v3, areas[i], ics, ich, flagMergeThr);
        }
    }
    
    private void RasterizeTriangle(final List<RecastVertex> vertexes, final int v0, final int v1, final int v2, final short area, final float ics, final float ich, final int flagMergeThr) {
        final int w = this.Width;
        final int h = this.Height;
        final float by = this.Bmax[1] - this.Bmin[1];
        RecastVertex tempMin = new RecastVertex(vertexes.get(v0));
        RecastVertex tempMax = new RecastVertex(vertexes.get(v0));
        tempMin = RecastVertex.Min(tempMin, vertexes.get(v1));
        tempMin = RecastVertex.Min(tempMin, vertexes.get(v2));
        tempMax = RecastVertex.Max(tempMax, vertexes.get(v1));
        tempMax = RecastVertex.Max(tempMax, vertexes.get(v2));
        if (!this.OverlapBounds(this.Bmin, this.Bmax, tempMin.ToArray(), tempMax.ToArray())) {
            return;
        }
        int x0 = (int)((tempMin.getX() - this.Bmin[0]) * ics);
        int y0 = (int)((tempMin.getZ() - this.Bmin[2]) * ics);
        int x2 = (int)((tempMin.getX() - this.Bmin[0]) * ics);
        int y2 = (int)((tempMin.getZ() - this.Bmin[2]) * ics);
        x0 = Math.max(0, Math.min(x0, w - 1));
        y0 = Math.max(0, Math.min(y0, h - 1));
        x2 = Math.max(0, Math.min(x2, w - 1));
        y2 = Math.max(0, Math.min(y2, h - 1));
        final float[] inArray = new float[21];
        final float[] outArray = new float[21];
        final float[] inrowArray = new float[21];
        for (int y3 = y0; y3 <= y2; ++y3) {
            System.arraycopy(vertexes.get(v0).ToArray(), 0, inArray, 0, 3);
            System.arraycopy(vertexes.get(v1).ToArray(), 0, inArray, 3, 3);
            System.arraycopy(vertexes.get(v2).ToArray(), 0, inArray, 6, 3);
            int nvrow = 3;
            final float cz = this.Bmin[2] + y3 * this.Cs;
            nvrow = this.ClipPoly(inArray, nvrow, outArray, 0, 1, -cz);
            if (nvrow >= 3) {
                nvrow = this.ClipPoly(outArray, nvrow, inrowArray, 0, -1, cz + this.Cs);
                if (nvrow >= 3) {
                    for (int x3 = x0; x3 <= x2; ++x3) {
                        int nv = nvrow;
                        final float cx = this.Bmin[0] + x3 * this.Cs;
                        nv = this.ClipPoly(inrowArray, nv, outArray, 1, 0, -cx);
                        if (nv >= 3) {
                            nv = this.ClipPoly(outArray, nv, inArray, -1, 0, cx + this.Cs);
                            if (nv >= 3) {
                                float smin = inArray[1];
                                float smax = inArray[1];
                                for (int i = 1; i < nv; ++i) {
                                    smin = Math.min(smin, inArray[i * 3 + 1]);
                                    smax = Math.max(smax, inArray[i * 3 + 1]);
                                }
                                smin -= this.Bmin[1];
                                smax -= this.Bmin[1];
                                if (smax >= 0.0f) {
                                    if (smin <= by) {
                                        if (smin < 0.0f) {
                                            smin = 0.0f;
                                        }
                                        if (smax > by) {
                                            smax = by;
                                        }
                                        final int ismin = Math.max(0, Math.min((int)Math.floor(smin * ich), 32767));
                                        final int ismax = Math.max(ismin + 1, Math.min((int)Math.floor(smax * ich), 32767));
                                        this.AddSpan(x3, y3, ismin, ismax, area, flagMergeThr);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void AddSpan(final int x, final int y, final int smin, final int smax, final short area, final int flagMergeThr) {
        final int idx = x + y * this.Width;
        final Span s = this.AllocSpan();
        s.SMin = smin;
        s.SMax = smax;
        s.Area = area;
        s.Next = null;
        if (this.Spans[idx] == null) {
            this.Spans[idx] = s;
            return;
        }
        Span prev = null;
        Span cur = this.Spans[idx];
        while (cur != null && cur.SMin <= s.SMax) {
            if (cur.SMax < s.SMin) {
                prev = cur;
                cur = cur.Next;
            }
            else {
                if (cur.SMin < s.SMin) {
                    s.SMin = cur.SMin;
                }
                if (cur.SMax > s.SMax) {
                    s.SMax = cur.SMax;
                }
                if (Math.abs((int)s.SMax - (int)cur.SMax) <= flagMergeThr) {
                    s.Area = Math.max(s.Area, cur.Area);
                }
                final Span next = cur.Next;
                this.FreeSpan(cur);
                if (prev != null) {
                    prev.Next = next;
                }
                else {
                    this.Spans[idx] = next;
                }
                cur = next;
            }
        }
        if (prev != null) {
            s.Next = prev.Next;
            prev.Next = s;
        }
        else {
            s.Next = this.Spans[idx];
            this.Spans[idx] = s;
        }
    }
    
    private void FreeSpan(final Span ptr) {
        if (ptr == null) {
            return;
        }
        ptr.Next = this.Freelist;
        this.Freelist = ptr;
    }
    
    private Span AllocSpan() {
        if (this.Freelist == null) {
            this.Freelist = new Span();
        }
        final Span it = this.Freelist;
        this.Freelist = this.Freelist.Next;
        return it;
    }
    
    private int ClipPoly(final float[] inArray, final int n, final float[] outArray, final int pnx, final int pnz, final float pd) {
        for (int i = 0; i < n; ++i) {
            this.d[i] = pnx * inArray[i * 3 + 0] + pnz * inArray[i * 3 + 2] + pd;
        }
        int m = 0;
        int j = 0;
        int k = n - 1;
        while (j < n) {
            final Boolean ina = this.d[k] >= 0.0f;
            final Boolean inb = this.d[j] >= 0.0f;
            if (ina != inb) {
                final float s = this.d[k] / (this.d[k] - this.d[j]);
                outArray[m * 3 + 0] = inArray[k * 3 + 0] + (inArray[j * 3 + 0] - inArray[k * 3 + 0]) * s;
                outArray[m * 3 + 1] = inArray[k * 3 + 1] + (inArray[j * 3 + 1] - inArray[k * 3 + 1]) * s;
                outArray[m * 3 + 2] = inArray[k * 3 + 2] + (inArray[j * 3 + 2] - inArray[k * 3 + 2]) * s;
                ++m;
            }
            if (inb) {
                outArray[m * 3 + 0] = inArray[j * 3 + 0];
                outArray[m * 3 + 1] = inArray[j * 3 + 1];
                outArray[m * 3 + 2] = inArray[j * 3 + 2];
                ++m;
            }
            k = j;
            ++j;
        }
        return m;
    }
    
    private Boolean OverlapBounds(final float[] amin, final float[] amax, final float[] bmin, final float[] bmax) {
        Boolean overlap = true;
        overlap = (amin[0] <= bmax[0] && amax[0] >= bmin[0] && overlap);
        overlap = (amin[1] <= bmax[1] && amax[1] >= bmin[1] && overlap);
        overlap = (amin[2] <= bmax[2] && amax[2] >= bmin[2] && overlap);
        return overlap;
    }
    
    public void FilterLowHangingWalkableObstacles(final int walkableClimb) {
        final int w = this.Width;
        for (int h = this.Height, y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                Span ps = null;
                Boolean previousWalkable = false;
                long previousArea = HeightField.NullArea;
                for (Span s = this.Spans[x + y * w]; s != null; s = s.Next) {
                    final Boolean walkable = s.Area != HeightField.NullArea;
                    if (!walkable && previousWalkable && Math.abs((int)s.SMax - (int)ps.SMax) <= walkableClimb) {
                        s.Area = previousArea;
                    }
                    previousWalkable = walkable;
                    previousArea = s.Area;
                    ps = s;
                }
            }
        }
    }
    
    public void FilterLedgeSpans(final int walkableHeight, final int walkableClimb) {
        final int w = this.Width;
        final int h = this.Height;
        final int MaxHeight = 65535;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                for (Span s = this.Spans[x + y * w]; s != null; s = s.Next) {
                    if (s.Area != HeightField.NullArea) {
                        final int bot = (int)s.SMax;
                        final int top = (s.Next != null) ? ((int)s.Next.SMin) : MaxHeight;
                        int minh = MaxHeight;
                        int asmin = (int)s.SMax;
                        int asmax = (int)s.SMax;
                        for (int dir = 0; dir < 4; ++dir) {
                            final int dx = x + Helper.GetDirOffsetX(dir);
                            final int dy = y + Helper.GetDirOffsetY(dir);
                            if (dx < 0 || dy < 0 || dx >= w || dy >= h) {
                                minh = Math.min(minh, -walkableClimb - bot);
                            }
                            else {
                                Span ns = this.Spans[dx + dy * w];
                                int nbot = -walkableClimb;
                                int ntop = (ns != null) ? ((int)ns.SMin) : MaxHeight;
                                if (Math.min(top, ntop) - Math.max(bot, nbot) > walkableHeight) {
                                    minh = Math.min(minh, nbot - bot);
                                }
                                for (ns = this.Spans[dx + dy * w]; ns != null; ns = ns.Next) {
                                    nbot = (int)ns.SMax;
                                    ntop = ((ns.Next != null) ? ((int)ns.Next.SMin) : MaxHeight);
                                    if (Math.min(top, ntop) - Math.max(bot, nbot) > walkableHeight) {
                                        minh = Math.min(minh, nbot - bot);
                                        if (Math.abs(nbot - bot) <= walkableClimb) {
                                            if (nbot < asmin) {
                                                asmin = nbot;
                                            }
                                            if (nbot > asmax) {
                                                asmax = nbot;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (minh < -walkableClimb) {
                            s.Area = HeightField.NullArea;
                        }
                        if (asmax - asmin > walkableClimb) {
                            s.Area = HeightField.NullArea;
                        }
                    }
                }
            }
        }
    }
    
    public void FilterWalkableLowHeightSpans(final int walkableHeight) {
        final int w = this.Width;
        final int h = this.Height;
        final int MaxHeight = 65535;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                for (Span s = this.Spans[x + y * w]; s != null; s = s.Next) {
                    final int bot = (int)s.SMax;
                    final int top = (s.Next != null) ? ((int)s.Next.SMin) : MaxHeight;
                    if (top - bot <= walkableHeight) {
                        s.Area = HeightField.NullArea;
                    }
                }
            }
        }
    }
    
    public int GetHeightFieldSpanCount() {
        final int w = this.Width;
        final int h = this.Height;
        int spanCount = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                for (Span s = this.Spans[x + y * w]; s != null; s = s.Next) {
                    if (s.Area != HeightField.NullArea) {
                        ++spanCount;
                    }
                }
            }
        }
        return spanCount;
    }
    
    static {
        HeightField.NullArea = 0L;
    }
}
