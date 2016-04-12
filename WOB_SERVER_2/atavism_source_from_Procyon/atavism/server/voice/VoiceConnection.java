// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.voice;

import java.io.BufferedOutputStream;
import atavism.server.util.SecureToken;
import atavism.server.engine.OID;
import atavism.server.network.ClientConnection;

public class VoiceConnection
{
    public ClientConnection con;
    public OID playerOid;
    public GroupMember groupMember;
    public OID groupOid;
    public VoiceGroup group;
    public byte micVoiceNumber;
    public SecureToken authToken;
    public short seqNum;
    public boolean listenToYourself;
    public BufferedOutputStream recordSpeexStream;
    
    public VoiceConnection(final ClientConnection con) {
        this.con = null;
        this.authToken = null;
        this.seqNum = 0;
        this.listenToYourself = false;
        this.recordSpeexStream = null;
        this.con = con;
    }
    
    @Override
    public String toString() {
        return "oid " + this.playerOid + " " + this.con.IPAndPort();
    }
}
