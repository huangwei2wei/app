// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.worldmgr;

public class CharacterGenerator
{
    protected CharacterFactory charFactory;
    
    public CharacterGenerator() {
        this.charFactory = null;
    }
    
    public CharacterFactory getCharacterFactory() {
        return this.charFactory;
    }
    
    public void setCharacterFactory(final CharacterFactory fact) {
        this.charFactory = fact;
    }
}
