// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.msgsys.SubjectMessage;

public class LogoutMessage extends SubjectMessage
{
    private String playerName;
    public static final MessageType MSG_TYPE_LOGOUT;
    private static final long serialVersionUID = 1L;
    
    public LogoutMessage() {
    }
    
    public LogoutMessage(final OID playerOid, final String playerName) {
        super(LogoutMessage.MSG_TYPE_LOGOUT, playerOid);
        this.setPlayerName(playerName);
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String name) {
        this.playerName = name;
    }
    
    static {
        MSG_TYPE_LOGOUT = MessageType.intern("ao.LOGOUT");
    }
}
