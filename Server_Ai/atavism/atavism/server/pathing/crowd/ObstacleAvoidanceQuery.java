// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

import atavism.server.objects.Vector2;
import atavism.server.pathing.recast.Helper;

public class ObstacleAvoidanceQuery
{
    private ObstacleAvoidanceParams _params;
    private float _invHorizTime;
    private float _vmax;
    private float _invVmax;
    private int _maxCircles;
    private ObstacleCircle[] _circles;
    private int _nCircles;
    private int _maxSegments;
    private ObstacleSegment[] _segments;
    private int _nSegments;
    private final int MaxPatternDivs = 32;
    private final int MaxPatternRings = 4;
    
    public ObstacleAvoidanceQuery() {
        this._maxCircles = 0;
        this._circles = null;
        this._nCircles = 0;
        this._maxSegments = 0;
        this._segments = null;
        this._nSegments = 0;
    }
    
    public Boolean Init(final int maxCircles, final int maxSegments) {
        this._maxCircles = maxCircles;
        this._nCircles = 0;
        this._circles = new ObstacleCircle[this._maxCircles];
        for (int i = 0; i < this._maxCircles; ++i) {
            this._circles[i] = new ObstacleCircle();
        }
        this._maxSegments = maxSegments;
        this._nSegments = 0;
        this._segments = new ObstacleSegment[this._maxSegments];
        for (int i = 0; i < this._maxSegments; ++i) {
            this._segments[i] = new ObstacleSegment();
        }
        return true;
    }
    
    public void Reset() {
        this._nCircles = 0;
        this._nSegments = 0;
    }
    
    private void Prepare(final float[] pos, final float[] dvel) {
        for (int i = 0; i < this._nCircles; ++i) {
            final ObstacleCircle cir = this._circles[i];
            final float[] pb = cir.p;
            final float[] orig = { 0.0f, 0.0f, 0.0f };
            float[] dv = new float[3];
            cir.dp = Helper.VSub(pb[0], pb[1], pb[2], pos[0], pos[1], pos[2]);
            cir.dp = Helper.VNormalize(cir.dp);
            dv = Helper.VSub(cir.dvel[0], cir.dvel[1], cir.dvel[2], dvel[0], dvel[1], dvel[2]);
            final float a = Helper.TriArea2D(orig, cir.dp, dv);
            if (a < 0.01f) {
                cir.np[0] = -cir.dp[2];
                cir.np[2] = cir.dp[0];
            }
            else {
                cir.np[0] = cir.dp[2];
                cir.np[2] = -cir.dp[0];
            }
        }
        for (int i = 0; i < this._nSegments; ++i) {
            final ObstacleSegment seg = this._segments[i];
            final float r = 0.01f;
            seg.touch = (Helper.DistancePtSegSqr2D(pos[0], pos[1], pos[2], seg.p[0], seg.p[1], seg.p[2], seg.q[0], seg.q[1], seg.q[2]).x < r * r);
        }
    }
    
    public int SweetCircleCircle(final float[] c0, final float r0, final float[] v, final float[] c1, final float r1, final Vector2 htMinMax) {
        final float EPS = 0.001f;
        final float[] s = Helper.VSub(c1[0], c1[1], c1[2], c0[0], c0[1], c0[2]);
        final float r2 = r0 + r1;
        final float c2 = Helper.VDot2D(s, s) - r2 * r2;
        float a = Helper.VDot2D(v, v);
        if (a < EPS) {
            return 0;
        }
        final float b = Helper.VDot2D(v, s);
        final float d = b * b - a * c2;
        if (d < 0.0f) {
            return 0;
        }
        a = 1.0f / a;
        final float rd = (float)Math.sqrt(d);
        htMinMax.x = (b - rd) * a;
        htMinMax.y = (b + rd) * a;
        return 1;
    }
    
    public Vector2 isectRaySeg(final float[] ap, final float[] u, final float[] bp, final float[] bq) {
        final Vector2 raySeg = new Vector2();
        final float[] v = Helper.VSub(bq[0], bq[1], bq[2], bp[0], bp[1], bp[2]);
        final float[] w = Helper.VSub(ap[0], ap[1], ap[2], bp[0], bp[1], bp[2]);
        float d = Helper.VPerp2D(u, v);
        if (Math.abs(d) < 1.0E-6f) {
            raySeg.x = 0.0;
            return raySeg;
        }
        d = 1.0f / d;
        raySeg.y = Helper.VPerp2D(v, w) * d;
        if (raySeg.y < 0.0 || raySeg.y > 1.0) {
            raySeg.x = 0.0;
            return raySeg;
        }
        final float s = Helper.VPerp2D(u, w) * d;
        if (s < 0.0f || s > 1.0f) {
            raySeg.x = 0.0;
            return raySeg;
        }
        raySeg.x = 1.0;
        return raySeg;
    }
    
