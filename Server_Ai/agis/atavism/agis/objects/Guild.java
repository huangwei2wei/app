// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.engine.EnginePlugin;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import java.util.Map;
import java.util.HashMap;
import atavism.server.util.Log;
import atavism.server.plugins.WorldManagerClient;
import atavism.server.engine.OID;
import java.util.ArrayList;
import java.io.Serializable;

public class Guild implements Serializable
{
    private String guildName;
    private String faction;
    private ArrayList<GuildRank> ranks;
    private ArrayList<GuildMember> members;
    private String motd;
    private String omotd;
    private static final long serialVersionUID = 1L;
    
    public Guild(final String guildName, final String faction, final ArrayList<String> rankNames, final ArrayList<ArrayList<String>> rankPermissions, final OID leaderOid, final ArrayList<OID> initiates) {
        this.guildName = guildName;
        this.faction = faction;
        this.ranks = new ArrayList<GuildRank>();
        for (int i = 0; i < rankNames.size(); ++i) {
            final GuildRank newRank = new GuildRank(rankNames.get(i), rankPermissions.get(i));
            this.ranks.add(newRank);
        }
        this.members = new ArrayList<GuildMember>();
        final GuildMember leader = new GuildMember(leaderOid, 0);
        this.members.add(leader);
        for (int j = 0; j < initiates.size(); ++j) {
            final GuildMember initiate = new GuildMember(initiates.get(j), this.ranks.size() - 1);
            this.members.add(initiate);
        }
        this.motd = "Welcome to the guild.";
        this.omotd = "Welcome to the guild.";
    }
    
    public void handleCommand(final OID oid, final String commandType, final Serializable commandData, final Serializable commandDataTwo) {
        if (commandType.equals("getUpdate")) {
            this.sendMessageSingle("sendGuildData", oid, null);
            return;
        }
        if (commandType.equals("acceptInvite")) {
            return;
        }
        if (!this.hasPermission(oid, commandType)) {
            WorldManagerClient.sendObjChatMsg(oid, 1, "You do not have permission to perform that command.");
            return;
        }
        if (commandType.equals("addRank")) {
            final String rankName = (String)commandData;
            final ArrayList<String> permissions = (ArrayList<String>)commandDataTwo;
            this.addRank(rankName, permissions);
        }
        else if (commandType.equals("setMotd")) {
            this.motd = (String)commandData;
            this.sendMessageAll("newMOTD", null);
        }
    }
    
    private boolean hasPermission(final OID oid, final String command) {
        int rankNum = -1;
        for (int i = 0; i < this.members.size(); ++i) {
            final OID memberOid = this.members.get(i).oid;
            if (memberOid.equals((Object)oid)) {
                rankNum = this.members.get(i).rank;
                break;
            }
        }
        if (rankNum == -1) {
            Log.error("GUILD: Command issuer has no rank in this guild.");
            return false;
        }
        final GuildRank rank = this.ranks.get(rankNum);
        return rank.permissions.contains(command);
    }
    
    private void addRank(final String rankName, final ArrayList<String> permissions) {
        final GuildRank newRank = new GuildRank(rankName, permissions);
        this.ranks.add(newRank);
        this.sendMessageAll("rankUpdate", null);
    }
    
    private void sendMessageAll(final String msgType, final Serializable data) {
        for (int i = 0; i < this.members.size(); ++i) {
            final GuildMember member = this.members.get(i);
            if (member.status != 0) {
                this.sendMessageSingle(msgType, member.oid, data);
            }
        }
    }
    
    private void sendMessageRank(final String msgType, final int rank, final Serializable data) {
        for (int i = 0; i < this.members.size(); ++i) {
            final GuildMember member = this.members.get(i);
            if (member.status != 0 && member.rank == rank) {
                this.sendMessageSingle(msgType, member.oid, data);
            }
        }
    }
    
