// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Log;
import atavism.server.math.AOVector;
import atavism.server.math.Geometry;
import java.util.HashMap;
import atavism.server.util.Logger;
import java.util.Map;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class PathInfo implements Serializable, Cloneable, Marshallable
{
    private Map<String, PathObjectType> typeDictionary;
    private Map<String, PathData> pathDictionary;
    protected static final Logger log;
    private static final long serialVersionUID = 1L;
    
    public PathInfo(final Map<String, PathObjectType> typeDictionary, final Map<String, PathData> pathDictionary) {
        this.typeDictionary = typeDictionary;
        this.pathDictionary = pathDictionary;
    }
    
    public PathInfo() {
        this.typeDictionary = new HashMap<String, PathObjectType>();
        this.pathDictionary = new HashMap<String, PathData>();
    }
    
    public void performPathingTest(final Geometry geometry) {
        this.performPathingTest1(geometry);
        this.performPathingTest2(geometry);
        this.performPathingTest3(geometry);
        this.performPathingTest4(geometry);
    }
    
    protected void performPathingTest1(final Geometry geometry) {
        PathInfo.log.debug("PATHING TEST 1");
        final String type = "Generic";
        final AOVector loc1 = this.getCenterOfPolygon(type, "meetinghouse1", 6);
        final AOVector loc2 = this.getCenterOfPolygon(type, "meetinghouse2", 7);
        final PathFinderValue value = this.performSearch(type, geometry, loc1, loc2);
        this.showTestResult(value, loc1, loc2);
    }
    
    protected void performPathingTest2(final Geometry geometry) {
        PathInfo.log.debug("PATHING TEST 2");
        final AOVector loc1 = new AOVector(-146466.0f, 25908.0f, -302033.0f);
        final String type = "Generic";
        final AOVector loc2 = this.getCenterOfPolygon(type, "meetinghouse2", 7);
        final PathFinderValue value = this.performSearch(type, geometry, loc1, loc2);
        this.showTestResult(value, loc1, loc2);
    }
    
    protected void performPathingTest3(final Geometry geometry) {
        PathInfo.log.debug("PATHING TEST 3");
        final AOVector loc1 = new AOVector(-123465.0f, 27281.0f, -303274.0f);
        final String type = "Generic";
        final AOVector loc2 = this.getCenterOfPolygon(type, "meetinghouse2", 7);
        final PathFinderValue value = this.performSearch(type, geometry, loc1, loc2);
        this.showTestResult(value, loc1, loc2);
    }
    
    protected void performPathingTest4(final Geometry geometry) {
        PathInfo.log.debug("PATHING TEST 4");
        final AOVector loc1 = new AOVector(-123465.0f, 27281.0f, -303274.0f);
        final AOVector loc2 = new AOVector(-136465.0f, 27597.0f, -214821.0f);
        final String type = "Generic";
        final PathFinderValue value = this.performSearch(type, geometry, loc1, loc2);
        this.showTestResult(value, loc1, loc2);
    }
    
    protected AOVector getCenterOfPolygon(final String type, final String modelName, final int polygonIndex) {
        if (Log.loggingDebug) {
            PathInfo.log.debug("Getting PathData for model " + modelName);
        }
        final PathData pd = this.pathDictionary.get(modelName);
        final PathObject po = pd.getPathObjectForType(type);
        final PathPolygon cv = po.getCVPolygon(polygonIndex);
        final AOVector loc = cv.getCentroid();
        if (Log.loggingDebug) {
            PathInfo.log.debug(modelName + " polygon " + polygonIndex + " centroid is " + loc);
        }
        return loc;
    }
    
    protected PathFinderValue performSearch(final String type, final Geometry geometry, final AOVector loc1, final AOVector loc2) {
        PathInfo.log.debug("Creating PathSearcher");
        PathSearcher.createPathSearcher(this, geometry);
        PathInfo.log.debug("Calling PathSearcher.findPath");
        return PathSearcher.findPath(type, loc1, loc2, true);
    }
    
    protected void showTestResult(final PathFinderValue value, final AOVector loc1, final AOVector loc2) {
        if (Log.loggingDebug) {
            PathInfo.log.debug("Plotting path from " + loc1 + " to " + loc2 + ", PathResult was " + value.getResult().toString());
            PathInfo.log.debug("Calculated path is " + value.stringPath(0));
            PathInfo.log.debug("TerrainString is '" + value.getTerrainString() + "'");
        }
    }
    
    public float getTypeHalfWidth(final String type) {
        if (this.typeDictionary.containsKey(type)) {
            return this.typeDictionary.get(type).getWidth();
        }
        PathInfo.log.error("In getTypeHalfWidth, can't find path object type '" + type + "'!");
        return 100.0f;
    }
    
    public Object clone() {
        return new PathInfo(this.typeDictionary, this.pathDictionary);
    }
    
    public boolean pathObjectTypeSupported(final String type) {
        return this.typeDictionary.containsKey(type);
    }
    
    public void setTypeDictionary(final Map<String, PathObjectType> typeDictionary) {
        this.typeDictionary = typeDictionary;
    }
    
    public Map<String, PathObjectType> getTypeDictionary() {
        return this.typeDictionary;
    }
    
    public void setPathDictionary(final Map<String, PathData> pathDictionary) {
        this.pathDictionary = pathDictionary;
    }
    
    public Map<String, PathData> getPathDictionary() {
        return this.pathDictionary;
    }
    
    static {
        log = new Logger("PathInfo");
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.typeDictionary != null) {
            flag_bits = 1;
        }
        if (this.pathDictionary != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.typeDictionary != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.typeDictionary);
        }
        if (this.pathDictionary != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.pathDictionary);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.typeDictionary = (Map<String, PathObjectType>)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.pathDictionary = (Map<String, PathData>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
