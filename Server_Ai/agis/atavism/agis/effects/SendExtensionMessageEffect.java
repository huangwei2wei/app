// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.agis.core.AgisEffect;

public class SendExtensionMessageEffect extends AgisEffect
{
    protected String messageType;
    
    public SendExtensionMessageEffect(final int id, final String name) {
        super(id, name);
        this.messageType = "";
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo obj = state.getTarget();
        final CombatInfo caster = state.getSource();
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", this.messageType);
        final WorldManagerClient.TargetedExtensionMessage eMsg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, caster.getOwnerOid(), caster.getOwnerOid(), false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)eMsg);
    }
    
    public String getMessageType() {
        return this.messageType;
    }
    
    public void setMessageType(final String messageType) {
        this.messageType = messageType;
    }
}
