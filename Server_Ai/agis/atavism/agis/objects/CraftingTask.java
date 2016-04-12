// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.List;
import atavism.agis.plugins.CombatClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.server.plugins.InventoryClient;
import java.util.Collection;
import java.util.ArrayList;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.server.util.Log;
import atavism.agis.plugins.CraftingPlugin;
import atavism.server.engine.OID;
import java.util.LinkedList;

public class CraftingTask implements Runnable
{
    protected CraftingRecipe recipe;
    LinkedList<Integer> componentIds;
    LinkedList<Integer> componentStacks;
    LinkedList<Long> specificComponents;
    boolean useSpecificItems;
    OID playerOid;
    int recipeId;
    
    public CraftingTask(final CraftingRecipe recipe, final OID oid, final int recipeId) {
        this.useSpecificItems = false;
        this.recipe = recipe;
        this.componentIds = new LinkedList<Integer>();
        this.componentStacks = new LinkedList<Integer>();
        this.playerOid = oid;
        this.recipeId = recipeId;
        final LinkedList<LinkedList<CraftingComponent>> components = recipe.getRequiredCraftingComponents();
        for (int i = 0; i < CraftingPlugin.GRID_SIZE; ++i) {
            for (int j = 0; j < CraftingPlugin.GRID_SIZE; ++j) {
                this.componentIds.add(components.get(i).get(j).getItemId());
                this.componentStacks.add(components.get(i).get(j).getCount());
            }
        }
    }
    
    public CraftingTask(final CraftingRecipe r, final LinkedList<Long> cid, final LinkedList<Integer> cs, final OID oid, final int recipeId) {
        this.useSpecificItems = false;
        this.recipe = r;
        this.specificComponents = cid;
        this.componentStacks = cs;
        this.playerOid = oid;
        this.recipeId = recipeId;
        this.useSpecificItems = true;
    }
    
    @Override
    public void run() {
        Log.debug("CRAFT: running crafting task with specific items: " + this.useSpecificItems);
        if (this.useSpecificItems) {
            for (int i = 0; i < this.specificComponents.size(); ++i) {
                AgisInventoryClient.removeSpecificItem(this.playerOid, OID.fromLong((long)this.specificComponents.get(i)), false, this.componentStacks.get(i));
            }
        }
        else {
            final ArrayList<Integer> test = new ArrayList<Integer>();
            test.addAll(this.componentIds);
            final List<OID> itemList = (List<OID>)InventoryClient.findItems(this.playerOid, (ArrayList)test);
            for (int j = 0; j < itemList.size(); ++j) {
                AgisInventoryClient.removeGenericItem(this.playerOid, test.get(j), false, this.componentStacks.get(j));
            }
        }
        AgisInventoryClient.generateItem(this.playerOid, this.recipe.getResultItemId(), "", this.recipe.getResultItemCount(), null);
        CombatClient.abilityUsed(this.playerOid, this.recipe.getSkillID());
        Log.debug("CRAFTING PLUGIN: Crafting Recipe " + this.recipeId + " with skill: " + this.recipe.getSkillID());
    }
}
