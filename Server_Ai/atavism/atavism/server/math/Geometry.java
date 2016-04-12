// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.math;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import atavism.server.util.Log;
import java.util.LinkedList;
import java.util.Collection;
import java.io.Serializable;

public class Geometry implements Serializable, Cloneable
{
    float minX;
    float maxX;
    float minZ;
    float maxZ;
    public static float GEO_MIN_X;
    public static float GEO_MAX_X;
    public static float GEO_MIN_Z;
    public static float GEO_MAX_Z;
    private static final long serialVersionUID = 1L;
    
    public Geometry() {
        this.minX = 0.0f;
        this.maxX = 0.0f;
        this.minZ = 0.0f;
        this.maxZ = 0.0f;
    }
    
    public Geometry(final float minX, final float maxX, final float minZ, final float maxZ) {
        this.minX = 0.0f;
        this.maxX = 0.0f;
        this.minZ = 0.0f;
        this.maxZ = 0.0f;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    
    @Override
    public String toString() {
        return "[Geometry X=" + this.getMinX() + "," + this.getMaxX() + " Z=" + this.getMinZ() + "," + this.getMaxZ() + "]";
    }
    
    public Object clone() {
        return new Geometry(this.minX, this.maxX, this.minZ, this.maxZ);
    }
    
    public boolean equals(final Geometry other) {
        return this.minX == other.minX && this.maxX == other.maxX && this.minZ == other.minZ && this.maxZ == other.maxZ;
    }
    
    public float getMinX() {
        return this.minX;
    }
    
    public float getMaxX() {
        return this.maxX;
    }
    
    public float getMinZ() {
        return this.minZ;
    }
    
    public float getMaxZ() {
        return this.maxZ;
    }
    
    public void setMinX(final int x) {
        this.minX = x;
    }
    
    public void setMaxX(final int x) {
        this.maxX = x;
    }
    
    public void getMinZ(final int z) {
        this.minZ = z;
    }
    
    public void getMaxZ(final int z) {
        this.maxZ = z;
    }
    
    Point getCenter() {
        final int halfX = (int)(((long)this.maxX - (long)this.minX) / 2L - 1L);
        final int halfZ = (int)(((long)this.maxZ - (long)this.minZ) / 2L - 1L);
        return new Point(halfX, 0.0f, halfZ);
    }
    
    public boolean contains(final Point pt) {
        return pt != null && pt.getX() >= this.getMinX() && pt.getX() <= this.getMaxX() && pt.getZ() >= this.getMinZ() && pt.getZ() <= this.getMaxZ();
    }
    
    public boolean contains(final Geometry g) {
        return g != null && g.getMinX() >= this.getMinX() && g.getMaxX() <= this.getMaxX() && g.getMinZ() >= this.getMinZ() && g.getMaxZ() <= this.getMaxZ();
    }
    
    public boolean overlaps(final Geometry g) {
        return g != null && g.getMaxX() >= this.getMinX() && g.getMinX() <= this.getMaxX() && g.getMaxZ() >= this.getMinZ() && g.getMinZ() <= this.getMaxZ();
    }
    
    public Collection<Point> getCorners() {
        final Collection<Point> corners = new LinkedList<Point>();
        corners.add(new Point(this.minX, 0.0f, this.minZ));
        corners.add(new Point(this.minX, 0.0f, this.maxZ));
        corners.add(new Point(this.maxX, 0.0f, this.minZ));
        corners.add(new Point(this.maxX, 0.0f, this.maxZ));
        return corners;
    }
    
    public Geometry[] divide() {
        final long ldiffX = (long)this.maxX - (long)this.minX;
        final long lhalfX = ldiffX / 2L - 1L;
        Log.debug("DIVIDE: maxX=" + this.maxX + " minX=" + this.minX + " ldiffX=" + ldiffX + " lhalfX=" + lhalfX);
        final long ldiffZ = (long)this.maxZ - (long)this.minZ;
        final long lhalfZ = ldiffZ / 2L - 1L;
        final Geometry[] ga = { new Geometry(this.minX, this.minX + lhalfX + 1.0f, this.minZ, this.minZ + lhalfZ + 1.0f), new Geometry(this.minX + lhalfX + 1.0f, this.maxX, this.minZ, this.minZ + lhalfZ + 1.0f), new Geometry(this.minX, this.minX + lhalfX + 1.0f, this.minZ + lhalfZ + 1.0f, this.maxZ), new Geometry(this.minX + lhalfX + 1.0f, this.maxX, this.minZ + lhalfZ + 1.0f, this.maxZ) };
        return ga;
    }
    
    public boolean isAdjacent(final Geometry gOther) {
        return gOther.getMinX() == this.getMaxX() + 1.0f || gOther.getMaxX() == this.getMinX() - 1.0f || gOther.getMaxZ() == this.getMinZ() - 1.0f || gOther.getMinZ() == this.getMaxZ() + 1.0f;
    }
    
    public static Geometry maxGeometry() {
        return new Geometry(Geometry.GEO_MIN_X, Geometry.GEO_MAX_X, Geometry.GEO_MIN_Z, Geometry.GEO_MAX_Z);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.defaultWriteObject();
    }
    
    static {
        Geometry.GEO_MIN_X = -499999.0f;
        Geometry.GEO_MAX_X = 500000.0f;
        Geometry.GEO_MIN_Z = -499999.0f;
        Geometry.GEO_MAX_Z = 500000.0f;
    }
}
