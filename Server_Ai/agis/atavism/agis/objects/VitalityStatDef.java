// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import atavism.agis.core.Agis;
import atavism.agis.core.AgisEffect;
import atavism.agis.plugins.CombatPlugin;
import atavism.server.util.Log;
import java.util.ArrayList;

public class VitalityStatDef extends AgisStatDef
{
    private int min;
    private int max;
    private String maxStat;
    private int shiftTarget;
    private String onMax;
    private String onMin;
    private int shiftValue;
    private int reverseShiftValue;
    private boolean isShiftPercent;
    private int shiftInterval;
    private ArrayList<StatShiftRequirement> shiftRequirements;
    
    public VitalityStatDef(final String name, final String maxStatName) {
        super(name);
        this.min = 0;
        this.max = 100;
        this.shiftValue = 0;
        this.reverseShiftValue = 0;
        this.isShiftPercent = false;
        this.shiftInterval = 2;
        this.shiftRequirements = new ArrayList<StatShiftRequirement>();
        this.maxStat = maxStatName;
    }
    
    public VitalityStatDef(final String name) {
        super(name);
        this.min = 0;
        this.max = 100;
        this.shiftValue = 0;
        this.reverseShiftValue = 0;
        this.isShiftPercent = false;
        this.shiftInterval = 2;
        this.shiftRequirements = new ArrayList<StatShiftRequirement>();
    }
    
    @Override
    public void update(final AgisStat stat, final CombatInfo info) {
        if (this.maxStat != null && !this.maxStat.isEmpty()) {
            final int statMax = info.statGetCurrentValue(this.maxStat);
            stat.max = statMax;
        }
        else {
            stat.max = this.max;
        }
        stat.min = this.min;
        if (info.dead()) {
            if (this.shiftValue > 0) {
                stat.setBaseValue(0);
            }
            else if (this.maxStat != null && !this.maxStat.isEmpty()) {
                final int statMax = info.statGetCurrentValue(this.maxStat);
                stat.setBaseValue(statMax);
            }
            else {
                stat.setBaseValue(this.max);
            }
        }
        stat.setDirty(true);
        super.update(stat, info);
    }
    
    @Override
    public void notifyFlags(final AgisStat stat, final CombatInfo info, final int oldFlags, final int newFlags) {
        if ((this.shiftTarget == 1 && !info.isUser()) || (this.shiftTarget == 2 && !info.isMob())) {
            return;
        }
        if (info.dead()) {
            return;
        }
        if (((oldFlags ^ newFlags) & 0x2) == 0x2) {
            Log.debug("STAT: stat max state changed");
            Log.debug("STAT: newFlags equals: " + (newFlags & 0x2));
            if ((newFlags & 0x2) == 0x2) {
                this.onMaxHit(stat, info);
            }
        }
        if (((oldFlags ^ newFlags) & 0x1) == 0x1 && (newFlags & 0x1) == 0x1) {
            this.onMinHit(stat, info);
        }
    }
    
    void onMaxHit(final AgisStat stat, final CombatInfo info) {
        Log.debug("STAT: onMax hit for stat: " + this.name + " with action: " + this.onMax);
        if (this.onMax != null && !this.onMax.isEmpty()) {
            if (this.onMax.equals("death")) {
                Log.debug("STAT: dealing death");
                CombatPlugin.handleDeath(info);
                stat.setBaseValue(0);
            }
            else if (this.onMax.startsWith("effect")) {
                int effectID = -1;
                final String[] vals = this.onMax.split(":");
                if (vals.length > 1) {
                    effectID = Integer.parseInt(vals[1]);
                }
                if (effectID != -1) {
                    final AgisEffect effect = (AgisEffect)Agis.EffectManager.get(effectID);
                    if (effect != null) {
                        final HashMap<String, Integer> params = new HashMap<String, Integer>();
                        params.put("result", 1);
                        params.put("skillType", -1);
                        params.put("hitRoll", 75);
                        AgisEffect.applyEffect(effect, info, info, -1, params);
                    }
                }
            }
        }
    }
    
