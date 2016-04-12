// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.objects;

import atavism.server.engine.Matcher;
import atavism.server.engine.SearchManager;
import atavism.server.engine.Searchable;
import atavism.server.engine.SearchSelection;
import atavism.server.engine.SearchClause;
import atavism.server.util.Log;
import java.util.Collection;
import java.util.Iterator;
import atavism.server.engine.ScriptManager;
import atavism.server.engine.WorldFileLoader;
import atavism.server.math.Point;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import atavism.server.engine.WorldLoaderOverride;
import atavism.server.engine.WorldCollectionLoaderContext;
import atavism.server.pathing.PathInfo;
import java.util.List;
import java.util.LinkedList;
import atavism.server.engine.TerrainConfig;

public class Instance extends Entity
{
    public static final int STATE_INIT = 0;
    public static final int STATE_GENERATE = 1;
    public static final int STATE_LOAD = 2;
    public static final int STATE_AVAILABLE = 3;
    public static final int STATE_UNLOAD = 4;
    public static final int STATE_DELETE = 5;
    private transient RegionSearch regionSearch;
    private transient String globalSkybox;
    private transient Fog globalFog;
    private transient Color globalAmbientLight;
    private transient LightData globalDirLightData;
    private transient OceanData oceanData;
    private transient TerrainConfig terrainConfig;
    private transient Region globalRegion;
    private transient RoadRegionConfig roadConfig;
    private transient LinkedList<Region> regionList;
    private transient LinkedList<String> regionConfig;
    private transient List<SpawnData> spawnGen;
    private transient PathInfo pathInfo;
    private String name;
    private String worldFileName;
    private String initScriptFileName;
    private String loadScriptFileName;
    private String templateName;
    private String worldLoaderOverrideName;
    private WorldCollectionLoaderContext loaderContext;
    private InstanceTemplate template;
    private int populationLimit;
    private transient int state;
    private transient WorldLoaderOverride worldLoaderOverride;
    private transient int playerPopulation;
    private static ThreadLocal<Instance> loadingInstance;
    private static final long serialVersionUID = 1L;
    
    public Instance() {
        this.regionSearch = new RegionSearch();
        this.globalSkybox = "";
        this.globalRegion = new Region("Global Region");
        this.roadConfig = new RoadRegionConfig();
        this.regionList = new LinkedList<Region>();
        this.regionConfig = new LinkedList<String>();
        this.spawnGen = new LinkedList<SpawnData>();
        this.pathInfo = new PathInfo();
        this.populationLimit = -1;
        this.state = 0;
    }
    
    public Instance(final OID oid) {
        super(oid);
        this.regionSearch = new RegionSearch();
        this.globalSkybox = "";
        this.globalRegion = new Region("Global Region");
        this.roadConfig = new RoadRegionConfig();
        this.regionList = new LinkedList<Region>();
        this.regionConfig = new LinkedList<String>();
        this.spawnGen = new LinkedList<SpawnData>();
        this.pathInfo = new PathInfo();
        this.populationLimit = -1;
        this.state = 0;
        this.globalRegion.addConfig(this.roadConfig);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getTemplateName() {
        return this.templateName;
    }
    
    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }
    
    public String getWorldFileName() {
        return this.worldFileName;
    }
    
    public void setWorldFileName(final String fileName) {
        this.worldFileName = fileName;
    }
    
    public String getInitScriptFileName() {
        return this.initScriptFileName;
    }
    
    public void setInitScriptFileName(final String fileName) {
        this.initScriptFileName = fileName;
    }
    
    public String getLoadScriptFileName() {
        return this.loadScriptFileName;
    }
    
