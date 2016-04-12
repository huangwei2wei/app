// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.io.Serializable;

public class PlayerFactionData implements Serializable
{
    protected int faction;
    protected String name;
    protected int reputation;
    protected boolean atWar;
    protected String group;
    protected int category;
    private static final long serialVersionUID = 1L;
    
    public PlayerFactionData() {
        this.faction = -1;
        this.reputation = 0;
        this.atWar = false;
        this.group = null;
        this.category = 0;
    }
    
    public PlayerFactionData(final int faction, final String name, final int reputation, final String group, final int category) {
        this.faction = -1;
        this.reputation = 0;
        this.atWar = false;
        this.group = null;
        this.category = 0;
        this.faction = faction;
        this.name = name;
        this.reputation = reputation;
        this.group = group;
        this.category = category;
        Log.debug("FACTION: Player faction data created for faction " + name);
    }
    
    public void updateReputation(final int delta) {
        this.reputation += delta;
    }
    
    public int getFaction() {
        return this.faction;
    }
    
    public void setFaction(final int faction) {
        this.faction = faction;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getReputation() {
        return this.reputation;
    }
    
    public void setReputation(final int reputation) {
        this.reputation = reputation;
    }
    
    public boolean getAtWar() {
        return this.atWar;
    }
    
    public void setAtWar(final boolean atWar) {
        this.atWar = atWar;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public void setGroup(final String group) {
        this.group = group;
    }
    
    public int getCategory() {
        return this.category;
    }
    
    public void setCategory(final int category) {
        this.category = category;
    }
}
