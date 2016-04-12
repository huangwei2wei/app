// 
// Decompiled by Procyon v0.5.30
// 

package atavism.agis.objects;

import atavism.server.network.AOByteBuffer;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import atavism.server.network.ClientSerializable;
import java.io.Serializable;

public class AgisStat implements Serializable, ClientSerializable
{
    protected String name;
    public Integer min;
    public Integer max;
    public Integer base;
    public Integer current;
    public Integer shift;
    public Integer shiftReverse;
    public Integer shiftBase;
    Map<Object, Integer> modifiers;
    Map<Object, Float> percentModifiers;
    Map<Object, Integer> shiftModifiers;
    transient boolean dirty;
    int flags;
    private static final long serialVersionUID = 1L;
    
    public AgisStat() {
        this.modifiers = new HashMap<Object, Integer>();
        this.percentModifiers = new HashMap<Object, Float>();
        this.shiftModifiers = new HashMap<Object, Integer>();
        this.dirty = false;
        this.flags = 0;
    }
    
    public AgisStat(final String statName) {
        this.modifiers = new HashMap<Object, Integer>();
        this.percentModifiers = new HashMap<Object, Float>();
        this.shiftModifiers = new HashMap<Object, Integer>();
        this.dirty = false;
        this.flags = 0;
        this.name = statName;
    }
    
    public AgisStat(final String statName, final int value) {
        this.modifiers = new HashMap<Object, Integer>();
        this.percentModifiers = new HashMap<Object, Float>();
        this.shiftModifiers = new HashMap<Object, Integer>();
        this.dirty = false;
        this.flags = 0;
        this.name = statName;
        final Integer value2 = value;
        this.max = value2;
        this.current = value2;
        this.base = value2;
    }
    
    public AgisStat(final String statName, final int min, final int max) {
        this.modifiers = new HashMap<Object, Integer>();
        this.percentModifiers = new HashMap<Object, Float>();
        this.shiftModifiers = new HashMap<Object, Integer>();
        this.dirty = false;
        this.flags = 0;
        this.name = statName;
        this.min = min;
        this.max = max;
        final Integer value = min;
        this.current = value;
        this.base = value;
    }
    
    public AgisStat(final String statName, final int min, final int max, final boolean startAtMax) {
        this.modifiers = new HashMap<Object, Integer>();
        this.percentModifiers = new HashMap<Object, Float>();
        this.shiftModifiers = new HashMap<Object, Integer>();
        this.dirty = false;
        this.flags = 0;
        this.name = statName;
        this.min = min;
        this.max = max;
        if (startAtMax) {
            final Integer value = max;
            this.current = value;
            this.base = value;
        }
        else {
            final Integer value2 = min;
            this.current = value2;
            this.base = value2;
        }
    }
    
    public AgisStat(final String statName, final int min, final int max, final int base) {
        this.modifiers = new HashMap<Object, Integer>();
        this.percentModifiers = new HashMap<Object, Float>();
        this.shiftModifiers = new HashMap<Object, Integer>();
        this.dirty = false;
        this.flags = 0;
        this.name = statName;
        this.min = min;
        this.max = max;
        final Integer value = base;
        this.current = value;
        this.base = value;
    }
    
    @Override
    public String toString() {
        return "[AgisStat: " + this.name + "=" + this.current + " (base=" + this.base + " min=" + this.min + " max=" + this.max + ")]";
    }
    
    public Integer getMin() {
        return this.min;
    }
    
    public void setMin(final Integer min) {
        this.min = min;
    }
    
    public Integer getMax() {
        return this.max;
    }
    
    public void setMax(final Integer max) {
        this.max = max;
    }
    
    public Integer getBase() {
        return this.base;
    }
    
    public void setBase(final Integer base) {
        this.base = base;
    }
    
    public Integer getCurrent() {
        return this.current;
    }
    
    public void setCurrent(final Integer current) {
        this.current = current;
    }
    
    public int getShift(final int direction) {
        if (direction == -1) {
            return this.shiftReverse;
        }
        return this.shift;
    }
    
    public Integer getShift() {
        return this.shift;
    }
    
    public void setShift(final Integer shift) {
        this.shift = shift;
    }
    
    public Integer getShiftReverse() {
        return this.shiftReverse;
    }
    
