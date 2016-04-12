// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Iterator;
import java.util.List;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class PathData implements Serializable, Cloneable, Marshallable
{
    int version;
    List<PathObject> pathObjects;
    private static final long serialVersionUID = 1L;
    
    public PathData() {
    }
    
    public PathData(final int version, final List<PathObject> pathObjects) {
        this.version = version;
        this.pathObjects = pathObjects;
    }
    
    @Override
    public String toString() {
        return "[PathData " + this.pathObjects.size() + " path objects]";
    }
    
    public Object clone() {
        return new PathData(this.version, this.getPathObjects());
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public List<PathObject> getPathObjects() {
        return this.pathObjects;
    }
    
    public PathObject getPathObjectForType(final String type) {
        for (final PathObject pathObject : this.pathObjects) {
            if (pathObject.getType().equals(type)) {
                return pathObject;
            }
        }
        return null;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.pathObjects != null) {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        buf.putInt(this.version);
        if (this.pathObjects != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.pathObjects);
        }
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        this.version = buf.getInt();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.pathObjects = (List<PathObject>)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
