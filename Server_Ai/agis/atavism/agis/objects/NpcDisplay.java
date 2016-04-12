// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public class NpcDisplay
{
    int id;
    String name;
    String prefab;
    String gender;
    String portrait;
    private static final long serialVersionUID = 1L;
    
    public NpcDisplay() {
    }
    
    public NpcDisplay(final int id, final String name, final String prefab, final String gender, final String portrait) {
        this.id = id;
        this.name = name;
        this.prefab = prefab;
        this.gender = gender;
        this.portrait = portrait;
    }
    
    public boolean matches(final String prefab, final String gender) {
        return this.prefab.equals(prefab) && this.gender.equals(gender);
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getDisplayName() {
        return this.name;
    }
    
    public void setDisplayName(final String name) {
        this.name = name;
    }
    
    public String getPrefab() {
        return this.prefab;
    }
    
    public void setPrefab(final String prefab) {
        this.prefab = prefab;
    }
    
    public String getGender() {
        return this.gender;
    }
    
    public void setGender(final String gender) {
        this.gender = gender;
    }
    
    public String getPortrait() {
        return this.portrait;
    }
    
    public void setPortrait(final String portrait) {
        this.portrait = portrait;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.id) + ", " + this.name + ", race: " + ", gender: " + this.gender;
    }
}
