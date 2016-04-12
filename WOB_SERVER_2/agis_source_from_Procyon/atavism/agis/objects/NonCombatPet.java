// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.server.util.Logger;
import java.io.Serializable;

public class NonCombatPet extends Pet implements Serializable
{
    static final Logger log;
    private static final long serialVersionUID = 1L;
    
    static {
        log = new Logger("NonCombatPet");
    }
    
    public NonCombatPet() {
    }
    
    public NonCombatPet(final int mobTemplateID, final OID mobObj, final boolean isSpawned, final OID ownerOid) {
        this.mobTemplateID = mobTemplateID;
        this.mobObj = mobObj;
        this.isSpawned = isSpawned;
        this.ownerOid = ownerOid;
    }
    
    @Override
    public boolean despawnPet() {
        WorldManagerClient.despawn(this.mobObj);
        return true;
    }
}
