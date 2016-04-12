// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import atavism.agis.plugins.AgisInventoryClient;
import atavism.agis.plugins.ArenaPlugin;
import atavism.server.util.Log;
import atavism.msgsys.Message;
import atavism.server.engine.Engine;
import atavism.agis.plugins.CombatClient;
import atavism.agis.database.ArenaDatabase;
import atavism.agis.util.ExtendedCombatMessages;
import atavism.server.engine.EnginePlugin;
import atavism.server.plugins.WorldManagerClient;
import java.util.LinkedList;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import atavism.server.engine.OID;
import java.io.Serializable;

public class ArenaStats implements Serializable
{
    OID oid;
    String name;
    int level;
    int experience;
    int experienceRequired;
    int wins;
    int losses;
    int totalKills;
    int totalDeaths;
    int objectsConsumed;
    HashMap<Integer, ArenaTypeStats> arenaTypesMap;
    HashMap<Integer, ArenaSubTypeStats> arenaSubTypesMap;
    protected static final int initialRating = 5000;
    private static final long serialVersionUID = 1L;
    
    public ArenaStats(final OID oid, final String name) {
        this.totalKills = 0;
        this.totalDeaths = 0;
        this.oid = oid;
        this.totalKills = 0;
        this.totalDeaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.name = name;
        this.objectsConsumed = 0;
        this.arenaTypesMap = new HashMap<Integer, ArenaTypeStats>();
        this.arenaSubTypesMap = new HashMap<Integer, ArenaSubTypeStats>();
    }
    
    public void createDefaultStats() {
        this.level = 1;
        this.experience = 0;
        this.experienceRequired = 1500;
    }
    
