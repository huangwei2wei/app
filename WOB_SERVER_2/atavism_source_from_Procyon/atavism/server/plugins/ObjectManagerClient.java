// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.server.messages.OIDNamespaceMessage;
import atavism.msgsys.SubjectMessage;
import java.util.Map;
import atavism.server.objects.ObjectType;
import atavism.msgsys.GenericMessage;
import java.util.List;
import atavism.server.engine.BasicWorldNode;
import atavism.server.objects.Entity;
import atavism.msgsys.NoRecipientsException;
import java.util.Iterator;
import atavism.server.util.Log;
import java.util.Collection;
import atavism.server.objects.Light;
import atavism.server.objects.LightData;
import java.io.Serializable;
import atavism.server.engine.Namespace;
import atavism.server.math.Point;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.server.objects.Template;
import atavism.msgsys.MessageType;

public class ObjectManagerClient
{
    public static final MessageType MSG_TYPE_SET_PERSISTENCE;
    public static final MessageType MSG_TYPE_SET_SUBPERSISTENCE;
    public static final MessageType MSG_TYPE_MODIFY_NAMESPACE;
    public static final MessageType MSG_TYPE_LOAD_OBJECT;
    public static final MessageType MSG_TYPE_LOAD_SUBOBJECT;
    public static final MessageType MSG_TYPE_UNLOAD_OBJECT;
    public static final MessageType MSG_TYPE_UNLOAD_SUBOBJECT;
    public static final MessageType MSG_TYPE_DELETE_OBJECT;
    public static final MessageType MSG_TYPE_DELETE_SUBOBJECT;
    public static final MessageType MSG_TYPE_LOAD_OBJECT_DATA;
    public static final MessageType MSG_TYPE_SAVE_OBJECT_DATA;
    public static final MessageType MSG_TYPE_SAVE_OBJECT;
    public static final MessageType MSG_TYPE_SAVE_SUBOBJECT;
    public static final MessageType MSG_TYPE_GENERATE_OBJECT;
    public static final MessageType MSG_TYPE_GENERATE_SUB_OBJECT;
    public static final MessageType MSG_TYPE_SUB_OBJECT_DEPS_READY;
    public static final MessageType MSG_TYPE_REGISTER_TEMPLATE;
    public static final MessageType MSG_TYPE_GET_TEMPLATE;
    public static final MessageType MSG_TYPE_GET_TEMPLATE_NAMES;
    public static final MessageType MSG_TYPE_FIX_WNODE_REQ;
    public static final MessageType MSG_TYPE_GET_NAMED_OBJECT;
    public static final MessageType MSG_TYPE_GET_MATCHING_OBJECTS;
    public static final MessageType MSG_TYPE_GET_OBJECT_STATUS;
    public static final String BASE_TEMPLATE = "BaseTemplate";
    public static final int BASE_TEMPLATE_ID = -1;
    public static final String TEMPL_PERSISTENT = ":persistent";
    public static final String TEMPL_INSTANCE_RESTORE_STACK = "instanceStack";
    public static final String TEMPL_CURRENT_INSTANCE_NAME = "currentInstanceName";
    
    public static OID generateObject(final int templateID, final String templateType, final Template overrideTemplate) {
        final Message msg = new GenerateObjectMessage(templateID, templateType, overrideTemplate);
        return Engine.getAgent().sendRPCReturnOID(msg);
    }
    
