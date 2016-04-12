// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.util.Log;
import java.io.Serializable;
import atavism.server.messages.PropertyMessage;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;

public class FactionClient
{
    public static final MessageType MSG_TYPE_REPUTATION_UPDATED;
    public static final MessageType MSG_TYPE_UPDATE_PVP_STATE;
    
    static {
        MSG_TYPE_REPUTATION_UPDATED = MessageType.intern("faction.REPUTATION_UPDATED");
        MSG_TYPE_UPDATE_PVP_STATE = MessageType.intern("faction.UPDATE_PVP_STATE");
    }
    
    public static void getAttitude(final OID oid, final OID targetOid) {
        final getAttitudeMessage msg = new getAttitudeMessage(oid, targetOid);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public static class getAttitudeMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public getAttitudeMessage() {
        }
        
        public getAttitudeMessage(final OID oid, final OID targetOid) {
            super(oid);
            this.setProperty("target", (Serializable)targetOid);
            this.setMsgType(FactionClient.MSG_TYPE_REPUTATION_UPDATED);
            Log.debug("COMBAT CLIENT: getAttitudeMessage hit 1");
        }
    }
}
