// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.engine.OID;
import com.app.server.atavism.server.math.Point;

public class RoadSegment extends AOObject
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
    
    @Override
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
}
