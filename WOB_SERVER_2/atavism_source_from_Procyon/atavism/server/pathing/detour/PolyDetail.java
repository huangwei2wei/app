// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.detour;

public class PolyDetail
{
    public long VertBase;
    public long TriBase;
    public short VertCount;
    public short TriCount;
    
    @Override
    public String toString() {
        return String.format("VertBase: {0}, VertCount: {1}, TriBase: {2}, TriCount: {3}", this.VertBase, this.VertCount, this.TriBase, this.TriCount);
    }
}
