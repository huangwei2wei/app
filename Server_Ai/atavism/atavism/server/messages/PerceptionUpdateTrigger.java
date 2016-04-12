// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.msgsys.SubscriptionHandle;
import atavism.msgsys.AgentHandle;
import atavism.msgsys.FilterUpdate;

public interface PerceptionUpdateTrigger
{
    void preUpdate(final PerceptionFilter p0, final FilterUpdate.Instruction p1, final AgentHandle p2, final SubscriptionHandle p3);
    
    void postUpdate(final PerceptionFilter p0, final FilterUpdate.Instruction p1, final AgentHandle p2, final SubscriptionHandle p3);
}
