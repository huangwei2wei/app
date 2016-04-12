// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.HashMap;
import java.util.Map;
import atavism.server.math.Quaternion;
import atavism.server.math.Point;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class Marker implements Serializable, Cloneable, Marshallable
{
    public static final long PROP_POINT = 1L;
    public static final long PROP_ORIENTATION = 2L;
    public static final long PROP_PROPERTIES = 4L;
    public static final long PROP_ALL = 7L;
    public static final ObjectType OBJECT_TYPE;
    private Point point;
    private Quaternion orientation;
    private Map<String, Serializable> properties;
    private static final long serialVersionUID = 1L;
    
    public Marker() {
    }
    
    public Marker(final Point point) {
        this.setPoint(point);
    }
    
    public Marker(final Point point, final Quaternion orient) {
        this.setPoint(point);
        this.setOrientation(orient);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Marker copy = (Marker)super.clone();
        if (copy.point != null) {
            copy.point = (Point)copy.point.clone();
        }
        if (copy.orientation != null) {
            copy.orientation = (Quaternion)copy.orientation.clone();
        }
        if (copy.properties != null) {
            copy.properties = new HashMap<String, Serializable>(copy.properties);
        }
        return copy;
    }
    
    @Override
    public String toString() {
        return "[Marker pt=" + this.point + " ori=" + this.orientation + " " + ((this.properties == null) ? "0 props" : (this.properties.size() + " props]"));
    }
    
    public Point getPoint() {
        return this.point;
    }
    
    public void setPoint(final Point point) {
        this.point = point;
    }
    
    public Quaternion getOrientation() {
        return this.orientation;
    }
    
    public void setOrientation(final Quaternion orient) {
        this.orientation = orient;
    }
    
    public Serializable getProperty(final String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }
    
    public Serializable setProperty(final String key, final Serializable value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Serializable>();
        }
        return this.properties.put(key, value);
    }
    
    public Map<String, Serializable> getPropertyMapRef() {
        return this.properties;
    }
    
    public void setProperties(final Map<String, Serializable> props) {
        if (props != null) {
            this.properties = new HashMap<String, Serializable>(props);
        }
        else {
            this.properties = null;
        }
    }
    
    static {
        OBJECT_TYPE = ObjectType.intern((short)21, "Marker");
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.point != null) {
            flag_bits = 1;
        }
        if (this.orientation != null) {
            flag_bits |= 0x2;
        }
        if (this.properties != null) {
            flag_bits |= 0x4;
        }
        buf.putByte(flag_bits);
        if (this.point != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.point);
        }
        if (this.orientation != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.orientation);
        }
        if (this.properties != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.properties);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.point = (Point)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.orientation = (Quaternion)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x4) != 0x0) {
            this.properties = (Map<String, Serializable>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
