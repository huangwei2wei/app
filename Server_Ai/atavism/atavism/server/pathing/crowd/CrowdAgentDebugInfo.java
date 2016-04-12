// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

public class CrowdAgentDebugInfo
{
    public int Idx;
    public float[] OptStart;
    public float[] OptEnd;
    public ObstacleAvoidanceDebugData Vod;
    
    public CrowdAgentDebugInfo() {
        this.OptStart = new float[3];
        this.OptEnd = new float[3];
    }
}
