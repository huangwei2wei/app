// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.ArrayList;
import java.io.Serializable;

public class BehaviorTemplate implements Serializable
{
    int id;
    String name;
    String baseAction;
    boolean weaponsSheathed;
    int roamRadius;
    ArrayList<String> patrolMarkers;
    int patrolPause;
    ArrayList<Integer> patrolPauses;
    boolean hasCombat;
    int aggroRadius;
    ArrayList<Integer> startsQuests;
    ArrayList<Integer> endsQuests;
    ArrayList<Integer> startsDialogues;
    int merchantTable;
    int questOpenLoot;
    boolean isChest;
    int pickupItem;
    String otherUse;
    private static final long serialVersionUID = 1L;
    
    public BehaviorTemplate() {
        this.baseAction = "";
        this.weaponsSheathed = false;
        this.roamRadius = 0;
        this.patrolMarkers = new ArrayList<String>();
        this.patrolPause = 0;
        this.patrolPauses = new ArrayList<Integer>();
        this.hasCombat = false;
        this.aggroRadius = 0;
        this.startsQuests = new ArrayList<Integer>();
        this.endsQuests = new ArrayList<Integer>();
        this.startsDialogues = new ArrayList<Integer>();
        this.merchantTable = -1;
        this.questOpenLoot = -1;
        this.isChest = false;
        this.pickupItem = -1;
        this.otherUse = null;
    }
    
    public BehaviorTemplate(final int id, final String name) {
        this.baseAction = "";
        this.weaponsSheathed = false;
        this.roamRadius = 0;
        this.patrolMarkers = new ArrayList<String>();
        this.patrolPause = 0;
        this.patrolPauses = new ArrayList<Integer>();
        this.hasCombat = false;
        this.aggroRadius = 0;
        this.startsQuests = new ArrayList<Integer>();
        this.endsQuests = new ArrayList<Integer>();
        this.startsDialogues = new ArrayList<Integer>();
        this.merchantTable = -1;
        this.questOpenLoot = -1;
        this.isChest = false;
        this.pickupItem = -1;
        this.otherUse = null;
        this.id = id;
        this.name = name;
    }
    
    public int getID() {
        return this.id;
    }
    
    public void setID(final int id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getBaseAction() {
        return this.baseAction;
    }
    
    public void setBaseAction(final String baseAction) {
        this.baseAction = baseAction;
    }
    
    public boolean getWeaponsSheathed() {
        return this.weaponsSheathed;
    }
    
    public void setWeaponsSheathed(final boolean weaponsSheathed) {
        this.weaponsSheathed = weaponsSheathed;
    }
    
    public int getRoamRadius() {
        return this.roamRadius;
    }
    
    public void setRoamRadius(final int roamRadius) {
        this.roamRadius = roamRadius;
    }
    
    public ArrayList<String> getPatrolMarkers() {
        return this.patrolMarkers;
    }
    
    public void setPatrolMarkers(final ArrayList<String> patrolMarkers) {
        this.patrolMarkers = patrolMarkers;
    }
    
    public int getPatrolPause() {
        return this.patrolPause;
    }
    
    public void setPatrolPause(final int patrolPause) {
        this.patrolPause = patrolPause;
    }
    
    public ArrayList<Integer> getPatrolPauses() {
        return this.patrolPauses;
    }
    
    public void setPatrolPauses(final ArrayList<Integer> patrolPauses) {
        this.patrolPauses = patrolPauses;
    }
    
    public boolean getHasCombat() {
        return this.hasCombat;
    }
    
    public void setHasCombat(final boolean hasCombat) {
        this.hasCombat = hasCombat;
    }
    
    public int getAggroRadius() {
        return this.aggroRadius;
    }
    
    public void setAggroRadius(final int aggroRadius) {
        this.aggroRadius = aggroRadius;
    }
    
    public ArrayList<Integer> getStartsQuests() {
        return this.startsQuests;
    }
    
    public void setStartsQuests(final ArrayList<Integer> startsQuests) {
        this.startsQuests = startsQuests;
    }
    
    public ArrayList<Integer> getEndsQuests() {
        return this.endsQuests;
    }
    
    public void setEndsQuests(final ArrayList<Integer> endsQuests) {
        this.endsQuests = endsQuests;
    }
    
    public ArrayList<Integer> getStartsDialogues() {
        return this.startsDialogues;
    }
    
    public void setStartsDialogues(final ArrayList<Integer> startsDialogues) {
        this.startsDialogues = startsDialogues;
    }
    
    public int getMerchantTable() {
        return this.merchantTable;
    }
    
    public void setMerchantTable(final int merchantTable) {
        this.merchantTable = merchantTable;
    }
    
    public int getQuestOpenLoot() {
        return this.questOpenLoot;
    }
    
    public void setQuestOpenLoot(final int questOpenLoot) {
        this.questOpenLoot = questOpenLoot;
    }
    
    public boolean getIsChest() {
        return this.isChest;
    }
    
    public void setIsChest(final boolean isChest) {
        this.isChest = isChest;
    }
    
    public int getPickupItem() {
        return this.pickupItem;
    }
    
    public void setPickupItem(final int pickupItem) {
        this.pickupItem = pickupItem;
    }
    
    public String getOtherUse() {
        return this.otherUse;
    }
    
    public void setOtherUse(final String otherUse) {
        this.otherUse = otherUse;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.id) + ":" + this.name;
    }
}
