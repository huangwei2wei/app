// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.agis.plugins.AgisMobClient;
import atavism.agis.objects.BehaviorTemplate;
import java.io.Serializable;
import atavism.server.objects.SpawnData;
import atavism.server.objects.ObjectTypes;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.util.Log;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;

public class SpawnMobAcquireHook implements AcquireHook
{
    protected int mobTemplateID;
    private static final long serialVersionUID = 1L;
    
    public SpawnMobAcquireHook() {
    }
    
    public SpawnMobAcquireHook(final int currencyID) {
        this.setMobTemplateID(currencyID);
    }
    
    public void setMobTemplateID(final int mobTemplateID) {
        if (mobTemplateID < 1) {
            throw new RuntimeException("SpawnMobAcquireHook.setMobTemplateID: bad mob template");
        }
        this.mobTemplateID = mobTemplateID;
    }
    
    public int getMobTemplateID() {
        return this.mobTemplateID;
    }
    
    @Override
    public boolean acquired(final OID activatorOid, final AgisItem item) {
        if (Log.loggingDebug) {
            Log.debug("SpawnMobAcquireHook.activate: activator=" + activatorOid + " item=" + item + " resource=" + this.mobTemplateID);
        }
        final WorldManagerClient.ObjectInfo objInfo = WorldManagerClient.getObjectInfo(activatorOid);
        if (objInfo.objType != ObjectTypes.player) {
            return false;
        }
        final SpawnData sd = new SpawnData();
        sd.setProperty("id", (Serializable)(int)System.currentTimeMillis());
        sd.setTemplateID(this.mobTemplateID);
        final BehaviorTemplate behavTmpl = new BehaviorTemplate();
        behavTmpl.setHasCombat(true);
        behavTmpl.setWeaponsSheathed(false);
        behavTmpl.setRoamRadius(0);
        sd.setLoc(objInfo.loc);
        sd.setOrientation(objInfo.orient);
        sd.setInstanceOid(objInfo.instanceOid);
        sd.setSpawnRadius(0);
        sd.setCorpseDespawnTime(1000);
        sd.setRespawnTime(1000);
        sd.setNumSpawns(1);
        sd.setProperty("behaviourTemplate", (Serializable)behavTmpl);
        AgisMobClient.spawnMob(sd);
        return true;
    }
    
    @Override
    public String toString() {
        return "SpawnMobAcquireHook=" + this.mobTemplateID;
    }
}
