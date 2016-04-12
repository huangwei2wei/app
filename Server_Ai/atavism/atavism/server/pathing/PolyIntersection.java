// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

public class PolyIntersection
{
    public int poly1Corner;
    public int poly2Corner;
    public PathIntersection intr;
    
    public PolyIntersection(final int poly1Corner, final int poly2Corner, final PathIntersection intr) {
        this.poly1Corner = poly1Corner;
        this.poly2Corner = poly2Corner;
        this.intr = intr;
    }
}
