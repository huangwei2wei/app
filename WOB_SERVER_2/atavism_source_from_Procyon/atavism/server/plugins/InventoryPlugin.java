// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.msgsys.Message;
import atavism.server.util.Log;
import atavism.server.util.ObjectLockManager;
import atavism.server.objects.Entity;
import atavism.server.objects.Template;
import atavism.server.objects.AOObject;
import atavism.server.engine.OID;
import atavism.server.engine.Hook;
import java.util.List;
import atavism.server.util.AORuntimeException;
import java.util.Collection;
import atavism.server.engine.Namespace;
import java.util.ArrayList;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.util.LockFactory;
import java.util.concurrent.locks.Lock;
import atavism.server.util.Logger;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.EnginePlugin;

public abstract class InventoryPlugin extends EnginePlugin implements MessageCallback
{
    public static String INVENTORY_PLUGIN_NAME;
    protected static final Logger log;
    protected Lock lock;
    public static final String INVENTORY_PROP_BAG_KEY = "inv.bag";
    public static final String INVENTORY_PROP_BACKREF_KEY = "inv.backref";
    
    public InventoryPlugin() {
        super(InventoryPlugin.INVENTORY_PLUGIN_NAME);
        this.lock = LockFactory.makeLock("InventoryPlugin");
        this.setPluginType("Inventory");
    }
    
    @Override
    public void onActivate() {
        try {
            this.registerHooks();
            MessageTypeFilter filter = new MessageTypeFilter();
            filter.addType(InventoryClient.MSG_TYPE_ACTIVATE);
            filter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
            filter.addType(InventoryClient.MSG_TYPE_DESTROY_ITEM);
            Engine.getAgent().createSubscription(filter, this);
            filter = new MessageTypeFilter();
            filter.addType(InventoryClient.MSG_TYPE_ADD_ITEM);
            filter.addType(InventoryClient.MSG_TYPE_INV_REMOVE);
            filter.addType(InventoryClient.MSG_TYPE_CREATE_INV);
            filter.addType(InventoryClient.MSG_TYPE_LOOTALL);
            filter.addType(InventoryClient.MSG_TYPE_INV_FIND);
            Engine.getAgent().createSubscription(filter, this, 8);
            final List<Namespace> namespaces = new ArrayList<Namespace>();
            namespaces.add(Namespace.BAG);
            namespaces.add(Namespace.AGISITEM);
            this.registerPluginNamespaces(namespaces, new InventoryGenerateSubObjectHook());
            this.registerLoadHook(Namespace.BAG, new InventoryLoadHook());
            this.registerLoadHook(Namespace.AGISITEM, new ItemLoadHook());
            this.registerUnloadHook(Namespace.BAG, new InventoryUnloadHook());
            this.registerUnloadHook(Namespace.AGISITEM, new ItemUnloadHook());
            this.registerDeleteHook(Namespace.BAG, new InventoryDeleteHook());
            this.registerDeleteHook(Namespace.AGISITEM, new ItemDeleteHook());
            this.registerSaveHook(Namespace.BAG, new InventorySaveHook());
            this.registerSaveHook(Namespace.AGISITEM, new ItemSaveHook());
        }
        catch (Exception e) {
            throw new AORuntimeException("activate failed", e);
        }
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(InventoryClient.MSG_TYPE_ADD_ITEM, new AddItemHook());
        this.getHookManager().addHook(InventoryClient.MSG_TYPE_ACTIVATE, new ItemActivateHook());
        this.getHookManager().addHook(InventoryClient.MSG_TYPE_LOOTALL, new LootAllHook());
        this.getHookManager().addHook(InventoryClient.MSG_TYPE_INV_FIND, new FindItemHook());
        this.getHookManager().addHook(InventoryClient.MSG_TYPE_INV_REMOVE, new RemoveItemHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT, new UpdateObjHook());
        this.getHookManager().addHook(InventoryClient.MSG_TYPE_DESTROY_ITEM, new DestroyItemHook());
    }
    
    public abstract void updateObject(final OID p0, final OID p1);
    
    public abstract boolean equipItem(final AOObject p0, final OID p1, final boolean p2);
    
    protected abstract boolean activateObject(final OID p0, final OID p1, final OID p2);
    
    protected abstract SubObjData createInvSubObj(final OID p0, final Template p1);
    
    protected abstract SubObjData createItemSubObj(final OID p0, final Template p1);
    
    protected abstract void loadInventory(final Entity p0);
    
    protected abstract void loadItem(final Entity p0);
    
    protected abstract void unloadInventory(final Entity p0);
    
    protected abstract void unloadItem(final Entity p0);
    
    protected abstract void deleteInventory(final Entity p0);
    
    protected void deleteInventory(final OID oid) {
    }
    
    protected abstract void deleteItem(final Entity p0);
    
    protected void deleteItem(final OID oid) {
    }
    
    protected abstract void saveInventory(final Entity p0, final Namespace p1);
    
    protected abstract void saveItem(final Entity p0, final Namespace p1);
    
    protected abstract void sendInvUpdate(final OID p0);
    
