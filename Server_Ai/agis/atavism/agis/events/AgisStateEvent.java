// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.util.Log;
import atavism.agis.objects.AgisStates;
import atavism.server.objects.AOObject;
import atavism.agis.objects.AgisMob;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.events.StateEvent;

public class AgisStateEvent extends StateEvent
{
    public AgisStateEvent() {
    }
    
    public AgisStateEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
    }
    
    public AgisStateEvent(final AgisMob agisMob, final boolean fullState) {
        this.setObject((AOObject)agisMob);
        if (fullState) {
            this.addState(AgisStates.Dead.toString(), (int)(agisMob.isDead() ? 1 : 0));
            final boolean combatState = agisMob.getAutoAttackTarget() != null;
            this.addState(AgisStates.Combat.toString(), (int)(combatState ? 1 : 0));
            this.addState(AgisStates.Attackable.toString(), (int)(agisMob.attackable() ? 1 : 0));
            this.addState(AgisStates.Stunned.toString(), (int)(agisMob.isStunned() ? 1 : 0));
            if (Log.loggingDebug) {
                Log.debug("AgisStateEvent: added state of mob " + agisMob.getName() + ", deadstate=" + (agisMob.isDead() ? 1 : 0) + ", combatState=" + combatState);
            }
        }
    }
    
    public String getName() {
        return "AgisStateEvent";
    }
}
