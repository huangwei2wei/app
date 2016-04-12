// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public class BaseStatDef extends AgisStatDef
{
    private int min;
    private int max;
    
    public BaseStatDef(final String name) {
        super(name);
        this.min = 0;
        this.max = 100000;
    }
    
    @Override
    public void update(final AgisStat stat, final CombatInfo info) {
        stat.max = this.max;
        stat.min = this.min;
        stat.setDirty(true);
        super.update(stat, info);
    }
    
    public void setMin(final int min) {
        this.min = min;
    }
    
    public void setMax(final int max) {
        this.max = max;
    }
}
