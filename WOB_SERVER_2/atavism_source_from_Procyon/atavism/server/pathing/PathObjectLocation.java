// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.math.AOVector;

public class PathObjectLocation
{
    protected PathObject pathObject;
    protected AOVector loc;
    protected byte kind;
    protected int polyIndex;
    
    public PathObjectLocation(final PathObject pathObject, final AOVector loc, final byte kind, final int polyIndex) {
        this.pathObject = pathObject;
        this.loc = loc;
        this.kind = kind;
        this.polyIndex = polyIndex;
    }
    
    public PathObject getPathObject() {
        return this.pathObject;
    }
    
    public AOVector getLoc() {
        return this.loc;
    }
    
    public byte getKind() {
        return this.kind;
    }
    
    public int getPolyIndex() {
        return this.polyIndex;
    }
    
    @Override
    public String toString() {
        return "[PathObjectLocation loc = " + this.loc + "; kind = " + this.kind + "; polyIndex = " + this.polyIndex + "]";
    }
}
