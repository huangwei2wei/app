// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import atavism.server.engine.Namespace;
import atavism.server.math.Point;
import java.util.LinkedList;
import atavism.server.marshalling.Marshallable;

public class Road extends Entity implements Marshallable
{
    Integer halfWidth;
    protected LinkedList<Point> points;
    public static int maxSegmentLengthMillis;
    private static final long serialVersionUID = 1L;
    
    public Road() {
        this.halfWidth = null;
        this.points = new LinkedList<Point>();
        this.setNamespace(Namespace.WORLD_MANAGER);
    }
    
    public Road(final String name) {
        super(name);
        this.halfWidth = null;
        this.points = new LinkedList<Point>();
        this.setNamespace(Namespace.WORLD_MANAGER);
    }
    
    @Override
    public String toString() {
        String s = "[Road: name=" + this.getName() + ", halfWidth=" + this.getHalfWidth();
        for (final Point p : this.getPoints()) {
            s = s + " " + p;
        }
        return s + "]";
    }
    
    public void setHalfWidth(final Integer width) {
        this.halfWidth = width;
    }
    
    public Integer getHalfWidth() {
        return this.halfWidth;
    }
    
    public void setPoints(final List<Point> points) {
        this.lock.lock();
        try {
            this.points = new LinkedList<Point>(points);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<Point> getPoints() {
        this.lock.lock();
        try {
            return new LinkedList<Point>(this.points);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addPoint(final Point point) {
        this.lock.lock();
        try {
            this.points.add(point);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<RoadSegment> generateRoadSegments() {
        this.lock.lock();
        try {
            final List<RoadSegment> list = new LinkedList<RoadSegment>();
            final Iterator<Point> iter = this.points.iterator();
            Point lastPoint = null;
            while (iter.hasNext()) {
                if (lastPoint == null) {
                    lastPoint = iter.next();
                }
                else {
                    final Point curPoint = iter.next();
                    final RoadSegment seg = new RoadSegment(this.getName(), lastPoint, curPoint);
                    list.add(seg);
                    lastPoint = curPoint;
                }
            }
            return list;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    static {
        Road.maxSegmentLengthMillis = 10;
    }
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.halfWidth != null) {
            flag_bits = 1;
        }
        if (this.points != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.halfWidth != null) {
            buf.putInt((int)this.halfWidth);
        }
        if (this.points != null) {
            MarshallingRuntime.marshalLinkedList(buf, (Object)this.points);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.halfWidth = buf.getInt();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.points = (LinkedList<Point>)MarshallingRuntime.unmarshalLinkedList(buf);
        }
        return this;
    }
}
