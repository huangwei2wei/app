// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.math;

public class Matrix4 implements Comparable<Matrix4>, Cloneable
{
    private float[][] _data;
    
    public Matrix4() {
        this._data = new float[4][4];
        this._data[0][0] = 1.0f;
        this._data[1][1] = 1.0f;
        this._data[2][2] = 1.0f;
        this._data[3][3] = 1.0f;
    }
    
    public Matrix4(final float m00, final float m01, final float m02, final float m03, final float m10, final float m11, final float m12, final float m13, final float m20, final float m21, final float m22, final float m23, final float m30, final float m31, final float m32, final float m33) {
        this._data = new float[4][4];
        this._data[0][0] = m00;
        this._data[0][1] = m01;
        this._data[0][2] = m02;
        this._data[0][3] = m03;
        this._data[1][0] = m10;
        this._data[1][1] = m11;
        this._data[1][2] = m12;
        this._data[1][3] = m13;
        this._data[2][0] = m20;
        this._data[2][1] = m21;
        this._data[2][2] = m22;
        this._data[2][3] = m23;
        this._data[3][0] = m30;
        this._data[3][1] = m31;
        this._data[3][2] = m32;
        this._data[3][3] = m33;
    }
    
    public Object clone() {
        final Matrix4 rv = new Matrix4();
        rv.assign(this);
        return rv;
    }
    
    public float get(final int row, final int column) {
        return this._data[row][column];
    }
    
    public void set(final int row, final int column, final float val) {
        this._data[row][column] = val;
    }
    
    public Matrix4 translate(final AOVector vec) {
        final float[] array = this._data[0];
        final int n = 3;
        array[n] += vec.getX();
        final float[] array2 = this._data[1];
        final int n2 = 3;
        array2[n2] += vec.getY();
        final float[] array3 = this._data[2];
        final int n3 = 3;
        array3[n3] += vec.getZ();
        return this;
    }
    
    public Matrix4 rotate(final Quaternion rot) {
        final Matrix4 rotMatrix = fromRotation(rot);
        this.assign(rotMatrix.multiply(this));
        return this;
    }
    
    private static Matrix4 multiplyRef(final Matrix4 result, final Matrix4 left, final Matrix4 right) {
        result._data[0][0] = left._data[0][0] * right._data[0][0] + left._data[0][1] * right._data[1][0] + left._data[0][2] * right._data[2][0] + left._data[0][3] * right._data[3][0];
        result._data[0][1] = left._data[0][0] * right._data[0][1] + left._data[0][1] * right._data[1][1] + left._data[0][2] * right._data[2][1] + left._data[0][3] * right._data[3][1];
        result._data[0][2] = left._data[0][0] * right._data[0][2] + left._data[0][1] * right._data[1][2] + left._data[0][2] * right._data[2][2] + left._data[0][3] * right._data[3][2];
        result._data[0][3] = left._data[0][0] * right._data[0][3] + left._data[0][1] * right._data[1][3] + left._data[0][2] * right._data[2][3] + left._data[0][3] * right._data[3][3];
        result._data[1][0] = left._data[1][0] * right._data[0][0] + left._data[1][1] * right._data[1][0] + left._data[1][2] * right._data[2][0] + left._data[1][3] * right._data[3][0];
        result._data[1][1] = left._data[1][0] * right._data[0][1] + left._data[1][1] * right._data[1][1] + left._data[1][2] * right._data[2][1] + left._data[1][3] * right._data[3][1];
        result._data[1][2] = left._data[1][0] * right._data[0][2] + left._data[1][1] * right._data[1][2] + left._data[1][2] * right._data[2][2] + left._data[1][3] * right._data[3][2];
        result._data[1][3] = left._data[1][0] * right._data[0][3] + left._data[1][1] * right._data[1][3] + left._data[1][2] * right._data[2][3] + left._data[1][3] * right._data[3][3];
        result._data[2][0] = left._data[2][0] * right._data[0][0] + left._data[2][1] * right._data[1][0] + left._data[2][2] * right._data[2][0] + left._data[2][3] * right._data[3][0];
        result._data[2][1] = left._data[2][0] * right._data[0][1] + left._data[2][1] * right._data[1][1] + left._data[2][2] * right._data[2][1] + left._data[2][3] * right._data[3][1];
        result._data[2][2] = left._data[2][0] * right._data[0][2] + left._data[2][1] * right._data[1][2] + left._data[2][2] * right._data[2][2] + left._data[2][3] * right._data[3][2];
        result._data[2][3] = left._data[2][0] * right._data[0][3] + left._data[2][1] * right._data[1][3] + left._data[2][2] * right._data[2][3] + left._data[2][3] * right._data[3][3];
        result._data[3][0] = left._data[3][0] * right._data[0][0] + left._data[3][1] * right._data[1][0] + left._data[3][2] * right._data[2][0] + left._data[3][3] * right._data[3][0];
        result._data[3][1] = left._data[3][0] * right._data[0][1] + left._data[3][1] * right._data[1][1] + left._data[3][2] * right._data[2][1] + left._data[3][3] * right._data[3][1];
        result._data[3][2] = left._data[3][0] * right._data[0][2] + left._data[3][1] * right._data[1][2] + left._data[3][2] * right._data[2][2] + left._data[3][3] * right._data[3][2];
        result._data[3][3] = left._data[3][0] * right._data[0][3] + left._data[3][1] * right._data[1][3] + left._data[3][2] * right._data[2][3] + left._data[3][3] * right._data[3][3];
        return result;
    }
    
