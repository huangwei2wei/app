// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.plugins;

import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.io.Serializable;
import atavism.server.engine.EnginePlugin;
import atavism.server.engine.Namespace;
import atavism.server.engine.OID;

public class ClientParameter
{
    public static final String ClientParameterExtensionType = "ClientParameter";
    
    public static String GetClientParameter(final OID playerOid, final String parameterName) {
        return (String)EnginePlugin.getObjectProperty(playerOid, Namespace.WORLD_MANAGER, "clientparm." + parameterName);
    }
    
    public static void SetClientParameter(final OID playerOid, final String parameterName, final String parameterValue) {
        final ClientParameterMessage msg = new ClientParameterMessage(playerOid);
        msg.setProperty(parameterName, parameterValue);
        Engine.getAgent().sendBroadcast(msg);
    }
    
    public static class ClientParameterMessage extends WorldManagerClient.TargetedExtensionMessage
    {
        private static final long serialVersionUID = 1L;
        
        public ClientParameterMessage(final OID oid) {
            super("ClientParameter", oid);
        }
    }
}
