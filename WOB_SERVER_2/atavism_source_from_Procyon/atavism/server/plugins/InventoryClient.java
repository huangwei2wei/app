// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import java.util.Iterator;
import atavism.server.network.AOByteBuffer;
import java.util.HashMap;
import java.util.Map;
import atavism.server.engine.EventParser;
import atavism.server.messages.ClientMessage;
import atavism.msgsys.SubjectMessage;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.util.Log;
import atavism.server.engine.OID;
import atavism.server.engine.Namespace;
import atavism.msgsys.MessageType;

public class InventoryClient
{
    public static final MessageType MSG_TYPE_ADD_ITEM;
    public static final MessageType MSG_TYPE_CREATE_INV;
    public static final MessageType MSG_TYPE_INV_UPDATE;
    public static final MessageType MSG_TYPE_ACTIVATE;
    public static final MessageType MSG_TYPE_LOOTALL;
    public static final MessageType MSG_TYPE_INV_FIND;
    public static final MessageType MSG_TYPE_INV_REMOVE;
    public static final MessageType MSG_TYPE_DESTROY_ITEM;
    public static final String INV_METHOD_OID = "oid";
    public static final String INV_METHOD_TEMPLATE = "template";
    public static final String INV_METHOD_TEMPLATE_LIST = "templateList";
    public static final String TEMPL_ITEMS = ":inv_items";
    public static final String TEMPL_EQUIP_INFO = "item_equipInfo";
    public static final String TEMPL_ACTIVATE_HOOK = "item_activateHook";
    public static final String TEMPL_ICON = "item_icon";
    public static final String TEMPL_DCMAP = "item_dcmap";
    public static final String TEMPL_VALUE = "item_value";
    public static Namespace NAMESPACE;
    public static Namespace ITEM_NAMESPACE;
    
    public static void getInventory(final OID objOid) {
    }
    
    public static void activateObject(final OID objOid, final OID activatorOid, final OID targetOid) {
        final ActivateMessage msg = new ActivateMessage(objOid, activatorOid, targetOid);
        if (Log.loggingDebug) {
            Log.debug("InventoryClient.activateObject: activator=" + msg.getActivatorOid() + ", objOid=" + msg.getSubject() + ", targetOid=" + msg.getTargetOid());
        }
        Engine.getAgent().sendBroadcast(msg);
    }
    
    public static boolean lootAll(final OID looterOid, final OID containerOid) {
        final LootAllMessage msg = new LootAllMessage(looterOid, containerOid);
        if (Log.loggingDebug) {
            Log.debug("InventoryClient.lootAll: looterOid=" + looterOid + ", container=" + containerOid);
        }
        return Engine.getAgent().sendRPCReturnBoolean(msg);
    }
    
    public static boolean addItem(final OID containerOid, final OID mobOid, final OID rootContainerOid, final OID itemOid) {
        final AddItemMessage msg = new AddItemMessage(containerOid, mobOid, rootContainerOid, itemOid);
        return Engine.getAgent().sendRPCReturnBoolean(msg);
    }
    
    public static OID removeItem(final OID mobOid, final OID itemOid) {
        final Message msg = new RemoveOrFindItemMessage(InventoryClient.MSG_TYPE_INV_REMOVE, mobOid, "oid", itemOid);
        return Engine.getAgent().sendRPCReturnOID(msg);
    }
    
    public static OID removeItem(final OID mobOid, final int templateID) {
        final Message msg = new RemoveOrFindItemMessage(InventoryClient.MSG_TYPE_INV_REMOVE, mobOid, "template", templateID);
        return Engine.getAgent().sendRPCReturnOID(msg);
    }
    
    public static List<OID> removeItems(final OID mobOid, final ArrayList<Integer> templateNames) {
        final Message msg = new RemoveOrFindItemMessage(InventoryClient.MSG_TYPE_INV_REMOVE, mobOid, "templateList", templateNames);
        return (List<OID>)Engine.getAgent().sendRPCReturnObject(msg);
    }
    
    public static OID findItem(final OID mobOid, final int templateID) {
        final Message msg = new RemoveOrFindItemMessage(InventoryClient.MSG_TYPE_INV_FIND, mobOid, "template", templateID);
        final OID oid = Engine.getAgent().sendRPCReturnOID(msg);
        Log.debug("findItem: got response");
        return oid;
    }
    
