// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.events;

import java.util.Iterator;
import atavism.server.engine.Engine;
import java.util.Collection;
import java.util.LinkedList;
import atavism.server.objects.Entity;
import atavism.server.objects.Road;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.LockFactory;
import atavism.server.math.Point;
import java.util.List;
import java.util.concurrent.locks.Lock;
import atavism.server.engine.Event;

public class RoadEvent extends Event
{
    private String roadName;
    private Lock lock;
    private List<Point> points;
    
    public RoadEvent() {
        this.roadName = null;
        this.lock = LockFactory.makeLock("RoadEventLock");
        this.points = null;
    }
    
    public RoadEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.roadName = null;
        this.lock = LockFactory.makeLock("RoadEventLock");
        this.points = null;
    }
    
    public RoadEvent(final Road road) {
        super(road);
        this.roadName = null;
        this.lock = LockFactory.makeLock("RoadEventLock");
        this.points = null;
        this.setPoints(road.getPoints());
        this.setRoadName(road.getName());
    }
    
    @Override
    public String getName() {
        return "RoadEvent";
    }
    
    public void setRoadName(final String name) {
        this.roadName = name;
    }
    
    public String getRoadName() {
        return this.roadName;
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
    
    @Override
    public AOByteBuffer toBytes() {
        final int msgId = Engine.getEventServer().getEventID(this.getClass());
        final AOByteBuffer buf = new AOByteBuffer(400);
        this.lock.lock();
        try {
            buf.putOID(this.getObjectOid());
            buf.putInt(msgId);
            buf.putString(this.getRoadName());
            buf.putInt(this.points.size());
            for (final Point p : this.points) {
                buf.putPoint(p);
            }
            buf.flip();
            return buf;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void parseBytes(final AOByteBuffer buf) {
        buf.rewind();
        this.setObjectOid(buf.getOID());
        buf.getInt();
        this.setRoadName(buf.getString());
        this.lock.lock();
        try {
            this.points = new LinkedList<Point>();
            for (int numPoints = buf.getInt(), i = 0; i < numPoints; ++i) {
                final Point p = buf.getPoint();
                this.points.add(p);
            }
        }
        finally {
            this.lock.unlock();
        }
    }
}
