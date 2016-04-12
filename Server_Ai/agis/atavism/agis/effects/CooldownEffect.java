// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.effects;

import java.util.Iterator;
import java.util.Set;
import atavism.agis.objects.CombatInfo;
import atavism.agis.util.EventMessageHelper;
import atavism.agis.core.Cooldown;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;
import atavism.agis.core.AgisEffect;

public class CooldownEffect extends AgisEffect
{
    static Random random;
    public int effectSkillType;
    public ArrayList<String> cooldownsToAlter;
    public Long cooldownOffset;
    private static final long serialVersionUID = 1L;
    
    static {
        CooldownEffect.random = new Random();
    }
    
    public CooldownEffect(final int id, final String name) {
        super(id, name);
        this.effectSkillType = 0;
        this.cooldownsToAlter = new ArrayList<String>();
        this.cooldownOffset = 0L;
    }
    
    @Override
    public void apply(final EffectState state) {
        super.apply(state);
        final Map<String, Integer> params = (Map<String, Integer>)state.getParams();
        final int result = params.get("result");
        this.effectSkillType = params.get("skillType");
        final CombatInfo target = state.getTarget();
        final CombatInfo source = state.getSource();
        String abilityEvent = "CombatCooldownExtended";
        switch (result) {
            case 3: {
                abilityEvent = "CombatMissed";
                break;
            }
            case 4: {
                abilityEvent = "CombatParried";
                break;
            }
            case 5: {
                abilityEvent = "CombatDodged";
                break;
            }
            default: {
                final Map<String, Cooldown.State> cooldowns = target.getCooldownMap();
                final Set<Map.Entry<String, Cooldown.State>> cooldownSet = cooldowns.entrySet();
                for (final Map.Entry<String, Cooldown.State> e : cooldownSet) {
                    final String cooldownName = e.getKey();
                    if (this.cooldownsToAlter.contains(cooldownName) || (this.cooldownsToAlter.contains("ALL") && !cooldownName.equals("GLOBAL"))) {
                        final Cooldown.State cState = e.getValue();
                        cState.timeAdjustment(this.cooldownOffset);
                    }
                }
                break;
            }
        }
        EventMessageHelper.SendCombatEvent(source.getOwnerOid(), target.getOwnerOid(), abilityEvent, -1, this.getID(), -1, -1);
    }
    
    @Override
    public void setEffectSkillType(final int type) {
        this.effectSkillType = type;
    }
    
    @Override
    public int GetEffectSkillType() {
        return this.effectSkillType;
    }
    
    public void addCooldownToAlter(final String cooldown) {
        this.cooldownsToAlter.add(cooldown);
    }
    
    public void setCooldownsToAlter(final ArrayList<String> cooldowns) {
        this.cooldownsToAlter = cooldowns;
    }
    
    public ArrayList<String> getCooldownsToAlter() {
        return this.cooldownsToAlter;
    }
    
    public void setCooldownOffset(final Long offset) {
        this.cooldownOffset = offset;
    }
    
    public Long GetCooldownOffset() {
        return this.cooldownOffset;
    }
}
