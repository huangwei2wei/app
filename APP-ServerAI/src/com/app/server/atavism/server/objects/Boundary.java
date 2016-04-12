// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.app.server.atavism.server.util.LockFactory;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import com.app.server.atavism.server.math.Point;
import java.util.List;
import com.app.server.atavism.server.math.Geometry;
import java.io.Serializable;

public class Boundary implements Cloneable, Serializable
{
    protected Geometry boundingBox;
    List<Point> pointList;
    transient Lock lock;
    String name;
    private static final long serialVersionUID = 1L;
    
    public Boundary() {
        this.boundingBox = null;
        this.pointList = new LinkedList<Point>();
        this.lock = null;
        this.name = null;
        this.setupTransient();
    }
    
    public Boundary(final String name) {
        this.boundingBox = null;
        this.pointList = new LinkedList<Point>();
        this.lock = null;
        this.name = null;
        this.setupTransient();
        this.name = name;
    }
    
    public Boundary(final List<Point> points) {
        this.boundingBox = null;
        this.pointList = new LinkedList<Point>();
        this.lock = null;
        this.name = null;
        this.setupTransient();
        this.setPoints(points);
    }
    
    void setupTransient() {
        this.lock = LockFactory.makeLock("BoundaryLock");
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setupTransient();
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        String s = "[Boundary: name=" + this.name;
        this.lock.lock();
        try {
            for (final Point p : this.pointList) {
                s = s + " p=" + p;
            }
            return s + "]";
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object clone() {
        this.lock.lock();
        try {
            final Boundary b = new Boundary(this.pointList);
            b.setName(this.getName());
            return b;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void setPoints(final List<Point> points) {
        this.lock.lock();
        try {
            this.pointList = new LinkedList<Point>(points);
            this.boundingBox = null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public List<Point> getPoints() {
        this.lock.lock();
        try {
            return new LinkedList<Point>(this.pointList);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void addPoint(final Point p) {
        this.lock.lock();
        try {
            this.pointList.add(p);
            this.boundingBox = null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Geometry getBoundingBox() {
        this.lock.lock();
        try {
            if (this.boundingBox != null) {
                return this.boundingBox;
            }
            float minX = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float minZ = Float.MAX_VALUE;
            float maxZ = Float.MIN_VALUE;
            for (final Point p : this.pointList) {
                if (p.getX() < minX) {
                    minX = p.getX();
                }
                if (p.getX() > maxX) {
                    maxX = p.getX();
                }
                if (p.getZ() < minZ) {
                    minZ = p.getZ();
                }
                if (p.getZ() > maxZ) {
                    maxZ = p.getZ();
                }
            }
            return this.boundingBox = new Geometry(minX, maxX, minZ, maxZ);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean contains(final Point p) {
        int count = 0;
        this.lock.lock();
        try {
            Float maxZ = null;
            Float maxX = null;
            for (final Point tmpP : this.pointList) {
                if (maxZ == null) {
                    maxZ = tmpP.getZ();
                    maxX = tmpP.getX();
                }
                else {
                    if (tmpP.getZ() > maxZ) {
                        maxZ = tmpP.getZ();
                    }
                    if (tmpP.getX() <= maxX) {
                        continue;
                    }
                    maxX = tmpP.getX();
                }
            }
            if (maxZ == null || maxX == null) {
                return false;
            }
            Point prevPoint = null;
            Point curPoint = null;
            Point firstPoint = null;
            final Iterator<Point> iter = this.pointList.iterator();
            while (iter.hasNext()) {
                if (curPoint == null) {
                    curPoint = (firstPoint = iter.next());
                }
                else {
                    prevPoint = curPoint;
                    curPoint = iter.next();
                    final Vector2 p2 = new Vector2(prevPoint.getX(), prevPoint.getZ());
                    final Vector2 p3 = new Vector2(curPoint.getX(), curPoint.getZ());
                    final Vector2 p4 = new Vector2(p.getX(), p.getZ());
                    final Vector2 p5 = new Vector2(maxX, maxZ + 1.0f);
                    if (!IntersectSegments(p2, p3, p4, p5)) {
                        continue;
                    }
                    ++count;
                }
            }
            if (IntersectSegments(new Vector2(firstPoint.getX(), firstPoint.getZ()), new Vector2(curPoint.getX(), curPoint.getZ()), new Vector2(p.getX(), p.getZ()), new Vector2(maxX, maxZ + 1.0f))) {
                ++count;
            }
            final boolean rv = count % 2 != 0;
            return rv;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private static boolean IntersectSegments(final Vector2 p1, final Vector2 p2, final Vector2 p3, final Vector2 p4) {
        final double den = (p4.y - p3.y) * (p2.x - p1.x) - (p4.x - p3.x) * (p2.y - p1.y);
        final double t1num = (p4.x - p3.x) * (p1.y - p3.y) - (p4.y - p3.y) * (p1.x - p3.x);
        final double t2num = (p2.x - p1.x) * (p1.y - p3.y) - (p2.y - p1.y) * (p1.x - p3.x);
        if (den == 0.0) {
            return false;
        }
        final double t1 = t1num / den;
        final double t2 = t2num / den;
        return t1 >= 0.0 && t1 < 1.0 && t2 >= 0.0 && t2 <= 1.0;
    }
    
    public static Boundary getMaxBoundary() {
        final int min = Integer.MIN_VALUE;
        final int max = Integer.MAX_VALUE;
        final Boundary b = new Boundary();
        b.addPoint(new Point(min, 0.0f, max));
        b.addPoint(new Point(max, 0.0f, max));
        b.addPoint(new Point(max, 0.0f, min));
        b.addPoint(new Point(min, 0.0f, min));
        return b;
    }
}
