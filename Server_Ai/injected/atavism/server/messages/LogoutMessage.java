// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.network.AOByteBuffer;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.SubjectMessage;

public class LogoutMessage extends SubjectMessage implements Marshallable
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.playerName != null && this.playerName != "") {
            flag_bits = 1;
        }
        buf.putByte(flag_bits);
        if (this.playerName != null && this.playerName != "") {
            buf.putString(this.playerName);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.playerName = buf.getString();
        }
        return this;
    }
}
