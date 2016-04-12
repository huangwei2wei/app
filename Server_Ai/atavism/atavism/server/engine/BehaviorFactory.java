// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import java.io.Serializable;

public interface BehaviorFactory extends Serializable
{
    Behavior generate();
}
