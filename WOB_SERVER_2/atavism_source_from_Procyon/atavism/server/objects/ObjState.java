// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.Serializable;

public abstract class ObjState implements Serializable
{
    public abstract Integer getIntValue();
    
    public abstract String getStateName();
    
    @Override
    public int hashCode() {
        return this.getStateName().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof ObjState && this.getStateName().equals(((ObjState)other).getStateName());
    }
}
