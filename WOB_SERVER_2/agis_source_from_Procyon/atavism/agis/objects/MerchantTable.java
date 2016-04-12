// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.ArrayList;

public class MerchantTable
{
    int id;
    String name;
    ArrayList<Integer> items;
    ArrayList<Integer> itemCounts;
    ArrayList<Integer> itemRespawns;
    private static final long serialVersionUID = 1L;
    
    public MerchantTable() {
        this.items = new ArrayList<Integer>();
        this.itemCounts = new ArrayList<Integer>();
        this.itemRespawns = new ArrayList<Integer>();
    }
    
    public MerchantTable(final int id, final String name) {
        this.items = new ArrayList<Integer>();
        this.itemCounts = new ArrayList<Integer>();
        this.itemRespawns = new ArrayList<Integer>();
        this.id = id;
        this.name = name;
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
    
    public ArrayList<Integer> getItemRespawns() {
        return this.itemRespawns;
    }
    
    public void setItemRespawns(final ArrayList<Integer> itemRespawns) {
        this.itemRespawns = itemRespawns;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.id) + ", " + this.name;
    }
}