    public static OID generateObject(final int templateID, final String templateType, final Point loc) {
        final Template override = new Template();
        if (loc != null) {
            override.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, loc);
        }
        final Message msg = new GenerateObjectMessage(templateID, templateType, override);
        return Engine.getAgent().sendRPCReturnOID(msg);
    }
    
    public static OID generateObject(final int templateID, final String templateType, final OID instanceOid, final Point loc) {
        final Template override = new Template();
        override.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, loc);
        override.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instanceOid);
        final Message msg = new GenerateObjectMessage(templateID, templateType, override);
        return Engine.getAgent().sendRPCReturnOID(msg);
    }
    
    public static OID generateLight(final OID instanceOid, final LightData lightData) {
        final Template template = new Template(lightData.getName());
        template.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_OBJECT_TYPE, WorldManagerClient.TEMPL_OBJECT_TYPE_LIGHT);
        template.put(Namespace.WORLD_MANAGER, Light.LightDataPropertyKey, lightData);
        template.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_LOC, lightData.getInitLoc());
        template.put(Namespace.WORLD_MANAGER, WorldManagerClient.TEMPL_INSTANCE, instanceOid);
        return generateObject(0, "BaseTemplate", template);
    }
    
    public static OID loadObject(final OID oid) {
        return loadSubObject(oid, null);
    }
    
    public static OID loadObject(final String key) {
        final LoadObjectMessage msg = new LoadObjectMessage(key);
        final OID oid = Engine.getAgent().sendRPCReturnOID(msg);
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient.loadObject: key=" + key + ", oid=" + oid);
        }
        return oid;
    }
    
    public static Boolean unloadObject(final OID oid) {
        return unloadSubObject(oid, null);
    }
    
    public static Boolean addSubObjectNamespace(final OID oid, final Collection<Namespace> namespaces) {
        final ModifyNamespaceMessage message = new ModifyNamespaceMessage(oid, namespaces);
        message.setCommand("add");
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    public static OID loadSubObject(final OID oid, final Collection<Namespace> namespaces) {
        final LoadObjectMessage msg = new LoadObjectMessage(oid, namespaces);
        final OID respOid = Engine.getAgent().sendRPCReturnOID(msg);
        if (Log.loggingDebug) {
            String nsString = "null";
            if (namespaces != null) {
                nsString = "";
                for (final Namespace ns : namespaces) {
                    nsString += ns;
                }
            }
            Log.debug("ObjectManagerClient.loadSubObject: oid=" + oid + " ns=" + nsString + ", received response oid " + respOid);
        }
        return respOid;
    }
    
    public static Boolean unloadSubObject(final OID oid, final Collection<Namespace> namespaces) {
        Boolean rc = false;
        try {
            final UnloadObjectMessage msg = new UnloadObjectMessage(oid, namespaces);
            rc = Engine.getAgent().sendRPCReturnBoolean(msg);
        }
        catch (NoRecipientsException nre) {
            Log.exception("ObjectManagerClient.unloadSubObject(): ", nre);
        }
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient.unloadSubObject: oid=" + oid + ", received response " + rc);
        }
        return rc;
    }
    
    public static Boolean deleteObject(final OID oid) {
        final DeleteObjectMessage msg = new DeleteObjectMessage(oid);
        final Boolean rc = Engine.getAgent().sendRPCReturnBoolean(msg);
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient.deleteObject: oid=" + oid + ", received response " + rc);
        }
        return rc;
    }
    
    public static boolean saveObject(final OID oid, final String persistenceKey) {
        final SaveObjectMessage msg = new SaveObjectMessage(oid, persistenceKey);
        return Engine.getAgent().sendRPCReturnBoolean(msg);
    }
    
    public static boolean saveObject(final OID oid) {
        final SaveObjectMessage msg = new SaveObjectMessage(oid);
        return Engine.getAgent().sendRPCReturnBoolean(msg);
    }
    
    public static boolean saveObjectData(final String persistenceKey, final Entity entity, final Namespace namespace) {
        entity.lock();
        byte[] entityData;
        try {
            entityData = entity.toBytes();
        }
        finally {
            entity.unlock();
        }
        final SaveObjectDataMessage msg = new SaveObjectDataMessage(entity.getOid(), persistenceKey, entityData, namespace);
        return Engine.getAgent().sendRPCReturnBoolean(msg);
    }
    
    public static Entity loadObjectData(final String persistenceKey) {
        final LoadObjectDataMessage msg = new LoadObjectDataMessage(persistenceKey);
        return (Entity)Engine.getAgent().sendRPCReturnObject(msg);
    }
    
    public static Entity loadObjectData(final OID oid, final Namespace namespace) {
        final LoadObjectDataMessage msg = new LoadObjectDataMessage(oid, namespace);
        return (Entity)Engine.getAgent().sendRPCReturnObject(msg);
    }
    
    public static boolean registerTemplate(final Template template) {
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient: registering template: " + template);
        }
        final Message msg = new RegisterTemplateMessage(template);
        final Boolean rv = Engine.getAgent().sendRPCReturnBoolean(msg);
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient: registered template: " + template);
        }
        return rv;
    }
    
    public static Template getTemplate(final int templateID, final String templateType) {
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient: get template: " + templateID + ":" + templateType);
        }
        final Message msg = new GetTemplateMessage(templateID, templateType);
        final Template template = (Template)Engine.getAgent().sendRPCReturnObject(msg);
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient: got template: " + template);
        }
        return template;
    }
    
    public static void setPersistenceFlag(final OID oid, final boolean flag) {
        final SetPersistenceMessage msg = new SetPersistenceMessage(oid, Boolean.valueOf(flag));
        Engine.getAgent().sendRPC(msg);
    }
    
    public static boolean fixWorldNode(final OID oid, final BasicWorldNode worldNode) {
        final FixWorldNodeMessage message = new FixWorldNodeMessage(oid, worldNode);
        return Engine.getAgent().sendRPCReturnBoolean(message);
    }
    
    public static List<String> getTemplateNames(final String templateType) {
        final GenericMessage message = new GenericMessage();
        message.setMsgType(ObjectManagerClient.MSG_TYPE_GET_TEMPLATE_NAMES);
        message.setProperty("templateType", templateType);
        final List<String> templateNames = (List<String>)Engine.getAgent().sendRPCReturnObject(message);
        if (Log.loggingDebug) {
            Log.debug("ObjectManagerClient: got " + templateNames.size() + " template names");
        }
        return templateNames;
    }
    
    public static OID getNamedObject(final OID instanceOid, final String name, final ObjectType objectType) {
        final GetNamedObjectMessage message = new GetNamedObjectMessage(instanceOid, name, objectType);
        return Engine.getAgent().sendRPCReturnOID(message);
    }
    
    public static List<OID> getMatchingObjects(final OID instanceOid, final String name, final ObjectType objectType, final Map<Namespace, Map<String, Serializable>> filters) {
        final GetMatchingObjectsMessage message = new GetMatchingObjectsMessage(instanceOid, name, objectType, filters);
        final Object rv = Engine.getAgent().sendRPCReturnObject(message);
        if (rv == null) {
            return null;
        }
        return (List<OID>)rv;
    }
    
    public static ObjectStatus getObjectStatus(final OID oid) {
        final SubjectMessage message = new SubjectMessage(ObjectManagerClient.MSG_TYPE_GET_OBJECT_STATUS, oid);
        return (ObjectStatus)Engine.getAgent().sendRPCReturnObject(message);
    }
    
    static {
        MSG_TYPE_SET_PERSISTENCE = MessageType.intern("ao.SET_PERSISTENCE");
        MSG_TYPE_SET_SUBPERSISTENCE = MessageType.intern("ao.SET_SUBPERSISTENCE");
        MSG_TYPE_MODIFY_NAMESPACE = MessageType.intern("ao.MODIFY_NAMESPACE");
        MSG_TYPE_LOAD_OBJECT = MessageType.intern("ao.LOAD_OBJECT");
        MSG_TYPE_LOAD_SUBOBJECT = MessageType.intern("ao.LOAD_SUBOBJECT");
        MSG_TYPE_UNLOAD_OBJECT = MessageType.intern("ao.UNLOAD_OBJECT");
        MSG_TYPE_UNLOAD_SUBOBJECT = MessageType.intern("ao.UNLOAD_SUBOBJECT");
        MSG_TYPE_DELETE_OBJECT = MessageType.intern("ao.DELETE_OBJECT");
        MSG_TYPE_DELETE_SUBOBJECT = MessageType.intern("ao.DELETE_SUBOBJECT");
        MSG_TYPE_LOAD_OBJECT_DATA = MessageType.intern("ao.LOAD_OBJECT_DATA");
        MSG_TYPE_SAVE_OBJECT_DATA = MessageType.intern("ao.SAVE_OBJECT_DATA");
        MSG_TYPE_SAVE_OBJECT = MessageType.intern("ao.SAVE_OBJECT");
        MSG_TYPE_SAVE_SUBOBJECT = MessageType.intern("ao.SAVE_SUBOBJECT");
        MSG_TYPE_GENERATE_OBJECT = MessageType.intern("ao.GENERATE_OBJECT");
        MSG_TYPE_GENERATE_SUB_OBJECT = MessageType.intern("ao.GENERATE_SUB_OBJECT");
        MSG_TYPE_SUB_OBJECT_DEPS_READY = MessageType.intern("ao.SUB_OBJECT_DEPS_READY");
        MSG_TYPE_REGISTER_TEMPLATE = MessageType.intern("ao.REGISTER_TEMPLATE");
        MSG_TYPE_GET_TEMPLATE = MessageType.intern("ao.GET_TEMPLATE");
        MSG_TYPE_GET_TEMPLATE_NAMES = MessageType.intern("ao.GET_TEMPLATE_NAMES");
        MSG_TYPE_FIX_WNODE_REQ = MessageType.intern("ao.FIX_WNODE_REQ");
        MSG_TYPE_GET_NAMED_OBJECT = MessageType.intern("ao.GET_NAMED_OBJECT");
        MSG_TYPE_GET_MATCHING_OBJECTS = MessageType.intern("ao.GET_MATCHING_OBJECTS");
        MSG_TYPE_GET_OBJECT_STATUS = MessageType.intern("ao.GET_OBJECT_STATUS");
    }
    
    public static class ModifyNamespaceMessage extends Message
    {
        private String command;
        private OID oid;
        private Collection<Namespace> namespaces;
        private static final long serialVersionUID = 1L;
        
        public ModifyNamespaceMessage() {
            super(ObjectManagerClient.MSG_TYPE_MODIFY_NAMESPACE);
        }
        
        public ModifyNamespaceMessage(final OID oid) {
            super(ObjectManagerClient.MSG_TYPE_MODIFY_NAMESPACE);
            this.setOid(oid);
        }
        
        public ModifyNamespaceMessage(final OID oid, final Collection<Namespace> namespaces) {
            super(ObjectManagerClient.MSG_TYPE_MODIFY_NAMESPACE);
            this.setOid(oid);
            this.setNamespaces(namespaces);
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public void setOid(final OID oid) {
            this.oid = oid;
        }
        
        public Collection<Namespace> getNamespaces() {
            return this.namespaces;
        }
        
        public void setNamespaces(final Collection<Namespace> namespaces) {
            this.namespaces = namespaces;
        }
        
        public void setCommand(final String cmd) {
            this.command = cmd;
        }
        
        public String getCommand() {
            return this.command;
        }
    }
    
    public static class LoadObjectMessage extends Message
    {
        private String key;
        private OID oid;
        private Collection<Namespace> namespaces;
        private static final long serialVersionUID = 1L;
        
        public LoadObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT);
        }
        
        public LoadObjectMessage(final OID oid) {
            super(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT);
            this.setOid(oid);
        }
        
        public LoadObjectMessage(final OID oid, final Collection<Namespace> namespaces) {
            super(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT);
            this.setOid(oid);
            this.setNamespaces(namespaces);
        }
        
        public LoadObjectMessage(final String key) {
            super(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT);
            this.setKey(key);
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public void setOid(final OID oid) {
            this.oid = oid;
        }
        
        public Collection<Namespace> getNamespaces() {
            return this.namespaces;
        }
        
        public void setNamespaces(final Collection<Namespace> namespaces) {
            this.namespaces = namespaces;
        }
        
        public void setKey(final String key) {
            this.key = key;
        }
        
        public String getKey() {
            return this.key;
        }
    }
    
    public static class UnloadObjectMessage extends Message
    {
        private OID oid;
        private Collection<Namespace> namespaces;
        private static final long serialVersionUID = 1L;
        
        public UnloadObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_UNLOAD_OBJECT);
        }
        
        public UnloadObjectMessage(final OID oid) {
            super(ObjectManagerClient.MSG_TYPE_UNLOAD_OBJECT);
            this.setOid(oid);
        }
        
        public UnloadObjectMessage(final OID oid, final Collection<Namespace> namespaces) {
            super(ObjectManagerClient.MSG_TYPE_UNLOAD_OBJECT);
            this.setOid(oid);
            this.setNamespaces(namespaces);
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public void setOid(final OID oid) {
            this.oid = oid;
        }
        
        public Collection<Namespace> getNamespaces() {
            return this.namespaces;
        }
        
        public void setNamespaces(final Collection<Namespace> namespaces) {
            this.namespaces = namespaces;
        }
    }
    
    public static class DeleteObjectMessage extends Message
    {
        private OID oid;
        private static final long serialVersionUID = 1L;
        
        public DeleteObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_DELETE_OBJECT);
        }
        
        public DeleteObjectMessage(final OID oid) {
            super(ObjectManagerClient.MSG_TYPE_DELETE_OBJECT);
            this.setOid(oid);
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public void setOid(final OID oid) {
            this.oid = oid;
        }
    }
    
    public static class SaveObjectMessage extends Message
    {
        private OID oid;
        private String key;
        private static final long serialVersionUID = 1L;
        
        public SaveObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT);
        }
        
        public SaveObjectMessage(final OID oid) {
            this.setMsgType(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT);
            this.setOid(oid);
        }
        
        public SaveObjectMessage(final OID oid, final String key) {
            this.setMsgType(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT);
            this.setKey(key);
            this.setOid(oid);
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public void setOid(final OID oid) {
            this.oid = oid;
        }
        
        public void setKey(final String key) {
            this.key = key;
        }
        
        public String getKey() {
            return this.key;
        }
    }
    
    public static class SaveObjectDataMessage extends SubjectMessage
    {
        String key;
        Object dataBytes;
        Namespace namespace;
        private static final long serialVersionUID = 1L;
        
        public SaveObjectDataMessage() {
            super(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT_DATA);
        }
        
        SaveObjectDataMessage(final OID oid, final String persistenceKey, final byte[] data, final Namespace namespace) {
            super(ObjectManagerClient.MSG_TYPE_SAVE_OBJECT_DATA, oid);
            this.setDataBytes(data);
            this.setKey(persistenceKey);
            this.setNamespace(namespace);
        }
        
        public void setDataBytes(final byte[] dataBytes) {
            this.dataBytes = dataBytes;
        }
        
        public byte[] getDataBytes() {
            return (byte[])this.dataBytes;
        }
        
        public void setKey(final String key) {
            this.key = key;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public Namespace getNamespace() {
            return this.namespace;
        }
        
        public void setNamespace(final Namespace namespace) {
            this.namespace = namespace;
        }
    }
    
    public static class LoadObjectDataMessage extends SubjectMessage
    {
        private String key;
        private Namespace namespace;
        private static final long serialVersionUID = 1L;
        
        public LoadObjectDataMessage() {
            super(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT_DATA);
        }
        
        public LoadObjectDataMessage(final OID oid, final Namespace namespace) {
            super(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT_DATA, oid);
            this.setNamespace(namespace);
        }
        
        public LoadObjectDataMessage(final String persistenceKey) {
            super(ObjectManagerClient.MSG_TYPE_LOAD_OBJECT_DATA);
            this.setKey(persistenceKey);
        }
        
        public void setKey(final String key) {
            this.key = key;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public Namespace getNamespace() {
            return this.namespace;
        }
        
        public void setNamespace(final Namespace namespace) {
            this.namespace = namespace;
        }
    }
    
    public static class GenerateObjectMessage extends Message
    {
        private int templateID;
        private String templateType;
        Template overrideTemplate;
        private static final long serialVersionUID = 1L;
        
        public GenerateObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_GENERATE_OBJECT);
            this.templateID = 0;
            this.templateType = null;
            this.overrideTemplate = null;
        }
        
        GenerateObjectMessage(final int templateID, final String templateType) {
            this.templateID = 0;
            this.templateType = null;
            this.overrideTemplate = null;
            this.setMsgType(ObjectManagerClient.MSG_TYPE_GENERATE_OBJECT);
            this.setTemplateID(templateID);
            this.setTemplateType(templateType);
        }
        
        GenerateObjectMessage(final int templateID, final String templateType, final Template overrideTemplate) {
            this(templateID, templateType);
            this.setOverrideTemplate(overrideTemplate);
        }
        
        public int getTemplateID() {
            return this.templateID;
        }
        
        public void setTemplateID(final int templateID) {
            this.templateID = templateID;
        }
        
        public String getTemplateType() {
            return this.templateType;
        }
        
        public void setTemplateType(final String templateType) {
            this.templateType = templateType;
        }
        
        public void setOverrideTemplate(final Template t) {
            this.overrideTemplate = t;
        }
        
        public Template getOverrideTemplate() {
            return this.overrideTemplate;
        }
    }
    
    public static class SubObjectDepsReadyMessage extends OIDNamespaceMessage
    {
        private static final long serialVersionUID = 1L;
        
        public SubObjectDepsReadyMessage() {
            super(ObjectManagerClient.MSG_TYPE_SUB_OBJECT_DEPS_READY);
        }
        
        public SubObjectDepsReadyMessage(final OID masterOid, final Namespace namespace) {
            super(ObjectManagerClient.MSG_TYPE_SUB_OBJECT_DEPS_READY, masterOid);
            this.setNamespace(namespace);
        }
    }
    
    public static class GenerateSubObjectMessage extends OIDNamespaceMessage
    {
        Template template;
        boolean persistent;
        private static final long serialVersionUID = 1L;
        
        public GenerateSubObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_GENERATE_SUB_OBJECT);
            this.template = null;
            this.persistent = false;
        }
        
        public GenerateSubObjectMessage(final OID oid, final Namespace namespace, final Template template) {
            super(ObjectManagerClient.MSG_TYPE_GENERATE_SUB_OBJECT, oid, namespace);
            this.template = null;
            this.persistent = false;
            this.setTemplate(template);
        }
        
        public void setTemplate(final Template t) {
            this.template = t;
        }
        
        public Template getTemplate() {
            return this.template;
        }
        
        public OID getMasterOid() {
            return this.getSubject();
        }
        
        public void setMasterOid(final OID masterOid) {
            this.setSubject(masterOid);
        }
        
        public boolean getPersistenceFlag() {
            return this.persistent;
        }
        
        public void setPersistenceFlag(final boolean flag) {
            this.persistent = flag;
        }
    }
    
    public static class LoadSubObjectMessage extends OIDNamespaceMessage
    {
        private static final long serialVersionUID = 1L;
        
        public LoadSubObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_LOAD_SUBOBJECT);
        }
        
        public LoadSubObjectMessage(final OID oid, final Namespace namespace) {
            super(ObjectManagerClient.MSG_TYPE_LOAD_SUBOBJECT, oid, namespace);
        }
        
        public OID getMasterOid() {
            return this.getSubject();
        }
        
        public void setMasterOid(final OID oid) {
            this.setSubject(oid);
        }
    }
    
    public static class UnloadSubObjectMessage extends OIDNamespaceMessage
    {
        private static final long serialVersionUID = 1L;
        
        public UnloadSubObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_UNLOAD_SUBOBJECT);
        }
        
        public UnloadSubObjectMessage(final OID oid, final Namespace namespace) {
            super(ObjectManagerClient.MSG_TYPE_UNLOAD_SUBOBJECT, oid, namespace);
        }
    }
    
    public static class DeleteSubObjectMessage extends OIDNamespaceMessage
    {
        private static final long serialVersionUID = 1L;
        
        public DeleteSubObjectMessage() {
            super(ObjectManagerClient.MSG_TYPE_DELETE_SUBOBJECT);
        }
        
        public DeleteSubObjectMessage(final OID oid, final Namespace namespace) {
            super(ObjectManagerClient.MSG_TYPE_DELETE_SUBOBJECT, oid, namespace);
        }
    }
    
    public static class RegisterTemplateMessage extends Message
    {
        private Template template;
        private static final long serialVersionUID = 1L;
        
        public RegisterTemplateMessage() {
            super(ObjectManagerClient.MSG_TYPE_REGISTER_TEMPLATE);
            this.template = null;
        }
        
        RegisterTemplateMessage(final Template template) {
            this.template = null;
            this.setMsgType(ObjectManagerClient.MSG_TYPE_REGISTER_TEMPLATE);
            this.setTemplate(template);
        }
        
        public Template getTemplate() {
            return this.template;
        }
        
        public void setTemplate(final Template template) {
            this.template = template;
        }
    }
    
    public static class GetTemplateMessage extends Message
    {
        private int templateID;
        private String templateType;
        private static final long serialVersionUID = 1L;
        
        public GetTemplateMessage() {
            super(ObjectManagerClient.MSG_TYPE_GET_TEMPLATE);
            this.templateID = 0;
            this.templateType = null;
        }
        
        GetTemplateMessage(final int templateID, final String templateType) {
            this.templateID = 0;
            this.templateType = null;
            this.setMsgType(ObjectManagerClient.MSG_TYPE_GET_TEMPLATE);
            this.setTemplateID(templateID);
            this.setTemplateType(templateType);
        }
        
        public int getTemplateID() {
            return this.templateID;
        }
        
        public void setTemplateID(final int templateID) {
            this.templateID = templateID;
        }
        
        public String getTemplateType() {
            return this.templateType;
        }
        
        public void setTemplateType(final String templateType) {
            this.templateType = templateType;
        }
    }
    
    public static class SetPersistenceMessage extends OIDNamespaceMessage
    {
        private Boolean persistVal;
        private static final long serialVersionUID = 1L;
        
        public SetPersistenceMessage() {
            super(ObjectManagerClient.MSG_TYPE_SET_PERSISTENCE);
        }
        
        public SetPersistenceMessage(final OID oid, final Boolean persistVal) {
            super(ObjectManagerClient.MSG_TYPE_SET_PERSISTENCE, oid);
            this.setPersistVal(persistVal);
        }
        
        public SetPersistenceMessage(final OID oid, final Namespace namespace, final Boolean persistVal) {
            super(ObjectManagerClient.MSG_TYPE_SET_PERSISTENCE, oid, namespace);
            this.setPersistVal(persistVal);
        }
        
        public Boolean getPersistVal() {
            return this.persistVal;
        }
        
        public void setPersistVal(final Boolean persistVal) {
            this.persistVal = persistVal;
        }
    }
    
    public static class SetSubPersistenceMessage extends SetPersistenceMessage
    {
        private static final long serialVersionUID = 1L;
        
        public SetSubPersistenceMessage() {
            this.setMsgType(ObjectManagerClient.MSG_TYPE_SET_SUBPERSISTENCE);
        }
        
        public SetSubPersistenceMessage(final OID oid, final Namespace namespace, final Boolean persistVal) {
            super(oid, namespace, persistVal);
            this.setMsgType(ObjectManagerClient.MSG_TYPE_SET_SUBPERSISTENCE);
        }
    }
    
    public static class FixWorldNodeMessage extends Message
    {
        private OID oid;
        private BasicWorldNode worldNode;
        private static final long serialVersionUID = 1L;
        
        public FixWorldNodeMessage() {
        }
        
        public FixWorldNodeMessage(final OID oid, final BasicWorldNode worldNode) {
            super(ObjectManagerClient.MSG_TYPE_FIX_WNODE_REQ);
            this.setOid(oid);
            this.setWorldNode(worldNode);
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public void setOid(final OID oid) {
            this.oid = oid;
        }
        
        public BasicWorldNode getWorldNode() {
            return this.worldNode;
        }
        
        public void setWorldNode(final BasicWorldNode worldNode) {
            this.worldNode = worldNode;
        }
    }
    
    public static class GetNamedObjectMessage extends Message
    {
        private OID instanceOid;
        private String name;
        private ObjectType objectType;
        private static final long serialVersionUID = 1L;
        
        public GetNamedObjectMessage() {
        }
        
        public GetNamedObjectMessage(final OID instanceOid, final String name, final ObjectType objectType) {
            super(ObjectManagerClient.MSG_TYPE_GET_NAMED_OBJECT);
            this.instanceOid = instanceOid;
            this.name = name;
            this.objectType = objectType;
        }
        
        public OID getInstanceOid() {
            return this.instanceOid;
        }
        
        public String getName() {
            return this.name;
        }
        
        public ObjectType getObjectType() {
            return this.objectType;
        }
    }
    
    public static class GetMatchingObjectsMessage extends Message
    {
        private OID instanceOid;
        private String name;
        private ObjectType objectType;
        private Map<Namespace, Map<String, Serializable>> filters;
        private static final long serialVersionUID = 1L;
        
        public GetMatchingObjectsMessage() {
        }
        
        public GetMatchingObjectsMessage(final OID instanceOid, final String name, final ObjectType objectType, final Map<Namespace, Map<String, Serializable>> filters) {
            super(ObjectManagerClient.MSG_TYPE_GET_MATCHING_OBJECTS);
            this.instanceOid = instanceOid;
            this.name = name;
            this.objectType = objectType;
            this.filters = filters;
        }
        
        public OID getInstanceOid() {
            return this.instanceOid;
        }
        
        public String getName() {
            return this.name;
        }
        
        public ObjectType getObjectType() {
            return this.objectType;
        }
        
        public Map<Namespace, Map<String, Serializable>> getFilters() {
            return this.filters;
        }
    }
    
    public static class ObjectStatus
    {
        public OID oid;
        public String name;
        public ObjectType type;
        public boolean persistent;
        public List<Namespace> namespaces;
        public List<Namespace> loadedNamespaces;
        private static final long serialVersionUID = 1L;
    }
}
