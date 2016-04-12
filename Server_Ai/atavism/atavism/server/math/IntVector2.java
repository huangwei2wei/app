// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.math;

import java.io.Serializable;

public class IntVector2 implements Cloneable, Serializable
{
    public int x;
    public int y;
    private static final long serialVersionUID = 1L;
    
    public IntVector2() {
        this.x = -1;
        this.y = -1;
    }
    
    public IntVector2(final int x, final int y) {
        this.x = -1;
        this.y = -1;
        this.x = x;
        this.y = y;
    }
    
    public Object clone() {
        return new IntVector2(this.x, this.y);
    }
    
    @Override
    public String toString() {
        return "[IntVector2 x=" + this.x + " y=" + this.y + "]";
    }
}