    protected abstract boolean addItem(final OID p0, final OID p1, final OID p2);
    
    protected abstract boolean removeItemFromBag(final OID p0, final OID p1);
    
    protected abstract boolean lootAll(final OID p0, final OID p1);
    
    protected abstract boolean containsItem(final OID p0, final OID p1);
    
    protected abstract OID findItem(final OID p0, final int p1);
    
    protected abstract ArrayList<OID> findItems(final OID p0, final ArrayList<Integer> p1);
    
    protected abstract OID removeItem(final OID p0, final OID p1, final boolean p2);
    
    protected abstract OID removeItem(final OID p0, final int p1, final boolean p2);
    
    protected abstract ArrayList<OID> removeItems(final OID p0, final ArrayList<Integer> p1, final boolean p2);
    
    protected boolean destroyItem(final OID containerOid, final OID itemOid) {
        throw new RuntimeException("not implemented");
    }
    
    public Lock getLock() {
        return this.lock;
    }
    
    static {
        InventoryPlugin.INVENTORY_PLUGIN_NAME = "Inventory";
        log = new Logger("InventoryPlugin");
    }
    
    class InventoryGenerateSubObjectHook extends GenerateSubObjectHook
    {
        public InventoryGenerateSubObjectHook() {
            super(InventoryPlugin.this);
        }
        
        @Override
        public SubObjData generateSubObject(final Template template, final Namespace namespace, final OID masterOid) {
            if (Log.loggingDebug) {
                InventoryPlugin.log.debug("GenerateSubObjectHook: masterOid=" + masterOid + ", template=" + template);
            }
            if (namespace.equals(Namespace.BAG)) {
                return InventoryPlugin.this.createInvSubObj(masterOid, template);
            }
            if (namespace.equals(Namespace.AGISITEM)) {
                return InventoryPlugin.this.createItemSubObj(masterOid, template);
            }
            InventoryPlugin.log.error("InventoryGenerateSubObjectHook: unknown namespace: " + namespace);
            return null;
        }
    }
    
    class InventoryLoadHook implements LoadHook
    {
        @Override
        public void onLoad(final Entity e) {
            InventoryPlugin.this.loadInventory(e);
        }
    }
    
    class InventoryUnloadHook implements UnloadHook
    {
        @Override
        public void onUnload(final Entity e) {
            InventoryPlugin.this.unloadInventory(e);
        }
    }
    
    class InventoryDeleteHook implements DeleteHook
    {
        @Override
        public void onDelete(final Entity e) {
            InventoryPlugin.this.deleteInventory(e);
        }
        
        @Override
        public void onDelete(final OID oid, final Namespace namespace) {
            InventoryPlugin.this.deleteInventory(oid);
        }
    }
    
    class ItemLoadHook implements LoadHook
    {
        @Override
        public void onLoad(final Entity e) {
            InventoryPlugin.this.loadItem(e);
        }
    }
    
    class ItemUnloadHook implements UnloadHook
    {
        @Override
        public void onUnload(final Entity e) {
            InventoryPlugin.this.unloadItem(e);
        }
    }
    
    class ItemDeleteHook implements DeleteHook
    {
        @Override
        public void onDelete(final Entity e) {
            InventoryPlugin.this.deleteItem(e);
        }
        
        @Override
        public void onDelete(final OID oid, final Namespace namespace) {
            InventoryPlugin.this.deleteItem(oid);
        }
    }
    
    class InventorySaveHook implements SaveHook
    {
        @Override
        public void onSave(final Entity e, final Namespace namespace) {
            InventoryPlugin.this.saveInventory(e, namespace);
        }
    }
    
    class ItemSaveHook implements SaveHook
    {
        @Override
        public void onSave(final Entity e, final Namespace namespace) {
            InventoryPlugin.this.saveItem(e, namespace);
        }
    }
    
