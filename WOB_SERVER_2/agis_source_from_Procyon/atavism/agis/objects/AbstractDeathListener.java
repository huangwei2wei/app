// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.engine.OID;
import atavism.server.util.Log;
import atavism.agis.events.AgisStateEvent;
import atavism.server.objects.AOObject;
import atavism.server.engine.Event;
import java.rmi.RemoteException;
import atavism.server.engine.AbstractEventListener;

public abstract class AbstractDeathListener extends AbstractEventListener
{
    protected String name;
    protected boolean isDead;
    
    public AbstractDeathListener() throws RemoteException {
        this.name = "";
        this.isDead = false;
    }
    
    public AbstractDeathListener(final String name) throws RemoteException {
        this.name = "";
        this.isDead = false;
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    protected abstract void handleDeath(final Event p0, final AOObject p1);
    
    public void handleEvent(final Event event, final AOObject target) {
        final AgisStateEvent stateEvent = (AgisStateEvent)event;
        final OID eventObjOid = stateEvent.getObjectOid();
        if (Log.loggingDebug) {
            Log.debug("AbstractDeathListener: handleEvent target=" + target + " eventobj=" + eventObjOid);
        }
        if (eventObjOid.equals((Object)target.getOid())) {
            final Integer dead = stateEvent.getStateMap().get(AgisStates.Dead);
            if (dead != null && dead == 1 && !this.isDead) {
                this.isDead = true;
                Log.debug("AbstractDeathListener: handleEvent object is dead");
                this.handleDeath(event, target);
            }
        }
    }
}
