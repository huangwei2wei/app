// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

public class ResistanceStatDef extends AgisStatDef
{
    public ResistanceStatDef(final String name) {
        super(name);
    }
    
    @Override
    public void update(final AgisStat stat, final CombatInfo info) {
        stat.max = 80;
        stat.min = 0;
        stat.setDirty(true);
        super.update(stat, info);
    }
}
