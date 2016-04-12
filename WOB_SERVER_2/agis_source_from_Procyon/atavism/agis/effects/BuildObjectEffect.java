// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import atavism.agis.objects.BuildObjectTemplate;
import atavism.agis.objects.CombatInfo;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import atavism.server.plugins.WorldManagerClient;
import java.io.Serializable;
import java.util.HashMap;
import atavism.agis.plugins.AgisInventoryClient;
import java.util.LinkedList;
import atavism.agis.plugins.VoxelClient;
import atavism.agis.core.AgisEffect;

public class BuildObjectEffect extends AgisEffect
{
    protected int buildObjectTemplateID;
    private static final long serialVersionUID = 1L;
    
    public BuildObjectEffect(final int id, final String name) {
        super(id, name);
        this.buildObjectTemplateID = -1;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo obj = state.getTarget();
        final CombatInfo caster = state.getSource();
        final BuildObjectTemplate tmpl = VoxelClient.getBuildingTemplate(this.buildObjectTemplateID);
        if (tmpl == null) {
            return;
        }
        final LinkedList<Integer> components = new LinkedList<Integer>();
        final LinkedList<Integer> componentCounts = new LinkedList<Integer>();
        for (final int itemReq : tmpl.getStage(0).getItemReqs().keySet()) {
            components.add(itemReq);
            componentCounts.add(tmpl.getStage(0).getItemReqs().get(itemReq));
        }
        final boolean hasItems = AgisInventoryClient.checkComponents(caster.getOwnerOid(), components, componentCounts);
        if (!hasItems) {
            return;
        }
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", "start_build_object");
        props.put("buildObjectTemplate", tmpl.getId());
        props.put("gameObject", tmpl.getStage(0).getGameObject());
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, caster.getOwnerOid(), caster.getOwnerOid(), false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public int getBuildObjectTemplateID() {
        return this.buildObjectTemplateID;
    }
    
    public void setBuildObjectTemplateID(final int buildObjectTemplateID) {
        this.buildObjectTemplateID = buildObjectTemplateID;
    }
}
