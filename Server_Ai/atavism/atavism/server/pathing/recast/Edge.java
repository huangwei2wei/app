// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

public class Edge
{
    public int[] Vert;
    public int[] PolyEdge;
    public int[] Poly;
    
    public Edge() {
        this.Vert = new int[2];
        this.PolyEdge = new int[2];
        this.Poly = new int[2];
    }
}
