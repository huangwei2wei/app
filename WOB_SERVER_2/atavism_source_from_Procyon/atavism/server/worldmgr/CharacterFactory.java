// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.worldmgr;

import java.util.Map;
import atavism.server.engine.OID;

public abstract class CharacterFactory
{
    public abstract OID createCharacter(final String p0, final OID p1, final Map p2);
    
    public String deleteCharacter(final String worldName, final OID atavismID, final OID oid, final Map properties) {
        return null;
    }
}
