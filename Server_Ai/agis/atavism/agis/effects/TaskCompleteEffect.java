// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import atavism.agis.objects.CombatInfo;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.plugins.QuestClient;
import atavism.agis.core.AgisEffect;

public class TaskCompleteEffect extends AgisEffect
{
    protected int taskID;
    private static final long serialVersionUID = 1L;
    
    public TaskCompleteEffect(final int id, final String name) {
        super(id, name);
        this.taskID = -1;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final CombatInfo obj = state.getTarget();
        final CombatInfo caster = state.getSource();
        final QuestClient.TaskUpdateMessage msg = new QuestClient.TaskUpdateMessage(caster.getOwnerOid(), this.taskID, 1);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public int getTaskID() {
        return this.taskID;
    }
    
    public void setTaskID(final int taskID) {
        this.taskID = taskID;
    }
}