    private float ProcessSample(final float[] vcand, final float cs, final float[] pos, final float rad, final float[] vel, final float[] dvel) {
        return this.ProcessSample(vcand, cs, pos, rad, vel, dvel, null);
    }
    
    private float ProcessSample(final float[] vcand, final float cs, final float[] pos, final float rad, final float[] vel, final float[] dvel, final ObstacleAvoidanceDebugData debug) {
        float tmin = this._params.horizTime;
        float side = 0.0f;
        int nside = 0;
        for (int i = 0; i < this._nCircles; ++i) {
            final ObstacleCircle cir = this._circles[i];
            float[] vab = Helper.VScale(vcand[0], vcand[1], vcand[2], 2.0f);
            vab = Helper.VSub(vab[0], vab[1], vab[2], vel[0], vel[1], vel[2]);
            side += (float)Math.max(0.0, Math.min(1.0, Math.min(Helper.VDot2D(cir.dp, vab) * 0.5f + 0.5f, Helper.VDot2D(cir.np, vab) * 2.0f)));
            ++nside;
            final Vector2 htMinMax = new Vector2();
            if (this.SweetCircleCircle(pos, rad, vab, cir.p, cir.rad, htMinMax) != 0) {
                float htmin = (float)htMinMax.x;
                final float htmax = (float)htMinMax.y;
                if (htmin < 0.0f && htmax > 0.0f) {
                    htmin = -htmin * 0.5f;
                }
                if (htmin >= 0.0f && htmin < tmin) {
                    tmin = htmin;
                }
            }
        }
        for (int i = 0; i < this._nSegments; ++i) {
            final ObstacleSegment seg = this._segments[i];
            float htmin2 = 0.0f;
            if (seg.touch) {
                final float[] sdir = Helper.VSub(seg.q[0], seg.q[1], seg.q[2], seg.p[0], seg.p[1], seg.p[2]);
                final float[] snorm = { -sdir[2], 0.0f, sdir[0] };
                if (Helper.VDot2D(snorm, vcand) < 0.0f) {
                    continue;
                }
                htmin2 = 0.0f;
            }
            else {
                final Vector2 raySeg = this.isectRaySeg(pos, vcand, seg.p, seg.q);
                htmin2 = (float)raySeg.y;
                if (raySeg.x == 0.0) {
                    continue;
                }
            }
            htmin2 *= 2.0f;
            if (htmin2 < tmin) {
                tmin = htmin2;
            }
        }
        if (nside > 0) {
            side /= nside;
        }
        final float vpen = this._params.weightDesVel * (Helper.VDist2D(vcand, dvel) * this._invVmax);
        final float vcpen = this._params.weightCurVel * (Helper.VDist2D(vcand, vel) * this._invVmax);
        final float spen = this._params.weightSide * side;
        final float tpen = this._params.weightToi * (1.0f / (0.1f + tmin * this._invHorizTime));
        final float penalty = vpen + vcpen + spen + tpen;
        if (debug != null) {
            debug.AddSample(vcand, cs, penalty, vpen, vcpen, spen, tpen);
        }
        return penalty;
    }
    
    public void AddCircle(final float[] pos, final float rad, final float[] vel, final float[] dvel) {
        if (this._nCircles >= this._maxCircles) {
            return;
        }
        final ObstacleCircle cir = this._circles[this._nCircles++];
        Helper.VCopy(cir.p, pos);
        cir.rad = rad;
        Helper.VCopy(cir.vel, vel);
        Helper.VCopy(cir.dvel, dvel);
    }
    
    public void AddSegment(final float[] p, final float[] q) {
        if (this._nSegments >= this._maxSegments) {
            return;
        }
        final ObstacleSegment seg = this._segments[this._nSegments++];
        Helper.VCopy(seg.p, p);
        Helper.VCopy(seg.q, q);
    }
    
    public int SampleVelocityGrid(final float[] pos, final float rad, final float vmax, final float[] vel, final float[] dvel, final float[] nvel, final ObstacleAvoidanceParams param) {
        return this.SampleVelocityGrid(pos, rad, vmax, vel, dvel, nvel, null);
    }
    
