// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.HashMap;
import atavism.server.engine.OID;
import java.io.Serializable;

public class SpecificArenaStats implements Serializable
{
    OID oid;
    String name;
    int totalKills;
    int totalDeaths;
    int rating;
    int bestRating;
    HashMap<String, Integer> killsByDay;
    HashMap<String, Integer> deathsByDay;
    private static final long serialVersionUID = 1L;
    
    public SpecificArenaStats() {
        this.totalKills = 0;
        this.totalDeaths = 0;
        this.killsByDay = new HashMap<String, Integer>();
        this.deathsByDay = new HashMap<String, Integer>();
    }
    
    public SpecificArenaStats(final OID oid, final String name, final int startingRating) {
        this.totalKills = 0;
        this.totalDeaths = 0;
        this.killsByDay = new HashMap<String, Integer>();
        this.deathsByDay = new HashMap<String, Integer>();
        this.oid = oid;
        this.name = name;
        this.rating = startingRating;
        this.bestRating = startingRating;
    }
    
    public void addKill() {
        ++this.totalKills;
    }
    
    public void addDeath() {
        ++this.totalDeaths;
    }
    
    public void updateStats(final int newRating, final int newBest, final int kills, final int deaths) {
        this.rating = newRating;
        this.bestRating = newBest;
        this.totalKills += kills;
        this.totalDeaths += deaths;
    }
    
    public OID getOid() {
        return this.oid;
    }
    
    public void setOid(final OID oid) {
        this.oid = oid;
    }
    
    public int getTotalKills() {
        return this.totalKills;
    }
    
    public void setTotalKills(final int totalKills) {
        this.totalKills = totalKills;
    }
    
    public int getRating() {
        return this.rating;
    }
    
    public void setRating(final int rating) {
        this.rating = rating;
    }
    
    public int getBestRating() {
        return this.bestRating;
    }
    
    public void setBestRating(final int bestRating) {
        this.bestRating = bestRating;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getTotalDeaths() {
        return this.totalDeaths;
    }
    
    public void setTotalDeaths(final int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }
    
    public HashMap<String, Integer> getKillsByDay() {
        return this.killsByDay;
    }
    
    public void setKillsByDay(final HashMap<String, Integer> killsByDay) {
        this.killsByDay = killsByDay;
    }
    
    public HashMap<String, Integer> getDeathsByDay() {
        return this.deathsByDay;
    }
    
    public void setDeathsByDay(final HashMap<String, Integer> deathsByDay) {
        this.deathsByDay = deathsByDay;
    }
    
    @Override
    public String toString() {
        return "Arena Stats: character " + this.name + " has rating: " + this.rating + "; kills: " + this.totalKills + " deaths: " + this.totalDeaths;
    }
}
