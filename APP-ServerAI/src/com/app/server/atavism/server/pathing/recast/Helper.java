// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.recast;

import com.app.server.atavism.server.math.IntVector2;
import com.app.server.atavism.server.pathing.detour.DetourNumericReturn;
import com.app.server.atavism.server.objects.Vector2;

public class Helper
{
    public static int NavMeshMagic;
    public static int NavMeshVersion;
    public static int StatusDetailMast;
    protected static int[] offsetX;
    protected static int[] offsetY;
    
    public static int GetDirOffsetX(final int dir) {
        return Helper.offsetX[dir & 0x3];
    }
    
    public static int GetDirOffsetY(final int dir) {
        return Helper.offsetY[dir & 0x3];
    }
    /**
     * ���2��N�η������ v
     * @param v
     * @return
     */
    public static long NextPow2(long v) {
        --v;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        ++v;
        return v;
    }
    
    public static long Ilog2(long v) {
        long r = (v > 65535L) ? 16L : 0L;
        v >>= (int)r;
        long shift = (v > 255L) ? 8L : 0L;
        v >>= (int)shift;
        r |= shift;
        shift = ((v > 15L) ? 4L : 0L);
        v >>= (int)shift;
        r |= shift;
        shift = ((v > 3L) ? 2L : 0L);
        v >>= (int)shift;
        r |= shift;
        r |= v >> 1;
        return r;
    }
    
    public static float GetSlabCoord(final float vax, final float vay, final float vaz, final int side) {
        if (side == 0 || side == 4) {
            return vax;
        }
        if (side == 2 || side == 6) {
            return vaz;
        }
        return 0.0f;
    }
    
    public static void CalcSlabEndPoints(final float vax, final float vay, final float vaz, final float vbx, final float vby, final float vbz, final float[] bmin, final float[] bmax, final int side) {
        if (side == 0 || side == 4) {
            if (vaz < vbz) {
                bmin[0] = vaz;
                bmin[1] = vay;
                bmax[0] = vbz;
                bmax[1] = vby;
            }
            else {
                bmin[0] = vbz;
                bmin[1] = vby;
                bmax[0] = vaz;
                bmax[1] = vay;
            }
        }
        else if (side == 2 || side == 6) {
            if (vax < vbx) {
                bmin[0] = vax;
                bmin[1] = vay;
                bmax[0] = vbx;
                bmax[1] = vby;
            }
            else {
                bmin[0] = vbx;
                bmin[1] = vby;
                bmax[0] = vax;
                bmax[1] = vay;
            }
        }
    }
    
    public static Boolean OverlapSlabs(final float[] amin, final float[] amax, final float[] bmin, final float[] bmax, final float px, final float py) {
        final float minx = Math.max(amin[0] + px, bmin[0] + px);
        final float maxx = Math.min(amax[0] - px, bmax[0] - px);
        if (minx > maxx) {
            return false;
        }
        final float ad = (amax[1] - amin[1]) / (amax[0] - amin[0]);
        final float ak = amin[1] - ad * amin[0];
        final float bd = (bmax[1] - bmin[1]) / (bmax[0] - bmin[0]);
        final float bk = bmin[1] - bd * bmin[0];
        final float aminy = ad * minx + ak;
        final float amaxy = ad * maxx + ak;
        final float bminy = bd * minx + bk;
        final float bmaxy = bd * maxx + bk;
        final float dmin = bminy - aminy;
        final float dmax = bmaxy - amaxy;
        if (dmin * dmax < 0.0f) {
            return true;
        }
        final float thr = py * 2.0f * (py * 2.0f);
        if (dmin * dmin <= thr || dmax * dmax <= thr) {
            return true;
        }
        return false;
    }
    
    public static int OppositeTile(final int side) {
        return side + 4 & 0x7;
    }
    
