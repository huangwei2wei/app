// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;

public class ArenaGridBlock implements Serializable
{
    int x;
    int y;
    int z;
    Object objectInBlock;
    private static final long serialVersionUID = 1L;
    
    public ArenaGridBlock() {
    }
    
    public ArenaGridBlock(final int x, final int y, final int z, final Object object) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.objectInBlock = object;
    }
    
    public boolean placeObjectInBlock(final Object object) {
        if (this.objectInBlock == null) {
            this.objectInBlock = object;
            return true;
        }
        return false;
    }
    
    public boolean hasObject() {
        return this.objectInBlock != null;
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public void setZ(final int z) {
        this.z = z;
    }
    
    public Object getObjectInBlock() {
        return this.objectInBlock;
    }
    
    public void setObjectInBlock(final Object object) {
        this.objectInBlock = object;
    }
}
