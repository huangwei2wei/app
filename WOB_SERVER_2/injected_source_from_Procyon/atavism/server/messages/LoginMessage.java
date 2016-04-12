// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.messages;

import atavism.server.marshalling.MarshallingRuntime;
import atavism.server.network.AOByteBuffer;
import atavism.msgsys.MessageType;
import atavism.server.engine.OID;
import atavism.server.marshalling.Marshallable;
import atavism.msgsys.SubjectMessage;

public class LoginMessage extends SubjectMessage implements Marshallable
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
    
    @Override
    public void marshalObject(final AOByteBuffer buf) {
        super.marshalObject(buf);
        byte flag_bits = 0;
        if (this.playerName != null && this.playerName != "") {
            flag_bits = 1;
        }
        if (this.instanceOid != null) {
            flag_bits |= 0x2;
        }
        buf.putByte(flag_bits);
        if (this.playerName != null && this.playerName != "") {
            buf.putString(this.playerName);
        }
        if (this.instanceOid != null) {
            MarshallingRuntime.marshalObject(buf, (Object)this.instanceOid);
        }
    }
    
    @Override
    public Object unmarshalObject(final AOByteBuffer buf) {
        super.unmarshalObject(buf);
        final byte flag_bits0 = buf.getByte();
        if ((flag_bits0 & 0x1) != 0x0) {
            this.playerName = buf.getString();
        }
        if ((flag_bits0 & 0x2) != 0x0) {
            this.instanceOid = (OID)MarshallingRuntime.unmarshalObject(buf);
        }
        return this;
    }
}