    public static List<OID> findItems(final OID mobOid, final ArrayList<Integer> templateIDs) {
        final Message msg = new RemoveOrFindItemMessage(InventoryClient.MSG_TYPE_INV_FIND, mobOid, "templateList", templateIDs);
        return (List<OID>)Engine.getAgent().sendRPCReturnObject(msg);
    }
    
    static {
        MSG_TYPE_ADD_ITEM = MessageType.intern("ao.ADD_ITEM");
        MSG_TYPE_CREATE_INV = MessageType.intern("ao.CREATE_INV");
        MSG_TYPE_INV_UPDATE = MessageType.intern("ao.INV_UPDATE");
        MSG_TYPE_ACTIVATE = MessageType.intern("ao.ACTIVATE");
        MSG_TYPE_LOOTALL = MessageType.intern("ao.LOOTALL");
        MSG_TYPE_INV_FIND = MessageType.intern("ao.INV_FIND");
        MSG_TYPE_INV_REMOVE = MessageType.intern("ao.INV_REMOVE");
        MSG_TYPE_DESTROY_ITEM = MessageType.intern("ao.DESTROY_ITEM");
        InventoryClient.NAMESPACE = null;
        InventoryClient.ITEM_NAMESPACE = null;
    }
    
    public static class AddItemMessage extends SubjectMessage
    {
        OID container;
        OID rootContainer;
        OID item;
        OID mob;
        private static final long serialVersionUID = 1L;
        
        public AddItemMessage() {
            super(InventoryClient.MSG_TYPE_ADD_ITEM);
        }
        
        public AddItemMessage(final OID containerOid, final OID mob, final OID rootContainer, final OID itemOid) {
            super(InventoryClient.MSG_TYPE_ADD_ITEM, containerOid);
            this.setContainer(containerOid);
            this.setMob(mob);
            this.setRootContainer(rootContainer);
            this.setItem(itemOid);
        }
        
        public void setContainer(final OID oid) {
            this.container = oid;
        }
        
        public OID getContainer() {
            return this.container;
        }
        
        public void setRootContainer(final OID oid) {
            this.rootContainer = oid;
        }
        
        public OID getRootContainer() {
            return this.rootContainer;
        }
        
        public void setItem(final OID oid) {
            this.item = oid;
        }
        
        public OID getItem() {
            return this.item;
        }
        
        public void setMob(final OID oid) {
            this.mob = oid;
        }
        
        public OID getMob() {
            return this.mob;
        }
    }
    
    public static class RemoveOrFindItemMessage extends SubjectMessage
    {
        private String method;
        private Object payload;
        private static final long serialVersionUID = 1L;
        
        public RemoveOrFindItemMessage() {
        }
        
        public RemoveOrFindItemMessage(final MessageType msgType, final OID mobOid, final String method, final Serializable payload) {
            super(msgType, mobOid);
            this.setMethod(method);
            this.setPayload(payload);
        }
        
        public String getMethod() {
            return this.method;
        }
        
        public void setMethod(final String method) {
            this.method = method;
        }
        
        public Object getPayload() {
            return this.payload;
        }
        
        public void setPayload(final Object payload) {
            this.payload = payload;
        }
    }
    
    public static class InvUpdateMessage extends SubjectMessage implements ClientMessage, EventParser
    {
        Map<InvPos, ItemInfo> invMap;
        private static final long serialVersionUID = 1L;
        
        public InvUpdateMessage() {
            super(InventoryClient.MSG_TYPE_INV_UPDATE);
            this.invMap = new HashMap<InvPos, ItemInfo>();
        }
        
        public InvUpdateMessage(final OID mobOid) {
            super(InventoryClient.MSG_TYPE_INV_UPDATE, mobOid);
            this.invMap = new HashMap<InvPos, ItemInfo>();
        }
        
        public void addItem(final int bagNum, final int bagPos, final OID itemOid, final String itemName, final String itemIcon) {
            final InvPos invPos = new InvPos(bagNum, bagPos);
            this.invMap.put(invPos, new ItemInfo(itemOid, itemName, itemIcon));
        }
        
        public int getNumEntries() {
            return this.invMap.size();
        }
        
