// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.server.engine.OID;
import atavism.server.plugins.InventoryClient;
import atavism.server.plugins.ObjectManagerClient;
import atavism.server.plugins.ObjectManagerPlugin;
import java.io.Serializable;
import atavism.server.engine.Namespace;
import atavism.server.objects.Template;
import atavism.agis.core.AgisEffect;

public class CreateItemEffect extends AgisEffect
{
    protected int item;
    protected int numberToCreate;
    private static final long serialVersionUID = 1L;
    
    public CreateItemEffect(final int id, final String name) {
        super(id, name);
        this.item = -1;
        this.numberToCreate = 1;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final OID bagOid;
        final OID playerOid = bagOid = state.getSource().getOwnerOid();
        final Template overrideTemplate = new Template();
        overrideTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", (Serializable)true);
        final OID itemOid = ObjectManagerClient.generateObject(this.item, ObjectManagerPlugin.ITEM_TEMPLATE, overrideTemplate);
        InventoryClient.addItem(bagOid, playerOid, bagOid, itemOid);
    }
    
    public int getItem() {
        return this.item;
    }
    
    public void setItem(final int template) {
        this.item = template;
    }
    
    public int getNumberToCreate() {
        return this.numberToCreate;
    }
    
    public void setNumberToCreate(final int numberToCreate) {
        this.numberToCreate = numberToCreate;
    }
}
