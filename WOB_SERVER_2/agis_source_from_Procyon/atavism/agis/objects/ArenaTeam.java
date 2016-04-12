// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;
import atavism.server.engine.OID;
import java.util.LinkedList;

public class ArenaTeam
{
    protected int teamNum;
    protected String teamName;
    protected boolean teamActive;
    protected int teamScore;
    protected int teamGoal;
    protected int minMembers;
    protected LinkedList<ArenaMember> teamMembers;
    protected LinkedList<ArenaMember> activeMembers;
    
    public void initialiseTeam(final int teamNum, final String teamName, final int teamGoal, final int minMembers) {
        this.teamNum = teamNum;
        this.teamName = teamName;
        this.teamGoal = teamGoal;
        this.minMembers = minMembers;
        this.teamActive = true;
        this.teamScore = 0;
        this.teamMembers = new LinkedList<ArenaMember>();
        this.activeMembers = new LinkedList<ArenaMember>();
    }
    
    public void addTeamMember(final OID oid, final String name, final String race, final int base_speed, final boolean useWeapons, final boolean useHealth) {
        final ArenaMember member = new ArenaMember(oid, name, this.teamNum, base_speed, useWeapons, useHealth);
        member.setProperty("race", race);
        this.teamMembers.add(member);
        this.activeMembers.add(member);
    }
    
    public boolean hasMember(final OID oid) {
        for (final ArenaMember member : this.teamMembers) {
            if (member.getOid().equals((Object)oid)) {
                return true;
            }
        }
        return false;
    }
    
    public ArenaMember getTeamMember(final OID oid) {
        for (final ArenaMember member : this.teamMembers) {
            if (member.getOid().equals((Object)oid)) {
                return member;
            }
        }
        return null;
    }
    
    public ArenaMember getTeamMember(final int pos) {
        if (this.teamMembers.size() >= pos) {
            return this.teamMembers.get(pos);
        }
        return null;
    }
    
    public ArrayList<OID> getTeamMembersOids() {
        final ArrayList<OID> oids = new ArrayList<OID>();
        for (final ArenaMember member : this.teamMembers) {
            oids.add(member.getOid());
        }
        return oids;
    }
    
    public ArenaMember removePlayer(final OID oid) {
        for (final ArenaMember member : this.activeMembers) {
            if (member.getOid().equals((Object)oid)) {
                this.activeMembers.remove(member);
                Log.debug("ARENA: removed player: " + oid + " from arena team");
                if (this.activeMembers.size() == 0) {
                    this.teamActive = false;
                }
                return member;
            }
        }
        return null;
    }
    
    public void playTeamDeathAnimations() {
        for (final ArenaMember member : this.teamMembers) {
            member.playDeathAnimation();
        }
    }
    
    public void playTeamVictoryAnimations() {
        for (final ArenaMember member : this.teamMembers) {
            member.playVictoryAnimation();
        }
    }
    
    public void updateScore(final int delta) {
        this.teamScore += delta;
    }
    
    public int getTeamSize() {
        return this.teamMembers.size();
    }
    
    public String getTeamName() {
        return this.teamName;
    }
    
    public void setTeamActive(final boolean active) {
        this.teamActive = active;
    }
    
    public boolean getTeamActive() {
        return this.teamActive;
    }
    
    public int getTeamScore() {
        return this.teamScore;
    }
    
    public void setTeamScore(final int teamScore) {
        this.teamScore = teamScore;
    }
    
    public int getTeamGoal() {
        return this.teamGoal;
    }
    
    public LinkedList<ArenaMember> getTeamMembers() {
        return this.teamMembers;
    }
    
    public LinkedList<ArenaMember> getActiveMembers() {
        return this.activeMembers;
    }
}
