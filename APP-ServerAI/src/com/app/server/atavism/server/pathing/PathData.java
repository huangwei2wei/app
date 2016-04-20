// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.pathing;

import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

public class PathData implements Serializable, Cloneable
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
}
