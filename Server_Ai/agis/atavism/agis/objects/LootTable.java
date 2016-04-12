// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.util.Random;
import java.util.ArrayList;

public class LootTable
{
    int id;
    String name;
    ArrayList<Integer> items;
    ArrayList<Integer> itemCounts;
    ArrayList<Integer> itemChances;
    private static final long serialVersionUID = 1L;
    
    public LootTable() {
        this.items = new ArrayList<Integer>();
        this.itemCounts = new ArrayList<Integer>();
        this.itemChances = new ArrayList<Integer>();
    }
    
    public LootTable(final int id, final String name, final ArrayList<Integer> items, final ArrayList<Integer> itemCounts, final ArrayList<Integer> itemChances) {
        this.items = new ArrayList<Integer>();
        this.itemCounts = new ArrayList<Integer>();
        this.itemChances = new ArrayList<Integer>();
        this.id = id;
        this.name = name;
        this.items = items;
        this.itemCounts = itemCounts;
        this.itemChances = itemChances;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public ArrayList<Integer> getItems() {
        return this.items;
    }
    
    public void setItems(final ArrayList<Integer> items) {
        this.items = items;
    }
    
    public ArrayList<Integer> getItemCounts() {
        return this.itemCounts;
    }
    
    public void setItemCounts(final ArrayList<Integer> itemCounts) {
        this.itemCounts = itemCounts;
    }
    
    public ArrayList<Integer> getItemChances() {
        return this.itemChances;
    }
    
    public void setItemChances(final ArrayList<Integer> itemChances) {
        this.itemChances = itemChances;
    }
    
    public int getTotalRollChance() {
        int totalRollChance = 0;
        for (int i = 0; i < this.itemChances.size(); ++i) {
            totalRollChance += this.itemChances.get(i);
        }
        return totalRollChance;
    }
    
    public int getRandomItemNum() {
        final Random rand = new Random();
        final int roll = rand.nextInt(this.getTotalRollChance());
        int currentTotal = 0;
        Log.debug("LOOT: generating random number for table: " + this.id + ". Roll is: " + roll + "; with " + this.itemChances.size() + " items");
        for (int i = 0; i < this.itemChances.size(); ++i) {
            currentTotal += this.itemChances.get(i);
            Log.debug("LOOT: currentTotal for itemChance: " + i + " is: " + currentTotal);
            if (currentTotal >= roll) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.id) + ", " + this.name;
    }
}