    private static Matrix4 multiplyRef(final Matrix4 result, final float scalar, final Matrix4 transform) {
        result._data[0][0] = scalar * transform._data[0][0];
        result._data[0][1] = scalar * transform._data[0][1];
        result._data[0][2] = scalar * transform._data[0][2];
        result._data[0][3] = scalar * transform._data[0][3];
        result._data[1][0] = scalar * transform._data[1][0];
        result._data[1][1] = scalar * transform._data[1][1];
        result._data[1][2] = scalar * transform._data[1][2];
        result._data[1][3] = scalar * transform._data[1][3];
        result._data[2][0] = scalar * transform._data[2][0];
        result._data[2][1] = scalar * transform._data[2][1];
        result._data[2][2] = scalar * transform._data[2][2];
        result._data[2][3] = scalar * transform._data[2][3];
        result._data[3][0] = scalar * transform._data[3][0];
        result._data[3][1] = scalar * transform._data[3][1];
        result._data[3][2] = scalar * transform._data[3][2];
        result._data[3][3] = scalar * transform._data[3][3];
        return result;
    }
    
    public static Matrix4 multiply(final Matrix4 left, final Matrix4 right) {
        final Matrix4 result = new Matrix4();
        return multiplyRef(result, left, right);
    }
    
    public Matrix4 multiply(final Matrix4 other) {
        this.assign(multiply(this, other));
        return this;
    }
    
    public static Matrix4 multiply(final float scalar, final Matrix4 transform) {
        return multiplyRef(transform, scalar, transform);
    }
    
    public Matrix4 multiply(final float scalar) {
        return multiplyRef(this, scalar, this);
    }
    
    public static AOVector multiply(final Matrix4 matrix, final AOVector vector) {
        final float inverseW = 1.0f / (matrix._data[3][0] + matrix._data[3][1] + matrix._data[3][2] + matrix._data[3][3]);
        final float x = (matrix._data[0][0] * vector.getX() + matrix._data[0][1] * vector.getY() + matrix._data[0][2] * vector.getZ() + matrix._data[0][3]) * inverseW;
        final float y = (matrix._data[1][0] * vector.getX() + matrix._data[1][1] * vector.getY() + matrix._data[1][2] * vector.getZ() + matrix._data[1][3]) * inverseW;
        final float z = (matrix._data[2][0] * vector.getX() + matrix._data[2][1] * vector.getY() + matrix._data[2][2] * vector.getZ() + matrix._data[2][3]) * inverseW;
        return new AOVector(x, y, z);
    }
    
    public static Plane multiply(final Matrix4 transform, final Plane plane) {
        final AOVector planeNormal = plane.getNormal();
        final AOVector planePoint = (AOVector)planeNormal.clone();
        planePoint.scale(-1.0f * plane.getD());
        final AOVector newPoint = multiply(transform, planePoint);
        final AOVector newPointPlusNormal = multiply(transform, AOVector.add(planePoint, planeNormal));
        final AOVector newNormal = AOVector.sub(newPointPlusNormal, newPoint).normalize();
        return new Plane(newNormal, newPoint);
    }
    
    public boolean equals(final Matrix4 other) {
        return this.compareTo(other) == 0;
    }
    
