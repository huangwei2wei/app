// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.util;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import atavism.agis.objects.AgisItem;
import atavism.agis.objects.AgisMob;
import atavism.server.objects.DisplayContext;
import atavism.agis.objects.AgisObject;

public class AgisDisplayContext
{
    public static DisplayContext createFullDisplayContext(final AgisObject obj) {
        DisplayContext dc = obj.displayContext();
        if (dc == null) {
            return null;
        }
        dc = (DisplayContext)dc.clone();
        if (!(obj instanceof AgisMob)) {
            return dc;
        }
        final AgisMob mob = (AgisMob)obj;
        final Set<AgisItem> items = mob.getEquippedItems();
        for (final AgisItem item : items) {
            final DisplayContext itemDC = item.displayContext();
            final String meshFile = itemDC.getMeshFile();
            if (meshFile == null) {
                continue;
            }
            if (itemDC.getAttachableFlag()) {
                continue;
            }
            final Set<DisplayContext.Submesh> submeshes = (Set<DisplayContext.Submesh>)itemDC.getSubmeshes();
            dc.addSubmeshes((Collection)submeshes);
        }
        return dc;
    }
}
