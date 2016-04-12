// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

public class ArenaTemplate implements Serializable
{
    protected int typeID;
    protected int arenaType;
    protected String arenaName;
    protected int arenaCategory;
    protected int levelReq;
    protected ArrayList<ArenaTeam> teams;
    protected HashMap<String, Integer> resourceGoals;
    protected int length;
    protected int victoryCondition;
    protected boolean raceSpecific;
    protected int numRounds;
    protected ArrayList<Integer>[] spawnIDs;
    protected HashMap<Integer, Integer> victoryPayment;
    protected HashMap<Integer, Integer> defeatPayment;
    protected int victoryExp;
    protected int defeatExp;
    protected boolean useWeapons;
    protected String worldFile;
    private static final long serialVersionUID = 1L;
    
    public ArenaTemplate(final int typeID, final int arenaType, final int arenaCategory, final int duration, final int condition, final String worldFile, final String arenaName, final boolean raceSpecific, final int numRounds, final ArrayList<ArrayList<Integer>> spawns) {
        Log.debug("ARENA TEMPLATE: starting arenaTemplate creation");
        this.length = duration;
        this.victoryCondition = condition;
        this.typeID = typeID;
        this.arenaType = arenaType;
        this.arenaCategory = arenaCategory;
        this.worldFile = worldFile;
        this.arenaName = arenaName;
        this.raceSpecific = raceSpecific;
        this.numRounds = numRounds;
        this.useWeapons = false;
        this.teams = new ArrayList<ArenaTeam>();
        Log.debug("ARENA TEMPLATE: finished arenaTemplate creation");
    }
    
    public void addTeam(final String name, final int size, final String race, final int goal) {
        final ArenaTeam team = new ArenaTeam(name, size, race, goal);
        this.teams.add(team);
    }
    
    public int getNumTeams() {
        return this.teams.size();
    }
    
    public int getTeamSize(final int team) {
        return this.teams.get(team).getSize();
    }
    
    public ArrayList<String>[] getTeamRaces() {
        final ArrayList[] teamRaces = new ArrayList[this.teams.size()];
        for (int i = 0; i < this.teams.size(); ++i) {
            teamRaces[i] = this.teams.get(i).getRaces();
        }
        return (ArrayList<String>[])teamRaces;
    }
    
    public int[] getTeamSizes() {
        final int[] teamSizes = new int[this.teams.size()];
        for (int i = 0; i < this.teams.size(); ++i) {
            teamSizes[i] = this.teams.get(i).getSize();
        }
        return teamSizes;
    }
    
    public String[] getTeamNames() {
        final String[] teamNames = new String[this.teams.size()];
        for (int i = 0; i < this.teams.size(); ++i) {
            teamNames[i] = this.teams.get(i).getName();
        }
        return teamNames;
    }
    
    public int[] getTeamGoals() {
        final int[] teamGoals = new int[this.teams.size()];
        for (int i = 0; i < this.teams.size(); ++i) {
            teamGoals[i] = this.teams.get(i).getGoal();
        }
        return teamGoals;
    }
    
    public int getTypeID() {
        return this.typeID;
    }
    
    public void setTypeID(final int typeID) {
        this.typeID = typeID;
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
    
    public int getLevelReq() {
        return this.levelReq;
    }
    
    public void setLevelReq(final int levelReq) {
        this.levelReq = levelReq;
    }
    
    public ArrayList<ArenaTeam> getTeams() {
        return this.teams;
    }
    
    public void setTeams(final ArrayList<ArenaTeam> teams) {
        this.teams = teams;
    }
    
    public HashMap<String, Integer> getResourceGoals() {
        return this.resourceGoals;
    }
    
    public void setResourceGoals(final HashMap<String, Integer> resourceGoals) {
        this.resourceGoals = resourceGoals;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setLength(final int length) {
        this.length = length;
    }
    
    public int getVictoryCondition() {
        return this.victoryCondition;
    }
    
    public void setVictoryCondition(final int victoryCondition) {
        this.victoryCondition = victoryCondition;
    }
    
    public boolean getRaceSpecific() {
        return this.raceSpecific;
    }
    
    public void setRaceSpecific(final boolean raceSpecific) {
        this.raceSpecific = raceSpecific;
    }
    
    public int getNumRounds() {
        return this.numRounds;
    }
    
    public void setNumRounds(final int numRounds) {
        this.numRounds = numRounds;
    }
    
    public ArrayList<Integer>[] getSpawnIDs() {
        return this.spawnIDs;
    }
    
    public void setSpawnIDs(final ArrayList<Integer>[] spawnIDs) {
        this.spawnIDs = spawnIDs;
    }
    
    public HashMap<Integer, Integer> getVictoryPayment() {
        return this.victoryPayment;
    }
    
    public void setVictoryPayment(final HashMap<Integer, Integer> victoryPayment) {
        this.victoryPayment = victoryPayment;
    }
    
    public HashMap<Integer, Integer> getDefeatPayment() {
        return this.defeatPayment;
    }
    
    public void setDefeatPayment(final HashMap<Integer, Integer> defeatPayment) {
        this.defeatPayment = defeatPayment;
    }
    
    public int getVictoryExp() {
        return this.victoryExp;
    }
    
    public void setVictoryExp(final int victoryExp) {
        this.victoryExp = victoryExp;
    }
    
    public int getDefeatExp() {
        return this.defeatExp;
    }
    
    public void setDefeatExp(final int defeatExp) {
        this.defeatExp = defeatExp;
    }
    
    public boolean getUseWeapons() {
        return this.useWeapons;
    }
    
    public void setUseWeapons(final boolean useWeapons) {
        this.useWeapons = useWeapons;
    }
    
    public String getWorldFile() {
        return this.worldFile;
    }
    
    public void setWorldFile(final String worldFile) {
        this.worldFile = worldFile;
    }
    
    public class ArenaTeam
    {
        String name;
        int size;
        ArrayList<String> races;
        int goal;
        
        public ArenaTeam(final String name, final int size, final String race, final int goal) {
            this.name = name;
            this.size = size;
            this.races = new ArrayList<String>();
            if (race != null && race.equals("")) {
                this.races.add(race);
            }
            this.goal = goal;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public int getSize() {
            return this.size;
        }
        
        public void setSize(final int size) {
            this.size = size;
        }
        
        public ArrayList<String> getRaces() {
            return this.races;
        }
        
        public void setRaces(final ArrayList<String> race) {
            this.races = race;
        }
        
        public int getGoal() {
            return this.goal;
        }
        
        public void setGoal(final int goal) {
            this.goal = goal;
        }
    }
}
