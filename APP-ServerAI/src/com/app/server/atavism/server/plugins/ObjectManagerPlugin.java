// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.server.plugins;

import java.util.LinkedHashMap;
import atavism.server.engine.BasicWorldNode;
import atavism.server.engine.WMWorldNode;
import atavism.server.objects.AOObject;
import atavism.msgsys.GenericMessage;
import atavism.server.objects.ObjectTypes;
import atavism.msgsys.SubjectMessage;
import java.util.Set;
import atavism.server.util.Table;
import atavism.msgsys.BooleanResponseMessage;
import atavism.msgsys.ResponseMessage;
import atavism.server.messages.OIDNamespaceMessage;
import atavism.msgsys.ResponseCallback;
import atavism.msgsys.NoRecipientsException;
import atavism.server.math.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.io.Serializable;
import atavism.server.objects.Entity;
import atavism.server.objects.EntityManager;
import atavism.server.objects.ObjectType;
import atavism.msgsys.GenericResponseMessage;
import java.util.Iterator;
import atavism.msgsys.Message;
import atavism.server.util.Log;
import atavism.server.engine.Hook;
import java.util.List;
import atavism.server.util.AORuntimeException;
import java.util.Collection;
import atavism.server.engine.Namespace;
import java.util.ArrayList;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.management.Management;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.engine.OID;
import java.util.Map;
import atavism.server.objects.Template;
import atavism.server.engine.Manager;
import java.util.HashMap;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class ObjectManagerPlugin extends EnginePlugin
{
    private static final int INSTANCE_OK = 0;
    private static final int INSTANCE_LOADING = 1;
    private static final int INSTANCE_UNLOADING = 2;
    private static final int INSTANCE_DELETING = 3;
    public static int nextFreeTemplateID;
    public static String ITEM_TEMPLATE;
    public static String MOB_TEMPLATE;
    protected static final Logger log;
    protected HashMap<String, Manager<Template>> templateManager;
    private Map<OID, InstanceState> instanceContent;
    
    public ObjectManagerPlugin() {
        super("ObjectManager");
        this.templateManager = new HashMap<String, Manager<Template>>();
        this.instanceContent = new HashMap<OID, InstanceState>();
        this.setPluginType("ObjectManager");
    }
    
    @Override
    public void onActivate() {
        try {
            ObjectManagerPlugin.log.debug("ObjectManagerPlugin.onActivate started");
            this.registerTemplate(new Template("BaseTemplate", -1, "BaseTemplate"));
            this.registerHooks();
            final MessageTypeFilter filter = new MessageTypeFilter();
            filter.addType(ObjectManagerClient.MSG_TYPE_SET_PERSISTENCE);
            filter.addType(ObjectManagerClient.MSG_TYPE_MODIFY_NAMESPACE);
            filter.addType(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT);
            filter.addType(ObjectManagerClient.MSG_TYPE_UNLOAD_OBJECT);
            filter.addType(ObjectManagerClient.MSG_TYPE_DELETE_OBJECT);
            filter.addType(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT_DATA);
            filter.addType(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT);
            filter.addType(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT_DATA);
            filter.addType(ObjectManagerClient.MSG_TYPE_GENERATE_OBJECT);
            filter.addType(ObjectManagerClient.MSG_TYPE_REGISTER_TEMPLATE);
            filter.addType(ObjectManagerClient.MSG_TYPE_GET_TEMPLATE);
            filter.addType(ObjectManagerClient.MSG_TYPE_GET_TEMPLATE_NAMES);
            filter.addType(ObjectManagerClient.MSG_TYPE_FIX_WNODE_REQ);
            filter.addType(InstanceClient.MSG_TYPE_UNLOAD_INSTANCE);
            filter.addType(InstanceClient.MSG_TYPE_DELETE_INSTANCE);
            filter.addType(InstanceClient.MSG_TYPE_LOAD_INSTANCE_CONTENT);
            filter.addType(ObjectManagerClient.MSG_TYPE_GET_NAMED_OBJECT);
            filter.addType(ObjectManagerClient.MSG_TYPE_GET_MATCHING_OBJECTS);
            filter.addType(Management.MSG_TYPE_GET_PLUGIN_STATUS);
            filter.addType(ObjectManagerClient.MSG_TYPE_GET_OBJECT_STATUS);
            Engine.getAgent().createSubscription(filter, this, 8);
            final List<Namespace> namespaces = new ArrayList<Namespace>();
            namespaces.add(Namespace.OBJECT_MANAGER);
            this.registerPluginNamespaces(namespaces, null);
            ObjectManagerPlugin.log.debug("onActivate completed");
        }
        catch (Exception e) {
            throw new AORuntimeException("activate failed", e);
        }
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_SET_PERSISTENCE, new SetPersistenceHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_MODIFY_NAMESPACE, new ModifyNamespaceHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT, new LoadObjectHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_UNLOAD_OBJECT, new UnloadObjectHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_DELETE_OBJECT, new DeleteObjectHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT_DATA, new LoadObjectDataHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT_DATA, new SaveObjectDataHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT, new SaveObjectHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_GENERATE_OBJECT, new GenerateObjectHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_REGISTER_TEMPLATE, new RegisterTemplateHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_GET_TEMPLATE, new GetTemplateHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_GET_TEMPLATE_NAMES, new GetTemplateNamesHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_FIX_WNODE_REQ, new FixWorldNodeHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_UNLOAD_INSTANCE, new UnloadInstanceHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_DELETE_INSTANCE, new DeleteInstanceHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_LOAD_INSTANCE_CONTENT, new LoadInstanceContentHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_GET_NAMED_OBJECT, new GetNamedObjectHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_GET_MATCHING_OBJECTS, new GetMatchingObjectsHook());
        this.getHookManager().addHook(Management.MSG_TYPE_GET_PLUGIN_STATUS, new GetPluginStatusHook());
        this.getHookManager().addHook(ObjectManagerClient.MSG_TYPE_GET_OBJECT_STATUS, new GetObjectStatusHook());
    }
    
    void resolveDeps(final OID masterOid, final Namespace namespace, final Map<Namespace, Collection<Namespace>> depMap) {
        if (Log.loggingDebug) {
            ObjectManagerPlugin.log.debug("resolveDeps: masterOid=" + masterOid + ", ns=" + namespace);
        }
        final Collection<Namespace> depNamespaces = depMap.get(namespace);
        if (depNamespaces == null) {
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("resolveDeps: no deps for ns " + namespace);
            }
            return;
        }
        if (Log.loggingDebug) {
            if (depNamespaces == null) {
                Log.debug("resolveDeps: depNamespaces is null");
            }
            else {
                Log.debug("resolveDeps: depNamespaces.size() " + depNamespaces.size());
                int i = 0;
                for (final Object object : depNamespaces) {
                    Log.debug("resolveDeps: depNamespaces element " + i++ + " " + object);
                }
            }
        }
        for (final Namespace depNS : depNamespaces) {
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("resolveDeps: ns " + namespace + " depends on ns " + depNS);
            }
            final Collection<Namespace> childDeps = depMap.get(depNS);
            if (childDeps != null) {
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("resolveDeps: ns " + namespace + ", depNS=" + depNS + ", has further deps, recursing");
                }
                this.resolveDeps(masterOid, depNS, depMap);
            }
        }
        if (Log.loggingDebug) {
            ObjectManagerPlugin.log.debug("resolveDeps: ns " + namespace + ": resolved all deps, removing from table");
        }
        depMap.remove(namespace);
        final ObjectManagerClient.SubObjectDepsReadyMessage msg = new ObjectManagerClient.SubObjectDepsReadyMessage(masterOid, namespace);
        final Boolean resp = Engine.getAgent().sendRPCReturnBoolean(msg);
        if (resp.equals(Boolean.FALSE)) {
            ObjectManagerPlugin.log.error("dependency failed");
        }
        if (Log.loggingDebug) {
            ObjectManagerPlugin.log.debug("resolveDeps: ns " + namespace + ": got response msg, result=" + resp);
        }
    }
    
    GenericResponseMessage generateSubObject(final OID masterOid, final Namespace namespace, final Template template) {
        final ObjectManagerClient.GenerateSubObjectMessage msg = new ObjectManagerClient.GenerateSubObjectMessage(masterOid, namespace, template);
        final GenericResponseMessage respMsg = (GenericResponseMessage)Engine.getAgent().sendRPC(msg);
        return respMsg;
    }
    
    private void addInstance(final MasterObject instance) {
        final InstanceState instanceState = new InstanceState(instance);
        synchronized (this.instanceContent) {
            final InstanceState previous = this.instanceContent.put(instance.getOid(), instanceState);
            if (previous != null) {
                Log.error("addInstance: duplicate instance [OLD " + previous + "] [NEW " + instanceState + "]");
            }
        }
        if (Log.loggingDebug) {
            Log.debug("addInstance: added instanceOid=" + instance.getOid());
        }
    }
    
    private void removeInstance(final MasterObject instance) {
        synchronized (this.instanceContent) {
            final InstanceState instanceState = this.instanceContent.get(instance.getOid());
            if (instanceState == null) {
                Log.error("removeInstance: unknown instanceOid=" + instance.getOid());
                return;
            }
            if (instanceState.entities.size() > 0) {
                Log.warn("removeInstance: wrong state: " + instanceState);
            }
            this.instanceContent.remove(instance.getOid());
        }
        if (Log.loggingDebug) {
            Log.debug("removeInstance: removed instanceOid=" + instance.getOid());
        }
    }
    
    private void addInstanceContent(final OID instanceOid, final MasterObject entity) {
        if (Log.loggingDebug) {
            Log.debug("addInstanceContent: instanceOid=" + instanceOid + " oid=" + entity.getOid());
        }
        synchronized (this.instanceContent) {
            final InstanceState instanceState = this.instanceContent.get(instanceOid);
            if (instanceState == null) {
                Log.error("addInstanceContent: unknown instanceOid=" + instanceOid + " for " + entity);
                return;
            }
            instanceState.entities.add(entity);
        }
    }
    
    private void removeInstanceContent(final OID instanceOid, final MasterObject entity) {
        synchronized (this.instanceContent) {
            final InstanceState instanceState = this.instanceContent.get(instanceOid);
            if (instanceState == null) {
                Log.error("removeInstanceContent: unknown instanceOid=" + instanceOid);
                return;
            }
            if (Log.loggingDebug) {
                Log.debug("removeInstanceContent: instanceOid=" + instanceOid + " oid=" + entity.getOid() + " count=" + instanceState.entities.size());
            }
            instanceState.entities.remove(entity);
        }
    }
    
    private boolean isInstanceOk(final OID instanceOid, final int newStatus) {
        synchronized (this.instanceContent) {
            final InstanceState instanceState = this.instanceContent.get(instanceOid);
            if (instanceState != null) {
                final boolean result = instanceState.status == 0;
                if (result && newStatus != -1) {
                    instanceState.status = newStatus;
                }
                return result;
            }
            return false;
        }
    }
    
    private boolean isInstanceLoading(final OID instanceOid) {
        synchronized (this.instanceContent) {
            final InstanceState instanceState = this.instanceContent.get(instanceOid);
            return instanceState != null && (instanceState.status == 0 || instanceState.status == 1);
        }
    }
    
    private void setInstanceStatus(final OID instanceOid, final int newStatus) {
        synchronized (this.instanceContent) {
            final InstanceState instanceState = this.instanceContent.get(instanceOid);
            instanceState.status = newStatus;
        }
    }
    
    private OID getInstanceNamedObject(final OID instanceOid, final String name, final ObjectType objectType) {
        synchronized (this.instanceContent) {
            final InstanceState instanceState = this.instanceContent.get(instanceOid);
            if (instanceState == null) {
                return null;
            }
            if (objectType != null) {
                for (final MasterObject entity : instanceState.entities) {
                    final String entityName = entity.getName();
                    if (entity.getType() == objectType && entityName != null && entityName.equals(name)) {
                        return entity.getOid();
                    }
                }
            }
            else {
                for (final MasterObject entity : instanceState.entities) {
                    final String entityName = entity.getName();
                    if (entityName != null && entityName.equals(name)) {
                        return entity.getOid();
                    }
                }
            }
            return null;
        }
    }
    
    private OID getNamedObject(final String name, final ObjectType objectType) {
        final Entity[] entities = EntityManager.getAllEntitiesByNamespace(Namespace.OBJECT_MANAGER);
        if (objectType != null) {
            for (final Entity entity : entities) {
                final String entityName = entity.getName();
                if (entity.getType() == objectType && entityName != null && entityName.equals(name)) {
                    return entity.getOid();
                }
            }
        }
        else {
            for (final Entity entity : entities) {
                final String entityName = entity.getName();
                if (entityName != null && entityName.equals(name)) {
                    return entity.getOid();
                }
            }
        }
        return null;
    }
    
    private List<OID> getMatchingObjects(final OID oid, final String name, final ObjectType objectType, final Map<Namespace, Map<String, Serializable>> filters) {
        final List<OID> rv = new LinkedList<OID>();
        final Collection<Entity> entities = new HashSet<Entity>();
        if (oid == null) {
            final Entity[] arr$;
            final Entity[] entityArray = arr$ = EntityManager.getAllEntitiesByNamespace(Namespace.OBJECT_MANAGER);
            for (final Entity entity : arr$) {
                entities.add(entity);
            }
        }
        else {
            final InstanceState instanceState = this.instanceContent.get(oid);
            if (instanceState == null) {
                return rv;
            }
            for (final Entity entity2 : instanceState.entities) {
                entities.add(entity2);
            }
        }
        for (final Entity entity3 : entities) {
            if (name != null) {
                final String entityName = entity3.getName();
                if (entityName == null) {
                    continue;
                }
                if (!entityName.equals(name)) {
                    continue;
                }
            }
            if (objectType != null && !entity3.getType().isA(objectType)) {
                continue;
            }
            boolean possibleMatch = true;
            if (filters != null) {
                for (final Map.Entry<Namespace, Map<String, Serializable>> namespaceEntry : filters.entrySet()) {
                    final Namespace namespace = namespaceEntry.getKey();
                    final Map<String, Serializable> namespaceFilters = namespaceEntry.getValue();
                    final List<String> keys = new LinkedList<String>();
                    final List<Serializable> filterValues = new LinkedList<Serializable>();
                    for (final Map.Entry<String, Serializable> filter : namespaceFilters.entrySet()) {
                        keys.add(filter.getKey());
                        filterValues.add(filter.getValue());
                    }
                    final List<Serializable> objectValues = EnginePlugin.getObjectProperties(entity3.getOid(), namespace, keys);
                    if (objectValues == null) {
                        possibleMatch = false;
                        break;
                    }
                    for (int i = 0; i < keys.size(); ++i) {
                        final String key = keys.get(i);
                        final Serializable filterVal = filterValues.get(i);
                        final Serializable val = objectValues.get(i);
                        if (filterVal != null || val != null) {
                            if ((filterVal != null && val == null) || (filterVal == null && val != null) || !filterVal.equals(val)) {
                                possibleMatch = false;
                                break;
                            }
                        }
                    }
                    if (!possibleMatch) {
                        break;
                    }
                }
            }
            if (!possibleMatch) {
                continue;
            }
            rv.add(entity3.getOid());
        }
        return rv;
    }
    
    protected boolean registerTemplate(final Template tmpl) {
        final String templateType = tmpl.getTemplateType();
        if (!this.templateManager.containsKey(templateType)) {
            this.templateManager.put(templateType, new Manager<Template>(templateType + "Manager"));
        }
        return this.templateManager.get(templateType).register(tmpl.getTemplateID(), tmpl);
    }
    
    public static int getNextFreeTemplateID() {
        return ObjectManagerPlugin.nextFreeTemplateID--;
    }
    
    static {
        ObjectManagerPlugin.nextFreeTemplateID = -2;
        ObjectManagerPlugin.ITEM_TEMPLATE = "item";
        ObjectManagerPlugin.MOB_TEMPLATE = "mob";
        log = new Logger("ObjectManagerPlugin");
    }
    
    class ModifyNamespaceHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flag) {
            final ObjectManagerClient.ModifyNamespaceMessage message = (ObjectManagerClient.ModifyNamespaceMessage)msg;
            final OID oid = message.getOid();
            final Collection<Namespace> namespaces = message.getNamespaces();
            final String command = message.getCommand();
            final Entity masterEntity = EntityManager.getEntityByNamespace(oid, Namespace.OBJECT_MANAGER);
            final Boolean rv = masterEntity != null;
            if (rv) {
                for (final Namespace ns : namespaces) {
                    masterEntity.addSubObjectNamespace(ns);
                }
                Engine.getPersistenceManager().setDirty(masterEntity);
            }
            Engine.getAgent().sendBooleanResponse(msg, rv);
            return true;
        }
    }
    
    class LoadObjectHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.LoadObjectMessage msg = (ObjectManagerClient.LoadObjectMessage)m;
            OID oid = msg.getOid();
            String persistenceKey = null;
            MasterObject entity = null;
            if (oid == null) {
                persistenceKey = msg.getKey();
                if (persistenceKey == null) {
                    Log.warn("LoadObjectHook: no key or oid");
                    Engine.getAgent().sendOIDResponse(msg, null);
                    return false;
                }
                final Entity temp = Engine.getDatabase().loadEntity(persistenceKey);
                if (temp == null) {
                    ObjectManagerPlugin.log.error("LoadObjectHook: unknown object, key=" + persistenceKey);
                    Engine.getAgent().sendOIDResponse(msg, null);
                    return false;
                }
                if (!(temp instanceof MasterObject) || temp.getSubObjectNamespacesInt() == null) {
                    ObjectManagerPlugin.log.error("LoadObjectHook: not a master object, key=" + persistenceKey + " oid=" + temp.getOid());
                    Engine.getAgent().sendOIDResponse(msg, null);
                    return false;
                }
                entity = (MasterObject)EntityManager.getEntityByNamespace(temp.getOid(), Namespace.OBJECT_MANAGER);
                if (entity != null) {
                    if (entity.loadComplete()) {
                        Log.debug("LoadObjectHook: object already loaded oid=" + oid + " entity=" + entity);
                        Engine.getAgent().sendOIDResponse(msg, oid);
                        return false;
                    }
                }
                else {
                    entity = (MasterObject)temp;
                    EntityManager.registerEntityByNamespace(entity, Namespace.OBJECT_MANAGER);
                }
                oid = entity.getOid();
            }
            else {
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("LoadObjectHook: master oid=" + oid);
                }
                entity = (MasterObject)EntityManager.getEntityByNamespace(oid, Namespace.OBJECT_MANAGER);
                if (entity != null) {
                    if (entity.loadComplete()) {
                        final Point location = new Point();
                        final OID instanceOid = Engine.getDatabase().getLocation(oid, WorldManagerClient.NAMESPACE, location);
                        Log.debug("LoadObjectHook: object already loaded oid=" + oid + " entity=" + entity + " with instanceOid: " + entity.getInstanceOid() + " and instanceOid from the database: " + instanceOid);
                        if (!instanceOid.equals(entity.getInstanceOid())) {}
                        Engine.getAgent().sendOIDResponse(msg, oid);
                        return false;
                    }
                }
                else {
                    entity = (MasterObject)Engine.getDatabase().loadEntity(oid, Namespace.OBJECT_MANAGER);
                    if (entity != null) {
                        EntityManager.registerEntityByNamespace(entity, Namespace.OBJECT_MANAGER);
                    }
                }
            }
            if (entity == null || entity.isDeleted()) {
                ObjectManagerPlugin.log.error("LoadObjectHook: no such entity with oid " + oid + " or key " + persistenceKey);
                Engine.getAgent().sendOIDResponse(msg, null);
                return false;
            }
            Collection<Namespace> namespaces = msg.getNamespaces();
            if (namespaces == null) {
                namespaces = entity.getSubObjectNamespaces();
            }
            OID instanceOid = null;
            Point location2 = null;
            if (namespaces.contains(WorldManagerClient.NAMESPACE) && !entity.isNamespaceLoaded(WorldManagerClient.NAMESPACE)) {
                location2 = new Point();
                instanceOid = Engine.getDatabase().getLocation(oid, WorldManagerClient.NAMESPACE, location2);
                if (instanceOid == null) {
                    Log.error("LoadObjectHook: world manager object missing instanceOid, entity=" + entity);
                    Engine.getAgent().sendOIDResponse(msg, null);
                    return false;
                }
                Log.debug("POP: got instance on load: " + instanceOid);
                if (ObjectManagerPlugin.this.instanceContent.get(instanceOid) == null) {
                    final int rc = InstanceClient.loadInstance(instanceOid);
                    if (rc != 0) {
                        if (rc != -1) {
                            Log.error("LoadObjectHook: internal error loading instanceOid=" + instanceOid + " for oid=" + oid + ", rc=" + rc);
                        }
                        Engine.getAgent().sendOIDResponse(msg, null);
                        return false;
                    }
                }
                if (!ObjectManagerPlugin.this.isInstanceLoading(instanceOid)) {
                    Log.error("LoadObjectHook: instance unavailable for oid=" + oid + " instanceOid=" + instanceOid + " " + ObjectManagerPlugin.this.instanceContent.get(instanceOid));
                    Engine.getAgent().sendOIDResponse(msg, null);
                    return false;
                }
                entity.setInstanceOid(instanceOid);
            }
            for (final Namespace namespace : namespaces) {
                if (entity.isNamespaceLoaded(namespace)) {
                    continue;
                }
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("LoadObjectHook: masterOid=" + oid + ", sending load subobj msg, ns=" + namespace);
                }
                ObjectManagerClient.LoadSubObjectMessage loadSubMsg;
                if (namespace == WorldManagerClient.NAMESPACE) {
                    loadSubMsg = new WorldManagerClient.LoadSubObjectMessage(oid, namespace, location2, instanceOid);
                }
                else {
                    loadSubMsg = new ObjectManagerClient.LoadSubObjectMessage(oid, namespace);
                }
                Boolean rv;
                try {
                    rv = Engine.getAgent().sendRPCReturnBoolean(loadSubMsg);
                }
                catch (NoRecipientsException e) {
                    ObjectManagerPlugin.log.exception("LoadObjectHook: sub object load failed, maybe instance does not exist", e);
                    Engine.getAgent().sendOIDResponse(msg, null);
                    return false;
                }
                if (!rv) {
                    ObjectManagerPlugin.log.error("LoadObjectHook: sub object load failed: " + namespace);
                    Engine.getAgent().sendOIDResponse(msg, null);
                    return false;
                }
                entity.addLoadedNamespace(namespace);
            }
            if (namespaces.contains(WorldManagerClient.INSTANCE_NAMESPACE)) {
                ObjectManagerPlugin.this.addInstance(entity);
            }
            if (instanceOid != null && !entity.getType().isPlayer()) {
                ObjectManagerPlugin.this.addInstanceContent(instanceOid, entity);
            }
            Engine.getAgent().sendOIDResponse(msg, oid);
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("LoadObjectHook: sent success response for master obj=" + oid);
            }
            return true;
        }
    }
    
    class UnloadObjectHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.UnloadObjectMessage msg = (ObjectManagerClient.UnloadObjectMessage)m;
            final OID oid = msg.getOid();
            final MasterObject entity = (MasterObject)EntityManager.getEntityByNamespace(oid, Namespace.OBJECT_MANAGER);
            if (entity == null) {
                ObjectManagerPlugin.log.error("UnloadObjectHook: no such entity oid=" + oid);
                Engine.getAgent().sendBooleanResponse(msg, false);
                return false;
            }
            Collection<Namespace> namespaces = msg.getNamespaces();
            if (namespaces == null) {
                namespaces = entity.getSubObjectNamespaces();
            }
            int failure = 0;
            for (final Namespace namespace : namespaces) {
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("UnloadObjectHook: oid=" + oid + ", sending unload subobj msg, ns=" + namespace);
                }
                final ObjectManagerClient.UnloadSubObjectMessage unloadSubMsg = new ObjectManagerClient.UnloadSubObjectMessage(oid, namespace);
                final Boolean rv = Engine.getAgent().sendRPCReturnBoolean(unloadSubMsg);
                if (!rv) {
                    ObjectManagerPlugin.log.error("UnloadObjectHook: sub object unload failed oid=" + oid + " ns=" + namespace);
                    ++failure;
                }
                else {
                    if (msg.getNamespaces() == null) {
                        continue;
                    }
                    entity.removeLoadedNamespace(namespace);
                }
            }
            if (msg.getNamespaces() == null) {
                EntityManager.removeEntityByNamespace(entity, Namespace.OBJECT_MANAGER);
                if (entity.getPersistenceFlag() && Engine.getPersistenceManager().isDirty(entity)) {
                    Engine.getPersistenceManager().persistEntity(entity);
                }
            }
            if (namespaces.contains(WorldManagerClient.INSTANCE_NAMESPACE)) {
                ObjectManagerPlugin.this.removeInstance(entity);
            }
            if (entity.getInstanceOid() != null && !entity.getType().isPlayer() && namespaces.contains(WorldManagerClient.NAMESPACE)) {
                ObjectManagerPlugin.this.removeInstanceContent(entity.getInstanceOid(), entity);
            }
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("UnloadObjectHook: unloaded oid=" + oid + ", " + failure + " failures");
            }
            Engine.getAgent().sendBooleanResponse(msg, failure == 0);
            return true;
        }
    }
    
    class DeleteObjectHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.DeleteObjectMessage msg = (ObjectManagerClient.DeleteObjectMessage)m;
            final OID oid = msg.getOid();
            final MasterObject entity = (MasterObject)EntityManager.getEntityByNamespace(oid, Namespace.OBJECT_MANAGER);
            if (entity == null) {
                ObjectManagerPlugin.log.debug("DeleteObjectHook: no such entity oid=" + oid);
                Engine.getDatabase().deleteObjectData(oid);
                Engine.getAgent().sendBooleanResponse(msg, true);
                return false;
            }
            if (entity.isDeleted()) {
                return true;
            }
            entity.setDeleted();
            final List<Namespace> namespaces = entity.getSubObjectNamespaces();
            int failure = 0;
            for (final Namespace namespace : namespaces) {
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("DeleteObjectHook: oid=" + oid + ", sending delete subobj msg, ns=" + namespace);
                }
                final ObjectManagerClient.DeleteSubObjectMessage deleteSubMsg = new ObjectManagerClient.DeleteSubObjectMessage(oid, namespace);
                final Boolean rv = Engine.getAgent().sendRPCReturnBoolean(deleteSubMsg);
                if (!rv) {
                    ObjectManagerPlugin.log.error("DeleteObjectHook: sub object delete failed oid=" + oid + " ns=" + namespace);
                    ++failure;
                }
            }
            Engine.getDatabase().deleteObjectData(oid);
            EntityManager.removeEntityByNamespace(entity, Namespace.OBJECT_MANAGER);
            if (namespaces.contains(WorldManagerClient.INSTANCE_NAMESPACE)) {
                ObjectManagerPlugin.this.removeInstance(entity);
            }
            if (entity.getInstanceOid() != null && !entity.getType().isPlayer()) {
                ObjectManagerPlugin.this.removeInstanceContent(entity.getInstanceOid(), entity);
            }
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("DeleteObjectHook: deleted oid=" + oid + ", " + failure + " failures");
            }
            Engine.getAgent().sendBooleanResponse(msg, failure == 0);
            return true;
        }
    }
    
    class SaveObjectHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.SaveObjectMessage msg = (ObjectManagerClient.SaveObjectMessage)m;
            new SaveObjectProcessor(msg).processMessage();
            return true;
        }
    }
    
    static class SaveObjectProcessor implements ResponseCallback
    {
        ObjectManagerClient.SaveObjectMessage msg;
        OID oid;
        String key;
        MasterObject masterObj;
        List<Message> pendingRPC;
        
        public SaveObjectProcessor(final ObjectManagerClient.SaveObjectMessage message) {
            this.msg = message;
            this.oid = this.msg.getOid();
            this.key = this.msg.getKey();
            this.masterObj = (MasterObject)EntityManager.getEntityByNamespace(this.oid, Namespace.OBJECT_MANAGER);
        }
        
        public void processMessage() {
            if (Log.loggingDebug) {
                Log.debug("SaveObjectHook: oid=" + this.oid);
            }
            if (!this.masterObj.getPersistenceFlag()) {
                Log.warn("Ignoring saveObject for non-persistent object oid=" + this.oid);
                Engine.getAgent().sendBooleanResponse(this.msg, Boolean.FALSE);
                return;
            }
            final List<Namespace> namespaces = this.masterObj.getSubObjectNamespaces();
            if (Log.loggingDebug) {
                String s = "";
                for (final Namespace ns : namespaces) {
                    if (s != "") {
                        s += ",";
                    }
                    s += ns;
                }
                Log.debug("SaveObjectHook: masterObj namespaces " + s);
            }
            synchronized (this.pendingRPC = new ArrayList<Message>(namespaces.size())) {
                for (final Namespace namespace : namespaces) {
                    if (Log.loggingDebug) {
                        Log.debug("SaveObjectHook: oid=" + this.oid + ", sending save subobj msg to ns=" + namespace);
                    }
                    final Message saveSubMsg = new OIDNamespaceMessage(ObjectManagerClient.MSG_TYPE_SAVE_SUBOBJECT, this.oid, namespace);
                    this.pendingRPC.add(saveSubMsg);
                    Engine.getAgent().sendRPC(saveSubMsg, this);
                }
            }
        }
        
        @Override
        public void handleResponse(final ResponseMessage response) {
            synchronized (this.pendingRPC) {
                Message request = null;
                for (final Message message : this.pendingRPC) {
                    if (message.getMsgId() == response.getRequestId()) {
                        this.pendingRPC.remove(message);
                        request = message;
                        break;
                    }
                }
                if (request == null) {
                    Log.error("SaveObjectHook: unexpected response " + response);
                }
                if (!((BooleanResponseMessage)response).getBooleanVal()) {
                    ObjectManagerPlugin.log.warn("SaveObjectHook: sub object load failed for oid=" + this.oid + " " + request);
                }
            }
            if (this.pendingRPC.size() == 0) {
                this.saveMasterObject();
                Engine.getAgent().sendBooleanResponse(this.msg, Boolean.TRUE);
            }
        }
        
        void saveMasterObject() {
            if (Log.loggingDebug) {
                Log.debug("SaveObjectHook: saving master object oid=" + this.oid);
            }
            Engine.getPersistenceManager().callSaveHooks(this.masterObj);
            Engine.getDatabase().saveObject(this.key, this.masterObj.toBytes(), this.masterObj.getNamespace());
            if (Log.loggingDebug) {
                Log.debug("SaveObjectHook: success oid=" + this.oid);
            }
        }
    }
    
    class LoadObjectDataHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.LoadObjectDataMessage msg = (ObjectManagerClient.LoadObjectDataMessage)m;
            final OID oid = msg.getSubject();
            final String persistenceKey = msg.getKey();
            Entity entity = null;
            if (persistenceKey != null) {
                entity = Engine.getDatabase().loadEntity(persistenceKey);
            }
            else if (oid != null) {
                entity = Engine.getDatabase().loadEntity(oid, msg.getNamespace());
            }
            else {
                ObjectManagerPlugin.log.error("LoadObjectDataHook: oid and key both null");
            }
            Engine.getAgent().sendObjectResponse(msg, entity);
            return true;
        }
    }
    
    class SaveObjectDataHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.SaveObjectDataMessage msg = (ObjectManagerClient.SaveObjectDataMessage)m;
            final OID oid = msg.getSubject();
            final String persistenceKey = msg.getKey();
            if (msg.getNamespace() == Namespace.TRANSIENT) {
                ObjectManagerPlugin.log.warn("SaveObjectDataHook: ignoring transient namespace for oid=" + oid + " key=" + persistenceKey);
                Engine.getAgent().sendBooleanResponse(msg, Boolean.FALSE);
                return false;
            }
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("SaveObjectDataHook: oid=" + oid);
            }
            final byte[] data = msg.getDataBytes();
            Engine.getDatabase().saveObject(persistenceKey, data, msg.getNamespace());
            Engine.getAgent().sendBooleanResponse(msg, Boolean.TRUE);
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("SaveObjectDataHook: sent response for obj=" + oid);
            }
            return true;
        }
    }
    
    class GenerateObjectHook implements Hook
    {
        Table<OID, Namespace, Collection<Namespace>> depTable;
        
        GenerateObjectHook() {
            this.depTable = new Table<OID, Namespace, Collection<Namespace>>();
        }
        
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.GenerateObjectMessage msg = (ObjectManagerClient.GenerateObjectMessage)m;
            final int templateID = msg.getTemplateID();
            final String templateType = msg.getTemplateType();
            Log.debug("ANDREW: getting template: " + templateID + " of templateType: " + templateType);
            final Template template = ObjectManagerPlugin.this.templateManager.get(templateType).get(templateID);
            if (template == null) {
                Log.error("template not found: " + templateType + ":" + templateID);
                Engine.getAgent().sendOIDResponse(msg, null);
                return false;
            }
            final Template overrideTemplate = msg.getOverrideTemplate();
            Template finalTemplate;
            if (overrideTemplate != null) {
                finalTemplate = template.merge(overrideTemplate);
            }
            else {
                finalTemplate = template;
            }
            Boolean persistent = (Boolean)finalTemplate.get(Namespace.OBJECT_MANAGER, ":persistent");
            if (persistent == null) {
                persistent = false;
            }
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("GenerateObjectHook: generating entity: " + finalTemplate.getName() + ", template=" + finalTemplate);
            }
            String entityName = (String)finalTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_NAME);
            if (entityName == null) {
                entityName = finalTemplate.getName();
            }
            final OID instanceOid = (OID)finalTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE);
            final ObjectType objectType = (ObjectType)finalTemplate.get(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE);
            final MasterObject masterObj = new MasterObject(entityName);
            masterObj.setPersistenceFlag(persistent);
            masterObj.setInstanceOid(instanceOid);
            if (objectType != null) {
                masterObj.setType(objectType);
            }
            final Map<String, Serializable> objMgrProps = finalTemplate.getSubMap(Namespace.OBJECT_MANAGER);
            if (objMgrProps != null) {
                for (final Map.Entry<String, Serializable> entry : objMgrProps.entrySet()) {
                    if (!entry.getKey().startsWith(":")) {
                        masterObj.setProperty(entry.getKey(), entry.getValue());
                    }
                }
            }
            EntityManager.registerEntityByNamespace(masterObj, Namespace.OBJECT_MANAGER);
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("GenerateObjectHook: created master obj: " + masterObj);
            }
            final Set<Namespace> namespaces = finalTemplate.getNamespaces();
            namespaces.remove(Namespace.OBJECT_MANAGER);
            masterObj.setSubObjectNamespaces(namespaces);
            if (persistent) {
                Engine.getPersistenceManager().persistEntity(masterObj);
            }
            for (final Namespace namespace : namespaces) {
                final Template subTemplate = finalTemplate.restrict(namespace);
                subTemplate.put(Namespace.OBJECT_MANAGER, ":persistent", persistent);
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("GenerateObjectHook: creating subobj for ns=" + namespace + ", subTemplate=" + subTemplate);
                }
                final GenericResponseMessage respMsg = ObjectManagerPlugin.this.generateSubObject(masterObj.getOid(), namespace, subTemplate);
                masterObj.addLoadedNamespace(namespace);
                final List<Namespace> depNamespaces = (List<Namespace>)respMsg.getData();
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("GenerateObjectHook: created subobj for ns=" + namespace);
                }
                if (depNamespaces != null) {
                    if (depNamespaces.isEmpty()) {
                        continue;
                    }
                    this.depTable.put(masterObj.getOid(), namespace, depNamespaces);
                }
            }
            final Map<Namespace, Collection<Namespace>> depMap = this.depTable.getSubMap(masterObj.getOid());
            if (depMap != null && !depMap.isEmpty()) {
                while (!depMap.isEmpty()) {
                    final Namespace ns = depMap.keySet().iterator().next();
                    ObjectManagerPlugin.this.resolveDeps(masterObj.getOid(), ns, depMap);
                }
            }
            if (namespaces.contains(WorldManagerClient.INSTANCE_NAMESPACE)) {
                ObjectManagerPlugin.this.addInstance(masterObj);
            }
            if (instanceOid != null && !masterObj.getType().isPlayer()) {
                ObjectManagerPlugin.this.addInstanceContent(instanceOid, masterObj);
            }
            Engine.getAgent().sendOIDResponse(msg, masterObj.getOid());
            return true;
        }
    }
    
    class LoadInstanceContentHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final SubjectMessage message = (SubjectMessage)m;
            final OID instanceOid = message.getSubject();
            final MasterObject entity = (MasterObject)EntityManager.getEntityByNamespace(instanceOid, Namespace.OBJECT_MANAGER);
            if (entity == null) {
                Log.error("LoadInstanceContentHook: instance not loaded instanceOid=" + instanceOid);
                Engine.getAgent().sendBooleanResponse(message, false);
                return true;
            }
            if (!ObjectManagerPlugin.this.isInstanceOk(instanceOid, 1)) {
                Log.error("LoadInstanceContentHook: instance not available instanceOid=" + instanceOid);
                Engine.getAgent().sendBooleanResponse(message, false);
                return true;
            }
            final List<OID> content = Engine.getDatabase().getInstanceContent(instanceOid, ObjectTypes.player);
            for (final OID oid : content) {
                if (ObjectManagerClient.loadObject(oid) != null) {
                    WorldManagerClient.spawn(oid);
                }
            }
            ObjectManagerPlugin.this.setInstanceStatus(instanceOid, 0);
            Engine.getAgent().sendBooleanResponse(message, true);
            return true;
        }
    }
    
    class UnloadInstanceHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final SubjectMessage message = (SubjectMessage)m;
            final OID instanceOid = message.getSubject();
            final MasterObject entity = (MasterObject)EntityManager.getEntityByNamespace(instanceOid, Namespace.OBJECT_MANAGER);
            if (entity == null) {
                Log.error("UnloadInstanceHook: instance not loaded oid=" + instanceOid);
                Engine.getAgent().sendBooleanResponse(message, false);
                return true;
            }
            if (!ObjectManagerPlugin.this.isInstanceOk(instanceOid, 2)) {
                Log.error("UnloadInstanceHook: instance not available instanceOid=" + instanceOid);
                Engine.getAgent().sendBooleanResponse(message, false);
                return true;
            }
            final InstanceState instanceState = ObjectManagerPlugin.this.instanceContent.get(instanceOid);
            if (instanceState != null) {
                final List<MasterObject> objects = new ArrayList<MasterObject>(instanceState.entities);
                for (final MasterObject obj : objects) {
                    if (!obj.getType().isPlayer()) {
                        ObjectManagerClient.unloadObject(obj.getOid());
                    }
                }
            }
            final SubjectMessage unloadedMessage = new SubjectMessage(InstanceClient.MSG_TYPE_INSTANCE_UNLOADED, instanceOid);
            Engine.getAgent().sendBroadcastRPC(unloadedMessage, new InstanceRPCCallback(instanceOid, "InstanceUnloaded"));
            ObjectManagerClient.unloadObject(instanceOid);
            Engine.getAgent().sendBooleanResponse(message, true);
            return true;
        }
    }
    
    class DeleteInstanceHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final SubjectMessage message = (SubjectMessage)m;
            final OID instanceOid = message.getSubject();
            final MasterObject entity = (MasterObject)EntityManager.getEntityByNamespace(instanceOid, Namespace.OBJECT_MANAGER);
            if (entity == null) {
                Log.error("DeleteInstanceHook: instance not loaded oid=" + instanceOid);
                Engine.getAgent().sendBooleanResponse(message, false);
                return true;
            }
            if (!ObjectManagerPlugin.this.isInstanceOk(instanceOid, 3)) {
                Log.error("DeleteInstanceHook: instance not available instanceOid=" + instanceOid);
                Engine.getAgent().sendBooleanResponse(message, false);
                return true;
            }
            final InstanceState instanceState = ObjectManagerPlugin.this.instanceContent.get(instanceOid);
            if (instanceState != null) {
                final List<MasterObject> objects = new ArrayList<MasterObject>(instanceState.entities);
                for (final MasterObject obj : objects) {
                    if (!obj.getType().isPlayer()) {
                        ObjectManagerClient.deleteObject(obj.getOid());
                    }
                }
            }
            final SubjectMessage deletedMessage = new SubjectMessage(InstanceClient.MSG_TYPE_INSTANCE_DELETED, instanceOid);
            Engine.getAgent().sendBroadcastRPC(deletedMessage, new InstanceRPCCallback(instanceOid, "InstanceDeleted"));
            ObjectManagerClient.deleteObject(instanceOid);
            Engine.getAgent().sendBooleanResponse(message, true);
            return true;
        }
    }
    
    public static class InstanceRPCCallback implements ResponseCallback
    {
        OID instanceOid;
        String operation;
        
        public InstanceRPCCallback(final OID instanceOid, final String operation) {
            this.instanceOid = instanceOid;
            this.operation = operation;
        }
        
        @Override
        public void handleResponse(final ResponseMessage response) {
            Log.debug(this.operation + ": got response, instanceOid=" + this.instanceOid);
        }
    }
    
    class SetPersistenceHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.SetPersistenceMessage msg = (ObjectManagerClient.SetPersistenceMessage)m;
            final OID oid = msg.getSubject();
            final Entity master = EntityManager.getEntityByNamespace(oid, Namespace.OBJECT_MANAGER);
            if (master == null) {
                Log.error("SetPersistenceHook: no master entity found for oid " + oid);
                Engine.getAgent().sendBooleanResponse(m, false);
            }
            final Boolean persistVal = msg.getPersistVal();
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("SetPersistenceHook: masterOid=" + oid + ", persistVal=" + persistVal);
            }
            final List<Namespace> namespaces = master.getSubObjectNamespaces();
            for (final Namespace namespace : namespaces) {
                if (Log.loggingDebug) {
                    ObjectManagerPlugin.log.debug("SetPersistenceHook: masterOid=" + oid + ", sending setpersistence msg to sub ns " + namespace);
                }
                final Message persistSubMsg = new ObjectManagerClient.SetSubPersistenceMessage(oid, namespace, persistVal);
                Engine.getAgent().sendRPC(persistSubMsg);
            }
            master.setPersistenceFlag(persistVal);
            if (persistVal) {
                Engine.getPersistenceManager().setDirty(master);
                ObjectManagerPlugin.log.debug("SetPersistenceHook: set master object dirty");
            }
            else {
                Engine.getDatabase().deleteObjectData(oid);
            }
            ObjectManagerPlugin.log.debug("SetPersistenceHook: done with persistence");
            Engine.getAgent().sendBooleanResponse(m, true);
            return true;
        }
    }
    
    class RegisterTemplateHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.RegisterTemplateMessage msg = (ObjectManagerClient.RegisterTemplateMessage)m;
            final Template template = msg.getTemplate();
            final boolean successStatus = ObjectManagerPlugin.this.registerTemplate(template);
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("handleRegisterTemplateMsg: registered template: " + template + ", success=" + successStatus);
            }
            ObjectManagerPlugin.log.debug("handleRegisterTemplateMsg: sending response message");
            Engine.getAgent().sendBooleanResponse(msg, successStatus);
            ObjectManagerPlugin.log.debug("handleRegisterTemplateMsg: response message sent");
            return true;
        }
    }
    
    class GetTemplateHook implements Hook
    {
        @Override
        public boolean processMessage(final Message m, final int flags) {
            final ObjectManagerClient.GetTemplateMessage msg = (ObjectManagerClient.GetTemplateMessage)m;
            final String templateType = msg.getTemplateType();
            final int templateID = msg.getTemplateID();
            final Template template = ObjectManagerPlugin.this.templateManager.get(templateType).get(templateID);
            Engine.getAgent().sendObjectResponse(msg, template);
            return true;
        }
    }
    
    class GetTemplateNamesHook implements Hook
    {
        @Override
        public boolean processMessage(final Message message, final int flags) {
            final GenericMessage msg = (GenericMessage)message;
            final String templateType = (String)msg.getProperty("templateType");
            final List<Integer> templateIDs = ObjectManagerPlugin.this.templateManager.get(templateType).keyList();
            Engine.getAgent().sendObjectResponse(message, templateIDs);
            return true;
        }
    }
    
    class FixWorldNodeHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final ObjectManagerClient.FixWorldNodeMessage message = (ObjectManagerClient.FixWorldNodeMessage)msg;
            final BasicWorldNode worldNode = message.getWorldNode();
            Entity entity = null;
            try {
                entity = Engine.getDatabase().loadEntity(message.getOid(), WorldManagerClient.NAMESPACE);
            }
            catch (AORuntimeException e) {
                Engine.getAgent().sendBooleanResponse(msg, false);
                return false;
            }
            if (entity == null) {
                Log.error("FixWorldNodeHook: unknown oid=" + message.getOid());
                Engine.getAgent().sendBooleanResponse(msg, false);
                return false;
            }
            if (!(entity instanceof AOObject)) {
                Log.error("FixWorldNodeHook: not instanceof AOObject oid=" + message.getOid() + " class=" + entity.getClass().getName());
                Engine.getAgent().sendBooleanResponse(msg, false);
                return false;
            }
            final AOObject obj = (AOObject)entity;
            final WMWorldNode wnode = (WMWorldNode)obj.worldNode();
            wnode.setInstanceOid(worldNode.getInstanceOid());
            if (worldNode.getLoc() != null) {
                wnode.setLoc(worldNode.getLoc());
            }
            if (worldNode.getOrientation() != null) {
                wnode.setOrientation(worldNode.getOrientation());
            }
            if (worldNode.getDir() != null) {
                wnode.setDir(worldNode.getDir());
            }
            Engine.getPersistenceManager().persistEntity(obj);
            if (Log.loggingDebug) {
                ObjectManagerPlugin.log.debug("FixWorldNodeHook: done oid=" + message.getOid() + " wnode=" + obj.worldNode());
            }
            Engine.getAgent().sendBooleanResponse(msg, true);
            return true;
        }
    }
    
    class GetNamedObjectHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final ObjectManagerClient.GetNamedObjectMessage message = (ObjectManagerClient.GetNamedObjectMessage)msg;
            OID oid = null;
            if (message.getInstanceOid() != null) {
                oid = ObjectManagerPlugin.this.getInstanceNamedObject(message.getInstanceOid(), message.getName(), message.getObjectType());
            }
            else if (message.getName() != null) {
                oid = ObjectManagerPlugin.this.getNamedObject(message.getName(), message.getObjectType());
            }
            Engine.getAgent().sendOIDResponse(message, oid);
            return true;
        }
    }
    
    class GetMatchingObjectsHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final ObjectManagerClient.GetMatchingObjectsMessage message = (ObjectManagerClient.GetMatchingObjectsMessage)msg;
            final List<OID> rv = ObjectManagerPlugin.this.getMatchingObjects(message.getInstanceOid(), message.getName(), message.getObjectType(), message.getFilters());
            Engine.getAgent().sendObjectResponse(message, rv);
            return true;
        }
    }
    
    class GetPluginStatusHook implements Hook
    {
        int lastLoginCount;
        
        GetPluginStatusHook() {
            this.lastLoginCount = 0;
        }
        
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final LinkedHashMap<String, Serializable> status = new LinkedHashMap<String, Serializable>();
            status.put("plugin", ObjectManagerPlugin.this.getName());
            try {
                status.put("account", Engine.getDatabase().getAccountCount(Engine.getWorldName()));
            }
            catch (Exception e) {
                Log.exception("GetPluginStatusHook", e);
            }
            Engine.getAgent().sendObjectResponse(msg, status);
            return true;
        }
    }
    
    class GetObjectStatusHook implements Hook
    {
        int lastLoginCount;
        
        GetObjectStatusHook() {
            this.lastLoginCount = 0;
        }
        
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final OID oid = ((SubjectMessage)msg).getSubject();
            final ObjectManagerClient.ObjectStatus objectStatus = new ObjectManagerClient.ObjectStatus();
            objectStatus.oid = oid;
            final MasterObject masterObject = (MasterObject)EntityManager.getEntityByNamespace(oid, Namespace.OBJECT_MANAGER);
            if (masterObject == null) {
                final String name = Engine.getDatabase().getObjectName(oid, Namespace.OBJECT_MANAGER);
                objectStatus.name = name;
                Engine.getAgent().sendObjectResponse(msg, objectStatus);
                return true;
            }
            if (masterObject.isDeleted()) {
                Engine.getAgent().sendObjectResponse(msg, objectStatus);
                return true;
            }
            objectStatus.name = masterObject.getName();
            objectStatus.type = masterObject.getType();
            objectStatus.persistent = masterObject.getPersistenceFlag();
            objectStatus.namespaces = new ArrayList<Namespace>(masterObject.getSubObjectNamespaces());
            objectStatus.loadedNamespaces = new ArrayList<Namespace>(objectStatus.namespaces.size());
            final int loadedNS = masterObject.getLoadedNamespaces();
            for (int nsBit = 2, nsInt = 1; nsBit != 0; nsBit <<= 1, ++nsInt) {
                if ((loadedNS & nsBit) != 0x0) {
                    final Namespace namespace = Namespace.getNamespaceFromInt(nsInt);
                    objectStatus.loadedNamespaces.add(namespace);
                }
            }
            Engine.getAgent().sendObjectResponse(msg, objectStatus);
            return true;
        }
    }
    
    public static class MasterObject extends Entity
    {
        private transient int loadedNamespaces;
        private transient OID instanceOid;
        private static final long serialVersionUID = 1L;
        
        public MasterObject() {
            this.loadedNamespaces = 0;
        }
        
        public MasterObject(final String name) {
            super(name);
            this.loadedNamespaces = 0;
        }
        
        public OID getInstanceOid() {
            return this.instanceOid;
        }
        
        public void setInstanceOid(final OID instanceOid2) {
            this.instanceOid = instanceOid2;
        }
        
        public int getLoadedNamespaces() {
            return this.loadedNamespaces;
        }
        
        public void setLoadedNamespaces(final int namespaceBits) {
            this.loadedNamespaces = namespaceBits;
        }
        
        public void addLoadedNamespace(final Namespace namespace) {
            this.loadedNamespaces |= 1 << namespace.getNumber();
        }
        
        public void removeLoadedNamespace(final Namespace namespace) {
            this.loadedNamespaces &= ~(1 << namespace.getNumber());
        }
        
        public boolean isNamespaceLoaded(final Namespace namespace) {
            return (this.loadedNamespaces & 1 << namespace.getNumber()) != 0x0;
        }
        
        public boolean loadComplete() {
            return this.getSubObjectNamespacesInt() == this.loadedNamespaces;
        }
    }
    
    private static class InstanceState
    {
        public MasterObject instance;
        public int status;
        public Set<MasterObject> entities;
        
        public InstanceState(final MasterObject instance) {
            this.entities = new HashSet<MasterObject>();
            this.instance = instance;
            this.status = 0;
        }
        
        @Override
        public String toString() {
            return "instanceOid=" + this.instance.getOid() + " status=" + statusToString(this.status) + " entityCount=" + this.entities.size();
        }
        
        public static String statusToString(final int status) {
            if (status == 0) {
                return "OK";
            }
            if (status == 1) {
                return "LOADING";
            }
            if (status == 2) {
                return "UNLOADING";
            }
            if (status == 3) {
                return "DELETING";
            }
            return "" + status + " (unknown)";
        }
    }
}