        public Map<InvPos, ItemInfo> getEntries() {
            return new HashMap<InvPos, ItemInfo>(this.invMap);
        }
        
        @Override
        public AOByteBuffer toBuffer() {
            final AOByteBuffer buf = new AOByteBuffer(400);
            buf.putOID(this.getSubject());
            buf.putInt(43);
            buf.putInt(this.getNumEntries());
            for (final InvPos invPos : this.invMap.keySet()) {
                final ItemInfo itemInfo = this.invMap.get(invPos);
                buf.putOID(itemInfo.itemOid);
                buf.putInt(invPos.bagNum);
                buf.putInt(invPos.bagPos);
                buf.putString(itemInfo.itemName);
                buf.putString(itemInfo.itemIcon);
            }
            buf.flip();
            return buf;
        }
        
        @Override
        public void parseBytes(final AOByteBuffer buf) {
            buf.getOID();
            buf.getInt();
            for (int nEntry = buf.getInt(), ii = 0; ii < nEntry; ++ii) {
                final OID itemOid = buf.getOID();
                final InvPos invPos = new InvPos();
                invPos.bagNum = buf.getInt();
                invPos.bagPos = buf.getInt();
                this.invMap.put(invPos, new ItemInfo(itemOid, buf.getString(), buf.getString()));
            }
        }
        
        public static class InvPos implements Serializable
        {
            public Integer bagNum;
            public Integer bagPos;
            private static final long serialVersionUID = 1L;
            
            public InvPos() {
            }
            
            public InvPos(final int bagNum, final int bagPos) {
                this.bagNum = bagNum;
                this.bagPos = bagPos;
            }
            
            @Override
            public String toString() {
                return "[InvPos bagNum=" + this.bagNum + ", bagPos=" + this.bagPos + "]";
            }
            
            @Override
            public boolean equals(final Object other) {
                final InvPos otherI = (InvPos)other;
                return this.bagNum != null && this.bagPos != null && this.bagNum.equals(otherI.bagNum) && this.bagPos.equals(otherI.bagPos);
            }
            
            @Override
            public int hashCode() {
                return this.bagNum.hashCode() ^ this.bagPos.hashCode();
            }
        }
    }
    
    public static class ItemInfo implements Serializable
    {
        public OID itemOid;
        public String itemName;
        public String itemIcon;
        private static final long serialVersionUID = 1L;
        
        public ItemInfo() {
        }
        
        public ItemInfo(final OID itemOid, final String itemName, final String itemIcon) {
            this.itemOid = itemOid;
            this.itemName = itemName;
            this.itemIcon = itemIcon;
        }
        
        @Override
        public String toString() {
            return "[ItemInfo: itemOid=" + this.itemOid + ",itemName=" + this.itemName + ",itemIcon=" + this.itemIcon + "]";
        }
    }
    
    public static class ActivateMessage extends SubjectMessage
    {
        protected OID activatorOid;
        protected OID targetOid;
        private static final long serialVersionUID = 1L;
        
        public ActivateMessage() {
            super(InventoryClient.MSG_TYPE_ACTIVATE);
        }
        
        public ActivateMessage(final OID objOid, final OID activatorOid, final OID targetOid) {
            super(InventoryClient.MSG_TYPE_ACTIVATE, objOid);
            this.setActivatorOid(activatorOid);
            this.setTargetOid(targetOid);
        }
        
        public void setActivatorOid(final OID oid) {
            this.activatorOid = oid;
        }
        
        public OID getActivatorOid() {
            return this.activatorOid;
        }
        
        public void setTargetOid(final OID oid) {
            this.targetOid = oid;
        }
        
        public OID getTargetOid() {
            return this.targetOid;
        }
    }
    
    public static class LootAllMessage extends SubjectMessage
    {
        OID containerOid;
        private static final long serialVersionUID = 1L;
        
        public LootAllMessage() {
            super(InventoryClient.MSG_TYPE_LOOTALL);
        }
        
        public LootAllMessage(final OID looterOid, final OID containerOid) {
            super(InventoryClient.MSG_TYPE_LOOTALL, looterOid);
            this.setContainerOid(containerOid);
        }
        
        public void setContainerOid(final OID containerOid) {
            this.containerOid = containerOid;
        }
        
        public OID getContainerOid() {
            return this.containerOid;
        }
    }
}
