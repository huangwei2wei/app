// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.physics;

import atavism.server.math.AOVector;
import atavism.server.math.Plane;
import java.util.LinkedList;
import java.util.List;
import atavism.server.math.Matrix4;
import org.apache.log4j.Logger;

public class Shape<T extends Geometry>
{
    private static Logger logger;
    private T geometry;
    private Matrix4 transform;
    private List<Transform> transformChain;
    private boolean transformDirty;
    
    public Shape() {
        this.transformChain = new LinkedList<Transform>();
        this.transformDirty = false;
    }
    
    public void setGeometry(final T geom) {
        this.geometry = geom;
    }
    
    public T getGeometry() {
        return this.geometry;
    }
    
    public void addTransform(final Transform t) {
        this.transformChain.add(t);
        this.transformDirty = true;
    }
    
    public Matrix4 getTransform() {
        if (this.transformDirty) {
            this.updateTransform();
        }
        return this.transform;
    }
    
    public List<AOVector> computeIntersection(final Plane objectSpacePlane) {
        final Matrix4 geometryTransform = this.getTransform();
        final Plane geometrySpacePlane = Matrix4.multiply(geometryTransform.getInverse(), objectSpacePlane);
        final List<AOVector> points = this.geometry.computeIntersection(geometrySpacePlane);
        final List<AOVector> rv = new LinkedList<AOVector>();
        if (Shape.logger.isDebugEnabled()) {
            Shape.logger.debug((Object)("geometry transform: " + geometryTransform.toString()));
            Shape.logger.debug((Object)("geometryTransform.getInverse(): " + geometryTransform.getInverse().toString()));
            Shape.logger.debug((Object)("geometrySpacePlane: " + geometrySpacePlane.toString()));
            Shape.logger.debug((Object)("objectSpacePlane: " + objectSpacePlane.toString()));
            for (int i = 0; i < points.size(); ++i) {
                Shape.logger.debug((Object)("added geometry transformed point: " + Matrix4.multiply(geometryTransform, points.get(i))));
            }
        }
        for (int i = 0; i < points.size(); ++i) {
            rv.add(Matrix4.multiply(geometryTransform, points.get(i)));
        }
        return rv;
    }
    
    private void updateTransform() {
        this.transform = new Matrix4();
        for (int i = 0; i < this.transformChain.size(); ++i) {
            final Matrix4 entryMatrix = this.transformChain.get(i).getTransform();
            this.transform = Matrix4.multiply(entryMatrix, this.transform);
        }
        this.transformDirty = false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Matrix4 tmp = this.getTransform();
        sb.append("Transform Chain: ");
        for (int i = 0; i < this.transformChain.size(); ++i) {
            final Transform t = this.transformChain.get(i);
            sb.append("  ");
            sb.append(t);
        }
        sb.append("Transform: ");
        sb.append(tmp.toString());
        sb.append("\n");
        sb.append("Geometry: ");
        sb.append(this.geometry.toString());
        return sb.toString();
    }
    
    static {
        Shape.logger = Logger.getLogger((Class)Shape.class);
    }
}