    class UpdateObjHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.UpdateMessage cMsg = (WorldManagerClient.UpdateMessage)msg;
            final OID oid = cMsg.getSubject();
            final OID nOid = cMsg.getTarget();
            if (!oid.equals(nOid)) {
                return true;
            }
            final Lock objLock = InventoryPlugin.this.getObjectLockManager().getLock(oid);
            objLock.lock();
            try {
                InventoryPlugin.this.updateObject(oid, cMsg.getTarget());
                return true;
            }
            finally {
                objLock.unlock();
            }
        }
    }
    
    class AddItemHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InventoryClient.AddItemMessage aMsg = (InventoryClient.AddItemMessage)msg;
            final OID containerOid = aMsg.getContainer();
            final OID itemOid = aMsg.getItem();
            final OID mobOid = aMsg.getMob();
            if (Log.loggingDebug) {
                InventoryPlugin.log.debug("addItemHook: containerOid=" + containerOid + ", itemOid=" + itemOid);
            }
            final Lock objLock = InventoryPlugin.this.getObjectLockManager().getLock(mobOid);
            objLock.lock();
            try {
                final boolean rv = InventoryPlugin.this.addItem(mobOid, containerOid, itemOid);
                if (Log.loggingDebug) {
                    InventoryPlugin.log.debug("addItemHook: containerOid=" + containerOid + ", itemOid=" + itemOid + ", result=" + rv);
                }
                Engine.getAgent().sendBooleanResponse(msg, rv);
                InventoryPlugin.this.sendInvUpdate(mobOid);
                return rv;
            }
            finally {
                objLock.unlock();
            }
        }
    }
    
    class DestroyItemHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.ExtensionMessage destroyMsg = (WorldManagerClient.ExtensionMessage)msg;
            final OID ownerOid = destroyMsg.getSubject();
            final OID itemOid = (OID)destroyMsg.getProperty("itemOid");
            if (Log.loggingDebug) {
                InventoryPlugin.log.debug("DestroyItemHook: ownerOid=" + ownerOid + ", itemOid=" + itemOid);
            }
            boolean rv = false;
            rv = InventoryPlugin.this.containsItem(ownerOid, itemOid);
            if (!rv) {
                InventoryPlugin.log.debug("DestroyItemHook: item " + itemOid + " not owned by owner " + ownerOid);
                return true;
            }
            rv = ObjectManagerClient.deleteObject(itemOid);
            if (rv) {
                InventoryPlugin.this.sendInvUpdate(ownerOid);
            }
            if (Log.loggingDebug) {
                InventoryPlugin.log.debug("DestroyItemHook.deleteObject: success=" + rv);
            }
            return true;
        }
    }
    
    class ItemActivateHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InventoryClient.ActivateMessage aMsg = (InventoryClient.ActivateMessage)msg;
            final OID activatorOid = aMsg.getActivatorOid();
            final OID objOid = aMsg.getSubject();
            final OID targetOid = aMsg.getTargetOid();
            if (Log.loggingDebug) {
                InventoryPlugin.log.debug("ItemActivateHook: activatorOid=" + activatorOid + ", objOid=" + objOid + ", targetOid=" + targetOid);
            }
            final Lock objLock = InventoryPlugin.this.getObjectLockManager().getLock(activatorOid);
            objLock.lock();
            try {
                return InventoryPlugin.this.activateObject(objOid, activatorOid, targetOid);
            }
            finally {
                objLock.unlock();
            }
        }
    }
    
    class LootAllHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InventoryClient.LootAllMessage lMsg = (InventoryClient.LootAllMessage)msg;
            final OID looterOid = lMsg.getSubject();
            final OID containerOid = lMsg.getContainerOid();
            if (Log.loggingDebug) {
                InventoryPlugin.log.debug("LootAllHook: looter=" + looterOid + ", container=" + containerOid);
            }
            final boolean rv = InventoryPlugin.this.lootAll(looterOid, containerOid);
            Engine.getAgent().sendBooleanResponse(lMsg, rv);
            return rv;
        }
    }
    
    class FindItemHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InventoryClient.RemoveOrFindItemMessage findMsg = (InventoryClient.RemoveOrFindItemMessage)msg;
            final OID mobOid = findMsg.getSubject();
            final String method = findMsg.getMethod();
            InventoryPlugin.log.debug("FindItemHook: got message");
            if (method.equals("template")) {
                final int template = (int)findMsg.getPayload();
                final OID resultOid = InventoryPlugin.this.findItem(mobOid, template);
                Engine.getAgent().sendOIDResponse(findMsg, resultOid);
            }
            else if (method.equals("templateList")) {
                final ArrayList<Integer> templateList = (ArrayList<Integer>)findMsg.getPayload();
                final ArrayList<OID> resultList = InventoryPlugin.this.findItems(mobOid, templateList);
                Engine.getAgent().sendObjectResponse(findMsg, resultList);
            }
            else {
                Log.error("FindItemHook: unknown method=" + method);
            }
            return true;
        }
    }
    
    class RemoveItemHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InventoryClient.RemoveOrFindItemMessage delMsg = (InventoryClient.RemoveOrFindItemMessage)msg;
            final OID mobOid = delMsg.getSubject();
            final String method = delMsg.getMethod();
            InventoryPlugin.log.debug("RemoveItemHook: got message");
            if (method.equals("oid")) {
                final OID oid = (OID)delMsg.getPayload();
                final OID result = InventoryPlugin.this.removeItem(mobOid, oid, true);
                Engine.getAgent().sendOIDResponse(delMsg, result);
            }
            else if (method.equals("template")) {
                final int templateID = (int)delMsg.getPayload();
                final OID result = InventoryPlugin.this.removeItem(mobOid, templateID, true);
                Engine.getAgent().sendOIDResponse(delMsg, result);
            }
            else if (method.equals("templateList")) {
                final ArrayList<Integer> templateList = (ArrayList<Integer>)delMsg.getPayload();
                final ArrayList<OID> result2 = InventoryPlugin.this.removeItems(mobOid, templateList, true);
                Engine.getAgent().sendObjectResponse(delMsg, result2);
            }
            else {
                Log.error("RemoveItemHook: unknown method=" + method);
            }
            InventoryPlugin.this.sendInvUpdate(mobOid);
            return true;
        }
    }
}
