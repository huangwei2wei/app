// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import java.util.Collection;
import atavism.server.engine.SearchSelection;
import atavism.server.engine.SearchClause;
import atavism.server.engine.TerrainConfig;
import atavism.server.engine.Namespace;
import atavism.server.objects.InstanceTemplate;
import atavism.msgsys.SubjectMessage;
import atavism.agis.plugins.AgisMobClient;
import atavism.server.engine.WorldCollectionLoaderContext;
import java.io.Serializable;
import atavism.msgsys.Message;
import atavism.server.engine.PluginStatus;
import atavism.server.util.Log;
import atavism.server.engine.WorldLoaderOverride;
import java.io.File;
import atavism.server.util.FileUtil;
import atavism.server.objects.EntityManager;
import atavism.server.engine.OID;
import java.util.Iterator;
import atavism.server.objects.SpawnData;
import atavism.server.objects.Instance;
import atavism.server.engine.Hook;
import atavism.server.objects.Region;
import atavism.server.engine.Searchable;
import atavism.server.objects.EntitySearchable;
import atavism.server.engine.MatcherFactory;
import atavism.server.engine.SearchManager;
import atavism.server.engine.PropertyMatcher;
import atavism.server.objects.Entity;
import atavism.server.engine.PropertySearch;
import atavism.server.messages.PopulationFilter;
import atavism.server.objects.ObjectTypes;
import atavism.msgsys.MessageCallback;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.msgsys.MessageTypeFilter;
import atavism.server.engine.DefaultWorldLoaderOverride;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import atavism.server.objects.Template;
import java.util.Map;
import atavism.server.util.Logger;
import atavism.server.engine.EnginePlugin;

public class InstancePlugin extends EnginePlugin
{
    protected static final Logger log;
    private static Map<String, Class> loaderOverrideClasses;
    Map<String, Template> instanceTemplates;
    List<String> pendingUniqueNames;
    PopulationChangeCallback populationChangeCallback;
    
    public InstancePlugin() {
        super("Instance");
        this.instanceTemplates = new HashMap<String, Template>();
        this.pendingUniqueNames = new ArrayList<String>();
        this.populationChangeCallback = null;
        this.setPluginType("Instance");
        registerWorldLoaderOverrideClass("default", DefaultWorldLoaderOverride.class);
    }
    
    @Override
    public void onActivate() {
        this.setPluginAvailable(false);
        this.registerHooks();
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(InstanceClient.MSG_TYPE_REGISTER_INSTANCE_TEMPLATE);
        filter.addType(InstanceClient.MSG_TYPE_CREATE_INSTANCE);
        filter.addType(InstanceClient.MSG_TYPE_LOAD_INSTANCE);
        filter.addType(InstanceClient.MSG_TYPE_GET_INSTANCE_INFO);
        filter.addType(InstanceClient.MSG_TYPE_GET_MARKER);
        filter.addType(InstanceClient.MSG_TYPE_GET_REGION);
        filter.addType(InstanceClient.MSG_TYPE_GET_ENTITY_OIDS);
        filter.addType(WorldManagerClient.MSG_TYPE_SPAWNED);
        filter.addType(WorldManagerClient.MSG_TYPE_DESPAWNED);
        Engine.getAgent().createSubscription(filter, this, 8);
        final MessageTypeFilter noResponseFilter = new MessageTypeFilter();
        noResponseFilter.addType(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT);
        Engine.getAgent().createSubscription(noResponseFilter, this);
        final PopulationFilter populationFilter = new PopulationFilter(ObjectTypes.player);
        Engine.getAgent().createSubscription(populationFilter, this);
        this.registerPluginNamespace(InstanceClient.NAMESPACE, new InstanceGenerateSubObjectHook());
        this.registerUnloadHook(InstanceClient.NAMESPACE, new InstanceUnloadHook());
        this.registerDeleteHook(InstanceClient.NAMESPACE, new InstanceDeleteHook());
        SearchManager.registerMatcher(PropertySearch.class, Entity.class, new PropertyMatcher.Factory());
        SearchManager.registerSearchable(ObjectTypes.instance, new EntitySearchable(ObjectTypes.instance));
        SearchManager.registerMatcher(Region.Search.class, Region.class, new PropertyMatcher.Factory());
        SearchManager.registerSearchable(Region.OBJECT_TYPE, new RegionSearch());
    }
    
