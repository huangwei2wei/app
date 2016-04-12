// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing.recast;

public class RecastVertex
{
    protected float[] Verts;
    public float X;
    public float Y;
    public float Z;
    
    public float getX() {
        return this.Verts[0];
    }
    
    public void setX(final float value) {
        this.Verts[0] = value;
    }
    
    public float getY() {
        return this.Verts[1];
    }
    
    public void setY(final float value) {
        this.Verts[1] = value;
    }
    
    public float getZ() {
        return this.Verts[2];
    }
    
    public void setZ(final float value) {
        this.Verts[2] = value;
    }
    
    public RecastVertex() {
        this.Verts = new float[3];
    }
    
    public RecastVertex(final RecastVertex copy) {
        this.X = copy.X;
        this.Y = copy.Y;
        this.Z = copy.Z;
    }
    
    public RecastVertex(final float x, final float y, final float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
    
    public RecastVertex(final float[] inArray) {
        try {
            if (inArray.length < 3) {
                throw new Exception();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.X = inArray[0];
        this.Y = inArray[1];
        this.Z = inArray[2];
    }
    
    public float[] ToArray() {
        return this.Verts;
    }
    
    public Boolean Equals(final RecastVertex other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        return other.X == this.X && other.Y == this.Y && other.Z == this.Z;
    }
    
    public Boolean Equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass().isInstance(RecastVertex.class)) {
            return false;
        }
        return this.Equals((RecastVertex)obj);
    }
    
    public static RecastVertex Add(final RecastVertex left, final RecastVertex right) {
        return new RecastVertex(left.X + right.X, left.Y + right.Y, left.Z + right.Z);
    }
    
    public static RecastVertex Sub(final RecastVertex left, final RecastVertex right) {
        return new RecastVertex(left.X - right.X, left.Y - right.Y, left.Z - right.Z);
    }
    
    public static RecastVertex Mult(final RecastVertex left, final int scale) {
        return new RecastVertex(left.X * scale, left.Y * scale, left.Z * scale);
    }
    
    public static RecastVertex Mult(final RecastVertex left, final RecastVertex right, final int scale) {
        return new RecastVertex(left.X + right.X * scale, left.Y + right.Y * scale, left.Z + right.Z * scale);
    }
    
    public static RecastVertex Cross(final RecastVertex left, final RecastVertex right) {
        return new RecastVertex(left.Y * right.Z - left.Z * right.Y, left.Z * right.X - left.X * right.Z, left.X * right.Y - left.Y * right.X);
    }
    
    public static float Dot(final RecastVertex left, final RecastVertex right) {
        return left.X * right.X + left.Y * right.Y + left.Z * right.Z;
    }
    
    public static RecastVertex Min(final RecastVertex left, final RecastVertex right) {
        return new RecastVertex(Math.min(left.X, right.X), Math.min(left.Y, right.Y), Math.min(left.Z, right.Z));
    }
    
    public static RecastVertex Max(final RecastVertex left, final RecastVertex right) {
        return new RecastVertex(Math.max(left.X, right.X), Math.max(left.Y, right.Y), Math.max(left.Z, right.Z));
    }
    
    public static float Distance(final RecastVertex left, final RecastVertex right) {
        final float dx = right.X - left.X;
        final float dy = right.Y - left.Y;
        final float dz = right.Z - left.Z;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    public static float SquareDistance(final RecastVertex left, final RecastVertex right) {
        final float dx = right.X - left.X;
        final float dy = right.Y - left.Y;
        final float dz = right.Z - left.Z;
        return dx * dx + dy * dy + dz * dz;
    }
    
    public void Normalize() {
        final float d = (float)(1.0 / Math.sqrt(this.X * this.X + this.Y * this.Y + this.Z * this.Z));
        this.X *= d;
        this.Y *= d;
        this.Z *= d;
    }
}
