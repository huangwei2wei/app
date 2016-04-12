// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.objects.DisplayContext;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import atavism.agis.objects.AgisItem;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import atavism.agis.objects.AgisMob;
import atavism.server.objects.AOObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.server.util.Logger;
import atavism.server.events.ModelInfoEvent;

public class AgisModelInfoEvent extends ModelInfoEvent
{
    static final Logger log;
    
    static {
        log = new Logger("AgisModelInfoEvent");
    }
    
    public AgisModelInfoEvent() {
    }
    
    public AgisModelInfoEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
    }
    
    public AgisModelInfoEvent(final AOObject obj) {
        super(obj);
        if (obj instanceof AgisMob) {
            this.processAgisMob((AgisMob)obj);
        }
    }
    
    public AgisModelInfoEvent(final OID oid) {
        super(oid);
    }
    
    public String getName() {
        return "AgisModelInfoEvent";
    }
    
    void processAgisMob(final AgisMob mob) {
        final Set<AgisItem> items = mob.getEquippedItems();
        if (Log.loggingDebug) {
            AgisModelInfoEvent.log.debug("processAgisMob: mob=" + mob.getName() + ", num items=" + items.size());
        }
        for (final AgisItem item : items) {
            if (Log.loggingDebug) {
                AgisModelInfoEvent.log.debug("processAgisMob: mob=" + mob.getName() + ", considering equipped item " + item.getName());
            }
            final DisplayContext itemDC = item.displayContext();
            final String meshFile = itemDC.getMeshFile();
            if (meshFile == null) {
                continue;
            }
            if (itemDC.getAttachableFlag()) {
                continue;
            }
            final Set<DisplayContext.Submesh> submeshes = (Set<DisplayContext.Submesh>)itemDC.getSubmeshes();
            if (Log.loggingDebug) {
                AgisModelInfoEvent.log.debug("processAgisMob: mob=" + mob.getName() + ", adding submeshes for item " + item.getName() + ", dc=" + this.dc);
            }
            this.dc.addSubmeshes((Collection)submeshes);
            if (!Log.loggingDebug) {
                continue;
            }
            AgisModelInfoEvent.log.debug("processAgisMob: mob=" + mob.getName() + ", done adding submeshes for item " + item.getName() + ", dc=" + this.dc);
        }
    }
}
