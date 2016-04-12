// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import atavism.server.util.Log;
import atavism.agis.objects.ArenaStats;
import atavism.server.engine.OID;

public class ArenaDatabase
{
    protected static ArenaQueries queries;
    protected static final String ARENA_TYPE_TABLE = "arena_type_stats";
    protected static final String ARENA_SUB_TABLE = "arena_sub_stats";
    
    static {
        ArenaDatabase.queries = new ArenaQueries();
    }
    
    public ArenaStats loadArenaStats(final OID characterOid, final String characterName) {
        try {
            Log.debug("ARENADB: attempting to load arena stats for: " + characterOid);
            final String table = "base_arena_stats";
            final String selectString = "SELECT * FROM " + table + " where player_oid=" + characterOid.toLong();
            final ResultSet rs = ArenaDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                boolean entryExists = false;
                if (rs.next()) {
                    final ArenaStats arenaStats = new ArenaStats(characterOid, characterName);
                    arenaStats.setLevel(rs.getInt("level"));
                    arenaStats.setExperience(rs.getInt("exp"));
                    arenaStats.setExperienceRequired(rs.getInt("exp_required"));
                    arenaStats.setWins(rs.getInt("wins"));
                    arenaStats.setLosses(rs.getInt("losses"));
                    arenaStats.setTotalKills(rs.getInt("kills"));
                    arenaStats.setTotalDeaths(rs.getInt("deaths"));
                    arenaStats.setObjectsConsumed(rs.getInt("objects_used"));
                    entryExists = true;
                    Log.debug("ARENA: got arena stats for character " + characterOid);
                    return arenaStats;
                }
                if (!entryExists) {
                    return this.createArenaStats(characterOid, characterName);
                }
            }
        }
        catch (SQLException ex) {}
        return null;
    }
    
    public ArenaStats createArenaStats(final OID characterOid, final String characterName) {
        final ArenaStats arenaStats = new ArenaStats(characterOid, characterName);
        arenaStats.createDefaultStats();
        Log.debug("ARENADB: attempting to create arena stats for: " + characterOid);
        final String tableName = "base_arena_stats";
        final String columnNames = "player_oid,player_name,level,exp,exp_required,wins,losses,kills,deaths,objects_used";
        try {
            final PreparedStatement stmt = ArenaDatabase.queries.prepare("INSERT INTO " + tableName + " (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, characterOid.toLong());
            stmt.setString(2, characterName);
            stmt.setInt(3, arenaStats.getLevel());
            stmt.setInt(4, arenaStats.getExperience());
            stmt.setInt(5, arenaStats.getExperienceRequired());
            stmt.setInt(6, arenaStats.getWins());
            stmt.setInt(7, arenaStats.getLosses());
            stmt.setInt(8, arenaStats.getTotalKills());
            stmt.setInt(9, arenaStats.getTotalDeaths());
            stmt.setInt(10, arenaStats.getObjectsConsumed());
            ArenaDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException e) {
            return null;
        }
        return arenaStats;
    }
    
    public int updateArenaStats(final ArenaStats arenaStats) {
        Log.debug("Writing arena stats data to database");
        final String tableName = "base_arena_stats";
        int updated;
        try {
            final PreparedStatement stmt = ArenaDatabase.queries.prepare("UPDATE " + tableName + " set level=?, exp=?, exp_required=?, " + "wins=?, losses=?, kills=?, deaths=?, objects_used=? where player_oid=?");
            stmt.setInt(1, arenaStats.getLevel());
            stmt.setInt(2, arenaStats.getExperience());
            stmt.setInt(3, arenaStats.getExperienceRequired());
            stmt.setInt(4, arenaStats.getWins());
            stmt.setInt(5, arenaStats.getLosses());
            stmt.setInt(6, arenaStats.getTotalKills());
            stmt.setInt(7, arenaStats.getTotalDeaths());
            stmt.setInt(8, arenaStats.getObjectsConsumed());
            stmt.setLong(9, arenaStats.getOid().toLong());
            Log.debug("ARENADB: updating stats with statement: " + stmt.toString());
            updated = ArenaDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote arena stats data to database");
        return updated;
    }
    
    public ArenaStats.ArenaTypeStats loadArenaTypeStats(final OID characterOid, final ArenaStats.ArenaTypeStats arenaStats) {
        try {
            Log.debug("ARENADB: attempting to load arena type stats for: " + characterOid);
            final String selectString = "SELECT * FROM arena_type_stats where player_oid=" + characterOid.toLong() + " and arena_type=" + arenaStats.getArenaType();
            final ResultSet rs = ArenaDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                boolean entryExists = false;
                if (rs.next()) {
                    Log.debug("ARENA: got arena type stats for character " + characterOid);
                    arenaStats.setWins(rs.getInt("wins"));
                    arenaStats.setLosses(rs.getInt("losses"));
                    arenaStats.setKills(rs.getInt("kills"));
                    arenaStats.setDeaths(rs.getInt("deaths"));
                    arenaStats.setRating(rs.getInt("rating"));
                    entryExists = true;
                    return arenaStats;
                }
                if (!entryExists) {
                    Log.debug("ARENA: could not find arena type stats for character " + characterOid);
                    return this.createArenaTypeStats(characterOid, arenaStats);
                }
            }
        }
        catch (SQLException ex) {}
        return null;
    }
    
    public ArenaStats.ArenaTypeStats createArenaTypeStats(final OID characterOid, final ArenaStats.ArenaTypeStats arenaStats) {
        Log.debug("ARENADB: attempting to create arena type stats for: " + characterOid);
        final String columnNames = "player_oid,arena_type,wins,losses,rating,kills,deaths";
        try {
            final PreparedStatement stmt = ArenaDatabase.queries.prepare("INSERT INTO arena_type_stats (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, characterOid.toLong());
            stmt.setInt(2, arenaStats.getArenaType());
            stmt.setInt(3, arenaStats.getWins());
            stmt.setInt(4, arenaStats.getLosses());
            stmt.setInt(5, arenaStats.getRating());
            stmt.setInt(6, arenaStats.getKills());
            stmt.setInt(7, arenaStats.getDeaths());
            ArenaDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException e) {
            return null;
        }
        return arenaStats;
    }
    
    public int updateArenaTypeStats(final OID characterOid, final ArenaStats.ArenaTypeStats arenaStats) {
        Log.debug("Writing arena type stats data to database");
        int updated;
        try {
            final PreparedStatement stmt = ArenaDatabase.queries.prepare("UPDATE arena_type_stats set wins=?, losses=?, rating=?, kills=?, deaths=? where player_oid=? and arena_type=?");
            stmt.setInt(1, arenaStats.getWins());
            stmt.setInt(2, arenaStats.getLosses());
            stmt.setInt(3, arenaStats.getRating());
            stmt.setInt(4, arenaStats.getKills());
            stmt.setInt(5, arenaStats.getDeaths());
            stmt.setLong(6, characterOid.toLong());
            stmt.setInt(7, arenaStats.getArenaType());
            Log.debug("ARENADB: updating stats with statement: " + stmt.toString());
            updated = ArenaDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote arena stats data to database");
        return updated;
    }
    
    public ArenaStats.ArenaSubTypeStats loadArenaSubTypeStats(final OID characterOid, final ArenaStats.ArenaSubTypeStats arenaStats) {
        try {
            Log.debug("ARENADB: attempting to load arena type stats for: " + characterOid);
            final String selectString = "SELECT * FROM arena_sub_stats where player_oid=" + characterOid.toLong() + " and arena_sub_type=" + arenaStats.getArenaSubType();
            final ResultSet rs = ArenaDatabase.queries.executeSelect(selectString);
            if (rs != null) {
                boolean entryExists = false;
                if (rs.next()) {
                    Log.debug("ARENA: got arena type stats for character " + characterOid);
                    arenaStats.setWins(rs.getInt("wins"));
                    arenaStats.setLosses(rs.getInt("losses"));
                    arenaStats.setKills(rs.getInt("kills"));
                    arenaStats.setDeaths(rs.getInt("deaths"));
                    arenaStats.setRating(rs.getInt("rating"));
                    entryExists = true;
                    return arenaStats;
                }
                if (!entryExists) {
                    Log.debug("ARENA: could not find arena type stats for character " + characterOid);
                    return this.createArenaSubTypeStats(characterOid, arenaStats);
                }
            }
        }
        catch (SQLException ex) {}
        return null;
    }
    
    public ArenaStats.ArenaSubTypeStats createArenaSubTypeStats(final OID characterOid, final ArenaStats.ArenaSubTypeStats arenaStats) {
        Log.debug("ARENADB: attempting to create arena type stats for: " + characterOid);
        final String columnNames = "player_oid,arena_type,arena_sub_type,wins,losses,rating,kills,deaths";
        try {
            final PreparedStatement stmt = ArenaDatabase.queries.prepare("INSERT INTO arena_sub_stats (" + columnNames + ") values (?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, characterOid.toLong());
            stmt.setInt(2, arenaStats.getArenaType());
            stmt.setInt(3, arenaStats.getArenaSubType());
            stmt.setInt(4, arenaStats.getWins());
            stmt.setInt(5, arenaStats.getLosses());
            stmt.setInt(6, arenaStats.getRating());
            stmt.setInt(7, arenaStats.getKills());
            stmt.setInt(8, arenaStats.getDeaths());
            ArenaDatabase.queries.executeInsert(stmt);
        }
        catch (SQLException e) {
            return null;
        }
        return arenaStats;
    }
    
    public int updateArenaSubTypeStats(final OID characterOid, final ArenaStats.ArenaSubTypeStats arenaStats) {
        Log.debug("Writing arena type stats data to database");
        int updated;
        try {
            final PreparedStatement stmt = ArenaDatabase.queries.prepare("UPDATE arena_sub_stats set wins=?, losses=?, rating=?, kills=?, deaths=? where player_oid=? and arena_sub_type=?");
            stmt.setInt(1, arenaStats.getWins());
            stmt.setInt(2, arenaStats.getLosses());
            stmt.setInt(3, arenaStats.getRating());
            stmt.setInt(4, arenaStats.getKills());
            stmt.setInt(5, arenaStats.getDeaths());
            stmt.setLong(6, characterOid.toLong());
            stmt.setInt(7, arenaStats.getArenaSubType());
            Log.debug("ARENADB: updating stats with statement: " + stmt.toString());
            updated = ArenaDatabase.queries.executeUpdate(stmt);
        }
        catch (SQLException e) {
            return -1;
        }
        Log.debug("Wrote arena stats data to database");
        return updated;
    }
}
