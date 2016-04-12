// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Map;
import atavism.agis.core.AgisEffect;

public class ParryEffect extends AgisEffect
{
    protected String weapon;
    private static final long serialVersionUID = 1L;
    
    public ParryEffect(final int id, final String name) {
        super(id, name);
        this.weapon = "";
        this.isPeriodic(false);
        this.isPersistent(true);
    }
    
    public void setWeapon(final String weapon) {
        this.weapon = weapon;
    }
    
    public String getWeapon() {
        return this.weapon;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
    }
    
    @Override
    public void pulse(final EffectState state) {
        super.pulse(state);
    }
}