    private int getGamesPlayed(final OID playerOid) {
        int gamesPlayed = 0;
        final Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(12, 0);
        final long startOfDay = cal.getTimeInMillis();
        final Date d = new Date();
        final long currentTime = d.getTime();
        final LinkedList<Long> lastPlayed = (LinkedList<Long>)EnginePlugin.getObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "lastGames");
        for (int k = 0; k < lastPlayed.size(); ++k) {
            if (lastPlayed.get(k) > startOfDay) {
                ++gamesPlayed;
            }
        }
        lastPlayed.addFirst(currentTime);
        if (lastPlayed.size() > 3) {
            lastPlayed.removeLast();
        }
        EnginePlugin.setObjectProperty(playerOid, WorldManagerClient.NAMESPACE, "lastGames", (Serializable)lastPlayed);
        return gamesPlayed;
    }
    
    public void updateStats(final int arenaType, final int arenaSubType, final int kills, final int deaths, final boolean wonArena, final int expAwarded, final int ratingAdjustment) {
        this.totalKills += kills;
        this.totalDeaths += deaths;
        if (wonArena) {
            ++this.wins;
        }
        else {
            ++this.losses;
        }
        this.experience += expAwarded;
        ExtendedCombatMessages.sendCombatText(this.oid, "+" + expAwarded + "xp", 17);
        final int gamesPlayed = this.getGamesPlayed(this.oid);
        if (gamesPlayed == 0) {
            final int expBonus = 1000;
            this.experience += expBonus;
            ExtendedCombatMessages.sendCombatText(this.oid, "1st Daily Game: +" + expBonus + "xp", 17);
        }
        else if (gamesPlayed == 1) {
            final int expBonus = 500;
            this.experience += expBonus;
            ExtendedCombatMessages.sendCombatText(this.oid, "2nd Daily Game: +" + expBonus + "xp", 17);
        }
        else if (gamesPlayed == 2) {
            final int expBonus = 200;
            this.experience += expBonus;
            ExtendedCombatMessages.sendCombatText(this.oid, "3rd Daily Game: +" + expBonus + "xp", 17);
        }
        if (this.experience > this.experienceRequired) {
            this.levelUp();
        }
        final ArenaDatabase aDB = new ArenaDatabase();
        aDB.updateArenaStats(this);
        this.sendArenaStatUpdate();
        final ArenaTypeStats typeStats = this.getArenaTypeStats(arenaType);
        typeStats.updateStats(kills, deaths, wonArena, expAwarded, ratingAdjustment);
        aDB.updateArenaTypeStats(this.oid, typeStats);
        final ArenaSubTypeStats subTypeStats = this.getArenaSubTypeStats(arenaType, arenaSubType);
        subTypeStats.updateStats(kills, deaths, wonArena, expAwarded, ratingAdjustment);
        aDB.updateArenaSubTypeStats(this.oid, subTypeStats);
    }
    
    public void alterExp(final int delta) {
        this.experience += delta;
        ExtendedCombatMessages.sendCombatText(this.oid, "+" + delta + "xp", 17);
        if (this.experience > this.experienceRequired) {
            this.levelUp();
        }
        final ArenaDatabase aDB = new ArenaDatabase();
        aDB.updateArenaStats(this);
        this.sendArenaStatUpdate();
    }
    
    private void levelUp() {
        ExtendedCombatMessages.sendCombatText(this.oid, "Level Up!", 17);
        ++this.level;
        this.experience -= this.experienceRequired;
        this.experienceRequired += this.level * 350;
        if (this.experience > this.experienceRequired) {
            this.levelUp();
        }
    }
    
    public void sendArenaStatUpdate() {
        EnginePlugin.setObjectPropertyNoResponse(this.oid, CombatClient.NAMESPACE, "level", (Serializable)this.level);
        final WorldManagerClient.TargetedPropertyMessage tPropMsg = new WorldManagerClient.TargetedPropertyMessage(this.oid, this.oid);
        tPropMsg.setProperty("arena_exp", (Serializable)this.experience);
        tPropMsg.setProperty("arena_exp_req", (Serializable)this.experienceRequired);
        Engine.getAgent().sendBroadcast((Message)tPropMsg);
        EnginePlugin.setObjectPropertyNoResponse(this.oid, WorldManagerClient.NAMESPACE, "arena_exp", (Serializable)this.experience);
        EnginePlugin.setObjectPropertyNoResponse(this.oid, WorldManagerClient.NAMESPACE, "arena_exp_req", (Serializable)this.experienceRequired);
    }
    
    public ArenaTypeStats getArenaTypeStats(final int arenaType) {
        if (!this.arenaTypesMap.containsKey(arenaType)) {
            final ArenaDatabase aDB = new ArenaDatabase();
            ArenaTypeStats typeStats = new ArenaTypeStats(arenaType);
            typeStats = aDB.loadArenaTypeStats(this.oid, typeStats);
            this.arenaTypesMap.put(arenaType, typeStats);
        }
        return this.arenaTypesMap.get(arenaType);
    }
    
    public ArenaSubTypeStats getArenaSubTypeStats(final int arenaType, final int arenaSubType) {
        if (!this.arenaSubTypesMap.containsKey(arenaSubType)) {
            final ArenaDatabase aDB = new ArenaDatabase();
            ArenaSubTypeStats typeStats = new ArenaSubTypeStats(arenaType, arenaSubType);
            typeStats = aDB.loadArenaSubTypeStats(this.oid, typeStats);
            this.arenaSubTypesMap.put(arenaType, typeStats);
        }
        return this.arenaSubTypesMap.get(arenaType);
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public void setOid(final OID oid) {
        this.oid = oid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public void setLevel(final int level) {
        this.level = level;
    }
    
    public int getExperience() {
        return this.experience;
    }
    
    public void setExperience(final int experience) {
        this.experience = experience;
    }
    
    public int getExperienceRequired() {
        return this.experienceRequired;
    }
    
    public void setExperienceRequired(final int experienceRequired) {
        this.experienceRequired = experienceRequired;
    }
    
    public int getWins() {
        return this.wins;
    }
    
    public void setWins(final int wins) {
        this.wins = wins;
    }
    
    public int getLosses() {
        return this.losses;
    }
    
    public void setLosses(final int losses) {
        this.losses = losses;
    }
    
    public int getTotalKills() {
        return this.totalKills;
    }
    
    public void setTotalKills(final int totalKills) {
        this.totalKills = totalKills;
    }
    
    public int getTotalDeaths() {
        return this.totalDeaths;
    }
    
    public void setTotalDeaths(final int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }
    
    public int getObjectsConsumed() {
        return this.objectsConsumed;
    }
    
    public void setObjectsConsumed(final int objectsConsumed) {
        this.objectsConsumed = objectsConsumed;
    }
    
    public HashMap<Integer, ArenaTypeStats> getArenaTypesMap() {
        return this.arenaTypesMap;
    }
    
    public void setArenaTypesMap(final HashMap<Integer, ArenaTypeStats> arenaTypesMap) {
        this.arenaTypesMap = arenaTypesMap;
    }
    
    public HashMap<Integer, ArenaSubTypeStats> getArenaSubTypesMap() {
        return this.arenaSubTypesMap;
    }
    
    public void setArenaSubTypesMap(final HashMap<Integer, ArenaSubTypeStats> arenaSubTypesMap) {
        this.arenaSubTypesMap = arenaSubTypesMap;
    }
    
    @Override
    public String toString() {
        return "Arena Stats: character " + this.name + " has kills: " + this.totalKills + " deaths: " + this.totalDeaths;
    }
    
    public static HashMap<OID, Integer> CalculateRatings(final int arenaType, final int arenaSubType, final ArenaTeam[] teams, final int winningTeam, final HashMap<Integer, Integer> victoryPayments, final HashMap<Integer, Integer> defeatPayments, final int victoryExp, final int defeatExp) {
        final HashMap<OID, Integer> ratingAdjustments = new HashMap<OID, Integer>();
        final int numTeams = teams.length;
        final int[] avgRatings = new int[numTeams];
        Log.debug("ARENA: rating calcs 1");
        for (int i = 0; i < numTeams; ++i) {
            int avgRating = 0;
            for (final ArenaMember member : teams[i].getTeamMembers()) {
                final ArenaStats stats = ArenaPlugin.getPlayerArenaStats(member.getOid());
                final ArenaTypeStats typeStats = stats.getArenaTypeStats(arenaType);
                avgRating += typeStats.getRating();
            }
            avgRatings[i] = avgRating / teams[i].getTeamSize();
        }
        Log.debug("ARENA: rating calcs 2");
        int losersRating = 0;
        for (int j = 0; j < avgRatings.length; ++j) {
            if (j != winningTeam) {
                losersRating += avgRatings[j];
            }
        }
        losersRating /= avgRatings.length - 1;
        int ratingDif = 0;
        if (winningTeam != -1) {
            ratingDif = losersRating - avgRatings[winningTeam];
        }
        int ratingAdjustment = ratingDif / 25 + 15;
        if (winningTeam == -1) {
            ratingAdjustment = 0;
        }
        Log.debug("ARENA: rating calcs 3 with arena adjustment: " + ratingAdjustment);
        for (int k = 0; k < numTeams; ++k) {
            for (final ArenaMember member2 : teams[k].getTeamMembers()) {
                final OID oid = member2.getOid();
                int expRewarded = 0;
                boolean wonArena = false;
                int ratingDelta = 0;
                if (k == winningTeam) {
                    for (final int currency : victoryPayments.keySet()) {
                        AgisInventoryClient.alterCurrency(oid, currency, victoryPayments.get(currency));
                        final String message = "You have received " + victoryPayments.get(currency) + " Coins";
                        WorldManagerClient.sendObjChatMsg(oid, 2, message);
                    }
                    expRewarded = victoryExp;
                    wonArena = true;
                    ratingDelta = ratingAdjustment;
                    ratingAdjustments.put(oid, ratingDelta);
                }
                else {
                    for (final int currency : defeatPayments.keySet()) {
                        AgisInventoryClient.alterCurrency(oid, currency, defeatPayments.get(currency));
                        final String message = "You have received " + defeatPayments.get(currency) + " Coins";
                        WorldManagerClient.sendObjChatMsg(oid, 2, message);
                    }
                    expRewarded = defeatExp;
                    ratingDelta = -ratingAdjustment;
                    ratingAdjustments.put(oid, ratingDelta);
                }
                ArenaPlugin.updateArenaStats(arenaType, arenaSubType, oid, member2.getKills(), member2.getDeaths(), wonArena, expRewarded, ratingDelta);
            }
        }
        Log.debug("ARENA: rating calcs 4");
        return ratingAdjustments;
    }
    
    public class ArenaTypeStats
    {
        int arenaType;
        int wins;
        int losses;
        int rating;
        int kills;
        int deaths;
        int objectsConsumed;
        
        public ArenaTypeStats(final int arenaType) {
            this.arenaType = arenaType;
            this.kills = 0;
            this.deaths = 0;
            this.wins = 0;
            this.losses = 0;
            this.rating = 5000;
        }
        
        public void updateStats(final int kills, final int deaths, final boolean wonArena, final int expAwarded, final int ratingAdjustment) {
            this.kills += kills;
            this.deaths += deaths;
            if (wonArena) {
                ++this.wins;
            }
            else {
                ++this.losses;
            }
            this.rating += ratingAdjustment;
        }
        
        public int getArenaType() {
            return this.arenaType;
        }
        
        public void setArenaType(final int arenaType) {
            this.arenaType = arenaType;
        }
        
        public int getWins() {
            return this.wins;
        }
        
        public void setWins(final int wins) {
            this.wins = wins;
        }
        
        public int getLosses() {
            return this.losses;
        }
        
        public void setLosses(final int losses) {
            this.losses = losses;
        }
        
        public int getRating() {
            return this.rating;
        }
        
        public void setRating(final int rating) {
            this.rating = rating;
        }
        
        public int getKills() {
            return this.kills;
        }
        
        public void setKills(final int kills) {
            this.kills = kills;
        }
        
        public int getDeaths() {
            return this.deaths;
        }
        
        public void setDeaths(final int deaths) {
            this.deaths = deaths;
        }
    }
    
    public class ArenaSubTypeStats
    {
        int arenaType;
        int arenaSubType;
        int wins;
        int losses;
        int rating;
        int kills;
        int deaths;
        int objectsConsumed;
        
        public ArenaSubTypeStats(final int arenaType, final int arenaSubType) {
            this.arenaType = arenaType;
            this.arenaSubType = arenaSubType;
            this.kills = 0;
            this.deaths = 0;
            this.wins = 0;
            this.losses = 0;
            this.rating = 5000;
        }
        
        public void updateStats(final int kills, final int deaths, final boolean wonArena, final int expAwarded, final int ratingAdjustment) {
            this.kills += kills;
            this.deaths += deaths;
            if (wonArena) {
                ++this.wins;
            }
            else {
                ++this.losses;
            }
            this.rating += ratingAdjustment;
        }
        
        public int getArenaType() {
            return this.arenaType;
        }
        
        public void setArenaType(final int arenaType) {
            this.arenaType = arenaType;
        }
        
        public int getArenaSubType() {
            return this.arenaSubType;
        }
        
        public void setArenaSubType(final int arenaSubType) {
            this.arenaSubType = arenaSubType;
        }
        
        public int getWins() {
            return this.wins;
        }
        
        public void setWins(final int wins) {
            this.wins = wins;
        }
        
        public int getLosses() {
            return this.losses;
        }
        
        public void setLosses(final int losses) {
            this.losses = losses;
        }
        
        public int getRating() {
            return this.rating;
        }
        
        public void setRating(final int rating) {
            this.rating = rating;
        }
        
        public int getKills() {
            return this.kills;
        }
        
        public void setKills(final int kills) {
            this.kills = kills;
        }
        
        public int getDeaths() {
            return this.deaths;
        }
        
        public void setDeaths(final int deaths) {
            this.deaths = deaths;
        }
    }
}
