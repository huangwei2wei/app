// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.abilities;

import atavism.server.engine.OID;
import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import java.io.Serializable;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.agis.core.AgisAbilityState;
import atavism.agis.core.AgisAbility;

public class CreateItemAbility extends AgisAbility
{
    protected int item;
    
    public CreateItemAbility(final String name) {
        super(name);
        this.item = -1;
    }
    
    public int getItem() {
        return this.item;
    }
    
    public void setItem(final int template) {
        this.item = template;
    }
    
    @Override
    public void completeActivation(final AgisAbilityState state) {
        super.completeActivation(state);
        final OID bagOid;
        final OID playerOid = bagOid = state.getSource().getOwnerOid();
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
        final OID itemOid = ObjectManagerClient.generateObject(this.item, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
        InventoryClient.addItem(bagOid, playerOid, bagOid, itemOid);
    }
}
