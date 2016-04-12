// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.core;

import atavism.server.engine.BasicWorldNode;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.math.AOVector;
import java.io.Serializable;
import atavism.server.plugins.WorldManagerClient;
import atavism.agis.plugins.VoxelClient;
import atavism.agis.objects.AgisItem;
import atavism.server.engine.OID;
import atavism.server.util.Log;

public class CreateClaimActivateHook implements ActivateHook
{
    protected int size;
    private static final long serialVersionUID = 1L;
    
    public CreateClaimActivateHook() {
    }
    
    public CreateClaimActivateHook(final AgisAbility ability) {
        Log.debug("AJ: creating CreateClaimActivateHook with ability: " + ability.getID());
        this.setSize(ability.getID());
    }
    
    public CreateClaimActivateHook(final int size) {
        this.setSize(size);
    }
    
    public void setSize(final int size) {
        if (size < 5) {
            throw new RuntimeException("CreateClaimActivateHook.setSize: bad size");
        }
        Log.debug("AJ: setting size to: " + size);
        this.size = size;
    }
    
    public int getSize() {
        return this.size;
    }
    
    @Override
    public boolean activate(final OID activatorOid, final AgisItem item, final OID targetOid) {
        if (Log.loggingDebug) {
            Log.debug("CreateClaimActivateHook.activate: activator=" + activatorOid + " item=" + item + " size=" + this.size + " target=" + targetOid);
        }
        final WorldManagerClient.ExtensionMessage eMsg = new WorldManagerClient.ExtensionMessage(VoxelClient.MSG_TYPE_CREATE_CLAIM, (String)null, activatorOid);
        eMsg.setProperty("name", (Serializable)"My Claim");
        final BasicWorldNode wNode = WorldManagerClient.getWorldNode(activatorOid);
        eMsg.setProperty("loc", (Serializable)new AOVector(wNode.getLoc()));
        eMsg.setProperty("size", (Serializable)this.size);
        eMsg.setProperty("forSale", (Serializable)false);
        eMsg.setProperty("cost", (Serializable)0);
        eMsg.setProperty("currency", (Serializable)0);
        eMsg.setProperty("owned", (Serializable)true);
        eMsg.setProperty("item", (Serializable)item.getOid());
        eMsg.setProperty("claimTemplateItem", (Serializable)item.getTemplateID());
        Engine.getAgent().sendBroadcast((Message)eMsg);
        return true;
    }
    
    @Override
    public String toString() {
        return "CreateClaimActivateHook:size=" + this.size;
    }
}
