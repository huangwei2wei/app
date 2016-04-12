// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.server.engine.OID;
import atavism.agis.plugins.ArenaClient;
import atavism.agis.core.AgisAbilityState;
import atavism.agis.core.AgisAbility;

public class CaptureBattleNodeAbility extends AgisAbility
{
    public CaptureBattleNodeAbility(final String name) {
        super(name);
    }
    
    @Override
    public void completeActivation(final AgisAbilityState state) {
        super.completeActivation(state);
        final OID casterOid = state.getSource().getOwnerOid();
        ArenaClient.resourceNodeAssaulted(casterOid, state.getTarget().getOwnerOid());
    }
}
