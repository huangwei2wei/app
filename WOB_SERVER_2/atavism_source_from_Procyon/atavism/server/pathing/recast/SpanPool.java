// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing.recast;

public class SpanPool
{
    public static int SpansPerPool;
    public SpanPool Next;
    public Span[] Items;
    
    public SpanPool() {
        this.Items = new Span[SpanPool.SpansPerPool];
    }
    
    static {
        SpanPool.SpansPerPool = 2048;
    }
}
