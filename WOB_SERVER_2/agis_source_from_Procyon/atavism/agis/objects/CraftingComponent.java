// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public class CraftingComponent
{
    protected String name;
    protected int count;
    protected int itemId;
    
    public CraftingComponent(final String name, final Integer id) {
        this.name = name;
        this.count = 1;
        this.itemId = id;
    }
    
    public CraftingComponent(final String name, final int count, final Integer id) {
        this.name = name;
        this.count = count;
        this.itemId = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public int getItemId() {
        return this.itemId;
    }
}