    void onMinHit(final AgisStat stat, final CombatInfo info) {
        Log.debug("STAT: onMin hit for stat: " + this.name + " with action: " + this.onMin);
        if (this.onMin != null && !this.onMin.isEmpty()) {
            if (this.onMin.equals("death")) {
                Log.debug("STAT: dealing death");
                CombatPlugin.handleDeath(info);
                if (this.maxStat != null && !this.maxStat.isEmpty()) {
                    final int statMax = info.statGetCurrentValue(this.maxStat);
                    stat.setBaseValue(statMax);
                }
                else {
                    stat.setBaseValue(this.max);
                }
            }
            else if (this.onMin.startsWith("effect")) {
                int effectID = -1;
                final String[] vals = this.onMin.split(":");
                if (vals.length > 1) {
                    effectID = Integer.parseInt(vals[1]);
                }
                Log.debug("STAT: effectID: " + effectID);
                if (effectID != -1) {
                    final AgisEffect effect = (AgisEffect)Agis.EffectManager.get(effectID);
                    if (effect != null) {
                        final HashMap<String, Integer> params = new HashMap<String, Integer>();
                        params.put("result", 1);
                        params.put("skillType", -1);
                        params.put("hitRoll", 75);
                        AgisEffect.applyEffect(effect, info, info, -1, params);
                    }
                }
            }
        }
    }
    
    public int getShiftDirection(final AgisStat stat, final CombatInfo info) {
        int shiftDirection = 1;
        for (final StatShiftRequirement shiftReq : this.shiftRequirements) {
            Log.debug("STAT: " + this.name + " checking req: " + shiftReq.requirement + " with reqState: " + shiftReq.reqState + " against actual value: " + info.getBooleanProperty(shiftReq.requirement));
            if (shiftReq.reqState && !info.getBooleanProperty(shiftReq.requirement)) {
                if (!shiftReq.setReverse) {
                    return 0;
                }
                shiftDirection = -1;
            }
            else {
                if (shiftReq.reqState || !info.getBooleanProperty(shiftReq.requirement)) {
                    continue;
                }
                if (!shiftReq.setReverse) {
                    return 0;
                }
                shiftDirection = -1;
            }
        }
        if (shiftDirection == 1) {
            if (this.shiftValue == 0) {
                return 0;
            }
            if (this.shiftValue > 0 && stat.current >= stat.max) {
                this.onMaxHit(stat, info);
                return 0;
            }
            if (this.shiftValue < 0 && stat.current <= stat.min) {
                this.onMinHit(stat, info);
                return 0;
            }
        }
        else if (shiftDirection == -1) {
            if (this.reverseShiftValue == 0) {
                return 0;
            }
            if (this.reverseShiftValue > 0 && stat.current >= stat.max) {
                this.onMaxHit(stat, info);
                return 0;
            }
            if (this.reverseShiftValue < 0 && stat.current <= stat.min) {
                this.onMinHit(stat, info);
                return 0;
            }
        }
        return shiftDirection;
    }
    
    public boolean checkShiftTarget(final CombatInfo info) {
        return (this.shiftTarget != 1 || info.isUser()) && (this.shiftTarget != 2 || info.isMob());
    }
    
    public void setMin(final int min) {
        this.min = min;
    }
    
    public int getMin() {
        return this.min;
    }
    
    public void setMax(final int max) {
        this.max = max;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public void setShiftTarget(final int shiftTarget) {
        this.shiftTarget = shiftTarget;
    }
    
    public int getShiftTarget() {
        return this.shiftTarget;
    }
    
    public void setOnMaxHit(final String onMax) {
        this.onMax = onMax;
    }
    
    public String getOnMaxHit() {
        return this.onMax;
    }
    
    public void setOnMinHit(final String onMin) {
        this.onMin = onMin;
    }
    
    public String getOnMinHit() {
        return this.onMin;
    }
    
    public void setShiftInterval(final int interval) {
        this.shiftInterval = interval;
    }
    
    public int getShiftInterval() {
        return this.shiftInterval;
    }
    
    public void setShiftValue(final int shiftValue) {
        this.shiftValue = shiftValue;
    }
    
    public int getShiftValue() {
        return this.shiftValue;
    }
    
    public void setReverseShiftValue(final int reverseShiftValue) {
        this.reverseShiftValue = reverseShiftValue;
    }
    
    public int getReverseShiftValue() {
        return this.reverseShiftValue;
    }
    
    public void isShiftPercent(final boolean isShiftPercent) {
        this.isShiftPercent = isShiftPercent;
    }
    
    public boolean getIsShiftPercent() {
        return this.isShiftPercent;
    }
    
    public void addShiftRequirement(final String req, final boolean reqTrue, final boolean setReverse) {
        final StatShiftRequirement shiftReq = new StatShiftRequirement(req, reqTrue, setReverse);
        this.shiftRequirements.add(shiftReq);
    }
    
    class StatShiftRequirement
    {
        String requirement;
        boolean reqState;
        boolean setReverse;
        
        public StatShiftRequirement(final String req, final boolean reqState, final boolean setReverse) {
            this.requirement = req;
            this.reqState = reqState;
            this.setReverse = setReverse;
        }
    }
}
