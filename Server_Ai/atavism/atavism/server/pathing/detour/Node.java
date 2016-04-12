// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;

public class Node
{
    public float[] Pos;
    public float Cost;
    public float Total;
    public long PIdx;
    public long Flags;
    public long Id;
    public static int NullIdx;
    public static int NodeOpen;
    public static int NodeClosed;
    
    public Node() {
        this.Pos = new float[3];
    }
    
    static {
        Node.NullIdx = -1;
        Node.NodeOpen = 1;
        Node.NodeClosed = 2;
    }
}
