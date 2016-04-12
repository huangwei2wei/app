// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import java.io.Serializable;

public class DisplayState implements Serializable
{
    public static DisplayState IN_COMBAT;
    public static DisplayState NON_COMBAT;
    int id;
    private static final long serialVersionUID = 1L;
    
    public DisplayState() {
        this.id = -1;
    }
    
    public DisplayState(final int id) {
        this.id = -1;
        this.id = id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    @Override
    public int hashCode() {
        return this.getId();
    }
    
    @Override
    public String toString() {
        return "[DisplayState id=" + this.getId() + "]";
    }
    
    @Override
    public boolean equals(final Object other) {
        final DisplayState otherDS = (DisplayState)other;
        return this.id == otherDS.getId();
    }
    
    static {
        DisplayState.IN_COMBAT = new DisplayState(1);
        DisplayState.NON_COMBAT = new DisplayState(2);
    }
}
