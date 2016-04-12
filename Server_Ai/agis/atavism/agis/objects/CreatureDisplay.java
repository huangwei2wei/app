// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public class CreatureDisplay
{
    int id;
    String name;
    String species;
    String subspecies;
    String model;
    String gender;
    private static final long serialVersionUID = 1L;
    
    public CreatureDisplay() {
    }
    
    public CreatureDisplay(final int id, final String name, final String species, final String subspecies, final String gender, final String model) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.subspecies = subspecies;
        this.gender = gender;
        this.model = model;
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
    
    public String getSpecies() {
        return this.species;
    }
    
    public void setSpecies(final String species) {
        this.species = species;
    }
    
    public String getSubSpecies() {
        return this.subspecies;
    }
    
    public void setSubSpecies(final String subspecies) {
        this.subspecies = subspecies;
    }
    
    public String getGender() {
        return this.gender;
    }
    
    public void setGender(final String gender) {
        this.gender = gender;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public void setModel(final String model) {
        this.model = model;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.id) + ", " + this.name + ", race: " + ", gender: " + this.gender;
    }
}
