// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.msgsys.Message;
import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;
import atavism.server.util.Log;
import atavism.agis.plugins.AnimationClient;
import atavism.server.engine.Engine;
import atavism.server.math.Point;
import atavism.server.engine.OID;
import atavism.agis.core.AgisAbilityState;

public class CoordinatedEffectState
{
    protected CoordinatedEffect coordinatedEffect;
    protected AgisAbilityState abilityState;
    protected OID effectOid;
    protected OID sourceOid;
    protected OID targetOid;
    protected Point loc;
    
    public CoordinatedEffectState(final CoordinatedEffect coordinatedEffect, final OID sourceOid, final OID targetOid, final Point loc, final AgisAbilityState abilityState) {
        this.coordinatedEffect = null;
        this.abilityState = null;
        this.effectOid = Engine.getOIDManager().getNextOid();
        this.loc = null;
        this.sourceOid = sourceOid;
        this.targetOid = targetOid;
        this.abilityState = abilityState;
        this.loc = loc;
        this.coordinatedEffect = coordinatedEffect;
    }
    
    public AnimationClient.InvokeEffectMessage generateInvokeMessage() {
        Log.debug("[CYC] generateInvokeMessage for " + this.coordinatedEffect.getEffectName());
        final AnimationClient.InvokeEffectMessage msg = new AnimationClient.InvokeEffectMessage(this.sourceOid, this.coordinatedEffect.getEffectName());
        if (this.coordinatedEffect.sendSourceOid()) {
            msg.setProperty("sourceOID", (Serializable)this.sourceOid);
        }
        if (this.coordinatedEffect.sendTargetOid()) {
            msg.setProperty("targetOID", (Serializable)this.targetOid);
        }
        if (this.loc != null) {
            msg.setProperty("point", (Serializable)this.loc);
        }
        for (final Map.Entry<String, Serializable> entry : this.coordinatedEffect.copyArgMap().entrySet()) {
            msg.setProperty(entry.getKey(), entry.getValue());
        }
        return msg;
    }
    
    public void invoke() {
        final AnimationClient.InvokeEffectMessage msg = this.generateInvokeMessage();
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("[CYC] CoordinatedEffectState.invoke(): " + msg);
    }
    
    public void invoke(final String iconName, final Boolean displayIcon) {
        final AnimationClient.InvokeEffectMessage msg = this.generateInvokeMessage();
        if (iconName != null) {
            msg.setProperty("hasIcon", true);
            msg.setProperty("iconName", iconName);
            msg.setProperty("displayIcon", displayIcon);
        }
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("[CYC] CoordinatedEffectState.invoke(): " + msg);
    }
    
    public AgisAbilityState getAbilityState() {
        return this.abilityState;
    }
}
