// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.arenas;

import atavism.server.util.Log;
import java.util.ArrayList;
import java.io.Serializable;

public class ArenaCategory implements Serializable
{
    protected int categoryID;
    protected ArrayList<String> skins;
    private static final long serialVersionUID = 1L;
    
    public ArenaCategory(final int categoryID, final ArrayList<String> skins) {
        Log.debug("ARENA TEMPLATE: starting arenaTemplate creation");
        this.categoryID = categoryID;
        this.skins = skins;
        Log.debug("ARENA TEMPLATE: finished arenaTemplate creation");
    }
    
    public int getCategoryID() {
        return this.categoryID;
    }
    
    public void setCategoryID(final int categoryID) {
        this.categoryID = categoryID;
    }
    
    public ArrayList<String> getSkins() {
        return this.skins;
    }
    
    public void setSkins(final ArrayList<String> skins) {
        this.skins = skins;
    }
}
