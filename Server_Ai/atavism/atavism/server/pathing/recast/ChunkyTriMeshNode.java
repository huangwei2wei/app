// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

public class ChunkyTriMeshNode
{
    public float[] bmin;
    public float[] bmax;
    public int i;
    public int n;
    
    public ChunkyTriMeshNode() {
        this.bmin = new float[2];
        this.bmax = new float[2];
        this.i = 0;
        this.n = 0;
    }
}
