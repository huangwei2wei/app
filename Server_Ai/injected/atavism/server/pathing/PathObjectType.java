// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.pathing;

import atavism.server.network.AOByteBuffer;
import atavism.server.marshalling.Marshallable;
import java.io.Serializable;

public class PathObjectType implements Serializable, Cloneable, Marshallable
{
    private String name;
    private float height;
    private float width;
    private float maxClimbSlope;
    private static final long serialVersionUID = 1L;
    
    public PathObjectType() {
    }
    
    public PathObjectType(final String name, final float height, final float width, final float maxClimbSlope) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.maxClimbSlope = maxClimbSlope;
    }
    
    public Object clone() {
        return new PathObjectType(this.name, this.height, this.width, this.maxClimbSlope);
    }
    
    public String getName() {
        return this.name;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getMaxClimbSlope() {
        return this.maxClimbSlope;
    }
    
    public void marshalObject(final AOByteBuffer buf) {
        byte flag_bits = 0;
        if (this.name != null && this.name != "") {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.name != null && this.name != "") {
            buf.putString(this.name);
        }
        buf.putFloat(this.height);
        buf.putFloat(this.width);
        buf.putFloat(this.maxClimbSlope);
    }
    
    public Object unmarshalObject(final AOByteBuffer buf) {
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.name = buf.getString();
        }
        this.height = buf.getFloat();
        this.width = buf.getFloat();
        this.maxClimbSlope = buf.getFloat();
        return this;
    }
}
