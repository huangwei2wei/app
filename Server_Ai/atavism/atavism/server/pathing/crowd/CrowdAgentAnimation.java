// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.crowd;

public class CrowdAgentAnimation
{
    public Boolean Active;
    public float[] InitPos;
    public float[] StartPos;
    public float[] EndPos;
    public long PolyRef;
    public float T;
    public float TMax;
    
    public CrowdAgentAnimation() {
        this.InitPos = new float[3];
        this.StartPos = new float[3];
        this.EndPos = new float[3];
    }
}
