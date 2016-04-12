// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import java.io.Serializable;

public interface IPropertyMessage
{
    void setProperty(final String p0, final Serializable p1);
    
    void removeProperty(final String p0);
    
    Serializable getProperty(final String p0);
}
