// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import atavism.server.objects.SpawnData;
import atavism.server.objects.RegionConfig;
import atavism.server.objects.Region;
import atavism.server.objects.Template;
import atavism.server.objects.LightData;

public interface WorldLoaderOverride
{
    boolean adjustLightData(final String p0, final String p1, final LightData p2);
    
    boolean adjustObjectTemplate(final String p0, final String p1, final Template p2);
    
    boolean adjustRegion(final String p0, final String p1, final Region p2);
    
    boolean adjustRegionConfig(final String p0, final String p1, final Region p2, final RegionConfig p3);
    
    boolean adjustSpawnData(final String p0, final String p1, final SpawnData p2);
}