    private void sendMessageSingle(final String msgType, final OID oid, final Serializable data) {
        final Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put("ext_msg_subtype", msgType);
        if (msgType == "sendGuildData") {
            props.put("guildName", this.guildName);
            props.put("motd", this.motd);
            props.put("omotd", this.omotd);
            props.put("numMembers", this.members.size());
            for (int i = 0; i < this.members.size(); ++i) {
                final GuildMember member = this.members.get(i);
                props.put("memberOid" + i, (Serializable)member.oid);
                props.put("memberName" + i, member.name);
                props.put("memberRank" + i, member.rank);
                props.put("memberLevel" + i, member.level);
                props.put("memberZone" + i, member.zone);
                props.put("memberNote" + i, member.note);
                props.put("memberStatus" + i, member.status);
            }
        }
        else if (msgType == "memberUpdate") {
            final Long memberOid = (Long)data;
            for (int j = 0; j < this.members.size(); ++j) {
                final GuildMember member2 = this.members.get(j);
                if (memberOid.equals(member2.oid)) {
                    props.put("memberOid" + j, (Serializable)member2.oid);
                    props.put("memberName" + j, member2.name);
                    props.put("memberRank" + j, member2.rank);
                    props.put("memberLevel" + j, member2.level);
                    props.put("memberZone" + j, member2.zone);
                    props.put("memberNote" + j, member2.note);
                    props.put("memberStatus" + j, member2.status);
                }
            }
        }
        else if (msgType == "rankUpdate") {
            props.put("numRanks", this.ranks.size());
            for (int i = 0; i < this.ranks.size(); ++i) {
                final GuildRank rank = this.ranks.get(i);
                props.put("rankName" + i, rank.rankName);
                props.put("rankNumPermissions" + i, rank.permissions.size());
                for (int k = 0; k < rank.permissions.size(); ++k) {
                    props.put("rankNum" + i + "Permission" + k, rank.permissions.get(k));
                }
            }
        }
        else if (msgType == "newMOTD") {
            props.put("motd", this.motd);
        }
        final WorldManagerClient.TargetedExtensionMessage msg = new WorldManagerClient.TargetedExtensionMessage(WorldManagerClient.MSG_TYPE_EXTENSION, oid, oid, false, (Map)props);
        Engine.getAgent().sendBroadcast((Message)msg);
    }
    
    public void setGuildName(final String guildName) {
        this.guildName = guildName;
    }
    
    public String getGuildName() {
        return this.guildName;
    }
    
    public void setFaction(final String faction) {
        this.faction = faction;
    }
    
    public String getFaction() {
        return this.faction;
    }
    
    public void setRanks(final ArrayList<GuildRank> ranks) {
        this.ranks = ranks;
    }
    
    public ArrayList<GuildRank> getRanks() {
        return this.ranks;
    }
    
    public void setMembers(final ArrayList<GuildMember> members) {
        this.members = members;
    }
    
    public ArrayList<GuildMember> getMembers() {
        return this.members;
    }
    
    public void setMOTD(final String motd) {
        this.motd = motd;
    }
    
    public String getMOTD() {
        return this.motd;
    }
    
    public void setOMOTD(final String omotd) {
        this.omotd = omotd;
    }
    
    public String getOMOTD() {
        return this.omotd;
    }
    
    private class GuildRank implements Serializable
    {
        protected String rankName;
        protected ArrayList<String> permissions;
        private static final long serialVersionUID = 1L;
        
        public GuildRank(final String rankName, final ArrayList<String> permissions) {
            this.rankName = rankName;
            this.permissions = permissions;
        }
        
        public void setRankName(final String rankName) {
            this.rankName = rankName;
        }
        
        public String getRankName() {
            return this.rankName;
        }
        
        public void setPermissions(final ArrayList<String> permissions) {
            this.permissions = permissions;
        }
        
        public ArrayList<String> getPermissions() {
            return this.permissions;
        }
    }
    
    private class GuildMember implements Serializable
    {
        protected OID oid;
        protected String name;
        protected int rank;
        protected int level;
        protected String zone;
        protected String note;
        protected int status;
        private static final long serialVersionUID = 1L;
        
        public GuildMember(final OID oid, final int rank) {
            this.oid = oid;
            this.name = WorldManagerClient.getObjectInfo(oid).name;
            this.rank = rank;
            this.zone = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "zone");
            this.note = "";
            this.status = 1;
        }
        
        public void setOid(final OID oid) {
            this.oid = oid;
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setRank(final int rank) {
            this.rank = rank;
        }
        
        public int getRank() {
            return this.rank;
        }
        
        public void setLevel(final int level) {
            this.level = level;
        }
        
        public int getLevel() {
            return this.level;
        }
        
        public void setZone(final String zone) {
            this.zone = zone;
        }
        
        public String getZone() {
            return this.zone;
        }
        
        public void setNote(final String note) {
            this.note = note;
        }
        
        public String getNote() {
            return this.note;
        }
        
        public void setStatus(final int status) {
            this.status = status;
        }
        
        public int getStatus() {
            return this.status;
        }
    }
}
