// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.agis.objects.AgisItem;
import java.io.Serializable;
import atavism.agis.plugins.AgisMobClient;
import atavism.agis.core.AgisAbilityState;
import atavism.agis.core.AgisAbility;

public class SpawnPetAbility extends AgisAbility
{
    public SpawnPetAbility(final String name) {
        super(name);
    }
    
    @Override
    public void completeActivation(final AgisAbilityState state) {
        super.completeActivation(state);
        final AgisItem item = state.getItem();
        final String petRef = (String)item.getProperty("petRef");
        AgisMobClient.spawnPet(state.getSource().getOid(), petRef, 4, 0L, -1, this.skillType);
    }
}
