// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.behaviors;

import atavism.msgsys.MessageAgent;
import atavism.msgsys.Message;
import atavism.msgsys.IFilter;
import atavism.server.engine.Engine;
import atavism.server.plugins.WorldManagerClient;
import atavism.msgsys.MessageTypeFilter;
import java.util.HashMap;
import java.util.Map;
import atavism.msgsys.MessageCallback;
import atavism.server.engine.Behavior;

public class ChatResponseBehavior extends Behavior implements MessageCallback
{
    Map<String, String> responses;
    Long eventSub;
    private static final long serialVersionUID = 1L;
    
    public ChatResponseBehavior() {
        this.responses = new HashMap<String, String>();
        this.eventSub = null;
    }
    
    public void initialize() {
        final MessageTypeFilter filter = new MessageTypeFilter();
        filter.addType(WorldManagerClient.MSG_TYPE_COM);
        this.eventSub = Engine.getAgent().createSubscription((IFilter)filter, (MessageCallback)this);
    }
    
    public void activate() {
    }
    
    public void deactivate() {
        if (this.eventSub != null) {
            Engine.getAgent().removeSubscription(this.eventSub);
            this.eventSub = null;
        }
    }
    
    public void handleMessage(final Message msg, final int flags) {
        if (msg instanceof WorldManagerClient.ComMessage) {
            final WorldManagerClient.ComMessage comMsg = (WorldManagerClient.ComMessage)msg;
            final String response = this.responses.get(comMsg.getString());
            if (response != null && MessageAgent.responseExpected(flags)) {
                WorldManagerClient.sendChatMsg(this.obj.getOid(), "", 1, response);
            }
        }
    }
    
    public void addChatResponse(final String trigger, final String response) {
        this.responses.put(trigger, response);
    }
}