    protected void registerHooks() {
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_REGISTER_INSTANCE_TEMPLATE, new RegisterInstanceTemplateHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_CREATE_INSTANCE, new CreateInstanceHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_LOAD_INSTANCE, new LoadInstanceHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_GET_INSTANCE_INFO, new GetInstanceInfoHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_GET_REGION, new GetRegionHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_SPAWNED, new PopulationHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_DESPAWNED, new PopulationHook());
        this.getHookManager().addHook(InstanceClient.MSG_TYPE_GET_ENTITY_OIDS, new GetMatchingEntityOidsHook());
        this.getHookManager().addHook(WorldManagerClient.MSG_TYPE_UPDATE_OBJECT, new UpdateObjectHook());
    }
    
    protected void sendSpawnGenerators(final Instance instance) {
        final List<SpawnData> spawnDataList = instance.getSpawnData();
        for (final SpawnData spawnData : spawnDataList) {
            MobManagerClient.createSpawnGenerator(spawnData);
        }
    }
    
    public static void registerWorldLoaderOverrideClass(final String name, final Class loaderOverrideClass) {
        synchronized (InstancePlugin.loaderOverrideClasses) {
            InstancePlugin.loaderOverrideClasses.put(name, loaderOverrideClass);
        }
    }
    
    public static Class getWorldLoaderOverrideClass(final String name) {
        return InstancePlugin.loaderOverrideClasses.get(name);
    }
    
    private OID waitForUniqueName(final String instanceName) {
        OID instanceOid = null;
        synchronized (this.pendingUniqueNames) {
            while (this.pendingUniqueNames.contains(instanceName)) {
                try {
                    this.pendingUniqueNames.wait();
                }
                catch (InterruptedException ex) {}
            }
            final Instance instance = this.getInstance(instanceName);
            if (instance == null) {
                instanceOid = this.getPersistentInstanceOid(instanceName);
            }
            else {
                instanceOid = instance.getOid();
            }
            if (instanceOid == null) {
                this.pendingUniqueNames.add(instanceName);
            }
            return instanceOid;
        }
    }
    
    private void releaseUniqueName(final String instanceName) {
        synchronized (this.pendingUniqueNames) {
            this.pendingUniqueNames.remove(instanceName);
            this.pendingUniqueNames.notifyAll();
        }
    }
    
    private Instance getInstance(final String name) {
        final Entity[] arr$;
        final Entity[] entities = arr$ = EntityManager.getAllEntitiesByNamespace(InstanceClient.NAMESPACE);
        for (final Entity entity : arr$) {
            if (entity instanceof Instance && name.equals(entity.getName())) {
                return (Instance)entity;
            }
        }
        return null;
    }
    
    private List<OID> getMatchingEntityOids(final String name) {
        final Entity[] entities = EntityManager.getAllEntitiesByNamespace(InstanceClient.NAMESPACE);
        final List<OID> rv = new ArrayList<OID>();
        for (final Entity entity : entities) {
            if (name.equals(entity.getName())) {
                rv.add(entity.getOid());
            }
        }
        return rv;
    }
    
    public OID getPersistentInstanceOid(final String name) {
        return Engine.getDatabase().getOidByName(name, InstanceClient.NAMESPACE);
    }
    
    private boolean fileExist(final String fileName) {
        final File file = new File(FileUtil.expandFileName(fileName));
        return file.exists() && file.canRead();
    }
    
    private WorldLoaderOverride createLoaderOverride(final String loaderName) {
        Class loaderClass = null;
        try {
            loaderClass = InstancePlugin.loaderOverrideClasses.get(loaderName);
            if (loaderClass == null) {
                Log.error("World loader override class not registered, name=" + loaderName);
            }
            return loaderClass.newInstance();
        }
        catch (Exception ex) {
            Log.exception("failed instantiating world loader, name=" + loaderName + " class=" + loaderClass.getName(), ex);
            return null;
        }
    }
    
    protected final PluginStatus selectWorldManagerPlugin() {
        final List<PluginStatus> plugins = Engine.getDatabase().getPluginStatus("WorldManager");
        final Iterator<PluginStatus> iterator = plugins.iterator();
        while (iterator.hasNext()) {
            final PluginStatus plugin = iterator.next();
            if (plugin.run_id != Engine.getAgent().getDomainStartTime()) {
                iterator.remove();
            }
        }
        if (plugins.size() == 0) {
            return null;
        }
        return this.selectBestWorldManager(plugins);
    }
    
    protected PluginStatus selectBestWorldManager(final List<PluginStatus> plugins) {
        PluginStatus selection = null;
        int selectionEntityCount = Integer.MAX_VALUE;
        for (final PluginStatus plugin : plugins) {
            final Map<String, String> status = Engine.makeMapOfString(plugin.status);
            int entityCount;
            try {
                entityCount = Integer.parseInt(status.get("entities"));
            }
            catch (Exception e) {
                Log.exception("selectBestWorldManager: wmgr " + plugin.plugin_name + " invalid entity count: " + status.get("entities"), e);
                continue;
            }
            if (entityCount < selectionEntityCount) {
                selection = plugin;
                selectionEntityCount = entityCount;
            }
        }
        return selection;
    }
    
    public void registerPopulationChangeCallback(final PopulationChangeCallback populationChangeCallback) {
        this.populationChangeCallback = populationChangeCallback;
    }
    
    static {
        log = new Logger("InstancePlugin");
        InstancePlugin.loaderOverrideClasses = new HashMap<String, Class>();
    }
    
    class RegisterInstanceTemplateHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InstanceClient.RegisterInstanceTemplateMessage message = (InstanceClient.RegisterInstanceTemplateMessage)msg;
            final Template template = message.getTemplate();
            if (Log.loggingDebug) {
                Log.debug("RegisterInstanceTemplateHook: template=" + template);
            }
            if (template == null) {
                Engine.getAgent().sendBooleanResponse(message, Boolean.FALSE);
                return true;
            }
            if (template.getName() == null) {
                Log.error("RegisterInstanceTemplateHook: missing template name");
                Engine.getAgent().sendBooleanResponse(message, Boolean.FALSE);
                return true;
            }
            InstancePlugin.this.instanceTemplates.put(template.getName(), template);
            if (Log.loggingDebug) {
                Log.debug("RegisterInstanceTemplateHook: added template name=" + template.getName());
            }
            Engine.getAgent().sendBooleanResponse(message, Boolean.TRUE);
            return true;
        }
    }
    
    class CreateInstanceHook implements Hook
    {
        String instanceName;
        Boolean uniqueNameFlag;
        
        CreateInstanceHook() {
            this.uniqueNameFlag = false;
        }
        
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InstanceClient.CreateInstanceMessage message = (InstanceClient.CreateInstanceMessage)msg;
            return new CreateInstanceHook().handleMessage(message);
        }
        
        private boolean handleMessage(final InstanceClient.CreateInstanceMessage message) {
            try {
                final OID instanceOid = this.createInstance(message.getTemplateName(), message.getOverrideTemplate());
                Engine.getAgent().sendOIDResponse(message, instanceOid);
            }
            finally {
                if (this.uniqueNameFlag) {
                    InstancePlugin.this.releaseUniqueName(this.instanceName);
                }
            }
            return true;
        }
        
        private OID createInstance(final String templateName, final Template overrideTemplate) {
            if (Log.loggingDebug) {
                Log.debug("CreateInstanceHook: templateName=" + templateName + " override=" + overrideTemplate);
            }
            if (templateName == null) {
                return null;
            }
            final Template template = InstancePlugin.this.instanceTemplates.get(templateName);
            if (template == null) {
                Log.error("CreateInstanceHook: unknown template name=" + templateName);
                return null;
            }
            Template mergedTemplate;
            try {
                mergedTemplate = (Template)template.clone();
            }
            catch (CloneNotSupportedException ex) {
                return null;
            }
            mergedTemplate = mergedTemplate.merge(overrideTemplate);
            String worldManagerPlugin = (String)mergedTemplate.get(WorldManagerClient.INSTANCE_NAMESPACE, ":wmName");
            if (worldManagerPlugin == null || worldManagerPlugin.equals("")) {
                final PluginStatus plugin = InstancePlugin.this.selectWorldManagerPlugin();
                if (plugin == null) {
                    Log.error("CreateInstanceHook: no world manager for instance, templateName=" + templateName);
                    return null;
                }
                if (Log.loggingDebug) {
                    Log.debug("CreateInstanceHook: assigned world manager " + plugin.plugin_name + " host=" + plugin.host_name);
                }
                mergedTemplate.put(WorldManagerClient.INSTANCE_NAMESPACE, ":wmName", plugin.plugin_name);
                worldManagerPlugin = plugin.plugin_name;
            }
            mergedTemplate.put(InstanceClient.NAMESPACE, "templateName", templateName);
            this.uniqueNameFlag = (Boolean)mergedTemplate.get(InstanceClient.NAMESPACE, "uniqueName");
            if (this.uniqueNameFlag == null) {
                this.uniqueNameFlag = false;
            }
            this.instanceName = (String)mergedTemplate.get(InstanceClient.NAMESPACE, "name");
            if (this.uniqueNameFlag) {
                final OID instanceOid = InstancePlugin.this.waitForUniqueName(this.instanceName);
                if (instanceOid != null) {
                    Log.debug("CreateInstanceHook: instance name already exists, name=" + this.instanceName + " instanceOid=" + instanceOid);
                    return null;
                }
            }
            String initScript = (String)mergedTemplate.get(InstanceClient.NAMESPACE, "initScriptFileName");
            if (initScript != null) {
                initScript = FileUtil.expandFileName(initScript);
                mergedTemplate.put(InstanceClient.NAMESPACE, "initScriptFileName", initScript);
            }
            if (initScript != null && !InstancePlugin.this.fileExist(initScript)) {
                Log.error("CreateInstanceHook: init file not found fileName=" + initScript);
                return null;
            }
            String loadScript = (String)mergedTemplate.get(InstanceClient.NAMESPACE, "loadScriptFileName");
            if (loadScript != null) {
                loadScript = FileUtil.expandFileName(loadScript);
                mergedTemplate.put(InstanceClient.NAMESPACE, "loadScriptFileName", loadScript);
            }
            if (loadScript != null && !InstancePlugin.this.fileExist(loadScript)) {
                Log.error("CreateInstanceHook: load script file not found fileName=" + loadScript);
                return null;
            }
            final OID instanceOid2 = ObjectManagerClient.generateObject(-1, "BaseTemplate", mergedTemplate);
            if (instanceOid2 == null) {
                Log.error("CreateInstanceHook: generateObject failed name=" + mergedTemplate.get(InstanceClient.NAMESPACE, "name") + " templateName=" + templateName + " wmName=" + worldManagerPlugin);
                return null;
            }
            Log.info("InstancePlugin: CREATE_INSTANCE instanceOid=" + instanceOid2 + " name=[" + this.instanceName + "]" + " templateName=[" + templateName + "]" + " wmName=" + worldManagerPlugin);
            final Instance instance = (Instance)EntityManager.getEntityByNamespace(instanceOid2, InstanceClient.NAMESPACE);
            String loaderName = instance.getWorldLoaderOverrideName();
            if (loaderName == null) {
                loaderName = "default";
            }
            instance.setWorldLoaderOverride(InstancePlugin.this.createLoaderOverride(loaderName));
            if (!instance.loadWorldData()) {
                Log.error("CreateInstanceHook: load world file failed fileName=" + instance.getWorldFileName());
                return null;
            }
            final WorldCollectionLoaderContext loaderContext = new WorldCollectionLoaderContext();
            final String worldCollectionFiles = (String)mergedTemplate.get(InstanceClient.NAMESPACE, "worldCollectionFiles");
            if (worldCollectionFiles != null) {
                for (final String worldCollectionFile : worldCollectionFiles.split(",")) {
                    loaderContext.addWorldCollectionFile(worldCollectionFile);
                }
            }
            final String worldCollectionDatabaseKeys = (String)mergedTemplate.get(InstanceClient.NAMESPACE, "worldCollectionDatabaseKeys");
            if (worldCollectionDatabaseKeys != null) {
                for (final String worldCollectionKey : worldCollectionDatabaseKeys.split(",")) {
                    loaderContext.addWorldCollectionDatabaseKey(worldCollectionKey);
                }
            }
            instance.setWorldCollectionLoaderContext(loaderContext);
            if (!instance.loadWorldCollections()) {
                Log.error("CreateInstanceHook: load world collections failed loaderContext=" + instance.getWorldCollectionLoaderContext());
                return null;
            }
            InstancePlugin.this.sendSpawnGenerators(instance);
            final int populationLimit = (int)mergedTemplate.get(InstanceClient.NAMESPACE, "populationLimit");
            instance.setPopulationLimit(populationLimit);
            if (!instance.runInitScript()) {
                Log.error("CreateInstanceHook: init world script failed fileName=" + instance.getInitScriptFileName());
                return null;
            }
            instance.setWorldLoaderOverride(null);
            instance.setState(3);
            final InstanceTemplate iTmpl = AgisMobClient.getInstanceTemplate(templateName);
            AgisMobClient.spawnInstanceObjects(iTmpl, instanceOid2);
            final SubjectMessage loadedMessage = new SubjectMessage(InstanceClient.MSG_TYPE_INSTANCE_LOADED, instanceOid2);
            Engine.getAgent().sendBroadcast(loadedMessage);
            return instanceOid2;
        }
    }
    
    class LoadInstanceHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final SubjectMessage message = (SubjectMessage)msg;
            final OID instanceOid = message.getSubject();
            Instance instance = (Instance)EntityManager.getEntityByNamespace(instanceOid, InstanceClient.NAMESPACE);
            if (instance != null) {
                if (Log.loggingDebug) {
                    Log.debug("LoadInstanceHook: instance already loaded instanceOid=" + instanceOid + " state=" + instance.getState());
                }
                if (instance.getState() == 3) {
                    Engine.getAgent().sendIntegerResponse(message, 0);
                }
                else {
                    Engine.getAgent().sendIntegerResponse(message, -4);
                }
                return true;
            }
            final List<Namespace> namespaces = Engine.getDatabase().getObjectNamespaces(instanceOid);
            if (namespaces == null) {
                Log.debug("LoadInstanceHook: unknown instanceOid=" + instanceOid);
                Engine.getAgent().sendIntegerResponse(message, -1);
                return true;
            }
            if (!namespaces.contains(InstanceClient.NAMESPACE)) {
                Log.error("LoadInstanceHook: not an instance oid=" + instanceOid);
                Engine.getAgent().sendIntegerResponse(message, -2);
                return true;
            }
            final PluginStatus plugin = InstancePlugin.this.selectWorldManagerPlugin();
            if (plugin == null) {
                Log.error("LoadInstanceHook: no world manager for instance, instanceOid=" + instanceOid);
                Engine.getAgent().sendIntegerResponse(message, -3);
                return true;
            }
            if (Log.loggingDebug) {
                Log.debug("LoadInstanceHook: assigned world manager " + plugin.plugin_name + " host=" + plugin.host_name + " for instanceOid=" + instanceOid);
            }
            WorldManagerClient.hostInstance(instanceOid, plugin.plugin_name);
            final OID result = ObjectManagerClient.loadObject(instanceOid);
            if (result == null) {
                Engine.getAgent().sendIntegerResponse(message, -2);
                return true;
            }
            instance = (Instance)EntityManager.getEntityByNamespace(instanceOid, InstanceClient.NAMESPACE);
            Log.info("InstancePlugin: LOAD_INSTANCE instanceOid=" + instanceOid + " name=[" + instance.getName() + "]" + " templateName=[" + instance.getTemplateName() + "]" + " wmName=" + plugin.plugin_name);
            instance.setState(2);
            String loaderName = instance.getWorldLoaderOverrideName();
            if (loaderName == null) {
                loaderName = "default";
            }
            instance.setWorldLoaderOverride(InstancePlugin.this.createLoaderOverride(loaderName));
            if (!instance.loadWorldData()) {
                Log.error("LoadInstanceHook: load world file failed fileName=" + instance.getWorldFileName() + " instanceOid=" + instanceOid);
                Engine.getAgent().sendIntegerResponse(message, -2);
                return true;
            }
            if (!instance.loadWorldCollections()) {
                Log.error("LoadInstanceHook: load world collections failed loaderContext=" + instance.getWorldCollectionLoaderContext() + " instanceOid=" + instanceOid);
                Engine.getAgent().sendIntegerResponse(message, -2);
                return true;
            }
            final SubjectMessage loadContentMessage = new SubjectMessage(InstanceClient.MSG_TYPE_LOAD_INSTANCE_CONTENT, instanceOid);
            Engine.getAgent().sendRPC(loadContentMessage);
            InstancePlugin.this.sendSpawnGenerators(instance);
            if (!instance.runLoadScript()) {
                Log.error("LoadInstanceHook: init world script failed fileName=" + instance.getInitScriptFileName() + " instanceOid=" + instanceOid);
                Engine.getAgent().sendIntegerResponse(message, -2);
                return true;
            }
            instance.setWorldLoaderOverride(null);
            instance.setState(3);
            Engine.getAgent().sendIntegerResponse(message, 0);
            return true;
        }
    }
    
    class InstanceGenerateSubObjectHook extends GenerateSubObjectHook
    {
        public InstanceGenerateSubObjectHook() {
            super(InstancePlugin.this);
        }
        
        @Override
        public SubObjData generateSubObject(final Template template, final Namespace namespace, final OID instanceOid) {
            final Instance instance = new Instance(instanceOid);
            instance.setType(ObjectTypes.instance);
            instance.setName((String)template.get(InstanceClient.NAMESPACE, "name"));
            final String templateName = (String)template.get(InstanceClient.NAMESPACE, "templateName");
            Log.debug("PORTAL: template name: " + templateName);
            instance.setTemplateName(templateName);
            instance.setInitScriptFileName((String)template.get(InstanceClient.NAMESPACE, "initScriptFileName"));
            instance.setLoadScriptFileName((String)template.get(InstanceClient.NAMESPACE, "loadScriptFileName"));
            instance.setWorldLoaderOverrideName((String)template.get(InstanceClient.NAMESPACE, "loaderOverrideName"));
            final String terrainConfigFile = (String)template.get(InstanceClient.NAMESPACE, "terrainConfigFile");
            if (terrainConfigFile != null) {
                final TerrainConfig terrainConfig = new TerrainConfig();
                terrainConfig.setConfigType("file");
                terrainConfig.setConfigData(terrainConfigFile);
                instance.setTerrainConfig(terrainConfig);
            }
            final Map<String, Serializable> props = template.getSubMap(Namespace.INSTANCE);
            for (final Map.Entry<String, Serializable> entry : props.entrySet()) {
                final String key = entry.getKey();
                final Serializable value = entry.getValue();
                if (!key.startsWith(":")) {
                    instance.setProperty(key, value);
                }
            }
            Boolean persistent = (Boolean)template.get(Namespace.OBJECT_MANAGER, ":persistent");
            if (persistent == null) {
                persistent = false;
            }
            instance.setPersistenceFlag(persistent);
            instance.setState(1);
            EntityManager.registerEntityByNamespace(instance, InstanceClient.NAMESPACE);
            if (persistent) {
                Engine.getPersistenceManager().persistEntity(instance);
            }
            return new SubObjData();
        }
    }
    
    class InstanceUnloadHook implements UnloadHook
    {
        @Override
        public void onUnload(final Entity ee) {
            final Instance instance = (Instance)ee;
            instance.setState(4);
            if (instance.getPersistenceFlag()) {
                Engine.getPersistenceManager().persistEntity(instance);
            }
            Log.info("InstancePlugin: INSTANCE_UNLOAD instanceOid=" + instance.getOid() + " name=[" + instance.getName() + "]" + " templateName=[" + instance.getTemplateName() + "]");
        }
    }
    
    class InstanceDeleteHook implements DeleteHook
    {
        @Override
        public void onDelete(final OID oid, final Namespace namespace) {
        }
        
        @Override
        public void onDelete(final Entity ee) {
            final Instance instance = (Instance)ee;
            instance.setState(5);
            Log.info("InstancePlugin: INSTANCE_DELETE instanceOid=" + instance.getOid() + " name=[" + instance.getName() + "]" + " templateName=[" + instance.getTemplateName() + "]");
        }
    }
    
    class GetInstanceInfoHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InstanceClient.GetInstanceInfoMessage message = (InstanceClient.GetInstanceInfoMessage)msg;
            if ((message.getFlags() & 0x2000) != 0x0 && message.getInstanceOid() == null) {
                final List<InstanceClient.InstanceInfo> list = this.getMultipleInstanceInfo(message.getFlags(), message.getInstanceName());
                Engine.getAgent().sendObjectResponse(msg, list);
            }
            else {
                final InstanceClient.InstanceInfo info = this.getInstanceInfo(message.getFlags(), message.getInstanceOid(), message.getInstanceName());
                Engine.getAgent().sendObjectResponse(msg, info);
            }
            return true;
        }
        
        public InstanceClient.InstanceInfo getInstanceInfo(final int infoFlags, OID instanceOid, final String instanceName) {
            final InstanceClient.InstanceInfo info = new InstanceClient.InstanceInfo();
            Instance instance = null;
            if (instanceOid == null) {
                if (instanceName == null) {
                    return info;
                }
                instance = InstancePlugin.this.getInstance(instanceName);
                if (instance == null) {
                    instanceOid = InstancePlugin.this.getPersistentInstanceOid(instanceName);
                    if (instanceOid == null) {
                        instanceOid = InstanceClient.loadInstance(instanceName);
                        if (instanceOid == null) {
                            return info;
                        }
                    }
                }
                else {
                    instanceOid = instance.getOid();
                    info.loaded = (instance.getState() == 3);
                }
            }
            else {
                instance = (Instance)EntityManager.getEntityByNamespace(instanceOid, InstanceClient.NAMESPACE);
                if (instance == null) {
                    return info;
                }
                info.loaded = (instance.getState() == 3);
            }
            if ((infoFlags & 0x1) != 0x0) {
                info.oid = instanceOid;
            }
            if (instance == null) {
                return info;
            }
            this.getInstanceInfo(instance, infoFlags, info);
            return info;
        }
        
        public List<InstanceClient.InstanceInfo> getMultipleInstanceInfo(final int infoFlags, final String instanceName) {
            final Entity[] entities = EntityManager.getAllEntitiesByNamespace(InstanceClient.NAMESPACE);
            final List<InstanceClient.InstanceInfo> list = new ArrayList<InstanceClient.InstanceInfo>();
            for (final Entity entity : entities) {
                if (entity instanceof Instance) {
                    if (instanceName.equals(((Instance)entity).getName())) {
                        final Instance instance = (Instance)entity;
                        final InstanceClient.InstanceInfo info = new InstanceClient.InstanceInfo();
                        info.loaded = (instance.getState() == 3);
                        info.oid = instance.getOid();
                        this.getInstanceInfo(instance, infoFlags, info);
                        list.add(info);
                    }
                }
            }
            return list;
        }
        
        public void getInstanceInfo(final Instance instance, final int infoFlags, final InstanceClient.InstanceInfo info) {
            if ((infoFlags & 0x2) != 0x0) {
                info.name = instance.getName();
            }
            if ((infoFlags & 0x4) != 0x0) {
                info.templateName = instance.getTemplateName();
            }
            if ((infoFlags & 0x8) != 0x0) {
                info.skybox = instance.getGlobalSkybox();
            }
            if ((infoFlags & 0x10) != 0x0) {
                info.fog = instance.getGlobalFog();
            }
            if ((infoFlags & 0x20) != 0x0) {
                info.ambientLight = instance.getGlobalAmbientLight();
            }
            if ((infoFlags & 0x40) != 0x0) {
                info.dirLight = instance.getGlobalDirectionalLight();
            }
            if ((infoFlags & 0x80) != 0x0) {
                info.ocean = instance.getOceanData();
            }
            if ((infoFlags & 0x100) != 0x0) {
                info.worldFile = instance.getWorldFileName();
            }
            if ((infoFlags & 0x200) != 0x0) {
                info.terrainConfig = instance.getTerrainConfig();
            }
            if ((infoFlags & 0x400) != 0x0) {
                info.regionConfig = instance.getRegionConfig();
            }
            if ((infoFlags & 0x800) != 0x0) {
                info.playerPopulation = instance.getPlayerPopulation();
            }
            if ((infoFlags & 0x1000) != 0x0) {
                info.populationLimit = instance.getPopulationLimit();
            }
        }
    }
    
    class GetRegionHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InstanceClient.GetRegionMessage message = (InstanceClient.GetRegionMessage)msg;
            final Instance instance = (Instance)EntityManager.getEntityByNamespace(message.getInstanceOid(), InstanceClient.NAMESPACE);
            if (instance == null) {
                Log.error("GetRegionHook: unknown instanceOid=" + message.getInstanceOid());
                Engine.getAgent().sendObjectResponse(msg, null);
                return true;
            }
            final Region region = instance.getRegion(message.getRegionName());
            Log.debug("GetRegionHook: name=" + message.getRegionName() + " instanceOid=" + message.getInstanceOid() + " " + region);
            if (region == null) {
                Log.error("GetRegionHook: unknown regionName=" + message.getRegionName() + " instanceOid=" + message.getInstanceOid());
                Engine.getAgent().sendObjectResponse(msg, null);
                return true;
            }
            final Region result = new Region(region.getName());
            result.setPriority(region.getPriority());
            final long fetchFlags = message.getFlags();
            if ((fetchFlags & 0x1L) != 0x0L) {
                result.setBoundary(region.getBoundary());
            }
            if ((fetchFlags & 0x2L) != 0x0L) {
                result.setProperties(region.getPropertyMapRef());
            }
            Engine.getAgent().sendObjectResponse(msg, result);
            return true;
        }
    }
    
    class CheckPopulationHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            return true;
        }
    }
    
    class PopulationHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            OID instanceOid;
            int delta;
            if (msg instanceof WorldManagerClient.SpawnedMessage) {
                final WorldManagerClient.SpawnedMessage message = (WorldManagerClient.SpawnedMessage)msg;
                instanceOid = message.getInstanceOid();
                delta = 1;
            }
            else {
                if (!(msg instanceof WorldManagerClient.DespawnedMessage)) {
                    return true;
                }
                final WorldManagerClient.DespawnedMessage message2 = (WorldManagerClient.DespawnedMessage)msg;
                instanceOid = message2.getInstanceOid();
                delta = -1;
            }
            final Instance instance = (Instance)EntityManager.getEntityByNamespace(instanceOid, InstanceClient.NAMESPACE);
            if (instance == null) {
                Log.error("PopulationHook: unknown instanceOid=" + instanceOid + " msg=" + msg);
                return true;
            }
            final int population = instance.changePlayerPopulation(delta);
            if (InstancePlugin.this.populationChangeCallback != null) {
                InstancePlugin.this.populationChangeCallback.onInstancePopulationChange(instanceOid, instance.getName(), population);
            }
            return true;
        }
    }
    
    class GetMatchingEntityOidsHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InstanceClient.GetMatchingEntityOidsMessage message = (InstanceClient.GetMatchingEntityOidsMessage)msg;
            final Entity[] entities = EntityManager.getAllEntitiesByNamespace(InstanceClient.NAMESPACE);
            final List<OID> oids = InstancePlugin.this.getMatchingEntityOids(message.getEntityName());
            Engine.getAgent().sendObjectResponse(msg, oids);
            return true;
        }
    }
    
    class GetNavMeshHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final InstanceClient.GetNavMeshPathMessage message = (InstanceClient.GetNavMeshPathMessage)msg;
            final OID instanceOid = WorldManagerClient.getObjectInfo(message.getMobOid()).instanceOid;
            final Instance instance = (Instance)EntityManager.getEntityByNamespace(instanceOid, InstanceClient.NAMESPACE);
            if (instance == null) {
                Log.error("GetNavMeshHook: unknown instanceOid=" + instanceOid + " msg=" + msg);
                return true;
            }
            return true;
        }
    }
    
    class UpdateObjectHook implements Hook
    {
        @Override
        public boolean processMessage(final Message msg, final int flags) {
            final WorldManagerClient.UpdateMessage updateReq = (WorldManagerClient.UpdateMessage)msg;
            final OID notifyOid = updateReq.getTarget();
            final OID updateOid = updateReq.getSubject();
            if (Log.loggingDebug) {
                InstancePlugin.log.debug("UpdateObjectHook: notifyOid=" + notifyOid + " updateOid=" + updateOid);
            }
            final Entity updateEntity = EntityManager.getEntityByNamespace(updateOid, InstanceClient.NAMESPACE);
            if (updateEntity == null) {
                InstancePlugin.log.debug("UpdateObjectHook: could not find sub object for oid=" + updateOid);
                return false;
            }
            this.sendTargetedPropertyMessage(notifyOid, updateOid, updateEntity);
            return true;
        }
        
        private void sendTargetedPropertyMessage(final OID targetOid, final OID updateOid, final Entity updateEntity) {
            final WorldManagerClient.TargetedPropertyMessage propMessage = new WorldManagerClient.TargetedPropertyMessage(targetOid, updateOid);
            propMessage.setProperty("collisionPoints", updateEntity.getProperty("collisionPoints"));
            Engine.getAgent().sendBroadcast(propMessage);
        }
    }
    
    public class RegionSearch implements Searchable
    {
        @Override
        public Collection runSearch(final SearchClause search, final SearchSelection selection) {
            final Region.Search regionSearch = (Region.Search)search;
            final Instance instance = (Instance)EntityManager.getEntityByNamespace(regionSearch.getInstanceOid(), InstanceClient.NAMESPACE);
            if (instance == null) {
                Log.error("runSearch: unknown instanceOid=" + regionSearch.getInstanceOid());
                return null;
            }
            return instance.runRegionSearch(search, selection);
        }
    }
    
    public static class PopulationChangeCallback
    {
        public void onInstancePopulationChange(final OID instanceOid, final String name, final int population) {
        }
    }
}