    public void setShiftReverse(final Integer shiftReverse) {
        this.shiftReverse = shiftReverse;
    }
    
    public Integer getShiftBase() {
        return this.shiftBase;
    }
    
    public void setShiftBase(final Integer shiftBase) {
        this.shiftBase = shiftBase;
    }
    
    public Map<Object, Integer> getModifiers() {
        return this.modifiers;
    }
    
    public void setModifiers(final Map<Object, Integer> modifiers) {
        this.modifiers = modifiers;
    }
    
    public Map<Object, Float> getPercentModifiers() {
        return this.percentModifiers;
    }
    
    public void setPercentModifiers(final Map<Object, Float> modifiers) {
        this.percentModifiers = modifiers;
    }
    
    public Map<Object, Integer> getShiftModifiers() {
        return this.shiftModifiers;
    }
    
    public void setShiftModifiers(final Map<Object, Integer> modifiers) {
        this.shiftModifiers = modifiers;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public void setFlags(final int flags) {
        this.flags = flags;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void modifyBaseValue(final int delta) {
        this.base += delta;
        if (this.min != null && this.base < this.min) {
            this.base = this.min;
        }
        if (this.max != null && this.base > this.max) {
            this.base = this.max;
        }
        this.applyMods();
        this.setDirty(true);
    }
    
    public void setBaseValue(final int value) {
        this.base = value;
        if (this.min != null && this.base < this.min) {
            this.base = this.min;
        }
        if (this.max != null && this.base > this.max) {
            this.base = this.max;
        }
        this.applyMods();
        this.setDirty(true);
    }
    
    public void addModifier(final Object id, final int delta) {
        this.modifiers.put(id, delta);
        this.applyMods();
        this.setDirty(true);
    }
    
    public void removeModifier(final Object id) {
        this.modifiers.remove(id);
        this.applyMods();
        this.setDirty(true);
    }
    
    public void addPercentModifier(final Object id, final float percent) {
        this.percentModifiers.put(id, percent);
        this.applyMods();
        this.setDirty(true);
    }
    
    public void removePercentModifier(final Object id) {
        this.percentModifiers.remove(id);
        this.applyMods();
        this.setDirty(true);
    }
    
    public void addShiftModifier(final Object id, final int percent) {
        this.shiftModifiers.put(id, percent);
        this.applyShiftMods();
        this.setDirty(true);
    }
    
    public void removeShiftModifier(final Object id) {
        this.shiftModifiers.remove(id);
        this.applyShiftMods();
        this.setDirty(true);
    }
    
    public void setBaseShiftValue(final int value, final int reverseValue) {
        this.shiftBase = value;
        this.shift = value;
        this.shiftReverse = reverseValue;
    }
    
    public void setMaxValue(final int value) {
        this.setMax(value);
        this.applyMods();
        this.setDirty(true);
    }
    
    public int getCurrentValue() {
        if (this.current == null) {
            return 0;
        }
        return this.current;
    }
    
    public int getBaseValue() {
        return this.base;
    }
    
    public int getMinValue() {
        return this.min;
    }
    
    public int getMaxValue() {
        return this.max;
    }
    
    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    protected void applyMods() {
        this.current = this.base;
        for (final Integer mod : this.modifiers.values()) {
            this.current += mod;
        }
        for (final Float mod2 : this.percentModifiers.values()) {
            this.current += (int)(this.current * mod2 / 100.0f);
        }
        if (this.min != null && this.current <= this.min) {
            this.current = this.min;
        }
        if (this.max != null && this.current >= this.max) {
            this.current = this.max;
        }
    }
    
    protected void applyShiftMods() {
        this.shift = this.shiftBase;
        for (final Integer mod : this.shiftModifiers.values()) {
            this.current += mod;
        }
    }
    
    protected int computeFlags() {
        int newFlags = 0;
        if (this.min != null && this.current == this.min) {
            newFlags |= 0x1;
        }
        if (this.max != null && this.current == this.max) {
            newFlags |= 0x2;
        }
        return newFlags;
    }
    
    public boolean isSet() {
        return this.current != null;
    }
    
    public void encodeObject(final AOByteBuffer buffer) {
        AOByteBuffer.putInt(buffer, this.getCurrentValue());
    }
}
