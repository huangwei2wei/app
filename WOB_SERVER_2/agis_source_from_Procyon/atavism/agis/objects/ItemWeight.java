// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public class ItemWeight
{
    int id;
    String name;
    String stat1;
    int weight1;
    String stat2;
    int weight2;
    String stat3;
    int weight3;
    boolean isPrefix;
    private static final long serialVersionUID = 1L;
    
    public ItemWeight() {
        this.stat1 = "";
        this.weight1 = 0;
        this.stat2 = "";
        this.weight2 = 0;
        this.stat3 = "";
        this.weight3 = 0;
    }
    
    public ItemWeight(final int id, final String name, final String stat1, final int weight1, final String stat2, final int weight2, final String stat3, final int weight3, final boolean prefix) {
        this.stat1 = "";
        this.weight1 = 0;
        this.stat2 = "";
        this.weight2 = 0;
        this.stat3 = "";
        this.weight3 = 0;
        this.id = id;
        this.name = name;
        this.stat1 = stat1;
        this.weight1 = weight1;
        this.stat2 = stat2;
        this.weight2 = weight2;
        this.stat3 = stat3;
        this.weight3 = weight3;
        this.isPrefix = prefix;
    }
    
    public int getTotalStatWeight() {
        return this.weight1 + this.weight2 + this.weight3;
    }
    
    public int getItemWeightID() {
        return this.id;
    }
    
    public void setItemWeightID(final int id) {
        this.id = id;
    }
    
    public String getItemWeightName() {
        return this.name;
    }
    
    public void setItemWeightName(final String name) {
        this.name = name;
    }
    
    public String getStat1() {
        return this.stat1;
    }
    
    public void setStat1(final String stat1) {
        this.stat1 = stat1;
    }
    
    public int getWeight1() {
        return this.weight1;
    }
    
    public void setWeight1(final int weight1) {
        this.weight1 = weight1;
    }
    
    public String getStat2() {
        return this.stat2;
    }
    
    public void setStat2(final String stat2) {
        this.stat2 = stat2;
    }
    
    public int getWeight2() {
        return this.weight2;
    }
    
    public void setWeight2(final int weight2) {
        this.weight2 = weight2;
    }
    
    public String getStat3() {
        return this.stat3;
    }
    
    public void setStat3(final String stat3) {
        this.stat3 = stat3;
    }
    
    public int getWeight3() {
        return this.weight3;
    }
    
    public void setWeight3(final int weight3) {
        this.weight3 = weight3;
    }
    
    public boolean getIsPrefix() {
        return this.isPrefix;
    }
    
    public void setIsPrefix(final boolean isPrefix) {
        this.isPrefix = isPrefix;
    }
}