    @Override
    public int compareTo(final Matrix4 other) {
        if (this == other) {
            return 0;
        }
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (this._data[i][j] != other._data[i][j]) {
                    return (this._data[i][j] < other._data[i][j]) ? -1 : 1;
                }
            }
        }
        return 0;
    }
    
    public static Matrix4 fromRotation(final Quaternion rot) {
        final Matrix4 rv = new Matrix4();
        rv.setRotation(rot);
        return rv;
    }
    
    public static Matrix4 fromTranslation(final AOVector vec) {
        final Matrix4 rv = new Matrix4();
        rv.setTranslation(vec);
        return rv;
    }
    
    public AOVector getTranslation() {
        return new AOVector(this._data[0][3], this._data[1][3], this._data[2][3]);
    }
    
    public void setTranslation(final AOVector vec) {
        this._data[0][3] = vec.getX();
        this._data[1][3] = vec.getY();
        this._data[2][3] = vec.getZ();
    }
    
    public void setRotation(final Quaternion rot) {
        final float tx = 2.0f * rot.getX();
        final float ty = 2.0f * rot.getY();
        final float tz = 2.0f * rot.getZ();
        final float twx = tx * rot.getW();
        final float twy = ty * rot.getW();
        final float twz = tz * rot.getW();
        final float txx = tx * rot.getX();
        final float txy = ty * rot.getX();
        final float txz = tz * rot.getX();
        final float tyy = ty * rot.getY();
        final float tyz = tz * rot.getY();
        final float tzz = tz * rot.getZ();
        this._data[0][0] = 1.0f - (tyy + tzz);
        this._data[0][1] = txy - twz;
        this._data[0][2] = txz + twy;
        this._data[1][0] = txy + twz;
        this._data[1][1] = 1.0f - (txx + tzz);
        this._data[1][2] = tyz - twx;
        this._data[2][0] = txz - twy;
        this._data[2][1] = tyz + twx;
        this._data[2][2] = 1.0f - (txx + tyy);
    }
    
    private Matrix4 getAdjoint() {
        final float val0 = this._data[1][1] * (this._data[2][2] * this._data[3][3] - this._data[3][2] * this._data[2][3]) - this._data[1][2] * (this._data[2][1] * this._data[3][3] - this._data[3][1] * this._data[2][3]) + this._data[1][3] * (this._data[2][1] * this._data[3][2] - this._data[3][1] * this._data[2][2]);
        final float val2 = -(this._data[0][1] * (this._data[2][2] * this._data[3][3] - this._data[3][2] * this._data[2][3]) - this._data[0][2] * (this._data[2][1] * this._data[3][3] - this._data[3][1] * this._data[2][3]) + this._data[0][3] * (this._data[2][1] * this._data[3][2] - this._data[3][1] * this._data[2][2]));
        final float val3 = this._data[0][1] * (this._data[1][2] * this._data[3][3] - this._data[3][2] * this._data[1][3]) - this._data[0][2] * (this._data[1][1] * this._data[3][3] - this._data[3][1] * this._data[1][3]) + this._data[0][3] * (this._data[1][1] * this._data[3][2] - this._data[3][1] * this._data[1][2]);
        final float val4 = -(this._data[0][1] * (this._data[1][2] * this._data[2][3] - this._data[2][2] * this._data[1][3]) - this._data[0][2] * (this._data[1][1] * this._data[2][3] - this._data[2][1] * this._data[1][3]) + this._data[0][3] * (this._data[1][1] * this._data[2][2] - this._data[2][1] * this._data[1][2]));
        final float val5 = -(this._data[1][0] * (this._data[2][2] * this._data[3][3] - this._data[3][2] * this._data[2][3]) - this._data[1][2] * (this._data[2][0] * this._data[3][3] - this._data[3][0] * this._data[2][3]) + this._data[1][3] * (this._data[2][0] * this._data[3][2] - this._data[3][0] * this._data[2][2]));
        final float val6 = this._data[0][0] * (this._data[2][2] * this._data[3][3] - this._data[3][2] * this._data[2][3]) - this._data[0][2] * (this._data[2][0] * this._data[3][3] - this._data[3][0] * this._data[2][3]) + this._data[0][3] * (this._data[2][0] * this._data[3][2] - this._data[3][0] * this._data[2][2]);
        final float val7 = -(this._data[0][0] * (this._data[1][2] * this._data[3][3] - this._data[3][2] * this._data[1][3]) - this._data[0][2] * (this._data[1][0] * this._data[3][3] - this._data[3][0] * this._data[1][3]) + this._data[0][3] * (this._data[1][0] * this._data[3][2] - this._data[3][0] * this._data[1][2]));
        final float val8 = this._data[0][0] * (this._data[1][2] * this._data[2][3] - this._data[2][2] * this._data[1][3]) - this._data[0][2] * (this._data[1][0] * this._data[2][3] - this._data[2][0] * this._data[1][3]) + this._data[0][3] * (this._data[1][0] * this._data[2][2] - this._data[2][0] * this._data[1][2]);
        final float val9 = this._data[1][0] * (this._data[2][1] * this._data[3][3] - this._data[3][1] * this._data[2][3]) - this._data[1][1] * (this._data[2][0] * this._data[3][3] - this._data[3][0] * this._data[2][3]) + this._data[1][3] * (this._data[2][0] * this._data[3][1] - this._data[3][0] * this._data[2][1]);
        final float val10 = -(this._data[0][0] * (this._data[2][1] * this._data[3][3] - this._data[3][1] * this._data[2][3]) - this._data[0][1] * (this._data[2][0] * this._data[3][3] - this._data[3][0] * this._data[2][3]) + this._data[0][3] * (this._data[2][0] * this._data[3][1] - this._data[3][0] * this._data[2][1]));
        final float val11 = this._data[0][0] * (this._data[1][1] * this._data[3][3] - this._data[3][1] * this._data[1][3]) - this._data[0][1] * (this._data[1][0] * this._data[3][3] - this._data[3][0] * this._data[1][3]) + this._data[0][3] * (this._data[1][0] * this._data[3][1] - this._data[3][0] * this._data[1][1]);
        final float val12 = -(this._data[0][0] * (this._data[1][1] * this._data[2][3] - this._data[2][1] * this._data[1][3]) - this._data[0][1] * (this._data[1][0] * this._data[2][3] - this._data[2][0] * this._data[1][3]) + this._data[0][3] * (this._data[1][0] * this._data[2][1] - this._data[2][0] * this._data[1][1]));
        final float val13 = -(this._data[1][0] * (this._data[2][1] * this._data[3][2] - this._data[3][1] * this._data[2][2]) - this._data[1][1] * (this._data[2][0] * this._data[3][2] - this._data[3][0] * this._data[2][2]) + this._data[1][2] * (this._data[2][0] * this._data[3][1] - this._data[3][0] * this._data[2][1]));
        final float val14 = this._data[0][0] * (this._data[2][1] * this._data[3][2] - this._data[3][1] * this._data[2][2]) - this._data[0][1] * (this._data[2][0] * this._data[3][2] - this._data[3][0] * this._data[2][2]) + this._data[0][2] * (this._data[2][0] * this._data[3][1] - this._data[3][0] * this._data[2][1]);
        final float val15 = -(this._data[0][0] * (this._data[1][1] * this._data[3][2] - this._data[3][1] * this._data[1][2]) - this._data[0][1] * (this._data[1][0] * this._data[3][2] - this._data[3][0] * this._data[1][2]) + this._data[0][2] * (this._data[1][0] * this._data[3][1] - this._data[3][0] * this._data[1][1]));
        final float val16 = this._data[0][0] * (this._data[1][1] * this._data[2][2] - this._data[2][1] * this._data[1][2]) - this._data[0][1] * (this._data[1][0] * this._data[2][2] - this._data[2][0] * this._data[1][2]) + this._data[0][2] * (this._data[1][0] * this._data[2][1] - this._data[2][0] * this._data[1][1]);
        return new Matrix4(val0, val2, val3, val4, val5, val6, val7, val8, val9, val10, val11, val12, val13, val14, val15, val16);
    }
    
    public Matrix4 getInverse() {
        final Matrix4 adjoint = this.getAdjoint();
        return multiply(1.0f / this.getDeterminant(), adjoint);
    }
    
    public float getDeterminant() {
        final float result = this._data[0][0] * (this._data[1][1] * (this._data[2][2] * this._data[3][3] - this._data[3][2] * this._data[2][3]) - this._data[1][2] * (this._data[2][1] * this._data[3][3] - this._data[3][1] * this._data[2][3]) + this._data[1][3] * (this._data[2][1] * this._data[3][2] - this._data[3][1] * this._data[2][2])) - this._data[0][1] * (this._data[1][0] * (this._data[2][2] * this._data[3][3] - this._data[3][2] * this._data[2][3]) - this._data[1][2] * (this._data[2][0] * this._data[3][3] - this._data[3][0] * this._data[2][3]) + this._data[1][3] * (this._data[2][0] * this._data[3][2] - this._data[3][0] * this._data[2][2])) + this._data[0][2] * (this._data[1][0] * (this._data[2][1] * this._data[3][3] - this._data[3][1] * this._data[2][3]) - this._data[1][1] * (this._data[2][0] * this._data[3][3] - this._data[3][0] * this._data[2][3]) + this._data[1][3] * (this._data[2][0] * this._data[3][1] - this._data[3][0] * this._data[2][1])) - this._data[0][3] * (this._data[1][0] * (this._data[2][1] * this._data[3][2] - this._data[3][1] * this._data[2][2]) - this._data[1][1] * (this._data[2][0] * this._data[3][2] - this._data[3][0] * this._data[2][2]) + this._data[1][2] * (this._data[2][0] * this._data[3][1] - this._data[3][0] * this._data[2][1]));
        return result;
    }
    
    private void assign(final Matrix4 other) {
        if (this == other) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this._data[i][j] = other._data[i][j];
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < 4; ++i) {
            if (i == 0) {
                sb.append("\n[");
            }
            else {
                sb.append(" ");
            }
            for (int j = 0; j < 4; ++j) {
                if (j != 0) {
                    sb.append(" ");
                }
                sb.append(this._data[i][j]);
            }
            if (i == 3) {
                sb.append("]\n");
            }
            else {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
