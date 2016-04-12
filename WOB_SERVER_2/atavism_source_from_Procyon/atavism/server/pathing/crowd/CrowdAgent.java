// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

import atavism.server.pathing.detour.NavMeshQuery;
import atavism.server.pathing.recast.Helper;

public class CrowdAgent
{
    public static int CrowdAgentMaxNeighbors;
    public static int CrowdAgentMaxCorners;
    public Boolean Active;
    public CrowdAgentState State;
    public PathCorridor Corridor;
    public LocalBoundary Boundary;
    public float TopologyOptTime;
    public CrowdNeighbour[] Neis;
    public int NNeis;
    public float DesiredSpeed;
    public float[] npos;
    public float[] disp;
    public float[] dvel;
    public float[] nvel;
    public float[] vel;
    public CrowdAgentParams Param;
    public float[] CornerVerts;
    public short[] CornerFlags;
    public long[] CornerPolys;
    public int NCorners;
    public MoveRequestState TargetState;
    public long TargetRef;
    public float[] TargetPos;
    public long TargetPathQRef;
    public Boolean TargetReplan;
    public float TargetReplanTime;
    
    public CrowdAgent() {
        this.Neis = new CrowdNeighbour[CrowdAgent.CrowdAgentMaxNeighbors];
        this.npos = new float[3];
        this.disp = new float[3];
        this.dvel = new float[3];
        this.nvel = new float[3];
        this.vel = new float[3];
        this.CornerVerts = new float[CrowdAgent.CrowdAgentMaxCorners * 3];
        this.CornerFlags = new short[CrowdAgent.CrowdAgentMaxCorners];
        this.CornerPolys = new long[CrowdAgent.CrowdAgentMaxCorners];
        this.TargetPos = new float[3];
        this.Corridor = new PathCorridor();
        this.Boundary = new LocalBoundary();
        for (int i = 0; i < CrowdAgent.CrowdAgentMaxNeighbors; ++i) {
            this.Neis[i] = new CrowdNeighbour();
        }
    }
    
    public void Integrate(final float dt) {
        final float maxDelta = this.Param.MaxAcceleration * dt;
        float[] dv = Helper.VSub(this.nvel[0], this.nvel[1], this.nvel[2], this.vel[0], this.vel[1], this.vel[2]);
        final float ds = Helper.VLen(dv);
        if (ds > maxDelta) {
            dv = Helper.VScale(dv[0], dv[1], dv[2], maxDelta / ds);
        }
        this.vel = Helper.VAdd(this.vel[0], this.vel[1], this.vel[2], dv[0], dv[1], dv[2]);
        if (Helper.VLen(this.vel) > 1.0E-4f) {
            this.npos = Helper.VMad(this.npos, this.npos, this.vel, dt);
        }
        else {
            Helper.VSet(this.vel, 0.0f, 0.0f, 0.0f);
        }
    }
    
    public Boolean OverOffMeshConnection(final float radius) {
        if (this.NCorners <= 0) {
            return false;
        }
        final Boolean offMeshConnection = (this.CornerFlags[this.NCorners - 1] & NavMeshQuery.StraightPathOffMeshConnection) != 0x0;
        if (offMeshConnection) {
            final float distSq = Helper.VDist2DSqr(this.npos[0], this.npos[1], this.npos[2], this.CornerVerts[(this.NCorners - 1) * 3 + 0], this.CornerVerts[(this.NCorners - 1) * 3 + 1], this.CornerVerts[(this.NCorners - 1) * 3 + 2]);
            if (distSq < radius * radius) {
                return true;
            }
        }
        return false;
    }
    
    public float GetDistanceToGoal(final float range) {
        if (this.NCorners <= 0) {
            return range;
        }
        final Boolean endOfPath = (this.CornerFlags[this.NCorners - 1] & NavMeshQuery.StraightPathEnd) != 0x0;
        if (endOfPath) {
            final float[] temp = new float[3];
            System.arraycopy(this.CornerVerts, (this.NCorners - 1) * 3, temp, 0, 3);
            return Math.min(Helper.VDist2D(this.npos, temp), range);
        }
        return range;
    }
    
    public float[] CalcSmoothSteerDirection(float[] dir) {
        if (this.NCorners <= 0) {
            Helper.VSet(dir, 0.0f, 0.0f, 0.0f);
            return dir;
        }
        final int ip0 = 0;
        final int ip2 = Math.min(1, this.NCorners - 1);
        final float[] p0 = new float[3];
        final float[] p2 = new float[3];
        final int sourcePos0 = ip0 * 3;
        final int sourcePos2 = ip2 * 3;
        System.arraycopy(this.CornerVerts, sourcePos0, p0, 0, 3);
        System.arraycopy(this.CornerVerts, sourcePos2, p2, 0, 3);
        final float[] dir2 = Helper.VSub(p0[0], p0[1], p0[2], this.npos[0], this.npos[1], this.npos[2]);
        float[] dir3 = Helper.VSub(p2[0], p2[1], p2[2], this.npos[0], this.npos[1], this.npos[2]);
        dir3[1] = (dir2[1] = 0.0f);
        final float len0 = Helper.VLen(dir2);
        final float len2 = Helper.VLen(dir3);
        if (len2 > 0.001f) {
            dir3 = Helper.VScale(dir3[0], dir3[1], dir3[2], 1.0f / len2);
        }
        dir[0] = dir2[0] - dir3[0] * len0 * 0.5f;
        dir[1] = 0.0f;
        dir[2] = dir2[2] - dir3[2] * len0 * 0.5f;
        dir = Helper.VNormalize(dir);
        return dir;
    }
    
    public float[] CalcStraightSteerDirection(float[] dir) {
        if (this.NCorners <= 0) {
            Helper.VSet(dir, 0.0f, 0.0f, 0.0f);
            return dir;
        }
        dir = Helper.VSub(this.CornerVerts[0], this.CornerVerts[1], this.CornerVerts[2], this.npos[0], this.npos[1], this.npos[2]);
        dir[1] = 0.0f;
        dir = Helper.VNormalize(dir);
        return dir;
    }
    
    static {
        CrowdAgent.CrowdAgentMaxNeighbors = 6;
        CrowdAgent.CrowdAgentMaxCorners = 4;
    }
}
