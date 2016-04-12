// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.server.math.Point;
import atavism.agis.objects.CombatInfo;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.math.AOVector;
import atavism.agis.plugins.VoxelClient;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.core.AgisEffect;

public class CreateClaimEffect extends AgisEffect
{
    private static final long serialVersionUID = 1L;
    
    public CreateClaimEffect(final int id, final String name) {
        super(id, name);
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo obj = state.getTarget();
        final CombatInfo caster = state.getSource();
        final WorldManagerClient.ExtensionMessage createMsg = new WorldManagerClient.ExtensionMessage(caster.getOwnerOid());
        createMsg.setMsgType(VoxelClient.MSG_TYPE_CREATE_CLAIM);
        final Point loc = WorldManagerClient.getObjectInfo(caster.getOwnerOid()).loc;
        createMsg.setProperty("loc", (Serializable)new AOVector(loc));
        Engine.getAgent().sendBroadcast((Message)createMsg);
    }
}
