// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.engine.BasicWorldNode;
import atavism.server.engine.OID;
import atavism.server.engine.InterpolatedWorldNode;

public interface EntityWithWorldNode
{
    InterpolatedWorldNode getWorldNode();
    
    OID getOid();
    
    void setOid(final OID p0);
    
    void setDirLocOrient(final BasicWorldNode p0);
    
    void setWorldNode(final InterpolatedWorldNode p0);
    
    Entity getEntity();
}
