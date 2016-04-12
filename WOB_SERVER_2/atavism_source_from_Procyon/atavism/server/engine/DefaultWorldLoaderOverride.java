// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.objects.SpawnData;
import atavism.server.objects.RegionConfig;
import atavism.server.objects.Region;
import atavism.server.objects.Template;
import atavism.server.objects.LightData;

public class DefaultWorldLoaderOverride implements WorldLoaderOverride
{
    @Override
    public boolean adjustLightData(final String worldCollectionName, final String objectName, final LightData lightData) {
        return true;
    }
    
    @Override
    public boolean adjustObjectTemplate(final String worldCollectionName, final String objectName, final Template template) {
        return true;
    }
    
    @Override
    public boolean adjustRegion(final String worldCollectionName, final String objectName, final Region region) {
        return true;
    }
    
    @Override
    public boolean adjustRegionConfig(final String worldCollectionName, final String objectName, final Region region, final RegionConfig regionConfig) {
        return true;
    }
    
    @Override
    public boolean adjustSpawnData(final String worldCollectionName, final String objectName, final SpawnData spawnData) {
        return true;
    }
}