    public void setLoadScriptFileName(final String fileName) {
        this.loadScriptFileName = fileName;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public String getWorldLoaderOverrideName() {
        return this.worldLoaderOverrideName;
    }
    
    public void setWorldLoaderOverrideName(final String loaderName) {
        this.worldLoaderOverrideName = loaderName;
    }
    
    public WorldLoaderOverride getWorldLoaderOverride() {
        return this.worldLoaderOverride;
    }
    
    public void setWorldLoaderOverride(final WorldLoaderOverride loaderOverride) {
        this.worldLoaderOverride = loaderOverride;
    }
    
    public boolean loadWorldData() {
        this.setGlobalSkybox("");
        final Fog fog = new Fog("global fog");
        fog.setStart(50);
        fog.setEnd(100);
        fog.setColor(Color.White);
        this.setGlobalFog(fog);
        final Color lightColor = Color.White;
        this.setGlobalAmbientLight(lightColor);
        this.setupGlobalRegion();
        final Message msg = new WorldManagerClient.NewRegionMessage(this.getOid(), this.getGlobalRegion());
        Engine.getAgent().sendBroadcast(msg);
        return true;
    }
    
    protected void setupGlobalRegion() {
        final Boundary globalBoundary = new Boundary();
        globalBoundary.addPoint(new Point(-2000000.0f, 0.0f, 2000000.0f));
        globalBoundary.addPoint(new Point(2000000.0f, 0.0f, 2000000.0f));
        globalBoundary.addPoint(new Point(2000000.0f, 0.0f, -2000000.0f));
        globalBoundary.addPoint(new Point(-2000000.0f, 0.0f, -2000000.0f));
        this.getGlobalRegion().setBoundary(globalBoundary);
    }
    
    public boolean loadWorldFile() {
        final WorldFileLoader loader = new WorldFileLoader(this.worldFileName, this.worldLoaderOverride);
        return loader.load(this);
    }
    
    public boolean loadWorldCollections() {
        return this.loaderContext == null || this.loaderContext.load(this);
    }
    
    public boolean runInitScript() {
        if (this.initScriptFileName == null) {
            return true;
        }
        setCurrentInstance(this);
        try {
            final ScriptManager scriptManager = new ScriptManager();
            scriptManager.init();
            scriptManager.runFileWithThrow(this.initScriptFileName);
            return true;
        }
        catch (Exception e) {}
        finally {
            setCurrentInstance(null);
        }
        return false;
    }
    
    public boolean runLoadScript() {
        if (this.loadScriptFileName == null) {
            return true;
        }
        setCurrentInstance(this);
        try {
            final ScriptManager scriptManager = new ScriptManager();
            scriptManager.init();
            scriptManager.runFileWithThrow(this.loadScriptFileName);
            return true;
        }
        catch (Exception e) {}
        finally {
            setCurrentInstance(null);
        }
        return false;
    }
    
    public String getGlobalSkybox() {
        return this.globalSkybox;
    }
    
    public void setGlobalSkybox(final String skybox) {
        this.globalSkybox = skybox;
    }
    
    public Fog getGlobalFog() {
        return this.globalFog;
    }
    
    public void setGlobalFog(final Fog fog) {
        this.globalFog = fog;
    }
    
    public Color getGlobalAmbientLight() {
        return this.globalAmbientLight;
    }
    
    public void setGlobalAmbientLight(final Color lightColor) {
        this.globalAmbientLight = lightColor;
    }
    
    public LightData getGlobalDirectionalLight() {
        return this.globalDirLightData;
    }
    
    public void setGlobalDirectionalLight(final LightData lightData) {
        this.globalDirLightData = lightData;
    }
    
    public OceanData getOceanData() {
        return this.oceanData;
    }
    
    public void setOceanData(final OceanData od) {
        this.oceanData = od;
    }
    
    public TerrainConfig getTerrainConfig() {
        return this.terrainConfig;
    }
    
    public void setTerrainConfig(final TerrainConfig terrainConfig) {
        this.terrainConfig = terrainConfig;
    }
    
    public Region getGlobalRegion() {
        return this.globalRegion;
    }
    
    public RoadRegionConfig getRoadConfig() {
        return this.roadConfig;
    }
    
    public int getPopulationLimit() {
        return this.populationLimit;
    }
    
    public void setPopulationLimit(final int populationLimit) {
        this.populationLimit = populationLimit;
    }
    
    public synchronized void addRegion(final Region region) {
        this.regionList.add(region);
    }
    
    public synchronized Region getRegion(final String regionName) {
        for (final Region region : this.regionList) {
            if (region.getName().equals(regionName)) {
                return region;
            }
        }
        return null;
    }
    
    public synchronized List<Region> getRegionList() {
        return new LinkedList<Region>(this.regionList);
    }
    
    public synchronized void addRegionConfig(final String region) {
        this.regionConfig.add(region);
    }
    
    public synchronized List<String> getRegionConfig() {
        return new LinkedList<String>(this.regionConfig);
    }
    
    public synchronized void addSpawnData(final SpawnData spawnData) {
        this.spawnGen.add(spawnData);
    }
    
    public synchronized List<SpawnData> getSpawnData() {
        return this.spawnGen;
    }
    
    public synchronized void setWorldCollectionLoaderContext(final WorldCollectionLoaderContext context) {
        this.loaderContext = context;
    }
    
    public synchronized WorldCollectionLoaderContext getWorldCollectionLoaderContext() {
        return this.loaderContext;
    }
    
    public PathInfo getPathInfo() {
        return this.pathInfo;
    }
    
    public synchronized int changePlayerPopulation(final int delta) {
        Log.debug("POP: changePlayerPopulation by: " + delta + " with previous total: " + this.playerPopulation);
        return this.playerPopulation += delta;
    }
    
    public int getPlayerPopulation() {
        return this.playerPopulation;
    }
    
    public static Instance current() {
        if (Instance.loadingInstance.get() == null) {
            Log.error("Instance.current() called in the wrong context");
        }
        return Instance.loadingInstance.get();
    }
    
    public static OID currentOid() {
        final Instance instance = Instance.loadingInstance.get();
        if (instance != null) {
            return instance.getOid();
        }
        Log.error("Instance.currentOid() called in the wrong context");
        return null;
    }
    
    static void setCurrentInstance(final Instance instance) {
        Instance.loadingInstance.set(instance);
    }
    
    public Collection runRegionSearch(final SearchClause search, final SearchSelection selection) {
        return this.regionSearch.runSearch(search, selection);
    }
    
    static {
        Instance.loadingInstance = new ThreadLocal<Instance>();
    }
    
    class RegionSearch implements Searchable
    {
        @Override
        public Collection runSearch(final SearchClause search, final SearchSelection selection) {
            final Matcher matcher = SearchManager.getMatcher(search, Region.class);
            if (matcher == null) {
                return null;
            }
            final List<Object> resultList = new LinkedList<Object>();
            for (final Region region : Instance.this.regionList) {
                final boolean rc = matcher.match(region.getPropertyMapRef());
                if (rc) {
                    this.selectProperties(region.getName(), region, selection, resultList);
                }
            }
            return resultList;
        }
        
        void selectProperties(final String name, final Region region, final SearchSelection selection, final List<Object> resultList) {
            if (selection.getResultOption() == 2) {
                resultList.add(name);
                return;
            }
            final long propFlags = selection.getPropFlags();
            final Region result = new Region();
            result.setName(region.getName());
            result.setPriority(region.getPriority());
            if ((propFlags & 0x1L) != 0x0L) {
                result.setBoundary(region.getBoundary());
            }
            if ((propFlags & 0x2L) != 0x0L) {
                result.setProperties(region.getPropertyMapRef());
            }
            if (selection.getResultOption() == 1) {
                resultList.add(new SearchEntry(name, result));
            }
            else if ((propFlags & 0x3L) != 0x0L) {
                resultList.add(result);
            }
        }
    }
}