    public int SampleVelocityGrid(final float[] pos, final float rad, final float vmax, final float[] vel, final float[] dvel, final float[] nvel, final ObstacleAvoidanceParams param, final ObstacleAvoidanceDebugData debug) {
        this.Prepare(pos, dvel);
        this._params = new ObstacleAvoidanceParams(param);
        this._invHorizTime = 1.0f / this._params.horizTime;
        this._vmax = vmax;
        this._invVmax = 1.0f / vmax;
        Helper.VSet(nvel, 0.0f, 0.0f, 0.0f);
        if (debug != null) {
            debug.Reset();
        }
        final float cvx = dvel[0] * this._params.velBias;
        final float cvz = dvel[2] * this._params.velBias;
        final float cs = vmax * 2.0f * (1.0f - this._params.velBias) / (this._params.gridSize - 1);
        final float half = (this._params.gridSize - 1) * cs * 0.5f;
        float minPenalty = Float.MAX_VALUE;
        int ns = 0;
        for (int y = 0; y < this._params.gridSize; ++y) {
            for (int x = 0; x < this._params.gridSize; ++x) {
                final float[] vcand = { cvx + x * cs - half, 0.0f, cvz + y * cs - half };
                if (vcand[0] * vcand[0] + vcand[2] * vcand[2] <= (vmax + cs / 2.0f) * (vmax + cs / 2.0f)) {
                    final float penalty = this.ProcessSample(vcand, cs, pos, rad, vel, dvel, debug);
                    ++ns;
                    if (penalty < minPenalty) {
                        minPenalty = penalty;
                        Helper.VCopy(nvel, vcand);
                    }
                }
            }
        }
        return ns;
    }
    
    public int SampleVelocityAdaptive(final float[] pos, final float rad, final float vmax, final float[] vel, final float[] dvel, final float[] nvel, final ObstacleAvoidanceParams param) {
        return this.SampleVelocityAdaptive(pos, rad, vmax, vel, dvel, nvel, param, null);
    }
    
    public int SampleVelocityAdaptive(final float[] pos, final float rad, final float vmax, final float[] vel, final float[] dvel, final float[] nvel, final ObstacleAvoidanceParams param, final ObstacleAvoidanceDebugData debug) {
        this.Prepare(pos, dvel);
        this._params = new ObstacleAvoidanceParams(param);
        this._invHorizTime = 1.0f / this._params.horizTime;
        this._vmax = vmax;
        this._invVmax = 1.0f / vmax;
        Helper.VSet(nvel, 0.0f, 0.0f, 0.0f);
        if (debug != null) {
            debug.Reset();
        }
        final float[] pat = new float[258];
        int npat = 0;
        final int ndivs = this._params.adaptiveDivs;
        final int nrings = this._params.adaptiveRings;
        final int depth = this._params.adaptiveDepth;
        final int nd = Math.max(1, Math.min(32, ndivs));
        final int nr = Math.max(1, Math.min(4, nrings));
        final float da = 1.0f / nd * 3.1415927f * 2.0f;
        final float dang = (float)Math.atan2(dvel[2], dvel[0]);
        pat[npat * 2 + 1] = (pat[npat * 2 + 0] = 0.0f);
        ++npat;
        for (int j = 0; j < nr; ++j) {
            final float r = (nr - j) / nr;
            float a = dang + (j & 0x1) * 0.5f * da;
            for (int i = 0; i < nd; ++i) {
                pat[npat * 2 + 0] = (float)Math.cos(a) * r;
                pat[npat * 2 + 1] = (float)Math.sin(a) * r;
                ++npat;
                a += da;
            }
        }
        float cr = vmax * (1.0f - this._params.velBias);
        final float[] res = new float[3];
        Helper.VSet(res, dvel[0] * this._params.velBias, 0.0f, dvel[2] * this._params.velBias);
        int ns = 0;
        for (int k = 0; k < depth; ++k) {
            float minPenalty = Float.MAX_VALUE;
            final float[] bvel = new float[3];
            Helper.VSet(bvel, 0.0f, 0.0f, 0.0f);
            for (int l = 0; l < npat; ++l) {
                final float[] vcand = { res[0] + pat[l * 2 + 0] * cr, 0.0f, res[2] + pat[l * 2 + 1] * cr };
                if (vcand[0] * vcand[0] + vcand[2] * vcand[2] <= (vmax + 0.001f) * (vmax + 0.001f)) {
                    final float penalty = this.ProcessSample(vcand, cr / 10.0f, pos, rad, vel, dvel, debug);
                    ++ns;
                    if (penalty < minPenalty) {
                        minPenalty = penalty;
                        Helper.VCopy(bvel, vcand);
                    }
                }
            }
            Helper.VCopy(res, bvel);
            cr *= 0.5f;
        }
        Helper.VCopy(nvel, res);
        return ns;
    }
    
    public int ObstacleCircleCount() {
        return this._nCircles;
    }
    
    public ObstacleCircle GetObstacleCircle(final int i) {
        return this._circles[i];
    }
    
    public int ObstacleSegmentCount() {
        return this._nSegments;
    }
    
    public ObstacleSegment GetObstacleSegment(final int i) {
        return this._segments[i];
    }
}
