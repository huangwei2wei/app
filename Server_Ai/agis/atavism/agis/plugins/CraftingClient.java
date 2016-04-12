// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageType;

public class CraftingClient
{
    public static final MessageType MSG_TYPE_HARVEST_RESOURCE;
    public static final MessageType MSG_TYPE_GATHER_RESOURCE;
    public static final MessageType MSG_TYPE_CRAFTING_CRAFT_ITEM;
    public static final MessageType MSG_TYPE_CRAFTING_GRID_UPDATED;
    public static final MessageType MSG_TYPE_GET_BLUEPRINTS;
    
    static {
        MSG_TYPE_HARVEST_RESOURCE = MessageType.intern("crafting.HARVEST_RESOURCE");
        MSG_TYPE_GATHER_RESOURCE = MessageType.intern("crafting.GATHER_RESOURCE");
        MSG_TYPE_CRAFTING_CRAFT_ITEM = MessageType.intern("crafting.CRAFT_ITEM");
        MSG_TYPE_CRAFTING_GRID_UPDATED = MessageType.intern("crafting.GRID_UPDATED");
        MSG_TYPE_GET_BLUEPRINTS = MessageType.intern("crafting.GET_BLUEPRINTS");
    }
    
    public static void CraftItem(final String item) {
        final CraftItemMessage msg = new CraftItemMessage(item);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("CRAFTING CLIENT: Craft Item!");
    }
    
    public static class CraftItemMessage extends WorldManagerClient.ExtensionMessage
    {
        private static final long serialVersionUID = 1L;
        
        public CraftItemMessage() {
        }
        
        public CraftItemMessage(final String itemName) {
            this.setProperty("ItemName", (Serializable)itemName);
        }
    }
}
