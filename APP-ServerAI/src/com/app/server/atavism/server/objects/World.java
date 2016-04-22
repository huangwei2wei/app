// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.objects;

import com.app.server.atavism.server.util.LockFactory;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.List;
import com.app.server.atavism.server.math.Geometry;
import com.app.server.atavism.server.engine.OID;

public class World
{
    public static OID DEBUG_OID;
    private static Geometry worldGeometry;
    private static Geometry localGeo;
    private static List<String> uiThemes;
    public static Boolean FollowsTerrainOverride;
    public static int perceiverRadius;
    private static int locTolerance;
    private static PermissionFactory defaultPermissionFactory;
    private static Lock staticLock;
    
    public static void setGeometry(final Geometry g) {
        World.worldGeometry = g;
    }
    
    public static Geometry getGeometry() {
        return World.worldGeometry;
    }
    
    public static void setLocalGeometry(final Geometry g) {
        World.localGeo = g;
    }
    
    public static Geometry getLocalGeometry() {
        return World.localGeo;
    }
    
    public static void addTheme(final String theme) {
        World.staticLock.lock();
        try {
            World.uiThemes.add(theme);
        }
        finally {
            World.staticLock.unlock();
        }
    }
    
    public static void setTheme(final String theme) {
        World.staticLock.lock();
        try {
            World.uiThemes.clear();
            World.uiThemes.add(theme);
        }
        finally {
            World.staticLock.unlock();
        }
    }
    
    public static void clearThemes() {
        World.staticLock.lock();
        try {
            World.uiThemes.clear();
        }
        finally {
            World.staticLock.unlock();
        }
    }
    
    public static List<String> getThemes() {
        World.staticLock.lock();
        try {
            return new LinkedList<String>(World.uiThemes);
        }
        finally {
            World.staticLock.unlock();
        }
    }
    
    public static void setLocTolerance(final int dist) {
        World.locTolerance = dist;
    }
    
    public static int getLocTolerance() {
        return World.locTolerance;
    }
    
    public static void setDefaultPermission(final PermissionFactory factory) {
        World.defaultPermissionFactory = factory;
    }
    
    public static PermissionFactory getDefaultPermission() {
        return World.defaultPermissionFactory;
    }
    
    static {
        World.DEBUG_OID = OID.fromLong(2221998L);
        World.worldGeometry = null;
        World.localGeo = null;
        World.uiThemes = new LinkedList<String>();
        World.FollowsTerrainOverride = null;
        World.perceiverRadius = 100;
        World.locTolerance = 10;
        World.defaultPermissionFactory = null;
        World.staticLock = LockFactory.makeLock("StaticWorldLock");
    }
}
