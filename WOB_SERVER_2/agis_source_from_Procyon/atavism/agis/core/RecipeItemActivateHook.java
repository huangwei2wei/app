// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import java.util.HashMap;
import atavism.agis.plugins.AgisInventoryClient;
import java.io.Serializable;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.engine.EnginePlugin;
import java.util.LinkedList;
import atavism.server.objects.ObjectTypes;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class RecipeItemActivateHook implements ActivateHook
{
    protected int recipeID;
    private static final long serialVersionUID = 1L;
    
    public RecipeItemActivateHook() {
    }
    
    public RecipeItemActivateHook(final int recipeID) {
        this.setRecipeID(recipeID);
    }
    
    public void setRecipeID(final int recipeID) {
        if (recipeID == -1) {
            throw new RuntimeException("RecipeItemActivateHook.setResource: bad resource");
        }
        this.recipeID = recipeID;
    }
    
    public int getRecipeID() {
        return this.recipeID;
    }
    
    @Override
    public boolean activate(final OID activatorOid, final AgisItem item, final OID targetOid) {
        if (Log.loggingDebug) {
            Log.debug("RecipeItemActivateHook.activate: activator=" + activatorOid + " item=" + item + " recipe=" + this.recipeID);
        }
        if (WorldManagerClient.getObjectInfo(activatorOid).objType != ObjectTypes.player) {
            return false;
        }
        LinkedList<String> recipes = (LinkedList<String>)EnginePlugin.getObjectProperty(activatorOid, WorldManagerClient.NAMESPACE, "recipes");
        if (recipes == null) {
            recipes = new LinkedList<String>();
        }
        else if (recipes.contains(new StringBuilder().append(this.recipeID).toString())) {
            ExtendedCombatMessages.sendErrorMessage(activatorOid, "You already know that recipe");
            return false;
        }
        recipes.add(new StringBuilder().append(this.recipeID).toString());
        EnginePlugin.setObjectProperty(activatorOid, WorldManagerClient.NAMESPACE, "recipes", (Serializable)recipes);
        ExtendedCombatMessages.sendCombatText(activatorOid, "Learned new Blueprint", 16);
        AgisInventoryClient.removeSpecificItem(activatorOid, item.getOid(), true, 1);
        return true;
    }
    
    public static void sendRecipes(final OID oid, final LinkedList<String> recipes) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "recipes");
        int numResources = 0;
        for (final String resourceID : recipes) {
            Log.debug("RESOURCE: got currency to send: " + resourceID);
            props.put("resource" + numResources + "ID", resourceID);
            ++numResources;
        }
        props.put("numRecipes", numResources);
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("RECIPES: sending down recipes message to: " + oid + " with props: " + props);
    }
    
    @Override
    public String toString() {
        return "RecipeItemActivateHook=" + this.recipeID;
    }
}
