// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.msgsys.MessageType;
import atavism.server.engine.OID;
import atavism.msgsys.SubjectMessage;

public class LoginMessage extends SubjectMessage
{
    private String playerName;
    private OID instanceOid;
    public static final MessageType MSG_TYPE_LOGIN;
    private static final long serialVersionUID = 1L;
    
    public LoginMessage() {
    }
    
    public LoginMessage(final OID playerOid, final String playerName) {
        super(LoginMessage.MSG_TYPE_LOGIN, playerOid);
        this.setPlayerName(playerName);
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String name) {
        this.playerName = name;
    }
    
    public OID getInstanceOid() {
        return this.instanceOid;
    }
    
    public void setInstanceOid(final OID oid) {
        this.instanceOid = oid;
    }
    
    static {
        MSG_TYPE_LOGIN = MessageType.intern("ao.LOGIN");
    }
}
