// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.plugins;

import atavism.server.messages.PropertyMessage;
import java.io.Serializable;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.server.engine.OID;
import atavism.msgsys.MessageType;

public class GuildClient
{
    public static final MessageType MSG_TYPE_CREATE_GUILD;
    public static final MessageType MSG_TYPE_GUILD_COMMAND;
    
    static {
        MSG_TYPE_CREATE_GUILD = MessageType.intern("guild.createGuild");
        MSG_TYPE_GUILD_COMMAND = MessageType.intern("guild.guildCommand");
    }
    
    public static void createGuild(final OID oid, final String guildName) {
        final createGuildMessage msg = new createGuildMessage(oid, guildName);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("GUILD CLIENT: createGuildMessage hit 2");
    }
    
    public static void guildCommand(final OID oid, final String commandType, final Serializable data, final Serializable dataTwo) {
        final guildCommandMessage msg = new guildCommandMessage(oid, commandType, data, dataTwo);
        Engine.getAgent().sendBroadcast((Message)msg);
        Log.debug("GUILD CLIENT: guildCommandMessage hit 2");
    }
    
    public static class createGuildMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public createGuildMessage() {
        }
        
        public createGuildMessage(final OID oid, final String guildName) {
            super(oid);
            this.setMsgType(GuildClient.MSG_TYPE_CREATE_GUILD);
            this.setProperty("guildName", (Serializable)guildName);
            Log.debug("GUILD CLIENT: createGuildMessage hit 1");
        }
    }
    
    public static class guildCommandMessage extends PropertyMessage
    {
        private static final long serialVersionUID = 1L;
        
        public guildCommandMessage() {
        }
        
        public guildCommandMessage(final OID oid, final String commandType, final Serializable data, final Serializable dataTwo) {
            super(oid);
            this.setMsgType(GuildClient.MSG_TYPE_GUILD_COMMAND);
            this.setProperty("commandType", (Serializable)commandType);
            this.setProperty("commandData", data);
            this.setProperty("commandDataTwo", dataTwo);
            Log.debug("GUILD CLIENT: guildCommandMessage hit 1");
        }
    }
}
