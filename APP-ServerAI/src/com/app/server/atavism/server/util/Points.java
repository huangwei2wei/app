// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.util;

import com.app.server.atavism.server.math.Point;
import java.util.Random;

public class Points
{
    private static Random random;
    
    public static Point findNearby(final Point point, final int radius) {
        final float radiusSQ = radius * radius;
        float distSQ;
        float dx;
        float dz;
        do {
            dx = Points.random.nextFloat() * (2 * radius) - radius;
            dz = Points.random.nextFloat() * (2 * radius) - radius;
            distSQ = dx * dx + dz * dz;
        } while (distSQ > radiusSQ);
        final Point newPoint = (Point)point.clone();
        newPoint.add(dx, 0.0f, dz);
        return newPoint;
    }
    
    public static Point findAdjacent(final Point point, final int radius) {
        final double angle = 6.283185307179586 * Points.random.nextDouble();
        final int dx = (int)(Math.sin(angle) * radius);
        final int dz = (int)(Math.cos(angle) * radius);
        final Point newPoint = (Point)point.clone();
        newPoint.add(dx, 0, dz);
        return newPoint;
    }
    
    public static boolean isClose(final Point p1, final Point p2, final int radius) {
        final double dx = p1.getX() - p2.getX();
        final double dz = p1.getZ() - p2.getZ();
        final double distSQ = dx * dx + dz * dz;
        final double radiusSQ = radius * radius;
        return distSQ <= radiusSQ;
    }
    
    public static Point offset(final Point p, final int x, final int z) {
        final Point point = (Point)p.clone();
        point.add(x, 0, z);
        return point;
    }
    
    static {
        Points.random = new Random();
    }
}
