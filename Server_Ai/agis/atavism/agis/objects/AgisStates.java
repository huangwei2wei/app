// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public enum AgisStates
{
    Dead("Dead", 0, "deadState"), 
    PVP("PVP", 1, "pvpstate"), 
    Combat("Combat", 2, "combatstate"), 
    QuestAvailable("QuestAvailable", 3, "questavailable"), 
    QuestInProgress("QuestInProgress", 4, "questinprogress"), 
    QuestConcludable("QuestConcludable", 5, "questconcludable"), 
    Attackable("Attackable", 6, "attackable"), 
    Lootable("Lootable", 7, "lootable"), 
    Stunned("Stunned", 8, "stunned"), 
    Movement("Movement", 9, "movement"), 
    ItemAvailable("ItemAvailable", 10, "itemavailable"), 
    ItemsToSell("ItemsToSell", 11, "itemstosell");
    
    String str;
    
    private AgisStates(final String s, final int n, final String encodeStr) {
        this.str = null;
        this.str = encodeStr;
    }
    
    @Override
    public String toString() {
        return this.str;
    }
}
