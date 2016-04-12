// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.events;

import atavism.server.util.AORuntimeException;
import atavism.server.objects.Entity;
import atavism.agis.objects.AgisObject;
import atavism.server.network.ClientConnection;
import atavism.server.network.AOByteBuffer;
import atavism.agis.objects.AgisMob;
import atavism.server.engine.Event;

public class CombatEvent extends Event
{
    private String attackType;
    private AgisMob attacker;
    
    public CombatEvent() {
        this.attackType = null;
        this.attacker = null;
    }
    
    public CombatEvent(final AOByteBuffer buf, final ClientConnection con) {
        super(buf, con);
        this.attackType = null;
        this.attacker = null;
    }
    
    public CombatEvent(final AgisMob attacker, final AgisObject target, final String attackType) {
        super((Entity)target);
        this.attackType = null;
        this.attacker = null;
        this.setAttackType(attackType);
        this.setAttacker(attacker);
    }
    
    public String getName() {
        return "CombatEvent";
    }
    
    public AOByteBuffer toBytes() {
        throw new AORuntimeException("not implemented");
    }
    
    public void parseBytes(final AOByteBuffer buf) {
        throw new AORuntimeException("not implemented");
    }
    
    public void setAttacker(final AgisMob attacker) {
        this.attacker = attacker;
    }
    
    public AgisMob getAttacker() {
        return this.attacker;
    }
    
    public void setAttackType(final String attackType) {
        this.attackType = attackType;
    }
    
    public String getAttackType() {
        return this.attackType;
    }
}
