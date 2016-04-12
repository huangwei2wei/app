// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.server.math.Point;
import atavism.server.marshalling.Marshallable;

public class RoadSegment extends AOObject implements Marshallable
{
    Point start;
    Point end;
    private static final long serialVersionUID = 1L;
    
    public RoadSegment() {
    }
    
    public RoadSegment(final OID oid) {
        super(oid);
    }
    
    public RoadSegment(final String name, final Point start, final Point end) {
        super(name);
        this.setStart(start);
        this.setEnd(end);
    }
    
    @Override
    public String toString() {
        return "[RoadSegment: " + super.toString() + " start=" + this.getStart() + ", end=" + this.getEnd() + "]";
    }
    
    public ObjectType getType() {
        return ObjectTypes.road;
    }
    
    public void setStart(final Point start) {
        this.start = (Point)start.clone();
    }
    
    public Point getStart() {
        return (Point)this.start.clone();
    }
    
    public void setEnd(final Point end) {
        this.end = (Point)end.clone();
    }
    
    public Point getEnd() {
        return (Point)this.end.clone();
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.start != null) {
            flag_bits = 1;
        }
        if (this.end != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.start != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.start);
        }
        if (this.end != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.end);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.start = (Point)MarshallingRuntime.unmarshalObject(buf);
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.end = (Point)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
