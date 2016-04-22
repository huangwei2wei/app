// 
// Decompiled by Procyon v0.5.30
// 

package com.app.server.atavism.agis.objects;

import java.util.Iterator;
import com.app.server.atavism.server.engine.EnginePlugin;
import com.app.server.atavism.server.plugins.WorldManagerClient;
 
import com.app.server.atavism.server.engine.OID;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class ArenaQueue
{
	private static Logger log = Logger.getLogger("navmesh");
    protected int numTeams;
    protected int playersInQueue;
    protected boolean raceSpecific;
    protected QueueTeam[] teams;
    protected int arenaType;
    protected int arenaCategory;
    protected String arenaName;
    protected int arenaDifficulty;
    
    public ArenaQueue(final int numTeams, final boolean raceSpecific, final ArrayList<String>[] teamRaces, final int[] sizeReqs, final int arenaType, final String arenaName, final int category, final int difficulty) {
        this.numTeams = numTeams;
        this.raceSpecific = raceSpecific;
        this.teams = new QueueTeam[numTeams];
        for (int i = 0; i < numTeams; ++i) {
            this.teams[i] = new QueueTeam(sizeReqs[i], teamRaces[i]);
        }
        this.playersInQueue = 0;
        this.arenaType = arenaType;
        this.arenaName = arenaName;
        this.arenaCategory = category;
        this.arenaDifficulty = difficulty;
    }
    
    public boolean addPlayer(final OID oid, final String name) {
        if (this.isPlayerInQueue(oid)) {
            log.warn("ARENA QUEUE: player " + oid + " is already in this queue");
            return false;
        }
        if (this.raceSpecific) {
            final ArrayList<Integer> queuesToJoin = new ArrayList<Integer>();
            String playerRace = "";
            try {
                playerRace = (String)EnginePlugin.getObjectProperty(oid, WorldManagerClient.NAMESPACE, "race");
            }
            catch (NullPointerException e) {
            	log.warn("ARENA QUEUE: players race not defined. Player oid: " + oid);
            }
            for (int i = 0; i < this.numTeams; ++i) {
                if (this.teams[i].canJoinTeam(playerRace)) {
                    queuesToJoin.add(i);
                }
            }
            if (queuesToJoin.size() == 0) {
            	log.warn("ARENA QUEUE: player " + oid + " has no queue they can join.");
                return false;
            }
            int queueToJoin = queuesToJoin.get(0);
            int percentOfSmallestQueue = this.teams[queuesToJoin.get(0)].getPercentFull();
            for (int j = 0; j < this.numTeams; ++j) {
                final int percentFull = this.teams[j].getPercentFull();
                if (percentFull < percentOfSmallestQueue) {
                    queueToJoin = j;
                    percentOfSmallestQueue = percentFull;
                }
            }
            this.teams[queueToJoin].addPlayer(oid, name);
            ++this.playersInQueue;
        }
        else {
            int queueToJoin2 = 0;
            int percentOfSmallestQueue2 = this.teams[0].getPercentFull();
            for (int i = 0; i < this.numTeams; ++i) {
                final int percentFull2 = this.teams[i].getPercentFull();
                if (percentFull2 < percentOfSmallestQueue2) {
                    queueToJoin2 = i;
                    percentOfSmallestQueue2 = percentFull2;
                }
            }
            this.teams[queueToJoin2].addPlayer(oid, name);
            ++this.playersInQueue;
        }
        return true;
    }
    
    public void removePlayer(final OID oid) {
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i].hasPlayer(oid)) {
                this.teams[i].removePlayer(oid);
                --this.playersInQueue;
                log.debug("QUEUE: removed player " + oid + " from queue: " + this.arenaName);
            }
        }
    }
    
    public boolean isPlayerInQueue(final OID oid) {
        for (int i = 0; i < this.numTeams; ++i) {
            if (this.teams[i].hasPlayer(oid)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isQueueReady() {
    	log.debug("ARENA - checking if queue for arena " + this.arenaName + " is ready. NumTeams: " + this.numTeams);
        for (int i = 0; i < this.numTeams; ++i) {
        	log.debug("ARENA QUEUE: checking size of team " + i + ". size of queue: " + this.teams[i].getNumQueued());
            if (!this.teams[i].teamReady()) {
                return false;
            }
        }
        return true;
    }
    
    public int getNumPlayersInQueue() {
        return this.playersInQueue;
    }
    
    public ArrayList<QueueMember>[] getPlayersQueued() {
        final ArrayList[] playersQueued = new ArrayList[this.numTeams];
        for (int i = 0; i < this.numTeams; ++i) {
            playersQueued[i] = this.teams[i].getPlayersQueued();
        }
        return (ArrayList<QueueMember>[])playersQueued;
    }
    
    public int getArenaType() {
        return this.arenaType;
    }
    
    public void setArenaType(final int arenaType) {
        this.arenaType = arenaType;
    }
    
    public String getArenaName() {
        return this.arenaName;
    }
    
    public void setArenaName(final String arenaName) {
        this.arenaName = arenaName;
    }
    
    public int getArenaCategory() {
        return this.arenaCategory;
    }
    
    public void setArenaCategory(final int arenaCategory) {
        this.arenaCategory = arenaCategory;
    }
    
    public int getArenaDifficulty() {
        return this.arenaDifficulty;
    }
    
    public void setArenaDifficulty(final int arenaDifficulty) {
        this.arenaDifficulty = arenaDifficulty;
    }
    
    class QueueTeam
    {
        ArrayList<QueueMember> playersQueued;
        int sizeReq;
        ArrayList<String> racesAllowed;
        
        public QueueTeam(final int sizeReq, final ArrayList<String> racesAllowed) {
            this.sizeReq = sizeReq;
            this.racesAllowed = racesAllowed;
            this.playersQueued = new ArrayList<QueueMember>();
        }
        
        public boolean canJoinTeam(final String race) {
            for (final String raceAllowed : this.racesAllowed) {
                if (raceAllowed.equals(race)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean teamReady() {
            return this.playersQueued.size() >= this.sizeReq;
        }
        
        public boolean hasPlayer(final OID oid) {
            for (final QueueMember member : this.playersQueued) {
                if (member.getOid().equals((Object)oid)) {
                    return true;
                }
            }
            return false;
        }
        
        public void addPlayer(final OID oid, final String name) {
            final QueueMember member = new QueueMember(oid, name);
            this.playersQueued.add(member);
        }
        
        public void removePlayer(final OID oid) {
            for (final QueueMember member : this.playersQueued) {
                if (member.getOid().equals((Object)oid)) {
                    this.playersQueued.remove(member);
                }
            }
        }
        
        public int getPercentFull() {
            return this.playersQueued.size() / this.sizeReq;
        }
        
        public int getNumQueued() {
            return this.playersQueued.size();
        }
        
        public int getSizeReq() {
            return this.sizeReq;
        }
        
        public ArrayList<QueueMember> getPlayersQueued() {
            return this.playersQueued;
        }
    }
    
    public class QueueMember
    {
        protected OID oid;
        protected String name;
        protected String race;
        
        public QueueMember(final OID oid, final String name) {
            this.oid = oid;
            this.name = name;
            this.race = "";
            log.debug("QUEUE: added member with race: " + this.race);
        }
        
        public OID getOid() {
            return this.oid;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getRace() {
            return this.race;
        }
    }
}
