// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.objects.AgisEquipSlot;
import atavism.server.objects.AOObject;
import atavism.server.util.Log;
import atavism.server.engine.Engine;
import atavism.server.plugins.InventoryPlugin;
import atavism.agis.plugins.AgisInventoryPlugin;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class EquipActivateHook implements ActivateHook
{
    protected OID itemOid;
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean activate(final OID activatorOid, final AgisItem item, final OID targetOid) {
        final AgisInventoryPlugin invPlugin = (AgisInventoryPlugin)Engine.getPlugin(InventoryPlugin.INVENTORY_PLUGIN_NAME);
        if (Log.loggingDebug) {
            Log.debug("EquipActivateHook: calling invPlugin, item=" + item + ", activatorOid=" + activatorOid + ", targetOid=" + targetOid);
        }
        final AgisInventoryPlugin.EquipMap equipMap = invPlugin.getEquipMap(activatorOid);
        invPlugin.getLock().lock();
        AgisEquipSlot slot;
        try {
            slot = equipMap.getSlot(item.getMasterOid());
        }
        finally {
            invPlugin.getLock().unlock();
        }
        invPlugin.getLock().unlock();
        if (slot == null) {
            if (Log.loggingDebug) {
                Log.debug("EquipActivateHook: item not equipped: " + item);
            }
            return invPlugin.equipItem(item, activatorOid, true);
        }
        if (Log.loggingDebug) {
            Log.debug("EquipActivateHook: item IS equipped: " + item);
        }
        return invPlugin.unequipItem(item, activatorOid, false);
    }
}
