// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.objects.AOObject;
import atavism.agis.objects.AgisObject;
import atavism.server.engine.Event;

public abstract class AgisEvent extends Event
{
    public AgisEvent() {
    }
    
    public AgisEvent(final AgisObject obj) {
        this.setObject((AOObject)obj);
    }
    
    public AgisEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
    }
}
