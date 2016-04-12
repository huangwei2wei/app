// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Map;
import atavism.agis.core.AgisEffect;

public class ResultEffect extends AgisEffect
{
    protected int result;
    private static final long serialVersionUID = 1L;
    
    public ResultEffect(final int id, final String name) {
        super(id, name);
        this.result = 0;
        this.isPeriodic(false);
        this.isPersistent(true);
    }
    
    public void setResult(final int res) {
        this.result = res;
    }
    
    public int getResult() {
        return this.result;
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