    public static float VDist(final float v1x, final float v1y, final float v1z, final float v2x, final float v2y, final float v2z) {
        final float dx = v2x - v1x;
        final float dy = v2y - v1y;
        final float dz = v2z - v1z;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public static float[] VLerp(final float[] dest, final float v1x, final float v1y, final float v1z, final float v2x, final float v2y, final float v2z, final float t) {
        dest[0] = v1x + (v2x - v1x) * t;
        dest[1] = v1y + (v2y - v1y) * t;
        dest[2] = v1z + (v2z - v1z) * t;
        return dest;
    }
    
    public static Boolean DistancePtPolyEdgesSqr(final float ptx, final float pty, final float ptz, final float[] verts, final int nverts, final float[] ed, final float[] et) {
        Boolean c = false;
        int i = 0;
        int j = nverts - 1;
        while (i < nverts) {
            final int vi = i * 3;
            final int vj = j * 3;
            if (verts[vi + 2] > ptz != verts[vj + 2] > ptz && ptx < (verts[vj + 0] - verts[vi + 0]) * (ptz - verts[vi + 2]) / (verts[vj + 2] - verts[vi + 2]) + verts[vi + 0]) {
                c = !c;
            }
            ed[j] = DistancePtSegSqr2D(ptx, pty, ptz, verts[vj + 0], verts[vj + 1], verts[vj + 2], verts[vi + 0], verts[vi + 1], verts[vi + 2], et, j);
            j = i++;
        }
        return c;
    }
    
    public static float DistancePtSegSqr2D(final float ptx, final float pty, final float ptz, final float px, final float py, final float pz, final float qx, final float qy, final float qz, final float[] et, final int t) {
        final float pqx = qx - px;
        final float pqz = qz - pz;
        float dx = ptx - px;
        float dz = ptz - pz;
        final float d = pqx * pqx + pqz * pqz;
        et[t] = pqx * dx + pqz * dz;
        if (d > 0.0f) {
            et[t] /= d;
        }
        if (et[t] < 0.0f) {
            et[t] = 0.0f;
        }
        else if (et[t] > 1.0f) {
            et[t] = 1.0f;
        }
        dx = px + et[t] * pqx - ptx;
        dz = pz + et[t] * pqz - ptz;
        return dx * dx + dz * dz;
    }
    
    public static Vector2 DistancePtSegSqr2D(final float ptx, final float pty, final float ptz, final float px, final float py, final float pz, final float qx, final float qy, final float qz) {
        final Vector2 numericReturn = new Vector2();
        final float pqx = qx - px;
        final float pqz = qz - pz;
        float dx = ptx - px;
        float dz = ptz - pz;
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
        dx = px + t * pqx - ptx;
        dz = pz + t * pqz - ptz;
        numericReturn.x = dx * dx + dz * dz;
        numericReturn.y = t;
        return numericReturn;
    }
    
    public static DetourNumericReturn ClosestHeightPointTriangle(final float px, final float py, final float pz, final float ax, final float ay, final float az, final float bx, final float by, final float bz, final float cx, final float cy, final float cz) {
        final DetourNumericReturn numericReturn = new DetourNumericReturn();
        final float[] v0 = VSub(cx, cy, cz, ax, ay, az);
        final float[] v2 = VSub(bx, by, bz, ax, ay, az);
        final float[] v3 = VSub(px, py, pz, ax, ay, az);
        final float dot00 = VDot2D(v0, v0);
        final float dot2 = VDot2D(v0, v2);
        final float dot3 = VDot2D(v0, v3);
        final float dot4 = VDot2D(v2, v2);
        final float dot5 = VDot2D(v2, v3);
        final float invDenom = 1.0f / (dot00 * dot4 - dot2 * dot2);
        final float u = (dot4 * dot3 - dot2 * dot5) * invDenom;
        final float v4 = (dot00 * dot5 - dot2 * dot3) * invDenom;
        final float EPS = 1.0E-4f;
        if (u >= -EPS && v4 >= -EPS && u + v4 <= 1.0f + EPS) {
            numericReturn.floatValue = ay + v0[1] * u + v2[1] * v4;
            numericReturn.boolValue = true;
            return numericReturn;
        }
        numericReturn.boolValue = false;
        return numericReturn;
    }
    
    public static float VDot2D(final float[] u, final float[] v) {
        return u[0] * v[0] + u[2] * v[2];
    }
    
    public static float VDot(final float[] u, final float[] v) {
        return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
    }
    
    public static float[] VMad(final float[] dest, final float[] v1, final float[] v2, final float s) {
        dest[0] = v1[0] + v2[0] * s;
        dest[1] = v1[1] + v2[1] * s;
        dest[2] = v1[2] + v2[2] * s;
        return dest;
    }
    
    public static float[] VSub(final float v1x, final float v1y, final float v1z, final float v2x, final float v2y, final float v2z) {
        final float[] dest = { v1x - v2x, v1y - v2y, v1z - v2z };
        return dest;
    }
    
    public static float[] VAdd(final float v1x, final float v1y, final float v1z, final float v2x, final float v2y, final float v2z) {
        final float[] dest = { v1x + v2x, v1y + v2y, v1z + v2z };
        return dest;
    }
    
    public static float VDistSqr(final float v1x, final float v1y, final float v1z, final float v2x, final float v2y, final float v2z) {
        final float dx = v2x - v1x;
        final float dy = v2y - v1y;
        final float dz = v2z - v1z;
        return dx * dx + dy * dy + dz * dz;
    }
    /**
     * �Ƿ��ص�
     * @param amin
     * @param amax
     * @param bmin
     * @param bmax
     * @return
     */
    public static Boolean OverlapQuantBounds(final int[] amin, final int[] amax, final int[] bmin, final int[] bmax) {
        Boolean overlap = true;
        overlap = (amin[0] <= bmax[0] && amax[0] >= bmin[0] && overlap);
        overlap = (amin[1] <= bmax[1] && amax[1] >= bmin[1] && overlap);
        overlap = (amin[2] <= bmax[2] && amax[2] >= bmin[2] && overlap);
        return overlap;
    }
    
    public static float[] VMin(final float[] mn, final float vx, final float vy, final float vz) {
        mn[0] = Math.min(mn[0], vx);
        mn[1] = Math.min(mn[1], vx);
        mn[2] = Math.min(mn[2], vx);
        return mn;
    }
    
    public static float[] VMax(final float[] mn, final float vx, final float vy, final float vz) {
        mn[0] = Math.max(mn[0], vx);
        mn[1] = Math.max(mn[1], vx);
        mn[2] = Math.max(mn[2], vx);
        return mn;
    }
    
    public static Boolean OverlapBounds(final float aminx, final float aminy, final float aminz, final float amaxx, final float amaxy, final float amaxz, final float bminx, final float bminy, final float bminz, final float bmaxx, final float bmaxy, final float bmaxz) {
        Boolean overlap = true;
        overlap = (aminx <= bmaxx && amaxx >= bminx && overlap);
        overlap = (aminy <= bmaxy && amaxy >= bminy && overlap);
        overlap = (aminz <= bmaxz && amaxz >= bminz && overlap);
        return overlap;
    }
    
    public static long HashRef(long a) {
        a += ~(a << 15);
        a ^= a >> 10;
        a += a << 3;
        a ^= a >> 6;
        a += ~(a << 11);
        a ^= a >> 16;
        return a;
    }
    
    public static float TriArea2D(final float[] a, final float[] b, final float[] c) {
        final float abx = b[0] - a[0];
        final float abz = b[2] - a[2];
        final float acx = c[0] - a[0];
        final float acz = c[2] - a[2];
        return acx * abz - abx * acz;
    }
    
    public static void RandomPointInConvexPoly(final float[] pts, final int npts, final float[] areas, final float s, final float t, final float[] outPt) {
        float areasum = 0.0f;
        final float[] va = new float[3];
        final float[] vb = new float[3];
        final float[] vc = new float[3];
        for (int i = 2; i < npts; ++i) {
            System.arraycopy(pts, 0, va, 0, 3);
            System.arraycopy(pts, (i - 1) * 3, vb, 0, 3);
            System.arraycopy(pts, i * 3, vc, 0, 3);
            areas[i] = TriArea2D(va, vb, vc);
            areasum += Math.max(0.001f, areas[i]);
        }
        final float thr = s * areasum;
        float acc = 0.0f;
        float u = 0.0f;
        int tri = 0;
        for (int j = 2; j < npts; ++j) {
            final float dacc = areas[j];
            if (thr >= acc && thr < acc + dacc) {
                u = (thr - acc) / dacc;
                tri = j;
                break;
            }
            acc += dacc;
        }
        final float v = (float)Math.sqrt(t);
        final float a = 1.0f - v;
        final float b = (1.0f - u) * v;
        final float c = u * v;
        final int pa = 0;
        final int pb = (tri - 1) * 3;
        final int pc = tri * 3;
        outPt[0] = a * pts[pa + 0] + b * pts[pb + 0] + c * pts[pc + 0];
        outPt[1] = a * pts[pa + 1] + b * pts[pb + 1] + c * pts[pc + 1];
        outPt[2] = a * pts[pa + 2] + b * pts[pb + 2] + c * pts[pc + 2];
    }
    
    public static Boolean VEqual(final float p0x, final float p0y, final float p0z, final float p1x, final float p1y, final float p1z) {
        final float thr = 3.7252903E-9f;
        final float d = VDistSqr(p0x, p0y, p0z, p1x, p1y, p1z);
        return d < thr;
    }
    
    public static DetourNumericReturn IntersectSegSeg2D(final float apx, final float apy, final float apz, final float aqx, final float aqy, final float aqz, final float[] bp, final float[] bq) {
        final DetourNumericReturn numericReturn = new DetourNumericReturn();
        final float[] u = VSub(aqx, aqy, aqz, apx, apy, apz);
        final float[] v = VSub(bq[0], bq[1], bq[2], bp[0], bp[1], bp[2]);
        final float[] w = VSub(apx, apy, apz, bp[0], bp[1], bp[2]);
        final float d = VPerpXZ(u, v);
        if (Math.abs(d) < 1.0E-6f) {
            numericReturn.boolValue = false;
            return numericReturn;
        }
        final Vector2 vectorVal = new Vector2();
        vectorVal.x = VPerpXZ(v, w) / d;
        vectorVal.y = VPerpXZ(u, w) / d;
        numericReturn.vector2Value = vectorVal;
        numericReturn.boolValue = true;
        return numericReturn;
    }
    
    private static float VPerpXZ(final float[] a, final float[] b) {
        return a[0] * b[2] - a[2] * b[0];
    }
    
    public static Boolean PointInPolygon(final float ptx, final float pty, final float ptz, final float[] verts, final int nverts) {
        Boolean c = false;
        int i = 0;
        int j = nverts - 1;
        while (i < nverts) {
            final int vi = i * 3;
            final int vj = j * 3;
            if (verts[vi + 2] > ptz != verts[vj + 2] > ptz && ptx < (verts[vj + 0] - verts[vi + 0]) * (ptz - verts[vi + 2]) / (verts[vj + 2] - verts[vi + 2]) + verts[vi + 0]) {
                c = !c;
            }
            j = i++;
        }
        return c;
    }
    
    public static Boolean IntersectSegmentPoly2D(final float[] p0, final float[] p1, final float[] verts, final int nverts, final Vector2 tMinMax, final IntVector2 segMinMax) {
        final float EPS = 1.0E-8f;
        tMinMax.x = 0.0;
        tMinMax.y = 1.0;
        segMinMax.x = -1;
        segMinMax.y = -1;
        final float[] dir = VSub(p1[0], p1[1], p1[2], p0[0], p0[1], p0[2]);
        int i = 0;
        int j = nverts - 1;
        while (i < nverts) {
            final float[] edge = VSub(verts[i * 3 + 0], verts[i * 3 + 1], verts[i * 3 + 2], verts[j * 3 + 0], verts[j * 3 + 1], verts[j * 3 + 2]);
            final float[] diff = VSub(p0[0], p0[1], p0[2], verts[j * 3 + 0], verts[j * 3 + 1], verts[j * 3 + 2]);
            final float n = VPerp2D(edge, diff);
            final float d = VPerp2D(dir, edge);
            if (Math.abs(d) < EPS) {
                if (n < 0.0f) {
                    return false;
                }
            }
            else {
                final float t = n / d;
                if (d < 0.0f) {
                    if (t > tMinMax.x) {
                        tMinMax.x = t;
                        segMinMax.x = j;
                        if (tMinMax.x > tMinMax.y) {
                            return false;
                        }
                    }
                }
                else if (t < tMinMax.y) {
                    tMinMax.y = t;
                    segMinMax.y = j;
                    if (tMinMax.y < tMinMax.x) {
                        return false;
                    }
                }
            }
            j = i++;
        }
        return true;
    }
    
    public static float VPerp2D(final float[] u, final float[] v) {
        return u[2] * v[0] - u[0] * v[2];
    }
    
    public static float[] VNormalize(final float[] v) {
        final float d = 1.0f / (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        final int n = 0;
        v[n] *= d;
        final int n2 = 1;
        v[n2] *= d;
        final int n3 = 2;
        v[n3] *= d;
        return v;
    }
    
    public static float[] VScale(final float vx, final float vy, final float vz, final float t) {
        final float[] dest = { vx * t, vy * t, vz * t };
        return dest;
    }
    
    public static Boolean OverlapPolyPoly2D(final float[] polya, final int npolya, final float[] polyb, final int npolyb) {
        final float eps = 1.0E-4f;
        int i = 0;
        int j = npolya - 1;
        while (i < npolya) {
            final int va = j * 3;
            final int vb = i * 3;
            final float[] n = { polya[vb + 2] - polya[va + 2], 0.0f, -(polya[vb + 0] - polya[va + 0]) };
            final Vector2 minMaxA = ProjectPoly(n, polya, npolya);
            final Vector2 minMaxB = ProjectPoly(n, polyb, npolyb);
            final float amin = (float)minMaxA.x;
            final float amax = (float)minMaxA.y;
            final float bmin = (float)minMaxB.x;
            final float bmax = (float)minMaxB.y;
            if (!OverlapRange(amin, amax, bmin, bmax, eps)) {
                return false;
            }
            j = i++;
        }
        i = 0;
        j = npolya - 1;
        while (i < npolyb) {
            final int va = j * 3;
            final int vb = i * 3;
            final float[] n = { polyb[vb + 2] - polyb[va + 2], 0.0f, -(polyb[vb + 0] - polyb[va + 0]) };
            final Vector2 minMaxA = ProjectPoly(n, polya, npolya);
            final Vector2 minMaxB = ProjectPoly(n, polyb, npolyb);
            final float amin = (float)minMaxA.x;
            final float amax = (float)minMaxA.y;
            final float bmin = (float)minMaxB.x;
            final float bmax = (float)minMaxB.y;
            if (!OverlapRange(amin, amax, bmin, bmax, eps)) {
                return false;
            }
            j = i++;
        }
        return true;
    }
    
    private static Boolean OverlapRange(final float amin, final float amax, final float bmin, final float bmax, final float eps) {
        return amin + eps <= bmax && amax - eps >= bmin;
    }
    
    private static Vector2 ProjectPoly(final float[] axis, final float[] poly, final int npoly) {
        final Vector2 minMax = new Vector2();
        final float[] temp = new float[3];
        System.arraycopy(poly, 0, temp, 0, 3);
        final Vector2 vector2 = minMax;
        final Vector2 vector3 = minMax;
        final double n = VDot2D(axis, temp);
        vector3.y = n;
        vector2.x = n;
        for (int i = 1; i < npoly; ++i) {
            System.arraycopy(poly, i * 3, temp, 0, 3);
            final float d = VDot2D(axis, temp);
            minMax.x = Math.min(minMax.x, d);
            minMax.y = Math.max(minMax.y, d);
        }
        return minMax;
    }
    
    public static void VSet(final float[] dest, final float x, final float y, final float z) {
        dest[0] = x;
        dest[1] = y;
        dest[2] = z;
    }
    
    public static void VCopy(final float[] dest, final float[] source) {
        dest[0] = source[0];
        dest[1] = source[1];
        dest[2] = source[2];
    }
    
    public static float VDist2D(final float[] v1, final float[] v2) {
        final float dx = v2[0] - v1[0];
        final float dz = v2[2] - v1[2];
        return (float)Math.sqrt(dx * dx + dz * dz);
    }
    
    public static float VDist2DSqr(final float v1x, final float v1y, final float v1z, final float v2x, final float v2y, final float v2z) {
        final float dx = v2x - v1x;
        final float dz = v2z - v1z;
        return dx * dx + dz * dz;
    }
    
    public static float VLen(final float[] v) {
        return (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }
    
    public static float VLenSqr(final float[] v) {
        return v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
    }
    
    public static float Clamp(final float v, final float min, final float max) {
        return (v < min) ? min : ((v > max) ? max : v);
    }
    
    static {
        Helper.NavMeshMagic = 1;
        Helper.NavMeshVersion = 1;
        Helper.StatusDetailMast = 268435455;
        Helper.offsetX = new int[] { -1, 0, 1, 0 };
        Helper.offsetY = new int[] { 0, 1, 0, -1 };
    }
}
