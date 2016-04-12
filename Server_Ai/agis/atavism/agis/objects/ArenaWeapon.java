// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.io.Serializable;

public class ArenaWeapon implements Serializable
{
    int uses;
    String type;
    int weaponID;
    int abilityID;
    int displayID;
    int cooldown;
    int damage;
    int range;
    private static final long serialVersionUID = 1L;
    public static final String ARENA_WEAPON_MELEE = "Melee";
    public static final String ARENA_WEAPON_RANGED = "Ranged";
    public static final String ARENA_WEAPON_UNARMED = "Unarmed";
    public static final String ARENA_WEAPON_SHIELD = "Shield";
    
    public ArenaWeapon() {
        this.weaponID = -1;
        this.abilityID = -1;
        this.displayID = -1;
        this.cooldown = 1000;
        this.damage = 1;
        this.range = 1000;
    }
    
    public ArenaWeapon(final int abilityID, final String type, final int displayID) {
        this.weaponID = -1;
        this.abilityID = -1;
        this.displayID = -1;
        this.cooldown = 1000;
        this.damage = 1;
        this.range = 1000;
        this.uses = 10;
        this.abilityID = abilityID;
        this.type = type;
        if (abilityID == 1) {
            this.cooldown = 1000;
            this.damage = 10;
            this.range = 6000;
        }
        else if (abilityID == 2) {
            this.cooldown = 1500;
            this.damage = 15;
            this.range = 20000;
        }
        else if (abilityID == 3) {
            this.cooldown = 1000;
            this.damage = 6;
            this.range = 6000;
        }
    }
    
    public int weaponUsed() {
        return --this.uses;
    }
    
    public int getUses() {
        return this.uses;
    }
    
    public void setUses(final int uses) {
        this.uses = uses;
    }
    
    public String getWeaponType() {
        return this.type;
    }
    
    public void setWeaponType(final String type) {
        this.type = type;
    }
    
    public int getWeaponID() {
        return this.weaponID;
    }
    
    public void setWeaponID(final int weaponID) {
        this.weaponID = weaponID;
    }
    
    public int getDisplayID() {
        return this.displayID;
    }
    
    public void setDisplayID(final int displayID) {
        this.displayID = displayID;
    }
    
    public int getAbilityID() {
        return this.abilityID;
    }
    
    public int getCooldown() {
        return this.cooldown;
    }
    
    public void setCooldown(final int cooldown) {
        this.cooldown = cooldown;
    }
    
    public int getDamage() {
        return this.damage;
    }
    
    public void setDamage(final int damage) {
        this.damage = damage;
    }
    
    public int getRange() {
        return this.range;
    }
    
    public void setRange(final int range) {
        this.range = range;
    }
}
