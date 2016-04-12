// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.engine;

import java.util.Set;
import com.app.server.atavism.server.math.Quaternion;
import com.app.server.atavism.server.objects.AOObject;
import java.io.Serializable;

public interface WorldNode extends Locatable, Interpolatable, Serializable
{
    AOObject getObject();
    
    void setObject(final AOObject p0);
    
    WorldNode getParent();
    
    void setParent(final WorldNode p0);
    
    Quaternion getOrientation();
    
    void setOrientation(final Quaternion p0);
    
    Set<WorldNode> getChildren();
    
    void setChildren(final Set<WorldNode> p0);
    
    void addChild(final WorldNode p0);
    
    void removeChild(final WorldNode p0);
    
    boolean isSpawned();
}
